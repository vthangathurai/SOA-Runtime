/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.cachepolicy;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;

/**
 * 
 * This is the default cache provider that the framework defaults to, when no cache provider
 * is configured. The isCacheEnabled() is coded to return false, thus calls to the lookup/insert
 * should not happen.
 * 
 *
 */
public class DefaultCacheProvider implements CacheProvider  { 

	public synchronized void init(ClientServiceDesc serviceDesc, URL serviceURL) {
		// empty implementation
	} 

	//@Override
	public void insert(CacheContext cacheContext) throws ServiceException {
		throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
				ErrorConstants.ERRORDOMAIN)); 
	}

	//@Override
	public void invalidate() throws ServiceException {
		throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
				ErrorConstants.ERRORDOMAIN));
	}

	//@Override
	public <T> T lookup(CacheContext cacheContext) throws ServiceException {
		throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
				ErrorConstants.ERRORDOMAIN));
	}

	public  boolean isCacheEnabled() {
		return false;
	}
}
