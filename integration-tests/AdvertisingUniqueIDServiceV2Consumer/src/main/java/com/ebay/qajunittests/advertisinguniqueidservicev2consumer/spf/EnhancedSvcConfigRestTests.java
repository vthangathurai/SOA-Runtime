package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.spf;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;
import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayRequest;
import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayResponse;
import com.ebay.marketplace.services.AckValue;
import com.ebay.marketplace.services.advertisinguniqueidservicev2.advertisinguniqueidservicev2.gen.SharedAdvertisingUniqueIDServiceV2Consumer;
import com.ebay.qacommonutils.types1.HttpTestClient;
import com.ebay.soaframework.common.exceptions.ServiceCreationException;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.types.SOAHeaders;
import com.ebay.soaframework.spf.impl.internal.config.ServiceConfigManager;
/*
 * vyaramala
 * TestCases for http://jirap.corp.ebay.com:8080/browse/SOAPLATFORM-618
 */
public class EnhancedSvcConfigRestTests {
	public static HttpTestClient http = HttpTestClient.getInstance();
	public Map<String, String> queryParams = new HashMap<String, String>();
	String response = null;

	/*
	 * ServiceConfig.xml
	  <provider-options>
	   <request-params-mapping>
	 		<operation name="testSchemaValidationWithUPA">
				<option name="language" alias="lang" type="string">path[5]</option>
				<option name="sitedId" type="string">path[6]</option>
				<option name="clientId">path[7]</option>
			</operation>
		</request-params-mapping>
	  </provider-options>
	 *
	 */
	
	
	@Test
	public void testDefaultConfigurationRegularREST() {
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
		queryParams.put("language","1");
		queryParams.put("siteId", "0");
		queryParams.put("clientId","abc");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v2", queryParams);
		Assert.assertTrue(response.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
	}

	@Test
	public void testWithSIFInSPF() {
		boolean success = false;

		try {
//			ServiceConfigManager.getInstance().setConfigTestCase("config");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();

			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","localpre24");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "False");
			GetMessagesForTheDayResponse resp =
				consumer.testSchemaValidationWithUPA(param0);
			System.out.println(resp.getMessageList().get(0).getMessage());
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			exception.printStackTrace();
		}
		Assert.assertTrue(success);
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("config");
		} catch (ServiceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testDefaultConfigurationRequestParamMapping() {
		queryParams.clear();
//		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v2/testSchemaValidationWithUPA/1/0/abc", queryParams);
		System.out.println("testDefaultConfiguration" + response);
		Assert.assertTrue(response.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
	}
	@Test
	public void testDefaultConfigurationRequestParamMappingWithAlias() {
		queryParams.clear();
//		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		queryParams.put("lang","3");
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v2/testSchemaValidationWithUPA/1/0/abc", queryParams);
		System.out.println("testDefaultConfiguration" + response);
		Assert.assertTrue(response.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 3"));

	}

	@Test
	public void testWithHeaderMappingOptionsDefaultREST() {
//		Default UseCase
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testEnhancedRest");
		queryParams.put("in(0)","hello");
		String response = http.getResponse("http://localhost:8080/services/advertise/UniqueIDService/v1", queryParams);
		System.out.println("testWithHeaderMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>hello</out>"));
	}
	@Ignore 
	public void testWithHeaderMappingOptionsDefaultUseCase() {
//		Usecase with headermapping
		queryParams.clear();
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v1/testEnhancedRest/hello", queryParams);
		System.out.println("testWithHeaderMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>hello</out>"));
		System.out.println(" ** testWithHeaderMappingOptions ** ");
	}

	/*@Test
	public void testOperationMappingOptionsRegularREST() {
		System.out.println(" ** testOperationMappingOptions ** ");
//		Default UseCase
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testEnhancedRest");
		queryParams.put("in(0)","hello");
		String response = http.getResponse("http://localhost:8080/services/advertise/UniqueIDService/v1", queryParams);
		System.out.println("testOperationMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>hello</out>"));
		queryParams.clear();
	}*/
	@Test
	public void testOperationMappingOptionsDefaultUseCase() {
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v1/enhanced/hello", queryParams);
		System.out.println("testOperationMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>hello</out>"));
		System.out.println(" ** testOperationMappingOptions ** ");

	}
	@Test
//	Relative Mapping Testing for Enhanced Rest Configuration
	public void testDefaultRESTConfigurationWithRelativeMapping() {
		System.out.println(" ** testDefaultConfigurationWithRelativeMapping ** ");
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithoutUPA");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		queryParams.put("in","hello");
		String response = http.getResponse("http://localhost:8080/services/advertise/UniqueIDService/v2", queryParams);
		System.out.println("testOperationMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>helloTesting enhanced REST Feature relative mapping</out>"));
	}
	@Test
	public void testWithRelativeMapping1() {
		queryParams.clear();
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v2/testSchemaValidationWithoutUPA/Audi", queryParams);
		System.out.println("testOperationMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>AudiTesting enhanced REST Feature relative mapping</out>"));
	}
	@Test
	public void testWithRelativeMapping2() {
		queryParams.clear();
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v2/testSchemaValidationWithoutUPA/Golf", queryParams);
		System.out.println("testOperationMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>GolfTesting enhanced REST Feature relative mapping</out>"));
	}
	@Test
	public void testWithRelativeMapping3() {
		queryParams.clear();
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/services/advertise/UniqueIDService/v2/testSchemaValidationWithoutUPA/Bmw", queryParams);
		System.out.println("testOperationMappingOptions" + response);
		Assert.assertTrue(response.contains("<out>BmwTesting enhanced REST Feature relative mapping</out>"));
		System.out.println(" ** testDefaultConfigurationWithRelativeMapping ** ");
	}
	@Test
	public void testRestInLocalModePost24() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
		GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//		clientId is mandatory. But this test will not throw an error
//		param0.setClientId("schemavalidation");
		param0.setLanguage("us-ENG");
		param0.setMessageType(null);
		param0.setSiteId("0");
		System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
	}
	@Test
	public void testRestInLocalModePre24() {

	}
	@Test
	public void testWith_WS_SPF_1() {
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV2");
		queryParams.put("language","1");
		queryParams.put("siteId", "0");
		queryParams.put("clientId","abc");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/ws/spf", queryParams);
		Assert.assertTrue(response.contains("Call reached IMPL as schemaValidation went thru fine.siteid - 0clientid - abclang - 1"));
	}
	@Test
	public void testWith_WS_SPF_2() {

		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV2");
//		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testEnhancedRest");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/ws/spf/testSchemaValidationWithoutUPA/Bmw", queryParams);
		System.out.println(response);
		Assert.assertTrue(response.contains("<out>BmwTesting enhanced REST Feature relative mapping</out>"));
	}

	@Test
	public void testWith_WS_SPF_3() {
		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV1");
		response = http.getResponse(
				"http://localhost:8080/ws/spf/services/advertise/testEnhancedRest/hello", queryParams);
		Assert.assertTrue(response.contains("<out>hello</out>"));
	}

//	Error Conditions
	@Test
	public void testAbsoluteMappingWithSameIndex() {
		try {
		ServiceConfigManager.getInstance().setConfigTestCase("configAbsoluteMappingWithSameIndex");
		SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
		GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//		clientId is mandatory. But this test will not throw an error
//		param0.setClientId("schemavalidation");
		param0.setLanguage("us-ENG");
		param0.setMessageType(null);
		param0.setSiteId("0");
		System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertEquals(
					"Error validating configuration file META-INF/soa/services/configAbsoluteMappingWithSameIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
					e.getMessage());
		}
	}
	@Test
	public void testRelativeMappingWithSameIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configRelativeMappingWithSameIndex");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configRelativeMappingWithSameIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
						e.getMessage());
			}
	}
	@Test
	public void testRequestParamMappingWithNegativeIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configRequestParamMappingWithNegativeIndex");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
						e.getMessage());
			}
	}
	@Test
	public void testHeaderMappingWithNegativeIndex() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configHeaderMappingWithNegativeIndex");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
						e.getMessage());
			}

	}
	@Test
	public void testRequestParamMappingWithCharacters() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configRequestParamMappingWithCharacters");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
						e.getMessage());
			}
	}
	@Test
	public void testHeaderMappingWithCharacters() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configHeaderMappingWithCharacters");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
						e.getMessage());
			}
	}

	@Test
	public void testInvalidRequestParamMapping() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configInvalidRequestParamMapping");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configRequestParamMappingWithNegativeIndex/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Duplicates indices for url path elements",
						e.getMessage());
			}
	}
	
//	Operation Mapping with other HTTP verbs
	
	@Test
	public void testGetWithOpMapping() throws MalformedURLException {
		Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1/eG/hello");
		String body = "";
		Response response = http.getResponse(request, queryParams, body, "GET");
		System.out.println("testGetWithOpMapping" + response.getBody());
		Assert.assertTrue(response.getBody().contains("<out>hello</out>"));
		System.out.println(" ** testOperationMappingOptions ** ");
	}
	
	@Test
	public void testPostWithOpMapping() throws MalformedURLException {
		Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1/ePo");
		String body = "<?xml version='1.0' encoding='UTF-8'?><testEnhancedRest" +
		" xmlns:ms=\"http://www.ebay.com/marketplace/services\"" +
		" xmlns:ns3=\"http://www.ebay.com/soa/test/user\"" +
		" xmlns:ns2=\"http://www.ebay.com/soa/test/payment\"" +
		" xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\">" +
		" <in>hello</in></testEnhancedRest>";
		Response response = http.getResponse(request, queryParams, body, "POST");
		System.out.println("testOperationMappingOptions" + response.getBody());
		Assert.assertTrue(response.getBody().contains("<out>hello</out>"));
		System.out.println(" ** testOperationMappingOptions ** ");
	}
	
	@Test
	public void testDeleteWithOpMapping() throws MalformedURLException {
		Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1/eD/hello");
		String body = "";
		Response response = http.getResponse(
				request, queryParams,
				body, "DELETE");
		System.out.println("testOperationMappingOptions" + response.getBody());
		Assert.assertTrue(response.getBody().contains("<out>hello</out>"));
		System.out.println(" ** testOperationMappingOptions ** ");
	}
	
	@Test
	public void testPutWithOpMapping() throws MalformedURLException {
		String body = "<?xml version='1.0' encoding='UTF-8'?><testEnhancedRest" +
		" xmlns:ms=\"http://www.ebay.com/marketplace/services\"" +
		" xmlns:ns3=\"http://www.ebay.com/soa/test/user\"" +
		" xmlns:ns2=\"http://www.ebay.com/soa/test/payment\"" +
		" xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\">" +
		" <in>hello</in></testEnhancedRest>";
		Request request = new Request("http://localhost:8080/services/advertise/UniqueIDService/v1/ePu");
		Response response = http.getResponse(
				request, queryParams, body,  "PUT");
		System.out.println("testOperationMappingOptions" + response.getBody());
		Assert.assertTrue(response.getBody().contains("<out>hello</out>"));
		System.out.println(" ** testOperationMappingOptions ** ");
	}
	
//	Negative Cases
	// Defaults to GET operation
	@Test
	public void testEmptyVerb() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configEmptyVerb");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println();
			Assert.assertEquals(AckValue.SUCCESS, consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertFalse(true);
			}
	}
	
	@Test
	public void testInvalidVerb() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configInvalidVerb");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			Assert.assertTrue(false);
			} catch (Exception e) {
				Assert.assertEquals(
						"Error validating configuration file META-INF/soa/services/configInvalidVerb/AdvertisingUniqueIDServiceV2/ServiceConfig.xml: Unsupported Http Verb supplied (supported values are : GET,POST,PUT,DELETE): 'FOO'",
						e.getMessage());
			}
	}
	
	@Test
	public void testDuplicateMethodNameMapping() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configDuplicateMethodNameMapping");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			Assert.assertEquals(AckValue.SUCCESS, consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				Assert.assertFalse(true);
			}
	}
	
	@Test
	public void testEmptyOpNameMapping() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configEmptyOpNameMapping");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
			Assert.assertTrue(false);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				
			}
	}

	@Test
	public void testVerbCaseSensitivity() {
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configVerbCaseSensitivity");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			System.out.println(consumer.testSchemaValidationWithUPA(param0).getAck());
//			Assert.assertEquals(AckValue.SUCCESS, consumer.testSchemaValidationWithUPA(param0).getAck());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.assertFalse(true);
			}
	}
	
	
}
