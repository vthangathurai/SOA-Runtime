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



public class HashMapOfArrayList {
		private HashMap<String, ArrayList<String>> myNestedMap;

		public HashMap<String, ArrayList<String>> getMyNestedMap() {
			return myNestedMap;
		}

		public void setMyNestedMap(
				HashMap<String, ArrayList<String>> myNestedMap) {
			this.myNestedMap = myNestedMap;
		}
		
	    public boolean equals(Object other) {
	    	if (other == null) {
	    		return false;
	    	}
	    	if (!(other instanceof HashMapOfArrayList)) {
	    		return false;
	    	}
	    	HashMapOfArrayList otherObj = (HashMapOfArrayList) other;
	    	
	    	return TestUtils.equals(myNestedMap, otherObj.myNestedMap);
	    }
}
