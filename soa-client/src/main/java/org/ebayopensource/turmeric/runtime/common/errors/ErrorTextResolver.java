/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.errors;

/**
 * Defines interface for a class capable of resolving localized error texts.
 * 
 * @author ichernyshev
 */
public interface ErrorTextResolver {

	/**
	 * Returns text in the requested locale.
	 * 
	 * If localized text is not found, returns null.
	 * If locale is null, returns English version.
	 * 
	 * @param id error text Id
	 * @param domain error text domain
	 * @param locale error text locale
	 * @return appropriate text for this locale; null if none
	 */
	public String getErrorText(String id, String domain, String locale);
}
