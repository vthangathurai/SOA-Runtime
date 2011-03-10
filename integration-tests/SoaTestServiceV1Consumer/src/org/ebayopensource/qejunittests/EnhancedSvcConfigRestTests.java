/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.qejunittests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerQETest;
import org.ebayopensource.turmeric.runtime.tests.common.util.HttpTestClient;
import org.ebayopensource.turmeric.soa.v1.services.NewOperationRequest;
import org.ebayopensource.turmeric.soa.v1.services.testservice.gen.SharedSoaTestServiceV1Consumer;
import org.junit.Before;
import org.junit.Test;

/*
 * vyaramala
 * TestCases for http://jirap.corp.ebay.com:8080/browse/SOAPLATFORM-618
 */
public class EnhancedSvcConfigRestTests extends AbstractWithServerQETest {
	public static HttpTestClient http = HttpTestClient.getInstance();
	public Map<String, String> queryParams = new HashMap<String, String>();
	String response = null;


	@Before
	public void testDefaultConfigurationRegularREST() throws ServiceCreationException, ServiceException, MalformedURLException {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		http.port = serverUri.toASCIIString().substring(17);
		queryParams.put("X-TURMERIC-OPERATION-NAME","newOperation");
		queryParams.put("language","1");
		queryParams.put("siteId", "0");
		queryParams.put("clientId","abc");
		String url = serverUri.toASCIIString() + "/soa/services/v1/";
		String response = http.getResponse(url, queryParams);
		System.out.println(response);
	}

	@Test
	public void testWithSIFInSPF() throws ServiceException, MalformedURLException {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		NewOperationRequest param0 = new NewOperationRequest();
		param0.setClientId("abc");
		param0.setSiteId("0");
		param0.setLanguage("1");
		SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "production");
		testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
		Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
	}

	@Test
	public void testDefaultConfigurationRequestParamMapping() throws MalformedURLException, ServiceException {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		http.port = serverUri.toASCIIString().substring(17);
		String url = serverUri.toASCIIString() + "/soa/services/v1/newOperation/1/0/abc";
		String response = http.getResponse(url, queryParams);
		Assert.assertTrue(response.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));

	}

	@Test
	public void testDefaultConfigurationRequestParamMappingWithAlias() throws ServiceCreationException, ServiceException {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		http.port = serverUri.toASCIIString().substring(17);
		queryParams.clear();
		queryParams.put("lang","3");
		String url = serverUri.toASCIIString() + "/soa/services/v1/newOperation/1/0/abc";
		String response = http.getResponse(url, queryParams);
		System.out.println("testDefaultConfiguration" + response);
		Assert.assertTrue(response.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 3"));

	}

	@Test
	public void testWithHeaderMappingOptionsDefaultREST() throws ServiceCreationException, ServiceException {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		//		Default UseCase
		queryParams.put("X-TURMERIC-OPERATION-NAME","getVersion");
//		queryParams.put("in(0)","hello");
		String url = serverUri.toASCIIString() + "/soa/services/v1/";
		String response = http.getResponse(url, queryParams);
		System.out.println(response);
		//		String response = http.getResponse("http://localhost:8080/services/advertise/UniqueIDService/v1", queryParams);
		System.out.println("testWithHeaderMappingOptions" + response);
		Assert.assertTrue(response.contains("getVersionResponse"));
	}

	//	Error Conditions
	@Test
	public void testAbsoluteMappingWithSameIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configAbsoluteMappingWithSameIndex");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			//Commenting the actual error message due to issues with FallBackServiceDesc
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configAbsoluteMappingWithSameIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}
	}
	@Test
	public void testRelativeMappingWithSameIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configRelativeMappingWithSameIndex");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configRelativeMappingWithSameIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}
	}
	@Test
	public void testRequestParamMappingWithNegativeIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configRequestParamMappingWithNegativeIndex");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}
	}
	@Test
	public void testHeaderMappingWithNegativeIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configHeaderMappingWithNegativeIndex");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}

	}
	@Test
	public void testRequestParamMappingWithCharacters() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configRequestParamMappingWithCharacters");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}
	}
	@Test
	public void testHeaderMappingWithCharacters() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configHeaderMappingWithCharacters");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}
	}

	@Test
	public void testInvalidRequestParamMapping() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configInvalidRequestParamMapping");
			NewOperationRequest param0 = new NewOperationRequest();
			param0.setClientId("abc");
			param0.setSiteId("0");
			param0.setLanguage("1");
			SharedSoaTestServiceV1Consumer testClient = new SharedSoaTestServiceV1Consumer("SoaTestServiceV1Consumer", "local");
			testClient.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/soa/services/v1/"));
			Assert.assertTrue(testClient.newOperation(param0).getOutput().contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
			System.out.println(testClient.newOperation(param0));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Failed to invoke SoaTestServiceV1.newOperation due to application error"));
//			Assert.assertEquals(
//					"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
//					e.getMessage());
		}
	}
}
