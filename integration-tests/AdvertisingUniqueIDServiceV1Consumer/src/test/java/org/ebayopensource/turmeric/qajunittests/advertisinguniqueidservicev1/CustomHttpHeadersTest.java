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

import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.advertising.v1.services.ChainedTransportHeaders;
import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertising.v1.services.GetTransportHeaders;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.junit.Assert;
import org.junit.Test;


public class CustomHttpHeadersTest { 
//extends AbstractWithQEServerTest {

	
	@Test
	public void testPosWithHeaderOptionsInCC() throws ServiceException {
		System.out.println("-- testPosWithHeaderOptionsInCC --");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB1");
		GetTransportHeaders param0 = new GetTransportHeaders();
		param0.getIn().add(0, "X-EBAY-SOA-CCTEST-HEADER1");
		param0.getIn().add(1, "X-EBAY-SOA-CCTEST-HEADER2");
		param0.getIn().add(2, "X-EBAY-SOA-CCTEST-HEADER3");
		client.getTransportHeaders(param0).getOut();
		Map<String, String> respHeaders = client.getService().getResponseContext().getTransportHeaders();
//		System.out.println(respHeaders.get("X-EBAY-SOA-CCTEST-HEADER1"));
		Assert.assertEquals("BAR", respHeaders.get("X-EBAY-SOA-CCTEST-HEADER1"));
		Assert.assertEquals("80", respHeaders.get("X-EBAY-SOA-CCTEST-HEADER2"));
		Assert.assertEquals("true", respHeaders.get("X-EBAY-SOA-CCTEST-HEADER3"));
		System.out.println("-- testPosWithHeaderOptionsInCC --");
		
	}
	
	@Test
	public void testPosWithOverrideHeaderOptionsInCC() throws ServiceException {
		System.out.println("-- testPosWithOverrideHeaderOptionsInCC --");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB2");
		GetTransportHeaders param0 = new GetTransportHeaders();
		param0.getIn().add(0, "X-EBAY-SOA-OCCTEST-HEADER1");
		param0.getIn().add(1, "X-EBAY-SOA-OCCTEST-HEADER2");
		param0.getIn().add(2, "X-EBAY-SOA-OCCTEST-HEADER3");
		client.getTransportHeaders(param0).getOut();
//		System.out.println(client.getTransportHeaders(param0).getOut().get(0));
//		System.out.println(client.getTransportHeaders(param0).getOut().get(1));
//		System.out.println(client.getTransportHeaders(param0).getOut().get(2));
		Map<String, String> respHeaders = client.getService().getResponseContext().getTransportHeaders();
		Assert.assertEquals("BAR", respHeaders.get("X-EBAY-SOA-OCCTEST-HEADER1"));
		Assert.assertEquals("90", respHeaders.get("X-EBAY-SOA-OCCTEST-HEADER2"));
		Assert.assertEquals("^%", respHeaders.get("X-EBAY-SOA-OCCTEST-HEADER3"));
		System.out.println("-- testPosWithOverrideHeaderOptionsInCC --");
	}
	
	@Test
	public void testPosWithTransportHeadersInSessionHeader() throws ServiceException {
		System.out.println("-- testPosWithTransportHeadersInSessionHeader --");
		String header1 = "X-EBAY-SOA-SESSIONTEST-HEADER1";
		String header2 = "X-EBAY-SOA-SESSIONTEST-HEADER2";
		String header3 = "X-EBAY-SOA-SESSIONTEST-HEADER3";
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB2");
		GetTransportHeaders param0 = new GetTransportHeaders();
		Service svc = client.getService();
		param0.getIn().add(0, header1);
		param0.getIn().add(1, header2);
		param0.getIn().add(2, header3);	
		svc.setSessionTransportHeader(header1, "Session");
		svc.setSessionTransportHeader(header2, "100");
		svc.setSessionTransportHeader(header3, "@#!");
		List<String> response = client.getTransportHeaders(param0).getOut();
		Map<String, String> respHeaders = svc.getResponseContext().getTransportHeaders();
		Assert.assertEquals("Session", respHeaders.get(header1));
		Assert.assertEquals("100", respHeaders.get(header2));
		Assert.assertEquals("@#!", respHeaders.get(header3));
		System.out.println("-- testPosWithTransportHeadersInSessionHeader --");
	}
	
	@Test
	public void testPosWithTransportHeaderInRequestContext() throws ServiceException {
		System.out.println("-- testPosWithTransportHeaderInRequestContext --");
		String header1 = "X-EBAY-SOA-SESSIONTEST-HEADER1";
		String header2 = "X-EBAY-SOA-SESSIONTEST-HEADER2";
		String header3 = "X-EBAY-SOA-SESSIONTEST-HEADER3";
		String header4 = "X-EBAY-SOA-RCTEST-HEADER4";
		String header5 = "X-EBAY-SOA-RCTEST-HEADER5";
		String header6 = "X-EBAY-SOA-RCTEST-HEADER6";

		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB2");
		GetTransportHeaders param0 = new GetTransportHeaders();
		Service svc = client.getService();
		
		param0.getIn().add(0, header4);
		param0.getIn().add(1, header5);
		param0.getIn().add(2, header6);	
		
		svc.setSessionTransportHeader(header1, "Session");
		svc.setSessionTransportHeader(header2, "100");
		svc.setSessionTransportHeader(header3, "@#!");
		
		svc.getRequestContext().setTransportHeader(header4, "RequestContext");
		svc.getRequestContext().setTransportHeader(header5, "150");
		svc.getRequestContext().setTransportHeader(header6, "&(");
		List<String> response = client.getTransportHeaders(param0).getOut();
		
		Map<String, String> respHeaders = svc.getResponseContext().getTransportHeaders();
		Assert.assertEquals("RequestContext", respHeaders.get(header4));
		Assert.assertEquals("150", respHeaders.get(header5));
		Assert.assertEquals("&(", respHeaders.get(header6));
		System.out.println("-- testPosWithTransportHeaderInRequestContext --");
	}
	
	
	/*
	@Test
	public void testPosChainedScenarionSvc1() throws ServiceException {
		System.out.println("-- testPosChainedScenarionSvc1 --");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB7");
		ChainedTransportHeaders param0 = new ChainedTransportHeaders();
		param0.getIn().add(0, "X-EBAY-SOA-TEST");
		param0.getIn().add(1, "SVC1");
		param0.getIn().add(2, "SVC2");
		List<String> out = client.chainedTransportHeaders(param0).getOut();
//		System.out.println(out.get(0));
//		System.out.println(out.get(1));
//		System.out.println(out.get(2));
		Assert.assertEquals("X-EBAY-SOA-TEST null", out.get(0));
		Assert.assertEquals("SVC1 V1", out.get(1));
		Assert.assertEquals("SVC2 V2", out.get(2));
		System.out.println("-- testPosChainedScenarionSvc1 --");
		
	}
	
	@Test
	public void testPosChainedScenarionSvc2() throws ServiceException {
		System.out.println("-- testPosChainedScenarionSvc2 --");
		SharedAdvertisingUniqueIDServiceV2Consumer client = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer", "ESB1");
		GetNestedTransportHeaders param0 = new GetNestedTransportHeaders();
		param0.getIn().add(0, "SVC1");
		param0.getIn().add(1, "SVC2");
		GetNestedTransportHeadersResponse out = client.getNestedTransportHeaders(param0);
//		System.out.println(out.getOut().get(0));
//		System.out.println(out.getOut().get(1));
		Assert.assertEquals("SVC1 V2", out.getOut().get(0));
		Assert.assertEquals("SVC2 V2", out.getOut().get(1));
		System.out.println("-- testPosChainedScenarionSvc2 --");
		
	}*/
	
	/*@Test
	public void testPosTransportHeadersDynamicConfigBean() throws ServiceException {
		System.out.println("-- testPosTransportHeadersConfigBean --");
		HttpTestClient httpClient = HttpTestClient.getInstance();
		Map<String, String> queryParams = new HashMap<String, String>();
		String response;
		queryParams.clear();
		
		queryParams.put("SVC1", "V3");
		queryParams.put("SVC2", "V4");
		
		response = httpClient.getResponse("http://localhost:8080/Turmeric/Console/UpdateConfigCategoryXml", queryParams);
//		queryParams.put("SVC3", "V5");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB7");
		ChainedTransportHeaders param0 = new ChainedTransportHeaders();
		param0.getIn().add(0, "X-EBAY-SOA-TEST");
		param0.getIn().add(1, "SVC1");
		param0.getIn().add(2, "SVC2");
		List<String> out = client.chainedTransportHeaders(param0).getOut();
//		System.out.println(out.get(0));
//		System.out.println(out.get(1));
//		System.out.println(out.get(2));
		Assert.assertEquals("X-EBAY-SOA-TEST null", out.get(0));
		Assert.assertEquals("SVC1 V1", out.get(1));
//		Assert.assertEquals("SVC2 V4", out.get(2));
		queryParams.put("id","com.ebay.soa.client.AdvertisingUniqueIDServiceV2.UniqueIDServiceV2Client.ESB1.HTTP11.TransportHeaders");
		queryParams.put("SVC1", "V2");
		queryParams.put("SVC2", "V2");
		response = httpClient.getResponse("http://localhost:8080/Turmeric/Console/UpdateConfigCategoryXml", queryParams);
		System.out.println("-- testPosTransportHeadersConfigBean --");
	}*/
	
	@Test
	public void testNegEmptyHeaderValueInConfig() throws ServiceException {
		System.out.println("-- testNegEmptyHeaderValueInConfig --");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB3");
		GetTransportHeaders param0 = new GetTransportHeaders();
		param0.getIn().add(0, "X-EBAY-SOA-CCTEST-HEADER1");
		param0.getIn().add(1, "X-EBAY-SOA-CCTEST-header2");
//		System.out.println(client.getTransportHeaders(param0).getOut().get(1));
		Assert.assertNull(client.getService().getResponseContext().getTransportHeader("X-EBAY-SOA-CCTEST-HEADER1"));
		Assert.assertNull(client.getService().getResponseContext().getTransportHeader("X-EBAY-SOA-CCTEST-header2"));
		System.out.println("-- testNegEmptyHeaderValueInConfig --");
	}
	
	@Test
	public void testNegEmptyHeaderInConfig() {
		System.out.println("-- testNegEmptyHeaderInConfig --");
		AdvertisingUniqueIDServiceV1SharedConsumer client;
		String errorMsg = "Missing option name in option list: 'header-options'";
		try {
			client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB4");
			GetTransportHeaders param0 = new GetTransportHeaders();
			param0.getIn().add(0, "X-EBAY-SOA-CCTEST-header2");
			System.out.println(client.getTransportHeaders(param0).getOut().get(1));
			Assert.assertTrue("Test should throw ServiceCreationException", false);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains(errorMsg));
		}
		
	}
	
	@Test
	public void testNegNullHeader() {
		System.out.println("-- testNegNullHeader --");
		String header1 = "X-EBAY-SOA-TEST-HEADER1";
		String header2 = "X-EBAY-SOA-TEST-HEADER2";
		
		AdvertisingUniqueIDServiceV1SharedConsumer client;
		try {
			client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB2");
			GetTransportHeaders param0 = new GetTransportHeaders();
			Service svc = client.getService();
			
			param0.getIn().add(0, header1);
			param0.getIn().add(1, header2);
			
			svc.setSessionTransportHeader(null, "NULLHEADER");
			svc.setSessionTransportHeader(header1, "100");
			List<String> response = client.getTransportHeaders(param0).getOut();
			Assert.assertTrue("Test should throw NPE", false);
		} catch (Exception e) {
			Assert.assertTrue(true);
			
		}
		System.out.println("-- testNegNullHeader --");
		
	}
	
	@Test
	public void testNegNullHeaderValue() throws ServiceException {
		System.out.println("-- testNegNullHeaderValue --");
		String header1 = "X-EBAY-SOA-TEST-HEADER1";
		String header2 = "X-EBAY-SOA-TEST-HEADER3";
		
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB2");
		GetTransportHeaders param0 = new GetTransportHeaders();
		Service svc = client.getService();
		
		param0.getIn().add(0, header1);
		param0.getIn().add(1, header2);
		
		svc.setSessionTransportHeader(header1, null);
		svc.setSessionTransportHeader(header2, "150");
		List<String> response = client.getTransportHeaders(param0).getOut();
		
		Map<String, String> respHeaders = svc.getResponseContext().getTransportHeaders();
//		System.out.println(response.get(0));
//		System.out.println(response.get(1));
		Assert.assertNull(respHeaders.get("NULL"));
		Assert.assertEquals("150", respHeaders.get(header2));
		System.out.println("-- testNegNullHeaderValue --");
	}
	
	@Test
	public void testNegKnownSOAServiceNameHeader() throws ServiceException {
		System.out.println("-- testNegKnownSOAServiceNameHeader --");
		String header1 = "X-EBAY-SOA-SERVICE-NAME";
		String header2 = "X-EBAY-SOA-TEST-HEADER2";
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB5");
		GetTransportHeaders param0 = new GetTransportHeaders();
		Service svc = client.getService();
		param0.getIn().add(0, header1);
		param0.getIn().add(1, header2);
		List<String> response = client.getTransportHeaders(param0).getOut();
		
		Map<String, String> respHeaders = svc.getResponseContext().getTransportHeaders();
		System.out.println("Vasudha " + respHeaders.get(header1));
		Assert.assertEquals("testNegKnownSOAServiceNameHeader", respHeaders.get(header1));
		Assert.assertEquals("80", respHeaders.get(header2));	
		System.out.println("-- testNegKnownSOAServiceNameHeader --");
	}
	
	@Test
	public void testNegKnownSOARequestDataBindingHeader() throws ServiceException {
		System.out.println("-- testNegKnownSOARequestDataBindingHeader --");
		String header1 = "X-TURMERIC-REQUEST-DATA-FORMAT";
		String header2 = "X-EBAY-SOA-TEST-HEADER2";
		
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB6");
		GetTransportHeaders param0 = new GetTransportHeaders();
		Service svc = client.getService();
		
		param0.getIn().add(0, header1);
		param0.getIn().add(1, header2);
		
		svc.setSessionTransportHeader(header1, "testNegKnownSOARequestDataBindingHeader");
		svc.setSessionTransportHeader(header2, "80");
		List<String> response = client.getTransportHeaders(param0).getOut();
		Map<String, String> respHeaders = svc.getResponseContext().getTransportHeaders();

		Assert.assertEquals("XML", respHeaders.get(header1));
		Assert.assertEquals("80", respHeaders.get(header2));	
		System.out.println("-- testNegKnownSOARequestDataBindingHeader --");
	}
	
	@Test
	public void testNegKnownHTTPHeader() throws ServiceException {
		System.out.println("-- testNegKnownHTTPHeader --");
		String header1 = "CONTENT-TYPE";
		String header2 = "X-EBAY-SOA-TEST-HEADER2";
		
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB2");
		GetTransportHeaders param0 = new GetTransportHeaders();
		Service svc = client.getService();
		
		param0.getIn().add(0, header1);
		param0.getIn().add(1, header2);
		
		svc.setSessionTransportHeader(header1, "testNegKnownHTTPHeader");
		svc.setSessionTransportHeader(header2, "100");
		List<String> response = client.getTransportHeaders(param0).getOut();
		System.out.println(response.get(0));
		System.out.println(response.get(1));
		Map<String, String> respHeaders = svc.getResponseContext().getTransportHeaders();
		System.out.println("Vasudha - " + respHeaders.get(header1));
		Assert.assertEquals("text/xml; charset=UTF-8", respHeaders.get(header1));
		Assert.assertEquals("100", respHeaders.get(header2));
		System.out.println("-- testNegKnownHTTPHeader --");
			
	}
	
	@Test
	public void testNegURLEncodedHeaderValue() throws ServiceException {
		System.out.println("-- testNegURLEncodedHeaderValue --");	
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB8");
		GetTransportHeaders param0 = new GetTransportHeaders();
		param0.getIn().add(0, "X-EBAY-SOA-CCTEST-HEADER1");
		param0.getIn().add(1, "X-EBAY-SOA-CCTEST-HEADER2");
		param0.getIn().add(2, "X-EBAY-SOA-CCTEST-HEADER3");
		System.out.println(client.getTransportHeaders(param0).getOut().get(0));
		System.out.println(client.getTransportHeaders(param0).getOut().get(1));
		System.out.println(client.getTransportHeaders(param0).getOut().get(2));
		Map<String, String> respHeaders = client.getService().getResponseContext().getTransportHeaders();
		Assert.assertEquals("&", respHeaders.get("X-EBAY-SOA-CCTEST-HEADER1"));
		Assert.assertEquals("80", respHeaders.get("X-EBAY-SOA-CCTEST-HEADER2"));
		Assert.assertEquals("true", respHeaders.get("X-EBAY-SOA-CCTEST-HEADER3"));		
		System.out.println("-- testNegURLEncodedHeaderValue --");
	}
	
	
}
