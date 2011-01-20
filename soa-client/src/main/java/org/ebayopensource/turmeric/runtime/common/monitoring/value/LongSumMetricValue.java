/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricThreshold;

/**
 * A metric value that uses Long to aggregate a sum.
 * 
 * @author wdeng
 */
public class LongSumMetricValue extends LongMetricValue implements MetricThreshold {

	private static final int ZERO = 0;
	private static final int POSITIVE_INT = 1;
	private static final int NEGATIVE_INT = -1;
	
	/**
	 * @param id A MetricId.
	 */
	public LongSumMetricValue(MetricId id) {
		super(id);
	}

	/**
	 * @param id A MetricId
	 * @param initValue The initial value in <code>long</code>.
	 */
	public LongSumMetricValue(MetricId id, long initValue) {
		super(id, initValue);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#addOtherValue(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue, boolean)
	 */
	@Override
	public void addOtherValue(MetricValue other, boolean isPositive) {
		checkUpdateable();

		LongSumMetricValue other2 = (LongSumMetricValue)other;
		if (isPositive) {
			m_value += other2.m_value;
		} else {
			m_value -= other2.m_value;
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue)
	 */
	@Override
	public void update(MetricValue newValue) {
		checkUpdateable();
		checkInstance(newValue, LongSumMetricValue.class);
		LongSumMetricValue value = (LongSumMetricValue)newValue;
		m_value += value.m_value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(int)
	 */
	@Override
	public void update(int value) {
		checkUpdateable();
		m_value += value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(long)
	 */
	@Override
	public void update(long value) {
		checkUpdateable();
		m_value += value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(float)
	 */
	@Override
	public void update(float value) {
		checkUpdateable();
		m_value += value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(double)
	 */
	@Override
	public void update(double value) {
		checkUpdateable();
		m_value += value;
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.MetricThreshold#fromString(java.lang.String)
	 */
	@Override
	public MetricValue fromString(String value) {
		if (value == null) {
			return null;
		}
		long threshold = Long.valueOf(value).longValue();
		LongSumMetricValue newValue = (LongSumMetricValue) deepCopy(false);
		newValue.m_value = threshold;
		return newValue;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.MetricThreshold#compare(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue)
	 */
	@Override
	public int compare(MetricValue other) {
		// checkUpdateable();
		if (other instanceof LongSumMetricValue) {
			LongSumMetricValue value = (LongSumMetricValue) other;
			if (m_value == value.m_value) {
				return ZERO;
			}
			if (m_value > value.m_value) {
				return POSITIVE_INT;
			}
			if (m_value < value.m_value) {
				return NEGATIVE_INT;
			}
		}
		throw new ClassCastException();
	}	
}
