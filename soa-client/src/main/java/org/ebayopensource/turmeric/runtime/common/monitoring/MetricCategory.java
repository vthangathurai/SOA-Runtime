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
 * 
 * Enum for metric categories. 
 * 
 * @author wdeng
 */
public enum MetricCategory {
	/**
	 * total execution time, comm time, etc.
	 */
	Timing("Timing"),
	/**
	 * the metric is cretical to the health of the service, such as, fault rate,
	 * OOM count, etc.
	 */
	Error("Error"),
	/**
	 * For all other metrics.
	 */
	Other("Other");

	private final String value;

	/**
	 * @param v The string value of the enum.
	 */
	MetricCategory(String v) {
		value = v;
	}

	
	/**
	 * @return the string value of the enum.
	 */
	public String value() {
		return value;
	}

	/**
	 * Returns the MetricCategory value for the given string value.
	 * @param v The string value.
	 * @return A MetricCategory.
	 */
	public static MetricCategory fromString(String v) {
		for (MetricCategory c : MetricCategory.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		return null;
	}
}
