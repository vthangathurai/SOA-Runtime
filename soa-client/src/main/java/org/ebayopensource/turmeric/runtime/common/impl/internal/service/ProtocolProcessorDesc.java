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
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;


/**
 * @author ichernyshev
 */
public final class ProtocolProcessorDesc {

	private final String m_name;
	private final ProtocolProcessor m_processor;
	private final Set<String> m_supportedPayloads;

	public ProtocolProcessorDesc(String name, ProtocolProcessor processor,
		Set<String> supportedPayloads)
	{
		if (name == null || processor == null) {
			throw new NullPointerException();
		}

		m_name = name;
		m_processor = processor;

		if (supportedPayloads != null && !supportedPayloads.isEmpty()) {
			m_supportedPayloads = Collections.unmodifiableSet(supportedPayloads);
		} else {
			m_supportedPayloads = null;
		}
	}

	public String getName() {
		return m_name;
	}

	public ProtocolProcessor getProcessor() {
		return m_processor;
	}

	public boolean isPayloadSupported(String payload) {
		if (m_supportedPayloads == null) {
			// NULL set means ALL
			return true;
		}

		return m_supportedPayloads.contains(payload);
	}
}
