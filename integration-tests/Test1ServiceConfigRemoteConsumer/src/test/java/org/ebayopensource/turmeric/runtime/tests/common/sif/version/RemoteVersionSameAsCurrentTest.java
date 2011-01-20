/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.version;

import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.version.BaseVersionTest;


public class RemoteVersionSameAsCurrentTest extends BaseVersionTest {

	public RemoteVersionSameAsCurrentTest() throws Exception {
		super("configremote");
	}

	protected void setupDriver(Test1Driver driver) {
		driver.setServiceVersion("1.1.0");
		driver.setVerifier(new Verifier("1.0.0"));
	}
}
