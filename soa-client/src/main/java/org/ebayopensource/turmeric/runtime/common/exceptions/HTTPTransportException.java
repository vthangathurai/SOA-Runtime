/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;


import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;


/**
 * Exception reflecting HTTP error due to non-successful HTTP status code (e.g. 404, 500).
 * 
 */
public class HTTPTransportException extends TransportException {
	private int m_statusCode;

	/**
	 * 
	 * @param errorData  A CommonErrorData providing detail information to create a
	 * 		ServiceException
	 * @param statusCode Status code of the HTTP response.
	 * @param cause a Throwable providing the root cause.ss
	 */
	/**
	 * Constructor.
	 * @param errorData error data of the exception
	 * @param statusCode the status code of the exception
	 * @param cause the cause of the exception
	 */
	public HTTPTransportException(CommonErrorData errorData, int statusCode, Throwable cause) {
		super(errorData, cause);
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.exceptions.TransportException#getStatusCode()
	 */
	@Override
	public String getStatusCode() {
		return Integer.toString(m_statusCode);
	}

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
}
