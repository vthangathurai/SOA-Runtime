/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ITransportPoller;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.TransportPollerUtil;

import com.ebay.kernel.service.invocation.client.http.nio.CompletionQueue;

public class HTTPSyncAsyncClientTransportPoller extends CompletionQueue
		implements ITransportPoller {

	/**
	 * Retrieves and removes all at the head of this queue. It returns null if
	 * the queue is empty when <tt>block</tt> is false otherwise waits until
	 * one item is available.
	 * 
	 * @param block
	 *            <tt>true</tt> if the call blocks when the queue is empty and
	 *            <tt>false</tt> returns after emptying the queue.
	 * @throws InterruptedException
	 *             if interrupted while waiting.
	 * @return all available entries of the queue, when <tt>block</tt> is
	 *         false get at least one entry.
	 */
	public List<Future<?>> poll(boolean block) throws InterruptedException {
		return TransportPollerUtil.poll(this, block);
	}

	/**
	 * Retrieves and removes all at the head of this queue. It returns null if
	 * the queue is empty when <tt>block</tt> is false otherwise waits until
	 * one item is available.
	 * 
	 * @param block
	 *            <tt>true</tt> if the call blocks when the queue is empty and
	 *            <tt>false</tt> returns after emptying the queue.
	 * @param timeout
	 *            in miliseconds
	 * @throws InterruptedException
	 *             if interrupted while waiting.
	 * @return all available entries of the queue, when <tt>block</tt> is
	 *         false get at least one entry.
	 */
	public List<Future<?>> poll(boolean block, long timeout)
			throws InterruptedException {
		return TransportPollerUtil.poll(this, block, timeout);
	}

	public Future<?> poll(long timeout) throws InterruptedException {
		return poll(timeout, TimeUnit.MILLISECONDS);
	}

}
