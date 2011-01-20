/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.builders;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXB;

import org.ebayopensource.turmeric.tools.codegen.builders.BaseCodeGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.ELDomainInfoHolder;
import org.ebayopensource.turmeric.tools.errorlibrary.codegen.ErrorLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.errorlibrary.util.ErrorLibraryUtils;

import org.ebayopensource.turmeric.common.config.ErrorBundle;

public abstract class ErrorLibraryBaseGenerator extends BaseCodeGenerator{


	protected void populateCodeGenContext(ErrorLibraryCodeGenContext codeGenContext, List<String> listOfDomain) throws CodeGenFailedException {
		
		for (String domainName : listOfDomain) {
			String xmlLocation = ErrorLibraryUtils.getXMLLocationForDomain(codeGenContext, domainName);
            if (!CodeGenUtil.isFileExists(xmlLocation)) {
                throw new CodeGenFailedException("ErrorData.xml for domain [" + domainName + "] not found: " + xmlLocation);
            }
			ELDomainInfoHolder holder = new ELDomainInfoHolder();
			ErrorBundle errorBundle = null;
			try {
//				JAXBContext jc = JAXBRIContext.newInstance(ErrorBundle.class);
//				Unmarshaller u = jc.createUnmarshaller();
//				ErrorBundle errorBundle = (ErrorBundle) u.unmarshal(new FileInputStream(xmlLocation));
				
				File xmlFile = new File(xmlLocation);
				errorBundle = JAXB.unmarshal(xmlFile, ErrorBundle.class);
				
				if(errorBundle != null)
					holder.setErrorBundle(errorBundle);
			} catch (Exception exception) {
				CodeGenFailedException codeGenFailedException = new 
	        	CodeGenFailedException(exception.getMessage(), exception);
				throw codeGenFailedException;
			}
			
			if(errorBundle != null){
				codeGenContext.setOrganization(errorBundle.getOrganization());
				if(errorBundle.getPackageName() != null){
					holder.setPackageName(errorBundle.getPackageName().toLowerCase());
				}
				if(errorBundle.getLibraryVersion() != null && codeGenContext.getVersion() == null)
					codeGenContext.setVersion(errorBundle.getLibraryVersion());
				codeGenContext.getDomainInfoMap().put(domainName, holder);
			}
		}
		
	}
	
	protected void validateMetaData(ErrorLibraryCodeGenContext codeGenContext, String domainName) throws CodeGenFailedException {
		boolean isValidated = false;
		try {
			isValidated = ErrorLibraryUtils.validateMetadataFiles(codeGenContext, domainName);
		} catch (PreProcessFailedException exception) {
			CodeGenFailedException codeGenFailedException = new 
        	CodeGenFailedException("Metadata Validation failed : " +
        			exception.getMessage(), exception);
			throw codeGenFailedException;
		}
		if(!isValidated){
			CodeGenFailedException codeGenFailedException = new 
        	CodeGenFailedException("Metadata validation failed." +
        			"\n The metadata files are not consistent. The Error.properties " +
        			"must contain all the errors defined in ErrorData.xml.");
			throw codeGenFailedException;
		}
	}
}
