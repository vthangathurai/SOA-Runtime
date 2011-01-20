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
 * Title:			SOAP12Fault.java
 * Description:  	
 * Copyright:		Copyright (c) 2008
 * Company:			eBay
 * @author 			Gary Yue, Igor Dralyuk
 * @version			1.0
 *
 * Class that models the SOAP 1.2 Fault
 */
public class SOAP12Fault {

	private String m_code;
	private String m_reason;
	private String m_role;
	private Object m_detail;
	
	public SOAP12Fault(String code, String reason, String role) {
		m_code = code;
		m_reason = reason;
		m_role = role;
	}

    /**
     * Get SOAP fault code
     * @return
     */
	public String getCode() {
		return m_code;
	}
	
	/**
	 * Set SOAP fault code
	 * @param code
	 */
	public void setCode(String code) {
		m_code = code;
	}
	
	/**
	 * Get SOAP fault reason
	 * @return
	 */
	public String getReason() {
		return m_reason;
	}
	
	/**
	 * Set SOAP fault reason
	 * @param reason
	 */
	public void setReason(String reason) {
		m_reason = reason;
	}
	
	/**
	 * Get SOAP fault role
	 * @return
	 */
	public String getRole() {
		return m_role;
	}
	
	/**
	 * Set SOAP fault role
	 * @param role
	 */
	public void setRole(String role) {
		m_role = role;
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
		StringBuilder sb = new StringBuilder("SOAP12Fault ");
		sb.append("[code=").append(m_code)
		.append(", reason=").append(m_reason)
		.append(", role=").append(m_role)
		.append(", detail=").append(m_detail).append("]");
		return sb.toString();
	}
}
