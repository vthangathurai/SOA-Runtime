/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.g11n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleId;
import org.ebayopensource.turmeric.runtime.common.g11n.LocaleInfo;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


public class GlobalIdEntryImpl implements GlobalIdEntry {
	public static final LocaleInfo FALLBACK_LOCALE = new LocaleInfo("en-US", "US", true, false, false);
	private final Map<String, LocaleInfo> m_locales;
	private final String m_id;
	private final boolean m_isDefault;
	private LocaleInfo m_defaultLocale = FALLBACK_LOCALE;

	public GlobalIdEntryImpl(String id, Map<String, LocaleInfo> locales) {
		if (id == null || locales == null) {
			throw new NullPointerException();
		}
		m_id = id;
		m_locales = locales;
		if (id.equals(SOAConstants.DEFAULT_GLOBAL_ID)) { // TODO have a real xml element for this?
			m_isDefault = true;
		} else {
			m_isDefault = false;
		}
		for (LocaleInfo locale : locales.values()) {
			if (locale.isDefault()) {
				m_defaultLocale = locale;
				break;
			}
		}
	}

	public Collection<LocaleInfo> getAllLocales() {
		return Collections.unmodifiableCollection(m_locales.values());
	}

	public LocaleInfo getDefaultLocale() {
		return m_defaultLocale;
	}

	public LocaleInfo getLocale(LocaleId id) {
		return m_locales.get(id.toString());
	}

	/**
	 * @return the m_isDefault
	 */
	public boolean isDefaultGlobalId() {
		return m_isDefault;
	}

	public String getId() {
		return m_id;
	}

	public GlobalIdEntryImpl copy() {
		// LocaleInfo is immutable so we just create shallow copy of the hashmap.
		Map<String, LocaleInfo> outLocales = new HashMap<String, LocaleInfo>(m_locales);
		GlobalIdEntryImpl result = new GlobalIdEntryImpl(m_id, outLocales);
		return result;
	}

	public void dump(StringBuffer sb) {
		sb.append("global Id: " + m_id + '\n');
		List<String> locales = new ArrayList<String> (m_locales.keySet());
		Collections.sort(locales);
		for (String key : locales) {
			LocaleInfo locale = m_locales.get(key);
		//for (LocaleInfo locale: m_locales.values()) {
			sb.append("  Locale: lang=" + locale.getLanguage() + " terr=" + locale.getTerritory());
			if (locale.isDefault()) {
				sb.append(" default=true");
			}
			if (locale.isDisabledInRegistry()) {
				sb.append(" disabledInRegistry=true");
			}
			if (locale.isDisabledByPlatform()) {
				sb.append(" disabledByPlatform=true");
			}

			sb.append('\n');
		}
	}



}
