/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceDispatchImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.RawDispatchData;
import org.ebayopensource.turmeric.runtime.sif.service.InvokerExchange;


public class AsyncResponse<T> implements Response<T> {

	private final ClientMessageContextImpl m_msgContext;

	private final Future<?> m_transportResponse;

	private boolean m_wasGetSuccessful = false;

	private T m_Response;

	private IAsyncResponsePoller m_poller;

	private Map<String, Object> m_context = null;

	private ExecutionException m_execException = null;

	private RuntimeException m_runtimeException = null;

	public AsyncResponse(ClientMessageContextImpl msgContext) {
		// Store the msgContext to process the response
		m_msgContext = msgContext;
		if (m_msgContext.getTransport().supportsPoll()) {
			m_poller = m_msgContext.getServicePoller();
		}
		// Store the future returned by the transport
		m_transportResponse = m_msgContext.getFutureResponse();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return m_transportResponse.cancel(mayInterruptIfRunning);
	}

	public T get() throws InterruptedException, ExecutionException {
		if (m_wasGetSuccessful) {
			if (m_execException != null)
				throw m_execException;
			if (m_runtimeException != null)
				throw m_runtimeException;
			return m_Response;
		}
		ClientMessageProcessor cmp;
		try {
			cmp = ClientMessageProcessor.getInstance();
			cmp.processResponse(m_msgContext);
			Message inboundMessage = m_msgContext.getResponseMessage();
			try {
				if (m_msgContext.getOutParams() != null)
					handleOutParamsResponse(inboundMessage, m_msgContext
							.getOutParams());
				else if (m_msgContext.getOutBuffer() != null)
					handleRawByteResponse(inboundMessage, m_msgContext
							.getOutBuffer());
				else
					handleTypedResponse(inboundMessage);
			} finally {
				m_wasGetSuccessful = true;
				if (m_poller != null) {
					m_poller.remove(m_transportResponse);
				}
			}
			return m_Response;
		} catch (ServiceException e) {
			m_execException = new ExecutionException(e);
			throw m_execException;
		} catch (RuntimeException e) {
			m_runtimeException = e;
			throw m_runtimeException;
		}
	}

	@SuppressWarnings("unchecked")
	private void handleTypedResponse(Message inboundMessage)
			throws ServiceException, ServiceInvocationException {
		BaseServiceDispatchImpl.checkForErrors(
				(BaseMessageImpl) inboundMessage, m_msgContext, m_msgContext
						.getAdminName());
		m_Response = (T) inboundMessage.getParam(0);
		m_context = AsyncUtils.extractContext(inboundMessage);
	}

	@SuppressWarnings("unchecked")
	private void handleRawByteResponse(Message inboundMessage,
			ByteBufferWrapper outWrapper) throws ServiceException,
			ServiceInvocationException {
		BaseServiceDispatchImpl.getOutBoundRawData(inboundMessage, outWrapper);
		m_context = AsyncUtils.extractContext(inboundMessage);
		m_Response = (T) new InvokerExchange(null, outWrapper);
	}

	@SuppressWarnings("unchecked")
	private void handleOutParamsResponse(Message inboundMessage,
			List<Object> outParams) throws ServiceException,
			ServiceInvocationException {
		BaseServiceDispatchImpl.checkForErrors(
				(BaseMessageImpl) inboundMessage, m_msgContext, m_msgContext
						.getAdminName());
		BaseServiceDispatchImpl.getOutParams(inboundMessage, outParams);
		m_context = AsyncUtils.extractContext(inboundMessage);
		m_Response = (T) new RawDispatchData(null, outParams);
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		m_transportResponse.get(timeout, unit);
		return get();
	}

	public boolean isCancelled() {
		return m_transportResponse.isCancelled();
	}

	public boolean isDone() {
		return m_transportResponse.isDone();
	}

	public Map<String, Object> getContext() {
		return m_context;
	}

    public ClientMessageContextImpl getMessageContext() {
        return m_msgContext;
    }
	
}
