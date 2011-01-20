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

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.TransportInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransport;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HTTPClientTransportGetTest extends AbstractWithServerTest {
	private HTTPClientTransport m_transport;

    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	public HTTPClientTransport getTransport() throws Exception {
		if (m_transport != null) {
			return m_transport;
		}
		HTTPClientTransport transport = new HTTPClientTransport();
		ClientServiceId id = new ClientServiceId("test1", "local");
		TransportOptions options = new TransportOptions();
		options.getProperties().put(SOAConstants.HTTP_VERSION, SOAConstants.TRANSPORT_HTTP_11);

		TransportInitContextImpl initCtx = new TransportInitContextImpl(id, "Test_HTTP_Get", options);
		transport.init(initCtx);
		initCtx.kill();
		m_transport = transport;
		return m_transport;
	}

	@Test
	public void httpClient() throws Exception {
		HTTPClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx =
			TestUtils.createClientMessageContextForHttpGet(TestUtils.createTestMessage(), serviceURL, 2048, "local");

		TransportOptions options = new TransportOptions();

		transport.invoke(ctx.getRequestMessage(), options);
		InboundMessageImpl response = (InboundMessageImpl) ctx.getResponseMessage();
		response.recordPayload(32768);
		byte[] data = response.getRecordedData();
		String dataStr = new String(data);
		System.out.println(dataStr);
//		Assert.assertEquals(message, expected, actual)nvns:ms="http://www.ebayopensource.org/turmeric/common/v1/types"&nvns:xs="http://www.w3.org/2001/XMLSchema"&nvns:ns2="http://www.ebay.com/test/soaframework/sample/types1"&nvns:ns3="http://iop.pb.com"&nvns:xsi="http://www.w3.org/2001/XMLSchema-instance"&body(0)="SOA+in+Chinese+is+%27%C3%83%C3%A6%C2%B7%C3%BE%C3%8E%C3%B1%C2%B5%C3%84%C2%BC%C3%9C%C2%B9%C2%B9%27"&recipients(0).entry(0).key(0)="soa0%40ebay.com"&recipients(0).entry(0).value(0).city(0)="San+Jose"&recipients(0).entry(0).value(0).emailAddress(0)="soa0%40ebay.com"&recipients(0).entry(0).value(0).postCode(0)="95125"&recipients(0).entry(0).value(0).state(0)="CA"&recipients(0).entry(0).value(0).streetNumber(0)="2000"&something(0).xsi:@type="xs%3Astring"&something(0)="This+is+from+the+any+object+type"&subject(0)="SOA+Framework+test+message"
	}
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void httpClientAsyncException() throws Exception {
		HTTPClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx =
			TestUtils.createClientMessageContextForHttpGet(TestUtils.createTestMessage(), serviceURL, 2048, "local");

		TransportOptions options = new TransportOptions();

		try { 
			transport.invokeAsync(ctx.getRequestMessage(), options);
			Assert.fail("Expected UnsupportedOperationException");
		}
		catch (UnsupportedOperationException e){
		}
	} 
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void httpClientRetrieveException() throws Exception {
		HTTPClientTransport transport = getTransport();
		URL serviceURL = serverUri.resolve("?myTestOperation").toURL();
		MessageContext ctx =
			TestUtils.createClientMessageContextForHttpGet(TestUtils.createTestMessage(), serviceURL, 2048, "local");

		try { 
			transport.retrieve(ctx, null);
			Assert.fail("Expected UnsupportedOperationException");
		}
		catch (UnsupportedOperationException e){
		}
	} 

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void httpClientBadLength() throws Exception {
		HTTPClientTransport transport = getTransport();
		URL serviceURL = serverUri.toURL();
		MessageContext ctx =
			TestUtils.createClientMessageContextForHttpGet(TestUtils.createTestMessage(), serviceURL, 5, "local");

		TransportOptions options = new TransportOptions();

		boolean gotException = false;
		try {
			transport.invoke(ctx.getRequestMessage(), options);
		} catch (ServiceException e) {
			gotException = true;
			Assert.assertEquals(e.getMessage().substring(0, 19), "REST URL has length");
		}
		Assert.assertTrue(gotException);
	}
}
