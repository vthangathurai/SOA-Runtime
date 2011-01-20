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
 * DataValidationErrorException is thrown when an data validation error is detected during
 * serialization or deserialization.
 * 
 * @author wdeng
 *
 */
public class DataValidationErrorException extends BindingException {
	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 1L;
	private int m_row;
	private int m_col;
	private Severity m_severity;
	
	/**
     * Constructs a <code>DataValidationErrorException</code> with the specified severity,
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
	public DataValidationErrorException(Severity severity, int row, int col, String msg, Throwable t) {
		super(msg, t);
		m_row = row;
		m_col = col;
		m_severity = severity;
	}
	
	/**
	 * Returns the row number.
	 * @return the row number.
	 */
	public int getRow() {
		return m_row;
	}
	
	/**
	 * Returns the column number.
	 * @return the column number.
	 */
	public int getCol() {
		return m_col;
	}
	
	/**
	 * Returns the severity.
	 * @return the severity.
	 */
	public Severity getSeverity() {
		return m_severity;
	}
	
	/**
	 * Data validation error severity.
	 * 
	 * @author wdeng
	 *
	 */
	public enum Severity {
		/**
		 * Severity Warning.
		 */
		Warning, 	
		/**
		 * Severity Error.
		 */
		Error, 
		/**
		 * Severity Fatal.
		 */
		Fatal
	}
}
