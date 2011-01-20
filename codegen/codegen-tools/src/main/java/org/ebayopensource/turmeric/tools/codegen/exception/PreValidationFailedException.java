/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.exception;

import java.util.List;

import org.ebayopensource.turmeric.tools.codegen.validator.MessageObject;


public class PreValidationFailedException extends PreProcessFailedException {
	
	private static final long serialVersionUID = -8949265219558172115L;

	public PreValidationFailedException() {
		super();
	}

	public PreValidationFailedException(String msg) {
		super(msg);
	}
	
	public PreValidationFailedException(String msg, List<MessageObject> errMsgList) {
		super(msg, errMsgList);
	}
	

	public PreValidationFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public PreValidationFailedException(
			String msg, Throwable cause, List<MessageObject> errMsgList) {
		super(msg, cause, errMsgList);
	}	
	

}
