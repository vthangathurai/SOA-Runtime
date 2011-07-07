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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.log.Log;
import org.mortbay.util.TypeUtil;

/**
 * Primitive Access Log handler to just dump access log style requests to the console.
 */
public class AccessLoggingHandler extends
		org.mortbay.jetty.handler.HandlerWrapper {
	private boolean logServer = true;
	private boolean logExtended = true;
	private boolean logCookies = true;
	private boolean logLatency = true;

	private void createLogEntry(int dispatch, Request request, Response response) {
		StringBuilder buf = new StringBuilder();

		switch (dispatch) {
		case REQUEST:
			buf.append("[REQUEST] ");
			break;
		case DEFAULT:
			buf.append("[DEFAULT] ");
			break;
		case ERROR:
			buf.append("[ERROR] ");
			break;
		case FORWARD:
			buf.append("[FORWARD] ");
			break;
		case INCLUDE:
			buf.append("[INCLUDE] ");
			break;
		default:
			buf.append("[Dispatch:").append(dispatch).append("] ");
		}
		
		if (logServer) {
			buf.append(request.getServerName());
			buf.append(' ');
		}

		buf.append(request.getRemoteAddr());
		buf.append(" - ");
		String user = request.getRemoteUser();
		buf.append((user == null) ? " - " : user);
		buf.append(" [");
		buf.append(request.getTimeStampBuffer().toString());

		buf.append("] \"");
		buf.append(request.getMethod());
		buf.append(' ');

		buf.append(request.getUri());

		buf.append(' ');
		buf.append(request.getProtocol());
		buf.append("\" ");
		int status = response.getStatus();
		if (status <= 0) {
			status = 404;
		}
		buf.append((char) ('0' + ((status / 100) % 10)));
		buf.append((char) ('0' + ((status / 10) % 10)));
		buf.append((char) ('0' + (status % 10)));

		long responseLength = response.getContentCount();
		if (responseLength >= 0) {
			buf.append(' ');
			if (responseLength > 99999) {
				buf.append(Long.toString(responseLength));
			} else {
				if (responseLength > 9999)
					buf.append((char) ('0' + ((responseLength / 10000) % 10)));
				if (responseLength > 999)
					buf.append((char) ('0' + ((responseLength / 1000) % 10)));
				if (responseLength > 99)
					buf.append((char) ('0' + ((responseLength / 100) % 10)));
				if (responseLength > 9)
					buf.append((char) ('0' + ((responseLength / 10) % 10)));
				buf.append((char) ('0' + (responseLength) % 10));
			}
			buf.append(' ');
		} else {
			buf.append(" - ");
		}

		if (logExtended) {
			String referer = request.getHeader(HttpHeaders.REFERER);
			if (referer == null) {
				buf.append("\"-\" ");
			} else {
				buf.append('"').append(referer).append("\" ");
			}

			String agent = request.getHeader(HttpHeaders.USER_AGENT);
			if (agent == null) {
				buf.append("\"-\" ");
			} else {
				buf.append('"').append(agent).append('"');
			}
		}

		if (logCookies) {
			Cookie[] cookies = request.getCookies();
			if (cookies == null || cookies.length == 0) {
				buf.append(" -");
			} else {
				buf.append(" \"");
				for (int i = 0; i < cookies.length; i++) {
					if (i != 0) {
						buf.append(';');
					}
					buf.append(cookies[i].getName());
					buf.append('=');
					buf.append(cookies[i].getValue());
				}
				buf.append('\"');
			}
		}

		if (logLatency) {
			buf.append(' ');
			buf.append(TypeUtil.toString(System.currentTimeMillis()
					- request.getTimeStamp()));
		}

		Log.info(buf.toString());
	}

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
	    try {
	        super.handle(target, request, response, dispatch);
	    } finally {
	        createLogEntry(dispatch, (Request) request, (Response) response);
	    }
	}
}