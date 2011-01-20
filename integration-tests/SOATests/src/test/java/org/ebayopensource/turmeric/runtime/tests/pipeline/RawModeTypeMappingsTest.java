/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.pipeline;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.junit.asserts.JsonAssert;
import org.ebayopensource.turmeric.junit.asserts.XmlAssert;
import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class RawModeTypeMappingsTest extends AbstractWithServerTest {

	final int KB = 1024;
	public static final String ADMIN_NAME = "test1";
	public static final String CLIENT_NAME = "remote";
	private Map<String, String> m_headers = new HashMap<String, String>();


	public static final String RAW_MESSAGE = "<?xml version='1.0' encoding='UTF-8'?><ns3:Message xmlns:ns3=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:sct=\"http://www.ebayopensource.org/turmeric/common/v1/types\">Hello</ns3:Message>";
	public static final String NV_RAW_MESSAGE = "X-TURMERIC-REST-PAYLOAD&Message=Hello";
	public static final String OLD_NV_RAW_MESSAGE = "Message=Hello";

	public static final String EXPECTED_XML_RESPONSE = "<?xml version='1.0' encoding='UTF-8'?><xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ms=\"http://www.ebayopensource.org/turmeric/common/v1/types\">Hello</xs:Message>";
	public static final String EXPECTED_JSON_RESPONSE = "{\"jsonns.ns2\":\"http://iop.pb.com\",\"jsonns.ns3\":\"http://www.ebay.com/test/soaframework/sample/types1\",\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\",\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\",\"jsonns.xsi\":\"http://www.w3.org/2001/XMLSchema-instance\",\"xs.Message\":[\"Hello\"]}";
	
	@Rule
	public NeedsConfig needsconfig = new NeedsConfig("config");

	private void debug(String msg) {
		System.out.println(msg);
	}

	@Before
	public void setUp() throws Exception {
		//super.setUp();
		m_headers = new HashMap<String, String>();
		m_headers.put(SOAHeaders.SERVICE_OPERATION_NAME, "echoString");
	}
	
	@Test
	public void testNormalCalls() throws Exception {
		Service m_test1 = ServiceFactory.create(ADMIN_NAME, CLIENT_NAME, serverUri.toURL(), true);

		byte[] param = RAW_MESSAGE.getBytes();
		debug("Raw request: " + new String(param));

		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));

		// Use a ByteBufferWrapper instead of ByteBuffer since we don't know the response size
		ByteBufferWrapper outParam = new ByteBufferWrapper();

		m_test1.invoke(m_headers, inParam, outParam);

		ByteBuffer outBuffer = outParam.getByteBuffer();

		String xmlResponse = (outBuffer == null) ? "NULL" : new String(outBuffer.array());
		debug("XML Raw response: " + xmlResponse);

		XmlAssert.assertEquals(EXPECTED_XML_RESPONSE, xmlResponse);
	}

	@Test
	public void testNV_JSONRawCalls() throws Exception {
		Service m_test1 = ServiceFactory.create(ADMIN_NAME, CLIENT_NAME, serverUri.toURL(), true);

		ServiceInvokerOptions options = m_test1.getInvokerOptions();
		options.setRequestBinding(BindingConstants.PAYLOAD_NV);
		options.setResponseBinding(BindingConstants.PAYLOAD_JSON);
		options.setREST(true);

		byte[] param = NV_RAW_MESSAGE.getBytes();
		debug("NV Raw request: " + new String(param));

		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));

		// Use a ByteBufferWrapper instead of ByteBuffer since we don't know the response size
		ByteBufferWrapper outParam = new ByteBufferWrapper();

		m_test1.invoke(m_headers, inParam, outParam);

		ByteBuffer outBuffer = outParam.getByteBuffer();
		String jsonResponse = new String(outBuffer.array());

		debug("JSON Raw response: " + jsonResponse);
		
		JsonAssert.assertJsonObjectEquals(EXPECTED_JSON_RESPONSE, jsonResponse);
	}

}
