/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.exception;

public class MissingInputOptionException extends BaseCodeGenException {

	private static final long serialVersionUID = 9109990816784897308L;

	public MissingInputOptionException() {
		super();
	}

	public MissingInputOptionException(String msg) {
		super(msg);
	}

	public MissingInputOptionException(String msg, Throwable cause) {
		super(msg, cause);
	}


}
