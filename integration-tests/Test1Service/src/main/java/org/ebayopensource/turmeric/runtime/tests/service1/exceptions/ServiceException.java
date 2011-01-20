/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.exceptions;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.ErrorDesc;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.LocalizableErrorData;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.LocalizableErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;


/**
 * Exception (either System or application) that can be thrown while processing a message.
 *
 * @author ichernyshev, smalladi
 */
@SuppressWarnings("deprecation")
public class ServiceException extends org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException implements ServiceExceptionInterface {

	public ServiceException(ErrorDesc errorDesc) {
		this(errorDesc, null, null);
	}

	public ServiceException(ErrorDesc errorDesc, Object[] params) {
		this(errorDesc, params, null);
	}


	public ServiceException(ErrorDesc errorDesc, Object[] params, Throwable cause)
	{
		this(new LocalizableErrorMessage(new LocalizableErrorData(errorDesc, params)),
			errorDesc.getSubcategory(), null, cause);
	}

	public ServiceException(CommonErrorData errorData) {
		this(errorData, null);
	}

	public ServiceException(List<CommonErrorData> errorData) {
		this(errorData, null);
	}

	public ServiceException(CommonErrorData errorData, Throwable cause)
	{
		this(new LocalizableErrorMessage(errorData), cause);
	}

	public ServiceException(List<CommonErrorData> errorData, Throwable cause)
	{
		this(new LocalizableErrorMessage(errorData), cause);
	}

	public ServiceException(LocalizableErrorMessage errorMessage, Throwable cause)
	{
		this(errorMessage, getErrorSubCategory(errorMessage), null, cause);
	}

	public ServiceException(LocalizableErrorMessage errorMessage, String defMessage, Throwable cause)
	{
		this(errorMessage, getErrorSubCategory(errorMessage), defMessage, cause);
	}

	private ServiceException(LocalizableErrorMessage errorMessage,
		ErrorSubcategory subcategory, String defMessage, Throwable cause)
	{
		super(LocalizableErrorMessage.getDefaultMessage(errorMessage, defMessage), cause);
	}

	private static final long serialVersionUID = 835486766282112209L;
	
	/**
	 * Need to come up with alternate logic in the long run 
	 */
	private static ErrorSubcategory getErrorSubCategory(LocalizableErrorMessage errorMessage){
		List<CommonErrorData> errorList = errorMessage.getError();
		String subDomain = null;
		if(errorList != null && !errorList.isEmpty() && errorList.get(0) != null){
			subDomain = errorList.get(0).getSubdomain();
		}
		if(subDomain == null)
			return null;
		return ErrorSubcategory.NAMETOERRORSUBCATEGORY.get(subDomain);
	}	
}
