/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.codegen;

import java.util.List;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.handler.ConsoleResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.DontPromptResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.V4TypeMappings;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions.TypeLibraryGenType;
import org.ebayopensource.turmeric.tools.library.builders.CodeGenTypeLibraryGenerator;
import org.ebayopensource.turmeric.tools.library.builders.TypeLibraryProjectPropertiesGenerator;
import org.ebayopensource.turmeric.tools.library.utils.V4TypeMappingsFactory;


public class TypeLibraryCodeGenBuilder {
	
	private static CallTrackingLogger s_logger = LogManager.getInstance(TypeLibraryCodeGenBuilder.class);
	
	
	private CallTrackingLogger getLogger() {
		return s_logger;
	}
	
	public void buildTypeLibrary(
			TypeLibraryInputOptions typeLibraryInputOptions, 
			UserResponseHandler responseHandler) throws CodeGenFailedException,Exception,BadInputValueException  {
		
    	//performLoggingInit(inputArgs);
    	s_logger = LogManager.getInstance(TypeLibraryCodeGenBuilder.class); //KEEPME  //PCR 
		
		getLogger().log(Level.INFO, "BEGIN: Service code generation ....");
		
		long startTime = System.currentTimeMillis();
		
				
		UserResponseHandler userResponseHandler = responseHandler;		
		if (typeLibraryInputOptions.isDontPrompt()) {
			userResponseHandler = new DontPromptResponseHandler();
		} 
		else if (userResponseHandler == null) {
			userResponseHandler = new ConsoleResponseHandler();
		}
		
		TypeLibraryCodeGenContext typeLibraryCodeGenContext = createContext(typeLibraryInputOptions, userResponseHandler);
	
		generateTypeLibrary(typeLibraryCodeGenContext);
		
		long endTime = System.currentTimeMillis();
		
		getLogger().log(Level.INFO, "END: Service code generation, took : " + (endTime - startTime) + " ms");
	} 
	
	public TypeLibraryInputOptions getTypeLibraryInputOptions(String[] args) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		// Parse & Validate input arguments 
		TypeLibraryInputOptions typeLibraryInputOptions = TypeLibraryCodeGenArgsParser.getInstance().parseTypeLibraryOptions(args);
		TypeLibraryCodeGenArgsValidator.getInstance().validate(typeLibraryInputOptions);
		
		getLogger().log(Level.INFO, "TypeLibrary Input Options : \n" + typeLibraryInputOptions.toString());
		
		return typeLibraryInputOptions;
	
	}
	
	public TypeLibraryInputOptions getTypeLibraryInputGenTypes(String[] args) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		// only parse here don't validate 
		TypeLibraryInputOptions typeLibraryInputOptions = TypeLibraryCodeGenArgsParser.getInstance().parseTypeLibraryGenTypes(args);
		
		//getLogger().log(Level.INFO, "Gen Types : \n" + typeLibraryInputOptions.toString()); Don't print here the input optons is yet to be populated
		
		return typeLibraryInputOptions;
	
	}
	

	
	private TypeLibraryCodeGenContext createContext(
			TypeLibraryInputOptions typeLibraryOptions,
			UserResponseHandler userResponseHandler) 
			throws CodeGenFailedException {
	
		TypeLibraryCodeGenContext typeLibraryCodeGenCtx = 
				new TypeLibraryCodeGenContext(typeLibraryOptions, userResponseHandler);
		
			
		return typeLibraryCodeGenCtx;
	}
	

	/*private boolean isGenTypeTypeLibrary (TypeLibraryCodeGenContext typeLibraryCodeGenContext) {
		TypeLibraryInputOptions inputOptions = typeLibraryCodeGenContext.getTypeLibraryInputOptions();
		TypeLibraryGenType codeGenType = inputOptions.getCodeGenType();
		return !((codeGenType == TypeLibraryGenType.genTypeAddType) || 
				 (codeGenType == TypeLibraryGenType.genTypeDeleteType) ||
				 (codeGenType == TypeLibraryGenType.genTypeCleanBuildTypeLibrary) ||
				 (codeGenType == TypeLibraryGenType.genTypeIncrBuildTypeLibrary) ||
				 (codeGenType == TypeLibraryGenType.genTypeCreateTypeLibrary) );
		
		
	}*/
	
	public boolean isGenTypeTypeLibrary(TypeLibraryInputOptions typeLibraryInputOptions) {
		boolean isTypeLibrary = false;
		//TypeLibraryGenType typeLibraryGenType = typeLibraryInputOptions.getCodeGenType();
		if(typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeAddType ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeDeleteType ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeCleanBuildTypeLibrary ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeIncrBuildTypeLibrary ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeCreateTypeLibrary || 
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.V4) {
	
			isTypeLibrary = true;
		}
				
		return isTypeLibrary;
	
	}
	
	public void generateTypeLibrary(TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException,BadInputValueException,Exception {
		TypeLibraryInputOptions typeLibraryInputOptions = codeGenCtx.getTypeLibraryInputOptions();
		TypeLibraryGenType typeLibraryGenType = typeLibraryInputOptions.getCodeGenType();
		String libraryName = codeGenCtx.getLibraryName();
		String projectRoot = typeLibraryInputOptions.getProjectRoot();
		String stagingLocation = typeLibraryInputOptions.getStaging();
		String dependentTypeLibs = typeLibraryInputOptions.getDependentTypeLibs();
		List<String> xsdTypes = typeLibraryInputOptions.getXsdTypes();
		String libraryVersion = typeLibraryInputOptions.getLibraryVersion();
		String libraryNs = typeLibraryInputOptions.getLibraryNamespace();
		String libraryCategory = typeLibraryInputOptions.getLibraryCategory();
		
		if (typeLibraryGenType == TypeLibraryGenType.genTypeCleanBuildTypeLibrary) {
				CodeGenTypeLibraryGenerator.genTypeCleanBuildTypeLibrary(
					projectRoot, libraryName, stagingLocation,
					dependentTypeLibs,codeGenCtx);
		}
		else if (typeLibraryGenType == TypeLibraryGenType .genTypeIncrBuildTypeLibrary) {
			CodeGenTypeLibraryGenerator.genTypeIncrBuildTypeLibrary(libraryName, xsdTypes,codeGenCtx);
		}
		else if (typeLibraryGenType == TypeLibraryGenType.genTypeAddType) {
			CodeGenTypeLibraryGenerator.genTypeAddType(libraryName, xsdTypes,codeGenCtx);
		}
		else if (typeLibraryGenType == TypeLibraryGenType .genTypeDeleteType) {
				CodeGenTypeLibraryGenerator.genTypeDeleteType(libraryName,xsdTypes,codeGenCtx);
		}
		else if (typeLibraryGenType == TypeLibraryGenType .genTypeCreateTypeLibrary) {
			
			try{
				TypeLibraryProjectPropertiesGenerator.getInstance().generate(codeGenCtx);
			}catch(Exception e){
				getLogger().log(Level.SEVERE, "The properties file could not be generated.");
			}
			
			CodeGenTypeLibraryGenerator.genTypeCreateTypeLibrary(libraryName,libraryNs,libraryVersion,libraryCategory,codeGenCtx);
			
			
		}
		else if (typeLibraryGenType == TypeLibraryGenType.V4){
			V4TypeMappings v4TypeMappings = V4TypeMappingsFactory.getInstance(codeGenCtx);
			v4TypeMappings.generate(codeGenCtx);
		}
	}

}
