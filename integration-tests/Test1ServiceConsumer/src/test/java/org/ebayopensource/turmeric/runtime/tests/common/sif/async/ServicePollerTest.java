/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.lang.Thread.State;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ITransportPoller;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ServicePoller;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransportPoller;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.local.LocalTransportPoller;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Test;

import com.ebay.kernel.service.invocation.client.exception.BaseClientSideException;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.nio.CompletionQueue;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClient;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClients;

public class ServicePollerTest extends AbstractWithServerTest {

	static final NioAsyncHttpClient client = NioAsyncHttpClients.newClient(
			"ServicePollerTest", null, 10000);

	@Test
	public void getSetOfTransportPoller() throws Exception {
		IAsyncResponsePoller poller = getPoller();
		Assert.assertTrue(poller.poll(false, false, -1).size() == 0);

		stimulatePollReq1(poller);
		Assert.assertTrue(poller.poll(false, false, -1).size() == 1);

		stimulatePollReq1(poller);

		ITransportPoller anotherTP = new LocalTransportPoller();
		poller.setTransportPoller(anotherTP);
		Assert.assertNotNull(anotherTP);
		Assert.assertTrue(anotherTP == poller.getTransportPoller());
		Assert.assertTrue(poller.poll(false, false, -1).size() == 0);
	}

	@Test
	public void simplePollBlock() throws Exception {

		IAsyncResponsePoller poller = getPoller();

		stimulatePollReq1(poller);
		Assert.assertTrue(poller.poll(true, true, -1).size() == 1);
	}

	@Test
	public void simplePollNoBlock() throws Exception {
		IAsyncResponsePoller poller = getPoller();

		stimulatePollReq1(poller);
		int size = 0;
		for (int i = 0; i < 100; i++) {
			size += poller.poll(false, true, -1).size();
			if (size == 1)
				break;
			Thread.sleep(20);
		}
		Assert.assertTrue(size == 1);
	}

	@Test
	public void simplePollNoPartial() throws Exception {

		IAsyncResponsePoller poller = getPoller();

		stimulatePollReq1(poller);
		stimulatePollReq1(poller);
		Assert.assertTrue(poller.poll(true, false, -1).size() == 2);

		stimulatePollReq1(poller);
		stimulatePollReq1(poller);
		Assert.assertTrue(poller.poll(false, false, -1).size() == 2);
	}

	@Test
	public void pollBlock() throws Exception {
		testPollNoTimeout(10, true, true);
	}

	@Test
	public void noPollBlock() throws Exception {
		testPollNoTimeout(10, false, true);
	}

	@Test
	public void noPartial() throws Exception {
		testPollNoTimeout(10, false, false);
		testPollNoTimeout(10, true, false);
	}

	@Test
	public void pollBlockTimout() throws Exception {
		pollwithShortTimeout(true, true);
		pollwithLongTimeout(true, true);
	}

	@Test
	public void noPollBlockTimeout() throws Exception {
		pollwithShortTimeout(false, true);
		pollwithLongTimeout(false, true);
	}

	@Test
	public void noPartialTimeout() throws Exception {
		pollwithShortTimeout(false, false);
		pollwithLongTimeout(false, false);
		pollwithShortTimeout(true, false);
		pollwithLongTimeout(true, false);
	}

	@Test
	public void nonBlockWithBlockFlagAndNoRequests() throws Exception {

		Thread tester = new Thread(new Runnable() {

			public void run() {
				try {
					testPollNoTimeout(0, true, true);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		tester.start();
		
		tester.join(1000);
		
		if(!tester.getState().equals(State.TERMINATED))
			Assert.fail(String.format("For zero requests polls needs to return immediately, thread state is %s",tester.getState().toString()));
			
	}


	private void testPollTimeout(IAsyncResponsePoller poller) throws Exception {
		for (int i = 0; i < 25; i++) {
			stimulatePollReq1(poller);
		}
	}

	private IAsyncResponsePoller getPoller() {
		IAsyncResponsePoller poller = new ServicePoller();

		// Initially transport poller needs to be null
		Assert.assertNull(poller.getTransportPoller());

		poller.setTransportPoller(new HTTPSyncAsyncClientTransportPoller());
		Assert.assertNotNull(poller.getTransportPoller());
		return poller;
	}

	
	private void testPollNoTimeout(int size, boolean block, boolean partial)
			throws Exception {
		IAsyncResponsePoller poller = getPoller();

		for (int i = 0; i < size; i++) {
			stimulatePollReq1(poller);
		}

		if (!partial) {
			int total = poller.poll(block, partial, -1).size();
			verifyResults(size, block, partial, poller, total);
		} else {
			int total = 0;
			for (int i = 0; i < 100; i++) {
				total += poller.poll(block, partial, -1).size();
				if (total == size)
					break;
				Thread.sleep(20);
			}
			verifyResults(size, block, partial, poller, total);
		}
	}

	private void verifyResults(int size, boolean block, boolean partial,
			IAsyncResponsePoller poller, int total) throws InterruptedException {
		System.out.printf("\ntestPollNoTimeout(%b,%b): Sent %d and got %d",
				block, partial, size, total);
		Assert.assertTrue(total == size);
		Assert.assertTrue(poller.poll(false, false, 100).size() == 0);
		System.out.printf("\ntestPollNoTimeout(%b,%b): queue empty verified",
				block, partial);
	}

	private void stimulatePollReq1(IAsyncResponsePoller poller)
			throws BaseClientSideException {
		// http://localhost:8080/ws/spf?wsdl&X-TURMERIC-SERVICE-NAME=Test1Service
		URI uri = serverUri.resolve("?wsdl");
		Request r1 = new Request(uri.getHost() + ":" + uri.getPort(), uri.getPath());
		r1.addParameter("X-TURMERIC-SERVICE-NAME", "Test1Service");

		sendRequest(poller, client, r1);
	}

	private void sendRequest(IAsyncResponsePoller poller,
			NioAsyncHttpClient client, Request req)
			throws BaseClientSideException {
		Future<?> future = client.send(req, (CompletionQueue) poller
				.getTransportPoller());

		poller.add(future, new TestResponse(future));
	}

	private void pollwithLongTimeout(boolean block, boolean partial)
			throws Exception {
		IAsyncResponsePoller poller = getPoller();
		testPollTimeout(poller);
		int total = (poller.poll(block, partial, 500).size());
		System.out.printf("%npollwithLongTimeout(%b,%b): Sent 25 and got %d%n", block, partial, total);
		Assert.assertThat("poller.poll(block,partial,500).size", total, is(25));
		Assert.assertThat("poller.poll(false, false, 100).size()", poller.poll(false, false, 100).size(), is(0));
		System.out.printf("%npollwithLongTimeout(%b,%b): queue empty verified%n", block, partial);
	}

	@SuppressWarnings("unchecked")
	private void pollwithShortTimeout(boolean block, boolean partial)
			throws Exception, InterruptedException {
		IAsyncResponsePoller poller = getPoller();
		testPollTimeout(poller);
		int size = 0;
		int total = 0;
		for (int i = 0; i < 20 && total < 25; i++) {
			size = poller.poll(block, partial, 10).size();
			Assert.assertThat("poller.poll.size", size, lessThanOrEqualTo(25));
			total += size;
			Thread.sleep(10);
		}
		System.out.printf("%npollwithShortTimeout(%b,%b): Sent 25 and got %d%n", block, partial, total);
		Assert.assertThat("Total", total, allOf(greaterThan(1),lessThanOrEqualTo(25)));
		Assert.assertThat("Poll.size", poller.poll(false, false, 100).size(), is(0));
		System.out.printf("%npollwithShortTimeout(%b,%b): queue empty verified%n", block, partial);
	}

	@SuppressWarnings("rawtypes")
	private static class TestResponse implements Response {
		private final Future<?> m_future;

		private Object m_response;

		private boolean m_isDone = false;

		public TestResponse(Future<?> future) {
			m_future = future;
		}

		public Map getContext() {
			return null;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public Object get() throws InterruptedException, ExecutionException {
			if (m_isDone)
				return m_response;
			try {
				return (m_response = m_future.get());
			} finally {
				m_isDone = true;
			}
		}

		public Object get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			return get();
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return m_isDone;
		}

	}

}
