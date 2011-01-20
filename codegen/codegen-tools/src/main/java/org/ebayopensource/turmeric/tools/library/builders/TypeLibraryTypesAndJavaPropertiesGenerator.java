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
package org.ebayopensource.turmeric.tools.library.builders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.AdditionalXSDInformation;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;


/**
 * @author arajmony
 *
 */
public class TypeLibraryTypesAndJavaPropertiesGenerator {

	public static final String TYPES_JAVA_PROP_FILE_NAME = "type_java_relation.properties";
	private static final String JAVA_TYPE_NOT_CREATED ="java_type_not_created";
	
	private static Logger s_logger = LogManager.getInstance(TypeLibraryTypesAndJavaPropertiesGenerator.class);
	
	private static TypeLibraryTypesAndJavaPropertiesGenerator s_LibraryTypeJavaTypePropertiesGenerator = 
				new TypeLibraryTypesAndJavaPropertiesGenerator();

	
	private TypeLibraryTypesAndJavaPropertiesGenerator() {
	}
	

	public static TypeLibraryTypesAndJavaPropertiesGenerator getInstance() {
		return s_LibraryTypeJavaTypePropertiesGenerator;
	}
	
	
	private  Logger getLogger() {
		return s_logger;
	}
	
	
	/**
	 * 
	 * @param projectRoot
	 * @param codeGenCtx
	 * @param additionalXSDInformation
	 * @param xsdTypeName
	 */
	public  void createUpdatePropertiesFile(TypeLibraryCodeGenContext codeGenCtx, AdditionalXSDInformation additionalXSDInformation, String xsdTypeName) {
		
		String libraryName = codeGenCtx.getLibraryName();
		String destPath = TypeLibraryUtilities.getTypesJavaPropertiesFolder(codeGenCtx, libraryName);
		
		File propertiesFile =  new File(destPath + TYPES_JAVA_PROP_FILE_NAME);
		
		Properties properties = new Properties();;
		
		if(propertiesFile.exists()){
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(propertiesFile);
				properties.load(inputStream);
			} catch (FileNotFoundException e) {
				getLogger().log(Level.WARNING, e.getMessage());
			} catch (IOException e) {
				getLogger().log(Level.WARNING, e.getMessage());
			}finally{
				CodeGenUtil.closeQuietly(inputStream);
			}
		}
		
		properties.put(xsdTypeName, JAVA_TYPE_NOT_CREATED);
		
		String contentToBeWrittenToFile = TypeLibraryUtilities.getStringContentFromProperties(properties);
		
		  try {
			CodeGenUtil.writeToFile(
					  destPath, 
					  TYPES_JAVA_PROP_FILE_NAME, 
					  contentToBeWrittenToFile);
		} catch (IOException e) {
			String errMsg = "Failed to generate : " + TYPES_JAVA_PROP_FILE_NAME;
			getLogger().log(Level.WARNING, errMsg, e);
		}
      
      getLogger().log(Level.INFO, 
      		"Successfully generated " + TYPES_JAVA_PROP_FILE_NAME + " under " + destPath);
		
		
	}

	
	
	
}
