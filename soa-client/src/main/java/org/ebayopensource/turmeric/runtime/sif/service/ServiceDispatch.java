/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Binding;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;

/**
 * 
 * Base implementation of the JAX-WS dispatch. Also, extending the dispatch
 * interface to input the operation name.
 * 
 * 
 * @param <T> An InvokerExchange
 */
public abstract class ServiceDispatch<T> implements Dispatch<T> {

	/**
	 * The operation name for this service dispatch.
	 */
	protected final String m_opName;

	/**
	 * @param opName The operation name for this dispatch.
	 */
	protected ServiceDispatch(String opName) {
		m_opName = opName;
	}

	/**
	 * Invokes the operation with the given InvokerExchange.
	 * @param opName The operation to invoke.
	 * @param inArg The InvokerExchange.
	 * @return An InvokerExchange containing response from the invocation.
	 */
	protected abstract T invoke(String opName, T inArg);

	/**
	 * Invokes the operation with the given InvokerExchange asynchronous pull.
	 * @param opName The operation to invoke.
	 * @param inArg The InvokerExchange.
	 * @return An Response of InvokerExchange containing response from the invocation.
	 */
	protected abstract Response<T> invokeAsync(String opName, T inArg);

	/**
	 * Invokes the operation with the given InvokerExchange using asynchronous push.
	 * @param opName The operation to invoke.
	 * @param inArg The InvokerExchange.
	 * @param handler The Async push handler to be called when response is 
	 *     ready.
	 * @return An Future of InvokerExchange containing response from the invocation.
	 */
	protected abstract Future<?> invokeAsync(String opName, T inArg,
			AsyncHandler<T> handler);

	/**
	 * Performs an one-way invocation of an operation with the given InvokerExchange.
	 * @param opName The operation to invoke.
	 * @param inArg The InvokerExchange.
	 */
	protected abstract void invokeOneWay(String opName, T inArg);


	/**
	 * Invokes with the given InvokerExchange of the Dispatch's default operation.
	 * @param inArg The InvokerExchange.
	 * @return An InvokerExchange containing response from the invocation.
	 */
	public T invoke(T inArg) {
		return invoke(m_opName, inArg);
	}

	/**
	 * Invokes the default operation with the given InvokerExchange asynchronous pull.
	 * @param inArg The InvokerExchange.
	 * @return An Response of InvokerExchange containing response from the invocation.
	 */
	public Response<T> invokeAsync(T inArg) {
		return invokeAsync(m_opName, inArg);
	}

	/**
	 * Invokes the default operation with the given InvokerExchange using asynchronous push.
	 * @param inArg The InvokerExchange.
	 * @param handler The Async push handler to be called when response is 
	 *     ready.
	 * @return An Future of InvokerExchange containing response from the invocation.
	 */
	public Future<?> invokeAsync(T inArg, AsyncHandler<T> handler) {
		return invokeAsync(m_opName, inArg, handler);
	}

	/**
	 * @deprecated.
	 * @param inArg The InvokerExchange.
	 * @param handler The Async push handler to be called when response is 
	 *     ready.
	 * @return An Future of InvokerExchange containing response from the invocation.
	 */

	public Future<?> invokeAsync(T inArg, BaseAsyncPushHandler<T> handler) {
		handler.setRequest(inArg);
		return invokeAsync(m_opName, inArg, handler);
	}

	/**
	 * Performs an one-way invocation of the default operation with the given InvokerExchange.
	 * @param inArg The InvokerExchange.
	 */
	public void invokeOneWay(T inArg) {
		invokeOneWay(m_opName, inArg);
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.BindingProvider#getBinding()
	 */
	@Override

	public Binding getBinding() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.BindingProvider#getEndpointReference()
	 */
	@Override
	public EndpointReference getEndpointReference() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.BindingProvider#getEndpointReference(java.lang.Class)
	 */
	@Override

	public <E extends EndpointReference> E getEndpointReference(Class<E> inArg) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.BindingProvider#getRequestContext()
	 */
	@Override
	public Map<String, Object> getRequestContext() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.BindingProvider#getResponseContext()
	 */
	@Override
	public Map<String, Object> getResponseContext() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates an empty response context object.
	 * 
	 * @return the response context
	 */
	protected static ResponseContext createResponseContext() {
		return new ResponseContext();
	}

	/**
	 * Creates a response context object based on the given client message
	 * context.
	 * 
	 * @param ctx
	 *            The given client message context
	 * @param needPayloadData
	 *            indicates if payload data needs to be set for this context
	 * @return the created responst context
	 * @throws ServiceException
	 *             throws if error happens
	 */
	protected static ResponseContext createResponseContext(
			ClientMessageContext ctx, Boolean needPayloadData)
			throws ServiceException {
		return new ResponseContext(ctx, needPayloadData);
	}

	/**
	 * Gets the request context properties.
	 * 
	 * @param reqContext
	 *            the request context
	 * @return the map storing all the properties of the context
	 */
	protected static Map<String, Object> getRequestContextPropertiesInternal(
			RequestContext reqContext) {
		return reqContext != null ? reqContext.getPropertiesInternal() : null;
	}

	/**
	 * Gets the transport headers from the request context.
	 * 
	 * @param reqContext
	 *            the request context
	 * @return the map storing all the transport headers
	 */
	protected static Map<String, String> getRequestContextTransportHeadersInternal(
			RequestContext reqContext) {
		return reqContext != null ? reqContext.getTransportHeadersInternal()
				: null;
	}

	/**
	 * Gets the message headers from the request context.
	 * 
	 * @param reqContext
	 *            the request context
	 * @return the map storing all the message headers
	 */
	protected static Collection<ObjectNode> getRequestContextMessageHeadersInternal(
			RequestContext reqContext) {
		return reqContext != null ? reqContext.getMessageHeadersInternal()
				: null;
	}

	/**
	 * Gets cookies from the request context.
	 * 
	 * @param reqContext
	 *            the request context
	 * @return the map storing the cookies
	 */
	protected static Map<String, Cookie> getRequestContextCookiesInternal(
			RequestContext reqContext) {
		return reqContext != null ? reqContext.getCookiesInternal() : null;
	}

	/**
	 * Gets all the content from the request context, including all the
	 * transport headers, the cookies and the properties.
	 * 
	 * @param reqContext
	 *            the request context
	 * @return the map storing all the content
	 */
	protected static Map<String, Object> getRequestContextInternal(
			RequestContext reqContext) {
		return reqContext != null ? reqContext.getContextAsMap() : null;
	}

	/**
	 * Gets all the content from the response context, including the transport
	 * headers, message headers, properties and payload data.
	 * 
	 * @param respContext
	 *            the response context
	 * @return the map storing all the content
	 */
	protected static Map<String, Object> getResponseContextInternal(
			ResponseContext respContext) {
		return respContext != null ? respContext.getContextAsMap() : null;
	}

}
