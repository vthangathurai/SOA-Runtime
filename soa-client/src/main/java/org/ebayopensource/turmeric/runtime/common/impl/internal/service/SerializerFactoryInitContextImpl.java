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

import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * @author ichernyshev
 */
public final class SerializerFactoryInitContextImpl extends BaseInitContext
	implements ISerializerFactory.InitContext
{
	private final Map<String,String> m_options;
	private final Class[] m_rootClasses;


	public SerializerFactoryInitContextImpl(ServiceId svcId, Map<String,String> options,
			Class[] rootClasses) {
		super(svcId);
		if (null == rootClasses || rootClasses.length == 0) {
			throw new NullPointerException();
		}
		m_rootClasses = rootClasses;
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
		return m_rootClasses;
	}
}
