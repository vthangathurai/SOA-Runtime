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
import org.ebayopensource.turmeric.runtime.sif.pipeline.ApplicationRetryHandler;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * @author ichernyshev
 */
public final class ApplicationRetryHandlerInitContextImpl extends BaseInitContext
	implements ApplicationRetryHandler.InitContext
{
	private final Collection<String> m_retryTransportCodes;
	private final Collection<String> m_retryExceptions;
	private final Collection<String> m_retryErrorIds;

	public ApplicationRetryHandlerInitContextImpl(ClientServiceId svcId,
		Collection<String> retryTransportCodes, Collection<String> retryExceptions,
		Collection<String> retryErrorIds)
	{
		super(svcId);

		if (retryTransportCodes != null) {
			m_retryTransportCodes = Collections.unmodifiableCollection(retryTransportCodes);
		} else {
			m_retryTransportCodes = CollectionUtils.EMPTY_STRING_SET;
		}

		if (retryExceptions != null) {
			m_retryExceptions = Collections.unmodifiableCollection(retryExceptions);
		} else {
			m_retryExceptions = CollectionUtils.EMPTY_STRING_SET;
		}
		
		if (retryErrorIds != null) {
			m_retryErrorIds = Collections.unmodifiableCollection(retryErrorIds);
		} else {
			m_retryErrorIds = CollectionUtils.EMPTY_STRING_SET;
		}
	}

	@Override
	public ClientServiceId getServiceId() {
		return (ClientServiceId)super.getServiceId();
	}

	public Collection<String> getRetryTransportCodes() {
		checkAlive();
		return m_retryTransportCodes;
	}

	public Collection<String> getRetryExceptions() {
		checkAlive();
		return m_retryExceptions;
	}

	public Collection<String> getRetryErrorIds() {
		checkAlive();
		return m_retryErrorIds;
	}

}
