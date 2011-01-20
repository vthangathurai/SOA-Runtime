/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;

/**
 * @author 
 */
public class SimpleVersionCheckHandler implements VersionCheckHandler {

	private String m_version;
	private Collection<String> m_supportedVersions;

	public void init(InitContext ctx) throws ServiceException
	{
		m_version = ctx.getVersion();
		m_supportedVersions = ctx.getSupportedVersion();

		if (m_version == null) {
			// TODO: throw ServiceException
			throw new NullPointerException();
		}
	}

	public String getVersion() {
		return m_version;
	}

	public void checkRequestVersion(MessageContext ctx)
		throws ServiceException
	{
		ServerMessageContext serverCtx = (ServerMessageContext) ctx;
		String versionStr = serverCtx.getInvokerVersion();
		
		if (versionStr != null) {
			if (!isVersionSupported(versionStr)) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_RT_VERSION_UNSUPPORTED, 
						ErrorConstants.ERRORDOMAIN, new Object[] {versionStr}));
			}
		}
	}
	
	public boolean isVersionSupported(String version) throws ServiceException {
		if (version == null) {
			return false;
		}

		if (m_supportedVersions.contains(version)) {
			return true;
		}
		return false;
	}
}
