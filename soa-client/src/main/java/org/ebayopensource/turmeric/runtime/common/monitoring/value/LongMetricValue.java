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
 * Base class for long integer base metric value. It provides a in-memory 
 * storage of metric value as a Long.
 * 
 * @author wdeng
 *
 */
public abstract class LongMetricValue extends BaseMetricValue {

	private static final List<MetricComponentType> TYPES =
		MetricComponentType.createTypesArray(Long.class, "value");

	/**
	 * The accumulated value in <code>long</code>.
	 */
	protected long m_value;

	/**
	 * @param id A MetricId.
	 */
	public LongMetricValue(MetricId id) {
		super(id, TYPES);
	}

	/**
	 * @param id A MetricId.
	 * @param initValue The initial value in <code>long</code>.
	 */
	public LongMetricValue(MetricId id, long initValue) {
		super(id, TYPES);
		m_value = initValue;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#getValues()
	 */
	@Override
	public MetricComponentValue[] getValues() {
		return new MetricComponentValue[] {new MetricComponentValue("value", Long.valueOf(m_value))};
	}

	/**
	 * @return The currently accumulated value in <code>long</code>.s
	 */
	public long getLongValue() {
		return m_value;
	}
}
