/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.utils;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class records raw data for outbound message.
 * 
 * @author wdeng
 */
public class OutboundRawDataRecorder extends FilterOutputStream {
	private ByteArrayOutputStream m_rawData;
	private long m_length;
	private int m_maxBytes;
	private boolean m_countOnly;
	private boolean m_shouldCount;

	public OutboundRawDataRecorder(OutputStream os, boolean countOnly, int maxBytes) {
    	super(os);
    	m_countOnly = countOnly;
    	m_length = 0;
    	if (maxBytes > 0) {
    		if (maxBytes > 65536) {
    			maxBytes = 65536;
    		}

    		m_maxBytes = maxBytes;
    	} else {
    		throw new IllegalArgumentException("Maximum payload bytes must be a positive integer");
    	}
    	m_shouldCount = false;
    	if (!m_countOnly) {
    		m_rawData = new ByteArrayOutputStream(m_maxBytes);
    		m_shouldCount = true;
    	}
    }

	@Override
    public void write(int b) throws IOException {
    	m_length++;
    	if (m_shouldCount) {
    		m_rawData.write(b);
    		if (m_length >= m_maxBytes) {
    			m_shouldCount = false;
    		}
    	}

    	super.write(b);
    }

    // Duplicate recording if we do this - this version of write() in the base class is
    // already calling write(int b), which is doing the necessary recording. 
//    public void write(byte b[], int off, int len) throws IOException {
//    	m_length+=len;
//    	if (!m_countOnly) {
//    		m_rawData.write(b, off, len);
//    	}
//    	super.write(b, off, len);
//    }

    public byte[] getRawByteData() {
    	if (m_rawData == null) {
    		throw new IllegalArgumentException("Cannot return data buffer in count-only mode");
    	}
    	return m_rawData.toByteArray();
    }
    
    public String getRawStringData() {
    	if (m_rawData == null) {
    		throw new IllegalArgumentException("Cannot return data buffer in count-only mode");
    	}
    	return m_rawData.toString();
    }
    public long getLength() {
    	return m_length;
    }
    
    public boolean isCountOnly() {
    	return m_countOnly;
    }
}
