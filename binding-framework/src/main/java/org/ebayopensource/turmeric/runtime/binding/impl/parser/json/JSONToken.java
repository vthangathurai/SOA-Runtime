/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.json;

/**
 * 
 * @author wdeng
 *
 */
class JSONToken {
	
	static final JSONToken END_TOKEN = new JSONToken(JSONTokenType.END, 0, 0);
	JSONTokenType m_type;

	char[] m_chars;

	int m_line;

	int m_column;
	
	int m_prefixEnd;
	
	private String m_text = null;
	private String m_prefix = null;
	private String m_name = null;

	JSONToken(JSONTokenType type, int line, int column) {
		m_type = type;
		m_line = line;
		m_column = column;
	}
	
	String getText() {
		if (null == m_text) {
			m_text = new String(m_chars, 0, m_chars.length);
		}
		return m_text;
	}
	boolean hasPrefix() {
		return m_prefixEnd > 0;
	}
	
	String getPrefix() {
		if (m_prefixEnd > 0) {
			m_prefix = new String(m_chars, 0, m_prefixEnd);
		}
		return m_prefix;
	}

	String getName() {
		if (m_prefixEnd > 0) {
			m_name = new String(m_chars, m_prefixEnd + 1, m_chars.length - m_prefixEnd - 1);
		}
		return m_name;
	}
	
	@Override
	public String toString() {
		return "Text   : " + m_text + "\nType : " + m_type + "\nline  : "
				+ m_line + "\ncBeg. : " + m_column;
	}
}
