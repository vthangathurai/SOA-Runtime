/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ErrorHelper;

/**
 * <code>ErrorDataFactory</code> is provided a set of convinient methods for
 * service implementation and consumer to create <code>ErrorData</code> defined
 * in Error Libraries.
 * 
 * It is initialized by Turmeric runtime when the runtime starts. 
 * 
 * @author ana, wdeng
 *
 */
public final class ErrorDataFactory {
	
	private static ConcurrentMap<String, ErrorDataProvider> s_errorDataProviderMap = 
				new ConcurrentHashMap<String, ErrorDataProvider>();
	
	private static final String V4_ERROR_PROVIDER = "org.ebayopensource.turmeric.runtime.common.errors.V4ErrorDataProvider"; 
	
	private ErrorDataFactory(){		
	}
	
	/**
	 * Initializing ErrorDataFactory with the available Error Libraries for a given 
	 * Error Domain.
	 * 
	 * @param domainName The name of an error domain.
	 * @return A ErrorDataProvider for the given domain.
	 */
	public static ErrorDataProvider initialize(String domainName){
		ErrorDataProvider tempProvider = s_errorDataProviderMap.get(domainName);
		if(tempProvider == null){
			URL fileUrl = ExceptionUtils.getErrordataXMLURL(domainName);
			if(fileUrl != null){
				tempProvider = PropertyFileBasedErrorProvider.getInstance();
				s_errorDataProviderMap.put(domainName, tempProvider);
				ErrorUtils.initialize(domainName);
			}
			else{
				tempProvider = getV4ErrorProvider();
				if(tempProvider != null)
					s_errorDataProviderMap.put(domainName, tempProvider);
			}				
		}
		
		if(tempProvider == null){			

			Object[] arguments = new Object[] {domainName};
			ExceptionUtils.throwServiceRuntimeException(ErrorLibraryBaseErrors.el_no_error_data_provider, arguments);
		}
		return tempProvider;
	}
	
	/**
	 * This method creates A CommonErrorData using the error defined in an Error Library
	 * for the given domain with the given error name.
	 * @param errorName  The name of an error.
	 * @param domain The name of an error domain.
	 * @return A CommonErrorData
	 */
	public static CommonErrorData createErrorData(String errorName, String domain){
		return createErrorData(errorName, domain, null);
	}
	
	
	/**
	 * This method creates A CommonErrorData using the error defined in an Error Library
	 * for the given domain with the given error name, and with the given params Object array
	 * as ErrorParameters.
	 * 
	 * @param errorName  The name of an error.
	 * @param domain The name of an error domain.
	 * @param params An array of ErrorParameters.
	 * @return A CommonErrorData
	 */
	public static CommonErrorData createErrorData(String errorName, String domain, Object[] params){
		return createErrorData(errorName, null, domain, params);		
	}
	
	/**
	 * This method creates A CommonErrorData using the error defined in an Error Library
	 * for the given domain from a given Error Library with the given error name, and with 
	 * the given params Object array
	 * as ErrorParameters.
	 * 
	 * @param errorName  The name of an error.
	 * @param errorLibraryName The name of the Error Library the named error is resided.
	 * @param domain The name of an error domain.
	 * @param params An array of ErrorParameters.
	 * @return A CommonErrorData
	 */
	public static CommonErrorData createErrorData(String errorName, String errorLibraryName, String domain, Object[] params){
		return createErrorData(errorName, errorLibraryName, domain, params, null);	
	}
	
	/**
	 * This method creates A CommonErrorData using the error defined in an Error Library
	 * for the given domain from a given Error Library with the given error name, and with 
	 * the given params Object array
	 * as ErrorParameters.
	 * 
	 * @param errorName  The name of an error.
	 * @param errorLibraryName The name of the Error Library the named error is resided.
	 * @param domain The name of an error domain.
	 * @param params An array of ErrorParameters.
	 * @param locale A locale the error message should be generaed.
	 * @return A CommonErrorData
	 */
	public static CommonErrorData createErrorData(String errorName, String errorLibraryName, String domain, Object[] params, Locale locale){
		CommonErrorData errorData = null;
		ErrorDataProvider provider = s_errorDataProviderMap.get(domain);
		if(provider == null)
			provider = initialize(domain);
		
		if(provider instanceof PropertyFileBasedErrorProvider){
			errorData  = ErrorUtils.createErrorData(errorName, errorLibraryName, domain, params, locale);
		}else{
			ErrorDataKey errorDataKey = new ErrorDataKey(errorLibraryName, domain, errorName);
			errorData = ErrorHelper.getCommonErrorData(errorDataKey, params, locale, getMessageContext());
		}
		
		if(errorData == null){
			Object[] arguments = new Object[] {errorName, domain};
			ExceptionUtils.throwServiceRuntimeException(ErrorLibraryBaseErrors.el_no_such_error_defined, arguments);
		}
				
		return errorData;
	}
	
	private static MessageContext getMessageContext(){
		return MessageContextAccessor.getContext();
	}
	
	
	private static ErrorDataProvider getV4ErrorProvider(){
		
		ErrorDataProvider errorDataClass = null;
		try {
			errorDataClass = ExceptionUtils.createInstance(V4_ERROR_PROVIDER,ErrorDataProvider.class, 
					Thread.currentThread().getContextClassLoader());
		} catch (Exception exception) {
			errorDataClass = null;
		}
		return errorDataClass;
	}

}
