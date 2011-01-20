/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.transport;

import java.net.URL;
import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.TransportInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HTTPSyncAsyncClientTransportGetTest extends AbstractWithServerTest {
	private static HTTPSyncAsyncClientTransport m_transport;

    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	public synchronized HTTPSyncAsyncClientTransport getTransport()
			throws Exception {
		if (m_transport != null) {
			return m_transport;
		}
		HTTPSyncAsyncClientTransport transport = new HTTPSyncAsyncClientTransport();
		ClientServiceId id = new ClientServiceId(
				"HTTPSyncAsyncClientTransportTest1", "local");
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
	public void httpClientInvoke() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local");

		TransportOptions options = new TransportOptions();

		transport.invoke(ctx.getRequestMessage(), options);
		InboundMessageImpl response = (InboundMessageImpl) ctx
				.getResponseMessage();
		response.recordPayload(32768);
		byte[] data = response.getRecordedData();
		String dataStr = new String(data);
		System.out.println(dataStr);
	}

	@Test
	public void httpClientInvokeAsyncForPull() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local");

		TransportOptions options = new TransportOptions();

		Future<?> futureResp = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		InboundMessageImpl response = (InboundMessageImpl) ctx
				.getResponseMessage();
		response.recordPayload(32768);

		byte[] data = response.getRecordedData();

		Assert.assertNull(data);

		transport.retrieve(ctx, futureResp);

		data = response.getRecordedData();

		Assert.assertNotNull(data);

		String dataStr = new String(data);
		System.out.println(dataStr);
	}

	@Test
	public void httpClientInvokeAsyncForPushWithOutRetrieve()
			throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local");

		TransportOptions options = new TransportOptions();

		AsyncCB cb = new AsyncCB();

		((ClientMessageContextImpl) ctx).setServiceAsyncCallback(cb);

		Future<?> future = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		while (!cb.isDone()) {
			Thread.sleep(200);
		}

		Assert.assertTrue(future.isDone());
		Assert.assertTrue(cb.isSuccess());
		Assert.assertNull(cb.getError());
		Assert.assertFalse(cb.isTimeOut());

		InboundMessageImpl response = (InboundMessageImpl) ctx
				.getResponseMessage();
		response.recordPayload(32768);

		byte[] data = response.getRecordedData();

		Assert.assertNotNull(data);

		String dataStr = new String(data);
		System.out.println(dataStr);
	}

	@Test
	public void httpClientInvokeAsyncForPushWithRetrieve() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 2048, "local");

		TransportOptions options = new TransportOptions();

		AsyncCB cb = new AsyncCBWithCtx(ctx, transport);

		((ClientMessageContextImpl) ctx).setServiceAsyncCallback(cb);

		Future<?> future = transport.invokeAsync(ctx.getRequestMessage(),
				options);

		while (!cb.isDone()) {
			Thread.sleep(200);
		}

		Assert.assertTrue(future.isDone());
		Assert.assertTrue(cb.isSuccess());
		Assert.assertNull(cb.getError());
		Assert.assertFalse(cb.isTimeOut());

		InboundMessageImpl response = (InboundMessageImpl) ctx
				.getResponseMessage();
		response.recordPayload(32768);

		byte[] data = response.getRecordedData();

		Assert.assertNotNull(data);

		String dataStr = new String(data);
		System.out.println(dataStr);
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void httpClientBadLength() throws Exception {
		HTTPSyncAsyncClientTransport transport = getTransport();
		URL serviceURL = serverUri.toURL();
		MessageContext ctx = TestUtils.createClientMessageContextForHttpGet(
				TestUtils.createTestMessage(), serviceURL, 5, "local");

		TransportOptions options = new TransportOptions();

		boolean gotException = false;
		try {
			transport.invoke(ctx.getRequestMessage(), options);
		} catch (ServiceException e) {
			gotException = true;
			Assert.assertEquals(e.getMessage().substring(0, 19),
					"REST URL has length");
		}
		Assert.assertTrue(gotException);
	}

	private static class AsyncCB implements AsyncCallBack {

		protected Throwable m_error;
		private boolean m_success = false;
		private boolean m_timeOut = false;
		private boolean m_isDone = false;

		public void onException(Throwable cause) {
			m_error = cause;
			m_isDone = true;
		}

		public void onResponseInContext() {
			m_isDone = true;
			m_success = true;
		}

		public void onTimeout() {
			m_timeOut = true;
			m_isDone = true;
		}

		public Throwable getError() {
			return m_error;
		}

		public boolean isSuccess() {
			return m_success;
		}

		public boolean isTimeOut() {
			m_isDone = true;
			return m_timeOut;
		}

		public boolean isDone() {
			return m_isDone;
		}

		@Override
		public void onResponseInContext(RunBefore runBefore) {
			// TODO Auto-generated method stub
			
		}
	}

	private static class AsyncCBWithCtx extends AsyncCB {
		private final MessageContext m_ctx;
		private final Transport m_transport;

		AsyncCBWithCtx(MessageContext ctx, Transport transport) {
			m_ctx = ctx;
			m_transport = transport;
		}

		@Override
		public void onResponseInContext() {
			try {
				m_transport.retrieve(m_ctx, null);
				super.onResponseInContext();
			} catch (ServiceException e) {
				m_error = e;
			}
		}
	}

}
