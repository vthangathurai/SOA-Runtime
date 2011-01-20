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
 * This is the metric value class to capture metric data in order to find the mean.
 * 
 * The value class aggregates the counts, the sum of values for a given MetricId.
 *
 * @author wdeng, ichernyshev
 */
public class AverageMetricValue extends BaseMetricValue {

	/**
	 * Registers the MetricComponent for this MetricValue.
	 */
	protected static final List<MetricComponentType> TYPES = MetricComponentType.createTypesArray(
		new MetricComponentType[] {
			new MetricComponentType("count", Long.class),
			new MetricComponentType("totalTime", Double.class)});

	/**
	 * Total counts.
	 */
	protected long m_count;
	/**
	 * Sum of times.
	 */
	protected double m_totalTime;

	/**
	 * 
	 * @param id  A MetricId.
	 */
	public AverageMetricValue(MetricId id) {
		this(id, TYPES);
	}

	/**
	 * @param id A MetricId
	 * @param count Total number of times being hit.
	 * @param totalTime Sum of process time
	 */
	public AverageMetricValue(MetricId id, int count, double totalTime) {
		this(id, TYPES, count, totalTime);
	}

	/**
	 * @param id A MetricId
	 * @param types A list of MetricComponentTypes.
	 */
	protected AverageMetricValue(MetricId id, List<MetricComponentType> types) {
		super(id, types);
	}

	/**
	 * @param id A MetricId
	 * @param types A list of MetricComponentTypes.
	 * @param count Total number of times being hit.
	 * @param totalTime Sum of process time
	 */
	protected AverageMetricValue(MetricId id, List<MetricComponentType> types,
		int count, double totalTime)
	{
		super(id, types);
		m_count = count;
		m_totalTime = totalTime;
	}

	@Override
	public void addOtherValue(MetricValue other, boolean isPositive) {
		checkUpdateable();

		AverageMetricValue other2 = (AverageMetricValue)other;
		if (isPositive) {
			m_count += other2.m_count;
			m_totalTime += other2.m_totalTime;
		} else {
			m_count -= other2.m_count;
			m_totalTime -= other2.m_totalTime;
		}
	}

	@Override
	public MetricComponentValue[] getValues() {
		return new MetricComponentValue[] {
			new MetricComponentValue("count", Long.valueOf(m_count)),
			new MetricComponentValue("totalTime", new Double(m_totalTime)),
		};
	}

	/**
	 * 
	 * @return The average time.
	 */
	public final double getAverageValue() {
		return m_totalTime / m_count;
	}

	/**
	 * 
	 * @return The total hit counts.
	 */
	public final long getCount() {
		return m_count;
	}

	/**
	 * 
	 * @return The total time from all the hits.
	 */
	public final double getTotalTime() {
		return m_totalTime;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue)
	 */
	@Override
	public void update(MetricValue newValue) {
		checkUpdateable();
		checkInstance(newValue, AverageMetricValue.class);
		AverageMetricValue amv = (AverageMetricValue)newValue;
		m_count += amv.m_count;
		m_totalTime += amv.m_totalTime;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(int)
	 */
	@Override
	public void update(int value) {
		checkUpdateable();
		m_count++;
		m_totalTime += value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(long)
	 */
	@Override
	public void update(long value) {
		checkUpdateable();
		m_count++;
		m_totalTime += value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(float)
	 */
	@Override
	public void update(float value) {
		checkUpdateable();
		m_count++;
		m_totalTime += value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue#update(double)
	 */
	@Override
	public void update(double value) {
		checkUpdateable();
		m_count++;
		m_totalTime += value;
	}
}
