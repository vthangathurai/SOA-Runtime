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
 * Exception reflecting failures in the transport.
 */
public class TransportException extends ServiceException {

	/**
	 * Constructor.
	 * @param errorData error data of the exception
	 * @param cause cause of the exception
	 */
	public TransportException(CommonErrorData errorData, Throwable cause) {
		super(errorData, cause);
	}

	/**
	 * Gets the status code of the exception.
	 * @return the status code.
	 */
	public String getStatusCode() {
		return null;
	}

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
}
