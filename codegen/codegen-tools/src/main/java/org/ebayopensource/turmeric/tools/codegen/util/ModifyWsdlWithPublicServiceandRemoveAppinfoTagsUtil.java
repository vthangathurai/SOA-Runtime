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
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.builders.WsdlWithPublicServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;


/**
 * @author aupadhay This class checks if "publicServiceName" property is set in
 *         service_metadata.properties and changes the input wsdl with correct
 *         name
 */

public class ModifyWsdlWithPublicServiceandRemoveAppinfoTagsUtil {

	private static final String PUBLIC_WSDL = "_public.wsdl";
	private static final String WSDL_LOC = "soa\\services\\wsdl\\";
	private static final String GENERATED_WSDL_LOC = CodeGenConstants.META_INF_FOLDER
			+ File.separator + WSDL_LOC;
	private static Logger s_logger = LogManager
			.getInstance(ModifyWsdlWithPublicServiceandRemoveAppinfoTagsUtil.class);
	
	
	public static void modifyWsdl(CodeGenContext codeGenCtx) throws CodeGenFailedException {
	
		String serviceName = codeGenCtx.getInputOptions().getServiceAdminName();
		//if publicServiceName is not set, generation is not required.
		if(CodeGenUtil.isEmptyString(serviceName)|| CodeGenUtil.isEmptyString(codeGenCtx.getProjectRoot()))
				return;
		
		String generatedwsdlPath = codeGenCtx.getProjectRoot() + File.separator
		+ CodeGenConstants.GEN_META_SRC_FOLDER + File.separator
		+ GENERATED_WSDL_LOC + serviceName + File.separator
		+ serviceName + PUBLIC_WSDL;	
		generatedwsdlPath = CodeGenUtil.toOSFilePath(generatedwsdlPath);
		boolean isGenRequired = isGenerationRequired(generatedwsdlPath,codeGenCtx.getInputOptions().getInputFile());
		if(isGenRequired)
		{
			WsdlWithPublicServiceGenerator generator = new WsdlWithPublicServiceGenerator();
			s_logger.log(Level.INFO, "Calling WSDLwithPublicServiceNmaeGenerator....");
			generator.generate(codeGenCtx);
			
		}
	}


	private static boolean isGenerationRequired(String generatedwsdlPath,
			String inputFileLoc) {
		File generatedFile = new File(generatedwsdlPath);
		File inputFile = new File(inputFileLoc);
		//if generated wsdl does not exist, it should be generated else check the timestamp.
		if(!(generatedFile.exists()))
			return true;
		else
		{
			long timeStampForGeneratedFile = generatedFile.lastModified();
			long timeStampForInputWsdl = inputFile.lastModified();
			if(timeStampForInputWsdl>timeStampForGeneratedFile)
				return true;
		}
		return false;
	}

	

}
