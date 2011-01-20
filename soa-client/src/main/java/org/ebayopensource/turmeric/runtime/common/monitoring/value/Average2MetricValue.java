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
 * This is the metric value class to capture metric data in order to find the mean and 
 * the deviation.
 * 
 * The value class aggregates the counts, the sum of values, and the square sum
 * of values for the metric.
 * 
 * @author wdeng, ichernyshev
 */
public class Average2MetricValue extends AverageMetricValue {

	/**
	 * Registers the MetricComponent for this MetricValue.
	 */
	protected static final List<MetricComponentType> TYPES2 = MetricComponentType.createTypesArray(
		AverageMetricValue.TYPES,
		new MetricComponentType[] {new MetricComponentType("totalTimeSquares", Double.class)});

	private double m_totalTimeSquares;

	/**
	 * @param id  A MetricId.
	 */
	public Average2MetricValue(MetricId id) {
		super(id, TYPES2);
	}

	/**
	 * @param id  A MetricId
	 * @param count Total number of hits.
	 * @param totalTime Sum of processing times for all the hits.
	 * @param totalTimeSquares Sum of time squares.
	 */
	public Average2MetricValue(MetricId id, int count, double totalTime, double totalTimeSquares) {
		super(id, TYPES2, count, totalTime);
		m_totalTimeSquares = totalTimeSquares;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.AverageMetricValue#addOtherValue(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue, boolean)
	 */
	@Override
	public void addOtherValue(MetricValue other, boolean isPositive) {
		super.addOtherValue(other, isPositive);

		Average2MetricValue other2 = (Average2MetricValue)other;
		if (isPositive) {
			m_totalTimeSquares += other2.m_totalTimeSquares;
		} else {
			m_totalTimeSquares -= other2.m_totalTimeSquares;
		}
	}

	/**
	 * @return the sum of time squares.
	 */
	public final double getTotalTimeSquares() {
		return m_totalTimeSquares;
	}

	@Override
	public MetricComponentValue[] getValues() {
		return new MetricComponentValue[] {
			new MetricComponentValue("count", Long.valueOf(m_count)),
			new MetricComponentValue("totalTime", new Double(m_totalTime)),
			new MetricComponentValue("totalTimeSquares", new Double(m_totalTimeSquares)),
		};
	}

	@Override
	public void update(MetricValue newValue) {
		super.update(newValue);

		checkInstance(newValue, Average2MetricValue.class);
		Average2MetricValue amv = (Average2MetricValue)newValue;
		m_totalTimeSquares += amv.m_totalTimeSquares;
	}

	@Override
	public void update(int value) {
		super.update(value);
		m_totalTimeSquares += (value * value);
	}

	@Override
	public void update(long value) {
		super.update(value);
		m_totalTimeSquares += (value * value);
	}

	@Override
	public void update(float value) {
		super.update(value);
		m_totalTimeSquares += (value * value);
	}

	@Override
	public void update(double value) {
		super.update(value);
		m_totalTimeSquares += (value * value);
	}
}
