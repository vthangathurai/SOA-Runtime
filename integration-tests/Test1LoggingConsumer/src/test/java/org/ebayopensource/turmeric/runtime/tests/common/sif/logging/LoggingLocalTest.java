/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.logging;

import java.io.IOException;

import org.ebayopensource.turmeric.junit.asserts.ClassLoaderAssert;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.junit.Test;


public class LoggingLocalTest extends BaseCallTest {
	public LoggingLocalTest() throws Exception {
		super("configlogging");
	}

	/**
	 * There have been some issues WRT access to the ClientConfig.xml, so this is just here to verify the basics.
	 */
	@Test
	public void testAccessToConfig() throws IOException {
		ClassLoaderAssert.assertResourcePresent("Standard ClientConfig", "META-INF/soa/client/config/configlogging/ClientConfig.xml");
	}

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver("test1", "configlogging", null, null);
		setupDriver(driver);
		return driver;
	}
}
