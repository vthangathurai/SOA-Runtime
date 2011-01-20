/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.protocolprocessor.soap;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Test;

import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClient;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClients;

public class SOAPAutoDetectionTest extends AbstractWithServerTest {
	private static BeanConfigCategoryInfo categoryInfo;
	private static NioAsyncHttpClient asyncClient;

	static {
		try {
			categoryInfo = BeanConfigCategoryInfo.createBeanConfigCategoryInfo(
					"testconfig", // category
					// Id
					null, // alias
					SOAConstants.CONFIG_BEAN_GROUP, // category
					// group name
					false, // persistent
					true, // Ops Managable
					null, // persistFileURI
					"SOA HttpClient Configuration");
		} catch (ConfigCategoryCreateException e) {
			throw new RuntimeException(e);
		}

		asyncClient = NioAsyncHttpClients.newClient(
				"Test1ServiceSOAPAutoDetectTester", categoryInfo, 50000);
	}

	@Test
	public void soap11WithEBayHeaderForProtocol() throws Exception {
		URL url = serverUri.toURL();
		Request request = new Request(url);
		request.setMethod(Request.POST);

		// --- Set Following Header
		request.addHeader("CONTENT-TYPE", "text/xml; charset=UTF-8");
		request.addHeader("X-TURMERIC-REQUEST-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-REQUEST-GUID",
				"12547120-5450-afe3-0b25-1845fffffffe");
		request.addHeader("X-TURMERIC-SERVICE-VERSION", "1.0.0");
		request.addHeader("X-TURMERIC-MESSAGE-PROTOCOL", "SOAP11");
		request.addHeader("X-TURMERIC-RESPONSE-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		request.addHeader("X-TURMERIC-SERVICE-NAME",
				"{http://www.ebayopensource.org/turmeric/common/v1/services}test1");
		request.addHeader("Host", "localhost:8080");

		String body = "<?xml version='1.0' encoding='utf-8'?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soapenv:Header />"
				+ "<soapenv:Body>"
				+ "<xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/types1\" xmlns:ns3_=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:ns3=\"http://iop.pb.com\">"
				+ "BH Test String</xs:Message></soapenv:Body></soapenv:Envelope>";
		byte[] httpPayloadData = body.getBytes();
		request.setRawData(httpPayloadData);
		Response response = asyncClient.send(request).get();
		Assert.assertThat("Response:Status Code", response.getStatusCode(), is(200));
		Assert.assertThat("Response:Body", response.getBody(), containsString("BH Test String"));
	}

	@Test
	public void soap11WithoutHeaderForProtocol() throws Exception {
		URL url = serverUri.toURL();
		Request request = new Request(url);
		request.setMethod(Request.POST);

		// --- Set Following Header
		request.addHeader("CONTENT-TYPE", "text/xml; charset=UTF-8");
		request.addHeader("X-TURMERIC-REQUEST-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-REQUEST-GUID",
				"12547120-5450-afe3-0b25-1845fffffffe");
		request.addHeader("X-TURMERIC-SERVICE-VERSION", "1.0.0");
		request.addHeader("X-TURMERIC-RESPONSE-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		request.addHeader("X-TURMERIC-SERVICE-NAME",
				"{http://www.ebayopensource.org/turmeric/common/v1/services}test1");
		request.addHeader("SOAPAction", "echoString");
		request.addHeader("Host", "localhost:8080");

		String body = "<?xml version='1.0' encoding='utf-8'?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soapenv:Header />"
				+ "<soapenv:Body>"
				+ "<xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/types1\" xmlns:ns3_=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:ns3=\"http://iop.pb.com\">"
				+ "BH Test String</xs:Message></soapenv:Body></soapenv:Envelope>";
		byte[] httpPayloadData = body.getBytes();
		request.setRawData(httpPayloadData);
		Response response = asyncClient.send(request).get();
		Assert.assertThat("Response:Status Code", response.getStatusCode(), is(200));
		Assert.assertThat("Response:Body", response.getBody(), containsString("BH Test String"));
	}

	@Test
	public void soap12WithEBayHeaderForProtocol() throws Exception {
		URL url = serverUri.toURL();
		Request request = new Request(url);
		request.setMethod(Request.POST);

		// --- Set Following Header
		request
				.addHeader("CONTENT-TYPE",
						"application/soap+xml; charset=UTF-8");
		request.addHeader("X-TURMERIC-REQUEST-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-REQUEST-GUID",
				"12547120-5450-afe3-0b25-1845fffffffe");
		request.addHeader("X-TURMERIC-SERVICE-VERSION", "1.0.0");
		request.addHeader("X-TURMERIC-MESSAGE-PROTOCOL", "SOAP12");
		request.addHeader("X-TURMERIC-RESPONSE-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		request.addHeader("X-TURMERIC-SERVICE-NAME",
				"{http://www.ebayopensource.org/turmeric/common/v1/services}test1");
		request.addHeader("Host", "localhost:8080");

		String body = "<?xml version='1.0' encoding='utf-8'?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">"
				+ "<soapenv:Header />"
				+ "<soapenv:Body>"
				+ "<xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns4=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/types1\" xmlns:ns3=\"http://iop.pb.com\">"
				+ "BH Test String</xs:Message></soapenv:Body></soapenv:Envelope>";
		byte[] httpPayloadData = body.getBytes();
		request.setRawData(httpPayloadData);
		Response response = asyncClient.send(request).get();
		Assert.assertThat("Response:Status Code", response.getStatusCode(), is(200));
		Assert.assertThat("Response:Body", response.getBody(), containsString("BH Test String"));
	}

	@Test
	public void soap12WithoutHeaderForProtocol() throws Exception {
		URL url = serverUri.toURL();
		Request request = new Request(url);
		request.setMethod(Request.POST);

		// --- Set Following Header
		request
				.addHeader("CONTENT-TYPE",
						"application/soap+xml; charset=UTF-8");
		request.addHeader("X-TURMERIC-REQUEST-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-REQUEST-GUID",
				"12547120-5450-afe3-0b25-1845fffffffe");
		request.addHeader("X-TURMERIC-SERVICE-VERSION", "1.0.0");
		request.addHeader("X-TURMERIC-RESPONSE-DATA-FORMAT", "XML");
		request.addHeader("X-TURMERIC-OPERATION-NAME", "echoString");
		request.addHeader("X-TURMERIC-SERVICE-NAME",
				"{http://www.ebayopensource.org/turmeric/common/v1/services}test1");
		request.addHeader("Host", "localhost:8080");

		String body = "<?xml version='1.0' encoding='utf-8'?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">"
				+ "<soapenv:Header />"
				+ "<soapenv:Body>"
				+ "<xs:Message xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns4=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/types1\" xmlns:ns3=\"http://iop.pb.com\">"
				+ "BH Test String</xs:Message></soapenv:Body></soapenv:Envelope>";
		byte[] httpPayloadData = body.getBytes();
		request.setRawData(httpPayloadData);
		Response response = asyncClient.send(request).get();
		Assert.assertThat("Response:Status Code", response.getStatusCode(), is(200));
		Assert.assertThat("Response:Body", response.getBody(), containsString("BH Test String"));
	}
}
