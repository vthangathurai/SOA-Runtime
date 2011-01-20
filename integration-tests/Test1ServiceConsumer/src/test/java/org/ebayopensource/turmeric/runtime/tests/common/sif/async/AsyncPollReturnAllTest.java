/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.sif.service.InvokerUtil;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Test;


public class AsyncPollReturnAllTest extends AbstractWithServerTest {
	private final String ECHO_STRING = "BH Test String";

	/**
	 * @throws Exception
	 */

	@Test
	@SuppressWarnings("unchecked")
	public void remoteBlocking() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(true, false);

		Assert.assertTrue(responseList.size() == 1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void remoteBlockingTimeout() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(true, false, 0);

		Assert.assertTrue(responseList.size() == 0);
	}

	/**
	 * the following testcase is exactly same as above. Except that block is
	 * false. If partial is set to false, then "block" parameter does not affect
	 * the behaviour of poll
	 * 
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void remoteNonBlocking() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(false, false);

		Assert.assertTrue(responseList.size() == 1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void remoteNonBlockingTimeout() throws Exception {
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(false, false, 0);

		Assert.assertTrue(responseList.size() < 1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void remoteDifferent_Operations_blocking() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, false);

		Assert.assertTrue(responseList.size() == 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void remoteDifferent_Operations_blocking_timeout()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, false, 0);

		Assert.assertTrue(responseList.size() <= 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void remoteDifferent_Operations_nonBlocking() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(false, false);

		Assert.assertTrue(responseList.size() == 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void remoteDifferent_Operations_nonBlocking_timeout()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(false, false, 0);

		Assert.assertTrue(responseList.size() < 2);
	}

	/**
	 * @throws Exception
	 */

	@Test
	@SuppressWarnings("unchecked")
	public void local_Blocking() throws Exception {
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(true, false);

		Assert.assertTrue(responseList.size() == 1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void local_Blocking_timeout() throws Exception {
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(true, false, 0);

		Assert.assertTrue(responseList.size() <= 1);
	}

	/**
	 * the following testcase is exactly same as above. Except that block is
	 * false. If partial is set to false, then "block" parameter does not affect
	 * the behaviour of poll
	 * 
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void local_NonBlocking() throws Exception {
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(false, false);

		Assert.assertTrue(responseList.size() == 1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void local_NonBlocking_timeout() throws Exception {
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(
				ECHO_STRING + "service1");
		List<Response<?>> responseList = service.poll(false, false, 0);

		Assert.assertTrue(responseList.size() <= 1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void local_Different_Operations_blocking() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, false);

		Assert.assertTrue(responseList.size() == 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void local_Different_Operations_blocking_timeout()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(true, false, 0);

		Assert.assertTrue(responseList.size() < 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void local_Different_Operations_nonBlocking() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(false, false);

		Assert.assertTrue(responseList.size() == 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void local_Different_Operations_nonBlocking_timeout()
			throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
		service.createDispatch("echoString").invokeAsync(ECHO_STRING);
		msg.setBody(msg.getBody());
		service.createDispatch("myTestOperation").invokeAsync(msg);

		List<Response<?>> responseList = service.poll(false, false, 0);

		Assert.assertTrue(responseList.size() < 2);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void invokerUtilPollLocal() throws Exception {
		LinkedList<Service> services = new LinkedList<Service>();
		for (int i = 0; i < 10; i++) {
			MyMessage msg = TestUtils.createTestMessage();
			Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
			msg.setBody(msg.getBody());
			service.createDispatch("myTestOperation").invokeAsync(msg);
			services.add(service);
		}
		List<Response<?>> responses = AsyncPollBlockingTest
				.getResponseList(InvokerUtil.pollGetAll(services));
		Assert.assertTrue(responses.size() == 20);
	}

//	@SuppressWarnings("unchecked")
//	public void testInvokerUtilPollLocalSmallTimeout() throws Exception {
//		LinkedList<Service> services = new LinkedList<Service>();
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		System.out
//				.println("testInvokerUtilPollLocalSmallTimeout: before time : "
//						+ System.currentTimeMillis());
//		List<Response<?>> responses = AsyncPollBlockingTest
//				.getResponseList(InvokerUtil.pollGetAll(services, 0));
//		System.out
//				.println("testInvokerUtilPollLocalSmallTimeout: after time : "
//						+ System.currentTimeMillis());
//		Assert.assertTrue(responses.size() < 20);
//	}

//	@SuppressWarnings("unchecked")
//	public void testInvokerUtilPollLocalLargeTimeout() throws Exception {
//		LinkedList<Service> services = new LinkedList<Service>();
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		System.out
//				.println("testInvokerUtilPollLocalLargeTimeout: before time : "
//						+ System.currentTimeMillis());
//		List<Response<?>> responses = AsyncPollBlockingTest
//				.getResponseList(InvokerUtil.pollGetAll(services, 500));
//		System.out
//				.println("testInvokerUtilPollLocalLargeTimeout: after time : "
//						+ System.currentTimeMillis());
//		Assert.assertTrue(responses.size() == 20);
//	}

	@Test
	@SuppressWarnings("unchecked")
	public void invokerUtilPollRemote() throws Exception {
		LinkedList<Service> services = new LinkedList<Service>();
		for (int i = 0; i < 10; i++) {
			MyMessage msg = TestUtils.createTestMessage();
			Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
			msg.setBody(msg.getBody());
			service.createDispatch("myTestOperation").invokeAsync(msg);
			services.add(service);
		}
		List<Response<?>> responses = AsyncPollBlockingTest
				.getResponseList(InvokerUtil.pollGetAll(services));
		Assert.assertTrue(responses.size() == 20);
	}

//	@SuppressWarnings("unchecked")
//	public void testInvokerUtilPollRemoteSmallTimeout() throws Exception {
//		LinkedList<Service> services = new LinkedList<Service>();
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		System.out
//				.println("testInvokerUtilPollRemoteSmallTimeout: before time : "
//						+ System.currentTimeMillis());
//		List<Response<?>> responses = AsyncPollBlockingTest
//				.getResponseList(InvokerUtil.pollGetAll(services, 0));
//		System.out
//				.println("testInvokerUtilPollRemoteSmallTimeout: after time : "
//						+ System.currentTimeMillis());
//		Assert.assertTrue(responses.size() < 20);
//	}

//	@SuppressWarnings("unchecked")
//	public void testInvokerUtilPollRemoteLargeTimeout() throws Exception {
//		LinkedList<Service> services = new LinkedList<Service>();
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		System.out
//				.println("testInvokerUtilPollRemoteLargeTimeout: before time : "
//						+ System.currentTimeMillis());
//		List<Response<?>> responses = AsyncPollBlockingTest
//				.getResponseList(InvokerUtil.pollGetAll(services, 500));
//		System.out
//				.println("testInvokerUtilPollRemoteLargeTimeout: after time : "
//						+ System.currentTimeMillis());
//		Assert.assertTrue(responses.size() == 20);
//	}

	@Test
	@SuppressWarnings("unchecked")
	public void invokerUtilPollMixed() throws Exception {
		LinkedList<Service> services = new LinkedList<Service>();
		for (int i = 0; i < 10; i++) {
			MyMessage msg = TestUtils.createTestMessage();
			Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
			msg.setBody(msg.getBody());
			service.createDispatch("myTestOperation").invokeAsync(msg);
			services.add(service);
		}
		for (int i = 0; i < 10; i++) {
			MyMessage msg = TestUtils.createTestMessage();
			Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
			msg.setBody(msg.getBody());
			service.createDispatch("myTestOperation").invokeAsync(msg);
			services.add(service);
		}

		List<Response<?>> responses = AsyncPollBlockingTest
				.getResponseList(InvokerUtil.pollGetAll(services));
		Assert.assertTrue(responses.size() == 40);
	}

//	@SuppressWarnings("unchecked")
//	public void testInvokerUtilPollMixedSmallTimeout() throws Exception {
//		LinkedList<Service> services = new LinkedList<Service>();
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		System.out
//				.println("testInvokerUtilPollMixedSmallTimeout: before time : "
//						+ System.currentTimeMillis());
//		List<Response<?>> responses = AsyncPollBlockingTest
//				.getResponseList(InvokerUtil.pollGetAll(services, 0));
//		System.out
//				.println("testInvokerUtilPollMixedSmallTimeout: after time : "
//						+ System.currentTimeMillis());
//
//		Assert.assertTrue(responses.size() < 40);
//	}

//	@SuppressWarnings("unchecked")
//	public void testInvokerUtilPollMixedLargeTimeout() throws Exception {
//		LinkedList<Service> services = new LinkedList<Service>();
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "remote", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		for (int i = 0; i < 10; i++) {
//			MyMessage msg = TestUtils.createTestMessage();
//			Service service = ServiceFactory.create("test1", "local", serverUri.toURL());
//			service.createDispatch("echoString").invokeAsync(ECHO_STRING);
//			msg.setBody(msg.getBody());
//			service.createDispatch("myTestOperation").invokeAsync(msg);
//			services.add(service);
//		}
//		System.out
//				.println("testInvokerUtilPollMixedLargeTimeout: before time : "
//						+ System.currentTimeMillis());
//		List<Response<?>> responses = AsyncPollBlockingTest
//				.getResponseList(InvokerUtil.pollGetAll(services, 500));
//		System.out
//				.println("testInvokerUtilPollMixedLargeTimeout: after time : "
//						+ System.currentTimeMillis());
//		Assert.assertTrue(responses.size() == 40);
//	}

}
