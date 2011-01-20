/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import static org.hamcrest.Matchers.*;

import java.net.URI;

import org.ebayopensource.turmeric.junit.logging.UKernelLoggingUtils;
import org.ebayopensource.turmeric.runtime.tests.common.logging.SimpleConsoleHandler;
import org.junit.Assert;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;


/**
 * Embedded Jetty Proxy Server suitable for unit testing with.
 */
public class SimpleJettyProxyServer {
	private Server server;
	private URI serverURI;
	private URI destURI;
	private boolean accessLog = true;

	public SimpleJettyProxyServer(URI destURI) {
		/*
		 * Run the server with a system assigned port number. The system will find the first available port and use it.
		 */
		this(destURI, 0);
	}

	public SimpleJettyProxyServer(URI destURI, int port) {
		SimpleConsoleHandler.init();
		JavaUtilLog.init("SimpleJettyProxyServer");
		UKernelLoggingUtils.initTesting();
		
		this.destURI = destURI;
		server = new Server(port);
		
		// Create contexts handler for holding servlets.
		ContextHandlerCollection contexts = new ContextHandlerCollection();

		// Create root context (with sessions) for servlets
		Context root = new Context(contexts, "/", Context.SESSIONS);

		// Proxy requests incoming to this proxy, out to designated destURI.
		ServletHolder sh = new ServletHolder(org.mortbay.servlet.ProxyServlet.Transparent.class);
		sh.setInitParameter("ProxyTo", destURI.toASCIIString());
		root.addServlet(sh, "/*");
		
		server.setHandler(root);
	}

	public void wrapHandlers(HandlerWrapper wrapper) {
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
	
	public URI getDestURI() {
		return destURI;
	}

	public void start() throws Exception {
		UKernelLoggingUtils.initTesting();
		
		if(accessLog) {
			AccessLoggingHandler access = new AccessLoggingHandler();
			wrapHandlers(access);
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

		if (serverURI == null) {
			// Critical Failure. (highly unlikely!)
			throw new IllegalStateException(
					"Unable to figure out the serverURI.  Local port not defined!?");
		}
	}

	public void stop() throws Exception {
		if (server != null) {
			server.stop();
		}
	}
}
