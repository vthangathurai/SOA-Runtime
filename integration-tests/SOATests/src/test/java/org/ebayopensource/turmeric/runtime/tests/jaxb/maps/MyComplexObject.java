/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.jaxb.maps;

import java.util.ArrayList;
import java.util.HashMap;

import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.Address;



public class MyComplexObject {
		private HashMap<String, ArrayList<HashMap <String, String>>> myNestedMap;
		private Address m_address;

		public HashMap<String, ArrayList<HashMap<String, String>>> getMyNestedMap() {
			return myNestedMap;
		}

		public void setMyNestedMap(
				HashMap<String, ArrayList<HashMap<String, String>>> myNestedMap) {
			this.myNestedMap = myNestedMap;
		}

		public Address getAddress() {
			return m_address;
		}

		public void setAddress(Address address) {
			this.m_address = address;
		}
		
	    public boolean equals(Object other) {
	    	if (other == null) {
	    		return false;
	    	}
	    	if (!(other instanceof MyComplexObject)) {
	    		return false;
	    	}
	    	MyComplexObject otherObj = (MyComplexObject) other;
	    	
	    	return TestUtils.equals(myNestedMap, otherObj.myNestedMap) && TestUtils.equals(m_address, otherObj.m_address);
	    }
}
