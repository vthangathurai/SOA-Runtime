/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import static org.junit.Assert.assertNull;

import java.net.URL;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.SuccessVerifier;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.RequestHeader;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author gyue
 */
public class RemoteSOAPCallTest extends AbstractWithServerTest  {

	@Test
	public  void remoteSOAPCall() throws Exception {
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap12(true);
		driver.setRepeatCount(1);
		driver.doCall();
	}

	@Test
	public  void remoteSOAPCallWithoutArgument() throws Exception {
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML",
				"myNonArgOperation");
		driver.setVerifier(new NullResponseVerifier());
		driver.setExpectingSameMessage(false);

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();
	}

	private  class NullResponseVerifier implements SuccessVerifier {

		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception {
			assertNull("Expecting null response but got non-null back",
					response);
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResponse, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception {
			assertNull("Expecting null response but got non-null back",
					response);
		}
	}

	@Test
	public  void remoteSOAPCallPositive_SingleMsgHeaderThruHandler()
			throws Exception {
		

		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configmsgheader", null, serviceURL, "XML", "XML");
		driver.setExpectResponseMsgHeader(true);

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		

	}

	@Test
	public  void remoteSOAPCallPositive_SingleMsgHeaderThruContext()
			throws Exception {
		
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver.addMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	@Test
	public  void remoteSOAPCallPositive_MultipleMsgHeaderThruContext()
			throws Exception {
		
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver.addMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());
		driver.addMessageHeader(createDummyHeaderAsObjectNode());
		driver.addMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	@Test
	public  void remoteSOAPCallPositive_SingleMsgHeaderThruSession()
			throws Exception {
		
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver
				.addSessionMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	@Test
	public  void remoteSOAPCallPositive_MultipleMsgHeaderThruSession()
			throws Exception {
		
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver
				.addSessionMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());
		driver.addSessionMessageHeader(createDummyHeaderAsObjectNode());
		driver
				.addSessionMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	@Test
	public  void remoteSOAPCallPositive_MultipleMsgHeaderThruContextAndSession()
			throws Exception {
		
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver.addMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());
		driver.addMessageHeader(createDummyHeaderAsObjectNode());
		driver
				.addSessionMessageHeaderAsJavaObject(createDummyHeaderAsJavaObject());
		driver.addSessionMessageHeader(createDummyHeaderAsObjectNode());

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	@Test
	public  void remoteSOAPCallNegative_BadServiceURLSyncOnly()
			throws Exception {
		
		//URL serviceURL = serverUri.resolve("badddd").toURL();
		URL serviceURL = new URL("http://localhostbaddd:4146/ws/spf");
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver.setExpectedError(7000, ServiceInvocationException.class,
				ServiceInvocationRuntimeException.class, "error");
		driver.setNoPayloadData(true);
		driver.skipAyncTest(true);

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}
	
	@Test
	@Ignore //see TURMERIC-1097
	public  void remoteSOAPCallNegative_BadServiceURLAsync() throws Exception {
		
		//URL serviceURL = serverUri.resolve("badddd").toURL();
		URL serviceURL = new URL("http://localhostbaddd:4146/ws/spf");
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, null, null);
		driver.setExpectedError(7000, ServiceInvocationException.class,
				ServiceInvocationRuntimeException.class, "error");
		driver.setNoPayloadData(true);

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	@Test
	public  void remoteSOAPCallNegative_ServerError()
			throws Exception {
		
		URL serviceURL = serverUri.toURL();
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serviceURL, "XML", "XML");
		driver.setHeader_Test1Exception(true);
		driver.setExpectedError(2005, ErrorMessage.class,
				ServiceInvocationRuntimeException.class, "Test1Exception");
		driver.setNoPayloadData(true);

		// SOAP 11
		System.out.println(">> SOAP11");
		driver.setUseSoap11(true);
		driver.setRepeatCount(1);
		driver.doCall();

		// SOAP 12
		System.out.println(">> SOAP12");
		driver.setUseSoap12(true);
		driver.setRepeatCount(1);
		driver.doCall();

		
	}

	private static Object createDummyHeaderAsJavaObject() {
		RequestHeader header = new RequestHeader();
		header.setPartnerID("sdf");
		header.setPartnerTransactionDate("sdf");
		header.setPassword("sdfffff");
		return header;
	}

	private static ObjectNode createDummyHeaderAsObjectNode() {
		ObjectNode node = new JavaObjectNodeImpl(null,
				createDummyHeaderAsJavaObject());
		return node;
	}

}
