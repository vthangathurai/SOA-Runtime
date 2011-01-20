/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Concrete implementation of the CacheKey. This is a ArrayList based implementation
 * The individual cache elements are stored in an ArrayList.
 *
 */
public class CacheKeyList extends CacheKey {
	private List<CacheElement> m_elements = new ArrayList<CacheElement>();		
	private long m_expired = 0;
	
	/**
	 * @param opName An operation name.
	 */
	public CacheKeyList (String opName) {
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
		CacheKeyList k = (CacheKeyList) ko;
		long currentTime =  System.currentTimeMillis();
		if ((m_expired != 0 && m_expired < currentTime)
				|| (k.m_expired != 0 && k.m_expired < currentTime))
			return false;
		if ((m_opName == null && k.m_opName != null)
				|| (m_opName != null && !m_opName.equals(k.m_opName)))
			return false;
		for (int i = 0; i < m_elements.size(); i++) {
			CacheElement value1 = m_elements.get(i);
			CacheElement value2 = k.m_elements.get(i);
			
			if ((value1 == null && value2 != null) ||
				(value1 != null && value2 == null) ||
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
		CacheElement element = new CacheElement(key, value);
		m_elements.add(element);
		m_hashCode ^= element.hashCode();
	}
	
	/**
	 * Returns the expiration time.
	 * @return the expiration time.
	 */
	public long getExpired() {
		return m_expired;
	}

	/**
	 * Sets the expiration time.
	 * @param expired A time.
	 */
	public void setExpired(long expired) {
		m_expired = expired;
	}

	private static class CacheElement {
		String m_key;
		Object m_value;
		int m_hashCode;
		
		public CacheElement(String key, Object value) {
			m_key = key;
			m_value = value;
			if (value != null) {
				m_hashCode = key.hashCode() ^ value.hashCode();
			} else {
				m_hashCode = key.hashCode();
			}
		}

		@Override
		public int hashCode() {
			return m_hashCode; 
		}
		
		//public CacheElement
		// one cache key is equals to another one if all the key/values  matches.
		// TODO: please verify logic. Not tested.
		@Override
		public boolean equals(Object ko) {
			if (this == ko)
				return true;
			if (!(ko instanceof CacheElement))
				return false;
			CacheElement k = (CacheElement) ko;
			if ((m_key == null && k.m_key != null) ||
					(m_key != null && k.m_key == null) ||
					(m_key != null && k.m_key != null && !m_key.equals(k.m_key)))
					return false;

			if ((m_value == null && k.m_value != null) ||
					(m_value != null && k.m_value == null) ||
					(m_value != null && k.m_value != null && !m_value.equals(k.m_value)))
					return false;
			
			return true;
		}
		
		@Override
		public String toString() {
			return new StringBuffer().append(m_key).append("=").append(m_value).append(";").toString();
		}
	}
}
