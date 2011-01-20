/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.IntSumMetricValue;
import org.junit.Test;


/**
 * @author wdeng
 */
public class MetricDefTest extends BaseMonitoringTest {

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void constructorWithNullMetricName () {
		try {
			new MetricDef(null, null, null, MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
			assertTrue("Expecting NullPointerException", false);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		try {
			new MetricDef("Test", new QName("", "Service1"), null,
				null, MetricCategory.Other, IntSumMetricValue.class);
			assertTrue("Expecting NullPointerException", false);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		try {
			new MetricDef("Test", new QName("", "Service1"), null,
				MonitoringLevel.NORMAL, null, IntSumMetricValue.class);
			assertTrue("Expecting NullPointerException", false);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void equalsWithArgOfDiffType () {
		MetricDef def = new MetricDef("Test", new QName("", "Service1"), null,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
		assertEquals(MetricCategory.fromString("Other"), def.getCategory());
		assertFalse(def.equals(null));
		assertFalse(def.equals(new Object()));
	}
}
