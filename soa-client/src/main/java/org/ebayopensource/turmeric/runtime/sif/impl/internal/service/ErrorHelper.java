/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MetricsRegistrationHelper;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

public class ErrorHelper {

	private static Logger LOGGER = Logger.getInstance(ErrorHelper.class);
	
	private static final Locale DEFAULT_LOCALE = Locale.US;
	
	/**
	 * Retrieve a SOA error as a CommonErrorData.  CommonErrorData is the preferred ErrorData format.
	 * The locale of the CommonErrorData is implied within MessageContext.
	 * 
	 * @param key specifies the bundle and errorname to retrieve
	 * @param args placeholder arguments to pass onto the localizable message and resolution
	 * @param ctx MessageContext
	 * @return a CommonErrorData that corresponds to the bundle and errorname specified.
	 * @throws NullPointerException if a validation error occurred -- if key is null, key.getBundle() is null, or key.getErrorName is null
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 * @throws ServiceException if was a problem finding the locale
	 */
	public static CommonErrorData getCommonErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, MessageContext ctx ) throws ServiceException {
		Locale locale = getLocale( ctx.getResponseMessage().getG11nOptions() );
		ErrorDataProvider errorDataProvider = ctx.getErrorDataProvider();
		CommonErrorData errorData = getCommonErrorData( errorDataProvider, key, args, locale );
		processErrorGroups( errorData, ctx );
		return errorData;
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
	public static CommonErrorData getCommonErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Locale locale, MessageContext ctx ){
		try {
			ErrorDataProvider errorDataProvider = ctx.getErrorDataProvider();
			CommonErrorData errorData = getCommonErrorData( errorDataProvider, key, args, locale );
			processErrorGroups( errorData, ctx );
			return errorData;
		} catch ( ServiceException e ) {
			throw ServiceRuntimeException.wrap( e );
		}
	}
	
	private static CommonErrorData getCommonErrorData( ErrorDataProvider errorDataProvider, ErrorDataProvider.ErrorDataKey key, Object[] args, Locale locale ) {
		if ( errorDataProvider == null )
			throw new ServiceRuntimeException( ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_ERROR_DATA_PROVIDER,
					ErrorConstants.ERRORDOMAIN));
		return errorDataProvider.getCommonErrorData( key, args, locale );
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
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 * @throws ServiceException if was a problem finding the locale
	 */
	public static CommonErrorData getErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, MessageContext ctx ) throws ServiceException {
		Locale locale = getLocale( ctx.getResponseMessage().getG11nOptions() );
		ErrorDataProvider errorDataProvider = ctx.getErrorDataProvider();
		return getErrorData( errorDataProvider, key, args, locale );
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
	public static CommonErrorData getErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Locale locale, MessageContext ctx ) {
		try {
			ErrorDataProvider errorDataProvider = ctx.getErrorDataProvider();
			return getErrorData( errorDataProvider, key, args, locale );
		} catch ( ServiceException e ) {
			throw ServiceRuntimeException.wrap( e );
		}
	}

	private static CommonErrorData getErrorData( ErrorDataProvider errorDataProvider, ErrorDataProvider.ErrorDataKey key, Object[] args, Locale locale ) { 
		if ( errorDataProvider == null )
			throw new ServiceRuntimeException( ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_ERROR_DATA_PROVIDER,
					ErrorConstants.ERRORDOMAIN));
		return errorDataProvider.getCommonErrorData( key, args, locale );
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
	 * @throws ServiceRuntimeException if no error could be found or no error data provider was configured
	 * @throws ServiceException if was a problem finding the locale
	 */
	public static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz, MessageContext ctx )
	throws ServiceException {
		Locale locale = getLocale( ctx.getResponseMessage().getG11nOptions() );
		ErrorDataProvider errorDataProvider = ctx.getErrorDataProvider();
		T errorData = getCustomErrorData( errorDataProvider, key, args, clazz, locale );
		processErrorGroups( errorData, ctx );
		return errorData; 
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
	public static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz, Locale locale, MessageContext ctx ) {
		try {
			ErrorDataProvider errorDataProvider = ctx.getErrorDataProvider();
			T errorData = getCustomErrorData( errorDataProvider, key, args, clazz, locale );
			processErrorGroups( errorData, ctx );
			return errorData;
		} catch ( ServiceException e ) {
			throw ServiceRuntimeException.wrap( e );
		}
	}

	private static <T extends CommonErrorData> T getCustomErrorData( ErrorDataProvider errorDataProvider, ErrorDataProvider.ErrorDataKey key, Object[] args, Class<T> clazz, Locale locale ) { 
		if ( errorDataProvider == null )
			throw new ServiceRuntimeException( ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_ERROR_DATA_PROVIDER,
					ErrorConstants.ERRORDOMAIN));
		return errorDataProvider.getCustomErrorData( key, args, clazz, locale );
	}
	
	private static Locale getLocale( G11nOptions g11nOptions ) {
		if (g11nOptions == null) {
			return DEFAULT_LOCALE;
		}
		List<String> locales = g11nOptions.getLocales();
		if (locales == null || locales.isEmpty()) {
			return DEFAULT_LOCALE;
		}

		// Normally server logic reduces the locale list to the first one in the client's list that is supported
		// by the handling service.  In early stages of the logic, we have all the client's requested locales,
		// and we just try to use the first one specified.  If this does not have a resource bundle, we'll fall
		// back to the raw unlocalized form of the message (basically English with underscores).
		//
		Locale locale;
		String localeString = locales.get(0);
		int offset = localeString.indexOf( '-' );
		if ( offset > 0 ) {
			String language = localeString.substring( 0, offset );
			String country = localeString.substring( offset+1 );
			locale = new Locale( language, country );
		} else {
			/**
			 * This condition would be unexpected.  Just take the locale string as-is then.
			 */
			locale = new Locale( localeString );
		}
		return locale;
	}
	
	public static void processErrorGroups( CommonErrorData errorData, MessageContext ctx ) {
		if ( errorData.getErrorGroups() != null && errorData.getErrorGroups().trim().length() > 0 ) {
			MetricsRegistrationHelper.registerMetricsForErrorDataGrps
			( errorData, errorData.getOrganization(), errorData.getErrorGroups(), ctx );
		}
		
		/**
		 * Error groups will not be sent out on the wire
		 */
		errorData.setErrorGroups( null );
	}
	
	public static void main( String[] args ) {
		List<String> localeStrings = new ArrayList<String>();
		localeStrings.add( "fr-CA" );
		localeStrings.add( "fr-FR" );
		localeStrings.add( "en-US" );
		localeStrings.add( "es-US" );
		localeStrings.add( "zh-Hans" );
		G11nOptions g11nOptions = new G11nOptions( G11nOptions.DEFAULT_CHARSET, localeStrings );
		Locale locale = getLocale( g11nOptions );
		if ( Locale.CANADA_FRENCH.equals( locale ) )
			if ( LOGGER.isLogEnabled( LogLevel.INFO ) && LOGGER.isInfoEnabled() )
				LOGGER.log( LogLevel.INFO, locale.toString() );
		
//		CommonErrorData errorData = new CommonErrorData();
//		errorData.getErrorGroup().add( "foo" );
//		new ErrorDataGroupEliminator().eliminateErrorGroup( errorData );
//		System.out.println( errorData.getErrorGroup() );
	
	}
}
