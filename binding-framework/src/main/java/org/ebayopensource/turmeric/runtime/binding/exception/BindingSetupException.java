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
 * BindingSetupException is thrown if there is an abnormal condition during binding framework initialization.
 * 
 * @author wdeng
 *
 */
public class BindingSetupException extends BindingException {
	/**
	 * serial version UID.
	 */
	 static final long serialVersionUID = 1L;

/**
	* Constructs a BindingSetupException with the specified cause and a detail message of 
	* <code>(cause==null ? null : cause.toString()) </code>
	* (which typically contains the class and detail message of cause). 
	* This constructor is useful for BindingSetupException that are little more 
	* than wrappers for other throwables. 
	*
	* @param  t - The cause (which is saved for later retrieval by the 
	* <code>{@link java.lang.Throwable#getCause}</code> method). 
	* (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	*
	*/
	public BindingSetupException(Throwable t) {
		super(t);
	}
}
