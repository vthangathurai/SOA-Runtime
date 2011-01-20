/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds relevant response information for pseudo-operations such as ?wsdl.  
 * @author rmurphy
 */
public class ResponseMetaContext {
	private final Map<String, String> m_transportHeaders;
	private OutputStream m_outputStream;
	private String m_contentType;

	/**
	 * Constructor.
	 * @param outputStream an OutputStream.
	 */
	public ResponseMetaContext(OutputStream outputStream) {
		if (outputStream == null) {
			throw new NullPointerException();
		}
		m_transportHeaders = new HashMap<String, String>();
		m_outputStream = outputStream;
	}

	/**
	 * Returns the transport header map to be set into the outbound response.
	 * @return the transport header amp
	 */
	public Map<String, String> getTransportHeaders() {
		return m_transportHeaders;
	}

	/**
	 * Returns the MIME type to be set into the outbound response.
	 * @return the content type
	 */
	public String getContentType() {
		return m_contentType;
	}

	/**
	 * Sets the MIME type of the outbound response.
	 * @param type the content type
	 */
	public void setContentType(String type) {
		m_contentType = type;
	}

	/**
	 * Returns the output stream associated with the outbound response.
	 * @return the output stream
	 */
	public OutputStream getOutputStream() {
		return m_outputStream;
	}
	
	
}
