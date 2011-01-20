/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;

public class CodeGenTestMessage extends MyMessage {
    private static final long serialVersionUID = 1L;
    
    private int m_testId;
	private List<Map<String, Long>> m_complexList = null;
	public String m_testStr;
	
	
	public List<Map<String, Long>> getComplexList() {
		return m_complexList;
	}
	public void setComplexList(List<Map<String, Long>> complexList) {
		this.m_complexList = complexList;
	}
	
	
	public int getTestId() {
		return m_testId;
	}
	
	public void setTestId(int id) {
		m_testId = id;
	}

}
