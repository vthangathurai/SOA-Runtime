/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.log.Log;

/**
 * Some tests assume that the server process is slow. So this handler simply adds a parameterized delay to the
 * processing of a request, simulating a slow server.
 */
public class IntentionalDelayHandler extends HandlerWrapper {

	/**
	 * Delay to add, in milliseconds.
	 */
	private long delay = 2000;

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		/* only delay proper requests, not internal routing */
		if (dispatch == Handler.REQUEST) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				Log.warn("Unable to cause intentional delay of " + delay
						+ " ms: " + e.getClass().getName() + ": "
						+ e.getMessage());
			}
		}
		super.handle(target, request, response, dispatch);
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + " (delay: " + delay + ")";
	}
}
