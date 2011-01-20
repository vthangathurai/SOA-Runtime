/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;


public class SOAServerTransportRequest implements ISOATransportRequest {
	
	HttpServletRequest m_request = null;
	 
	private SOAServerTransportRequest (HttpServletRequest request) {
		m_request = request;
	}
	
	public static ISOATransportRequest createRequest (HttpServletRequest request) {
		return new SOAServerTransportRequest(request);
	}
	
	
	public Cookie[] retrieveCookies() {
		Cookie[] soaCookies = null;
		javax.servlet.http.Cookie[] httpCookies = m_request.getCookies();
		if (httpCookies != null) {
			soaCookies = new Cookie[httpCookies.length];
			for (int i=0; i<httpCookies.length; i++) {
				javax.servlet.http.Cookie httpCookie = httpCookies[i];

				String name = httpCookie.getName();
				String value = httpCookie.getValue();
				soaCookies[i] = new Cookie(name, value);
			}
		}
		return soaCookies;
	}

	public InputStream getInputStream() throws IOException{
		return m_request.getInputStream();
	}

	public String getLocalAddr() {
		return m_request.getLocalAddr();
	}

	public String getProtocol() {
		return m_request.getProtocol();
	}

	public String getRemoteAddr() {
		return m_request.getRemoteAddr();
	}

	public String getRemoteHost() {
		return m_request.getRemoteHost();
	}

	public int getServerPort() {
		return m_request.getServerPort();
	}

	public String getServerName() {
		return m_request.getServerName();
	}

	public String getMethod() {
		return m_request.getMethod();
	}

	public String getRequestURI() throws ServiceException {
		return m_request.getRequestURI();
	}

	public Map<String, String> getHeaderNames() throws ServiceException {
		Map<String, String> headers = new HashMap<String, String> ();
		Enumeration reqHeaders = m_request.getHeaderNames();
		while (reqHeaders.hasMoreElements()) {
			String name = (String)reqHeaders.nextElement();
			name = SOAHeaders.normalizeName(name, true);
			String value = m_request.getHeader(name);
			headers.put(name, value);
		}
		return headers;
	}

	@Override
	public String getQueryString() {
		return m_request.getQueryString();
	}
	
	public ServiceAddress getServiceAddress() throws ServiceException
	{
		String serviceHostName = m_request.getServerName();
		String serviceIpAddr = m_request.getLocalAddr();

		URL serviceURL;
		try {
			serviceURL = new URL("http", serviceHostName, getRequestURI());
		} catch (MalformedURLException e) {
			// TODO: should we log a warning here?
			serviceURL = null;
		}

		ServiceAddress serviceAddress = new ServiceAddress(
			serviceHostName, serviceIpAddr, serviceURL, false);

		return serviceAddress;
	}

	public ServiceAddress getClientAddress() throws ServiceException
	{
		String clientHostName = m_request.getRemoteHost();
		String clientIp = m_request.getRemoteAddr();

		ServiceAddress serviceAddress = new ServiceAddress(
			clientHostName, clientIp, null, false);

		return serviceAddress;
	}
	
	public Object getUnderlyingObject() {
		return m_request;
	}

}
