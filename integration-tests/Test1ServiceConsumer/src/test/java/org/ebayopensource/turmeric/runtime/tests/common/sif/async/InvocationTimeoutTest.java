/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import static org.hamcrest.Matchers.containsString;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithSlowProxyServerTest;
import org.junit.Assert;
import org.junit.Test;


public class InvocationTimeoutTest extends AbstractWithSlowProxyServerTest {
	private final String ECHO_STRING = "BH Test String";
	
	@Test
	@SuppressWarnings("unchecked")
	public void testConsecutiveErrors() throws Exception {
		Service service = createProxiedService("Test1Service", "invocationTimeout");
		
		try {
			Object[] input = new Object[1];
			input[0] = ECHO_STRING;
			service.invoke("echoString", input, new LinkedList<Object>());
			Assert.fail("Timeout Exception Expected for service.invoke");
		} catch (ServiceInvocationException ex) {
			Assert.assertThat(ex.getClass().getName() + " message",
					ex.getMessage(), containsString("TimeoutException"));
			Assert.assertTrue(ex.getMessage().contains("TimeoutException"));
			System.out.printf("%s: %s (on service.invoke)%n", ex.getClass()
					.getName(), ex.getMessage());
		}
		
		try {
			service.createDispatch("echoString").invoke(ECHO_STRING);
			Assert.fail("Timeout Exception Expected for dispatch.invoke");
		} catch (WebServiceException ex) {
			Assert.assertThat(ex.getClass().getName() + " message",
					ex.getMessage(), containsString("TimeoutException"));
			Assert.assertTrue(ex.getMessage().contains("TimeoutException"));
			System.out.printf("%s: %s (on dispatch.invoke)%n", ex.getClass()
					.getName(), ex.getMessage());
		}

		try {
			Response<?> resp = service.createDispatch("echoString")
					.invokeAsync(ECHO_STRING);
			resp.get();
			Assert.fail("Timeout Exception Expected for future.get");
		} catch (ExecutionException ex) {
			Assert.assertThat(ex.getClass().getName() + " message",
					ex.getMessage(), containsString("TimeoutException"));
			Assert.assertTrue(ex.getMessage().contains("TimeoutException"));
			System.out.printf("%s: %s (on future.get)%n", ex.getClass()
					.getName(), ex.getMessage());
		}
	}
}
