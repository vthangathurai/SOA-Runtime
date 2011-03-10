package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

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
}
