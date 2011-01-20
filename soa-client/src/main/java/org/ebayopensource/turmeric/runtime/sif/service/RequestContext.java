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
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ServiceCallHelper;


/**
 * Using RequestContext, a client can set arbitrary header elements in the
 * request message before it is sent.
 * 
 * Settings made into this class apply to just one message (the next request
 * message), and are then cleared out. Examples include a cookie coming in from
 * the user.
 * 
 * Refer to <code>Service.setSessionTransportHeader()</code> for setting of
 * session level headers, i.e. those which should be sent until later changed
 * within the Service object.
 * 
 * The headers in this class, the session level headers, and automatic framework
 * headers are all combined for submission into the message context and
 * eventually the outbound transport.
 * 
 * Setting cookies is provided as an additional convenience method. This is an
 * alternative to setting headers in handlers via
 * <code>MessageContext.getRequestMessage().setTransportHeader()</code>
 * 
 * @author ichernyshev, smalladi
 */
public final class RequestContext {
	private Map<String, String> m_transportHeaders;
	private Map<String, Cookie> m_cookies;
	private Map<String, Object> m_properties = new HashMap<String, Object>();
	private Collection<ObjectNode> m_messageHeaders;

	/**
	 * Sets a transport header applicable to the next service invocation only.
	 * 
	 * @param name
	 *            the header name
	 * @param value
	 *            the value of the header
	 */
	public void setTransportHeader(String name, String value) {
		if (name == null) {
			throw new NullPointerException();
		}

		name = SOAHeaders.normalizeName(name, false);
		value = SOAHeaders.normalizeValue(name, value);

		if (m_transportHeaders == null) {
			m_transportHeaders = new HashMap<String, String>();
		}
		m_transportHeaders.put(name, value);
	}

	/**
	 * Get the transport header setting in effect for the next service
	 * invoation.
	 * 
	 * @param name
	 *            the name of the header value to retrieve.
	 * @return the header value, or null if none is set.
	 */
	public String getTransportHeader(String name) {
		if (m_transportHeaders == null || name == null) {
			return null;
		}

		name = SOAHeaders.normalizeName(name, false);
		return m_transportHeaders.get(name);
	}

	/**
	 * Package private call to retrieve the entire transport header map.
	 * 
	 * @return the header map.
	 */
	Map<String, String> getTransportHeadersInternal() {
		return m_transportHeaders;
	}

	/**
	 * Adds message header to message as Object Node.
	 * 
	 * @param objectNode
	 *            header object node to set
	 * @throws ServiceException
	 */
	public void addMessageHeader(ObjectNode objectNode) {
		if (objectNode == null) {
			return;
		}
		if (m_messageHeaders == null) {
			m_messageHeaders = new ArrayList<ObjectNode>();
		}
		m_messageHeaders.add(objectNode);
	}

	/**
	 * Adds message header to message as an Java object.
	 * 
	 * @param headerJavaObject
	 *            header object node to set
	 * @throws ServiceException
	 */
	public void addMessageHeaderAsJavaObject(Object headerJavaObject) {
		if (headerJavaObject == null) {
			return;
		}
		if (m_messageHeaders == null) {
			m_messageHeaders = new ArrayList<ObjectNode>();
		}
		m_messageHeaders.add(new JavaObjectNodeImpl(null, headerJavaObject));
	}

	/**
	 * Packaage private call to retrieve the entire message header array.
	 * 
	 * @return message header array
	 */
	Collection<ObjectNode> getMessageHeadersInternal() {
		return m_messageHeaders;
	}

	/**
	 * Package private call to retrieve the entire cookie map.
	 * 
	 * @return the cookie map.
	 */
	Map<String, Cookie> getCookiesInternal() {
		return m_cookies;
	}

	/**
	 * Sets a cookie to be passed on the next service invocation only.
	 * 
	 * @param cookie
	 *            the cookie value
	 */
	public void setCookie(Cookie cookie) {
		if (m_cookies == null) {
			m_cookies = new HashMap<String, Cookie>();
		}

		m_cookies.put(cookie.getName(), cookie);
	}

	/**
	 * Get the cookie value by cookie name that is in effect for the next
	 * service invoation, if any is set with this name.
	 * 
	 * @param name
	 *            the name of the cookie to retrieve.
	 * @return the cookie, or null if none is set.
	 */
	public Cookie getCookie(String name) {
		if (name != null && m_cookies != null) {
			return m_cookies.get(name.toUpperCase());
		}

		return null;
	}

	/**
	 * Returns the current array of cookies that are set for the next service
	 * invocation.
	 * 
	 * @return the cookie array.
	 */
	public Cookie[] getCookies() {
		if (m_cookies == null) {
			return ServiceCallHelper.EMPTY_COOKIES;
		}

		Cookie[] result = m_cookies.values().toArray(
				new Cookie[m_cookies.size()]);
		return result;
	}

	/**
	 * Get the property setting in effect for the current request context.
	 * 
	 * @param name
	 *            the name of the property value to retrieve.
	 * @return the property value, or null if none is set.
	 */
	public Object getProperty(String name) {
		return m_properties.get(name);
	}

	/**
	 * Sets a property applicable to the current request context.
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
	 * @return the properties map.
	 */
	Map<String, Object> getPropertiesInternal() {
		return m_properties;
	}

	/**
	 * Gets the context values.
	 * @return a map containing the values of the context
	 */
	Map<String, Object> getContextAsMap() {
		Map<String, Object> result = new HashMap<String,Object>();

		if(m_transportHeaders != null)
			result.putAll(m_transportHeaders);
		
		if(m_cookies != null)
			result.putAll(m_cookies);
		
		if(m_properties != null)
			result.putAll(m_properties);
	
		return result; 
	}
}
