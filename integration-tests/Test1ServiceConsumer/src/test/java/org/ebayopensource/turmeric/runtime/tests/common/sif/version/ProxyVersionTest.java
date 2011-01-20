/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.version;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.service.CommonServiceOperations;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.junit.Test;


public class ProxyVersionTest {

	@Test
	public void getVersion() throws Exception {
		//due to change in ServiceFactory
		URL url = new URL("http://www.ebay.com/");
		Service test1 = ServiceFactory.create(
				"test1", "local", url, null);
		test1.getProxy();
		ServiceInvokerOptions options = test1.getInvokerOptions();
		options.setTransportName(SOAConstants.TRANSPORT_LOCAL);
		CommonServiceOperations proxy = test1.getProxy();
		String version = proxy.getServiceVersion();
		assertEquals("1.0.0", version);
		boolean isSupported = proxy.isServiceVersionSupported(version);
		assertEquals(true, isSupported);
	}

}
