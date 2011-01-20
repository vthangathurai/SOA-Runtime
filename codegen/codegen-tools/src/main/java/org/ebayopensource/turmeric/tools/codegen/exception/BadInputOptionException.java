/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.exception;

public class BadInputOptionException extends BaseCodeGenException {

	private static final long serialVersionUID = 2873770386289965308L;

	public BadInputOptionException() {
		super();
	}

	public BadInputOptionException(String msg) {
		super(msg);
	}

	public BadInputOptionException(String msg, Throwable cause) {
		super(msg, cause);
	}


}
