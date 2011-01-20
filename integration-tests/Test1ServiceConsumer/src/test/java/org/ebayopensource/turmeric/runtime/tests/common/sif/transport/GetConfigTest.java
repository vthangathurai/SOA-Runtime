/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.transport;

import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransport;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransportConfig;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class GetConfigTest extends AbstractWithServerTest {
	private final String ECHO_STRING = "BH Test String";

    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	@Test
	@SuppressWarnings("unchecked")
	public void getOldTransportConfigTest() throws Exception {
		Service service = ServiceFactory.create("test1", "oldTransport", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString").invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);

		ClientServiceDesc serviceDesc = ClientServiceDescFactory.getInstance()
				.getServiceDesc("test1", "oldTransport", null, false);
		Transport transport = serviceDesc.getTransport("HTTP11");

		Assert.assertNotNull(transport);
		Assert.assertTrue(transport instanceof HTTPClientTransport);
		Assert.assertNotNull(((HTTPClientTransport) transport).getConfig());
		Assert
				.assertNotNull(((HTTPClientTransport) transport).getConfig() instanceof HTTPClientTransportConfig);
		Assert
				.assertNotNull(((HTTPClientTransportConfig) ((HTTPClientTransport) transport)
						.getConfig()).getSvcInvocationConfig());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getNewTransportConfigTest() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);

		ClientServiceDesc serviceDesc = ClientServiceDescFactory.getInstance()
				.getServiceDesc("test1", "remote", null, false);
		Transport transport = serviceDesc.getTransport("HTTP11");

		Assert.assertNotNull(transport);
		Assert.assertTrue(transport instanceof HTTPSyncAsyncClientTransport);
		Assert.assertNotNull(((HTTPSyncAsyncClientTransport) transport)
				.getConfig());
		Assert.assertNotNull(((HTTPSyncAsyncClientTransport) transport)
				.getConfig() instanceof HTTPClientTransportConfig);
		Assert
				.assertNotNull(((HTTPClientTransportConfig) ((HTTPSyncAsyncClientTransport) transport)
						.getConfig()).getNioSvcInvocationConfig());
	}
}
