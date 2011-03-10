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
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;


public interface ISOATransportRequest {

	String getProtocol();

	Cookie[] retrieveCookies()throws ServiceException;

	String getServerName();
	
	String getMethod();

	int getServerPort();

	String getLocalAddr();

	String getRemoteAddr();

	String getRemoteHost();
	
	String getRequestURI() throws ServiceException;
	
	String getQueryString();

	InputStream getInputStream() throws IOException;
	
	public ServiceAddress getServiceAddress() throws ServiceException;
	
	public ServiceAddress getClientAddress() throws ServiceException;
	
	Map <String,String> getHeaderNames( ) throws ServiceException;
	
	Object getUnderlyingObject() ;
	
	String getServletPath();

}
