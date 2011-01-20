/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;


import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorClassificationCodeType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorParameterType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.SeverityCodeType;


import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;



/**
 * @author wdeng
 */
public class TestUtils {
	public static final String TEST1_SERVICE_NAME = "test1";
	public static final QName TEST1_SERVICE_QNAME = new QName(SOAConstants.DEFAULT_SERVICE_NAMESPACE, TEST1_SERVICE_NAME);

	public static boolean equals(Object obj1, Object obj2) {
		if (null == obj1) {
			return null == obj2;
		}
		return obj1.equals(obj2);
	}

	public static List<ErrorType> errorMessageToErrorTypeList(ErrorMessage msg) {
		List<CommonErrorData> errorDataList = msg.getError();
		List<ErrorType> result = new ArrayList<ErrorType>();
		for (CommonErrorData errorData : errorDataList) {
			ErrorType errorType = errorDataToErrorType(errorData);
			result.add(errorType);
		}
		return result;
	}

	public static ErrorMessage errorTypeListToErrorMessage(
			List<ErrorType> errorTypeList) {
		List<CommonErrorData> errorDataList = new ArrayList<CommonErrorData>();
		for (ErrorType errorType : errorTypeList) {
			CommonErrorData errorData = errorTypeToErrorData(errorType);
			errorDataList.add(errorData);
		}
		ErrorMessage result = new ErrorMessage();
		result.getError().addAll(errorDataList);

		return result;
	}

	public static CommonErrorData errorTypeToErrorData(ErrorType errorType) {
		CommonErrorData errorData = new CommonErrorData();
		ErrorCategory category;
		ErrorClassificationCodeType classification = errorType
				.getErrorClassification();
		if (classification != null) {
			if (classification
					.equals(ErrorClassificationCodeType.REQUEST_ERROR)) {
				category = ErrorCategory.REQUEST;
			} else if (classification
					.equals(ErrorClassificationCodeType.SYSTEM_ERROR)) {
				category = ErrorCategory.SYSTEM;
			} else { // using CustomCode - hack
				category = ErrorCategory.APPLICATION;
			}
			errorData.setCategory(category);
		}
		if (errorType.getErrorCode() != null) {
			long errorCode = Long.valueOf(errorType.getErrorCode()).longValue();
			errorData.setErrorId(errorCode);
		}
		List<ErrorParameterType> params = errorType.getErrorParameters();
		if (params != null && !params.isEmpty()) {
			ErrorParameterType param = params.get(params.size() - 1);
			if (param.getParamID().equals("Exception")) {
				errorData.setExceptionId(param.getValue());
			}
		}
		errorData.setMessage(errorType.getLongMessage());
		SeverityCodeType severityCode = errorType.getSeverityCode();
		if (severityCode != null) {
			ErrorSeverity severity = (severityCode
					.equals(SeverityCodeType.ERROR) ? ErrorSeverity.ERROR
					: ErrorSeverity.WARNING);
			errorData.setSeverity(severity);
		}
		return errorData;
	}

	public static ErrorType errorDataToErrorType(CommonErrorData errorData) {
		ErrorType errorType = new ErrorType();
		ErrorClassificationCodeType classification;
		ErrorCategory category = errorData.getCategory();
		if (category.equals(ErrorCategory.REQUEST)) {
			classification = ErrorClassificationCodeType.REQUEST_ERROR;
		} else if (category.equals(ErrorCategory.SYSTEM)) {
			classification = ErrorClassificationCodeType.SYSTEM_ERROR;
		} else { // application
			classification = ErrorClassificationCodeType.CUSTOM_CODE;
		}
		errorType.setErrorClassification(classification);
		errorType.setErrorCode(String.valueOf(errorData.getErrorId()));
		errorType.setShortMessage(errorData.getMessage());
		errorType.setLongMessage(errorData.getMessage());
		SeverityCodeType severity = (errorData.getSeverity().equals(
				ErrorSeverity.ERROR) ? SeverityCodeType.ERROR
				: SeverityCodeType.WARNING);
		errorType.setSeverityCode(severity);
		String exception = errorData.getExceptionId();
		if (exception != null) {
			ErrorParameterType param = new ErrorParameterType();
			param.setParamID("Exception");
			param.setValue(exception);
			errorType.getErrorParameters().add(param);
		}
		// not supported: "real" parameters
		return errorType;
	}
}
