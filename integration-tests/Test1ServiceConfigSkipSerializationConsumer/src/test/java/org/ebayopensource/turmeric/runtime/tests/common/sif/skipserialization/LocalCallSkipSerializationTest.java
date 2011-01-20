/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.skipserialization;

import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.junit.ServiceCleanup;
import org.ebayopensource.turmeric.runtime.tests.common.sif.tester.ServicePayloadExecutor;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author ichernyshev
 */
public class LocalCallSkipSerializationTest extends AbstractWithServerTest {
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
    public void testNormalCalls() throws Exception {
        ServicePayloadExecutor test = createExecutor();
        test.doCalls();
    }
}
