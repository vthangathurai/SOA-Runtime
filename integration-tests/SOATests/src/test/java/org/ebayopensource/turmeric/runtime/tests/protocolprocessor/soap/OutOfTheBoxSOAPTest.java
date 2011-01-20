/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.protocolprocessor.soap;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.junit.asserts.XmlAssert;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class OutOfTheBoxSOAPTest extends BaseCallTest {

	public static final String ADMIN_NAME = "test1";

	private Service m_test1;
	private Map<String, String> m_headers;

	public static final String XML_RAW_MESSAGE = "<ns3:Message xmlns:ns3=\"http://www.w3.org/2001/XMLSchema\">Hello</ns3:Message>";
	
	public final static String SOAP11_RAW_MESSAGE =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<soapenv:Body>" +
		XML_RAW_MESSAGE +
		"</soapenv:Body>" +
		"</soapenv:Envelope>";

	public final static String SOAP12_RAW_MESSAGE =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
		"<soapenv:Body>" +
		XML_RAW_MESSAGE +
		"</soapenv:Body>" +
		"</soapenv:Envelope>";

	public static final String EXPECTED_SOAP11_RESPONSE = "<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ms=\"http://www.ebayopensource.org/turmeric/common/v1/types\">Hello</xs:Message></soapenv:Body></soapenv:Envelope>";
	public static final String EXPECTED_SOAP12_RESPONSE = "<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Header/><soapenv:Body><xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ms=\"http://www.ebayopensource.org/turmeric/common/v1/types\">Hello</xs:Message></soapenv:Body></soapenv:Envelope>";
	
	public OutOfTheBoxSOAPTest() throws Exception {
		super("config");
	}

	@Test
	public void testNormalCalls() throws ServiceException {
		// noop
	}

	@Test
	@Ignore //see TURMERIC-1098
	public void sOAP11LocalCall() throws Exception {
		m_test1 = ServiceFactory.create(ADMIN_NAME, "local", false);
	
		byte[] param = SOAP11_RAW_MESSAGE.getBytes();
		println("SOAP11 request: " + new String(param));
	
		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));
	
		ByteBufferWrapper outParam = new ByteBufferWrapper();
	
		m_headers = new HashMap<String, String>();
		m_headers.put(SOAHeaders.SERVICE_OPERATION_NAME, "echoString");
		m_test1.setSessionTransportHeader("CONTENT-TYPE", "text/xml; charset=UTF-8");
		//m_headers.put(SOAHeaders.MESSAGE_PROTOCOL,SOAConstants.MSG_PROTOCOL_SOAP_11);
	
		m_test1.invoke(m_headers, inParam, outParam);
	
		ByteBuffer outBuffer = outParam.getByteBuffer();
		Assert.assertNotNull("outBuffer", outBuffer);
		String xmlResponse = new String(outBuffer.array());
		println("SOAP11 Raw response: " + xmlResponse);
	
		XmlAssert.assertEquals(EXPECTED_SOAP11_RESPONSE, xmlResponse);
	}
	
	@Test
	public void sOAP12LocalCall() throws Exception {
		m_test1 = ServiceFactory.create(ADMIN_NAME, "local", false);

		byte[] param = SOAP12_RAW_MESSAGE.getBytes();
		println("SOAP12 request: " + new String(param));

		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));

		ByteBufferWrapper outParam = new ByteBufferWrapper();

		m_headers = new HashMap<String, String>();
		m_headers.put(SOAHeaders.SERVICE_OPERATION_NAME, "echoString");
		// Axis2 HTTPTransportUtils looks at the content type to determine SOAP version
		// application/soap+xml is used for SOAP12. text/xml is used for SOAP11
		m_test1.setSessionTransportHeader("CONTENT-TYPE", "application/soap+xml; charset=UTF-8");

		m_test1.invoke(m_headers, inParam, outParam);

		ByteBuffer outBuffer = outParam.getByteBuffer();
		Assert.assertNotNull("outBuffer", outBuffer);
		String xmlResponse = new String(outBuffer.array());
		println("SOAP12 Raw response: " + xmlResponse);

		XmlAssert.assertEquals(EXPECTED_SOAP12_RESPONSE, xmlResponse);
	}

	@Test
	@Ignore //see /TURMERIC-1098
	public void sOAP11RemoteCall() throws Exception {
		m_test1 = ServiceFactory.create(ADMIN_NAME, "remote", serverUri.toURL(), false);

		byte[] param = SOAP11_RAW_MESSAGE.getBytes();
		println("SOAP11 request: " + new String(param));

		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));

		ByteBufferWrapper outParam = new ByteBufferWrapper();

		m_headers = new HashMap<String, String>();
		m_headers.put(SOAHeaders.SERVICE_OPERATION_NAME, "echoString");
		m_test1.setSessionTransportHeader("CONTENT-TYPE", "application/soap+xml; charset=UTF-8");
		//m_headers.put(SOAHeaders.MESSAGE_PROTOCOL,SOAConstants.MSG_PROTOCOL_SOAP_11);

		m_test1.invoke(m_headers, inParam, outParam);

		ByteBuffer outBuffer = outParam.getByteBuffer();
		Assert.assertNotNull("outBuffer", outBuffer);
		String xmlResponse = new String(outBuffer.array());
		println("SOAP11 Raw response: " + xmlResponse);

		XmlAssert.assertEquals(EXPECTED_SOAP11_RESPONSE, xmlResponse);
	}

	@Test
	public void sOAP12RemoteCall() throws Exception {
		m_test1 = ServiceFactory.create(ADMIN_NAME, "remote", serverUri.toURL(), false);

		byte[] param = SOAP12_RAW_MESSAGE.getBytes();
		println("SOAP12 request: " + new String(param));

		ByteBufferWrapper inParam = new ByteBufferWrapper();
		inParam.setByteBuffer(ByteBuffer.wrap(param));

		ByteBufferWrapper outParam = new ByteBufferWrapper();

		m_headers = new HashMap<String, String>();
		m_headers.put(SOAHeaders.SERVICE_OPERATION_NAME, "echoString");

		// Axis2 HTTPTransportUtils looks at the content type to determine SOAP version
		// application/soap+xml is used for SOAP12. text/xml is used for SOAP11
		m_test1.setSessionTransportHeader("CONTENT-TYPE", "application/soap+xml; charset=UTF-8");
		
		m_test1.invoke(m_headers, inParam, outParam);

		
		ByteBuffer outBuffer = outParam.getByteBuffer();
		Assert.assertNotNull("outBuffer", outBuffer);
		String xmlResponse = new String(outBuffer.array());
		println("SOAP12 Raw response: " + xmlResponse);

		XmlAssert.assertEquals(EXPECTED_SOAP12_RESPONSE, xmlResponse);
	}
}
