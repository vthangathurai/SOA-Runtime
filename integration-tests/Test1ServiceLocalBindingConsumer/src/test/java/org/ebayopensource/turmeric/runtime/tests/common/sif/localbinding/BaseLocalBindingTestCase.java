/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.localbinding;

import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool.ThreadPoolStats;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseLocalCallTest;
import org.junit.Before;


public abstract class BaseLocalBindingTestCase extends BaseLocalCallTest {

	public BaseLocalBindingTestCase() throws Exception {
		super();
	}

	public BaseLocalBindingTestCase(String clientName) throws Exception {
		super(clientName);
	}
	
	@Before
	public void showThreadPoolStats() {
		ThreadPoolStats tpStats = LocalBindingThreadPool.getInstance().getStatistics();
		System.out.println(tpStats.toString());		
	}
}
