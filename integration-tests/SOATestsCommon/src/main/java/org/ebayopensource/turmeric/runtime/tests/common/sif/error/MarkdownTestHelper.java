/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownState;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateId;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateId;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateManager;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;

/**
 * @author ichernyshev
 */
public final class MarkdownTestHelper {

	private MarkdownTestHelper() {
		// no instances
	}

	@SuppressWarnings("rawtypes")
	public static MarkdownStateSnapshot getClientState(String adminName,
		String clientName, String opName)
	{
		return SOAClientMarkdownStateManager.getInstance().getStateSnapshot(
			new SOAClientMarkdownStateId(adminName, opName, clientName));
	}

	public static void markupClientManually(String adminName, String clientName, String opName) {
		SOAClientMarkdownStateManager.getInstance().markUpManually(
			new SOAClientMarkdownStateId(adminName, opName, clientName));
		SOAClientMarkdownState state = SOAClientMarkdownStateManager.getInstance().getState(
			new SOAClientMarkdownStateId(adminName, opName, clientName));
		state.resetAutoMarkdownData();
	}

	public static void markdownClientManually(String adminName, String clientName, String opName) {
		SOAClientMarkdownStateManager.getInstance().markDownManually(
			new SOAClientMarkdownStateId(adminName, opName, clientName));
	}

	@SuppressWarnings("rawtypes")
	public static MarkdownStateSnapshot getServerState(String adminName, String opName)
	{
		return SOAServerMarkdownStateManager.getInstance().getStateSnapshot(
			new SOAServerMarkdownStateId(adminName, opName, null));
	}

	public static void markupServerManually(String adminName, String opName) {
		SOAServerMarkdownStateManager.getInstance().markUpManually(
			new SOAServerMarkdownStateId(adminName, opName, null));
	}

	public static void markdownServerManually(String adminName, String opName) {
		SOAServerMarkdownStateManager.getInstance().markDownManually(
			new SOAServerMarkdownStateId(adminName, opName, null));
	}
}
