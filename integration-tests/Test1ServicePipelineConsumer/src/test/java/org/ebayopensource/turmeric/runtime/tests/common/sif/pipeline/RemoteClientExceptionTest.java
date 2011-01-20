/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.pipeline;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseClientExceptionTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;



/**
 * @author ichernyshev
 */
public class RemoteClientExceptionTest extends BaseClientExceptionTest {

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver("test1", "clientpipeline", "config", new URL("http://localhost:8080/ws/spf"));
		return driver;
	}
}
