package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.common.util.HttpTestClient;
import org.junit.Test;

import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;

public class MixedModeWithParamMappingTests {
	public static HttpTestClient http = HttpTestClient.getInstance();
	public Map<String, String> queryParams = new HashMap<String, String>();
	String response = null;
	
	@Test
	public void testRegularScenario1WithValidPayload() throws org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException, MalformedURLException {
		System.out.println(" ** testRegularScenario1WithValidPayload ** ");
		Request request = new Request(
				"http://localhost:9090/services/advertise/UniqueIDService/v2/testSchemaValidationWithUPA/2/1230/foo");
		String body = "<?xml version='1.0' encoding='UTF-8'?>" + 
			"<testSchemaValidationWithUPA xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\">" +
			"<clientId>schemavalidation</clientId><siteId>0</siteId><language>us-ENG</language>" + 
			"</testSchemaValidationWithUPA>";
		Response response = http.getResponse(request, queryParams, body, "POST");
		System.out.println(response.getBody());
		Assert.assertTrue(response.getBody().
				contains("Call reached IMPL as schemaValidation went thru fine.siteid - 1230clientid - foolang - 2"));
		System.out.println(" ** testRegularScenario1WithValidPayload ** ");
	}
	
	@Test
	public void testRegularScenarioWithMissingValuesInPayload() throws ServiceException, MalformedURLException {
		System.out.println(" ** testRegularScenarioWithMissingValuesInPayload ** ");
		Request request = new Request(
				"http://localhost:9090/services/advertise/UniqueIDService/v2/testSchemaValidationWithUPA/2/1230/foo");
		String body = "<?xml version='1.0' encoding='UTF-8'?>" + 
			"<testSchemaValidationWithUPA xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\">" +
			"<clientId></clientId><siteId></siteId><language></language>" + 
			"</testSchemaValidationWithUPA>";
		Response response = http.getResponse(request, queryParams, body, "POST");
		System.out.println(response.getBody());
		Assert.assertTrue(response.getBody()
				.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 1230clientid - foolang - 2"));
		System.out.println(" ** testRegularScenarioWithMissingValuesInPayload ** ");
	}
	
	@Test
	public void testWithPostOperationMapping1() throws ServiceException, MalformedURLException {
		System.out.println(" ** testWithPostOperationMapping ** ");
		String body = "<?xml version='1.0' encoding='UTF-8'?><testEnhancedRest" +
		" xmlns:ms=\"http://www.ebay.com/marketplace/services\"" +
		" xmlns:ns3=\"http://www.ebay.com/soa/test/user\"" +
		" xmlns:ns2=\"http://www.ebay.com/soa/test/payment\"" +
		" xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\">" +
		" <in>hello</in></testEnhancedRest>";
		Request request = new Request("http://localhost:9090/services/advertise/UniqueIDService/v1/ePo/foo");
		Response response = http.getResponse(
				request, queryParams, body,  "POST");
		System.out.println("test" + response.getBody());
		Assert.assertTrue(response.getBody().contains("<out>foo</out>"));
		
	}
	
	
	@Test
	public void testWithPostOperationMapping2() throws ServiceException, MalformedURLException {
		System.out.println(" ** testWithPostOperationMapping ** ");
		String body = "<?xml version='1.0' encoding='UTF-8'?><testEnhancedRest" +
		" xmlns:ms=\"http://www.ebay.com/marketplace/services\"" +
		" xmlns:ns3=\"http://www.ebay.com/soa/test/user\"" +
		" xmlns:ns2=\"http://www.ebay.com/soa/test/payment\"" +
		" xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\">" +
		" <in>hello</in></testEnhancedRest>";
		Request request = new Request("http://localhost:9090/services/advertise/UniqueIDService/v1/enhanced/foo");
		Response response = http.getResponse(
				request, queryParams, body,  "POST");
		System.out.println("test" + response.getBody());
		Assert.assertTrue(response.getBody().contains("<out>foo</out>"));
		
	}
	
	/*@Test
	public void testMixedModePositiveCaseRemoteMode() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		client.getService().setServiceLocation(new URL("http://localhost:9090/services/advertise/UniqueIDService/v1/enhanced/foo"));
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add("bar");
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.echoMessage(param0).getOut(), " Response foo");
	}
	
	
	@Test
	public void testMixedModePositiveCaseRemoteModeWithGetEnabled() throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		client.getServiceInvokerOptions().setREST(Boolean.TRUE);
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.testEnhancedRest(param0).getOut(), " Response foo");
	}
	@Test
	public void testMixedModePositiveCaseRemoteModeWithURL() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		client.getService().setServiceLocation(new URL("http://localhost:8080/services/advertise/UniqueIDService/v1"));
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.echoMessage(param0).getOut(), " Response MixedMode");
	}
	@Test
	public void testMixedModePositiveCaseRemoteModeWithURLGet() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		client.getServiceInvokerOptions().setREST(Boolean.TRUE);
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		client.getService().setServiceLocation(new URL("http://localhost:8080/services/advertise/UniqueIDService/v1"));
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.testEnhancedRest(param0).getOut(), " Response MixedMode");
	}
	
	@Test
	public void testMixedModePositiveCaseLocalMode() throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		client.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.testEnhancedRest(param0).getOut(), " Response foo");
	}
	@Test
	public void testMixedModePositiveCaseOtherOperationRemote() throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.testEnhancedRest(param0).getOut(), " Response foo" );
	}
	@Test
	public void testMixedModePositiveCaseOtherOperationLocal() throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode3");
		client.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.testEnhancedRest(param0).getOut(), " Response foo" );
	}*/
	
}
