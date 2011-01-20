/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.errors;

import java.util.ArrayList;
import java.util.List;


import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * Derived class of ErrorMessage (the default wire format error message within the SOA Framework).
 * LocalizableErrorMessage holds pre-localized error information within a ServiceException,
 * prior to the error mapping process which will perform localization and transfer to the pre-serialized error response
 * (wire) object.  Most LocalizableErrorMessages contain LocalizableErrorData, which is a unit of error data for
 * a particular exception event.
 *
 * @author ichernyshev
 * @deprecated Replaced by ErrorLibrary
 */
public class LocalizableErrorMessage extends ErrorMessage {
	/**
	 * Constructs a LocalizableErrorMessage with the specified ErrorData
	 * (already localized, or LocalizableErrorData)
	 */
	public LocalizableErrorMessage(CommonErrorData errorData) {
		error = new ArrayList<CommonErrorData>();
		error.add(errorData);
		validate();
	}

	/**
	 * Constructs a LocalizableErrorMessage with an list of ErrorData
	 */
	public LocalizableErrorMessage(List<CommonErrorData> errorData) {
		error = new ArrayList<CommonErrorData>(errorData);
		validate();
	}

	/**
	 * Returns the number of ErrorData in this LocalizableErrorMessage.
	 * @return
	 */
	public int size() {
		return (error != null ? error.size() : 0);
	}

	private void validate() {
		if (error.isEmpty()) {
			throw new IllegalArgumentException("ErrorDataList cannot be empty");
		}

		for (int i=0; i<error.size(); i++) {
			CommonErrorData errorData = error.get(i);
			if (errorData == null) {
				// an ErrorData cannot be NULL
				throw new NullPointerException("ErrorData at " + i + " is NULL");
			}
		}
	}

	public static String getDefaultMessage(ErrorMessage msg) {
		return getDefaultMessage(msg, null);
	}

	/**
	 * Returns the stored message string that is most descriptive of the specified ErrorMessage; either the
	 * message string of the first system-category ErrorData added if present (since system errors are more
	 * severe than request or application errors), or the first ErrorData added, otherwise.
	 * @param msg the ErrorMessage for which to return a message string.
	 * @return the message string; this is a pre-localized string if the ErrorData is itself pre-localized.
	 */
	public static String getDefaultMessage(ErrorMessage msg, String forceMessage) {
		if (forceMessage != null) {
			return forceMessage;
		}

		if (msg == null) {
			return null;
		}

		List<CommonErrorData> errorDataList = msg.getError();
		if (errorDataList.isEmpty()) {
			return null;
		}

		CommonErrorData firstError = errorDataList.get(0);
		return firstError.getMessage();
	}

	/**
	 * Localize all ErrorData in this message.  For those error data that are of the form LocalizableErrorData,
	 * the localizeMessage() method will be called on these to finalize the localization.
	 * @param locale the locale in which to perform localization.
	 */
	public void localizeMessages(String locale) {
		for (int i=0; i<error.size(); i++) {
			CommonErrorData errorData = error.get(i);

			if (errorData instanceof LocalizableErrorData) {
				LocalizableErrorData localizableErrorType = (LocalizableErrorData)errorData;
				localizableErrorType.localizeMessage(locale);
			}
		}
	}
}
