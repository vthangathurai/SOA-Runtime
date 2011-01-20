/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.SimpleTransportDispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageContextImpl;


public class SimpleServerResponseDispatcher extends SimpleTransportDispatcher {
	
	public SimpleServerResponseDispatcher(boolean shouldCleanupData) {
		super(shouldCleanupData);
	}

	@Override
	protected void invokeTransport(MessageContext ctx, Object transportData) throws ServiceException {
		ServerMessageContextImpl serverCtx = (ServerMessageContextImpl) ctx;
		Message rspMessage = serverCtx.getResponseMessage();
		rspMessage.setTransportData(transportData);
			
		long startTime = System.nanoTime();
		try {
			// TODO - server side may need to pass transport options also?
			serverCtx.getTransport().invoke(rspMessage, null);
		} finally {
			long duration = System.nanoTime() - startTime;
			serverCtx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_RESP_DISPATCH, startTime, duration);			
		}		
	}

	@Override
	protected void doPreTransportMapping(MessageContext ctx) throws ServiceException {
		// noop
	}

	@Override
	protected Future<?> invokeAsyncTransport(MessageContext ctx, Object transportData) throws ServiceException {
		throw new UnsupportedOperationException("SimpleServerResponseDispatcher does not support invokeAsyncTransport");
	}

	@Override
	protected void retrieveTransport(MessageContext ctx, Future<?> futureResp) throws ServiceException {
		throw new UnsupportedOperationException("SimpleServerResponseDispatcher does not support retrieveTransport");
	}
}
