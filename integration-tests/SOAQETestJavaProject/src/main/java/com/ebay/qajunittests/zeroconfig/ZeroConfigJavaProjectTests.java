package com.ebay.qajunittests.zeroconfig;

import junit.framework.Assert;

import org.junit.Test;

import com.ebay.marketplace.advertising.v1.services.EchoMessageRequest;
import com.ebay.marketplace.services.advertisinguniqueidservicev1.advertisinguniqueidservicev1.gen.SharedAdvertisingUniqueIDServiceV1Consumer;
import com.ebay.soaframework.common.exceptions.ServiceException;

public class ZeroConfigJavaProjectTests {

	/*
	 * Verify the config from SOAConfig.jar is picked
	 * production, sandbox, staging, feature
	 */
	@Test
	public void testWithFlagTrueInProductionEnvt() throws ServiceException {
		try {
			SharedAdvertisingUniqueIDServiceV1Consumer client = 
				new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
						"production",
						SharedAdvertisingUniqueIDServiceV1Consumer.class, 
						true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			Assert.assertTrue(ex.getMessage().
					contains("Transport HTTP error for target address: http://svcs.ebay.com" +
					"/services/advertise/UniqueIDService/v1"));
			ex.printStackTrace();
		}
	}

	/*
	 * Verify the config from SOAConfig.jar is picked
	 * production, sandbox, staging, feature
	 */
	@Test
	public void testWithFlagTrueInSandboxEnvt() {
		try {
			SharedAdvertisingUniqueIDServiceV1Consumer client = 
				new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
						"sandbox",
						SharedAdvertisingUniqueIDServiceV1Consumer.class, 
						true);
			
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			Assert.assertTrue(ex.getMessage().
					contains("Inbound data error during deserialization: "));
			ex.printStackTrace();
		}
	}

	@Test
	public void testWithFlagTrueInStagingEnvt() {
		try {
			SharedAdvertisingUniqueIDServiceV1Consumer client = 
				new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
						"staging",
						SharedAdvertisingUniqueIDServiceV1Consumer.class, 
						true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			Assert.assertTrue(ex.getMessage().
					contains("Transport HTTP error for target address:" + 
							" http://svcs.qa.ebay.com/services/advertise/UniqueIDService/v1"));
		}
	}

	@Test
	public void testWithFlagTrueInFeatureEnvt() throws ServiceException {

		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
					"feature",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					true);
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(),
		" Echo Message = Foo");

	}

	/*
	 * verify valid error message is displayed envt is missing
	 */
	@Test
	public void testWithFlagTrueInvalidEnvt() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
					"foo",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertFalse(true);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage()
					.contains("Unable to load file: META-INF/soa/client/config/foo/DefaultClientConfig.xml"));
			e.printStackTrace();
		}

	}

	/*
	 * Verify valid error message CC is missing
	 */
	@Test
	public void testWithFlagFalseProductionEnvt() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
					"feature",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					false);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertFalse(true);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().contains("Unable to load file: " + 
					"META-INF/soa/client/config/AdvertisingUniqueIDServiceV1Consumer/" + 
					"production/AdvertisingUniqueIDServiceV1/ClientConfig.xml"));
		}

	}
	@Test
	public void testWithFlagFalseInvalidEnvt() throws ServiceException {
		try {
			SharedAdvertisingUniqueIDServiceV1Consumer client = 
				new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", 
						"dev",
						SharedAdvertisingUniqueIDServiceV1Consumer.class, 
						false);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());

			ex.printStackTrace();
		}
	}
	
}
