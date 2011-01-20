/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.nv;

import javax.xml.namespace.QName;

/**
 * @author wdeng
 */
public final class NVPathPart extends QName {
	private final int m_index;
	private final boolean m_isAttribute;
	private final int m_localPartChecksum;

	public NVPathPart(QName qname, int index, boolean isAttribute) {
		this(qname.getNamespaceURI(), qname.getLocalPart(),
			index, isAttribute, calcChecksum(qname.getLocalPart()));
	}

	public NVPathPart(String namespace, String localPart,
		int index, boolean isAttribute, int localPartChecksum)
	{
		super(namespace, localPart, "");

		if (localPart == null || index < -1 || localPart.length() == 0) {
			throw new IllegalArgumentException();
		}

		m_index = index;
		m_isAttribute = isAttribute;
		m_localPartChecksum = localPartChecksum;
	}

	public int getIndex() {
		return m_index;
	}

	public boolean isAttribute() {
		return m_isAttribute;
	}

	public int getLocalPartChecksum() {
		return m_localPartChecksum;
	}

	public static int calcChecksum(String str) {
		if (str == null) {
			return 0;
		}

		int result = 0;
		int len = str.length();
		for (int i=0; i<len; i++) {
			result ^= str.charAt(i);
		}

		return result;
	}

	private static final long serialVersionUID = 835486766282112209L;
}
