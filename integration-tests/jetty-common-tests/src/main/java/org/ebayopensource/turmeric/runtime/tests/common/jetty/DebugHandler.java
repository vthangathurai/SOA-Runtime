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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.mortbay.jetty.Request;
import org.mortbay.log.Log;

public class DebugHandler extends org.mortbay.jetty.handler.HandlerWrapper {
    private static boolean enabled = false;

    public static void enable() {
        enabled = true;
    }
    
	public DebugHandler() {
        super();
        DebugHandler.enabled = false; // by default
    }

    @Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
        if(!enabled) {
            super.handle(target, request, response, dispatch);
            return;
        }
        
        Log.info(String.format("handle(\"%s\", HttpServletRequest, HttpServletResponse, %d)", target, dispatch));
        DebugHttpServletRequest debugRequest = new DebugHttpServletRequest(request);
		DebugHttpServletResponse debugResponse = new DebugHttpServletResponse(response);
		try {
			super.handle(target, debugRequest, debugResponse, dispatch);
		} finally {
            logRequest((Request) request);
			logResponse(debugResponse);
		}
    }

	private void logResponse(DebugHttpServletResponse debugResponse) {
		String ln = SystemUtils.LINE_SEPARATOR;
		byte responseBody[] = debugResponse.getCapturedBody();
		StringBuilder dbg = new StringBuilder();
		
		dbg.append(ln).append(" Status: ").append(debugResponse.getStatusCode());
		dbg.append(ln).append(" Reason: ").append(debugResponse.getStatusReason());
		dbg.append(ln).append(" Content-Length: ").append(debugResponse.getContentLength());

		Map<String,String> headers = debugResponse.getHeaders();
		dbg.append(ln).append(" Headers (").append(headers.size()).append(")");
		for(String name: headers.keySet()) {
			dbg.append(ln).append("  ").append(name).append(": ");
			dbg.append(headers.get(name));
		}
		
		dbg.append(ln).append(" Body.length: ").append(responseBody.length);
		dbg.append(ln).append(WordUtils.wrap(new String(responseBody, 0,
				responseBody.length), 70));
		
		Log.info("Response: " + dbg.toString());
	}

	private void logRequest(Request request) {
		String ln = SystemUtils.LINE_SEPARATOR;

		StringBuilder dbg = new StringBuilder();
		
		dbg.append(ln).append(" Method: ").append(request.getMethod());
		dbg.append(ln).append(" Request URI: ").append(request.getRequestURI());
		if(request.getQueryString() != null) {
			dbg.append("?").append(request.getQueryString());
		}
		
		@SuppressWarnings("unchecked")
		List<String> headerNames = Collections.list(request.getHeaderNames());
		dbg.append(ln).append(" HTTP Request Headers (").append(headerNames.size()).append(")");
		for(String name: headerNames) {
			dbg.append(ln).append("  ").append(name).append(": ");
			dbg.append(request.getHeader(name));
		}
		
		@SuppressWarnings("unchecked")
		List<String> paramNames = Collections.list(request.getParameterNames());
		dbg.append(ln).append(" Request Parameters (").append(paramNames.size()).append(")");
		for(String name: paramNames) {
			dbg.append(ln).append("  ").append(name).append(" = ");
			dbg.append(request.getParameter(name));
		}
		
		Log.info("Request: " + dbg.toString());
	}
}
