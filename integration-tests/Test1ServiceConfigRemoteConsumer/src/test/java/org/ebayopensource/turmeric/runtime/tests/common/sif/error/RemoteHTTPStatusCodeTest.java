/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import static org.hamcrest.Matchers.*;

import java.net.HttpURLConnection;

import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransportConfig;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

/**
 * @author idralyuk
 */
public class RemoteHTTPStatusCodeTest extends AbstractWithServerTest {
	protected String baseUrl;

	@Before
	public void initUrl() throws Exception
	{
		baseUrl = serverUri.resolve("?myNonArgOperation" +
				"&X-TURMERIC-SERVICE-NAME=test1" +
				"&ver=5.6.7").toASCIIString();
	}
	
	private static HttpClient s_client;

	public static synchronized HttpClient getClient() throws Exception {
		if (s_client != null) {
			return s_client;
		}

		TransportOptions options = new TransportOptions();
		options.getProperties().put(SOAConstants.HTTP_VERSION, SOAConstants.TRANSPORT_HTTP_11);
		HTTPClientTransportConfig config = new HTTPClientTransportConfig(RemoteHTTPStatusCodeTest.class.getName(), options);
		SvcInvocationConfig svcConfig = config.getSvcInvocationConfig();
		HttpClient client = new HttpClient(svcConfig, null);

		s_client = client;
		return s_client;
	}

	@Test
	public void nonSOAPDefault() throws Exception {
		HttpClient client = getClient();
		Request request = new Request(baseUrl);
		Response response = client.invoke(request);
		Assert.assertThat("response.body should look like error",
				response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME
						.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_INTERNAL_ERROR));
	}

	@Test
	public void nonSOAPAlternate() throws Exception {
		HttpClient client = getClient();
		Request request = new Request(baseUrl + "&X-TURMERIC-ALTERNATE-FAULT-STATUS");
		Response response = client.invoke(request);
		Assert.assertThat("response.body should look like error",
				response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME
						.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_OK));
	}

	@Test
	public void sOAPDefault() throws Exception {
		HttpClient client = getClient();
		Request request = new Request(baseUrl + "&X-TURMERIC-MESSAGE-PROTOCOL=SOAP11&X-TURMERIC-REQUEST-DATA-FORMAT=XML");
		Response response = client.invoke(request);
		Assert.assertThat("response.body should look like error",
				response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME
						.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_INTERNAL_ERROR));
	}

	@Test
	public void sOAPAlternate() throws Exception {
		HttpClient client = getClient();
		Request request = new Request(baseUrl + "&X-TURMERIC-MESSAGE-PROTOCOL=SOAP11&X-TURMERIC-REQUEST-DATA-FORMAT=XML&X-TURMERIC-ALTERNATE-FAULT-STATUS");
		Response response = client.invoke(request);
		Assert.assertThat("response.body should look like error",
				response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME
						.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_OK));
	}
}
