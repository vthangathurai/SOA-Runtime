/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.error.BaseExceptionTest;


/**
 * @author ichernyshev
 */
public class RemoteExceptionTest extends BaseExceptionTest {

	protected Test1Driver createDriver() throws Exception {
		
		m_mappingErrorId = ErrorDataCollection.svc_rt_unable_to_map_errors.getErrorId();
		m_mappingErrorString = "Unable to map errors due to: java.lang.RuntimeException";
		
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME, "configremote", null, serverUri.toURL());

		return driver;
	}
}
