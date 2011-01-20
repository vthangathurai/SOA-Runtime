/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.util.Map;
import java.util.HashMap;

public class OperationMappings implements Cloneable {

	private Map<String, OperationMapping> operationMap;
	
	public OperationMappings() {
		operationMap = new HashMap<String, OperationMapping>();
	}

	public OperationMapping getOperationMapping(String key) {
		return operationMap.get(key);
	}
	
	// TODO - should it throw an exception if  duplicate operation mapping keys are present
	public void add(OperationMapping om) {
		operationMap.put(om.getKey(), om);
	}

	public OperationMappings clone() {
		
		OperationMappings retval = null;
		try {
			retval = (OperationMappings) super.clone();
		} catch (CloneNotSupportedException e) {
			// should never happen
			// e.printStackTrace();
		}
		return retval;
		
	}
}
