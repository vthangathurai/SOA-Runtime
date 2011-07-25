/**
 * 
 */
package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.qacommonutils.types1.HttpTestClient;

/**
 * @author rarekatla
 *
 */
public class GetCahcePolicyRESTTests {

	public static HttpTestClient http = HttpTestClient.getInstance();
	public Map<String, String> queryParams = new HashMap<String, String>();
	
	@Test 
	public void checkCachePolicyWhenCongfiured() {
			try {
				String expectedResponse = "operationCachePolicy name=\"getPaymentDetails";
				queryParams.clear();
				String testURL="http://localhost:8080/ws/spf?X-EBAY-SOA-SERVICE-NAME=SOATestAP1Caching&X-EBAY-SOA-OPERATION-NAME=getCachePolicy";
				String cachePolicy =  http.getResponse(testURL , queryParams);
				System.out.println(cachePolicy);
				Assert.assertTrue("",cachePolicy.contains(expectedResponse));
			} catch (Exception e) {
				System.err.println(e.getMessage());
				Assert.assertTrue("Error - No Exception should be thrown ", false);
			}  
		}
	
	@Test 
	public void checkCachePolicyWhenNotCongfiured() {
			try {
				queryParams.clear();
				String expectedResponse = "No CachePolicy defined for service: AdvertisingUniqueIDServiceV1";
				String testURL="http://localhost:8080/services/advertise/UniqueIDService/v1?X-EBAY-SOA-OPERATION-NAME=getCachePolicy";
				Assert.assertTrue("",http.getResponse(testURL , queryParams).contains(expectedResponse));
			} catch (Exception e) {
				System.err.println(e.getMessage());
				Assert.assertTrue("Error - No Exception should be thrown ", false);
			}  
		}
	@Test 
	public void checkCachePolicyWhenNotCongfiuredInvalid() {
			/*try {
				queryParams.clear();
				String expectedResponse = "<ns2:version>1.0.0</ns2:version></getVersionResponse>";
				String testURL="http://localhost:8080/services/advertise/UniqueIDService/v1/ns2?X-EBAY-SOA-OPERATION-NAME=getVersion";
				Assert.assertTrue("",http.getResponse(testURL , queryParams).contains(expectedResponse));
			} catch (Exception e) {
				System.err.println(e.getMessage());
				Assert.assertTrue("Error - No Exception should be thrown ", false);
			}  */
		}
			
}
