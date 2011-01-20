/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.service;

import java.nio.charset.Charset;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceBrowserCompStatus;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateManager;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;
import com.ebay.kernel.util.xml.IXmlStreamWriter;
import com.ebay.kernel.util.xml.XmlStreamUtil;

/**
 * @author ichernyshev
 */
final class ServerServiceBrowserCompStatus extends BaseServiceBrowserCompStatus {

	public ServerServiceBrowserCompStatus() {
		super("TurmericServerBrowser", false, "Server");
	}

	@Override
	protected MarkdownStateSnapshot getMarkdownStatus(ServiceDesc desc, String operation) {
		return SOAServerMarkdownStateManager.getInstance().getStateSnapshot((ServerServiceDesc)desc, operation);
	}

	@Override
	protected void renderServiceDescAttrs(ServiceDesc desc, boolean isFullView,
		IXmlStreamWriter xmlWriter, Map<String, String> props)
	{
		// nothing to add
	}

	@Override
	protected void renderServiceDescElements(ServiceDesc desc,
		boolean isFullView, IXmlStreamWriter xmlWriter,
		Map<String, String> props)
	{
		ServerServiceDesc desc2 = (ServerServiceDesc)desc;

		String serviceImplClassName = desc2.getServiceImplClassName();
		XmlStreamUtil.safeWriteChildCData(xmlWriter, "impl-class", serviceImplClassName);

		Charset serviceCharset = desc2.getServiceCharset();
		if (serviceCharset != null) {
			XmlStreamUtil.safeWriteChildCData(xmlWriter, "service-charset", serviceCharset.toString());
		}

		if (!isFullView) {
			return;
		}

		// TODO: add these
		//ErrorMapper errorMapper = desc2.getErrorMapper();
		//Map<String, GlobalIdDesc> globalIdMap = desc2.getGlobalIds();
		//VersionCheckHandler versionCheckHandler = desc2.getVersionCheckHandler();
	}
}
