/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown;

import org.ebayopensource.turmeric.runtime.common.impl.internal.markdown.SOABaseMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;

import com.ebay.kernel.markdown.IMarkdownStateManagerInitCtx;
import com.ebay.kernel.markdown.MarkdownStateManagerFactory;
import com.ebay.kernel.markdown.MarkdownStateSnapshot;

/**
 * @author ichernyshev
 */
public final class SOAServerMarkdownStateManager
	extends SOABaseMarkdownStateManager<SOAServerMarkdownStateId,SOAServerMarkdownState>
{
	private static SOAServerMarkdownStateManager s_instance;

	public static synchronized SOAServerMarkdownStateManager getInstance() {
		if (s_instance == null) {
			SOAServerMarkdownStateManager mgr = new SOAServerMarkdownStateManager();
			MarkdownStateManagerFactory.registerManager(mgr, "turmeric_server");
			s_instance = mgr;
		}

		return s_instance;
	}

	private SOAServerMarkdownStateManager() {
		// only local instances
	}

	@Override
	public void init(IMarkdownStateManagerInitCtx<SOAServerMarkdownStateId> ctx) {
		super.init(ctx);
		ctx.setDisplayName("SOA Server-Side Services");
	}

	public MarkdownStateSnapshot<SOAServerMarkdownStateId> getMarkdownState(
		ServerServiceDesc svcDesc, String operation, boolean recordMissedCall)
	{
		SOAServerMarkdownStateId id = new SOAServerMarkdownStateId(svcDesc.getAdminName(),
			operation, svcDesc.getServiceId().getVersion());

		return checkMarkdownState(id, recordMissedCall);
	}

	public MarkdownStateSnapshot<SOAServerMarkdownStateId> getStateSnapshot(
		ServerServiceDesc svcDesc, String operation)
	{
		SOAServerMarkdownStateId id = new SOAServerMarkdownStateId(svcDesc.getAdminName(),
			operation, svcDesc.getServiceId().getVersion());

		return getStateSnapshot(id);
	}

	@Override
	protected SOAServerMarkdownState createDefaultState(SOAServerMarkdownStateId id) {
		return new SOAServerMarkdownState(this, id);
	}

	@Override
	protected SOAServerMarkdownState createState(SOAServerMarkdownStateId id,
		MarkdownStateSnapshot<SOAServerMarkdownStateId> modelState)
	{
		return new SOAServerMarkdownState(this, id, modelState);
	}

	@Override
	protected boolean doesStateIdInclude(SOAServerMarkdownStateId outer, SOAServerMarkdownStateId inner) {
		if (!super.doesStateIdInclude(outer, inner)) {
			return false;
		}

		String outerVersion = outer.getVersion();
		if (outerVersion != null) {
			String innerVersion = inner.getVersion();
			if (innerVersion == null || !innerVersion.equals(outerVersion)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isValidId(String idStr) {
		SOAServerMarkdownStateId id = SOAServerMarkdownStateId.parseIdSafe(idStr);
		if (id == null) {
			return false;
		}

		return true;
	}

	@Override
	public SOAServerMarkdownStateId parseId(String idStr) {
		return SOAServerMarkdownStateId.parseId(idStr);
	}

	@Override
	protected SOAServerMarkdownStateId createSoaStateId(String adminName, String opName, String subname) {
		return new SOAServerMarkdownStateId(adminName, opName, subname);
	}
}
