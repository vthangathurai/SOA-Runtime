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
package org.ebayopensource.turmeric.runtime.tests.spf.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServerUtils;


/**
 * @author wdeng
 */
@SuppressWarnings({"rawtypes", "deprecation"})
public class TestServletRequest implements HttpServletRequest {
	
	private Hashtable<String, String> m_headers = new Hashtable<String, String>();
	private Hashtable<String, String> m_parameters = new Hashtable<String, String>();
	private String m_method = "GET";
	private String m_protocol = HTTPServerUtils.HTTP_1_1;
	private String m_body;
	private String m_qstring;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getAuthType()
	 */
	public String getAuthType() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getAuthType()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	public String getContextPath() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getContextPath()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getCookies()
	 */
	public Cookie[] getCookies() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	public long getDateHeader(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getDateHeader()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String arg0) {
		return m_headers.get(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	public Enumeration getHeaderNames() {
		return m_headers.keys();
	}

	public void addHeader(String name, String value) {
		m_headers.put(name, value);
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
	 */
	public Enumeration getHeaders(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getHeaders()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
	 */
	public int getIntHeader(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getIntHeader()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getMethod()
	 */
	public String getMethod() {
		return m_method;
	}
	
	public void setMethod(String method) {
		m_method = method;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
	 */
	public String getPathInfo() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getPathInfo()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	public String getPathTranslated() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getPathTranslated()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getQueryString()
	 */
	public String getQueryString() {
		return m_qstring;
	}

	public void setQueryString(String qstring) {
		m_qstring = qstring;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	public String getRemoteUser() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getRemoteUser()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
	 */
	public String getRequestURI() {
		return "/ws/spf";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		return new StringBuffer("http://localhost:8080/ws/spf?" + getQueryString());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
	 */
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getRequestedSessionId()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	public String getServletPath() {
		return "ws/spf";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getSession()
	 */
	public HttpSession getSession() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getSession()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	public HttpSession getSession(boolean arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getSession()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getUserPrincipal()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
	 */
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".isRequestedSessionIdFromCookie()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
	 */
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".isRequestedSessionIdFromURL()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
	 */
	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".isRequestedSessionIdFromUrl()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
	 */
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".isRequestedSessionIdValid()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".isUserInRole()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getAttribute()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getAttributeNames()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getCharacterEncoding()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentLength()
	 */
	public int getContentLength() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getContentLength()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentType()
	 */
	public String getContentType() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getContentType()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		return new TestServletInputStream(m_body);
	}
	
	public void setBody(String b) {
		m_body = b;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalAddr()
	 */
	public String getLocalAddr() {
		return "127.0.0.1";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalName()
	 */
	public String getLocalName() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getLocalName()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalPort()
	 */
	public int getLocalPort() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getLocalPort()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getLocale()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	public Enumeration getLocales() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getLocales()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		return m_parameters.get(arg0);
	}
	
	public void addParameter(String name, String value) {
		m_parameters.put(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	public Map getParameterMap() {
		Map<String, String[]> result = new HashMap<String, String[]>();
		for (String key : m_parameters.keySet()) {
			result.put(key, getParameterValues(key));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		return m_parameters.keys();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String arg0) {
		String[] result = new String[1];
		result[0] = m_parameters.get(arg0);
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getProtocol()
	 */
	public String getProtocol() {
		return m_protocol;
	}
	
	public void setProtocol(String p) {
		m_protocol = p;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getReader()
	 */
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getReader()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getRealPath()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemoteAddr()
	 */
	public String getRemoteAddr() {
		return "127.0.0.1";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemoteHost()
	 */
	public String getRemoteHost() {
		return "localhost";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemotePort()
	 */
	public int getRemotePort() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getRemotePort()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getRequestDispatcher()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getScheme()
	 */
	public String getScheme() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".getAuthType()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServerName()
	 */
	public String getServerName() {
		return "spf";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServerPort()
	 */
	public int getServerPort() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#isSecure()
	 */
	public boolean isSecure() {
		throw new UnsupportedOperationException(TestServletRequest.class + ".isSecure()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".removeAttribute()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		throw new UnsupportedOperationException(TestServletRequest.class + ".setAttribute()");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		throw new UnsupportedOperationException(TestServletRequest.class + ".setCharacterEncoding()");
	}

}
