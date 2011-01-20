/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown;

import org.ebayopensource.turmeric.runtime.common.impl.internal.markdown.SOABaseMarkdownState;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;

/**
 * @author ichernyshev
 */
public final class SOAServerMarkdownState extends SOABaseMarkdownState<SOAServerMarkdownStateId> {

	public SOAServerMarkdownState(SOAServerMarkdownStateManager mgr,
		SOAServerMarkdownStateId id, MarkdownStateSnapshot<SOAServerMarkdownStateId> modelState)
	{
		super(mgr, id, modelState);
	}

	public SOAServerMarkdownState(SOAServerMarkdownStateManager mgr, SOAServerMarkdownStateId id)
	{
		super(mgr, id);
	}
}
