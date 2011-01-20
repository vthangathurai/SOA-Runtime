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

import org.ebayopensource.turmeric.runtime.common.cachepolicy.BaseCachePolicyProvider;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheKey;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyHolder;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.OperationCachePolicy;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.ResponseWrapper;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc.CachableValueAccessor;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


public class MyMessageCachePolicyProvider extends BaseCachePolicyProvider {
	
	Map<CacheKey, ResponseWrapper> m_cache = null;
	protected enum STATUS { UNINITIALIZED, INIT_FAILED, INIT_SUCCESS }
	protected STATUS m_status = STATUS.UNINITIALIZED ;

	protected enum INIT_OPERATION {LOCAL_INIT, EXCEPTION, SUPER_INIT}
	
	static String OP_KEY = "myTestOperation";
	
	static protected INIT_OPERATION s_initOperation = INIT_OPERATION.LOCAL_INIT;
	static protected boolean s_reInit = false;
	/**
	 * This CachePolicyProvider uses another type of request object
	 * , so it needs another CachableValueAccessor 
	 */
	public synchronized void init(ClientServiceDesc serviceDesc, URL serviceURL) throws ServiceException 
	{
		if (s_reInit) {
			invalidate();
			s_reInit = false;
		}
		if (m_status != STATUS.UNINITIALIZED)
			return;
		try {
			if (s_initOperation == INIT_OPERATION.EXCEPTION)
				throw new ServiceException("some");
			super.init(serviceDesc, serviceURL);
			if (s_initOperation == INIT_OPERATION.LOCAL_INIT) {
				super.init(serviceDesc, serviceURL);
				CachePolicyHolder holder = new CachePolicyHolder();
				OperationCachePolicy opPolicy = new OperationCachePolicy();
				opPolicy.setTTL(60);
				holder.getOperationCachePolicies().put(OP_KEY, opPolicy);
				Map<String, CachableValueAccessor> opToValueAccessorMap = new HashMap<String, CachableValueAccessor>();
				opToValueAccessorMap.put(OP_KEY, RuntimeClientCacheTest.buildAccessorCache2(MyMessage.class));
				m_desc = new CachePolicyDesc(holder, opToValueAccessorMap);
			}
			m_cache = new HashMap<CacheKey, ResponseWrapper>();
			m_status = STATUS.INIT_SUCCESS;
		}  catch (ServiceException e) {
			m_status = STATUS.INIT_FAILED;
			throw e;
		}
	}

//	static public boolean isInitException() {
//		return s_initException;
//	}
//
//	static public void setInitException(boolean initException) {
//		s_initException = initException;
//	}
//
	public static boolean isReInit() {
		return s_reInit;
	}

	public static void setReInit(boolean reInit) {
		s_reInit = reInit;
	}

	/**
	 * Example of a cache invalidation (full cleanup) 
	 */
	public synchronized void invalidate() throws ServiceException {
		m_cache = new HashMap<CacheKey, ResponseWrapper>();
		m_status = STATUS.UNINITIALIZED;
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

	public static INIT_OPERATION getInitOperation() {
		return s_initOperation;
	}

	public static void setInitOperation(INIT_OPERATION initOperation) {
		s_initOperation = initOperation;
	}
}
