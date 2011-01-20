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
package org.ebayopensource.turmeric.tools.library;

/**
 * @author arajmony
 *
 */
public class RegistryUpdateDetails {
	
	private String m_libraryName;
	private boolean m_updateStatus;
	private String m_message;
	
	
	public String getLibraryName() {
		return m_libraryName;
	}
	public void setLibraryName(String name) {
		m_libraryName = name;
	}
	
	
	public boolean isUpdateSucess() {
		return m_updateStatus;
	}
	public void setIsUpdateSucess(boolean status) {
		m_updateStatus = status;
	}
	
	
	public String getMessage() {
		return m_message;
	}
	public void setMessage(String m_message) {
		this.m_message = m_message;
	}
	
	
}
