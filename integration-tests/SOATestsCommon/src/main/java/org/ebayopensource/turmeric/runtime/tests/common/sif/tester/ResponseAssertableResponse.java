/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import java.util.Map;

import javax.xml.ws.Response;

import org.junit.Assert;

@SuppressWarnings("rawtypes")
public class ResponseAssertableResponse implements AssertableResponse {
    private Response response;
    private Map<String, Object> context;

    @SuppressWarnings("unchecked")
    public ResponseAssertableResponse(Response response) {
        this.response = response;
        this.context = this.response.getContext();
    }

    @Override
    public void assertValueEquals(String key, String expectedValue) {
        String actualValue = getString(key);
        Assert.assertNotNull("Response.getContext().get(" + key + ")", actualValue);
        Assert.assertEquals(expectedValue, actualValue);
    }

    @Override
    public void assertValueEqualsIgnoreCase(String key, String expectedValue) {
        String actualValue = getString(key);
        Assert.assertNotNull("Response.getContext().get(" + key + ")", actualValue);
        Assert.assertEquals(expectedValue.toLowerCase(), actualValue.toLowerCase());
    }

    @Override
    public String getString(String key) {
        return (String) context.get(key);
    }
}