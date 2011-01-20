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
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.DiffBasedMetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;


/**
 * This is the class that at given time interval,  get a current snapshot from the registry, 
 * at the same time reset the aggregated metric values, and save the snapshot to all the 
 * registered storage provider.
 * 
 * The SOA framework's component status should be able to trigger a snapshot process to 
 * get the up to the second metric information.s 
 * 
 * @author wdeng
 */
final class MetricsSnapshotScheduler extends TimerTask {
	
	private final MonitoringDesc m_desc;
	private final MetricsCollector m_collector;
	Timer m_timer;
	
	MetricsSnapshotScheduler(MonitoringDesc desc, MetricsCollector collector) {
		m_desc = desc;
		m_collector = collector;
//		addShutdownHook();
	}

	public void start() {
		long interval = m_desc.getSnapshotInterval() * 1000; //convert from second to ms
		if (interval <= 0) {
			return;
		}

		m_timer = new Timer(MetricsSnapshotScheduler.class.getSimpleName(), true);
		m_timer.schedule(this, interval, interval);
	}

	@Override
	public void run() {
		persistSnapshot(); 
	}

	synchronized void persistSnapshot() {
		try {
			long timeSnapshot = System.currentTimeMillis();
			Collection<MetricValueAggregator> snapshot = m_collector.getAllMetricValues();
			Collection<MetricsStorageProvider> providers = m_desc.getAllProviders();
			for (Iterator<MetricsStorageProvider> it=providers.iterator(); it.hasNext(); ) {
				MetricsStorageProvider provider = it.next();
				provider.saveMetricSnapshot(timeSnapshot, snapshot);
			}
		} catch (Exception e) {
			LogManager.getInstance(MetricsSnapshotScheduler.class).log(Level.INFO, "Exception pushing metrics snapshot.", e);
 		}
	}
	
	synchronized void persistSnapshot(String resetAdminName)
	{
		persistSnapshot();

		Collection<MetricsStorageProvider> providers = m_desc.getAllProviders();
		for (Iterator<MetricsStorageProvider> it=providers.iterator(); it.hasNext(); ) {
			MetricsStorageProvider provider = it.next();
			if(provider instanceof DiffBasedMetricsStorageProvider)
			{
				((DiffBasedMetricsStorageProvider)provider).resetPreviousSnapshot(resetAdminName);
			}
		}
	}
	
/*	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
	        public void run() {
	        	m_timer.cancel();
	        	persistSnapshot();
	        }
	    });
	}
*/}
