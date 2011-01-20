/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.utils.HttpTestClient;
import org.junit.Assert;
import org.junit.Test;

import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

public class WSDLEnhancementsTest {

	@Test
	public void wsdlContentDisplay2() throws Exception {
		System.out.println("testWsdlContentDisplay2()");
		HttpTestClient httpClient = HttpTestClient.getInstance();
		Map queryParams = new HashMap();
		String wsdlFileContent = httpClient.getResponse("http://localhost:8080//services/advertise/UniqueIDService/v1?wsdl", queryParams);
//		System.out.println("browser content=" +wsdlFileContent);
		CharSequence wsdlCharset = "wsdl:definitions xmlns:wsdl=";
		assertTrue("Expected string - '" + wsdlCharset +"' NOT found", wsdlFileContent.contains(wsdlCharset));
		wsdlCharset = "wsdl:service name=\"UniqueIDService\"";
		assertTrue("Expected string - '" + wsdlCharset +"' NOT found", wsdlFileContent.contains(wsdlCharset));		
		System.out.println("testWsdlContentDisplay2()");
	}
	
	/*
	 * vyaramala - Updated TC to assert on internal error
	 * As eBay server functionality has been modified.
	 */
	@Test
	public  void wsdlContentDisplay1SvcOnly() throws Exception {	
		System.out.println("testWsdlContentDisplay1SvcOnly()");
		HttpTestClient httpClient = HttpTestClient.getInstance();
		Response response = null;
		Map queryParams = new HashMap();
		//set queryParams?
		try {
			String body ="{\"greet\":[\"Test\"],}";
			response = httpClient.getResponseDB(new Request("http://localhost:8080/services/advertise/foo/v1?wsdl"), queryParams, body, "POST");
			Assert.assertEquals(response.getStatusCode(), 404);
		} catch(Exception ex){
			System.out.println("Error - Expected string - '" + response +"' NOT found");
			fail();
		}
		System.out.println("testWsdlContentDisplay1SvcOnly()");
	}	
	}
