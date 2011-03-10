/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import static org.hamcrest.Matchers.greaterThan;

import java.net.URI;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.junit.logging.UKernelLoggingUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.servlet.TurmericConsoleFrontController;
import org.ebayopensource.turmeric.runtime.tests.common.logging.SimpleConsoleHandler;
import org.junit.Assert;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;


/**
 * Embedded Jetty Server suitable for unit testing with.
 */
public class SimpleJettyServer {
	private Server server;
	private URI serverURI;
	private boolean accessLog = true;

	public SimpleJettyServer() {
		/*
		 * Run the server with a system assigned port number. The system will find the first available port and use it.
		 */
		this(0);
	}
	
	public SimpleJettyServer(int port) {
		this(port, null, null);
	}
	
	public SimpleJettyServer(int port, String servletName, String serviceAdminName) {
		setup(port, servletName, serviceAdminName);
	}

	private void setup(int port, String servletName, String serviceAdminName) {
		JavaUtilLog.init("SimpleJettyServer");
		server = new Server(port);

		// Create contexts handler for holding servlets.
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		// Create root context (with sessions) for servlets
		Context root = new Context(contexts, "/", Context.SESSIONS);

		// for http://localhost:{port}/ws/spf, etc.
		ServletHolder sh = new ServletHolder(org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet.class);
		// sh.setInitParameter(SOAConstants.SERVLET_PARAM_LOGGER_INIT, "false");
		if (serviceAdminName != null) {
			sh.setInitParameter(SOAConstants.SERVLET_PARAM_ADMIN_NAME, serviceAdminName);
		}
		sh.setInitParameter(SOAConstants.SERVLET_PARAM_LOGGER_RESOURCE_NAME, com.ebay.kernel.logger.Logger.CONSOLE_LOGGING_NAME);
		root.addServlet(sh, servletName == null ? "/ws/spf/*" : "/Turmeric/" + servletName + "/*");
		
		ServletHolder sh1 = new ServletHolder(org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet.class);
		sh1.setInitParameter(SOAConstants.SERVLET_PARAM_LOGGER_RESOURCE_NAME, com.ebay.kernel.logger.Logger.CONSOLE_LOGGING_NAME);
		sh1.setInitParameter(SOAConstants.SERVLET_PARAM_ADMIN_NAME, "AdvertisingUniqueIDServiceV1");
		root.addServlet(sh1, "/services/advertise/UniqueIDService/v1/*");
		
		ServletHolder sh2 = new ServletHolder(org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet.class);
		sh2.setInitParameter(SOAConstants.SERVLET_PARAM_LOGGER_RESOURCE_NAME, com.ebay.kernel.logger.Logger.CONSOLE_LOGGING_NAME);
		sh2.setInitParameter(SOAConstants.SERVLET_PARAM_ADMIN_NAME, "SoaTestServiceV1");
		root.addServlet(sh2, "/soa/services/v1/*");
		// for http://localhost:{port}/Turmeric/Console
//		ServletHolder controller = new ServletHolder(
//				TurmericConsoleFrontController.class);
//		root.addServlet(controller, "/Turmeric/Console/*");
		
		server.setHandler(root);
	}
	
	public void wrapHandlers(HandlerWrapper wrapper) {
		Log.info("Wrapping root handler with " + wrapper);
		Handler originalHandler = server.getHandler();
		Assert.assertNotNull("There is apparently no original handler on "
				+ "the server!?  Unable to wrap a null handler.",
				originalHandler);
		wrapper.setHandler(originalHandler);
		server.setHandler(wrapper);
	}

	public URI getServerURI() {
		return serverURI;
	}

	public URI getSPFURI() {
		return serverURI.resolve("/ws/spf/");
	}
	

	public void start() throws Exception {
		wrapHandlers(new DebugHandler());
		
		if(accessLog) {
			wrapHandlers(new AccessLoggingHandler());
		}
		
		server.start();

		// Get Server URI
		int port = 0;
		for (Connector connector : server.getConnectors()) {
			port = connector.getLocalPort();
			Assert.assertThat(
					"Server startup failure: listen port not assigned."
							+ "Your system is likely *very* busy right now.",
					port, greaterThan(0));
			serverURI = new URI("http://localhost:" + port);
		}
		
		Log.info("Server started - " + serverURI.toASCIIString());

		if (serverURI == null) {
			// Critical Failure. (highly unlikely!)
			throw new IllegalStateException(
					"Unable to figure out the serverURI.  Local port not defined!?");
		}
	}

	public void stop() throws Exception {
		if (server != null) {
			Log.info("Server stopped - " + serverURI.toASCIIString());
			server.stop();
		}
	}

	/**
	 * Will execute a {@link Server#join()} to wait until the server is done executing.
	 * <p>
	 * (this method only returns once the server is shut down).
	 * 
	 * @throws InterruptedException
	 */
	public void joinServer() throws InterruptedException {
		server.join();
	}
	
	public static void main(String[] args) {
		SimpleConsoleHandler.init();
		
		UKernelLoggingUtils.initTesting();
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			new SimpleConsoleHandler.Init();
			Logger cllogger = Logger.getLogger("jetty.classloader");
			Thread.currentThread().setContextClassLoader(new LoggingClassLoader(cllogger, original));
			SimpleJettyServer jetty = new SimpleJettyServer(8080);
			jetty.start();
			jetty.joinServer();
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}
}
