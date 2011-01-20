/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleId;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.exceptions.AppErrorWrapperException;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ErrorMapper;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * @author rmurphy
 */
public class DefaultErrorMapperImpl implements ErrorMapper {

	private static final String DEFAULT_LOCALE_LANGUAGE = "en-US";
	private static final String IS_INTERNAL_CLIENT = "IS_INTERNAL_CLIENT";

	public void init(InitContext ctx) throws ServiceException {
		// noop
	}

	public Object mapErrors(List<Throwable> errors, ServerMessageContext ctx) throws ServiceException
	{
		// Call error handler only for error flow (i.e. errors present in fatal list).
		// In such a flow, there may be output params, but these would be from previous work by the request dispatcher.
		// Since output flow is no longer occurring, these output params must be discarded.
		// The error handler will write new output params reflecting the error message content.

		ErrorMessage result = new ErrorMessage();

		String localeLang = getLocaleLanguage(ctx);

		for (Throwable error : errors) {
			boolean isAppWrapper = false;
			if (error instanceof AppErrorWrapperException) {
				error = ((AppErrorWrapperException)error).getCause();
				isAppWrapper = true;
			}

			ServiceExceptionInterface exception;
			if (error instanceof ServiceExceptionInterface) {
				exception = (ServiceExceptionInterface)error;
			} else {
				if (isAppWrapper) {
					exception = new ServiceException(
							ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_APPLICATION_INTERNAL_ERROR,
							ErrorConstants.ERRORDOMAIN, new Object[] {error.toString()}), error);
				} else {
					exception = new ServiceException(
							ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_INTERNAL_ERROR,
							ErrorConstants.ERRORDOMAIN, new Object[] {error.toString()}), error);
				}
			}

			exception.localizeMessage(localeLang);

			ErrorMessage errorMessage2 = exception.getErrorMessage();
			if (errorMessage2 != null) {
				List<CommonErrorData> errorDataList = errorMessage2.getError();
				for (int i=0; i<errorDataList.size(); i++) {
					CommonErrorData errorData = errorDataList.get(i);

					boolean islocalTransport = ctx.getResponseMessage().getTransportProtocol().equals(SOAConstants.TRANSPORT_LOCAL);

					Boolean isInternalClient = (Boolean)ctx.getProperty(IS_INTERNAL_CLIENT);

					boolean isInternalClientOrLocalTransport = (isInternalClient != null?  isInternalClient.booleanValue() || islocalTransport : islocalTransport);

					if (isInternalClientOrLocalTransport) {
						errorData.setExceptionId(error.getClass().getName());
					}
					result.getError().add(errorData);
				}
			}
		}

		return result;
	}

	private String getLocaleLanguage(ServerMessageContext ctx) throws ServiceException {
		OutboundMessage responseMessage = (OutboundMessage)ctx.getResponseMessage();
		G11nOptions g11nOptions = responseMessage.getG11nOptions();
		if (g11nOptions == null) {
			return DEFAULT_LOCALE_LANGUAGE;
		}
		List<String> locales = g11nOptions.getLocales();
		if (locales == null || locales.isEmpty()) {
			return DEFAULT_LOCALE_LANGUAGE;
		}

		// Normally server logic reduces the locale list to the first one in the client's list that is supported
		// by the handling service.  In early stages of the logic, we have all the client's requested locales,
		// and we just try to use the first one specified.  If this does not have a resource bundle, we'll fall
		// back to the raw unlocalized form of the message (basically English with underscores).
		//
		String locale = locales.get(0);
		LocaleId localeId = LocaleId.valueOf(locale);
		return localeId.getLanguage();
	}
}
