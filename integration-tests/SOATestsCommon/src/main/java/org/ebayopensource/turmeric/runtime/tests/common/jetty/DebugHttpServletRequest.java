/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.mortbay.log.Log;

class DebugHttpServletRequest extends HttpServletRequestWrapper
{
    private static final Logger LOG = Logger.getLogger("jetty.debug.request");
	private byte requestBody[];
	private boolean bodyAsInputStream = false;
	private boolean bodyAsReader = false;
	private BufferedReader bodyReader;
	private ServletInputStream bodyInputStream;
	
	public DebugHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		
		InputStream in = null;
		try {
			in = request.getInputStream();
			requestBody = IOUtils.toByteArray(in);
			LOG.info(String.format("Request Body (%,d bytes) : %n%s", requestBody.length,
					WordUtils.wrap( new String(requestBody,0,requestBody.length), 70) ));
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
	    LOG.info("getInputStream()");
		if(bodyInputStream != null) { 
			return bodyInputStream;
		}
		
		if (bodyAsReader) {
			throw new IOException(
					"Body content already opened as BufferedReader via .getReader()");
		}
		
		bodyAsInputStream = true;
		bodyInputStream = new ServletInputStream() {
			private InputStream in = new ByteArrayInputStream(requestBody);
			
			@Override
			public int read() throws IOException {
				return in.read();
			}
		};
		return bodyInputStream;
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
	    LOG.info("getReader()");
		if(bodyReader != null) {
			return bodyReader;
		}
		
		if(bodyAsInputStream) {
			throw new IOException(
			"Body content already opened as ServletInputStream via .getInputStream()");
		}
		
		bodyAsReader = true;
		bodyReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestBody)));
		
		return bodyReader;
	}
}