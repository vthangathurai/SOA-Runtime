/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.CodeGenType;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.builders.BaseCodeGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceInterfaceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.JavaWSDLGenerator;
import org.ebayopensource.turmeric.tools.codegen.external.JavaWSDLGeneratorFactory;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBinder;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBindingFactory;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;
import org.ebayopensource.turmeric.tools.codegen.validator.MessageObject;
import org.ebayopensource.turmeric.tools.codegen.validator.SourceValidator;

import org.ebayopensource.turmeric.runtime.codegen.common.ImplClassDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.InterfaceClassDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.InterfaceDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.InterfaceType;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameCemcMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameToCemcMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;

public class CodeGenPreProcessor {
	
	private static Logger s_logger = LogManager.getInstance(CodeGenPreProcessor.class);
	private static final String GEN_INTERFACE_SUFFIX = "Gen";
	
	public static ServiceCodeGenDefType parseCodeGenXml(String filePath)
			throws BadInputValueException {

		ServiceCodeGenDefType svcCodeGenDefType = null;

		try {
			File xmlFile = new File(filePath);

			JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			svcCodeGenDefType = (ServiceCodeGenDefType) javaXmlBinder
					.unmarshal(xmlFile, ServiceCodeGenDefType.class);
		} catch (Exception ex) {
			String errMsg = "Failed to parse input xml : " + filePath;
			s_logger.log(Level.SEVERE, errMsg, ex);
			throw new BadInputValueException(errMsg, ex);
		}

		return svcCodeGenDefType;
	}
	
	
	public static int preProcess(CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {
		
		int exitCode = 0;
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		if (inputOptions.getInputType() == InputType.INTERFACE) {
			exitCode = processInterface(codeGenCtx);
		}	
		else if (inputOptions.getInputType() == InputType.WSDL) {
			preProcessWSDL(codeGenCtx);
			
			inputOptions.setInputType(InputType.INTERFACE);				
			exitCode = processInterface(codeGenCtx);
		}			
		else if (inputOptions.getInputType() == InputType.CLASS) {
			preProcessImplClass(
					inputOptions.getInputFile(), 
					inputOptions.getGenInterfaceName(), 
					inputOptions.getGenInterfacePackage(), 
					null, 
					codeGenCtx);
			inputOptions.setInputType(InputType.INTERFACE);
			exitCode = processInterface(codeGenCtx);
		} 		
		else if (inputOptions.getInputType() == InputType.XML) {			
			ServiceCodeGenDefType svcCodeGenDefType = inputOptions.getSvcCodeGenDefType();
			InterfaceType interfaceType = svcCodeGenDefType.getInterfaceInfo();
			
			if (interfaceType.getInterfaceClassDef() != null) {
				InterfaceClassDefType interfaceDef = interfaceType.getInterfaceClassDef();
				inputOptions.setInputFile(interfaceDef.getInterfaceClassName());
				inputOptions.setInputType(InputType.INTERFACE);
				
				exitCode = processInterface(codeGenCtx);
			}
			else if (interfaceType.getInterfaceDef() != null) {
				InterfaceDefType interfaceDef = interfaceType.getInterfaceDef();
				preProcessInterfaceDef(interfaceDef, codeGenCtx);
				inputOptions.setInputType(InputType.INTERFACE);
				
				exitCode = processInterface(codeGenCtx);
			}
			else if (interfaceType.getImplClassDef() != null) {
				ImplClassDefType implClassDef = interfaceType.getImplClassDef();
				preProcessImplClassDef(implClassDef, codeGenCtx);
				inputOptions.setInputType(InputType.INTERFACE);
				
				exitCode = processInterface(codeGenCtx);
			} 
			else if (interfaceType.getWsdlDef() != null) {
				inputOptions.setInputFile(
	 					interfaceType.getWsdlDef().getWsdlFile());
				preProcessWSDL(codeGenCtx);
				
				inputOptions.setInputType(InputType.INTERFACE);				
				exitCode = processInterface(codeGenCtx);
			}			
		}
		cleanTemporaryFiles(codeGenCtx);
		return exitCode;
		
	}
	/**
	 * This method is used for cleaning temporary files created for wsdl2java
	 * @param ctx
	 */
	private static void cleanTemporaryFiles(CodeGenContext ctx)
	{
		if(ctx.getInputOptions().getIsWsdlTobeDeleted())
		{
			s_logger.log(Level.INFO, "Deleting temporary file : " + ctx.getInputOptions().getInputFile());
			File inputFile = new File(ctx.getInputOptions().getInputFile());
			try {
				CodeGenUtil.deleteFile(inputFile);
			} catch (IOException e) {
				s_logger.log(Level.WARNING, "Could not delete temporary file :" + ctx.getInputOptions().getInputFile());

			}

		}
	}
	private static int processInterface(CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {
		
		s_logger.log(Level.FINE, "BEGIN: Processing interface...");
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		int exitCode = 0;
		if (!codeGenCtx.getGeneratedJavaFiles().isEmpty()) {
			exitCode = 1;
		}
		else {	
			compileInterface(codeGenCtx);			
			// Validate interface
			exitCode = preProcessInterface(codeGenCtx.getServiceInterfaceClassName(), codeGenCtx);			
			// Initialize
			String qualifiedInterfaceName = 
					CodeGenUtil.toQualifiedClassName(codeGenCtx.getServiceInterfaceClassName());	
			codeGenCtx.setServiceInterfaceClassName(qualifiedInterfaceName);
			
			JTypeTable jTypeTable = IntrospectUtil.initializeJType(qualifiedInterfaceName);
			
			addCemClasses(jTypeTable, inputOptions);
			
			codeGenCtx.setJTypeTable(jTypeTable);
		}
		
		s_logger.log(Level.FINE, "END: Processing interface, exitCode :" + exitCode);
		
		return exitCode;
	}


	private static void addCemClasses(
				JTypeTable jTypeTable, 
				InputOptions inputOptions) throws CodeGenFailedException {
		
		OpNameToCemcMappingList opNameToCemcMappings = 	inputOptions.getOpNameToCemcMappings();
		
		if (opNameToCemcMappings != null && 
			opNameToCemcMappings.getOpNameCemcMap().size() > 0) {				
			for (OpNameCemcMappingType opNameCemcMapEntry : opNameToCemcMappings.getOpNameCemcMap()) {
				String cemClassName = opNameCemcMapEntry.getCustomErrMsgClass();
				try {
					Class customErrMsgClass = IntrospectUtil.loadClass(cemClassName);
					jTypeTable.getTypesReferred().add(customErrMsgClass);
				} 
				catch (ClassNotFoundException clsNotFoundEx) {
					throw new CodeGenFailedException(
							"Failed to load custom error message class : " + cemClassName, clsNotFoundEx);
				}
			}
		}
		
	}
	
	private static int preProcessInterface(				
				String interfaceClassName, 
				CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {
		
		int exitCode = 0;
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		if(inputOptions.getCodeGenType().equals(CodeGenType.ServiceFromWSDLImpl)){
			populateCodegenContextWithWSDLFromClassPath(codeGenCtx);
		}
		
		String qualifiedInterfaceName = CodeGenUtil.toQualifiedClassName(interfaceClassName);	
		
		Class interfaceClass = null;
		try {
			interfaceClass = IntrospectUtil.loadClass(qualifiedInterfaceName);
		} catch (ClassNotFoundException clsNotFound) {
			throw new PreProcessFailedException(clsNotFound.getMessage(), clsNotFound);
		}
		
		List<MessageObject> errorList = 
				SourceValidator.validateServiceInterface(interfaceClass);
		
		processFatalErros(errorList);

		if (!errorList.isEmpty()) {			
			UserResponseHandler usrRespHandler = codeGenCtx.getUserResponseHandler();
			boolean autoRefineInterface = isAutoRefineInput(usrRespHandler, getPromptMsg());
			if (!autoRefineInterface) {
				exitCode = 1;
			} 
			else {
				List<String> generatedJavaSrcFiles = null;
				ServiceInterfaceGenerator svcInterfaceGenerator = 
						ServiceInterfaceGenerator.getInstance();
				try {
					String interfaceName = inputOptions.getGenInterfaceName();
					String interfacePackage = inputOptions.getGenInterfacePackage();
					
					if(CodeGenUtil.isEmptyString(interfaceName))
						interfaceName = interfaceClass.getSimpleName() + GEN_INTERFACE_SUFFIX ;
					
					if(CodeGenUtil.isEmptyString(interfacePackage))
						interfacePackage = interfaceClass.getPackage().getName();
					
					generatedJavaSrcFiles = 
						svcInterfaceGenerator.internalGenerateJavaInterface(
							interfaceClass, 
							interfaceName,
							interfacePackage,
							codeGenCtx.getSrcLocation(), 
							codeGenCtx.getJavaSrcDestLocation());
					
					codeGenCtx.addGeneratedJavaSrcFiles(generatedJavaSrcFiles);
					
					BaseCodeGenerator.compileJavaFiles(
								generatedJavaSrcFiles, 
								codeGenCtx.getBinLocation());
					
					String svcInterfaceClassName = 
						CodeGenUtil.getQualifiedClassName(
									generatedJavaSrcFiles.get(0), 
									codeGenCtx.getJavaSrcDestLocation());
					codeGenCtx.setServiceInterfaceClassName(svcInterfaceClassName);
					
					s_logger.log(Level.FINE, "preProcessInterface() - generated interface : " + svcInterfaceClassName);
				
				} catch (PreProcessFailedException preProcessFailedEx) {
					throw preProcessFailedEx;
				} catch (CodeGenFailedException codeGenFailedEx) {
					throw codeGenFailedEx;
				} catch (Exception ex) {
					s_logger.log(Level.SEVERE, ex.getMessage(), ex);
					throw new CodeGenFailedException(ex.getMessage(), ex);
				}				
			} 
		}
		
		return exitCode;
	
	}
	
		
	
	
	private static void populateCodegenContextWithWSDLFromClassPath(CodeGenContext codeGenCtx) {
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		String serviceName = inputOptions.getServiceAdminName();
		
		String wsdlRelativeLocation = "META-INF/soa/services/wsdl/" + serviceName + "/" + serviceName + ".wsdl";
		
		File wsdlFile = null;
		InputStream inputStream= null;

		try {
			inputStream = CodeGenUtil.getInputStreamForAFileFromClasspath(wsdlRelativeLocation, CodeGenPreProcessor.class.getClassLoader());
			if(inputStream == null) {
				s_logger.log(Level.WARNING, "The WSDL file could not be found in the classpath");
				return;
			} else {
				 wsdlFile = CodeGenUtil.getFileFromInputStream(inputStream, ".wsdl");
			}
		} finally {
			CodeGenUtil.closeQuietly(inputStream);
		}
		
		if(wsdlFile == null){
			s_logger.log(Level.WARNING, "The WSDL file could not be be created from the input stream");
			return;
		}
		
		try {
			WSDLUtil.populateCodegenCtxWithWSDLDetails(wsdlFile.getAbsolutePath(), codeGenCtx);
			//After CodegenCtx has been populated with wsdlDetails, temp file should be deleted.
			CodeGenUtil.deleteFile(wsdlFile);
		} catch (PreProcessFailedException e) {
			s_logger.log(Level.WARNING, "" + e.getMessage());
		} catch (IOException e) {
			s_logger.log(Level.WARNING, "The temprary WSDL file could not be be deleted at location :"+ wsdlFile.getAbsolutePath());		
		}
		
	}


	private static void preProcessInterfaceDef(				
			InterfaceDefType interfaceDef, 
			CodeGenContext codeGenCtx) 
		throws PreProcessFailedException, CodeGenFailedException {
		
		List<MessageObject> errorList = 
				SourceValidator.validateInterfaceDef(interfaceDef);
		
		processFatalErros(errorList);
		
		if (!errorList.isEmpty()) {
			throw new PreProcessFailedException(
					"Interface definition is not valid", errorList);
			
		}
		
		
		ServiceInterfaceGenerator svcInterfaceGenerator = 
				ServiceInterfaceGenerator.getInstance();
		
		String generatedJavaInterface = null;
		try {
			generatedJavaInterface = 
					svcInterfaceGenerator.generateJavaInterface(
							interfaceDef, 
							codeGenCtx.getJavaSrcDestLocation());
			
			codeGenCtx.addGeneratedJavaSrcFile(generatedJavaInterface);
			
			BaseCodeGenerator.compileJavaFile(
					generatedJavaInterface, 
					codeGenCtx.getBinLocation());			
			
			String svcInterfaceClassName = 
				CodeGenUtil.getQualifiedClassName(
						generatedJavaInterface, 
						codeGenCtx.getJavaSrcDestLocation());
			
			codeGenCtx.setServiceInterfaceClassName(svcInterfaceClassName);	
			
			s_logger.log(Level.FINE, "preProcessInterfaceDef() - generated interface : " + svcInterfaceClassName);
		
		} catch (PreProcessFailedException preProcessFailedEx) {
			throw preProcessFailedEx;
		} catch (CodeGenFailedException codeGenFailedEx) {
			throw codeGenFailedEx;
		} catch (Exception ex) {
			s_logger.log(Level.SEVERE, ex.getMessage(), ex);
			throw new CodeGenFailedException(ex.getMessage(), ex);
		}			
		
	}
	
	
	private static void preProcessImplClass(				
			String className,
			String newInterfaceName,
			String newInterfacePkgName,
			List<String> exposedMethodNames,
			CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {
		
		String qualifiedClassName = CodeGenUtil.toQualifiedClassName(className);
		
		List<MessageObject> errorList = 
				SourceValidator.validateClassForService(
						qualifiedClassName, 
						exposedMethodNames);
		
		processFatalErros(errorList);

		if (!errorList.isEmpty()) {			
			UserResponseHandler usrRespHandler = codeGenCtx.getUserResponseHandler();
			boolean autoRefineInterface = isAutoRefineInput(usrRespHandler, getPromptMsg());
			if (!autoRefineInterface) {	
				throw new PreProcessFailedException("Code generation stopped.");	
			} 
			else {
				ServiceInterfaceGenerator svcInterfaceGenerator = 
						ServiceInterfaceGenerator.getInstance();
				try {
					
					List<String> generatedJavaSrcFiles =		
							svcInterfaceGenerator.generateJavaInterface(
										qualifiedClassName, 
										newInterfaceName,
										newInterfacePkgName,
										exposedMethodNames,
										codeGenCtx.getSrcLocation(),
										codeGenCtx.getJavaSrcDestLocation());
					
					codeGenCtx.addGeneratedJavaSrcFiles(generatedJavaSrcFiles);
					
					BaseCodeGenerator.compileJavaFiles(
							generatedJavaSrcFiles, 
							codeGenCtx.getBinLocation());
					
					
					String svcInterfaceClassName = 
							CodeGenUtil.getQualifiedClassName(
									generatedJavaSrcFiles.get(0), 
									codeGenCtx.getJavaSrcDestLocation());
					codeGenCtx.setServiceInterfaceClassName(svcInterfaceClassName);	
					
					s_logger.log(Level.FINE, "preProcessImplClass() - generated interface : " + svcInterfaceClassName);
					
				} catch (PreProcessFailedException preProcessFailedEx) {
					throw preProcessFailedEx;
				} catch (CodeGenFailedException codeGenFailedEx) {
					throw codeGenFailedEx;
				} catch (Exception ex) {
					s_logger.log(Level.SEVERE, ex.getMessage(), ex);
					throw new CodeGenFailedException(ex.getMessage(), ex);
				}			
				
			} 
		}
		
	}
	
	
	private static void preProcessImplClassDef(				
			ImplClassDefType implClassDef, 
			CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {
		
		List<String> exposedMethodNames = null;
		if (implClassDef.getExposedMethods() != null) {
			exposedMethodNames = implClassDef.getExposedMethods().getMethodName();
		}		
		
		preProcessImplClass(
				implClassDef.getImplClassName(), 
				implClassDef.getInterfaceName(), 
				implClassDef.getInterfacePackage(), 
				exposedMethodNames, 
				codeGenCtx);
	
	}
	
	
	private static void preProcessWSDL(CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {	
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		try {
			// Attempt to use as set Java Src Dest Location 
			// If user specifies a Java Src Dest Location, use it.
			// Do not tack on arbitrary extra paths, as this will
			// break the classloader lookup at loadClass and introspection
			// in later codegen tasks, especially when running in Eclipse
			// and Maven Plugin.
			String javaSrcDestLoc  = codeGenCtx.getJavaSrcDestLocation(false);

			// Use [LEGACY] behavior if javaSrcDestLoc is unset.
			if(javaSrcDestLoc == null) {
	            // [LEGACY] the interface generated from WSDL or XML should always be generated under the "client" folder.
				javaSrcDestLoc  = codeGenCtx.getJavaSrcDestLocation(true);
				javaSrcDestLoc = CodeGenUtil.toOSFilePath(javaSrcDestLoc) + CodeGenConstants.CLIENT_GEN_FOLDER;
			}
			
			String wsdlFileLoc = inputOptions.getInputFile();
			
			if(codeGenCtx.getWsdlDefinition() == null){
				Definition definition = WSDLUtil.getWSDLDefinition(wsdlFileLoc);
				codeGenCtx.setWsdlDefinition(definition);
			}

			JavaWSDLGenerator wsdlGen = JavaWSDLGeneratorFactory.getInstance();		
			wsdlGen.wsdl2Java(codeGenCtx, javaSrcDestLoc);			
			
						
			String interfacePkgName = inputOptions.getGenInterfacePackage();
			
			
			
			WSDLUtil.populateCodegenCtxWithWSDLDetails(wsdlFileLoc, codeGenCtx);
			
			String qualifiedIntfName = inputOptions.getGenInterfaceName();
			qualifiedIntfName = interfacePkgName + "." + qualifiedIntfName;
			qualifiedIntfName = CodeGenUtil.toQualifiedClassName(qualifiedIntfName);
		
			
			String wsdl2JavaSrcLoc = wsdlGen.wsdl2JavaGenSrcLoc(javaSrcDestLoc);

			moveFiles(wsdl2JavaSrcLoc, javaSrcDestLoc);
			
			
			codeGenCtx.setSchemaTypesJavaFileLocation(javaSrcDestLoc);
			codeGenCtx.setServiceInterfaceClassName(qualifiedIntfName);
			codeGenCtx.setWSDLURI(wsdlFileLoc);
			codeGenCtx.setAsyncInputType((inputOptions.getInputType() == InputType.WSDL));
			codeGenCtx.setAsyncJavaSrcDestLocation(javaSrcDestLoc);
			
			BaseCodeGenerator.compileJavaFile(
						qualifiedIntfName, 
						javaSrcDestLoc, 
						codeGenCtx.getBinLocation());			
		
		} catch (PreProcessFailedException preProcessFailedEx) {
			throw preProcessFailedEx;
		} catch (CodeGenFailedException codegenFailedEx) {
			throw codegenFailedEx;
		} catch (Exception ex) {
			s_logger.log(Level.SEVERE, ex.getMessage(), ex);
			throw new CodeGenFailedException(ex.getMessage(), ex);
		}
	}
		
	
	
	private static void processFatalErros(List<MessageObject> errorList) 
			throws PreValidationFailedException {
	
		if (errorList.isEmpty()) {
			return;
		}
		
		List<MessageObject> fatalErrors = new ArrayList<MessageObject>();
		for (MessageObject errMsgObj : errorList) {
			if (errMsgObj.isFatalError()) {
				fatalErrors.add(errMsgObj);
			}
		}		
		if (!fatalErrors.isEmpty()) {
			throw new PreValidationFailedException(fatalErrors.toString());
		}
	}
	
	private static void compileInterface(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {
		// 1. Load interface class
		// 2. If step 1 fails, then try to compile interface source file
		// 3. Try to load class again to make sure that compiled class is in the
		// classpath

		String interfaceName = codeGenCtx.getServiceInterfaceClassName();

		String qualifiedIntfName = CodeGenUtil.toQualifiedClassName(interfaceName);

		try {
			IntrospectUtil.loadClass(qualifiedIntfName);
			
		} catch (ClassNotFoundException clsNotFoundEx) {
			try {
				
				//If the original InputType is WSDL then we should look for the generated Interface java file under gen-src\client
				InputOptions inputOptions = codeGenCtx.getInputOptions();
				
				String interfaceSourceLocation = codeGenCtx.getSrcLocation();
				
				if(inputOptions.getOriginalInputType() == InputType.WSDL) {
					// If user specifies a Java Src Dest Location, use it.
					// Do not tack on arbitrary extra paths, as this will
					// break the classloader lookup at the loadClass later
					// when running in Eclipse and Maven Plugin.
					String javaSrcDestLoc = codeGenCtx.getJavaSrcDestLocation(false);

					// Use [LEGACY] behavior if javaSrcDestLoc is unset.
					if(javaSrcDestLoc == null) {
			            // [LEGACY] the interface generated from WSDL or XML should always be generated under the "client" folder.
						javaSrcDestLoc  = codeGenCtx.getJavaSrcDestLocation(true);
						javaSrcDestLoc = CodeGenUtil.toOSFilePath(javaSrcDestLoc) + CodeGenConstants.CLIENT_GEN_FOLDER;
					}
					
					interfaceSourceLocation = javaSrcDestLoc;
				}
				
				BaseCodeGenerator.compileJavaFile(
						interfaceName, 
						interfaceSourceLocation, 
						codeGenCtx.getBinLocation());
			} 
			catch (Exception ex) {
				throw new CodeGenFailedException(
						"Failed to compile java source file : " + interfaceName , ex);
			}

			try {
				IntrospectUtil.loadClass(qualifiedIntfName);
			} 
			catch (ClassNotFoundException clsNotFoundEx2) {
				throw new CodeGenFailedException("Failed to load java class : "
						+ qualifiedIntfName + "\n Cause: " + clsNotFoundEx2.getCause(), clsNotFoundEx2);
			}
		}
	}
	
	private static void moveFiles(String srcLoc, String destLoc) throws IOException {
		
		File srcDir = new File(srcLoc);	
		String srcPath = srcDir.getAbsolutePath();
		
		File destDir = new File(destLoc);	
		String destPath = destDir.getAbsolutePath();		
		String normalizedDestLoc = CodeGenUtil.toOSFilePath(destPath);
		
		List<String> allFiles = new ArrayList<String>();
		CodeGenUtil.addAllFiles(srcDir, allFiles);
		if (allFiles.size() > 0) {
			for (String filePath : allFiles) {				
				int pos = filePath.indexOf(srcPath);
				String relativeFilePath = filePath.substring(pos + srcPath.length() + 1);
				// Strip-off file name
				String relativeFileLoc = relativeFilePath.substring(0,relativeFilePath.lastIndexOf(File.separator));
				String destFileLoc = normalizedDestLoc + relativeFileLoc;
				CodeGenUtil.move(filePath, destFileLoc, true);
			}
		}
		
		CodeGenUtil.deleteDir(srcDir);
	}
	
	
	private static boolean isAutoRefineInput(
			UserResponseHandler userRespHandler,
			String promptMsg) {
		boolean isAutoRefineInput = 
			userRespHandler.getBooleanResponse(promptMsg);
		
		return isAutoRefineInput;
	}
	
	
	
	private static String getPromptMsg() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("One or more of the following must be corrected : \n");
		strBuffer.append("1. Service methods should take only one parameter.\n");
		strBuffer.append("2. Service method parameter types / return type should be non-collection types.\n\n");

		strBuffer.append("Do you want CodeGen tool to create a new interface based on your input interface / class?\n"); 
		
		return strBuffer.toString();
	}
	

}
