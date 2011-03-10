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

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;

import org.ebayopensource.turmeric.common.config.ClientConfigList;
import org.ebayopensource.turmeric.common.config.ObjectFactory;
import org.ebayopensource.turmeric.common.config.ServiceConfig;
import org.ebayopensource.turmeric.common.config.ServiceSecurityConfig;
import org.ebayopensource.turmeric.common.config.ServiceTypeMappingConfig;

public class SunJavaXmlBindingImpl implements JavaXmlBinder {
	
	//private static final String DEFAULT_SCHEMA_FILE_NAME = "schema1.xsd";
	private static final String DEFAULT_NS =  SOAConstants.SOA_CONFIG_NAMESPACE;
	private final static QName serviceQNAME = new QName(DEFAULT_NS, "service");
	private final static QName clientConfigListQNAME = new QName(DEFAULT_NS, "client-config-list");
	private final static QName serviceConfigQNAME = new QName(DEFAULT_NS, "service-config");
	private final static QName securityPolicyQNAME = new QName(DEFAULT_NS, "security-policy");
	
	public SunJavaXmlBindingImpl() {		
	}
	
	
	public void marshal(Object typeObj, Writer fileWriter) {
		JAXB.marshal(typeObj, fileWriter);
	}
	
	
	public <T> T  unmarshal(File xmlFile, Class<T> type) {
		return JAXB.unmarshal(xmlFile, type);
	}
	
    public <T> T  unmarshal(InputStream input, Class<T> type) {
        return JAXB.unmarshal(input, type);
    }
	
	public void generateSchema(
			CodeGenContext codeGenCtx, 
			Set<Class<?>> typesReferred,
			String schemaFileName,
		
			String destLocation) throws CodeGenFailedException {
	
		/*Commenting this method.Using internal classes
		 * Not permitted anymore.
		 */
	}
	
	
	
	public void generateTypeMappingsXml(
				ServiceTypeMappingConfig typeMappings,
				Writer fileWriter) throws Exception {		
	    // create an element for marshalling
	    JAXBElement<ServiceTypeMappingConfig> typeMappingsElement = 
	    	new JAXBElement<ServiceTypeMappingConfig>(serviceQNAME, ServiceTypeMappingConfig.class, null, typeMappings); 
	    marshal(typeMappingsElement, fileWriter);
	}
		
	
	public void generateClientConfigXml(
			ClientConfigList clientCfgList,
			Writer fileWriter) throws Exception {		
		// create an element for marshalling
	    JAXBElement<ClientConfigList> clientCfgListElement = 
	    	new JAXBElement<ClientConfigList>(clientConfigListQNAME, ClientConfigList.class, null, clientCfgList);
		
	    marshal(clientCfgListElement, fileWriter);
	}
	
	
	public void generateServiceConfigXml(
			ServiceConfig serviceConfig,
			Writer fileWriter) throws Exception {
		// create an element for marshalling
        JAXBElement<ServiceConfig> serviceConfigElement = 
        	new JAXBElement<ServiceConfig>(serviceConfigQNAME, ServiceConfig.class, null, serviceConfig);
		
	    marshal(serviceConfigElement, fileWriter);
	}
	
	
	public void generateSecurityPolicyXml(
			ServiceSecurityConfig securityPolicyConfig,
			Writer fileWriter)  throws CodeGenFailedException {
		
		// create an element for marshalling
        JAXBElement<ServiceSecurityConfig> serviceSecurityConfigElement = 
        	new JAXBElement<ServiceSecurityConfig>(securityPolicyQNAME, ServiceSecurityConfig.class, null, securityPolicyConfig);
		
	    marshal(serviceSecurityConfigElement, fileWriter);
	}

	
}
