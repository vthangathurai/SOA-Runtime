/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;


/**
 * Base class for integer base metric value. It provides a in-memory 
 * storage of metric value as an Integer for a given MetricId.
 * 
 * @author wdeng
 *
 */
public abstract class IntMetricValue extends BaseMetricValue {

	private static final List<MetricComponentType> TYPES =
		MetricComponentType.createTypesArray(Integer.class, "value");

	/**
	 * The accumulated value in <code>int</code>.
	 */
	protected int m_value;

	/**
	 * @param id A MetricId.
	 */
	public IntMetricValue(MetricId id) {
		super(id, TYPES);
	}

	/**
	 * @param id A MetricId.
	 * @param initValue The initial value in <code>int</code>.
	 */
	public IntMetricValue(MetricId id, int initValue) {
		super(id, TYPES);
		m_value = initValue;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#getValues()
	 */
	@Override
	public MetricComponentValue[] getValues() {
		return new MetricComponentValue[] {new MetricComponentValue("value", Integer.valueOf(m_value))};
	}

	/**
	 * @return The current accumulated value in <code>int</code>.
	 */
	public int getIntValue() {
		return m_value;
	}
}
