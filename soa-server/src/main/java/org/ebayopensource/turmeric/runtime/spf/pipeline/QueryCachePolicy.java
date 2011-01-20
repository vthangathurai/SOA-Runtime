/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;

/**
 * Implements the getCachePolicy operation.  This class is shared across threads and does not contain any state information.
 * @author rpallikonda
 */
public class QueryCachePolicy {


	private static InputStream getDefaultCachePolicy() {
		String xmlbuf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<serviceCachePolicy xmlns=\"http://www.ebayopensource.org/turmeric/common/config\" name=\"test1\"> " +
		"</serviceCachePolicy>";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlbuf.getBytes());
		return inputStream;
	}
	
	private static void copy(InputStream input, StringBuilder output)
	throws IOException {
		byte[] buf = new byte[8192];
		int numRead = 0;
		while ((numRead = input.read(buf)) != -1) {
			output.append(new String(buf, 0, numRead));
		}
	}
	
	/**
	 * Returns the CachePolicy from the given ServiceDesc.
	 * 
	 * @param serviceDesc  an ServiceDesc.
	 * @return an String representing the cache policy.
	 * @throws ServiceException Exception when failed to get the policy.
	 */
	public String getCachePolicy(ServerServiceDesc serviceDesc ) throws ServiceException {
		
		StringBuilder output = new StringBuilder();
		String serviceAdminName = serviceDesc.getAdminName();
		
		InputStream cachePolicyData = null;
		try {
			cachePolicyData = ServiceConfigManager.getInstance().getCachePolicy(serviceAdminName);
			if (cachePolicyData == null) {
				cachePolicyData = getDefaultCachePolicy();
			     throw new ServiceException(
			    	ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_NO_CACHE_POLICY, 
			    			ErrorConstants.ERRORDOMAIN, new Object[] {serviceAdminName}));
				   
			}
			
			copy(cachePolicyData, output);
		} catch (IOException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {serviceAdminName, e.toString()}), e);
		}
		finally {
			if (cachePolicyData != null)
				try {
					cachePolicyData.close();
				} catch (IOException e) {
					// Ignoring it
				}
		}
		return output.toString();
	}
}
