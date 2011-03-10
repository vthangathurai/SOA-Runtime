/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/

package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertising.v1.services.TestPrimitiveTypesRequest;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerQETest;
import org.junit.Test;

public class SvcInvocationTests extends AbstractWithServerQETest {

	@Test
	public void testRemoteMode1() throws ServiceException, MalformedURLException {
//		ServiceConfigManager.getInstance().setConfigTestCase("testConfig");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "production");
		System.out.println("uri VASU" + serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(" Echo Message = Foo", client.echoMessage(param0).getOut());
//		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
//		byte b = 12;
//		request.setTypeByte(b);
//		System.out.println(client.testPrimitiveTypes(request).getOut());
	
		
	}
	@Test
	public void testRemoteMode3() throws ServiceException, MalformedURLException {
		
//		/		ServiceConfigManager.getInstance().setConfigTestCase("testConfig");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "production");
		System.out.println("uri VASU" + serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		byte b = 12;
		request.setTypeByte(b);
		System.out.println(client.testPrimitiveTypes(request).getOut());
		Assert.assertEquals("From Server 12", client.testPrimitiveTypes(request).getOut());
	}


	@Test
	public void testRemoteMode() throws ServiceException, MalformedURLException {
//		ServiceConfigManager.getInstance().setConfigTestCase("testConfig");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "production");
		System.out.println("uri VASU" + serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
//		EchoMessageRequest param0 = new EchoMessageRequest();
//		param0.setIn("Foo");
//		System.out.println(client.echoMessage(param0).getOut());
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		byte b = 12;
		request.setTypeByte(b);
		System.out.println(client.testPrimitiveTypes(request).getOut());
	
		
	}
}
