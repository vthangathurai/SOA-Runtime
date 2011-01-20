/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.service;

import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleId;


public class GlobalIdDesc {
	private final GlobalIdEntry m_globalIdEntry;
	
	private final boolean m_supported;
	private final Set<String> m_supportedLocales;

	public GlobalIdDesc(GlobalIdEntry entry, boolean supported, Set<String> supportedLocales) {
		 m_globalIdEntry = entry;
		 m_supported = supported;
		 m_supportedLocales = supportedLocales;
	}
	
	public String getId() {
		return m_globalIdEntry.getId();
	}
	
	/**
	 * @return the GlobalIdEntry
	 */
	public GlobalIdEntry getGlobalIdEntry() {
		return m_globalIdEntry;
	}
	
	/**
	 * @return whether this global ID is supported
	 */
	public boolean isSupported() {
		return m_supported;
	}

	/**
	 * @return whether the global ID with the given language and territory is supported
	 */
	public boolean isLocaleSupported(LocaleId localeId) {
		if (m_supportedLocales == null || m_supportedLocales.isEmpty()) {
			return true;
		}
		return (m_supportedLocales.contains(localeId.toString()));
	}
	
}
