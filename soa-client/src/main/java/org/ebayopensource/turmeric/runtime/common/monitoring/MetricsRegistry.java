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

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * In order for the monitoring system to collect a metric, the metric's MetricDef has 
 * to be registered with the MetricRegistry. The system provides two registries: one 
 * for client metrics and the other for server metrics. Each registry manages a table 
 * that maps from MetricIds to MetricDefs. The IDs can take wildcard characters. It 
 * provides a set of convenient (un)register methods to (un)register MetricDefs.
 * 
 * A service registers their metrics through the code-generated ServiceImpl class and 
 * has an init method generated. This init method will be called when the SOA framework 
 * starts the service. The application developer can add metrics registration code in 
 * the constructor of their service impl class.
 *
 * If a new MetricDef overlaps with an existing definition an exception will be thrown.
 *
 * @author wdeng
 * @author ichernyshev
 */
public abstract class MetricsRegistry {

	private static MetricsRegistry s_clientInstance;
	private static MetricsRegistry s_serverInstance;

	/**
	 * 
	 * @return the client registry instance.
	 */
	public static MetricsRegistry getClientInstance() {
		if (s_clientInstance == null) {
			throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_CLIENT_NOT_INITIALIZED,
					ErrorConstants.ERRORDOMAIN));
		}

		return s_clientInstance;
	}

	/**
	 * 
	 * @return the server registry instance.
	 */
	public static MetricsRegistry getServerInstance() {
		if (s_serverInstance == null) {
			throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_SERVER_NOT_INITIALIZED,
					ErrorConstants.ERRORDOMAIN));
		}

		return s_serverInstance;
	}

	/**
	 * Sets  the client registry instance.
	 * @param value  the client registry instance.
	 */
	protected static void setClientInstance(MetricsRegistry value) {
		if (value == null) {
			throw new NullPointerException();
		}

		if (s_clientInstance != null) {
			throw new IllegalStateException("Client MetricsRegistry instance has already been set");
		}

		s_clientInstance = value;
	}

	/**
	 * Sets  the server registry instance.
	 * @param value  the server registry instance.
	 */
	protected static void setServerInstance(MetricsRegistry value) {
		if (value == null) {
			throw new NullPointerException();
		}

		if (s_serverInstance != null) {
			throw new IllegalStateException("Server MetricsRegistry instance has already been set");
		}

		s_serverInstance = value;
	}

	/**
	 * registerMetrics is used to register metric with the monitoring system runtime. 
	 * registerMetric is expected to be called at init time
	 * 
	 * @param metricDefs List of MetricDefs to be registered. 
	 * @throws ServiceException Exception when metric registering fails.
	 */
	public abstract void registerMetrics(List<MetricDef> metricDefs) throws ServiceException;

	/**
	 * The registerMetric method registers the MetricDefs. 
	 * The MetricId can contain wildcard in its serviceName, 
	 * operationName, and/or usecaseName to indicate an apply-all.  
	 * or null to indicate don't-care.
	 *  
	 * registerMetric is expected to be called at init time.
	 * @param metricDef A MetricDef.
	 * @throws ServiceException Exception when metric registering fails.
	 */
	public abstract void registerMetric(MetricDef metricDef) throws ServiceException;

	/**
	 * Unregisters the given list of MetricDefs.
	 * @param metricDefs List of MetricDefs to be registered. 
	 * @return  the given list of MetricDefs.
	 */
	public abstract List<MetricDef> unregisterMetricsByDef(List<MetricDef> metricDefs);

	/**
	 * Unregisters the given MetricDef.
	 * 
	 * @param metricDef A MetricDef.
	 * @return the given MetricDef.
	 */
	public abstract MetricDef unregisterMetricByDef(MetricDef metricDef);

	/**
	 * Unregisters the metric identified by the given metric name, the service admin name, and
	 * the operation name.
	 * @param metricName A metric name
	 * @param serviceName A service name QName
	 * @param opName An operation name.
	 * @return The metric corresponding to the given metric name, service name, and operation 
	 * name.
	 */
	public abstract MetricDef unregisterMetric(String metricName, QName serviceName, String opName);

	/**
	 * Gets the MetricDef for the given MetricId.  
	 * If the definition don't exist in the registry but the id 
	 * is included in a definition with wildcards, A new 
	 * MetricDef will be created, registered and return.
	 * 
	 * @param metricId A metric id without any wildcard and/or don't-care
	 * @return A MetricDef for the given MetricId.
	 */
	public abstract MetricDef findMetricDef(MetricId metricId);

	/**
	 * Registers all the MetricDefs defined in a class as public static
	 * and final constants.
	 * 
	 * @param clazz A class object.
	 */
	public abstract void registerAllMetricsForClass(Class clazz);
}
