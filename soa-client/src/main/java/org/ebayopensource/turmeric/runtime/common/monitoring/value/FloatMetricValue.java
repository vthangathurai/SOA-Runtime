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
 * Base class for floating point number base metric value. It provides a in-memory 
 * storage of metric value as a Float.
 * 
 * @author wdeng
 *
 */
public abstract class FloatMetricValue extends BaseMetricValue {

	private static final List<MetricComponentType> TYPES =
		MetricComponentType.createTypesArray(Float.class, "value");

	/**
	 * Accumulated value in <code>float</code>.
	 */
	protected float m_value;

	/**
	 * @param id A MetricId.
	 */
	public FloatMetricValue(MetricId id) {
		super(id, TYPES);
	}

	/**
	 * @param id A MetricId
	 * @param initValue The intial value in <code>float</code>.
	 */
	public FloatMetricValue(MetricId id, float initValue) {
		super(id, TYPES);
		m_value = initValue;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#getValues()
	 */
	@Override
	public MetricComponentValue[] getValues() {
		return new MetricComponentValue[] {new MetricComponentValue("value", new Float(m_value))};
	}

	/**
	 * @return The current accumulated value in <code>float</code>.
	 */
	public float getFloatValue() {
		return m_value;
	}
}
