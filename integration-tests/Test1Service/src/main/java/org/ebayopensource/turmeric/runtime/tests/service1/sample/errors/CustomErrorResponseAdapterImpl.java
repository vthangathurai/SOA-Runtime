/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.errors;

import java.util.List;

import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.CustomErrorMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


/**
 * @author ichernyshev
 */
public class CustomErrorResponseAdapterImpl implements ErrorResponseAdapter {

	public void init(InitContext ctx) {
	}

	public String getErrorText(Object errorResponse) {
		List<ErrorType> errorTypes = getErrorTypes(errorResponse);
		if (errorTypes == null || errorTypes.isEmpty()) {
			return null;
		}

		return errorTypes.get(0).getLongMessage();
	}

	public String getExceptionClassName(Object errorResponse) {
		ErrorType errorType = getApplicationError(errorResponse);
		if (errorType == null) {
			return null;
		}

		List<ErrorParameterType> params = errorType.getErrorParameters();
		if (params != null && !params.isEmpty()) {
			ErrorParameterType param = params.get(params.size()-1);
			if (param.getParamID().equals("Exception")) {
				return param.getValue();
			}
		}

		return null;
	}

	public Long getErrorId(Object errorResponse) {
		List<ErrorType> errorTypes = getErrorTypes(errorResponse);
		if (errorTypes == null || errorTypes.isEmpty()) {
			return null;
		}

		String errorCode = errorTypes.get(0).getErrorCode();
		if (errorCode == null) {
			return null;
		}

		return Long.parseLong(errorCode);
	}

	public Boolean hasSystemErrors(Object errorResponse) {
		List<ErrorType> errorTypes = getErrorTypes(errorResponse);
		if (errorTypes == null || errorTypes.isEmpty()) {
			return null;
		}

		// try to find first application error
		for (ErrorType errorData: errorTypes) {
			if (errorData == null) {
				continue;
			}

			ErrorClassificationCodeType classification = errorData.getErrorClassification();
			if (classification == null) {
				continue;
			}

			if (classification.equals(ErrorClassificationCodeType.SYSTEM_ERROR)) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	private List<ErrorType> getErrorTypes(Object errorResponse) {
		if (errorResponse instanceof CustomErrorMessage) {
			CustomErrorMessage customErrorMessage = (CustomErrorMessage) errorResponse;
			return customErrorMessage.getError();
		}

		if (errorResponse instanceof MyMessage) {
			MyMessage msg = (MyMessage) errorResponse;
			return msg.getError();
		}

		return null;
	}

	private ErrorType getApplicationError(Object errorResponse) {
		List<ErrorType> errorTypes = getErrorTypes(errorResponse);
		if (errorTypes == null || errorTypes.isEmpty()) {
			return null;
		}

		// try to find first application error
		for (ErrorType errorData: errorTypes) {
			if (errorData == null) {
				continue;
			}

			ErrorClassificationCodeType classification = errorData.getErrorClassification();
			if (classification == null) {
				continue;
			}

			if (!classification.equals(ErrorClassificationCodeType.REQUEST_ERROR) &&
				!classification.equals(ErrorClassificationCodeType.SYSTEM_ERROR))
			{
				return errorData;
			}
		}

		return null;
	}
}
