/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

/**
 * 
 * This needs to be an immutable class as instances of this are populated
 * in a java.util.Map in OperationMappings which is cloned.
 * 
 */
public class OperationMapping implements Cloneable {

	private final String operationName;
	private final String alias;
	
	public OperationMapping(String operationName, String alias) {
		this.operationName = operationName;
		this.alias = alias;
	}
	
	public String getKey() {
		return alias;
	}
	
	public String getOperationName() {
		return operationName;
	}
	
}
