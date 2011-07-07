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
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.Response;
import org.mortbay.log.Log;

public class DebugHttpServletResponse extends HttpServletResponseWrapper {
    private static final Logger LOG = Logger.getLogger("jetty.debug.response");

	class DelegatingServletOutputStream extends ServletOutputStream {
		private OutputStream out;

		public DelegatingServletOutputStream(OutputStream out) {
			this.out = out;
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}
	}

	private ByteArrayOutputStream captured = new ByteArrayOutputStream();

	public DebugHttpServletResponse(HttpServletResponse response) {
		super(response);
	}
	
	public byte[] getCapturedBody() {
		return captured.toByteArray();
	}
	
	private Response getJettyResponse() {
		return (Response) getResponse();
	}
	
	public Map<String, String> getHeaders() {
		HttpFields fields = getJettyResponse().getHttpFields();
		Map<String,String> headers = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> names = fields.getFieldNames();
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			headers.put(name, fields.getStringField(name));
		}
		
		return headers;
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new DelegatingServletOutputStream(new TeeOutputStream(
				super.getOutputStream(), captured));
	}
	
	private void debug(String format, Object ... args) {
	    LOG.info("## DEBUG ## " + String.format(format, args));
	}
	
	@Override
	public void sendError(int sc) throws IOException {
	    debug("sendError(%d)", sc);
	    super.sendError(sc);
	}
	
	@Override
	public void sendError(int sc, String msg) throws IOException {
	    debug("sendError(%d, \"%s\")%n", sc, msg);
	    super.sendError(sc, msg);
	}
	
	@Override
	public void setHeader(String name, String value) {
	    debug("setHeader(\"%s\", \"%s\")%n", name, value);
	    super.setHeader(name, value);
	}
	
	@Override
	public void setStatus(int sc) {
	    debug("setStatus(%d)%n", sc);
	    super.setStatus(sc);
	}
	
	@Override
	public void setStatus(int sc, String sm) {
	    debug("setStatus(%d, \"%s\")%n", sc, sm);
	    super.setStatus(sc, sm);
	}
	
	public int getStatusCode() {
		return getJettyResponse().getStatus();
	}
	
	public String getStatusReason() {
		return getJettyResponse().getReason();
	}

	public long getContentLength() {
		return getJettyResponse().getContentCount();
	}
}
