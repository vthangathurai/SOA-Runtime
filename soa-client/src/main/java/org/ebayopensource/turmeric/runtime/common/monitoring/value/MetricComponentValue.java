/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

/**
 * MetricValue is a complex type of value that can contains multiple 
 * component values. For example,  MetricValue for calculating average time
 * contains a total count component value, and a total time component value.
 * MetricComponentValue object holds the name and value for a MetricComponentValue.
 *
 * @author ichernyshev
 */
public class MetricComponentValue {

	private final String m_name;
	private final Object m_value;

	/**
	 * @param name  The name of the component value.
	 * @param value  The value to be stored with the MetricComponenetValue object.
	 */
	public MetricComponentValue(String name, Object value) {
		m_name = name;
		m_value = value;
	}

	/**
	 * @return The component value name.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return The stored value.
	 */
	public Object getValue() {
		return m_value;
	}
}
