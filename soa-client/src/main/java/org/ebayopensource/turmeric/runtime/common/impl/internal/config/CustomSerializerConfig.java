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
 * <p>Java class for CustomSerializerConfig complex type.
 * 
 * 
 * 
 * 
 */
public class CustomSerializerConfig {

    protected String javaTypeName;
    protected String serializerClassName;
    protected String deserializerClassName;
    protected String xmlTypeName;

    /**
     * Gets the value of the javaTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJavaTypeName() {
        return javaTypeName;
    }

    /**
     * Sets the value of the javaTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJavaTypeName(String value) {
        this.javaTypeName = value;
    }

    /**
     * Gets the value of the serializerClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerializerClassName() {
        return serializerClassName;
    }

    /**
     * Sets the value of the serializerClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerializerClassName(String value) {
        this.serializerClassName = value;
    }

    /**
     * Gets the value of the deserializerClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeserializerClassName() {
        return deserializerClassName;
    }

    /**
     * Sets the value of the deserializerClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeserializerClassName(String value) {
        this.deserializerClassName = value;
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
