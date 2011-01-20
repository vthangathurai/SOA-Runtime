/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;


import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.common.sif.error.MarkdownTestHelper;
import org.junit.After;
import org.junit.Assume;
import org.junit.Test;


/**
 * Similar in scope to {@link BaseCallTest} but only allows LOCAL transport calls
 */
public abstract class BaseLocalCallTest extends AbstractTurmericTestCase {
	protected static final String CONFIG_ROOT = "config";
	protected String m_clientName;

	public BaseLocalCallTest() throws Exception {
		this(null);
	}
	
	public BaseLocalCallTest(String clientName) throws Exception {
		m_clientName = clientName;
	}
	
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				m_clientName, CONFIG_ROOT, LOCAL_TRANSPORT);

		setupDriver(driver);
		return driver;
	}
	
	protected void setupDriver(Test1Driver driver) {
	}
	
	@After
	public void resetMarkupClient() throws Exception {
		MarkdownTestHelper.markupClientManually(Test1Driver.TEST1_ADMIN_NAME, null, null);
	}
	
	protected void println(String text) {
		System.out.println(this.getClass().getSimpleName() + ": " + text);
	}
	
	@Test
	public void testNormalCalls() throws Exception {
		/* If m_clientName is null, it is impossible to run this test, so 
		 * So lets use the junit Assume class and flag this test as
		 * Ignored in this situation.
		 */ 
		Assume.assumeNotNull(m_clientName);
		
		// Assume passes, let the test continue.
		println("Creating Driver");
		Test1Driver driver = createDriver();
		println("Calling Driver");
		driver.doCall();
	}
}
