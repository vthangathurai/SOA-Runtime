/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.exception;

/**
 * @author arajmony
 *
 */
public class BaseTypeLibraryException extends Exception {

	private static final long serialVersionUID = -2239190583762521813L;
	
	public BaseTypeLibraryException() {
	}

	public BaseTypeLibraryException(String msg) {
		super(msg);
	}

	public BaseTypeLibraryException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
