/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

/**
 * Indicates the status of an asynchronous dispatch, including a dispatch id which acts like a correlation id. 
 * @author smalladi
 */
public class DispatchStatus {
	private long m_dispatchID;
	private DispatchType m_type;

	/**
	 * Get the dispatch ID to be associated to this unique instance of asynchronous dispatching.
	 * @return the dispatch ID
	 */
	public long getDispatchID() {
		return m_dispatchID;
	}

	/**
	 * Set the dispatch ID to be associated to this unique instance of asynchronous dispatching.
	 * @param dispatchid the dispatch ID 
	 */
	public void setDispatchID(long dispatchid) {
		m_dispatchID = dispatchid;
	}

	/**
	 * Returns the disptach direction (request or response).
	 * @return the dispatch direction
	 */
	public DispatchType getType() {
		// TODO: remove m_type
		return m_type;
	}

	/**
	 * Sets the dispatch direction.
	 * @param type the dispatch direction
	 */
	public void setType(DispatchType type) {
		m_type = type;
	}
}
