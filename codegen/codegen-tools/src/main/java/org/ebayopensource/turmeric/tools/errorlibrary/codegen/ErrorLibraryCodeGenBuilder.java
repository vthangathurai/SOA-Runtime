/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.codegen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.wsdl.WSDLException;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.SourceGeneratorErrorLib;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions.ErrorLibraryGenType;
import org.ebayopensource.turmeric.tools.errorlibrary.builders.ErrorConstantsGenerator;
import org.ebayopensource.turmeric.tools.errorlibrary.builders.ErrorDataCollectionGenerator;
import org.ebayopensource.turmeric.tools.errorlibrary.util.ErrorLibraryUtils;


public class ErrorLibraryCodeGenBuilder {
	
	private static CallTrackingLogger s_logger = LogManager.getInstance(ErrorLibraryCodeGenBuilder.class);
	
	
	private CallTrackingLogger getLogger() {
		return s_logger;
	}
	
	public void buildErrorLibrary(
			ErrorLibraryInputOptions errorLibraryInputOptions) throws CodeGenFailedException,Exception,BadInputValueException  {
		
    	s_logger = LogManager.getInstance(ErrorLibraryCodeGenBuilder.class); //KEEPME  //PCR 
		
		getLogger().log(Level.INFO, "BEGIN: ErrorLibrary code generation ....");
		
		long startTime = System.currentTimeMillis();
		
		ErrorLibraryCodeGenContext errorLibraryCodeGenContext = createContext(errorLibraryInputOptions);
	
		internalStartCodeGen(errorLibraryCodeGenContext);
		
		long endTime = System.currentTimeMillis();
		
		getLogger().log(Level.INFO, "END: ErrorLibrary code generation, took : " + (endTime - startTime) + " ms");
	} 
	
	public ErrorLibraryInputOptions getErrorLibraryInputOptions(String[] args) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		// Parse & Validate input arguments 
		ErrorLibraryInputOptions errorLibraryInputOptions = ErrorLibraryCodeGenArgsParser.getInstance().parseErrorLibraryOptions(args);
		ErrorLibraryCodeGenArgsValidator.getInstance().validate(errorLibraryInputOptions);
		
		getLogger().log(Level.INFO, "ErrorLibrary Input Options : \n" + errorLibraryInputOptions.toString());
		
		return errorLibraryInputOptions;
	
	}
	
	public ErrorLibraryInputOptions getErrorLibraryInputGenTypes(String[] args) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		// only parse here don't validate 
		ErrorLibraryInputOptions errorLibraryInputOptions = ErrorLibraryCodeGenArgsParser.getInstance().parseErrorLibraryGenTypes(args);
		
		return errorLibraryInputOptions;
	
	}
	

	
	private ErrorLibraryCodeGenContext createContext(
			ErrorLibraryInputOptions errorLibraryOptions) 
			throws CodeGenFailedException {
	
		ErrorLibraryCodeGenContext errorLibraryCodeGenCtx = 
				new ErrorLibraryCodeGenContext(errorLibraryOptions);
		
		// create directories if doesn't exists
		try {
			CodeGenUtil.createDir(errorLibraryCodeGenCtx.getGenJavaSrcDestFolder());
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}
			
		return errorLibraryCodeGenCtx;
	}
	
	
	public boolean isGenTypeErrorLibrary(ErrorLibraryInputOptions errorLibraryInputOptions) {
		boolean isErrorLibrary = false;
		
		isErrorLibrary = ErrorLibraryUtils.isGenTypeErrorLibrary(errorLibraryInputOptions);
				
		return isErrorLibrary;
	
	}
	

	private void internalStartCodeGen(ErrorLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException, WSDLException, MissingInputOptionException, PreProcessFailedException {		
		
		List<SourceGeneratorErrorLib> codeGeneratorList = getCodeGenerators(codeGenCtx);		
		for (SourceGeneratorErrorLib codeGenerator : codeGeneratorList) {	
			String logMsg = codeGenerator.getClass().getSimpleName() + ".generate()";
			try {
				getLogger().log(Level.INFO, "BEGIN: " + logMsg);
				codeGenerator.generate(codeGenCtx);	
				getLogger().log(Level.INFO, "END: " + logMsg);
			} catch (CodeGenFailedException exception) {
					getLogger().log(Level.SEVERE, "ERROR: " + logMsg, exception.toString());
					throw exception;
			}
		}
		
	}	
	
	private List<SourceGeneratorErrorLib> getCodeGenerators(ErrorLibraryCodeGenContext codeGenCtx) throws MissingInputOptionException, PreProcessFailedException {
		
		ErrorLibraryInputOptions inputOptions = codeGenCtx.getInputOptions();		
		List<SourceGeneratorErrorLib> codeGenerators = new ArrayList<SourceGeneratorErrorLib>();
		ErrorLibraryGenType errorLibraryGenType = inputOptions.getCodeGenType();
		
		if (errorLibraryGenType == ErrorLibraryGenType.genTypeConstants) {
			codeGenerators.add(ErrorConstantsGenerator.getInstance());
		}
		else if (errorLibraryGenType == ErrorLibraryGenType.genTypeDataCollection) {
			codeGenerators.add(ErrorDataCollectionGenerator.getInstance());
		}
		else if (errorLibraryGenType == ErrorLibraryGenType.genTypeErrorLibAll) {
			codeGenerators.add(ErrorConstantsGenerator.getInstance());
			codeGenerators.add(ErrorDataCollectionGenerator.getInstance());	
		}
		else if(errorLibraryGenType == ErrorLibraryGenType.genTypeCommandLineAll) {
			List<String> domainList = inputOptions.getDomainList();
			
			if(domainList.isEmpty()) {
				String listOfDomainFromPropertiesFile = null;
				listOfDomainFromPropertiesFile = ErrorLibraryUtils.readDomainListFromErrorLibraryProperties(codeGenCtx);
				if(listOfDomainFromPropertiesFile == null) {
					throw new MissingInputOptionException(
							"List of domains is missing which is mandatory. "
									+ "Pls provide the value for this option -domain");
				}
				domainList = ErrorLibraryUtils.getListOfDomains(listOfDomainFromPropertiesFile);
				if(!codeGenCtx.getInputOptions().getDomainList().isEmpty()) {
					codeGenCtx.getInputOptions().getDomainList().clear();
				}
				codeGenCtx.getInputOptions().getDomainList().addAll(domainList);
			}
			codeGenerators.add(ErrorConstantsGenerator.getInstance());
			codeGenerators.add(ErrorDataCollectionGenerator.getInstance());	
		}
		
		return codeGenerators;
		
	}
	
}
