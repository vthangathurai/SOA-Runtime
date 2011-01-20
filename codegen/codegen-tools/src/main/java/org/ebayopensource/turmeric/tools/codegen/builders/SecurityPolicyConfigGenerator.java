/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.ConfigHelper;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

import org.ebayopensource.turmeric.common.config.ServiceSecurityConfig;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;

/**
 * Security Policy generator.
 * 
 * Generates either default security policy based on template or 
 * spcified by the user as input in the xml file.
 *   
 * 
 * @author rmandapati
 */
public class SecurityPolicyConfigGenerator  implements SourceGenerator {
	
	private static final String SECURITY_POLICY_CONFIG_TEMPLATE = 
		"org/ebayopensource/turmeric/tools/codegen/template/security-policy.tpt";	
	

	private static final String GEN_SECURITY_POLICY_CONFIG_DIR = "META-INF/soa/services/config";
	private static final String SECURITY_POLICY_CONFIG_FILE_NAME = "SecurityPolicy.xml";
	
	private static Logger s_logger = LogManager.getInstance(SecurityPolicyConfigGenerator.class);
	
	
	
	private static SecurityPolicyConfigGenerator s_securityPolicyGenerator  =
			new SecurityPolicyConfigGenerator();

	
	private Logger getLogger() {
		return s_logger;
	}
	

	private SecurityPolicyConfigGenerator() {}


	public static SecurityPolicyConfigGenerator getInstance() {
		return s_securityPolicyGenerator;
	}
	
	
	public boolean continueOnError() {
		return false;
	}

	
	public void generate(CodeGenContext codeGenCtx)  throws CodeGenFailedException {		
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		ServiceCodeGenDefType svcCodeGenDef = inputOptions.getSvcCodeGenDefType();
		// If Security Policy info specified in the XML 
		if (svcCodeGenDef != null &&
			svcCodeGenDef.getSecurityPolicyInfo() != null &&
			svcCodeGenDef.getSecurityPolicyInfo().getSecurityCfg() != null) {					
			ServiceSecurityConfig serviceSecurityCfg = 
					svcCodeGenDef.getSecurityPolicyInfo().getSecurityCfg();	
			
			createSecurityPolicyFile(codeGenCtx, serviceSecurityCfg);
		}
		else { 
			// get Security policy template content
			String templateContent = null;
			try {
				templateContent = CodeGenUtil.getTemplateContent(SECURITY_POLICY_CONFIG_TEMPLATE);
			} catch (Exception ex) {
				throw new CodeGenFailedException(
							"Failed to read : " + SECURITY_POLICY_CONFIG_TEMPLATE, ex);
			}
			
			createDefaultSecurityPolicyFile(templateContent, codeGenCtx);
		}		
		
	}	
	
	
	
	private void createSecurityPolicyFile(
			CodeGenContext codeGenCtx,
			ServiceSecurityConfig serviceSecurityConfig) throws CodeGenFailedException {
		
		try {
			InputOptions inputOptions = codeGenCtx.getInputOptions();        
	        String destFolderPath = 
	        		CodeGenUtil.genDestFolderPath(
	        		codeGenCtx.getMetaSrcDestLocation(), 
	        		inputOptions.getServiceAdminName(),
	        		GEN_SECURITY_POLICY_CONFIG_DIR);

	        ConfigHelper.generateSecurityPolicyXml(
	        			serviceSecurityConfig, 
	        			destFolderPath, 
	        			SECURITY_POLICY_CONFIG_FILE_NAME);
			
			getLogger().log(Level.INFO, 
					"Successfully generated " + SECURITY_POLICY_CONFIG_FILE_NAME);
	        
		} catch (Exception ex) {
			String errMsg = "Failed to generate " + SECURITY_POLICY_CONFIG_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ex);
			throw new CodeGenFailedException(errMsg , ex);
		} 
	}
	
	
	private void createDefaultSecurityPolicyFile(
			String fileContent, 
			CodeGenContext codeGenCtx) throws CodeGenFailedException {
		
		Writer fileWriter = null;
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();		
        String destFolderPath = 
    		CodeGenUtil.genDestFolderPath(
    				codeGenCtx.getMetaSrcDestLocation(), 
    				inputOptions.getServiceAdminName(),
    				GEN_SECURITY_POLICY_CONFIG_DIR);        
		
		try {
			fileWriter = CodeGenUtil.getFileWriter(destFolderPath, SECURITY_POLICY_CONFIG_FILE_NAME);
			fileWriter.write(fileContent);
			
			getLogger().log(Level.INFO, 
					"File " + SECURITY_POLICY_CONFIG_FILE_NAME + "generated under " + destFolderPath);

		} 
		catch (IOException ioEx) {
			String errMsg = "Failed to create : " + SECURITY_POLICY_CONFIG_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ioEx);
			throw new CodeGenFailedException(errMsg, ioEx);
		} 
		finally {
			CodeGenUtil.closeQuietly(fileWriter);
		}

	}


	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(GEN_SECURITY_POLICY_CONFIG_DIR)+ serviceAdminName + File.separatorChar + SECURITY_POLICY_CONFIG_FILE_NAME ;
		return filePath;
		
	}	
	
	
}
