/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.version;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.version.BaseVersionTest;


public class VersionNonnumericTest extends BaseVersionTest {

	public VersionNonnumericTest() throws Exception {
		super("local");
	}

	protected void setupDriver(Test1Driver driver) {
		driver.setServiceVersion("A.1.0");
		driver.setExpectedError(ErrorDataCollection.svc_rt_invalid_version_format.getErrorId(), 
				ServiceInvocationException.class, ServiceInvocationRuntimeException.class, "Invalid version format A.1.0");
	}
}
