/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;


/**
 * This interface defines a metric collection unit.  It can be a primitive 
 * value or a comples type value
 * 
 * update() method defines the aggregation behaviors of this metric value.
 * 
 * @author wdeng
 */
public interface MetricValue {

	/**
	 * This method defines the metric aggregation behaviors. The 
	 * concrete method should be synchronized to guarantee data 
	 * integrity.
	 * 
	 * @param newValue A given MetricValue.
	 */
	public void update(MetricValue newValue);

	/**
	 * Convinient method to update a metric value of primitive types.
	 * 
	 * @param value An int value to be updated.
	 */
	public void update(int value);
	
	/**
	 * Convinient method to update a metric value of primitive types.
	 * 
	 * @param value A long value to be updated.
	 */
	public void update(long value);
	
	/**
	 * Convinient method to update a metric value of primitive types.
	 * 
	 * @param value A float value to be updated.
	 */
	public void update(float value);

	/**
	 * Convinient method to update a metric value of primitive types.
	 * 
	 * @param value A double value to be updated.
	 */
	public void update(double value);	

	/**
	 * @return the list of java types of all its components.
	 */
	public List<MetricComponentType> getAllComponentsTypes();

	/**
	 * Returns the current value of this MetricValue, 
	 * potentially to be used by the StorageProvider.
	 * 
	 * @return A List of MetricComponentValue.
	 */
	public MetricComponentValue[] getValues();

	/**
	 * @return the service metric id of this metric value
	 */
	public MetricId getMetricId();

	/**
	 * @return true if the MetricValue is read-only.
	 * 
	 * @return
	 */
	public boolean isReadOnly();

	/**
	 * Returns a deep copy of itself but leaving the MetricId as reference.
	 * 
	 * @param isReadOnly True if the MetricValue copy should be readonly.
	 * @return a deep copied MetricValue.  
	 */

	public MetricValue deepCopy(boolean isReadOnly);

	/**
	 * Returns a MetricValue contains the different from the caller against 
	 * the given argument value.  The return value is a readonly value if the 
	 * flag isReadOnly is set to true.
	 *  
	 * @param prevValue A MetricValue to find the diff.
	 * @param isReadOnly True if the generated MetricValue should be set to readonly
	 * @return a MetricValue contains the different from the caller against 
	 * the given argument value.
	 */
	public MetricValue diff(MetricValue prevValue, boolean isReadOnly);

	/**
	 * Returns a MetricValue contains the sum of the caller and the given argument
	 * MetricValue.  The return value is readonly if the isReadOnly flag is set
	 * to true.
	 *
	 * @param other A MetricValue to be added to the receiver.
	 * @param isReadOnly True if the generated MetricValue should be set to readonly
	 * @return a MetricValue representing the sum.
	 */
	public MetricValue add(MetricValue other, boolean isReadOnly);
}
