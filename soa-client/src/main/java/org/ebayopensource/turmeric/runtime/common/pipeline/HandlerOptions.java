/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.Collections;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * This class holds processed configuration information about a handler.  A list
 * of HandlerOptions is provided at pipeline initialization.
 * @author ichernyshev
 */
public final class HandlerOptions {
	private String m_name;
	private String m_className;
	private Map<String,String> m_options;
	private boolean m_continueOnError;
	private boolean m_runOnError;

	/**
	 * Constructor. Called by the framework.
	 * @param name the name of the handler
	 * @param className the class name of the handler implementation
	 * @param options the options (configuration property map) from the handler configuration
	 * @param continueOnError indicates whether to continue execution of the pipeline
	 * even if this handler throws an exception.
	 * @param runOnError indicates whether the handler should be invoked even
	 * when terminating exceptions occur in the pipeline.
	 */
	public HandlerOptions(String name, String className,
		Map<String,String> options, boolean continueOnError, boolean runOnError)
	{
		m_name = name;
		m_className = className;

		if (options != null) {
			m_options = Collections.unmodifiableMap(options);
		} else {
			m_options = CollectionUtils.EMPTY_STRING_MAP;
		}

		m_continueOnError = continueOnError;
		m_runOnError = runOnError;
	}

	/**
	 * Returns the name of the handler.
	 * @return the name of the handler
	 */
	public String getName() {
		 return m_name;
	 }

	 /**
	  * Returns the class name of the handler implementation.
	  * @return the class name
	  */
	public String getClassName() {
		return m_className;
	}

	/**
	 * Returns the options (configuration property map) from the handler configuration.
	 * @return the options map
	 */
	public Map<String,String> getOptions() {
		return m_options;
	}

	/**
	 * Indicates whether to continue execution of the pipeline even if this handler
	 * throws an exception.
	 * @return whether to continue on exceptions
	 */
	public boolean isContinueOnError() {
		return m_continueOnError;
	}

	/**
	 * Indicates whether the handler should be invoked even
	 * when terminating exceptions occur in the pipeline.
	 * @return whether to run the handler even when there are terminating exceptions
	 */
	public boolean isRunOnError() {
		return m_runOnError;
	}
}
