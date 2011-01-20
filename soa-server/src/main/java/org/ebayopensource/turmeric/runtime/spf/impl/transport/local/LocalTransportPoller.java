/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.spf.impl.transport.local;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ITransportPoller;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.TransportPollerUtil;


public class LocalTransportPoller implements ITransportPoller {
	private final LinkedBlockingQueue<Future<?>> m_blockingQueue = new LinkedBlockingQueue<Future<?>>();

	public Future<?> poll() {
		return m_blockingQueue.poll();
	}

	public List<Future<?>> poll(boolean block) throws InterruptedException {
		return TransportPollerUtil.poll(this, block);
	}

	public List<Future<?>> poll(boolean block, long timeout)
			throws InterruptedException {
		return TransportPollerUtil.poll(this, block, timeout);
	}

	public Future<?> take() throws InterruptedException {
		return m_blockingQueue.take();
	}

	public BlockingQueue<Future<?>> getBlockingQueue() {
		return m_blockingQueue;
	}

	public Future<?> poll(long timeout) throws InterruptedException {
		return m_blockingQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}

}
