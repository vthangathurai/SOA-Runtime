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

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownState;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownStateFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * @author ichernyshev
 */
public class DefaultAutoMarkdownStateFactory implements AutoMarkdownStateFactory {

	private ClientServiceId m_svcId;
	private ExceptionMatcher m_matcher;
	private int m_errCountThreshold;

	public void init(InitContext ctx) throws ServiceException {
		m_svcId = ctx.getServiceId();
		m_matcher = createMatcher(ctx.getTransportCodes(),
			ctx.getExceptions(), ctx.getErrorIds());
		m_errCountThreshold = ctx.getErrorCountThreshold();
	}

	protected ExceptionMatcher createMatcher(Collection<String> transportCodes,
		Collection<String> exceptions, Collection<String> errorIds)
		throws ServiceException
	{
		Collection<String> excludedTransportCodes = getExcludedTransportCodes();
		Collection<String> excludedExceptions = getExcludedExceptions();
		Collection<String> excludedErrorIds = getExcludedErrorIds();

		return new ExceptionMatcher(m_svcId, "AutoMarkdownStateFactory",
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
		return null;
	}

	public boolean isSvcLevelAutoMarkdown() {
		return true;
	}

	public AutoMarkdownState createAutoMarkdownState(String adminName,
		String opName, String clientName)
	{
		if (opName != null || clientName != null) {
			// no automatic partial markdown
			return null;
		}

		return new DefaultAutoMarkdownState(m_svcId, m_matcher, m_errCountThreshold);
	}
}
