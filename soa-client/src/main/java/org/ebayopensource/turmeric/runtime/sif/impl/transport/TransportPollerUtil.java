/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.impl.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ITransportPoller;


public class TransportPollerUtil {

	public static List<Future<?>> poll(ITransportPoller poller, boolean block)
			throws InterruptedException {
		List<Future<?>> pollResult = new ArrayList<Future<?>>();

		if (block)
			pollResult.add(poller.take());

		Future<?> future = null;

		while ((future = poller.poll()) != null)
			pollResult.add(future);

		return pollResult;
	}

	public static List<Future<?>> poll(ITransportPoller poller, boolean block,
			long timeout) throws InterruptedException {

		if (timeout < 0) {
			return poll(poller, block);
		}

		List<Future<?>> pollResult = new ArrayList<Future<?>>();
		long starttime = System.currentTimeMillis();
		long currenttime = starttime;

		Future<?> future = null;
		do {
			if ((future = poller.poll(timeout - (currenttime - starttime))) != null) {
				pollResult.add(future);
			} else
				break;
		} while ((currenttime = System.currentTimeMillis()) < starttime
				+ timeout);

		return pollResult;
	}

}
