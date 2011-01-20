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


public class PreProcessFailedException extends BaseCodeGenException {
	
	private static final long serialVersionUID = 9059810739074899647L;
	
	private List<MessageObject> m_errorMsgList;
	
	public PreProcessFailedException() {
		super();
	}

	public PreProcessFailedException(String msg) {
		super(msg);
	}
	
	public PreProcessFailedException(String msg, List<MessageObject> errMsgList) {
		super(msg);
		setErrorMsgList(errMsgList);
	}
	

	public PreProcessFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public PreProcessFailedException(
			String msg, Throwable cause, List<MessageObject> errMsgList) {
		super(msg, cause);
		setErrorMsgList(errMsgList);
	}
	

	public List<MessageObject> getErrorMsgList() {
		return m_errorMsgList;
	}

	public void setErrorMsgList(List<MessageObject> errMsgList) {
		m_errorMsgList = errMsgList;
	}

}
