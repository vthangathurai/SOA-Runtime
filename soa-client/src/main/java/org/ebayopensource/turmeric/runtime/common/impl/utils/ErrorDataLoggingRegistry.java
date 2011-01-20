/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.ebayopensource.turmeric.common.v1.types.ErrorData;


/**
 * 
 * @author stecheng
 *
 */
public class ErrorDataLoggingRegistry {
	
	private static ErrorDataLoggingRegistry INSTANCE = new ErrorDataLoggingRegistry();
	private ErrorDataLoggingRegistry() { /** empty */ }
	private Map<ErrorData,String> messageMap = Collections.synchronizedMap( new WeakHashMap<ErrorData,String>() );
	private Map<ErrorData,String> resolutionMap = Collections.synchronizedMap( new WeakHashMap<ErrorData,String>() );
	
	public static ErrorDataLoggingRegistry getInstance() { return INSTANCE; }

	public String getEnglishMessage( ErrorData errorData ) {
		return messageMap.get( errorData );
	}
	public String getEnglishResolution( ErrorData errorData ) {
		return resolutionMap.get( errorData );
	}
	
	public void update( ErrorData errorData, String message, String resolution ) {
		messageMap.put( errorData, message );
		resolutionMap.put( errorData, resolution );
	}
	
	public void remove( ErrorData errorData ) {
		messageMap.remove( errorData );
		resolutionMap.remove( errorData );
	}
}
