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
 * MetricClassifier defines the measure point for Metric.  Metrics are collected per
 * usecase (identifying different calling client application), source data center (where the
 * call is made from), and target data center (the data center the service is hosted)
 * 
 * @author smalladi
 * @author wdeng
 */
public class MetricClassifier {
	private final String m_useCase;
	private final String m_sourceDC;
	private final String m_targetDC;
	private int m_hashCode;
	
	/**
	 * @param usecase  A string to identify the different clients of a service.
	 * @param sourceDC A source data center name.
	 * @param targetDC A target data center name.
	 */
	public MetricClassifier(String usecase, String sourceDC, String targetDC) {
		if (null == usecase || null == sourceDC || null == targetDC) {
			throw new NullPointerException();
		}

		m_useCase = usecase;
		m_sourceDC = sourceDC;
		m_targetDC = targetDC;
	}

	/**
	 * Returns the source data center identifier.
	 * 
	 * @return  the source data center identifier.
	 */
	public String getSourceDC() {
		return m_sourceDC;
	}

	/**
	 * Returns the target data center identifier.

	 * @return  the target data center identifier.
	 */
	public String getTargetDC() {
		return m_targetDC;
	}

	/**
	 * Returns the usecase which identifies a client.
	 * @return  the usecase which identifies a client.
	 */
	public String getUseCase() {
		return m_useCase;
	}

	@Override
	public int hashCode() {
		if (m_hashCode == 0) {
			m_hashCode = m_useCase.hashCode() ^ m_sourceDC.hashCode() ^ m_targetDC.hashCode();
		}

		return m_hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (null == other || !(other instanceof MetricClassifier)) {
			return false;
		}

		MetricClassifier clzfier = (MetricClassifier)other;
		return m_useCase.equals(clzfier.m_useCase) &&
			m_sourceDC.equals(clzfier.m_sourceDC) &&
			m_targetDC.equals(clzfier.m_targetDC);
	}

	@Override
	public String toString() {
		return "MetricClassifier: uc=" + m_useCase 
			+ ", src_dc=" + m_sourceDC + ", tgt_dc=" + m_targetDC;
	}
}
