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

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConfigUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * Global Client configuration generator.
 * 
 * Generates global client configuration based on a template.
 * Client group config is included optionally based on user input.
 *   
 * 
 * @author rmandapati
 */
public class GlobalClientConfigGenerator implements SourceGenerator {

	private static final String GLOBAL_CLIENT_CONFIG_TEMPLATE = 
		"org/ebayopensource/turmeric/tools/codegen/template/globalclientconfig.tpt";	
	private static final String CLIENT_GROUP_CONFIG_TEMPLATE = 
		"org/ebayopensource/turmeric/tools/codegen/template/clientgroupconfig.tpt";
	

	
	private static final String GEN_CLIENT_CONFIG_DIR = "META-INF/soa/client/config";
	private static final String GLOBAL_CLIENT_CONFIG_FILE_NAME = "GlobalClientConfig.xml";
	
	private static final String CLIENT_CONFIG_GROUPS = "@@CLIENT_CONFIG_GROUPS@@";
	private static final String GROUP_NAME = "@@GROUP_NAME@@";
	
	private static final String  DEFAULT_CLIENT_GROUP_NAME = "CommonClientGroup";
	
	private static Logger s_logger = LogManager.getInstance(GlobalClientConfigGenerator.class);
	
	private static GlobalClientConfigGenerator s_globalClientCfgGenerator  =
		new GlobalClientConfigGenerator();

	
	private Logger getLogger() {
		return s_logger;
	}
	

	private GlobalClientConfigGenerator() {}


	public static GlobalClientConfigGenerator getInstance() {
		return s_globalClientCfgGenerator;
	}
	
	
	public boolean continueOnError() {
		return false;
	}
	
	

	public void generate(CodeGenContext codeGenCtx)  throws CodeGenFailedException  {
		// get Global Client Config template content
		String clientCfgContent = null;
		try {
			clientCfgContent = CodeGenUtil.getTemplateContent(GLOBAL_CLIENT_CONFIG_TEMPLATE);
		} catch (Exception ex) {
			throw new CodeGenFailedException(
						"Failed to read : " + GLOBAL_CLIENT_CONFIG_TEMPLATE, ex);
		}
		clientCfgContent = CodeGenConfigUtil.addPackageDetailsToTemplateClasses(clientCfgContent);
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		String clientCfgGroupName = inputOptions.getClientCfgGroupName();
		if (CodeGenUtil.isEmptyString(clientCfgGroupName)) {
			clientCfgGroupName = DEFAULT_CLIENT_GROUP_NAME;
		} 
		
		String groupCfgContent = null;
		try {
			groupCfgContent = CodeGenUtil.getTemplateContent(CLIENT_GROUP_CONFIG_TEMPLATE);
		} catch (Exception ex) {
			throw new CodeGenFailedException(
						"Failed to read : " + CLIENT_GROUP_CONFIG_TEMPLATE, ex);
		}		
		groupCfgContent = CodeGenConfigUtil.addPackageDetailsToTemplateClasses(groupCfgContent);
		
		groupCfgContent = groupCfgContent.replaceAll(GROUP_NAME, clientCfgGroupName);
		clientCfgContent = clientCfgContent.replaceAll(CLIENT_CONFIG_GROUPS, groupCfgContent);
			

		// generate a new Global Client Config file
		createConfigFile(clientCfgContent, codeGenCtx);
	}
	
	
	private void createConfigFile(String fileContent, CodeGenContext codeGenCtx)
			throws CodeGenFailedException {

		String destFolder = getDestFolder(codeGenCtx);
		Writer fileWriter = getFileWriter(destFolder);
		try {
			fileWriter.write(fileContent);
			getLogger().log(Level.INFO, "Successfully generated " + GLOBAL_CLIENT_CONFIG_FILE_NAME + " under " + destFolder);
		} 
		catch (IOException ioEx) {
			String errMsg = "Failed to create : " + GLOBAL_CLIENT_CONFIG_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ioEx);
			throw new CodeGenFailedException(errMsg, ioEx);
		} 
		finally {
			CodeGenUtil.closeQuietly(fileWriter);
		}

	}
	
	
	
	private String getDestFolder(CodeGenContext codeGenCtx) {
        String destFolderPath = 
    		CodeGenUtil.genDestFolderPath(
    				codeGenCtx.getMetaSrcDestLocation(),	        		
    				GEN_CLIENT_CONFIG_DIR);
        
        return destFolderPath;
	}

	private Writer getFileWriter(String destFolder) throws CodeGenFailedException {
		Writer fileWriter = null;  	      
  		try {			
 			fileWriter = CodeGenUtil.getFileWriter(destFolder, GLOBAL_CLIENT_CONFIG_FILE_NAME);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}
 		
 		 return fileWriter;
	}


	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(GEN_CLIENT_CONFIG_DIR) +  GLOBAL_CLIENT_CONFIG_FILE_NAME ;
		return filePath;

	}
	

}
