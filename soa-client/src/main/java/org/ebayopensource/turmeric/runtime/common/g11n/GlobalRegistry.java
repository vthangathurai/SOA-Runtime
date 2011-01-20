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
 * Interface that the GlobalRegistryConfigManager would implement.
 * The GlobalRegistryConfigManager is the registry for the all 
 * globalization profiles that are available for services in that
 * deployment
 * 
 *
 */

public interface GlobalRegistry {
	/**
	 * init: loads the globalization profiles.
	 */
	public void init();
	
	/**
	 * Returns the GlobalIdEntry of a given globalId.
	 * 
	 * @param globalId the entry id
	 * @return the global ID value
	 */
	public GlobalIdEntry getGlobalIdEntry(String globalId);
	
	/**
	 * Returns all the GlobalIdEntry registered.
	 * @return a collection of all global Id entries
	 */
	public Collection<GlobalIdEntry> getAllEntries();
}
