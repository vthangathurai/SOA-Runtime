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

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.SecurityException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.MessageContextAccessorImpl;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.tests.common.util.ServerMessageContextTestBuilder;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;

import org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants;
import org.junit.Before;
import org.junit.Test;


public class ErrorMetricsTest extends BaseMonitoringTest {

    private MessageContext ctxt;
    private long keyId = 0;

    static private int totalErrCnt;
    static private int errCnt;
    static private int warnCnt;
    static private int sysCnt;
    static private int appCnt;
    static private int reqCnt;
    static private int configCnt;
    static private int authCnt;
    static private int authzCnt;
    static private int blkLstCnt;

    @Before
    public void setupServerContext() throws Exception {
        ServerMessageContextTestBuilder msgtest = new ServerMessageContextTestBuilder();
		msgtest.setTestServer(jetty);
		msgtest.setBindingName("XML");
        
		ctxt = msgtest.createServerMessageContext();
        MessageContextAccessorImpl.setContext(ctxt);        
        registerDynamicMetrics();
        storeInitialMetric();
        throwExceptions();
    }

    private void throwExceptions() throws Exception {
        ctxt.addError(new ServiceException(createErrorData(++keyId, ErrorCategory.APPLICATION, ErrorSeverity.ERROR, "TurmericRuntime", "Config")));
        ctxt.addError(new ServiceException(createErrorData(++keyId, ErrorCategory.SYSTEM, ErrorSeverity.ERROR, "Security", "Authorization")));
        ctxt.addError(new ServiceException(createErrorData(++keyId, ErrorCategory.REQUEST, ErrorSeverity.WARNING, "TurmericRuntime", "Markdown")));
        ctxt.addError(new ServiceException(createErrorData(++keyId, ErrorCategory.APPLICATION, ErrorSeverity.ERROR, "TurmericRuntime", "Config")));
        // Null Category and null Severity
        ctxt.addError(new ServiceException(createErrorData(++keyId, null, null, "TurmericRuntime", "Config")));
        // Both errorDescs are of System Category and error severity
        CommonErrorData errorData = ErrorUtils.createErrorData(ErrorConstants.SVC_SECURITY_UNEXPECTED_AUTHN_ERROR, ErrorConstants.ERRORDOMAIN,
        						new Object[] { "test1" });
        ctxt.addError(new SecurityException(errorData));
        errorData = ErrorUtils.createErrorData(ErrorConstants.SVC_SECURITY_BLACKLIST_FAILED, ErrorConstants.ERRORDOMAIN,
				new Object[] { "test2" }); 
        	
        ctxt.addError(new SecurityException(errorData));        
    }

    /*
     * Throw Exceptions to register dynamic metrics.
     */
    private void registerDynamicMetrics() throws Exception {        
        throwExceptions();
    }

    @Test
    public void testErrorCount() {
        assertEquals(5, getMetricCount("SoaFwk.Err.Severity.Error") - errCnt);
    }

    @Test
    public void testWarningCount() {
        assertEquals(1, getMetricCount("SoaFwk.Err.Severity.Warning") - warnCnt);
    }

    @Test
    public void testCategoryCount() {
        assertEquals(3, getMetricCount("SoaFwk.Err.Category.System") - sysCnt);
        assertEquals(2, getMetricCount("SoaFwk.Err.Category.Application") - appCnt);
        assertEquals(1, getMetricCount("SoaFwk.Err.Category.Request") - reqCnt);
    }

    @Test
    public void testInvariants() {
        // The reason for "+1": an Error was added with null Severity and null Category.  
        assertEquals(getMetricCount("SoaFwk.Err.Severity.Error") + getMetricCount("SoaFwk.Err.Severity.Warning")
                - (errCnt + warnCnt) + 1, getMetricCount("SoaFwk.Err.Total")
                - totalErrCnt);

        assertEquals(getMetricCount("SoaFwk.Err.Category.Application") + getMetricCount("SoaFwk.Err.Category.System")
                + getMetricCount("SoaFwk.Err.Category.Request")
                - (appCnt + reqCnt + sysCnt) + 1,
                getMetricCount("SoaFwk.Err.Total") - totalErrCnt);
    }

    @Test
    public void testTotalMetrics() throws Exception {
        assertEquals(7, getMetricCount("SoaFwk.Err.Total") - totalErrCnt);
    }

    @Test
    public void testSubDomainMetrics() {
        int subDomainMetricCnt = getMetricCount("SoaFwk.Err.TurmericRuntime.Config");
        assertEquals(1, subDomainMetricCnt - configCnt);
        
        subDomainMetricCnt = getMetricCount("SoaFwk.Err.Security.Authorization");
        assertEquals(1, subDomainMetricCnt - authCnt);

        assertEquals(7, getMetricCount("SoaFwk.Err.Total") - totalErrCnt);
    }

    @Test
    public void testUsingErrorDescs() {
        assertEquals(1, getMetricCount("SoaFwk.Err.Security.Authentication") - authzCnt);
        assertEquals(1, getMetricCount("SoaFwk.Err.Security.BlackList") - blkLstCnt);
    }

    @Test
	public void testResetMetrics() {
		MetricsCollector.getServerInstance().reset(TestUtils.TEST1_SERVICE_NAME);
		assertEquals(0, getMetricCount("SoaFwk.Err.Severity.Error"));
		assertEquals(0, getMetricCount("SoaFwk.Err.Severity.Warning"));
        assertEquals(0, getMetricCount("SoaFwk.Err.Category.System"));
        assertEquals(0, getMetricCount("SoaFwk.Err.Category.Application"));
        assertEquals(0, getMetricCount("SoaFwk.Err.Category.Request"));
        assertEquals(0, getMetricCount("SoaFwk.Err.Total"));
        assertEquals(0, getMetricCount("SoaFwk.Err.Security.Authentication"));
        assertEquals(0, getMetricCount("SoaFwk.Err.Security.BlackList"));
	}
    

    private static int getMetricCount(String metric) {
        int count = 0;
        try {
            MetricId metricId = new MetricId(metric, TestUtils.TEST1_SERVICE_NAME, MetricDef.OP_DONT_CARE);
            MetricValueAggregator metricValueAgg = null;
            metricValueAgg = MetricsCollector.getServerInstance().getMetricValue(metricId);

            Collection<MetricClassifier> classifiers = metricValueAgg.getClassifiers();
            for (MetricClassifier classifier : classifiers) {
                MetricValue value = metricValueAgg.getValue(classifier);
                MetricComponentValue[] compValues = value.getValues();
                count += Integer.valueOf(compValues[0].getValue().toString());
            }
        } catch (NumberFormatException e) {
            count = -1;
        }
        return count;
    }

    private static CommonErrorData createErrorData(long id, ErrorCategory category, ErrorSeverity severity, String domain, String subdomain) throws Exception {
        TestErrorDataContentProvider provider = new TestErrorDataContentProvider();
        ServerMessageProcessor.getInstance();

        ErrorDataKey key = new ErrorDataKey();
        key.setId(id);
        CommonErrorData errorData = provider.getErrorData(key, null, null);
        errorData.setCategory(category);
        errorData.setSeverity(severity);
        errorData.setDomain(domain);
        errorData.setSubdomain(subdomain);
        return errorData;
    }

    private static void storeInitialMetric() {
        totalErrCnt = getMetricCount("SoaFwk.Err.Total");
        errCnt = getMetricCount("SoaFwk.Err.Severity.Error");
        warnCnt = getMetricCount("SoaFwk.Err.Severity.Warning");
        sysCnt = getMetricCount("SoaFwk.Err.Category.System");
        appCnt = getMetricCount("SoaFwk.Err.Category.Application");
        reqCnt = getMetricCount("SoaFwk.Err.Category.Request");
        configCnt = getMetricCount("SoaFwk.Err.TurmericRuntime.Config");
        authCnt = getMetricCount("SoaFwk.Err.Security.Authorization");
        authzCnt = getMetricCount("SoaFwk.Err.Security.Authentication");
        blkLstCnt = getMetricCount("SoaFwk.Err.Security.BlackList");
    }

}
