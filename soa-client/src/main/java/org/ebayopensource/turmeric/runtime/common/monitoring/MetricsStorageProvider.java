/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;

import java.util.Collection;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;


/**
 * MetricsStorageProvider interface is provided for developers to implement and provide 
 * their own storage mechanism to store the metrics. The interface provides methods 
 * to store registered metrics and the aggregated metrics values. 
 * 
 * StorageProvider implementers register their storage provider through the Default Service 
 * Group configuration file by listing the MetricStrorageProvider impls to be used.
 *
 * The framework runs two MetricsSnapshotSchedulers in two separate background threads: 
 * one for the client side metrics and the other for the server side metrics. The schedulers 
 * wake up after a preset interval (specified in global config file's monitor-config section), 
 * call MetricsCollector to get the current snapshot, retrieve the list of all the registered 
 * storage provider impls from the monitoring configuration, and call the provider's 
 * saveMetricsSnapshot method, one by one.
 *
 * The snapshot contains up to the point aggregated information from the time the server 
 * started up. If a diff snapshot is needed, the storage provider implementation is 
 * responsible for finding the difference.
 * 
 * @author wdeng
 */
public interface MetricsStorageProvider {

	/**
	 * @param options configuration options
	 * @param name    storage provider name
	 * @param collectionLocation client or server side
	 * @param snapshotInterval frequency of snapshots in seconds
	 */
	public void init(Map<String,String> options, String name, String collectionLocation, Integer snapshotInterval);
	
	/**
	 * This method is to be implemented by the provider to storage the passed in
	 * metrics snapshot.
	 * 
	 * @param timeSnapshot	Time the sanpshot is taken.
	 * @param snapshotCollection		the snapshot.
	 * @throws ServiceException 
	 */
	public void saveMetricSnapshot(long timeSnapshot, Collection<MetricValueAggregator> snapshotCollection) throws ServiceException;
}
