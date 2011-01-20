/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;

/**
 * 
 * Helper to do asynchronous poll across multiple service calls.
 * 
 */
public class InvokerUtil {

	/**
	 * Poll all the services and return the responses. the method is BLOCKED
	 * until all the outstanding responses are available.
	 * 
	 * @param services
	 *            the services to be polled
	 * @return the map containing all the responses.
	 * @throws ServiceInvocationException
	 *             throws this exception when the invocation failed
	 * @throws InterruptedException
	 *             throws this exception when the failed to wait until all the
	 *             responses return.
	 */
	public static Map<Service, List<Response<?>>> pollGetAll(
			List<Service> services) throws ServiceInvocationException,
			InterruptedException {

		return pollInternal(services, false, false);
	}


	/**
	 * Poll all the services and return the responses. the method is BLOCKED
	 * until all the outstanding responses are available or the timeout is
	 * reached.
	 * 
	 * @param services
	 *            the services to be polled
	 * @param timeout
	 *            timeout in milliseconds
	 * @return the map containing all the responses.
	 * @throws ServiceInvocationException
	 *             throws this exception when the invocation failed
	 * @throws InterruptedException
	 *             throws this exception when the failed to wait
	 * @throws ExecutionException
	 *             throws this exception if the thread pool executor failed
	 */
	static Map<Service, List<Response<?>>> pollGetAll(List<Service> services,
			long timeout) throws ServiceInvocationException,
			InterruptedException, ExecutionException {
		return pollGetAll(services, timeout, Service.getDefaultExecutor());
	}

	/**
	 * Poll all the services and return the responses. the method is BLOCKED
	 * until all the outstanding responses are available or the timeout is
	 * reached.
	 * 
	 * @param services
	 *            the services to be polled
	 * @param timeout
	 *            timeout in milliseconds
	 * @param executor
	 *            the thread pool executor
	 * @return the map containing all the responses
	 * @throws ServiceInvocationException
	 *             throws this exception when the invocation failed
	 * @throws InterruptedException
	 *             throws this exception when the failed to wait
	 * @throws ExecutionException
	 *             throws this exception if the thread pool executor failed
	 */
	public static Map<Service, List<Response<?>>> pollGetAll(
			List<Service> services, long timeout, Executor executor)
			throws ServiceInvocationException, InterruptedException,
			ExecutionException {

		return pollInternal(services, false, false, timeout, executor);
	}

	/**
	 * Poll all the services and return the responses. If block is true, the
	 * method is BLOCKED until all the outstanding responses are available. If
	 * block is false, the method returns immediately with all available
	 * responses
	 * 
	 * @param services
	 *            the services to be polled
	 * @param block
	 *            blocking or non-blocking call
	 * @return the map containing all the responses
	 * @throws ServiceInvocationException
	 *             throws this exception when the invocation failed
	 * @throws InterruptedException
	 *             throws this exception when the failed to wait
	 */
	static Map<Service, List<Response<?>>> poll(List<Service> services,
			boolean block) throws ServiceInvocationException,
			InterruptedException {

		return pollInternal(services, block, true);
	}

	/**
	 * Poll all the services and return the responses. If block is true, the
	 * method is BLOCKED until all the outstanding responses are available or
	 * timeout is reached. If block is false, the method returns immediately
	 * with all available responses
	 * 
	 * @param services
	 *            the services to be polled
	 * @param block
	 *            blocking or non-blocking call
	 * @param timeout
	 *            timeout in milliseconds
	 * @return the map containing all the responses
	 * @throws ServiceInvocationException
	 *             throws this exception when the invocation failed
	 * @throws InterruptedException
	 *             throws this exception when the failed to wait
	 * @throws ExecutionException
	 *             throws this exception if the thread pool executor failed
	 */
	static Map<Service, List<Response<?>>> poll(List<Service> services,
			boolean block, long timeout) throws ServiceInvocationException,
			InterruptedException, ExecutionException {
		return poll(services, block, timeout, Service.getDefaultExecutor());
	}


	/**
	 * Poll all the services and return the responses. If block is true, the
	 * method is BLOCKED until all the outstanding responses are available or
	 * timeout is reached. If block is false, the method returns immediately
	 * with all available responses
	 * 
	 * @param services
	 *            the services to be polled
	 * @param block
	 *            blocking or non-blocking call
	 * @param timeout
	 *            timeout in milliseconds
	 * @param executor
	 *            the thread executor
	 * @return the map containing all the responses
	 * @throws ServiceInvocationException
	 *             throws this exception when the invocation failed
	 * @throws InterruptedException
	 *             throws this exception when the failed to wait
	 * @throws ExecutionException
	 *             throws this exception if the thread pool executor failed
	 */
	static Map<Service, List<Response<?>>> poll(List<Service> services,
			boolean block, long timeout, Executor executor)
			throws ServiceInvocationException, InterruptedException,
			ExecutionException {

		return pollInternal(services, block, true, timeout, executor);
	}

	private static Map<Service, List<Response<?>>> pollInternal(
			List<Service> services, boolean block, boolean partial)
			throws InterruptedException {
		Map<Service, List<Response<?>>> responses = new HashMap<Service, List<Response<?>>>();
		for (Service service : services) {
			responses.put(service, service.poll(block, partial));
		}
		return responses;
	}

	private static Map<Service, List<Response<?>>> pollInternal(
			List<Service> services, boolean block, boolean partial,
			long timeout, Executor executor) throws InterruptedException,
			ExecutionException {
		Map<Service, List<Response<?>>> responses = new HashMap<Service, List<Response<?>>>();
		List<PollTask> pollTasks = new LinkedList<PollTask>();

		for (Service service : services) {
			PollTask task = new PollTask(service, block, partial, timeout);
			executor.execute(task);
			pollTasks.add(task);
		}

		for (PollTask pollTask : pollTasks) {
			responses.put(pollTask.getService(), pollTask.get());
		}
		return responses;
	}

	private static class PollTask implements Runnable,
			Future<List<Response<?>>> {

		private final Service m_service;

		private final boolean m_block;

		private final boolean m_partial;

		private final long m_timeout;

		private boolean m_isDone = false;

		private final List<Response<?>> responses = new LinkedList<Response<?>>();

		private InterruptedException m_interruptedException = null;

		public PollTask(Service service, boolean block, boolean partial,
				long timeout) {
			m_service = service;
			m_block = block;
			m_partial = partial;
			m_timeout = timeout;
		}

		public Service getService() {
			return m_service;
		}

		public void run() {
			synchronized (responses) {
				try {
					responses.addAll(m_service.poll(m_block, m_partial,
							m_timeout));
				} catch (InterruptedException e) {
					m_interruptedException = e;
				} finally {
					m_isDone = true;
					responses.notify();
				}
			}
		}

		// Cancel will not work, false by default
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public List<Response<?>> get() throws InterruptedException,
				ExecutionException {
			synchronized (responses) {
				if (!isDone())
					responses.wait();

				if (m_interruptedException != null) {
					throw m_interruptedException;
				}

				return responses;
			}
		}

		public List<Response<?>> get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			return get();
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return m_isDone;
		}

	}

}
