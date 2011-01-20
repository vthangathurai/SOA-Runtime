/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.monitoring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.junit.Test;


/**
 * This class contains tests to cover the corner cases
 * for MetricId that are not covered by MetricsRegistryTests.
 * 
 * The main tests are in MetricsRegistryTests.
 * 
 * @author wdeng
 */
public class MetricIdTest extends BaseMonitoringTest {

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void constructorWithNullMetricName () {
		try {
			new MetricId(null, null,  null);
			assertTrue("Expecting NullPointerException", false);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void equalsWithArgOfDiffType () {
		MetricId id = new MetricId("test", "Service1",  null);
		assertFalse(id.equals(new Object()));
		assertFalse(id.equals(null));
	}
}
