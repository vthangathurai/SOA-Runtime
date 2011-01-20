/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.Collections;
import java.util.Map;

import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * @author ichernyshev
 */
public final class DeserializerFactoryInitContextImpl extends BaseInitContext
	implements IDeserializerFactory.InitContext
{
	private final Map<String,String> m_options;
	private final Class[] m_rootClasses;
	private final Schema m_upaAwaremasterSchema;
	

	public DeserializerFactoryInitContextImpl(ServiceId svcId, Map<String,String> options, 
						Class[] rootClasses, Schema upaAwaremasterSchema) {
		super(svcId);
		if (null == rootClasses || rootClasses.length == 0) {
			throw new NullPointerException();
		}
		m_rootClasses = rootClasses;
		m_upaAwaremasterSchema = upaAwaremasterSchema;
		if (options != null) {
			m_options = Collections.unmodifiableMap(options);
		} else {
			m_options = CollectionUtils.EMPTY_STRING_MAP;
		}
	}

	public Map<String,String> getOptions() {
		checkAlive();
		return m_options;
	}

	public Class[] getRootClasses() {
		checkAlive();
		return m_rootClasses;
	}
	
	public Schema getUpaAwareMasterSchema() {
		checkAlive();
		return m_upaAwaremasterSchema;
	}
}
