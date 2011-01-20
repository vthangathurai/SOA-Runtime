/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

/**
 * Base class representing client/service configuration internal to the framework.  
 * <p>
 * Note: Most ConfigHolder data is available in higher-level structures. Refer to ServiceDesc and related structures
 * as the primary configuration in the public API for SOA framework.
 * 
 * @author rmurphy
 */
public class BaseConfigHolder {
	protected boolean m_readOnly = false;
	
	/**
	 * Test whether the object is read-only.  This method is invoked from all setters and will throw an exception if a 
	 * setter is invoked during read-only mode. 
	 */
	protected void checkReadOnly() {
		if (m_readOnly)
			throw new UnsupportedOperationException("Cannot set data while in read-only mode");
	}

	/**
	 * Returns whether the object is read-only.  Read-only objects prevent any setting of data in order to avoid multithreaded
	 * access conflicts.
	 * @return the read-only state
	 */
	public boolean isReadOnly() {
		return m_readOnly;
	}
	
	/**
	 * Set the read-only property to true.
	 */
	public void lockReadOnly() {
		m_readOnly = true;
	}


}
