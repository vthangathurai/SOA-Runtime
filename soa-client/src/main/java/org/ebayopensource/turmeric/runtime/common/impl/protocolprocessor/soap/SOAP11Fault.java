/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap;

/**
 * Title:			SOAP11Fault.java
 * Description:  	
 * Copyright:		Copyright (c) 2008
 * Company:			eBay
 * @author 			Gary Yue, Igor Dralyuk
 * @version			1.0
 *
 * Class that models the SOAP 1.1 Fault
 */
public class SOAP11Fault {

	private String m_faultCode;
	private String m_faultString;
	private String m_faultActor;
	private Object m_detail;
	
	public SOAP11Fault(String code, String string, String actor) {
		m_faultCode = code;
		m_faultString = string;
		m_faultActor = actor;
	}
		
    /**
     * Get SOAP fault code
     * @return
     */
	public String getFaultCode() {
		return m_faultCode;
	}
	
	/**
	 * Set SOAP fault code
	 * @param code
	 */
	public void setFaultCode(String code) {
		m_faultCode = code;
	}
	
	/**
	 * Get SOAP fault string
	 * @return
	 */
	public String getFaultString() {
		return m_faultString;
	}
	
	/**
	 * Set SOAP fault string
	 * @param reason
	 */
	public void setFaultString(String string) {
		m_faultString = string;
	}
	
	/**
	 * Get SOAP fault actor
	 * @return
	 */
	public String getFaultActor() {
		return m_faultActor;
	}
	
	/**
	 * Set SOAP fault actor
	 * @param role
	 */
	public void setFaultActor(String actor) {
		m_faultActor = actor;
	}

	/**
	 * Get SOAP fault detail
	 * @return
	 */
	public Object getDetail() {
		return m_detail;
	}
	
	/**
	 * Set SOAP fault detail
	 * @param detail
	 */
	public void setDetail(Object detail) {
		m_detail = detail;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SOAP11Fault ");
		sb.append("[faultCode=").append(m_faultCode)
		.append(", faultString=").append(m_faultString)
		.append(", faultActor=").append(m_faultActor)
		.append(", detail=").append(m_detail).append("]");
		return sb.toString();
	}
}
