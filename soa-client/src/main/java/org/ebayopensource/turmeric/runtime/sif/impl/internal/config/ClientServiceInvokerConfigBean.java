/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.configuration.ConfigurationException;

public class ClientServiceInvokerConfigBean extends ClientServiceConfigBean {
	private static final long serialVersionUID = 7934173099185743611L;

	public static final String PROP_REQUEST_BINDING = "REQUEST_BINDING";
	public static final String PROP_RESPONSE_BINDING = "RESPONSE_BINDING";
	public static final String PROP_PREFERRED_TRANSPORT_NAME = "PREFERRED_TRANSPORT_NAME";
	public static final String PROP_MESSAGE_PROTOCOL_NAME = "MESSAGE_PROTOCOL_NAME";
	public static final String PROP_APP_LEVEL_NUM_RETRIES = "APP_LEVEL_NUM_RETRIES";
	public static final String PROP_USE_CASE = "USE_CASE";
	public static final String PROP_SERVICE_URL= "SERVICE_URL";
	public static final String PROP_USE_REST = "USE_REST";
	public static final String PROP_MAX_URL_REST_LEN = "PROP_MAX_URL_REST_LEN";

	public static final BeanPropertyInfo REQUEST_BINDING = createBeanPropertyInfo(
			"m_requestBinding", PROP_REQUEST_BINDING, true);

	public static final BeanPropertyInfo RESPONSE_BINDING = createBeanPropertyInfo(
			"m_responseBinding", PROP_RESPONSE_BINDING, true);

	public static final BeanPropertyInfo PREFERRED_TRANSPORT_NAME = createBeanPropertyInfo(
			"m_preferredTransport", PROP_PREFERRED_TRANSPORT_NAME, true);

	public static final BeanPropertyInfo MESSAGE_PROTOCOL_NAME = createBeanPropertyInfo(
			"m_messageProtocolName", PROP_MESSAGE_PROTOCOL_NAME, true);

	public static final BeanPropertyInfo APP_LEVEL_NUM_RETRIES = createBeanPropertyInfo(
			"m_appLevelNumRetries", PROP_APP_LEVEL_NUM_RETRIES, true);

	public static final BeanPropertyInfo USE_CASE = createBeanPropertyInfo(
			"m_useCase", PROP_USE_CASE, true);

	public static final BeanPropertyInfo SERVICE_URL = createBeanPropertyInfo(
			"m_serviceUrl", PROP_SERVICE_URL, true);

	public static final BeanPropertyInfo USE_REST = createBeanPropertyInfo(
			"m_useREST", PROP_USE_REST, true);

	public static final BeanPropertyInfo MAX_URL_REST_LEN = createBeanPropertyInfo(
			"m_maxURLLengthForREST", PROP_MAX_URL_REST_LEN, true);

	// modifiable properties
	private String m_requestBinding;
	private String m_responseBinding;
	private String m_preferredTransport;
	private String m_messageProtocolName;
	private String m_appLevelNumRetries;
	private String m_useCase;
	private String m_serviceUrl;
	private String m_useREST;
	private String m_maxURLLengthForREST;

	ClientServiceInvokerConfigBean(ClientConfigHolder config) throws ConfigCategoryCreateException, ConfigurationException, ServiceException {
		super(config);
		init(config, "Invoker");
	}

	public String getAppLevelNumRetries() {
		return m_appLevelNumRetries;
	}

	public String getRequestBinding() {
		return m_requestBinding;
	}

	public String getResponseBinding() {
		return m_responseBinding;
	}

	public String getTransportName() {
		return m_preferredTransport;
	}

	public String getMessageProtocolName() {
		return m_messageProtocolName;
	}

	public String getUseCase() {
		return m_useCase;
	}

	//TODO: is this needed?
	public String getServiceUrl() {
		return m_serviceUrl;
	}

	public String getUseRest() {
		return m_useREST;
	}

	public String getMaxURLLengthForREST() {
		return m_maxURLLengthForREST;
	}

	//properties
	public void setAppLevelNumRetries(String value) {
		changeProperty(APP_LEVEL_NUM_RETRIES, m_appLevelNumRetries, value);
	}

	public void setRequestBinding(String value) {
		changeProperty(REQUEST_BINDING, m_requestBinding, value);
	}

	public void setResponseBinding(String value) {
		changeProperty(RESPONSE_BINDING, m_responseBinding, value);
	}

	public void setPreferredTransport(String value) {
		changeProperty(PREFERRED_TRANSPORT_NAME, m_preferredTransport, value.toUpperCase());
	}

	public void setMessageProtocolName(String value) {
		changeProperty(MESSAGE_PROTOCOL_NAME, m_messageProtocolName, value);
	}

	public void setUseCase(String value) {
		changeProperty(USE_CASE, m_useCase, value);
	}

	//TODO: is this needed?
	public void setServiceUrl(String value) {
		changeProperty(SERVICE_URL, m_serviceUrl, value);
	}

	public void setUseREST(String value) {
		changeProperty(USE_REST, m_useREST, value);
	}

	public void setMaxURLLengthForREST(String value) {
		changeProperty(USE_CASE, m_maxURLLengthForREST, value);
	}

	@Override
	protected void setDefaultsFromConfig(ClientConfigHolder config) {
		String value = config.getRequestDataBinding();
		if (value != null) {
			m_requestBinding = value;
		}

		value = config.getResponseDataBinding();
		if (value != null) {
			m_responseBinding = value;
		}

		value = config.getMessageProtocol();
		if (value != null) {
			m_messageProtocolName = value;
		}

		Integer iValue = config.getAppLevelNumRetries();
		if (iValue != null) {
			m_appLevelNumRetries = String.valueOf(iValue);
		}

		value = config.getPreferredTransport();
		if (value != null) {
			m_preferredTransport = value;
		}

		value = config.getServiceLocation();
		if (value != null) {
			m_serviceUrl = value;
		}

		value = config.getInvocationUseCase();
		if (value != null) {
			m_useCase = value;
		}

		iValue = config.getMaxURLLengthForREST();
		if (iValue != null) {
			m_maxURLLengthForREST = String.valueOf(iValue);
		}

		Boolean bValue = config.getUseREST();
		if (value != null) {
			m_useREST = String.valueOf(bValue);
		}
	}

	@Override
	protected void updateConfigHolder(ClientConfigHolder config) {
		if (m_requestBinding != null) {
			config.setRequestDataBinding(m_requestBinding);
		}

		if (m_responseBinding != null) {
			config.setResponseDataBinding(m_responseBinding);
		}

		if (m_messageProtocolName != null) {
			config.setMessageProtocol(m_messageProtocolName);
		}

		if (m_appLevelNumRetries != null) {
			config.setAppLevelNumRetries(Integer.valueOf(m_appLevelNumRetries));
		}

		if (m_preferredTransport != null) {
			config.setPreferredTransport(m_preferredTransport);
		}

		if (m_serviceUrl != null) {
			config.setServiceLocation(m_serviceUrl);
		}

		if (m_useCase != null) {
			config.setInvocationUseCase(m_useCase);
		}

		if (m_useREST != null) {
			config.setUseREST(Boolean.valueOf(m_useREST));
		}

		if (m_maxURLLengthForREST != null) {
			config.setMaxURLLengthForREST(Integer.valueOf(m_maxURLLengthForREST));
		}
	}

}
