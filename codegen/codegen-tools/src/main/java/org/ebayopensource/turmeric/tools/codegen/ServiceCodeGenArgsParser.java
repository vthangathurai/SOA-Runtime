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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.CodeGenType;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.ServiceLayer;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

import org.ebayopensource.turmeric.runtime.codegen.common.NSPkgMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.NSToPkgMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameCemcMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameToCemcMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.PkgNSMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.PkgToNSMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceLayerType;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceType;
import org.ebayopensource.turmeric.runtime.codegen.common.ToolInputType;


public class ServiceCodeGenArgsParser {
	
	private static Logger s_logger = LogManager.getInstance(ServiceCodeGenArgsParser.class);
	
	private static final String TRUE_VALUE = "true";
	private static final String CONST_OBJECT_FACT_GEN ="noObjectFactoryGeneration";
	private static final ServiceCodeGenArgsParser SINGLETON_INSTANCE = 
			new ServiceCodeGenArgsParser();
	
	//instance variables have to be reset inside method resetInstanceVariables, since this is a singleton class
	boolean isWsdlWithNoPath = false;
	boolean isSlayerValidationReq = false;
	private String m_serviceLayerValue;
	
	private ServiceCodeGenArgsParser() {		
	}
	
	private Logger getLogger() {
		return s_logger;
	}
	
	public static ServiceCodeGenArgsParser getInstance() {
		return SINGLETON_INSTANCE;
	}

	public InputOptions parse(String[] args)
			throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
  
		//since this is a singleton class, it is possible the same object would be used mutiple times, 
		//hence reset the instance level variables to their default values
		resetInstanceVariables();
		
		//Print the input arguments 
		String inputArguments = Arrays.toString(args);
		getLogger().log(Level.INFO, "Original Input Args To codegen : \n" + inputArguments );
		
		// Parse input arguments 
		InputOptions inputOptions = parseArguments(args);

		inputOptions = processInputOptions(inputOptions);
		
		//For performing defaulting , validation for defaulting . for any processing of InputOptions
		doAdditionalProcessing(inputOptions);
	    
		return inputOptions;  
	}
	
	private void resetInstanceVariables() {
		isWsdlWithNoPath = false;
		isSlayerValidationReq = false;
		m_serviceLayerValue = null;
	}
	
	private void doAdditionalProcessing(InputOptions inputOptions) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		
		
		
		//if "-wsdl" is passed without any corresponding value
		if (isWsdlWithNoPath){
			buildWsdlPath(inputOptions);
		}
		
		//validate the contents of the service layer file passed thru the -asl option
		if(!CodeGenUtil.isEmptyString(inputOptions.getSvcLayerFileLocation()))
			validateServiceLayerFile(inputOptions);
		
		//validate the value passed in -slayer ,this won't be called If both -slayer and -asl are provided together
		//since in such cases the validation would have been performed in the method validateServiceLayerFile
		if(isSlayerValidationReq){
			validateSlayerValue(inputOptions);
		}
		
		
		//defaulting of values from the properties file 
	    if( ! (    inputOptions.getCodeGenType() == CodeGenType.ServiceMetadataProps
	    		|| inputOptions.getCodeGenType() == CodeGenType.ServiceIntfProjectProps 
	    	   ) 
	      ) {
	    	
	    	populateDefaultValuesFromMetaDataFile(inputOptions);
	    }

	    //find info about useExternalServiceFactory SOAPLATFORM-497
	    if(!CodeGenUtil.isEmptyString(inputOptions.getProjectRoot()))
	    	populateExternalServiceFactoryInfo(inputOptions.getProjectRoot(),inputOptions);

		//Need to set the inputType as interface for "DispatcherForBuild".
	    if (inputOptions.getCodeGenType().equals(CodeGenType.DispatcherForBuild)) {
	    		String svcIntfClassName = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.SERVICE_INTF_CLASS_NAME,inputOptions.getServiceAdminName());
	    		inputOptions.setInputType(InputType.INTERFACE);
	    		inputOptions.setInputFile(svcIntfClassName);
	    	// Need to construct proper args to make sure test and cc.xml are
	    	// generated in right location
	    	inputOptions.setProjectRoot(inputOptions.getDestLocation());
	    	String clientName = inputOptions.getServiceAdminName() + "_Test";
	    	inputOptions.setClientName(clientName);
	    	String service_consumer_project_filePath = inputOptions.getDestLocation()+ File.separatorChar + CodeGenConstants.SERVICE_IMPL_PROPERTIES_FILE;
	    	service_consumer_project_filePath = CodeGenUtil.toOSFilePath(service_consumer_project_filePath);

	    	File consumerPropsFile = new File(service_consumer_project_filePath);
	    	String smpVersion = null;
	    	if (consumerPropsFile.exists()) {
	    		InputStream inputStream = null;
	    		Properties consumerProps = new Properties();
	    		try {
	    			inputStream = new FileInputStream(consumerPropsFile);
	    			consumerProps.load(inputStream);
	    		} catch (FileNotFoundException e) {
	    			s_logger.log(Level.INFO, inputOptions.getDestLocation()
	    					+ File.separator
	    					+ "service_consumer_project.properties file"
	    					+ "could not be read");
	    		} catch (IOException e) {
	    			s_logger.log(Level.INFO, inputOptions.getDestLocation()
	    					+ File.separator
	    					+ "service_consumer_project.properties file"
	    					+ "could not be loaded");
	    		}
	    		finally
	    		{
	    			CodeGenUtil.closeQuietly(inputStream);
	    		}
	    		smpVersion = consumerProps
	    		.getProperty(CodeGenConstants.CONS_SERVICE_IMPL_VERSION);
	    	}
	    		if(! CodeGenUtil.isEmptyString(smpVersion) && Float.valueOf(smpVersion)>=1.1)
	    				inputOptions.setEnvironment("production");
	    		
	    		return;
	    	}

	    //intf_properties file needs to be read for populating adminname etc..
	    populateDefaultValuesFromProjPropFile(inputOptions);
	    populateDefaultValuesFromConsumerProperties(inputOptions);
	    if(inputOptions.getInputType() == null){
 	    	defaultInputType(inputOptions);
 	    }

	    //While generating test, need to make sure if the impl project is pre 2.4 
	    //cc.xml should not have serviceName tag for post 2.4.
	   if(inputOptions.isGenTests())
	   {
		
		   if(! CodeGenUtil.isEmptyString(inputOptions.getProjectRoot()))
		   {
			   s_logger.log(Level.INFO,"Looking for serviceImpl.properties file ");
			   populateVersionInfofromImplPropFile(inputOptions.getProjectRoot(),inputOptions);
		   }
	   }
		
	}
	
	private void populateVersionInfofromImplPropFile(String projectRoot,
			InputOptions inputoption) {
		String implPropFilePath = CodeGenUtil.toOSFilePath(projectRoot);
		implPropFilePath = implPropFilePath
				+ CodeGenConstants.SERVICE_IMPL_PROPERTIES_FILE;
		File svcImplPropFile = new File(CodeGenUtil
				.toOSFilePath(implPropFilePath));
		if (!svcImplPropFile.exists()) {
			s_logger.log(Level.INFO, implPropFilePath + " does not exist");
			return;
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(svcImplPropFile);
			Properties svcImplProps = new Properties();
			svcImplProps.load(inputStream);
			String svcImplVersion = svcImplProps.getProperty(
					CodeGenConstants.CONS_SERVICE_IMPL_VERSION, "1.0").trim();
			if (Float.valueOf(svcImplVersion) >= 1.1) {
				// serviceName tag is not required
				inputoption.setServiceNameRequired(false);
				s_logger.log(Level.INFO,
						"ServiceName  tag is not required in cc.xml ");
			}

		} catch (FileNotFoundException e) {
			s_logger.log(Level.WARNING, e.getCause() + e.getMessage());
		} catch (IOException e) {
			s_logger.log(Level.WARNING, e.getCause() + e.getMessage());
		} finally {
			CodeGenUtil.closeQuietly(inputStream);
		}
	}

	/**
	 * Scans the file service_impl_project.properties and finds out the value for property useExternalServiceFactory.
	 * 
	 * @param projectRoot
	 * @param inputoption
	 */
	private void populateExternalServiceFactoryInfo(String projectRoot,
			InputOptions inputoption) {
		String implPropFilePath = CodeGenUtil.toOSFilePath(projectRoot);
		implPropFilePath = implPropFilePath
				+ CodeGenConstants.SERVICE_IMPL_PROPERTIES_FILE;
		File svcImplPropFile = new File(CodeGenUtil
				.toOSFilePath(implPropFilePath));
		if (!svcImplPropFile.exists()) {
			s_logger.log(Level.INFO, implPropFilePath + " does not exist");
			return;
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(svcImplPropFile);
			Properties svcImplProps = new Properties();
			svcImplProps.load(inputStream);
			String isFactoryModeString = svcImplProps.getProperty("useExternalServiceFactory");
			if( !(CodeGenUtil.isEmptyString(isFactoryModeString) ) ){
				isFactoryModeString = isFactoryModeString.trim();
			}
			s_logger.log(Level.INFO,
			"Is factory mode string is identified ="+ isFactoryModeString);
			if("true".equalsIgnoreCase(isFactoryModeString)){
				inputoption.setUseExternalServiceFactory(true);
			}
		} catch (FileNotFoundException e) {
			s_logger.log(Level.WARNING, "populateExternalServiceFactoryInfo " + e.getMessage());
		} catch (IOException e) {
			s_logger.log(Level.WARNING, "populateExternalServiceFactoryInfo " + e.getMessage());
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					s_logger.log(Level.WARNING, "populateExternalServiceFactoryInfo. Could not close inputstream "
							+ e.getCause());
				}
		}
	}

	private void buildWsdlPath(InputOptions inputOptions)
	         throws BadInputValueException{
		
		String metaSrcDestLoc = inputOptions.getMetaSrcDestLocation();
		String destLocation   = inputOptions.getDestLocation();
		if (CodeGenUtil.isEmptyString(destLocation))
			destLocation = "."; //defaulting 
		
		String projectRoot = inputOptions.getProjectRoot();
		if(!CodeGenUtil.isEmptyString(projectRoot)){
			destLocation = projectRoot;
		}
		
		if (CodeGenUtil.isEmptyString(metaSrcDestLoc)) {
			// set it to default
			metaSrcDestLoc = CodeGenUtil.genDestFolderPath(destLocation, CodeGenConstants.META_SRC_FOLDER); 
			//defaulting to the current directory if values for options -mdest and -dest are null(or empty).
		} 
		
		String WSDLpath  =  CodeGenUtil.toOSFilePath(metaSrcDestLoc);
		WSDLpath        +=  CodeGenInfoFinder.getPathforNonModifiableArtifact(inputOptions.getServiceAdminName(), "WSDL");
		
		inputOptions.setInputFile(WSDLpath);
		
	}

	/**
	 * validates the contents of the service layer file passed thru the -asl option
	 * @param inputOptions
	 * @throws BadInputOptionException
	 * @throws BadInputValueException
	 */
	private void validateServiceLayerFile(InputOptions inputOptions)
			throws BadInputOptionException, BadInputValueException,MissingInputOptionException {
		List<String> serviceLayersList = null;
		Pattern regexPattern = null;
		Matcher regexMatcher = null;

		/*
		 * The pattern for the service layer name (break down of the regular expression used
		 *  a) \p{Alnum} -> represent Alpha Numerals 
		 *  b) [_-] -> underscore , dash 
		 *  c) \p{Blank} -> space or tab 
		 *  d) * -> zero or more occurances of any of the above
		 */
		regexPattern = Pattern.compile("[\\p{Alnum}[_-]\\p{Blank}]*");
		String serviceLayerFile = inputOptions.getSvcLayerFileLocation();

		try {
			serviceLayersList = CodeGenInfoFinder.getServiceLayers(serviceLayerFile);
			
			if (serviceLayersList.size() == 0)
				throw new BadInputValueException(
						"The file specified through the -asl option is empty or invalid.please check. "
								+ "The file is in location " + serviceLayerFile);

			for (String serviceLayer : serviceLayersList) {
				regexMatcher = regexPattern.matcher(serviceLayer);
				if (!regexMatcher.matches()) {
					String errMsg = "The service layer name "
							+ serviceLayer
							+ " specified in the Service Layer file (-asl) is not valid."
							+ "It should contain only \n a)Alpha Numeral \n b) space(tab) \n c) underscore \"_\" \n d) dash \"-\" ";
					throw new BadInputValueException(errMsg);
				}
			}

			
			//for the gen type "GlobalServerConfig" , we can ignore -slayer defaulting and validation
			if (inputOptions.getCodeGenType()== CodeGenType.GlobalServerConfig)
			   return;
			   
			/* 
			 * this logic is valid until plugin makes sure that it always passes -slayer when -asl is passed
			 * Logic for the outermost IF part (when -slayer is not passed)
			 * 1. If the List has only one layer, then make it the current service layer
			 * 2. If the List has more than one value 
			 *    2.1 Get the default service layer value 
			 *    2.2 If the default value is null then set the service layer as the last one in the List (since BUSINESS comes at the last)
			 *    2.3 if the default value is not null then set the default value as the current layer if it exists in the List as well
			 *    2.4 if the default value is not null and it does not exist in the List, then make the last one in the List as the current service layer
			 * Logic for the outermost ELSE part
			 * 1.validate the passed -slayer value against the List
			 */
			if (CodeGenUtil.isEmptyString(m_serviceLayerValue)) {
				if (serviceLayersList.size() == 1)
					inputOptions.setServiceLayer(serviceLayersList.get(0));
				else {
					
					String defaultServiceLayer = inputOptions.getServiceLayer();
					String lastLayer = serviceLayersList.get(serviceLayersList.size()-1);
					
					if(CodeGenUtil.isEmptyString(defaultServiceLayer))
						inputOptions.setServiceLayer(lastLayer);
					else{
						if (serviceLayersList.contains(defaultServiceLayer))
							inputOptions.setServiceLayer(defaultServiceLayer);
						else
							inputOptions.setServiceLayer(lastLayer);
							
					}
					/*
					String errMsg = "Error: The System is unable to default a value for Service Layer since the file mentioned in option "
							+ InputOptions.OPT_SVC_LAYER_FILE_LOC
							+ " has more than one Service Layer defined. \n Resolution: Please provide a value for the option "
							+ InputOptions.OPT_SVC_LAYER;
					throw new MissingInputOptionException(errMsg);
					*/
				}
			} else {
				if (serviceLayersList.contains(m_serviceLayerValue))
					inputOptions.setServiceLayer(m_serviceLayerValue);
				else
					throw new BadInputOptionException(
							"Invalid service layer specified : "
									+ m_serviceLayerValue);
			}
			
			//no need to call the method validateSlayerValue in this case, since -slayer has already been validated thru this flow
			isSlayerValidationReq = false;
			
		} catch (CodeGenFailedException ex) {
			throw new BadInputOptionException(ex.getMessage(), ex);
		}
		
		

	}
	
	/*
	 * validates the -slayer value against the -asl value if present or it validates the -slayer against the default file
	 */
	private void validateSlayerValue(InputOptions inputOptions) 
	           throws BadInputOptionException {
	   
		String serviceLayerFile =  inputOptions.getSvcLayerFileLocation();
		List<String> serviceLayersList = null;
		
		try{
			if (CodeGenUtil.isEmptyString(serviceLayerFile))
				serviceLayersList   = CodeGenInfoFinder.getServiceLayersFromDefaultFile();
			else
				serviceLayersList   = CodeGenInfoFinder.getServiceLayers(serviceLayerFile);

			if (serviceLayersList.contains(m_serviceLayerValue))
				inputOptions.setServiceLayer(m_serviceLayerValue);
			else
				throw new BadInputOptionException("Invalid service layer specified : " + m_serviceLayerValue);	
			
			}catch(CodeGenFailedException ex){
				throw new BadInputOptionException(ex.getMessage(),ex);
			}
		
		
	}
	
	private void populateDefaultValuesFromMetaDataFile(InputOptions inputOptions)
	   throws BadInputValueException{
		
		try{
			CodeGenInfoFinder.updateMetaDataMap(inputOptions);
			
		}
		catch(CodeGenFailedException codeGenex){
			throw new BadInputValueException(codeGenex.getMessage(),codeGenex);
		}catch(Exception exception){
			String errMsg = "Could not update the properties Map";
			getLogger().log(Level.WARNING, errMsg, exception);
			return;
		}
		
		
		String serviceAdminName = inputOptions.getServiceAdminName();
		
	    if(inputOptions.isWSDLBasedService()) {
	    	if(CodeGenUtil.isEmptyString(inputOptions.getWSDLURI())){
	    		String wsdlURI = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.ORIGINAL_WSDL_URI,serviceAdminName);
	    		inputOptions.setWSDLURI(wsdlURI);	
	    	}
	    }
        // Added for AdCommerce Support,If inputOption does not have publicServiceName value.Default it from <service_metadata>.properties
	    if(CodeGenUtil.isEmptyString(inputOptions.getPublicServiceName()))
	    {
	    String publicServiceNameFromProps = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.PUBLIC_SERVICE_NAME,serviceAdminName);
	    inputOptions.setPublicServiceName(publicServiceNameFromProps);
	    }

    	String interfaceFullName = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.SERVICE_INTF_CLASS_NAME,serviceAdminName);
    	String interfaceName     = null;
    	String interfacePackage  = null;
    	if(interfaceFullName != null){
              int position     = interfaceFullName.lastIndexOf(".");
              if(position!=-1)
              {
              interfacePackage = interfaceFullName.substring(0, position);
              interfaceName    = interfaceFullName.substring(position+1);
              }
    	}

	    
	    if(CodeGenUtil.isEmptyString(inputOptions.getGenInterfacePackage())){
	    	inputOptions.setGenInterfacePackage(interfacePackage);
	    }
	    
	    if(CodeGenUtil.isEmptyString(inputOptions.getGenInterfaceName())){
	    	inputOptions.setGenInterfaceName(interfaceName);
	    }
	    String serviceNs = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.SERVICE_NAMESPACE,serviceAdminName);
	    if(CodeGenUtil.isEmptyString(inputOptions.getNamespace())){
	    	inputOptions.setNamespace(serviceNs);
	    }
	    
	    	
	    	
	}
	
	private void populateDefaultValuesFromConsumerProperties(
			InputOptions inputOptions) throws BadInputValueException {

		FileInputStream inputStream = null;
		
		String projectRoot = CodeGenUtil.isEmptyString(inputOptions.getProjectRoot()) ? inputOptions.getDestLocation() : inputOptions.getProjectRoot();
		String service_consumer_project_filePath = projectRoot+ File.separatorChar + CodeGenConstants.SERVICE_CONSUMER_PROPS_FILE;
		service_consumer_project_filePath = CodeGenUtil.toOSFilePath(service_consumer_project_filePath);
		
		File consumerPropsFile = new File(service_consumer_project_filePath);
		// service_consumer_project.properties
		// does not exist => need not to default its value
		if (!consumerPropsFile.exists()) {
			getLogger().log(Level.INFO,	"envMapper not populated from service_consumer_project.properties");
			getLogger().log(Level.INFO,	"not_generate_base_consumer property for consumer also not populated from  service_consumer_project.properties");
			return;
		}
		try {
			
			inputStream = new FileInputStream(consumerPropsFile);
			Properties consumerProps = new Properties();
			consumerProps.load(inputStream);
			String envmapperValue = consumerProps.getProperty(CodeGenConstants.ENVMAPPER_PROP);
			if(envmapperValue!=null)
				envmapperValue =envmapperValue.trim();
			getLogger().log(Level.INFO,"envMapper value defaulted from service_consumer_project.properties");
			// if envMapper value is already present, then value should not be
			// defaulted from service_consumer_project.properties
			if (!CodeGenUtil.isEmptyString(envmapperValue))
				inputOptions.setEnvironmentMapper(envmapperValue);
			
			String consumerVersion = consumerProps.getProperty(CodeGenConstants.CONS_PROJECT_PROPERTIES_FILE_VERSION, "1.0").trim();
			if(Float.valueOf(consumerVersion)>=1.1)
				inputOptions.setServiceNameRequired(false);
			
			getLogger().log(Level.INFO,	"looking for not_generate_base_consumer property inside  service_consumer_project.properties");
			// This would determine if BaseConsumer generation is required
			if (isServiceAdminNamePresent(inputOptions.getServiceAdminName(),consumerProps))
				inputOptions.setIsBaseConsumerGenertionReq(false);
			
		} catch (Exception e) {
			throw new BadInputValueException(
					"could not populate EnvMapper value...service_consumer_project.properties does not exist");
		}
		finally {
			CodeGenUtil.closeQuietly(inputStream);
		}

	}
	
	/**
	 * This methos checks if a particular svcName is present inside consumer.properties under
	 * not_generate_base_consumer property and BaseConsumer generation is required for it.
	 * @param svcAdminName
	 * @param consumerProps
	 * @return
	 */
	private boolean isServiceAdminNamePresent(String svcAdminName,
			Properties consumerProps) {
		// returning true would mean BAseConsumer generation is not required
		if (consumerProps == null) {
			s_logger.log(Level.INFO, "baseConsumer generation  required for " + svcAdminName);
			return false;
		}
		String NoBaseConsumerProp = consumerProps.getProperty(CodeGenConstants.NO_BASE_CONSUMERPROP);
		if (CodeGenUtil.isEmptyString(NoBaseConsumerProp)) {
			s_logger.log(Level.INFO,"not_generate_base_consumer property value not found..Base consumer generation required for "	+ svcAdminName);
			return false;
		} 
		else {
			String[] allsvcAdminNames = NoBaseConsumerProp.split(",");
			for (String currentSvcAdminName : allsvcAdminNames) {
				if (currentSvcAdminName.trim().equalsIgnoreCase(svcAdminName)) {
					s_logger.log(Level.INFO,
									"Found Entry for "
											+ svcAdminName
											+ "under not_generate_base_consumer property...BaseConsumer generation is not required for "
											+ svcAdminName);
					return true;
				}
			}
		}
		s_logger.log(Level.INFO,
						" Entry Not found  for "
								+ svcAdminName
								+ "under not_generate_base_consumer property...BaseConsumer generation is  required for "
								+ svcAdminName);
		return false;
	}
	
	
	
	private void populateDefaultValuesFromProjPropFile(InputOptions inputOptions)
	   throws BadInputValueException{
		
		try{
			CodeGenInfoFinder.updateSvcIntfProjPropMap(inputOptions);
		}
		catch(Exception exception){
			String errMsg = "Could not update the Service Interface Project properties (service_intf_project.properties) Map";
			getLogger().log(Level.WARNING, errMsg, exception);
			return;
		}
		
		String initialAdminName = inputOptions.getServiceAdminName();
		
		if(CodeGenUtil.isEmptyString(inputOptions.getServiceLocation()))
			inputOptions.setServiceLocation(CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.SERVICE_LOCATION,initialAdminName));
		
        //envMapper would also be present in sipp for post 2.4 projects
		String envmapperValue = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.ENVMAPPER_PROP, initialAdminName);
		//empty value for envmapper means pre 2.4 project.
		if(! CodeGenUtil.isEmptyString(envmapperValue))
		{
			inputOptions.setEnvironmentMapper(envmapperValue);
			s_logger.log(Level.INFO, "EnvMapper value read from " + inputOptions.getProjectRoot() + File.separator +"service_intf_project.properties");
		}
		else
		{
			s_logger.log(Level.INFO, "EnvMapper value not present in  " + inputOptions.getProjectRoot() + File.separator +"service_intf_project.properties");

		}
		String ns2pkgFromIntfProperties = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.NS_2_PKG,initialAdminName);
		if(!CodeGenUtil.isEmptyString(ns2pkgFromIntfProperties)){
			ns2pkgFromIntfProperties = ns2pkgFromIntfProperties.replaceAll("[|]", "=");
			String ns2pkgStr = inputOptions.getNS2Pkg();
			if(CodeGenUtil.isEmptyString(ns2pkgStr))
				ns2pkgStr = ns2pkgFromIntfProperties;
			else
				ns2pkgStr += "," + ns2pkgFromIntfProperties;
			
			inputOptions.setNS2Pkg(ns2pkgStr);
		}
		// if -noObjectFactoryGeneration is passed defaulting does not happen from propertiesFile.
		if(! inputOptions.isObjectFactoryDeletionOptionPassed())
		{
		String isObjectFactoryGenReq = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CONST_OBJECT_FACT_GEN, initialAdminName);
        if(!CodeGenUtil.isEmptyString(isObjectFactoryGenReq) && isObjectFactoryGenReq.equalsIgnoreCase("true"))
        	inputOptions.setObjectFactoryTobeDeleted(true);
		}
        
		if(CodeGenUtil.isEmptyString(inputOptions.getCommonTypesNS())){
			inputOptions.setCommonTypesNS(CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.CTNS,initialAdminName));
		}
		// check if enableNamespace folding option is set to true.
		// except for genType WsdlConversionToMns
		if (!(inputOptions.isEnabledNamespaceFoldingSet()) && !(inputOptions.getCodeGenType()
				.equals(InputOptions.CodeGenType.WsdlConversionToMns))) {
			String enableNamespaceValue = CodeGenInfoFinder
			.getPropertyFromSvcIntfProjProp(
					CodeGenConstants.ENABLE_NAMESPACE_FOLDING,
					initialAdminName);
			// check if enableNmaespaceFolding prop exists
			if (!(CodeGenUtil.isEmptyString(enableNamespaceValue))) {
				if(enableNamespaceValue.equalsIgnoreCase(TRUE_VALUE))
					inputOptions.setEnabledNamespaceFolding(true);
				else
					inputOptions.setEnabledNamespaceFolding(false); 
			} else {
				inputOptions.setEnabledNamespaceFolding(false);
			}
		}

		//values in service_intf_props are put using serviceAdminName(which might be serviceName,if not available) as key hence need to be retrieved in similar way.
		String svcIntfProjPropsVersion = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.SVC_INTF_PROJECT_PROPERTIES_FILE_VERSION,initialAdminName);
		if(CodeGenUtil.isEmptyString(svcIntfProjPropsVersion))
			svcIntfProjPropsVersion = "1.0";
		try {
			
			float svcIntfFileVersion = Float.valueOf(svcIntfProjPropsVersion).floatValue();
			if(svcIntfFileVersion >=1.1 )
				inputOptions.setIsConsumerAnInterfaceProjectArtifact(true);
		} catch (NumberFormatException e) {
			String errMsg =  "The properties files service_interface_project.properties file has the property " +CodeGenConstants.SVC_INTF_PROJECT_PROPERTIES_FILE_VERSION + " in non-numeric format.The format should be m.n Eg: 1.1, 1.2 " ;
			getLogger().log(Level.WARNING,errMsg , e);
			throw new BadInputValueException(errMsg,e);
		}
		
		String adminNameFromIntfProps = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.ADMIN_NAME,initialAdminName);
		if(! CodeGenUtil.isEmptyString(adminNameFromIntfProps))
			inputOptions.setServiceAdminName(adminNameFromIntfProps);
		
		if(inputOptions.isWSDLBasedService()) {
	    	if(CodeGenUtil.isEmptyString(inputOptions.getWSDLURI())){
	    		String wsdlURI = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.ORIGINAL_WSDL_URI,initialAdminName);
	    		inputOptions.setWSDLURI(wsdlURI);	
	    	}
	    }
		
		String interfaceFullName = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.SERVICE_INTF_CLASS_NAME,initialAdminName);
    	String interfaceName     = null;
    	String interfacePackage  = null;
    	if(interfaceFullName != null){
              int position     = interfaceFullName.lastIndexOf(".");
              if(position!=-1)
              {
              interfacePackage = interfaceFullName.substring(0, position);
              interfaceName    = interfaceFullName.substring(position+1);
              }
    	}
    	  if(CodeGenUtil.isEmptyString(inputOptions.getGenInterfacePackage())){
  	    	inputOptions.setGenInterfacePackage(interfacePackage);
  	    }
    	  if(CodeGenUtil.isEmptyString(inputOptions.getGenInterfaceName())){
  	    	inputOptions.setGenInterfaceName(interfaceName);
  	    }
    	  
    	String sharedConsumerPackage = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.PROPERTY_SHARED_CONSUMER_SHORTER_PATH, initialAdminName);
    	if(!CodeGenUtil.isEmptyString(sharedConsumerPackage)) {
    	    // Only set if a value exists.
    	    inputOptions.setShortPathForSharedConsumer(sharedConsumerPackage);  
    	}
	}

	private void defaultInputType(InputOptions inputOptions)
	throws BadInputValueException{
		 //Defaulting the Input Type if it is null 
	    
	    	String serviceAdminName = inputOptions.getServiceAdminName();
	    	String projectSourceType = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.INTERFACE_SOURCE_TYPE, serviceAdminName);
	    	String wsdlURI = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.ORIGINAL_WSDL_URI,serviceAdminName);
	    	String interfaceClassWithPkg = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.SERVICE_INTF_CLASS_NAME,serviceAdminName);
	    	
	    	
	    	
	    	if(!CodeGenUtil.isEmptyString(projectSourceType)){
	    		if(InputOptions.InterfaceSourceType.BLANK_WSDL.value().equalsIgnoreCase(projectSourceType)  
	    	    		|| InputOptions.InterfaceSourceType.WSDL.value().equalsIgnoreCase(projectSourceType)){
	    			inputOptions.setInputType(InputType.WSDL);
	    			inputOptions.setOriginalInputType(InputType.WSDL);
		    		if(CodeGenUtil.isEmptyString(inputOptions.getInputFile()))
		    		    buildWsdlPath(inputOptions);
	    		}
	    		else if(InputOptions.InterfaceSourceType.INTERFACE.value().equalsIgnoreCase(projectSourceType)){
	    			inputOptions.setInputType(InputType.INTERFACE);
	    			inputOptions.setOriginalInputType(InputType.INTERFACE);
		    		if(CodeGenUtil.isEmptyString(inputOptions.getInputFile()))
		    			inputOptions.setInputFile(interfaceClassWithPkg);
	    		}
	    		
	    	}
	    	else if(!CodeGenUtil.isEmptyString(wsdlURI)) {
	    		inputOptions.setInputType(InputType.WSDL);
	    		inputOptions.setOriginalInputType(InputType.WSDL);
	    		if(CodeGenUtil.isEmptyString(inputOptions.getInputFile()))
	    		    buildWsdlPath(inputOptions);
	    	}
	    	else if (!CodeGenUtil.isEmptyString(interfaceClassWithPkg)){
	    		inputOptions.setInputType(InputType.INTERFACE);
	    		inputOptions.setOriginalInputType(InputType.INTERFACE);
	    		if(CodeGenUtil.isEmptyString(inputOptions.getInputFile()))
	    			inputOptions.setInputFile(interfaceClassWithPkg);
	    	}
	    
	}
	
	private InputOptions parseArguments(String[] args) throws BadInputOptionException{

		if (args == null || args.length == 0) {
			// print usage information and exit
			throw new BadInputOptionException();
		}

		InputOptions options = new InputOptions();
		int i = 0;
		int argsLength = args.length;

		while (i < argsLength) {
			String optName = (args[i] == null) ? null : args[i].toLowerCase();

			if (InputOptions.OPT_HELP.equals(optName)) {
				// print usage information and exit
				throw new BadInputOptionException();
			} 
			else if (InputOptions.OPT_SRVC_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setServiceName(args[i]);
			} 
			else if (InputOptions.OPT_ADMIN_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setServiceAdminName(args[i]);
			} 
			else if (InputOptions.OPT_JAVA_HOME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setJavaHome(args[i]);
			} 
			else if (InputOptions.OPT_JDK_HOME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setJdkHome(args[i]);
			} 
			else if (InputOptions.OPT_SVC_NAME_SPACE.equals(optName)) {
				i = getNextOptionIndex(i, args);
				//if namespace option is used, value can not be empty.
				if(CodeGenUtil.isEmptyString(args[i]))
					throw new BadInputOptionException("namespace can not be empty string");
				options.setNamespace(args[i]);
			} 
			else if (InputOptions.OPT_VERBOSE.equals(optName)) {
				options.setVerbose(true);
			} 
			else if (InputOptions.OPT_SRC_DIR.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setSrcLocation(args[i]);
			} 
            else if (InputOptions.OPT_META_SRC_DIR.equals(optName)) {
                i = getNextOptionIndex(i, args);
                options.setMetaSrcLocation(args[i]);
            } 
			else if (InputOptions.OPT_DEST_DIR.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setDestLocation(args[i]);
			} 
			else if (InputOptions.OPT_JAVA_SRC_GEN_DIR.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setJavaSrcDestLocation(args[i]);
			} 
			else if (InputOptions.OPT_META_SRC_GEN_DIR.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setMetaSrcDestLocation(args[i]);
			} 
			else if (InputOptions.OPT_BIN_DIR.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setBinLocation(args[i]);
			} 
			else if (InputOptions.OPT_ENABLEDNAMESPACE_FOLDING.equals(optName)) {
				options.setEnabledNamespaceFolding(true);
			} 
			else if (InputType.INTERFACE.value().equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setInputFile(args[i]);
				options.setInputType(InputType.INTERFACE);
				options.setOriginalInputType(InputType.INTERFACE);
			} 
			else if (InputType.CLASS.value().equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setInputFile(args[i]);
				options.setInputType(InputType.CLASS);
				options.setOriginalInputType(InputType.CLASS);
			} 
			else if (InputType.XML.value().equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setInputFile(args[i]);
				options.setInputType(InputType.XML);
				options.setOriginalInputType(InputType.XML);
			} 
			else if (InputType.WSDL.value().equals(optName)) {
				i = getNextOptionIndex(i, args);
				if (!args[i].startsWith("-")) {
				   options.setInputFile(args[i]);
				   options.setInputType(InputType.WSDL);
				   options.setOriginalInputType(InputType.WSDL);
				}else
				{   
					i--;
					options.setInputType(InputType.WSDL);
					options.setOriginalInputType(InputType.WSDL);
					isWsdlWithNoPath = true;
				    /* the path if set over here at this stage would be incomplete since we need the meta source destination directory's
				     * path to derive the full path of the WSDL file, and since the value for that option (-mdest) is usually provided
				     * only after -wsdl option , infact it can come in any order from the command line, since it is at the users
				     * discretion, so ideally for this scenario we can set the file path for the WSDL file only after processing all the
				     * options. Hence a new method buildWsdlPath has been added to validate this.
				    */
				}
				
			} 
			else if (InputOptions.OPT_CODE_GEN_TYPE.equals(optName)) {
				i = getNextOptionIndex(i, args);
				CodeGenType codeGenType = CodeGenType.getCodeGenType(args[i]);
				if (codeGenType == null) {
					throw new BadInputOptionException(
							"Invalid code gen type specified : " + args[i]);
				}
				options.setCodeGenType(codeGenType);
			} 
			else if (InputOptions.OPT_SVC_IMPL_CLASS_NAME.equals(optName)) {
				int nextIndex = getNextOptionIndex(i, args); 
				String implClassName = args[nextIndex];

				if(implClassName.startsWith("-")){
					getLogger().log(Level.INFO, "Option -sicn is not passed with corresponding value. Hence ignoring the option.");
				}else{
					options.setServiceImplClassName( implClassName );
					i =	nextIndex;
				}
			}
			else if (InputOptions.OPT_GEN_INTERFACE_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setGenInterfaceName(args[i]);
			} 
			else if (InputOptions.OPT_CONSUMER_ID.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setConsumerId(args[i]);
			} 
			else if (InputOptions.OPT_GEN_INTERFACE_PACKAGE.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setGenInterfacePackage(args[i]);
			} 
			else if (InputOptions.OPT_CLIENT_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setClientName(args[i]);
			} 
			else if (InputOptions.OPT_SVC_CURR_VERSION.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setSvcCurrVersion(args[i]);
			} 
			else if (InputOptions.OPT_SCFG_GROUP_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setServerCfgGroupName(args[i]);
			} 
			else if (InputOptions.OPT_CCFG_GROUP_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setClientCfgGroupName(args[i]);
			} 
			else if (InputOptions.OPT_GEN_SVC_SKELETON.equals(optName)) {
				options.setIsGenSkeleton(true);
			} 
			else if (InputOptions.OPT_IMPL_CSI.equals(optName)) {
				options.setImplCommonSvcInterface(true);
			} 
			else if (InputOptions.OPT_ADD_VI.equals(optName)) {
				options.setAddVI(true);
			} 
			else if (InputOptions.OPT_SVC_LOC.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setServiceLocation(args[i]);
			} 
			else if (InputOptions.OPT_WSDL_LOC.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setWSDLLocation(args[i]);
			} 
			else if (InputOptions.OPT_GEN_TESTS.equals(optName)) {
				options.setGenTests(true);
			} 
			else if (InputOptions.OPT_DONT_PROMPT.equals(optName)) {
				options.setIsDontPrompt(true);
			} 
			else if (InputOptions.OPT_IMPL_CSI.equals(optName)) {
				options.setImplCommonSvcInterface(true);
			} 
			else if (InputOptions.OPT_DOC_LIT_WRAPPED.equals(optName)) {
				options.setIsDocLitWrapped(true);
			} 
			else if (InputOptions.OPT_SVC_LAYER.equals(optName)) {
				i = getNextOptionIndex(i, args);
				m_serviceLayerValue = args[i];
				//since -slayer and -asl may come in any order from command line, we will postpone the validation of the
				//value passed for -slayer
				isSlayerValidationReq = true;
			} 
			else if (InputOptions.OPT_OP_NAME_CEMC_MAP.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setOpNameToCemcMapString(args[i]);
			} 
			else if (InputOptions.OPT_NO_GLOBAL_CONFIG.equals(optName)) {
				options.setIsNoGlobalConfig(true);
				getLogger().log(Level.INFO, "The codegen option -ngc is now deprecated.");
			} 
			else if (InputOptions.OPT_PKG_2_NS.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setPackageToNSMap(args[i]);
			} 
			else if (InputOptions.OPT_NO_COMPILE.equals(optName)) {
				options.setIsNoCompile(true);
			} 
			else if (InputOptions.OPT_GEN_SHARED_CONSUMER.equals(optName)) {
			    options.setIsGenerateSharedConsumer(true);
			}
			else if (InputOptions.OPT_PACKAGE_SHARED_CONSUMER.equals(optName)) {
			    i = getNextOptionIndex(i, args);
			    options.setSharedConsumerPackage(args[i]);
			}
			else if (InputOptions.OPT_CONTINUE_ON_ERROR.equals(optName)) {
				options.setIsContinueOnError(true);
			} 
			else if (InputOptions.OPT_SVC_LAYER_FILE_LOC.equals(optName)){
				i = getNextOptionIndex(i,args);
				options.setSvcLayerFileLocation(args[i]);
			}
			else if(InputOptions.OPT_PROJECT_ROOT.equals(optName)){
				i = getNextOptionIndex(i,args);
				options.setProjectRoot(args[i]);
				options.setShouldMigrate(true);
			}
			else if(InputOptions.OPT_USE_INTERFACE_JAR.equals(optName)){
				options.setUseInterfaceJar(true);
				options.setShouldMigrate(true);
			} 
			else if (InputOptions.OPT_NS_2_PKG.equals(optName)){
				i = getNextOptionIndex(i,args);
				options.setNS2Pkg(args[i]);
			}
			else if (InputOptions.OPT_LOG_CONFIG_FILE.equals(optName)){
				i = getNextOptionIndex(i,args);
				options.setLogConfigFile(args[i]);
			}
			else if (InputOptions.OPT_BINDING_FILE.equalsIgnoreCase(optName)){
				i = getNextOptionIndex(i, args);
				options.getBindingFileNames().add(args[i]);
			}
			else if (InputOptions.OPT_HTTP_PROXY_HOST.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setHttpProxyHost(args[i]);
			}
			else if (InputOptions.OPT_HTTP_PROXY_PORT.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setHttpProxyPort(args[i]);
			} 
			else if (InputOptions.OPT_OVER_WRITE_IMPLEMENTATION_SKELETON.equals(optName)){
				options.setOverWriteSkeleton(true);
			}
			else if (InputOptions.OPT_COMMON_TYPES_NS.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setCommonTypesNS(args[i]);
			} 
			else if (InputOptions.OPT_PUBLIC_SVC.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setPublicServiceName(args[i]);
			} 
			else if (InputOptions.OPT_ENV_MAPPER.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setEnvironmentMapper(args[i]);
			} 
			else if (InputOptions.OPT_ENV_NAME.equals(optName)) {
				i = getNextOptionIndex(i, args);
				options.setEnvironment(args[i]);
			} 
			else if (InputOptions.OPT_OBJECTFACT_GEN.toLowerCase().equals(optName)) {
				i = getNextOptionIndex(i, args);
				boolean booleanValue = Boolean.parseBoolean(args[i]);
				options.setObjectFactoryDeletionOptionPassed(true);
				options.setObjectFactoryTobeDeleted(booleanValue);
			} 
			else {
				String errMsg = "Unknown option specified, option name : " + optName;
				throw new BadInputOptionException(errMsg);
			} 
			// move to next option
			i++;
		}

		return options;
	}
	
	public static String getOption(String[] args, String optionName) {
		for (int i = 0; i < args.length; i++) {
			String option = args[i];
			if (option.equals(optionName)) {
				return args[i + 1];
			}
		}
		return null;
	}

	public static int getNextOptionIndex(int currentOptIndex, String[] args)
			throws BadInputOptionException {
		int nextOptionIndex = currentOptIndex + 1;
		if (nextOptionIndex >= args.length) {
			throw new BadInputOptionException("Missing parameter for '"
					+ args[currentOptIndex] + "' option.");
		}

		return nextOptionIndex;
	}
	
	

	private InputOptions processInputOptions(InputOptions inputOptions)
			throws BadInputValueException {

		ServiceCodeGenDefType svcCodeGenDefType = null;

		String pkgNSMapStr = inputOptions.getPackageToNSMap();
		if (!CodeGenUtil.isEmptyString(pkgNSMapStr)) {
			PkgToNSMappingList pkgToNSMappings = parsePackageNSString(pkgNSMapStr);
			inputOptions.setPkgNSMappings(pkgToNSMappings);
		} 
		
		String nsPkgMapStr = inputOptions.getNS2Pkg();
		if(!CodeGenUtil.isEmptyString(nsPkgMapStr)){
			NSToPkgMappingList nsToPkgMappingList = parseNSPackageString(nsPkgMapStr);
			inputOptions.setNSToPkgMappingList(nsToPkgMappingList);
		}
		
		String opNameCemcStr = inputOptions.getOpNameToCemcMapString();
		if (!CodeGenUtil.isEmptyString(opNameCemcStr)) {
			OpNameToCemcMappingList opNameTpCemcMappings = parseOpNameCemcString(opNameCemcStr);
			inputOptions.setOpNameToCemcMappings(opNameTpCemcMappings);
		}
		

		if (inputOptions.getInputType() == InputType.XML) {
			svcCodeGenDefType = CodeGenPreProcessor
					.parseCodeGenXml(inputOptions.getInputFile());

			inputOptions.setSvcCodegenDefType(svcCodeGenDefType);

			ToolInputType toolInput = svcCodeGenDefType.getToolInputInfo();
			if (toolInput != null) {
				if (inputOptions.getCodeGenType() == null) {
					inputOptions.setCodeGenType(CodeGenType
							.getCodeGenType(toolInput.getGenType().value()));
				}
				if (inputOptions.getSrcLocation() == InputOptions.DEFAULT_DIR) {
					inputOptions.setSrcLocation(toolInput.getSrcLocation());
				}
				if (inputOptions.getDestLocation() == InputOptions.DEFAULT_DIR) {
					inputOptions.setDestLocation(toolInput.getDestLocation());
				}
				if (inputOptions.getBinLocation() == null) {
					inputOptions.setBinLocation(toolInput.getBinLocation());
				}
				if (inputOptions.getServiceImplClassName() == null) {
					inputOptions.setServiceImplClassName(toolInput
							.getServiceImplClassName());
				}
				if (inputOptions.getGenInterfacePackage() == null) {
					inputOptions.setGenInterfacePackage(toolInput
							.getGenInterfacePkgName());
				}
				if (inputOptions.getGenInterfaceName() == null) {
					inputOptions.setGenInterfaceName(toolInput
							.getGenInterfaceName());
				}
				if (inputOptions.getClientCfgGroupName() == null) {
					inputOptions.setClientCfgGroupName(toolInput
							.getClientCfgGroupName());
				}
				if (inputOptions.getServerCfgGroupName() == null) {
					inputOptions.setServerCfgGroupName(toolInput
							.getServiceCfgGroupName());
				}
				if (!inputOptions.isImplCommonSvcInterface()) {
					inputOptions.setImplCommonSvcInterface(
							getBooleanValue(toolInput.isImplCommonSvcInterface()));
				}
				if (!inputOptions.isGenSkeleton()) {
					inputOptions.setIsGenSkeleton(getBooleanValue(toolInput
							.isAddValidateInternals()));
				}
				if (!inputOptions.isAddVI()) {
					inputOptions.setAddVI(getBooleanValue(toolInput
							.isAddValidateInternals()));
				}

				if (inputOptions.getClientName() == null) {
					inputOptions.setClientName(toolInput.getClientName());
				}

				if (inputOptions.getSvcCurrVersion() == InputOptions.DEFAULT_SERVICE_VERSION) {
					inputOptions.setSvcCurrVersion(toolInput
							.getServiceCurrVersion());
				}

				if (!inputOptions.isDontPrompt()) {
					inputOptions.setIsDontPrompt(getBooleanValue(toolInput
							.isDontPrompt()));
				}

				if (!inputOptions.isGenTests()) {
					inputOptions.setGenTests(getBooleanValue(toolInput
							.isGenTestClasses()));
				}
				
				if (!inputOptions.isNoCompile()) {
					inputOptions.setIsNoCompile(
							getBooleanValue(toolInput.isNoCompile()));
				}

				if (!inputOptions.isContinueOnError()) {
					inputOptions.setIsContinueOnError(
							getBooleanValue(toolInput.isContinueOnError()));
				}


				if (inputOptions.getServiceLayer().equals(ServiceLayer.BUSINESS.name())) {
					ServiceLayerType svcLayerType = toolInput.getServiceLayer();
					if (svcLayerType != null) {
						ServiceLayer svcLevel = ServiceLayer
								.getServiceLayer(svcLayerType.name());
						inputOptions.setServiceLayer(svcLevel.name());
					}
				}
			}

			ServiceType svcType = svcCodeGenDefType.getServiceInfo();
			if (svcType != null) {
				QName svcQName = svcType.getServiceName();
				if (inputOptions.getNamespace() == null) {
					inputOptions.setNamespace(svcQName.getNamespaceURI());
				}
				if (inputOptions.getServiceAdminName() == null) {
					inputOptions.setServiceName(svcQName.getLocalPart());
				}
				if (inputOptions.getServiceLocation() == null) {
					inputOptions.setServiceLocation(svcType.getServiceLocation());
				}
				if (inputOptions.getWSDLLocation() == null) {
					inputOptions.setWSDLLocation(svcType.getWsdlLocation());
				}
			}
		}

		String interfacePkgName = inputOptions.getGenInterfacePackage();
		inputOptions.setGenInterfacePackage(CodeGenUtil.normalizePackageName(interfacePkgName));

		processSrcLocations(inputOptions);

		return inputOptions;
	}

	
	private NSToPkgMappingList parseNSPackageString(String nsPackageMapMapStr)
	throws BadInputValueException {

		Map<String, String> keyValueMap = getKeyValues(nsPackageMapMapStr);

		if (keyValueMap == null) {
			if (CodeGenUtil.isEmptyString(nsPackageMapMapStr)) {
				return null;
			} else {
				String errMsg = "Input value specified for '" + InputOptions.OPT_NS_2_PKG + 
				"' option is not well-formed, should be in ns1=pkg1,ns2=pkg2 format.";
				throw new BadInputValueException(errMsg);
			}
		} 
		else {
			
			NSToPkgMappingList nsToPkgMappingList = new NSToPkgMappingList();
	
			for (Map.Entry<String, String> mapEntry : keyValueMap.entrySet()) {		
				
				NSPkgMappingType nsPkgMappingType = new NSPkgMappingType();
				nsPkgMappingType.setNamespace(mapEntry.getKey());
				nsPkgMappingType.setPackage(mapEntry.getValue());
				
				nsToPkgMappingList.getPkgNsMap().add(nsPkgMappingType);
			}
	
			return nsToPkgMappingList;
		}
	}
	
	
	private PkgToNSMappingList parsePackageNSString(String packageNSMapStr)
			throws BadInputValueException {
		
		Map<String, String> keyValueMap = getKeyValues(packageNSMapStr);
		
		if (keyValueMap == null) {
			if (CodeGenUtil.isEmptyString(packageNSMapStr)) {
				return null;
			} else {
				String errMsg = "Input value specified for '" + InputOptions.OPT_PKG_2_NS + 
							"' option is not well-formed, should be in pkg1=ns1,pkg2=ns2 format.";
				throw new BadInputValueException(errMsg);
			}
		} 
		else {
			PkgToNSMappingList pkgToNSMappings = new PkgToNSMappingList();
			
			for (Map.Entry<String, String> mapEntry : keyValueMap.entrySet()) {				
				PkgNSMappingType pkgNSMapType = new PkgNSMappingType();
				pkgNSMapType.setPackage(mapEntry.getKey());
				pkgNSMapType.setNamespace(mapEntry.getValue());
				
				pkgToNSMappings.getPkgNsMap().add(pkgNSMapType);
			}
			
			return pkgToNSMappings;
		}
	}
	
	
	
	private OpNameToCemcMappingList parseOpNameCemcString(String opNameCemcStr)
			throws BadInputValueException {

		Map<String, String> keyValueMap = getKeyValues(opNameCemcStr);;

		if (keyValueMap == null) {
			if (CodeGenUtil.isEmptyString(opNameCemcStr)) {
				return null;
			} else {
				String errMsg = "Input value specified for '" + InputOptions.OPT_OP_NAME_CEMC_MAP + 
							"' option is not well-formed, should be in op1=cemc1,op2=cemc2 or all=cemc format.";
				throw new BadInputValueException(errMsg);
			}
		} else {
			OpNameToCemcMappingList opNameTpCemcMappings = new OpNameToCemcMappingList();

			for (Map.Entry<String, String> mapEntry : keyValueMap.entrySet()) {
				OpNameCemcMappingType opNameToCemcType = new OpNameCemcMappingType();
				String key = mapEntry.getKey();
				if (key.equalsIgnoreCase(CodeGenConstants.ALL)) {
					key = CodeGenConstants.ALL;
				}
				opNameToCemcType.setOperationName(key);
				opNameToCemcType.setCustomErrMsgClass(mapEntry.getValue());

				opNameTpCemcMappings.getOpNameCemcMap().add(opNameToCemcType);
			}

			return opNameTpCemcMappings;
		}
	}
	
	
	
	
	private Map<String, String> getKeyValues(String keyValueString) 
			throws BadInputValueException {
		
		Map<String, String> keyValueMap = null;
		
		String[] keyValueEntries = keyValueString.split(",");
		
		if (keyValueEntries != null && keyValueEntries.length > 0) {
			
			keyValueMap = new HashMap<String, String>();
			
			for (String keyValueEntry : keyValueEntries) {
				String[] keyValueArray = keyValueEntry.trim().split("=");
				if (keyValueArray == null || keyValueArray.length != 2) {
					keyValueMap = null;
					break;
				}

				keyValueMap.put(keyValueArray[0].trim(), keyValueArray[1].trim());
			}			
		}
		
		return keyValueMap;
	}
	

	private void processSrcLocations(InputOptions inputOptions) {
		String srcLocSeperator = ";";
		if ((inputOptions.getInputType() == InputType.INTERFACE)
				&& (inputOptions.getSrcLocation().indexOf(srcLocSeperator) > 0)) {
			String qualifiedIntfClassName = inputOptions.getInputFile();
			String originalSrcLoc = inputOptions.getSrcLocation();
			String[] allSrcLocations = originalSrcLoc.split(srcLocSeperator);
			for (String srcLoc : allSrcLocations) {
				String javaSrcFilePath = CodeGenUtil.toJavaSrcFilePath(srcLoc,
						qualifiedIntfClassName);
				if (CodeGenUtil.isFileExists(javaSrcFilePath)) {
					// set real src location of the interface
					inputOptions.setSrcLocation(srcLoc);
					break;
				}
			}
			// keep all source locations for future reference
			inputOptions.setAllSrcLocations(allSrcLocations);
		}
	}
	
	

	private boolean getBooleanValue(Boolean booleanObj) {
		if (booleanObj == null) {
			return false;
		}
		return booleanObj.booleanValue();
	}
}
