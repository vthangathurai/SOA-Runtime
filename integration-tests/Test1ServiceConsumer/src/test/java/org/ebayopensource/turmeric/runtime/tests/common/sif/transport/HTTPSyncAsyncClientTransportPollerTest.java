/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.tests.common.sif.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransportPoller;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.ebay.kernel.service.invocation.SvcChannelStatus;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClient;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClientImpl;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncResponseFuture;
import com.ebay.kernel.service.invocation.client.http.nio.NioHttpConfig;
import com.ebay.kernel.service.invocation.client.http.nio.NioSvcInvocationConfig;

public class HTTPSyncAsyncClientTransportPollerTest extends AbstractWithServerTest {
	private static NioAsyncHttpClient client = null;

    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	static {
		boolean proxyEnable = false;
		try {
			Socket socket = new Socket();
			InetSocketAddress address = new InetSocketAddress("skyline.qa.ebay.com", 80);
			socket.connect(address, 2000);
			proxyEnable = true;
		} catch (IOException e) {
		}

		NioSvcInvocationConfig nioSvcConfig = new NioSvcInvocationConfig(
				null, // BeanConfigCategory
				"HTTPSyncAsyncClientTransportPollerTest",
				SvcChannelStatus.MARK_UP);

		nioSvcConfig.createConnectionConfig(10000, 0);

		nioSvcConfig.createRequestConfig(Integer.valueOf(10000), null, // soLinger
				null, // soRcvBuf,
				null, // soSndBuf,
				Boolean.TRUE, // tcpNoDelay
				null); // soKeepLive

		NioHttpConfig nioHttpConfig = nioSvcConfig.createHttpConfig(true, // followHttpRedirect
				false, // enableKeepAlive
				false); // enableProxy

		if (proxyEnable) {
			nioHttpConfig.createProxyConfig("skyline.qa.ebay.com", null, "80", null,
					null);
			nioHttpConfig.setProxyEnabled(true);
		}
		client = new NioAsyncHttpClientImpl(nioSvcConfig);
	}

	@Test
	public void simpleTake() throws Exception {
		HTTPSyncAsyncClientTransportPoller completionQueue = new HTTPSyncAsyncClientTransportPoller();
		// NioAsyncHttpClient client = NioAsyncHttpClients.newClient(
		// "testSimpleTake", null, 10000);

		Request r1 = new Request("www.google.com", "/search");
		r1.addParameter("q", "jujube");

		client.send(r1, completionQueue);
		NioAsyncResponseFuture future = completionQueue.take();
		Assert.assertTrue(future.getRequest() == r1);
		future.get();
	}

	@Test
	public void simplePoll() throws Exception {
		HTTPSyncAsyncClientTransportPoller completionQueue = new HTTPSyncAsyncClientTransportPoller();
		// NioAsyncHttpClient client = NioAsyncHttpClients.newClient(
		// "testSimplePoll", null, 10000);

		Request r1 = new Request("www.google.com", "/search");
		r1.addParameter("q", "jujube");

		client.send(r1, completionQueue);

		NioAsyncResponseFuture future = null;
		for (int i = 0; i < 100 && future == null; i++) {
			future = completionQueue.poll();
			Thread.sleep(100);
		}

		Assert.assertNotNull(future);
		Assert.assertTrue(future.getRequest() == r1);
		future.get();
	}

	@Test
	public void simpleBlockingPoll() throws Exception {
		HTTPSyncAsyncClientTransportPoller completionQueue = new HTTPSyncAsyncClientTransportPoller();
		// NioAsyncHttpClient client = NioAsyncHttpClients.newClient(
		// "testSimpleBlockingPoll", null, 10000);

		Request r1 = new Request("www.google.com", "/search");
		r1.addParameter("q", "jujube");
		client.send(r1, completionQueue);

		List<Future<?>> futures = completionQueue.poll(true);
		Assert.assertTrue(futures.size() == 1);
		futures.get(0).get();
	}

	@Test
	public void simpleNonBlockingPoll() throws Exception {
		HTTPSyncAsyncClientTransportPoller completionQueue = new HTTPSyncAsyncClientTransportPoller();
		// NioAsyncHttpClient client = NioAsyncHttpClients.newClient(
		// "testSimpleNonBlockingPoll", null, 10000);

		Request r1 = new Request("www.google.com", "/search");
		r1.addParameter("q", "jujube");
		client.send(r1, completionQueue);

		List<Future<?>> futures = null;
		for (int i = 0; i < 100; i++) {
			futures = completionQueue.poll(false);
			if (futures.size() > 0)
				break;
			Thread.sleep(100);
		}
		Assert.assertTrue(futures.size() == 1);
		futures.get(0).get();
	}

	@Test
	public void blockingPoll() throws Exception {
		testPoll("testBlockingPoll", true);
	}

	@Test
	public void nonBlockingPoll() throws Exception {
		testPoll("testNonBlockingPoll", false);
	}

	
	private void testPoll(String name, boolean block) throws Exception {
		HTTPSyncAsyncClientTransportPoller completionQueue = new HTTPSyncAsyncClientTransportPoller();
		// NioAsyncHttpClient client = NioAsyncHttpClients.newClient(name, null,
		// 10000);

		Request r1 = new Request("www.google.com", "/search");
		r1.addParameter("q", "jujube");

		Request r2 = new Request("www.bing.com", "/search");
		r2.addParameter("q", "jujube");
		r2.addParameter("go", "");
		r2.addParameter("form", "QBLH");
		r2.addParameter("qs", "n");
		r2.addParameter("sk", "");
		r2.addParameter("sc", "8-4");
		
		Request r3 = new Request("search.yahoo.com", "/search");
		r3.addParameter("p", "jujube");
		r3.addParameter("fr", "yfp-t-471");
		r3.addParameter("toggle", "1");
		r3.addParameter("cop", "mss");
		r3.addParameter("ei", "UTF-8");

		Future<?> f1 = client.send(r1, completionQueue);
		Future<?> f2 = client.send(r2, completionQueue);
		Future<?> f3 = client.send(r3, completionQueue);

		int size = 0;
		List<Future<?>> futures = null;
		boolean r1Done = false, r2Done = false, r3Done = false;
		for (int i = 0; i < 300 && size < 3; i++) {
			futures = completionQueue.poll(block);
			if (block)
				Assert.assertTrue(futures.size() > 0);

			size += futures.size();
			for (Future<?> future : futures) {
				Request r = ((NioAsyncResponseFuture) future).getRequest();
				if (r == r1 && !r1Done)
					r1Done = true;
				else if (r == r2 && !r2Done)
					r2Done = true;
				else if (r == r3 && !r3Done)
					r3Done = true;
				else
					Assert.fail("Duplicate or unrecognized response");
			}
			futures = null;
			Thread.sleep(100);
		}

		Assert.assertTrue(size == 3);
		Assert.assertTrue(r1Done && r2Done && r3Done);

		f1.get();
		f2.get();
		f3.get();
	}
}
