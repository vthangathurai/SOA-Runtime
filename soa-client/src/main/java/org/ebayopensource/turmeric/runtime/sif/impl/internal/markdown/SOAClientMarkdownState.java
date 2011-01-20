/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown;

import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.internal.markdown.SOABaseMarkdownState;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownState;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;

/**
 * @author ichernyshev
 */
public final class SOAClientMarkdownState extends SOABaseMarkdownState<SOAClientMarkdownStateId> {

	private AutoMarkdownState m_autoState;
	private boolean m_isSvcLevelAutoMarkdown;

	public SOAClientMarkdownState(SOAClientMarkdownStateManager mgr,
		SOAClientMarkdownStateId id, MarkdownStateSnapshot<SOAClientMarkdownStateId> modelState)
	{
		super(mgr, id, modelState);
	}

	public SOAClientMarkdownState(SOAClientMarkdownStateManager mgr, SOAClientMarkdownStateId id)
	{
		super(mgr, id);
	}

	void setAutoMarkdownState(AutoMarkdownState autoState, boolean isSvcLevelAutoMarkdown) {
		synchronized (getLockObject()) {
			if (autoState != null && m_autoState != null) {
				autoState.copyStateFrom(m_autoState);
			}

			m_autoState = autoState;
			m_isSvcLevelAutoMarkdown = isSvcLevelAutoMarkdown;
		}
	}

	boolean isSvcLevelAutoMarkdown() {
		return m_isSvcLevelAutoMarkdown;
	}

	@Override
	protected void fillSnapshotAttrs(MarkdownStateSnapshot snapshot) {
		super.fillSnapshotAttrs(snapshot);

		if (m_autoState == null) {
			return;
		}

		Map<String,String> attrs = m_autoState.getSnapshotAttrs();
		if (attrs == null || attrs.isEmpty()) {
			return;
		}

		for (Map.Entry<String,String> e: attrs.entrySet()) {
			String name = e.getKey();
			String value = e.getValue();
			snapshot.setAttr(name, value);
		}
	}

	@Override
	protected void resetAutoMarkdownCounts() {
		super.resetAutoMarkdownCounts();

		if (m_autoState != null) {
			m_autoState.reset();
		}
	}

	@Override
	protected String countErrorInternal(Object ctxData, Object errorData) {
		try {
			if (m_autoState == null) {
				return null;
			}

			ClientMessageContext ctx = (ClientMessageContext)ctxData;
			Throwable e = (Throwable)errorData;

			m_autoState.countError(ctx, e);

			return m_autoState.getMarkdownReason();
		} catch (Throwable e2) {
			LogManager.getInstance(this.getClass()).log(Level.SEVERE,
				"Failure in countError on " + getId() + " while adding error " +
				errorData.toString() + ". Cause is " + e2.toString(), e2);
			return null;
		}
	}

	@Override
	protected void countSuccessInternal(Object ctxData) {
		try {
			if (m_autoState == null) {
				return;
			}

			ClientMessageContext ctx = (ClientMessageContext)ctxData;

			m_autoState.countSuccess(ctx);
		} catch (Throwable e2) {
			LogManager.getInstance(this.getClass()).log(Level.SEVERE,
				"Failure in countSuccess on " + getId() + ". Cause is " + e2.toString(), e2);
		}
	}
}
