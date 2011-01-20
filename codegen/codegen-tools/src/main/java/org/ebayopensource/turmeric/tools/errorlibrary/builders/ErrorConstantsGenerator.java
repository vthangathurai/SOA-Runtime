/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.builders;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.errorlibrary.ELDomainInfoHolder;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.SourceGeneratorErrorLib;
import org.ebayopensource.turmeric.tools.errorlibrary.codegen.ErrorLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.errorlibrary.util.ErrorLibraryUtils;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMod;


public class ErrorConstantsGenerator extends ErrorLibraryBaseGenerator
				implements SourceGeneratorErrorLib{
		
	private static final String ERROR_CONSTANTS_CLASSNAME = "ErrorConstants";
	private static final String ERROR_DOMAIN_NAME = "ERRORDOMAIN";
	
	private static Logger s_logger = LogManager.getInstance(ErrorDataCollectionGenerator.class);

	private static ErrorConstantsGenerator s_errorConstantsGenerator  =
		new ErrorConstantsGenerator();

	private ErrorConstantsGenerator() {
	}

	public static ErrorConstantsGenerator getInstance() {
		return s_errorConstantsGenerator;
	}

	private Logger getLogger() {
		return s_logger;
	}


	public void generate(ErrorLibraryCodeGenContext codeGenContext)  throws CodeGenFailedException {
		
		ErrorLibraryInputOptions inputOptions = codeGenContext.getInputOptions();

		List<String> listOfDomains = inputOptions.getDomainList();
		
		populateCodeGenContext(codeGenContext, listOfDomains);
		
		getLogger().log(Level.FINE, "Populated CodeGenContext with required data");

		StringBuffer codegenFailedMessage = new StringBuffer();
		boolean codegenFailed = false;
		for (String domainName : listOfDomains) {
			try {
				generateErrorConstants(codeGenContext, domainName);
			} catch (PreProcessFailedException exception) {
				codegenFailed = true;
				codegenFailedMessage.append("ErrorConstants generation for the domain \"").append(domainName)
						.append("\" failed. ").append(exception.getMessage()).append("\n\n");
			}catch (CodeGenFailedException exception) {
				s_logger.log(Level.INFO, "Unable to generate domain: " + domainName, exception);
				codegenFailed = true;
				// TODO: fix to capture entire stack trace so that line # and cause is not lost.
				codegenFailedMessage.append("ErrorConstants generation for the domain \"").append(domainName)
						.append("\" failed. ").append(exception.getMessage()).append("\n\n");
			}
		}
		
		if(codegenFailed)
			throw new CodeGenFailedException("CodeGen failed : " + codegenFailedMessage.toString());
		
	}
	
	private void generateErrorConstants(ErrorLibraryCodeGenContext codeGenContext, String domainName)
					throws CodeGenFailedException, PreProcessFailedException{


		ELDomainInfoHolder holder = codeGenContext.getDomainInfoMap().get(domainName);
		
		String fullyQualifiedClassName = ErrorLibraryUtils.getFullyQualifiedClassName(holder.getPackageName(), 
						ERROR_CONSTANTS_CLASSNAME);

		JCodeModel codeModel = new JCodeModel();
		
		JDefinedClass targetClass = createNewClass(codeModel, fullyQualifiedClassName);
		
		validateMetaData(codeGenContext, domainName);
		
		getLogger().log(Level.FINE, "Validation of Metadata files successful for domain " + domainName);
		
		addFields(codeGenContext, codeModel, targetClass, domainName); 
		
		generateJavaFile(codeModel, codeGenContext.getGenJavaSrcDestFolder());
		
		getLogger().log(Level.INFO, "Successfully generated " + fullyQualifiedClassName + " for the domain " + domainName);
	}
	
	private void addFields(ErrorLibraryCodeGenContext codeGenContext, JCodeModel codeModel,
			JDefinedClass targetClass, String domain) throws CodeGenFailedException{
		Set<String> errorNameSet = null;
		
		try {
			errorNameSet = ErrorLibraryUtils.getUniqueXMLErrorNames(codeGenContext, domain);
		} catch (PreProcessFailedException exception) {
			CodeGenFailedException codeGenFailedException = new 
        	CodeGenFailedException("ErrorConstants generation failed : " +
        			exception.getMessage(), exception);
			throw codeGenFailedException;
		}
		JClass stringJClass = getJClass(String.class, codeModel);
		
		targetClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, stringJClass, ERROR_DOMAIN_NAME, 
				JExpr.lit(domain));
		
		for (String errorName : errorNameSet) {
			targetClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, stringJClass, 
					errorName.toUpperCase(), JExpr.lit(errorName));
		}
		
		getLogger().log(Level.INFO, "Total number of Error constants " + errorNameSet.size());
		
		targetClass.constructor(JMod.PRIVATE);
	}
	
}
