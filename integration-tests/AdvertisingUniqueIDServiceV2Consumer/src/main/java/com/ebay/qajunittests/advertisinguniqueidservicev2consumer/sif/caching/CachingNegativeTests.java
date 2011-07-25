/**
 * 
 */
package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer.SOATestAPICachingClient;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.soaframework.spf.impl.internal.config.ServiceConfigManager;


/**
 * @author rarekatla
 *
 */
public class CachingNegativeTests {
	
	@Test 
	public void checkInvalidKeyInCachePolicy() {
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
		req.setId2(10);
		req.setSsn2(20333);
		try {
			SOATestAPICachingClient client = new SOATestAPICachingClient("AdvertisingUniqueIDServiceV2Consumer","sandbox");
			
			client.getEmpDetailsLocalMode(req);
			Assert.assertTrue(false);
			
		} catch(Throwable se) {
			String error = se.getLocalizedMessage();
			Assert.assertTrue(error.contains("Invalid key userid in cache policy"));
		}
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	  ServiceConfigManager.getInstance().setConfigTestCase("SOATestInvalidCachePolicy");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		   ServiceConfigManager.getInstance().setConfigTestCase("config");
	}
		
}
