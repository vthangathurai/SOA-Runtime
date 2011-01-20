/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MonitoringSystem;
import org.ebayopensource.turmeric.runtime.common.monitoring.DiffBasedMetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;

import com.ebay.kernel.calwrapper.CalHeartbeat;
import com.ebay.kernel.calwrapper.CalHeartbeatFactory;

/**
 * This class provides a default metrics snapshot logger to log diff based
 * metrics as CALHeartbeats. Each metric value in a snapshot is logged as
 * one CALHeartbeat with the metric name, service admin name, operation name,
 * usecase name,  client data center, server data center, and the metric value
 * as heartbeat data.
 *
 * @author wdeng
 */
public class DiffBasedSnapshotCALLogger extends SnapshotLogger implements DiffBasedMetricsStorageProvider{

	private static final String CAL_EVENT_NAME = "SOA_FW_METRIC";
	private static final String CAL_KEY_SERVICE = "svc";
	private static final String CAL_KEY_OP = "op";
	private static final String CAL_KEY_USECASE = "case";
	private static final String CAL_KEY_ROLE = "role";
	private static final String CAL_KEY_CLIENT_CENTER = "clientDC";
	private static final String CAL_KEY_SERVER_CENTER = "serverDC";
	private static final String CAL_KEY_INTERVAL = "interval";


	private SnapshotDiffHelper m_diffHelper;
	private String m_eventName;
	private boolean m_isServer = true;
	private boolean m_isHeartbeatSent = false;

	public DiffBasedSnapshotCALLogger() {
		m_diffHelper = new SnapshotDiffHelper();
	}

	@Override
	public void init(Map<String, String> options, String name, String collectionLocation, Integer snapshotInterval) {
		super.init(options, name, collectionLocation, snapshotInterval);
		m_eventName = CAL_EVENT_NAME;
		if (null != collectionLocation) {
			m_eventName = CAL_EVENT_NAME + "_" + collectionLocation;
			m_isServer = MonitoringSystem.COLLECTION_LOCATION_SERVER.equals(collectionLocation);
		}
	}

	@Override
	protected void epilog(long snapshotTime, Collection<MetricValueAggregator> snapshot) throws ServiceException {
		// noop
	}

	@Override
	protected void prelude(long snapshotTime, Collection<MetricValueAggregator> snapshot) throws ServiceException {
		m_isHeartbeatSent = false;
	}

	@Override
	protected void saveMetricValue(long timeSnapshot, MetricId id,
			MetricClassifier key, MetricValue value) {
		MetricComponentValue[] values = value.getValues();

		if (isAllZeros(values)) {
			// Make sure each snapshot we have at least one heartbeat
			// event generated.
			if (m_isHeartbeatSent) {
				return;
			}
			m_isHeartbeatSent = true;
		}

		String currentTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(timeSnapshot));

		String metricName = id.getMetricName();
		String noOpMetricName = metricName.replace(".Op.", ".");
		CalHeartbeat heartBeat = CalHeartbeatFactory.create(m_eventName, noOpMetricName, "0", currentTime);
		heartBeat.addData(CAL_KEY_SERVICE, id.getAdminName());
		heartBeat.addData(CAL_KEY_OP, id.getOperationName());
		heartBeat.addData(CAL_KEY_USECASE, key.getUseCase());
		heartBeat.addData(CAL_KEY_ROLE, m_isServer ? "1" : "0");
		heartBeat.addData(CAL_KEY_INTERVAL, getSnapshotInterval());
		heartBeat.addData(CAL_KEY_CLIENT_CENTER, key.getSourceDC());
		heartBeat.addData(CAL_KEY_SERVER_CENTER, key.getTargetDC());

		StringBuffer vs = new StringBuffer();
		int i;
		for (i=0; i<values.length; i++) {
			MetricComponentValue v = values[i];
			String name = "value" + i; //v.getName();
			vs.setLength(0);
			Formatter formater = new Formatter(vs);
			formatComponentValue(v, formater);
			heartBeat.addData(name, vs.toString().trim());
		}
		// Just to cover up CAL script that always expects two values.  The side effect
		// is more storage for CAL data.  In case someone complains later.  This is 
		// Requested by the CAL team
/*		if (i == 1) {
			heartBeat.addData("value1", "0");
		}
*/		heartBeat.completed();
	}

	@Override
	protected MetricValueAggregator getAggregator(MetricValueAggregator value) {
		return m_diffHelper.getAggregator(value);
	}

	/**
	 * Informs the SnapshotDiffHelper to record the snapshot.
	 */
	@Override
	protected void finalProcessing(long snapshotTime, Collection<MetricValueAggregator> snapshot) {
		m_diffHelper.recordSnapshot(snapshotTime, snapshot);
	}

	private String getSnapshotInterval() {
		if (m_snapshotInterval != null) {
			long interval = m_snapshotInterval.longValue() / 60;
			return String.valueOf(interval);
		}
		return null;
	}

	/**
	 * If all the compenent values are numbers and are zeros, return true.
	 *
	 * @param values
	 * @return
	 */
	private boolean isAllZeros(MetricComponentValue[] values) {
		for (int i=0; i<values.length; i++) {
			MetricComponentValue v = values[i];
			Object value = v.getValue();
			if (! (value instanceof Number)) {
				return false;
			}
			Number number = (Number)value;
			if (number.intValue() != 0) {
				return false;
			}
			if (number.floatValue() != 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void resetPreviousSnapshot(String adminName) {
		m_diffHelper.resetPreviousSnapshot(adminName);		
	}
}
