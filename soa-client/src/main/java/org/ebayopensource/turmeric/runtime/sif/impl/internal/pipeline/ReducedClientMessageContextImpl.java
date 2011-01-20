/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.LoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.ClientLoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ReducedClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


public class ReducedClientMessageContextImpl extends
		BaseMessageContextImpl<ClientServiceDesc, ClientServiceContext>
		implements ReducedClientMessageContext {

	final private Map<String, Object> m_requestProperties = new HashMap<String, Object>();
	final private Map<String, Object> m_responseProperties = new HashMap<String, Object>();
	final private MessageContext m_callerMessageContext;

	public ReducedClientMessageContextImpl(ClientServiceDesc serviceDesc,
			ServiceOperationDesc operation,
			ProtocolProcessorDesc protocolProcessor, Transport transport,
			BaseMessageImpl requestMessage, BaseMessageImpl responseMessage,
			ServiceAddress clientAddress, ServiceAddress serviceAddress,
			Map<String, Object> systemProperties, String serviceVersion,
			Charset effectiveCharset, String requestUri,
			LoggingHandlerUtils utils, boolean useAsync)
			throws ServiceException {
		super(serviceDesc, operation, protocolProcessor, transport,
				requestMessage, responseMessage, clientAddress, serviceAddress,
				systemProperties, serviceVersion, effectiveCharset, requestUri);

		m_callerMessageContext = MessageContextAccessor.getContext();

		if(useAsync)
			prepareAsyncMessageContext();
	}

	@Override
	protected Map<String, String> buildOutputHeaders(
			Map<String, String> customHeaders) throws ServiceException {
		throw new UnsupportedOperationException(
				"buildOutputHeaders() method is not supported on ReducedClientMessageContextImpl");
	}

	@Override
	protected ClientLoggingHandlerUtils getLoggingHandlerUtils() {
		return new ClientLoggingHandlerUtils();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#getInvokerVersion()
	 */
	public String getInvokerVersion() {
		return getServiceVersion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#getServiceId()
	 */
	public ClientServiceId getServiceId() {
		return getServiceDesc().getServiceId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#getRequestProperty(java.lang.String)
	 */
	public Object getRequestProperty(String name) {
		return m_requestProperties.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#setRequestProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setRequestProperty(String name, Object value) {
		if (name == null)
			throw new NullPointerException();
		m_requestProperties.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#getResponseProperty(java.lang.String)
	 */
	public Object getResponseProperty(String name) {
		return m_responseProperties.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#setResponseProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setResponseProperty(String name, Object value) {
		if (name == null)
			throw new NullPointerException();
		m_responseProperties.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#getRequestPropertyNames()
	 */
	public Set<String> getRequestPropertyNames() {
		return Collections.unmodifiableSet(m_requestProperties.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ReducedClientMessageContext#getResponsePropertyNames()
	 */
	public Set<String> getResponsePropertyNames() {
		return Collections.unmodifiableSet(m_responseProperties.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.runtime.sif.pipeline.ReducedClientMessageContext#getCallerMessageContext()
	 */
	public MessageContext getCallerMessageContext() {
		return m_callerMessageContext;
	}

}
