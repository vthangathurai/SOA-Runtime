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

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * BaseMetricValue is a base class for MetricValue.  Each metric has a MetricId,  
 * a list of MetricComponentTypes, and a flag to indicate whether the MetricValue 
 * is readonly.
 * 
 * @author wdeng, ichernyshev
 */
/**
 * @author wdeng
 *
 */
public abstract class BaseMetricValue implements MetricValue, Cloneable {

	private final MetricId m_metricId;
	private final List<MetricComponentType> m_types;
	private boolean m_isReadOnly;

	/**
	 * @param id A MetricId
	 * @param types A List of MetricComponentTypes for this Metric.
	 */
	protected BaseMetricValue(MetricId id, List<MetricComponentType> types) {
		if (id == null || types == null) {
			throw new NullPointerException();
		}

		if (types.size() == 0) {
			throw new IllegalArgumentException("Types array has to have at least one element");
		}

		m_metricId = id;
		m_types = types;
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#getMetricId()
	 */
	@Override
	public final MetricId getMetricId() {
		return m_metricId;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#isReadOnly()
	 */
	@Override
	public final boolean isReadOnly() {
		return m_isReadOnly;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#getAllComponentsTypes()
	 */
	@Override
	public final List<MetricComponentType> getAllComponentsTypes() {
		return m_types;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#deepCopy(boolean)
	 */
	@Override
	public MetricValue deepCopy(boolean isReadOnly) {
		try {
			BaseMetricValue newValue = (BaseMetricValue)clone();
			newValue.m_isReadOnly = isReadOnly; 
			return newValue;
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException("Clone is not supported for " + getClass().getName());
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#diff(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue, boolean)
	 */
	@Override
	public final MetricValue diff(MetricValue prevValue, boolean isReadOnly) {
		BaseMetricValue result = (BaseMetricValue)deepCopy(false);
		result.addOtherValue(prevValue, false);
		result.m_isReadOnly = isReadOnly;
		return result;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#add(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue, boolean)
	 */
	@Override
	public final MetricValue add(MetricValue other, boolean isReadOnly) {
		BaseMetricValue result = (BaseMetricValue)deepCopy(false);
		result.addOtherValue(other, true);
		result.m_isReadOnly = isReadOnly;
		return result;
	}

	/**
	 * Check whether an instance is assignable from the given Class.  Throws
	 * IllegalArgumentException when check fails.
	 * 
	 * @param inst An Object instance to be checked.
	 * @param clazz The Class object to be check against.
	 */
	protected static void checkInstance(Object inst, Class<?> clazz) {
		if (!clazz.isAssignableFrom(inst.getClass())) {
			throw new IllegalArgumentException(clazz.getName() + " is expected, while " +
				inst.getClass().getName() + " is passed in");
		}
	}

	/**
	 * Checks whether the MetricValue is readonly. If not, throws exception.
	 * 
	 */
	protected final void checkUpdateable() {
		if (m_isReadOnly) {
			throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_VALUE_READONLY,
					ErrorConstants.ERRORDOMAIN, new Object[] {m_metricId.toString(), this.getClass().getName()}));
		}
	}

	/**
	 * Returns an array of current MetricComponentValues.
	 */
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#getValues()
	 */
	@Override
	public abstract MetricComponentValue[] getValues();

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#update(org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue)
	 */
	@Override
	public abstract void update(MetricValue newValue);

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#update(int)
	 */
	@Override
	public abstract void update(int value);

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#update(long)
	 */
	@Override
	public abstract void update(long value);

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#update(float)
	 */
	@Override
	public abstract void update(float value);

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue#update(double)
	 */
	@Override
	public abstract void update(double value);

	/**
	 * Adds to or subtracts other value from this instance.
	 * @param other the other MetricValue object to be added or reduced.
	 * @param isPositive True if Addition should be performed.
	 */
	public abstract void addOtherValue(MetricValue other, boolean isPositive);
}
