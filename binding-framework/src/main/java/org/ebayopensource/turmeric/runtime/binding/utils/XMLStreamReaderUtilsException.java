/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

/**
 * Generic Exception class for XMLStreamReaderUtils.
 * 
 * @author gyue
 */
public class XMLStreamReaderUtilsException extends Exception {

	/**
	 * Default constructor.
	 */
	public XMLStreamReaderUtilsException() {
		super();
	}
	
	/**
	 * Constructor with an error message.
	 * 
	 * @param message An error message.
	 */
	public XMLStreamReaderUtilsException(String message) {
		super(message);
	}

	/**
	 * Constructor with a Throwable.
	 * @param e a Throwable.
	 */
	public XMLStreamReaderUtilsException(Throwable e) {
		super(e);
	}

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
}
