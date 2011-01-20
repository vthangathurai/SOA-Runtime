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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;


/**
 * @author arajmony
 *
 */
public class TypeLibraryProjectPropertiesGenerator {

	public static final String TYPE_LIB_PRJ_PROPERTIES_FILE_NAME = "type_library_project.properties";
	
	private static CallTrackingLogger s_logger = LogManager.getInstance(TypeLibraryProjectPropertiesGenerator.class);
	
	private static TypeLibraryProjectPropertiesGenerator s_typeLibraryProjectPropertiesGenerator;
	
	
	private TypeLibraryProjectPropertiesGenerator(){}
	
	public static TypeLibraryProjectPropertiesGenerator getInstance(){
		if(s_typeLibraryProjectPropertiesGenerator == null)
			s_typeLibraryProjectPropertiesGenerator = new TypeLibraryProjectPropertiesGenerator();
		
		return s_typeLibraryProjectPropertiesGenerator;
	}
	
	private static CallTrackingLogger getLogger(){
		return s_logger;
	}
	
	
	
	public void generate(TypeLibraryCodeGenContext libraryCodeGenContext) throws CodeGenFailedException{
		TypeLibraryInputOptions  typeLibraryInputOptions = libraryCodeGenContext.getTypeLibraryInputOptions();
		
		Properties properties = new Properties();
		
		String libraryName = typeLibraryInputOptions.getTypeLibraryName();
		if(!TypeLibraryUtilities.isEmptyString(libraryName))
			properties.put(TypeLibraryConstants.TYPE_LIBRARY_NAME, libraryName);
		
		
		String libraryNamespace = typeLibraryInputOptions.getLibraryNamespace();
		if(TypeLibraryUtilities.isEmptyString(libraryNamespace))
			libraryNamespace = TypeLibraryConstants.TYPE_INFORMATION_NAMESPACE;

		properties.put(TypeLibraryConstants.TYPE_LIBRARY_NAMESPACE, libraryNamespace);
		

		String libraryVersion = typeLibraryInputOptions.getLibraryVersion();
		if(TypeLibraryUtilities.isEmptyString(libraryVersion))
			libraryVersion = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_VERSION;

		properties.put(TypeLibraryConstants.TYPE_LIBRARY_VERSION, libraryVersion);


		String libraryCategory = typeLibraryInputOptions.getLibraryCategory();
		if(TypeLibraryUtilities.isEmptyString(libraryCategory))
			libraryCategory = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_CATEGORY;

		properties.put(TypeLibraryConstants.TYPE_LIBRARY_CATEGORY, libraryCategory);
		
		generatePropertiesFile(libraryCodeGenContext,properties);
		
	}

	private void generatePropertiesFile(TypeLibraryCodeGenContext libraryCodeGenContext, Properties properties) 
		throws CodeGenFailedException{
		TypeLibraryInputOptions  typeLibraryInputOptions = libraryCodeGenContext.getTypeLibraryInputOptions();
		
		OutputStream outputStream = null;
		String projectRoot = typeLibraryInputOptions.getProjectRoot();
		projectRoot = TypeLibraryUtilities.toOSFilePath(projectRoot);
		
		try {
			outputStream = TypeLibraryUtilities.getFileOutputStream(projectRoot,TYPE_LIB_PRJ_PROPERTIES_FILE_NAME);
			properties.store(outputStream,"*** Generated file, any changes will be lost upon regeneration ***");

			getLogger().log( Level.INFO, "Successfully generated " + TYPE_LIB_PRJ_PROPERTIES_FILE_NAME + " under " + projectRoot);
			
			
		} catch (IOException ioEx) {
			String errMsg = "Failed to generate : "	+ TYPE_LIB_PRJ_PROPERTIES_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ioEx);
			throw new CodeGenFailedException(errMsg, ioEx);
		} finally {
			TypeLibraryUtilities.closeOutputStream(outputStream);
		}
		
	}
	
	
}
