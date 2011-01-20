/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.types1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomErrorMessage", propOrder = {
    "error"
})
public class CustomErrorMessage {

    protected List<ErrorType> error;

    public List<ErrorType> getError() {
        if (error == null) {
            error = new ArrayList<ErrorType>();
        }
        return this.error;
    }
    public String toString() {
    	if (error != null && error.size() != 0) {
    		return error.get(0).getLongMessage();
    	} else {
    		return "(No error information)";
    	}
    }
}