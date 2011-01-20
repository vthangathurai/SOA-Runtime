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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;

/**
 * This class provides the initialization/loading of the cache policy on the 
 * client side. 
 * 
 * @author rpallikonda
 *
 */
public abstract class BaseCachePolicyProvider implements CacheProvider {

	/**
	 *  Flag to capture the initialization status
	 */
	
	private enum STATUS { UNINITIALIZED, INIT_FAILED, INIT_SUCCESS }
	private STATUS m_status = STATUS.UNINITIALIZED ;
	/**
	 * An object to hold CachePolicy meta data for a service. 
	 */
	protected CachePolicyDesc m_desc;
	
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.service.CacheProvider#init(ClientServiceDesc serviceDesc)
	 */
	/**
	 * init method. 
	 * It does the following:
	 * 1. Fetches the cache policy definition from the server side
	 * 2. Loads the policy
	 * 3. Creates the cache policy desc
	 *
	 * @param serviceDesc client service desc for the service
	 * @param serviceURL  service end-point (optional)
	 * @throws ServiceException Exception when initialization fails.
	 */
	public synchronized void init(ClientServiceDesc serviceDesc, URL serviceURL) throws ServiceException {
		
		if (m_status != STATUS.UNINITIALIZED)
			return;

		Preconditions.checkNotNull(serviceDesc);
		
		InputStream in = null;
		
		if (serviceURL == null ) { 
			serviceURL = serviceDesc.getDefServiceLocationURL();
		}
		try {
			in = fetchCachePolicy(serviceDesc.getServiceId(), serviceURL); 
			throwErrorOnNull(in);
			
			CachePolicyHolder holder = CachePolicyHolder.loadCachePolicy(in, serviceURL.toString(), SOAConstants.CACHE_POLICY_SCHEMA);
			throwErrorOnNull(holder);
			
			Collection<ServiceOperationDesc> operationServiceDescs = serviceDesc.getAllOperations();
			Map<String, ServiceOperationParamDesc> opReqTypeMap = createOpRequestTypeDesc(operationServiceDescs);
			m_desc = CachePolicyDesc.create(holder, opReqTypeMap);
			throwErrorOnNull(m_desc);
			
			m_status = STATUS.INIT_SUCCESS;
		}  catch (ServiceException e) {
			m_status = STATUS.INIT_FAILED;
			throw e;
		}
		finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
					// Ignoring it
				}
		}
	}
	
	/**
	 * Helper method
	 * @param in
	 * @throws ServiceException
	 */
	private void throwErrorOnNull(Object in) throws ServiceException {
		if (in == null) {
			m_status = STATUS.INIT_FAILED;
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_CACHE_POLICY_INIT_FAILED, ErrorConstants.ERRORDOMAIN));
		}
	}
	
	/**
	 *  Utility method to create the operation name -> request type mapping based on the
	 *  service operation desc information
	 * @param operationServiceDescs : service operation descs
	 * @return Map of operation name and operation request type
	 */
	private Map<String, ServiceOperationParamDesc> createOpRequestTypeDesc(
			Collection<ServiceOperationDesc> operationServiceDescs) {
		Map<String, ServiceOperationParamDesc> opReqTypeMap = new HashMap<String, ServiceOperationParamDesc>();
		for(ServiceOperationDesc opDesc: operationServiceDescs) {
			opReqTypeMap.put(opDesc.getName(), opDesc.getRequestType());
		}
		return opReqTypeMap;
	}

	/**
	 * 
	 * Fetch the CachePolicy from the server, it is marked as protected for testability.
	 *
	 * @param serviceId Service Id  
	 * @param serviceURL Servicew end point.
	 * 
	 * @return A InputStream to read the CachePolicy.
	 * @throws ServiceException Exception when initialization fails.
	 */	
	protected InputStream fetchCachePolicy(ClientServiceId serviceId, URL serviceURL) throws ServiceException {
		
		Preconditions.checkNotNull(serviceId);
		Preconditions.checkNotNull(serviceURL);
				
		Service service = ServiceFactory.create(serviceId.getAdminName(), serviceId.getEnvName(), serviceId.getClientName(), serviceURL);
		List<Object> outParams = new ArrayList<Object>();
		
		service.invoke(SOAConstants.OP_GET_CACHE_POLICY, null, outParams);
		
		String cachePolicyStr = outParams.size() > 0 ? (String)outParams.get(0) : null;
		throwErrorOnNull(cachePolicyStr);
		
		InputStream in = convertToInputStream(cachePolicyStr);
		return in;
		
	}

	private InputStream convertToInputStream(String cachePolicyStr) {
		ByteArrayInputStream stream = new ByteArrayInputStream(cachePolicyStr.getBytes());
		return stream;
	}

	/*
	 * @see CacheProvider#isCacheEnabled()
	 * 
	 */
	@Override
	public final boolean isCacheEnabled() {
	    /* ignore */
		return m_status == STATUS.INIT_SUCCESS;
	}
}
