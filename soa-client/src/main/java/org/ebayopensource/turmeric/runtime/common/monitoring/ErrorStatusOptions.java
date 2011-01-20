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
 * Class for Error Status Config options. Provides the 'Health Check' feature.
 * The health check is based on counting the specified metric within an 
 * observation window of recent time called the collection window. 
 *
 */
public class ErrorStatusOptions {
	
	/**
	 * Name of the metric, should have a valid MetricDef associated 
	 */
	private String m_metric;
	
	/**
	 * The threshold metric value as expressed in a string.
	 */
	private String m_threshold;	
	
	/**
	 * Optional; if supplied, it must be a positive integer.
	 */
	private int m_sampleSize;
	
	/**
	 * Returns the health check metric,  the metric used to measure the health of 
	 * the system.
	 * 
	 * @return the metric used as the health indicator.
	 */
	public String getMetric() {
		return m_metric;
	}
	
	/**
	 * Sets the metric used for system health measurement.
	 * 
	 * @param metric The metric used as the health indicator.
	 */
	public void setMetric(String metric) {
		m_metric = metric;
	}
	
	/**
	 * Returns the threashold value indicating abnormal system health.
	 * 
	 * @return the threashold value indicating abnormal system health.
	 */
	public String getThreshold() {
		return m_threshold;
	}
	
	/**
	 * Sets the threshold value of the health check metric.
	 * 
	 * @param threshold The threshold value of the health check metric.
	 */
	public void setThreshold(String threshold) {
		m_threshold = threshold;
	}
	
	/**
	 * Returns the number of consecutive violations of health metric threshold when the system 
	 * should be mark as abnormal.
	 *  
	 * @return the number of consecutive violations of health metric threshold when the system 
	 * should be mark as abnormal.
	 */
	public int getSampleSize() {
		return m_sampleSize;
	}

	/**
	 * Sets the number of consecutive violations of health metric threshold when the system 
	 * should be mark as abnormal.
	 * 
	 * @param sampleSize  The size of the sample.
	 */
	public void setSampleSize(int sampleSize) {
		m_sampleSize = sampleSize;
	}
}
