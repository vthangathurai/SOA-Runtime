/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.g11n;

import java.util.Collection;

/**
 * 
 * Interface for the GlobalId entries. This represents a single
 * globalization profile. Each service is allowed to configure a set
 * of globalIds that it would support.
 *
 */
public interface GlobalIdEntry {
	/**
	 * Returns the set of locales associates.
	 * @return a collection of all locales
	 */
	public Collection<LocaleInfo> getAllLocales();
	
	/**
	 * returns the default locale.
	 * 
	 * @return the default locale
	 */
	public LocaleInfo getDefaultLocale();
	
	/**
	 * returns the locale associate with the given local id.
	 * 
	 * @param id the Id for the information info entry
	 * @return the local information
	 */
	public LocaleInfo getLocale(LocaleId id);
	
	/**
	 * Global id associated with this gloablization profile.
	 * @return the Id of this entry
	 */
	public String getId();
	
	/**
	 * returns true, if this is the default global profile.
	 * @return the default global Id
	 */
	public boolean isDefaultGlobalId();
}
