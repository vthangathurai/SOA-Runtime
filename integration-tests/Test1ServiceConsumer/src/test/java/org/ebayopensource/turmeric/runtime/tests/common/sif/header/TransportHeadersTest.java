/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.header;

import junit.framework.TestCase;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Test;


/**
 * Test for the configuration and overriding of the transport headers. 
 * 
 * @author mpoplacenel
 */
public class TransportHeadersTest extends AbstractWithServerTest {
	
	/**
	 * Parameter for the WS invocation.
	 */
	private static final String ECHO_STRING = "Transport Headers Test String";

	private static final String SOA_HEADER_NAME = "X-TURMERIC-Routing-Profile-Name";
	
	private static final String ESB_HEADER_NAME = "X-TURMERIC-ESB-Routing-Profile-Name";
	
	@Test
	@SuppressWarnings("unchecked")
	public void globalTransportHeaders() throws Exception {
		Service service = ServiceFactory.create("test1", "transportHeaders", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString").invoke(ECHO_STRING);
		// the assert on the transport header actually happens in the request handler
		TestCase.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void clientTransportHeaders() throws Exception {
		Service service = ServiceFactory.create("test1", "transportHeaders_Client", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString").invoke(ECHO_STRING);
		// the assert on the transport header actually happens in the request handler
		TestCase.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void preferredTransportHeaders() throws Exception {
		Service service = ServiceFactory.create("test1", "transportHeaders_Preferred", serverUri.toURL());
		String outMessage = (String) service.createDispatch("echoString").invoke(ECHO_STRING);
		// assert should happen in the request handler
		TestCase.assertEquals(ECHO_STRING, outMessage);
	}

	/**
	 * Verifies the session headers override the transport headers defined in the configuration. 
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void sessionTransportHeaders() throws Exception {
		Service service = ServiceFactory.create("test1", "transportHeaders_Preferred", serverUri.toURL());
		service.setSessionTransportHeader(SOA_HEADER_NAME, "SOA_Session_Client_Profile");
		service.setSessionTransportHeader(ESB_HEADER_NAME, "ESB_Session_Client_Profile");
		// set the expected value for the handler to pick it up and assert against it
		service.getRequestContext().setProperty("testValueSOA", "SOA_Session_Client_Profile");
		service.getRequestContext().setProperty("testValueESB", "ESB_Session_Client_Profile");

		TestCase.assertEquals("Session header wasn't set as expected", 
				"SOA_Session_Client_Profile", 
				service.getSessionTransportHeader(SOA_HEADER_NAME));
		TestCase.assertEquals("Session header wasn't set as expected", 
				"ESB_Session_Client_Profile", 
				service.getSessionTransportHeader(ESB_HEADER_NAME));
		
		String outMessage = (String) service.createDispatch("echoString").invoke(ECHO_STRING);
		// the assert on the transport header actually happens in the request handler
		TestCase.assertEquals(ECHO_STRING, outMessage);
	}

	/**
	 * Verifies the request headers override all transport headers - those defined in the configuration 
	 * <strong>AND</strong> those defined as session transport headers. 
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void requestTransportHeaders() throws Exception {
		Service service = ServiceFactory.create("test1", "transportHeaders_Preferred", serverUri.toURL());
		service.setSessionTransportHeader(SOA_HEADER_NAME, "SOA_Session_Client_Profile");
		service.setSessionTransportHeader(ESB_HEADER_NAME, "ESB_Session_Client_Profile");
		service.getRequestContext().setTransportHeader(SOA_HEADER_NAME, "SOA_Request_Client_Profile");
		service.getRequestContext().setTransportHeader(ESB_HEADER_NAME, "ESB_Request_Client_Profile");
		// set the expected value for the handler to pick it up and assert against it
		service.getRequestContext().setProperty("testValueSOA", "SOA_Request_Client_Profile");
		service.getRequestContext().setProperty("testValueESB", "ESB_Request_Client_Profile");

		TestCase.assertEquals("Session header wasn't set as expected", 
				"SOA_Session_Client_Profile", 
				service.getSessionTransportHeader(SOA_HEADER_NAME));
		TestCase.assertEquals("Request header wasn't set as expected", 
				"SOA_Request_Client_Profile", 
				service.getRequestContext().getTransportHeader(SOA_HEADER_NAME));
		TestCase.assertEquals("Session header wasn't set as expected", 
				"ESB_Session_Client_Profile", service.getSessionTransportHeader(ESB_HEADER_NAME));
		TestCase.assertEquals("Request header wasn't set as expected", 
				"ESB_Request_Client_Profile", 
				service.getRequestContext().getTransportHeader(ESB_HEADER_NAME));
		
		String outMessage = (String) service.createDispatch("echoString").invoke(ECHO_STRING);
		// the assert on the transport header actually happens in the request handler
		TestCase.assertEquals(ECHO_STRING, outMessage);
	}

}
