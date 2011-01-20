/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.g11n;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleInfo;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class GlobalRegistryConfigMapper {
	public static void map(String filename, Set<String> systemLanguages, Element topLevel, GlobalRegistryConfigHolder dst) throws ServiceCreationException {
		NodeList globalIds = DomParseUtils.getImmediateChildrenByTagName(topLevel, "global-id");
		for (int i = 0; i < globalIds.getLength(); i++) {
			Element globalId = (Element) globalIds.item(i);
			parseLocaleEntries(filename, systemLanguages, globalId, dst);
		}
	}
	
	private static void parseLocaleEntries(String filename, Set<String> systemLanguages, Element globalId, GlobalRegistryConfigHolder dst) throws ServiceCreationException {
		String id = globalId.getAttribute("id");
		if (id == null || id.trim().length() == 0) {
			throwError(filename, "Missing 'id' attribute on element: 'global-id'");
		}
		Map<String, LocaleInfo> outLocales = new HashMap<String, LocaleInfo>();
		NodeList inLocales = DomParseUtils.getImmediateChildrenByTagName(globalId, "locale-info");
		boolean haveDefaultLocale = false;
		for (int i = 0; i < inLocales.getLength(); i++) {
			Element inLocale = (Element) inLocales.item(i);
			LocaleInfo outLocale = parseLocale(filename, systemLanguages, inLocale);
			String key = outLocale.getId().toString();
			if (outLocale.isDefault()) {
				if (haveDefaultLocale) {
					throwError(filename, "Multiple default locales for globalId: " + id);
				}
				haveDefaultLocale = true;
			}
			outLocales.put(key, outLocale);
		}
		GlobalIdEntryImpl outEntry = new GlobalIdEntryImpl(id, outLocales);
		dst.setEntry(id, outEntry);
	}
	
	private static LocaleInfo parseLocale(String filename, Set<String> systemLanguages, Element inLocale) throws ServiceCreationException {
		String language = inLocale.getAttribute("language");
		String territory = inLocale.getAttribute("territory");
		String defaultStr = inLocale.getAttribute("default");
		String disabledStr = inLocale.getAttribute("disabled");
		boolean isDefault = textToBoolean(filename, defaultStr, "default", false);
		boolean registryDisabled = textToBoolean(filename, disabledStr, "disabled", false);
		boolean platformDisabled;
		if (systemLanguages.contains(language)) {
			platformDisabled = false;
		} else {
			platformDisabled = true;
		}
		LocaleInfo outLocale = new LocaleInfo(language, territory,
			isDefault, registryDisabled, platformDisabled);
		return outLocale;
	}

	private static boolean textToBoolean(String filename, String text, String name, boolean isRequired) throws ServiceCreationException {
		if (isRequired && (text == null || text.length() == 0)) {
			throwError(filename, "Missing boolean value at element: '" + name + "'");
		}
		Boolean boolValue = Boolean.valueOf(text);
		return boolValue.booleanValue();
	}
	
	public static void throwError(String filename, String cause) throws ServiceCreationException {
		throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
				ErrorConstants.ERRORDOMAIN, new Object[] {filename, cause}));
	}
	
}
