/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;


/**
 * Interface for the pluggable client side cache providers. The providers implement
 * this interface to provide the cache functionality. When cache is enabled,
 * the framework shall trigger intialization of the cache on the 1st request,
 * and do call lookup() before proceeding to make the service call for processing
 * the client request. If the lookup() returns a non-null object, then it would
 * be returned to the client. Also, the framework shall pass the returned response 
 * object to the cache provider by calling the insert() operation.
 * 
 * A single instance of cache provider is created at the service loading time.
 * @author rpallikonda
 *
 */
public interface CacheProvider { 
	
	/**
	 * init method, invoked during the 1st client request, for cache providers
	 * to lazy init and load the cache policy.
	 * @param serviceDesc Client service desc
	 * @param serviceURL  targer service endpoint for fetching the cache policy
	 *  definition
	 * @throws ServiceException Throws service exception on any init failure
	 *   and returns false on subsequent calls to isCacheEnabled()
	 * 
	 */
	void init(ClientServiceDesc serviceDesc, URL serviceURL) throws ServiceException;
	
	/**
	 * @return True when the provider has caching enabled, 
	 * Framework checks the provider status by calling this operation, and
	 * skips calling lookup/insert operations when returned false.
	 */
	boolean isCacheEnabled();
	
	/**
	 * To lookup for the cached response, if any present.
	 * @param <T>  A service response type.
	 * @param cacheContext The context object for access to the request
	 *    object, as well as to set the response. 
	 * @return null or the cached response object
	 * @exception ServiceException When called on non-cache configured operation,
	 *   or any other errors
	 * @throws ServiceException Exception when lookup fails.
	 */
	<T> T lookup(CacheContext cacheContext) throws ServiceException;
	
	/**
	 * Called to provide the response object for caching.
	 * @param cacheContext context instance with request/response and other info.
	 * @throws ServiceException Exception when lnsertion fails.
	 */
	void insert(CacheContext cacheContext) throws ServiceException;
	
	/**
	 * providers should invalidate the currently cached entries and purge their
	 * cache entries.
	 * @throws ServiceException Exception when invalidate operation  fails.
	 */
	void invalidate() throws ServiceException;
}
