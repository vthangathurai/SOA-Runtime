/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;

import java.nio.ByteBuffer;

/**
 * Simple wrapper to store/retrieve the associated ByteBuffer.
 * @author rpallikonda
 *
 */
public class ByteBufferWrapper {

	private ByteBuffer m_byteBuffer;

	/**
	 * @return A ByteBuffer.
	 */
	public ByteBuffer getByteBuffer() {
		return m_byteBuffer;
	}

	/**
	 * Sets the ByteBuffer.
	 * @param b A ByteBuffer.
	 */
	public void setByteBuffer(ByteBuffer b) {
		m_byteBuffer = b;
	}
}
