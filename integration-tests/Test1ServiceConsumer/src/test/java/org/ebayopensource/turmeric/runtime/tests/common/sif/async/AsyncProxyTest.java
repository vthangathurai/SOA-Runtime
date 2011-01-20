/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithProxyServerTest;
import org.junit.Assert;
import org.junit.Test;


public class AsyncProxyTest extends AbstractWithProxyServerTest {
	
	private final String ECHO_STRING = "BH Test String";

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteSync() throws Exception {
		Service service = createProxiedService("test1", "proxyTransport");
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteAsyncPull() throws Exception {
		Service service = createProxiedService("test1", "proxyTransport");
		Response<String> resp = service.createDispatch("echoString")
				.invokeAsync(ECHO_STRING);
		String outMessage = resp.get();
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteAsyncPush() throws Exception {
		Service service = createProxiedService("test1", "proxyTransport");

		Handler handler = new Handler();

		Future<?> status = service.createDispatch("echoString").invokeAsync(
				ECHO_STRING, handler);

		while (!status.isDone()) {
			Thread.sleep(200);
		}

		String outMessage = handler.getRespString();
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	private class Handler implements AsyncHandler<String> {

		private String m_respString = null;

		public void handleResponse(Response<String> resp) {
			try {
				m_respString = resp.get();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		public String getRespString() {
			return m_respString;
		}
	}

}
