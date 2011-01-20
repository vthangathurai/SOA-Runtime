/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

/**
 * CacheKey: This is the cache key representation of the request.
 * To facilitate the cache providers to map the various request instances to 
 * a 'key' object, this 'key' instances can then be used to store/index the responses 
 * to be cached in an collection
 * 
 * The cache providers use the CachePolicyDesc.createCacheKey() to generate the key
 * representation of the request that needs to be looked up or inserted into the cache.
 * 
 */
public abstract class CacheKey {
	/**
	 * Enum of cache key types.
	 * 
	 * @author wdeng
	 *
	 */
	public enum KEY_CLASSES { 
		/**
		 * A map type of CacheKey.
		 */
		CacheKeyMap, 
		/**
		 * A list type of CacheKey.
		 */
		CacheKeyList}
	private static KEY_CLASSES s_status = KEY_CLASSES.CacheKeyMap ;

	/**
	 * The operation name.
	 */
	protected String m_opName = null;
	
	/**
	 * The hashcode.
	 */
	protected int m_hashCode = 0;

	/**
	 * 
	 * @param opName Service operation name
	 */
	public CacheKey (String opName) {
		m_opName = opName;
		m_hashCode = opName.hashCode();
	}

	/**
	 * 
	 * @param key dotted path of the element (i.e key expression)
	 * @param value value object associated with the above key
	 */
	abstract public void add(String key, Object value);
	
	/**
	 * Creates a CacheKey for the operation name given.
	 * @param opName An operation name.
	 * @return a CacheKey for the operation name given.
	 */
	public static CacheKey createCacheKey(String opName) {
		switch (s_status) {
			case CacheKeyMap: 
				return new CacheKeyMap(opName);
			case CacheKeyList: 
				return new CacheKeyList(opName); 
		}
		return null;
	}

	/**
	 * Sets the CacheKey type.
	 * 
	 * @param status A KEY_CLASSES enum value.
	 */
	public static void setStatus(KEY_CLASSES status) {
		s_status = status;
	}
}
