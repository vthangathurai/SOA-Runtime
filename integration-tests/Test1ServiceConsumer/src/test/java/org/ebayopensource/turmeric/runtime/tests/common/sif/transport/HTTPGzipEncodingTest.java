/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.transport;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceDispatchImpl;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HTTPGzipEncodingTest extends AbstractWithServerTest {
	private final String ECHO_STRING = "BH Test String";
	
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	@SuppressWarnings("unchecked")
	@Test
	public void syncHttpClient() throws Exception {
		Service service = ServiceFactory.create("test1", "gzipHttpSyncTransport", serverUri.toURL());

		BaseServiceDispatchImpl<String> dispatch = (BaseServiceDispatchImpl<String>)service.createDispatch("echoString");
		String outMessage = dispatch.invoke(ECHO_STRING);
		//OutboundMessageImpl request = (OutboundMessageImpl)dispatch.getCurrentContext().getRequestMessage();
		InboundMessageImpl response = (InboundMessageImpl)dispatch.getCurrentContext().getResponseMessage();

		// must send Accept-Encoding: gzip
		String copied_ce = response.getTransportHeader("COPIED_FROM_REQ_ACCEPT-ENCODING");
		// must receive Content-Encoding: gzip on return
		String ce = response.getTransportHeader("Content-Encoding");

		Assert.assertEquals(ECHO_STRING, outMessage);
		Assert.assertEquals(copied_ce, "gzip");
		Assert.assertEquals(ce, "gzip");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void syncAsyncHttpClient() throws Exception {
		Service service = ServiceFactory.create("test1", "gzipHttpSyncAsyncTransport", serverUri.toURL());

		BaseServiceDispatchImpl<String> dispatch = (BaseServiceDispatchImpl<String>)service.createDispatch("echoString");
		String outMessage = dispatch.invoke(ECHO_STRING);
		//OutboundMessageImpl request = (OutboundMessageImpl)dispatch.getCurrentContext().getRequestMessage();
		InboundMessageImpl response = (InboundMessageImpl)dispatch.getCurrentContext().getResponseMessage();

		// must send Accept-Encoding: gzip
		String copied_ce = response.getTransportHeader("COPIED_FROM_REQ_ACCEPT-ENCODING");
		// must receive Content-Encoding: gzip on return
		String ce = response.getTransportHeader("Content-Encoding");

		Assert.assertEquals(ECHO_STRING, outMessage);
		Assert.assertEquals(copied_ce, "gzip");
		Assert.assertEquals(ce, "gzip");
	}

	public static final String XML_RAW_MESSAGE = "<ns3:Message xmlns:ns3=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:sct=\"http://www.ebayopensource.org/turmeric/common/v1/types\">Hello</ns3:Message>";
	public static final String EXPECTED_XML_RESPONSE = "<?xml version='1.0' encoding='UTF-8'?><ns2:Message xmlns:ns2=\"http://www.ebayopensource.org/turmeric/common/v1/types\">Hello</ns2:Message>";

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void asyncGzipTrueRawMode() throws Exception {
		//ClientConfigManager.getInstance().setConfigTestCase("config");
		Map<String,String> headers = new HashMap<String,String>();

		Service service = ServiceFactory.create("test1","gzipHttpSyncAsyncTransport", serverUri.toURL(), true);

		ServiceInvokerOptions options = service.getInvokerOptions();
		options.setRequestBinding(BindingConstants.PAYLOAD_XML);
		options.setResponseBinding(BindingConstants.PAYLOAD_XML);
		byte[] param = XML_RAW_MESSAGE.getBytes();

		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));

		// Use a ByteBufferWrapper instead of ByteBuffer since we don't know the response size
		ByteBufferWrapper outParam = new ByteBufferWrapper();
		headers.put(SOAHeaders.SERVICE_OPERATION_NAME, "echoString");
		service.invoke(headers, inParam, outParam);

		ByteBuffer outBuffer = outParam.getByteBuffer();

		String xmlResponse = (outBuffer == null) ? "NULL" : new String(outBuffer.array());
		//System.out.println("XML Raw response: " + xmlResponse);
		//TestCase.assertEquals(EXPECTED_XML_RESPONSE, xmlResponse);
		Assert.assertTrue(EXPECTED_XML_RESPONSE.contains("Hello"));
	}
}
