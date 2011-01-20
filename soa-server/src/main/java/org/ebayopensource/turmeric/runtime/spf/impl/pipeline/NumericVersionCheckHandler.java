/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.NumericServiceVersion;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;


/**
 * @author
 */
public class NumericVersionCheckHandler implements VersionCheckHandler {

	private String m_version;
	private NumericServiceVersion m_numericVersion;

	public void init(InitContext ctx) throws ServiceException {
		m_version = ctx.getVersion();

		if (m_version == null) {
			// TODO: throw ServiceException here
			throw new NullPointerException();
		}

		m_numericVersion = NumericServiceVersion.valueOf(m_version);
		if (m_numericVersion == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_VERSION_FORMAT,
					ErrorConstants.ERRORDOMAIN, new Object[] {m_version, ctx.getServiceId().getAdminName()}));
		}
	}

	public String getVersion() {
		return m_version;
	}

	public boolean isVersionSupported(String versionStr)
		throws ServiceException
	{
		if (versionStr == null) {
			return false;
		}

		NumericServiceVersion numVersion = NumericServiceVersion.valueOf(versionStr);
		if (numVersion == null) {
			return false;
		}

		// Clients that are a newer version than us, are allowed, as long as the major
		// version is compatible (same).
		if (m_numericVersion.getMajorVersion() == numVersion.getMajorVersion()) {
			return true;
		}

		return false;
	}

	public void checkRequestVersion(MessageContext ctx)
		throws ServiceException
	{
		ServerMessageContext serverCtx = (ServerMessageContext) ctx;
		String versionStr = serverCtx.getInvokerVersion();

		if (versionStr == null || versionStr.trim().length() == 0) {
			// assume they understand our version
			return;
		}

		NumericServiceVersion numVersion = NumericServiceVersion.valueOf(versionStr);
		if (numVersion == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_INVALID_VERSION_FORMAT, 
					ErrorConstants.ERRORDOMAIN, new Object[] {versionStr}));
		}

		// Clients that are a newer version than us, are allowed, as long as the major
		// version is compatible (same).
		if (m_numericVersion.getMajorVersion() != numVersion.getMajorVersion()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_VERSION_UNSUPPORTED, 
					ErrorConstants.ERRORDOMAIN, new Object[] {versionStr}));
		}
	}
}
