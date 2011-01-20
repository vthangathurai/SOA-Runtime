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

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * Runtime exception for the framework errors.
 * @author ichernyshev
 */
public class ServiceRuntimeException extends RuntimeException implements ServiceExceptionInterface {

	private final transient ErrorMessage m_errorMessage;
	private transient ErrorSubcategory m_subcategory;

	/**
	 * Constructor.
	 * @param errorData error data of the exception
	 */
	public ServiceRuntimeException(CommonErrorData errorData) {
		this(errorData, null);
	}

	/**
	 * Constructor.
	 * @param errorData list of error data of the exception
	 */
	public ServiceRuntimeException(List<CommonErrorData> errorData) {
		this(errorData, null);
	}

	/**
	 * @param serviceException the service exception to be wrapped
	 * @return the wrapped exception
	 */
	public static RuntimeException wrap(ServiceException serviceException) {
		List<CommonErrorData> errors = serviceException.getErrorMessage().getError();
		if (errors.isEmpty()) {
			return new RuntimeException("ServiceException without error data: " +
				serviceException.toString(), serviceException);
		}

		ErrorSubcategory subcategory = serviceException.getSubcategory();
		return new ServiceRuntimeException(ErrorLibraryBaseErrors.getNewErrorMessage(errors),
			subcategory, serviceException.getMessage(), serviceException);
	}

	/**
	 * Constructor.
	 * @param errorData the error data of the exception
	 * @param cause the cause of the exception
	 */
	public ServiceRuntimeException(CommonErrorData errorData, Throwable cause)
	{
		this(ErrorLibraryBaseErrors.getNewErrorMessage(errorData), cause);
	}

	/**
	 * Constructor.
	 * @param errorData the list of error data of the exception
	 * @param cause the cause of the exception
	 */
	public ServiceRuntimeException(List<CommonErrorData> errorData, Throwable cause)
	{
		this(ErrorLibraryBaseErrors.getNewErrorMessage(errorData), cause);
	}

	/**
	 * Constructor.
	 * @param errorMessage the error message of the exception
	 * @param cause the cause of the exception
	 */
	public ServiceRuntimeException(ErrorMessage errorMessage, Throwable cause) {
		this(errorMessage, null, null, cause);
	}

	/**
	 * Constructor.
	 * @param errorMessage the error message of the exception
	 * @param defMessage the default error message
	 * @param cause the cause of the exception
	 */
	public ServiceRuntimeException(ErrorMessage errorMessage,
		String defMessage, Throwable cause)
	{
		this(errorMessage, null, defMessage, cause);
	}

	private ServiceRuntimeException(ErrorMessage errorMessage,
		ErrorSubcategory subcategory, String defMessage, Throwable cause)
	{
		super(ErrorLibraryBaseErrors.getDefaultMessage(errorMessage, defMessage), cause);
		m_errorMessage = errorMessage;
		m_subcategory = subcategory;
	}

	@Override
	public final ErrorMessage getErrorMessage() {
		return m_errorMessage;
	}

	/**
	 * Gets the sub-category.
	 * @return sub-category
	 */
	public final ErrorSubcategory getSubcategory() {
		return m_subcategory;
	}
 
	/**
	 * Removes the sub category.
	 */
	public final void eraseSubcategory() {
		m_subcategory = null;
	}

	@Override
	public final void localizeMessage(String locale) {}

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
}
