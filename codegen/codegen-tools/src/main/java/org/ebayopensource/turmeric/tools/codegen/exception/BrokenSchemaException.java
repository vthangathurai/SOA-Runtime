/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.exception;

/**
 * @author ichernyshev
 */
public class BrokenSchemaException extends UnsupportedSchemaException {


	private static final long serialVersionUID = -8380062658565661662L;

	public BrokenSchemaException(String msg) {
		super(msg);
	}

	public BrokenSchemaException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
