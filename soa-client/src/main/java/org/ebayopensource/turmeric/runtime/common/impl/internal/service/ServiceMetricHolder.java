/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * @author ichernyshev
 */
final class ServiceMetricHolder {

	private final ServiceId m_svcId;
	private final String m_opName;

	private Map<String,MetricValueAggregator> m_metrics;
	private Set<MetricDef> m_failedDefs;

	public ServiceMetricHolder(ServiceId svcId, String opName) {
		m_svcId = svcId;
		m_opName = opName;
	}

	public void update(MessageContext ctx, MetricDef def, long count) {
		try {
			if (m_metrics == null) {
				// build definitions lazily to save memory on unused and fallback operations/services
				if (m_opName != null) {
					m_metrics = buildValues(SystemMetricDefs.getAllOperationMetrics());
				} else {
					m_metrics = buildValues(SystemMetricDefs.getAllSvcMetrics());
				}
			}

			MetricValueAggregator value = m_metrics.get(def.getMetricName());
			if (value == null) {
				if (addFailedDef(def)) {
					LogManager.getInstance(ServiceMetricHolder.class).log(
						Level.WARNING, "Attempt to update unknown metric '" + def.getMetricName() +
						" on service '" + m_svcId.getAdminName() + "." + m_opName + "'");
				}
				return;
			}

			value.update(ctx, count);
		} catch (Throwable e) {
			if (addFailedDef(def)) {
				LogManager.getInstance(ServiceMetricHolder.class).log(
					Level.WARNING, "Error updating metric '" + def.getMetricName() +
					" on service '" + m_svcId.getAdminName() + "." + m_opName + "': " +
					e.toString(), e);
			}
		}
	}

	private synchronized boolean addFailedDef(MetricDef def) {
		if (m_failedDefs == null) {
			m_failedDefs = new HashSet<MetricDef>();
		}

		if (m_failedDefs.contains(def)) {
			return false;
		}

		m_failedDefs.add(def);
		return true;
	}

	private Map<String,MetricValueAggregator> buildValues(List defs)
	{
		MetricsCollector collector;
		if (m_svcId.isClientSide()) {
			collector = MetricsCollector.getClientInstance();
		} else {
			collector = MetricsCollector.getServerInstance();
		}

		Map<String,MetricValueAggregator> metrics = new HashMap<String,MetricValueAggregator>();
		for (Iterator it=defs.iterator(); it.hasNext(); ) {
			MetricDef def = (MetricDef)it.next();
			String name = def.getMetricName();
			MetricValueAggregator value = collector.getMetricValue(name, m_svcId, m_opName);
			metrics.put(name, value);
		}

		return metrics;
	}
}
