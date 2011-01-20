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
 *  Context created for the interface between the SIF and the Cache provider.
 *  The context instance is created per client request by the framework. It is passed
 *  to the configured cache provider for doing lookup/insert operation.
 *  It is a wrapper that stores references to the request/response/cacheKey objects
 *  related to the client request
 */
		
public class CacheContext {
	private String m_opName = null;
	private Object m_request = null;
	private Object m_response = null;
	private CacheKey m_cacheKey = null;
	private long m_TTL = 0;
	
	/**
	 * Returns the operation name.
	 * @return the operation name.
	 */
	public String getOpName() {
		return m_opName;
	}
	
	/**
	 * Sets the operation name.
	 * 
	 * @param opName An operation name
	 * @return <code>this</code>
	 *
	 */
	public CacheContext setOpName(String opName) {
		m_opName = opName;
		return this;
	}
	/**
	 * 
	 * @return The request object
	 */
	public Object getRequest() {
		return m_request;
	}
	
	/**
	 * Updates the context with the request object that is to be looked up in the cache.
	 * @param request  An request Java bean.
	 * @return The CacheContext for the request object.
	 */
	public CacheContext setRequest(Object request) {
		m_request = request;
		return this;
	}
	
	/**
	 * Returns the response Java bean.
	 * @return the response Java bean.
	 */
	public Object getResponse() {
		return m_response;
	}
	
	/**
	 * Updates the context with the response object found in the cache.
	 * @param response  the response Java bean.
	 * @return This CacheContext object.
	 */
	public CacheContext setResponse(Object response) {
		m_response = response;
		return this;
	}
	
	/**
	 * Returns the cache key.
	 * @return the cache key.
	 */
	public CacheKey getCacheKey() {
		return m_cacheKey;
	}
	
	/**
	 * Sets the cache key.
	 * @param cacheKey A cache key.
	 * @return this
	 */
	public CacheContext setCacheKey(CacheKey cacheKey) {
		m_cacheKey = cacheKey;
		return this;
	}
	
	/**
	 * 
	 * @return The configured 'Time to Live' for the operation responses cached
	 */
	public long getTTL() {
		return m_TTL;
	}
	
	/**
	 * 
	 * @param ttl sets the time duration of caching the response objects. 
	 * It is specified in seconds
	 */
	public void setTTL(long ttl) {
		m_TTL = ttl;
	}
}
