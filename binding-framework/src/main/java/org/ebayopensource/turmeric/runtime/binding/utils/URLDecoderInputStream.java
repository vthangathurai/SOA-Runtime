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
import java.io.IOException;
import java.io.InputStream;

/**
 * URLDecoderInputStreamReader URL decodes the input stream except for the character '&' and '%'.
 * 
 * @author wdeng
 *
 */
public class URLDecoderInputStream extends InputStream implements Closeable{

	private final static int NO_CHAR = -1;

	private InputStream m_is;
	private int[] m_unreadChar = {NO_CHAR, NO_CHAR};
	
	/**
	 * Constructor with InputStream as argument.
	 * 
	 * @param is  an InputStream.
	 */
	public URLDecoderInputStream(InputStream is) {
		m_is = is;
	}
	
	@Override
	public int read() throws IOException {
		int c = readFromUnreadBuffer();
		if (c == NO_CHAR) {
			c = m_is.read();
		}
		
		if (c == NO_CHAR) {
			return c;
		}
		
		// assume that any '+' is really a space
		if (c == '+') {
			return ' ';
		}

		// do HTTP URL decoding
		if (c == '%') {
			int c1 = m_is.read();
			if (c1 == NO_CHAR) {
				return c;
			}
			int b1 = BindingUtils.getHexDigitValue(c1);
			if (b1 == NO_CHAR) {
				unread(c1);
				return c;
			}
			int c2 = m_is.read();
			if (c2 == NO_CHAR) {
				unread(c1);
				return c;
			}

			int b2 = BindingUtils.getHexDigitValue(c2);
			if (b2 == -1) {
				unread(c1);
				unread(c2);
				return c;
			}

			char ch = (char) ((b1<<4)|b2);
			if (ch == '&' || ch == '%') {
				unread(c1);
				unread(c2);
				return c;
			}
			return ch;
		}

		return c;
	}

	private int readFromUnreadBuffer() {
		int c = m_unreadChar[0];
		m_unreadChar[0] = m_unreadChar[1];
		m_unreadChar[1] = NO_CHAR;
		return c;
	}
	
	private void unread(int ch) {
		if (m_unreadChar[0] == NO_CHAR) {
			m_unreadChar[0] = ch;
			return;
		}
		m_unreadChar[1] = ch;
	}
}
