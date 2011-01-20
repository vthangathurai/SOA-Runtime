/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.net.URL;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceBrowserCompStatus;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateManager;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;
import com.ebay.kernel.util.xml.IXmlStreamWriter;
import com.ebay.kernel.util.xml.XmlStreamUtil;

/**
 * @author ichernyshev
 */
final class ClientServiceBrowserCompStatus extends BaseServiceBrowserCompStatus {

	public ClientServiceBrowserCompStatus() {
		super("TurmericClientBrowser", true, "Client");
	}

	@Override
	protected MarkdownStateSnapshot getMarkdownStatus(ServiceDesc desc, String operation) {
		return SOAClientMarkdownStateManager.getInstance().getStateSnapshot((ClientServiceDesc)desc, operation);
	}

	@Override
	protected void renderServiceDescAttrs(ServiceDesc desc, boolean isFullView,
		IXmlStreamWriter xmlWriter, Map<String, String> props)
	{
		ClientServiceDesc desc2 = (ClientServiceDesc)desc;

		String clientName = desc2.getClientName();
		xmlWriter.writeAttribute("client-name", (clientName != null ? clientName : ""));
	}

	@Override
	protected void renderServiceDescElements(ServiceDesc desc,
		boolean isFullView, IXmlStreamWriter xmlWriter,
		Map<String, String> props)
	{
		ClientServiceDesc desc2 = (ClientServiceDesc)desc;

		String serviceVersion = desc2.getServiceVersion();
		if (serviceVersion != null) {
			XmlStreamUtil.safeWriteChildCData(xmlWriter, "service-version", serviceVersion);
		}

		String useCase = desc2.getUseCase();
		if (useCase != null) {
			XmlStreamUtil.safeWriteChildCData(xmlWriter, "use-case", useCase);
		}

		URL defServiceLocationURL =  desc2.getDefServiceLocationURL();
		if (defServiceLocationURL != null) {
			XmlStreamUtil.safeWriteChildCData(xmlWriter, "url", defServiceLocationURL.toExternalForm());
		}

		if (!isFullView) {
			return;
		}

		DataBindingDesc defRequestDataBinding = desc2.getDefRequestDataBinding();
		if (defRequestDataBinding != null) {
			XmlStreamUtil.writeChildText(xmlWriter, "def-request-data-binding",
				defRequestDataBinding.getName());
		}

		DataBindingDesc defResponseDataBinding = desc2.getDefResponseDataBinding();
		if (defResponseDataBinding != null) {
			XmlStreamUtil.writeChildText(xmlWriter, "def-response-data-binding",
				defResponseDataBinding.getName());
		}

		String defTransportName = desc2.getDefTransportName();
		if (defTransportName != null) {
			XmlStreamUtil.writeChildText(xmlWriter, "def-transport", defTransportName);
		}

		String messageProtocolName = desc2.getMessageProtocolName();
		if (messageProtocolName != null) {
			XmlStreamUtil.writeChildText(xmlWriter, "message-protocol", messageProtocolName);
		}

		Integer appLevelNumRetries = desc2.getAppLevelNumRetries();
		if (appLevelNumRetries != null) {
			XmlStreamUtil.writeChildText(xmlWriter, "app-level-retries", appLevelNumRetries.toString());
		}

		G11nOptions g11nOptions = desc2.getG11nOptions();
		if (g11nOptions != null) {
			writeG11Options(g11nOptions, xmlWriter, "g11n-options");
		}

		// TODO: add these
		//ApplicationRetryHandler retryHandler = desc2.getRetryHandler();
		//ErrorResponseAdapter customErrorResponseAdapter = desc2.getCustomErrorResponseAdapter();
		//AutoMarkdownStateFactory autoMarkdownStateFactory = desc2.getAutoMarkdownStateFactory();

		// TODO: add all transports
	}
}
