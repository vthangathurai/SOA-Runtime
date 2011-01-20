/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import static org.hamcrest.Matchers.*;

import java.io.PrintStream;
import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP11Fault;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP12Fault;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.CustomErrorMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.util.TestUtils;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.junit.Assert;

public class AssertableServiceError {
    private Exception exception;
    private Object response;
    private Object errorDataSource;
    private ErrorData errorData;

    public AssertableServiceError(Exception e) {
        this.exception = e;
        unwrapException(e);
    }

    private void unwrapException(Throwable t) {
        if (t instanceof ServiceInvocationException) {
            ServiceInvocationException sie = (ServiceInvocationException) t;
            response = sie.getErrorResponse();
            if (unwrapErrorData(response)) {
                return;
            }
        }

        if (t instanceof ServiceException) {
            ServiceException se = (ServiceException) t;
            Object response = se.getErrorMessage();
            if (unwrapErrorData(response)) {
                return;
            }
        }

        if (t instanceof ServiceExceptionInterface) {
            ServiceExceptionInterface sei = (ServiceExceptionInterface) t;
            Object response = sei.getErrorMessage();
            if (unwrapErrorData(response)) {
                return;
            }
        }

        if (t.getCause() != null) {
            unwrapException(t.getCause());
        }
    }

    private boolean unwrapErrorData(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof CustomErrorMessage) {
            CustomErrorMessage msg = (CustomErrorMessage) obj;
            this.errorDataSource = msg;
            ErrorType errorType = msg.getError().get(0);
            this.errorData = TestUtils.errorTypeToErrorData(errorType);
            return (this.errorData != null);
        }

        if (obj instanceof MyMessage) {
            MyMessage msg = (MyMessage) obj;
            this.errorDataSource = msg;
            ErrorType errorType = msg.getError().get(0);
            this.errorData = TestUtils.errorTypeToErrorData(errorType);
            return (this.errorData != null);
        }

        if (obj instanceof ErrorMessage) {
            ErrorMessage msg = (ErrorMessage) obj;
            this.errorDataSource = msg;
            this.errorData = msg.getError().get(0);
            return (this.errorData != null);
        }

        if (obj instanceof SOAP11Fault) {
            SOAP11Fault fault = (SOAP11Fault) obj;
            this.errorDataSource = fault;
            this.errorData = ((ErrorMessage) fault.getDetail()).getError().get(0);
            return (this.errorData != null);
        }

        if (obj instanceof SOAP12Fault) {
            SOAP12Fault fault = (SOAP12Fault) obj;
            this.errorDataSource = fault;
            this.errorData = ((ErrorMessage) fault.getDetail()).getError().get(0);
            return (this.errorData != null);
        }

        Assert.fail(String.format("Don't know how to unwrap error data for: (%s) %s", obj.getClass().getName(), obj));
        return false;
    }

    public void assertCause(Class<? extends Exception> clazz) {
        Throwable nested = this.exception;
        while (nested != null) {
            if (clazz.isAssignableFrom(nested.getClass())) {
                return; // found it
            }
            nested = nested.getCause();
        }
        dumpExceptionTree(System.out, "", this.exception);
        Assert.fail("Unable to find service exception cause: " + clazz.getName());
    }

    public void assertExceptionMessageContains(String subtext) {
        if (!this.exception.getMessage().contains(subtext)) {
            dumpExceptionTree(System.out, "", this.exception);
        }
        Assert.assertThat("Service exception", this.exception.getMessage(), containsString(subtext));
    }

    public void assertHasErrorData() {
        if (errorData == null) {
            dumpExceptionTree(System.out, "", this.exception);
        }
        Assert.assertNotNull(
                        "Service should have tossed an exception with error data present  (see console for stack trace details)",
                        errorData);
    }

    public void assertErrorDataId(long expectedId) {
        if (errorData == null) {
            return; // Checking against no error data is a different assert
        }
        long actualId = errorData.getErrorId();
        if (actualId != expectedId) {
            dumpExceptionTree(System.out, "", this.exception);
        }
        Assert.assertEquals("Service exception with error data ID (see console for stack trace details)", expectedId,
                        actualId);
    }

    public void assertErrorDataSource(Class<?> expectedErrorDataSource) {
        if (errorData == null) {
            return; // Checking against no error data is a different assert
        }
        Assert.assertThat("Service errordata source class type: " + errorDataSource.getClass(), errorDataSource,
                        instanceOf(expectedErrorDataSource));
    }

    public static void dumpExceptionTree(PrintStream s, String indent, Throwable t) {
        synchronized (s) {
            s.printf("%s%s: %s%n", indent, t.getClass().getName(), t.getMessage());

            if (t instanceof ServiceInvocationException) {
                ServiceInvocationException sie = (ServiceInvocationException) t;
                Object err = sie.getErrorResponse();
                s.printf("%s* ServiceInvocationException.getErrorResponse()=%s%n", indent, asId(indent, err));
                s.printf("%s* .isAppOnlyException() == %s%n", indent, sie.isAppOnlyException());
            }
            else if (t instanceof ServiceException) {
                ServiceException se = (ServiceException) t;
                Object err = se.getErrorMessage();
                s.printf("%s* ServiceException.getErrorMessage()=%s%n", indent, asId(indent, err));
            }
            else if (t instanceof ServiceExceptionInterface) {
                ServiceExceptionInterface sei = (ServiceExceptionInterface) t;
                Object err = sei.getErrorMessage();
                s.printf("%s* ServiceExceptionInterface.getErrorMessage()=%s%n", indent, asId(indent, err));
            }

            for (StackTraceElement trace : t.getStackTrace()) {
                s.printf("%s    at %s%n", indent, trace);
            }
            Throwable cause = t.getCause();

            if (cause != null) {
                s.printf("%s  + [CAUSED BY] ...%n", indent);
                dumpExceptionTree(s, indent + "    ", cause);
            }
        }
    }

    private static String asId(String indent, Object err) {
        if (err == null) {
            return "<null>";
        }
        StringBuilder id = new StringBuilder();
        id.append("(").append(err.getClass().getName()).append("): ");

        if (err instanceof ErrorMessage) {
            ErrorMessage em = (ErrorMessage) err;
            List<CommonErrorData> errors = em.getError();
            id.append("Has ").append(errors.size()).append(" Error(s)");
            for (ErrorData ed : errors) {
                id.append("\n");
                id.append(indent);
                id.append("* ErrorData [errorId=").append(ed.getErrorId());
                id.append(", exceptionId=").append(ed.getExceptionId());
                id.append(", message=").append(ed.getMessage());
                id.append("]");
            }
        }
        else if (err instanceof CommonErrorData) {
            CommonErrorData ed = (CommonErrorData) err;
            id.append(indent);
            id.append("* CommonErrorData [errorId=").append(ed.getErrorId());
            id.append(", exceptionId=").append(ed.getExceptionId());
            id.append(", message=").append(ed.getMessage());
            id.append("]");
        }
        else if (err instanceof ErrorData) {
            ErrorData ed = (ErrorData) err;
            id.append("\n");
            id.append(indent);
            id.append("* ErrorData [errorId=").append(ed.getErrorId());
            id.append(", exceptionId=").append(ed.getExceptionId());
            id.append(", message=").append(ed.getMessage());
            id.append("]");
        }
        else {
            id.append(err);
        }

        return id.toString();
    }
}
