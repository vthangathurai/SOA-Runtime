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

import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;


public class MyObject {
	private String[] names;
	private String emailAddress;
	private int id;
	private MyObject singleObject;
	private ArrayList<MyObject> multipleObjects;
	private Object genericObject;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String[] getNames() {
		return this.names;
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof MyObject)) {
			return false;
		}
		MyObject otherObject = (MyObject) obj;
		if (!TestUtils.equals(id, otherObject.id)) {
			return false;
		}
		if (null == names) {
			return otherObject.names == null;
		}
		if (null == otherObject.names) {
			return false;
		}
		if (names.length != otherObject.names.length) {
			return false;
		}
		for (int i = 0; i<names.length; i++) {
			if (!TestUtils.equals(names[i], otherObject.names[i])) {
				return false;
			}
		}		
		return TestUtils.equals(emailAddress, otherObject.emailAddress);
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public MyObject getSingleObject() {
		return singleObject;
	}
	public void setSingleObject(MyObject singleObject) {
		this.singleObject = singleObject;
	}
	public ArrayList<MyObject> getMultipleObjects() {
		return multipleObjects;
	}
	public void setMultipleObjects(ArrayList<MyObject> multipleObjects) {
		this.multipleObjects = multipleObjects;
	}
	public Object getGenericObject() {
		return genericObject;
	}
	public void setGenericObject(Object genericObject) {
		this.genericObject = genericObject;
	}
}
