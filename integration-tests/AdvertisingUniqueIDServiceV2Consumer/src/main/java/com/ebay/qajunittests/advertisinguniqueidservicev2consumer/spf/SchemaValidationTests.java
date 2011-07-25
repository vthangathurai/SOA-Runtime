package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.spf;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayRequest;
import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayResponse;
import com.ebay.marketplace.services.advertisinguniqueidservicev2.advertisinguniqueidservicev2.gen.SharedAdvertisingUniqueIDServiceV2Consumer;
import com.ebay.qacommonutils.types1.HttpTestClient;
import com.ebay.soaframework.common.exceptions.ServiceCreationException;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.types.ByteBufferWrapper;
import com.ebay.soaframework.common.types.SOAHeaders;
import com.ebay.soaframework.sif.impl.internal.config.ClientConfigManager;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;
import com.ebay.soaframework.sif.service.ServiceInvokerOptions;
import com.ebay.soaframework.spf.impl.internal.config.ServiceConfigManager;

public class SchemaValidationTests {

	String errorMsg ="Data validation error(line 1, col 141): cvc-complex-type.2.4.a: Invalid content was found starting with element 'siteId'. One of '{\"http://www.ebay.com/marketplace/advertising/v1/services\":clientId}' is expected.";
	public static HttpTestClient http = HttpTestClient.getInstance();
	public Map<String, String> queryParams = new HashMap<String, String>();
	String response;	
	/*
	 * FALSE set for ValidatePayload in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = FALSE
	 *	a.	Verify schema is loaded on service initialization
	 *	b.	And the header from client side is silently ignored.
	 */
	@Test
	public void testFalseSvcConfigScenario1() throws ServiceException {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadFalse");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "False");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
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
	/*
	 * False set for validatePayload in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = TRUE 
	 *	a.	Verify schema is loaded on service initialization
	 *	b.	And payload is validated
	 */
	@Test
	public void testFalseSvcConfigScenario2() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadFalse");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "True");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			Assert.assertEquals(errorMsg, exception.getMessage());
			exception.printStackTrace();
		}
		Assert.assertFalse(success);
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

	/*
	 * False set for validatePayload in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = TRUE 
	 *	a.	Verify schema is loaded on service initialization
	 *	b.	And payload is validated
	 */
	@Test
	public void testFalseSvcConfigScenario3() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadFalse");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "true");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);

			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			exception.printStackTrace();
		}
		Assert.assertTrue(success);

	}	
	
	/*
	 * FALSE set for schemaValidationLevel in ServiceConfig
	 *	No Header from client side
	 * 	a. Verify no validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testFalseSvcConfigNoTransportHeader() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadFalse");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new 
				SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			System.out.println(exception.getMessage());
			exception.printStackTrace();
		}
		Assert.assertTrue(success);

	}

	/*
	 * TRUE set for schemaValidationLevel in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = FALSE
	 * 	a. Verify no validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testTrueSvcConfigTransportHeaderFalse1() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadTrue");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "False");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
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
	
	/*
	 * TRUE set for schemaValidationLevel in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = FALSE
	 * 	a. Verify no validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testTrueSvcConfigTransportHeaderFalse2() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadTrue");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new 
				SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "False");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
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
	
	/*
	 * True set for validatePayload in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = TRUE
	 * 	a. Verify no validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testTRUESvcConfigTransportHeaderTRUE() {
		boolean success = false;
		
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadTrue");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new 
				SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "true");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			exception.printStackTrace();
			Assert.assertEquals(errorMsg, exception.getMessage());
		}
		Assert.assertFalse(success);
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

	/*
	 * TRUE set for validatePayload in ServiceConfig
	 *	X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL = doesnot exist
	 * 	a. Verify validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testTrueSvcConfigTransportHeaderNONE() {
		boolean success = false;
		
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadTrue");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new 
				SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			exception.printStackTrace();
			success = false;
			Assert.assertEquals(errorMsg, exception.getMessage());
		}
		Assert.assertFalse(success);
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
	/*
	 *  ValidatePayload does not exist in ServiceConfig
	 *	No Transport header
	 * 	a. Verify no validation performed
	 *  b. Verify is not schema is loaded
	 */
	@Test 
	public void testDefaultSvcConfigNoTransportHeader() {
		boolean success = false;
		
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadDoesNotExist");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			Assert.assertEquals(errorMsg, exception.getMessage());
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

	/*
	 *  ValidatePayload does not exist in ServiceConfig
	 *	No Transport header
	 * 	a. Verify no validation performed
	 *  b. Verify is not schema is loaded
	 *  c. header should be ignored silently
	 */
	@Test 
	public void testDefaultSvcConfigTransportHeaderExists() {
		boolean success = false;
		
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadDoesNotExist");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = 
				new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "true");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
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
	
	/*
	 * true set for validatePayload in ServiceConfig
	 *	Null Transport header
	 * 	a. Verify validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testTrueSvcConfigNullTransportHeader() {
		boolean success = false;
		
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadTrue");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = 
				new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, null);
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			System.out.println(resp.getMessageList().get(0).getMessage());
		}catch (Exception exception) {
			success = false;
			System.out.println(exception.getMessage());
			Assert.assertEquals(errorMsg, exception.getMessage());
		}
		Assert.assertFalse(success);
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

	/*
	 * TRUE set for schemaValidationLevel in ServiceConfig
	 *	FOO Transport header
	 * 	a. Verify validation performed
	 *  b. Verify is schema is loaded
	 */
	@Test 
	public void testTrueSvcConfigInvalidTransportHeader() {
		boolean success = false;
		
		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configValidatePayloadTrue");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = 
				new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "FOO");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
		}catch (Exception exception) {
			success = false;
			Assert.assertEquals(errorMsg, exception.getMessage());
		}
		Assert.assertFalse(success);
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

	
	/*
	 *  <data-binding name="NV">
     *  <options>
     *   <option name=”validatePayload”>FOO</option>
     *  </options>
 	 *	</data-binding>
	 */
	@Test
	public void testInvalidValidatePayloadValue() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configInvalidValidatePayload");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = 
				new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "True");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			System.out.println(exception.getMessage());
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
	
	/*
	 *  <data-binding name="NV">
     *  <options>
     *   <option name=”SchemaValidationLevel”>FOO</option>
     *  </options>
 	 *	</data-binding>
	 */
	/*@Test
	public void testInvalidSchemaValidationListenerClass() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configSchemaValidationListenerClassNull");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "None");
			GetMessagesForTheDayResponse resp = 
				consumer.getMessagesForTheDay(param0);
			System.out.println(resp.getAck().toString());
			if(("SUCCESS").equalsIgnoreCase(resp.getAck().toString()))
				success = true;
		}catch (Exception exception) {
			success = false;
			System.out.println(exception.getMessage());
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

*/	/*
	 *  <data-binding name="NV">
     *  <options>
     *   <option name=”validatePayLoad”></option>
     *  </options>
 	 *	</data-binding>
	 */
	@Test
	public void testEmptyValidatePayLoad() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configEmptyValidatePayload");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
			//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = 
				new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "True");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			System.out.println(exception.getMessage());
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
	public void testRESTCallWithHeaderTrue() {
		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV2");
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
//		queryParams.put("clientId","schemaValidationTest");
		queryParams.put("siteId", "0");
//		queryParams.put("messageType", "null");
//		queryParams.put("language","en-US");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","true");
		response = http.getResponse(
				"http://localhost:8080/ws/spf", queryParams);
//		System.out.println(response);
		Assert.assertTrue(response.contains("Invalid content was found starting with element 'siteId'"));
		
		
	}
	
	@Test
	public void testRESTCallWithHeaderFalse() {
		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV2");
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
		queryParams.put("language","en-US");
		queryParams.put("siteId", "0");
		queryParams.put("clientId","schemaValidationTest");
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","false");
		response = http.getResponse(
				"http://localhost:8080/ws/spf", queryParams);
		System.out.println(response);
//		Assert.assertTrue(response.contains("<ack>Success</ack>"));
	}

	@Test
	public void testRESTCallWithNoHeader() {
		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV2");
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
		queryParams.put("clientId","schemaValidationTest");
//		queryParams.put("language","en-US");
		queryParams.put("siteId", "0");
		
		queryParams.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL","foo");
		response = http.getResponse(
				"http://localhost:8080/ws/spf", queryParams);
		System.out.println(response);
		Assert.assertTrue(response.contains("<ack>Success</ack>"));
	}


	@Test
	public void testRESTCallWithInvalidHeader() {
		queryParams.put("X-EBAY-SOA-SERVICE-NAME","AdvertisingUniqueIDServiceV2");
		queryParams.put("X-EBAY-SOA-OPERATION-NAME","testSchemaValidationWithUPA");
//		queryParams.put("language","%22enUS%22");
		queryParams.put("siteId", "%220%22");
		queryParams.put("clientId","%22schemaValidationTest%22");
		response = http.getResponse(
				"http://localhost:8080/ws/spf", queryParams);
		System.out.println(response);
		Assert.assertTrue(response.contains("<ack>Success</ack>"));
		
	}

	@Ignore
	public void testRawCallMismatchArgument1() throws ServiceException {
		ClientConfigManager.getInstance().setConfigTestCase("config"); 
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		Service svc;
		ByteBufferWrapper inParam = new ByteBufferWrapper();
		ByteBufferWrapper outParam = new ByteBufferWrapper();
		Map<String, String> headers = new HashMap<String, String>();
		String rawMessage = "<?xml version='1.0' encoding='UTF-8'?><testSchemaValidationWithUPA xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\"><siteId>0</siteId><language>us-ENG</language></testSchemaValidationWithUPA>";
		byte[] param = null;
		svc = ServiceFactory.create ("AdvertisingUniqueIDServiceV2","AdvertisingUniqueIDServiceV2", true);
		ServiceInvokerOptions options = svc.getInvokerOptions();
		headers.put("X-EBAY-SOA-OPERATION-NAME", "testSchemaValidationWithUPA");
		headers.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL", "false");
		param = rawMessage.getBytes();
		
		options.setRequestBinding("XML");
		options.setResponseBinding("XML");
		inParam.setByteBuffer(ByteBuffer.wrap(param));
		svc.invoke(headers, inParam, outParam);
		String actualResponse = new String(outParam.getByteBuffer().array());
		System.out.println(actualResponse);
	}

	@Ignore
	public void testRawCallMismatchArgument2() throws ServiceException {
		Service svc;
		ByteBufferWrapper inParam = new ByteBufferWrapper();
		ByteBufferWrapper outParam = new ByteBufferWrapper();
		Map<String, String> headers = new HashMap<String, String>();
		String rawMessage = "<?xml version='1.0' encoding='UTF-8'?><testSchemaValidationWithoutUPA xmlns=\"http://www.ebay.com/marketplace/advertising/v1/services\"><in>true</in></testSchemaValidationWithoutUPA>";
		byte[] param = null;
		svc = ServiceFactory.create ("AdvertisingUniqueIDServiceV2","AdvertisingUniqueIDServiceV2", true);
		ServiceInvokerOptions options = svc.getInvokerOptions();
		headers.put("X-EBAY-SOA-OPERATION-NAME", "testSchemaValidationWithoutUPA");
		headers.put("X-EBAY-SOA-REQ-PAYLOAD-VALIDATION-LEVEL", "true");
		param = rawMessage.getBytes();
		
		options.setRequestBinding("XML");
		options.setResponseBinding("XML");
		inParam.setByteBuffer(ByteBuffer.wrap(param));
		svc.invoke(headers, inParam, outParam);
		String actualResponse = new String(outParam.getByteBuffer().array());
		System.out.println(actualResponse);
	}
	/*
	 *  <data-binding name="NV">
     *  <options>
     *   <option name=”SchemaValidationLevel”></option>
     *  </options>
 	 *	</data-binding>
	 */

	@Test
	public void testNullSchemaValidationListenerClass() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configSchemaValidationListenerClassNull");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "true");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			System.out.println(resp.getAck().toString());
			if(("SUCCESS").equalsIgnoreCase(resp.getAck().toString()))
				success = true;
		}catch (Exception exception) {
			success = false;
			Assert.assertEquals(errorMsg, exception.getMessage());
			exception.printStackTrace();
		}
		Assert.assertFalse(success);
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

	/*
	 *  <data-binding name="NV">
     *  <options>
     *   <option name=”SchemaValidationLevel”>
     *   	com.ebay.marketplace.services.advertisinguniqueidservicev2.TestJAXBValidationEventHandler
     *   </option>
     *  </options>
 	 *	</data-binding>
	 */

	@Test
	public void testCustomSchemaValidationListenerClass() {
		boolean success = false;

		try {
			ServiceConfigManager.getInstance().setConfigTestCase("configCustomJAXBValidator");
			GetMessagesForTheDayRequest param0 = new GetMessagesForTheDayRequest();
			//			clientId is mandatory. But this test will not throw an error
//			param0.setClientId("schemavalidation");
			param0.setLanguage("us-ENG");
			param0.setMessageType(null);
			param0.setSiteId("0");
			SharedAdvertisingUniqueIDServiceV2Consumer consumer = new SharedAdvertisingUniqueIDServiceV2Consumer("AdvertisingUniqueIDServiceV2Consumer","local");
			consumer.getService().getInvokerOptions().setRequestBinding("XML");
			consumer.getService().getInvokerOptions().setResponseBinding("XML");
			consumer.getService().getRequestContext().setTransportHeader(
					SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL, "true");
			GetMessagesForTheDayResponse resp = 
				consumer.testSchemaValidationWithUPA(param0);
			if(resp.getMessageList().get(0).getMessage().contains("schemaValidation"))
				success = true;
		}catch (Exception exception) {
			success = false;
			Assert.assertEquals(errorMsg, exception.getMessage());
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
}
