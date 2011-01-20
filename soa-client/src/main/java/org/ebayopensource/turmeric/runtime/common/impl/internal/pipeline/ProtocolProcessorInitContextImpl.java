/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;

/**
 * @author ichernyshev
 */
public final class ProtocolProcessorInitContextImpl extends BaseInitContext
	implements ProtocolProcessor.InitContext
{
	private final String m_name;
	private final String m_version;

	public ProtocolProcessorInitContextImpl(ServiceId svcId, String name, String version) {
		super(svcId);

		if (name == null) {
			throw new NullPointerException();
		}

		m_name = name;
		m_version = version;
	}

	public String getName() {
		checkAlive();
		return m_name;
	}

	public String getVersion() {
		checkAlive();
		return m_version;
	}
}
