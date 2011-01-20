/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.HashMap;
import java.util.Map;

public class StorageProviderConfig {
	String m_name;
	String m_classname;
	Map<String, String> m_options;
	
	/**
	 * @return the classname
	 */
	public String getClassname() {
		return m_classname;
	}
	/**
	 * @param classname the classname to set
	 */
	public void setClassname(String classname) {
		m_classname = classname;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return m_name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		m_name = name;
	}
	/**
	 * @return the options
	 */
	public Map<String, String> getOptions() {
		if (m_options == null) {
			m_options = new HashMap<String, String>();
		}
			
		return m_options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(Map<String, String> options) {
		m_options = options;
	}
	
	public StorageProviderConfig copy() {
		StorageProviderConfig result = new StorageProviderConfig();
		result.m_classname = m_classname;
		result.m_name = m_name;
		result.m_options = new HashMap<String, String>(m_options);
		return result;
	}
	
	public void dump(StringBuffer sb) {
		sb.append(  "Storage provider: " + m_name + '\n');
		sb.append("  className="+m_classname + '\n');
		ConfigUtils.dumpStringMap(sb, m_options, "  ");
	}
}
