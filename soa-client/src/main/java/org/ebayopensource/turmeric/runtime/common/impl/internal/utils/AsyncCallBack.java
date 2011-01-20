/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.utils;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

public interface AsyncCallBack {

	void onResponseInContext();

	void onException(Throwable cause);

	void onTimeout();
	
	boolean isDone();

	void onResponseInContext(RunBefore runBefore);
	
	static interface RunBefore {
		
		void run() throws ServiceException;
		
	}
}
