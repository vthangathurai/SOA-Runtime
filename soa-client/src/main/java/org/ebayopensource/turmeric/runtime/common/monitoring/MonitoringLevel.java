/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;

/**
 * MetricLogLevel defines the level of the metric in a way similar to the log level 
 * defined in java logging. A metric level can be specified in default global 
 * configuration files, client configuration files, and in service configuration, 
 * to enable the logging of the metrics.
 * 
 * Setting monitoring level to none turns off metric collection for the given service or 
 * for all services if set in the global configuration file.
 * 
 * @author wdeng
 */
public enum MonitoringLevel {
	/**
	 * Monitoring level - normal.
	 */
	NORMAL("normal"), 
	/**
	 * Monitoring level - fine.
	 */
	FINE("fine"), 
	/**
	 * Monitoring level - finest.
	 */
	FINEST("finest");

	private final String m_value;

	private MonitoringLevel(String v) {
		m_value = v;
	}

	/**
	 * 
	 * @return A MonitoringLevel value.
	 */
	public String getValue() {
		return m_value;
	}

	/**
	 * 
	 * @param v Another MonitoringLevel
	 * @return True if the two MonitoringLevels are the same.
	 */
	public static MonitoringLevel fromValue(String v) {
		for (MonitoringLevel c : MonitoringLevel.values()) {
			if (c.m_value.equalsIgnoreCase(v)) {
				return c;
			}
		}

		return null;
	}
}
