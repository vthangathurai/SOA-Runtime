/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.service;

import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;

/**
 * Defines set of properties overridable by the client application at runtime.
 * 
 * Any values not set here assume their defaults. None of the options are mandatory.
 * 
 * By design, this class does not allow to change the structure of the processing (e.g. pipeline
 * handler configuration).  Processing structure can only be configured using static 
 * configuration, and not on a per-request basis.
 * 
 * @author ichernyshev, smalladi
 */
public class ServiceInvokerOptions {

	private String m_requestBinding;
	private String m_responseBinding;
	private String m_transportName;
	private String m_transportResponseProtocol;
	private String m_messageProtocolName;
	private Integer m_appLevelNumRetries;
	@Deprecated
	private String m_useCase;
	private String m_consumerId;
	private Boolean m_useREST;
	private Integer m_maxURLLengthForREST;
	private Boolean m_shouldRecordResponsePayload;
	private TransportOptions m_transportOptions;
	private String m_urlPathInfo;
	
	/**
	 * The default constructor.
	 */
	public ServiceInvokerOptions() {
		// allow other packages to instantiate
	}

	/**
	 * Gets the request binding (data format).
	 * @return the request binding name; null indicates that the configuration or system default will be
	 * used.
	 */
	public String getRequestBinding() {
		return m_requestBinding;
	}

	/**
	 * Sets the request binding (data format).
	 * @param value the request binding name; null will delegate to the configuration or system default
	 */
	public void setRequestBinding(String value) {
		m_requestBinding = value;
	}

	/**
	 * Gets the response binding (data format).
	 * @return the response binding name; null indicates to use the same binding as the request binding.
	 */
	public String getResponseBinding() {
		return m_responseBinding;
	}

	/**
	 * Sets the response binding (data format).
	 * @param value the response binding name; null indicates to use the same binding as the request binding.
	 */
	public void setResponseBinding(String value) {
		m_responseBinding = value;
	}

	/**
	 * Gets the name of the transport to be used for requests.
	 * @return the transport name; null indicates that the configuration or system default will be
	 * used.
	 */
	public String getTransportName() {
		return m_transportName;
	}

	/**
	 * Sets the transport name to be used for requests.
	 * @param value the transport name; null will delegate to the configuration or system default
	 */
	public void setTransportName(String value) {
		m_transportName = value;
	}

	/**
	 * Gets the name of the transport to be used for responses (by the server).
	 * @return the transport name; null indicates to use the same as the request transport.
	 */
	public String getResponseTransportName() {
		return m_transportResponseProtocol;
	}

	/**
	 * Sets the transport name to be used for responses (by the server).
	 * @param value the transport name; null indicates to use the same as the request transport.
	 */
	public void setResponseTransportName(String value) {
		m_transportResponseProtocol = value;
	}

	/**
	 * Gets the message protocol name.
	 * @return the message protocol name; null indicates that the configuration or system default will be
	 * used.
	 */
	public String getMessageProtocolName() {
		return m_messageProtocolName;
	}

	/**
	 * Sets the message protocol name.
	 * @param value the message protocol name; null will delegate to the configuration or system default.
	 */
	public void setMessageProtocolName(String value) {
		m_messageProtocolName = value;
	}

	/**
	 * Gets a modifiable copy of the transport options.  Any options changed there will take effect on the
	 * next invocation, only if these dynamic changes are supported by the particular transport.
	 * @return the transport option
	 */
	public TransportOptions getTransportOptions() {
		if (m_transportOptions == null) {
			m_transportOptions = new TransportOptions();
		}
		return m_transportOptions;
	}

	/**
	 * Gets the number of application level retries for retryable exceptions and errors.
	 * @return the number of application level retries; null indicates no retry is performed.
	 */
	public Integer getAppLevelNumRetries() {
		return m_appLevelNumRetries;
	}

	/**
	 * Sets the number of application level retries for retryable exceptions and errors.
	 * @param numRetries numRetries the number of application level retries; null indicates no retry is performed.
	 */
	public void setAppLevelNumRetries(Integer numRetries) {
		m_appLevelNumRetries = numRetries;
	}

	/**
	 * Gets the use case that will be sent in X-TURMERIC-USE-CASE header.
	 * @return the use case; null indicates no use case value is sent.
	 * @deprecated
	 */
	@Deprecated
	public String getUseCase() {
		return m_useCase;
	}

	/**
	 * Sets the use case that will be sent in X-TURMERIC-USE-CASE header.
	 * @param useCase the use case; null indicates no use case value is sent.
	 * @deprecated
	 */
	@Deprecated
	public void setUseCase(String useCase) {
		m_useCase = useCase;
	}
	
	/**
	 * Gets the ConsumerId that will be sent in X-TURMERIC-CONSUMER-ID header.
	 * @return the consumer Id, null indicates no consumerId value is sent.
	 */
	public String getConsumerId() {
		return m_consumerId;
	}

	/**
	 * Sets the ConsumerId that will be sent in X-TURMERIC-CONSUMER-ID header.
	 * @param consumerId the consumerId, null indicates no consumerId value is sent.
	 * 
	 */
	public void setConsumerId(String consumerId) {
		m_consumerId = consumerId;
	}

	/**
	 * Returns whether transport should use HTTP GET method instead of POST - (HTTP transports only).
	 * @return whether transport should use HTTP GET method; null indicates no preference is set (used when merging); 
	 * transports should evaluate null as equivalent to false.
	 */
	public Boolean isREST() {
		return m_useREST;
	}

	/**
	 * Sets whether transport should use HTTP GET method instead of POST - (HTTP transports only).
	 * @param isREST whether transport should use HTTP GET method
	 */
	public void setREST(Boolean isREST) {
		m_useREST = isREST;
	}

	/**
	 * Returns the configured maximum length into which to code URL information for an HTTP GET.  The framework
	 * will throw an exception for requests that exceed this length.  Transports will implement some default 
	 * if this value is not available.
	 * @return the maximum URL encoding length
	 */
	public Integer getMaxURLLengthForREST() {
		return m_maxURLLengthForREST;
	}

	/**
	 * Sets the configured maximum length into which to code URL information for an HTTP GET.
	 * @param value the maximum URL encoding length
	 */
	public void setMaxURLLengthForREST(Integer value) {
		m_maxURLLengthForREST = value;
	}

	/**
	 * Get the flag indicating if the payload need to be recorded and available through 
	 * ResponseContext.
	 * @return true if records shall be recorded and false otherwise.
	 */
	public Boolean shouldRecordResponsePayload() {
		return m_shouldRecordResponsePayload;
	}

	/**
	 * Instructs framework to record payload and make it available through ResponseContext.
	 * @param value true if records shall be recorded and false otherwise.
	 */ 
	public void setRecordResponsePayload(Boolean value) {
		m_shouldRecordResponsePayload = value;
	}
	
	/**
	 * Returns the URI Path.
	 * @return the URI path
	 */
	public String getUrlPathInfo() {
		return m_urlPathInfo;
	}

	/**
	 * Sets the URI Path.
	 * @param urlPathInfo  the URI Path
	 */
	public void setUrlPathInfo(String urlPathInfo) {
		m_urlPathInfo = urlPathInfo;
	}
}
