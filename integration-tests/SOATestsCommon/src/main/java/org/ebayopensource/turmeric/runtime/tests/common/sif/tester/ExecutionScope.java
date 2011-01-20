/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;

public class ExecutionScope {
    private String requestPayloadFormat;
    private String responsePayloadFormat;
    private TestMode mode;

    public ExecutionScope(String request, String response, TestMode mode) {
        this.requestPayloadFormat = request;
        this.responsePayloadFormat = response;
        this.mode = mode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExecutionScope [requestPayloadFormat=");
        builder.append(requestPayloadFormat);
        builder.append(", responsePayloadFormat=");
        builder.append(responsePayloadFormat);
        builder.append(", mode=");
        builder.append(mode);
        builder.append("]");
        return builder.toString();
    }
}