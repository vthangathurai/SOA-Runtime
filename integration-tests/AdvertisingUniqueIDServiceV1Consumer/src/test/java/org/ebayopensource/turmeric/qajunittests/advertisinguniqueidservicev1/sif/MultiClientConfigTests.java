package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertisinguniqueidservicev1.gen.SharedAdvertisingUniqueIDServiceV1Consumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.junit.Test;


public class MultiClientConfigTests {


	@Test
	public void testFeatureEnvt() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient1  = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "feature");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("MCC Test");
		Assert.assertEquals(testClient1.echoMessage(param0).getOut(), " Echo Message = MCC Test");
		Assert.assertEquals(testClient1.getService().getResponseContext().getTransportHeader("X-TURMERIC-RESPONSE-DATA-FORMAT"), "XML");
	}

	@Test
	public void testProductionEnvt() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient1  = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "production");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("MCC Test");
		testClient1.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		Assert.assertEquals(testClient1.echoMessage(param0).getOut(), " Echo Message = MCC Test");
		Assert.assertEquals(testClient1.getService().getResponseContext().getTransportHeader("X-TURMERIC-RESPONSE-DATA-FORMAT"), "NV");
	}

	/*
	 * Default envt picked when no envtName is given is Production
	 */
	@Test
	public void testDefaultEnvt() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient1  = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("MCC Test");
		testClient1.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		Assert.assertEquals(testClient1.echoMessage(param0).getOut(), " Echo Message = MCC Test");
		Assert.assertEquals(testClient1.getService().getResponseContext().getTransportHeader("X-TURMERIC-RESPONSE-DATA-FORMAT"), "NV");
	}

	/*
	 * Custom Environment { myTestEnvt Setup }
	 *
	 */
	@Test
	public void testCustomEnvt() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient3  = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("MCC Test");
		testClient3.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		Assert.assertEquals(testClient3.echoMessage(param0).getOut(), " Echo Message = MCC Test");
		Assert.assertEquals(testClient3.getService().getResponseContext().getTransportHeader("X-TURMERIC-RESPONSE-DATA-FORMAT"), "XML");
	}



	@Test
	public void testNullEnvtValue()  {

		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("MCC Test");
		try {
			SharedAdvertisingUniqueIDServiceV1Consumer testClient4  = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", null);
			testClient4.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
			Assert.assertEquals(testClient4.echoMessage(param0).getOut(), " Echo Message = MCC Test");
			Assert.assertEquals(testClient4.getService().getResponseContext().getTransportHeader("X-EBAY-SOA-RESPONSE-DATA-FORMAT"), "NV");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(e.getMessage().contains("environment can not be null"));
		}

	}

	/* Negative Cases */

	/*
	 *	Missing client config folder from the envt.with below folder structure.
	 *		SOAAsyncMCCTestConsumer/meta-src/META-INF/soa/client/config/SOAAsyncMCCTestConsumer_client/staging/
	 *	a.	Appropriate error message should be thrown
	 */
	@Test
	public void testMissingCCFolder() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient5;

//		SharedSOAAsyncServiceConsumerEnvMapper testClient5;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" +
		"AdvertisingUniqueIDServiceV1Consumer/staging/AdvertisingUniqueIDServiceV1/" +
		"ClientConfig.xml";
		try {
			testClient5 = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "staging");
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("MCC Test");
			Assert.assertEquals(testClient5.echoMessage(param0).getOut(), " Echo Message = MCC Test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(e.getMessage().contains(errorMessage));
		}
	}

	/*
	 *	Missing client config folder from the envt.with below folder structure.
	 *		SOAAsyncMCCTestConsumer/meta-src/META-INF/soa/client/config/ SOAAsyncMCCTestConsumer_client/sandbox/ SOAAsyncService/
	 *	a.	Appropriate error message should be thrown
	 */
	@Test
	public void testMissingCC() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient6;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" +
		"AdvertisingUniqueIDServiceV1Consumer/sandbox/AdvertisingUniqueIDServiceV1/" +
		"ClientConfig.xml";
		try {
			testClient6 = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "sandbox");
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("MCC Test");
			Assert.assertEquals(testClient6.echoMessage(param0).getOut(), " Echo Message = MCC Test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(e.getMessage().contains(errorMessage));
		}

	}


	/*
	 *	Invalid client name
	 *	a.	Appropriate error message should be thrown
	 */
	@Test
	public void testMissingClientName() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient7;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" +
		"AdvertisingUniqueIDServiceV1Consumer_1/feature/AdvertisingUniqueIDServiceV1/" +
		"ClientConfig.xml";
		try {
			testClient7 = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer_1", "feature");
			Assert.assertEquals(testClient7.testEnhancedRest(null).getOut(), "10");
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains(errorMessage));

		}

	}

	@Test
	public void testMissingEnvt() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient8;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" +
				"AdvertisingUniqueIDServiceV1Consumer/myErrorEnvt/AdvertisingUniqueIDServiceV1/" +
				"ClientConfig.xml";
		try {
			testClient8 = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "myErrorEnvt");
			Assert.assertEquals(testClient8.testEnhancedRest(null).getOut(), "10");
			Assert.assertTrue("consumer creation should fail and throw exception" , false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.assertTrue(e.getMessage().contains(errorMessage));
		}
	}


	/*
	 *	When envtName is null, envMapper variable is not set
	 *		BaseSOAAsyncMCCTestConsumer testClient = new BaseSOAAsyncMCCTestConsumer(“SOAAsyncServiceTestClient”, null);  
	 *	a.	Appropriate error message should be thrown	
	 */
	@Test
	public void testNullEnvtWhenEnvtMapperVariableisNotSet() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient8;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" + 
				"SOAAsyncMCCTestServiceConsumer1_Client/myErrorEnvt/SOAAsyncService/" +
				"ClientConfig.xml";
		try {
			testClient8 = new SharedAdvertisingUniqueIDServiceV1Consumer("SOAAsyncMCCTestServiceConsumer_Client", null);
			EchoMessageRequest req = new EchoMessageRequest();
			req.setIn("test");
			Assert.assertEquals(testClient8.echoMessage(req).getOut(), "Test");
			Assert.assertTrue("consumer creation should fail and throw exception" , false);
		} catch (Exception e) {
			System.out.println(e.getMessage());
 			Assert.assertTrue(e.getMessage().contains(errorMessage));
		}
	}
	/*
	 *	Client Name is null  WhenEnvtMapperVariableisNotSet
	 *		BaseSOAAsyncMCCTestConsumer testClient = new BaseSOAAsyncMCCTestConsumer(null, "production");  
	 *	a.	Appropriate error message should be thrown	
	 *
	 */
	@Test
	public void testNullClientNameWhenEnvtMapperVariableisNotSet() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient8;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" + 
				"SOAAsyncMCCTestServiceConsumer1_Client/myErrorEnvt/SOAAsyncService/" +
				"ClientConfig.xml";
		try {
			EchoMessageRequest req = new EchoMessageRequest();
			req.setIn("test");
			Assert.assertEquals(testClient8.echoMessage(req).getOut(), "Test");
			Assert.assertTrue("consumer creation should fail and throw exception" , false);
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().contains(errorMessage));
		}
		
		
	}
	/*
	 *	Client Name is null When EnvtMapper Variable is Set
	 *		BaseSOAAsyncMCCTestConsumer testClient = new BaseSOAAsyncMCCTestConsumer(null, "production");  
	 *	a.	Appropriate error message should be thrown	
	 * http://quickbugstage.arch.ebay.com/show_bug.cgi?id=8279
	 */
	@Test
	public void testNullClientNameWhenEnvtMapperVariableisSet() {
		SharedAdvertisingUniqueIDServiceV1Consumer testClient8;
		String errorMessage = "Unable to load file: META-INF/soa/client/config/" + 
				"SOAAsyncMCCTestServiceConsumer1_Client/myErrorEnvt/SOAAsyncService/" +
				"ClientConfig.xml";
		try {
			EchoMessageRequest req = new EchoMessageRequest();
			req.setIn("test");
			Assert.assertEquals(testClient8.echoMessage(req).getOut(), "Test");
			Assert.assertTrue("consumer creation should fail and throw exception" , false);
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
    		Assert.assertTrue(e.getMessage().contains(errorMessage));
			
		}
	}
	/*
	@Test
	public void BUGDB00644700() throws ServiceException {
		BaseAdaptivePaymentsConsumer testClient = new BaseAdaptivePaymentsConsumer("SOAAsyncMCCTestServiceConsumer1_Client", "production");
		RefundRequest param0 = new RefundRequest();
		param0.setCurrencyCode("INR");
		Assert.assertEquals(testClient.refund(param0).getCurrencyCode(), " USD INR");
		Assert.assertEquals(testClient.getService().getResponseContext().getTransportHeader("X-EBAY-SOA-RESPONSE-DATA-FORMAT"), "NV");
	}
	*/
	/*
	 * Related to BUGDB00651611 
	 */
	@Test
	public void testWithClientConfigBean() {
		HttpTestClient http = HttpTestClient.getInstance();
		Map<String, String> queryParams = new HashMap<String, String>();
		String response = null;
		System.out.println(" ** testWithClientConfigBean ** ");
		try {
			queryParams.clear();
			String testURL="http://localhost:8080/ws/spf?X-TURMERIC-SOA-SERVICE-NAME=AdvertisingUniqueIDServiceV1&X-EBAY-SOA-OPERATION-NAME=getRequestID";
			response = http.getResponse(testURL , queryParams);
//			http://localhost:8080/ws/spf?X-EBAY-SOA-SERVICE-NAME=AdvertisingUniqueIDServiceV1&X-EBAY-SOA-OPERATION-NAME=getRequestID
			queryParams.put("X-TURMERIC-SOA-SERVICE-NAME", "AdvertisingUniqueIDServiceV1");
			queryParams.put("X-TURMERIC-SOA-OPERATION-NAME", "getRequestID");
			http.getResponse("http://localhost:8080/ws/spf", queryParams);
			queryParams.clear();
			queryParams.put("id","com.ebay.soa.client.AdvertisingUniqueIDServiceV2.UniqueIDServiceV2Client.dev.Invoker");
			queryParams.put("REQUEST_BINDING", "JSON");
			response = MetricUtils.invokeHttpClient(queryParams, "update");
			queryParams.put("forceXml","true");
			response = MetricUtils.invokeHttpClient(queryParams, "view");
			System.out.println("Response - " + response);
			assertTrue("Error - Request Binding is not updated to JSON ", 
					MetricUtils.parseXML(response, "REQUEST_BINDING").contentEquals("JSON"));
			System.out.println(" ** testWithClientConfigBean ** ");
		} catch (Exception se) {
			assertTrue("Error - No Exception should be thrown ", false);
		}
		queryParams.clear();
		queryParams.put("id","com.ebay.soa.client.AdvertisingUniqueIDServiceV2.UniqueIDServiceV2Client.dev.Invoker");
		queryParams.put("REQUEST_BINDING", "XML");
		response = MetricUtils.invokeHttpClient(queryParams, "update");
	}
	
