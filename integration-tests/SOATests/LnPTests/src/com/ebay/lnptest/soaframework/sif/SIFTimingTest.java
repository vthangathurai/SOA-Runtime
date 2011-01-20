/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package com.ebay.lnptest.soaframework.sif;

import junit.framework.TestCase;

import com.ebay.test.soaframework.sample.types1.MyMessage;
import com.ebay.test.soaframework.util.TestUtils;

/**
 * @author wdeng
 *
 */
public class SIFTimingTest extends TestCase {
	
	public void testLocalBindingSkipSerializationTiming() throws Exception {
		System.out.println("Start testLocalBindingSkipSerializationTiming");
		runTimingTestWithMultipleSize("LnPLocalSkip");
		System.out.println("End testLocalBindingSkipSerializationTiming");
	}
	

	public void testRemoteTiming() throws Exception {
		System.out.println("Start testRemoteTiming");
		runTimingTestWithMultipleSize("LnPRemoteTest", "http://localhost:8080/ws/spf/", 1000);
		System.out.println("End testRemoteTiming");
	}
	
	public void testLocalBindingTiming() throws Exception {
		System.out.println("Start testLocalBindingTiming");
		runTimingTestWithMultipleSize("LnPLocalTest");
		System.out.println("End testLocalBindingTiming");
	}
	
	
	private void runTimingTestWithMultipleSize(String clientName) throws Exception {
		runTimingTestWithMultipleSize(clientName, null, 1000);
	}
	
	private void runTimingTestWithMultipleSize(String clientName, String serviceLocation, int times) throws Exception {
		// Warn up time
		runTimingTestWithFixMessageSize(clientName, 20);
		runTimingTestWithFixMessageSize(clientName, 20);
		runTimingTestWithFixMessageSize(clientName, 20);
		
		runTimingTestWithFixMessageSize(clientName, "1K size", serviceLocation, 3, times, true);
		runTimingTestWithFixMessageSize(clientName, "4K size", serviceLocation, 20, times, true);
		runTimingTestWithFixMessageSize(clientName, "6K size", serviceLocation, 30, times, true);
		runTimingTestWithFixMessageSize(clientName, "8K size", serviceLocation, 40, times, true);
		runTimingTestWithFixMessageSize(clientName, "10K size", serviceLocation, 50, times, true);
		runTimingTestWithFixMessageSize(clientName, "20K size", serviceLocation, 100, times, true);
		runTimingTestWithFixMessageSize(clientName, "30K size", serviceLocation, 150, times, true);
	}
	
	private void runTimingTestWithFixMessageSize(String clientName, int n) throws Exception {
		runTimingTestWithFixMessageSize(clientName, "", n, false);
	}

	
	private void runTimingTestWithFixMessageSize(String clientName, String title, int n, boolean verbose) throws Exception {
		runTimingTestWithFixMessageSize(clientName, title, null, n, 1000, verbose);
	}

	private void runTimingTestWithFixMessageSize(String clientName, String title, String serviceLocation, int n, int TimesToRun, boolean verbose) throws Exception {
		TestClient client = new TestClient(clientName, serviceLocation);
		MyMessage msg = TestUtils.createTestMessage(n);
		
		runGc();
		long startTime = System.nanoTime();
		MyMessage returnMsg = msg;
		for (int i=0; i<TimesToRun; i++) {
			returnMsg = client.myTestOperation(msg);
		}
		double aveTime = (System.nanoTime() - startTime)/TimesToRun/1000000.0;
		
		if (verbose)
		System.out.println("Average time for " + TimesToRun + " runs of " + title + ": " + 
				aveTime + "ms" +
				" Number of addresses is " + returnMsg.getRecipients().size());
		
	}


	private void runGc() throws Exception{
		System.gc();
		System.gc();
		Thread.sleep(3000);
		System.gc();
	}
}
