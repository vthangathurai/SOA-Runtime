/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import java.io.IOException;
import java.io.InputStream;

import com.ebay.kernel.service.invocation.client.exception.ProcessingException;
import com.ebay.kernel.service.invocation.client.http.ReaderSelector.BodyReader;

public class DeferredBodyReader implements BodyReader {
	InputStream m_inputStream;

	public byte[] readBody(InputStream is) throws IOException,
			ProcessingException {
		m_inputStream = is;
		return null;
	}
	
	public InputStream getInputStream() {
		return m_inputStream;
	}

}
