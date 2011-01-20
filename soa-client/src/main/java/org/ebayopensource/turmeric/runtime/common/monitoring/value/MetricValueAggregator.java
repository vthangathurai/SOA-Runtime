/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;


/**
 * MetricValueAggregator the interface to collect metric for a given 
 * MetricValue. MetricClassifiers are used to identify data collection
 * point.  The update methods based on the MetricClassifiers to find the
 * right data point to aggregate.
 *  
 * @author smalladi, wdeng, ichernyshev
 */
public interface MetricValueAggregator extends MetricValue {

	/**
	 * Note that NORMAL metric level is always enabled and so does not need to be checked.
	 *
	 * @return True if this metric is enabled.
	 * 
	 */
	public boolean isEnabled();

	/**
	 * @return The MetricCategory for this metric.
	 */
	public MetricCategory getCategory();

	/**
	 * @return The Monitoring level assosiated with its MetricDef.
	 */
	public MonitoringLevel getLevel();

	/**
	 * @return returns the collection of MetricClassifiers for all the data points
	 * currently collected by this aggregator.
	 */
	public Collection<MetricClassifier> getClassifiers();

	/**
	 * @param classifier The MetricClassifier which defines a metric data point
	 * @return the MetricValue of the data point identified by the given classifier.
	 */
	public MetricValue getValue(MetricClassifier classifier);

	/**
	 * Returns the total value of this metric, all classifiers combined.
	 * 
	 * @return null if there is no data
	 */
	public MetricValue getTotalValue();

	/**
	 * Update methods to update the data point identified by the classifier
	 * information in the MessageContext.
	 * 
	 * @param ctx The MessageContext of the request.
	 * @param value The MetricValue to be used to update the aggregated value.
	 */
	public void update(MessageContext ctx, MetricValue value);
	
	/**
	 * Update methods to update the data point identified by the classifier
	 * information in the MessageContext.
	 * 
	 * @param ctx The MessageContext of the request.
	 * @param value The <code>int</code> value to be used to update the aggregated value.
	 */
	public void update(MessageContext ctx, int value);
	
	/**
	 * Update methods to update the data point identified by the classifier
	 * information in the MessageContext.
	 * 
	 * @param ctx The MessageContext of the request.
	 * @param value The <code>long</code> value to be used to update the aggregated value.
	 */
	public void update(MessageContext ctx, long value);
	
	/**
	 * Update methods to update the data point identified by the classifier
	 * information in the MessageContext.
	 * 
	 * @param ctx The MessageContext of the request.
	 * @param value The <code>float</code> value to be used to update the aggregated value.
	 */
	public void update(MessageContext ctx, float value);
	
	/**
	 * Update methods to update the data point identified by the classifier
	 * information in the MessageContext.
	 * 
	 * @param ctx The MessageContext of the request.
	 * @param value The <code>double</code> value to be used to update the aggregated value.
	 */
	public void update(MessageContext ctx, double value);
}
