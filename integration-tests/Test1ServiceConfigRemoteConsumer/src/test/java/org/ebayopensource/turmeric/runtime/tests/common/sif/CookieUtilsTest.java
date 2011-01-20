/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.junit.Assert;
import org.junit.Test;



public class CookieUtilsTest  {
	@Test
	public void cookieParse() {
		String cookieStr = "Part_Number=\"Rocket_Launcher;_0001\"; Version=\"1\"; Path=\"/acme\"; Max-Age=\"12345\"; Secure; Comment=\"this is a comment\"; Domain=\"www.ebay.com\"";
		Cookie cookie = HTTPCommonUtils.parseSetCookieValue(cookieStr);
		Assert.assertEquals("PART_NUMBER", cookie.getName());
		Assert.assertEquals("Rocket_Launcher;_0001", cookie.getValue());
		/*assertEquals(true, cookie.getSecure());
		assertEquals("this is a comment", cookie.getComment());
		assertEquals("www.ebay.com", cookie.getDomain());
		assertEquals(12345, cookie.getMaxAge());
		assertEquals("/acme", cookie.getPath());
		assertEquals(1, cookie.getVersion());*/
	}

	@Test
	public void cookieFormat() {
		Cookie a = new Cookie("name1", "value1");
		Cookie b = new Cookie("name2", "value2");
		Cookie c = new Cookie("name3", "value3");
		/*a.setDomain("domain");
		b.setDomain("domain2");
		b.setPath("path");*/
		Cookie[] list = new Cookie[] {a, b, c};
		StringBuffer buf = new StringBuffer("Cookie: ");
		HTTPCommonUtils.encodeCookieValue(buf, list);
		//assertEquals("Cookie: $Version=\"0\"; name1=\"value1\"; $Domain=\"domain\"; name2=\"value2\"; $Domain=\"domain2\"; $Path=\"path\"; name3=\"value3\"", buf.toString());
		Assert.assertEquals("Cookie: $Version=\"0\"; NAME1=\"value1\"; NAME2=\"value2\"; NAME3=\"value3\"", buf.toString());
	}
}
