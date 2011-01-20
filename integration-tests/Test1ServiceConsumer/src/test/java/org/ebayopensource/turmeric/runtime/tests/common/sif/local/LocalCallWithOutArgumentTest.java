/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.local;

import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.junit.Rule;


/**
 * @author wdeng
 */
public class LocalCallWithOutArgumentTest extends BaseCallTest {
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig(CONFIG_ROOT);

	public LocalCallWithOutArgumentTest() throws Exception {
		super();
	}

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				"local", CONFIG_ROOT, serverUri.toURL(), null, null,
				Test1Driver.OP_NAME_myNonArgOperation, null);
		driver.setExpectingSameMessage(false);
		return driver;
	}
}
