/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen;

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.builders.*;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;




/**
 * A utility class which has methods to derive file locations and to get information from
 * them.
 * 
 * @author arajmony
 *
 */
public class CodeGenInfoFinder {
	
	private static Logger s_logger = LogManager.getInstance(CodeGenInfoFinder.class);
	
	private static Logger getLogger() {
		return s_logger;
	}


	 private CodeGenInfoFinder() {}
	 
	 private static Map<String,Properties> serviceMetaDataPropsMap = new HashMap<String,Properties>();
	 private static Map<String,Properties> serviceIntfPropsMap     = new HashMap<String,Properties>();
	 private static final String m_plugin_caller = "PLUG-IN"; 
	 
	 

	 /**
	  * Gets the names of the Service Layers from the user given file and returns in a List format.
	  * @param alternativeServiceLayersFile the File path location of the user given file
	  * @return a List of ServiceLayers
	  * @throws CodeGenFailedException
	  */
	 public static List<String> getServiceLayers(String alternativeServiceLayersFile)
		 throws CodeGenFailedException {
		 String allServiceLayers = "";
		 
         if(CodeGenUtil.isFileExists(alternativeServiceLayersFile)){
        	 try {
        		 allServiceLayers = CodeGenUtil.getFileContents(alternativeServiceLayersFile);	 
        	 }catch(IOException ie){
        		 throw new CodeGenFailedException(ie.getMessage(), ie);
        	 }
         }
         else {
        	 throw new CodeGenFailedException("File doesn't exist at location :"+alternativeServiceLayersFile);
         }		 
         return getListOfServiceLayers(allServiceLayers);
	 }

	 
	 
	 /**
	  * Gets the names of the Service Layers from the default file in codegen-tools.jar and returns in a List format.
	  * @return a List of ServiceLayers
	  * @throws CodeGenFailedException
	  */
	 public static List<String> getServiceLayersFromDefaultFile()
		 throws  CodeGenFailedException {

		 String  allServiceLayers = "";

		 /*
		  * logic to get a handle to the file service_layers.txt
		  */
		 String defaultSvcLayerFilePath   = "META-INF/soa/service_layers.txt"; 
		 ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
		 InputStream	inStream   = myClassLoader.getResourceAsStream(defaultSvcLayerFilePath);
		 
		 if (inStream != null){
	       	 try {
	    		 allServiceLayers = readContent(inStream);	 
	    	 }catch(IOException ie){
	    		 throw new CodeGenFailedException(ie.getMessage(), ie);
	    	 }
		 }
		 else {
		   throw new CodeGenFailedException("Class loader could not locate the default service layer file " + defaultSvcLayerFilePath); 
		 }
		 
		 return  getListOfServiceLayers(allServiceLayers);
		 
	 }

	 
	 
	 private static List<String> getListOfServiceLayers(String allServiceLayers)
	               throws CodeGenFailedException  {
		 
		 List<String> serviceLayersList   = new ArrayList<String>();
		 StringTokenizer  serviceTokens   = new StringTokenizer(allServiceLayers,",");
		 
		 while(serviceTokens.hasMoreTokens())
			 serviceLayersList.add(serviceTokens.nextToken().trim()); 
		 
		 return serviceLayersList;
	 }
	 
	 
	private static String readContent(InputStream input) throws IOException {
		    Charset defaultCharset = Charset.defaultCharset(); 
			InputStreamReader isr = new InputStreamReader(input,defaultCharset);
			BufferedReader reader = null;
			
			StringBuilder strBuff = new StringBuilder();
			try {
				reader = new BufferedReader(isr);
				char[] charBuff = new char[512];
				int charsRead = -1;
				while ((charsRead = reader.read(charBuff)) > -1) {
					strBuff.append(charBuff, 0, charsRead);
				}
			} finally {
				CodeGenUtil.closeQuietly(reader);
			}
			return strBuff.toString();
		}	 


	
	public static  enum FileArtifactType {
		WSDL(WSDLGenerator.getInstance()),
		SERVICE_METADATA(ServiceMetadataFileGenerator.getInstance()),
		TYPE_MAPPINGS(TypeMappingsGenerator.getInstance()),
		CLIENT_CONFIG(ClientConfigGenerator.getInstance()),
		SERVICE_CONFIG(ServiceConfigGenerator.getInstance()),
		SECURITY_POLICY(SecurityPolicyConfigGenerator.getInstance()),
		WEB_APP_DESCRIPTOR(WebAppDescriptorGenerator.getInstance()),
		GLOBAL_CLIENT_CONFIG(GlobalClientConfigGenerator.getInstance()),
		GLOBAL_SERVICE_CONFIG(GlobalServiceConfigGenerator.getInstance()),
		XML_SCHEMA(XMLSchemaGenerator.getInstance()),
		SERVICE_PROXY(ServiceProxyGenerator.getInstance()),
		SERVICE_SKELETON(ServiceSkeletonGenerator.getInstance()),
		SERVICE_DISPATCHER(ServiceDispatcherGenerator.getInstance()),
		SERVICE_CONSUMER(ServiceConsumerGenerator.getInstance()),
		UNIT_TEST(UnitTestGenerator.getInstance()),
		TYPE_DEFS(TypeDefsBuilderGenerator.getInstance()),
		SERVICE_INTF_PROP(ServiceIntfPropertiesFileGenerator.getInstance())
		;
		
		private final SourceGenerator TYPE_VALUE;
				
		private FileArtifactType(SourceGenerator value){
			TYPE_VALUE = value;
		}
		
		public SourceGenerator value(){
			return TYPE_VALUE;
		}
		
		
		public static FileArtifactType getFileArtifactType(String fileArtifactName) {
			FileArtifactType fileArtifactOption = null;
            for( FileArtifactType fileArtifact : FileArtifactType.values() ) {
                if(fileArtifact.name().equals(fileArtifactName)) {
                	fileArtifactOption = fileArtifact;
                	break;
                 }
            }
			return fileArtifactOption;
		}
	}
	
	/**
	 * This method calls the overloaded version.
	 * 
	 * @param serviceAdminName  name of the service 
	 * @param inputFileArtifactType  type of file Artifact.  
	 * @return relative file path for the requested artifact
	 * @throws BadInputValueException
	 */
	public static String getPathforNonModifiableArtifact(String serviceAdminName, String inputFileArtifactType )
	                                     throws BadInputValueException{

		return getPathforNonModifiableArtifact(serviceAdminName,inputFileArtifactType,null);
	}

	
	/**
	 * overloaded version. returns path of a non-modifiable artifact 
	 * 
	 * @param serviceAdminName  name of the service 
	 * @param inputFileArtifactType  type of file Artifact.  
	 * @param interfaceName Extra parameter for passing any other value
	 * @return relative file path for the requested artifact
	 * @throws BadInputValueException
	 */
	public static String getPathforNonModifiableArtifact(String serviceAdminName, String inputFileArtifactType, String interfaceName )
    									throws BadInputValueException{
	
		FileArtifactType artifactType = FileArtifactType.getFileArtifactType(inputFileArtifactType);
		if ( artifactType == null)
			throw new BadInputValueException("The Artifact Type " + inputFileArtifactType + " is not a valid artifact type.");
		
		SourceGenerator artifactGenerator = artifactType.value();
		
		return artifactGenerator.getFilePath(serviceAdminName, interfaceName);
		
	}
	
	
	public String getPathforModifiableArtifact(){
		return "";
	}
	
	
	
	/**
	 * @param inputOptions , org.ebayopensource.turmeric.runtime.tools.codegen.InputOptions
	 * @return
	 * @throws BadInputValueException
	 * @throws IOException
	 * @throws CodeGenFailedException
	 */
	public static boolean updateMetaDataMap( InputOptions inputOptions) 
	   throws BadInputValueException, IOException, CodeGenFailedException {
		
		String classpath = System.getProperty("java.class.path");
		getLogger().log(Level.FINEST, "class path in updateMetaDataMap : " + classpath);
		
	    boolean isMetaDataMapInitilized = false;
	    String mDestRootPath    = null;
	    boolean throwExceptionOnFailure = false;
	    InputStream inStream = null;
	
 	    if(inputOptions == null)  
	       return isMetaDataMapInitilized;
	    
	    String serviceAdminName = inputOptions.getServiceAdminName();
    	Properties metaDataMap = serviceMetaDataPropsMap.get(serviceAdminName);
    	
    	if(metaDataMap == null){
    		metaDataMap = new Properties();
    		serviceMetaDataPropsMap.put(serviceAdminName, metaDataMap);
    	}
    	else{
    		metaDataMap.clear();
    	}
    		
	    
	    /* to get the service_metadata.properties file, follow the following priority
	     * 1. If the caller is plugin then codegen should look for the file both under project root (in both meta-src and gen-meta-src)
	     * 2. if -pr is provided, Look under folder specified thru -pr option (both meta-src and gen-meta-src)
	     * 3. else if -uij is provided Try to load the file from the class path 
	     * 4. else (this is for backward compatibility) Look for the file under both the meta files location (meta-src and gen-meta-src)
	     */
		String projectRoot = inputOptions.getProjectRoot();
		String caller = inputOptions.getCaller();
		
		try {
			if(m_plugin_caller.equals(caller) ||
					!CodeGenUtil.isEmptyString(projectRoot)
				){
				mDestRootPath = projectRoot;
				throwExceptionOnFailure = false;
				inStream = getMetaDataFileFromPath(mDestRootPath,inputOptions,throwExceptionOnFailure);
				
			}
			else if(inputOptions.getUseInterfaceJar()){
				throwExceptionOnFailure = true;
				inStream = getMetaDataFileFromJar(serviceAdminName,throwExceptionOnFailure);
			}
			else {
				mDestRootPath = inputOptions.getMetaSrcDestLocation();
				if(CodeGenUtil.isEmptyString(mDestRootPath) )
					mDestRootPath = inputOptions.getDestLocation();
				
				throwExceptionOnFailure = false;
				inStream = getMetaDataFileFromPath(mDestRootPath,inputOptions,throwExceptionOnFailure);
			}
		
			if(inStream != null){
				metaDataMap.load(inStream);
				isMetaDataMapInitilized = true;
			}
			else {
				isMetaDataMapInitilized = false;
			}
		}
		finally {
			CodeGenUtil.closeQuietly(inStream);
		}

		
		return isMetaDataMapInitilized;
	}
	
    /**
     * 
     * @param rootPath
     * @param inputOptions
     * @param throwExceptionOnFailure
     * @param lookUnderGenMetaSrc
     * @return
     * @throws CodeGenFailedException
     * @throws BadInputValueException
     * @throws IOException
     */
	private static InputStream getMetaDataFileFromPath(String rootPath,InputOptions inputOptions,
														boolean throwExceptionOnFailure)
		throws CodeGenFailedException,BadInputValueException,IOException{
		InputStream inStream = null;
		
		String serviceAdminName = inputOptions.getServiceAdminName();
		
		
		//SOA 2.4.  The SMP file should be first looked ofr under gen-meta-src and if not found then in meta-src
		
		String mDestPath= CodeGenUtil.toOSFilePath(rootPath) + CodeGenConstants.GEN_META_SRC_FOLDER;
		String filePath = getPathforNonModifiableArtifact(serviceAdminName,"SERVICE_METADATA");
		filePath        = CodeGenUtil.toOSFilePath(mDestPath) + filePath;
		
		//check for path with meta-src folders for existing projects pre 2.4
		if(! CodeGenUtil.isFileExists(filePath)){
			mDestPath= CodeGenUtil.toOSFilePath(rootPath) + CodeGenConstants.META_SRC_FOLDER;
			filePath        = CodeGenUtil.toOSFilePath(mDestPath) + getPathforNonModifiableArtifact(serviceAdminName,"SERVICE_METADATA");
		}
		
		
		if(!CodeGenUtil.isFileExists(filePath)){
			if(throwExceptionOnFailure)
				throw new CodeGenFailedException("Could not locate file " + filePath + " for option "+ InputOptions.OPT_PROJECT_ROOT);
			else
				return null;
		}
			
	    try{
	    	String fileContent = CodeGenUtil.getFileContents(filePath);
	    	if(CodeGenUtil.isEmptyString(fileContent))
	    		if(throwExceptionOnFailure)
	    			throw new CodeGenFailedException("Empty contents in  file " + filePath + " for option "+ InputOptions.OPT_PROJECT_ROOT);
	    		else
	    			return null;
		
	    	inStream = new FileInputStream(filePath);
	    }catch(IOException ioEx){
			if(throwExceptionOnFailure)
				throw new CodeGenFailedException("IOException for file in " + filePath + "\n" + ioEx);
			else
				return null;
	    	
	    }
		
		return inStream;
	}


	/**
	 * 
	 * @param serviceAdminName
	 * @param throwExceptionOnFailure
	 * @return
	 * @throws CodeGenFailedException
	 * @throws BadInputValueException
	 */
	private static InputStream getMetaDataFileFromJar(String serviceAdminName,boolean throwExceptionOnFailure)
	  		throws CodeGenFailedException,BadInputValueException{
		InputStream inStream = null;

		String filePath = getPathforNonModifiableArtifact(serviceAdminName,"SERVICE_METADATA");
		//search for this file using the current class loader
		filePath = filePath.replace('\\', '/');
		ClassLoader classLoader = CodeGenInfoFinder.class.getClassLoader();
		inStream = classLoader.getResourceAsStream(filePath);
		if(inStream == null)
			if (throwExceptionOnFailure)
				throw new CodeGenFailedException("Could not locate file " + filePath + " for option "+ InputOptions.OPT_USE_INTERFACE_JAR);
			else
				return null;
		
		
		return inStream;
	}
	
	
	
	/**
	 * 
	 * @param propertyName Name of the property for which the value is seeked
	 * @return
	 * @deprecated use the overloaded version getPropertyFromMetaData(String propertyName,String serviceAdminName)
	 */
	public static String getPropertyFromMetaData(String propertyName){
	
		return null; 
    }
	
    /**
     * Returns the value for a property from service_metadata.properties file for a given service. This method is to be used
     * only by codegen. 
     * @param propertyName Name of the property whose value is seeked
     * @param serviceAdminName  Name of the Service.
     * @return
     */
	public static String getPropertyFromMetaData(String propertyName,String serviceAdminName){
		Properties currServiceMetaDataProperties = serviceMetaDataPropsMap.get(serviceAdminName);

		if(currServiceMetaDataProperties == null) 
			return null;
		else
		{
			String propertyValue = currServiceMetaDataProperties.getProperty(propertyName);
			if (propertyValue != null)
				return propertyValue.trim();
			return null;
		}

    }
	
	
	/**
	 * Returns the value for a property from service_metadata.properties file for a given service. This method 
	 * is provided only for the Plugin to call.
	 * @param propertyName Name of the property whose value is seeked
	 * @param serviceAdminName  service admin Name of the Service.
	 * @param projectRoot  Project root location
	 * @return Value of the property, if property's value does not exist then returns a null
	 */
	public static String getMetaDataProperty(String propertyName,String serviceAdminName,String projectRoot){
		
		Properties currServiceProperties = serviceMetaDataPropsMap.get(serviceAdminName);
		boolean isException = false;
		
		//If the properties map for this service is null then we will have to initilize the properties map for this service
		if(currServiceProperties == null) {
			InputOptions tempInputOptions = new InputOptions();
			tempInputOptions.setServiceAdminName(serviceAdminName);
			tempInputOptions.setProjectRoot(projectRoot);
			tempInputOptions.setCaller(m_plugin_caller);
			
			try {
				updateMetaDataMap(tempInputOptions);
				currServiceProperties = serviceMetaDataPropsMap.get(serviceAdminName);
			} catch (BadInputValueException e) {
				//e.printStackTrace();
				isException = true;
			} catch (CodeGenFailedException e) {
				//e.printStackTrace();
				isException = true;
			} catch (IOException e) {
				//e.printStackTrace();
				isException = true;
			}
		}
		
		if(currServiceProperties != null)
		  return currServiceProperties.getProperty(propertyName);
		else if(isException)
		  return null;	
		else 
		  return null;	
		
	}
	
	
  	/**
  	 * 
  	 * @param inputOptions
  	 * @return status of the map update
  	 */
    public static boolean updateSvcIntfProjPropMap(InputOptions inputOptions) 
    			throws BadInputValueException,IOException{
    
    	boolean isSvcIntfProjPropMapInitilized = false;
		InputStream inStream = null;
    	
 	    if(inputOptions == null)  
	       return isSvcIntfProjPropMapInitilized;

 	    String serviceAdminName = inputOptions.getServiceAdminName();
 	    Properties currServiceIntfProjectProperties = serviceIntfPropsMap.get(serviceAdminName);
	  
 	    if(currServiceIntfProjectProperties == null){
 	    	currServiceIntfProjectProperties = new Properties();
 	    	serviceIntfPropsMap.put(serviceAdminName, currServiceIntfProjectProperties);
 	    }
 	    	
 	    
		String projectRoot = inputOptions.getProjectRoot();
		if(CodeGenUtil.isEmptyString(projectRoot)){
			getLogger().log(Level.INFO, "Project root not set" );		    
			return isSvcIntfProjPropMapInitilized;
		}
    	
		String filePath = getPathforNonModifiableArtifact(serviceAdminName,"SERVICE_INTF_PROP");
		filePath        = CodeGenUtil.toOSFilePath(projectRoot) + filePath;
		if(! CodeGenUtil.isFileExists(filePath)){
			getLogger().log(Level.INFO, "Could not locate the service_intf_project.properties" +
					" possible reason could be -pr option not set" );	
			return isSvcIntfProjPropMapInitilized;
		}
			
		
		try{
			inStream = new FileInputStream(filePath);
			currServiceIntfProjectProperties.load(inStream);
		}
		finally {
			IOUtils.closeQuietly(inStream);
		}

		isSvcIntfProjPropMapInitilized = true;
		return isSvcIntfProjPropMapInitilized;
   }
    
    
	/**
	 * Returns the value for a property from service_intf_project.properties file for a given service. This method 
	 * is provided only for the Plugin to call.
	 * @param propertyName Name of the property whose value is seeked
	 * @param serviceAdminName  Name of the Service.
	 * @param projectRoot  Project root location
	 * @return Value of the property, if property's value does not exist then returns a null
	 */
	public static String getSvcInterfaceProjectProperty(String propertyName,String serviceAdminName,String projectRoot){
		
		Properties currSvcInterfaceProjectProperties = serviceIntfPropsMap.get(serviceAdminName);
		boolean isException = false;
		//If the properties map for this service is null then we will have to initilize the properties map for this service
		if(currSvcInterfaceProjectProperties == null) {
			InputOptions tempInputOptions = new InputOptions();
			tempInputOptions.setServiceAdminName(serviceAdminName);
			tempInputOptions.setProjectRoot(projectRoot);
			
			try {
				updateSvcIntfProjPropMap(tempInputOptions);
				currSvcInterfaceProjectProperties = serviceIntfPropsMap.get(serviceAdminName);
			} catch (BadInputValueException e1) {
				//e1.printStackTrace();
				isException = true;
			} catch (IOException e1) {
				//e1.printStackTrace();
				isException = true;
			}
			
		}
		
		if(currSvcInterfaceProjectProperties != null)
		    return currSvcInterfaceProjectProperties.getProperty(propertyName);
		else if (isException)
			return null;
		else
			return null;
		
	}
    
	/**
	 * 
	 * @param propertyName Name of the property for which the value is seeked
	 * @return
	 * @deprecated use the new overloaded method getPropertyFromSvcIntfProjProp(String propertyName,String serviceAdminName)
	 */
	public static String getPropertyFromSvcIntfProjProp(String propertyName){
	
		return null; 
    }
	
    /**
     * Returns the value for a property from service_intf_project.properties file for a given service. This method is to be used
     * only by codegen. 
     * @param propertyName Name of the property whose value is seeked
     * @param serviceAdminName  Name of the Service.
     * @return
     */
	public static String getPropertyFromSvcIntfProjProp(String propertyName,String serviceAdminName){
		Properties currSvcInterfaceProjectProperties = serviceIntfPropsMap.get(serviceAdminName);

		if(currSvcInterfaceProjectProperties == null) 
			return null;
		else
		{
			String propertyValue = currSvcInterfaceProjectProperties.getProperty(propertyName);
			if (propertyValue != null)
				return propertyValue.trim();
			return null;
		}

    }
	

	
    
}

