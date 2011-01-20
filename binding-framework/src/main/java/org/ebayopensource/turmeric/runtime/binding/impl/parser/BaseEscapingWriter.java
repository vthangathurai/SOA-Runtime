/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import org.ebayopensource.turmeric.runtime.binding.utils.BufferedCharWriter;


/**
 * @author wdeng
 */
public abstract class BaseEscapingWriter extends Writer {
	
	private Writer m_writer;

	public BaseEscapingWriter(Writer writer, String encoding) {
		m_writer = writer;
	}

	public BaseEscapingWriter(OutputStream os, String encoding) {
		Charset charset = Charset.forName(encoding);
		m_writer = new BufferedCharWriter(os, charset, 2048);
	}

	
	protected abstract void writeEscapedChar(Writer w, char c) throws IOException;
	
	@Override
	public void write(String str, int off, int len) throws IOException {
		int strlen = str.length();
		if (off + len > strlen) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = 0; i < str.length(); i++) {
			write(str.charAt(i));
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		int bufsize = cbuf.length;
		if (off + len > bufsize) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = off; i < len; i++) {
			write(cbuf[i]);
		}
	}

	public void write(char b) throws IOException {
		if (b > 255) {
			m_writer.write(b);
			return;
		}
		writeEscapedChar(m_writer, b);
	}

	@Override
	public void close() throws IOException {
		m_writer.close();
	}

	@Override
	public void flush() throws IOException {
		m_writer.flush();
	}
}
