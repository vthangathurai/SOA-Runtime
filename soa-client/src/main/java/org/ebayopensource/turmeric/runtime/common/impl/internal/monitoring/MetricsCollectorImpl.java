/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author wdeng
 * @author ichernyshev
 */
final class MetricsCollectorImpl extends MetricsCollector {

	private final static String UNKNOWN_SERVICE_NAME = "__Unknown__";

	private final MetricsRegistryImpl m_registry;
	private final MetricsConfigManager m_configMgr;
	private Map<MetricId,MetricValueAggregatorImpl> m_valueById =
		new HashMap<MetricId,MetricValueAggregatorImpl>();

	MetricsCollectorImpl(MetricsRegistryImpl registry) {
		m_registry = registry;
		m_configMgr = new MetricsConfigManager(registry.isClientSide());
	}

	static void setClientInstance(MetricsCollectorImpl value) {
		MetricsCollector.setClientInstance(value);
	}

	static void setServerInstance(MetricsCollectorImpl value) {
		MetricsCollector.setServerInstance(value);
	}

	MetricsConfigManager getConfigManager() {
		return m_configMgr;
	}

	/**
	 * Using a MetricId to find the corresponding MetricValue 
	 * and return it. If the value doesn't exist, it returns null.
	 */
	@Override
	public MetricValueAggregator getMetricValue(String metricName) {
		MessageContext ctx = MessageContextAccessor.getContext();
		return getMetricValue(metricName, ctx);
	}

	@Override
	public MetricValueAggregator getMetricValue(String metricName, MessageContext ctx) {
		if (ctx == null) {
			return getMetricValue(metricName, UNKNOWN_SERVICE_NAME, null, "__Unknown__");
		}

		ServiceId id = ctx.getServiceId();
		return getMetricValue(metricName, id.getAdminName(),
			id.getServiceSubname(), ctx.getOperationName());
	}

	@Override
	public MetricValueAggregator getMetricValue(
		String metricName, ServiceId svcId, String opName)
	{
		return getMetricValue(metricName, svcId.getAdminName(),
			svcId.getServiceSubname(), opName);
	}

	@SuppressWarnings("deprecation")
	@Override
	public MetricValueAggregator getMetricValue(String metricName, String adminName,
		String subname, String opName)
	{
		MetricId id = new MetricId(metricName, adminName, subname, opName);
		return getMetricValue(id);
	}

	/**
	 * Using the ServiceId to find the corresponding MetricValue 
	 * and return it. If the value doesn't exist, create a new 
	 * MetricValueAggregator using the given value as the 
	 * initialization value.
	 */
	@Override
	public MetricValueAggregator getMetricValue(MetricId metricId)
	{
		/*
		 * This returns the ServiceMetricValueAggregator
		 * The caller is supposed to call update on the returned 
		 * aggregator, which also implements the MetricValue interface
		 * User is not expected to directly update his own instance of 
		 * the MetricValue and hence the name updatableMetricValue
		 * The update() calls on the returned aggregator delegates 
		 * to the corresponding update() call on the caller supplied 
		 * MetricValue 
		 */

		// no need to synchronize, since we simply swap pointers during update
		MetricValueAggregator value = m_valueById.get(metricId);
		if (value != null) {
			return value;
		}

		MetricDef def = m_registry.findMetricDef(metricId);
		if (def == null) {
			// this could be because metric is not registered or
			// because service was not loaded, so we could not find QName
			throw new ServiceRuntimeException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_DEFINITION_NOT_REGISTERED,
					ErrorConstants.ERRORDOMAIN, new Object[] {metricId}));
		}

		return addToAggregatorMap(metricId, def);
	}

	private synchronized MetricValueAggregator addToAggregatorMap(
		MetricId metricId, MetricDef metricDef)
	{
		// re-check map within synchronized call
		MetricValueAggregatorImpl value = m_valueById.get(metricId);
		if (value != null) {
			return value;
		}

		// Commenting out following if block as becuase of this the Service Level Metrics
		// are logged with wrong service name.
		/*if (metricDef.getOperationName() == null) {
			// if we don't care about operation - try to reuse an existing aggregator
			// otherwise we will always create a new aggregator for each new MetricId
			value = findByMetricDef(metricDef);
		}*/
		
		// no value to reuse, create a new one
		value = MetricValueAggregatorImpl.create(metricId, metricDef, this);
		
		// create new map and swap pointers
		Map<MetricId,MetricValueAggregatorImpl> newMap =
			new HashMap<MetricId,MetricValueAggregatorImpl>(m_valueById);
		newMap.put(metricId, value);
		m_valueById = newMap;

		return value;
	}

	/*private MetricValueAggregatorImpl findByMetricDef(MetricDef metricDef)
	{
		for (MetricValueAggregatorImpl m: m_valueById.values()) {
			if (m.getMetricDef() == metricDef) {
				return m;
			}
		}

		return null;
	}*/

	/**
	 * Resets All the metric value to its initial value.
	 */
	@Override
	public void reset() {
		// get a copy of map pointer before iterating
		Map<MetricId,MetricValueAggregatorImpl> valueById = m_valueById;

		for (MetricValueAggregatorImpl aggr: valueById.values()) {
			aggr.reset();
		}
	}

	@Override
	public MonitoringLevel getMonitoringLevel(String adminName) {
		return m_configMgr.getMonitoringLevel(adminName);
	}

	/**
	 * Gets all the metric aggregation values of the moment.
	 */
	@Override
	public List<MetricValueAggregator> getAllMetricValues() {
		// get a copy of map pointer before iterating
		Map<MetricId,MetricValueAggregatorImpl> valueById = m_valueById;

		int size = valueById.size();
		List<MetricValueAggregator> result = new ArrayList<MetricValueAggregator>(size);
		for (MetricValueAggregator value: valueById.values()) {
			MetricValueAggregator copy = (MetricValueAggregator)value.deepCopy(true);
			result.add(copy);
		}
		return result;
	}

	/**
	 * Removes the metric value aggregators from the map for the given metric id. The 
	 * MetricId can contain wildcard. In that case, multiple aggregators would be removed
	 * 
	 * @param metricId
	 */
	synchronized void removeMetricValues(MetricDef metricDef) {
		Map<MetricId,MetricValueAggregatorImpl> newMap =
			new HashMap<MetricId,MetricValueAggregatorImpl>(m_valueById);

		for (Iterator<MetricValueAggregatorImpl> it=newMap.values().iterator(); it.hasNext();) {
			MetricValueAggregatorImpl value = it.next();
			if (value.getMetricDef() == metricDef) {
				it.remove();
			}
		}
		m_valueById = newMap;
	}
	
	@Override
	public synchronized void reset(String adminName) {
		Map<MetricId, MetricValueAggregatorImpl> valueById = m_valueById;

		for (MetricId metricId : valueById.keySet()) {
			if (metricId.getAdminName().equals(adminName)) {
				m_valueById.get(metricId).reset();
			}
		}
	}
}
