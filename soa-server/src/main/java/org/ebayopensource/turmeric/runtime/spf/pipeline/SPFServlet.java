/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import static org.ebayopensource.turmeric.runtime.common.types.SOAConstants.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.InitializerConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerMessageContextBuilder;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServerUtils;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServletResponseTransport;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.ISOATransportRequest;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.SOAServerTransportRequest;

import com.ebay.kernel.configuration.ConfigurationContext;
import com.ebay.kernel.logger.LoggerInitHelper;
import com.ebay.kernel.memtrace.MemTrace;

/**
 * This is the entry point for all services in the synchronous world.
 *
 * This servlet provides a minimal amount of "glue" to the server-side framework.  A request
 * message is constructed using the input stream, and a response message and containing
 * MessageContext are also constructed.  This information is submitted to the
 * ServerMessageProcessor which manages the interface to the message protocol processor as well
 * as all pipeline operation.
 *
 * The standard servlet methods for both HTTP GET and HTTP POST handling are implemented.
 *
 * The servlet initializer reads an optional parameter from the servlet's web.xml entry.
 * This can be used to narrow the service support to one specific service (administrative name).
 *
 * @author smalladi, ichernyshev
 */
public class SPFServlet extends HttpServlet {	

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 835486766282112209L;
	private String m_serviceAdminName;
	private String m_urlMatchExpression;
	
	private static final Logger LOGGER = LogManager.getInstance(SPFServlet.class);

	/*
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)	 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/*
	 * Called by the server (via the service method) to allow a servlet to handle a POST request.
	 *  
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cmdName = "SOA_Unknown";
		MemTrace.getInstance().startCommandTracing();
		try {
			Transport responseTransport = new HTTPServletResponseTransport(req, resp);
			ISOATransportRequest soaRequest = SOAServerTransportRequest.createRequest(req);  
			HTTPServerUtils serverUtils = new HTTPServerUtils(soaRequest, m_serviceAdminName, m_urlMatchExpression);
			RequestMetaContext reqMetaCtx = serverUtils.getReqMetaCtx();
			PseudoOperation pseudoOp = PseudoOperationHelper.getPseudoOp(reqMetaCtx);
			if (pseudoOp != null) {
				cmdName = "SOA_Pseudo_" + pseudoOp.getClass().getSimpleName();
				ResponseMetaContext respMetaCtx = new ResponseMetaContext(resp.getOutputStream());
				// TODO: avoid exposing internal's ServerServiceDesc here
				ServerServiceDesc serviceDesc = serverUtils.getServiceResolver().lookupServiceDesc();
				pseudoOp.preinvoke(serviceDesc, reqMetaCtx, respMetaCtx);
				resp.setContentType(respMetaCtx.getContentType());
				pseudoOp.invoke(serviceDesc, reqMetaCtx, respMetaCtx);
				return;
			}
			ServerMessageContextBuilder builder = serverUtils.createMessageContext(responseTransport);
			cmdName = new StringBuilder().append("SOA_").append(builder.getServiceId().getAdminName()).
							append(".").append(builder.getOperationDesc().getName()).toString();
			builder.processCall();
		} catch (Throwable e) {
			LogManager.getInstance(SPFServlet.class).
				log(Level.SEVERE,"Unexpected error in SPFServlet: " + e.toString(), e);
			throw new ServletException(e);
		} finally {
			MemTrace.getInstance().stopCommandTracing(cmdName);
		}
	}

	/*
	 * A convenience method which can be overridden.
	 * so that there's no need to call <code>super.init(config)</code>
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			initInternal(config);
		} catch (ServletException e) {
			reportFailure(e);
			throw e;
		} catch (RuntimeException e) {
			reportFailure(e);
			throw e;
		} catch (Error e) {
			reportFailure(e);
			throw e;
		}
	}

	private void reportFailure(Throwable error) throws ServletException {
		try {
			ConfigurationContext configContext = ConfigurationContext.getInstance();
			if (!configContext.getInitSuccess() || configContext.getInitErrMsg() != null) {
				// already have failure, don't add more
				return;
			}
			configContext.setInitSuccess(false);
			configContext.setInitErrMsg("SOA initialization failure: " + error.toString());
		} catch (Exception e) {
			e.printStackTrace(); // KEEPME
		}
	}

	private void initInternal(ServletConfig config) throws ServletException {
		System.out.println("Initializing SPFServlet"); //KEEPME
				String logInit = config.getInitParameter(SERVLET_PARAM_LOGGER_INIT);
		// Only initialize logging if needed.
		if((logInit == null) || ("true".equalsIgnoreCase(logInit))) {
			String loggerConfigName = config.getInitParameter(SERVLET_PARAM_LOGGER_RESOURCE_NAME);
			if(loggerConfigName == null) {
				// Initialize MicroKernel logger using defaults.
				LoggerInitHelper.initLogger();
			} else {
				// Initialize MicroKernel logger defined configuration.
				com.ebay.kernel.logger.Logger.initLogProperties(loggerConfigName);
			}
		}
		try {
			ServerMessageContextBuilder.init();
		} catch (ServiceException e) {
			throw new ServletException(e);
		}
		// Servlet param name will be changed to SOA_ADMIN_NAME in 2.4
		m_serviceAdminName = config.getInitParameter(SERVLET_PARAM_ADMIN_NAME);
		if(m_serviceAdminName == null) {
			m_serviceAdminName = config.getInitParameter(SERVLET_PARAM_SERVICE_NAME);
		}
		if (m_serviceAdminName != null) {
			try {
				ServerMessageContextBuilder.validateServiceName(m_serviceAdminName);
			} catch (ServiceException e) {
				throw new ServletException("Unable to load servlet for " + m_serviceAdminName, e);
			}
			callInitializers(config);
		}
		getUrlMappingExpression(config);
		System.out.println("Initializing SPFServlet - DONE"); //KEEPME
	}

	private void callInitializers(ServletConfig config) throws ServletException {
		String noInitializer = config.getInitParameter(NO_INITIALIZER_PARAM_NAME);
		if (Boolean.valueOf(noInitializer).booleanValue()) {
			final String logMsg = "Initializers blocked for " + m_serviceAdminName + " service's servlet";
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine(logMsg);
			}
			log(logMsg);
			return;
		}
		try {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Calling initializers for service " + m_serviceAdminName);
			}
			InitializerConfigManager.getInstance().callInitializers(m_serviceAdminName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error calling initializers for service " + m_serviceAdminName, e);
		}
	}

	private void getUrlMappingExpression(ServletConfig config) throws ServletException {
		String urlMatch = config.getInitParameter(SERVLET_PARAM_URL_MATCH_EXPRESSION);
		if (urlMatch != null) {
			if (m_serviceAdminName != null) {
				StringBuilder error = new StringBuilder();
				error.append("Unable to load servlet for ").append(m_serviceAdminName);
				error.append(". Init param conflict: Expecting either ");
				error.append(SERVLET_PARAM_SERVICE_NAME).append(" / ").append(SERVLET_PARAM_ADMIN_NAME);
				error.append(" or ").append(SERVLET_PARAM_URL_MATCH_EXPRESSION);
				throw new ServletException(error.toString());
			}
			boolean invalidInput = false;
			if (urlMatch.startsWith("query[")) {
				if (!urlMatch.endsWith("]") || urlMatch.length() < 8) {
					invalidInput = true;
				}
			} else if (urlMatch.equals("queryop")) {
				//
			} else if (urlMatch.startsWith("path[")) {
				if (!urlMatch.endsWith("]") || urlMatch.length() < 7) {
					invalidInput = true;
				}
				String indexval = urlMatch.substring(5, urlMatch.length()-1);
				try {
					Integer.valueOf(indexval);
				} catch (NumberFormatException e) {
					invalidInput = true;
				}
			} else {
				invalidInput = true;
			}
			if (invalidInput) {
				StringBuilder error = new StringBuilder();
				error.append("Unable to load servlet for ").append(m_serviceAdminName);
				error.append(". I. Init param format error: ");				
				error.append(SERVLET_PARAM_URL_MATCH_EXPRESSION).append("=").append(urlMatch);				
				throw new ServletException(error.toString());
			}
			m_urlMatchExpression = urlMatch;
		}
	}	
}
