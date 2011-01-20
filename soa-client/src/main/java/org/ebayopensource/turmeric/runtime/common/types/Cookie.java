/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;

/**
 * Represents a cookie for SOA requests
 *
 * The cookie will be delievered to the server-side and then back to the client side
 * using an underlying transport protocol, if transport supports that.
 *
 * @author ichernyshev
 */
public final class Cookie {

	private final String m_name;
	private final String m_value;

	/**
	 * @param name A cookie name.
	 * @param value	The cookie value.
	 */
	public Cookie(String name, String value) {
		if (name == null) {
			throw new NullPointerException();
		}

		m_name = name.toUpperCase();
		m_value = value;
	}

	/**
	 * @return The name of this cookie.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return The value of this cookie.
	 */
	public String getValue() {
		return m_value;
	}
}
