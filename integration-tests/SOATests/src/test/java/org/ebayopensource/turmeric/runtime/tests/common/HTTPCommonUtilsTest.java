/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common;

import static org.junit.Assert.assertEquals;

import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.junit.Test;


public class HTTPCommonUtilsTest {
	private static final String COOKIE_VALUE = 
		"_ordercup_session=BAh7BjoPc2Vzc2lvbl9pZCIlYzA4ODgwZjE1MzMyZGYxNmFmOGZlNWMzZjU4NjkzMDk%3D--6de26248a9e5ae9364dcc59b2ca564d67edb83a6; path=/; HttpOnly";

	@Test
	public void parseSetCookieValue() throws Exception {
		assertEquals(HTTPCommonUtils.parseSetCookieValue(COOKIE_VALUE).getName().toLowerCase(),
				"_ordercup_session");
		assertEquals(HTTPCommonUtils.parseSetCookieValue(COOKIE_VALUE).getValue().toLowerCase(),
		"BAh7BjoPc2Vzc2lvbl9pZCIlYzA4ODgwZjE1MzMyZGYxNmFmOGZlNWMzZjU4NjkzMDk%3D--6de26248a9e5ae9364dcc59b2ca564d67edb83a6".toLowerCase());
	}
	
}
