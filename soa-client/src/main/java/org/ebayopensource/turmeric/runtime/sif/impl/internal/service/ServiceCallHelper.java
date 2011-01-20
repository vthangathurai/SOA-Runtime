/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP11Fault;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP12Fault;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;
import org.ebayopensource.turmeric.runtime.sif.service.Service;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * @author ichernyshev
 */
public final class ServiceCallHelper {

	public static final Cookie[] EMPTY_COOKIES = new Cookie[0];

	public static ServiceInvocationException createInvocationException(ClientMessageContextImpl ctx,
		String adminName, String opName, List<Throwable> clientErrors, Object errorResponse,
		boolean isAppOnlyException, boolean hasServerSystemErrors, String requestGuid)
	{
		Throwable rootCause = null;
		List<CommonErrorData> errorData = new ArrayList<CommonErrorData>();

		// iterate client errors
		for (Throwable error: clientErrors) {
			if (rootCause == null) {
				// Remember the first throwable in case we have to use its text to construct
				// a makeshift ErrorData in block (B) below.  This is also placed as the
				// cause in the creation we finally create below.  Note: This may be inaccurate, because
				// the exception might just be a side exception that was thrown *after* the main
				// exception we're processing, e.g. a client response handler exception that simply
				// happened after getting a response with an application error.
				rootCause = error;
			}

			if (error instanceof ServiceExceptionInterface) {
				ServiceExceptionInterface error2 = (ServiceExceptionInterface)error;
				ErrorMessage errorMessage = error2.getErrorMessage();
				if (errorMessage != null) {
					List<CommonErrorData> errorMessageDataList = errorMessage.getError();
					errorData.addAll(errorMessageDataList);
				}
			}

			// we are not handling non-ServiceExceptionInterface exceptions other than keeping
			// the first one as the "root cause"
		}

		if (errorResponse instanceof ErrorMessage) {
			// Custom error message types presumably do
			// not have ErrorData, otherwise most likely the service writer would
			// have subclassed ErrorMessage and we'd still be using this logic here.
			ErrorMessage errorMessage = (ErrorMessage)errorResponse;
			List<CommonErrorData> errorMessageDataList = errorMessage.getError();
			errorData.addAll(errorMessageDataList);
		} else if (errorResponse instanceof SOAP11Fault) {
			Object detail = ((SOAP11Fault)errorResponse).getDetail();
			if (detail instanceof ErrorMessage) {
				ErrorMessage errorMessage = (ErrorMessage) detail;
				List<CommonErrorData> errorMessageDataList = errorMessage.getError();
				errorData.addAll(errorMessageDataList);
				errorResponse = errorMessage;
			}

		} else if (errorResponse instanceof SOAP12Fault) {
			Object detail = ((SOAP12Fault)errorResponse).getDetail();
			if (detail instanceof ErrorMessage) {
				ErrorMessage errorMessage = (ErrorMessage) detail;
				List<CommonErrorData> errorMessageDataList = errorMessage.getError();
				errorData.addAll(errorMessageDataList);
				errorResponse = errorMessage;
			}
		}

		String exceptionMessageText;
		if (errorData.isEmpty()) {
			// (B)
			// The error list is all Throwables which do not implement ServiceExceptionInterface.
			// Also, any ErrorResponse appearing in the list "errors" is not an instance of ErrorMessage -
			// it's some alien custom error response.
			// So, we have nothing to go on so far; the error information isn't SOA recognizable.
			// Goal of this block is to create an ErrorData that has at least something in it.  We do
			// that at (C).
			String causeText;
			String errorName;
			Long remoteErrorId = null;
			if (rootCause != null) {
				// since we do have some throwable to blame on the client side, use its exception.
				// this should not happen when any SEI-compliant exceptions are present,
				// so it's OK to provide default error ID
				errorName = ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_SYS_CLIENT;
				causeText = rootCause.toString();
			} else if (errorResponse != null) {
				// ErrorResponse is non-null.  Note:  Whenever this is true, in the current code, the
				// caller has already put the same ErrorResponse on the "errors" list.  So, we're not
				// likely to find anything new here.  We already know the error response isn't an instance of
				// ErrorMessage from the check above.  We'll just use the error response toString() always.

				if (errorResponse instanceof ErrorMessage) {
					// the error data list was absolutely empty... use generic info
					errorName = ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_APP;
					causeText = "Empty ErrorMessage " + errorResponse.toString();
				} else {
					if (hasServerSystemErrors) {
						errorName = ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_SYS_SERVER;
					} else {
						errorName = ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_APP;
					}

					causeText = getCustomResponseText(ctx, errorResponse);
					remoteErrorId = getCustomResponseId(ctx, errorResponse);

					if (causeText == null) {
						// using toString on JAXB-generated objects makes no sense,
						// but we have no other choice...
						causeText = errorResponse.toString();
					}
				}
			} else {
				// this is really impossible
				errorName = ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_SYS_CLIENT;
				causeText = "Unknown";

				getLogger().log(Level.SEVERE, "Unexpected internal call to createInvocationException " +
					"with no client-side errors and no error response");
			}

			// (C)
			// Create a makeshift ErrorData with as good a message string as we came up with above.

			CommonErrorData errorData1 = ErrorDataFactory.createErrorData(errorName, ErrorConstants.ERRORDOMAIN,
					new Object[] {adminName + "." + opName, causeText});


			if (remoteErrorId != null) {
				errorData1.setErrorId(remoteErrorId.longValue());
//				errorData2.setErrorId(remoteErrorId.longValue());
			}

			errorData.add(errorData1);
//			errorData.add(errorData2);
//			exceptionMessageText = errorData2.getMessage();
			exceptionMessageText = errorData1.getMessage();
		} else {
			CommonErrorData seriousErrorData = findErrorData(errorData, ErrorCategory.SYSTEM);
			if (seriousErrorData == null) {
				seriousErrorData = errorData.get(0);
			}

			exceptionMessageText = seriousErrorData.getMessage();
		}

		Throwable applicationException = decodeApplicationException(ctx, errorResponse);

		// We could pass an index here - the first system error in the ErrorData list.
		return new ServiceInvocationException(exceptionMessageText,
			errorData, clientErrors, errorResponse,
			isAppOnlyException, applicationException, requestGuid, rootCause);
	}

	public static <T> Map<String, T> combineMaps(Map<String, T> sessionMap,
			Map<String, T> reqMap) {
		Map<String, T> result = null;

		if (sessionMap != null && !sessionMap.isEmpty()) {
			result = new HashMap<String, T>();
			result.putAll(sessionMap);
		}

		if (reqMap != null && !reqMap.isEmpty()) {
			if (result == null) {
				result = new HashMap<String, T>();
			}
			result.putAll(reqMap);
		}

		return result;
	}

	public static <T> Collection<T> combineCollections(
			Collection<T> sessionCollection, Collection<T> reqCollection) {
		Collection<T> result = null;

		if (sessionCollection != null && !sessionCollection.isEmpty()) {
			result = new ArrayList<T>();
			result.addAll(sessionCollection);
		}

		if (reqCollection != null && !reqCollection.isEmpty()) {
			if (result == null) {
				result = new ArrayList<T>();
			}
			result.addAll(reqCollection);
		}

		return result;
	}

	public static void checkMarkdownError(ClientMessageContextImpl context, Throwable e) {
		if (context == null) {
			return;
		}

		try {
			SOAClientMarkdownStateManager.getInstance().countError(
					context, e);
		} catch (Throwable e2) {
			getLogger().log(
					Level.SEVERE,
					"Unable to call countError for "
							+ context.getServiceDesc().getAdminName() + ": "
							+ e2.toString(), e2);
		}
	}

	public static boolean hasSystemErrorInResponse(ClientMessageContextImpl context, Object errorResponse) {
		if (!(errorResponse instanceof ErrorMessage)) {
			ErrorResponseAdapter responseAdapter = context
					.getServiceContext().getCustomErrorResponseAdapter();

			if (responseAdapter == null) {
				// we cannot process this response type, assume it's all
				// application errors
				return false;
			}

			try {
				Boolean result = responseAdapter.hasSystemErrors(errorResponse);
				return (result != null ? result.booleanValue() : false);
			} catch (Throwable e) {
				getLogger()
						.log(
								Level.SEVERE,
								"ErrorResponseAdapter '"
										+ responseAdapter.getClass().getName()
										+ "' threw unexpected error in hasSystemErrors "
										+ e.toString(), e);
				return false;
			}
		}

		ErrorMessage msg = (ErrorMessage) errorResponse;
		List<CommonErrorData> errors = msg.getError();

		// try to find first application error
		for (CommonErrorData errorData : errors) {
			if (errorData != null) {
				if (errorData.getCategory() != ErrorCategory.APPLICATION) {
					return true;
				}
			}
		}

		return false;
	}

	private static CommonErrorData findErrorData(List<CommonErrorData> errors, ErrorCategory category)
	{
		// try to find first error with the given category
		for (CommonErrorData errorData: errors) {
			if (errorData != null) {
				if (errorData.getCategory() == category) {
					return errorData;
				}
			}
		}
		return null;
	}

	private static String getCustomResponseText(ClientMessageContextImpl ctx, Object errorResponse) {
		ErrorResponseAdapter responseAdapter = ctx.getServiceContext().getCustomErrorResponseAdapter();

		if (responseAdapter == null) {
			// we cannot process this response type, assume it's all application errors
			return null;
		}

		try {
			return responseAdapter.getErrorText(errorResponse);
		} catch (Throwable e) {
			getLogger().log(Level.SEVERE, "ErrorResponseAdapter '" + responseAdapter.getClass().getName() +
				"' threw unexpected error in getErrorText " + e.toString(), e);
		}

		return null;
	}

	private static Long getCustomResponseId(ClientMessageContextImpl ctx, Object errorResponse) {
		ErrorResponseAdapter responseAdapter = ctx.getServiceContext().getCustomErrorResponseAdapter();

		if (responseAdapter == null) {
			// we cannot process this response type, use generic error id
			return null;
		}

		try {
			return responseAdapter.getErrorId(errorResponse);
		} catch (Throwable e) {
			getLogger().log(Level.SEVERE, "ErrorResponseAdapter '" + responseAdapter.getClass().getName() +
				"' threw unexpected error in getErrorId " + e.toString(), e);
		}

		return null;
	}

	/**
	 * Default internal function to reconstruct an application-specific exception from the error response
	 *
	 * This method matches operation exception names against the name specified in the first ErrorData
	 * of the error response. If there is a match, the corresponding exception is constructed using
	 * a constructor taking the error response class (e.g. ErrorMessage), or taking String
	 *
	 * If successful, the reconstructed exception is returned; otherwise it returns null
	 */
	private static Throwable decodeApplicationException(ClientMessageContextImpl ctx, Object errorResponse) {
		if (errorResponse == null) {
			return null;
		}

		ServiceOperationDescImpl operation = ctx.getOperation();

		// try to get existing helper data
		ServiceErrorHelperData errorHelperData = (ServiceErrorHelperData)operation.getErrorHelperData();
		if (errorHelperData != null) {
			if (!errorHelperData.hasData()) {
				// nothing was not found before...
				return null;
			}
		}

		// try to create helper data if it was not found
		if (errorHelperData == null) {
			try {
				errorHelperData = buildErrorHelperData(ctx.getServiceDesc(), operation.getName());
			} catch (Throwable e) {
				getLogger().log(Level.SEVERE, "Unexpected exception building error helper data for " +
					ctx.getAdminName() + "." + operation.getName() + ": " + e.toString(), e);
				errorHelperData = new ServiceErrorHelperData(null);
			}

			if (errorHelperData == null) {
				// could not build
				return null;
			}

			operation.setErrorHelperData(errorHelperData);
			if (!errorHelperData.hasData()) {
				// nothing was not found...
				return null;
			}
		}

		// get exception name and text from error response
		String exceptionName;
		String errorText;
		if (errorResponse instanceof ErrorMessage) {
			ErrorMessage errorResponse2 = (ErrorMessage)errorResponse;
			CommonErrorData errorData = findErrorData(errorResponse2.getError(), ErrorCategory.APPLICATION);
			if (errorData == null) {
				return null;
			}

			exceptionName = errorData.getExceptionId();
			errorText = errorData.getMessage();
			errorResponse = errorData;
		} else {
			ErrorResponseAdapter responseAdapter = ctx.getServiceContext().getCustomErrorResponseAdapter();

			if (responseAdapter == null) {
				// cannot reconstruct app error
				return null;
			}

			try {
				exceptionName = responseAdapter.getExceptionClassName(errorResponse);
			} catch (Throwable e) {
				exceptionName = null;
				getLogger().log(Level.SEVERE, "ErrorResponseAdapter '" + responseAdapter.getClass().getName() +
					"' threw unexpected error in getExceptionClassName " + e.toString(), e);
			}

			if (exceptionName == null) {
				// unknown error response, no appropriate exception, or error...
				return null;
			}

			errorText = getCustomResponseText(ctx, errorResponse);
		}

		// look up that exception name in error helper data
		ServiceExceptionInfo exceptionInfo = errorHelperData.m_exceptions.get(exceptionName);
		if (exceptionInfo == null) {
			// unknown exception name
			getLogger().log(Level.WARNING, "Operation " + ctx.getAdminName() + "." + operation.getName() +
				" returned an undeclared exception " + exceptionName);

			// TODO: allow exception's subclasses here
			return null;
		}

		// try to find some constructor that takes errorResponse or its base class
		Class responseType = errorResponse.getClass();

		while (responseType != Object.class) {
			Constructor constructor = exceptionInfo.m_simpleConstructors.get(responseType);
			if (constructor != null) {
				Object[] paramValues = new Object[] { errorResponse };
				return constructException(constructor, paramValues);
			}
			// try finding constructor that takes Super class
			responseType = responseType.getSuperclass();
		}


		// try to find some constructor that takes String
		Constructor constructor = exceptionInfo.m_simpleConstructors.get(String.class);
		if (constructor != null) {
			Object[] paramValues = new Object[] {errorText};
			return constructException(constructor, paramValues);
		}

		// no good constructor
		// TODO: do we want to use the default one???

		getLogger().log(Level.WARNING, "No valid constructor found to instantiate exception " +
			exceptionName);

		return null;
	}

	private static Throwable constructException(Constructor constructor, Object[] params) {
		Throwable result;
		try {
			result = (Throwable)constructor.newInstance(params);
		} catch (Exception ex) {
			result = null;

			getLogger().log(Level.WARNING, "Failed to instantiate exception class: " +
				constructor.getDeclaringClass().getName(), ex);
		}
		return result;
	}

	private static ServiceErrorHelperData buildErrorHelperData(ClientServiceDesc serviceDesc, String opName)
	{
		Class intfClass = serviceDesc.getServiceInterfaceClass();
		if (intfClass == null) {
			return new ServiceErrorHelperData(null);
		}

		Method method = getMethod(intfClass, opName);
		if (method == null) {
			getLogger().log(Level.SEVERE, "Unable to find operation " + opName +
				" on service interface " + intfClass.getName());

			return new ServiceErrorHelperData(null);
		}

		ServiceErrorHelperData result = new ServiceErrorHelperData(method);

		Class[] exceptions = method.getExceptionTypes();
		for (int i=0; i<exceptions.length; i++) {
			Class exceptionClass = exceptions[i];
			ServiceExceptionInfo info = buildExceptionInfo(exceptionClass);
			result.m_exceptions.put(exceptionClass.getName(), info);
		}

		return result;
	}

	private static ServiceExceptionInfo buildExceptionInfo(Class exceptionClass) {
		ServiceExceptionInfo result = new ServiceExceptionInfo(exceptionClass);

		Constructor[] constructors = exceptionClass.getConstructors();
		for (int i=0; i<constructors.length; i++) {
			Constructor constructor = constructors[i];
			Class[] params = constructor.getParameterTypes();
			if (params.length == 0) {
				//result.m_defConstructor = constructor;
				continue;
			}
			if (params.length == 1) {
				result.m_simpleConstructors.put(params[0], constructor);
				continue;
			}
			// we do not support complex constructors
		}

		return result;
	}

	private static Method getMethod(Class clazz, String name) {
		Method[] methods = clazz.getMethods();
		if (methods == null) {
			return null;
		}

		for (int i=0; i<methods.length; i++) {
			Method method = methods[i];
			if (name.equals(method.getName())) {
				return method;
			}
		}

		return null;
	}

	private ServiceCallHelper() {
		// no instances
	}

	private static Logger getLogger() {
		return LogManager.getInstance(Service.class);
	}

	private static class ServiceErrorHelperData {
		final Map<String,ServiceExceptionInfo> m_exceptions =
			new HashMap<String,ServiceExceptionInfo>();

		ServiceErrorHelperData(Method method) {
			// no impl
		}

		boolean hasData() {
			return !m_exceptions.isEmpty();
		}
	}

	private static class ServiceExceptionInfo {
		final Map<Class,Constructor> m_simpleConstructors =
			new HashMap<Class,Constructor>();
		//Constructor m_defConstructor;

		ServiceExceptionInfo(Class clazz) {
			// no impl
		}
	}

	/**
	 * Retrieve a SOA error as a CommonErrorData.  CommonErrorData is the preferred ErrorData format.
	 * The locale of the CommonErrorData is implied within MessageContext.
	 *
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param ctx MessageContext
	 * @return a CommonErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceException if was a problem finding the locale
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getCommonErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, MessageContext ctx ) throws ServiceException {
		return ErrorHelper.getCommonErrorData( key, args, ctx );
	}

	/**
	 * Retrieve a SOA error as a CommonErrorData.  CommonErrorData is the preferred ErrorData format.
	 * This API differs from
	 * {@link #getCommonErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, Object[], MessageContext)}
	 * in that it allows specification of a locale other than the one associated with the MessageContext.
	 *
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param ctx MessageContext
	 * @param locale desired Locale
	 * @return a CommonErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getCommonErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, MessageContext ctx, Locale locale ) {
		return ErrorHelper.getCommonErrorData( key, args, locale, ctx );
	}

	/**
	 * Retrieve a SOA error as an ErrorData.  This API was written for backwards compatibility for those legacy clients
	 * who only understand the original ErrorData format.  The locale of the ErrorData is implied within the MessageContext.
	 *
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param ctx MessageContext
	 * @return an ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceException if was a problem finding the locale
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, MessageContext ctx ) throws ServiceException {
		return ErrorHelper.getErrorData( key, args, ctx );
	}

	/**
	 * Retrieve a SOA error as an ErrorData.  This API was written for backwards compatibility for those legacy clients
	 * who only understand the original ErrorData format.  This API differs from
	 * {@link #getErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, Object[], MessageContext)}
	 * by allowing specification of a locale other than the one associated with the MessageContext.
	 *
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param ctx MessageContext
	 * @param locale desired Locale
	 * @return an ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, MessageContext ctx, Locale locale ) {
		return ErrorHelper.getErrorData( key, args, locale, ctx );
	}

	/**
	 * Retrieve a SOA error as "Custom" ErrorData.  "Custom" ErrorData are subclasses of CommonErrorData.  Use of a "Custom" ErrorData may
	 * be necessary if an application requires error state beyond what is provided by CommonErrorData.
	 * The locale of the "Custom" ErrorData is implied within MessageContext.
	 *
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param clazz Class reference to the "Custom" ErrorData
	 * @param ctx MessageContext
	 * @return a "Custom" ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceException if was a problem finding the locale
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz, MessageContext ctx )
	throws ServiceException {
		return ErrorHelper.getCustomErrorData( key, args, clazz, ctx );
	}

	/**
	 * Retrieve a SOA error as "Custom" ErrorData.  "Custom" ErrorData are subclasses of CommonErrorData.  Use of a "Custom" ErrorData may
	 * be necessary if an application requires error state beyond what is provided by CommonErrorData.
	 * This API differs from
	 * {@link #getCustomErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, Object[], Class, MessageContext)}
	 * by allowing specification of a locale other than the one associated with the MessageContext.
	 *
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param clazz Class reference to the "Custom" ErrorData
	 * @param ctx MessageContext
	 * @param locale desired Locale
	 * @return a "Custom" ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz, MessageContext ctx, Locale locale ) {
		return ErrorHelper.getCustomErrorData( key, args, clazz, locale, ctx );
	}
}
