/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.g11n;

/**
 * 
 * LocaleId represents a language localization. 
 *
 */
public class LocaleId {
    private final String m_language;
    private final String m_territory;

    
    /**
     * @param language A language 
     * @param territory One of the territories where the specified language is spoken.s 
     */
    public LocaleId(String language, String territory) {
    	if (language == null) {
    		throw new NullPointerException();
    	}

    	m_language = normalizeCase(language);
    	if (territory != null) {
    		m_territory = territory.toUpperCase();
    	} else {
    		m_territory = null;
    	}
    }
    
    // Normalize around the form: en-US (lower case language, upper case region)
	private String normalizeCase(String language) {
		int hyphenIx = language.indexOf('-');
		if (hyphenIx == -1 || hyphenIx == language.length() - 1) {
			return language.toLowerCase();
		}
		return language.substring(0, hyphenIx).toLowerCase() + "-" + language.substring(hyphenIx+1).toUpperCase();
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return m_language;
	}
	/**
	 * @return the territory
	 */
	public String getTerritory() {
		return m_territory;
	}
 
	@Override
	public String toString() {
		if (m_territory == null) {
			return m_language;
		}

		return m_language + "_" + m_territory;
	}

	/**
	 * @param s A Locale id in string format.
	 * @return A LocaleId object with the given locale id.
	 */
	public static LocaleId valueOf(String s) {
		int ix = s.indexOf("_");
		if (ix == -1) {
			return new LocaleId(s, null);
		}

		String language = s.substring(0, ix);
		String territory = s.substring(ix+1);
		return new LocaleId(language, territory);
	}
	
}
