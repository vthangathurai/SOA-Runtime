/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.localbinding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool.ThreadPoolStats;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Ignore;
import org.junit.Test;


public class InlineLocalBindingTest extends BaseLocalBindingTestCase {
	
	public InlineLocalBindingTest() throws Exception {
		super();
	}
	
	/*
	 * This test is trying to test that the INITIAL state has ZERO threads.
	 * This means that this tets hsould be run very differnely. Either @ start of jvm, or force a new jvm/instance of
	 * this pool and check, or put the check in the code itself that fails if the init did not happen correctly
	public void testNormalCalls() throws Exception {	
		ThreadPoolStats stats = LocalBindingThreadPool.getInstance().getStatistics();
		assertEquals("pool size should be 0.", 0, stats.getPoolSize());
	}*/
	
	@Test
	public void callsWithOverrides() throws Exception {
		LocalBindingThreadPool.getInstance().getStatistics();
		Test1Driver driver = createDriver();
		driver.setDetachedLocalBinding(true);
		driver.setRequestTimeout(10000);
		driver.doCall();
		ThreadPoolStats stats = LocalBindingThreadPool.getInstance().getStatistics();
		assertTrue("pool size should be > 0", stats.getPoolSize() > 0);
	}
	
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"inline", CONFIG_ROOT, LOCAL_TRANSPORT);
		driver.setVerifier(new Verifier());
		return driver;
	}
	
	protected class Verifier implements Test1Driver.SuccessVerifier {
		public void checkSuccess(Service service, String opName, MyMessage request,
			MyMessage response, byte[] payloadData) throws Exception
		{
			/**
			 * Uncomment the following to see the stats
			 * System.out.println("After running the InlineLocalBindingTest...");
			 * showThreadPoolStats();
			 */
		}
		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch, Response futureResponse, MyMessage request,
				MyMessage response, byte[] payloadData, TestMode mode) throws Exception
		{
			/**
			 * Uncomment the following to see the stats
			 * System.out.println("After running the InlineLocalBindingTest...");
			 * showThreadPoolStats();
			 */
		}
	}
}
