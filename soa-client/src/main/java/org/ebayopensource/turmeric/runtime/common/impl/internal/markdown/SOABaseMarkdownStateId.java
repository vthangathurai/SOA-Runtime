/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.markdown;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;

import com.ebay.kernel.markdown.IMarkdownStateId;

/**
 * @author ichernyshev
 */
public abstract class SOABaseMarkdownStateId implements IMarkdownStateId {

	protected final String m_adminName;
	protected final String m_operationName;
	protected final String m_subname;
	private int m_hashCode;

	public SOABaseMarkdownStateId(String adminName, String operationName, String subname)
	{
		if (adminName == null) {
			throw new NullPointerException();
		}

		m_adminName = adminName;
		m_operationName = operationName;
		m_subname = subname;
	}

	public final String getAdminName() {
		return m_adminName;
	}

	public final String getOperationName() {
		return m_operationName;
	}

	public final String getSubname() {
		return m_subname;
	}

	public final boolean isAdminState() {
		return (m_subname == null && m_operationName == null);
	}

	protected final void buildBaseStringId(StringBuilder sb, String subnameIdName) {
		sb.append(m_adminName);
		if (m_operationName != null) {
			sb.append(";op:");
			sb.append(m_operationName);
		}
		if (m_subname != null) {
			sb.append(';');
			sb.append(subnameIdName);
			sb.append(':');
			sb.append(m_subname);
		}
	}

	@Override
	public final String toString() {
		return getClass().getSimpleName() + "(" + getStringId() + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof SOABaseMarkdownStateId)) {
			return false;
		}

		if (other == this) {
			return true;
		}

		SOABaseMarkdownStateId other2 = (SOABaseMarkdownStateId)other;
		return m_adminName.equals(other2.m_adminName) &&
			BindingUtils.sameObject(m_operationName, other2.m_operationName) &&
			BindingUtils.sameObject(m_subname, other2.m_subname);
	}

	@Override
	public int hashCode() {
		if (m_hashCode == 0) {
			int hashCode = m_adminName.hashCode();
			if (m_operationName != null) {
				hashCode ^= m_operationName.hashCode();
			}
			if (m_subname != null) {
				hashCode ^= m_subname.hashCode();
			}
			m_hashCode = hashCode;
			return hashCode;
		}

		return m_hashCode;
	}
}
