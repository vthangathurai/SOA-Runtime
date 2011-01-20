/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * For testing against {@link SPFServlet} with Embedded Jetty Server.
 */
public abstract class AbstractWithServerTest extends AbstractTurmericTestCase {
	protected static SimpleJettyServer jetty;
	protected static URI serverUri;

	@BeforeClass
	public static void startServer() throws Exception {
		String externalServerPort = System.getProperty("external.jetty.server.port");
		if(StringUtils.isNotBlank(externalServerPort)) {
			int port = NumberUtils.toInt(externalServerPort);
			serverUri = URI.create("http://localhost:" + port + "/ws/spf");
			return;
		}
		
		jetty = new SimpleJettyServer();
		jetty.start();
		serverUri = jetty.getSPFURI();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		if(jetty != null) {
			jetty.stop();
		}
	}
}
