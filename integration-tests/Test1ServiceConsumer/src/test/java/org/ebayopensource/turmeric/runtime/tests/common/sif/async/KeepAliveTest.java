/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import static org.hamcrest.Matchers.greaterThan;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithProxyServerTest;
import org.junit.Assert;
import org.junit.Test;


public class KeepAliveTest extends AbstractWithProxyServerTest {
	private final String ECHO_STRING = "BH Test String";

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchSimpleKeepAlive() throws Exception {
		Service service = ServiceFactory.create("Test1Service", "keepAlive", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchSimpleKeepAlive10Consecutive() throws Exception {
		final int ITERATIONS = 10;
		
		// Send first just to prep config
		final Service keepAlive = ServiceFactory.create("Test1Service", "keepAlive", serverUri.toURL());
		{
			String outMessage = (String) keepAlive.createDispatch("echoString")
					.invoke(ECHO_STRING);
			Assert.assertEquals(ECHO_STRING, outMessage);
		}
		// Send first just to prep config
		final Service nonkeepAlive = ServiceFactory.create("Test1Service", "nonkeepAlive", serverUri.toURL());
		{
			String outMessage = (String) nonkeepAlive.createDispatch(
					"echoString").invoke(ECHO_STRING);
			Assert.assertEquals(ECHO_STRING, outMessage);
			outMessage = (String) keepAlive.createDispatch("echoString").invoke(ECHO_STRING);
			Assert.assertEquals(ECHO_STRING, outMessage);
		}

		long nonkeepAliveStartTime = System.nanoTime();
		for (int i = 0; i < ITERATIONS; i++) {
			String outMessage = (String) nonkeepAlive.createDispatch(
					"echoString").invoke(ECHO_STRING);
			Assert.assertEquals(ECHO_STRING, outMessage);
		}
		long nonkeepAliveEndTime = System.nanoTime();
		long nonkeepAliveDuration = nonkeepAliveEndTime - nonkeepAliveStartTime;
		
		long keepAliveStartTime = System.nanoTime();
		for (int i = 0; i < ITERATIONS; i++) {
			String outMessage = (String) keepAlive.createDispatch("echoString")
					.invoke(ECHO_STRING);
			Assert.assertEquals(ECHO_STRING, outMessage);
		}
		long keepAliveEndTime = System.nanoTime();
		long keepAliveDuration = keepAliveEndTime - keepAliveStartTime;

		System.out.printf("Duration (nano-time for %d records):%n", ITERATIONS);
		System.out.printf("   Non-Keep-Alive: %,13d ns%n", nonkeepAliveDuration);
		System.out.printf("       Keep-Alive: %,13d ns%n", keepAliveDuration);
		System.out.printf("             Diff: %,13d ns%n", (nonkeepAliveDuration - keepAliveDuration));

		Assert.assertThat("Non-Keep-Alive Duration > Keep-Alive Duration", 
				nonkeepAliveDuration, greaterThan(keepAliveDuration));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConsecutiveErrors() throws Exception {
		Service service = createProxiedService("Test1Service", "flakyWithHttpSyncAsyncTransportForKeepAlive");
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}
}
