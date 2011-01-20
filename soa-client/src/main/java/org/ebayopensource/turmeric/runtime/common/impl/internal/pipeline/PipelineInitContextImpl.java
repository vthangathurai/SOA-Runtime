/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.HandlerOptions;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.pipeline.PipelineMode;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * @author ichernyshev
 */
public final class PipelineInitContextImpl extends BaseInitContext
	implements Pipeline.InitContext
{
	private final PipelineMode m_pipelineMode;
	private final ClassLoader m_classLoader;
	private final List<HandlerOptions> m_handlerConfigs;

	public PipelineInitContextImpl(ServiceId svcId, PipelineMode pipelineMode,
		ClassLoader classLoader, List<HandlerOptions> handlerConfigs) {
		super(svcId);

		if (pipelineMode == null || classLoader == null) {
			throw new NullPointerException();
		}

		m_pipelineMode = pipelineMode;
		m_classLoader = classLoader;

		if (handlerConfigs != null) {
			m_handlerConfigs = Collections.unmodifiableList(handlerConfigs);
		} else {
			m_handlerConfigs = Collections.unmodifiableList(new ArrayList<HandlerOptions>());
		}
	}

	public PipelineMode getPipelineMode() {
		checkAlive();
		return m_pipelineMode;
	}

	public ClassLoader getClassLoader() {
		checkAlive();
		return m_classLoader;
	}

	public List<HandlerOptions> getHandlerConfigs() {
		checkAlive();
		return m_handlerConfigs;
	}
}
