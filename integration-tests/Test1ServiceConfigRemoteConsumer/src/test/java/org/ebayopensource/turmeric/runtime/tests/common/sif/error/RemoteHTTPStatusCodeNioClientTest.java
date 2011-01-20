/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.net.HttpURLConnection;
import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPClientTransportConfig;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Test;

import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

/**
 * @author idralyuk
 */
public class RemoteHTTPStatusCodeNioClientTest extends AbstractWithServerTest {

	private static final String TEST_URL_PARAMS = "?myNonArgOperation&X-TURMERIC-SERVICE-NAME=test1&X-TURMERIC-SERVICE-NAME=test1&ver=5.6.7"; 

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
	public void testNonSOAPDefault() throws Exception {
		HttpClient client = getClient();
		URL serviceURL = serverUri.resolve(TEST_URL_PARAMS).toURL();
		Request request = new Request(serviceURL);
		Response response = client.invoke(request);

		Assert.assertThat("response.body looks like an error", response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_INTERNAL_ERROR));
	}

	@Test
	public void testNonSOAPAlternate() throws Exception {
		HttpClient client = getClient();
		URL serviceURL = serverUri.resolve(TEST_URL_PARAMS + 
				"&X-TURMERIC-SERVICE-NAME=test1" +
				"&X-TURMERIC-ALTERNATE-FAULT-STATUS").toURL();
		Request request = new Request(serviceURL);
		Response response = client.invoke(request);

		Assert.assertThat("response.body looks like an error", response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_OK));
	}

	@Test
	public void testSOAPDefault() throws Exception {
		HttpClient client = getClient();
		URL serviceURL = serverUri.resolve(TEST_URL_PARAMS + 
				"&X-TURMERIC-MESSAGE-PROTOCOL=SOAP11" +
				"&X-TURMERIC-REQUEST-DATA-FORMAT=XML").toURL();
		Request request = new Request(serviceURL);
		Response response = client.invoke(request);

		Assert.assertThat("response.body looks like an error", response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_INTERNAL_ERROR));
	}

	@Test
	public void testSOAPAlternate() throws Exception {
		HttpClient client = getClient();
		URL serviceURL = serverUri.resolve(TEST_URL_PARAMS + 
				"&X-TURMERIC-MESSAGE-PROTOCOL=SOAP11" +
				"&X-TURMERIC-REQUEST-DATA-FORMAT=XML" +
				"&X-TURMERIC-ALTERNATE-FAULT-STATUS").toURL();
		Request request = new Request(serviceURL);
		Response response = client.invoke(request);

		Assert.assertThat("response.body looks like an error", response.getBody(), 
				containsString(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart()));
		Assert.assertThat("response.statusCode", response.getStatusCode(), 
				is(HttpURLConnection.HTTP_OK));
	}
}
