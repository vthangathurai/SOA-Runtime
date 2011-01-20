/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.ebay.kernel.cmdrunner.CommandRunner;
import com.ebay.kernel.cmdrunner.CommandRunnerFactory;
import com.ebay.kernel.cmdrunner.CommandRunnerPropertyBean;
import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * A factory that creates either a command runner or a Java thread pool.
 * 
 * NOTE thread pool should not be created lightly in SOA runtime.  
 * 
 * @author cyang
 */
public class ThreadPoolFactory {
	private static final ThreadPoolFactory INSTANCE = new ThreadPoolFactory();
	private static final int DFLT_MIN_POOL_SIZE = 0;
	private static final int DFLT_MAX_POOL_SIZE = Integer.MAX_VALUE;
	private static final TimeUnit DFLT_KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
	private static Logger s_logger = null;
	
	/**
	 * Singleton accessor.
	 * 
	 * @return the singleton object
	 */
	public static ThreadPoolFactory getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Create a command runner or a Java cached thread pool with 0 core and
	 * an unbounded max pool size.  This method creates a command runner thread
	 * pool if the CommandRunnerPropertyBean parameter is not null.
	 * @param threadPoolName String that denotes the thread pool name
	 * @param keepAliveTimeInSec when the number of threads is greater than the core
     * the core, this is the maximum time that excess idle threads will wait for new
	 * remain in the pool 
	 * @param cmdRunnerProps CommandRunnerPropertyBean that provides command runner
	 * thread pool configuration.
	 * @return Executor
	 */
	public Executor createExecutor(final String threadPoolName,
									final long keepAliveTimeInSec,
									final CommandRunnerPropertyBean cmdRunnerProps)
	{
		if (threadPoolName == null) {
			throw new IllegalArgumentException(
				"Failed to create thread pool because pool name is null!");
		}
		if (cmdRunnerProps != null) {
			// no need to set useDaemon thread as it's the default
			return CommandRunnerFactory.createCommandRunner(cmdRunnerProps);
		}
		return createJavaCachedThreadPool(threadPoolName, keepAliveTimeInSec);
	}
	
	private Executor createJavaCachedThreadPool(String threadPoolName, long keepAliveTimeInSec) {
		ThreadFactory threadFactory = new DaemonThreadFactory(threadPoolName);
		// create a thread pool with a 0 core pool size and an unbounded max pool size
        return new ThreadPoolExecutor
        	(DFLT_MIN_POOL_SIZE, DFLT_MAX_POOL_SIZE, keepAliveTimeInSec,
        	DFLT_KEEP_ALIVE_TIME_UNIT, new SynchronousQueue<Runnable>(),
        	threadFactory);
	}
	
	/**
	 * Shut down the thread pool accordingly.
	 * @param executor thread pool
	 * @param useCmdRunner whether to use command runner or not
	 */
	public void shutdown(final Executor executor, final boolean useCmdRunner) {
		if (executor == null) {
			getLogger().log(LogLevel.WARN, "ThreadPoolFactory: can't shutdown a null executor.");
			return;
		}
		
		if (useCmdRunner) {
			final CommandRunner cr = (CommandRunner)executor;
			
			if (getLogger().isDebugEnabled()) {
				getLogger().log(LogLevel.DEBUG,
					"Shutting down command runner thread pool: "+cr.getName());
				getLogger().log(LogLevel.DEBUG,
					"... current active thread count: "+cr.getActiveThreadCount());				
			}
			cr.shutdown();
		} else {
			final ThreadPoolExecutor ex = (ThreadPoolExecutor)executor;
			if (getLogger().isDebugEnabled()) {
				getLogger().log(LogLevel.DEBUG,
					"Shutting down Java thread pool with active thread count: "+ex.getActiveCount());
			}
			ex.shutdown();
		}
	}
	
	private static Logger getLogger() {
		if (s_logger == null) {
			s_logger = Logger.getInstance(ThreadPoolFactory.class);
		}
		return s_logger;
	}

	//------------------- Inner Class ------------------
	
	/**
	 * Custom thread factory to create daemon threads.
	 */
	private static final class DaemonThreadFactory implements ThreadFactory {
		private final ThreadFactory m_defaultFactory = Executors.defaultThreadFactory();
		private final String m_threadPoolName;
		private final AtomicInteger m_threadId = new AtomicInteger(1);

		DaemonThreadFactory(final String threadPoolName) {
			m_threadPoolName = threadPoolName;
		}
		
		/**
		 * (non-Javadoc)
		 * @see java.util.concurrent.ThreadFactory#newThread()
		 */
		public Thread newThread(Runnable runnable) {
			Thread t = m_defaultFactory.newThread(runnable);
			t.setName(m_threadPoolName + "_" + m_threadId.getAndIncrement());
			if (!t.isDaemon()) {
				t.setDaemon(true);
			}
			return t;
		}
	}
}
