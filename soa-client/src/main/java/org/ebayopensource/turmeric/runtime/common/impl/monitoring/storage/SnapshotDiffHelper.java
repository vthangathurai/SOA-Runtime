/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage;

import java.util.ArrayList;
import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;


/**
 * @author wdeng
 */
class SnapshotDiffHelper {

	private Collection<MetricValueAggregator> m_previousSnapshot;
	//private long m_prevSnapshotTime;

	void recordSnapshot(long snapshotTime, Collection<MetricValueAggregator> snapshot) {
		//m_prevSnapshotTime = snapshotTime;
		m_previousSnapshot = snapshot;
	}		

	void resetPreviousSnapshot(String adminName) {
		Collection<MetricValueAggregator> savedCopy = m_previousSnapshot;
		m_previousSnapshot = new ArrayList<MetricValueAggregator>(
				m_previousSnapshot.size());
		for (MetricValueAggregator metricValueAggregator : savedCopy) {
			if (!metricValueAggregator.getMetricId().getAdminName().equals(
					adminName)) {
				m_previousSnapshot.add(metricValueAggregator);
			}
		}
	}
	
	MetricValueAggregator getAggregator(MetricValueAggregator current) {
		if (null == m_previousSnapshot) {
			return current;
		}

		MetricId id = current.getMetricId();
		for (MetricValueAggregator value: m_previousSnapshot) {
			if (id.equals(value.getMetricId())) {
				return (MetricValueAggregator)current.diff(value, true);
			}
		}
		return current;
	}

}
