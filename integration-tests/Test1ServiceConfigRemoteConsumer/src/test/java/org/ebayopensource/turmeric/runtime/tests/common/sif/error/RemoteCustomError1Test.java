/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import org.ebayopensource.turmeric.runtime.common.exceptions.HTTPTransportException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.sif.tester.AssertErrorData;
import org.ebayopensource.turmeric.runtime.tests.common.sif.tester.AssertNoPayload;
import org.ebayopensource.turmeric.runtime.tests.common.sif.tester.ServicePayloadExecutor;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.CustomErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.junit.Rule;
import org.junit.Test;



public class RemoteCustomError1Test extends AbstractWithServerTest {
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");
    
    private ServicePayloadExecutor createExecutor() throws Exception {
        // Setup Request
        ServicePayloadExecutor test = new ServicePayloadExecutor();
        test.setServiceNameDefault();
        test.setClientName("configremote");
        test.setServiceURL(jetty);

        // Use known working formats
        test.addAllPayloadFormats();

        // Test all modes
        test.addAllTestModes();
        
        test.setOperationName("customError1");
        test.setUseInParams(false);
        test.addTransportHeader("TEST_CALL_HEADER", "MyValue");

        return test;
    }
    
    @Test
    public void test1Exception() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION, "true");
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_HARMLESS_EXCEPTION, "true");
        
        AssertErrorData error = new AssertErrorData();
        error.needsCause(ServiceInvocationException.class);
        error.needsExceptionText("Test1Exception: Our test1 exception");
        error.needsErrorDataSource(CustomErrorMessage.class);
        error.needsErrorDataId(2005);
        test.setAssertException(error);
        
        test.doCalls();
    }
    
    @Test
    public void test1ServiceException() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_SERVICE_EXCEPTION, "true");
        
        AssertErrorData error = new AssertErrorData();
        error.needsCause(ServiceInvocationException.class);
        error.needsExceptionText("with data my_program_data");
        error.needsErrorDataSource(CustomErrorMessage.class);
        error.needsErrorDataId(120000);
        test.setAssertException(error);
        
        test.doCalls();
    }
    
    @Test
    public void errorMapperException() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.addTransportHeader(Test1Constants.TR_HDR_ERROR_MAPPER_EXCEPTION, "true");
        test.addTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION, "true");
        
        AssertErrorData error = new AssertErrorData();
        error.needsCause(HTTPTransportException.class);
        error.needsExceptionText("Internal server error, HTTP status code=500");
        error.optionalErrorDataId(ErrorDataCollection.svc_transport_http_error);
        error.needsErrorDataSource(ErrorMessage.class);
        test.setAssertException(error);
        
        test.setAssertPayload(AssertNoPayload.INSTANCE);
        
        test.doCalls();
    }
    
    @Test
    public void testHandlerException() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.doCalls();
    }
}
