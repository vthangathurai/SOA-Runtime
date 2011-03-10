package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.advertising.v1.services.GetTransportHeaders;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.common.util.HttpTestClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

public class OperationNameMismatchTest {
	public static HttpTestClient http = HttpTestClient.getInstance();
	public Map<String, String> queryParams = new HashMap<String, String>();
	Response response;
	String body;

	@BeforeClass
	public static void setUp() throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB1");
		GetTransportHeaders param0 = new GetTransportHeaders();
		param0.getIn().add("Test");
		System.out.println(client.getTransportHeaders(param0).getOut().get(0));
	}
	@Test
	public void testXMLIncorrectMethod() {
		System.out.println("--- testXML ---");
/*
		try {
			queryParams.put("X-TURMERIC-SERVICE-NAME", "{http://www.ebay.com/marketplace/advertising/v1/services}UniqueIDService");
			queryParams.put("X-TURMERIC-OPERATION-NAME","getTransportHeaders");
			queryParams.put("X-TURMERIC-REQUEST-DATA-FORMAT", "XML");
			queryParams.put("X-TURMERIC-RESPONSE-DATA-FORMAT","XML");
			queryParams.put("X-TURMERIC-SERVICE-VERSION","1.0.0");
			body = "<?xml version='1.0' encoding='UTF-8'?><getTransportHeadersError xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\"><in>Test</in></getTransportHeadersError>";
			Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1");
			response = http.getResponseDB(request, queryParams, body, "POST");
			Assert.assertTrue(response.getBody().contains("null"));
			System.out.println(response.getBody());
		} catch (Exception e) {
			assertTrue("Error - No Exception should be thrown ", false);
		}
		*/
		System.out.println("--- testXML ---");

	}

	@Test
	public void testIncorrectPayload() {
		System.out.println("--- testIncorrectPayload ---");
/*
		try {
			queryParams.put("X-EBAY-SOA-SERVICE-NAME", "{http://www.ebay.com/marketplace/advertising/v1/services}UniqueIDService");
			queryParams.put("X-EBAY-SOA-OPERATION-NAME","getTransportHeaders");
			queryParams.put("X-EBAY-SOA-REQUEST-DATA-FORMAT", "XML");
			queryParams.put("X-EBAY-SOA-RESPONSE-DATA-FORMAT","XML");
			queryParams.put("X-EBAY-SOA-SERVICE-VERSION","1.0.0");
			body = "<?xml version='1.0' encoding='UTF-8'?><getTransportHeadersError xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\"><in>Test</getTransportHeadersError>";
			Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1");
			response = http.getResponseDB(request, queryParams, body, "POST");
			System.out.println(response.getBody());
//			Assert.assertFalse(response.getBody().contains("wstxException"));
			Assert.assertTrue(response.getBody().contains("<errorId>5014</errorId>"));


		} catch (Exception e) {
			assertTrue("Error - No Exception should be thrown ", false);
		}*/
		System.out.println("--- testIncorrectPayload ---");
	}
	@Test
	public void testJSON() {
		System.out.println("--- testJSON ---");
		/*try {
			queryParams.put("X-EBAY-SOA-SERVICE-NAME", "{http://www.ebay.com/marketplace/advertising/v1/services}UniqueIDService");
			queryParams.put("X-EBAY-SOA-OPERATION-NAME","getTransportHeaders");
			queryParams.put("X-EBAY-SOA-REQUEST-DATA-FORMAT", "JSON");
			queryParams.put("X-EBAY-SOA-RESPONSE-DATA-FORMAT","JSON");
			queryParams.put("X-EBAY-SOA-SERVICE-VERSION","1.0.0");
			body = "{\"getTransportHeadersError\":[{\"in\":[\"Test\"]}]}";
			Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1");
			response = http.getResponseDB(request, queryParams, body, "POST");
//			System.out.println(response.getBody());
			Assert.assertTrue(response.getBody().contains("{\"getTransportHeadersResponse\":]}"));
		} catch (Exception e) {
			assertTrue("Error - No Exception should be thrown ", false);
		}*/
		System.out.println("--- testJSON ---");

	}
}
