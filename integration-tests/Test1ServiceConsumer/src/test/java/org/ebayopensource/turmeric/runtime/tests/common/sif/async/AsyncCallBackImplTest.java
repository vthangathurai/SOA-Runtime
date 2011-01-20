/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.TransportInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.AsyncCallBackImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.PushResponse;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class AsyncCallBackImplTest extends AbstractWithServerTest {
	private static HTTPSyncAsyncClientTransport m_transport;

	public HTTPSyncAsyncClientTransport getTransport() throws Exception {
		if (m_transport != null) {
			return m_transport;
		}
		HTTPSyncAsyncClientTransport transport = new HTTPSyncAsyncClientTransport();
		ClientServiceId id = new ClientServiceId("AsyncCallBackImplTest1",
				"local");
		TransportOptions options = new TransportOptions();
		options.getProperties().put(SOAConstants.HTTP_VERSION,
				SOAConstants.TRANSPORT_HTTP_11);

		TransportInitContextImpl initCtx = new TransportInitContextImpl(id,
				"Test_HTTP_Get", options);
		transport.init(initCtx);
		initCtx.kill();
		m_transport = transport;
		return m_transport;
	}

	@Test
	public void asyncCallBackForVoidReturnOperation() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myVoidReturnOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport,"myVoidReturnOperation");

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		ctx.setProperty(ClientMessageContext.REQUEST_SENT_TIME, System.nanoTime());

		TestHander<Object> handler = new TestHander<Object>();
		((ClientMessageContextImpl) ctx).setClientAsyncHandler(handler);

		AsyncCallBackImpl callBack = new AsyncCallBackImpl(
				((ClientMessageContextImpl) ctx));

		((ClientMessageContextImpl) ctx).setServiceAsyncCallback(callBack);

		TransportOptions options = new TransportOptions();

		Future<?> futureResp = transport.invokeAsync(ctx.getRequestMessage(),
				options);
		
		((ClientMessageContextImpl) ctx).setFutureResponse(futureResp);

		while (!futureResp.isDone()) {
			Thread.sleep(200);
		}

		Assert.assertTrue(handler.getReturn() instanceof PushResponse);

		Assert.assertTrue(handler.getError() instanceof ExecutionException);
		Assert.assertTrue(handler.getError().getCause() instanceof ServiceException);
		Assert.assertTrue(handler.getError().getCause().getMessage()
				.contains("Unable to access parameter values on void message"));

	}

	@Test
	public void asyncCallBackOnException() throws Exception {

		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport);

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		TestHander<Object> handler = new TestHander<Object>();
		((ClientMessageContextImpl) ctx).setClientAsyncHandler(handler);

		final AsyncCallBackImpl callBack = new AsyncCallBackImpl(
				((ClientMessageContextImpl) ctx));

		((ClientMessageContextImpl) ctx).setServiceAsyncCallback(callBack);

		Runnable task = new Runnable() {
			public void run() {
				callBack.onException(new RuntimeException(
						"Exception Propogation"));
			}
		};

		new Thread(task).start();

		while (!handler.isDone()) {
			Thread.sleep(200);
		}

		Assert.assertTrue(handler.getReturn() instanceof PushResponse);

		Assert.assertTrue(handler.getError() instanceof ExecutionException);
		Assert
				.assertTrue(handler.getError().getCause() instanceof RuntimeException);
		Assert.assertTrue(handler.getError().getCause().getMessage()
				.contains("Exception Propogation"));
	}

	@Test
	public void asyncCallBackOnTime() throws Exception {

		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport);

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		TestHander<Object> handler = new TestHander<Object>();
		((ClientMessageContextImpl) ctx).setClientAsyncHandler(handler);

		final AsyncCallBackImpl callBack = new AsyncCallBackImpl(
				((ClientMessageContextImpl) ctx));

		((ClientMessageContextImpl) ctx).setServiceAsyncCallback(callBack);

		Runnable task = new Runnable() {
			public void run() {
				callBack.onTimeout();
			}
		};

		new Thread(task).start();

		while (!handler.isDone()) {
			Thread.sleep(200);
		}

		Assert.assertTrue(handler.getReturn() instanceof PushResponse);

		Assert.assertTrue(handler.getError() instanceof ExecutionException);
		Assert.assertTrue(handler.getError().getCause() instanceof ServiceException);
		Assert.assertTrue(handler.getError().getCause().getMessage()
				.contains("Transport communication failure for target address"));
	}

	private static class TestHander<T> implements AsyncHandler<T> {
		private Response<T> m_resp;
		private Throwable m_error;
		private boolean m_done = false;

		public void handleResponse(Response<T> resp) {
			try {
				m_resp = resp;
				m_resp.get();
			} catch (Throwable e) {
				m_error = e;
			} finally {
				m_done = true;
			}
		}

		public Response<T> getReturn() {
			return m_resp;
		}

		public Throwable getError() {
			return m_error;
		}

		public boolean isDone() {
			return m_done;
		}
	}

}
