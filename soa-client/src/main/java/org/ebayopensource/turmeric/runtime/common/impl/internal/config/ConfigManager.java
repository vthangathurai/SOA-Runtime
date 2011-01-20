/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.List;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public abstract class ConfigManager { 
	
	protected static final String s_schemaPath = "META-INF/soa/schema/";
	private static final String TYPEMAPPINGS_SCHEMA_PATH = s_schemaPath + "common/TypeMappings.xsd";
	
	protected static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	protected TypeMappingConfigHolder loadTypeMappingData(String typeMappingConfigFileName) throws ServiceCreationException {
		return loadTypeMappingData(typeMappingConfigFileName, false);
	}
	
	protected TypeMappingConfigHolder loadTypeMappingData(String typeMappingConfigFileName, boolean rawMode) throws ServiceCreationException {
		Document typeMappingDoc = ParseUtils.parseConfig(typeMappingConfigFileName, TYPEMAPPINGS_SCHEMA_PATH, rawMode, "service", SchemaValidationLevel.NONE);
		TypeMappingConfigHolder holder = new TypeMappingConfigHolder();
		if (rawMode) return holder;
		Element mapElement = typeMappingDoc.getDocumentElement();
		TypeMappingConfigMapper.map(typeMappingConfigFileName, mapElement, holder);
		return holder;
	}
	
	protected MetadataPropertyConfigHolder loadMetadataPropertyData(String metaDataConfigFileName, List<String> serviceLayers) throws ServiceCreationException {
		return 	loadMetadataPropertyData(metaDataConfigFileName, serviceLayers, false);
	}
	
	protected MetadataPropertyConfigHolder loadMetadataPropertyData(String metaDataConfigFileName, List<String> serviceLayers, boolean rawMode) throws ServiceCreationException {
		MetadataPropertyConfigHolder holder = new MetadataPropertyConfigHolder();
		MetadataPropertyConfigMapper.map(metaDataConfigFileName, rawMode, holder, serviceLayers);
		return holder;
	}
	
	// Retro-fits the name space to match Name space from the WSDL
	protected TypeMappingConfigHolder loadTypeMappingDataWithModifiedNS(
			String typeMappingConfigFileName, QName oldSvcQName,
			QName svcQName, Definition wsdlDefinition, boolean rawMode)
			throws ServiceCreationException {

		Document typeMappingDoc = ParseUtils.parseConfig(
				typeMappingConfigFileName, TYPEMAPPINGS_SCHEMA_PATH, rawMode,
				"service", SchemaValidationLevel.NONE);

		TypeMappingConfigHolder holder = new TypeMappingConfigHolder();
		if (rawMode)
			return holder;
		Element mapElement = typeMappingDoc.getDocumentElement();
		TypeMappingConfigMapper.map(typeMappingConfigFileName, mapElement,
				oldSvcQName, svcQName, wsdlDefinition, holder);
		return holder;
	}
}
