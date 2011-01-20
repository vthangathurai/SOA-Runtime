/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.service;

import java.util.Locale;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ErrorHelper;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;


/**
 * Set of utilities that may be useful to service implementations.
 * 
 * 
 * @author stecheng
 *
 */
public class ServiceImplHelper {

	/**
	 * Retrieve a SOA error as a CommonErrorData.  CommonErrorData is the preferred ErrorData format.
	 * The locale of the CommonErrorData is implied within MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @return a CommonErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceException if was a problem finding the locale
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getCommonErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args ) throws ServiceException {
		return ErrorHelper.getCommonErrorData( key, args, getMessageContext() );
	}

	/**
	 * Retrieve a SOA error as a CommonErrorData.  CommonErrorData is the preferred ErrorData format.
	 * This API differs from {@link #getCommonErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, Object[])}
	 * in that it allows specification of a locale other than the one associated with the MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param locale desired Locale
	 * @return a CommonErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getCommonErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Locale locale ) {
		return ErrorHelper.getCommonErrorData( key, args, locale, getMessageContext() );
	}

	/**
	 * Retrieve a SOA error as an ErrorData.  This API was written for backwards compatibility for those legacy clients
	 * who only understand the original ErrorData format.  The locale of the ErrorData is implied within the MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @return an ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceException if was a problem finding the locale
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args ) throws ServiceException {
		return ErrorHelper.getErrorData( key, args, getMessageContext() );
	}
	
	/**
	 * Retrieve a SOA error as an ErrorData.  This API was written for backwards compatibility for those legacy clients
	 * who only understand the original ErrorData format.  This API differs from
	 * {@link #getErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, Object[])} by allowing specification
	 * of a locale other than the one associated with the MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param locale desired Locale
	 * @return an ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static CommonErrorData getErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Locale locale ) {
		return ErrorHelper.getErrorData( key, args, locale, getMessageContext() );
	}

	/**
	 * Retrieve a SOA error as "Custom" ErrorData.  "Custom" ErrorData are subclasses of CommonErrorData.  Use of a "Custom" ErrorData may
	 * be necessary if an application requires error state beyond what is provided by CommonErrorData.
	 * The locale of the "Custom" ErrorData is implied within MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param clazz Class reference to the "Custom" ErrorData
	 * @return a "Custom" ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceException if was a problem finding the locale
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz ) throws ServiceException {
		return ErrorHelper.getCustomErrorData( key, args, clazz, getMessageContext() );
	}

	/**
	 * Retrieve a SOA error as "Custom" ErrorData.  "Custom" ErrorData are subclasses of CommonErrorData.  Use of a "Custom" ErrorData may
	 * be necessary if an application requires error state beyond what is provided by CommonErrorData.
	 * This API differs from
	 * {@link #getCustomErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, Object[], Class)}
	 * by allowing specification of a locale other than the one associated with the MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param clazz Class reference to the "Custom" ErrorData
	 * @param locale desired Locale
	 * @return a "Custom" ErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 */
	public static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz, Locale locale ) {
		return ErrorHelper.getCustomErrorData( key, args, clazz, locale, getMessageContext() );
	}
	
	private static MessageContext getMessageContext() {
		return MessageContextAccessor.getContext();
	}
}
