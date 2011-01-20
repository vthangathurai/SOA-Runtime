/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.spf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.mail.internet.MimeBodyPart;

import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.sample.transports.TestTransport;
import org.ebayopensource.turmeric.runtime.tests.common.util.SOAPTestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.SetResponseHeaderHandler;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Rule;
import org.junit.Test;


/**
 * SOAP version of the server pipeline test
 * @author gyue
 */
public class SOAPServerPipelineTest extends AbstractTurmericTestCase {

	 @Rule
	 public NeedsConfig needsconfig = new NeedsConfig("testconfig");
	 
	public  void mIMEBodyPart() throws Exception {
		String ATTACHMENT_DATA_MIME_BLOCK =
			"content-type: application/octet-stream" +"\n" +
			"content-id: <1.urn:uuid:9E55D9AADCAC7C46E811592318363373.org>" + "\n" +
			"content-transfer-encoding: binary" + "\n" +
			"\n" +
			"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "\n";
		ByteArrayInputStream is = new ByteArrayInputStream(ATTACHMENT_DATA_MIME_BLOCK.getBytes());
		MimeBodyPart attachment = new MimeBodyPart(is);

		String ATTACHMENT_ROOT_MIME_BLOCK =
			"content-type: application/xop+xml; charset=UTF-8; type=\"text/xml\";" + "\n" +
			"content-id: <0.urn:uuid:9E55D9AADCAC7C46E811592318362122.org>" + "\n" +
			"content-transfer-encoding: binary" +
			"\n\n" +
			"<ns2:MyMessage xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/service/message\">" +
			"<body>SOA SOA, SOS.</body>" +
			"<recipients><entry><key>soa@ebay.com</key><value><city>San Jose</city><emailAddress>soa@ebay.com</emailAddress><postCode>95125</postCode><state>CA</state><streetNumber>2145</streetNumber></value></entry></recipients>" +
			"<subject>Test SOA JAXB XML ser/deser</subject>" +
			"<binaryData>" +
		    	"<xop:Include href=\"cid:1.urn:uuid:9E55D9AADCAC7C46E811592318363373.org\" xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" />" +
		   "</binaryData>" +
		   "</ns2:MyMessage>";

			ByteArrayInputStream is1 = new ByteArrayInputStream(ATTACHMENT_ROOT_MIME_BLOCK.getBytes());
			MimeBodyPart root = new MimeBodyPart(is1);

			String rootCid = root.getHeader("content-id", "|");
			String attachmentCid = attachment.getHeader("content-id", "|");

			assertEquals("<0.urn:uuid:9E55D9AADCAC7C46E811592318362122.org>", rootCid);
			assertEquals("<1.urn:uuid:9E55D9AADCAC7C46E811592318363373.org>", attachmentCid);
	}

	@Test
	public  void sOAPPositive_GoodRequest() throws Exception {
		System.out.println(">>SOAP11");
		ServerMessageContext serverCtx =
				SOAPTestUtils.createServerMessageContextForTest1Service(SOAPTestUtils.GOOD_SOAP_REQUEST);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, false);

		System.out.println(">>SOAP12");
		serverCtx =
				SOAPTestUtils.createServerMessageContextForTest1Service(SOAPTestUtils.GOOD_SOAP_12_REQUEST, SOAConstants.MSG_PROTOCOL_SOAP_12);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, false);
	}

	@Test
	public  void sOAPPositive_GoodRequestWithSpace() throws Exception {
		System.out.println(">>SOAP11");
		ServerMessageContext serverCtx =
				SOAPTestUtils.createServerMessageContextForTest1Service(SOAPTestUtils.GOOD_SOAP_REQUEST_WITH_SPACE);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, false);

		System.out.println(">>SOAP12");
		serverCtx =
				SOAPTestUtils.createServerMessageContextForTest1Service(SOAPTestUtils.GOOD_SOAP_12_REQUEST_WITH_SPACE, SOAConstants.MSG_PROTOCOL_SOAP_12);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, false);
	}

	@Test
	public  void sOAPPositive_GoodRequestWithComments() throws Exception {
		System.out.println(">>SOAP11");
		ServerMessageContext serverCtx =
				SOAPTestUtils.createServerMessageContextForTest1Service(SOAPTestUtils.GOOD_SOAP_REQUEST_WITH_COMMENTS);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, false);

		System.out.println(">>SOAP12");
		serverCtx =
				SOAPTestUtils.createServerMessageContextForTest1Service(SOAPTestUtils.GOOD_SOAP_12_REQUEST_WITH_COMMENTS, SOAConstants.MSG_PROTOCOL_SOAP_12);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, false);
	}


	@Test
	public  void sOAPNegative_InvalidStartBodyTag() throws Exception {
		// test flow w/ invalid start body tag
		System.out.println(">>SOAP11");
		ServerMessageContext serverCtx = SOAPTestUtils.createServerMessageContextForTest1Service(
									SOAPTestUtils.BAD_SOAP_REQUEST_INVALIDSTARTBODYTAG);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, true);

		System.out.println(">>SOAP12");
		serverCtx = SOAPTestUtils.createServerMessageContextForTest1Service(
									SOAPTestUtils.BAD_SOAP_12_REQUEST_INVALIDSTARTBODYTAG, SOAConstants.MSG_PROTOCOL_SOAP_12);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, true);
	}

	@Test
	public  void sOAPNegative_InvalidEndBodyTag() throws Exception {
		// test flow w/ invalid end body tag
		System.out.println(">>SOAP11");
		ServerMessageContext serverCtx = SOAPTestUtils.createServerMessageContextForTest1Service(
									SOAPTestUtils.BAD_SOAP_REQUEST_INVALIDENDBODYTAG);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, true);

		System.out.println(">>SOAP12");
		serverCtx = SOAPTestUtils.createServerMessageContextForTest1Service(
									SOAPTestUtils.BAD_SOAP_12_REQUEST_INVALIDENDBODYTAG, SOAConstants.MSG_PROTOCOL_SOAP_12);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, true);
	}

	@Test
	public  void sOAPNegative_BadXMLBodyTag() throws Exception {
		// test flow w/ bad XML body
		System.out.println(">>SOAP11");
		ServerMessageContext serverCtx = SOAPTestUtils.createServerMessageContextForTest1Service(
					SOAPTestUtils.BAD_SOAP_REQUEST_BADXMLBODY);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, true);

		System.out.println(">>SOAP12");
		serverCtx = SOAPTestUtils.createServerMessageContextForTest1Service(
					SOAPTestUtils.BAD_SOAP_12_REQUEST_BADXMLBODY, SOAConstants.MSG_PROTOCOL_SOAP_12);
		runAndTest(serverCtx, SOAPTestUtils.SOAP_BINDING_NAME, true);
	}

	private static Message runAndTest(ServerMessageContext serverCtx, String payloadType, boolean isNegativeTest) throws Exception {
		MyMessage msg = null;
		Message request = serverCtx.getRequestMessage();
		ServerMessageProcessor.getInstance();
		ServerMessageProcessor processor = ServerMessageProcessor.getInstance();
		processor.processMessage(serverCtx);
		Message resp = serverCtx.getResponseMessage();

		// get the configuered TestTransport and display the result
		System.out.println("response: " + TestTransport.result);

		List<Throwable> errors = serverCtx.getErrorList();
		if (isNegativeTest) {
			if (serverCtx.getMessageProtocol().equals(SOAConstants.MSG_PROTOCOL_SOAP_12)) {
				// ensure it contains a SOAP12 fault
				if (!SOAPTestUtils.containSOAP12Fault(TestTransport.result)) {
					System.out.println("ERROR>>: Expected SOAP12 fault/ErrorMessage, but not found!");
					assertTrue(false);
				}
			} else {
				// ensure it contains a SOAP11 fault
				if (!SOAPTestUtils.containSOAP11Fault(TestTransport.result)) {
					System.out.println("ERROR>>: Expected SOAP11 fault/ErrorMessage, but not found!");
					assertTrue(false);
				}
			}
			if (errors != null && !errors.isEmpty()) {
				Throwable error = errors.get(0);
				System.out.println("Expected exception>>: " + error.toString());
			}
		} else {
			// ensure it DOES NOT contain a SOAP fault
			if (SOAPTestUtils.containSOAP11Fault(TestTransport.result)
					&& SOAPTestUtils.containSOAP12Fault(TestTransport.result) ) {
				System.out.println("ERROR>>: Found SOAP fault/ErrorMessage");
				assertTrue(false);
			}

			msg = (MyMessage)resp.getParam(0);
			assertEquals(SOAPTestUtils.MESSAGE_BODY_TEXT, msg.getBody());

			if (errors != null && !errors.isEmpty()) {
				Throwable error = errors.get(0);
				throw new Exception("Failed to invoke test service: " + error.toString(), error);
			}
		}

		assertEquals(payloadType, request.getTransportHeader(SOAHeaders.REQUEST_DATA_FORMAT));
		assertEquals(payloadType, request.getTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT));
//		assertEquals(TestUtils.SOA_MESSAGE_PROTOCOL_VALUE, request.getTransportHeader(SOAHeaders.MESSAGE_PROTOCOL));
//		assertEquals(TestUtils.ELEMENT_ORDERING_PRESERVE_VALUE, request.getTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE));

//		assertEquals(TestUtils.SOA_MESSAGE_PROTOCOL_VALUE, resp.getTransportHeader(ReadHeaderHandler.TEST_HEADER_PREFIX + SOAHeaders.MESSAGE_PROTOCOL));
//		assertEquals(TestUtils.ELEMENT_ORDERING_PRESERVE_VALUE, resp.getTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE));

		if (!isNegativeTest) {
			assertEquals(payloadType, resp.getTransportHeader(org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.ClientReadHeaderHandler.COPIED_REQUEST_HEADER_PREFIX + SOAHeaders.REQUEST_DATA_FORMAT));
			assertEquals(payloadType, resp.getTransportHeader(org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.ClientReadHeaderHandler.COPIED_REQUEST_HEADER_PREFIX + SOAHeaders.RESPONSE_DATA_FORMAT));
			assertEquals(SetResponseHeaderHandler.VALUE, resp.getTransportHeader(SetResponseHeaderHandler.KEY));
		}

		return resp;
	}
}
