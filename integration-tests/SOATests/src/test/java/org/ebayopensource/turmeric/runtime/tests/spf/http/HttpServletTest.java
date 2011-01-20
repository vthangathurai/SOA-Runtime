/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.spf.http;

import static org.junit.Assert.*;

import java.util.List;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerMessageContextBuilder;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServerUtils;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServletResponseTransport;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.ISOATransportRequest;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.SOAServerTransportRequest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;


/**
 * @author wdeng
 */
public class HttpServletTest extends AbstractTurmericTestCase {

	@Rule
	public NeedsConfig needsconfig = new NeedsConfig("testconfig");
	
	@Test
	public  void httpServerUtilsGet() throws Exception {
		System.out.println("**** Starting testHttpServerUtilsGet");

		TestServletRequest req = new TestServletRequest();
		req.setMethod("GET");
		req.addParameter(SOAHeaders.SERVICE_NAME, "test1");
		req.addParameter(SOAHeaders.SERVICE_OPERATION_NAME, "myTestOperation");
		req.addParameter(SOAHeaders.GLOBAL_ID, "EBAY-US");
		req.setQueryString("myTestOperation&X-TURMERIC-SERVICE-NAME=test1&X-TURMERIC-OPERATION-NAME=myTestOperation&X-TURMERIC-GLOBAL-ID=EBAY-US&" +
				TestUtils.NV_INPUT_BODY);
		TestServletResponse resp = new TestServletResponse();

		Transport responseTransport = new HTTPServletResponseTransport(resp);
		ISOATransportRequest request = SOAServerTransportRequest.createRequest(req);
		HTTPServerUtils serverUtils = new HTTPServerUtils(request, null, null);

		ServerMessageContextBuilder helper = serverUtils.createMessageContext(responseTransport);

		assertEquals("NV", helper.getTransportHeader(SOAHeaders.REQUEST_DATA_FORMAT));
		assertEquals("XML", helper.getTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT));
		assertEquals(String.valueOf(false).toUpperCase(), helper.getTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE).toUpperCase());

		assertEquals("myTestOperation", helper.getTransportHeader(SOAHeaders.SERVICE_OPERATION_NAME));
		assertEquals("test1", helper.getTransportHeader(SOAHeaders.SERVICE_NAME));
		System.out.println("**** Ending testHttpServerUtilsGet");
	}

	@Test
	public  void httpServerUtilsPost() throws Exception {
		System.out.println("**** Starting testHttpServerUtilsPost");

		TestServletRequest req = new TestServletRequest();
		req.setMethod("POST");
		req.addHeader(SOAHeaders.SERVICE_NAME, "test1");
		req.addHeader(SOAHeaders.SERVICE_OPERATION_NAME, "myTestOperation");
		req.addHeader(SOAHeaders.REQUEST_DATA_FORMAT, "NV");
		req.addHeader(SOAHeaders.GLOBAL_ID, "EBAY-US");
		req.setBody("X-TURMERIC-SERVICE-NAME=test1&X-TURMERIC-OPERATION-NAME=myTestOperation&X-TURMERIC-GLOBAL-ID=EBAY-US&" +
						TestUtils.NV_INPUT_BODY);

		ServerMessageContextBuilder.init();
		
		TestServletResponse resp = new TestServletResponse();
		Transport responseTransport = new HTTPServletResponseTransport(resp);
		ISOATransportRequest request = SOAServerTransportRequest.createRequest(req);
		HTTPServerUtils serverUtils = new HTTPServerUtils(request, null, null);
		
		ServerMessageContextBuilder helper = serverUtils.createMessageContext(responseTransport);

		assertEquals("NV", helper.getTransportHeader(SOAHeaders.REQUEST_DATA_FORMAT));
		assertEquals(null, helper.getTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT));
		assertEquals(null, helper.getTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE));

		assertEquals("myTestOperation", helper.getTransportHeader(SOAHeaders.SERVICE_OPERATION_NAME));
		assertEquals("test1", helper.getTransportHeader(SOAHeaders.SERVICE_NAME));

		System.out.println("**** Ending testHttpServerUtilsPost");
	}
	
	@Test
	@Ignore //see TURMERIC-1117: http://www.ebayopensource.org/jira/browse/TURMERIC-1117 
	/*
	 * Ignored as the feature is turned as a CAL warning and the ServiceException is not thrown. 
	 */
	public void httpServerNonMatchingOperationName() throws Exception {
		System.out
				.println("**** Starting testHttpServerNonMatchingOperationName");

		String xmlInputBody = "<?xml version='1.0' encoding='UTF-8'?><ns2:test xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/service/message\"></ns2:test>";
		TestServletRequest req = new TestServletRequest();
		req.setMethod("POST");
		req.addHeader(SOAHeaders.SERVICE_NAME, "test1");
		req.addHeader(SOAHeaders.SERVICE_OPERATION_NAME, "myTestOperation");
		req.addHeader(SOAHeaders.REQUEST_DATA_FORMAT, "XML");
		req.addHeader(SOAHeaders.GLOBAL_ID, "EBAY-US");
		req.setBody(xmlInputBody);

		ServerMessageContextBuilder.init();

		TestServletResponse resp = new TestServletResponse();
		Transport responseTransport = new HTTPServletResponseTransport(resp);
		ISOATransportRequest request = SOAServerTransportRequest
				.createRequest(req);
		HTTPServerUtils serverUtils = new HTTPServerUtils(request, null, null);

		ServerMessageContextBuilder helper = serverUtils
				.createMessageContext(responseTransport);

		List<Throwable> errorList = helper.getContextErrorList();
		boolean foundError = false;
		for (Throwable t : errorList) {
			if (t instanceof ServiceException) {
				ServiceException e = (ServiceException) t;
				foundError = "Operation name header myTestOperation does not match {http://www.ebay.com/test/soaframework/sample/service/message}test in the payload"
						.equals(e.getMessage());
				if (foundError) {
					break;
				}
			}
		}
		assertTrue("Expected error not found", foundError);

		System.out.println("**** Ending testHttpServerNonMatchingOperationName");
	}
}
