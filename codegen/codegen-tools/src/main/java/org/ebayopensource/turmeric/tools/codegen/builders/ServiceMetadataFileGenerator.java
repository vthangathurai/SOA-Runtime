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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.CodeGenInfoFinder;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * Service Meta data File generator.
 * 
 * Generates the service_metadata.properties file.
 */

public class ServiceMetadataFileGenerator implements SourceGenerator {
	
	
	private static final String SERVICE_METADATA_FILE_DIR = "META-INF/soa/common/config";
	private static final String SERVICE_METADATA_FILE_NAME = "service_metadata.properties";
	private static final String SERVICE_INTF_PROP_FILE_NAME = "service_intf_project.properties";
	
	
	private static Logger s_logger = LogManager.getInstance(ServiceMetadataFileGenerator.class);
	
	
	private static ServiceMetadataFileGenerator s_svcMetadataFileGenerator = 
				new ServiceMetadataFileGenerator();

	private ServiceMetadataFileGenerator() {
	}

	public static ServiceMetadataFileGenerator getInstance() {
		return s_svcMetadataFileGenerator;
	}

	
	
	private Logger getLogger() {
		return s_logger;
	}
	
	
	
	public boolean continueOnError() {
		return false;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException, WSDLException {
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		Properties svcMetadataProps = new  Properties();
		String serviceName = codeGenCtx.getInputOptions().getServiceName();
		String namespace = inputOptions.getNamespace();
		
		//These values need to be derived from service_intf_project.properties file.
		//location at <projectRoot>\service_intf_project.properties
		//should not use CodegenInfoFinder for reading this file since serviceName might chnage.
		Properties sipProperty = getvaluesFromSvcIntfPropertiesFile(codeGenCtx);
		String serviceAdminName = sipProperty.getProperty(CodeGenConstants.ADMIN_NAME,codeGenCtx.getServiceAdminName());
		String serviceVersion =sipProperty.getProperty(CodeGenConstants.SERVICE_VERSION,inputOptions.getSvcCurrVersion());
		String serviceLayer = sipProperty.getProperty(CodeGenConstants.SERVICE_LAYER, inputOptions.getServiceLayer());
		String svcIntfClassName = sipProperty.getProperty(CodeGenConstants.SERVICE_INTF_CLASS_NAME);
		String wsdlUri = sipProperty.getProperty(CodeGenConstants.ORIGINAL_WSDL_URI);
		String namespacePart = sipProperty.getProperty(CodeGenConstants.SERVICE_NS_PART);
		String domainName = sipProperty.getProperty(CodeGenConstants.SVC_DOMAIN_NAME);
		
		String wsdlLoc = inputOptions.getOriginalInputType() == InputType.WSDL ? inputOptions.getInputFile() : null;
		String serviceNameFromWsdl = serviceName;
		if (!CodeGenUtil.isEmptyString(wsdlLoc)) {
			// get ServiceName and namespace from wsdl.
			//overriding serviceName would lead to side effects like chnage in structure of smp
			try{
				serviceNameFromWsdl = getFirstServiceNameFromWsdl(wsdlLoc,codeGenCtx);
				namespace = getTargetNamespacefromWsdl(wsdlLoc,codeGenCtx);
			}catch (PreProcessFailedException exception) {
				CodeGenFailedException codeGenFailedException = new 
	        	CodeGenFailedException(exception.getMessage(), exception);
				codeGenFailedException.setMessageFormatted(true);
				throw codeGenFailedException;
			}
			
		}
		
		svcMetadataProps.put(CodeGenConstants.SERVICE_NAME,serviceNameFromWsdl);
		if(! CodeGenUtil.isEmptyString(namespacePart))
			svcMetadataProps.put(CodeGenConstants.SERVICE_NS_PART, namespacePart);

		if(! CodeGenUtil.isEmptyString(domainName))
			svcMetadataProps.put(CodeGenConstants.SVC_DOMAIN_NAME, domainName);
		
		//adminName should be put inside smp file only if it is post 2.4 project
		if(! CodeGenUtil.isEmptyString(sipProperty.getProperty(CodeGenConstants.SERVICE_VERSION)))
		svcMetadataProps.put(CodeGenConstants.ADMIN_NAME, serviceAdminName);
		
		//smp needs to be updated with smp_version
		String version = sipProperty.getProperty(CodeGenConstants.SVC_INTF_PROJECT_PROPERTIES_FILE_VERSION);
		if(! CodeGenUtil.isEmptyString(version))
			svcMetadataProps.put(CodeGenConstants.SVC_CONSUMER_VERSION,version);

		svcMetadataProps.put(CodeGenConstants.SERVICE_VERSION, serviceVersion);
		svcMetadataProps.put(CodeGenConstants.SERVICE_LAYER, serviceLayer);
		svcMetadataProps.put(CodeGenConstants.ENABLE_NAMESPACE_FOLDING,Boolean.toString(inputOptions.isEnabledNamespaceFoldingSet()));
		
		if(! CodeGenUtil.isEmptyString(namespace))
			svcMetadataProps.put(CodeGenConstants.SERVICE_NAMESPACE, namespace);
		
		if( ! CodeGenUtil.isEmptyString(inputOptions.getPublicServiceName()))
			svcMetadataProps.put(CodeGenConstants.PUBLIC_SERVICE_NAME, inputOptions.getPublicServiceName());
			
		/*
		 * for input type INTERFACE get the package and interface details from the -interface option
		 * for other input types get the value from -gin and -gip options
		 */
		
		if(CodeGenUtil.isEmptyString(svcIntfClassName)){
			if(inputOptions.getOriginalInputType() == InputType.INTERFACE ){
					svcIntfClassName = CodeGenUtil.toQualifiedClassName(inputOptions.getInputFile());
			}
			else {	
				String inputPackageName = inputOptions.getGenInterfacePackage();
				String inputInterfaceName = inputOptions.getGenInterfaceName();
				if( ! ( CodeGenUtil.isEmptyString(inputPackageName) || CodeGenUtil.isEmptyString(inputInterfaceName) )  )
					svcIntfClassName = inputPackageName + "." + inputInterfaceName ;
			}
		}
		//still svcIntfClassName might be null
		if(! CodeGenUtil.isEmptyString(svcIntfClassName))
		svcMetadataProps.put(CodeGenConstants.SERVICE_INTF_CLASS_NAME, svcIntfClassName );
		
		/*
		 * Logic for deriving the Interface details
		 * 1. get the value from -gin and -gip options
		 * 2. if either/both -gin or -gip is null and the  input type is INTERFACE then  
		 *      try to derive the corresponding value from the value passed to "-interface" option
		 */ /*
			String inputPackageName   = inputOptions.getGenInterfacePackage();
			String inputInterfaceName = inputOptions.getGenInterfaceName();
			
			if( ! ( CodeGenUtil.isEmptyString(inputPackageName) || CodeGenUtil.isEmptyString(inputInterfaceName) )  ) {
				svcMetadataProps.put(CodeGenConstants.SERVICE_INTF_CLASS_NAME, inputPackageName + "." + inputInterfaceName );
			}
			else if(inputOptions.getOriginalInputType() == InputType.INTERFACE ) {
				String inputInterface       = CodeGenUtil.toQualifiedClassName(inputOptions.getInputFile());
				String derivedPackageName   = CodeGenUtil.getPackageName(inputInterface);
				
				if(CodeGenUtil.isEmptyString(inputPackageName))
				   inputPackageName   = CodeGenUtil.getPackageName(inputInterface);
				
				if(CodeGenUtil.isEmptyString(inputInterfaceName))
				   inputInterfaceName = inputInterface.substring(derivedPackageName.length()+1,inputInterface.length());
				
				svcMetadataProps.put(CodeGenConstants.SERVICE_INTF_CLASS_NAME, inputPackageName + "." + inputInterfaceName );
				
			}*/
				
		/* the following If statement has been based on the following things
		 * a) only for gen type "ServiceMetadataProps" the service_metatdata.properties file would be created.
		 * b) for gen type  "ServiceMetadataProps", the pre-process logic wont be called
		 */

		if(inputOptions.getOriginalInputType() == InputType.WSDL ){
			if(CodeGenUtil.isEmptyString(wsdlUri)){
				if (!CodeGenUtil.isEmptyString(inputOptions.getInputFile()))
					wsdlUri = inputOptions.getInputFile();
			}
			svcMetadataProps.put(CodeGenConstants.ORIGINAL_WSDL_URI, wsdlUri);	
		}
		
		
		generateSvcMetadataPropFile(svcMetadataProps, codeGenCtx);
	}

	
	
	private Properties getvaluesFromSvcIntfPropertiesFile(CodeGenContext ctx) throws CodeGenFailedException   {
		
		Properties props = new Properties();
		String root = ctx.getProjectRoot();
		if(CodeGenUtil.isEmptyString(root))
			root = ctx.getDestLocation();
		
		String pathOfSiPFile = CodeGenUtil.toOSFilePath(root)  + SERVICE_INTF_PROP_FILE_NAME;
		String SIPFilepath = CodeGenUtil.toOSFilePath(pathOfSiPFile);
		FileInputStream in = null;
		try {
			 in = new FileInputStream(SIPFilepath);
			props.load(in);
			
		} catch (FileNotFoundException e) {
			s_logger.log(Level.WARNING,"Could not find service_intf_project.properties ");
			
		} catch (IOException e) {
			s_logger.log(Level.WARNING,"Could not load service_intf_project.properties ");

		}
		finally
		{
			CodeGenUtil.closeQuietly(in);
		}
		
		return props;
	}

	private void generateSvcMetadataPropFile(
			Properties svcMetadataProps,
			CodeGenContext codeGenCtx) throws CodeGenFailedException {

		OutputStream outputStream = null;
		InputOptions options = null;
		String mDestRootPath          = null;
		
		try{
			options =  codeGenCtx.getInputOptions();
			String projectRoot = options.getProjectRoot();
			
			if(!CodeGenUtil.isEmptyString(projectRoot)){
				mDestRootPath = projectRoot;
			}
			else {
				mDestRootPath = options.getMetaSrcDestLocation();
				if(CodeGenUtil.isEmptyString(mDestRootPath) )
					mDestRootPath = codeGenCtx.getDestLocation();
			}
			
			String metaSrcPath    = CodeGenUtil.toOSFilePath(mDestRootPath) + CodeGenConstants.META_SRC_FOLDER;
			String genMetaSrcPath;
			if( InputOptions.DEFAULT_DIR.equals( codeGenCtx.getMetaSrcDestLocation()) ) {
				// Default Dir
				genMetaSrcPath = CodeGenUtil.toOSFilePath(mDestRootPath) + CodeGenConstants.GEN_META_SRC_FOLDER; 
			} else {
				// Specified Dir
				genMetaSrcPath = codeGenCtx.getMetaSrcDestLocation();
			}
			
	        String destFolderPath = 
	        	CodeGenUtil.genDestFolderPath(
	        			metaSrcPath,
		        		codeGenCtx.getServiceAdminName(),
		        		SERVICE_METADATA_FILE_DIR);
	        String smpFileLocation = destFolderPath + File.separatorChar + SERVICE_METADATA_FILE_NAME;
	        File smpFile = new File(smpFileLocation);
	        // if serviceIntf project is pre 2.4 i.e smp exists in older path, it should not be regenerated.
	        if(smpFile.exists())
	        	return;
	        else
	        	//chnage the path for smp file if it does not exist in older path
	        	destFolderPath = CodeGenUtil.genDestFolderPath(
	        			genMetaSrcPath,
	        			codeGenCtx.getServiceAdminName(),
	        			SERVICE_METADATA_FILE_DIR);

			outputStream = CodeGenUtil.getFileOutputStream(destFolderPath, SERVICE_METADATA_FILE_NAME);
			svcMetadataProps.store(outputStream, "*** Generated file, any changes will be lost upon regeneration ***");
		
			getLogger().log(Level.INFO, 
					"Successfully generated " + SERVICE_METADATA_FILE_NAME + " under " + destFolderPath);

			//calling to update the properties map
			try{
			CodeGenInfoFinder.updateMetaDataMap(options);
			}catch(Exception exception){
				String errMsg = "Could not update the properties Map";
				getLogger().log(Level.WARNING, errMsg, exception);
			}
			
			//not required to delte this file from now on
			//check for the old properties file under gen-meta-src and delete the same since we now have the new one generated under meta-src
//			String oldDestFolderPath = 
//	        	CodeGenUtil.genDestFolderPath(
//	        			fileToBeDeletedInPath,
//		        		codeGenCtx.getServiceAdminName(),
//		        		SERVICE_METADATA_FILE_DIR);
//			
//				String oldPropertiesFilePath = CodeGenUtil.normalizePath(oldDestFolderPath) + SERVICE_METADATA_FILE_NAME;
//	 			try{
//					CodeGenUtil.deleteFile(new File(oldPropertiesFilePath));
//					
//				}catch(IOException iEx) {
//					String errMsg = "Failed to delete the old properties file : " + oldPropertiesFilePath;
//					getLogger().log(Level.INFO, errMsg, iEx);
//				}
			
		} catch (IOException ioEx) {
			String errMsg = "Failed to generate : " + SERVICE_METADATA_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ioEx);
			throw new CodeGenFailedException(errMsg, ioEx);
		} finally {
			CodeGenUtil.closeQuietly(outputStream);
		}
		

	}
	public  String getTargetNamespacefromWsdl(String wsdlLoc,CodeGenContext ctx)
	throws WSDLException, PreProcessFailedException {
		
		Definition wsdldef = ctx.getWsdlDefinition()!=null?ctx.getWsdlDefinition() :WSDLUtil.getWSDLDefinition(wsdlLoc);
		ctx.setWsdlDefinition(wsdldef);
		return wsdldef.getTargetNamespace();

	}

	public  String getFirstServiceNameFromWsdl(String wsdlLoc,CodeGenContext ctx)
	throws WSDLException, PreProcessFailedException {
		
		Definition wsdldef = ctx.getWsdlDefinition()!=null ? ctx.getWsdlDefinition() :WSDLUtil.getWSDLDefinition(wsdlLoc);
		//Wsdl might have various services. Return one of the serviceName
		//codegen supports single service currently.
		@SuppressWarnings("unchecked")
		Map<QName, String> serviceMap = wsdldef.getServices();
		Set<QName> qnameSet = serviceMap.keySet();
		Iterator<QName> iterator = qnameSet.iterator();
		return 	iterator.next().getLocalPart();
		
	}


	public String getFilePath(String serviceAdminName, String interfaceName) {
   
		String filePath = CodeGenUtil.toOSFilePath(SERVICE_METADATA_FILE_DIR)+ serviceAdminName + File.separatorChar + SERVICE_METADATA_FILE_NAME ;
		return filePath;
	}

	
}
