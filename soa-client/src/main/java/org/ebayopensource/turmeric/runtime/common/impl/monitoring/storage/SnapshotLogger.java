/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage;

import java.util.Collection;
import java.util.Formatter;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;


/**
 * This is an abstract base class for storage provider. It implements the 
 * MetricsSotrageProvider interface.  The saveMetricSnapshot method has 
 * interception points: prelude, saveMetricValue, epilog, finalProcessing) 
 * for subclass to customize the logging behaviors.  
 * 	prelude customizes the header;
 * 	epilog customizes the tailer;
 *  saveMetricValue logs the metric value for one metric.
 *  finalProcessing cleans up the logger after a snapshot is logged. 
 * 
 * @author wdeng
 */
public abstract class SnapshotLogger implements MetricsStorageProvider {
	
	String m_collectionLocation;
	Integer m_snapshotInterval;
	public void init(Map<String,String> options, String name, String collectionLocation, Integer snapshotInterval) {
		m_collectionLocation = collectionLocation;
		m_snapshotInterval = snapshotInterval;
	}
	
	public final void saveMetricSnapshot(long timeSnapshot, Collection<MetricValueAggregator> snapshot) throws ServiceException
	{
		if (snapshot.isEmpty()) {
			return;
		}
		if (snapshot.size() == 0) {
			return;
		}
		try {
			prelude(timeSnapshot, snapshot);
	
			for (MetricValueAggregator aggr: snapshot) {
				aggr = getAggregator(aggr);
				MetricId id = aggr.getMetricId();
				Collection<MetricClassifier> clzfers = aggr.getClassifiers();
				for (MetricClassifier clzfer: clzfers) {
					MetricValue value = aggr.getValue(clzfer);
					saveMetricValue(timeSnapshot, id, clzfer, value);
				}
			}
			epilog(timeSnapshot, snapshot);
		} finally {
			finalProcessing(timeSnapshot, snapshot);
		}
	}

	/**
	 * This method provides the hook-point for converting the aggregated value to 
	 * its value for output.  For example, diff based provider will find the difference
	 * between the passed in value and the previous value and returns the diff.
	 * 
	 * By default  it dose nothing but return the argument value.
	 *  
	 * @param value
	 * @return
	 */
	protected MetricValueAggregator getAggregator(MetricValueAggregator value) {
		return value;
	}

	/**
	 * Used to customize the header of a snapshot log. default is do nothing.
	 * 
	 * @param snapshotTime
	 * @param snapshot
	 * @throws ServiceException
	 */
	protected void prelude(long snapshotTime, Collection<MetricValueAggregator> snapshot) 
		throws ServiceException {
		// subclasses can hook in
	}

	/**
	 * Used to customize the tailer of a snapshot log. default is do nothing.
	 * 
	 * @param snapshotTime
	 * @param snapshot
	 * @throws ServiceException
	 */
	protected void epilog(long snapshotTime, Collection<MetricValueAggregator> snapshot) throws ServiceException {
		// subclasses can hook in
	}

	/**
	 * Defines clean up behaviors after a snapshot is logged. 
	 * 
	 * @param snapshotTime
	 * @param snapshot
	 */
	protected void finalProcessing(long snapshotTime, Collection<MetricValueAggregator> snapshot) {
		// subclasses can hook in
	}

	/**
	 * Defines the way to log one metric value.
	 * 
	 * @param timeSnapshot
	 * @param id
	 * @param key
	 * @param value
	 */
	protected abstract void saveMetricValue(long timeSnapshot, MetricId id,
			MetricClassifier key, MetricValue value);	

	
	protected static void formatComponentValue(MetricComponentValue value, Formatter formatter) {
		Object v = value.getValue();
		if (v instanceof Float || v instanceof Double) {
			formatter.format("%1$30.4f", v);
			return;
		}
		formatter.format("%1$s", v);
	}

	public String getCollectionLocation() {
		return m_collectionLocation;
	}
}
