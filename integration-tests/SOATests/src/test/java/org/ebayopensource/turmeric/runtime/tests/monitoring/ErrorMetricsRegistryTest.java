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
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MetricsRegistrationHelper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.MessageContextAccessorImpl;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.util.SOAPTestUtils;
import org.ebayopensource.turmeric.runtime.tests.common.util.ServerMessageContextTestBuilder;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.junit.Test;


/**
 * @author rpallikonda
 *
 */
public class ErrorMetricsRegistryTest extends BaseMonitoringTest  {
	
	private void registerServerErrorGroups() throws Exception {
		ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");
		
		ServerMessageContext ctxt = msgtest.createServerMessageContext();
		MessageContextAccessorImpl.setContext(ctxt);
		CommonErrorData error1 = new CommonErrorData();
		error1.setDomain("SOA");
		error1.setOrganization("MPLACE");
		error1.setErrorId(1);
		
		CommonErrorData error2 = new CommonErrorData();
		error2.setDomain("SOA");
		error2.setOrganization("MPLACE");
		error2.setErrorId(100);
		
		assertTrue("Registering Error Groups for error1 failed", MetricsRegistrationHelper.registerMetricsForErrorDataGrps(error1, "MPLACE", "errgrp1 errgrp2"));
		assertTrue("Registering Error Groups for error2 failed", MetricsRegistrationHelper.registerMetricsForErrorDataGrps(error2, "MPLACE", "errgrp2 errgrp3"));
		
	//	validateServerErrorGroupRegistration();
				
	}
	
	private void registerClientErrorGroups() throws Exception {
		
		ClientMessageProcessor.getInstance();
		MessageContext ctxt =
			SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_REQUEST);
		MessageContextAccessorImpl.setContext(ctxt);
		CommonErrorData error1 = new CommonErrorData();
		error1.setDomain("SOA");
		error1.setOrganization("MPLACE");
		error1.setErrorId(1);
		
		CommonErrorData error2 = new CommonErrorData();
		error2.setDomain("SOA");
		error2.setOrganization("MPLACE");
		error2.setErrorId(100);
		
		assertTrue("Registering Error Groups for error1 failed", MetricsRegistrationHelper.registerMetricsForErrorDataGrps(error1, "MPLACE", "errgrp1 errgrp2"));
		assertTrue("Registering Error Groups for error2 failed", MetricsRegistrationHelper.registerMetricsForErrorDataGrps(error2, "MPLACE", "errgrp2 errgrp3"));
				
	}	
	
/*	private boolean registerErrorGroup1() {
		CommonErrorData error1 = new CommonErrorData();
		error1.setDomain("SOA");
		error1.setOrganization("MPLACE");
		error1.setErrorId(1);
		
		return MetricsRegistrationHelper.registerMetricsForErrorDataGrps(error1, "errgrp1", "errgrp2");
		
		
	}*/
	
	private void validateMetricCount(MetricId metricId, int expected, boolean isClientSide) {
		MetricValueAggregator metricValueAgg = null;
		if (isClientSide)
			metricValueAgg = MetricsCollector.getClientInstance().getMetricValue(metricId);
		else 
			metricValueAgg = MetricsCollector.getServerInstance().getMetricValue(metricId);
		
		Collection<MetricClassifier> classifiers = metricValueAgg.getClassifiers();
		for(MetricClassifier classifier:classifiers){
				MetricValue value = metricValueAgg.getValue(classifier);
				MetricComponentValue[] compValues = value.getValues();
				assertEquals("The metric count did not match for classifier: " + classifier.getUseCase() + 
								"and component: " + compValues[0].getName(),  compValues[0].getValue().toString(), String.valueOf(expected));
		}
		
	}
	
	private void unregisterErrorGroups() {
		
		String errgrp1 = "MPLACE.SOA.errgrp1";
		String errgrp2 = "MPLACE.SOA.errgrp2";
		String errgrp3 = "MPLACE.SOA.errgrp3";
		
		SystemMetricDefs.SvcLevelErrorMetricDef svcMetricDef = new SystemMetricDefs.SvcLevelErrorMetricDef(errgrp1);
		SystemMetricDefs.OpLevelErrorMetricDef opMetricDef = new SystemMetricDefs.OpLevelErrorMetricDef(svcMetricDef);
		
		MetricsRegistry.getClientInstance().unregisterMetricByDef(svcMetricDef);
		MetricsRegistry.getClientInstance().unregisterMetricByDef(opMetricDef);
		MetricsRegistry.getServerInstance().unregisterMetricByDef(svcMetricDef);
		MetricsRegistry.getServerInstance().unregisterMetricByDef(opMetricDef);
		
		svcMetricDef = new SystemMetricDefs.SvcLevelErrorMetricDef(errgrp2);
		opMetricDef = new SystemMetricDefs.OpLevelErrorMetricDef(svcMetricDef);
		
		MetricsRegistry.getClientInstance().unregisterMetricByDef(svcMetricDef);
		MetricsRegistry.getClientInstance().unregisterMetricByDef(opMetricDef);
		MetricsRegistry.getServerInstance().unregisterMetricByDef(svcMetricDef);
		MetricsRegistry.getServerInstance().unregisterMetricByDef(opMetricDef);
		
		svcMetricDef = new SystemMetricDefs.SvcLevelErrorMetricDef(errgrp3);
		opMetricDef = new SystemMetricDefs.OpLevelErrorMetricDef(svcMetricDef);
		
		MetricsRegistry.getClientInstance().unregisterMetricByDef(svcMetricDef);
		MetricsRegistry.getClientInstance().unregisterMetricByDef(opMetricDef);
		MetricsRegistry.getServerInstance().unregisterMetricByDef(svcMetricDef);
		MetricsRegistry.getServerInstance().unregisterMetricByDef(opMetricDef);		
	}
	
	@Test
	public void errorGroupRegistration() throws Exception {
		registerServerErrorGroups();
				
		MetricId metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errgrp1", "blah" , MetricDef.OP_DONT_CARE);
		MetricDef metricDef = MetricsRegistry.getServerInstance().findMetricDef(metricId);
		assertNotNull("No Service Metrics found for errgrp1 in Server registry" , metricDef);
		
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errgrp1", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getServerInstance().findMetricDef(metricId);
		assertNotNull("No OP Metrics found for errgrp1 in Server registry" , metricDef);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errgrp2", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getServerInstance().findMetricDef(metricId);
		assertNotNull("No Service Metrics found for errgrp2 in Server registry" , metricDef);
		
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errgrp2", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getServerInstance().findMetricDef(metricId);
		assertNotNull("No OP Metrics found for errgrp2  in Server registry" , metricDef);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errgrp3", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getServerInstance().findMetricDef(metricId);
		assertNotNull("No Service Metrics found for errgrp3 in Server registry" , metricDef);
		
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errgrp3", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getServerInstance().findMetricDef(metricId);
		assertNotNull("No OP Metrics found for errgrp3  in Server registry" , metricDef);		

		registerClientErrorGroups();
		
		 metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errgrp1", "blah" , MetricDef.OP_DONT_CARE);	
		 metricDef = MetricsRegistry.getClientInstance().findMetricDef(metricId);
		assertNotNull("No Service Metrics found for errgrp1 in Client registry" , metricDef);
		
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errgrp1", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getClientInstance().findMetricDef(metricId);
		assertNotNull("No OP Metrics found for errgrp1 in Client registry" , metricDef);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errgrp2", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getClientInstance().findMetricDef(metricId);
		assertNotNull("No Service Metrics found for errgrp2 in Client registry" , metricDef);
		
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errgrp2", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getClientInstance().findMetricDef(metricId);
		assertNotNull("No OP Metrics found for errgrp1  in Client registry" , metricDef);

		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errgrp3", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getClientInstance().findMetricDef(metricId);
		assertNotNull("No Service Metrics found for errgrp3 in Client registry" , metricDef);
		
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errgrp3", "blah" , MetricDef.OP_DONT_CARE);
		metricDef = MetricsRegistry.getClientInstance().findMetricDef(metricId);
		assertNotNull("No OP Metrics found for errgrp3  in Client registry" , metricDef);
		
		unregisterErrorGroups();
				
	}
	

	@Test
	public void groupMetricUpdate() throws Exception {
		TestErrorDataContentProvider provider = new TestErrorDataContentProvider();

		ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");
		
		MessageContext ctxt = msgtest.createServerMessageContext();
		MessageContextAccessorImpl.setContext(ctxt);
		ErrorDataKey key = new ErrorDataKey();
		key.setId(1);
		CommonErrorData errorData1 = provider.getErrorData(key, null, null);
		
		key.setId(2);
		CommonErrorData errorData2 = provider.getErrorData(key, null, null);
		
		ServiceException se1 = new ServiceException(errorData1);
		ServiceException se2 = new ServiceException(errorData2);
		
		ctxt.addError(se1);
		ctxt.addError(se2);
		
		MetricId metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp1", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 1, false);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp2", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 2, false);

		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp3", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 1, false);
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errtestgrp1", TestUtils.TEST1_SERVICE_NAME , "myTestOperation");
		validateMetricCount(metricId, 1, false);
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errtestgrp2", TestUtils.TEST1_SERVICE_NAME , "myTestOperation");
		validateMetricCount(metricId, 2, false);
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errtestgrp3", TestUtils.TEST1_SERVICE_NAME , "myTestOperation");
		validateMetricCount(metricId, 1, false);
		
		
		// Now throw the expection on the client side
		ClientMessageProcessor.getInstance();
		ctxt =
			SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_REQUEST);
	
		MessageContextAccessorImpl.setContext(ctxt);
		key = new ErrorDataKey();
		key.setId(1);
		errorData1 = provider.getErrorData(key, null, null);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp1", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 0, true);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp2", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 0, true);

		se1 = new ServiceException(errorData1);
		ctxt.addError(se1);
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp1", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 1, true);
		validateMetricCount(metricId, 1, false); // The server side count should still be same
		
		metricId = new MetricId("SoaFwk.Err.MPLACE.SOA.errtestgrp2", TestUtils.TEST1_SERVICE_NAME , MetricDef.OP_DONT_CARE);
		validateMetricCount(metricId, 1, true);
		validateMetricCount(metricId, 2, false); // The server side count should still be same

		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errtestgrp1", TestUtils.TEST1_SERVICE_NAME , "myTestOperation");
		validateMetricCount(metricId, 1, true);
		validateMetricCount(metricId, 1, false); // The server side count should still be same
		
		metricId = new MetricId("SoaFwk.Op.Err.MPLACE.SOA.errtestgrp2", TestUtils.TEST1_SERVICE_NAME , "myTestOperation");
		validateMetricCount(metricId, 1, true);
		validateMetricCount(metricId, 2, false); // The server side count should still be same
	}
}

