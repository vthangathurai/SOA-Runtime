/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetadataPropertyConfigHolder extends BaseConfigHolder {
	private Map<String, String> m_properties = new HashMap<String, String>();
	
	public static final String KEY_LAYER = "service_layer";
	public static final String KEY_VERSION = "service_version";
	public static final String SERVICE_NAME = "service_name";
	public static final String ADMIN_NAME = "admin_name";
	public static final String SERVICE_NAMESPACE = "service_namespace";
	public static final String SMP_VERSION = "smp_version";
	
	public MetadataPropertyConfigHolder copy() {
		MetadataPropertyConfigHolder newCH = new MetadataPropertyConfigHolder();
		newCH.m_readOnly = false;
		newCH.m_properties = new HashMap<String, String>(m_properties);
		return newCH;
	}

	public Collection<String> getProperties() {
		return Collections.unmodifiableCollection(m_properties.values());
	}

	public String getLayer() {
		return m_properties.get(KEY_LAYER);
	}
	
	public String getVersion() {
		return m_properties.get(KEY_VERSION);
	}

	public String getServiceName() {
		return m_properties.get(SERVICE_NAME);
	}
	
	public String getServiceNamespace() {
		return m_properties.get(SERVICE_NAMESPACE);
	}
	
	public String getAdminName() {
		return m_properties.get(ADMIN_NAME);
	}
	
	public String getProperty(String key) {
		return m_properties.get(key);
	}
	
	/**	 
	 * Returns the smp_version property.
	 * 
	 * @return smp_version specified in the servicemeta_properties file, if
	 * 			exists and it has a valid value for smp_version property.
	 * 			-1 if the format is wrong
	 * 			0 if pre-2.4 config.
	 */
	@SuppressWarnings("boxing")
	public double getSmpVersion() {
		Double smpVersion = 1.0d;
		try {
			String smp = m_properties.get(SMP_VERSION);
			if (smp != null) {
				smpVersion = Double.valueOf(smp);
			}
		} 
		catch (NumberFormatException nfe) {	
			smpVersion = -1d; 
		}
		return smpVersion;
	}
	
	void setProperty(String propertyName, String value) {
		checkReadOnly();
		m_properties.put(propertyName, value);
	}

	/*
	 * Provide a user-readable description of the configuration into a
	 * StringBuffer. @param sb the StringBuffer into which to write the
	 * description
	 */
	public void dump(StringBuffer sb) {
		sb.append("========== Meta Data ==========\n" );
		ConfigUtils.dumpStringMap(sb, m_properties, "\t");
	}
}
