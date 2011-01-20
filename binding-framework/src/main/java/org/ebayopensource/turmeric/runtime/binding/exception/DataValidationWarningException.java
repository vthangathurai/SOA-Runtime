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
package org.ebayopensource.turmeric.runtime.binding.exception;


/**
 * DataValidationWarningException is thrown when an data validation warning is detected during
 * serialization or deserialization.
 * 
 * @author wdeng
 *
 */
public class DataValidationWarningException extends DataValidationErrorException {
	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 1L;

	
	/**
     * Constructs a <code>DataValidationWarningException</code> with the specified severity,
     * row and column location, detail message, and the cause. The error message string 
     * <code>msg</code> can later be
     * retrieved by the <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
  	 * 
	 * @param severity  - The severity of the data validation error.
	 * @param row - approximate row number in the payload where the error occurs.
	 * @param col - approximate column number in the payload where the error occurs.
	 * @param msg - The detail error message.
	 * @param t - The cause (which is saved for later retrieval by the 
 	 * <code>{@link java.lang.Throwable#getCause}</code> method). 
	 * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 *
	 */
	public DataValidationWarningException(Severity severity, int row, int col, String msg, Throwable t) {
		super(severity, row, col, msg, t);
	}

}
