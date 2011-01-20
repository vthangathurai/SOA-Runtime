/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.validation;

/**
 * 
 * @author arajmony
 *
 */
public class SchemaBaseDetails {

	
	String m_filePathForStrictValidation;
	String m_serviceAdminName;
	boolean m_isEnableNSWSDL;
	
	
	
	
	public String getFilePathForStrictValidation() {
		return m_filePathForStrictValidation;
	}
	public void setFilePathForStrictValidation(String pathForStrictValidation) {
		m_filePathForStrictValidation = pathForStrictValidation;
	}
	
	
	public String getServiceAdminName() {
		return m_serviceAdminName;
	}
	public void setServiceAdminName(String adminName) {
		m_serviceAdminName = adminName;
	}
	
	
	public boolean isEnableNSWSDL() {
		return m_isEnableNSWSDL;
	}
	public void setIsEnableNSWSDL(boolean enableNSWSDL) {
		m_isEnableNSWSDL = enableNSWSDL;
	}
	
	
	
}
