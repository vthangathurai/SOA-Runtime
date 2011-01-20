/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.codegen;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.ServiceCodeGenArgsParser;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions.TypeLibraryGenType;


public class TypeLibraryCodeGenArgsParser {
	
	private static Logger s_logger = LogManager.getInstance(ServiceCodeGenArgsParser.class);

	private static final TypeLibraryCodeGenArgsParser SINGLETON_INSTANCE = 
		new TypeLibraryCodeGenArgsParser();


	private TypeLibraryCodeGenArgsParser() {		
	}

	private Logger getLogger() {
		return s_logger;
	}

	public static TypeLibraryCodeGenArgsParser getInstance() {
		return SINGLETON_INSTANCE;
	}
	
	public TypeLibraryInputOptions parseTypeLibraryOptions(String[] args)
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {

//		since this is a singleton class, it is possible the same object would be used mutiple times, 
//		hence reset the instance level variables to their default values
		resetInstanceVariables();

//		Print the input arguments 
		String inputArguments = Arrays.toString(args);
		getLogger().log(Level.INFO, "Original TypeLibraryInput Args To codegen : \n" + inputArguments );

//		Parse input arguments 
		TypeLibraryInputOptions typeLibraryInputOptions = parseTypeLibraryArguments(args);

		typeLibraryInputOptions = processTypeLibraryInputOptions(typeLibraryInputOptions);

//		For performing defaulting , validation for defaulting . for any processing of InputOptions
		doAdditionalProcessing(typeLibraryInputOptions);

		return typeLibraryInputOptions;
	}
	
	/*
	 * Parse the arguments to fetch all the typelibrary genTypes
	 */
	public TypeLibraryInputOptions parseTypeLibraryGenTypes(String[] args) throws BadInputOptionException,BadInputValueException{
		TypeLibraryInputOptions typeLibraryOptions = new TypeLibraryInputOptions();
		int i = 0;
		int argsLength = args.length;
		while (i < argsLength) {
			String optName = (args[i] == null) ? null : args[i].toLowerCase();
			 if (TypeLibraryInputOptions.OPT_CODE_GEN_TYPE.equals(optName)) {
				i = getNextOptionIndex(i, args,optName,true);
				TypeLibraryGenType typeLibraryGenType = TypeLibraryGenType.getTypeLibraryGenType(args[i]);
				typeLibraryOptions.setCodeGenType(typeLibraryGenType);
			} 
			
			i++;
		}
		return typeLibraryOptions;
	}
	
	/*
	 * 
	 * Parse all the typelibrary input arguments
	 *
	 */
	
	private TypeLibraryInputOptions parseTypeLibraryArguments(String[] args) throws BadInputOptionException,BadInputValueException{
		TypeLibraryInputOptions typeLibraryOptions = new TypeLibraryInputOptions();
		if (args == null || args.length == 0) {
			// print usage information and exit
			throw new BadInputOptionException("Arguments for code generation missing");
		}
		int i = 0;
		int argsLength = args.length;
		while (i < argsLength) {
			String optName = args[i].toLowerCase();
			if(TypeLibraryInputOptions.OPT_PROJECT_ROOT.equals(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setProjectRoot(args[i]);
			}
			else if (TypeLibraryInputOptions.OPT_JAVA_SRC_GEN_DIR.equals(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setJavaSrcDestLocation(args[i]);
			} 
			else if (TypeLibraryInputOptions.OPT_META_SRC_GEN_DIR.equals(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setMetaSrcDestLocation(args[i]);
			} 
			else if (TypeLibraryInputOptions.OPT_META_SRC_DIR.equals(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setMetaSrcLocation(args[i]);
			} 
			else if(TypeLibraryInputOptions.OPT_LIBRARY_NAME.equals(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setTypeLibraryName(args[i]);
			}	
			else if(TypeLibraryInputOptions.OPT_XSD_TYPE.equals(optName)){
					i = getNextOptionIndex(i,args,optName,true);
					for(String XSDTypeName : args[i].split(","))
						typeLibraryOptions.getXsdTypes().add(XSDTypeName);
					
			}
			else if(TypeLibraryInputOptions.OPT_STAGING_AREA.equals(optName)){
					i = getNextOptionIndex(i,args,optName,true);
					typeLibraryOptions.setStagingArea(args[i]);
			}	
			else if(TypeLibraryInputOptions.OPT_DEPENDENT_LIBS.equals(optName)){
				  try{
					  i = getNextOptionIndex(i,args,optName,true);
					  typeLibraryOptions.setDependentTypeLibs(args[i]);
				  }catch(Exception e){
					  typeLibraryOptions.setDependentTypeLibs("");
				  }
			}	
			else if (TypeLibraryInputOptions.OPT_CODE_GEN_TYPE.equals(optName)) {
				i = getNextOptionIndex(i, args,optName,true);
				TypeLibraryGenType typeLibraryGenType = TypeLibraryGenType.getTypeLibraryGenType(args[i]);
				if (typeLibraryGenType == null) {
					throw new BadInputOptionException(
							"Invalid code gen type specified : " + args[i]);
				}
				typeLibraryOptions.setCodeGenType(typeLibraryGenType);
			} 
			else if (TypeLibraryInputOptions.OPT_LIBRARY_VERSION.equalsIgnoreCase(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setLibraryVersion(args[i]);
				
			}
			else if (TypeLibraryInputOptions.OPT_LIBRARY_NAMESPACE.equalsIgnoreCase(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setLibraryNamespace(args[i]);
			}
			else if (TypeLibraryInputOptions.OPT_LIBRARY_CATEGORY.equalsIgnoreCase(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setLibraryCategory(args[i]);
			} 
			else if (TypeLibraryInputOptions.OPT_LOG_CONFIG_FILE.equals(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setLogConfigFile(args[i]);
			}
			else if (TypeLibraryInputOptions.OPT_ADD_CP_TO_XJC.equalsIgnoreCase(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setAdditionalClassPathToXJC(args[i]);
			} 
			else if (TypeLibraryInputOptions.OPT_ADD_BUILD_CP_TO_XJC.equalsIgnoreCase(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				typeLibraryOptions.setAddBuildClassPathToXJC(args[i]);
			} 
			else if (TypeLibraryInputOptions.OPT_V4_CATALOG_FILE.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				typeLibraryOptions.setV4Catalog(args[i]);
			}
			else if (TypeLibraryInputOptions.OPT_V4_DEST_LOCATION.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				typeLibraryOptions.setV4DestLocation(args[i]);
			}
			else if (TypeLibraryInputOptions.OPT_V4_NS_2_PKG.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				typeLibraryOptions.setV4Pkg(args[i]);
			}
			else if (TypeLibraryInputOptions.OPT_V4_WSDL_LOCATION.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				typeLibraryOptions.setV4WsdlLocation(args[i]);
			}
			else if (InputOptions.OPT_JAVA_HOME.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				// do nothing for -javahome . This inputoption is already set.
			}
			else if (InputOptions.OPT_JDK_HOME.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				// do nothing for -jdkHome . This inputotption is already set.
			}
			else {
				String errorMessage = "Invalid option " + optName + " specified. This option is not recognized.";
				throw new BadInputOptionException(errorMessage);
			}
			
			i++;
		}
		return typeLibraryOptions;
	}
	
	private void resetInstanceVariables() {
		
	}
	
	private void doAdditionalProcessing(TypeLibraryInputOptions inputOptions) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {

		
	}
	
	private TypeLibraryInputOptions processTypeLibraryInputOptions(TypeLibraryInputOptions typeLibraryInputOptions)
	throws BadInputValueException {
		
		return typeLibraryInputOptions;
	}
	
	public static int getNextOptionIndex(int currentOptIndex, String[] args, String optionName,boolean shouldHaveValue)
	throws BadInputOptionException,BadInputValueException {
		int nextOptionIndex = currentOptIndex + 1;
		if (nextOptionIndex >= args.length) {
			throw new BadInputValueException("Missing parameter for '"
					+ args[currentOptIndex] + "' option.");
		}

		if(args[nextOptionIndex].startsWith("-") && shouldHaveValue){
			String errMsg = "Please provide a value for the option " + optionName;
			throw new BadInputValueException(errMsg);
		}
		
		String nextArgument = args[nextOptionIndex];
		if(CodeGenUtil.isEmptyString(nextArgument.trim())){
			String errMsg = "Please provide a proper value for the option " + optionName;
			throw new BadInputValueException(errMsg);
		}
				
		
		return nextOptionIndex;
	}
	
	public static String getOptionValue(String[] args, String optionName) {
		for (int i = 0; i < args.length; i++) {
			String option = args[i];
			if (option.equals(optionName)) {
				return args[i + 1];
			}
		}
		return null;
	}



}
