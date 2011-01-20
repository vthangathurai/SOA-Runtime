/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.codegen;

import java.util.List;

import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;


public class ErrorLibraryCodeGenArgsValidator {
	private static final ErrorLibraryCodeGenArgsValidator SINGLETON_INSTANCE = 
			new ErrorLibraryCodeGenArgsValidator();
		
	
	private ErrorLibraryCodeGenArgsValidator() {		
	}
	
	
	public static ErrorLibraryCodeGenArgsValidator getInstance() {
		return SINGLETON_INSTANCE;
	}

	
	public void validate(ErrorLibraryInputOptions inputOptions)
			throws MissingInputOptionException, BadInputValueException {
		
		StringBuffer validatorBuilder = new StringBuffer(1000);
		
		if(inputOptions == null) {
			throw new BadInputValueException("InputOptions is null");
		} 
		
		if (inputOptions.getCodeGenType() == null) {
			validatorBuilder.append("Code gen type is missing which is mandatory. Pls provide the value for this option ")
							.append(ErrorLibraryInputOptions.OPT_CODE_GEN_TYPE).append("\n");
		}
		
		if(!ErrorLibraryInputOptions.isGenTypeErrorLibrary(inputOptions)){
			validatorBuilder.append("Invalid ErrorLibrary gentype. Pls provide valid value for this option ")
			.append(ErrorLibraryInputOptions.OPT_CODE_GEN_TYPE).append("\n");
		}

		if(CodeGenUtil.isEmptyString(inputOptions.getProjectRoot())){
			// Possibly invalid. (only if dest and metasrc are not set)
			if(CodeGenUtil.isEmptyString(inputOptions.getDestLocation()) &&
					CodeGenUtil.isEmptyString(inputOptions.getMetaSrcDir()))
			{
				validatorBuilder.append("Project Root is missing.");
				validatorBuilder.append(" Please provide the value for this option ");
				validatorBuilder.append(ErrorLibraryInputOptions.OPT_PROJECT_ROOT);
				validatorBuilder.append(" or provide values for ");
				validatorBuilder.append(ErrorLibraryInputOptions.OPT_DEST_LOCATION);
				validatorBuilder.append(" and ");
				validatorBuilder.append(ErrorLibraryInputOptions.OPT_META_SRC_DIR);
				validatorBuilder.append("\n");
			}
			
			if(CodeGenUtil.isEmptyString(inputOptions.getDestLocation()))
			{
				validatorBuilder.append("Project Generated Content Destination is missing.");
				validatorBuilder.append(" Please provide the value for this option ");
				validatorBuilder.append(ErrorLibraryInputOptions.OPT_DEST_LOCATION);
				validatorBuilder.append("\n");
			}

			if(CodeGenUtil.isEmptyString(inputOptions.getMetaSrcDir()))
			{
				validatorBuilder.append("Project Meta Src Dir is missing.");
				validatorBuilder.append(" Please provide the value for this option ");
				validatorBuilder.append(ErrorLibraryInputOptions.OPT_META_SRC_DIR);
				validatorBuilder.append("\n");
			}
		} else if(!CodeGenUtil.dirExists(inputOptions.getProjectRoot())) {
		    validatorBuilder.append("Project Root directory does not exist (or is not a directory): ");
		    validatorBuilder.append(inputOptions.getProjectRoot());
            validatorBuilder.append("\n");
		}

		if(CodeGenUtil.isEmptyString(inputOptions.getErrorLibraryName())){
			validatorBuilder.append("Error Library name is missing which is mandatory. Pls provide the value for this option ")
			.append(ErrorLibraryInputOptions.OPT_ERRORLIBRARY_NAME).append("\n");
		}

		List<String> domainListInputOption = inputOptions.getDomainList();
		// NOTE: if no domains are provided on the command line, then the
		//       domains in domain_list.properties are used, the validation
		//       for this step is performed later by the ErrorLibraryCodeGenBuilder
		if(!domainListInputOption.isEmpty()){
			// Validate the values
			for (String domain : domainListInputOption) {
				if(CodeGenUtil.isEmptyString(domain.trim())){
					validatorBuilder.append("Domains cannot be empty. Pls provide a valid domain name.");
					break;
				}
			}
		}
		
		if(!CodeGenUtil.isEmptyString(validatorBuilder.toString())){
			throw new MissingInputOptionException(validatorBuilder.toString());
		}

	}
	
}
