/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleId;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleInfo;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.service.GlobalIdDesc;

import com.ebay.kernel.util.StringUtils;
/**
 * G11nHandler processes the global id and locale headers and populate request message's
 * and response message's G11NOptions.
 *
 */
public class G11nHandler extends BaseHandler {

	private boolean m_requireGlobalId = false;

	@Override
	public void init(InitContext ctx) throws ServiceException {
		super.init(ctx);
		Map<String, String> options = ctx.getOptions();
		if (null != options && options.size() > 0) {
			String requireGid = options.get("require-global-id");
			if (!isEmptyString(requireGid))
				m_requireGlobalId = !("false".equalsIgnoreCase(requireGid));
		}
	}

	@Override
	public void invoke(MessageContext ctx) throws ServiceException {
		InboundMessage requestMessage = (InboundMessage) ctx
				.getRequestMessage();
		OutboundMessage responseMessage = (OutboundMessage) ctx
				.getResponseMessage();
		ServerMessageContextImpl serviceCtx = (ServerMessageContextImpl) ctx;
		ServerServiceDesc serviceDesc = serviceCtx.getServiceDesc();
		Map<String, String> transportHeaders = requestMessage
				.getTransportHeaders();
		String globalIdStr = transportHeaders.get(SOAHeaders.GLOBAL_ID);
		String localeStr = transportHeaders.get(SOAHeaders.LOCALE_LIST);

		if( m_requireGlobalId && isEmptyString(globalIdStr) ) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_G11N_MISSING_GLOBAL_ID,
					ErrorConstants.ERRORDOMAIN, new Object[] {globalIdStr}));

		} else if (m_requireGlobalId == false && isEmptyString(globalIdStr)) {
			globalIdStr = SOAConstants.DEFAULT_GLOBAL_ID;
		}

		GlobalIdDesc globalId = serviceDesc.getGlobalId(globalIdStr);
		if (globalId == null) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_G11N_NO_SUCH_GLOBAL_ID,
					ErrorConstants.ERRORDOMAIN, new Object[] { globalIdStr }));
		}
		if (!globalId.isSupported()) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_G11N_UNSUPPORTED_GLOBAL_ID,
							ErrorConstants.ERRORDOMAIN, new Object[] { globalIdStr }));
		}

		LocaleInfo selectedLocale;
		List<String> locales = null;
		if (localeStr != null) {
			locales = StringUtils.splitStr(localeStr, ',', true);
			selectedLocale = findSupportedLocale(locales, globalId, localeStr);
		} else {
			selectedLocale = globalId.getGlobalIdEntry().getDefaultLocale();
		}

		List<String> selectedLocaleForG11n = new ArrayList<String>();
		selectedLocaleForG11n.add(selectedLocale.getId().toString());

		G11nOptions inputG11nOptions = ctx.getRequestMessage().getG11nOptions();

		G11nOptions g11nOptions = new G11nOptions(
				inputG11nOptions.getCharset(), selectedLocaleForG11n,
				globalIdStr);
		responseMessage.setG11nOptions(g11nOptions);
	}

	private LocaleInfo findSupportedLocale(List<String> locales,
			GlobalIdDesc globalId, String localeStr) throws ServiceException {
		LocaleInfo selectedLocale = null;
		GlobalIdEntry globalIdEntry = globalId.getGlobalIdEntry();
		for (String locale : locales) {
			LocaleId localeId = LocaleId.valueOf(locale);
			LocaleInfo localeInfo = globalIdEntry.getLocale(localeId);
			if (localeInfo == null) {
				// Even though this bad locale may be among other locales that
				// are usable, we don't want to
				// tolerate it. This can mask issues that cause errors in other
				// use cases.
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_G11N_NO_SUCH_LOCALE,
						ErrorConstants.ERRORDOMAIN, new Object[] { locale }));
			}

			if (selectedLocale == null && !localeInfo.isDisabled()
					&& globalId.isLocaleSupported(localeId)) {
				selectedLocale = localeInfo;
			}
		}

		if (selectedLocale == null) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_G11N_UNSUPPORTED_LOCALE,
							ErrorConstants.ERRORDOMAIN, new Object[] { localeStr }));
		}

		return selectedLocale;
	}

	private static boolean isEmptyString(String input) {
		if (input == null || input.trim().length()< 1) {
			return true;
		}
		return false;

	}

}
