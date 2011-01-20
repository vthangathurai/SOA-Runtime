/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.ErrorDesc;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.LocalizableErrorData;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.LocalizableErrorMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.CustomErrorMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.util.TestUtils;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;

import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * @author ichernyshev
 */
public class Test1ServiceException extends Exception implements ServiceExceptionInterface {

	private static final long serialVersionUID = 835486766282112209L;
	private final ErrorMessage m_errorMessage;

	public Test1ServiceException(ErrorDesc errorDesc) {
		this(errorDesc, null, null);
	}

	public Test1ServiceException(ErrorDesc errorDesc, Object[] params) {
		this(errorDesc, params, null);
	}

	public Test1ServiceException(ErrorDesc errorDesc, Object[] params, Throwable cause)
	{
		this(new LocalizableErrorData(errorDesc, params), cause);
	}

	public Test1ServiceException(CommonErrorData errorData)
	{
		this(errorData, null);
	}

	public Test1ServiceException(CommonErrorData errorData, Throwable cause)
	{
		this(new LocalizableErrorMessage(errorData), cause);
	}

	public Test1ServiceException(List<CommonErrorData> errorData, Throwable cause)
	{
		this(new LocalizableErrorMessage(errorData), cause);
	}

	public Test1ServiceException(ErrorMessage errorMessage) {
		this(errorMessage, null);
	}

	public Test1ServiceException(ErrorMessage errorMessage, Throwable cause)
	{
		super(LocalizableErrorMessage.getDefaultMessage(errorMessage, null), cause);
		m_errorMessage = errorMessage;
	}

	public Test1ServiceException(CustomErrorMessage errorMessage) {
		this(errorMessage.getError());
	}

	public Test1ServiceException(MyMessage errorMessage) {
		this(errorMessage.getError());
	}

	private Test1ServiceException(List<ErrorType> errorTypes) {
		this(TestUtils.errorTypeListToErrorMessage(errorTypes));
	}

	public final ErrorMessage getErrorMessage() {
		return m_errorMessage;
	}

	public final void localizeMessage(String locale) {
		if (m_errorMessage instanceof LocalizableErrorMessage) {
			((LocalizableErrorMessage)m_errorMessage).localizeMessages(locale);
		}
	}
}
