/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.exception;

public class CodeGenFailedException extends BaseCodeGenException {

	private static final long serialVersionUID = 2359794849331560455L;
	
	private boolean isMessageFormatted = false;

	public boolean isMessageFormatted() {
		return isMessageFormatted;
	}

	public void setMessageFormatted(boolean isMessageFormatted) {
		this.isMessageFormatted = isMessageFormatted;
	}

	public CodeGenFailedException() {
		super();
	}

	public CodeGenFailedException(String msg) {
		super(msg);
	}

	public CodeGenFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	
}
