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
 * <p>Java class for LocaleInfoConfig complex type. 
 */
public class LocaleInfo {

	private final LocaleId m_id;
	private final boolean m_default;
    private final boolean m_registryDisabled;
    private final boolean m_platformDisabled;

    /**
     * @param language A language.
     * @param territory A territory speaking the given language.
     * @param def True if it is a default locale.
     * @param registryDisabled True if the Locale is disabled from Locale registry.
     * @param platformDisabled True if the Locale is disabled by the platform.
     */
    public LocaleInfo(String language, String territory, boolean def, boolean registryDisabled, boolean platformDisabled) {
    	m_id = new LocaleId(language, territory);
    	m_default = def;
    	m_registryDisabled = registryDisabled;
    	m_platformDisabled = platformDisabled;
    }
        
    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return m_id.getLanguage();
    }

    /**
     * Gets the value of the territory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerritory() {
        return m_id.getTerritory();
    }

    /**
     * Gets an ID representing both language and territory (separated by underscore).
     * 
     * @return
     *     possible object is
     *     {@link LocaleId }
     *     
     */
    public LocaleId getId() {
        return m_id;
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDefault() {
        return m_default;
    }

    /**
     * Returns true if the registry entry has marked this locale disabled within the associated
     * global ID usage.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDisabledInRegistry() {
        return m_registryDisabled;
    }

    /**
     * Returns true if this locale's language is not in the list of available locales from Locale
     * JDK class.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDisabledByPlatform() {
        return m_platformDisabled;
    }
    
    /**
     * 
     * @return True if the Locale is disabled.
     */
    public boolean isDisabled() {
    	return (m_registryDisabled || m_platformDisabled);
    }
}
