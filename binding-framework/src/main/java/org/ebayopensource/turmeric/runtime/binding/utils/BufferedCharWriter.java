/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Write text to a character-output stream, buffering characters so as to
 * provide for the efficient writing of single characters, arrays, and strings.
 * <p> The buffer size may be specified
 * <p> In general, a Writer sends its output immediately to the underlying
 * character or byte stream.  Unless prompt output is required, it is advisable
 * to wrap a BufferedWriter around any Writer whose write() operations may be
 * costly, such as FileWriters and OutputStreamWriters.
 *
 * @author ichernyshev
 */
public final class BufferedCharWriter extends Writer implements Closeable, Flushable {

	private Writer m_writer;
	private char[] m_buf;
	private int m_size;
	
	/**
     * Create a new buffered character-output stream writer that uses an output
     * buffer of the given size and supports the specified CharSet.
     *
     * @param  os  OutputStream
     * @param  charset  CharSet 
     * @param  capacity  Output-buffer size, a positive integer
	 *
     */
	public BufferedCharWriter(OutputStream os, Charset charset, int capacity) {
		if (os == null) {
			throw new NullPointerException();
		}

		if (capacity <= 0) {
			throw new IllegalArgumentException();
		}

		m_writer = new OutputStreamWriter(os, charset);
		m_buf = new char[capacity];
	}

	private void ensureOpen() throws IOException {
		if (m_writer == null) {
			throw new IOException("Stream closed");
		}
	}
	
	/**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
	@Override
	public void close() throws IOException {
		if (m_writer == null) {
			return;
		}

		flushBuffer();
		m_writer.close();
		m_writer = null;
	}

	/**
     * Flush the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
	@Override
	public void flush() throws IOException {
		flushBuffer();
		m_writer.flush();
	}
	
	private void flushBuffer() throws IOException {
		ensureOpen();
		if (m_size != 0) {
			m_writer.write(m_buf, 0, m_size);
			m_size = 0;
		}
	}

	/**
     * Write the entire String.
     *  
     * @param  str   String to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
	@Override
	public void write(String str) throws IOException {
		write(str, 0, str.length());
	}
	
	/**
     * Write a portion of a String.
     *  
     * @param  str   String to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
	@Override
	public void write(String str, int off, int len) throws IOException {
		int strlen = str.length();
		if (off + len > strlen) {
			throw new IndexOutOfBoundsException();
		}
		
		int lenThisRound = len;
		int bufSize = m_buf.length - m_size;
		while (len > 0) {
			lenThisRound = len;
			if (m_size + lenThisRound >= m_buf.length) {
				flushBuffer();
				bufSize = m_buf.length;
			}

			if (lenThisRound > bufSize) {
				lenThisRound = bufSize;
			}
			str.getChars(off, off + lenThisRound, m_buf, m_size);
			m_size += lenThisRound;
			len -= lenThisRound;
			off += lenThisRound;
		}
	}

	/**
     * Write a character.
     *  
     * @param  b  character to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
	public void write(char b) throws IOException {
		if (m_size == m_buf.length) {
			flushBuffer();
		}
		m_buf[m_size] = b;
		m_size++;
	}

	/**
     * Write a portion of an array of characters.
     *  
     * @param  cbuf  A character array
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		int bufsize = cbuf.length;
		if (off + len > bufsize) {
			throw new IndexOutOfBoundsException();
		}
		int lenThisRound = len;
		int bufSize = m_buf.length - m_size;
		while (len > 0) {
			lenThisRound = len;
			if (m_size + lenThisRound >= m_buf.length) {
				flushBuffer();
				bufSize = m_buf.length;
			}

			if (lenThisRound > bufSize) {
				lenThisRound = bufSize;
			}
			System.arraycopy(cbuf, off, m_buf, m_size, lenThisRound);
			m_size += lenThisRound;
			len -= lenThisRound;
			off += lenThisRound;
		}
	}
}
