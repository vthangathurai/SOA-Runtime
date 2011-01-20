/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorParameter;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;

/**
 * @deprecated
 * This class is replaced by ErrorDataFactory.
 * 
 * @author ana, wdeng
 *
 */
public class ErrorUtils {
	
	private static final Locale DEFAULT_LOCALE = Locale.US;
	private static PropertyFileBasedErrorProvider s_errorProvider = PropertyFileBasedErrorProvider.getInstance();
	
	/**
	 * @deprecated
	 * @param errorName The name of a CommonErrorData.
	 * @param domain The domain of a CommonErrorData.
	 * @return A CommonErrorData for the given error domain and error name.
	 */
	public static CommonErrorData createErrorData(String errorName, String domain){
		return createErrorData(errorName, domain, null);
	}
	
	/**
	 * @deprecated
	 * @param errorName The name of a CommonErrorData.
	 * @param domain The domain of a CommonErrorData.
	 * @param params An array of ErrorParameters.
	 * @return A CommonErrorData for the given error domain and error name.
	 */
	public static CommonErrorData createErrorData(String errorName, String domain, Object[] params){
		return createErrorData(errorName, null, domain, params);		
	}
	
	/**
	 * @deprecated
	 * @param errorName The name of a CommonErrorData.
	 * @param errorLibraryName The name of the Error Library hosting the CommonErrorData.
	 * @param domain The domain of a CommonErrorData.
	 * @param params An array of ErrorParameters.
	 * @return A CommonErrorData for the given error domain and error name.
	 */
	public static CommonErrorData createErrorData(String errorName, String errorLibraryName, String domain, Object[] params){
		return createErrorData(errorName, errorLibraryName, domain, params, null);	
	}
	
	
	/**
	 * @deprecated
	 * @param errorName The name of a CommonErrorData.
	 * @param errorLibraryName The name of the Error Library hosting the CommonErrorData.
	 * @param domain The domain of a CommonErrorData.
	 * @param params An array of ErrorParameters.
	 * @param locale A locale the error message should be generaed.
	 * @return A CommonErrorData for the given error domain and error name.
	 */
	public static CommonErrorData createErrorData(String errorName, String errorLibraryName, String domain, Object[] params, Locale locale){
		Locale locale2 = (locale == null) ? getLocale() : locale;		
		ErrorDataKey errorDataKey = new ErrorDataKey(errorLibraryName, domain, errorName);
		CommonErrorData errorData = s_errorProvider.getCommonErrorData(errorDataKey, params, locale2);
				
		return errorData;
	}
	
	/**
	 * @deprecated
	 * @param domain  An error domain.
	 */
	public static void initialize(String domain){
		s_errorProvider.initialize(domain);
	}
	
	/**
	 * @deprecated
	 * @param errorData An CommonErrorData
	 * @param locale a locale.
	 */
	public static void localizeMessage(CommonErrorData errorData, String locale) {
			s_errorProvider.buildMessageAndResolution(errorData, locale, getParamList(errorData));			
	}
	
	private static Object[] getParamList(CommonErrorData errorData){
		List<String> paramList = new ArrayList<String>();
		List<ErrorParameter> errorParameterList = errorData.getParameter();
		for (ErrorParameter errorParameter : errorParameterList) {
			paramList.add(errorParameter.getValue());
		}
		return paramList.toArray();
	}
	
	private static Locale getLocale() {
		
		MessageContext ctx = MessageContextAccessor.getContext();
		G11nOptions g11nOptions = null;
		if(ctx != null && ctx.getResponseMessage() != null){
			try {
				g11nOptions = ctx.getResponseMessage().getG11nOptions();
			} catch (ServiceException e) {
				// No op
			}
		}
		
		if (g11nOptions == null) {
			return DEFAULT_LOCALE;
		}
		List<String> locales = g11nOptions.getLocales();
		if (locales == null || locales.isEmpty()) {
			return DEFAULT_LOCALE;
		}
		
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

}
