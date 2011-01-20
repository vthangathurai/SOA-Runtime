/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import java.net.URI;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;


/**
 */
public class ApplicationRetryTest extends BaseCallTest {
	public ApplicationRetryTest() throws Exception {
		super("configremote");
	}

	protected Test1Driver createDriver() throws Exception {
		URI serverUri = URI.create("http://coolhost:" + 9090 + "/ws/spf");
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
			m_clientName, BaseCallTest.CONFIG_ROOT, serverUri.toURL());

		setupDriver(driver);
		return driver;
	}

	public void setupDriver(Test1Driver driver) {
		super.setupDriver(driver);

		driver.setNoPayloadData(true);
		driver.setExpectedError(ErrorDataCollection.svc_transport_comm_failure.getErrorId(), 
				ServiceInvocationException.class, 
				ServiceInvocationRuntimeException.class, 
				"Transport communication failure for target address http://coolhost:9090/ws/spf");
		try {
			driver.doCall();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}
