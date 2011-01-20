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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.ebayopensource.turmeric.junit.rules.TestingDir;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;

public abstract class AbstractWithSlowProxyServerTest extends AbstractTurmericTestCase {
	protected static SimpleJettyServer jetty;
	protected static SimpleJettyProxyServer proxyServer;
	protected static URI serverUri;
	protected static URI proxyUri;
	
	@Rule public TestingDir testingdir = new TestingDir();

	@BeforeClass
	public static void startServers() throws Exception {
		// Setup Real Server.
		jetty = new SimpleJettyServer();
		jetty.start();
		serverUri = jetty.getSPFURI();

		// Setup Transparent Proxy Server to Real Server.
		proxyServer = new SimpleJettyProxyServer(jetty.getServerURI());
		IntentionalDelayHandler slowserver = new IntentionalDelayHandler();
		slowserver.setDelay(3000);
		proxyServer.wrapHandlers(slowserver);
		proxyServer.start();
		proxyUri = proxyServer.getServerURI();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		jetty.stop();
		proxyServer.stop();
	}

	private Document parseXml(URL url) throws IOException, JDOMException {
		InputStream in = null;
		try {
			in = url.openStream();
			SAXBuilder builder = new SAXBuilder(false);
			return builder.build(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private void writeXml(File file, Document doc) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			XMLOutputter serializer = new XMLOutputter();
			serializer.getFormat().setIndent("  ");
			serializer.getFormat().setLineSeparator(SystemUtils.LINE_SEPARATOR);
			serializer.output(doc, writer);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Create a proxied service that uses the embedded jetty proxy server + embedded jetty SPF server.
	 * <p>
	 * The process here is:
	 * <p>
	 * <ol>
	 * <li>Read the raw ClientConfig.xml as specified by the clientName parameter</li>
	 * <li>Updates the <code>PROXY_HOST</code> and <code>PROXY_PORT</code> values present in the raw ClientConfig.xml</li>
	 * <li>Write the modified ClientConfig.xml back out to a test specific directory (
	 * <code>target/tests/{testClassName}/{testMethodName}/res/</code>) using the path
	 * <code>META-INF/soa/client/config/{clientName}_modified/ClientConfig.xml</code></li>
	 * <li>Using a test local classloader, and the {@link ServiceFactory#create(String, String, URL)} method to load the
	 * modified ClientConfig.xml to create a {@link Service} suitable for talking to the embedded jetty proxy server.</li>
	 * </ol>
	 * 
	 * @param serviceAdminName
	 *            the service admin name.
	 * @param clientName
	 *            the client name.
	 * @return
	 */
	protected Service createProxiedService(String serviceAdminName,
			String clientName) throws Exception {
		// Read/Parse baseline ClientConfig.xml

		String rawConfigPath = String.format(
				"META-INF/soa/client/config/%s/ClientConfig.xml", clientName);
		URL rawConfigUrl = ClassLoader.getSystemResource(rawConfigPath);
		Assert.assertThat("Unable to find config resource: " + rawConfigPath,
				rawConfigUrl, notNullValue());

		Document doc = parseXml(rawConfigUrl);

		// Modify the PROXY_HOST and PROXY_PORT to point to embedded jetty server

		XPath expression = new JDOMXPath(
				"//e:default-options/e:other-options/e:option");
		expression.addNamespace("e",
				"http://www.ebayopensource.org/turmeric/common/config");
		// Navigator navigator = expression.getNavigator();

		@SuppressWarnings("unchecked")
		List<Element> nodes = expression.selectNodes(doc);
		for (Element elem : nodes) {
			String optName = elem.getAttributeValue("name").trim();
			if ("PROXY_HOST".equals(optName)) {
				elem.setText(proxyUri.getHost());
			} else if ("PROXY_PORT".equals(optName)) {
				elem.setText(String.valueOf(proxyUri.getPort()));
			}
		}

		// Write modified document out to target directory

		File testingResourceDir = new File(testingdir.getDir(), "res");
		MavenTestingUtils.ensureEmpty(testingResourceDir);
		String modifiedConfigPath = String.format(
				"META-INF/soa/client/config/%s_modified/ClientConfig.xml",
				clientName);
		File outputFile = new File(testingResourceDir, modifiedConfigPath);
		MavenTestingUtils.ensureDirExists(outputFile.getParentFile());

		System.out.println("Writing modified ClientConfig to "
				+ outputFile.getAbsolutePath());
		writeXml(outputFile, doc);

		// Let ServiceFactory create service from modified ClientConfig.xml
		URL urls[] = new URL[] { testingResourceDir.toURI().toURL() };
		URLClassLoader testingSpecificCL = new URLClassLoader(urls, this
				.getClass().getClassLoader());

		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(testingSpecificCL);
			return ServiceFactory.create(serviceAdminName, clientName
					+ "_modified", null);
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}
}
