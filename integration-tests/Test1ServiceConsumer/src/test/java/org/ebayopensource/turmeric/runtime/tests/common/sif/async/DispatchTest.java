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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.Future;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.service.BaseAsyncPushHandler;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class DispatchTest extends AbstractWithServerTest {

	private final String ECHO_STRING = "BH Test String";
	
	@Rule
	public NeedsConfig needsconfig = new NeedsConfig("config");

	@Test
	@SuppressWarnings("unchecked")
	public void dispatchLocalSync() throws Exception {
		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void dispatchRemoteSync() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void dispatchLocalAsyncPull() throws Exception {
		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());
		Response<String> resp = service.createDispatch("echoString")
				.invokeAsync(ECHO_STRING);
		String outMessage = resp.get();
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void dispatchRemoteAsyncPull() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		Response<String> resp = service.createDispatch("echoString")
				.invokeAsync(ECHO_STRING);
		String outMessage = resp.get();
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void dispatchLocalAsyncPush() throws Exception {
		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());

		Handler handler = new Handler();
		String request = new String(ECHO_STRING);

		Future<?> status = service.createDispatch("echoString").invokeAsync(
				request, handler);
		
		while (!status.isDone() || handler.getRespString().equals("notdone")) {
			Thread.sleep(200);
		}

		String outMessage = handler.getRespString();
		Assert.assertEquals(ECHO_STRING, outMessage);
		Assert.assertTrue(handler.getRequest() == request);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void dispatchRemoteAsyncPush() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());

		Handler handler = new Handler();
		String request = new String(ECHO_STRING);

		Future<?> status = service.createDispatch("echoString").invokeAsync(
				request, handler);

		while (!status.isDone() || handler.getRespString().equals("notdone")) {
			Thread.sleep(200);
		}
		
		String outMessage = handler.getRespString();
		Assert.assertEquals(ECHO_STRING, outMessage);
		Assert.assertTrue(handler.getRequest() == request);
	}

	@Test
	public void dispatchRemoteSync_InvalidUrl() throws Exception {
		invalidUrl_Internal(TestMode.ASYNC_SYNC);
	}

	@Test
	public void dispatchRemoteAsyncPull_InvalidUrl() throws Exception {
		invalidUrl_Internal(TestMode.ASYNC_PULL);
	}

	@Test
	public void dispatchRemoteAsyncPush_InvalidUrl() throws Exception {
		invalidUrl_Internal(TestMode.ASYNC_PUSH);
	}

	@SuppressWarnings("unchecked")
	private void invalidUrl_Internal(TestMode mode) throws Exception {
		Service service = ServiceFactory.create("test1", "invalidUrl", null);
		@SuppressWarnings("rawtypes")
		Dispatch dispatch = service.createDispatch("echoString");

		try {
			if (mode.equals(TestMode.ASYNC_SYNC))
				dispatch.invoke(ECHO_STRING);
			else if (mode.equals(TestMode.ASYNC_PULL))
				dispatch.invokeAsync(ECHO_STRING);
			else if (mode.equals(TestMode.ASYNC_PUSH))
				dispatch.invokeAsync(ECHO_STRING, new Handler());
			Assert.fail("Should throw expection. Invalid url is configured in" +
					" ClientConfig.xml, Please check");
		} catch (WebServiceException e) {
			Throwable cause = e.getCause();
			Assert.assertTrue(cause instanceof ServiceInvocationException);
			String expected = "Transport communication failure for target address";
			Assert.assertThat(e.getMessage(), allOf(containsString(expected),
					containsString(UnresolvedAddressException.class.getName())));
		}
	}

	@Test
	public void dispatchRemoteSync_withHandlerInvokeException()
			throws Exception {
		withHandlerException_Internal(TestMode.ASYNC_SYNC);
	}

	@Test
	public void dispatchRemoteAsyncPull_withHandlerInvokeException()
			throws Exception {
		withHandlerException_Internal(TestMode.ASYNC_PULL);
	}

	@Test
	public void dispatchRemoteAsyncPush_withHandlerInvokeException()
			throws Exception {
		withHandlerException_Internal(TestMode.ASYNC_PUSH);
	}

	@SuppressWarnings("unchecked")
	private void withHandlerException_Internal(TestMode mode) throws Exception {
		Service service = ServiceFactory.create("test1", "handler", serverUri.toURL());
		@SuppressWarnings("rawtypes")
		Dispatch dispatch = service.createDispatch("echoString");
		try {
			if (mode.equals(TestMode.ASYNC_SYNC)) {
				dispatch.invoke(ECHO_STRING);
			} else if (mode.equals(TestMode.ASYNC_PULL)) {
				dispatch.invokeAsync(ECHO_STRING);
			} else if (mode.equals(TestMode.ASYNC_PUSH)) {
				dispatch.invokeAsync(ECHO_STRING, new Handler());
			}
			Assert.fail("Should throw expection because the handler that is configured in clientconfig.xml throws Exception, Pl check");
		} catch (WebServiceException e) {
			Throwable t = e.getCause();
			Assert.assertTrue(t instanceof ServiceInvocationException);
			Assert.assertEquals(t.getCause().getMessage(), "Testing handler exception");
		}
	}

	private class Handler extends BaseAsyncPushHandler<String> {

		private String m_respString = "notdone";

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
