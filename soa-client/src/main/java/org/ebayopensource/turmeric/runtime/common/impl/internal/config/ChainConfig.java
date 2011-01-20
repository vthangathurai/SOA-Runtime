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
 * <p>Java class for ChainConfig complex type.
 * 
 * 
 * 
 * 
 */
public class ChainConfig {

    protected List<HandlerConfig> handler;
    protected String name;
    protected PresenceConfig presence;

    /**
     * Gets the value of the handler property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handler property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     *    getHandler().add(newItem);
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HandlerConfig }
     * 
     * 
     */
    public List<HandlerConfig> getHandler() {
        if (handler == null) {
            handler = new ArrayList<HandlerConfig>();
        }
        return this.handler;
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
     * Gets the value of the presence property.
     * 
     * @return
     *     possible object is
     *     {@link PresenceConfig }
     *     
     */
    public PresenceConfig getPresence() {
        return presence;
    }

    /**
     * Sets the value of the presence property.
     * 
     * @param value
     *     allowed object is
     *     {@link PresenceConfig }
     *     
     */
    public void setPresence(PresenceConfig value) {
        this.presence = value;
    }

}
