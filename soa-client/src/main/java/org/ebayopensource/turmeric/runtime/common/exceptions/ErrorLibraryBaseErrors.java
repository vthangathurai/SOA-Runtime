/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;


/**
 * 
 * Bootstrap errors for ErrorLibrary.
 * 
 */
public class ErrorLibraryBaseErrors {

	private final static String ORGANIZATION = "eBay";

	private final static String DOMAIN = "TurmericRuntime";

	private final static String SUBDOMAIN = "ErrorLibrary";
	
	/*
	 * Error messages for the ErrorLibrray bootstrap errors that are not locale specific
	 */

	private final static String EL_INITIALIZATION_FAILED_MESSAGE = "Initialization of property based ErrorLibrary " +
						"failed for the domain \"{0}\"";

	private final static String EL_NO_SUCH_ERROR_DEFINED_MESSAGE = "\"{0}\" No such error defined in the " +
						"collection for the domain \"{1}\"";

	private final static String EL_VALIDATION_FAILED_MESSAGE = "ResourceBundle of property based EL for the domain " +
						"\"{0}\" is inconsistent. Error<locale>.properties does not contain all the errors defined " +
						"in the corresponding ErrorData.xml.";

	private final static String EL_ERRORCOLLECTION_NOT_AVAILABLE_MESSAGE = "Unable to instantiate {0} with class {1}. " +
						"Class not found";
	
	private final static String EL_INST_EXCEPTION_MESSAGE = "Unable to instantiate class {0}. " +
						"Instantiation exception"; 
	
	private final static String EL_NO_ERROR_DATA_PROVIDER_MESSAGE = "Error Data Provider is not " +
						"configured for domain {0}"; 
	
	private final static String EL_IO_ERROR_MESSAGE = "I/O error reading file: {0}"; 
		
		

	/**
	 * Initialization failed.
	 */
	public final static CommonErrorData el_initialization_failed = createCommonErrorData(
			13000L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_initialization_failed", EL_INITIALIZATION_FAILED_MESSAGE,
			DOMAIN, SUBDOMAIN, null);

	/**
	 * validation failed.
	 */
	public final static CommonErrorData el_validation_failed = createCommonErrorData(
			13001L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_validation_failed", EL_VALIDATION_FAILED_MESSAGE, DOMAIN,
			SUBDOMAIN, null);

	/**
	 * error not defined.
	 */
	public final static CommonErrorData el_no_such_error_defined = createCommonErrorData(
			13002L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_no_such_error_defined", EL_NO_SUCH_ERROR_DEFINED_MESSAGE,
			DOMAIN, SUBDOMAIN, null);

	/**
	 * Error collection not available.
	 */
	public final static CommonErrorData el_errorcollection_not_available = createCommonErrorData(
			13003L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_errorcollection_not_available", EL_ERRORCOLLECTION_NOT_AVAILABLE_MESSAGE, 
			DOMAIN, SUBDOMAIN, (null));
	
	/**
	 * Installation error.
	 */
	public final static CommonErrorData el_inst_exception = createCommonErrorData(
			13004L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_inst_exception", EL_INST_EXCEPTION_MESSAGE, 
			DOMAIN, SUBDOMAIN, (null));
	
	/**
	 * No-error-data error.
	 */
	public final static CommonErrorData el_no_error_data_provider = createCommonErrorData(
			13005L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_no_error_data_provider", EL_NO_ERROR_DATA_PROVIDER_MESSAGE, 
			DOMAIN, SUBDOMAIN, (null));

	/**
	 * IO error.
	 */
	public final static CommonErrorData el_io_error = createCommonErrorData(
			13006L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM),
			"el_io_error", EL_IO_ERROR_MESSAGE, 
			DOMAIN, SUBDOMAIN, (null));

	
	private static CommonErrorData createCommonErrorData(long errorId,
			ErrorSeverity severity, ErrorCategory category, String errorName,
			String message, String domain, String subDomain, String errorGroup) {
		CommonErrorData errorData = new CommonErrorData();
		errorData.setErrorId(errorId);
		errorData.setSeverity(severity);
		errorData.setCategory(category);
		errorData.setSubdomain(subDomain);
		errorData.setDomain(domain);
		errorData.setMessage(message);
		errorData.setErrorGroups(errorGroup);
		errorData.setErrorName(errorName);
		errorData.setOrganization(ORGANIZATION);
		return errorData;
	}
	
	/**
	 * Insert the parameters into the errer data.
	 * @param errorData contains the message as the template 
	 * @param params parameters to be added to the message
	 */
	public static void buildBootstrapErrorMessage(CommonErrorData errorData, Object[] params){
		String formattedMessage = null;
		if(errorData.getMessage() != null)
			formattedMessage = MessageFormat.format(errorData.getMessage(), params);
		if(formattedMessage != null)
			errorData.setMessage(formattedMessage);
	}

	/**
	 * Returns the stored message string that is most descriptive of the specified ErrorMessage; either the
	 * message string of the first system-category ErrorData added if present (since system errors are more
	 * severe than request or application errors), or the first ErrorData added, otherwise.
	 * @param msg the ErrorMessage for which to return a message string.
	 * @param forceMessage if not null, the this message will be returned as default.
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
	 * Creates a new error message containing the error data.
	 * @param errorData the error data
	 * @return newly created error message
	 */
	public static ErrorMessage getNewErrorMessage(CommonErrorData errorData){
		List<CommonErrorData> errorDataList = new ArrayList<CommonErrorData>();
		errorDataList.add(errorData);
		return getNewErrorMessage(errorDataList); 
	}
	
	/**
	 * Creates a new error message containing the error data.
	 * @param errorData a list of error data
	 * @return newly created error message
	 */
	public static ErrorMessage getNewErrorMessage(List<CommonErrorData> errorData){
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.getError().addAll(errorData);
		
		return errorMessage;
	}
}
