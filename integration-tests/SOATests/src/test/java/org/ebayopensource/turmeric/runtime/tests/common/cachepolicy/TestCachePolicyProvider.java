/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.cachepolicy;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheKey;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyHolder;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheProvider;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.OperationCachePolicy;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.ResponseWrapper;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc.CachableValueAccessor;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;


public class TestCachePolicyProvider implements CacheProvider {
	
	Map<CacheKey, ResponseWrapper> m_cache = null;
	static String OP_KEY = "opKey";		
	protected CachePolicyDesc m_desc;
	protected enum STATUS { UNINITIALIZED, INIT_FAILED, INIT_SUCCESS }
	protected STATUS m_status = STATUS.UNINITIALIZED ;

	/**
	 * Create simplified CachePolicyDesc, CachePolicyHolder, OperationCachePolicy
	 */
	public synchronized void init(ClientServiceDesc serviceDesc, URL serviceURL) throws ServiceException
	{
		if (m_status != STATUS.UNINITIALIZED)
			return;
		try {
			CachePolicyHolder holder = new CachePolicyHolder();
			OperationCachePolicy opPolicy = new OperationCachePolicy();
			opPolicy.setTTL(60);
			holder.getOperationCachePolicies().put(OP_KEY, opPolicy);
			Map<String, CachableValueAccessor> opToValueAccessorMap = new HashMap<String, CachableValueAccessor>();
			opToValueAccessorMap.put(OP_KEY, RuntimeClientCacheTest.buildAccessorCache(TestEchoObjectRequest.class));
			m_desc = new CachePolicyDesc(holder, opToValueAccessorMap);
			m_cache = new HashMap<CacheKey, ResponseWrapper>();
			m_status = STATUS.INIT_SUCCESS;
		}  catch (ServiceException e) {
			m_status = STATUS.INIT_FAILED;
			throw e;
		}
	}

	/**
	 * Example of a lookup operation, which can send in CacheContext a 
	 * generated CacheKey for further usage in "insert"  
	 */
	public <T> T lookup(CacheContext cacheContext) throws ServiceException {
		CacheKey key = m_desc.generateCacheKey(cacheContext);
	    if (key != null) {
			cacheContext.setCacheKey(key);
			ResponseWrapper wrapper = m_cache.get(key);
			if (wrapper != null)
				return (T) wrapper.getResponse();
			else
		    	return null;
	    } else {
	    	return null;
	    }
	}

	/**
	 * Example of an insert operation
	 */
	public void insert(CacheContext cacheContext) throws ServiceException {
		CacheKey key = cacheContext.getCacheKey(); 
		if (key == null)
			key = m_desc.generateCacheKey(cacheContext);
	    if (key != null) {
	    	synchronized (m_cache) { 
	    		m_cache.put(key, new ResponseWrapper(cacheContext.getResponse(), cacheContext.getTTL()*1000 + System.currentTimeMillis()));
	    	}
	    }
	}

	/**
	 * Example of a cache invalidation (full cleanup) 
	 */
	public synchronized void invalidate() throws ServiceException {
		m_cache = new HashMap<CacheKey, ResponseWrapper>();
		m_status = STATUS.UNINITIALIZED;
	}

	/**
	 * Method handles the indication, if CacheProvider can be actually used
	 */
	public final boolean isCacheEnabled() {
		return m_status == STATUS.INIT_SUCCESS;
	}

	public Map<CacheKey, ResponseWrapper> getCache() {
		return m_cache;
	}

}
