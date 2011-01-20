/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownState;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * @author ichernyshev
 */
public class DefaultAutoMarkdownState implements AutoMarkdownState {

	//private final ClientServiceId m_svcId;
	private final ExceptionMatcher m_matcher;
	private final int m_errCountThreshold;
	private int m_errCount;
	private String m_markdownReason;

	public DefaultAutoMarkdownState(ClientServiceId svcId,
		ExceptionMatcher matcher, int errCountThreshold)
	{
		if (svcId == null || matcher == null) {
			throw new NullPointerException();
		}

		//m_svcId = svcId;

		m_matcher = matcher;

		if (errCountThreshold > 0) {
			m_errCountThreshold = errCountThreshold;
		} else {
			m_errCountThreshold = 10;
		}
	}

	public void copyStateFrom(AutoMarkdownState other) {
		m_markdownReason = other.getMarkdownReason();
	}

	public void reset() {
		m_markdownReason = null;
		m_errCount = 0;
	}

	public String getMarkdownReason() {
		return m_markdownReason;
	}

	public Map<String,String> getSnapshotAttrs() {
		if (m_errCount != 0) {
			Map<String,String> result = new HashMap<String,String>();
			result.put("err_count", String.valueOf(m_errCount));
			return result;
		}

		return null;
	}

	public void countSuccess(ClientMessageContext ctx) throws ServiceException {
		m_errCount = 0;
	}

	public void countError(ClientMessageContext ctx, Throwable e) throws ServiceException {
		String errorStr = m_matcher.getMatchingError(ctx, e);
		if (errorStr == null) {
			return;
		}

		m_errCount++;

		if (m_errCount >= m_errCountThreshold && m_markdownReason == null) {
			m_markdownReason = "Exception " + errorStr;
		}
	}
}
