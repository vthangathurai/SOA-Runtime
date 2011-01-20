/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.g11n;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.impl.internal.g11n.GlobalRegistryConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;


/**
 * 
 */
public class NoG11nLocalCallTest extends BaseCallTest {
	
    /*
	 * NV is giving getAttributeType - Unsupported operation exception.
	 */
	final String[] FORMATS = { BindingConstants.PAYLOAD_XML,
			BindingConstants.PAYLOAD_FAST_INFOSET,
			BindingConstants.PAYLOAD_JSON };


        public NoG11nLocalCallTest() throws Exception {
		super();
	}

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME, "configNoG11n", null, serverUri.toURL(), FORMATS, FORMATS, "myTestOperation", TestUtils.createTestMessage());
		setupDriver(driver);	
		return driver;
	}
	
	@Override
	protected void setupDriver(Test1Driver driver){
		GlobalRegistryConfigManager.setCommonPath("config-no-g11n");
	}
}
