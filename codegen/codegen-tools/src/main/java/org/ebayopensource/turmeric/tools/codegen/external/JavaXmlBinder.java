/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external;

import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.util.Set;

import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;

import org.ebayopensource.turmeric.common.config.ClientConfigList;
import org.ebayopensource.turmeric.common.config.ServiceConfig;
import org.ebayopensource.turmeric.common.config.ServiceSecurityConfig;
import org.ebayopensource.turmeric.common.config.ServiceTypeMappingConfig;

public interface JavaXmlBinder {
	
	
	public void marshal(Object typeObj, Writer fileWriter);
	
    public <T> T unmarshal( InputStream input, Class<T> type );
	
	public <T> T unmarshal(File xmlFile, Class<T> type);
	
	
	public void generateSchema(			
			CodeGenContext codeGenCtx, 
			Set<Class<?>> typesReferred,
			String schemaFileName,
			String destLocation) throws CodeGenFailedException;

	public void generateTypeMappingsXml(
				ServiceTypeMappingConfig typeMappings,
				Writer fileWriter) throws Exception;
	
	
	public void generateClientConfigXml(
			ClientConfigList clientCfgList,
			Writer fileWriter) throws Exception;
	
	public void generateServiceConfigXml(
			ServiceConfig serviceConfig,
			Writer fileWriter) throws Exception;
	
	
	public void generateSecurityPolicyXml(
			ServiceSecurityConfig securityPolicyConfig,
			Writer fileWriter)  throws CodeGenFailedException;
	
	
}
