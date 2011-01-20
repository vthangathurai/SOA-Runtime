/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.CustomHttpHeadersTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
	CustomHttpHeadersTest.class
//	WSDLEnhancementsTest.class,
//	OperationNameMismatchTest.class
	})

public class AllTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		return suite;
	}

}
