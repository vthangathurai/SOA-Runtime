/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.config;

import static org.hamcrest.Matchers.*;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.service.invocation.SvcChannelStatus;
import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.actionmanager.RemoteSvcInvocationActionManagerAdapter;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

public class HeaderMappingTest extends AbstractWithServerTest {
	private static final String serviceName = "headerMapping";

	private static HttpClient httpClient;
	
	@Before
	public void setUp() throws Exception {
		String host = serverUri.getHost();
		String port = String.valueOf( serverUri.getPort() );

		SvcInvocationConfig svcInvocationConfig = new SvcInvocationConfig(
				BeanConfigCategoryInfo
						.createBeanConfigCategoryInfo(
								"org.ebayopensource.turmeric.runtime.tests.config.HeaderMappingTest",
								null, "test", false, false, null, null,
								true), "HeaderMappingTest",
				SvcChannelStatus.MARK_UP, host, port, false, false);
	
		svcInvocationConfig.createConnectionConfig(4, 8);
		httpClient = new HttpClient(svcInvocationConfig,
				new RemoteSvcInvocationActionManagerAdapter(
						svcInvocationConfig, 2, 10000));
	}

	private static Request createRequest() throws Exception {
		Request request = new Request(serverUri.toURL());
		request.addHeader("CONTENT-TYPE", "text/plain; charset=UTF-8");
		request.addHeader("X-TURMERIC-SERVICE-NAME", "{" + SOAConstants.DEFAULT_SERVICE_NAMESPACE + "}" + serviceName);
		request.addHeader("X-TURMERIC-REQUEST-DATA-FORMAT", "NV");
		request.addHeader("X-TURMERIC-RESPONSE-DATA-FORMAT", "NV");
		request.addParameter("Message(0)", "Emeryville");
		request.setMethod(Request.POST);

		return request;
	}

	@Test
	public void testHeaderMapping() throws Exception {
		Request request = createRequest();
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		//request.addHeader("Z-OPNAME", "echoString");
		Response response = httpClient.invoke(request);
		String body = response.getBody();
		System.out.println("<><><>body: "+body);
		Assert.assertThat(body, not(containsString("exception-id")));
		Assert.assertThat(body, containsString("Message(0)=\"Emeryville\""));
	}

	@Test
	public void testHeaderMappingConflict() throws Exception {
		Request request = createRequest();
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		request.addHeader("Z-OPNAME", "shoutString");
		Response response = httpClient.invoke(request);
		String body = response.getBody();
		System.out.println("<><>Body: "+body);
		String expectedContent = ("Input URL gave a value for header " +
				"X-TURMERIC-OPERATION-NAME equal to echoString but has a " +
				"conflicting mapped value").replace(' ', '+');
		Assert.assertThat("Contains expected response", body, containsString(expectedContent));
	}

	@Test
	public void testHeaderSuppression() throws Exception {
		Request request = createRequest();
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		request.addHeader("X-TURMERIC-SERVICE-VERSION", "5.6.7");
		Response response = httpClient.invoke(request);
		String version = response.getHeader("COPIED_FROM_REQ_X-TURMERIC-SERVICE-VERSION");
		Assert.assertThat(version, is(nullValue()));
	}
}
