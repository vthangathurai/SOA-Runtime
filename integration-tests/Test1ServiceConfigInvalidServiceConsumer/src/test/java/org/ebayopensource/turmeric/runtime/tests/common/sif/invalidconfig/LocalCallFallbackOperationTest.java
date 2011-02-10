/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.invalidconfig;



import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;


/**
 * @author ichernyshev
 */
public class LocalCallFallbackOperationTest extends BaseCallTest {
	public LocalCallFallbackOperationTest() throws Exception {
	}
	
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1DriverExt("configInvalid", "configInvalid", null, null, "JSON", "JSON");
		//Test1Driver driver = new Test1DriverExt("configInvalid", "configInvalid", null, serverUri.toURL());
		setupDriver(driver);	
		return driver;
	}
	
	protected void setupDriver(Test1Driver driver) {
		driver.setExpectedError(
				ErrorDataCollection.svc_rt_no_service_desc_for_admin_name.getErrorId(),
				ServiceInvocationException.class, 
				ServiceInvocationRuntimeException.class, 
				"Failure in locating the expected service descriptor for admin name: configInvalid");
	}
}
