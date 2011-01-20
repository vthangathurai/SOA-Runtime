/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

/**
 * 
 * @author gyue, wdeng
 *
 */
public class ResponseWrapper {

	private long m_expired = 0;
	private Object m_response = null;
	
	/**
	 * 
	 * @param response  An Java bean representing a service response.
	 * @param expired A <code>long</code> value representing expired time.
	 */
	public ResponseWrapper(Object response, long expired) {
		m_expired = expired;
		m_response = response;
	}
	
	/**
	 * 
	 * @return  the expiration time.
	 */
	public long getExpired() {
		return m_expired;
	}
	
	/**
	 * Sets the expiration time.
	 * @param expired the expiration time.
	 */
	public void setExpired(long expired) {
		m_expired = expired;
	}
	
	/**
	 * @return A Java bean representing the response.
	 */
	public Object getResponse() {
		return m_response;
	}
}
