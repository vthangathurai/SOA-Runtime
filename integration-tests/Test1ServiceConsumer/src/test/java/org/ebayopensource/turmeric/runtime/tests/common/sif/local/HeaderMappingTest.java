/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.sif.service.RequestContext;
import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.DebugHandler;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class HeaderMappingTest extends AbstractWithServerTest {
    @Rule
    public NeedsConfig needsconfig = new NeedsConfig("config");

	public static final String ADMIN_NAME = "headerMapping";
	public static final String CLIENT_NAME = "local";

	@Test
	public void testNormalCalls() throws Exception {
	    DebugHandler.enable();
		Service test1 = ServiceFactory.create(ADMIN_NAME, CLIENT_NAME, serverUri.toURL());

		Map<String,String> headers = new HashMap<String, String>();
		headers.put("X-TURMERIC-SERVICE-VERSION", "header-0-value-should-be-suppressed");
        headers.put("header-1-name", "header-1-value");
        headers.put("header-2-name", "header-2-value");
        // headers.put("Z-GUID", "zuid-should-be-suppressed"); // Special case?

		RequestContext reqCtx = test1.getRequestContext();
		
		for(Map.Entry<String, String> header: headers.entrySet()) {
		    reqCtx.setTransportHeader(header.getKey(), header.getValue());
		}
		
		String param1 = "Hello";
		Object[] inParams = new Object[] { param1 };
		List<Object> outParams = new ArrayList<Object>();
		test1.invoke("echoString", inParams, outParams);
		// Tested using debug mode

		ResponseContext respCtx = test1.getResponseContext();

		// Deal with suppressed headers
		headers.put("X-TURMERIC-SERVICE-VERSION", "1.0.0");
		
		// Assert that they were copied to the response
        for(Map.Entry<String, String> header: headers.entrySet()) {
		    String respHeaderKey = "COPIED_FROM_REQ_" + header.getKey().toUpperCase();
            Assert.assertEquals("request header [" + header.getKey() + "] copied to response header [" + respHeaderKey + "]",
                            header.getValue(), respCtx.getTransportHeader(respHeaderKey));
		}
	}
}
