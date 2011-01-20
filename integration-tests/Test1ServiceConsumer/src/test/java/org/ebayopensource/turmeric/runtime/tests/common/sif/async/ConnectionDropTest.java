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

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithProxyServerTest;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionDropTest extends AbstractWithProxyServerTest {
	private final String ECHO_STRING = "BH Test String";
	
	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteSyncWithSyncAsyncTransport() throws Exception {
		Service service = createProxiedService("Test1Service", "flakyWithHttpSyncAsyncTransport");
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteWithSyncTransport() throws Exception {
		Service service = createProxiedService("Test1Service", "flakyWithHttpSyncTransport");
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}
}
