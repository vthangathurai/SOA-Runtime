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
 * <p>Java class for PipelineTreeConfig complex type.
 * 
 * 
 * 
 * 
 */
public class PipelineTreeConfig {

    protected List<Object> handlerOrChain;

    /**
     * Gets the value of the handlerOrChain property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handlerOrChain property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     *    getHandlerOrChain().add(newItem);
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChainConfig }
     * {@link HandlerConfig }
     * 
     * 
     */
    public List<Object> getHandlerOrChain() {
        if (handlerOrChain == null) {
            handlerOrChain = new ArrayList<Object>();
        }
        return this.handlerOrChain;
    }

}
