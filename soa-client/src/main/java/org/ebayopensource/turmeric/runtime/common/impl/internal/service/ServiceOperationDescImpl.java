/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;


/**
 * Defines attributes for a specific operation
 *
 * @author ichernyshev
 */
public final class ServiceOperationDescImpl implements ServiceOperationDesc {

	//private final ServiceId m_svcId;
	private final String m_name;
	private final String m_methodName;
	private final ServiceOperationParamDesc m_requestType;
	private final ServiceOperationParamDesc m_responseType;
	private final ServiceOperationParamDesc m_errorType;
	private final ServiceOperationParamDesc m_requestHeaders;
	private final ServiceOperationParamDesc m_responseHeaders;
	private final Map<String,Object> m_props;
	private final boolean m_isExisting;
	private final boolean m_isSupported;

	private final ServiceMetricHolder m_metrics;

	// error helper data, used on the client side only
	private Object m_errorHelperData;

	public ServiceOperationDescImpl(ServiceId svcId, String name) {
			if (svcId == null || name == null) {
				throw new NullPointerException();
			}
			m_name = name;
			m_methodName = name;
			m_requestType = null;
			m_responseType = null;
			m_errorType = null;
			m_requestHeaders = null;
			m_responseHeaders = null;
			m_isExisting = true;
			m_isSupported = true;
			m_props = null;

			m_metrics = new ServiceMetricHolder(svcId, name);
		}
	
	public ServiceOperationDescImpl(ServiceId svcId, String name,
		ServiceOperationParamDesc requestType, ServiceOperationParamDesc responseType,
		ServiceOperationParamDesc errorType, ServiceOperationParamDesc requestHeaders, ServiceOperationParamDesc responseHeaders, Map<String,Object> props,
		boolean isExisting, boolean isSupported)
	{
		this(svcId, name, name, requestType, responseType, errorType, requestHeaders, responseHeaders, props,
				isExisting, isSupported);
	}
	public ServiceOperationDescImpl(ServiceId svcId, String name, String methodName,
		ServiceOperationParamDesc requestType, ServiceOperationParamDesc responseType,
		ServiceOperationParamDesc errorType, ServiceOperationParamDesc requestHeaders, ServiceOperationParamDesc responseHeaders, Map<String,Object> props,
		boolean isExisting, boolean isSupported)
	{
		if (svcId == null || name == null || requestType == null ||
			responseType == null || errorType == null)
		{
			throw new NullPointerException();
		}

		List<Class> errorRootJava = errorType.getRootJavaTypes();
		List<DataElementSchema> errorRootElem = errorType.getRootElements();

		if (errorRootJava == null || errorRootJava.size() != 1 ||
			errorRootElem == null || errorRootElem.size() != 1)
		{
			throw new IllegalArgumentException("Error type should contain 1 parameter");
		}

		for (int i=0; i<errorRootJava.size(); i++) {
			if (errorRootJava.get(i) == null) {
				throw new IllegalArgumentException("Root java type cannot be null");
			}
		}

		for (int i=0; i<errorRootElem.size(); i++) {
			if (errorRootElem.get(i) == null) {
				throw new IllegalArgumentException("Root element cannot be null");
			}
		}

		//m_svcId = svcId;
		m_name = name;
		m_methodName = methodName;
		m_requestType = requestType;
		m_responseType = responseType;
		m_errorType = errorType;
		m_requestHeaders = requestHeaders;
		m_responseHeaders = responseHeaders;
		m_isExisting = isExisting;
		m_isSupported = isSupported;

		if (props != null && !props.isEmpty()) {
			m_props = Collections.unmodifiableMap(new HashMap<String,Object>(props));
		} else {
			m_props = null;
		}

		m_metrics = new ServiceMetricHolder(svcId, name);
	}

	public String getName() {
		return m_name;
	}
	
	public String getMethodName() {
		return m_methodName;
	}

	public boolean isExisting() {
		return m_isExisting;
	}

	public boolean isSupported() {
		return m_isSupported;
	}

	public ServiceOperationParamDesc getRequestType() {
		return m_requestType;
	}

	public ServiceOperationParamDesc getResponseType() {
		return m_responseType;
	}

	public ServiceOperationParamDesc getErrorType() {
		return m_errorType;
	}

	public ServiceOperationParamDesc getRequestHeaders() {
		return m_requestHeaders;
	}

	public ServiceOperationParamDesc getResponseHeaders() {
		return m_responseHeaders;
	}

	public Object getProperty(String name) {
		return (m_props != null ? m_props.get(name) : null);
	}

	public Collection<String> getPropertyNames() {
		if (m_props == null) {
			return CollectionUtils.EMPTY_STRING_SET;
		}

		return m_props.keySet();
	}

	public synchronized Object getErrorHelperData() {
		return m_errorHelperData;
	}

	public synchronized void setErrorHelperData(Object data) {
		m_errorHelperData = data;
	}

	/**
	 * Updates a given metric, never throws exceptions
	 */
	public void updateMetric(MessageContext ctx, SystemMetricDefs.OpLevelMetricDef def, long count) {
		if (m_isExisting) {
			m_metrics.update(ctx, def, count);
		}
	}
}
