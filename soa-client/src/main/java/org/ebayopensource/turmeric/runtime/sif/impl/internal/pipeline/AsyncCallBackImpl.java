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
import java.util.concurrent.Executor;

import javax.xml.ws.AsyncHandler;


import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceDispatchImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.RawDispatchData;
import org.ebayopensource.turmeric.runtime.sif.service.InvokerExchange;

public class AsyncCallBackImpl implements AsyncCallBack {

	private final ClientMessageContextImpl m_msgContext;

	private final AsyncHandler<?> m_handler;

	private final Executor m_executor;

	private boolean m_isDone = false;

	public AsyncCallBackImpl(BaseMessageContextImpl msgContext) {
		m_msgContext = (ClientMessageContextImpl) msgContext;
		m_handler = m_msgContext.getClientAsyncHandler();
		m_executor = m_msgContext.getExecutor();
	}

	public void onException(Throwable cause) {
		final Throwable finalCause = cause;

		if (m_executor != null) {
			m_executor.execute(new Runnable() {
				public void run() {
					doExceptionProcessing(finalCause);
				}
			});
		} else {
			doExceptionProcessing(finalCause);
		}
	}

	public void onResponseInContext() {
		onResponseInContext(null);
	}

	@Override
	public void onResponseInContext(final RunBefore runBefore) {
		if (m_executor != null) {
			m_executor.execute(new Runnable() {
				public void run() {
					doResponseHandling(runBefore);
				}
			});
		} else if (runBefore != null) { // clientStreaming case
			doExceptionProcessing(new ServiceException("Null executor not supported in clientStreaming mode"));
		} else { // old null-executor path
			doResponseHandling(null);
		}
	}

	public void onTimeout() {
		if (m_executor != null) {
			m_executor.execute(new Runnable() {
				public void run() {
					doTimeoutProcessing();
				}
			});
		} else {
			doTimeoutProcessing();
		}
	}

	public boolean isDone() {
		return m_isDone;
	}

	@SuppressWarnings("unchecked")
	void doExceptionProcessing(Throwable cause) {
		try {
			m_handler.handleResponse(new PushResponse(null, cause, null));
		} finally {
			m_isDone = true;
		}
	}

	@SuppressWarnings("unchecked")
	void doResponseHandling(RunBefore runBefore) {
		ClientMessageProcessor cmp;
		try {
			if (runBefore != null) {
				runBefore.run();
			}
			cmp = ClientMessageProcessor.getInstance();
			cmp.processResponse(m_msgContext);
			Message inboundMessage = m_msgContext.getResponseMessage();
			PushResponse response;
			if (m_msgContext.getOutParams() != null)
				response = handleOutParamsResponse(inboundMessage, m_msgContext
						.getOutParams());
			else if (m_msgContext.getOutBuffer() != null)
				response = handleRawByteResponse(inboundMessage, m_msgContext
						.getOutBuffer());
			else
				response = handleTypedResponse(inboundMessage);

			m_handler.handleResponse(response);
		} catch (ServiceException e) {
			PushResponse response = new PushResponse(null, e, null);
			m_handler.handleResponse(response);
		} finally {
			m_isDone = true;
		}
	}

	@SuppressWarnings("unchecked")
	void doTimeoutProcessing() {
		try {
			m_handler.handleResponse(new PushResponse(null,
					new ServiceException(
							ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_COMM_FAILURE,
								ErrorConstants.ERRORDOMAIN)),null));
		} finally {
			m_isDone = true;
		}
	}

	@SuppressWarnings("unchecked")
	private PushResponse handleTypedResponse(Message inboundMessage)
			throws ServiceException, ServiceInvocationException {
		BaseServiceDispatchImpl.checkForErrors(
				(BaseMessageImpl) inboundMessage, m_msgContext, m_msgContext
						.getAdminName());
		Map<String, Object> context = AsyncUtils.extractContext(inboundMessage);
		return new PushResponse(inboundMessage.getParam(0), null, context);
	}

	@SuppressWarnings("unchecked")
	private PushResponse handleRawByteResponse(Message inboundMessage,
			ByteBufferWrapper outWrapper) throws ServiceException,
			ServiceInvocationException {
		BaseServiceDispatchImpl.getOutBoundRawData(inboundMessage, outWrapper);
		Map<String, Object> context = AsyncUtils.extractContext(inboundMessage);
		return new PushResponse(new InvokerExchange(null, outWrapper), null,
				context);
	}

	@SuppressWarnings("unchecked")
	private PushResponse handleOutParamsResponse(Message inboundMessage,
			List<Object> outParams) throws ServiceException,
			ServiceInvocationException {
		BaseServiceDispatchImpl.checkForErrors(
				(BaseMessageImpl) inboundMessage, m_msgContext, m_msgContext
						.getAdminName());
		BaseServiceDispatchImpl.getOutParams(inboundMessage, outParams);
		Map<String, Object> context = AsyncUtils.extractContext(inboundMessage);
		return new PushResponse(new RawDispatchData(null, outParams), null,
				context);
	}

}
