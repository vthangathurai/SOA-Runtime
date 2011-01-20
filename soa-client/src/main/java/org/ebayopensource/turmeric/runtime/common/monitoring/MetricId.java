/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;

/**
 * A metric has a unique ID represented by the MetricId object. A metric is uniquely 
 * identified by a metric name plus the service name and operation name, for example, 
 * the CallCount and TotalTime of RatingEngine service's getFee operation. Service name 
 * and operation name can take a wildcard character '*' to indicate that the metric is 
 * applicable to all services or operations and the operation name can take a null value 
 * to indicate don't care, which aggregates the metric at the service level. * 
 * 
 * MetricId is used as a key to find metric definition. A Metric
 * is uniquely identified by
 * <UL>
 * <LI> Metric Name: the name of a metric, for example,  SoaFwk.Time.Total
 * <LI> Service Admin Name: the unique name for a service
 * <LI> Service Operation Name: the name of the operation in the service request.
 * </UL>
 *     
 * 
 * @author wdeng
 */
/**
 * @author wdeng
 *
 */
public final class MetricId {
	private final String m_metricName;
	private final String m_adminName;
	/*
	 * @deprecated
	 */
	private final String m_serviceSubname;
	private final String m_operationName;
	private int m_hashCode;

	
	/**
	 * @param metricName The name of a metric
	 * @param adminName The admin name of a service with whom the metric is defined.
	 * @param operationName The name of an operation with whom the metric is defined.
	 */
	public MetricId(String metricName, String adminName, String operationName) {
		this(metricName, adminName, "", operationName);
	}

	/**
	 * 
	 * @param metricName The name of a metric
	 * @param adminName The admin name of a service with whom the metric is defined.
	 * @param subname deprecated.
	 * @param operationName The name of an operation with whom the metric is defined.
	 * @deprecated
	 */
	public MetricId(String metricName, String adminName, String subname, String operationName) {
		if (metricName == null || adminName == null) {
			throw new NullPointerException();
		}

		m_metricName = metricName;
		m_adminName = adminName;
		m_serviceSubname = subname;
		m_operationName = operationName;
	}

	/**
	 * @return the name of the metric.
	 * 
	 * 
	 */
	public String getMetricName() {
		return m_metricName;
	}

	/**
	 * @return the admin name of the service the metric is defined for.
	 * 
	 */
	public String getAdminName() {
		return m_adminName;
	}

	/**
	 * @return the operation name the metric is defined for.
	 */
	public String getOperationName() {
		return m_operationName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (m_hashCode == 0) {
			int r = m_metricName.hashCode() ^ m_adminName.hashCode();
			if (m_operationName != null) {
				r ^= m_operationName.hashCode();
			}
			m_hashCode = r;
		}

		return m_hashCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof MetricId)) {
			return false;
		}

		MetricId otherId = (MetricId)other;
		return m_metricName.equals(otherId.m_metricName) &&
			m_adminName.equals(otherId.m_adminName) &&
			equals(m_operationName, otherId.m_operationName);
	}

	private static boolean equals(Object o1, Object o2) {
		if (null == o1) {
			return null == o2;
		}
		return o1.equals(o2);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetricId: name=" + m_metricName
			+ ", svc=" + m_adminName
			+ ", op=" + (m_operationName == null ? "_null_" : m_operationName);
	}
}
