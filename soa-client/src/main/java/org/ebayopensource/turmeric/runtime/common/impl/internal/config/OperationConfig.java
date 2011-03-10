/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.ArrayList;
import java.util.List;



/**
 * <p>Java class for OperationConfig complex type.
 * 
 * 
 * 
 * 
 */
public class OperationConfig {

    protected MessageTypeConfig requestMessage;
    protected MessageTypeConfig responseMessage;
    protected MessageTypeConfig errorMessage;
    protected List<MessageHeaderConfig> requestHeader;
    protected List<MessageHeaderConfig> responseHeader;
    protected String methodName;
    protected String name;

    /**
     * Gets the value of the requestMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageTypeConfig }
     *     
     */
    public MessageTypeConfig getRequestMessage() {
        return requestMessage;
    }

    /**
     * Sets the value of the requestMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageTypeConfig }
     *     
     */
    public void setRequestMessage(MessageTypeConfig value) {
        this.requestMessage = value;
    }

    /**
     * Gets the value of the responseMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageTypeConfig }
     *     
     */
    public MessageTypeConfig getResponseMessage() {
        return responseMessage;
    }

    /**
     * Sets the value of the responseMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageTypeConfig }
     *     
     */
    public void setResponseMessage(MessageTypeConfig value) {
        this.responseMessage = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link MessageTypeConfig }
     *     
     */
    public MessageTypeConfig getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageTypeConfig }
     *     
     */
    public void setErrorMessage(MessageTypeConfig value) {
        this.errorMessage = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }
    
    public void setMethodName(String methodName)
    {
    	this.methodName = methodName;
    }
    
    public String getMethodName()
    {
    	return this.methodName;
    }
    
    /**
     * Gets the value of the requestHeader property.
     * 
     */
    public List<MessageHeaderConfig> getRequestHeader() {
        if (requestHeader == null) {
            requestHeader = new ArrayList<MessageHeaderConfig>();
        }
        return this.requestHeader;
    }

    /**
     * Gets the value of the responseHeader property.
     */
    public List<MessageHeaderConfig> getResponseHeader() {
        if (responseHeader == null) {
            responseHeader = new ArrayList<MessageHeaderConfig>();
        }
        return this.responseHeader;
    }

}
