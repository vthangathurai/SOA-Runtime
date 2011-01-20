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
@XmlType(name = "ErrorAndResponseMessage", propOrder = {
    "error",
    "response"
})
public class ErrorAndResponseMessage {
	private List<ErrorType> error;
    protected String response;
    public List<ErrorType> getError() {
        if (error == null) {
            error = new ArrayList<ErrorType>();
        }
        return error;
    }
    public String getResponse() {
    	return response;
    }
    public void setResponse(String value) {
    	response = value;
    }
	public void setError(List<ErrorType> error) {
		this.error = error;
	}
}
