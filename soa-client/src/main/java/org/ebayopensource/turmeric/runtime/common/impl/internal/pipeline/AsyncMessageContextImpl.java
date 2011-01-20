/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;


public class AsyncMessageContextImpl implements AsyncMessageContext {
	private AsyncHandler m_clientAsyncHandler;

	private AsyncCallBack m_serviceAsyncCallback;

	private Future<?> m_futureResponse;

	private Executor m_executor;

	public void setClientAsyncHandler(AsyncHandler handler) {
		m_clientAsyncHandler = handler;
	}

	public AsyncHandler getClientAsyncHandler() {
		return m_clientAsyncHandler;
	}

	public Future<?> getFutureResponse() {
		return m_futureResponse;
	}

	public void setFutureResponse(Future<?> futureResponse) {
		m_futureResponse = futureResponse;
	}

	public Executor getExecutor() {
		return m_executor;
	}

	public void setExecutor(Executor executor) {
		m_executor = executor;
	}

	public void setServiceAsyncCallback(AsyncCallBack callback) {
		m_serviceAsyncCallback = callback;
	}

	public AsyncCallBack getServiceAsyncCallback() {
		return m_serviceAsyncCallback;
	}
}
