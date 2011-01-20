/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.tests.spf.local;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ebayopensource.turmeric.runtime.spf.impl.transport.local.LocalTransportPoller;
import org.junit.Test;


public class LocalTransportPollerTest  {

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime
			.getRuntime().availableProcessors(), Runtime.getRuntime()
			.availableProcessors(), 200, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	@Test
	public void simpleTake() throws Exception {
		LocalTransportPoller completionQueue = new LocalTransportPoller();
		Sleeper aSleeper = new Sleeper(200, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(aSleeper));

		Future<?> future = completionQueue.take();
		assertTrue(future == aSleeper.getFuture());
		future.get();
	}

	@Test
	public void simplePoll() throws Exception {
		LocalTransportPoller completionQueue = new LocalTransportPoller();
		Sleeper aSleeper = new Sleeper(500, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(aSleeper));

		Future<?> future = completionQueue.poll();
		assertNull(future);
		for (int i = 0; i < 100 && future == null; i++) {
			future = completionQueue.poll();
			Thread.sleep(100);
		}
		assertTrue(future == aSleeper.getFuture());
		future.get();
	}

	@Test
	public void simpleBlockingPoll() throws Exception {
		LocalTransportPoller completionQueue = new LocalTransportPoller();
		Sleeper aSleeper = new Sleeper(200, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(aSleeper));

		List<Future<?>> futures = completionQueue.poll(true);
		assertTrue(futures.size() == 1);
		futures.get(0).get();
	}

	@Test
	public void simpleNonBlockingPoll() throws Exception {
		LocalTransportPoller completionQueue = new LocalTransportPoller();
		Sleeper aSleeper = new Sleeper(500, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(aSleeper));

		List<Future<?>> futures = completionQueue.poll(false);
		assertTrue(futures.size() == 0);

		futures = null;
		for (int i = 0; i < 10; i++) {
			futures = completionQueue.poll(false);
			if (futures.size() > 0)
				break;
			Thread.sleep(100);
		}
		assertTrue(futures.size() == 1);
		futures.get(0).get();
	}

	@Test
	public void blockingPoll() throws Exception {
		testPoll(true);
	}

	@Test
	public void nonBlockingPoll() throws Exception {
		testPoll(false);
	}

	
	private void testPoll(boolean block) throws Exception {
		LocalTransportPoller completionQueue = new LocalTransportPoller();
		Sleeper sleeper1 = new Sleeper(500, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(sleeper1));
		Sleeper sleeper2 = new Sleeper(200, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(sleeper2));
		Sleeper sleeper3 = new Sleeper(100, completionQueue.getBlockingQueue());
		executor.execute(new CustomFutureTask(sleeper3));

		int size = 0;
		List<Future<?>> futures = null;
		boolean r1Done = false, r2Done = false, r3Done = false;
		for (int i = 0; i < 300 && size < 3; i++) {
			futures = completionQueue.poll(block);
			if (block)
				assertTrue(futures.size() > 0);

			size += futures.size();
			for (Future<?> future : futures) {
				if (future == sleeper1.getFuture() && !r1Done)
					r1Done = true;
				else if (future == sleeper2.getFuture() && !r2Done)
					r2Done = true;
				else if (future == sleeper3.getFuture() && !r3Done)
					r3Done = true;
				else
					fail("Duplicate or unrecognized response");
			}
			futures = null;
			Thread.sleep(100);
		}

		assertTrue(size == 3);
		assertTrue(r1Done && r2Done && r3Done);
	}

	private static class CustomFutureTask extends FutureTask<Long> {
		public CustomFutureTask(Sleeper sleeper) {
			super(sleeper);
			sleeper.setFuture(this);
		}
	}

	private static class Sleeper implements Callable<Long> {
		private long wait = 100;

		private final BlockingQueue<Future<?>> blockingQueue;

		private FutureTask<Long> future;

		public Sleeper(long wait, BlockingQueue<Future<?>> blockingQueue) {
			this.wait = wait;
			this.blockingQueue = blockingQueue;
		}

		public void setFuture(FutureTask<Long> future) {
			this.future = future;
		}

		public FutureTask<Long> getFuture() {
			return this.future;
		}

		public Long call() throws Exception {
			try {
				Thread.sleep(wait);
				return new Long(wait);
			} finally {
				blockingQueue.add(future);
			}
		}
	}
}
