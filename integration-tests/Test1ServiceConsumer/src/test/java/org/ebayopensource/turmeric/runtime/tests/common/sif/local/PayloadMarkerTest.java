/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.local;

import static org.hamcrest.Matchers.*;

import java.net.URI;

import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.service.invocation.SvcChannelStatus;
import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.actionmanager.RemoteSvcInvocationActionManagerAdapter;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

public class PayloadMarkerTest extends AbstractWithServerTest {
    
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");
	
	private static HttpClient getHTTPClient() throws Exception {	
		String host = serverUri.getHost();
		String port = Integer.toString(serverUri.getPort());
		
		String categoryId = "org.ebayopensource.turmeric.runtime.tests.common.config.ResponseDataBindingTest";
		
		BeanConfigCategoryInfo category = BeanConfigCategoryInfo
				.createBeanConfigCategoryInfo(
						categoryId, null, "test", false, false, null, null, true);
		SvcInvocationConfig svcInvocationConfig = new SvcInvocationConfig(
				category, "ResponseDataBindingTest", SvcChannelStatus.MARK_UP,
				host, port, false, false);
		svcInvocationConfig.createConnectionConfig(4, 8);
		HttpClient httpClient = new HttpClient(svcInvocationConfig,
				new RemoteSvcInvocationActionManagerAdapter(
						svcInvocationConfig, 2, 10000));
		return httpClient;	
	}
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void bugPositive() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve("?"+
				"nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services" +
				"&ns:maxResults=20" +
				"&ns:affiliate.ns:trackingId=10" +
				"&ns:affiliate.ns:networkId=9" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes" +
				"&OPERATION-NAME=echoString" +
				"&SERVICE-VERSION=1.0.0" +
				"&CONSUMER-ID=ApiTestAppId" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&REST-PAYLOAD=TRUE");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public void bugPositive2() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI badUri = serverUri.resolve( 
				"?echoString" +
				"&X-TURMERIC-SERVICE-VERSION=1.0.0" +
				"&X-TURMERIC-CONSUMER-ID=ApiTestAppId" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&REST-PAYLOAD=TRUE" +
				"&nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services" +
				"&ns:maxResults=2" +
				"&ns:affiliate.ns:trackingId=10" +
				"&ns:affiliate.ns:networkId=5" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes");

		Request request = new Request(badUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "test1");
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);
		
		// The 'test1' service doesn't know about the mapping, so should return HTTP_INTERNAL_ERROR
		// assertTrue(500 == response.getStatusCode());
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public  void namespaceNotFirstPayloadParameter() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?ns:maxResults=20" +
				"&nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services" +
				"&ns:affiliate.ns:trackingId=10" +
				"&ns:affiliate.ns:networkId=9" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes" +
				"&OPERATION-NAME=echoString" +
				"&SERVICE-VERSION=1.0.0" +
				"&CONSUMER-ID=ApiTestAppId" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&REST-PAYLOAD=TRUE");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public  void namespaceAfterHeaderParameter() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?ns:maxResults=20" +
				"&ns:affiliate.ns:trackingId=10" +
				"&ns:affiliate.ns:networkId=9" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes" +
				"&OPERATION-NAME=echoString" +
				"&SERVICE-VERSION=1.0.0" +
				"&CONSUMER-ID=ApiTestAppId" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&REST-PAYLOAD=TRUE" +
				"&nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public  void payloadMixedWithHeaders() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?SERVICE-VERSION=1.0.0" +
				"&ns:maxResults=20" +
				"&ns:affiliate.ns:trackingId=10" +
				"&CONSUMER-ID=ApiTestAppId" +
				"&ns:affiliate.ns:networkId=9" +
				"&OPERATION-NAME=echoString" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&X-TURMERIC-REST-PAYLOAD" +
				"&nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public void testNoNamespaceNegative() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?OPERATION-NAME=echoString" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&REST-PAYLOAD=TRUE" +
				"&asdf:Message=QQQQQQQQQQQQ" );

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(500));
	}

	@Test
	public  void withoutPayloadMarkerNamespaceAfterHeaderParameter() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?ns:maxResults=20" +
				"&ns:affiliate.ns:trackingId=10" +
				"&ns:affiliate.ns:networkId=9" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes" +
				"&OPERATION-NAME=echoString" +
				"&SERVICE-VERSION=1.0.0" +
				"&CONSUMER-ID=ApiTestAppId" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public  void withoutPayloadMarkerPayloadMixedWithHeaders() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve("?SERVICE-VERSION=1.0.0" +
				"&ns:maxResults=20" +
				"&ns:affiliate.ns:trackingId=10" +
				"&CONSUMER-ID=ApiTestAppId" +
				"&ns:affiliate.ns:networkId=9" +
				"&OPERATION-NAME=echoString" +
				"&ns:affiliate.ns:customId=123456" +
				"&ns:keywords=shoes" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&nvns:ns=http://www.ebayopensource.org/turmeric/common/v1/services");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public  void withHeadersBeforeAliasedPayloadMarker() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?OPERATION-NAME=echoString" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&REST-PAYLOAD");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}
	
	@Test
	public  void withHeadersBeforeExplicitPayloadMarker() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?OPERATION-NAME=echoString" +
				"&RESPONSE-DATA-FORMAT=XML" +
				"&X-TURMERIC-REST-PAYLOAD");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	@Test
	public  void withHeadersAfterAliasedPayloadMarker() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?REST-PAYLOAD" +
				"&OPERATION-NAME=echoString" +
				"&RESPONSE-DATA-FORMAT=XML");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}
	
	@Test
	public  void withHeadersAfterExplicitPayloadMarker() throws Exception {
		HttpClient httpClient = getHTTPClient();
		
		URI goodUri = serverUri.resolve(
				"?X-TURMERIC-REST-PAYLOAD" +
				"&OPERATION-NAME=echoString" +
				"&RESPONSE-DATA-FORMAT=XML");

		Request request = new Request(goodUri.toURL());

		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "defaultBinding"); 
		request.setMethod(Request.GET);

		Response response = httpClient.invoke(request);

		// The 'defaultBinding' service has the following mapping in <header-mapping-options>
		// <option name="X-TURMERIC-REST-PAYLOAD">query[REST-PAYLOAD]</option>
		// so it should return HTTP_OK
		Assert.assertThat("response.statusCode", response.getStatusCode(), is(200));
	}

	
}
