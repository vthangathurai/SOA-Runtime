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
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;

/**
 * @author ichernyshev
 */
public final class TransportInitContextImpl extends BaseInitContext
	implements Transport.InitContext
{
	private final String m_name;
	private final TransportOptions m_options;

	public TransportInitContextImpl(ServiceId svcId, String name, TransportOptions options) {
		super(svcId);

		if (name == null || options == null) {
			throw new NullPointerException();
		}

		m_name = name;
		m_options = options;
	}

	public String getName() {
		checkAlive();
		return m_name;
	}

	public TransportOptions getOptions() {
		checkAlive();
		return m_options;
	}
}
