/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.WSDLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.CodeGenType;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.builders.AsyncServiceInterfaceGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ClientConfigGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.DispatcherForBuildGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.GlobalClientConfigGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.GlobalServiceConfigGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.SecurityPolicyConfigGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceConfigGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceConsumerGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceDispatcherGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceIntfPropertiesFileGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceMetadataFileGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceProxyGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.ServiceSkeletonGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.TypeDefsBuilderGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.TypeMappingsGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.UnitTestGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.WSDLGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.WSDLSingleSchemaGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.WebAppDescriptorGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.WsdlWithMultipleNsGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.WsdlWithPublicServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.handler.ConsoleResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.DontPromptResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenClassLoader;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.EnableNamespaceFoldingUtil;
import org.ebayopensource.turmeric.tools.codegen.util.JavacHelper;
import org.ebayopensource.turmeric.tools.codegen.util.ModifyWsdlWithPublicServiceandRemoveAppinfoTagsUtil;
import org.ebayopensource.turmeric.tools.codegen.util.SOAVersionType;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.codegen.ErrorLibraryCodeGenBuilder;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class ServiceCodeGenBuilder {
	
	private static Logger s_logger = LogManager.getInstance(ServiceCodeGenBuilder.class);
	
	
	private Logger getLogger() {
		return s_logger;
	}

	/**
	 * Initiates service code generation process
	 * 
	 * @param String[] - Code generation options
	 * @throws Exception
	 */
	public void build(
			String[] inputArgs, 
			UserResponseHandler responseHandler) throws Exception  {
		
    	//performLoggingInit(inputArgs);
    //	s_logger = LogManager.getInstance(ServiceCodeGenBuilder.class);
		
		getLogger().log(Level.INFO, "BEGIN: Service code generation ....");
		
		long startTime = System.currentTimeMillis();
		
		// GenType is a required field (so we know what to do)
        String genType = null;
        boolean foundGenType = false;
        int arglen = inputArgs.length;
        for (int i = 0; i < arglen; i++) {
            if (foundGenType && genType == null) {
                genType = inputArgs[i];
            }
            if (InputOptions.OPT_CODE_GEN_TYPE.equalsIgnoreCase(inputArgs[i])) {
                foundGenType = true;
            }
        }
        if (!foundGenType) {
            throw new MissingInputOptionException("provide a value for -gentype option");
        }
        if (CodeGenUtil.isEmptyString(genType)) {
            throw new BadInputValueException("Please provide a proper value for the option -gentype");
        }
		
		TypeLibraryCodeGenBuilder typeLibraryBuilder = new TypeLibraryCodeGenBuilder();
		//Call the TypeLibrary GenTypes
		TypeLibraryInputOptions typeLibraryInputOptions = typeLibraryBuilder.getTypeLibraryInputGenTypes(inputArgs);

		/*
		 * If the GenType is from the TypeLibrary then perform the
		 * typelibrary codegen otherwise follow the normal flow.
		 */
		if(typeLibraryBuilder.isGenTypeTypeLibrary(typeLibraryInputOptions)){
			try{
				typeLibraryInputOptions = typeLibraryBuilder.getTypeLibraryInputOptions(inputArgs); 
				typeLibraryBuilder.buildTypeLibrary(typeLibraryInputOptions, responseHandler);
			}catch(CodeGenFailedException ce){
				getLogger().log(Level.SEVERE,"CodeGenFailedException for type library. throwing this exception back to caller."  );
				throw ce;
			}catch(Exception e){
				getLogger().log(Level.SEVERE,"Exception from codegen for type library. throwing this exception back to caller."  );
				throw e;
			}	
			
		} else {
			ErrorLibraryCodeGenBuilder errorLibraryBuilder = new ErrorLibraryCodeGenBuilder();
			//Call the ErrorLibrary GenTypes
			ErrorLibraryInputOptions errorLibraryInputOptions = errorLibraryBuilder.getErrorLibraryInputGenTypes(inputArgs); 
			
			if(errorLibraryBuilder.isGenTypeErrorLibrary(errorLibraryInputOptions)){
				try{
					errorLibraryInputOptions = errorLibraryBuilder.getErrorLibraryInputOptions(inputArgs);
					errorLibraryBuilder.buildErrorLibrary(errorLibraryInputOptions);
				}catch(CodeGenFailedException ce){
					getLogger().log(Level.SEVERE,"CodeGenFailedException for error library. throwing this exception back to caller."  );
					throw ce;
				}catch(Exception e){
					getLogger().log(Level.SEVERE,"Exception from codegen for error library. throwing this exception back to caller."  );
					throw e;
				}	
				
			} else {
				InputOptions inputOptions = getInputOptions(inputArgs);		
				
				UserResponseHandler userResponseHandler = responseHandler;		
				if (inputOptions.isDontPrompt()) {
					userResponseHandler = new DontPromptResponseHandler();
				} 
				else if (userResponseHandler == null) {
					userResponseHandler = new ConsoleResponseHandler();
				}
			
				CodeGenContext codeGenCtx = createContext(inputOptions, userResponseHandler);
				if(inputOptions.isEnabledNamespaceFoldingSet()&& isMMNWsdlGenerationRequired(codeGenCtx.getInputOptions()))
				{
					EnableNamespaceFoldingUtil.enableNamespaceFolding(codeGenCtx);
				}
			   //Adcommerce support,Need to generate new Wsdl with Public ServiceName
				//this shall create new wsdl if publicservicename is set.
				if(! CodeGenUtil.isEmptyString(inputOptions.getPublicServiceName()))
				{
					ModifyWsdlWithPublicServiceandRemoveAppinfoTagsUtil.modifyWsdl(codeGenCtx);
				}
				int statusCode = preProcess(codeGenCtx);
				if (statusCode == 0) {
					internalStartCodeGen(codeGenCtx);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		
		getLogger().log(Level.INFO, "END: Service code generation, took : " + (endTime - startTime) + " ms");
	} 
	
	private int preProcess(CodeGenContext codeGenCtx) 
			throws PreProcessFailedException, CodeGenFailedException {
		
		int statusCode = 0;
		
		// add bin location to classpath if it's not already in the classpath; 
		// any generated classes by codegen tool will be loadable later using 
		// class.forName()
		boolean isSuccess = JavacHelper.addToClasspath(codeGenCtx.getBinLocation());
		codeGenCtx.setIsBinLocAddedToClasspath(isSuccess);
		
		
		boolean isPreProcessRequired = isPreProcessRequired(codeGenCtx.getInputOptions());
		if (isPreProcessRequired) {
			statusCode = CodeGenPreProcessor.preProcess(codeGenCtx);
		}
		
		return statusCode;
	}
	
	private void internalStartCodeGen(CodeGenContext codeGenCtx) throws CodeGenFailedException, WSDLException {		
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		List<SourceGenerator> codeGeneratorList = getCodeGenerators(inputOptions);		
		for (SourceGenerator codeGenerator : codeGeneratorList) {	
			String logMsg = codeGenerator.getClass().getSimpleName() + ".generate()";
			try {
				getLogger().log(Level.INFO, "BEGIN: " + logMsg);
				codeGenerator.generate(codeGenCtx);	
				getLogger().log(Level.INFO, "END: " + logMsg);
			} catch (CodeGenFailedException cgfEx) {
				if (!codeGenerator.continueOnError() || !inputOptions.isContinueOnError()) {
					getLogger().log(Level.SEVERE, "ERROR: " + logMsg, cgfEx.toString());
					throw cgfEx;
				}
				getLogger().log(Level.WARNING, logMsg + ": " + cgfEx.toString(), cgfEx);
			}
		}
		
	}	
	
	
	
	private CodeGenContext createContext(
			InputOptions inputOptions, 
			UserResponseHandler userResponseHandler) 
			throws CodeGenFailedException {
	
		CodeGenContext codeGenCtx = 
				new CodeGenContext(inputOptions, userResponseHandler);
		
		String javaSrcDestLoc = inputOptions.getJavaSrcDestLocation();
		String metaSrcDestLoc = inputOptions.getMetaSrcDestLocation();

		String destLocation = inputOptions.getDestLocation();
		
		// Generated Java Source files will go here
		if (CodeGenUtil.isEmptyString(javaSrcDestLoc)) {
			// set it to default
			javaSrcDestLoc = CodeGenUtil.genDestFolderPath(destLocation, GEN_SRC_FOLDER);
		} 		
		// Generated XML/Config files will go here
		if (CodeGenUtil.isEmptyString(metaSrcDestLoc)) {
			// set it to default
			metaSrcDestLoc = CodeGenUtil.genDestFolderPath(destLocation, GEN_META_SRC_FOLDER);
		} 
			
		// Compiled Java Classes will go here
		String binLocation = inputOptions.getBinLocation();
		if (CodeGenUtil.isEmptyString(binLocation)) {
			binLocation = CodeGenUtil.genDestFolderPath(destLocation, "bin");
			inputOptions.setBinLocation(binLocation);
		}
		// create directories if doesn't exists
		try {			
			CodeGenUtil.createDir(javaSrcDestLoc);
			CodeGenUtil.createDir(metaSrcDestLoc);
			CodeGenUtil.createDir(binLocation);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}
		
		codeGenCtx.setJavaSrcDestLocation(javaSrcDestLoc);
		codeGenCtx.setMetaSrcDestLocation(metaSrcDestLoc);
		
		derivePollMethodGenerationDecision(codeGenCtx);
		
		return codeGenCtx;
	}
	
	

	private boolean isPreProcessRequired(InputOptions inputOptions) {
		CodeGenType codeGenType = inputOptions.getCodeGenType();
		return !((codeGenType == CodeGenType.GlobalClientConfig) || 
				 (codeGenType == CodeGenType.GlobalServerConfig) ||
				 (codeGenType == CodeGenType.SecurityPolicyConfig) ||
				 (codeGenType == CodeGenType.ServiceMetadataProps) ||
				 (codeGenType == CodeGenType.ServiceIntfProjectProps) ||
				 (codeGenType == CodeGenType.WebXml) || 
				 (codeGenType == CodeGenType.ServerConfig) ||
				 (codeGenType == CodeGenType.ClientConfig) ||
				 (codeGenType == CodeGenType.WSDLWithSingleSchema) ||
				 (codeGenType == CodeGenType.WsdlConversionToMns)||
				 (codeGenType==CodeGenType.WsdlWithPublicServiceName));
	}
	
	private boolean isMMNWsdlGenerationRequired(InputOptions inputOptions) {
		CodeGenType codeGenType = inputOptions.getCodeGenType();
		return !((codeGenType == CodeGenType.GlobalClientConfig)
				|| (codeGenType == CodeGenType.GlobalServerConfig)
				|| (codeGenType == CodeGenType.SecurityPolicyConfig)
				|| (codeGenType == CodeGenType.ServiceMetadataProps)
				|| (codeGenType == CodeGenType.ServiceIntfProjectProps)
				|| (codeGenType == CodeGenType.WebXml)
				|| (codeGenType == CodeGenType.ServerConfig)
				|| (codeGenType == CodeGenType.ClientConfig)
				|| (codeGenType == CodeGenType.WSDLWithSingleSchema)
				|| (codeGenType == CodeGenType.WsdlConversionToMns) || (codeGenType == CodeGenType.WsdlWithPublicServiceName)||
				(inputOptions.getInputType().equals(InputType.INTERFACE)));
		
	}
	
	private List<SourceGenerator> getCodeGenerators(InputOptions inputOptions) {
		
		CodeGenType codeGenType = inputOptions.getCodeGenType();		
		List<SourceGenerator> codeGenerators = new ArrayList<SourceGenerator>();
		
		if (codeGenType == CodeGenType.All) {
			codeGenerators.add(AsyncServiceInterfaceGenerator.getInstance());
			codeGenerators.add(ServiceProxyGenerator.getInstance());
			codeGenerators.add(ServiceSkeletonGenerator.getInstance());
			codeGenerators.add(ServiceDispatcherGenerator.getInstance());
			codeGenerators.add(TypeMappingsGenerator.getInstance());
				codeGenerators.add(ServiceMetadataFileGenerator.getInstance());
			codeGenerators.add(ClientConfigGenerator.getInstance());
			codeGenerators.add(ServiceConfigGenerator.getInstance());
			codeGenerators.add(SecurityPolicyConfigGenerator.getInstance());
			codeGenerators.add(WebAppDescriptorGenerator.getInstance());
			codeGenerators.add(ServiceConsumerGenerator.getInstance());
			codeGenerators.add(UnitTestGenerator.getInstance());
			
			if (inputOptions.isNoGlobalConfig() == false) {
				codeGenerators.add(GlobalClientConfigGenerator.getInstance());
				codeGenerators.add(GlobalServiceConfigGenerator.getInstance());
			}			
			// If Service is being generated from WSDL
			// then don't need to generated Schema and WSDL
			// as Schema and WSDL are already existing
			if (inputOptions.isWSDLBasedService() == false) {
				codeGenerators.add(WSDLGenerator.getInstance());
			}
			codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.Server) {
			codeGenerators.add(ServiceSkeletonGenerator.getInstance());
			codeGenerators.add(ServiceDispatcherGenerator.getInstance());
			codeGenerators.add(TypeMappingsGenerator.getInstance());
			codeGenerators.add(ServiceConfigGenerator.getInstance());
			codeGenerators.add(SecurityPolicyConfigGenerator.getInstance());
			codeGenerators.add(WebAppDescriptorGenerator.getInstance());
			
			if (inputOptions.isNoGlobalConfig() == false) {
				codeGenerators.add(GlobalServiceConfigGenerator.getInstance());
			}			
			codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
			if (inputOptions.isGenTests()) {
				codeGenerators.add(UnitTestGenerator.getInstance());
			}
		}
		else if (codeGenType == CodeGenType.Client) {
			codeGenerators.add(AsyncServiceInterfaceGenerator.getInstance());
			codeGenerators.add(ServiceProxyGenerator.getInstance());
			codeGenerators.add(TypeMappingsGenerator.getInstance());
				codeGenerators.add(ServiceMetadataFileGenerator.getInstance());
//BUGDB00514281
//			codeGenerators.add(ClientConfigGenerator.getInstance());
			
			if (inputOptions.isNoGlobalConfig() == false) {
				codeGenerators.add(GlobalClientConfigGenerator.getInstance());
			}			
			// If Service is being generated from WSDL
			// then don't need to generated Schema and WSDL
			// as Schema and WSDL are already existing
			if (!inputOptions.isWSDLBasedService()) {
				codeGenerators.add(WSDLGenerator.getInstance());
			}
			codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
			
			if (inputOptions.isGenTests() && inputOptions.isBaseConsumerGenertionReq()) {
				codeGenerators.add(ServiceConsumerGenerator.getInstance());
			}
		}
		else if (codeGenType == CodeGenType.ServerNoConfig) {
			codeGenerators.add(ServiceSkeletonGenerator.getInstance());
			codeGenerators.add(ServiceDispatcherGenerator.getInstance());
			codeGenerators.add(TypeMappingsGenerator.getInstance());
			codeGenerators.add(WebAppDescriptorGenerator.getInstance());			
			
			codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
			if (inputOptions.isGenTests()) {
				codeGenerators.add(UnitTestGenerator.getInstance());
			}
		}
		else if (codeGenType == CodeGenType.ClientNoConfig) {
			codeGenerators.add(AsyncServiceInterfaceGenerator.getInstance());
			codeGenerators.add(ServiceProxyGenerator.getInstance());
			codeGenerators.add(TypeMappingsGenerator.getInstance());
				codeGenerators.add(ServiceMetadataFileGenerator.getInstance());
			// If Service is being generated from WSDL
			// then don't need to generated Schema and WSDL
			// as Schema and WSDL are already existing
			if (!inputOptions.isWSDLBasedService()) {
				codeGenerators.add(WSDLGenerator.getInstance());
			}
			codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
			
			if (inputOptions.isGenTests() && inputOptions.isBaseConsumerGenertionReq()) {
				codeGenerators.add(ServiceConsumerGenerator.getInstance());
			}
			
 			//add consumer generator for generating shared consumer
 			if(inputOptions.isGenerateSharedConsumer() || inputOptions.isConsumerAnInterfaceProjectArtifact()) {
 				codeGenerators.add(ServiceConsumerGenerator.getInstance());
 			}
			
		}
		else if (codeGenType == CodeGenType.Proxy) {
			codeGenerators.add(AsyncServiceInterfaceGenerator.getInstance());
			codeGenerators.add(ServiceProxyGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.Dispatcher) {
			codeGenerators.add(ServiceDispatcherGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.DispatcherForBuild) {
			codeGenerators.add(ServiceDispatcherGenerator.getInstance());
			//similar to plugin call for building impl project
			codeGenerators.add(ClientConfigGenerator.getInstance());
			codeGenerators.add(UnitTestGenerator.getInstance());
			//HACK
			//This is to only delete contents inside "client" folder 
			codeGenerators.add(DispatcherForBuildGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.DispatcherForMaven) {
			codeGenerators.add(ServiceDispatcherGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.Interface) {
			// HACK
			// for generating interface from WSDL, we don't need any
			// Code generators; preProcess will take care of it.
		}
		else if (codeGenType == CodeGenType.ConfigAll) {
			codeGenerators.add(ClientConfigGenerator.getInstance());
			codeGenerators.add(ServiceConfigGenerator.getInstance());
			codeGenerators.add(SecurityPolicyConfigGenerator.getInstance());

			if (inputOptions.isNoGlobalConfig() == false) {
				codeGenerators.add(GlobalClientConfigGenerator.getInstance());
				codeGenerators.add(GlobalServiceConfigGenerator.getInstance());
			}			
		}
		else if (codeGenType == CodeGenType.GlobalClientConfig) {
			codeGenerators.add(GlobalClientConfigGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.GlobalServerConfig) {
			codeGenerators.add(GlobalServiceConfigGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.ClientConfig) {
			codeGenerators.add(ClientConfigGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.ServerConfig) {
			codeGenerators.add(ServiceConfigGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.Schema) {
		}
		else if (codeGenType == CodeGenType.SISkeleton) {
			codeGenerators.add(ServiceSkeletonGenerator.getInstance());
		}
        else if (codeGenType == CodeGenType.TypeMappings) {
            codeGenerators.add(TypeMappingsGenerator.getInstance());
        }
        else if (codeGenType == CodeGenType.WebXml) {
            codeGenerators.add( WebAppDescriptorGenerator.getInstance());
        }
        else if (codeGenType == CodeGenType.Wsdl) {
            codeGenerators.add( WSDLGenerator.getInstance());
        }
        else if (codeGenType == CodeGenType.UnitTest) {
            codeGenerators.add(UnitTestGenerator.getInstance());
        }
        else if (codeGenType == CodeGenType.Consumer) {
        	//BaseConsumer for Consumer projects is to be generated for only pre 2.4 projects.
        	//for post 2.4 shared consumer present in svc_intf project shall  be used.
        	if(inputOptions.isBaseConsumerGenertionReq())
 			codeGenerators.add(ServiceConsumerGenerator.getInstance());
        }
        else if (codeGenType == CodeGenType.TypeDefs) {
			
        	codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
        }
		else if (codeGenType == CodeGenType.SecurityPolicyConfig) {
			codeGenerators.add(SecurityPolicyConfigGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.ServiceMetadataProps) {
			codeGenerators.add(ServiceMetadataFileGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.ServiceIntfProjectProps) {
			codeGenerators.add(ServiceIntfPropertiesFileGenerator.getInstance());
		}
		else if (codeGenType == CodeGenType.ServiceFromWSDLIntf){
			codeGenerators.add(AsyncServiceInterfaceGenerator.getInstance());
			codeGenerators.add(ServiceProxyGenerator.getInstance());        
			codeGenerators.add(TypeMappingsGenerator.getInstance());
				codeGenerators.add(ServiceMetadataFileGenerator.getInstance());
 			codeGenerators.add(TypeDefsBuilderGenerator.getInstance());
			
 			//add consumer generator for generating shared consumer
 			if(inputOptions.isConsumerAnInterfaceProjectArtifact())
 				codeGenerators.add(ServiceConsumerGenerator.getInstance());
 			
			if (inputOptions.isGenTests() && inputOptions.isBaseConsumerGenertionReq()) 
				codeGenerators.add(ServiceConsumerGenerator.getInstance());    
		}
		else if (codeGenType == CodeGenType.ServiceFromWSDLImpl){
			codeGenerators.add(ServiceSkeletonGenerator.getInstance());
			if (inputOptions.isGenTests()) {
				codeGenerators.add(UnitTestGenerator.getInstance());
				codeGenerators.add(ClientConfigGenerator.getInstance());	
			}            
			codeGenerators.add(WebAppDescriptorGenerator.getInstance());    
			codeGenerators.add(ServiceDispatcherGenerator.getInstance()); 
			
		}
		else if (codeGenType == CodeGenType.WSDLWithSingleSchema) {
			codeGenerators.add(WSDLSingleSchemaGenerator.getInstance());
		}
		else if(codeGenType == CodeGenType.WsdlConversionToMns){
			codeGenerators.add(WsdlWithMultipleNsGenerator.getInstance());
		}
		else if(codeGenType == CodeGenType.WsdlWithPublicServiceName){
			codeGenerators.add(WsdlWithPublicServiceGenerator.getInstance());
		}
		else if(codeGenType == CodeGenType.SharedConsumer){
			codeGenerators.add(ServiceConsumerGenerator.getInstance());
			inputOptions.setIsConsumerAnInterfaceProjectArtifact(true);
		}

		
		
		return codeGenerators;		
	}
	
	
	
	private InputOptions getInputOptions(String[] args) 
			throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		
		// Parse & Validate input arguments 
		InputOptions inputOptions = ServiceCodeGenArgsParser.getInstance().parse(args);
		ServiceCodeGenArgsValidator.getInstance().validate(inputOptions);
		
		getLogger().log(Level.INFO, "Input Options : \n" + inputOptions.toString());
		
		return inputOptions;
	}
	
		
	private void derivePollMethodGenerationDecision(CodeGenContext codeGenCtx) {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if( ! (classLoader instanceof CodeGenClassLoader) ){
			classLoader = ServiceCodeGenBuilder.class.getClassLoader();
			if( ! (classLoader instanceof CodeGenClassLoader)){
				codeGenCtx.setGeneratePollMethod(true); //set true if not able to find a class loader of type CodegenClassLoader and return
				return;
			}
		}
		
		String resourceName = "META-INF/BuildInfo.xml";
		CodeGenClassLoader codeGenClassLoader = (CodeGenClassLoader)classLoader;
		String version = null;
		
		
		URL urlOfJar = codeGenClassLoader.getURLOfJarFileWithASpecifiedName("soa-client.jar");
		if(urlOfJar != null){
			JarFile jarFile = null;
			InputStream inputStream = null;
				try {
					jarFile = new JarFile(CodeGenUtil.urlToFile(urlOfJar));
					JarEntry jarEntry = jarFile.getJarEntry(resourceName);
					if(jarEntry != null){
						 inputStream = jarFile.getInputStream(jarEntry);
						if(inputStream != null){
							
							DocumentBuilderFactory  documentBuilderFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
							Document document = documentBuilder.parse(inputStream);
							
							Element rootElement = (Element) document.getDocumentElement();
							
							NodeList nodes  = DomParseUtils.getImmediateChildrenByTagName(rootElement, "Property");
							for(int i = 0 ;i< nodes.getLength() ; i++){
								Element currElement = (Element)nodes.item(i);
								if("library.version".equals(currElement.getAttribute("name"))){
									version = DomParseUtils.getText(currElement);
									break;
								}
							}
						}
					}
				} catch (IOException e) {
					getLogger().log(Level.INFO, "Exception in method derivePollMethodGenerationDecision : "+ e);
				} catch (ParserConfigurationException e) {
					getLogger().log(Level.INFO, "Exception in method derivePollMethodGenerationDecision : "+ e);
				} catch (SAXException e) {
					getLogger().log(Level.INFO, "Exception in method derivePollMethodGenerationDecision : "+ e);
				} catch (ServiceCreationException e) {
					getLogger().log(Level.INFO, "Exception in method derivePollMethodGenerationDecision : "+ e);
				} finally {
					CodeGenUtil.closeQuietly(inputStream);
					CodeGenUtil.closeQuietly(jarFile);
				}
		
		}
		

		// if version is still null then set generation to true
		if(CodeGenUtil.isEmptyString(version)) {
			codeGenCtx.setGeneratePollMethod(true);
			return;
		}
		
		SOAVersionType versionToCompare = new SOAVersionType(CodeGenConstants.MIN_VERSION_FOR_GENERATING_POLL_METHOD);
		SOAVersionType currVersion = new SOAVersionType(version);
		
		if(currVersion.compare(versionToCompare) >= 0)
			codeGenCtx.setGeneratePollMethod(true);
		else
			codeGenCtx.setGeneratePollMethod(false);
			

		
	}


}
