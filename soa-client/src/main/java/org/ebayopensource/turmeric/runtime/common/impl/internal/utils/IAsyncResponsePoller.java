/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.utils;

import java.util.List;
import java.util.concurrent.Future;

import javax.xml.ws.Response;

public interface IAsyncResponsePoller {

	public ITransportPoller getTransportPoller();

	public void setTransportPoller(ITransportPoller poller);

	public List<Response<?>> poll(boolean block, boolean partial, long timeout)
			throws InterruptedException;

	public void add(Future<?> future, Response<?> response);

	public Response<?> remove(Future<?> future);

}
