/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ITransportPoller;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;


public class ServicePoller implements IAsyncResponsePoller {

	private ITransportPoller m_poller;

	private final Map<Future<?>, Response<?>> m_mapping = Collections
			.synchronizedMap(new WeakHashMap<Future<?>, Response<?>>());

	private List<Response<?>> getAll(long timeout) throws InterruptedException {
		List<Response<?>> responses = new LinkedList<Response<?>>();

		if (m_poller == null) {
			forceGetAll(responses);
		} else {
			if (timeout < 0)
				getAllWithoutTimeout(timeout, responses);
			else
				getAllWithTimeout(timeout, responses);
		}
		return responses;
	}

	private void getAllWithTimeout(long timeout, List<Response<?>> responses)
			throws InterruptedException {
		int total = m_mapping.size();
		long starttime = System.currentTimeMillis();
		long currenttime = starttime;

		while (responses.size() < total && currenttime < starttime + timeout) {
			pollIntoList(true, responses, timeout - (currenttime - starttime));
			currenttime = System.currentTimeMillis();
		}
	}

	private void getAllWithoutTimeout(long timeout, List<Response<?>> responses)
			throws InterruptedException {
		int total = m_mapping.size();
		while (responses.size() < total)
			pollIntoList(true, responses, timeout);
	}

	private void forceGetAll(List<Response<?>> responses) {
		LinkedList<Future<?>> futures = new LinkedList<Future<?>>(m_mapping
				.keySet());
		getResponsesForFutures(responses, futures);
	}

	private List<Response<?>> poll(boolean block, long timeout)
			throws InterruptedException {
		List<Response<?>> responses = new LinkedList<Response<?>>();
		pollIntoList(block, responses, timeout);
		return responses;
	}

	private void pollIntoList(boolean block, List<Response<?>> responses,
			long timeout) throws InterruptedException {
		if (m_poller != null && m_mapping.size() > 0) {
			getResponsesForFutures(responses, m_poller.poll(block, timeout));
		}
	}

	private void getResponsesForFutures(List<Response<?>> responses,
			List<Future<?>> futures) {
		for (Future<?> future : futures) {
			Response<?> response = m_mapping.remove(future);
			if (response != null) {
				try {
					response.get();
				} catch (Throwable e) {
					// can't do much user can the exception when get() is
					// called.
					getLogger().log(
							Level.WARNING,
							"Exception calling get() on Response " + ": "
									+ e.toString(), e);
				}
				responses.add(response);
			}
		}
	}

	static Logger getLogger() {
		return LogManager.getInstance(ServicePoller.class);
	}

	public ITransportPoller getTransportPoller() {
		return m_poller;
	}

	public void setTransportPoller(ITransportPoller poller) {
		m_mapping.clear();
		m_poller = poller;
	}

	public List<Response<?>> poll(boolean block, boolean partial, long timeout)
			throws InterruptedException {

		if (m_poller == null)
			throw new UnsupportedOperationException(
					"Transport doesn't support polling");

		if (partial == false)
			return getAll(timeout);

		return poll(block, timeout);
	}

	public void add(Future<?> future, Response<?> response) {
		m_mapping.put(future, response);
	}

	public Response<?> remove(Future<?> future) {
		return m_mapping.remove(future);
	}

}
