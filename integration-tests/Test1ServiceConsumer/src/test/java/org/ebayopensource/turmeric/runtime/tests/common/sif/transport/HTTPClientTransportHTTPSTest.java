/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.TransportInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransport;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransportConfig;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Rule;
import org.junit.Test;


public class HTTPClientTransportHTTPSTest extends AbstractTurmericTestCase {
	private static volatile boolean proxyEnable = false;

	@Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	static {
		try {
			Socket socket = new Socket();
			InetSocketAddress address = new InetSocketAddress(
					"skyline.qa.ebay.com", 80);
			socket.connect(address, 2000);
			proxyEnable = true;
		} catch (IOException e) {
		}
	}
	@Test
	public void httpClient() throws Exception {
		HTTPClientTransport transport = new HTTPClientTransport();
		ClientServiceId id = new ClientServiceId("test1", "local");
		TransportOptions options = new TransportOptions();
		options.getProperties().put(HTTPClientTransportConfig.USE_HTTPS, "TRUE");
		options.getProperties().put(SOAConstants.HTTP_VERSION, SOAConstants.TRANSPORT_HTTP_10);
		if (proxyEnable) {
			new Socket("skyline.qa.ebay.com", 80);
			options.getProperties().put(HTTPClientTransportConfig.PROXY_HOST,
					"skyline.qa.ebay.com");
			options.getProperties().put(HTTPClientTransportConfig.PROXY_PORT,
					"80");
			options.getProperties().put(
					HTTPClientTransportConfig.PROXY_ENABLED, "true");
		}

		URL serviceURL = new URL("https://www.verisign.com/");
		MessageContext ctx =
			TestUtils.createClientMessageContextForHttpGet(TestUtils.createTestMessage(), serviceURL, 2048, "local");

		Message request = ctx.getRequestMessage();
		InboundMessageImpl response = (InboundMessageImpl) ctx.getResponseMessage();
		response.recordPayload(32768);

		TransportInitContextImpl initCtx = new TransportInitContextImpl(id, "Test_HTTPS", options);
		transport.init(initCtx);
		initCtx.kill();

		transport.invoke(request, options);
		byte[] data = response.getRecordedData();
		String dataStr = new String(data);
		System.out.println(dataStr);
	}



}
