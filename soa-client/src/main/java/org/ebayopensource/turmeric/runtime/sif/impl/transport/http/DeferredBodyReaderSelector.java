/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import com.ebay.kernel.service.invocation.client.http.ReaderSelector;
import com.ebay.kernel.service.invocation.client.http.Response;

public class DeferredBodyReaderSelector implements ReaderSelector {
	private DeferredBodyReader m_reader;
	public BodyReader choose(Response handler) {
		return getReader();
	}
	
	public DeferredBodyReader getReader() {
		if (m_reader == null) {
			m_reader = new DeferredBodyReader();
		}
		return m_reader;
	}
}
