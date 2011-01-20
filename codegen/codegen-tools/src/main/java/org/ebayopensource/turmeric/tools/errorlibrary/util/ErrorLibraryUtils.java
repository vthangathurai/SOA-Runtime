/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.ELDomainInfoHolder;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions.ErrorLibraryGenType;
import org.ebayopensource.turmeric.tools.errorlibrary.codegen.ErrorLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.ebayopensource.turmeric.common.config.Error;

public class ErrorLibraryUtils {
	private static final String ERROR_METADATA_PATH = "META-INF/errorlibrary";
	private static final String ERRORBUNDLE_XSD_LOCATION = "META-INF/soa/schema/errorLibrary/ErrorBundle.xsd";
	private static final String PROPERTY_LIST_OF_DOMAINS = "listOfDomains";
	
	private static Logger s_logger = LogManager.getInstance(ErrorLibraryUtils.class);
	
	private static Logger getLogger() {
		return s_logger;
	}

	public static boolean validateMetadataFiles(ErrorLibraryCodeGenContext codeGenContext,
					String domain) throws PreProcessFailedException{
		boolean isValidated = false;
		
		validateAgainstSchema(getXMLLocationForDomain(codeGenContext, domain), ERRORBUNDLE_XSD_LOCATION);
		Set<String> propertiesErrorSet = null;
		Set<String> xmlErrorSet = null;
		StringBuffer duplicatesBuffer = new StringBuffer(400);
		try {
			xmlErrorSet = getUniqueXMLErrorNames(codeGenContext, domain);
		} catch (PreValidationFailedException exception) {
			getLogger().log(Level.FINE, "Duplicates found in ErrorData.xml");
			duplicatesBuffer.append(exception.getMessage());
		}
		try {
			propertiesErrorSet = getUniquePropertyErrorNames(codeGenContext, domain);
		} catch (PreProcessFailedException exception) {
			getLogger().log(Level.FINE, "Duplicates found in Errors.properties");
			duplicatesBuffer.append("\n").append(exception.getMessage());
		}
		
		try {
			if(xmlErrorSet != null && propertiesErrorSet != null){
				isConsistent(xmlErrorSet, propertiesErrorSet);
				isValidated = true;				
			}
		} catch (PreProcessFailedException exception) {
			getLogger().log(Level.FINE, "Inconsistencies found");
			duplicatesBuffer.append("\n").append(exception.getMessage());
		}
		
		if(!CodeGenUtil.isEmptyString(duplicatesBuffer.toString()))
			throw new PreProcessFailedException(duplicatesBuffer.toString());
			
		
		return isValidated;
	}
	
	public static boolean isConsistent(Set<String> xmlErrors, Set<String> propertyErrors) throws PreProcessFailedException{
		boolean isErrorPropertiesConsistent = false;
		
		Set<String> propErrorscopy = new HashSet<String>();
		propErrorscopy.addAll(propertyErrors);
		
		if(propertyErrors.containsAll(xmlErrors)){
			isErrorPropertiesConsistent = true;
			propErrorscopy.removeAll(xmlErrors);
		}
		
		if(propErrorscopy.isEmpty())
			getLogger().log(Level.INFO, "The meta-data files are consistent");
		else{
			getLogger().log(Level.WARNING, "The Errors.properties file has more " +
					"errors defined in addition to those existing in ErrorData.xml. They are \n" + propErrorscopy);
		}
		xmlErrors.removeAll(propertyErrors);
		
		if(!xmlErrors.isEmpty()){
			getLogger().log(Level.SEVERE, "The meta-data files are inconsistent. Errors.properties does not" +
					" have all the errors defined in ErrorData.xml");
			throw new PreProcessFailedException("Errors.properties does not" +
					" have all the errors defined in ErrorData.xml namely " + xmlErrors);
			
		}
		
		return isErrorPropertiesConsistent;
	}
	
	public static Set<String> getUniquePropertyErrorNames(ErrorLibraryCodeGenContext codeGenContext,
				String domainName) throws PreProcessFailedException {
		
		List<String> duplicateBuffer = new ArrayList<String>();
		Set<String> propertiesErrorSet = new TreeSet<String>();
		Map<String, String> errorProperties = new HashMap<String, String>();
		InputStream inputStream = null;
		// We don't use java.util.Properties to load the properties. This is because, we wanna figure out 
		// if the entries in the given Error.properties are unique. If we use java.util.Properties, it filters 
		// out the duplicates during loading stage itself and we cannot check for uniqueness.
		
		BufferedReader propertyReader = null;
		InputStreamReader isr = null;
		
		try {
			String tempStr = null;
			inputStream = new FileInputStream(new File(getPropertiesLocationForDomain(codeGenContext, domainName)));
			isr = new InputStreamReader(inputStream, "UTF-8");
			propertyReader = new BufferedReader(isr);
			while ((tempStr = propertyReader.readLine()) != null) {
				tempStr = tempStr.trim();
				if(tempStr.contains("=") && !tempStr.startsWith("#")){
					String keyValue[] = tempStr.split("=");
					String key = keyValue[0];
					String value = null;
					if(keyValue.length > 1)
						value = keyValue[1];
					String existingValue = errorProperties.put(key, value);
					if(key.endsWith(".message")){
						String errorName = key.substring(0, key.lastIndexOf(".message"));
						if(existingValue != null)
							duplicateBuffer.add(key.substring(0, key.lastIndexOf(".message")));
						else
							propertiesErrorSet.add(errorName);
					}
					
				}				
			}
				
		} catch (FileNotFoundException exception) {
			throw new PreProcessFailedException("Properties file not" +
					" found in the location <errorLibrary>/meta-src/META-INF/errorlibrary/"
					+ domainName, exception);
			
		} catch (IOException exception) {
			throw new PreProcessFailedException("IOException in accessing the properties file " +
					"in the location <errorLibrary>/meta-src/META-INF/errorlibrary/"
					+ domainName, exception);
			
		}finally {
			CodeGenUtil.closeQuietly(propertyReader);
			CodeGenUtil.closeQuietly(isr);
			CodeGenUtil.closeQuietly(inputStream);
		}
		
		if(!duplicateBuffer.isEmpty())
			throw new PreValidationFailedException("Duplicates found in Error.properties. They are " + duplicateBuffer);
		
		
		return propertiesErrorSet;
	}
	
	public static Set<String> getUniqueXMLErrorNames(ErrorLibraryCodeGenContext codeGenContext,
				String domain) throws PreProcessFailedException{
		
		ELDomainInfoHolder holder = codeGenContext.getDomainInfoMap().get(domain);		
		
		List<String> duplicateBuffer = new ArrayList<String>();
		Set<String> xmlErrorSet = new TreeSet<String>();
		List<String> invalidXMLErrorList = new ArrayList<String>();
		List<Error> errorsInXML = null;
		if(holder.getErrorBundle() != null && holder.getErrorBundle().getErrorlist() != null)
			errorsInXML = holder.getErrorBundle().getErrorlist().getError();
			
		if(errorsInXML != null ){
			for (Error error : errorsInXML) {
				if(error != null){
					if(error.getName() != null && validateVariableSemantics(error.getName())){
						boolean entryAdded = xmlErrorSet.add(error.getName());
						if(!entryAdded)
							duplicateBuffer.add(error.getName());
					} else
						invalidXMLErrorList.add(error.getName());
				}
			}
		}
		
		if(!invalidXMLErrorList.isEmpty()){
			throw new PreProcessFailedException("The error name(s) " +
					invalidXMLErrorList + " have whitespace character and CodeGen cannot proceed. " +
							"Pls check your ErrorData.xml file and fix it." +
							" \nAlso check Errors.properties as they have one-one mapping" +
							" with ErrorData.xml file");
			
		}
		
		if(!duplicateBuffer.isEmpty())
			throw new PreValidationFailedException("Duplicates found in ErrorData.xml. They are " + duplicateBuffer);
		
		return xmlErrorSet;
	}
	
	public static boolean validateVariableSemantics(String argument){
		
		//TODO  use regular expression instead to check for both upper and lower case alphabets
		boolean isValidate = true;
		if (argument != null)
			for (int i = 0; i < argument.length(); i++)
				if (Character.isWhitespace(argument.charAt(i)) || Character.isSpaceChar(argument.charAt(i))) {
					isValidate = false;
					break;
				}		
		return isValidate;
	}
	
	public static boolean validateAgainstSchema(String xmlLocation, 
				String xsdLocation) throws PreProcessFailedException{
		boolean isValidate = false;
		
		Document document = null;
		Schema schema = null;
	    try {
	    	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    	domFactory.setNamespaceAware(true);
			DocumentBuilder parser = domFactory.newDocumentBuilder();
			document = parser.parse(new File(xmlLocation));
			
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		    URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource(xsdLocation);
		    if(schemaUrl == null) {
		    	throw new PreProcessFailedException("Unable to find schema resource: " + xsdLocation);
		    }

			InputStream stream = null;
			try {
				stream = schemaUrl.openStream();
				Source schemaFile = new StreamSource(stream);
				schema = factory.newSchema(schemaFile);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		    
		} catch (ParserConfigurationException exception) {
			throw new PreProcessFailedException("XML parsing failed : " + 
					exception.getMessage(), exception);
			
		} catch (SAXException exception) {
			throw new PreProcessFailedException("XML parsing failed : " + 
					exception.getMessage(), exception);
			
		} catch (IOException exception) {
			throw new PreProcessFailedException("XML parsing failed because of IOException : " + 
					exception.getMessage(), exception);
		} catch (Exception exception) {
			throw new PreProcessFailedException("XML parsing failed : " + 
					exception.getMessage(), exception);
		}

	    Validator validator = schema.newValidator();

	    try {
	        validator.validate(new DOMSource(document));
	        isValidate = true;
	    } catch (SAXException exception) {
	    	throw new PreProcessFailedException("XML validation against XSD failed : " + 
					exception.getMessage(), exception);
	    } catch (IOException exception) {
			throw new PreProcessFailedException("Schema validation failed because of IOException : " + 
					exception.getMessage(), exception);
		}

		return isValidate;
	}
	
	
	public static String getFullyQualifiedClassName(String packageName, String className) throws PreProcessFailedException{
		
		StringBuffer fullyQualifiedName = new StringBuffer(200);
		if(CodeGenUtil.isEmptyString(className))
			throw new PreProcessFailedException("ClassName cannot be null/empty");
		if(!CodeGenUtil.isEmptyString(packageName) && !CodeGenUtil.isEmptyString(className))
			fullyQualifiedName.append(packageName.toLowerCase()).append(".").append(className);
		return fullyQualifiedName.toString();
	}
	
	
	public static String getXMLLocationForDomain(ErrorLibraryCodeGenContext codeGenContext,
			String domain){
		StringBuilder xmlLocation = new StringBuilder();
		xmlLocation.append(codeGenContext.getMetaSrcFolder());
		xmlLocation.append(ERROR_METADATA_PATH).append("/");
		xmlLocation.append(domain);
		return CodeGenUtil.toOSFilePath(xmlLocation.toString()) + "ErrorData.xml";
	}
	
	
	public static String getPropertiesLocationForDomain(ErrorLibraryCodeGenContext codeGenContext,
			String domain) throws IOException{
		StringBuilder propLocation = new StringBuilder();
		propLocation.append(codeGenContext.getMetaSrcFolder());
		propLocation.append(ERROR_METADATA_PATH).append("/");
		propLocation.append(domain).append("/");
		
		String propBaseDir = CodeGenUtil.toOSFilePath(propLocation.toString());
		
		try {
			String[] listOfFiles = TypeLibraryUtilities.getFilesInDir(propBaseDir, ".properties");
			for (String fileName : listOfFiles) {
				if(fileName.startsWith("Errors_en")){
					propLocation.append(fileName);
					break;
				}
			}
		} catch (Exception exception) {
			throw new IOException(exception);
		}
		return propLocation.toString();
	}
	
	public static List<String> getListOfDomains(String propertyValue){
		
		String[] arrayOFDomains = propertyValue.split(",");
		List<String> listOfDomains = new ArrayList<String>();

		for(String currDomain : arrayOFDomains)
			listOfDomains.add(currDomain.trim());
		
		return listOfDomains;
	}
	
	
	
	public static String readDomainListFromErrorLibraryProperties(
			ErrorLibraryCodeGenContext codeGenContext) throws PreProcessFailedException {
		StringBuilder filename = new StringBuilder();
		filename.append(codeGenContext.getMetaSrcFolder());
		filename.append(ERROR_METADATA_PATH).append("/");
		filename.append(codeGenContext.getInputOptions().getErrorLibraryName());
		filename.append("/domain_list.properties");
		
		Properties errorLibraryProperties = new Properties();

		File propertiesFile = new File(CodeGenUtil.toOSFilePath(filename.toString()));
		InputStream inStream = null;

		try {
			inStream = new FileInputStream(propertiesFile);
			errorLibraryProperties.load(inStream);
		} catch (FileNotFoundException exception) {
			throw new PreProcessFailedException("The domain_list.properties file not found. " + exception.getMessage(), exception);
		} catch (IOException exception) {
			throw new PreProcessFailedException("The domain_list.properties file could not be read due to IOException and"
							+ " hence CodeGen cannot proceed with generation of artifacts. " + exception.getMessage(), exception);
		} finally {
			IOUtils.closeQuietly(inStream);
		}
		return errorLibraryProperties.getProperty(PROPERTY_LIST_OF_DOMAINS);
	}
	
	public static boolean isGenTypeErrorLibrary(ErrorLibraryInputOptions errorLibraryInputOptions) {
		boolean isErrorLibrary = false;
		if(errorLibraryInputOptions != null)
			if(errorLibraryInputOptions.getCodeGenType() == ErrorLibraryGenType.genTypeDataCollection ||
				errorLibraryInputOptions.getCodeGenType() == ErrorLibraryGenType.genTypeConstants ||
				errorLibraryInputOptions.getCodeGenType() == ErrorLibraryGenType.genTypeErrorLibAll ||
				errorLibraryInputOptions.getCodeGenType() == ErrorLibraryGenType.genTypeCommandLineAll) {
	
				isErrorLibrary = true;
			}
				
		return isErrorLibrary;
	
	}
	
}
