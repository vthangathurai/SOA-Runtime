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
package org.ebayopensource.turmeric.runtime.binding.impl.parser.xml;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.codehaus.stax2.io.EscapingWriterFactory;

/**
 * @author wdeng
 *
 */
public class XMLTextEscapingWriterFactory implements EscapingWriterFactory {

	/* (non-Javadoc)
	 * @see org.codehaus.stax2.io.EscapingWriterFactory#createEscapingWriterFor(java.io.Writer, java.lang.String)
	 */
	public Writer createEscapingWriterFor(Writer w, String enc)
			throws UnsupportedEncodingException {
		return new XMLTextEscapingWriter(w, enc);
	}

	/* (non-Javadoc)
	 * @see org.codehaus.stax2.io.EscapingWriterFactory#createEscapingWriterFor(java.io.OutputStream, java.lang.String)
	 */
	public Writer createEscapingWriterFor(OutputStream out, String enc)
			throws UnsupportedEncodingException {
		return new XMLTextEscapingWriter(out, enc);
	}

}
