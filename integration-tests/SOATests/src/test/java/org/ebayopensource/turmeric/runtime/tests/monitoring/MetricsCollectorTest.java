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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentType;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.junit.Before;
import org.junit.Test;


/**
 * @author wdeng
 */
public class MetricsCollectorTest extends BaseMonitoringTest {

	@Before
	public void setUp() throws Exception {
		
		ClientConfigManager.getInstance().setConfigTestCase("testconfig", true);
		ServiceConfigManager.getInstance().setConfigTestCase("testconfig", true);
		ClientMessageProcessor.getInstance();
		ServerMessageProcessor.getInstance();

		// load the client-side service to ensure that its admin name is known
		ClientServiceDescFactory.getInstance().getServiceDesc(
			MetricsRegistryTest.SERVICE_NAME_ITEM, "default");
		ServerServiceDescFactory.getInstance().getServiceDesc(MetricsRegistryTest.SERVICE_NAME_ITEM);
	}

	@Test
	public void getUpdatableMetricValueForExistingMetricDef_Client() throws Exception {
		subTestGetUpdatableMetricValueForExistingMetricDef(
			MetricsRegistry.getClientInstance(), MetricsCollector.getClientInstance());
	}

	@Test
	public void getUpdatableMetricValueForExistingMetricDef_Server() throws Exception {
		subTestGetUpdatableMetricValueForExistingMetricDef(
			MetricsRegistry.getServerInstance(), MetricsCollector.getServerInstance());
	}

	private void subTestGetUpdatableMetricValueForExistingMetricDef(
		MetricsRegistry registry, MetricsCollector collector)
		throws Exception
	{
		try {
			MetricsRegistryTest.registerTestMetricDefs(registry);

			collector.reset();

			MetricId METRIC_ID_SPECIFIC = new MetricId(MetricsRegistryTest.METRIC_NAME_OTHER,
				MetricsRegistryTest.SERVICE_NAME_ITEM, MetricsRegistryTest.OP_NAME_FIND);

			// Try to get aggr
			MetricValueAggregator aggr = collector.getMetricValue(METRIC_ID_SPECIFIC);
			assertNotNull(aggr);

			// Try call getUpdatableMetricValue that already exists, should get the same one back.
			MetricValueAggregator aggr1 = collector.getMetricValue(METRIC_ID_SPECIFIC);
			assertSame(aggr, aggr1);
			
			// Try to get all metric values
			List<MetricValueAggregator> snapshot = collector.getAllMetricValues();

			MetricValueAggregator aggr2 = findAggregator(snapshot, aggr.getMetricId());
			List<MetricComponentType> types = aggr2.getAllComponentsTypes();
			assertNotNull(aggr2);
			assertNotSame(aggr, aggr2);
			assertSame(aggr.getMetricId(), aggr2.getMetricId());
			assertSame(aggr.getAllComponentsTypes(), types);
			assertEquals(aggr.getClassifiers(), aggr2.getClassifiers());
			if (types.isEmpty()) {
				fail("Empty types");
			}

			for (MetricClassifier classifier: aggr.getClassifiers()) {
				MetricValue comp1 = aggr.getValue(classifier);
				MetricValue comp2 = aggr2.getValue(classifier);
				assertNotSame(comp1, comp2);

				assertSame(comp1.getMetricId(), comp2.getMetricId());
				assertSame(comp1.getMetricId(), aggr.getMetricId());
				assertSame(comp1.getAllComponentsTypes(), types);
				assertSame(comp2.getAllComponentsTypes(), types);

				MetricComponentValue[] values1 = comp1.getValues();
				MetricComponentValue[] values2 = comp2.getValues();
				assertNotSame(values1, values2);
				assertEquals(values1.length, values2.length);
				assertEquals(values1.length, types.size());

				for (int i=0; i<values1.length; i++) {
					MetricComponentValue value1 = values1[i];
					MetricComponentValue value2 = values2[i];
					assertSame(value1.getName(), value2.getName());

					MetricComponentType type = findComponentType(types, value1.getName());
					assertNotNull(type);

					assertEquals(value1.getValue(), value2.getValue());
				}
			}

			// Try to reset the map
			collector.reset();
			snapshot = collector.getAllMetricValues();
			aggr2 = findAggregator(snapshot, aggr.getMetricId());
			assertTrue(aggr2.getClassifiers().isEmpty());
		} finally {
			MetricsRegistryTest.unregisterTestMetricDefs(registry);
		}
	}

	public static MetricValueAggregator findAggregator(List<MetricValueAggregator> snapshot, MetricId id) {
		for (MetricValueAggregator value: snapshot)  {
			if (value.getMetricId().equals(id)) {
				return value;
			}
		}
		return null;
	}

	public static MetricComponentType findComponentType(List<MetricComponentType> types, String name) {
		for (MetricComponentType type: types) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void getUpdatableMetricValueForNonRegisteredMetricId() throws Exception {
		try {
			MetricsRegistryTest.registerTestMetricDefs(MetricsRegistry.getClientInstance());

			MetricId nonRegisteredId =  new MetricId(
					MetricsRegistryTest.METRIC_NAME_OTHER, 
					MetricsRegistryTest.SERVICE_NAME_ITEM,
					MetricsRegistryTest.OP_NAME_EDIT);

			// Try to get the non-registed metric
			try {
				MetricsCollector.getClientInstance().getMetricValue(nonRegisteredId);
				fail("Expecting Service Exception 'Metric definition not registered'");
			} catch (ServiceRuntimeException e) {
				assertTrue(true);
			}
		} finally {
			MetricsRegistryTest.unregisterTestMetricDefs(MetricsRegistry.getClientInstance());
		}
	}
}
