/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.util.List;


import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;



/**
 * Runtime exception for the framework errors during invocation.
 * @author ichernyshev
 */
public final class ServiceInvocationRuntimeException extends ServiceRuntimeException
    implements ServiceInvocationExceptionInterface
{
    /*
     * The wrapped ServiceInvocationException
     */
    private final ServiceInvocationException invocationException;

    /**
     * @param e exception to be wrapped
     * @param cause the cause of the exception
     */
    public ServiceInvocationRuntimeException(ServiceInvocationException e, Throwable cause) {
        // ServiceInvocationException cannot have NULL m_errorMessage
        super(ErrorLibraryBaseErrors.getNewErrorMessage(e.getErrorMessage().getError()), e.getMessage(), cause);
        this.invocationException = e;
    }

    /**
     * Constructor retained for backward compatibility.
     * @param msg message of the exception
     * @param errorData error data of the exception
     * @param clientErrors client errors
     * @param errorResponse error response
     * @param isAppOnlyException true if it is an application only exception
     * @param applicationException application exception
     * @param requestGuid the GUID of the request
     * @param cause the cause of the exception
     * @deprecated
     */
    public ServiceInvocationRuntimeException(String msg,
            List<CommonErrorData> errorData, List<Throwable> clientErrors,
            Object errorResponse, boolean isAppOnlyException,
            Throwable applicationException, String requestGuid, Throwable cause) {
			super(ErrorLibraryBaseErrors.getNewErrorMessage(errorData), msg, cause);
	        this.invocationException = new ServiceInvocationException(msg, errorData,
                clientErrors, errorResponse, isAppOnlyException,
                applicationException, requestGuid, cause);
    }


    @Override
    public List<Throwable> getClientErrors() {
        return invocationException.getClientErrors();
    }

    @Override
    public Object getErrorResponse() {
        return invocationException.getErrorResponse();
    }

    @Override
    public boolean isAppOnlyException() {
        return invocationException.isAppOnlyException();
    }

    @Override
    public String getRequestGuid() {
        return invocationException.getRequestGuid();
    }

    @Override
    public Throwable getApplicationException() {
        return invocationException.getApplicationException();
    }
 
    /**
     * Checks of the exception happens at the client side.
     * @return true if at client side
     */
    public boolean isClientSide() {
        return invocationException.isClientSide();
    }

    /*
    * Uncomment once the QE has the time to verify this feature
    *
    @Override
    public String getMessage() {
        StringBuilder errorMessage = new StringBuilder();
        if (isAppOnlyException()) {
            errorMessage.append("Application Error - ");
        }
        else {
            if (isClientSide()) {
                errorMessage.append("SOA Client Runtime Error - ");
            } else {
                errorMessage.append("SOA Server Runtime Error - ");
            }
        }
        errorMessage.append(super.getMessage());
        return errorMessage.toString();
    }

    */
	/**
	 * serial version UID.
	 */
    static final long serialVersionUID = 835486766282112209L;
}
