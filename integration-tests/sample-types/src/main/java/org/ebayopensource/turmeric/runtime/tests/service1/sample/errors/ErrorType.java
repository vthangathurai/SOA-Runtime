/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.ebayopensource.turmeric.runtime.tests.service1.sample.errors;

import java.util.ArrayList;
import java.util.List;

public class ErrorType {

   protected String shortMessage;
    protected String longMessage;
    protected String errorCode;
    protected SeverityCodeType severityCode;
    protected List<ErrorParameterType> errorParameters;
    protected ErrorClassificationCodeType errorClassification;

    /**
     * Gets the value of the shortMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortMessage() {
        return shortMessage;
    }

    /**
     * Sets the value of the shortMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortMessage(String value) {
        this.shortMessage = value;
    }

    /**
     * Gets the value of the longMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongMessage() {
        return longMessage;
    }

    /**
     * Sets the value of the longMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongMessage(String value) {
        this.longMessage = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the severityCode property.
     * 
     * @return
     *     possible object is
     *     {@link SeverityCodeType }
     *     
     */
    public SeverityCodeType getSeverityCode() {
        return severityCode;
    }

    /**
     * Sets the value of the severityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeverityCodeType }
     *     
     */
    public void setSeverityCode(SeverityCodeType value) {
        this.severityCode = value;
    }

    /**
     * Gets the value of the errorParameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errorParameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorParameterType }
     * 
     * 
     */
    public List<ErrorParameterType> getErrorParameters() {
        if (errorParameters == null) {
            errorParameters = new ArrayList<ErrorParameterType>();
        }
        return this.errorParameters;
    }

    public void setErrorParameters(List<ErrorParameterType> errParams) {
    	errorParameters = errParams;
    }
    
    /**
     * Gets the value of the errorClassification property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorClassificationCodeType }
     *     
     */
    public ErrorClassificationCodeType getErrorClassification() {
        return errorClassification;
    }

    /**
     * Sets the value of the errorClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorClassificationCodeType }
     *     
     */
    public void setErrorClassification(ErrorClassificationCodeType value) {
        this.errorClassification = value;
    }

}
