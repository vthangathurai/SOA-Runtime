/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;

import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueFactory;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * MetricDef is a metric definition.  It contains 
 * 
 * <UL>
 * <LI>  A MetricId(metric name, service admin name, and operation name): the 
 * 			unique identifier for the metric.
 * <LI>  A Monitoring Level. The level is used to control whether the metric will
 * 			be logged at runtime based on runtime monitoring level setting.  
 * <LI>  A MetricCategory.  The category groups the metric into logically related groups.
 * <LI>  A MetricValueFactory.  The factory to create Initial MetricValue for this metric. 
 * 			The framework provides a SimpleMetricValueFactory. This factory calls the given 
 * 			MetricValue's single argument constructor to create a new value where the argument 
 * 			is a MetricId. 
 * </UL>	
 * @author wdeng, ichernyshev
 */
public class MetricDef {
	/**
	 * Indicator for applying to all operations.
	 */
	public final static String OP_APPLY_TO_ALL = "*";
	/**
	 * Indicator for don't care operations.
	 */
	public final static String OP_DONT_CARE = null;
	
	/**
	 * Apply to all services.
	 */
	public final static QName SVC_APPLY_TO_ALL = new QName("", "*", "");

	private final String m_metricName;
	private final QName m_serviceName;
	private final String m_operationName;
	private final MonitoringLevel m_level;
	private final MetricCategory m_category;
	private final MetricValueFactory m_valueFactory;
	private final String m_description;
	private int m_hashCode;

	/**
	 * @param metricName The name of a metric
	 * @param serviceName The name of a service with whom the metric is defined.
	 * @param operationName The name of an operation with whom the metric is defined.
	 * @param level The log level of a metric.
	 * @param category The category of a metric.
	 * @param valueClass The value class for the metric, this class is used to update the value.
	 * 
	 */
	public MetricDef(String metricName, QName serviceName, String operationName,
		MonitoringLevel level, MetricCategory category, Class<? extends MetricValue> valueClass)
	{
		this(metricName, serviceName, operationName, level, category,
			new SimpleMetricValueFactory(valueClass), null);
	}

	/**
	 * @param metricName The name of a metric
	 * @param serviceName The name of a service with whom the metric is defined.
	 * @param operationName The name of an operation with whom the metric is defined.
	 * @param level The log level of a metric.
	 * @param category The category of a metric.
	 * @param valueFactory The factory class to create MetricValue objects.
	 */
	public MetricDef(String metricName, QName serviceName, String operationName,
		MonitoringLevel level, MetricCategory category, MetricValueFactory valueFactory)
	{
		this(metricName, serviceName, operationName, level, category, valueFactory, null);
	}

	/**
	 * @param metricName The name of a metric
	 * @param serviceName The name of a service with whom the metric is defined.
	 * @param operationName The name of an operation with whom the metric is defined.
	 * @param level The log level of a metric.
	 * @param category The category of a metric.
	 * @param valueClass The value class for the metric, this class is used to update the value.
	 * @param description The description of a metric.
	 */
	public MetricDef(String metricName, QName serviceName, String operationName,
		MonitoringLevel level, MetricCategory category, Class<? extends MetricValue> valueClass,
		String description)
	{
		this(metricName, serviceName, operationName, level, category,
			new SimpleMetricValueFactory(valueClass), description);
	}

	/**
	 * @param metricName The name of a metric
	 * @param serviceName The name of a service with whom the metric is defined.
	 * @param operationName The name of an operation with whom the metric is defined.
	 * @param level The log level of a metric.
	 * @param category The category of a metric.
	 * @param valueFactory The factory class to create MetricValue objects.
	 * @param description The description of a metric.
	 */
	public MetricDef(String metricName, QName serviceName, String operationName,
		MonitoringLevel level, MetricCategory category, MetricValueFactory valueFactory,
		String description)
	{
		if (metricName == null || serviceName == null || level == null ||
			category == null || valueFactory == null)
		{
			throw new NullPointerException();
		}

		m_metricName = metricName;
		m_serviceName = serviceName;
		m_operationName = operationName;
	 	m_level = level;
	 	m_category = category;
	 	m_valueFactory = valueFactory;
	 	m_description = description;
	}

	/**
	 * @return the metric name 
	 * 
	 */
	public final String getMetricName() {
		return m_metricName;
	}

	/**
	 * @return the admin name of the service the metric is defined for.
	 * 
	 */
	public final QName getServiceName() {
		return m_serviceName;
	}

	/**
	 * @return the operation name the metric is defined for.
	 * 
	 */
	public final String getOperationName() {
		return m_operationName;
	}

	/**
	 * @return the category of the metric in the definition.
	 * 
	 */
	public final MetricCategory getCategory() {
		return m_category;
	}

	/**
	 * @return the monitoring level of the metric in the definition.
	 * 
	 */
	public final MonitoringLevel getLevel() {
		return m_level;
	}

	/**
	 * @return the MetricValueFactory of the metric in the definition.
	 * 
	 */
	public final MetricValueFactory getValueFactory() {
		return m_valueFactory;
	}

	/**
	 * @return the description of the MetricDef.
	 * 
	 */
	public final String getDescription() {
		return m_description;
	}

	@Override
	public final int hashCode() {
		if (m_hashCode == 0) {
			m_hashCode = m_metricName.hashCode() ^ m_serviceName.hashCode();
		}
		return m_hashCode;
	}

	@Override
	public final boolean equals(Object other) {
		if (null == other || !(other instanceof MetricDef)) {
			return false;
		}

		MetricDef otherDef = (MetricDef)other;
		return m_level == otherDef.m_level &&
			m_category == otherDef.m_category &&
			isSameId(otherDef) &&
			m_valueFactory.isSame(otherDef.m_valueFactory);
	}

	/**
	 * Returns true if the caller has the same metric id as the <code>other</code> MetricDef instance.
	 * 
	 * @param other another MetricDef
	 * @return True if both MetricDefs have the same id.
	 */
	public final boolean isSameId(MetricDef other) {
		if (null == other) {
			return false;
		}

		return m_metricName.equals(other.m_metricName) &&
			m_serviceName.equals(other.m_serviceName) &&
			BindingUtils.sameObject(m_operationName, other.m_operationName);
	}

	@Override
	public final String toString() {
		return "MetricDef: name=" + m_metricName + ", svc=" + m_serviceName +
			", op=" + m_operationName + ", level=" + m_level + ", cat=" + m_category;
	}

	private static class SimpleMetricValueFactory implements MetricValueFactory {
		private final static Class[] METRIC_VALUE_CONSTR_TYPE = new Class[] { MetricId.class };
		private final Constructor<? extends MetricValue> m_valueConstructor;

		public SimpleMetricValueFactory(Class<? extends MetricValue> valueClass) {
			if (valueClass == null) {
				throw new NullPointerException();
			}

			if (!MetricValue.class.isAssignableFrom(valueClass)) {
				throw new IllegalArgumentException(valueClass.getName() +
					" is not a valid class for MetricValue");
			}

			try {
				m_valueConstructor = valueClass.getConstructor(METRIC_VALUE_CONSTR_TYPE);
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(valueClass.getName() +
					" does not have constructor taking MetricId: " + e.toString(), e);
			} catch (SecurityException e) {
				throw new IllegalArgumentException("Unable to access constructor for " +
					valueClass.getName() + " due to: " + e.toString(), e);
			}
		}

		public boolean isSame(MetricValueFactory other) {
			if (!(other instanceof SimpleMetricValueFactory)) {
				return false;
			}

			SimpleMetricValueFactory other2 = (SimpleMetricValueFactory)other;
			return other2.m_valueConstructor.getDeclaringClass() == m_valueConstructor.getDeclaringClass();
		}

		public MetricValue create(MetricId id) {
			if (id == null) {
				throw new NullPointerException();
			}

			try {
				return m_valueConstructor.newInstance(new Object[] {id});
			} catch (Exception e) {
				throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_CANNOT_INSTANTIATE_VALUE,
						ErrorConstants.ERRORDOMAIN, new Object[] {id.toString(), e.toString()}), e);
			}
		}
	}
}
