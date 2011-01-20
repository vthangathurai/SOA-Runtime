/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;


public interface AsyncMessageContext {

	public void setClientAsyncHandler(AsyncHandler handler);

	public AsyncHandler getClientAsyncHandler();

	public Future<?> getFutureResponse();

	public void setFutureResponse(Future<?> futureResponse);

	public Executor getExecutor();

	public void setExecutor(Executor executor);

	public void setServiceAsyncCallback(AsyncCallBack callback);

	public AsyncCallBack getServiceAsyncCallback();

}
