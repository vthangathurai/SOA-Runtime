/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.util.Collections;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * @author ichernyshev
 */
public final class LoggingHandlerInitContextImpl extends BaseInitContext
	implements LoggingHandler.InitContext
{
	private final Map<String,String> m_options;
	private boolean m_supportsErrorLogging;

	public LoggingHandlerInitContextImpl(ServiceId svcId, Map<String,String> options) {
		super(svcId);

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

	public boolean supportsErrorLogging() {
		return m_supportsErrorLogging;
	}

	public void setSupportsErrorLogging() {
		checkAlive();
		m_supportsErrorLogging = true;
	}
}
