/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


public class UrlMappingsDesc {
	private final Map<Integer,String> m_pathMap;
	private final Map<String,String> m_queryMap;
	private final String m_queryOpMapping;
	private Set<String> m_rejectList;
	
	public final static UrlMappingsDesc EMPTY_MAPPINGS = new UrlMappingsDesc();
	
	private final Map<String, String> m_upperCaseQueryMap;
	
	private UrlMappingsDesc() {
		m_pathMap = Collections.unmodifiableMap(new HashMap<Integer,String>());
		m_queryMap = CollectionUtils.EMPTY_STRING_MAP;
		m_queryOpMapping = null;
		m_upperCaseQueryMap = CollectionUtils.EMPTY_STRING_MAP;
	}
	
	public UrlMappingsDesc(Map<Integer,String> pathMap,
			Map<String,String> queryMap,
			String queryOpMapping) {
		m_pathMap = Collections.unmodifiableMap(pathMap);
		m_queryMap = Collections.unmodifiableMap(queryMap);
		m_queryOpMapping = queryOpMapping;
		
		Map<String, String> tmp = new HashMap<String, String>();
		for(Map.Entry<String, String> entry : m_queryMap.entrySet()) {
			tmp.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		
		m_upperCaseQueryMap = Collections.unmodifiableMap(tmp);
		m_rejectList = Collections.EMPTY_SET;
	}
	
	public UrlMappingsDesc(Map<Integer, String> pathMap, Map<String, String> queryMap, String queryOpMapping, Set<String> rejectList) {
		
		this(pathMap, queryMap, queryOpMapping);
		
		m_rejectList = Collections.unmodifiableSet(rejectList);
	}

	/**
	 * @return the m_pathMap
	 */
	public Map<Integer,String> getPathMap() {
		return m_pathMap;
	}

	/**
	 * @return the m_queryMap
	 */
	public Map<String,String> getQueryMap() {
		return m_queryMap;
	}

	/**
	 * @return the m_queryOpMap
	 */
	public String getQueryOpMapping() {
		return m_queryOpMapping;
	}
	
	public Map<String, String> getUpperCaseQueryMap() {
		return m_upperCaseQueryMap;
	}
	
	public Set<String> getRejectList() {
		return m_rejectList;
	}

}
