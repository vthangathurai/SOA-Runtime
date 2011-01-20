/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 *
 */
package org.ebayopensource.turmeric.runtime.common.exceptions;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;




/**
 * This exception is thrown to report problems during schema extraction.
 *  
 * @author arajmony
 *
 */
public class SchemaExtractionException extends ServiceException {


	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 5894484006683320727L;

	/**
	 * 
	 * @param message The error message.
	 */
	public SchemaExtractionException(String message) {
		this(getErrorData(message), null);
	}

	/**
	 * 
	 * @param message The error message.
	 * @param arg1 The Throwable that is the cause of this exception.
	 */
	public SchemaExtractionException(String message, Throwable arg1) {
		this(getErrorData(message), arg1);
	}

	/**
	 * 
	 * @param errorData  The CommonErrorData structure to be reported.
	 */
	public SchemaExtractionException(CommonErrorData errorData) {
		this(errorData, null);
	}

	/**
	 * 
	 * @param errorData  The CommonErrorData structure to be reported.
	 * @param cause  The Throwable that is the cause of this exception.
	 */
	public SchemaExtractionException(CommonErrorData errorData, Throwable cause) {
		super(errorData, cause);
	}

	private static CommonErrorData getErrorData(String message){
		CommonErrorData error = ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNABLE_TO_EXTRACT_SCHEMA,
				ErrorConstants.ERRORDOMAIN, new Object[]{message});
		return error;
	}
}
