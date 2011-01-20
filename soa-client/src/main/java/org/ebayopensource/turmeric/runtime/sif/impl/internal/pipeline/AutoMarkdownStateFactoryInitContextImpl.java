/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.util.Collection;
import java.util.Collections;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownStateFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * @author ichernyshev
 */
public final class AutoMarkdownStateFactoryInitContextImpl extends BaseInitContext
	implements AutoMarkdownStateFactory.InitContext
{
	private final Collection<String> m_transportCodes;
	private final Collection<String> m_exceptions;
	private final Collection<String> m_errorIds;
	private final int m_errorCountThreshold;

	public AutoMarkdownStateFactoryInitContextImpl(ClientServiceId svcId,
		Collection<String> transportCodes, Collection<String> exceptions, Collection<String> errorIds,
		int errorCountThreshold)
	{
		super(svcId);

		if (transportCodes != null) {
			m_transportCodes = Collections.unmodifiableCollection(transportCodes);
		} else {
			m_transportCodes = CollectionUtils.EMPTY_STRING_SET;
		}

		if (exceptions != null) {
			m_exceptions = Collections.unmodifiableCollection(exceptions);
		} else {
			m_exceptions = CollectionUtils.EMPTY_STRING_SET;
		}
		
		if (errorIds != null) {
			m_errorIds = Collections.unmodifiableCollection(errorIds);
		} else {
			m_errorIds = CollectionUtils.EMPTY_STRING_SET;
		}

		m_errorCountThreshold = errorCountThreshold;
	}

	@Override
	public ClientServiceId getServiceId() {
		return (ClientServiceId)super.getServiceId();
	}

	public Collection<String> getTransportCodes() {
		checkAlive();
		return m_transportCodes;
	}

	public Collection<String> getExceptions() {
		checkAlive();
		return m_exceptions;
	}

	public Collection<String> getErrorIds() {
		checkAlive();
		return m_errorIds;
	}

	public int getErrorCountThreshold() {
		checkAlive();
		return m_errorCountThreshold;
	}
}
