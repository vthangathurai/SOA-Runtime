/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.local.LocalTransport;


public class SOALocalTransportRequest implements ISOATransportRequest {

	private ClientMessageContext ctx = null;
	private TransportOptions options = null;

	private SOALocalTransportRequest(ClientMessageContext sctx,
			TransportOptions soptions) {
		this.ctx = sctx;
		this.options = soptions;
	}

	public static ISOATransportRequest createRequest(ClientMessageContext sctx,
			TransportOptions soptions) {
		return new SOALocalTransportRequest(sctx, soptions); 
	}

	public Cookie[] retrieveCookies() throws ServiceException {
		return cloneCookies(((OutboundMessage) ctx.getRequestMessage())
				.getCookies());
	}

	public Map<String, String> getHeaderNames() throws ServiceException {
		return ((OutboundMessage) ctx.getRequestMessage()).buildOutputHeaders();

	}
	
	public String getProtocol() {
		return SOAConstants.TRANSPORT_LOCAL;
	}

	public String getMethod() {
		return "";
	}
	
	public TransportOptions getOptions(){
		return this.options;
	}


	public InputStream getInputStream() throws IOException {
		return null;
	}

	public String getLocalAddr() {
		return null;
	}
	

	public String getQueryString() {

		return null;
	}

	public String getRemoteAddr() {

		return null;
	}

	public String getRemoteHost() {

		return null;
	}

	public String getRequestURI() throws ServiceException {
		String path = null;
		URL serviceLocation = getServiceAddress().getServiceUrl();
		if (serviceLocation != null) {
			path = serviceLocation.getPath();
		}
		return path;

	}

	public String getServerName() {
		return "(local binding)";
	}

	public int getServerPort() {
		return 0;
	}

	public ServiceAddress getClientAddress() throws ServiceException {
		ServiceAddress clientAddress = new ServiceAddress(null);
		return clientAddress;
	}

	public ServiceAddress getServiceAddress() throws ServiceException {
		ServiceAddress serviceAddress = ctx.getServiceAddress();
		serviceAddress = new ServiceAddress(serviceAddress.getServiceUrl());
		return serviceAddress;
	}

	private Cookie[] cloneCookies(Cookie[] cookies) {
		if (cookies == null) {
			return null;
		}

		Cookie[] result = new Cookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			Cookie oldCookie = cookies[i];
			Cookie newCookie = new Cookie(oldCookie.getName(), oldCookie
					.getValue());
			result[i] = newCookie;
		}

		return result;
	}

	public Object getUnderlyingObject() {
		return ctx;
	}

	@Override
	public String getServletPath() {		
		// remove the ending '/*'
		String servletPath = options.getProperty(LocalTransport.REQUEST_URI);
		int index = -1;
		if(servletPath != null && (index = servletPath.indexOf("/*")) != -1 ) {
			servletPath = servletPath.substring(0, index);
		}
		return servletPath;
	}

}
