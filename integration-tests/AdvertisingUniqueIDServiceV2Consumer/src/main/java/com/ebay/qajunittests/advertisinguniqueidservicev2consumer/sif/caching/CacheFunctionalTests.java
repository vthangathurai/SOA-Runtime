/**
 * 
 */
package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer.SOATestAPICachingClient;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsResponse;
import com.ebay.marketplace.v1.services.GetPaymentDetailsRequest;
import com.ebay.marketplace.v1.services.GetPaymentDetailsResponse;
import com.ebay.soaframework.common.exceptions.ServiceException;



/**
 * @author rarekatla
 *
 */
public class CacheFunctionalTests {
	
	@Test 
	public void checkEmpDetails_Cache() throws ServiceException {
		SOATestAPICachingClient client = new SOATestAPICachingClient("AdvertisingUniqueIDServiceV2Consumer","dev");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
		req.setId2(10);
		req.setSsn2(20333);
		GetEmployeeDetailsResponse res= client.getEmpDetailsRemoteMode(req);
		// Should get from Cache
		GetEmployeeDetailsResponse res1= client.getEmpDetailsRemoteMode(req);
		Assert.assertSame(res, res1);
		
	}
	
	@Test 
	public void checkEmpDetails_DiffRequest() throws ServiceException {
		SOATestAPICachingClient client = new SOATestAPICachingClient("AdvertisingUniqueIDServiceV2Consumer","dev");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
		req.setId2(20);
		req.setSsn2(20333);
		req.setUserName2("John");
		GetEmployeeDetailsResponse res= client.getEmpDetailsRemoteMode(req);
		// Should get from Cache
		GetEmployeeDetailsResponse res1= client.getEmpDetailsRemoteMode(req);
		Assert.assertSame(res, res1);
				
	}
	
	@Test 
	public void checkPaymentDetails_Cache() throws ServiceException {
		SOATestAPICachingClient client = new SOATestAPICachingClient("AdvertisingUniqueIDServiceV2Consumer","dev");
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		req.setId1(10);
		req.setSsn1(20333);
		GetPaymentDetailsResponse res= client.getPayDetailsRemoteMode(req);
		// Should get from Cache
		GetPaymentDetailsResponse res1= client.getPayDetailsRemoteMode(req);
		Assert.assertSame(res, res1);				
				
	}
	
	// update client bean and check whether cache is deleted.
	
	@Ignore 
	public void checkCacheWhenClientBeanUpdated() throws ServiceException {
		SOATestAPICachingClient client = new SOATestAPICachingClient("AdvertisingUniqueIDServiceV2Consumer","dev");
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		req.setId1(10);
		req.setSsn1(20333);
		GetPaymentDetailsResponse res= client.getPayDetailsRemoteMode(req);
		// Should get from Cache
		GetPaymentDetailsResponse res1= client.getPayDetailsRemoteMode(req);
		
		Assert.assertSame(res, res1);
		
		SOATestAPICachingClient clientJSON = new SOATestAPICachingClient("AdvertisingUniqueIDServiceV2Consumer","dev");
		
		GetPaymentDetailsResponse res2= clientJSON.getPayDetailsRemoteModeJSON(req);

		Assert.assertNotSame("Check with Raghu", res, res2);
	}
	
		
}
