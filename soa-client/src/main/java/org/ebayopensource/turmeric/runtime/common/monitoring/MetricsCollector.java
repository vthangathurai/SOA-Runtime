/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * MetricsCollector is the runtime manager of all the MetricValue collected. Each metric has a 
 * MetricValueAggregator stored and managed by the MetricsCollector.
 * 
 * User can obtain the MetricValueAggregator calling the getMetricValue methods, then update 
 * MetricValueAggregator with new metric data.
 * 
 * MetricsCollector also provides getAllMetricValues() to return a snapshot of metric values
 * currently collected by the runtime.
 * 
 * MetricsCollector has two singleton instances one for the client side runtime and the other for 
 * the server side runtime.
 * 
 * @author wdeng
 * @author ichernyshev
 */
public abstract class MetricsCollector {

	private static MetricsCollector s_clientInstance;
	private static MetricsCollector s_serverInstance;

	/**
	 * @return A MetricsCollector instance to be used on the client side.
	 */
	public static MetricsCollector getClientInstance() {
		if (s_clientInstance == null) {
			throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_CLIENT_NOT_INITIALIZED,
					ErrorConstants.ERRORDOMAIN));
		}

		return s_clientInstance;
	}

	/**
	 * @return A MetricsCollector instance to be used on the server side.
	 */
	public static MetricsCollector getServerInstance() {
		if (s_serverInstance == null) {
			throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_SERVER_NOT_INITIALIZED,
					ErrorConstants.ERRORDOMAIN));
		}

		return s_serverInstance;
	}

	/**
	 * Sets the MetricsCollector instance to be used on the client side.
	 * @param value A MetricsCollector instance to be used on the client side.
	 */
	protected static void setClientInstance(MetricsCollector value) {
		if (value == null) {
			throw new NullPointerException();
		}

		if (s_clientInstance != null) {
			throw new IllegalStateException("Client MetricsCollector instance has already been set");
		}

		s_clientInstance = value;
	}

	/**
	 * Sets the MetricsCollector instance to be used on the server side.
	 * @param value A MetricsCollector instance to be used on the server side.
	 */
	protected static void setServerInstance(MetricsCollector value) {
		if (value == null) {
			throw new NullPointerException();
		}

		if (s_serverInstance != null) {
			throw new IllegalStateException("Server MetricsCollector instance has already been set");
		}

		s_serverInstance = value;
	}

	/**
	 * Uses the given metric name, the service name and operation name from the currently
	 * MessageContext to find the corresponding MetricValue 
	 * and returns it. If the value doesn't exist, it returns null.
	 * 
	 * @param metricName The name of a metric.
	 * @return A MetricValueAggregator.
	 * 
	 */
	public abstract MetricValueAggregator getMetricValue(String metricName);

	/**
	 * Uses the given metric name, the service name and operation name from the given
	 * MessageContext to find the corresponding MetricValue 
	 * and returns it. If the value doesn't exist, it returns null.
	 * 
	 * @param metricName The name of a metric.
	 * @param ctx A MessageContext.
	 * @return A MetricValueAggregator.
	 */
	public abstract MetricValueAggregator getMetricValue(String metricName, MessageContext ctx);


	/**
	 * Uses the given metric name, the service name and operation name 
	 * to find the corresponding MetricValue 
	 * and returns it. If the value doesn't exist, it returns null.
	 * 
	 * @param metricName The name of a metric.
	 * @param svcId A ServiceId.
	 * @param opName An operation name.
	 * @return A MetricValueAggregator.
	 */
	public abstract MetricValueAggregator getMetricValue(
		String metricName, ServiceId svcId, String opName);


	/**
	 * Uses the given metric name, the service name, the service subname and the operation name 
	 * to find the corresponding MetricValue 
	 * and returns it. If the value doesn't exist, it returns null.
	 * 
	 * @param metricName The name of a metric.
	 * @param adminName A service admin name.
	 * @param subname deprecated.
	 * @param opName The name of an operation.
	 * @return A MetricValueAggregator.
	 * @deprecated
	 */
	public abstract MetricValueAggregator getMetricValue(
		String metricName, String adminName, String subname, String opName);

	/**
	 * Using the ServiceId to find the corresponding MetricValue 
	 * and return it. If the value doesn't exist, create a new 
	 * MetricValueAggregator using the given value as the 
	 * initialization value.
	 * 
	 * @param metricId A MetricId.
	 * @return A MetricValueAggregator.
	 */
	public abstract MetricValueAggregator getMetricValue(MetricId metricId);

	/**
	 * Resets all the metric value to their initial default value.
	 */
	public abstract void reset();

	/**
	 * Resets all the metric value to their initial default value for the given service
	 * admin name.
	 * @param adminName A service admin name
	 */
	public abstract void reset(String adminName);

	/**
	 * Returns the current monitoring level for the service with the given adminName.
	 * @param adminName A service admin name
	 * @return the current monitoring level for the service with the given adminName.
	 */
	public abstract MonitoringLevel getMonitoringLevel(String adminName);

	/**
	 * @return all the metric aggregation values of the moment.
	 * 
	 * 
	 */
	public abstract List<MetricValueAggregator> getAllMetricValues();
}
