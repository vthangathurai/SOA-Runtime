/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.junit.Assert;

public class AssertNoPayload implements PayloadAssert {
    public static final AssertNoPayload INSTANCE = new AssertNoPayload();

    public void assertPayload(ExecutionScope scope, Service svc, byte[] payloadData) throws Exception {
        Assert.fail("NOT IMPLEMENTED YET - assertPayload(" + scope + ", " + svc + ", " + payloadData);
    }
}
