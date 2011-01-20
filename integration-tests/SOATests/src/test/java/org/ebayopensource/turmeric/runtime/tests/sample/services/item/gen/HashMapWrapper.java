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

import java.util.HashMap;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.ItemType;



/**
 * @author wdeng
 *
 */
public class HashMapWrapper{
	private HashMap<String, ItemType>  m_object;
	
	public HashMapWrapper() {};
	
	public HashMapWrapper(HashMap<String, ItemType> map) {
		this.m_object = map;
	}

	public HashMap<String, ItemType> getMap() {
		return m_object;
	}

	public void setMap(HashMap<String, ItemType> map) {
		this.m_object = map;
	}
	
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof HashMapWrapper)) {
			return false;
		}
		HashMapWrapper wrapper = (HashMapWrapper) obj;
		if (null == m_object) {
			return null == wrapper.m_object;
		}
		return m_object.equals(wrapper.m_object);
	}
}
