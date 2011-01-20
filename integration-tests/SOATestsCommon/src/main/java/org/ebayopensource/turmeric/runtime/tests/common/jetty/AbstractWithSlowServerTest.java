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

import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractWithSlowServerTest extends AbstractTurmericTestCase {
	protected static SimpleJettyServer jetty;
	protected static URI serverUri;

	@BeforeClass
	public static void startServer() throws Exception {
		jetty = new SimpleJettyServer();
		IntentionalDelayHandler slowserver = new IntentionalDelayHandler();
		slowserver.setDelay(3000);
		jetty.wrapHandlers(slowserver);
		jetty.start();
		serverUri = jetty.getSPFURI();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		jetty.stop();
	}
}
