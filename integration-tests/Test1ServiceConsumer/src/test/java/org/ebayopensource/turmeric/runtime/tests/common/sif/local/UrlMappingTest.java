/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.local;



import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransportConfig;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

public class UrlMappingTest extends AbstractWithServerTest {
	private static HttpClient s_client;

    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	public static HttpClient getClient() throws Exception {
		if (s_client != null) {
			return s_client;
		}

		TransportOptions options = new TransportOptions();
		options.getProperties().put(SOAConstants.HTTP_VERSION, SOAConstants.TRANSPORT_HTTP_11);
		HTTPClientTransportConfig config = new HTTPClientTransportConfig(UrlMappingTest.class.getName(), options);
		SvcInvocationConfig svcConfig = config.getSvcInvocationConfig();
		HttpClient client = new HttpClient(svcConfig, null);

		s_client = client;
		return s_client;
	}

	@Test
	public void mappingOnGet() throws Exception {
		subtestMapping("GET");
	}

	@Test
	public void mappingOnPost() throws Exception {
		subtestMapping("POST");
	}

	/*
	 * service name (test1) can be specified in:
	 * 1. uri <param-value>path[3]</param-value>
	 * 		http://localhost:8080/ws/spf/json/test1?myNonArgOperation&ver=1.0.0
	 * 2. query as X-TURMERIC-SERVICE-NAME=test1
	 * 		http://localhost:8080/ws/spf/json?myNonArgOperation&X-TURMERIC-SERVICE-NAME=test1&ver=1.0.0
	 * 3. query as SERVICE-NAME=test1 , <param-value>query[SERVICE-NAME]</param-value>
	 * 		http://localhost:8080/ws/spf/json?myNonArgOperation&SERVICE-NAME=test1&ver=1.0.0
	 * 4. query as ?test1, <param-value>queryop</param-value>
	 * 		http://localhost:8080/ws/spf/json?test1&X-TURMERIC-OPERATION-NAME=myNonArgOperation&ver=1.0.0
	 *
	 * Before starting server, update web.xml <init-param> <param-value> to match the request url param in subtestMapping() below
	 * 		v3j2ee\ConfigInitWar\webApplication\WEB-INF\web.xml
	 *  <init-param>
     *    <param-name>SOA_SERVICE_URL_MATCH_EXPRESSION</param-name>
     *    <param-value>queryop</param-value> or  query[SERVICE-NAME] or path[3]
     *  </init-param>
	 */
	private void subtestMapping(String method) throws Exception {
		HttpClient client = getClient();
		//Request request = new Request("http://localhost:8080/ws/spf/json/test1?myNonArgOperation&ver=1.0.0"); //path[3]
		//Request request = new Request("http://localhost:8080/ws/spf/json?test1&X-TURMERIC-OPERATION-NAME=myNonArgOperation&ver=1.0.0"); //queryop
		//Request request = new Request("http://localhost:8080/ws/spf/json?test1&ver=1.0.0"); //queryop - negative test, op not specified in config either
		//Request request = new Request("http://localhost:8080/ws/spf/json?myNonArgOperation&SERVICE-NAME=test1&ver=1.0.0");//query[SERVICE-NAME]
		
		Request request = new Request(serverUri.resolve("json?myNonArgOperation&X-TURMERIC-SERVICE-NAME=test1&ver=1.0.0").toASCIIString());
		request.setMethod(method);
		Response response = client.invoke(request);
		String dataStr = response.getBody();
		System.out.println(dataStr);
		if (!dataStr.startsWith("{")) {
//		if (!dataStr.startsWith("{\"jsonns.xsi\":")) {
		//if (!dataStr.startsWith("{") || !dataStr.endsWith("}")) {
			Assert.fail("Response does not look like JSON: " + dataStr);
		}
		if (dataStr.indexOf(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()) != -1) {
			Assert.fail("Response looks like error: " + dataStr);
		}
	}

	@Test
	public void conflict() throws Exception {
		HttpClient client = getClient();
		Request request = new Request(serverUri.resolve("?myNonArgOperation&X-TURMERIC-SERVICE-NAME=test1&ver=1.0.0&reqbind=NV&X-TURMERIC-REQUEST-DATA-FORMAT=XML").toASCIIString());
		Response response = client.invoke(request);
		String dataStr = response.getBody();
		System.out.println(dataStr);
		if (dataStr.indexOf(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()) == -1) {
			Assert.fail("Response does not look like error: " + dataStr);
		}
		Assert.assertTrue(dataStr.contains("Input URL gave a value for header X-TURMERIC-REQUEST-DATA-FORMAT equal to XML but has a conflicting mapped value, NV"));
	}

	@Test
	public void badVersion() throws Exception {
		HttpClient client = getClient();
		Request request = new Request(serverUri.resolve("?myNonArgOperation&X-TURMERIC-SERVICE-NAME=test1&ver=2.3.4").toASCIIString());
		Response response = client.invoke(request);
		String dataStr = response.getBody();
		System.out.println(dataStr);
		if (dataStr.indexOf(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()) == -1) {
			Assert.fail("Response does not look like error: " + dataStr);
		}
		Assert.assertTrue(dataStr.contains("Service version 2.3.4 is unsupported"));
	}
}
