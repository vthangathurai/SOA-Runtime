/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.exceptions;

/**
 * This exception is the wrapper for all non-compliant IMPL exceptions.
 * 
 * Non-compliant IMPL exceptions are those not implementing ServiceExceptionInterface
 * 
 * @author ichernyshev
 */
public final class AppErrorWrapperException extends Exception {

	/**
	 * Constructor with application thrown exception as argument.
	 * @param appError application thrown exception.
	 */
	public AppErrorWrapperException(Throwable appError) {
		super(appError);
	}
	
	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = -3387516993124229948L;
}
