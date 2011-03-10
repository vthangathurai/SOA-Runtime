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
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.common.config.ClientConfig;
import org.ebayopensource.turmeric.common.config.ClientConfigList;
import org.ebayopensource.turmeric.common.config.ClientGroupConfig;
import org.ebayopensource.turmeric.common.config.InvocationOptionConfig;
import org.ebayopensource.turmeric.common.config.PreferredTransportConfig;
import org.ebayopensource.turmeric.runtime.codegen.common.ConfigType;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConfigUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

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

	private static final String CLIENT_CONFIG_TEMPLATE = 
		"org/ebayopensource/turmeric/tools/codegen/template/clientconfig.tpt";

	private static final String CONFIG_GROUP_NAME = "@@ClientConfigGroupName@@";
	private static final String CONFIG_GROUP_NAME_ATTR = "group=\"@@ClientConfigGroupName@@\"";
	private static final String CONFIG_SERVICE_NAME = "@@ServiceName@@";
	private static final String CONFIG_SERVICE_NAME_ATTR = "service-name=\"@@ServiceName@@\"";
	private static final String PREFERRED_TRANSPORT_NAME = "@@PreferredTransport@@";
	private static final String PREFERRED_TRANSPORT_NAME_ATTR = "name=\"@@PreferredTransport@@\"";

	private static final String SERVICE_INT_NAME = "@@ServiceInterfaceClassName@@";
	private static final String SERVICE_INT_NAME_NODE = "<service-interface-class-name>@@ServiceInterfaceClassName@@</service-interface-class-name>";
	private static final String WSDL_LOCATION = "@@WSDLLocation@@";
	private static final String WSDL_LOCATION_NODE = "<wsdl-location>@@WSDLLocation@@</wsdl-location>";
	private static final String SERVICE_LOCATION = "@@ServiceLocation@@";
	private static final String SERVICE_LOCATION_NODE = "<service-location>@@ServiceLocation@@</service-location>";

	private static final String REQ_DATA_BINDING = "@@RequestDataBinding@@";
	private static final String REQ_DATA_BINDING_NODE = "<request-data-binding>@@RequestDataBinding@@</request-data-binding>";
	private static final String RESP_DATA_BINDING = "@@ResponseDataBinding@@";
	private static final String RESP_DATA_BINDING_NODE = "<response-data-binding>@@ResponseDataBinding@@</response-data-binding>";
	private static final String CONSUMER_ID = "@@ConsumerId@@";
	private static final String CONSUMER_ID_NODE = "<consumer-id>@@ConsumerId@@</consumer-id>";

	private static final String INVOCATION_USE_CASE = "@@InvocationUseCase@@";
	private static final String INVOCATION_USE_CASE_NODE = "<invocation-use-case>@@InvocationUseCase@@</invocation-use-case>";

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

		Writer fileWriter = null;
		try {
			if(CodeGenUtil.isEmptyString(codeGenCtx.getClientName()))
					codeGenCtx.getInputOptions().setClientName(codeGenCtx.getServiceAdminName());
	        String destFolderPath =
	        		CodeGenUtil.genDestFolderPath(
	        		codeGenCtx.getMetaSrcDestLocation(),
	        		getSuffixPath(codeGenCtx.getClientName()));
	        if(! CodeGenUtil.isEmptyString(codeGenCtx.getInputOptions().getEnvironment())){
	        	destFolderPath = destFolderPath
						+ codeGenCtx.getInputOptions().getEnvironment()
						+ File.separator + codeGenCtx.getServiceAdminName()
						+ File.separator;
				destFolderPath = CodeGenUtil.toOSFilePath(destFolderPath);
	        }

	        String contents = getUpdatedClientConfigTemplate(codeGenCtx, clientCfgList);

	        fileWriter = CodeGenUtil.getFileWriter(destFolderPath, CLIENT_CONFIG_FILE_NAME);
			fileWriter.write(contents );

			getLogger().log(Level.INFO, "Successfully generated " + CLIENT_CONFIG_FILE_NAME + " under " + destFolderPath );

		} catch (Exception ex) {
			getLogger().log(Level.SEVERE, "Error " + ex.toString());
			throw new CodeGenFailedException("Failed to generate Client Config xml file" , ex);
		}finally{
			CodeGenUtil.flushAndCloseQuietly(fileWriter);
		}

	}

	private String getUpdatedClientConfigTemplate(CodeGenContext codeGenCtx,
			ClientConfigList clientCfgList) throws CodeGenFailedException  {
			try {
				if(clientCfgList == null || clientCfgList.getClientConfig().size() == 0 ){
					throw new CodeGenFailedException("The content of 'ClientConfigList' is empty.");
				}

				String contents = CodeGenUtil.getTemplateContent(CLIENT_CONFIG_TEMPLATE);
				ClientConfig clientConfig = clientCfgList.getClientConfig().get(0);

				contents = CodeGenConfigUtil.replaceTemplate(contents, CONFIG_GROUP_NAME, clientConfig.getGroup(), CONFIG_GROUP_NAME_ATTR);

				contents = CodeGenConfigUtil.replaceTemplate(contents, CONFIG_SERVICE_NAME, clientConfig.getServiceName(), CONFIG_SERVICE_NAME_ATTR);
				contents = CodeGenConfigUtil.replaceTemplate(contents, SERVICE_INT_NAME, clientConfig.getServiceInterfaceClassName(), SERVICE_INT_NAME_NODE);
				contents = CodeGenConfigUtil.replaceTemplate(contents, WSDL_LOCATION, clientConfig.getWsdlLocation(), WSDL_LOCATION_NODE);
				contents = CodeGenConfigUtil.replaceTemplate(contents, SERVICE_LOCATION, clientConfig.getServiceLocation(), SERVICE_LOCATION_NODE);
				
				ClientGroupConfig clientInstanceConfig = clientConfig.getClientInstanceConfig();
				InvocationOptionConfig invocationOptions = null;
				PreferredTransportConfig preferredTransport = null;

				if(clientInstanceConfig != null){
					invocationOptions = clientConfig.getClientInstanceConfig().getInvocationOptions();
				}
				if(invocationOptions != null){
					preferredTransport = invocationOptions.getPreferredTransport();

					contents = CodeGenConfigUtil.replaceTemplate(contents, REQ_DATA_BINDING, invocationOptions.getRequestDataBinding(), REQ_DATA_BINDING_NODE);
					contents = CodeGenConfigUtil.replaceTemplate(contents, RESP_DATA_BINDING, invocationOptions.getResponseDataBinding(), RESP_DATA_BINDING_NODE);
					contents = CodeGenConfigUtil.replaceTemplate(contents, CONSUMER_ID, invocationOptions.getConsumerId(), CONSUMER_ID_NODE);
					contents = CodeGenConfigUtil.replaceTemplate(contents, INVOCATION_USE_CASE, invocationOptions.getInvocationUseCase(), INVOCATION_USE_CASE_NODE);
				}

				String transportName = null;
				if(preferredTransport != null){
					transportName = preferredTransport.getName();
				}
				contents = CodeGenConfigUtil.replaceTemplate(contents, PREFERRED_TRANSPORT_NAME, transportName, PREFERRED_TRANSPORT_NAME_ATTR );
				return contents;
			} catch (Throwable e) {
				throw new CodeGenFailedException("Failed in retriveing the client config template " + CLIENT_CONFIG_TEMPLATE + e.getMessage(), e);
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
