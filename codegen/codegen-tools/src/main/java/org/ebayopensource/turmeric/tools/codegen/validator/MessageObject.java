/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.validator;

/**
 * Holder class for keeping validation error information.
 * 
 * 
 * @author rmandapati
 */

public class MessageObject {
	
	private String m_methodName;	
	private String m_message;	
	private String m_resolution;
	private boolean m_isFatalError;
	
	
	public MessageObject() {}
	
	
	
	
	public MessageObject(String message) {
		m_message = message;	
	}
	
	
	public MessageObject(String message, boolean isFatal) {
		m_message = message;	
		m_isFatalError = isFatal;
	}
	
	public MessageObject(
			String methodName,
			String message) {
	
		m_methodName = methodName;	
		m_message = message;	
	}
	
	
	public MessageObject(
				String methodName,
				String message,
				String resolution) {
		
		m_methodName = methodName;	
		m_message = message;	
		m_resolution = resolution;
	}
	
	
	public String getMessage() {
		return m_message;
	}
	
	public void setMessage(String message) {
		this.m_message = message;
	}
	
	
	
	public String getMethodName() {
		return m_methodName;
	}
	
	public void setMethodName(String name) {
		m_methodName = name;
	}
	
	
	
	public String getResolution() {
		return m_resolution;
	}
	
	public void setResolution(String resolution) {
		this.m_resolution = resolution;
	}


	public boolean isFatalError() {
		return m_isFatalError;
	}


	public void setIsFatalError(boolean fatalError) {
		m_isFatalError = fatalError;
	}
	
	
	public String toString() {
		
		StringBuilder strBuilder = new StringBuilder();
		
		if (getMethodName() != null) {
			strBuilder.append("Method Name : ").append(getMethodName()).append("\n");
		}		
		if (getMessage() != null) {
			strBuilder.append("Error Message : ").append(getMessage());
		}		
		if (getResolution() != null) {
			strBuilder.append("\nHow to fix : \n").append(getResolution());
		}	
		
		return strBuilder.toString();
	}
}
