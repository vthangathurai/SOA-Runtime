/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.services.repositoryservice.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class MainRunnerTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	@Test
	public void testRuntime() throws Exception {
		Server server;
		HttpClient client;
		String uri;

		
		server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8081);
		server.addConnector(connector);

		String contextPath = "";
		ServletContextHandler context = new ServletContextHandler(server,
				contextPath, ServletContextHandler.SESSIONS);
		String servletPath = "/ws/spf";
		ServletHolder servletHolder = new ServletHolder(SPFServlet.class);
		servletHolder.setInitOrder(1);
		/* According to this: http://ph-0148.eva.ebay.com/wiki/display/SOADOC/SPF+Servlet, the service name
		 * can be configured as a servlet init parameter
		 */
//		servletHolder.setInitParameter("SOA_SERVICE_NAME", "RepositoryService");
		context.addServlet(servletHolder, servletPath + "/*");

		server.start();
		uri = "http://localhost:" + connector.getLocalPort() + contextPath
				+ servletPath;

		System.err.println(uri);
		String filePath = new File("client-truststore.jks").getAbsolutePath();
		System.err.println(filePath);
		System.setProperty("javax.net.ssl.trustStore", filePath);
System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
System.setProperty("javax.net.ssl.trustStoreType", "JKS");

System.setProperty("org.ebayopensource.turmeric.repository.wso2.url",
		"https://127.0.0.1:9443/registry/");
System.setProperty(
		"org.ebayopensource.turmeric.repository.wso2.username", "admin");
System.setProperty(
		"org.ebayopensource.turmeric.repository.wso2.password", "admin");

System.err.println("javax.net.ssl.trustStore path="+System.getProperty("javax.net.ssl.trustStore"));

	}

}
