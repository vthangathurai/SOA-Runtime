/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.errorlibrary.exception;

/**
 * @author arajmony
 *
 */
public class ErrorIDFileLockedException extends Exception {
	
    static final long serialVersionUID = 811316294515843261L;
    
	public ErrorIDFileLockedException() {
	}

	public ErrorIDFileLockedException(String msg) {
		super(msg);
	}

	public ErrorIDFileLockedException(String msg, Throwable cause) {
		super(msg, cause);
	}


}
