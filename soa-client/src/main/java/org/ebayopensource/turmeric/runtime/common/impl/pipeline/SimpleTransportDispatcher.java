/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;


/**
 * Simple dispatcher implementation, invoking Transport to send the data
 * 
 * @author ichernyshev
 */
public abstract class SimpleTransportDispatcher implements Dispatcher {

	private final boolean m_shouldCleanupData;

	public SimpleTransportDispatcher(boolean shouldCleanupData) {
		m_shouldCleanupData = shouldCleanupData;
	}

	public void dispatchSynchronously(MessageContext ctx)
			throws ServiceException {
		dispatchInternal(ctx);
	}

	public Future<?> dispatch(MessageContext ctx) throws ServiceException {
		BaseMessageContextImpl ctxImpl = (BaseMessageContextImpl) ctx;

		doPreTransportMapping(ctx);
		Transport transport = ctxImpl.getTransport();
		Object transportData = transport.preInvoke(ctx);

		if (m_shouldCleanupData) {
			ctxImpl.cleanupRequestData();
		}
		return invokeAsyncTransport(ctx, transportData);
	}

	private void dispatchInternal(MessageContext ctx) throws ServiceException {
		BaseMessageContextImpl ctxImpl = (BaseMessageContextImpl) ctx;

		doPreTransportMapping(ctx);
		Transport transport = ctxImpl.getTransport();
		Object transportData = transport.preInvoke(ctx);

		if (m_shouldCleanupData) {
			ctxImpl.cleanupRequestData();
		}

		invokeTransport(ctx, transportData);
	}

	public void retrieve(MessageContext ctx, Future<?> futureResp)
			throws ServiceException {
		retrieveTransport(ctx, futureResp);
	}

	abstract protected void retrieveTransport(MessageContext ctx,
			Future<?> futureResp) throws ServiceException;

	abstract protected void invokeTransport(MessageContext ctx,
			Object transportData) throws ServiceException;

	abstract protected Future<?> invokeAsyncTransport(MessageContext ctx,
			Object transportData) throws ServiceException;

	abstract protected void doPreTransportMapping(MessageContext ctx)
			throws ServiceException;
}
