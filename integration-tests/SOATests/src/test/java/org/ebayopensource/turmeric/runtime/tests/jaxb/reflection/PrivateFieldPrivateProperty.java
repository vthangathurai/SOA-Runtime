/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.jaxb.reflection;

import javax.xml.bind.annotation.XmlRootElement;

import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;


/**
 * @author wdeng
 */
@XmlRootElement
public class PrivateFieldPrivateProperty {
	private String field;
	
	public PrivateFieldPrivateProperty() {}

	public PrivateFieldPrivateProperty(String name) {
		field = name;
	}

    public boolean equals(Object other) {
    	if (other == null) {
    		return false;
    	}
    	if (!(other instanceof PrivateFieldPrivateProperty)) {
    		return false;
    	}
    	PrivateFieldPrivateProperty otherObj = (PrivateFieldPrivateProperty) other;
    	
    	return TestUtils.equals(field, otherObj.field);
    }
}
