/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.jaxb.maps;

import java.util.HashMap;

import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.Address;



public class HashMapOfAddress {
		private HashMap<String, Address> myNestedMap;

		public HashMap<String, Address> getMyNestedMap() {
			return myNestedMap;
		}

		public void setMyNestedMap(
				HashMap<String, Address> myNestedMap) {
			this.myNestedMap = myNestedMap;
		}
		
	    public boolean equals(Object other) {
	    	if (other == null) {
	    		return false;
	    	}
	    	if (!(other instanceof HashMapOfAddress)) {
	    		return false;
	    	}
	    	HashMapOfAddress otherObj = (HashMapOfAddress) other;
	    	
	    	return TestUtils.equals(myNestedMap, otherObj.myNestedMap);
	    }
}
