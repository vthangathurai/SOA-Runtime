/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.common.impl.internal.utils;

import java.util.List;
import java.util.concurrent.Future;

/**
 * This class is a specification for a transport supplied "completion queue"
 * identified by SIF. "Completion Queue" is loosly speaking a "design pattern"
 * for fetching results of asynchronously queued tasks.
 * 
 * Tranports that provide implementation for this interface, provide the ability
 * to fetch results of "pull" type invocation of SOA requests through the
 * completion queue design pattern
 * 
 * @author cpenkar
 * 
 */
public interface ITransportPoller {

	/**
	 * Retrieves and removes the head of this queue, waiting if no elements are
	 * present on this queue. It follows the same semantics as
	 * <tt>BlockingQueue.take()</tt>.
	 * 
	 * @return the head of this queue
	 * @throws InterruptedException
	 *             if interrupted while waiting.
	 * @see java.util.concurrent.BlockingQueue
	 */
	public Future<?> take() throws InterruptedException;

	/**
	 * Retrieves and removes the head of this queue, or returns null if this
	 * queue is empty. It follows the same semantics as <tt>Queue.poll()</tt>.
	 * 
	 * @return the head of the queue, or <tt>null</tt> if this queue is empty.
	 */
	public Future<?> poll();

	/**
	 * Same as poll() with timeout in miliseconds</tt>.
	 * 
	 * @param timeout
	 *            in miliseconds
	 * @return the head of the queue, or <tt>null</tt> if this queue is empty.
	 * @throws InterruptedException
	 *             if interrupted while waiting.
	 */
	public Future<?> poll(long timeout) throws InterruptedException;

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
	 * @return all available entries of the queue, <tt>null</tt> if this queue
	 *         is empty when <tt>block</tt> is false.
	 */
	public List<Future<?>> poll(boolean block) throws InterruptedException;

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
			throws InterruptedException;
}
