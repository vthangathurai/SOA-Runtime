/**
 * 
 */
package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebay.marketplace.services.FindItemPrice;
import com.ebay.marketplace.services.FindItemPriceResponse;
import com.ebay.services.soaasyncservice.intf.gen.SOAAsyncServiceProxy;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.types.ByteBufferWrapper;
import com.ebay.soaframework.sif.impl.internal.config.ClientConfigManager;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;

/**
 * @author rarekatla
 * 
 */
public class CachingOverRawMode {
	private static final String serviceLocation = "http://localhost:8080/ws/spf";

	private Service service = null;
	URL m_serviceLocation;
	public static final String RAW_MESSAGE_XML = "<?xml version='1.0' encoding='UTF-8'?><findItemPrice xmlns=\"http://www.ebay.com/marketplace/services\"><itemName>camera</itemName></findItemPrice>";
	@Test
	public void checkCahceItemPrice() {
		
		ByteBufferWrapper inParam = new ByteBufferWrapper();
		ByteBufferWrapper outParam = new ByteBufferWrapper();
		Map<String, String> headers = new HashMap<String, String>();
		byte[] param = null;
		String svcAdminName = "SOAAsyncService";
		
		
		FindItemPrice req = new FindItemPrice();
		req.setItemName("camera");
		
		try {
			m_serviceLocation = new URL(serviceLocation);
			service = ServiceFactory.create(svcAdminName, "SOAAsyncService",
					m_serviceLocation);
			SOAAsyncServiceProxy proxy = service.getProxy();
			FindItemPriceResponse res = proxy.findItemPrice(req);
			System.out.println(res.getItemPrice());
						
			Service service = ServiceFactory.create("SOAAsyncService", "SOAAsyncService",false);
			
			headers.put("X-EBAY-SOA-OPERATION-NAME", "findItemPrice");
			param = RAW_MESSAGE_XML.getBytes();
					
			inParam.setByteBuffer(ByteBuffer.wrap(param));
				
			service.invoke(headers, inParam, outParam);
			
			String res1 =  new String(outParam.getByteBuffer().array());

			Assert.assertNotSame(res,res1);
			
			service.invoke(headers, inParam, outParam);
			
			String res2 =  new String(outParam.getByteBuffer().array());
			
			Assert.assertNotSame(res1,res2);
					
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ClientConfigManager.getInstance().setConfigTestCase("CacheConfig");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ClientConfigManager.getInstance().setConfigTestCase("config");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

}
