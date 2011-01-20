/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.HashMap;



/**
 * <p>Java class for SerializerConfig complex type.
 * 
 * 
 * 
 * 
 */
public class SerializerConfig {

    protected String serializerFactoryClassName;
    protected String deserializerFactoryClassName;
    protected String name;
    protected String mimeType;
    protected HashMap<String,String> options = new HashMap<String,String>();

    /**
     * Gets the value of the serializerFactoryClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerializerFactoryClassName() {
        return serializerFactoryClassName;
    }

    /**
     * Sets the value of the serializerFactoryClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerializerFactoryClassName(String value) {
        this.serializerFactoryClassName = value;
    }

    /**
     * Gets the value of the deserializerFactoryClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeserializerFactoryClassName() {
        return deserializerFactoryClassName;
    }

    /**
     * Sets the value of the deserializerFactoryClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeserializerFactoryClassName(String value) {
        this.deserializerFactoryClassName = value;
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

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }
    
    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link HashMap }
     *     
     */
    public HashMap<String,String> getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link HashMap }
     *     
     */
    public void setOptions(HashMap<String,String> value) {
        this.options = value;
    }
}
