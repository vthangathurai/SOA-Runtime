/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

public enum ServiceLayerType {
	COMMON("common"),
	INTERMEDIATE("intermediate"),
	BUSINESS("business");

	private final String m_value;

	private ServiceLayerType(String v) {
		m_value = v;
	}

	public String getValue() {
		return m_value;
	}

	public static ServiceLayerType fromValue(String v) {
		for (ServiceLayerType c : ServiceLayerType.values()) {
			if (c.m_value.equalsIgnoreCase(v)) {
				return c;
			}
		}

		return null;
	}
}
