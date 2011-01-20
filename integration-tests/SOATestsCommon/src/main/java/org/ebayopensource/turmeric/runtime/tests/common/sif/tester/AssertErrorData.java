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
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;

/**
 * A common implementation of ExceptionAssert for validating the service exception for various values.
 */
public class AssertErrorData implements ExceptionAssert {
    private Class<? extends Exception> expectedCause;
    private boolean mustHaveErrorData = true;
    private Long expectedErrorDataId;
    private Class<?> expectedErrorDataSource;
    private String expectedExceptionText;

    public void needsCause(Class<? extends Exception> exceptionClass) {
        this.expectedCause = exceptionClass;
    }

    public void needsErrorDataId(CommonErrorData errorData) {
        needsErrorDataId(errorData.getErrorId());
    }

    public void optionalErrorDataId(CommonErrorData errorData) {
        optionalErrorDataId(errorData.getErrorId());
    }
    
    public void optionalErrorDataId(long id) {
        this.mustHaveErrorData = false;
        this.expectedErrorDataId = id;
    }

    public void needsErrorDataId(long id) {
        this.mustHaveErrorData = true;
        this.expectedErrorDataId = id;
    }

    public void needsErrorDataSource(Class<?> errorDataSource) {
        this.expectedErrorDataSource = errorDataSource;
    }

    public void needsExceptionText(String subtext) {
        this.expectedExceptionText = subtext;
    }

    @Override
    public void assertException(ExecutionScope scope, Service svc, Exception exception) throws AssertionError,
                    Exception {
        AssertableServiceError error = new AssertableServiceError(exception);
        if (expectedCause != null) {
            error.assertCause(expectedCause);
        }
        if (expectedExceptionText != null) {
            error.assertExceptionMessageContains(expectedExceptionText);
        }
        if (mustHaveErrorData) {
            error.assertHasErrorData();
        }
        if (expectedErrorDataId != null) {
            error.assertErrorDataId(expectedErrorDataId);
        }
        if (expectedErrorDataSource != null) {
            error.assertErrorDataSource(expectedErrorDataSource);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AssertErrorData [expectedCause=");
        builder.append(expectedCause);
        builder.append(", mustHaveErrorData=");
        builder.append(mustHaveErrorData);
        builder.append(", expectedErrorDataId=");
        builder.append(expectedErrorDataId);
        builder.append(", expectedErrorDataSource=");
        builder.append(expectedErrorDataSource);
        builder.append(", expectedExceptionText=");
        builder.append(expectedExceptionText);
        builder.append("]");
        return builder.toString();
    }
}
