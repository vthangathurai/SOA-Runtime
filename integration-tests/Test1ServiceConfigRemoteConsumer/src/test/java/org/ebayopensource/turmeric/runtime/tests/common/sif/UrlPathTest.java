/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import java.util.ArrayList;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceDispatchImpl;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.tests.common.sif.ServiceChainingTest.Verifier;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;

/**
 * @author prjande
 */
public class UrlPathTest extends BaseCallTest {
	public UrlPathTest() throws Exception {
		super("configremote");
	}

	
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver("test1",
				"configremote", BaseCallTest.CONFIG_ROOT, jetty.getSPFURI().toURL(),
				BindingConstants.PAYLOAD_XML, BindingConstants.PAYLOAD_XML);
		driver.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		driver.setVerifier(new Verifier());
		driver.setUrlPathInfo("XML?myTestOperation&test1param=abc&test2param=def");
		driver.isUrlPathTest(true);
		return driver;
	}
	
	
	protected class Verifier implements Test1Driver.SuccessVerifier  {
		public void checkSuccess(Service service, String opName, MyMessage request,
			MyMessage response, byte[] payloadData) throws Exception
		{
			ServiceInvokerOptions options = service.getInvokerOptions();
			options.setUrlPathInfo("XML?myTestOperation&test1param=abc&test2param=def");

			MyMessage encodedMsg = new MyMessage();
			encodedMsg.setBody("mymsg");

			service.invoke(opName, new Object[] {encodedMsg}, new ArrayList<Object>());
			String urlpathinfo = service.getUrlPathInfo();
			Assert.assertNotNull("Service.urlPathInfo should not be null", urlpathinfo);
			String serviceUri = service.getServiceLocation().toString() + urlpathinfo;

			String expectedUri = jetty.getSPFURI().resolve("XML?myTestOperation&test1param=abc&test2param=def").toASCIIString();
			Assert.assertEquals(expectedUri, serviceUri);
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch, Response futureResponse, MyMessage request,
				MyMessage response, byte[] payloadData, TestMode mode) throws Exception
		{
            String urlpathinfo = ((BaseServiceDispatchImpl<?>)dispatch).getUrlPathInfo();
            Assert.assertNotNull("Dispatch.urlPathInfo should not be null", urlpathinfo);
            String serviceUri = service.getServiceLocation().toString() + urlpathinfo;

            String expectedUri = jetty.getSPFURI().resolve("XML?myTestOperation&test1param=abc&test2param=def").toASCIIString();
			Assert.assertEquals(expectedUri, serviceUri);
		}
	}
}
