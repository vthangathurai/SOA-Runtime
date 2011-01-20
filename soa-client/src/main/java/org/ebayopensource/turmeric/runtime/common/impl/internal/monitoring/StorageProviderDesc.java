/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;


/**
 * @author wdeng
 */
public class StorageProviderDesc {
	private final String m_name;
	private final MetricsStorageProvider m_provider;
	private final Map<String, String> m_properties;
	private final String m_collectionLocation;
	
	public StorageProviderDesc(String name, MetricsStorageProvider provider, 
		Map<String, String> properties, String collectionLocation)
	{
		if (name == null || provider == null) {
			throw new NullPointerException();
		}

		m_name = name;
		m_provider = provider;
		m_properties = Collections.unmodifiableMap(
			properties == null ? new HashMap<String,String>() : properties);
		m_collectionLocation = collectionLocation;
	}
	

	public String getName() {
		return m_name;
	}
	
	public Map<String, String> getProperties() {
		return m_properties;
	}
	
	public String getProperty(String name) {
		return m_properties.get(name);
	}
	
	public MetricsStorageProvider getProvider() {
		return m_provider;
	}
	
	public String getCollectionLocation() {
		return m_collectionLocation;
	}
}
