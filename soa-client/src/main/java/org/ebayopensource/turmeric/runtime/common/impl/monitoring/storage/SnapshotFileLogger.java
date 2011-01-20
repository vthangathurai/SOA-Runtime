/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentType;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * This class provides a default metrics snapshot logger to log aggregated
 * metrics to a log file. Each metric value in a snapshot is logged as one line
 * in the file with the metric name, service admin name, operation name, usecase
 * name, client data center, server data center, and the metric value. These
 * fields are delimited. The name of the log file and the delimiter can be
 * customized using <storage-option> when configuring storage provider. For
 * example,
 * 
 * <storage-provider name="DiffBasedFileLogger">
 * <class-name>org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.DSnapshotFileLogger</class-name>
 * <storage-options> <option name="filename">MyMetrics.log</option> <option
 * name="delimiter">;</option> </storage-options> </storage-provider>
 * 
 * By default the file name is SOAMetrics.log and the delimiter is ','.
 * 
 * @author wdeng
 */
public class SnapshotFileLogger extends SnapshotLogger implements
		MetricsStorageProvider {
    private static final Logger LOG = Logger.getLogger(SnapshotFileLogger.class.getName());

	public final static String PROP_FILENAME = "filename";

	public final static String PROP_DELIMITER = "delimiter";

	private final static String DEFAULT_LOG_FILE = "SOAMetrics.log";

	private final static String DEFAULT_DELIMITER = ",";

	private String m_delimiter = DEFAULT_DELIMITER;

	private File m_logFile;

	private Formatter m_formatter;

	private PrintStream m_printStream;

	private Map<String, String> s_filenames = new HashMap<String, String>();
	
	public SnapshotFileLogger() {
		File logDir = new File(System.getProperty("org.ebayopensource.turmeric.log.dir", "."));
		m_logFile = new File(logDir, DEFAULT_LOG_FILE);
	}

	@Override
	public void init(Map<String, String> options, String name, String collectionLocation, Integer snapshotInterval) {
		super.init(options, name, collectionLocation, snapshotInterval);
		if (null == options || options.size() < 1) {
			return;
		}
		String filename = options.get(PROP_FILENAME);
		if (null != filename && filename.length() > 0) {
			m_logFile = new File(filename);
			if(!m_logFile.isAbsolute() && !filename.contains(File.separator)) {
				File logDir = new File(System.getProperty("org.ebayopensource.turmeric.log.dir", "."));
				m_logFile = new File(logDir, filename);
			}
		}

		String delimiter = options.get(PROP_DELIMITER);
		if (null != delimiter && delimiter.length() > 0) {
			m_delimiter = delimiter;
		}
		writeHeader(name, collectionLocation);
	}

	@Override
	protected void epilog(long snapshotTime,
			Collection<MetricValueAggregator> snapshot) throws ServiceException {
		m_printStream.close();
	}

	@Override
	protected void prelude(long snapshotTime,
			Collection<MetricValueAggregator> snapshot) throws ServiceException {
		try {
			OutputStream os = new FileOutputStream(m_logFile, true);
			m_printStream = new PrintStream(os);
			m_formatter = new Formatter(m_printStream, Locale.US);
		} catch (FileNotFoundException e) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_CANNOT_WRITE_FILE,
					ErrorConstants.ERRORDOMAIN, new Object[] { m_logFile.getAbsolutePath() }), e);
		}
	}

	@Override
	protected void saveMetricValue(long timeSnapshot, MetricId id,
			MetricClassifier key, MetricValue value) {
		saveMetricValue(timeSnapshot, id, key, value, m_formatter, m_delimiter);
	}

	protected static void saveMetricValue(long timeSnapshot, MetricId id,
			MetricClassifier key, MetricValue value, Formatter formatter,
			String delimiter) {
		formatter.format(
				"%8$tD %8$tT%1$s%2$s%1$s%3$s%1$s%4$s%1$s%5$s%1$s%6$s%1$s%7$s",
				delimiter, id.getAdminName(), getOperationName(id), id
						.getMetricName(), key.getUseCase(), key.getSourceDC(),
				key.getTargetDC(), new Date(timeSnapshot));
		List<MetricComponentType> componentTypes = value
				.getAllComponentsTypes();
		MetricComponentValue[] values = value.getValues();
		int remaining = values.length;
		for (MetricComponentType type : componentTypes) {
			if (remaining == 0) {
				break;
			}
			formatter.format(delimiter);
			for (int i = 0; i < values.length; i++) {
				MetricComponentValue v = values[i];
				if (type.getName().equals(v.getName())) {
					formatComponentValue(v, formatter);
					remaining--;
					break;
				}
			}
		}
		formatter.format("\n");
	}

	private static String getOperationName(MetricId id) {
		String operationName = id.getOperationName();
		if (operationName == null) {
			String metricName = id.getMetricName();
			if (metricName != null
					&& metricName.toUpperCase().contains(".TOTAL")) {
				operationName = "ALL";
			} else {
				operationName = "N/A";
			}
		}
		return operationName;
	}

	private void writeHeader(String provider, String collectionLocation) {
		synchronized(s_filenames) {
			String lookup = s_filenames.get(provider);
			if (lookup == null) {
				if (!m_logFile.exists()) {
					try {
						OutputStream os = new FileOutputStream(m_logFile, true);
						PrintStream ps = new PrintStream(os);
						ps.printf("Provider Type=%s, Collection Location=%s, IP=%s, TID=%s\n",
										provider, collectionLocation, getIP(), getID());
						s_filenames.put(provider, provider);
						ps.close();
					} catch (FileNotFoundException e) {
						LOG.log(Level.WARNING, "Unable to write header", e);
					}
				}
			}
		}
	}

	private String getIP() {
		String result = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			byte[] ipAddr = addr.getAddress();

			for (int i = 0; i < ipAddr.length; i++) {
				if (i > 0) {
					result += ".";
				}
				result += ipAddr[i] & 0xFF;
			}
		} catch (UnknownHostException e) {
            LOG.log(Level.WARNING, "Unknown host for local host?", e);
		}

		return result;
	}

	private String getID() {
		return String.valueOf(Thread.currentThread().getId());
	}

}
