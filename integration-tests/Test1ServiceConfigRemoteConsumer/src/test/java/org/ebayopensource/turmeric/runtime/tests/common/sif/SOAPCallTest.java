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

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.SuccessVerifier;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.RequestHeader;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Test;


/**
 * @author gyue
 */
public class SOAPCallTest extends AbstractWithServerTest {

	@Test
	public  void localSOAPCall() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"configremote", null, serverUri.toURL(), "XML", "XML");
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

	@SuppressWarnings("unused")
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

	private static Object createDummyHeaderAsJavaObject() {
		RequestHeader header = new RequestHeader();
		header.setPartnerID("sdf");
		header.setPartnerTransactionDate("sdf");
		header.setPassword("sdfffff");
		return header;
	}

	@SuppressWarnings("unused")
	private static ObjectNode createDummyHeaderAsObjectNode() {
		ObjectNode node = new JavaObjectNodeImpl(null,
				createDummyHeaderAsJavaObject());
		return node;
	}

}
