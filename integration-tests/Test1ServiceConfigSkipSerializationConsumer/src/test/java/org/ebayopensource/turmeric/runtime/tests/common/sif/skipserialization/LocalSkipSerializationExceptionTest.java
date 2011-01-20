/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.skipserialization;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.junit.ServiceCleanup;
import org.ebayopensource.turmeric.runtime.tests.common.sif.tester.AssertErrorData;
import org.ebayopensource.turmeric.runtime.tests.common.sif.tester.ServicePayloadExecutor;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author ichernyshev
 */
public class LocalSkipSerializationExceptionTest extends AbstractWithServerTest {
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

    @Rule
    public ServiceCleanup cleanup = new ServiceCleanup("skipserialization");

    private ServicePayloadExecutor createExecutor() throws Exception {
        // Setup Request
        ServicePayloadExecutor test = new ServicePayloadExecutor();
        test.setServiceName("skipserialization");
        test.setClientName("skipserialization");
        test.setServiceURL(jetty);

        // Use known working formats
        test.addAllPayloadFormats();

        // Test all modes
        test.addAllTestModes();

        test.setOperationName("myTestOperation");
        test.setMessage(TestUtils.createTestMessage());
        test.addTransportHeader("TEST_CALL_HEADER", "MyValue");
        test.setSkipSerialization(true);

        return test;
    }

    @Test
    public void test1Exception() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION, "true");
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_HARMLESS_EXCEPTION, "true");

        AssertErrorData error = new AssertErrorData();
        error.needsCause(ServiceInvocationException.class);
        error.needsErrorDataSource(ErrorMessage.class);
        error.optionalErrorDataId(2005);
        test.setAssertException(error);

        test.doCalls();
    }

    @Test
    public void test1ServiceException() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_SERVICE_EXCEPTION, "true");

        AssertErrorData error = new AssertErrorData();
        error.needsCause(ServiceInvocationException.class);
        error.needsErrorDataSource(ErrorMessage.class);
        error.optionalErrorDataId(120000);
        test.setAssertException(error);

        test.doCalls();
    }

    @Test
    public void errorMapperException() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION, "true");
        test.addTransportHeader(Test1Constants.TR_HDR_ERROR_MAPPER_EXCEPTION, "true");

        AssertErrorData error = new AssertErrorData();
        error.needsCause(ServiceInvocationException.class);
        // error.needsExceptionText("Unable to map errors due to: java.lang.RuntimeException");
        error.needsExceptionText("java.lang.RuntimeException: Test error mapper exception");
        error.needsErrorDataSource(ErrorMessage.class);
        error.needsErrorDataId(ErrorDataCollection.svc_rt_unable_to_map_errors);
        test.setAssertException(error);

        test.doCalls();
    }

    @Test
    public void testHandlerException() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.doCalls();
    }
}
