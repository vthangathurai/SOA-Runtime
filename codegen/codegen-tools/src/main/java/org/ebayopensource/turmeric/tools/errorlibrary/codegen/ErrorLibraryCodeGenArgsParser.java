/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.codegen;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions.ErrorLibraryGenType;
import org.ebayopensource.turmeric.tools.errorlibrary.util.ErrorLibraryUtils;


public class ErrorLibraryCodeGenArgsParser {
	
	private static Logger s_logger = LogManager.getInstance(ErrorLibraryCodeGenArgsParser.class);

	private static final ErrorLibraryCodeGenArgsParser SINGLETON_INSTANCE = 
		new ErrorLibraryCodeGenArgsParser();


	private ErrorLibraryCodeGenArgsParser() {		
	}

	private Logger getLogger() {
		return s_logger;
	}

	public static ErrorLibraryCodeGenArgsParser getInstance() {
		return SINGLETON_INSTANCE;
	}
	
	public ErrorLibraryInputOptions parseErrorLibraryOptions(String[] args)
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		
 
		String inputArguments = Arrays.toString(args);
		getLogger().log(Level.INFO, "Original ErrorLibraryInput Args To codegen : \n" + inputArguments );

//		Parse input arguments 
		ErrorLibraryInputOptions errorLibraryInputOptions = parseErrorLibraryArguments(args);

		errorLibraryInputOptions = processErrorLibraryInputOptions(errorLibraryInputOptions);

//		For performing defaulting , validation for defaulting . for any processing of InputOptions
		doAdditionalProcessing(errorLibraryInputOptions);

		return errorLibraryInputOptions;
	}
	
	/*
	 * Parse the arguments to fetch all the errorlibrary genTypes
	 */
	public ErrorLibraryInputOptions parseErrorLibraryGenTypes(String[] args) throws BadInputOptionException,BadInputValueException{
		ErrorLibraryInputOptions errorLibraryOptions = new ErrorLibraryInputOptions();
		int i = 0;
		int argsLength = args.length;
		while (i < argsLength) {
			String optName = (args[i] == null) ? null : args[i].toLowerCase();
			 if (ErrorLibraryInputOptions.OPT_CODE_GEN_TYPE.equals(optName)) {
				i = getNextOptionIndex(i, args,optName,true);
				ErrorLibraryGenType errorLibraryGenType = ErrorLibraryGenType.getErrorLibraryGenType(args[i]);
				errorLibraryOptions.setCodeGenType(errorLibraryGenType);
			} 
			
			i++;
		}
		//getLogger().log(Level.INFO, "Gentype passed was " + errorLibraryOptions.getCodeGenType() );
		return errorLibraryOptions;
	}
	
	/*
	 * 
	 * Parse all the errorlibrary input arguments
	 *
	 */
	
	private ErrorLibraryInputOptions parseErrorLibraryArguments(String[] args) throws BadInputOptionException,BadInputValueException{
		ErrorLibraryInputOptions errorLibraryOptions = new ErrorLibraryInputOptions();
		if (args == null || args.length == 0) {
			// print usage information and exit
			throw new BadInputOptionException("Arguments for code generation missing");
		}
		int i = 0;
		int argsLength = args.length;
		while (i < argsLength) {
			String optName = args[i].toLowerCase();
			if(ErrorLibraryInputOptions.OPT_PROJECT_ROOT.equals(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				errorLibraryOptions.setProjectRoot(args[i]);
				
			}
			else if(ErrorLibraryInputOptions.OPT_ERRORLIBRARY_NAME.equals(optName)){
				i = getNextOptionIndex(i,args,optName,true);
				errorLibraryOptions.setErrorLibraryName(args[i]);
				
			}
			else if (ErrorLibraryInputOptions.OPT_CODE_GEN_TYPE.equals(optName)) {
				i = getNextOptionIndex(i, args,optName,true);
				ErrorLibraryGenType errorLibraryGenType = ErrorLibraryGenType.getErrorLibraryGenType(args[i]);
				if (errorLibraryGenType == null) {
					throw new BadInputOptionException(
							"Invalid code gen type specified : " + args[i]);
				}
				errorLibraryOptions.setCodeGenType(errorLibraryGenType);
			}
			else if (ErrorLibraryInputOptions.OPT_LIST_OF_DOMAIN.equals(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				errorLibraryOptions.getDomainList().addAll(ErrorLibraryUtils.getListOfDomains(args[i]));
			}
			else if (ErrorLibraryInputOptions.OPT_DEST_LOCATION.equals(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				errorLibraryOptions.setDestLocation(args[i]);
			}
			else if (ErrorLibraryInputOptions.OPT_META_SRC_DIR.equals(optName)) {
				i = getNextOptionIndex(i,args,optName,true);
				errorLibraryOptions.setMetaSrcDir(args[i]);
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
		return errorLibraryOptions;
	}
	private void doAdditionalProcessing(ErrorLibraryInputOptions inputOptions) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {

		
	}
	
	private ErrorLibraryInputOptions processErrorLibraryInputOptions(ErrorLibraryInputOptions errorLibraryInputOptions)
	throws BadInputValueException {
		
		return errorLibraryInputOptions;
	}
	
	public static int getNextOptionIndex(int currentOptIndex, String[] args, String optionName,boolean shouldHaveValue)
	throws BadInputOptionException,BadInputValueException {
		int nextOptionIndex = currentOptIndex + 1;
		if (nextOptionIndex >= args.length) {
			throw new BadInputValueException("Missing parameter for '"
					+ args[currentOptIndex] + "' option.");
		}

		if(args[nextOptionIndex] != null && args[nextOptionIndex].startsWith("-") && shouldHaveValue){
			String errMsg = "Please provide a value for the option " + optionName;
			throw new BadInputValueException(errMsg);
		}
		
		String nextArgument = args[nextOptionIndex];
		if(CodeGenUtil.isEmptyString(nextArgument)){
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
