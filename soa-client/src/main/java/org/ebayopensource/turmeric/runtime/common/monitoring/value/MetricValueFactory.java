/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;

/**
 * This is the factory to create the init MetricValue for a given MetricId. 
 * 
 * @author ichernyshev
 */
public interface MetricValueFactory {

	/**
	 * Creates and returns the init MetricValue for the given MetricId.
	 * 
	 * The class of the result object must always be the same for a given MetricId,
	 * which is neccesary for successful aggregation of values
	 * @param id  The MetricId for whom a MetricValue to be created.
	 * @return A MetricValue for the given MetricId.
	 */
	public MetricValue create(MetricId id);

	/**
	 * @param other Another MetricValueFactory object.
	 * @return true if the other MetricValueFactory is the same as the receiver.
	 */
	public boolean isSame(MetricValueFactory other);
}
