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
 * Generic Security Exception that wraps exceptions throw during security check.
 * 
 * @author gyue
 */
public class SecurityException extends ServiceException {

	/**
	 * Constructor.
	 * @param errorData the error data of the exception.
	 */
	public SecurityException(CommonErrorData errorData) {
		super(errorData);
	}

	/**
	 * Constructor.
	 * @param errorData the error data of the exception.
	 * @param cause the cause of the exeption.
	 */
	public SecurityException(CommonErrorData errorData, Throwable cause) {
		super(errorData, cause);
	}

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
}
