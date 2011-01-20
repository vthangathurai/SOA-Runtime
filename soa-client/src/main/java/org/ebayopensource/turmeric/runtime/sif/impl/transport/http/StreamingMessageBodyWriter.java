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
import java.io.OutputStream;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;

import com.ebay.kernel.service.invocation.client.exception.ProductionException;
import com.ebay.kernel.service.invocation.client.http.RequestBodyWriter;
import com.ebay.kernel.service.invocation.transport.http.ChunkedOutputStream;

/**
 * Supports streaming of outbound message data through a ChunkedOutputStream.
 * @author rmurphy, wdeng
 */
public class StreamingMessageBodyWriter implements RequestBodyWriter {
    final private OutboundMessage m_message;

    /**
     * Constructor; takes an OutboundMessage (e.g. client request message
     * with body data and possibly attachments).
     *
     * @param message the OutboundMessage with the data to send 
     */
    public StreamingMessageBodyWriter(OutboundMessage message) {
        m_message = message;
    }

    /* (non-Javadoc)
     * @see com.ebay.kernel.service.invocation.client.http.RequestBodyWriter#contentLength()
     */
    public int contentLength() {
        return -1;
    }

    /* (non-Javadoc)
     * @see com.ebay.kernel.service.invocation.client.http.RequestBodyWriter#writeBody(java.io.OutputStream)
     */
    public void writeBody(OutputStream out) throws ProductionException, IOException
    {
    	try {
			m_message.serialize(out);
			if (out instanceof ChunkedOutputStream) {
				ChunkedOutputStream cos = (ChunkedOutputStream)out;
				cos.finish();
			}
		} catch (ServiceException e) {
			throw new IOException(e.toString());
		}
    }
    
}
