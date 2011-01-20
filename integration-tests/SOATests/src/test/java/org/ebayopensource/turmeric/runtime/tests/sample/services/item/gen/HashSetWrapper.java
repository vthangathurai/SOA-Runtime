/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.tests.sample.services.item.gen;

import java.util.HashSet;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.ItemType;



/**
 * @author wdeng
 *
 */
public class HashSetWrapper{
	public HashSet<ItemType>  m_object;
	
	public HashSetWrapper() {};
	
	public HashSetWrapper(HashSet<ItemType> map) {
		this.m_object = map;
	}

	public HashSet<ItemType> getObject() {
		return m_object;
	}

	public void setObject(HashSet<ItemType> object) {
		this.m_object = object;
	}

	
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof HashSetWrapper)) {
			return false;
		}
		HashSetWrapper wrapper = (HashSetWrapper) obj;
		if (null == m_object) {
			return null == wrapper.m_object;
		}
		return m_object.equals(wrapper.m_object);
	}
}
