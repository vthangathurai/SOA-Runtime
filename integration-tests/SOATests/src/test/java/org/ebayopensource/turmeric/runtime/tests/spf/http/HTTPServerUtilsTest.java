/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.spf.http;

import static org.junit.Assert.*;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServerUtils;
import org.junit.Test;

/**
 * @author idralyuk
 */
public class HTTPServerUtilsTest extends AbstractTurmericTestCase {
	@Test
	public  void getNullProxyIP() throws Exception {
		String origProxyIP = null;
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(origProxyIP);
		assertEquals("", resultProxyIP);
	}
	
	@Test
	public  void getSingleGoodProxyIP() throws Exception {
		String origProxyIP = "72.5.124.61";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(origProxyIP);
		assertEquals(origProxyIP, resultProxyIP);
	}

	@Test
	public  void getSingleBadProxyIP() throws Exception {
		String origProxyIP = "111.222.333.444";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(origProxyIP);
		assertEquals("", resultProxyIP);
	}

	@Test
	public  void getLocalhostProxyIP() throws Exception {
		String proxyIP = "127.0.0.1";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(proxyIP);
		assertEquals("", resultProxyIP);
	}

	@Test
	public  void getPrivateProxyIPa() throws Exception {
		String proxyIP = "10.254.28.1";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(proxyIP);
		assertEquals("", resultProxyIP);
	}

	@Test
	public  void getPrivateProxyIPb() throws Exception {
		String proxyIP = "172.16.28.1";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(proxyIP);
		assertEquals("", resultProxyIP);
	}

	@Test
	public  void getPrivateProxyIPc() throws Exception {
		String proxyIP = "192.168.28.1";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(proxyIP);
		assertEquals("", resultProxyIP);
	}

	@Test
	public  void getMultipleGoodProxyIP() throws Exception {
		String origProxyIP = "66.135.205.13, 66.135.205.14, 66.135.221.10, 66.135.221.11";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(origProxyIP);
		assertEquals("66.135.205.13", resultProxyIP);
	}

	@Test
	public  void getMultipleGoodWithBadProxyIP() throws Exception {
		String origProxyIP = "127.0.0.1, 10.254.28.1, 172.16.28.1, 192.168.28.1, 66.211.160.87, 66.211.160.88";
		String resultProxyIP = HTTPServerUtils.getFirstPublicProxyIP(origProxyIP);
		assertEquals("66.211.160.87", resultProxyIP);
	}
}
