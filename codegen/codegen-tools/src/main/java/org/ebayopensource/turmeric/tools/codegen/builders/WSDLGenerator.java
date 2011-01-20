/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.service.CommonServiceOperations;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.JavaWSDLGenerator;
import org.ebayopensource.turmeric.tools.codegen.external.JavaWSDLGeneratorFactory;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;



/**
 * Generates WSDL for a service
 * 
 * 
 * @author rmandapati
 */
public class WSDLGenerator  extends BaseCodeGenerator implements SourceGenerator {
	
	private static final String WSDL_GEN_DIR = "META-INF/soa/services/wsdl";
	
	private static Logger s_logger = LogManager.getInstance(WSDLGenerator.class);
	
	private static WSDLGenerator s_wsdlGenerator = new WSDLGenerator();
	
	private WSDLGenerator() {}
	
	
	public static WSDLGenerator getInstance() {
		return s_wsdlGenerator;
	}
	

	private Logger getLogger() {
		return s_logger;
	}
	
	
	public boolean continueOnError() {
		return true;
	}
	
	public void generate(CodeGenContext codeGenCtx)  
			throws CodeGenFailedException  {
		
		generateWSDL(codeGenCtx);
	}
	
	
	private void generateWSDL(CodeGenContext codeGenCtx) 
			throws CodeGenFailedException {
	
		String metaSrcdestLoc = codeGenCtx.getMetaSrcDestLocation();
		String svcName = codeGenCtx.getServiceAdminName();
		String wsdlDestLocation = destFolderPath(metaSrcdestLoc, svcName);
		
		JavaWSDLGenerator wsdlGen = null;
		try {
			wsdlGen = JavaWSDLGeneratorFactory.getInstance();
		} catch (PreProcessFailedException exception) {
			CodeGenFailedException codeGenFailedException = new 
        	CodeGenFailedException(exception.getMessage(), exception);
			codeGenFailedException.setMessageFormatted(true);
			throw codeGenFailedException;
		}		
		
		String intfNameForWSDLGen = null;
		
		if (!codeGenCtx.getInputOptions().isImplCommonSvcInterface()) {
			intfNameForWSDLGen = codeGenCtx.getServiceInterfaceClassName();
		}
		else {
			String intfSrcLoc = null;
			if (CodeGenUtil.isEmptyString(codeGenCtx.getWSDLURI())) {
				intfSrcLoc = codeGenCtx.getSrcLocation();
			} else {
				intfSrcLoc = wsdlGen.wsdl2JavaGenSrcLoc(codeGenCtx.getJavaSrcDestLocation());
			}
			
			intfNameForWSDLGen = generateIntfForWSDLGen(codeGenCtx, intfSrcLoc);
		}
		
		try {		
			wsdlGen.java2WSDL(codeGenCtx, intfNameForWSDLGen, wsdlDestLocation);
			getLogger().log(Level.INFO, "Successfully generated " + svcName + ".wsdl");
		} finally {
			// if new interface is generated for the purpose of WSDL generation
			// then delete it
			if (codeGenCtx.getInputOptions().isImplCommonSvcInterface()) {
				try {
					String wsdlGenIntfPath = 
							CodeGenUtil.toJavaSrcFilePath(
										codeGenCtx.getJavaSrcDestLocation(), 
										intfNameForWSDLGen);
					CodeGenUtil.deleteFile(new File(wsdlGenIntfPath));
				} catch (Exception ex) {
					//NOPMD
				}
			}
		}
	}
	
	
	
	private String generateIntfForWSDLGen(
				CodeGenContext codeGenCtx, 
				String intfSrcLoc) throws CodeGenFailedException  {
		
		
		ServiceInterfaceGenerator svcIntfGenerator = ServiceInterfaceGenerator.getInstance();
	
		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();		
		Class<?> svcIntfClass = jTypeTable.getClazz();		
		
		String intfNameForWSDLGen = 
				getQualifiedIntfNameForWSDLGen(svcIntfClass, codeGenCtx.getServiceAdminName());
		
		List<Method> interfaceMethods = new ArrayList<Method>(jTypeTable.getMethods());
		Method[] commonSvcOpMethods = getCommonSvcOpInterface().getDeclaredMethods();
		for (Method method : commonSvcOpMethods) {
			interfaceMethods.add(method);
		}		
		Method[] allMethods = interfaceMethods.toArray(new Method[0]);
		
		Map<String, String[]> methodToParamNamesMap =
			svcIntfGenerator.getMethodToParamNameMap(svcIntfClass, intfSrcLoc);
		methodToParamNamesMap.putAll(COMMON_SVC_OP_METHOD_PARAM_NAME_MAP);
		
		List<String> generatedJavaFiles = 
				svcIntfGenerator.generateTypesAndInterface(
					getSimpleNameOfWSDLGenIntf(codeGenCtx.getServiceAdminName()),
					svcIntfClass.getPackage().getName(),
					allMethods, 
					methodToParamNamesMap, 
					codeGenCtx.getJavaSrcDestLocation());
		
		try {
			compileJavaFiles(
					generatedJavaFiles, 
					codeGenCtx.getBinLocation());
		} catch (Exception ex) {
			String errMsg = "generateIntfForWSDLGen failed!";
			s_logger.log(Level.SEVERE, errMsg, ex);
			throw new CodeGenFailedException(errMsg, ex);
		}
		
		return intfNameForWSDLGen;
	}
	
	
	private String getQualifiedIntfNameForWSDLGen(Class<?> srcInterfaceClass, String serviceName) {
		String pkgName = srcInterfaceClass.getPackage().getName();
		String intfName = getSimpleNameOfWSDLGenIntf(serviceName);
		
		return generateQualifiedClassName(pkgName, intfName);
	}
	
	
	private String getSimpleNameOfWSDLGenIntf(String serviceName) {
		return serviceName + "IntfForWSDLGen";
	}
	
	
	private Class<?> getCommonSvcOpInterface() {
		return CommonServiceOperations.class;
	}
	
	
	private String destFolderPath(String destLoc, String serviceName) 
			throws CodeGenFailedException {
        
		String destFolderPath = 
    		CodeGenUtil.genDestFolderPath(
    				destLoc, 
    				serviceName,
    				WSDL_GEN_DIR);
        
		try {
			CodeGenUtil.createDir(destFolderPath);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}
	
		return destFolderPath;
		
	}


	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(WSDL_GEN_DIR) + serviceAdminName + File.separatorChar +serviceAdminName + ".wsdl" ;
		return filePath;
	}
	
}
