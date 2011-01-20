/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorParameter;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The utility class for exceptions.
 *
 */
public class ExceptionUtils {
	
	private static final String ERRORDATA_URL_PATTERN = "META-INF/errorlibrary/";
		
	/**
	 * Find the nested exception inside Throwable t and of type clz.
	 * 
	 * @param t the throwable to be searched by
	 * @param clz the class to be searched for
	 * @return the result, null if not found
	 */
	public static Throwable getCauseOfType(Throwable t, Class<?> clz) {
		Throwable cause = t.getCause();
		while (cause != null) {
			if (clz.isAssignableFrom(cause.getClass())) {
				return cause;
			}
			t = cause;
			cause = t.getCause();
		}
		return null;
	}
	
	/**
	 * 
	 * @param t the exception
	 * @return true if Throwable t is caused by client timeout exception.
	 */
	public static boolean isClientTimeoutException(Throwable t) {
		Throwable cause = ExceptionUtils.getRootCause(t);
		if (!(cause instanceof SocketException)) {
			return false;
		}
		String message = cause.getMessage();
		if (null == message) {
			return false;
		}
		if (message.contains("Software caused connection abort")) {
			return true;
		}
		if (message.contains("Connection reset by peer")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param t the exception
	 * @return Returns the non-null root cause of the given Throwable t.
	 */
	public static Throwable getRootCause(Throwable t) {
		Throwable cause = t.getCause();
		while (cause != null) {
			t = cause;
			cause = t.getCause();
		}
		return t;
	}
	
	/**
	 * Used by Property based EL.
	 * 
	 * @param domainName domain name of the error data
	 * @return the URL of the ErrorData.xml resource
	 */
	
	public static URL getErrordataXMLURL(String domainName){

		String errorDataUrl = ERRORDATA_URL_PATTERN  + domainName + "/ErrorData.xml";
		URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(errorDataUrl);
		return fileUrl;
	}
	
	/**
	 * 
	 * Used by Property based EL. Though we have Util methods for DOM parsing, 
	 * we need to make ErrorLibrary independent and hence we need to have this Util method
	 * 
	 * @param inputStream the input stream to be parsed
	 * @return the packageName for the domain represented by the XML Stream
	 */
	public static String getPackageNameFromXML(InputStream inputStream) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document result = null;
		String packagename = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			result = builder.parse(inputStream);
			Element docElement = result.getDocumentElement();
	        if (docElement != null) 	    		
	            packagename = docElement.getAttribute("packageName");	        
		} catch (Exception e) {
		}
        
		return packagename;
	}
	
	/**
	 * Load a class by given class name and class loader.
	 * 
	 * @param <T> The type of class to be loaded
	 * @param className the class name 
	 * @param targetType the class type name, for error reporting purpose only
	 * @param cl the class loader
	 * @return the class
	 */
	public static <T> Class<T> loadClass(String className, Class<T> targetType, ClassLoader cl)
		{
			String targetTypeName;
			if (targetType != null) {
				targetTypeName = targetType.getName();
			} else {
				targetTypeName = "(unspecified assignment type)";
			}

			Class clazz = null;
			try {
				clazz = Class.forName(className, true, cl);
			} catch (Exception exception) {
				Object[] arguments =  new Object[] {targetTypeName, className};
				throwServiceRuntimeException(ErrorLibraryBaseErrors.el_errorcollection_not_available, arguments, exception);
			}


			@SuppressWarnings("unchecked")
			Class<T> result = clazz;

			return result;
		}
	

	/** 
	 * Creates an instance of the given class.
	 * @param <T> the class type of the instance
	 * @param className the class name
	 * @param targetType the type of the class, for error reporting purpose only.
	 * @param cl the class loader
	 * @return the created instance
	 */
	public static <T> T createInstance(String className, Class<T> targetType, ClassLoader cl)
	{
		
		Class<T> clazz = loadClass(className, targetType, cl);
		
		Object result = null;
		try {
				result = clazz.newInstance();
		}  catch (Exception exception) {
			Object[] arguments =  new Object[] {clazz.getName()};
			throwServiceRuntimeException(ErrorLibraryBaseErrors.el_inst_exception, arguments, exception);
		}

		// type cast cannot be done, but we've checked isAssignableFrom
		@SuppressWarnings("unchecked")
		T result2 = (T)result;

		return result2;
	}
	
	/**
	 * Throws a service runtime exception of given error data and arguments.
	 * The arguments will be inserted into the error data at the predefined places.
	 * 
	 * @param errorData the error data
	 * @param arguments the arguments
	 */
	public static void throwServiceRuntimeException(CommonErrorData errorData, Object[] arguments){
		throwServiceRuntimeException(errorData, arguments, null);
	}

	/**
	 * Throws a service runtime exception of given error data and arguments.
	 * The arguments will be inserted into the error data at the predefined places.
	 * 
	 * @param errorData the error data
	 * @param arguments the arguments
	 * @param cause the root cause
	 */
	public static void throwServiceRuntimeException(CommonErrorData errorData, Object[] arguments, Throwable cause){
		CommonErrorData commonErrorData = cloneErrorData(null, errorData, arguments);
		ErrorLibraryBaseErrors.buildBootstrapErrorMessage(commonErrorData, arguments);
		
		throw new ServiceRuntimeException(commonErrorData, cause);
	}

	/**
	 * Clone the error data.
	 * 
	 * @param key the key of the error data, the cloned error name will be from the key
	 * @param errorData the error data to be copied
	 * @param params parameters to be set to the cloned the error data
	 * @return the cloned error data
	 */
	public static CommonErrorData cloneErrorData(ErrorDataKey key, CommonErrorData errorData, 
			Object[] params){
		return cloneErrorData(key, errorData, params, null);
	}
	
	/**
	 * Clone the error data.
	 * 
	* @param key if not null, containing error name to be set to the cloned error data 
	 * @param errorData the error data to be copied
	 * @param params parameters to be set to the cloned the error data
	 * @param errorName if key is null, this will be the error name of the cloned error data
	 * @return the cloned error data
	 */
	public static  CommonErrorData cloneErrorData(ErrorDataKey key, CommonErrorData errorData, Object[] params, 
			String errorName){
		Preconditions.checkNotNull(errorData);
		CommonErrorData commonErrorData = new CommonErrorData();
		commonErrorData.setErrorName(errorData.getErrorName());
		if(commonErrorData.getErrorName() == null)
			commonErrorData.setErrorName(errorName);
		if(commonErrorData.getErrorName() == null && key != null)
			commonErrorData.setErrorName(key.getErrorName());
		commonErrorData.setCategory(errorData.getCategory());
		commonErrorData.setDomain(errorData.getDomain());
		commonErrorData.setErrorId(errorData.getErrorId());
		commonErrorData.setExceptionId(errorData.getExceptionId());
		commonErrorData.setSeverity(errorData.getSeverity());
		commonErrorData.setSubdomain(errorData.getSubdomain());	
		commonErrorData.setOrganization(errorData.getOrganization());
		setErrorParameters(commonErrorData, params);
		if(errorData.getMessage() != null)
			commonErrorData.setMessage(errorData.getMessage());
		return commonErrorData;
	}
	
	private static void setErrorParameters(CommonErrorData commonErrorData, Object[] args ) {
		if ( args != null && args.length > 0 ) {
			for ( int i = 0; i < args.length; i++ ) {
				if(args[i] != null){
					ErrorParameter errorParameter = new ErrorParameter();
					errorParameter.setName( "Param" + (i+1) );
					errorParameter.setValue( args[i].toString() );
					commonErrorData.getParameter().add( errorParameter );
				}				
			}
		}
	}
	
	
}
