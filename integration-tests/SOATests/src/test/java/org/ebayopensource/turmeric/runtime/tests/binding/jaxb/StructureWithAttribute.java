/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StructureWithAttribute", propOrder = {
    "myObjects"
})
public class StructureWithAttribute {

	    protected List<MyObject> myObjects;
	    @XmlAttribute(required = true)
	    protected int count;

	    public List<MyObject> getItem() {
	        if (myObjects == null) {
	            myObjects = new ArrayList<MyObject>();
	        }
	        return this.myObjects;
	    }



	    /**
		 * Gets the value of the count property.
		 * 
		 */
	    public int getCount() {
	        return count;
	    }

	    /**
		 * Sets the value of the count property.
		 * 
		 */
	    public void setCount(int value) {
	        this.count = value;
	    }

		
		public boolean equals(Object obj) {
			if (null == obj) {
				return false;
			}
			if (!(obj instanceof StructureWithAttribute)) {
				return false;
			}
			StructureWithAttribute otherObject = (StructureWithAttribute) obj;
			if (count != otherObject.count) {
				return false;
			}
			if (myObjects == null || myObjects.size()== 0) {
				return otherObject.myObjects==null || otherObject.myObjects.size() == 0;
			}
			return myObjects.equals(otherObject.myObjects);
		}
	}
