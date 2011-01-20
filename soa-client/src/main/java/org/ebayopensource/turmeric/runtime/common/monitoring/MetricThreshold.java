/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;

import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;

/**
 * MetricThreashold interface provides the ability to support threshold value comparison
 * against a metric.  For example, a MetricValue implemented MetricThreadhold interface
 * can be used for monitoring health check.
 * 
 * @author prjande
 */
public interface MetricThreshold {
	/**
	 * This method supports initialization of a comparison value from a string.
	 * @param comparisonVal comparison Value 
	 * @return MetricValue
	 */
	public MetricValue fromString(String comparisonVal);

	/**
	 * Provides inequality operator, returning a negative integer if self is less than other; 0 if equal to other; 
	 * a positive integer if self is greater than other.
	 * 
	 * @param other MetricValue
	 * @return int 
	 */
	public int compare(MetricValue other);
}
