/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;

/**
 * @author ichernyshev
 */
public class RemoteRestCallTest extends BaseCallTest {
	public RemoteRestCallTest() throws Exception {
		// given that HTTPClientTransport does not add REST_PAYLOAD parameter,
		// we add it here manually, just to test the server end
		super("configremote");
	}

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
			"configremote", BaseCallTest.CONFIG_ROOT, serverUri.resolve("?myTestOperation").toURL(),
			BindingConstants.PAYLOAD_XML, BindingConstants.PAYLOAD_XML,
			Test1Driver.OP_NAME_myTestOperation);

		driver.setUseRest(true);
		driver.setUseDefaultBinding(true);

		setupDriver(driver);
		return driver;
	}
}
