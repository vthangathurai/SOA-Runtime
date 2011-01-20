/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.sif.service.InvokerExchange;


public class RawDispatchData extends InvokerExchange {
	private final boolean m_inboundRawMode;

	private final boolean m_outboundRawMode;

	private final List<Object> m_outParams;

	public RawDispatchData(boolean inboundRawMode, boolean outboundRawMode,
			Object[] inParams, List<Object> outParams,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper) {
		super(null, inParams, inWrapper, outWrapper);
		m_inboundRawMode = inboundRawMode;
		m_outboundRawMode = outboundRawMode;
		m_outParams = outParams;
	}

	public RawDispatchData(InvokerExchange ex) {
		super(ex);
		m_inboundRawMode = ex.getInWrapper() != null;
		m_outboundRawMode = ex.getOutWrapper() != null;
		m_outParams = null;
	}

	public RawDispatchData(RawDispatchData ex) {
		super(ex);
		m_inboundRawMode = ex.m_inboundRawMode;
		m_outboundRawMode = ex.m_outboundRawMode;
		m_outParams = ex.m_outParams;
	}

	public RawDispatchData(Object[] inParams, List<Object> outParams) {
		super(null, null, null, null);
		m_inboundRawMode = false;
		m_outboundRawMode = false;
		m_outParams = outParams;
	}

	public boolean isInboundRawMode() {
		return m_inboundRawMode;
	}

	public boolean isOutboundRawMode() {
		return m_outboundRawMode;
	}

	public List<Object> getOutParams() {
		return m_outParams;
	}

	public Object[] getInParams() {
		return super.m_inParams;
	}
}
