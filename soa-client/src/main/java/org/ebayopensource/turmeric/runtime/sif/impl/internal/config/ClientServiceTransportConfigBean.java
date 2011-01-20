/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransportConfig;

import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;

public class ClientServiceTransportConfigBean extends ClientServiceConfigBean {
	private static final long serialVersionUID = 4675758632317831754L;

	public static final String PROP_HTTP_TRANSPORT_CLASS_NAME = "HTTP_TRANSPORT_CLASS_NAME";
	public static final String PROP_NUM_CONNECT_RETRIES = "NUM_CONNECT_RETRIES";
	public static final String PROP_CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
	public static final String PROP_RECEIVE_TIMEOUT = "RECEIVE_TIMEOUT";
	public static final String PROP_INVOCATION_TIMEOUT = "INVOCATION_TIMEOUT";
	public static final String PROP_SKIP_SERIALIZATION = "SKIP_SERIALIZATION";
	public static final String PROP_USE_DETACHED_LOCAL_BINDING = "USE_DETACHED_LOCAL_BINDING";

	public static final BeanPropertyInfo HTTP_TRANSPORT_CLASS_NAME = createBeanPropertyInfo(
			"m_httpTransportClassName", PROP_HTTP_TRANSPORT_CLASS_NAME, true);

	public static final BeanPropertyInfo NUM_CONNECT_RETRIES = createBeanPropertyInfo(
			"m_numConnectRetries", PROP_NUM_CONNECT_RETRIES, true);

	public static final BeanPropertyInfo CONNECTION_TIMEOUT = createBeanPropertyInfo(
			"m_connectTimeout", PROP_CONNECTION_TIMEOUT, true);

	public static final BeanPropertyInfo RECEIVE_TIMEOUT = createBeanPropertyInfo(
			"m_receiveTimeout", PROP_RECEIVE_TIMEOUT, true);

	public static final BeanPropertyInfo INVOCATION_TIMEOUT = createBeanPropertyInfo(
			"m_invocationTimeout", PROP_INVOCATION_TIMEOUT, true);

	public static final BeanPropertyInfo SKIP_SERIALIZATION = createBeanPropertyInfo(
			"m_skipSerialization", PROP_SKIP_SERIALIZATION, true);

	public static final BeanPropertyInfo USE_DETACHED_LOCAL_BINDING = createBeanPropertyInfo(
			"m_skipSerialization", PROP_USE_DETACHED_LOCAL_BINDING, true);

	// modifiable properties
	private String m_httpTransportClassName;
	private Integer m_numConnectRetries;
	private Integer m_connectTimeout;
	private Integer m_receiveTimeout;
	private Integer m_invocationTimeout;
	private Boolean m_skipSerialization;
	private Boolean m_useDetachedLocalBinding;

	// transportName key
	private String m_transportName;

	ClientServiceTransportConfigBean(ClientConfigHolder config, String transportName)
			throws ConfigCategoryCreateException, ServiceException {
		super(config);
		m_transportName = transportName;
		init(config, "Transport." + transportName);
	}

	public String getHttpTransportClassName() {
		return m_httpTransportClassName;
	}

	public Integer getNumConnectRetries() {
		return m_numConnectRetries;
	}

	public Integer getConnectionTimeout() {
		return m_connectTimeout;
	}

	public Integer getReceiveTimeout() {
		return m_receiveTimeout;
	}

	public Integer getInvocationTimeout() {
		return m_invocationTimeout;
	}

	public Boolean isSkipSerialization() {
		return m_skipSerialization;
	}

	public Boolean isUseDetachedLocalBinding() {
		return m_useDetachedLocalBinding;
	}

	//	properties
	public void SetHttpTransportClassName(String value) {
		changeProperty(HTTP_TRANSPORT_CLASS_NAME, m_httpTransportClassName, value);
	}

	public void setNumConnectRetries(Integer value) {
		changeProperty(NUM_CONNECT_RETRIES, m_numConnectRetries, value);
	}
	public void setConnectionTimeout(Integer value) {
		changeProperty(CONNECTION_TIMEOUT, m_connectTimeout, value);
	}
	public void setReceiveTimeout(Integer value) {
		changeProperty(RECEIVE_TIMEOUT, m_receiveTimeout, value);
	}

	public void setInvocationTimeout(Integer value) {
		changeProperty(INVOCATION_TIMEOUT, m_invocationTimeout, value);
	}

	public void setSkipSerialization(Boolean value) {
		changeProperty(SKIP_SERIALIZATION, m_skipSerialization, value);
	}

	public void setUseDetachedLocalBinding(Boolean value) {
		changeProperty(USE_DETACHED_LOCAL_BINDING, m_useDetachedLocalBinding, value);
	}

	@Override
	protected void setDefaultsFromConfig(ClientConfigHolder config) throws ServiceException {
		Map<String, TransportOptions> transportOptionsMap = config.getMessageProcessorConfig().getTransportOptions();
		TransportOptions transport = transportOptionsMap.get(m_transportName);
		if (transport == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_TRANSPORT_CONFIG,
					ErrorConstants.ERRORDOMAIN, new Object[] { null }));
		}
		String strValue = transport.getHttpTransportClassName();
		if (strValue != null) {
			m_httpTransportClassName = strValue;
		}

		Integer value = transport.getNumConnectRetries();
		if (value != null) {
			m_numConnectRetries = value;
		} else {
			m_numConnectRetries = Integer.valueOf(HTTPClientTransportConfig.DEFAULT_MAX_CONNECT_RETRY);
		}
		value = transport.getConnectTimeout();
		if (value != null) {
			m_connectTimeout = value;
		} else {
			m_connectTimeout = Integer.valueOf(HTTPClientTransportConfig.DEFAULT_HTTP_CONNECTION_TIMEOUT);
		}

		value = transport.getReceiveTimeout();
		if (value != null) {
			m_receiveTimeout = value;
		} else {
			m_receiveTimeout = Integer.valueOf(HTTPClientTransportConfig.DEFAULT_SOCKET_RECV_TIMEOUT);
		}

		value = transport.getInvocationTimeout();
		if (value != null) {
			m_invocationTimeout = value;
		} else {
			m_invocationTimeout = Integer.valueOf(HTTPClientTransportConfig.DEFAULT_INVOCATION_TIMEOUT);
		}

		Boolean bValue = transport.getSkipSerialization();
		if (bValue != null) {
			m_skipSerialization = bValue; 
		}
		
		bValue = transport.isUseDetachedLocalBinding();
		if (bValue != null) {
			m_useDetachedLocalBinding = bValue;
		}

	}

		public void updateConfigHolder(ClientConfigHolder config, String name, String value) throws ServiceException {
			Map<String, TransportOptions> transportOptionsMap = config.getMessageProcessorConfig().getTransportOptions();
			TransportOptions transport = transportOptionsMap.get(m_transportName);
			
			if (transport == null) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_TRANSPORT_CONFIG,
						ErrorConstants.ERRORDOMAIN, new Object[] { name }));
			}
	//		update the transportClasses map too
			Map<String, String> transportClassesMap = config.getMessageProcessorConfig().getTransportClasses();

			if (name.equalsIgnoreCase(PROP_HTTP_TRANSPORT_CLASS_NAME)) {
				transport.setHttpTransportClassName(value);
				transportClassesMap.put(m_transportName, m_httpTransportClassName);
			}
			else if (name.equalsIgnoreCase(PROP_NUM_CONNECT_RETRIES)) {
				transport.setNumConnectRetries(Integer.valueOf(value));
			}
			else if (name.equalsIgnoreCase(PROP_CONNECTION_TIMEOUT)) {
				transport.setConnectTimeout(Integer.valueOf(value));
			}
			else if (name.equalsIgnoreCase(PROP_RECEIVE_TIMEOUT)) {
				transport.setReceiveTimeout(Integer.valueOf(value));
			}
			else if (name.equalsIgnoreCase(PROP_INVOCATION_TIMEOUT)) {
				transport.setInvocationTimeout(Integer.valueOf(value));
			}
			else if (name.equalsIgnoreCase(PROP_SKIP_SERIALIZATION)) {
				transport.setSkipSerialization(Boolean.valueOf(value));
			}
			else if (name.equalsIgnoreCase(PROP_USE_DETACHED_LOCAL_BINDING)) {
				transport.setUseDetachedLocalBinding(Boolean.valueOf(value));
			}
	}

	@Override
	protected void updateConfigHolder(ClientConfigHolder config) {
		Map<String, TransportOptions> transportOptionsMap = config.getMessageProcessorConfig().getTransportOptions();
		TransportOptions transport = transportOptionsMap.get(m_transportName);

		transport.setHttpTransportClassName(m_httpTransportClassName);
		transport.setNumConnectRetries(m_numConnectRetries);
		transport.setConnectTimeout(m_connectTimeout);
		transport.setReceiveTimeout(m_receiveTimeout);
		transport.setInvocationTimeout(m_invocationTimeout);
		transport.setSkipSerialization(m_skipSerialization);
		transport.setUseDetachedLocalBinding(m_useDetachedLocalBinding);

		//update the transportClasses map too
		Map<String, String> transportClassesMap = config.getMessageProcessorConfig().getTransportClasses();
		transportClassesMap.put(m_transportName, m_httpTransportClassName);
	}

}
