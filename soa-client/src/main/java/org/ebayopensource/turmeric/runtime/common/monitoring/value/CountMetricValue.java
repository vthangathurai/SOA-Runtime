/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;

/**
 * This is the metric value class to capture counts  for a given MetricId.
 * 
 * @author wdeng
 *
 */
public class CountMetricValue extends LongSumMetricValue {

	/**
	 * @param id A MetricId.
	 */
	public CountMetricValue(MetricId id) {
		super(id);
	}

	/**
	 * @param id A MetricId.
	 * @param initValue The initial count value in <code>long</code>.
	 */
	public CountMetricValue(MetricId id, long initValue) {
		super(id, initValue);
	}
}
