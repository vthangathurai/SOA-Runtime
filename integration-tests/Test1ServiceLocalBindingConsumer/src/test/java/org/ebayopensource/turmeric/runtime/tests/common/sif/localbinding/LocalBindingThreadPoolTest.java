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

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool.ThreadPoolStats;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.junit.Ignore;
import org.junit.Test;


public class LocalBindingThreadPoolTest extends BaseLocalBindingTestCase {

	private Thread m_thread = null;
	private class MockTask implements Callable<MessageContext>{
		public MessageContext call(){
			m_thread = Thread.currentThread();
			return null;
		}
	}

	public LocalBindingThreadPoolTest() throws Exception {
		super();
	}

	@Test
	public void detachedLocalServerCall() throws Exception {
		//Get initial pool size
		ThreadPoolStats tpStats = LocalBindingThreadPool.getInstance().getStatistics();
		int tpSize = tpStats.getPoolSize();
		
		//Setup driver and execute call
		Test1Driver driver = createDriver();
		driver.setDetachedLocalBinding(true);
		driver.setRequestTimeout(1000);
		driver.doCall();
		
		//assert pool size increase by 1
		assertEquals(LocalBindingThreadPool.getInstance().getStatistics().getPoolSize() - tpSize, 1);
	}
	
	@Test
	public void inlineLocalServerCall() throws Exception {
		//Get initial pool size
		ThreadPoolStats tpStats = LocalBindingThreadPool.getInstance().getStatistics();
		int tpSize = tpStats.getPoolSize();
		
		//Setup driver and execute call
		Test1Driver driver = createDriver();
		driver.setDetachedLocalBinding(false);
		driver.setRequestTimeout(1000);
		driver.doCall();
		
		//assert pool size didn't increase
		assertEquals(LocalBindingThreadPool.getInstance().getStatistics().getPoolSize() - tpSize, 0);
	}

	@Test
	public void localBindingThreadPoolReuse() throws Exception {
		// Retrieve reference to an allocated thread
		LocalBindingThreadPool tpool = LocalBindingThreadPool.getInstance();
		int startIdle = tpool.getStatistics().getIdleThreadCount();
		Future<MessageContext> future = tpool.execute(new MockTask());
		future.get();
		Thread oldthread = m_thread;
		
		// Setup driver and execute call in allocated thread
		
		// how do we know for sure that the thread we want is the thread being used in the testdriver invocation?
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,"detached", CONFIG_ROOT, LOCAL_TRANSPORT, "XML", "XML");
		driver.setDetachedLocalBinding(true);
		driver.skipAyncTest(true);
		driver.setRequestTimeout(1000);
		driver.doCall();
		
		//Reuse allocated thread
		future = tpool.execute(new MockTask());
		future.get();
		
		ThreadPoolStats stats = LocalBindingThreadPool.getInstance().getStatistics();
		assertEquals(startIdle, stats.getIdleThreadCount());
		assertTrue("m_thread should have been alive", m_thread.isAlive());
		assertTrue("oldThread and m_thread should have been the same", oldthread == m_thread);
	}

	@Test
	public void localBindingThreadPoolGCEligibility() throws Exception {
		Test1Driver driver = createDriver();
		driver.doCall();
		driver = null;
		
		// Creating WeakReference reference
		ReferenceQueue<LocalBindingThreadPool> refQueue = new ReferenceQueue<LocalBindingThreadPool>();
		PhantomReference<LocalBindingThreadPool> tpRef = new PhantomReference<LocalBindingThreadPool>(LocalBindingThreadPool.getInstance(), refQueue);

		// Clearing all references to thread pool inside singleton
		Field fields[] = LocalBindingThreadPool.class.getDeclaredFields();
		for(Field field :  fields){
			if(field.getType().equals(LocalBindingThreadPool.class)){
				field.setAccessible(true);
				field.set(LocalBindingThreadPool.getInstance(), null);
			}
		}

		// Runnig Garbage Collection
		for(int i = 0; i<100; i++){
			System.gc();	
		}

		assertTrue(tpRef.isEnqueued());
	}

	
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"detached", CONFIG_ROOT, LOCAL_TRANSPORT);
		return driver;
	}

}
