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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;


public class DiffSnapshotView implements MetricsStorageProvider {

	private Map<String, MetricValue[]> m_metricValuesMap = new HashMap<String, MetricValue[]>();
	private Map<String, Integer> m_metricPointerMap = new HashMap<String, Integer>();
	private Collection<MetricValueAggregator> m_previousSnapshot;
	private Collection<MetricValueAggregator> m_currentSnapshot;
	private MetricValue m_metricValues[];
	private MetricsConfigManager m_configManager;
	private long m_previousSnapshotTime;
	private long m_currentSnapshotTime;
	private long m_refreshIntervalInMillis;
		
	private static int s_ptr = 0;
	private int m_sampleSize = 10;
	
	
	public void init(Map<String, String> options, String name, String collectionLocation, Integer snapshotInterval) {
		// noop
	}
	
	public void setConfigManager(MetricsConfigManager configManager) {
		m_configManager = configManager;
	}

	public final void saveMetricSnapshot(long timeSnapshot, Collection<MetricValueAggregator> snapshot)
		throws ServiceException
	{		
		if (snapshot.isEmpty()) {
			return;
		}

		if (m_refreshIntervalInMillis != 0 &&
			timeSnapshot < m_previousSnapshotTime + m_refreshIntervalInMillis)
		{
			return;
		}

		recordSnapshot(timeSnapshot, snapshot);
	}

	public void setRefreshInterval(long refreshIntervalInSeconds) {
		m_refreshIntervalInMillis = refreshIntervalInSeconds * 1000;
	}

	public long getLastInterval() {
		if (m_previousSnapshotTime == 0) {
			return -1;
		}

		return m_currentSnapshotTime - m_previousSnapshotTime;
	}

	public Collection<MetricValueAggregator> getDiffAggregator() {
		if (null == m_previousSnapshot) {
			return m_currentSnapshot;
		}
		Collection<MetricValueAggregator> diffAggr = new ArrayList<MetricValueAggregator>();
		for (MetricValueAggregator currentAggr : m_currentSnapshot) {
			MetricId id = currentAggr.getMetricId();
			for (MetricValueAggregator previousAggr : m_previousSnapshot) {
				if (id.equals(previousAggr.getMetricId())) {
					MetricValueAggregator aggr = (MetricValueAggregator) currentAggr
							.diff(previousAggr, true);
					diffAggr.add(aggr);
				}
			}
		}

		return diffAggr;
	}

	private void recordSnapshot(long snapshotTime,
			Collection<MetricValueAggregator> snapshot) {
		m_previousSnapshotTime = m_currentSnapshotTime;
		m_currentSnapshotTime = snapshotTime;
		m_previousSnapshot = m_currentSnapshot;
		m_currentSnapshot = snapshot;
 		
		populateMetricBuffer();
	}

	public void populateMetricBuffer() {
		
		MetricValue val = null;
		
		for (MetricValueAggregator mva : m_currentSnapshot) {
			MetricId id = mva.getMetricId();
			
			if(id == null) {
				continue;
			}

			String adminName = id.getAdminName();
			if(m_configManager == null) {
				continue;
			}
			
			ErrorStatusOptions errorStatusOption = m_configManager.getErrorStatusOption(adminName);
			
			if(errorStatusOption == null) {
				continue;
			}

			String metricName = errorStatusOption.getMetric();
			m_sampleSize = errorStatusOption.getSampleSize();
									
			if (id.getMetricName().equals(metricName)) {
				if (id.getOperationName() == null) {
					val = mva.getTotalValue();
					if (val == null) {
						// no data
						continue;
					}
										
					m_metricValues = m_metricValuesMap.get(adminName);
					Integer pointer = m_metricPointerMap.get(adminName);
					
					if (m_metricValues == null) {
						m_metricValues = new MetricValue[m_sampleSize];
					}
					
					if(pointer != null) {
						s_ptr = pointer.intValue();
					}
					
					if (s_ptr >= m_metricValues.length) {
						s_ptr = 0;
					}
					m_metricValues[s_ptr++] = val;
					
					m_metricValuesMap.put(adminName, m_metricValues);
					m_metricPointerMap.put(adminName, Integer.valueOf(s_ptr));
					
					break;
				}
			}			
		}

	}

	public MetricValue getTotalMetricValue(String adminName) {
		
		MetricValue totalMetricValue = null;
		m_metricValues = m_metricValuesMap.get(adminName);
			
		if(m_metricValues != null) {
			for (int i = 0; i < m_metricValues.length; i++) {
				if (m_metricValues[i] != null) {
					if (i == 0) {
						totalMetricValue = m_metricValues[i].deepCopy(false);
						continue;
					}
					if (totalMetricValue != null) {
						totalMetricValue.update(m_metricValues[i]);
					}
				} else {
					totalMetricValue = null;
					break;
				}
			}
		}
		
		return totalMetricValue;
	}
}
