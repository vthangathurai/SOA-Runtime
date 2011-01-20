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
package org.ebayopensource.turmeric.tools.library.exception;

/**
 * @author arajmony
 *
 */
public class PopulateRegistryException extends BaseTypeLibraryException {

	private    static final long serialVersionUID = 6334256062074653747L;
	
	public PopulateRegistryException() {
	}

	public PopulateRegistryException(String msg) {
		super(msg);
	}

	public PopulateRegistryException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
