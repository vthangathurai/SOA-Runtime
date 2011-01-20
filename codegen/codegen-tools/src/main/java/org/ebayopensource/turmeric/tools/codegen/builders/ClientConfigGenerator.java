/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.ConfigHelper;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

import org.ebayopensource.turmeric.common.config.ClientConfig;
import org.ebayopensource.turmeric.common.config.ClientConfigList;
import org.ebayopensource.turmeric.common.config.ClientGroupConfig;
import org.ebayopensource.turmeric.common.config.InvocationOptionConfig;
import org.ebayopensource.turmeric.common.config.PreferredTransportConfig;
import org.ebayopensource.turmeric.runtime.codegen.common.ConfigType;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;

/**
 * Service client configuration generator.
 *
 * Generates either default client configuration or configuration
 * spcified by the user as input in the xml file.
 *
 *
 * @author rmandapati
 */
public class ClientConfigGenerator implements SourceGenerator {

	private static Logger s_logger = LogManager.getInstance(ClientConfigGenerator.class);


	private Logger getLogger() {
		return s_logger;
	}



	private static final String GEN_CLIENT_CONFIG_DIR = "META-INF/soa/client/config";
	private static final String CLIENT_CONFIG_FILE_NAME = "ClientConfig.xml";


	private static ClientConfigGenerator s_clientConfigGenerator  =
		new ClientConfigGenerator();


	private ClientConfigGenerator() {}


	public static ClientConfigGenerator getInstance() {
		return s_clientConfigGenerator;
	}


	public boolean continueOnError() {
		return false;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {

		ClientConfigList clientCfgList = null;

		InputOptions inputOptions = codeGenCtx.getInputOptions();
		ServiceCodeGenDefType svcCodeGenDef = inputOptions.getSvcCodeGenDefType();
		// If config info is specified in XML
		if (svcCodeGenDef != null &&
			svcCodeGenDef.getConfigInfo() != null &&
			svcCodeGenDef.getConfigInfo().getClientCfg() != null) {
			ConfigType configType = svcCodeGenDef.getConfigInfo();
			clientCfgList = configType.getClientCfg();
		}
		else {
			clientCfgList =  createClientConfigList(codeGenCtx);
		}

		generateClientConfigXml(codeGenCtx, clientCfgList);
	}




	private void generateClientConfigXml(
			CodeGenContext codeGenCtx,
			ClientConfigList clientCfgList)  throws CodeGenFailedException {

		try {
			if(CodeGenUtil.isEmptyString(codeGenCtx.getClientName()))
					codeGenCtx.getInputOptions().setClientName(codeGenCtx.getServiceAdminName());
	        String destFolderPath =
	        		CodeGenUtil.genDestFolderPath(
	        		codeGenCtx.getMetaSrcDestLocation(),
	        		getSuffixPath(codeGenCtx.getClientName()));
	        if(! CodeGenUtil.isEmptyString(codeGenCtx.getInputOptions().getEnvironment()))
	        		{
	        destFolderPath = destFolderPath
						+ codeGenCtx.getInputOptions().getEnvironment()
						+ File.separator + codeGenCtx.getServiceAdminName()
						+ File.separator;
				destFolderPath = CodeGenUtil.toOSFilePath(destFolderPath);
	        		}
			ConfigHelper.generateClientConfigXml(
						clientCfgList,
						destFolderPath,
						CLIENT_CONFIG_FILE_NAME);

			getLogger().log(Level.INFO, "Successfully generated " + CLIENT_CONFIG_FILE_NAME + " under " + destFolderPath );

		} catch (Exception ex) {
			getLogger().log(Level.SEVERE, "Error " + ex.toString());
			throw new CodeGenFailedException("Failed to generate Client Config xml file" , ex);
		}
	}




	private ClientConfigList createClientConfigList(CodeGenContext codeGenCtx) {

		ClientConfigList clientCfgList = new ClientConfigList();

		ClientConfig clientCfg = createClientConfig(codeGenCtx);
		clientCfgList.getClientConfig().add(clientCfg);

		return clientCfgList;
	}


	private ClientConfig createClientConfig(CodeGenContext codeGenCtx) {

		InputOptions inputOptions = codeGenCtx.getInputOptions();

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setGroup(inputOptions.getClientCfgGroupName());
		//put serviceNametag in cc.xml only if pre 2.4 consumer
		if(codeGenCtx.getInputOptions().isServiceNameRequired())
		clientConfig.setServiceName(codeGenCtx.getServiceQName().toString());
		String svcInterfaceName =
			CodeGenUtil.toQualifiedClassName(codeGenCtx.getServiceInterfaceClassName());
		clientConfig.setServiceInterfaceClassName(svcInterfaceName);
		if (inputOptions.getSvcCodeGenDefType() == null) {
			clientConfig.setServiceLocation(inputOptions.getServiceLocation());
			clientConfig.setWsdlLocation(inputOptions.getWSDLLocation());
		} else {
			ServiceCodeGenDefType svcCodeGenDef = inputOptions.getSvcCodeGenDefType();
			clientConfig.setServiceLocation(svcCodeGenDef.getServiceInfo().getServiceLocation());
			clientConfig.setWsdlLocation(svcCodeGenDef.getServiceInfo().getWsdlLocation());
		}

		ClientGroupConfig clientGrpCfg = createClientGroupConfig(codeGenCtx);
		clientConfig.setClientInstanceConfig(clientGrpCfg);

		return clientConfig;
	}




	private ClientGroupConfig createClientGroupConfig(CodeGenContext codeGenCtx) {
		ClientGroupConfig clientGrpCfg = new ClientGroupConfig();

		InvocationOptionConfig invOptionsCfg = defaultInvocationOptions(codeGenCtx);
		clientGrpCfg.setInvocationOptions(invOptionsCfg);

		return clientGrpCfg;
	}


	private InvocationOptionConfig defaultInvocationOptions(CodeGenContext codeGenCtx) {

		String prefDataBinding = "XML";
		InputOptions inputOptions = codeGenCtx.getInputOptions();

		InvocationOptionConfig defaultInvOptions = new InvocationOptionConfig();
		//if consumerId is set then it should be used else invocation Use
		if(! CodeGenUtil.isEmptyString(codeGenCtx.getInputOptions().getConsumerId()))
		defaultInvOptions.setConsumerId(codeGenCtx.getInputOptions().getConsumerId());
		else
		defaultInvOptions.setInvocationUseCase(codeGenCtx.getServiceAdminName()+"Client");

		PreferredTransportConfig prefTransportCfg = new PreferredTransportConfig();
		if (inputOptions.getServiceLocation() != null &&
			inputOptions.getServiceLocation().startsWith("http")) {
			prefTransportCfg.setName(SOAConstants.TRANSPORT_HTTP_11);
		} else {
			prefTransportCfg.setName(SOAConstants.TRANSPORT_LOCAL);
		}

		//TransportOptionConfig transportOptCfg = new TransportOptionConfig();
		//transportOptCfg.setTransportName(CodeGenConstants.PREF_TRANSPORT_BINDING);
		//transportOptCfg.setNumRetries(Integer.valueOf(CodeGenConstants.NUM_OF_CONN_RETRIES));
		//prefTransportCfg.setOverrideOptions(transportOptCfg);

		defaultInvOptions.setPreferredTransport(prefTransportCfg);

		defaultInvOptions.setRequestDataBinding(prefDataBinding);
		defaultInvOptions.setResponseDataBinding(prefDataBinding);

		return defaultInvOptions;
	}


	private String getSuffixPath(String clientName) {
		if (CodeGenUtil.isEmptyString(clientName)) {
			return GEN_CLIENT_CONFIG_DIR +
				   File.separatorChar +
				   CodeGenConstants.DEFAULT_CLIENT_NAME;
		} else {
			return GEN_CLIENT_CONFIG_DIR + File.separatorChar + clientName;
		}
	}
	

	public String getFilePath(String serviceAdminName, String interfaceName){

		return CodeGenUtil.toOSFilePath(getSuffixPath(serviceAdminName)) + CLIENT_CONFIG_FILE_NAME;
	}

}
