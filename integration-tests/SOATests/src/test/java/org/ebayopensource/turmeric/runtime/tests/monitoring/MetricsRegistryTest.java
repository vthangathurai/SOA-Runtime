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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.IntSumMetricValue;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.junit.Before;
import org.junit.Test;


/**
 * @author wdeng
 */
public class MetricsRegistryTest extends BaseMonitoringTest {

	final static String METRIC_NAME_CC = "call count";
	final static String METRIC_NAME_TT = "total time";
	final static String METRIC_NAME_EC = "error count";
	final static String METRIC_NAME_FC = "failure count";
	final static String METRIC_NAME_DBT = "DB time";
	final static String METRIC_NAME_CT = "CPU time";
	final static String METRIC_NAME_OTHER = "other";

	final static String SERVICE_NAME_ITEM = "test1";
	final static String SERVICE_NAME_USER = "test1gen";
	final static QName SERVICE_QNAME_ITEM = new QName(SOAConstants.DEFAULT_SERVICE_NAMESPACE, SERVICE_NAME_ITEM, "");
	final static QName SERVICE_QNAME_USER = new QName("http://www.ebayopensource.org/turmeric/common/config", SERVICE_NAME_USER, "");

	final static String OP_NAME_FIND = "find";
	final static String OP_NAME_EDIT = "edit";

	@Before
	public void setupMessageProcessors() throws Exception {
		ClientMessageProcessor.getInstance();
		ServerMessageProcessor.getInstance();
		
		ServerServiceDescFactory serviceDescFactory = ServerServiceDescFactory.getInstance();
		serviceDescFactory.loadAllServices(); // Load the Test1 Service.

	}

	@Test
	public void basicMetricRegistration() throws Exception {
		registerTestMetricDefs();
		unregisterTestMetricDefs();
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void registerOpDontCareWithExistingApplyAllNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricDef def = new MetricDef(METRIC_NAME_CC, SERVICE_QNAME_ITEM, MetricDef.OP_DONT_CARE,
				MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
			try {
				registry.registerMetric(def);
				assertTrue("Expecting ServiceException", false);
			} catch (ServiceException e) {
				System.out.println("Caught expected: " + e.toString());
			}
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void registerApplyAllWithExistingDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricDef def = new MetricDef(METRIC_NAME_TT, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_APPLY_TO_ALL,
				MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
			try {
				registry.registerMetric(def);
				assertTrue("Expecting ServiceException", false);
			} catch (ServiceException e) {
				System.out.println("Caught expected: " + e.toString());
			}
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void registerSvcApplyAllWithExistingDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricDef def = new MetricDef(METRIC_NAME_TT, MetricDef.SVC_APPLY_TO_ALL, OP_NAME_FIND,
				MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
			try {
				registry.registerMetric(def);
				assertTrue("Expecting ServiceException", false);
			} catch (ServiceException e) {
				System.out.println("Caught expected: " + e.toString());
			}
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void registerOpApplyAllWithExistingDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricDef def = new MetricDef(METRIC_NAME_TT, SERVICE_QNAME_ITEM, MetricDef.OP_APPLY_TO_ALL,
				MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
			registry.registerMetric(def);

			MetricId specific = new MetricId(METRIC_NAME_TT, SERVICE_NAME_ITEM, OP_NAME_EDIT);
			MetricDef def2 = registry.findMetricDef(specific);
			assertNotSame(def, def2);
			assertEquals(def, def2);
		} finally {
			unregisterTestMetricDefs();
		}
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstApplyAll() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_CC, SERVICE_NAME_USER, OP_NAME_EDIT);
			MetricDef def = registry.findMetricDef(specific);
			assertTrue(null != def);
			assertEquals(METRIC_NAME_CC, def.getMetricName());
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstSvcApplyAll() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_EC, SERVICE_NAME_ITEM, OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertTrue(null != def);
			assertEquals(METRIC_NAME_EC, def.getMetricName());
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificSvcAgainstSvcApplyAllNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_EC, SERVICE_NAME_USER, MetricDef.OP_APPLY_TO_ALL);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstOpApplyAll() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_TT, SERVICE_NAME_ITEM, OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertTrue(null != def);
			assertEquals(METRIC_NAME_TT, def.getMetricName());
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstOpApplyAllNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_TT, SERVICE_NAME_USER, OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificSvcAgainstOpApplyAllNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_TT, SERVICE_NAME_USER, MetricDef.OP_APPLY_TO_ALL);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificOpAgainstOpApplyAll() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_TT, SERVICE_NAME_ITEM, OP_NAME_EDIT);
			MetricDef def = registry.findMetricDef(specific);
			assertTrue(null != def);
			assertEquals(METRIC_NAME_TT, def.getMetricName());
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificOpAgainstOpApplyAllNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_TT, SERVICE_NAME_USER, OP_NAME_EDIT);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstDontCare() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_FC, SERVICE_NAME_USER, OP_NAME_EDIT);
			MetricDef def = registry.findMetricDef(specific);
			assertNull(def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificSvcAgainstDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_FC, SERVICE_NAME_USER, MetricDef.OP_APPLY_TO_ALL);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificOpAgainstDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_FC, MetricDef.SVC_APPLY_TO_ALL.getLocalPart(), OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstOpDontCare() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_DBT, SERVICE_NAME_ITEM, OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertTrue(null != def);
			assertEquals(METRIC_NAME_DBT, def.getMetricName());
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstOpDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_DBT, SERVICE_NAME_USER, OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstSvcDontCare() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_CT, SERVICE_NAME_ITEM, OP_NAME_FIND);
			MetricDef def = registry.findMetricDef(specific);
			assertNull(def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getMetricDefSpecificAgainstSvcDontCareNeg() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		try {
			registerTestMetricDefs();
			MetricId specific = new MetricId(METRIC_NAME_CT, SERVICE_NAME_USER, OP_NAME_EDIT);
			MetricDef def = registry.findMetricDef(specific);
			assertEquals(null, def);
		} finally {
			unregisterTestMetricDefs();
		}
	}

	private void registerTestMetricDefs() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		registerTestMetricDefs(registry);
	}

	private void unregisterTestMetricDefs() throws Exception {
		MetricsRegistry registry = MetricsRegistry.getServerInstance();

		unregisterTestMetricDefs(registry);
	}

	/**
	 * Convinient method for register MetricDefs for testing.
	 *
	 * @throws Exception
	 */
	static void registerTestMetricDefs(MetricsRegistry registry) throws Exception {
		registerWithDef(registry);
		registerWithName(registry);
		registerWithDefs(registry);
	}

	/**
	 * Convinient method for unregister testing MetricDefs.
	 *
	 * @throws Exception
	 */
	static void unregisterTestMetricDefs(MetricsRegistry registry) throws Exception {
		unregisterWithId(registry);
		unregisterWithIds(registry);
		unregisterWithDef(registry);
		unregisterWithDefs(registry);
	}

	private static void registerWithDef(MetricsRegistry registry) throws Exception {
		MetricDef def = new MetricDef(METRIC_NAME_CC, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_APPLY_TO_ALL,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
		registry.registerMetric(def);
		MetricId specificId = new MetricId(def.getMetricName(), SERVICE_NAME_ITEM, OP_NAME_FIND);
		MetricDef def1 = registry.findMetricDef(specificId);
		assertEquals(def, def1);
	}

	private static void registerWithName(MetricsRegistry registry) throws Exception {
//TODO:  to be enabled when thread local MC in place.
//		registry.registerMetric(METRIC_NAME_OTHER, MonitoringLevel.INFO, MetricCategory.Other);
	}

	private static void registerWithDefs(MetricsRegistry registry) throws Exception {
		ArrayList<MetricDef> defs = new ArrayList<MetricDef>();
		MetricDef def = new MetricDef(METRIC_NAME_TT, SERVICE_QNAME_ITEM, MetricDef.OP_APPLY_TO_ALL,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
		defs.add(def);
		defs.add(new MetricDef(METRIC_NAME_EC, MetricDef.SVC_APPLY_TO_ALL, OP_NAME_FIND,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class));
		defs.add(new MetricDef(METRIC_NAME_DBT, SERVICE_QNAME_ITEM, MetricDef.OP_DONT_CARE,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class));
//		TODO:  to be disabled when thread local MC in place.
		defs.add(new MetricDef(METRIC_NAME_OTHER, SERVICE_QNAME_ITEM, OP_NAME_FIND,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class));
		registry.registerMetrics(defs);
		MetricId specificId = new MetricId(def.getMetricName(), SERVICE_NAME_ITEM, OP_NAME_FIND);
		MetricDef def1 = registry.findMetricDef(specificId);
		assertEquals(def, def1);
		assertEquals(def.toString(), def1.toString());
	}

	private static void unregisterWithId(MetricsRegistry registry) throws Exception {
		MetricDef def = new MetricDef(METRIC_NAME_CC, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_APPLY_TO_ALL,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
		MetricDef def1 = registry.unregisterMetric(METRIC_NAME_CC, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_APPLY_TO_ALL);
		assertEquals(def, def1);
	}

	private static void unregisterWithIds(MetricsRegistry registry) throws Exception {
		ArrayList<MetricDef> defs = new ArrayList<MetricDef>();
		MetricDef def = new MetricDef(METRIC_NAME_EC, MetricDef.SVC_APPLY_TO_ALL, OP_NAME_FIND,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
		defs.add(def);
		List<MetricDef> def1 = registry.unregisterMetricsByDef(defs);
		assertEquals(defs.size(), def1.size());
		assertTrue(def1.contains(def));
	}

	private static void unregisterWithDef(MetricsRegistry registry) throws Exception {
		MetricDef def = new MetricDef(METRIC_NAME_TT, SERVICE_QNAME_ITEM, MetricDef.OP_APPLY_TO_ALL,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class);
		registry.unregisterMetricByDef(def);
	}

	private static void unregisterWithDefs(MetricsRegistry registry) throws Exception {
		registry.unregisterMetric(METRIC_NAME_CC, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_APPLY_TO_ALL);
		registry.unregisterMetric(METRIC_NAME_TT, SERVICE_QNAME_ITEM, MetricDef.OP_APPLY_TO_ALL);
		ArrayList<MetricDef> defs = new ArrayList<MetricDef>();
		defs.add(new MetricDef(METRIC_NAME_DBT, SERVICE_QNAME_ITEM, MetricDef.OP_DONT_CARE,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class));
		defs.add(new MetricDef(METRIC_NAME_OTHER, SERVICE_QNAME_ITEM, OP_NAME_FIND,
			MonitoringLevel.NORMAL, MetricCategory.Other, IntSumMetricValue.class));
		registry.unregisterMetricsByDef(defs);
	}
}
