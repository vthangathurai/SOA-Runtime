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

import org.ebayopensource.turmeric.common.config.FeatureIndicatorConfig;
import org.ebayopensource.turmeric.common.config.NameValue;
import org.ebayopensource.turmeric.common.config.ProtocolProcessorConfig;
import org.ebayopensource.turmeric.common.config.ServiceConfig;
import org.ebayopensource.turmeric.common.config.ServiceGroupConfig;
import org.ebayopensource.turmeric.runtime.codegen.common.ConfigType;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.protocolprocessor.soap.ServerSOAPProtocolProcessor;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConfigUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

/**
 * Service configuration generator.
 *
 * Generates either default server configuration or configuration
 * spcified by the user as input in the xml file.
 *
 *
 * @author rmandapati
 */
public class ServiceConfigGenerator  implements SourceGenerator {

	private static final String GEN_SERVICE_CONFIG_DIR = "META-INF/soa/services/config";
	private static final String SERVICE_CONFIG_FILE_NAME = "ServiceConfig.xml";
	private static final String SOAP_11_VERSION = "1.1";
	private static final String SOAP_12_VERSION = "1.2";

	private static final String SERVICE_CONFIG_TEMPLATE = 
		"org/ebayopensource/turmeric/tools/codegen/template/serviceconfig.tpt";

	private static final String SERVICE_GROUP_NAME = "@@ServiceConfigGroupName@@";
	private static final String SERVICE_GROUP_NAME_ATTR = "group=\"@@ServiceConfigGroupName@@\"";
	private static final String SERVICE_INT_NAME = "@@ServiceInterfaceClassName@@";
	private static final String SERVICE_INT_NAME_NODE = "<service-interface-class-name>@@ServiceInterfaceClassName@@</service-interface-class-name>";
	private static final String SERVICE_IMPL_NAME = "@@ServiceImplClassName@@";
	private static final String SERVICE_IMPL_NAME_NODE = "<service-impl-class-name>@@ServiceImplClassName@@</service-impl-class-name>";

	private static Logger s_logger = LogManager.getInstance(ServiceConfigGenerator.class);



	private static ServiceConfigGenerator s_serviceConfigGenerator  =
		new ServiceConfigGenerator();


	private Logger getLogger() {
		return s_logger;
	}


	private ServiceConfigGenerator() {}


	public static ServiceConfigGenerator getInstance() {
		return s_serviceConfigGenerator;
	}


	public boolean continueOnError() {
		return false;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {

		ServiceConfig serviceConfig = null;

		InputOptions inputOptions = codeGenCtx.getInputOptions();
		ServiceCodeGenDefType svcCodeGenDef = inputOptions.getSvcCodeGenDefType();
		// If config info secion is specified in XML
		if (svcCodeGenDef != null &&
			svcCodeGenDef.getConfigInfo() != null &&
			svcCodeGenDef.getConfigInfo().getServerCfg() != null) {
			ConfigType configType = svcCodeGenDef.getConfigInfo();
			serviceConfig = configType.getServerCfg();
		}
		else {
			serviceConfig =  createServiceConfig(codeGenCtx);
		}

		generateServiceConfigXml(codeGenCtx, serviceConfig);
	}




	private void generateServiceConfigXml(
			CodeGenContext codeGenCtx,
			ServiceConfig serviceConfig)  throws CodeGenFailedException {

		Writer fileWriter = null;
		try {
			InputOptions inputOptions = codeGenCtx.getInputOptions();
	        String destFolderPath =
	        		CodeGenUtil.genDestFolderPath(
	        		codeGenCtx.getMetaSrcDestLocation(),
	        		inputOptions.getServiceAdminName(),
	        		GEN_SERVICE_CONFIG_DIR);

	        String contents = getUpdatedServiceConfigTemplate(codeGenCtx, serviceConfig);

	        fileWriter = CodeGenUtil.getFileWriter(destFolderPath, SERVICE_CONFIG_FILE_NAME);
			fileWriter.write(contents );

	        getLogger().log(Level.INFO,
	        		"Successfully generated  " + SERVICE_CONFIG_FILE_NAME);
		}
		catch (Exception ex) {
			String errMsg = "Failed to generate " + SERVICE_CONFIG_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ex);
			throw new CodeGenFailedException(errMsg , ex);
		}finally{
			CodeGenUtil.flushAndCloseQuietly(fileWriter);
		}


	}

	private String getUpdatedServiceConfigTemplate(CodeGenContext codeGenCtx,
			ServiceConfig serviceConfig) throws CodeGenFailedException  {
		try {
			if(serviceConfig == null ){
				throw new CodeGenFailedException("The content of 'serviceConfig' is empty.");
			}

			String contents = CodeGenUtil.getTemplateContent(SERVICE_CONFIG_TEMPLATE);

			contents = CodeGenConfigUtil.replaceTemplate(contents, SERVICE_GROUP_NAME, serviceConfig.getGroup(), SERVICE_GROUP_NAME_ATTR);
			contents = CodeGenConfigUtil.replaceTemplate(contents, SERVICE_INT_NAME, serviceConfig.getServiceInterfaceClassName(), SERVICE_INT_NAME_NODE);
			contents = CodeGenConfigUtil.replaceTemplate(contents, SERVICE_IMPL_NAME, serviceConfig.getServiceImplClassName(), SERVICE_IMPL_NAME_NODE);

			return contents;
		} catch (Throwable e) {
			throw new CodeGenFailedException("Failed in retriveing the service config template " + SERVICE_CONFIG_TEMPLATE + e.getMessage());
		}
	}

	private ServiceConfig createServiceConfig(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {

		ServiceConfig serviceConfig = new ServiceConfig();

		serviceConfig.setGroup(codeGenCtx.getInputOptions().getServerCfgGroupName());
		//This is commented coz runtime would look into smp file for servicename.
		//serviceConfig.setServiceName(codeGenCtx.getServiceQName().toString());

		String svcInterfaceName =
			CodeGenUtil.toQualifiedClassName(codeGenCtx.getServiceInterfaceClassName());
		serviceConfig.setServiceInterfaceClassName(svcInterfaceName);
		serviceConfig.setServiceImplClassName(codeGenCtx.getServiceImplClassName());

		ServiceGroupConfig serviceInstanceConfig = new ServiceGroupConfig();

		ProtocolProcessorConfig soap11ProtocolProcessor = new ProtocolProcessorConfig();
		soap11ProtocolProcessor.setName(SOAConstants.MSG_PROTOCOL_SOAP_11);
		soap11ProtocolProcessor.setVersion(SOAP_11_VERSION);
		FeatureIndicatorConfig soap11Indicator = new FeatureIndicatorConfig();
		NameValue soap11NameValue = new NameValue();
		soap11NameValue.setName(org.ebayopensource.turmeric.runtime.common.types.SOAHeaders.MESSAGE_PROTOCOL);
		soap11NameValue.setValue(SOAConstants.MSG_PROTOCOL_SOAP_11);
		soap11Indicator.setTransportHeader(soap11NameValue);
		soap11ProtocolProcessor.setIndicator(soap11Indicator);
		soap11ProtocolProcessor.setClassName(ServerSOAPProtocolProcessor.class.getName());
		serviceInstanceConfig.getProtocolProcessor().add(soap11ProtocolProcessor);

		ProtocolProcessorConfig soap12ProtocolProcessor = new ProtocolProcessorConfig();
		soap12ProtocolProcessor.setName(SOAConstants.MSG_PROTOCOL_SOAP_12);
		soap12ProtocolProcessor.setVersion(SOAP_12_VERSION);
		FeatureIndicatorConfig soap12Indicator = new FeatureIndicatorConfig();
		NameValue soap12NameValue = new NameValue();
		soap12NameValue.setName(org.ebayopensource.turmeric.runtime.common.types.SOAHeaders.MESSAGE_PROTOCOL);
		soap12NameValue.setValue(SOAConstants.MSG_PROTOCOL_SOAP_12);
		soap12Indicator.setTransportHeader(soap12NameValue);
		soap12ProtocolProcessor.setIndicator(soap12Indicator);
		soap12ProtocolProcessor.setClassName(ServerSOAPProtocolProcessor.class.getName());
		serviceInstanceConfig.getProtocolProcessor().add(soap12ProtocolProcessor);

		serviceConfig.setServiceInstanceConfig(serviceInstanceConfig);

		return serviceConfig;
	}


	public String getFilePath(String serviceAdminName, String interfaceName) {
		String filePath = CodeGenUtil.toOSFilePath(GEN_SERVICE_CONFIG_DIR)+ serviceAdminName + File.separatorChar + SERVICE_CONFIG_FILE_NAME ;
		return filePath;
	}





}
