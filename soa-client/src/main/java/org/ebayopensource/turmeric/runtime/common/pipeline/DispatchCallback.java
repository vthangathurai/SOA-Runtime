/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

/**
 * (For future use)
 * This interface provides a callback for asynchronous dispatchers.  Its method is
 * called when dispatching is complete.
 * @author smalladi
 *
 */
public interface DispatchCallback {
	/**
	 * (For future use)
	 * Indicates that the asynchronous dispatcher has completed dispatching.
	 * @param status provides the status of an asynchronous dispatch, including a dispatch id which acts like a correlation id.
	 */
	public void dispatched(DispatchStatus status);
}
