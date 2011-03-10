package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertisinguniqueidservicev1.gen.SharedAdvertisingUniqueIDServiceV1Consumer;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.junit.Test;


public class JAXWSComplianceTests {

	
	@Test
	public void testJAXWSComplianceWithException() {
		System.out.println(" ** testJAXWSComplianceWithException ** ");
		/*
		try {
//		ServiceConfigManager.getInstance().setConfigTestCase("JAXWSTests");
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "local");
		TestJAXWSCompliance1 param0 = new TestJAXWSCompliance1();
		param0.setIn("testJAXWSComplianceWithException");
		System.out.println(client.testJAXWSCompliance1(param0).getOut());
		} catch (Exception se) {
//			System.out.println(se.getMessage());
			Assert.assertEquals("Exception from Server", se.getMessage());
		}
		*/
		System.out.println(" ** testJAXWSComplianceWithException ** ");
	}
	
	@Test
	public void testJAXWSComplianceWithNoException() {
		System.out.println(" ** testJAXWSComplianceWithNoException ** ");
		/*
		try {
//		ServiceConfigManager.getInstance().setConfigTestCase("JAXWSTests");
		SharedAdvertisingUniqueIDServiceV1Consumer client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "local");
		TestJAXWSCompliance2 param0 = new TestJAXWSCompliance2();
		param0.setIn("testJAXWSComplianceWithNoException");
		System.out.println(client.testJAXWSCompliance2(param0).getOut());
		Assert.assertEquals("Success from Server - testJAXWSComplianceWithNoException", client.testJAXWSCompliance2(param0).getOut());
		} catch (ServiceException se) {
			Assert.assertTrue("Should not show any exception", false);
		}*/
		System.out.println(" ** testJAXWSComplianceWithNoException ** ");
	}
	

}
