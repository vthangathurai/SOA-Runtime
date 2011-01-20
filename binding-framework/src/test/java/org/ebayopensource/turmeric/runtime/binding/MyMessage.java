/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import java.util.Calendar;
import java.util.HashMap;

import javax.activation.DataHandler;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="body" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="recipients" type="{http://www.ebay.com/test/soafw/service/message}AddressList"/>
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class MyMessage {
    private String m_body;
    private Calendar m_createTime;
    private HashMap<String, Address> m_recipients = new HashMap<String, Address>();
    private String m_subject;
    private Object m_anyObject;
    private DataHandler m_binaryData;

    /**
     * Gets the value of the body property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBody() {
        return m_body;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBody(String value) {
        this.m_body = value;
    }

    /**
     * Gets the value of the createTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public Calendar getCreateTime() {
        return m_createTime;
    }

    /**
     * Sets the value of the createTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreateTime(Calendar value) {
        this.m_createTime = value;
    }

    /**
     * Gets the value of the recipients property.
     * 
     * @return
     *     possible object is
     *     {@link AddressList }
     *     
     */
    public HashMap<String, Address> getRecipients() {
        return m_recipients;
    }

    /**
     * Sets the value of the recipients property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressList }
     *     
     */
    public void setRecipients(HashMap<String, Address> value) {
        m_recipients = value;
    }

    public void addRecipient(Address recipient) {
    	m_recipients.put(recipient.getEmailAddress(), recipient);
    }
    
    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return m_subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.m_subject = value;
    }

    
	public Object getSomething() {
		return m_anyObject;
	}

	public void setSomething(Object any) {
		this.m_anyObject = any;
	}

	public DataHandler getBinaryData() {
		return m_binaryData;
	}

	public void setBinaryData(DataHandler binaryData) {
		this.m_binaryData = binaryData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m_anyObject == null) ? 0 : m_anyObject.hashCode());
		result = prime * result
				+ ((m_binaryData == null) ? 0 : m_binaryData.hashCode());
		result = prime * result + ((m_body == null) ? 0 : m_body.hashCode());
		result = prime * result
				+ ((m_createTime == null) ? 0 : m_createTime.hashCode());
		result = prime * result
				+ ((m_recipients == null) ? 0 : m_recipients.hashCode());
		result = prime * result
				+ ((m_subject == null) ? 0 : m_subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyMessage other = (MyMessage) obj;
		if (m_anyObject == null) {
			if (other.m_anyObject != null)
				return false;
		} else if (!m_anyObject.equals(other.m_anyObject))
			return false;
		if (m_binaryData == null) {
			if (other.m_binaryData != null)
				return false;
		} else if (!m_binaryData.equals(other.m_binaryData))
			return false;
		if (m_body == null) {
			if (other.m_body != null)
				return false;
		} else if (!m_body.equals(other.m_body))
			return false;
		if (m_createTime == null) {
			if (other.m_createTime != null)
				return false;
		} else if (!m_createTime.equals(other.m_createTime))
			return false;
		if (m_recipients == null) {
			if (other.m_recipients != null)
				return false;
		} else if (!m_recipients.equals(other.m_recipients))
			return false;
		if (m_subject == null) {
			if (other.m_subject != null)
				return false;
		} else if (!m_subject.equals(other.m_subject))
			return false;
		return true;
	}
}
