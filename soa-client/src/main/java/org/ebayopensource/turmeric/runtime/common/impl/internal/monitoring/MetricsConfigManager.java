/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.CommonConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;


public final class MetricsConfigManager {

	private final boolean m_isClientSide;

	private static final String DEFAULT_METRIC = "SoaFwk.Err.Total";

	private static final String DEFAULT_THRESHOLD_VALUE = "100";

	private static final int DEFAULT_SAMPLE_SIZE = 3;

	private Map<String, MonitoringLevel> m_configData = new HashMap<String, MonitoringLevel>();

	private Map<String, ErrorStatusOptions> m_errorOptionsMap = new HashMap<String, ErrorStatusOptions>();

	private DynamicMonitoringChangeMgr m_monitoringConfigBean;

	public static MetricsConfigManager getClientInstance() {
		MetricsCollectorImpl collector = (MetricsCollectorImpl) MetricsCollector
				.getClientInstance();
		return collector.getConfigManager();
	}

	public static MetricsConfigManager getServerInstance() {
		MetricsCollectorImpl collector = (MetricsCollectorImpl) MetricsCollector
				.getServerInstance();
		return collector.getConfigManager();
	}

	MetricsConfigManager(boolean isClientSide) {
		m_isClientSide = isClientSide;

		if (isClientSide) {
			m_monitoringConfigBean = DynamicMonitoringChangeMgr
					.getClientInstance();
		} else {
			m_monitoringConfigBean = DynamicMonitoringChangeMgr
					.getServerInstance();
		}
	}

	public synchronized void resetMonitoringLevel(String adminName) {
		// do not set actual value here as it can be "DEFAULT"
		Map<String, MonitoringLevel> configData = new HashMap<String, MonitoringLevel>(
				m_configData);
		configData.remove(adminName);
		m_configData = configData;
	}

	public MonitoringLevel getMonitoringLevel(String adminName) {
		MonitoringLevel result = m_configData.get(adminName);
		if (result != null) {
			return result;
		}

		MonitoringLevel defLevel;
		try {
			defLevel = getDefaultLevel(adminName);
		} catch (Throwable e) {
			LogManager.getInstance(MetricsConfigManager.class).log(
					Level.WARNING,
					"Unable to obtain logging level for '" + adminName
							+ "', assuming it's 'NORMAL': " + e.toString(), e);
			defLevel = MonitoringLevel.NORMAL;
		}

		synchronized (this) {
			result = m_configData.get(adminName);
			if (result == null) {
				// if no one went ahead of us, use the newly found defLevel
				result = defLevel;

				Map<String, MonitoringLevel> configData = new HashMap<String, MonitoringLevel>(
						m_configData);
				configData.put(adminName, result);
				m_configData = configData;
			}
		}

		return result;
	}

	private MonitoringLevel getDefaultLevel(String adminName) {
		// 1. check in the config bean overrides
		MonitoringLevel result = m_monitoringConfigBean
				.getMonitoringLevel(adminName);
		if (result != null) {
			return result;
		}

		// 2. go through multiple service descs
		BaseServiceDescFactory descFactory;
		if (m_isClientSide) {
			descFactory = BaseServiceDescFactory.getClientInstance();
		} else {
			descFactory = BaseServiceDescFactory.getServerInstance();
		}

		@SuppressWarnings("unchecked")
		Collection<ServiceDesc> descs = descFactory
				.getKnownServiceDescsByAdminName(adminName);

		for (ServiceDesc desc : descs) {
			CommonConfigHolder configHolder = desc.getConfig();
			MonitoringLevel level2 = configHolder.getMonitoringLevel();
			if (level2 != null) {
				if (result == null || level2.ordinal() > result.ordinal()) {
					// get the finer grained value
					result = level2;
				}
			}
		}

		if (result != null) {
			return result;
		}

		// 3. default to "NORMAL" if nothing found
		return MonitoringLevel.NORMAL;
	}

	public ErrorStatusOptions getErrorStatusOption(String adminName) {
		ErrorStatusOptions result = m_errorOptionsMap.get(adminName);

		// Check if the instance is serverside then only proceed else return
		// null
		if (!this.m_isClientSide) {
			if (result != null) {
				return result;
			}

			ErrorStatusOptions defErrorStat;
			try {
				defErrorStat = getDefErrorStatusOption(adminName);
			} catch (Throwable e) {
				LogManager.getInstance(MetricsConfigManager.class).log(
						Level.WARNING,
						"Unable to obtain logging level for '" + adminName
								+ "', assuming it's 'NORMAL': " + e.toString(),
						e);
				ErrorStatusOptions errorOptions = new ErrorStatusOptions();
				errorOptions.setMetric("SoaFwk.Err.Total");
				defErrorStat = errorOptions;
			}

			synchronized (this) {
				result = m_errorOptionsMap.get(adminName);
				if (result == null) {
					result = defErrorStat;

					Map<String, ErrorStatusOptions> errorDataMap = new HashMap<String, ErrorStatusOptions>(
							m_errorOptionsMap);
					errorDataMap.put(adminName, result);
					m_errorOptionsMap = errorDataMap;
				}
			}
		} else {
			result = null;
		}
		return result;
	}

	public ErrorStatusOptions getDefErrorStatusOption(String adminName) {
		ErrorStatusOptions errorStatusOptions = new ErrorStatusOptions();

		// 1. go through multiple service descs
		BaseServiceDescFactory descFactory;
		descFactory = BaseServiceDescFactory.getServerInstance();

		@SuppressWarnings("unchecked")
		Collection<ServiceDesc> descs = descFactory
				.getKnownServiceDescsByAdminName(adminName);

		for (ServiceDesc desc : descs) {
			if (this.m_isClientSide) {
				return null;
			}
			CommonConfigHolder configHolder = desc.getConfig();
			errorStatusOptions = configHolder.getErrorStatusOptions();

			if (errorStatusOptions != null) {
				if (errorStatusOptions.getMetric() == null) {
					errorStatusOptions.setMetric(DEFAULT_METRIC);
				}
				if (errorStatusOptions.getSampleSize() <= 0) {
					// Sample size must be a positive integer with default value
					// 3
					errorStatusOptions.setSampleSize(DEFAULT_SAMPLE_SIZE);
				}
				if (errorStatusOptions.getThreshold() == null) {
					// Setting default Threshold to value 100
					errorStatusOptions.setThreshold(DEFAULT_THRESHOLD_VALUE);
				}
				return errorStatusOptions;
			}
		}

		// 3. default to "SoaFwk.Err.Total" if nothing found
		errorStatusOptions = new ErrorStatusOptions();
		errorStatusOptions.setMetric(DEFAULT_METRIC);
		errorStatusOptions.setThreshold(DEFAULT_THRESHOLD_VALUE);
		errorStatusOptions.setSampleSize(DEFAULT_SAMPLE_SIZE);

		return errorStatusOptions;
	}
}
