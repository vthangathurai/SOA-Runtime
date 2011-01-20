/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;


/**
 * Using this class, clients can extract header information from the response.
 * This is the counterpart of the RequestContext.
 * 
 * Every request will create a new instance of this class
 * 
 * @author ichernyshev, smalladi
 */
public final class ResponseContext {
	private Map<String, String> m_transportHeaders;

	private Map<String, Object> m_properties = new HashMap<String, Object>();

	private Collection<ObjectNode> m_messageHeaders;

	private Collection<Object> m_messageHeadersJavaObject;

	private byte[] m_payloadData;

	/**
	 * Constants for PAYLOAD key in the context.
	 */
	public final static String PAYLOAD = "PAYLOAD";

	/**
	 * Constants for MESSAGE_HEADERS key in the context .
	 */
	public final static String MESSAGE_HEADERS = "MESSAGE_HEADERS";

	/**
	 * Constants for MESSAGE_HEADERS_AS_JAVA_OBJECT key in the context.
	 */
	public final static String MESSAGE_HEADERS_AS_JAVA_OBJECT = "MESSAGE_HEADERS_AS_JAVA_OBJECT";

	/**
	 * Package private constructor.
	 */
	ResponseContext() {
		// private instances
	}

	/**
	 * Package private constructor.
	 * 
	 * @param ctx
	 *            the message context of the invocation
	 * @param needPayloadData
	 *            indicates if payload data needs to be set for this context
	 * @throws ServiceException throws if error happens
	 */
	ResponseContext(ClientMessageContext ctx, Boolean needPayloadData)
			throws ServiceException {
		InboundMessage response = (InboundMessage) ctx.getResponseMessage();

		Map<String, String> transportHeaders = response.getTransportHeaders();
		if (transportHeaders != null) {
			m_transportHeaders = Collections.unmodifiableMap(transportHeaders);
		}

		Set<String> responsePropertyNames = ctx.getResponsePropertyNames();
		if (responsePropertyNames != null) {
			for (String name : responsePropertyNames) {
				m_properties.put(name, ctx.getResponseProperty(name));
			}
		}

		Collection<ObjectNode> messageHeaders = response.getMessageHeaders();
		if (messageHeaders != null) {
			m_messageHeaders = Collections
					.unmodifiableCollection(messageHeaders);
		}
		Collection<Object> messageHeadersJavaObj = response
				.getMessageHeadersAsJavaObject();
		if (messageHeadersJavaObj != null) {
			m_messageHeadersJavaObject = Collections
					.unmodifiableCollection(messageHeadersJavaObj);
		}

		if (needPayloadData != null && needPayloadData.booleanValue()) {
			m_payloadData = response.getRecordedData();
		}
	}

	/**
	 * Returns a specified transport header value coming from the server
	 * response.
	 * 
	 * @param name
	 *            the transport header to return
	 * @return the associated header value
	 */
	public String getTransportHeader(String name) {
		if (m_transportHeaders == null || name == null) {
			return null;
		}

		name = SOAHeaders.normalizeName(name, true);
		return m_transportHeaders.get(name);
	}

	/**
	 * Returns a map of all transport headers coming from the server response.
	 * 
	 * @return the map of headers.
	 */
	public Map<String, String> getTransportHeaders() {
		if (m_transportHeaders == null) {
			m_transportHeaders = CollectionUtils.EMPTY_STRING_MAP;
		}

		return m_transportHeaders;
	}

	/**
	 * Returns a collection of all message headers coming from the server
	 * response.
	 * 
	 * @return the collection of headers.
	 */
	public Collection<ObjectNode> getMessageHeaders() {
		if (m_messageHeaders == null) {
			m_messageHeaders = new ArrayList<ObjectNode>();
		}

		return m_messageHeaders;
	}

	/**
	 * Returns a collection of all message headers as Java objects coming from
	 * the server response.
	 * 
	 * @return the collection of headers.
	 */
	public Collection<Object> getMessageHeadersAsJavaObjects() {
		if (m_messageHeadersJavaObject == null) {
			m_messageHeadersJavaObject = new ArrayList<Object>();
		}

		return m_messageHeadersJavaObject;
	}

	/**
	 * Gets the payload data.
	 * @return the payload data in array of bytes
	 */
	public byte[] getPayloadData() {
		return m_payloadData;
	}

	/**
	 * Get the property setting in effect for the current response context.
	 * 
	 * @param name
	 *            the name of the property value to retrieve.
	 * @return the property value, or null if none is set.
	 */
	public Object getProperty(String name) {
		return m_properties.get(name);
	}

	/**
	 * Sets a property applicable to the current response context.
	 * 
	 * @param name
	 *            the property
	 * @param value
	 *            the value of the property
	 */
	public void setProperty(String name, Object value) {
		if (name == null)
			throw new NullPointerException();
		m_properties.put(name, value);
	}

	/**
	 * Package private call to retrieve the entire properties map.
	 * 
	 * @return the header map.
	 */
	Map<String, Object> getPropertiesInternal() {
		return m_properties;
	}

	/**
	 * Gets the context values.
	 * @return a map containing the values of the context
	 */
	Map<String, Object> getContextAsMap() {
		Map<String, Object> result = new HashMap<String, Object>();

		if (m_transportHeaders != null)
			result.putAll(m_transportHeaders);

		if (m_properties != null)
			result.putAll(m_properties);
		if (m_payloadData != null)
			result.put(PAYLOAD, m_payloadData);
		if (m_messageHeaders != null)
			result.put(MESSAGE_HEADERS, m_messageHeaders);
		if (m_messageHeaders != null)
			result.put(MESSAGE_HEADERS_AS_JAVA_OBJECT,
					m_messageHeadersJavaObject);
		return result;
	}
}
