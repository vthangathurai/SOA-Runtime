/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.utils.ThreadPoolConfig;
import org.ebayopensource.turmeric.runtime.common.utils.ThreadPoolFactory;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.RawDataServiceDispatch;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.RawDispatchData;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ServicePoller;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.TypedDataServiceDispatch;


/**
 * This is the interface through which clients can get access to a specific
 * service. Clients are expected to first create an instance of a service using
 * the static create method (ServiceFactory.create()). Then either the
 * getProxy() method can be used to get a proxy object for the interface using a
 * specific binding or simply an invoke() method can be called on Service, to
 * dynamically invoke a service (referred to as dynamic invocation interface,
 * DII).
 * <P>
 * Clients can set various invocation options prior to invoking the service, by
 * calling getInvokerOptions() and setting values on that structure. Most of
 * these options can also be specified in configuration. For any values
 * specified in both configuration and ServiceInvokerOptions, the
 * ServiceInvokerOptions values take precedence.
 * <P>
 * If clients want to send or retrieve certain header elements, they can do so,
 * using the request/response contexts available (getRequestContext(),
 * getResponseContext()).
 * <P>
 * A Service instance is not thread safe. If a client creates multiple threads
 * to invoke a service simultaneously, then each thread is expected to create
 * its own Service instance. This is because the request/response context is
 * specific to an invocation.
 * 
 * @author ichernyshev, smalladi, cpenkar
 */
public final class Service {

	private static final Class[] PROXY_CONSTRUCTOR_PARAM_TYPES = new Class[] { Service.class };

	private static final String THREAD_POOL_NAME_SEPARATOR = "-";

	private static final String THREAD_POOL_NAME_PREFIX = "ServiceAsyncProcessing";

	private static final String THREAD_POOL_NAME_Suffix = "default";

	private static final Executor DEFAULT_SERVICE_EXECUTOR = createDefaultExecutor();

	private final ClientServiceDesc m_serviceDesc;

	private final URL m_wsdlLocation;

	private final ServiceInvokerOptions m_invokerOptions;

	private final String m_serviceVersion;

	private URL m_serviceLocation;

	private Map<String, String> m_sessionTransportHeaders = new HashMap<String, String>();

	private Map<String, Cookie> m_cookies = new HashMap<String, Cookie>();

	private Collection<ObjectNode> m_sessionMessageHeaders = new ArrayList<ObjectNode>();

	private Object m_proxy;

	private G11nOptions m_g11nOptions;

	private String m_urlPathInfo;

	private RequestContext m_requestContext = null;

	private ResponseContext m_responseContext = null;

	private Executor m_executor;

	private int m_numTries = 0;

	private IAsyncResponsePoller m_poller = new ServicePoller();

	/**
	 * Internal constructor - client application code must use
	 * ServiceFactory.create() to construct a service object.
	 * 
	 * @param serviceDesc The client service desc
	 * @param serviceLocation The service location
	 * @param serviceVersion The service version
	 * @param wsdlLocation The wsdl location
	 * @throws ServiceException Throws when there is error
	 */
	Service(ClientServiceDesc serviceDesc, URL serviceLocation,
			String serviceVersion, URL wsdlLocation) throws ServiceException {
		if (serviceDesc == null) {
			throw new NullPointerException();
		}
		m_serviceDesc = serviceDesc;
		if (serviceLocation != null) {
			m_serviceLocation = serviceLocation;
		} else {
			m_serviceLocation = serviceDesc.getDefServiceLocationURL();
		}
		m_serviceVersion = serviceVersion; // null OK
		m_wsdlLocation = wsdlLocation; // null OK

		m_invokerOptions = new ServiceInvokerOptions();

		m_executor = getDefaultExecutor();

	}

	/**
	 * Retrieve the logger for this class.
	 * @return The logger
	 */
	static Logger getLogger() {
		return LogManager.getInstance(Service.class);
	}

	/**
	 * Retrieve the default thread pool executor.
	 * @return The thread pool executor
	 */
	static Executor getDefaultExecutor() {
		return DEFAULT_SERVICE_EXECUTOR;
	}

	/**
	 * Returns the administrative name of the service within this client
	 * instance. On the client side, the administrative name is the local part
	 * of the service qualified name configured in ClientConfig.xml.
	 * 
	 * @return the administrative name
	 */
	public String getAdminName() {
		return m_serviceDesc.getAdminName();
	}

	/**
	 * Returns the fully qualified name of the service to be accessed by this
	 * client instance; clients and services mutually associate this value in
	 * order to uniquely identify the service to be invoked.
	 * 
	 * @return the qualified name
	 */
	public QName getServiceQName() {
		return m_serviceDesc.getServiceQName();
	}

	/**
	 * Returns a proxy offering type-specific operations and conversions for the
	 * given service. The proxy will be created from a cleass specified in the
	 * service configuration. Proxies are usually generated by the code
	 * generation tool.
	 * 
	 * @param <T> type of the returning proxy object
	 * @return the proxy object
	 * @throws ServiceException throws when error happens
	 */
	public <T> T getProxy() throws ServiceException {
		if (m_proxy == null) {
			m_proxy = createProxy();
		}

		@SuppressWarnings("unchecked")
		T result = (T) m_proxy;

		return result;
	}

	/**
	 * Private method to construct proxy.
	 * 
	 * @return the proxy
	 * @throws ServiceException
	 */
	private Object createProxy() throws ServiceException {
		Class proxyClass = m_serviceDesc.getProxyClass();

		@SuppressWarnings("unchecked")
		Object result = ReflectionUtils.createInstance(proxyClass,
				PROXY_CONSTRUCTOR_PARAM_TYPES, new Object[] { this });

		return result;
	}

	/**
	 * Returns the Service's invoker options structure, which is never null. The
	 * client application code can proceed to set values into this structure,
	 * and these will be used for the next service invocation. The same
	 * structure remains allocated through the lifetime of this particular
	 * Service object.
	 * 
	 * @return ServiceInvokerOptions object
	 */
	public ServiceInvokerOptions getInvokerOptions() {
		return m_invokerOptions;
	}

	/**
	 * Returns the currently set globalization options. This is an immutable
	 * structure; to change the globalization options, allocate a new object and
	 * use <code>setG11nOptions()</code>.
	 * 
	 * @return the globalization options
	 */
	public G11nOptions getG11nOptions() {
		return m_g11nOptions;
	}

	/**
	 * Set the globalization options. These will be used for all service
	 * invocations until another call to setG11nOptions is made. If the value is
	 * null, options will be taken from the client configuration for this
	 * service. If configuration is also not available, a minimal globalization
	 * options structure will be used, with no global ID/locale information, and
	 * the JVM default character set (generally UTF-16).
	 * 
	 * @param value
	 *            the globalization options to be set
	 */
	public void setG11nOptions(G11nOptions value) {
		m_g11nOptions = value;
	}

	/**
	 * Returns the URL path info which is appended after the base URL.
	 * 
	 * @return the Url path Info
	 */
	public String getUrlPathInfo() {
		return m_urlPathInfo;
	}

	/**
	 * Set the Url Path info.
	 * 
	 * @param urlPathInfo
	 *            the Url Path to be set
	 */
	public void setUrlPathInfo(String urlPathInfo) {
		m_urlPathInfo = urlPathInfo;
	}

	/**
	 * Returns the request context to be used for the next service invocation.
	 * The client application code can proceed to set values into this
	 * structure, and these will be used for the next service invocation only.
	 * The Service's current request context reference is discarded after
	 * invoke() finishes, and the caller should access a new RequestContext for
	 * the following invocation.
	 * 
	 * @return the request context
	 */
	public RequestContext getRequestContext() {
		if (m_requestContext == null) {
			m_requestContext = new RequestContext();
		}
		return m_requestContext;
	}

	/**
	 * Returns the response context with results from the previous call to
	 * invoke(). The returned context is never null. The response context
	 * contains transport headers received from the server.
	 * 
	 * @return the response context.
	 */
	public ResponseContext getResponseContext() {
		if (m_responseContext == null) {
			m_responseContext = new ResponseContext();
		}
		return m_responseContext;
	}

	/**
	 * Returns the service location (endpoint address). This is the same URL
	 * passed to the ServiceFactory.create() call.
	 * 
	 * @return the service location.
	 */
	public URL getServiceLocation() {
		return m_serviceLocation;
	}

	/*
	 * Returns the service location (endpoint address). This is the same URL
	 * passed to the ServiceFactory.create() call.
	 * 
	 * @return the service location.
	 */
	/**
	 * Sets the service location (endpoint address).
	 * 
	 * @param sl
	 *            the new service location to use for subsequence calls.
	 * 
	 */
	public void setServiceLocation(URL sl) {
		m_serviceLocation = sl;
	}

	/**
	 * Returns the service WSDL URL. This feature is not currently
	 * implemented/used within the SOA framework.
	 * 
	 * @return the WSDL URL.
	 */
	public URL getWsdlLocation() {
		return m_wsdlLocation;
	}

	/**
	 * Returns the service version. This is either the value passed to
	 * ServiceFactory.create(), or if no such value was supplied, the version
	 * given in the client's configuration for this service.
	 * 
	 * @return the service version
	 */
	public String getVersion() {
		if (m_serviceVersion == null) {
			return m_serviceDesc.getServiceVersion();
		}

		return m_serviceVersion;
	}

	/**
	 * Returns a specified transport header value that was previously set with
	 * <code>setSessionTransportHeader()</code>, or null if none.
	 * 
	 * @param name
	 *            the name of the header value to be retrieved
	 * @return the header value
	 */
	public String getSessionTransportHeader(String name) {
		if (m_sessionTransportHeaders == null || name == null) {
			return null;
		}

		name = SOAHeaders.normalizeName(name, false);
		return m_sessionTransportHeaders.get(name);
	}

	/**
	 * Sets a transport header value with session level duration. Header values
	 * set with this function are retained across future invocations until
	 * changed (another call is made with the same name and a different value)
	 * or until the Service object is destroyed. In contrast, header values set
	 * into the RequestContext have effect only until the next service
	 * invocation completes. RequestContext header values should be considered
	 * temporary overrides of the more permanent session header values here.
	 * 
	 * @param name
	 *            the name of the header value to set
	 * @param value
	 *            the value to be set
	 */
	public void setSessionTransportHeader(String name, String value) {
		if (name == null) {
			throw new NullPointerException();
		}

		name = SOAHeaders.normalizeName(name, false);
		value = SOAHeaders.normalizeValue(name, value);

		m_sessionTransportHeaders.put(name, value);
	}

	/**
	 * Returns a copy of the entire map of session transport headers. Refer to
	 * <code>setSessionTransportHeader()</code>.
	 * 
	 * @return the session transport map
	 */
	public Map<String, String> getSessionTransportHeaders() {

		return Collections.unmodifiableMap(new HashMap<String, String>(
				m_sessionTransportHeaders));
	}

	/**
	 * Add a message header object as ObjectNode with session level duration.
	 * Header objects set with this function are retained across future
	 * invocations until changed (another call is made with the same name and a
	 * different value) or until the Service object is destroyed. In contrast,
	 * header objects set into the RequestContext have effect only until the
	 * next service invocation completes. RequestContext header objects should
	 * be considered temporary overrides of the more permanent session header
	 * objects here.
	 * 
	 * @param objectNode
	 *            message header object
	 */
	public void addSessionMessageHeader(ObjectNode objectNode) {
		if (objectNode == null) {
			return;
		}
		m_sessionMessageHeaders.add(objectNode);
	}

	/**
	 * Add a message header as an Java Object with session level duration.
	 * Header objects set with this function are retained across future
	 * invocations until changed (another call is made with the same name and a
	 * different value) or until the Service object is destroyed. In contrast,
	 * header objects set into the RequestContext have effect only until the
	 * next service invocation completes. RequestContext header objects should
	 * be considered temporary overrides of the more permanent session header
	 * objects here.
	 * 
	 * @param headerJavaObject
	 *            message header object
	 */
	public void addSessionMessageHeaderAsJavaObject(Object headerJavaObject) {
		if (headerJavaObject == null) {
			return;
		}
		m_sessionMessageHeaders.add(new JavaObjectNodeImpl(null,
				headerJavaObject));
	}

	/**
	 * Returns a copy of the entire map of session message headers.
	 * 
	 * @return the session message header collection
	 */
	public Collection<ObjectNode> getSessionMessageHeaders() {
		return Collections.unmodifiableCollection(new ArrayList<ObjectNode>(
				m_sessionMessageHeaders));
	}

	/**
	 * Sets a cookie with session level duration. Cookiess set with this
	 * function are retained across future invocations until changed (another
	 * set is made of a cookie with the same name) or until the Service object
	 * is destroyed. In contrast, cookies set into the RequestContext have
	 * effect only until the next service invocation completes. RequestContext
	 * cookies should be considered temporary overrides of the more permanent
	 * session header cookies here.
	 * 
	 * @param cookie
	 *            the cookie to be set
	 */
	public void setCookie(Cookie cookie) {
		m_cookies.put(cookie.getName(), cookie);
	}

	/**
	 * Returns a specified session cookie value by name that was previously set
	 * with <code>setCookie()</code>.
	 * 
	 * @param name
	 *            the name of the cookie value to be retrieved.
	 * @return the specified session cookie, or null if none.
	 */
	public Cookie getCookie(String name) {
		if (name != null && m_cookies != null) {
			return m_cookies.get(name.toUpperCase());
		}

		return null;
	}

	/**
	 * Returns all session cookie values. Refer to <code>setCookie()</code>.
	 * 
	 * @return the session cookies
	 */
	public Cookie[] getCookies() {
		Cookie[] result = m_cookies.values().toArray(
				new Cookie[m_cookies.size()]);
		return result;
	}

	/**
	 * Retrieves the cookies.
	 * @return The map containing the cookies.  Key is the cookie name and value is the cookie.
	 */
	Map<String, Cookie> getCookiesMap() {
		return Collections.unmodifiableMap(new HashMap<String, Cookie>(
				m_cookies));
	}

	/**
	 * This generic invocation method takes untyped input, output, and error
	 * argument information. invoke() constitutes the "Dynamic Invocation
	 * Interface" (DII) of the SOA Client. Any operation of a service can be
	 * invoked through this method, simply by giving the operation name.
	 * 
	 * Alternatively, the client application code can obtain a proxy using
	 * <code>getProxy()</code>.
	 * 
	 * @param opName
	 *            the name of the operation to be invoked
	 * @param inParams
	 *            an array of input parameters, of the appropriate Java types as
	 *            configured for the service (same as in the corresponding proxy
	 *            method signature and as in the type mappings configuration
	 *            that is generated by code generation).
	 * @param outParams
	 *            an array of output parameters, of the appropriate Java types
	 *            (as per input parameters).
	 * 
	 * @throws ServiceInvocationException
	 *             in case there are client or server side system errors, or if
	 *             there are application errors in the returned error response.
	 */
	public void invoke(String opName, Object[] inParams, List<Object> outParams)
			throws ServiceInvocationException {
		invoke(opName, false, false, inParams, outParams, null, null);
	}

	/**
	 * 
	 * @param block
	 *            Specifies if the poll method is blocking or non-blocking.
	 * @param partial
	 *            Specifies if the poll method returns list or ALL/PARTIAL
	 *            responses
	 * @return if block is true and if partial is true then method is blocked
	 *          until at least one response. It may return more than one. b. if
	 *          block is true and if partial is false then method is blocked
	 *          until all the outstanding responses are available. c. if block
	 *          is false and if partial is true then method is non-blocking and
	 *          returns immediately all available responses. this is option is
	 *          typical polling with no-timeout d. if block is false and if
	 *          partial is false then method is BLOCKED until all the
	 *          outstanding responses are available.
	 * 
	 * Note: if partial is false, Irrespective of "block" parameter, all
	 * outstanding responses are returned and method call is BLOCKING
	 * @throws InterruptedException throws if the polling gets interrupted during the waiting
	 */
	public List<Response<?>> poll(boolean block, boolean partial)
			throws InterruptedException {
		return m_poller.poll(block, partial, -1);
	}

	/**
	 * 
	 * @param block
	 *            Specifies if the poll method is blocking or non-blocking.
	 * @param partial
	 *            Specifies if the poll method returns list or ALL/PARTIAL
	 *            responses
	 * @param timeout
	 *            timeout in milliseconds
	 * @return if block is true and if partial is true then method is blocked
	 *          until atleast one response. It may return more than one. b. if
	 *          block is true and if partial is false then method is blocked
	 *          until all the outstanding responses are available. c. if block
	 *          is false and if partial is true then method is non-blocking and
	 *          returns immediately all available responses. this is option is
	 *          typical polling with no-timeout d. if block is false and if
	 *          partial is false then method is BLOCKED until all the
	 *          outstanding responses are available.
	 * 
	 * Note: if partial is false, Irrespective of "block" parameter, all
	 * outstanding responses are returned and method call is BLOCKING
	 * @throws InterruptedException throws if the polling gets interrupted during the waiting
	 */
	public List<Response<?>> poll(boolean block, boolean partial, long timeout)
			throws InterruptedException {
		return m_poller.poll(block, partial, timeout);
	}

	/**
	 * This generic invocation method facilitates working with serialized data,
	 * wrapped in a ByteBufferWrapper. Client applications that prefer to
	 * manipulate serialized request/response representations may call this
	 * method and pass in a pre-serialized request that will be transmitted to
	 * the service without modification. Clients will then receive a
	 * non-deserialized result back.
	 * 
	 * Takes in pre-serialized input buffer wrapper, output buffer wrapper that
	 * acts as a holder for result coming back. This invoke() constitutes the
	 * alternate "Dynamic Invocation Interface" (DII) of the SOA Client. Any
	 * operation of a service can be invoked through this method, simply by
	 * giving the operation name.
	 * 
	 * Alternatively, the client application code can obtain a proxy using
	 * <code>getProxy()</code>.
	 * 
	 * Note: since we don't know the size of the response buffer, we cannot
	 * preallocate space for it and therefore are using a wrapper.
	 * 
	 * @param headerMap
	 *            header map containing the name of the operation to be invoked
	 * @param inWrapper
	 *            a wrapper containing a ByteBuffer with pre-serialized request
	 * @param outWrapper
	 *            a place holder for non-deserialized result ByteBuffer, must be
	 *            initialized
	 * 
	 * @throws ServiceInvocationException
	 *             in case there are client or server side system errors, or if
	 *             there are application errors in the returned error response.
	 */
	public void invoke(Map<String, String> headerMap,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper)
			throws ServiceInvocationException {
		if (headerMap == null) {
			throw new IllegalArgumentException("Header map cannot be null");
		}

		String opName = headerMap.get(SOAHeaders.SERVICE_OPERATION_NAME);

		RequestContext ctx = getRequestContext();
		for (Map.Entry<String, String> e : headerMap.entrySet()) {
			ctx.setTransportHeader(e.getKey(), e.getValue());
		}

		invoke(opName, true, true, null, null, inWrapper, outWrapper);
	}

	/**
	 * For serializing the request, but skipping the response deserialization.
	 * @param opName the name of the operation to be invoked
	 * @param inParams
	 *            an array of input parameters, of the appropriate Java types as
	 *            configured for the service (same as in the corresponding proxy
	 *            method signature and as in the type mappings configuration
	 *            that is generated by code generation).
	 * @param outWrapper
	 *            a place holder for non-deserialized result ByteBuffer, must be
	 *            initialized
	 * @throws ServiceInvocationException
	 *             in case there are client or server side system errors, or if
	 *             there are application errors in the returned error response.
	 */
	public void invoke(String opName, Object[] inParams,
			ByteBufferWrapper outWrapper) throws ServiceInvocationException {
		invoke(opName, false, true, inParams, null, null, outWrapper);
	}

	private void invoke(String opName, boolean inboundRawMode,
			boolean outboundRawMode, Object[] inParams, List<Object> outParams,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper)
			throws ServiceInvocationException {
		RawDataServiceDispatch dispatch = null;
		try {
			try {
				dispatch = new RawDataServiceDispatch(opName,
						m_serviceLocation, m_serviceDesc, m_wsdlLocation,
						m_invokerOptions, m_serviceVersion, m_cookies,
						m_sessionTransportHeaders, m_sessionMessageHeaders,
						m_g11nOptions, getRequestContext(), null, null);
				RawDispatchData rawData = new RawDispatchData(inboundRawMode,
						outboundRawMode, inParams, outParams, inWrapper,
						outWrapper);

				// Cache functionality doesn't support call for cache policy
				// (getCachePolicy)
				// and "raw" calls
				boolean cacheSupported = !(SOAConstants.OP_GET_CACHE_POLICY
						.equals(opName) || inboundRawMode);
				if (cacheSupported
						&& m_serviceDesc.isCacheDisabledOnLocal() != null
						&& m_serviceDesc.isCacheDisabledOnLocal()
								.booleanValue()) {
					if (getInvokerOptions() != null
							&& SOAConstants.TRANSPORT_LOCAL
									.equalsIgnoreCase(getInvokerOptions()
											.getTransportName())) {
						cacheSupported = false;
					} else if (SOAConstants.TRANSPORT_LOCAL
							.equalsIgnoreCase(m_serviceDesc
									.getDefTransportName())) {
						cacheSupported = false;
					}
				}
				CacheProvider cacheProvider = null;
				if (cacheSupported) {
					cacheProvider = m_serviceDesc.getCacheProviderClass();
					// if cacheProvider somehow is not available, then we can't
					// use any caches
					cacheSupported = cacheProvider != null;
					if (cacheSupported) {
						try {
							cacheProvider
									.init(m_serviceDesc, m_serviceLocation);
						} catch (ServiceException e) {
							if (m_serviceDesc.isSkipCacheOnError() != null
									&& !m_serviceDesc.isSkipCacheOnError()
											.booleanValue()) {
								throw e;
							}
						}
					}
				}
				if (cacheSupported && cacheProvider.isCacheEnabled()) {
					// cacheContext
					CacheContext cacheContext = new CacheContext().setOpName(
							opName).setRequest(inParams[0]);
					Object result = cacheProvider.lookup(cacheContext);
					if (result != null) {
						outParams.add(result);
					} else {
						dispatch.invoke(rawData);
						cacheContext.setResponse(outParams.get(0));
						cacheProvider.insert(cacheContext);
					}
				} else {
					// "Old" behavior, which is a direct call to server
					dispatch.invoke(rawData);
					return;
				}
			} catch (WebServiceException e) {
				throw e.getCause();
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (ServiceInvocationException e) {
			throw e;
		} catch (Throwable e) {
			throw new WebServiceException(e);
		} finally {
			if (dispatch != null) {
				m_urlPathInfo = dispatch.getUrlPathInfo();
				m_requestContext = dispatch.getDispatchRequestContext();
				m_responseContext = dispatch.getDispatchResponseContext();
				m_numTries = dispatch.getLastTryCount();
			}
		}
	}

	/**
	 * This functions help user create a dispatch object which can also be used
	 * to invoke a given service operation in Sync and Aysnc (Pull & Push) mode.
	 * 
	 * This specific variant of createDispatch provides the user the ability to
	 * invoke the request by passing in JaxB request object and get a JaxB
	 * response object.
	 * 
	 * @param opName
	 *            the name of the operation to be invoked
	 * @return <code>ServiceDispatch</code> dispatch object
	 */
	public ServiceDispatch createDispatch(String opName) {
		try {
			return new TypedDataServiceDispatch(opName, m_serviceLocation,
					m_serviceDesc, m_wsdlLocation, m_invokerOptions,
					m_serviceVersion, m_cookies, m_sessionTransportHeaders,
					m_sessionMessageHeaders, m_g11nOptions,
					getRequestContext(), m_executor, m_poller);
		} catch (ServiceException e) {
			throw new WebServiceException(e);
		}
	}

	/**
	 * This functions help user create a dispatch object which can also be used
	 * to invoke a given service operation in Sync and Aysnc (Pull & Push) mode.
	 * 
	 * This specific variant of createDispatch provides the user the ability to
	 * invoke the request by passing in JaxB request object and get a JaxB
	 * response object.
	 * 
	 * @param opName
	 *            the name of the operation to be invoked
	 * @param isRaw
	 *            <code>true</code> if user would like to exchange 
	 *            request/response using InvokerExchange (allows exchange 
	 *            of raw packets) 
	 *            <code>false</code> if user would like to exchange 
	 *            request/response as JaxB objects.
	 * @return <code>ServiceDispatch</code> dispatch object
	 */
	public ServiceDispatch createDispatch(String opName, boolean isRaw) {
		try {
			return isRaw ? new RawDataServiceDispatch(opName,
					m_serviceLocation, m_serviceDesc, m_wsdlLocation,
					m_invokerOptions, m_serviceVersion, m_cookies,
					m_sessionTransportHeaders, m_sessionMessageHeaders,
					m_g11nOptions, getRequestContext(), m_executor, m_poller)
					: new TypedDataServiceDispatch(opName, m_serviceLocation,
							m_serviceDesc, m_wsdlLocation, m_invokerOptions,
							m_serviceVersion, m_cookies,
							m_sessionTransportHeaders, m_sessionMessageHeaders,
							m_g11nOptions, getRequestContext(), m_executor,
							m_poller);
		} catch (ServiceException e) {
			throw new WebServiceException(e);
		}
	}

	private static Executor createDefaultExecutor() {
		ThreadPoolConfig tpConfig = new ThreadPoolConfig(null,
				ThreadPoolConfig.DFLT_KEEP_ALIVE_TIME_IN_SEC);

		String poolName = buildThreadPoolName(THREAD_POOL_NAME_PREFIX,
				THREAD_POOL_NAME_Suffix, !tpConfig.isUseCmdRunner());

		return ThreadPoolFactory.getInstance().createExecutor(poolName,
				tpConfig.getKeepAliveTimeInSec(), tpConfig.getCmdRunnerProps());
	}

	private static String buildThreadPoolName(String prefix, String suffix,
			boolean appendSystemTime) {
		StringBuilder sb = new StringBuilder(50);
		sb.append(prefix).append(THREAD_POOL_NAME_SEPARATOR).append(suffix);
		if (appendSystemTime) {
			sb.append(THREAD_POOL_NAME_SEPARATOR);
			sb.append(System.currentTimeMillis());
		}
		return sb.toString();
	}

	/**
	 * Current executor in context of this service for Async push operations
	 * that require multi-threading support.
	 * 
	 * @return <code>Executor</code> Current executor in context of this service
	 */
	public Executor getExecutor() {
		return m_executor;
	}

	/**
	 * Allows user to set the current executor in context of this service for
	 * push operations.
	 * 
	 * @param executor
	 *            Executor to use in context of this service for push
	 *            operations. If user passes "null" then push will be executed
	 *            on the kernel thread that provided the response.
	 */
	public void setExecutor(Executor executor) {
		m_executor = executor;
	}

	/**
	 * Returns the total number of attempted service invocation tries during the
	 * most recent call to invoke(). The total number of attempted invocations
	 * is one plus the number of retries attempted.
	 * 
	 * @return the number of attempted invocations, or zero if invoke() has
	 *         never been called for this Service object.
	 */
	public int getLastTryCount() {
		return m_numTries;
	}

}
