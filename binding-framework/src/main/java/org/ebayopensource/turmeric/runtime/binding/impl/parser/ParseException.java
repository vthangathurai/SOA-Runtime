/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

/**
 * @author wdeng
 *
 */
public class ParseException extends RuntimeException {
    static final long serialVersionUID = -1;

	private String m_text;
	private int m_row;
	private int m_col;
	
	public ParseException(String text, int row, int col, Throwable t) {
		super(t);
		m_text = text;
		m_row = row;
		m_col = col;
	}
	
	public ParseException(String text, int row, int col, String msg) {
		super(msg);
		m_text = text;
		m_row = row;
		m_col = col;
	}
	
	@Override
	public String getMessage() {
		return "Parse error near (row: " + m_row + ", col: " + m_col 
			+ ") text: " + m_text + ": " + super.getMessage();
	}
	
	public String getText() {
		return m_text;
	}
	
	public int getRow() {
		return m_row;
	}
	
	public int getColumn () {
		return m_col;
	}
}
