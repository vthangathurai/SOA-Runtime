/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline;

import java.util.Collection;
import java.util.Collections;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;


/**
 * @author ichernyshev
 */
public final class VersionCheckHandlerInitContextImpl extends BaseInitContext
	implements VersionCheckHandler.InitContext
{
	private final String m_version;
	private final Collection<String> m_supportedVersion;

	public VersionCheckHandlerInitContextImpl(ServerServiceId svcId, String version,
		Collection<String> supportedVersion)
	{
		super(svcId);

		m_version = version;

		if (supportedVersion != null) {
			m_supportedVersion = Collections.unmodifiableCollection(supportedVersion);
		} else {
			m_supportedVersion = CollectionUtils.EMPTY_STRING_SET;
		}
	}

	@Override
	public ServerServiceId getServiceId() {
		return (ServerServiceId)super.getServiceId();
	}

	public String getVersion() {
		checkAlive();
		return m_version;
	}

	public Collection<String> getSupportedVersion() {
		checkAlive();
		return m_supportedVersion;
	}
}
