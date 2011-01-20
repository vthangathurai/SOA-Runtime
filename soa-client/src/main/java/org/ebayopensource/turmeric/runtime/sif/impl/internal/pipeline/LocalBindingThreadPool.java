/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.utils.ThreadPoolConfig;
import org.ebayopensource.turmeric.runtime.common.utils.ThreadPoolFactory;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;

import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * Global thread pool for local communication between SOA client and server.
 * @author cyang
 */
public final class LocalBindingThreadPool {
	private static final String THREAD_POOL_NAME_PREFIX = "LocalBinding";
	private static final String THREAD_POOL_NAME_SEPARATOR = "-";
	private static final String GLOBAL_THREAD_POOL_CATEGORY_ID =
		"ebay.soaframework.sif.GlobalLocalBindingThreadPool";
	
	private static LocalBindingThreadPool INSTANCE = null;
		

	private ThreadPoolConfig m_config;
	private ThreadPoolExecutor m_threadPool;
	private String m_threadPoolName;
	
	private static Logger s_logger = null;
	
	/**
	 * Convenient method to create a global thread pool for local binding
	 * @return LocalBindingThreadPool
	 */
	public static synchronized LocalBindingThreadPool getInstance() {
		if(INSTANCE == null){
			INSTANCE = new LocalBindingThreadPool(null, GLOBAL_THREAD_POOL_CATEGORY_ID, "global");
		}
		return INSTANCE;
	}

	/**
	 * Constructor
	 * @param categoryInfo config bean category info
	 * @param categoryId config bean category id
	 * @param threadPoolNameSuffix thread pool name suffix
	 */
	private LocalBindingThreadPool(final BeanConfigCategoryInfo categoryInfo,
									final String categoryId,
									final String threadPoolNameSuffix)
	{
		try{
			if (threadPoolNameSuffix == null || threadPoolNameSuffix.length() == 0) {
				throw new IllegalArgumentException
					("LocalBindingThreadPool: invalid thread pool name suffix!");			
			}
	
			BeanConfigCategoryInfo beanInfo = categoryInfo;
			if (beanInfo == null) {
				if (categoryId == null) {
					throw new IllegalArgumentException
						("Both config category info and config category id are null!");				
				}
				try {
					//System.out.println("<><> Creating new BeanConfigCategoryInfo in LocalBindingThreadPool.. categoryId:"+categoryId);
					beanInfo =
						BeanConfigCategoryInfo.createBeanConfigCategoryInfo(
							categoryId, // categoryId
							categoryId, // alias
							"ebay.soaframework.sif", // group
							true, // isPersistent
							true, // opsManagable
							null, // persistFileUri,
							"SOA client local binding thread pool configuration", // description; 
							true); //return existing one.
					
				} catch (ConfigCategoryCreateException e) {
					// should not happened... log it
					getLogger().log(LogLevel.ERROR, e);
					//System.out.println("<><><><> ERROR while ConfigCategoryCreateException creating LocalBindingThreadPool.. ------------");
					//e.printStackTrace();
					//System.out.println("---------- <><><><> ");
				}
			}
			// fetch the keep alive time from the static config
			long keepAliveTimeFromGlobalConfig = ThreadPoolConfig.DFLT_KEEP_ALIVE_TIME_IN_SEC;
			try {
				Long keepAliveInSecs = ClientConfigManager.getInstance().
					getGlobalConfig().getThreadKeepAliveTimeInSec();
				if (keepAliveInSecs != null) {
					keepAliveTimeFromGlobalConfig = keepAliveInSecs.longValue();
				}
			} catch (ServiceException se) {
				// should not happened... log it
				getLogger().log(LogLevel.ERROR, se.getLocalizedMessage());
				/*System.out.println("<><><><> ERROR ServiceException while creating LocalBindingThreadPool.. ------------");
				se.printStackTrace();
				System.out.println("---------- <><><><> ");*/
	
			}
			// create config bean
			m_config = new ThreadPoolConfig(beanInfo, keepAliveTimeFromGlobalConfig);
			// create the global thread pool
			m_threadPoolName = buildThreadPoolName
				(THREAD_POOL_NAME_PREFIX, threadPoolNameSuffix, !m_config.isUseCmdRunner());
			m_threadPool = (ThreadPoolExecutor)ThreadPoolFactory.getInstance().createExecutor
				(m_threadPoolName, m_config.getKeepAliveTimeInSec(), m_config.getCmdRunnerProps());
			System.out.println("<LocalBindingThreadPool> m_threadPoolName: "+m_threadPoolName+", m_threadPool:"+m_threadPool+", categoryId:"+categoryId);
		}catch(Throwable e){
			getLogger().log(LogLevel.ERROR, e.getLocalizedMessage());
			/*System.out.println("<><><><> ERROR ServiceException while creating LocalBindingThreadPool.. ------------");
			e.printStackTrace();
			System.out.println("---------- <><><><> ");*/
		}
	}
	
	/**
	 * Returns the configuration of the thread pool
	 */
	public ThreadPoolConfig getConfiguration() {
		return m_config;
	}
	
	/**
	 * Take a snapshot of the thread pool statistics
	 */
	public ThreadPoolStats getStatistics() {
		return new ThreadPoolStats(m_threadPool, m_threadPoolName);
	}
	
	/**
	 * Submits a task to the thread pool with a Callable.  The task is wrapped inside a
	 * future response which is returned immediately to the caller.
	 * @param callable Callable that carries out the task.
	 * @return Future<MessageContext> a future response for caller to wait on if
	 * desired.
	 */
	public Future<MessageContext> execute(final Callable<MessageContext> callable) {
        if (callable == null) {
        	throw new IllegalArgumentException("task runnable cannot be null!");
        }
        FutureTask<MessageContext> futureTask = new FutureTask<MessageContext>(callable);
        m_threadPool.execute(futureTask);

        return futureTask;
	}
	
	public Future<MessageContext> execute(final FutureTask<MessageContext> futureTask) {
        if (futureTask == null) {
        	throw new IllegalArgumentException("task runnable cannot be null!");
        }
        //FutureTask<MessageContext> futureTask = new FutureTask<MessageContext>(callable);
        m_threadPool.execute(futureTask);

        return futureTask;
	}

	/**
	 * Shutdown the thread pool.
	 */
	public void shutdown() {
		ThreadPoolFactory.getInstance().shutdown(m_threadPool, m_config.isUseCmdRunner());
	}

	private String buildThreadPoolName(String prefix, String suffix, boolean appendSystemTime) {
		StringBuilder sb = new StringBuilder(50);
		sb.append(prefix).append(THREAD_POOL_NAME_SEPARATOR).append(suffix);
		if (appendSystemTime) {
			sb.append(THREAD_POOL_NAME_SEPARATOR);
			sb.append(System.currentTimeMillis());
		}
		return sb.toString();
	}
	
	private static Logger getLogger() {
		if (s_logger == null) {
			s_logger = Logger.getInstance(LocalBindingThreadPool.class);
		}
		return s_logger;
	}
	
	//---------------- Inner class ---------------

	public static final class ThreadPoolStats {
		private String m_threadPoolName;
		private long m_keepAliveTimeInSecs;
		private int m_activeCount;
		private int m_poolSize;
		
		ThreadPoolStats(final ThreadPoolExecutor tp, final String tpName) {
			m_threadPoolName = tpName;
			m_keepAliveTimeInSecs = tp.getKeepAliveTime(TimeUnit.SECONDS);
			m_activeCount = tp.getActiveCount();
			m_poolSize = tp.getPoolSize();
		}
		
		/**
		 * Returns thread pool name
		 */
		public String getThreadPoolname() {
			return m_threadPoolName;
		}
		
		/**
		 * Returns thread pool keep alive time in seconds
		 */
		public long getKeepAliveTimeInSecs() {
			return m_keepAliveTimeInSecs;
		}
		
		/**
		 * Returns the approximate number of active threads
		 */
		public int getActiveCount() {
			return m_activeCount;
		}
		
		/**
		 * Returns the total number of threads in the pool
		 */
		public int getPoolSize() {
			return m_poolSize;
		}
		
		/**
		 * Returns the current number of idle thread
		 */
		public int getIdleThreadCount() {
			return m_poolSize - m_activeCount;
		}
		
		private static String TAB = "\t";
		private static String NEWLINE = "\n";
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(m_threadPoolName).append(" statistics: ");
			sb.append(new Date().toString()).append(NEWLINE);
			sb.append(TAB).append("Approximate number of active threads: ");
			sb.append(m_activeCount).append(NEWLINE);
			sb.append(TAB).append("Total number of threads in pool: ");
			sb.append(m_poolSize).append(NEWLINE);
			sb.append(TAB).append("Total number of idle thread count: ");
			sb.append(getIdleThreadCount()).append(NEWLINE);
			sb.append(TAB).append("Keep alive time in seconds: ");
			sb.append(m_keepAliveTimeInSecs).append(NEWLINE);
			return sb.toString();
		}
	}
}
