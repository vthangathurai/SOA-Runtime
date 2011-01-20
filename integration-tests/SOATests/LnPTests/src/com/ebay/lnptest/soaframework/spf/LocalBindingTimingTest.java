/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package com.ebay.lnptest.soaframework.spf;

import junit.framework.TestCase;

import com.ebay.test.soaframework.sif.Test1Driver;

/**
 * @author ichernyshev
 */
public class LocalBindingTimingTest extends TestCase {

	public static void testLocalCallTiming() throws Exception {
		System.out.println("**** Starting testLocalCallTiming");
		//Warn up
		doLocalCall("XML", "XML", 1, true);
		doLocalCall("NV", "NV", 1, true);
		doLocalCall("JSON", "JSON", 1, true);

		// Run
		doLocalCall("XML", "XML", 100, false);
		doLocalCall("NV", "NV", 100, false);
		doLocalCall("JSON", "JSON", 100, false);
		System.out.println("**** Ending testLocalCallTiming");
	}

	public static void doLocalCall(String reqDataFormat, String respDataFormat,
		int repeat, boolean checkResult) throws Exception
	{
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME, "local", "config", null, reqDataFormat, respDataFormat);
		driver.setRepeatCount(repeat);
		driver.doCall();
	}
}
