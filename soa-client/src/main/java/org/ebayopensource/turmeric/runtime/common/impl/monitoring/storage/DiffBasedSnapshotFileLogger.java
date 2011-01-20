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

import org.ebayopensource.turmeric.runtime.common.monitoring.DiffBasedMetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;



/**
 * This class provides a default metrics snapshot logger to log diff based 
 * metrics to a log file. Each metric value in a snapshot is logged as
 * one line in the file with the metric name, service admin name, operation name,
 * usecase name,  client data center, server data center, and the metric value. 
 * These fields are delimited. The name of the log file and the delimiter can be
 * customized using <storage-option> when configuring storage provider. For example,
 * 
 *	<storage-provider name="DiffBasedFileLogger">
 *  	<class-name>org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.DiffBasedSnapshotFileLogger</class-name>
 *		<storage-options>
 *			<option name="filename">MyMetrics.log</option>
 *			<option name="delimiter">;</option>
 *		</storage-options>
 *	</storage-provider>
 * 
 * By default the file name is SOAMetrics.log and the delimiter is ','.
 * 
 * @author wdeng
 */
public class DiffBasedSnapshotFileLogger extends SnapshotFileLogger 
	implements DiffBasedMetricsStorageProvider{
	
	private SnapshotDiffHelper m_diffHelper;
	
	public DiffBasedSnapshotFileLogger() {
		m_diffHelper = new SnapshotDiffHelper();
	}

	@Override
	protected MetricValueAggregator getAggregator(MetricValueAggregator value) {
		return m_diffHelper.getAggregator(value);
	}

	@Override
	protected void finalProcessing(long snapshotTime, Collection<MetricValueAggregator> snapshot) {
		m_diffHelper.recordSnapshot(snapshotTime, snapshot);
	}

	@Override
	public void resetPreviousSnapshot(String adminName) {
		m_diffHelper.resetPreviousSnapshot(adminName);
	}

}
