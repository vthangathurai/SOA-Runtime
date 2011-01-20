/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.concurrent.TimeUnit;

import com.ebay.kernel.bean.configuration.BaseConfigBean;
import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.cmdrunner.CommandRunnerPropertyBean;

/**
 * Thread pool configuration.
 * 
 * @author cyang
 */
public class ThreadPoolConfig extends BaseConfigBean {

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = -1564677834411056749L;

	/** 
	 * Default minimum thread pool size.
	 */
	public static final int DFLT_MIN_POOL_SIZE = 0;

	/** Default maximum thread pool size.  Currently it's set to have
	 * an unbounded thread pool size.
	 */	
	public static final int DFLT_MAX_POOL_SIZE = Integer.MAX_VALUE;

	/** Used to specify a time when the number of threads is greater
	 * than the core pool size.  This is the maximum time that excess
	 * idle threads will wait for new tasks before terminating.  The
	 * default keep alive time is 15 minutes.
	 */
	public static final long DFLT_KEEP_ALIVE_TIME_IN_SEC = 900;

	/** 
	 * Keep alive time threshold.
	 */
	public static final long KEEP_ALIVE_TIME_IN_SEC_THRESHOLD = 60;
	
	/** 
	 * Default keep alive time unit.
	 */
	public static final TimeUnit DFLT_KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

	/** 
	 * Thread pool's keep alive time.
	 */
	private long m_keepAliveTimeInSec = DFLT_KEEP_ALIVE_TIME_IN_SEC;
	/**
	 * Thread pool's keep alive time bean properties.
	 */
	public static final BeanPropertyInfo KEEP_ALIVE_TIME_IN_SEC =
		createBeanPropertyInfo(
			"m_keepAliveTimeInSec", "KEEP_ALIVE_TIME_IN_SEC", true);

	/** 
	 * True if use command runner as the thread pool.
	 */
	static final boolean DFLT_USE_CMD_RUNNER = false;
	private boolean m_useCmdRunner = DFLT_USE_CMD_RUNNER;
	/**
	 * Bean property to control using command runner as the thread pool.
	 */
	public static final BeanPropertyInfo USE_CMD_RUNNER = 
		createBeanPropertyInfo("m_useCmdRunner", "USE_CMD_RUNNER", true);

	/** Command runner properties used for the event thread pool if specified.
	 * Not a bean property.
	 */
	private CommandRunnerPropertyBean m_cmdRunnerProps = null;
	
	/**
	 * Contructor.
	 * @param category base config category information
	 * @param keepAliveTimeInSec thread pool's keep alive time in sec.
	 * @param useCmdRunner boolean to indicate that command runner is used.
	 * @param cmdRunnerProps command runner properties
	 */
	public ThreadPoolConfig(BeanConfigCategoryInfo category,
							long keepAliveTimeInSec,
							boolean useCmdRunner,
							CommandRunnerPropertyBean cmdRunnerProps)
	{
		m_keepAliveTimeInSec = keepAliveTimeInSec;
		if (keepAliveTimeInSec < KEEP_ALIVE_TIME_IN_SEC_THRESHOLD) {
			m_keepAliveTimeInSec = KEEP_ALIVE_TIME_IN_SEC_THRESHOLD;
		}
		m_cmdRunnerProps = cmdRunnerProps;
		m_useCmdRunner = useCmdRunner;
		if (m_useCmdRunner && m_cmdRunnerProps == null) {
			m_cmdRunnerProps = CommandRunnerPropertyBean.defaultCommandRunnerProperties();
		}
			
		init(category, true);
		addVetoableChangeListener(new ThreadPoolVetoListener(KEEP_ALIVE_TIME_IN_SEC));
	}
	
	/**
	 * Constructor.
	 * @param category base config category information.
	 */
	public ThreadPoolConfig(BeanConfigCategoryInfo category) {
		this(category, DFLT_KEEP_ALIVE_TIME_IN_SEC, false, null);
	}
	
	/**
	 * Constructor.
	 * @param category base config category information.
	 * @param keepAliveTimeInSecs The keep alive time in seconds.
	 */
	public ThreadPoolConfig(BeanConfigCategoryInfo category, long keepAliveTimeInSecs) {
		this(category, keepAliveTimeInSecs, false, null);
	}

	/**
	 * Constructor.
	 * @param category base config category information
	 * @param useCmdRunner boolean to indicate that command runner is used.
	 */
	public ThreadPoolConfig(BeanConfigCategoryInfo category, boolean useCmdRunner) {			
		this(category, DFLT_KEEP_ALIVE_TIME_IN_SEC,	useCmdRunner, null);
	}
	
	/**
	 * Constructor.
	 * @param category base config category information
	 * @param useCmdRunner boolean to indicate that command runner is used.
	 * @param cmdRunnerProps command runner properties
	 */
	public ThreadPoolConfig(BeanConfigCategoryInfo category,
							boolean useCmdRunner,
							CommandRunnerPropertyBean cmdRunnerProps)
	{			
		this(category, DFLT_KEEP_ALIVE_TIME_IN_SEC,	useCmdRunner, cmdRunnerProps);
	}
	
	//------------------------- Accessors -----------------------

	/**
	 * @return True if command runner is used as thread pool.
	 */
	public boolean isUseCmdRunner() {
		return m_useCmdRunner;
	}
	
	/**
	 * @return The command runner property bean.
	 */
	public CommandRunnerPropertyBean getCmdRunnerProps() {
		return m_cmdRunnerProps;
	}
	
	/**
	 * @return The keep alive time in second.
	 */
	public long getKeepAliveTimeInSec() {
		return m_keepAliveTimeInSec;
	}
	
	/**
	 * Sets the keep alive time in second as given.
	 * @param newValue The new keep alive time in second.
	 */
	public void setKeepAliveTimeInSec(final long newValue) {
		changeProperty(KEEP_ALIVE_TIME_IN_SEC, m_keepAliveTimeInSec, newValue);
	}

	/**
	 * Veto listener for the thread pool.
	 */
	static class ThreadPoolVetoListener implements VetoableChangeListener {
		private BeanPropertyInfo m_propertyInfo;

		/**
		 * @param propertyInfo The bean property info.
		 */
		ThreadPoolVetoListener(final BeanPropertyInfo propertyInfo) {
			m_propertyInfo = propertyInfo;
		}

		/**
		 * Ensure that only certain properties are changeable.
		 * @param evt PropertyChangeEvent
		 * @exception PropertyVetoException Exception when failed.
		 */
		public void vetoableChange(PropertyChangeEvent evt) 
			throws PropertyVetoException
		{
			BeanPropertyInfo i = BaseConfigBean.getBeanPropertyInfo(evt);
			if (i != m_propertyInfo) {
				throw new PropertyVetoException(
						evt.getPropertyName()+" cannot be changed.", evt);					
			}
			long newValue = ((Long)evt.getNewValue()).longValue();
			if (newValue < 0 || newValue < KEEP_ALIVE_TIME_IN_SEC_THRESHOLD) {
				throw new PropertyVetoException(
					evt.getPropertyName()+" cannot be smaller than "+ KEEP_ALIVE_TIME_IN_SEC_THRESHOLD,
					evt);
			}
		}
	}
}
