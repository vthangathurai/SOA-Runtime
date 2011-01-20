/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import javax.xml.namespace.QName;

/**
 * @author wdeng
 */
public class IndexedQName {
	private final String m_namespace;
	private final String m_localPart;
	private final String m_prefix;
	private final int m_index;
	private QName m_qname;

	public IndexedQName(QName qname, int index) {
		this(qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), index);
		m_qname = qname;
	}

	public IndexedQName(String namespace, String localPart, String prefix, int index) {
		if (localPart == null || prefix == null || index < -1) {
			throw new IllegalArgumentException();
		}

		if (namespace != null) {
			m_namespace = namespace;
		} else {
			m_namespace = "";
		}

		m_localPart = localPart;
		m_prefix = prefix;
		m_index = index;
	}

	public int getIndex() {
		return m_index;
	}

	public QName getQName() {
		if (m_qname == null) {
			m_qname = new QName(m_namespace, m_localPart, m_prefix);
		}
		return m_qname;
	}
	
	public String getNamespaceURI() {
		return m_namespace;
	}

	public String getPrefix() {
		return m_prefix;
	}

	public String getLocalPart() {
		return m_localPart;
	}

	@Override
	public int hashCode() {
		return m_namespace.hashCode() ^ m_prefix.hashCode() ^ m_localPart.hashCode() ^ m_index;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof IndexedQName)) {
			return false;
		}

		IndexedQName other2 = (IndexedQName)other;
		if (m_index != other2.m_index) {
			return false;
		}

		return sameQName(other2.m_namespace, other2.m_localPart, other2.m_prefix);
	}

	public boolean sameQName(IndexedQName qname) {
		return sameQName(qname.m_namespace, qname.m_localPart, qname.m_prefix);
	}

	public boolean sameQName(QName qname) {
		return sameQName(qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix());
	}

	public boolean sameQName(String namespace, String localPart, String prefix) {
		// start with the checks that are likely to return false
		// check for hashCode explicitly, because "equals" does not compute it
		if (localPart == null || localPart.hashCode() != m_localPart.hashCode()) {
			return false;
		}

		// check localPart first, it's different in most of the cases
		if (!m_localPart.equals(localPart)) {
			return false;
		}

		return m_namespace.equals(namespace);
	}
}
