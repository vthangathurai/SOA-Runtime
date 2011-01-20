/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown;

import org.ebayopensource.turmeric.runtime.common.impl.internal.markdown.SOABaseMarkdownStateId;

import com.ebay.kernel.markdown.MarkdownStateRuntimeException;
import com.ebay.kernel.markdown.simple.SimpleMarkdownStateManager;

/**
 * @author ichernyshev
 */
public final class SOAClientMarkdownStateId extends SOABaseMarkdownStateId {

	private static final String[] ID_PART_NAMES = new String[] {null, "op", "cl"};

	public SOAClientMarkdownStateId(String adminName, String operationName, String clientName) {
		super(adminName, operationName, clientName);
	}

	public String getClientName() {
		return getSubname();
	}

	public String getStringId() {
		StringBuilder sb = new StringBuilder();
		buildBaseStringId(sb, "cl");
		return sb.toString();
	}

	static SOAClientMarkdownStateId parseId(String idStr) {
		SOAClientMarkdownStateId result = parseIdSafe(idStr);
		if (result == null) {
			throw new MarkdownStateRuntimeException("Unable to parse SOA Client markdown state id '" + idStr + "'");
		}
		return result;
	}

	static SOAClientMarkdownStateId parseIdSafe(String idStr) {
		String[] parts = SimpleMarkdownStateManager.parseId(idStr, ID_PART_NAMES);
		if (parts == null || parts.length != ID_PART_NAMES.length || parts[0] == null) {
			return null;
		}

		return new SOAClientMarkdownStateId(parts[0], parts[1], parts[2]);
	}
}
