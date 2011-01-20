/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;


import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
public class ServiceChainingTest extends BaseCallTest {

	public ServiceChainingTest() throws Exception {
	}

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver("test1caller", //Test1Driver.TEST1_ADMIN_NAME,
				"configremote", BaseCallTest.CONFIG_ROOT, serverUri.toURL(),
				BindingConstants.PAYLOAD_XML, BindingConstants.PAYLOAD_XML,
				Test1Driver.OP_NAME_serviceChainingOperation);
		driver.setTransportHeader(Test1Constants.TR_CHAIN_HEADER, "chained_header_value");
		driver.addCookie(Test1Constants.TR_CHAIN_COOKIE, "chained_cookie_value");
		driver.setExpectingSameMessage(false);
		driver.setVerifier(new Verifier());
		return driver;
	}

	protected class Verifier implements Test1Driver.SuccessVerifier {
		public void checkSuccess(Service service, String opName, MyMessage request,
			MyMessage response, byte[] payloadData) throws Exception
		{
			String body = response.getBody();
			Assert.assertEquals("chained_header_valuechained_cookie_value", body);
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch, Response futureResponse, MyMessage request,
				MyMessage response, byte[] payloadData, TestMode mode) throws Exception
			{
				String body = response.getBody();
				Assert.assertEquals("chained_header_valuechained_cookie_value", body);
			}
	}
}

