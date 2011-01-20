/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents high-level request information needed before a service can be dispatched, including transport headers and
 * any pseudo-operation parameters that indicate an administrative operation such as ?wsdl.
 * 
 * @author rmurphy
 */
public class RequestMetaContext {
	private final boolean m_isGetMethod;
	private final String m_requestUri;
	private final Map<String, String> m_transportHeaders;
	private final Map<String, String> m_pseudoOpParams;
	private final String m_requiredAdminName;
	private Map<String, String> m_queryParams;
	private String m_urlMatchExpression;

	/**
	 * Constructor.
	 * @param isGetMethod true if this is an HTTP GET query
	 * @param requestUri the raw string of the request URI
	 * @param requiredAdminName specifies the service name that is pre-configured with the servlet, if any.  When this is used, only this
	 * service is considered valid for the servlet, and no service name needs to be supplied.
	 */
	public RequestMetaContext(boolean isGetMethod, String requestUri, String requiredAdminName)
	{
		m_isGetMethod = isGetMethod;
		m_requestUri = requestUri;
		m_transportHeaders = new HashMap<String,String>();
		m_pseudoOpParams = new HashMap<String,String>();
		m_requiredAdminName = requiredAdminName;
	}

	/**
	 * Returns whether this is an HTTP GET query.
	 * @return true if this is an HTTP GET query
	 */
	public boolean isGetMethod() {
		return m_isGetMethod;
	}
	
	/**
	 * Returns the raw string of the request URI.
	 * @return the raw string of the request URI
	 */
	public String getRequestUri() {
		return m_requestUri;
	}

	/**
	 * Returns the map of the transport (e.g. HTTP) headers
	 * @return the transport header map
	 */
	public Map<String, String> getTransportHeaders() {
		return m_transportHeaders;
	}
	
	/**
	 * Returns a map of the pseudo-operation parameters; these are query or header parameters.
	 * used to indicate pseudo-operations, such as ?wsdl
	 * @return the pseudo-operation parameter map
	 */
	public Map<String, String> getPseudoOperationParameters() {
		return m_pseudoOpParams;
	}

	/**
	 * Returns the service name that is pre-configured with the servlet, if any.  When this is used, only this
	 * service is considered valid for the servlet, and no service name needs to be supplied.
	 * @return the pre-configured service name
	 */
	public String getRequiredAdminName() {
		return m_requiredAdminName;
	}

	/**
	 * Returns an ordered map of query params.
	 * @return an ordered map of query params
	 */
	public Map<String, String> getQueryParams() {
		return m_queryParams;
	}
	
	/**
	 * Sets the query parameters. 
	 * @param queryParams NV maps with query parameters.
	 */
	public void setQueryParams(Map<String, String> queryParams) {
		m_queryParams = queryParams;
	}
	
	/**
	 * Returns the url match whose value is an expression in one of the following forms:
	 * 1. path[value]: value is an integer matching one of the / separated parts of the URL path, 
	 * 	  starting at position 0, e.g. http://localhost:8080/ws/spf, path[1] has the value spf.
	 * 2. queryop: Provides the name of the query parameter at the beginning of the query string, 
	 *    e.g. when using ?wsdl, queryop will produce value wsdl.
	 * 3. query[name] name is a string giving the name of a query parameter. The corresponding value 
	 *    will be returned; if there are multiple values, they will be separated by semicolons. 
	 * @return the url match expression
	 */
	public String getUrlMatchExpression() {
		 return m_urlMatchExpression;
	}
	
	/**
	 * Sets the match expression for the URL.
	 * @param urlMatchExpression An URL matching expression.
	 */
	public void setUrlMatchExpression(String urlMatchExpression) {
		m_urlMatchExpression = urlMatchExpression;
	}
}
