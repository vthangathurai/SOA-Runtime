package org.ebayopensource.turmeric.jetty;
/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

import java.io.File;
import java.net.URI;

import junit.framework.Assert;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.servlet.TurmericConsoleFrontController;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;


/**
 * Embedded Jetty Server suitable for unit testing with.
 */
public class DemoAppServer {
	private Server server;
	private URI serverURI;
	private boolean accessLog = true;

	public DemoAppServer() {
		/*
		 * Run the server with a system assigned port number. The system will find the first available port and use it.
		 */
		this(0);
	}

	public DemoAppServer(int port) {
		this(port, null, null);
	}

	public DemoAppServer(int port, String servletName, String serviceAdminName) {
		setup(port, servletName, serviceAdminName);
	}

	private void setup(int port, String servletName, String serviceAdminName) {
		System.out.println("Starting SimpleJettyServer ...");
		server = new Server(port);

		// Create contexts handler for holding servlets.
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		// Create root context (with sessions) for servlets
		Context root = new Context(contexts, "/DemoApp", Context.SESSIONS);
		root.setResourceBase(new File("./src/main").getAbsolutePath());

		ServletHolder sh = new ServletHolder(org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet.class);
		// sh.setInitParameter(SOAConstants.SERVLET_PARAM_LOGGER_INIT, "false");
		if (serviceAdminName != null) {
			sh.setInitParameter(SOAConstants.SERVLET_PARAM_ADMIN_NAME, serviceAdminName);
		}
		sh.setInitParameter(SOAConstants.SERVLET_PARAM_LOGGER_RESOURCE_NAME, com.ebay.kernel.logger.Logger.CONSOLE_LOGGING_NAME);
		root.addServlet(sh, servletName == null ? "/spf/*" : "/" + servletName + "/*");


		// for http://localhost:{port}/Turmeric/Console
		ServletHolder controller = new ServletHolder(
				TurmericConsoleFrontController.class);
		root.addServlet(controller, "/Console/*");


		// for static content
		ServletHolder staticContent = new ServletHolder(
				DefaultServlet.class);
		staticContent.setInitParameter("relativeResourceBase", "./webapp");
		root.addServlet(staticContent, "/");

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
		return serverURI.resolve("/spf/");
	}

	public void start() throws Exception {

		server.start();

		// Get Server URI
		int port = 0;
		for (Connector connector : server.getConnectors()) {
			port = connector.getLocalPort();
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

	public static void main(String[] args) throws Exception {
		DemoAppServer jetty = new DemoAppServer(8080, "ItemSearch", "ItemSearchServiceV1");
		jetty.start();
		jetty.joinServer(); // let it run until exited
	}
}
