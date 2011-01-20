/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.util.Collection;
import java.util.HashSet;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ApplicationRetryHandler;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

public class DefaultApplicationRetryHandler implements ApplicationRetryHandler {
	private ClientServiceId m_svcId;
	private ExceptionMatcher m_matcher;

	public void init(InitContext ctx) throws ServiceException {
		m_svcId = ctx.getServiceId();
		m_matcher = createMatcher(ctx.getRetryTransportCodes(),
			ctx.getRetryExceptions(), ctx.getRetryErrorIds());
	}

	protected ExceptionMatcher createMatcher(Collection<String> transportCodes,
		Collection<String> exceptions, Collection<String> errorIds)
		throws ServiceException
	{
		Collection<String> excludedTransportCodes = getExcludedTransportCodes();
		Collection<String> excludedExceptions = getExcludedExceptions();
		Collection<String> excludedErrorIds = getExcludedErrorIds();

		return new ExceptionMatcher(m_svcId, "AppRetryHandler",
			transportCodes, exceptions, errorIds,
			excludedTransportCodes, excludedExceptions, excludedErrorIds);
	}

	protected Collection<String> getExcludedTransportCodes() {
		return null;
	}

	protected Collection<String> getExcludedExceptions() {
		return null;
	}

	protected Collection<String> getExcludedErrorIds() {
		Collection<String> result = new HashSet<String>();
		result.add(String.valueOf(ErrorDataFactory.createErrorData(
				ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_SYS_CLIENT,
				ErrorConstants.ERRORDOMAIN).getErrorId()));
		result.add(String.valueOf(ErrorDataFactory.createErrorData(
				ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_SYS_SERVER,
				ErrorConstants.ERRORDOMAIN).getErrorId()));
		result.add(String.valueOf(ErrorDataFactory.createErrorData(
				ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_APP,
				ErrorConstants.ERRORDOMAIN).getErrorId()));

		return result;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.sif.pipeline.ApplicationRetryHandler#isRetryable(org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext, java.lang.Throwable)
	 */
	public boolean isRetryable(ClientMessageContext ctx, Throwable exception) {
		return m_matcher.getMatchingError(ctx, exception) != null;
	}
}
