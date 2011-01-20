/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * 
 * Class for supporting the transport header mapping feature.
 * The feature supports sending short alias for the headers such as
 * 'SERVICE-NAME' instead of 'X-TURMERIC-SERVICE-NAME' on the wire
 * Also, the service developers can specify any transport headers that
 * are to be suppressed and not added to the context, even though they 
 * are sent in the request as transport headers. 
 * 
 * HeaderMappingsDesc maintains two structures
 * <UL>
 * <LI> A map containing mappings from the incoming transport header to the replaced transport header
 * <LI> A set of transport headers to be suppressed.
 * </UL>
 *  
 *
 */
public class HeaderMappingsDesc {

	private final Map<String,String> m_headerMap;
	private final Set<String> m_suppressHeaderSet;
	/**
	 * The empty HeadermappingDesc instance.
	 */
	public final static HeaderMappingsDesc EMPTY_MAPPINGS = new HeaderMappingsDesc();
	
	private HeaderMappingsDesc() {
		m_headerMap = CollectionUtils.EMPTY_STRING_MAP;
		m_suppressHeaderSet = CollectionUtils.EMPTY_STRING_SET;
	}
	
	/**
	 * @param headerMap  A map of header mappings.
	 * @param suppressHeaderSet A set of headers to be suppressed.
	 */
	public HeaderMappingsDesc(Map<String,String> headerMap, Set<String> suppressHeaderSet) {
		m_headerMap = Collections.unmodifiableMap(headerMap);
		m_suppressHeaderSet = Collections.unmodifiableSet(suppressHeaderSet);
	}

	/**
	 * @return the header map.
	 */
	public Map<String,String> getHeaderMap() {
		return m_headerMap;
	}

	/**
	 * @return the the suppress header set
	 */
	public Set<String> getSuppressHeaderSet() {
		return m_suppressHeaderSet;
	}
}
