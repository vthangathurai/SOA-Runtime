/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.SimpleTransportDispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;


public class SimpleClientRequestDispatcher extends SimpleTransportDispatcher {

	public SimpleClientRequestDispatcher(boolean shouldCleanupData) {
		super(shouldCleanupData);
	}

	@Override
	protected void invokeTransport(MessageContext ctx, Object transportData)
			throws ServiceException {
		Message reqMessage = ctx.getRequestMessage();
		reqMessage.setTransportData(transportData);

		ClientMessageContextImpl clientCtx = (ClientMessageContextImpl) ctx;
		TransportOptions options = clientCtx.getTransportOptions();

		clientCtx
				.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_DISPATCH_START);
		invoke(reqMessage, clientCtx, options);
	}

	@Override
	protected Future<?> invokeAsyncTransport(MessageContext ctx,
			Object transportData) throws ServiceException {
		Message reqMessage = ctx.getRequestMessage();
		reqMessage.setTransportData(transportData);

		ClientMessageContextImpl clientCtx = (ClientMessageContextImpl) ctx;
		TransportOptions options = clientCtx.getTransportOptions();

		clientCtx
				.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_DISPATCH_ASYNC_SEND_START);
		Future<?> future = invokeAsync(reqMessage, clientCtx, options);
		clientCtx
				.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_DISPATCH_ASYNC_SEND_COMPLETE);
		return future;
	}

	@Override
	protected void retrieveTransport(MessageContext ctx, Future<?> futureResp)
			throws ServiceException {
		ClientMessageContextImpl clientCtx = (ClientMessageContextImpl) ctx;
		try {
			clientCtx
					.runLoggingHandlerStage(LoggingHandlerStage.RESPONSE_DISPATCH_ASYNC_RECEIVE_START);
			clientCtx.getTransport().retrieve(ctx, futureResp);
		} finally {
			Long startTime = (Long) clientCtx
					.getProperty(ClientMessageContext.REQUEST_SENT_TIME);
			if (startTime == null) {
				startTime = new Long(System.nanoTime());
			}
			long duration = System.nanoTime() - startTime.longValue();
			clientCtx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_CALL,
					startTime.longValue(), duration);

			clientCtx
					.runLoggingHandlerStage(LoggingHandlerStage.RESPONSE_DISPATCH_ASYNC_RECEIVE_COMPLETE);
		}
	}

	private void invoke(Message reqMessage, ClientMessageContextImpl clientCtx,
			TransportOptions options) throws ServiceException {
		long startTime = System.nanoTime();
		try {
			clientCtx.getTransport().invoke(reqMessage, options);
		} finally {
			long duration = System.nanoTime() - startTime;
			clientCtx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_CALL,
					startTime, duration);

			clientCtx
					.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_DISPATCH_COMPLETE);
		}
	}

	private Future<?> invokeAsync(Message reqMessage,
			ClientMessageContextImpl clientCtx, TransportOptions options)
			throws ServiceException {
		Long startTime = new Long(System.nanoTime());
		Future<?> futureResponse = null;

		clientCtx
				.setProperty(ClientMessageContext.REQUEST_SENT_TIME, startTime);
		futureResponse = clientCtx.getTransport().invokeAsync(reqMessage,
				options);

		return futureResponse;
	}

	@Override
	protected void doPreTransportMapping(MessageContext ctx)
			throws ServiceException {
		// noop
	}

}
