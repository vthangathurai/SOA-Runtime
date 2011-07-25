package com.ebay.consumer.zeroconfig;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.junit.Ignore;
import org.junit.Test;

public class ZeroConfigConsumerTests {


	/*
	 * If CC.xml exists in the project, use that
	 */
	@Test 
	public void testConsumerWithFlagTrueProductionUniqueIDServiceV1() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer client;

		client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
				"production",
				SharedAdvertisingUniqueIDServiceV1Consumer.class, 
				true);
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(),
		" Echo Message = Foo");
	}

	/*
	 * If CC.xml exists in the project, use that
	 */
	@Test 
	public void testConsumerWithFlagFalseProductionUniqueIDServiceV1() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer client;

		client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
				"production",
				SharedAdvertisingUniqueIDServiceV1Consumer.class, 
				false);
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(),
		" Echo Message = Foo");
	}

	@Test 
	public void testConsumerWithFlagTrueSandboxUniqueIDServiceV1() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"sandbox",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertEquals(client.echoMessage(param0).getOut(),
			" Echo Message = Foo");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			Assert.assertTrue(ex.getMessage().
					contains("Inbound data error during deserialization: "));
			ex.printStackTrace();
		}
	}

	@Test 
	public void testConsumerWithFlagTrueStagingUniqueIDServiceV1() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"staging",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertTrue(false);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			Assert.assertTrue(ex.getMessage().
					contains("Transport HTTP error for target address:" + 
					" http://svcs.qa.ebay.com/services/advertise/UniqueIDService/v1"));
		}
	}
	/*
	 * If CC.xml not present in the project
	 * Pick from SOAConfig.jar
	 */
	
	@Test 
	public void testConsumerWithFlagTrueFeatureUniqueIDServiceV1() throws ServiceException {
		SharedAdvertisingUniqueIDServiceV1Consumer client;

		client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
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
	 * If CC.xml exists in the project, use that
	 */
	@Test 
	public void testConsumerWithFlagTrueImplFactoryService1() throws ServiceException {
		SharedSOAQETestImplFactoryServiceV1Consumer consumer = 
			new SharedSOAQETestImplFactoryServiceV1Consumer
			("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"production",
					SharedSOAQETestImplFactoryServiceV1Consumer.class,
					true);
		consumer.getService().getRequestContext().setTransportHeader("Impl-Class", "1");
		String out = consumer.testImplFactory(null).getOutput();
		Assert.assertEquals("1.Impl1", out);
	}

	@Test 
	public void testConsumerWithFlagFalse1ImplFactoryService2() throws ServiceException {
		SharedSOAQETestImplFactoryServiceV1Consumer consumer1 = 
			new SharedSOAQETestImplFactoryServiceV1Consumer
			("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"feature",
					SharedSOAQETestImplFactoryServiceV1Consumer.class,
					true);
		consumer1.getService().getRequestContext().setTransportHeader("Impl-Class", "1");
		String out = consumer1.testImplFactory(null).getOutput();
		Assert.assertEquals("1.Impl1", out);
	}

	/*
	 * Verify Invalid CC error message
	 */
	@Test 
	public void testConsumerWithFlagTrueInvalidCC() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"dev",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertEquals(client.echoMessage(param0).getOut(),
			" Echo Message = Foo");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block\
			System.out.println(e.getMessage());
			e.printStackTrace();
			Assert.assertTrue(e.getMessage()
					.contains("Error validating configuration file META-INF/soa/client/config/" + 
					"AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer/dev/" + 
					"AdvertisingUniqueIDServiceV1/ClientConfig.xml: Cannot find group: MarketplaceClientGroup1"));

		}
	}

	/*
	 * Verify valid missing CC error message
	 */
	@Test 
	public void testConsumerWithFlagTrueMissingCC1() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"qe",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					true);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertEquals(client.echoMessage(param0).getOut(),
			" Echo Message = Foo");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().
					contains("Unable to load file: META-INF/soa/client/config/qe/DefaultClientConfig.xml"));
		}
	}
	/*
	 * Verify valid missing CC error message
	 */
	@Test 
	public void testConsumerWithFlagFalseMissingCC2() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"qe",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					false);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertEquals(client.echoMessage(param0).getOut(),
			" Echo Message = Foo");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().
					contains("Unable to load file: META-INF/soa/client/config" + 
					"/AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer/qe/AdvertisingUniqueIDServiceV1/ClientConfig.xml"));
		}
	}

	/*
	 * Verify invalid CC error message
	 */
	@Test 
	public void testConsumerWithFlagFalseInvalidCC() {
		SharedAdvertisingUniqueIDServiceV1Consumer client;
		try {
			client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
					"dev",
					SharedAdvertisingUniqueIDServiceV1Consumer.class, 
					false);
			EchoMessageRequest param0 = new EchoMessageRequest();
			param0.setIn("Foo");
			System.out.println(client.echoMessage(param0).getOut());
			Assert.assertEquals(client.echoMessage(param0).getOut(),
			" Echo Message = Foo");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.assertTrue(e.getMessage().
					contains("Error validating configuration file META-INF/soa/client/config" + 
							"/AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer/" + 
					"dev/AdvertisingUniqueIDServiceV1/ClientConfig.xml: Cannot find group: MarketplaceClientGroup1"));
		}
	}

	/*
	 * Invoke Service Factory method
	 */
	@Ignore
	public void testServiceFactoryMethod() throws ServiceException, MalformedURLException {
		Service svc = ServiceFactory.create("AdvertisingUniqueIDServiceV1ZeroConfigTestConsumer", 
				"production", "AdvertisingUniqueIDServiceV1", 
				new URL("http://localhost:8080/ws/spf"), false, true);

	}




}
