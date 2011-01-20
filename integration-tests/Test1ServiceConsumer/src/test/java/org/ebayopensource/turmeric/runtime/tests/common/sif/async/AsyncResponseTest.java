/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.TransportInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.AsyncResponse;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class AsyncResponseTest extends AbstractWithServerTest {
	private static HTTPSyncAsyncClientTransport m_transport;

	public HTTPSyncAsyncClientTransport getTransport() throws Exception {
		if (m_transport != null) {
			return m_transport;
		}
		HTTPSyncAsyncClientTransport transport = new HTTPSyncAsyncClientTransport();
		ClientServiceId id = new ClientServiceId("TestAsyncResponseTest1",
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

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void testAsyncResponseDone() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport);

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		TransportOptions options = new TransportOptions();

		Future<?> futureResp = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		((ClientMessageContextImpl) ctx).setFutureResponse(futureResp);

		@SuppressWarnings("rawtypes")
		AsyncResponse asyncResp = new AsyncResponse(
				((ClientMessageContextImpl) ctx));

		try {
			asyncResp.get();
		} catch (ExecutionException e) {
			// expected exception
		}

		Assert.assertTrue(futureResp.isDone() && asyncResp.isDone());
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void testAsyncResponseVoidReturnException() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myVoidReturnOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport, "myVoidReturnOperation");

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		TransportOptions options = new TransportOptions();

		Future<?> futureResp = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		ctx.setProperty(ClientMessageContext.REQUEST_SENT_TIME, System
				.nanoTime());

		((ClientMessageContextImpl) ctx).setFutureResponse(futureResp);

		@SuppressWarnings("rawtypes")
		AsyncResponse asyncResp = new AsyncResponse(
				((ClientMessageContextImpl) ctx));

		try {
			asyncResp.get();
			fail("Expected Execution Exception with cause ServiceException: "
					+ "Unable to access parameter values on void message");
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			Assert.assertTrue(cause instanceof ServiceException);
			Assert.assertTrue(cause.getMessage().contains(
					"Unable to access parameter values on void message"));
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void testAsyncResponseWithTimeout() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?serviceChainingOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport);

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		ctx.setProperty(ClientMessageContext.REQUEST_SENT_TIME, System
				.nanoTime());

		TransportOptions options = new TransportOptions();

		Future<?> futureResp = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		((ClientMessageContextImpl) ctx).setFutureResponse(futureResp);

		@SuppressWarnings("rawtypes")
		AsyncResponse asyncResp = new AsyncResponse(
				((ClientMessageContextImpl) ctx));

		try {
			asyncResp.get(0, TimeUnit.NANOSECONDS);
			fail("Expected Timeout Exception");
		} catch (TimeoutException e) {
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
        //@Ignore // The test case has to be evaluated. Seems like a wrong test case.
	public void testAsyncResponseWithCancel() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local",
				transport);

		ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED, System
				.nanoTime());

		TransportOptions options = new TransportOptions();

		Future<?> futureResp = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		((ClientMessageContextImpl) ctx).setFutureResponse(futureResp);

		@SuppressWarnings("rawtypes")
		AsyncResponse asyncResp = new AsyncResponse(
				((ClientMessageContextImpl) ctx));
		try {
			asyncResp.cancel(true);
			// we can't really time this so that the task will be cancelled or completed
			// by the time we check. we can put in a random wait but it still wont tell us
			// if it got a cancel request atleast.
		} catch (UnsupportedOperationException e) {
			fail("Expected successfull cancellation");
		}
	}

}
