/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.util.Collections;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Handler;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * @author ichernyshev
 */
public final class HandlerInitContextImpl extends BaseInitContext
	implements Handler.InitContext
{
	private final String m_name;
	private final Map<String,String> m_options;

	public HandlerInitContextImpl(ServiceId svcId, String name, Map<String,String> options) {
		super(svcId);

		if (name == null) {
			throw new NullPointerException();
		}

		m_name = name;

		if (options != null) {
			m_options = Collections.unmodifiableMap(options);
		} else {
			m_options = CollectionUtils.EMPTY_STRING_MAP;
		}
	}

	public String getName() {
		checkAlive();
		return m_name;
	}

	public Map<String,String> getOptions() {
		checkAlive();
		return m_options;
	}
}
