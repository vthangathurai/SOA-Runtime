/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.junit.Assert;

public class ServiceAssertableResponse implements AssertableResponse {
    private ResponseContext context;

    public ServiceAssertableResponse(Service service) {
        this.context = service.getResponseContext();
    }

    @Override
    public void assertValueEquals(String key, String expectedValue) {
        String actualValue = getString(key);
        Assert.assertNotNull("Service.getResponseContext().getTransportHeader(" + key + ")", actualValue);
        Assert.assertEquals(expectedValue, actualValue);
    }

    @Override
    public void assertValueEqualsIgnoreCase(String key, String expectedValue) {
        String actualValue = getString(key);
        Assert.assertNotNull("Service.getResponseContext().getTransportHeader(" + key + ")", actualValue);
        Assert.assertEquals(expectedValue.toLowerCase(), actualValue.toLowerCase());
    }

    @Override
    public String getString(String key) {
        return (String) context.getTransportHeader(key);
    }
}