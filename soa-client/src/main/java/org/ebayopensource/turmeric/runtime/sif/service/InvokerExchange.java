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

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;

/**
 * Wrapper class for representing parity with the JAX-WS dispatch model Has
 * support for DII validation.
 * 
 * 
 */
public class InvokerExchange {

	private final ByteBufferWrapper m_inWrapper;

	private final ByteBufferWrapper m_outWrapper;

	private final Map<String, String> m_headerMap;

	protected final Object[] m_inParams;

	/**
	 * @param headerMap  The client request header mapping
	 * @param inParams  The incoming request arguments.
	 * @param inWrapper Input wrapper for raw mode support. Caller uses it to 
	 *     pass in the raw request data.
	 * @param outWrapper Output wrapper for raw mode support. Caller uses it to
	 *     retrieve the returning raw response data back. 
	 */
	protected InvokerExchange(Map<String, String> headerMap, Object[] inParams,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper) {
		m_inParams = inParams;
		m_inWrapper = inWrapper;
		m_outWrapper = outWrapper;
		m_headerMap = headerMap;
	}

	/**
	 * @param headerMap  The client request header mapping
	 * @param inParam  An incoming request argument.
	 * @param inWrapper Input wrapper for raw mode support. Caller uses it to 
	 *     pass in the raw request data.
	 * @param outWrapper Output wrapper for raw mode support. Caller uses it to
	 *     retrieve the returning raw response data back. 
	 */
	protected InvokerExchange(Map<String, String> headerMap, Object inParam,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper) {
		this(headerMap, new Object[] { inParam }, inWrapper, outWrapper);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param toCopy
	 *            object to copy
	 */
	protected InvokerExchange(InvokerExchange toCopy) {
		this(toCopy.m_headerMap, toCopy.m_inParams, toCopy.m_inWrapper,
				toCopy.m_outWrapper);
	}

	/**
	 * @param headerMap  The client request header mapping
	 * @param inWrapper Input wrapper for raw mode support. Caller uses it to 
	 *     pass in the raw request data.
	 * @param outWrapper Output wrapper for raw mode support. Caller uses it to
	 *     retrieve the returning raw response data back. 
	 */
	public InvokerExchange(Map<String, String> headerMap,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper) {
		this(headerMap, null, inWrapper, outWrapper);
		if (inWrapper == null) {
			throw new RuntimeException(
					"DII inbound byte buffer wrapper cannot be null");
		}
		if (outWrapper == null) {
			throw new RuntimeException(
					"DII outbound byte buffer wrapper cannot be null");
		}
	}

	/**
	 * @param inParam  An incoming request argument.
	 * @param outWrapper Output wrapper for raw mode support. Caller uses it to
	 *     retrieve the returning raw response data back. 
	 */
	public InvokerExchange(Object inParam, ByteBufferWrapper outWrapper) {
		this(null, inParam, null, outWrapper);
		if (outWrapper == null) {
			throw new RuntimeException(
					"DII outbound byte buffer wrapper cannot be null");
		}
	}

	/**
	 * @return The passed in parameter(s).
	 */
	public Object getInParam() {
		return m_inParams[0];
	}

	/**
	 * @return The passed in raw request wrapper.
	 */
	public ByteBufferWrapper getInWrapper() {
		return m_inWrapper;
	}

	/**
	 * @return The raw response wrapper to retrieve response.
	 */
	public ByteBufferWrapper getOutWrapper() {
		return m_outWrapper;
	}

	/**
	 * @return The request header mapping.
	 */
	public Map<String, String> getHeaderMap() {
		return m_headerMap;
	}

}
