/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.util.List;

/**
 * Initializer to be loaded upon server startup, for each service's servlet that 
 * doesn't explicitly block it. 
 * 
 * An initializer must have an empty constructor and should keep no state. Also, 
 * it should be multi-threaded as it may be called with various service names from
 * different threads. 
 * 
 * @author mpoplacenel
 */
public interface Initializer {

	/**
	 * Performs the initialization action for the given service name. Should
	 * be thread-safe. 
	 * 
	 * @param serviceName the name of the service to initialize. 
	 * 
	 * @throws InitializerException wrapping any domain-specific checked exception.
	 */
	void initialize(String serviceName) throws InitializerException;
	
	List<String> getServiceNames();
	
	/**
	 * Exception thrown by initializers, wrapping whatever the internal problem is. 
	 */
	public class InitializerException extends Exception {

		private static final long serialVersionUID = -1L;

		/**
		 * Cause-only constructor. 
		 * 
		 * @param cause the original exception
		 */
		public InitializerException(Throwable cause) {
			super(cause);
		}

		/**
		 * Full constructor. 
		 * @param message the error message.
		 * @param cause the original exception.
		 */
		public InitializerException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
