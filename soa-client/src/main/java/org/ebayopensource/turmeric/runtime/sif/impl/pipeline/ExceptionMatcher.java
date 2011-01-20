/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.TransportException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * @author rmurphy, ichernyshev
 */
public class ExceptionMatcher {

	private final ClientServiceId m_svcId;
	private final String m_compName;
	private final Set<String> m_transportCodes;
	private final Set<String> m_exceptions;
	private final Set<Long> m_errorIds;
	private final Set<ErrorSubcategory> m_errorSubcategories;

	public ExceptionMatcher(ClientServiceId svcId, String compName,
		Collection<String> transportCodes,
		Collection<String> exceptions,
		Collection<String> errorIds,
		Collection<String> excludedTransportCodes,
		Collection<String> excludedExceptions,
		Collection<String> excludedErrorIds)
		throws ServiceException
	{
		if (svcId == null || compName == null) {
			throw new NullPointerException();
		}

		m_svcId = svcId;
		m_compName = compName;

		m_transportCodes = new HashSet<String>();
		if (transportCodes != null) {
			for (String name: transportCodes) {
				if (name.length() == 0) {
					continue;
				}

				name = name.toUpperCase();
				if (!checkExcludedId(name, excludedTransportCodes)) {
					continue;
				}

				m_transportCodes.add(name);
			}
		}

		m_exceptions = new HashSet<String>();
		if (exceptions != null) {
			for (String name: exceptions) {
				if (name.length() == 0) {
					continue;
				}

				if (!checkExcludedId(name, excludedExceptions)) {
					continue;
				}

				m_exceptions.add(name);
			}
		}

		m_errorIds = new HashSet<Long>();
		m_errorSubcategories = new HashSet<ErrorSubcategory>();
		if (errorIds != null) {
			for (String name: errorIds) {
				if (name.length() == 0) {
					continue;
				}

				name = name.toUpperCase();
				if (!checkExcludedId(name, excludedErrorIds)) {
					continue;
				}

				if (addSpecialErrorId(name)) {
					continue;
				}

				long errorId;
				try {
					errorId = Long.parseLong(name);
				} catch (NumberFormatException e) {
					throw new ServiceException(ErrorDataFactory.createErrorData(
							ErrorConstants.SVC_FACTORY_INVALID_ERROR_LIST,
							ErrorConstants.ERRORDOMAIN, new Object[] {svcId, name}));
				}

				m_errorIds.add(Long.valueOf(errorId));
			}
		}
	}

	private boolean checkExcludedId(String name, Collection<String> excludeList) {
		if (excludeList == null || !excludeList.contains(name)) {
			return true;
		}

		LogManager.getInstance(ExceptionMatcher.class).log(Level.SEVERE,
			m_compName + " in service " + m_svcId.getAdminName() +
			" refers disallowed error " + name + ". This error will not be honored");

		return false;
	}

	private boolean addSpecialErrorId(String name) {
		if ("COMM".equals(name)) {
			m_errorSubcategories.add(ErrorSubcategory.TRANSPORT_RECEIVE);
			m_errorSubcategories.add(ErrorSubcategory.TRANSPORT_SEND);
			return true;
		}

		if ("COMM_CONNECT".equals(name)) {
			m_errorIds.add(Long.valueOf(ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_CONNECT_EXCEPTION, 
					ErrorConstants.ERRORDOMAIN).getErrorId()));
			m_errorIds.add(Long.valueOf(ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_CONNECT_TIMEOUT_EXCEPTION, 
					ErrorConstants.ERRORDOMAIN).getErrorId()));
			m_errorIds.add(Long.valueOf(ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_COMM_FAILURE, 
					ErrorConstants.ERRORDOMAIN).getErrorId()));
			return true;
		}

		if ("COMM_RECV".equals(name)) {
			m_errorSubcategories.add(ErrorSubcategory.TRANSPORT_RECEIVE);
			return true;
		}

		if ("COMM_SEND".equals(name)) {
			m_errorSubcategories.add(ErrorSubcategory.TRANSPORT_SEND);
			return true;
		}

		return false;
	}

	public String getMatchingError(ClientMessageContext ctx, Throwable exception) {
		if (exception instanceof ServiceInvocationException) {
			ServiceInvocationException invException = (ServiceInvocationException) exception;

			// Check client-side errors
			for (Throwable clientException : invException.getClientErrors()) {
				String result = getMatchingTransportException(clientException);
				if (result != null) {
					return result;
				}

				result = getMatchingException(clientException, true);
				if (result != null) {
					return result;
				}
			}

			// test the application exception embedded in the exception
			Throwable applicationException = invException.getApplicationException();
			if (applicationException != null) {
				String result = getMatchingException(applicationException, true);
				if (result != null) {
					return result;
				}
			}

			// check error response
			Object errorResponse = invException.getErrorResponse();
			if (errorResponse != null) {
				String result = getMatchingResponse(ctx, errorResponse);
				if (result != null) {
					return result;
				}
			}

			return getMatchingException(exception, false);
		}

		// For exceptions coming directly up from the framework, see if these are in the list.
		// This is a rarer use case, since most exceptions come as part of a ServiceInvocationException.
		String result = getMatchingException(exception, true);
		if (result != null) {
			return result;
		}

		return null;
	}

	/**
	 * Returns true if any of the original exceptions (usually in ErrorData inside the message)
	 * are matching this list
	 */
	protected String getMatchingResponse(ClientMessageContext ctx, Object errorResponse) {

		if (errorResponse instanceof ErrorMessage) {
			List<CommonErrorData> errorDataList = ((ErrorMessage) errorResponse).getError();
			for (CommonErrorData errorData : errorDataList) {
				String exceptionName = errorData.getExceptionId();
				Long errorId = Long.valueOf(errorData.getErrorId());
				String result = getMatchingError(exceptionName, errorId);
				if (result != null) {
					return result;
				}
			}

			return null;
		}

		// this is not a standard error message, try custom response handler
		if (ctx != null) {
			ClientServiceContext svcCtx = ctx.getServiceContext();
			ErrorResponseAdapter responseAdapter = svcCtx.getCustomErrorResponseAdapter();

			if (responseAdapter != null) {
				String exceptionName = null;
				try {
					exceptionName = responseAdapter.getExceptionClassName(errorResponse);
				} catch (Throwable e) {
					getLogger().log(Level.SEVERE, "ErrorResponseAdapter '" + responseAdapter.getClass().getName() +
						"' threw unexpected error in getExceptionClassName " + e.toString(), e);
				}

				Long errorId = null;
				try {
					errorId = responseAdapter.getErrorId(errorResponse);
				} catch (Throwable e) {
					getLogger().log(Level.SEVERE, "ErrorResponseAdapter '" + responseAdapter.getClass().getName() +
							"' threw unexpected error in getErrorId " + e.toString(), e);
				}

				String result = getMatchingError(exceptionName, errorId);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	/**
	 * Returns true if the exception is listed in the exception list
	 */
	protected String getMatchingException(Throwable exception, boolean checkExceptionClass) {
		// This does a naming test only. It would be a good feature to test that the incoming exception
		// is an instance of the named exception, i.e. the incoming exception might be a derived class.
		// To test this, we'd have to instantiate all the named exceptions, which is cumbersome,
		// so we'll leave that as an exercise for the service writer

		Long errorId = null;
		if (exception instanceof ServiceExceptionInterface) {
			ServiceExceptionInterface ex2 = (ServiceExceptionInterface)exception;
			ErrorMessage msg = ex2.getErrorMessage();
			if (msg != null) {
				List<CommonErrorData> errorDatas = msg.getError();
				if (errorDatas != null && !errorDatas.isEmpty()) {
					CommonErrorData errorData = errorDatas.get(0);
					if (errorData != null) {
						errorId = Long.valueOf(errorData.getErrorId());
					}
				}
			}
		}

		if (checkExceptionClass) {
			String className = exception.getClass().getName();
			return getMatchingError(className, errorId);
		}

		return getMatchingError(null, errorId);
	}

	/**
	 * Returns true if an individual error (normally an ErrorData in the ErrorMessage)
	 * matches based on either the configured exception list, or the configured error ID list
	 */
	protected String getMatchingError(String exceptionName, Long errorId) {
		if (exceptionName != null && m_exceptions.contains(exceptionName)) {
			return exceptionName;
		}

		if (errorId != null && m_errorIds.contains(errorId)) {
			return "Err" + errorId.toString();
		}

		return null;
	}

	/**
	 * Returns true if the exception is a TransportException whose error code is listed in
	 * the transport code list
	 */
	protected String getMatchingTransportException(Throwable exception) {
		if (!(exception instanceof TransportException)) {
			return null;
		}

		TransportException transportException = (TransportException)exception;
		String code = transportException.getStatusCode();
		if (code != null) {
			code = code.toUpperCase();
			if (m_transportCodes.contains(code)) {
				return "Transport" + code;
			}
		}

		return null;
	}

	private Logger getLogger() {
		return LogManager.getInstance(ExceptionMatcher.class);
	}
}
