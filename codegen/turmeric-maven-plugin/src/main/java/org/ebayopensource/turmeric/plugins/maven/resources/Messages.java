/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.resources;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author yayu
 *
 */
public final class Messages {
	private static final ResourceBundle resources =  ResourceBundle.getBundle(
			Messages.class.getName().toLowerCase(Locale.US));

	/**
	 * 
	 */
	private Messages() {
		super();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.ResourceBundle#getString(java.lang.String)
	 */
	public final String getString(String key) {
		return resources.getString(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.ResourceBundle#getStringArray(java.lang.String)
	 */
	public final String[] getStringArray(String key) {
		return resources.getStringArray(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.ResourceBundle#getObject(java.lang.String)
	 */
	public final Object getObject(String key) {
		return resources.getObject(key);
	}

	/**
	 * @return
	 * @see java.util.ResourceBundle#getLocale()
	 */
	public Locale getLocale() {
		return resources.getLocale();
	}

	/**
	 * @return
	 * @see java.util.ResourceBundle#getKeys()
	 */
	public Enumeration<String> getKeys() {
		return resources.getKeys();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.ResourceBundle#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return resources.containsKey(key);
	}

	/**
	 * @return
	 * @see java.util.ResourceBundle#keySet()
	 */
	public Set<String> keySet() {
		return resources.keySet();
	}
	
	

}
