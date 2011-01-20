/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external;

import java.util.List; 
import java.util.ArrayList;

public class WSDLOperationType {
	
	private String m_operationName;
	private WSDLMessageType m_inMessage;
	private WSDLMessageType m_outMessage;
	private List<WSDLMessageType> m_requestHeader;
	private List<WSDLMessageType> m_responseHeader;
	private List<WSDLMessageType> m_faults;
	
	
	public String getOperationName() {
		return m_operationName;
	}
	
	public void setOperationName(String name) {
		m_operationName = name;
	}
	
	
	public WSDLMessageType getInMessage() {
		return m_inMessage;
	}
	
	public void setInMessage(WSDLMessageType message) {
		m_inMessage = message;
	}	
	
	public WSDLMessageType getOutMessage() {
		return m_outMessage;
	}
	
	public void setOutMessage(WSDLMessageType message) {
		m_outMessage = message;
	}
	
	public List<WSDLMessageType> getRequestHeader(){
		if(m_requestHeader == null)
			m_requestHeader = new ArrayList<WSDLMessageType>();
		
		return m_requestHeader;
	}
	
	public List<WSDLMessageType> getResponseHeader(){
		if(m_responseHeader == null)
			m_responseHeader = new ArrayList<WSDLMessageType>();
		
		return m_responseHeader;
	}

	public List<WSDLMessageType> getFaults(){
		if(m_faults == null)
			m_faults = new ArrayList<WSDLMessageType>();
		
		return m_faults;
	}
}
