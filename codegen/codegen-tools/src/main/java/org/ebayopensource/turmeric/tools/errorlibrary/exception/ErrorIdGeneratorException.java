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


public class ErrorIdGeneratorException extends Exception {
	
    static final long serialVersionUID = 8044075932804729914L;
	
	public ErrorIdGeneratorException(){
		super();
	}
	
	public ErrorIdGeneratorException( String message ) {
		super( message );
	}
	public ErrorIdGeneratorException( String message, Throwable cause ) {
		super( message, cause );
	}
}
