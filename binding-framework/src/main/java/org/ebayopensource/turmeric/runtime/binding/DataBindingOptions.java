/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import java.util.Map;

/**
 * Enumeration of config options supported by data binding.  
 * 
 * @author wdeng
 */
public enum DataBindingOptions {
	/**
	 * When specified as true, ignore socket output stream time out. By default it is true.
	 */
	IgnoreClientTimeout("ignoreClientTimeout", Boolean.TRUE),
	NoRoot("noRoot", Boolean.FALSE);
	
	private String m_optionName;
	private Object m_defaultValue;
	
	private DataBindingOptions(String optionName, Object defaultValue) {
		m_optionName = optionName;
		m_defaultValue = defaultValue;
	}
	/**
	 * Returns the name of an option.
	 * @return the name of an option.
	 */
	public String getOptionName() {
		return m_optionName;
	}
	/**
	 * Returns the default value for the option.
	 * @return the default value for the option.
	 */
	public Object getDefaultValue() {
		return m_defaultValue;
	}
	/**
	 * Gets the options from the given options map.
	 * 
	 * @param options A map of options. 
	 * @return true if the defaultValue is true.
	 */
	public  boolean getBoolOption(Map<String, String>options) {
		if (null == options) {
			return ((Boolean)getDefaultValue()).booleanValue();
		}
		String ignoreClientTimeout = options.get(getOptionName());
		if (ignoreClientTimeout != null && Boolean.parseBoolean(ignoreClientTimeout)) {
			return true;
		}
		return ((Boolean)getDefaultValue()).booleanValue();
	}
}
