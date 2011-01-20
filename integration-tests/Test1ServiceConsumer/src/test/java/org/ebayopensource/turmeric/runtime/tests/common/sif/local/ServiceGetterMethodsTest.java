/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.local;

import java.util.Map;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Rule;


/**
 * @author rpallikonda
 */
public class ServiceGetterMethodsTest extends BaseCallTest {
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig(CONFIG_ROOT);

	public ServiceGetterMethodsTest() throws Exception {
		super("local");
	}

	/*
	 * protected Test1Driver createDriver() throws Exception { // Test1Driver
	 * driver = new Test1Driver(m_configRoot, m_serviceURL); Test1Driver driver =
	 * new Test1Driver(Test1Driver.TEST1_QNAME, null, m_configRoot,
	 * m_serviceURL, null, null, null, TestUtils.createTestMessage());
	 * setupDriver(driver); return driver; }
	 */

	protected void setupDriver(Test1Driver driver) {
		driver.setVerifier(new Verifier());
		driver.setOutboundTransportHeader(Test1Constants.TR_CHAIN_HEADER,
				"chained_header");
		driver.addCookie(new Cookie("name1111", "value1"));
		driver.setG11nOptions(new G11nOptions());
		driver.setExpectingSameMessage(false);
	}

	protected class Verifier implements Test1Driver.SuccessVerifier {
		Verifier() {
		}

		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception {
			Assert.assertEquals("the serviceLocation returned is incorrect",
					serverUri.toASCIIString(), service
							.getServiceLocation().toString());
			Assert.assertEquals("the service Qname returned is incorrect",
					"test1", service.getServiceQName().getLocalPart());

			// TestCase.assertEquals("the WSDL Location returned is incorrect",
			// "webservices/latest/eBaySvc.wsdl", service.getWsdlLocation());
			service.getWsdlLocation(); // TODO: currently Test1Driver sets to
			// null

			// Factoring the cookie set by Test1ServiceImpl
			Cookie[] cookies = service.getCookies();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < cookies.length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				Cookie cookie = cookies[i];
				sb.append(cookie.getName());
				sb.append('=');
				sb.append(cookie.getValue());
			}
			System.out.println("Cookies returned: " + sb.toString());

			Assert.assertEquals("cookie array length should be 2", 2,
					cookies.length);

			Assert.assertTrue("G11nOption returned is invalid", service
					.getG11nOptions().getCharset().equals(
							G11nOptions.DEFAULT_CHARSET));
			service.getSessionTransportHeaders();
			String thvalue = service
					.getSessionTransportHeader(Test1Constants.TR_CHAIN_HEADER);
			Assert.assertEquals("The transport header returned is incorrect",
					"chained_header", thvalue);
			Assert.assertEquals(
					"The transport header Map returned is incorrect length", 1,
					service.getSessionTransportHeaders().size());
		}

		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResponse, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception {

			Map context;

			if (mode.equals(TestMode.ASYNC_SYNC)) {
				context = dispatch.getResponseContext();
			} else {
				context = futureResponse.getContext();
			}

			Assert.assertEquals("the serviceLocation returned is incorrect",
					serverUri.toASCIIString(), service
							.getServiceLocation().toString());
			Assert.assertEquals("the service Qname returned is incorrect",
					"test1", service.getServiceQName().getLocalPart());

			service.getWsdlLocation(); // TODO: currently Test1Driver sets to

			Assert.assertTrue("G11nOption returned is invalid", service
					.getG11nOptions().getCharset().equals(
							G11nOptions.DEFAULT_CHARSET));

			Assert.assertEquals("The transport header returned is incorrect",
					"chained_header", context
							.get("COPIED_FROM_REQ_CHAINED_HEADER"));
		}
	}
}
