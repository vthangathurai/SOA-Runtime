/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.g11n;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.g11n.GlobalRegistry;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleInfo;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ConfigManager;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.SchemaValidationLevel;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class GlobalRegistryConfigManager extends ConfigManager implements GlobalRegistry {
	private static final String BASE_PATH = "META-INF/soa/common/";
	private static String s_commonPath = BASE_PATH + "config/";
	private static GlobalRegistryConfigManager s_instance = null;
	private GlobalRegistryConfigHolder m_registryConfig = null;
	private Set<String> s_systemLanguages = new HashSet<String>();

	public static synchronized GlobalRegistryConfigManager getInstance() {
		if (s_instance == null) {
			s_instance = new GlobalRegistryConfigManager();
			s_instance.init();
		}
		return s_instance;
	}

	public GlobalIdEntry getGlobalIdEntry(String globalId) {
		return m_registryConfig.getEntry(globalId);
	}

	public void init() {
		loadSystemLanguages();
		try {
			m_registryConfig = loadGlobalRegistry();
		} catch (ServiceCreationException e) {
			// OK to have no workaround registry file - just initialize DEFAULT by itself.
			m_registryConfig = createDefaultRegistry();
		}

		// TODO - find what is supported by the system.
	}

	private void loadSystemLanguages() {
		Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++) {
			String language = locales[i].getLanguage();
			String country = locales[i].getCountry();
			String languageID = language + "-" + country;
			s_systemLanguages.add(languageID);
		}
	}

	private GlobalRegistryConfigHolder createDefaultRegistry() {
		GlobalRegistryConfigHolder defaultRegistry = new GlobalRegistryConfigHolder();
		GlobalIdEntryImpl defaultGlobalId = createSingleDefaultGlobalId();
		defaultRegistry.setEntry(SOAConstants.DEFAULT_GLOBAL_ID, defaultGlobalId);
		return defaultRegistry;
	}

	private GlobalIdEntryImpl createSingleDefaultGlobalId() {
		Map<String, LocaleInfo> locales = new HashMap<String, LocaleInfo>();
		locales.put(GlobalIdEntryImpl.FALLBACK_LOCALE.getId().toString(), GlobalIdEntryImpl.FALLBACK_LOCALE);
		GlobalIdEntryImpl defaultGlobalId = new GlobalIdEntryImpl(SOAConstants.DEFAULT_GLOBAL_ID, locales);
		return defaultGlobalId;
	}

	public Collection<GlobalIdEntry> getAllEntries() {
		return m_registryConfig.getAllEntries();
	}

	private GlobalRegistryConfigHolder loadGlobalRegistry() throws ServiceCreationException {
		String globalRegistryFileName = s_commonPath + "GlobalRegistry.xml";
		String globalRegistrySchemaName = s_schemaPath + "common/GlobalRegistry.xsd";
		Document globalRegistryDoc =  ParseUtils.parseConfig(globalRegistryFileName, globalRegistrySchemaName, true, "global-id-list", SchemaValidationLevel.NONE);
		if (globalRegistryDoc == null) {
			return createDefaultRegistry();
		}
		Element globalIdList = globalRegistryDoc.getDocumentElement();
		GlobalRegistryConfigHolder holder = new GlobalRegistryConfigHolder();
		GlobalRegistryConfigMapper.map(globalRegistryFileName, s_systemLanguages, globalIdList, holder);
		return holder;
	}

	public synchronized static void setCommonPath(String path) {
		s_commonPath = BASE_PATH + path + "/";
		if (s_instance != null) s_instance.init();
	}
}
