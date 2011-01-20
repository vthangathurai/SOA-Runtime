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
import java.util.List;
import java.util.ListIterator;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.CodeGenInfoFinder;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConfigUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;



/**
 * Global Service configuration generator.
 *
 * Generates either default server configuration or configuration
 * spcified by the user as input in the xml file.
 *
 *
 * @author rmandapati
 */
public class GlobalServiceConfigGenerator  implements SourceGenerator {

	private static final String GLOBAL_SERVICE_CONFIG_TEMPLATE =
		"org/ebayopensource/turmeric/tools/codegen/template/globalserviceconfig.tpt";
	private static final String SERVICE_GROUP_CONFIG_TEMPLATE =
		"org/ebayopensource/turmeric/tools/codegen/template/servicegroupconfig.tpt";

	private static final String GEN_SERVICE_CONFIG_DIR = "META-INF/soa/services/config";
	private static final String GLOBAL_SERVICE_CONFIG_FILE_NAME = "GlobalServiceConfig.xml";

	private static final String SERVICE_CONFIG_GROUPS = "@@SERVICE_CONFIG_GROUPS@@";
	private static final String GROUP_NAME = "@@GROUP_NAME@@";
	private static final String SERVICE_LAYER_NAMES = "@@SERVICE_LAYER_NAMES@@";

	private static final String  DEFAULT_SERVICE_GROUP_NAME = "CommonSOAServiceGroup";
	private static final String newline = System.getProperty("line.separator");

	private static final String SOAP_11 = "@@SOAP11@@";
	private static final String SOAP_11_VERSION = "@@SOAP11_VERSION@@";
	private static final String MESSAGE_PROTOCOL = "@@MESSAGE_PROTOCOL@@";
	private static final String SOAP_12 = "@@SOAP12@@";
	private static final String SOAP_12_VERSION = "@@SOAP12_VERSION@@";


	private static Logger s_logger = LogManager.getInstance(GlobalServiceConfigGenerator.class);

	private static GlobalServiceConfigGenerator s_globalSvcCfgGenerator  =
		new GlobalServiceConfigGenerator();



	private Logger getLogger() {
		return s_logger;
	}




	private GlobalServiceConfigGenerator() {}


	public static GlobalServiceConfigGenerator getInstance() {
		return s_globalSvcCfgGenerator;
	}


	public boolean continueOnError() {
		return false;
	}


	public void generate(CodeGenContext codeGenCtx)  throws CodeGenFailedException  {
		// get Global Service Config template content
		String serviceCfgContent = null;
		try {
			serviceCfgContent = CodeGenUtil.getTemplateContent(GLOBAL_SERVICE_CONFIG_TEMPLATE);
		} catch (Exception ex) {
			throw new CodeGenFailedException(
						"Failed to read : " + GLOBAL_SERVICE_CONFIG_TEMPLATE, ex);
		}

		serviceCfgContent = CodeGenConfigUtil.addPackageDetailsToTemplateClasses(serviceCfgContent);

		InputOptions inputOptions = codeGenCtx.getInputOptions();
		String svcCfgGroupName = inputOptions.getServerCfgGroupName();
		if (CodeGenUtil.isEmptyString(svcCfgGroupName)) {
			svcCfgGroupName = DEFAULT_SERVICE_GROUP_NAME;
		}

		String groupCfgContent = null;
		try {
			groupCfgContent = CodeGenUtil.getTemplateContent(SERVICE_GROUP_CONFIG_TEMPLATE);
		} catch (Exception ex) {
			throw new CodeGenFailedException(
						"Failed to read : " + SERVICE_GROUP_CONFIG_TEMPLATE, ex);
		}
		groupCfgContent = CodeGenConfigUtil.addPackageDetailsToTemplateClasses(groupCfgContent);

		groupCfgContent = groupCfgContent.replaceAll(GROUP_NAME, svcCfgGroupName);
		groupCfgContent = groupCfgContent.replaceAll(SOAP_11, SOAConstants.MSG_PROTOCOL_SOAP_11);
		groupCfgContent = groupCfgContent.replaceAll(SOAP_12, SOAConstants.MSG_PROTOCOL_SOAP_12);
		groupCfgContent = groupCfgContent.replaceAll(SOAP_11_VERSION, "1.1");
		groupCfgContent = groupCfgContent.replaceAll(SOAP_12_VERSION, "1.2");
		groupCfgContent = groupCfgContent.replaceAll(MESSAGE_PROTOCOL, SOAHeaders.MESSAGE_PROTOCOL);


		serviceCfgContent = serviceCfgContent.replaceAll(SERVICE_CONFIG_GROUPS, groupCfgContent);

		//get contents for the service-layer-config tag
		String svcLayerFilePath = inputOptions.getSvcLayerFileLocation();
		List<String> layersList;
		String layerNameTags="";

		if(CodeGenUtil.isEmptyString(svcLayerFilePath)){
			layersList = CodeGenInfoFinder.getServiceLayersFromDefaultFile();
		}else {
			layersList = CodeGenInfoFinder.getServiceLayers(svcLayerFilePath);
		}

		ListIterator<String> listIter = layersList.listIterator();
		while(listIter.hasNext()){
			layerNameTags += "<layer-name>" + listIter.next() + "</layer-name>" + newline;
		}


		serviceCfgContent = serviceCfgContent.replaceAll(SERVICE_LAYER_NAMES, layerNameTags);

		// generate a new Global Service Config file
		createConfigFile(serviceCfgContent, codeGenCtx);
	}


	private void createConfigFile(String fileContent, CodeGenContext codeGenCtx)
			throws CodeGenFailedException {

		String destFolder = getDestFolder(codeGenCtx);
		Writer fileWriter = getFileWriter(destFolder);
		try {
			fileWriter.write(fileContent);
			getLogger().log(Level.INFO,
					"Successfully generated " + GLOBAL_SERVICE_CONFIG_FILE_NAME + " under " + destFolder);
		}
		catch (IOException ioEx) {
			String errMsg = "Failed to create : " + GLOBAL_SERVICE_CONFIG_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg);
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
        				GEN_SERVICE_CONFIG_DIR);

        return destFolderPath;
	}



	private Writer getFileWriter(String destFolder) throws CodeGenFailedException {
		Writer fileWriter = null;
  		try {
 			fileWriter = CodeGenUtil.getFileWriter(destFolder, GLOBAL_SERVICE_CONFIG_FILE_NAME);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}

 		 return fileWriter;
	}




	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(GEN_SERVICE_CONFIG_DIR) + GLOBAL_SERVICE_CONFIG_FILE_NAME ;
		return filePath;

	}



}
