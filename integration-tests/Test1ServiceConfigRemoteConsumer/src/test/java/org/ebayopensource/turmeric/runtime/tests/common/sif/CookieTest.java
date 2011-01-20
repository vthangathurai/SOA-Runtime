/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;


import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;

public class CookieTest extends BaseCallTest {
	public static final String cookieTest = "Part_Number=\"Rocket_Launcher;_0001\"; Version=\"1\"; Path=\"/acme\"; Max-Age=\"12345\"; Secure; Comment=\"this is a comment\"; Domain=\"www.ebay.com\"";

	public CookieTest() throws Exception {
		super("configremote");
	}

	protected void setupDriver(Test1Driver driver) {
		driver.setHeader_Test1Cookie(cookieTest);
		Cookie[] cookieList = makeCookies();
		driver.setCookies(cookieList);
		driver.setVerifier(new Verifier(cookieList));
	}

	protected class Verifier implements Test1Driver.SuccessVerifier {
		private Cookie[] m_cookies;

		Verifier(Cookie[] cookieList) {
			m_cookies = cookieList;
		}

		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception {
			Cookie cookie = service.getCookie("Part_Number");
			Assert.assertNotNull(cookie);
			Assert.assertEquals("PART_NUMBER", cookie.getName());
			Assert.assertEquals("Rocket_Launcher;_0001", cookie.getValue());
			/*
			 * assertEquals(true, cookie.getSecure()); assertEquals("this is a
			 * comment", cookie.getComment()); assertEquals("www.ebay.com",
			 * cookie.getDomain()); assertEquals(12345, cookie.getMaxAge());
			 * assertEquals("/acme", cookie.getPath()); assertEquals(1,
			 * cookie.getVersion());
			 */
			for (int i = 0; i < m_cookies.length; i++) {
				cookie = service.getCookie(m_cookies[i].getName());
				Assert.assertEquals(m_cookies[i].getName(), cookie.getName());
				Assert.assertEquals(m_cookies[i].getValue(), cookie.getValue());
				/*
				 * assertEquals(m_cookies[i].getPath(), cookie.getPath());
				 * assertEquals(m_cookies[i].getDomain(), cookie.getDomain());
				 */
			}
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResponse, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception {
			Map context = null;
			if (mode.equals(TestMode.ASYNC_SYNC)) {
				context = dispatch.getResponseContext();
			} else {
				context = futureResponse.getContext();
			}
			Cookie cookie = (Cookie) context.get("Part_Number".toUpperCase());
			Assert.assertEquals("Rocket_Launcher;_0001", cookie.getValue());
			Assert.assertNotNull(cookie);
			Assert.assertEquals("PART_NUMBER", cookie.getName());

			String req_cookies = (String) context.get("COPIED_FROM_REQ_COOKIE");
			Assert.assertNotNull(req_cookies);
			StringTokenizer tokens = new StringTokenizer(req_cookies);
			Assert.assertNotNull(tokens);

			int count = 0;
			while (tokens.hasMoreTokens()) {
				String token = (String) tokens.nextElement();
				if (token.indexOf("Version") != -1)
					continue;
				String tokenName = token.substring(0, token.indexOf("="));
				String tokenValue = token.substring(token.indexOf("="), token
						.length());
				if (isCookieInList(new Cookie(tokenName, tokenValue)))
					++count;
			}
			Assert.assertEquals("all the cookies are returned from dispatch", count,
					m_cookies.length);

			for (int i = 0; i < m_cookies.length; ++i) {
				cookie = (Cookie) context.get("RESPONSE-"
						+ m_cookies[i].getName().toUpperCase());
				Assert.assertNotNull(cookie);
				Assert.assertTrue(cookie.getValue().equalsIgnoreCase("response-"+m_cookies[i].getValue()));
			}

		}

		private boolean isCookieInList(Cookie cookie) {
			for (Cookie localCookie : m_cookies) {
				if (localCookie.getName().toUpperCase().equals(
						cookie.getName().toUpperCase())
						&& localCookie.getValue().equals(cookie.getValue()))
					return true;
			}
			return true;
		}
	}

	private Cookie[] makeCookies() {
		Cookie a = new Cookie("name1", "value1");
		Cookie b = new Cookie("name2", "value2");
		Cookie c = new Cookie("name3", "value3");
		/*
		 * a.setDomain("domain"); b.setDomain("domain2"); b.setPath("path");
		 */
		Cookie[] list = new Cookie[] { a, b, c };
		return list;
	}
}
