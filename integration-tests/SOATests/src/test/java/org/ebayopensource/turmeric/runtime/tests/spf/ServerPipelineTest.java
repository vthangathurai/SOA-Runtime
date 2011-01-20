/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.spf;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNodeType;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.util.ServerMessageContextTestBuilder;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.ClientReadHeaderHandler;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.ExceptionTestHandler;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.RequestObjectNodeAccessHandler;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.ResponseObjectNodeAccessHandler;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.SetResponseHeaderHandler;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.Address;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author wdeng
 */
public class ServerPipelineTest extends AbstractWithServerTest {
    
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("testconfig");

	@Test
	public  void testXMLPositiveRoundtrip() throws Throwable {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);

		runAndTest(serverCtx, "XML");
	}


	@Test
	public  void testOrderedNVPositiveRoundtrip() throws Throwable {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("NV");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);

		runAndTest(serverCtx, "NV");
	}


	@Test
	public  void testUnorderedNVPositiveRoundtrip() throws Throwable {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("NV");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);

		Message request = serverCtx.getRequestMessage();

		request.setTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE, "false");
		runAndTest(serverCtx, "NV");
	}


	@Test
	public  void testJSONPositiveRoundtrip() throws Throwable {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("JSON");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);

		runAndTest(serverCtx, "JSON");
	}

	@Test
	public  void testSMPTurnAroundTime() throws Throwable {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);
		runAndTest(serverCtx, "XML");

		System.out.println("**** Run it again");

		serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);
		runAndTest(serverCtx, "XML");
	}


	@Test(expected=ServiceException.class)
	public  void testHandlerExceptionWithContinueOnErrorFalse() throws Throwable {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_THROW_EXCEPTION, Boolean.TRUE);
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_STOP_AT_ERROR_HANDLER);

		ServerMessageProcessor processor = ServerMessageProcessor.getInstance();
		processor.processMessage(serverCtx);

		List<Throwable> errors = serverCtx.getErrorList();
		assertTrue(errors != null && !errors.isEmpty());

		// Use junit4's expected annotation to test for expected ServiceException
		throw errors.get(0);
	}

	@Test
	public  void testXMLPositiveRoundtripWithoutNSURI() throws Throwable {
		// TODO:  plug in a message without nsURI
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");

		ServerMessageContext serverCtx = msgtest.createServerMessageContext();
		serverCtx.setProperty(ExceptionTestHandler.KEY_HANDLER_NAME, ExceptionTestHandler.NAME_CONTINUE_ON_ERROR_HANDLER);

		runAndTest(serverCtx, "XML");
	}

	private  void runAndTest(ServerMessageContext serverCtx, String payloadType) throws Throwable {
		Message request = serverCtx.getRequestMessage();

		long start = System.nanoTime();
		ServerMessageProcessor.getInstance();
		System.out.println("SMP initialization time(" + payloadType + "): " + (System.nanoTime() - start)/1000000.0);
		start = System.nanoTime();
		ServerMessageProcessor processor = ServerMessageProcessor.getInstance();
		// Enable Object Node Test
		request.setTransportHeader(RequestObjectNodeAccessHandler.H_REQUEST_TEST_OBJECT_NODE, "doit");
		processor.processMessage(serverCtx);
		System.out.println("SMP turnaround time for payload: " + payloadType + ": " + (System.nanoTime() - start)/1000000.0);

		List<Throwable> errors = serverCtx.getErrorList();
		for(Throwable t: errors) {
			throw t;
		}
		
		AssertableMessage treq = new AssertableMessage("request", request);

		treq.assertTransportHeader(SOAHeaders.REQUEST_DATA_FORMAT, payloadType);
		treq.assertTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT, payloadType);

		Message response = serverCtx.getResponseMessage();
		
		AssertableMessage tresp = new AssertableMessage("response", response);
		
		tresp.assertTransportHeader(ClientReadHeaderHandler.COPIED_REQUEST_HEADER_PREFIX + SOAHeaders.REQUEST_DATA_FORMAT, payloadType);
		tresp.assertTransportHeader(ClientReadHeaderHandler.COPIED_REQUEST_HEADER_PREFIX + SOAHeaders.RESPONSE_DATA_FORMAT, payloadType);

		// Test for SetResponseHeaderHandler
		tresp.assertTransportHeader(SetResponseHeaderHandler.KEY, SetResponseHeaderHandler.VALUE);

		// Tests for RequestObjectNodeAccessHandler
		tresp.assertTransportHeader(RequestObjectNodeAccessHandler.H_BODY_1, "SOA SOA, SOS.");
		tresp.assertTransportHeader(RequestObjectNodeAccessHandler.H_REQUEST_BODY_NODE_CLASS_TYPE, MyMessage.class.getSimpleName());
		tresp.assertTransportHeader(RequestObjectNodeAccessHandler.H_REQUEST_BODY_NODE_TYPE_PRE_DESER, ObjectNodeType.XML.name());
		tresp.assertTransportHeader(RequestObjectNodeAccessHandler.H_REQUEST_BODY_NODE_TYPE_POST_DESER, ObjectNodeType.JAVA.name());

		// Tests for ResponseObjectNodeAccessHandler
		tresp.assertTransportHeader(RequestObjectNodeAccessHandler.H_REQUEST_BODY_NODE_TYPE_POST_DESER, ObjectNodeType.JAVA.name());
		tresp.assertTransportHeader(ResponseObjectNodeAccessHandler.H_RESPONSE_BODY_NODE_CLASS_TYPE, MyMessage.class.getSimpleName());
		tresp.assertTransportHeader(ResponseObjectNodeAccessHandler.H_RESPONSE_BODY_NODE_TYPE, ObjectNodeType.JAVA.name());

		tresp.dumpParams();
		
		MyMessage msgOut = tresp.getParamMessage(0);
		String prefix = "response.param(0).MyMessage";
		Assert.assertThat(prefix + ".subject", msgOut.getSubject(), is(ServerMessageContextTestBuilder.TEST1_SUBJECT));
		
		Map<String,Address> recipients = msgOut.getRecipients();
		Assert.assertThat(prefix + ".recipients", recipients, notNullValue());
		
		Address address0 = recipients.get(ServerMessageContextTestBuilder.TEST1_EMAIL_ADDRESS);
		Assert.assertThat(prefix + ".recipients[" + ServerMessageContextTestBuilder.TEST1_EMAIL_ADDRESS + "]", address0, notNullValue());
		
		Assert.assertThat(prefix + ".recipients[" + ServerMessageContextTestBuilder.TEST1_EMAIL_ADDRESS + "].city", 
				address0.getCity(), is(TestUtils.CITY_NAME));
		
		tresp.assertTransportHeader(RequestObjectNodeAccessHandler.H_REQUEST_MSG_STRING,
				msgOut.toString());
	}

	class AssertableMessage
	{
		private String type;
		private Message message;
		public AssertableMessage(String type, Message message)
		{
			this.type = type;
			this.message = message;
		}
		
		public MyMessage getParamMessage(int index) throws ServiceException {
			// Make sure we actually have the parameter.
			Assert.assertThat(type + ".paramcount", message.getParamCount(), greaterThanOrEqualTo(index+1));
			
			Object obj = message.getParam(index);
			String prefix = type + ".param(" + index + ")";
			// Make sure it has a value
			Assert.assertThat(prefix, obj, notNullValue());
			Assert.assertTrue(prefix + " is of type MyMessage", (obj instanceof MyMessage));
			
			return (MyMessage) obj;
		}

		public void dumpParams() throws ServiceException {
			int count = message.getParamCount();
			System.out.printf("%s.paramcount=%d%n",type,count);
			for(int i=0; i<count; i++) {
				Object obj = message.getParam(i);
				if(obj == null) {
					System.out.printf("  [%d] <null>%n", i);
					continue;
				}
				if(obj instanceof MyMessage) {
					MyMessage msg = (MyMessage) obj;
					System.out.printf("  [%d] (MyMessage)%n", i);
					System.out.printf("       .subject=%s%n", msg.getSubject());
					System.out.printf("       .body=%s%n", msg.getBody());
					System.out.printf("       .recipients=%s%n", msg.getRecipients());
					System.out.printf("       .toString=%s%n", msg.toString());
					continue;
				}
				System.out.printf("  [%d] (%s) %s%n", i, obj.getClass().getName(), obj.toString());
			}
		}

		public void assertTransportHeader(String headerName,
				String expectedValue) throws ServiceException {
			Assert.assertThat(type + ".transportHeader[" + headerName + "]",
					message.getTransportHeader(headerName), is(expectedValue));
		}
	}
}
