/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.common.impl.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Pre-reading recorder.  On the first read call, will try to pre-read up to <code>maxBytes</code> bytes of data from the inner
 * stream.  Then, all reads will occur out of that pre-read buffer, until it is exhausted, and reading will continue to the
 * inner stream.
 *
 */
public class PrereadingRawDataRecorder extends FilterInputStream {
	private byte[] m_prereadBuffer;					// preread buffer, null if we haven't preread yet
	private ByteArrayInputStream m_prereadStream;	// preread stream, null if we're done using it (or haven't preread yet)
	private int m_length;							// number of bytes actually in the preread buffer/stream, could be < max.
	private int m_maxBytes;							// maximum number of bytes to buffer
	
    public PrereadingRawDataRecorder(InputStream is, int maxBytes) {
    	super(is);
    	m_length = 0;
    	if (maxBytes < 0) {
    		throw new IllegalArgumentException("Maximum payload bytes must be a positive integer");
    	}
    	if (maxBytes > 65536) {
    		maxBytes = 65536;
    	}
    	m_maxBytes = maxBytes;
    }

	@Override
    public int read() throws IOException {
    	preread();
    	if (m_prereadStream == null) {
    		// no more buffer data, delegate to the inner stream in case there is more data beyond the buffered part
    		return super.read();
    	}
    	int ch = m_prereadStream.read();
		if (ch == -1) {
			// no more buffer data, delegate
			m_prereadStream = null;
			return super.read();
		}
		return ch;
    }

	@Override
    public int read(byte b[], int off, int len) throws IOException {
    	if (b == null) {
    	    throw new NullPointerException();
    	}
    	preread();
    	if (m_prereadStream == null) {
    		// no more buffer data, delegate
    		return super.read(b, off, len);
    	}
    	int bytesRead = m_prereadStream.read(b, off, len);
    	if (bytesRead == -1) {
    		m_prereadStream = null;
    		return super.read(b, off, len);
    	} else if (bytesRead < len) {
    		// part of the data came from the buffer, try delegating to inner stream and sum the two return counts.
    		m_prereadStream = null;
    		int streamBytesRead = super.read(b, off + bytesRead, len - bytesRead);
    		if (streamBytesRead == -1) {
    			return bytesRead;
    		}
   			return bytesRead + streamBytesRead;
    	}

    	return bytesRead;
    }
    
	@Override
    public int available() throws IOException {
    	preread();
    	if (m_prereadStream == null) {
    		return super.available();
    	}

    	return m_prereadStream.available() + super.available();
    }

	@Override
    public boolean markSupported() {
    	return false;
    }
    
	@Override
    public synchronized void reset() throws IOException {
    	throw new IOException("mark/reset not supported");
    }

    public byte[] getRawByteData() throws IOException {
    	preread();
    	byte result[] = new byte[m_length];
    	System.arraycopy(m_prereadBuffer, 0, result, 0, m_length);
    	return result;
    }
    
    public long getLength() {
    	return m_length;
    }
    
    private void preread() throws IOException {
    	if (m_prereadBuffer != null) {
    		return;
    	}
    	m_prereadBuffer = new byte[m_maxBytes];
    	boolean isEof = false;
    	while (!isEof && m_length < m_maxBytes) {
    		int bytesRead = super.read(m_prereadBuffer, m_length, m_maxBytes - m_length);
    		if (bytesRead == -1) {
    			isEof = true;
    		} else {
    			m_length += bytesRead;
    		}
    	}
    	m_prereadStream = new ByteArrayInputStream(m_prereadBuffer, 0, m_length);
    }
}
