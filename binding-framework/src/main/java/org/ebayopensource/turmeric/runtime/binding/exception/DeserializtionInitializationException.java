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
 * DeserializtionInitializationException is thrown when an abnormal condition is detected during
 * deserialization initialization.
 * 
 * @author wdeng
 *
 */
public class DeserializtionInitializationException extends BindingException {
	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 0L;

	/**
     * Constructs a <code>DeserializtionInitializationException</code> without a detail error  message.
     */
	public DeserializtionInitializationException () {}

	/**
     * Constructs a <code>DeserializtionInitializationException</code> with the specified detail
     * message. The error message string <code>message</code> can later be
     * retrieved by the <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param   message   the detail message.
     */
	public DeserializtionInitializationException (String message) {
		super(message);
	}

	/**
     * Constructs a <code>DeserializtionInitializationException</code> with the specified detail
     * message and cause. Note that the detail message associated with cause 
	 * is not automatically incorporated into this exception's detail message.
     *
     * @param   message - The detail message (which is saved for later
	 * retrieval by the <code>{@link java.lang.Throwable#getMessage}</code> method).
	 *
	 * @param  t - The cause (which is saved for later retrieval by the 
	 * <code>{@link java.lang.Throwable#getCause}</code> method). 
	 * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
	public DeserializtionInitializationException (String message, Throwable t) {
		super(message, t);
	}

   /**
	* Constructs a DeserializtionInitializationException with the specified cause and a detail message of 
	* <code>(cause==null ? null : cause.toString()) </code>
	* (which typically contains the class and detail message of cause). 
	* This constructor is useful for DeserializtionInitializationException that are little more 
	* than wrappers for other throwables. 
	*
	* @param  t - The cause (which is saved for later retrieval by the 
	* <code>{@link java.lang.Throwable#getCause}</code> method). 
	* (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	*
	*/
	public DeserializtionInitializationException (Throwable t) {
		super(t);
	}
}
