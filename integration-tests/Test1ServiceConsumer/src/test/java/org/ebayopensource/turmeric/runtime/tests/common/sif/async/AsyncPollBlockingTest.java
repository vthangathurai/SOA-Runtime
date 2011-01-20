/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Test;


public class AsyncPollBlockingTest extends AbstractWithServerTest {
	private final String ECHO_STRING = "BH Test String";

	@Test
	@SuppressWarnings("unchecked")
	// TODO: this testcase is supposed to be tested using Test1Driver. 
	public void servicePoll_blocking() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(true, true);
		debug(responseList);
		
		Assert.assertThat("ReponseList.size", responseList.size(), is(1));
	}

	@Test
	@SuppressWarnings("unchecked")
	// TODO: this testcase is supposed to be tested using Test1Driver. 
	public void servicePoll_blocking_timeout() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(true, true, 0);
		debug(responseList);

		Assert.assertThat("ReponseList.size", responseList.size(), is(0));
	}

	@Test
	@SuppressWarnings("unchecked")
	// TODO: this testcase is supposed to be tested using Test1Driver. 
	public void servicePoll_blocking_different_Operations()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, true);
		debug(responseList);
		while (responseList.size() < 2) {
			responseList.addAll(service.poll(true, true));
		}

		Assert.assertThat("ReponseList.size", responseList.size(), is(2));
	}

	@Test
	@SuppressWarnings("unchecked")
	// TODO: this testcase is supposed to be tested using Test1Driver. 
	public void servicePoll_blocking_different_Operations_timeout()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, true, 0);
		debug(responseList);

		Assert.assertThat("ReponseList.size", responseList.size(), lessThan(2));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void servicePoll_local_blocking() throws Exception {
		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);

		List<Response<?>> responseList = service.poll(true, true);
		debug(responseList);

		Assert.assertThat("ReponseList.size", responseList.size(), is(1));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void servicePoll_local_blocking_timeout() throws Exception {
		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);

		List<Response<?>> responseList = service.poll(true, true, 0);
		debug(responseList);

		Assert.assertThat("ReponseList.size", responseList.size(), lessThanOrEqualTo(1));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void servicePoll_blocking_local_different_Operations()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();

		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, true);
		debug(responseList);
		while (responseList.size() < 2) {
			responseList.addAll(service.poll(true, true));
		}

		Assert.assertThat("ReponseList.size", responseList.size(), is(2));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void servicePoll_blocking_local_different_Operations_timeout()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();

		Service service = ServiceFactory.create("Test1Service", "localAsync", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, true, 0);
		
		debug(responseList);

		Assert.assertThat("ReponseList.size", responseList.size(), lessThanOrEqualTo(2));
	}

	static List<Response<?>> getResponseList(
			Map<Service, List<Response<?>>> respMap) {
		LinkedList<Response<?>> respList = new LinkedList<Response<?>>();
		for (Service service : respMap.keySet()) {
			respList.addAll(respMap.get(service));
		}
		return respList;
	}

	private void debug(List<Response<?>> responseList) throws Exception {
		System.out.printf("ResponseList.size = %d%n", responseList.size());
		for (Response<?> element : responseList) {
			System.out.printf("  element.get() = %s%n", element.get());
		}
	}
}
