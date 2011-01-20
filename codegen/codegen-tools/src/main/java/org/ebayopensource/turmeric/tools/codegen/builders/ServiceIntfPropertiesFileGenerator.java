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
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.OutputStream;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.CodeGenInfoFinder;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * Service Interface Properties file generator.
 * Generates the service_int_project.properties file
 * 
 * @author arajmony
 *
 */
public class ServiceIntfPropertiesFileGenerator implements SourceGenerator {


	private static final String SERVICE_INTF_PROP_FILE_NAME = "service_intf_project.properties";

	private static Logger s_logger = LogManager
			.getInstance(ServiceIntfPropertiesFileGenerator.class);

	private static ServiceIntfPropertiesFileGenerator s_svcIntfPropFileGenerator = new ServiceIntfPropertiesFileGenerator();

	private ServiceIntfPropertiesFileGenerator() {
	}

	public static ServiceIntfPropertiesFileGenerator getInstance() {
		return s_svcIntfPropFileGenerator;
	}

	private Logger getLogger() {
		return s_logger;
	}

	public boolean continueOnError() {
		return false;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();

		Properties svcIntfProjProps = new Properties();

		if(!CodeGenUtil.isEmptyString(inputOptions.getServiceLocation()))
		  svcIntfProjProps.put(CodeGenConstants.SERVICE_LOCATION, inputOptions.getServiceLocation());
		
		if(inputOptions.getInputType() == InputOptions.InputType.WSDL)
			svcIntfProjProps.put(CodeGenConstants.INTERFACE_SOURCE_TYPE,InputOptions.InterfaceSourceType.WSDL.value());
		else if(inputOptions.getInputType() == InputOptions.InputType.INTERFACE)
			svcIntfProjProps.put(CodeGenConstants.INTERFACE_SOURCE_TYPE,InputOptions.InterfaceSourceType.INTERFACE.value());
			
		if(!CodeGenUtil.isEmptyString(inputOptions.getCommonTypesNS())){
			svcIntfProjProps.put(CodeGenConstants.CTNS, inputOptions.getCommonTypesNS());
		}
		

		generateSvcIntfProjPropFile(svcIntfProjProps, codeGenCtx);

	}

	/**
	 * 
	 * @param svcMetadataProps
	 * @param codeGenCtx
	 * @throws CodeGenFailedException
	 */
	private void generateSvcIntfProjPropFile(Properties svcIntfProjProps,
			CodeGenContext codeGenCtx) throws CodeGenFailedException {

		OutputStream outputStream = null;

		InputOptions inputOptions = codeGenCtx.getInputOptions();
		String projectRoot = inputOptions.getProjectRoot();

		try {
			outputStream = CodeGenUtil.getFileOutputStream(projectRoot,SERVICE_INTF_PROP_FILE_NAME);
			svcIntfProjProps.store(outputStream,"*** Generated file, any changes will be lost upon regeneration ***");

			getLogger().log( Level.INFO, "Successfully generated " + SERVICE_INTF_PROP_FILE_NAME + " under " + projectRoot);
			
			//calling to update the properties map
			try{
			   CodeGenInfoFinder.updateSvcIntfProjPropMap(inputOptions);
			}catch(Exception exception){
				String errMsg = "Could not update the properties Map for properties file :" + SERVICE_INTF_PROP_FILE_NAME;
				getLogger().log(Level.WARNING, errMsg, exception);
			}

			
		} catch (IOException ioEx) {
			String errMsg = "Failed to generate : "	+ SERVICE_INTF_PROP_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ioEx);
			throw new CodeGenFailedException(errMsg, ioEx);
		} finally {
			CodeGenUtil.closeQuietly(outputStream);
		}

	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.tools.codegen.SourceGenerator#getFilePath(java.lang.String, java.lang.String)
	 */
	public String getFilePath(String serviceAdminName, String interfaceName) {
		String filePath = SERVICE_INTF_PROP_FILE_NAME ;
		return filePath;
	}

}
