/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1;


import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.GetRequestIDResponse;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.junit.Test;

/**
 * @author rarekatla
 *
 */
public class LoggingRequestIDTests {
	private AdvertisingUniqueIDServiceV1SharedConsumer consumerV1;
	@Test
	public void testNestedServiceRequestID () {
		try {
			 consumerV1 = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer","dev");
			 GetRequestIDResponse res = consumerV1.getReqID("LOCAL");
			 String requestId = consumerV1.getService().getResponseContext().getTransportHeader("X-TURMERIC-REQUEST-ID");
			 System.out.println(requestId);
			 Assert.assertTrue(res.getRequestID().contains(res.getGuid()));
			 Assert.assertTrue(requestId.contains("!AdvertisingUniqueIDServiceV1!"));
			 
		} catch (ServiceException e) {
			Assert.assertFalse("Error not Expected"+e.getMessage(), true);
		} 
	}

	@Test
	public void testOverwriteGUID() {
		try {
			 consumerV1 = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer","feature");
			 GetRequestIDResponse res = consumerV1.getReqID("LOCAL");
			 System.out.println(res);
			 Assert.assertEquals("1234abcd",res.getGuid());
			 Assert.assertTrue(res.getRequestID().contains(res.getGuid()));
		} catch (ServiceException e) {
			Assert.assertFalse("Error not Expected"+e.getMessage(), true);
		} 
	}
	
}
