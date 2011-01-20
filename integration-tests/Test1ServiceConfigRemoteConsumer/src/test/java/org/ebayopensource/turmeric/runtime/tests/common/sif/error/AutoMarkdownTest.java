/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import java.net.URL;
import java.util.Properties;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Service;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;

/**
 * @author ichernyshev
 */
public class AutoMarkdownTest extends AbstractWithServerTest {

	@Before
	public void setUp() throws Exception{
		//System.setProperty("test.log.out", "true");
	}
	
	@After
	public void tearDown() throws Exception {
		MarkdownTestHelper.markupClientManually("test1caller", null, null);
		
		//System.setProperty("test.log.out", null); -> thiw will fail coz of jvm issue. will throw nullptr
		Properties p = System.getProperties();
		if(p.containsKey("test.log.out"))
			p.remove("test.log.out");
		System.setProperties(p);
	}

	@Test
	public void testTest1Markdown() throws Exception {
		//ClientConfigManager.getInstance().setConfigTestCase("configremote");

		//URL serviceURL = serverUri.toURL();
		URL serviceURL = new URL("http://localhostbaddd:4146/ws/spf");
		
		Service service = ServiceFactory.create("test1caller", "configremote", serviceURL);
		
		Test1Service proxy = service.getProxy();

		for (int i=0; i<7; i++) {
			try {
				System.out.println("<><> Run#:"+i);
				proxy.myNonArgOperation();
				Assert.fail("Unexpected success: Was expecting a ServiceInvocationRuntimeException");
			} catch (ServiceInvocationRuntimeException e) {
				long expectedErrorId;
				if (i == 7) {
					expectedErrorId = ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_SERVICE_MARKDOWN, ErrorConstants.ERRORDOMAIN).getErrorId();
				} else {
					expectedErrorId = ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_COMM_FAILURE, ErrorConstants.ERRORDOMAIN).getErrorId();
					//was looking fr: SVC_TRANSPORT_CONNECT_EXCEPTION
				}

				long errorId = e.getErrorMessage().getError().get(0).getErrorId();
				if (errorId != expectedErrorId) {
					throw e;
				}

				System.out.println("Caught expected at #" + i + " : " + e.toString());
			}

			@SuppressWarnings("rawtypes")
			MarkdownStateSnapshot markdownState = MarkdownTestHelper.getClientState("test1caller", null, null);
			

			if (i >= 6) {
				Assert.assertTrue(
						"Client did not mark down on iteration #" + i,
						(markdownState.isProgramAlert() ||markdownState.isAlert()));
			} else {
				Assert.assertTrue("Client marked down on iteration #" + i
						+ " with " + markdownState.getReason(), 
						(!markdownState.isProgramAlert() || !markdownState.isAlert()));
			}
		}
	}
}
