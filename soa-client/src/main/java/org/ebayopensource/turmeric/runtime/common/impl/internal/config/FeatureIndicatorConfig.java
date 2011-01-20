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
 * <p>Java class for FeatureIndicatorConfig complex type.
 * 
 * 
 * 
 * 
 */
public class FeatureIndicatorConfig {

    protected String urlPattern;
    protected NameValue transportHeader;

    /**
     * Gets the value of the urlPattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURLPattern() {
        return urlPattern;
    }

    /**
     * Sets the value of the urlPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURLPattern(String value) {
        this.urlPattern = value;
    }

    /**
     * Gets the value of the transportHeader property.
     * 
     * @return
     *     possible object is
     *     {@link NameValue }
     *     
     */
    public NameValue getTransportHeader() {
        return transportHeader;
    }

    /**
     * Sets the value of the transportHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameValue }
     *     
     */
    public void setTransportHeader(NameValue value) {
        this.transportHeader = value;
    }

}
