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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.BaseConfigHolder;


public class GlobalRegistryConfigHolder extends BaseConfigHolder {
	private Map<String, GlobalIdEntryImpl> m_entries =
		new HashMap<String, GlobalIdEntryImpl>();

	public GlobalRegistryConfigHolder copy() {
		GlobalRegistryConfigHolder newCH = new GlobalRegistryConfigHolder();
		newCH.m_readOnly = false;
		newCH.m_entries = copyAllEntries(m_entries);
		return newCH;
	}

	private Map<String, GlobalIdEntryImpl> copyAllEntries(Map<String, GlobalIdEntryImpl> inEntries) {
		if (inEntries == null) {
			return null;
		}
		Map<String, GlobalIdEntryImpl> outEntries = new HashMap<String, GlobalIdEntryImpl>();
		for (Map.Entry<String, GlobalIdEntryImpl> entry : inEntries.entrySet()) {
			String globalId = entry.getKey();
			GlobalIdEntryImpl inEntry = entry.getValue();
			GlobalIdEntryImpl outEntry = inEntry.copy();
			outEntries.put(globalId, outEntry);
		}
		return outEntries;
	}

	public GlobalIdEntryImpl getEntry(String globalId) {
		GlobalIdEntryImpl result = m_entries.get(globalId);
		if (result == null) {
			return null;
		}

		if (isReadOnly()) {
			return result.copy();
		}

		return result;
	}

	public void setEntry(String globalId, GlobalIdEntryImpl entry) {
		checkReadOnly();
		m_entries.put(globalId, entry);
	}

	public Collection<String> getAllGlobalIds() {
		return Collections.unmodifiableCollection(m_entries.keySet());
	}

	public Collection<GlobalIdEntry> getAllEntries() {
		//Map<String, GlobalIdEntry> outEntries = new HashMap<String, GlobalIdEntry>(m_entries);
		//return Collections.unmodifiableCollection(outEntries.values());
		Map<String, GlobalIdEntry> outEntries = new HashMap<String, GlobalIdEntry>();
		List<String> entryKeys = new ArrayList<String>(m_entries.keySet());
		Collections.sort(entryKeys);
		Collection<GlobalIdEntry> out = new LinkedHashSet<GlobalIdEntry>();
		for (String key : entryKeys) {
			outEntries.put(key, m_entries.get(key));
			out.add(m_entries.get(key));
		}
		return Collections.unmodifiableCollection(out);
	}

	/*
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		sb.append("========== Global Registry ==========" + '\n');
		for (String globalId : m_entries.keySet()) {
			GlobalIdEntryImpl entry = m_entries.get(globalId);
			entry.dump(sb);
		}
	}


}
