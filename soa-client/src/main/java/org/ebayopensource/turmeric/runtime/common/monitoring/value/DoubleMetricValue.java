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
 * Base class for double precision floating point number base metric value. 
 * It provides a in-memory storage of metric value as a Double.
 * 
 * @author wdeng
 *
 */
public abstract class DoubleMetricValue extends BaseMetricValue {

	private static final List<MetricComponentType> TYPES =
		MetricComponentType.createTypesArray(Double.class, "value");

	/**
	 * The accumulated <code>double</code> value.
	 */
	protected double m_value;

	/**
	 * @param id A MetricId.
	 */
	public DoubleMetricValue(MetricId id) {
		super(id, TYPES);
	}

	/**
	 * @param id A MetricId.
	 * @param initValue The initial value in <code>double</code>.
	 */
	public DoubleMetricValue(MetricId id, double initValue) {
		super(id, TYPES);
		m_value = initValue;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#getValues()
	 */
	@Override
	public MetricComponentValue[] getValues() {
		return new MetricComponentValue[] {new MetricComponentValue("value", new Double(m_value))};
	}

    /**
     * @return The current accumulated value in <code>double</code>.
     */
    public double getDoubleValue() {
    	return m_value;
    }
}
