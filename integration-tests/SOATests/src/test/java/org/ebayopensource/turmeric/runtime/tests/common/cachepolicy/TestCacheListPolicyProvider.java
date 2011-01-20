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
import org.ebayopensource.turmeric.runtime.common.cachepolicy.OperationCachePolicy;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc.CachableValueAccessor;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;


public class TestCacheListPolicyProvider extends TestCachePolicyProvider {
	
	Map<CacheKey, Object> m_cacheList = null;

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
			m_cacheList = new HashMap<CacheKey, Object>();
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
	    	return (T) m_cacheList.get(key);
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
	    	//key.setExpired(cacheContext.getTTL()*1000 + System.currentTimeMillis());
	    	synchronized (m_cacheList) { 
	    		m_cacheList.put(key, cacheContext.getResponse());
	    	}
	    }
	}

	/**
	 * Example of a cache invalidation (full cleanup) 
	 */
	public void invalidate() throws ServiceException {
    	synchronized (m_cacheList) { 
    		m_cacheList = new HashMap<CacheKey, Object>();
    	}
	}

	public Map getCache() {
		return m_cacheList;
	}
}
