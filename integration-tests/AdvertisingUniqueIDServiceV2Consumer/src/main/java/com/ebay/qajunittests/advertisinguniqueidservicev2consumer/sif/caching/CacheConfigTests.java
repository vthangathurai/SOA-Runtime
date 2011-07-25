/**
 * 
 */
package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer.SOATestAPICachingClient;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsResponse;

/**
 * @author rarekatla
 * 
 */
public class CacheConfigTests {

	@Test
	public void checkNoValueForCacheProvider() {
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
		req.setId2(10);
		req.setSsn2(20333);
		try {
			SOATestAPICachingClient client = new SOATestAPICachingClient(
					"AdvertisingUniqueIDServiceV2Consumer", "cacheconfig");

			GetEmployeeDetailsResponse res = client.getEmpDetailsLocalMode(req);
			Assert.assertNotNull(res);

		} catch (Throwable se) {
			String error = se.getLocalizedMessage();
			Assert.assertTrue(error, false);
		}

	}

}
