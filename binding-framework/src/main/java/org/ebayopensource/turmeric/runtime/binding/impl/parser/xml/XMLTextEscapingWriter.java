/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.BaseEscapingWriter;


/**
 * @author wdeng
 */
public final class XMLTextEscapingWriter extends BaseEscapingWriter {
	
	public XMLTextEscapingWriter(Writer writer, String encoding) {
		super(writer, encoding);
	}
	
	public XMLTextEscapingWriter(OutputStream os, String encoding) {
		super(os, encoding);
	}
	
	@Override
	protected void writeEscapedChar(Writer w, char c) throws IOException {
		switch (c) {
			case '>': 
				w.write("&gt;");
				return;
			case '<':
				w.write("&lt;");
				return;
			case '&':
				w.write("&amp;");
				return;
			default:
				w.write(c);
		}
	}
}
