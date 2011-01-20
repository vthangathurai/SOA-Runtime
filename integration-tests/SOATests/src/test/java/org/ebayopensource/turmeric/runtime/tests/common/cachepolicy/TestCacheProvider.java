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
package org.ebayopensource.turmeric.runtime.tests.common.cachepolicy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.cachepolicy.BaseCachePolicyProvider;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * @author rpallikonda
 *
 */
public class TestCacheProvider extends BaseCachePolicyProvider {

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.cachepolicy.BaseCachePolicyProvider#invalidate()
	 */
	//@Override
	public void invalidate() {
		// TODO Auto-generated method stub
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
	}

	@Override
	protected InputStream  fetchCachePolicy(ClientServiceId serviceId, URL serviceURL)
			throws ServiceException {
		String xmlbuf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		
			"<serviceCachePolicy  xmlns=\"http://www.ebayopensource.org/turmeric/common/config\" name=\"cachepolicy1\"> " +
				"<operationCachePolicy name=\"myTestOperation\"> " +
					"<TTL>50</TTL>" +
					"<keyExpressionSet>" +
						"<keyExpression>body</keyExpression> " +
						"<keyExpression>subject</keyExpression> " +
					"</keyExpressionSet> " +
				"</operationCachePolicy> " +
			"</serviceCachePolicy>";
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlbuf.getBytes());
		return inputStream;
	}


	//@Override
	public void insert(CacheContext cacheContext) throws ServiceException {
		// TODO Auto-generated method stub
		
	}


	//@Override
	public <T> T lookup(CacheContext cacheContext) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
