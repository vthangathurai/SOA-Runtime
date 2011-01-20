/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Concrete implementation of the CacheKey. This is a Map based implementation
 *
 */
/**
 * @author wdeng
 *
 */
public class CacheKeyMap extends CacheKey {
	private Map<String, Object> m_elements = new HashMap<String, Object>();		
	
	/**
	 * @param opName Service operation name
	 */
	public CacheKeyMap (String opName) {
		super(opName);
	}

	// one cache key is equals to another one if all the key/values  matches.
	// TODO: please verify logic. Not tested.
	@Override
	public boolean equals(Object ko) {
		if (this == ko)
			return true;
		if (!(ko instanceof CacheKey))
			return false;
		CacheKeyMap k = (CacheKeyMap) ko;
		if ((m_opName == null && k.m_opName != null)
				|| (m_opName != null && !m_opName.equals(k.m_opName)))
			return false;
		
		Iterator<String> i = m_elements.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			Object value1 = m_elements.get(key);
			Object value2 = k.m_elements.get(key);
			
			if ((value1 == null && value2 != null) ||
				(value2 != null && value2 == null) ||
				(value1 != null && value2 != null && !value1.equals(value2)))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return m_hashCode; 
	}
	
	@Override
	public String toString() {
		return m_elements.toString();
	}

	@Override
	public void add(String key, Object value) {
		m_elements.put(key, value);
		if (value != null) {
			m_hashCode ^= key.hashCode() ^ value.hashCode();
		} else {
			m_hashCode ^= key.hashCode();
		}
		
	}
	
}





