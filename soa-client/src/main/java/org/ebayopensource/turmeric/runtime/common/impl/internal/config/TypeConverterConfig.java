/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;



/**
 * <p>Java class for TypeConverterConfig complex type.
 * 
 * 
 * 
 * 
 */
public class TypeConverterConfig {

    protected String boundJavaTypeName;
    protected String valueJavaTypeName;
    protected String typeConverterClassName;
    protected String xmlTypeName;

    /**
     * Gets the value of the boundJavaTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoundJavaTypeName() {
        return boundJavaTypeName;
    }

    /**
     * Sets the value of the boundJavaTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoundJavaTypeName(String value) {
        this.boundJavaTypeName = value;
    }

    /**
     * Gets the value of the valueJavaTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueJavaTypeName() {
        return valueJavaTypeName;
    }

    /**
     * Sets the value of the valueJavaTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueJavaTypeName(String value) {
        this.valueJavaTypeName = value;
    }

    /**
     * Gets the value of the typeConverterClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeConverterClassName() {
        return typeConverterClassName;
    }

    /**
     * Sets the value of the typeConverterClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeConverterClassName(String value) {
        this.typeConverterClassName = value;
    }

    /**
     * Gets the value of the xmlTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlTypeName() {
        return xmlTypeName;
    }

    /**
     * Sets the value of the xmlTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlTypeName(String value) {
        this.xmlTypeName = value;
    }

}
