/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.common.pipeline;

/**
 * A Session interface that allows storing session level state.
 * This state could be cached and made available to other services in the
 * user session. 
 * @author smalladi
 *
 */
public interface Session {
	/**
	 * Returns a unique identifier for this session.
	 * @return the session ID
	 */
	public long getSessionId();
	
	/**
	 * Get a specified session property by name.
	 * @param key the property name
	 * @return the property 
	 */
	public Object getProperty(String key);
	/**
	 * Set a specified property (any Java object) by name.
	 * @param key the property name
	 * @param value an object representing the property value
	 */
	public void setProperty(String key, Object value);
}
