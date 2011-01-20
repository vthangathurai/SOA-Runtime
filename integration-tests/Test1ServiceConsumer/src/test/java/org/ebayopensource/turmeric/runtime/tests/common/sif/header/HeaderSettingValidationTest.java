/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.header;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Test;


public class HeaderSettingValidationTest extends AbstractWithServerTest {
	private final String ECHO_STRING = "BH Test String";

	@Test
	@SuppressWarnings("unchecked")
	public void mixedCaseHeaderAccess() throws Exception {
		Service service = ServiceFactory.create("test1", "headerTester", serverUri.toURL());
		service.setSessionTransportHeader("MixedCaseHeader", "Available");

		Assert.assertEquals("Available", service
				.getSessionTransportHeader("MixedCaseHeader"));
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void nonMixedCaseHeaderAccess() throws Exception {
		Service service = ServiceFactory.create("test1", "headerTester", serverUri.toURL());

		Assert.assertNull(service
				.getSessionTransportHeader("MixedCaseHeader"));
		try {
			service.createDispatch("echoString").invoke(ECHO_STRING);
		} catch (RuntimeException e) {
			Assert.assertTrue(e.getMessage().contains(
					"Customer header: MixedCaseHeader was not available"));
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void allUpperCaseHeaderAccess() throws Exception {
		Service service = ServiceFactory.create("test1", "headerTester", serverUri.toURL());
		service.setSessionTransportHeader("MIXEDCASEHEADER", "Available");

		Assert.assertNull(service
				.getSessionTransportHeader("MixedCaseHeader"));
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}
}
