/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.version;

import java.util.Map;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;



/**
 * 
 */
public abstract class BaseVersionTest extends BaseCallTest {
	public BaseVersionTest() throws Exception {
		super();
	}
	
	public BaseVersionTest(String configRoot)
			throws Exception {
		super(configRoot);
	}

	protected class Verifier implements Test1Driver.SuccessVerifier {
		private final String m_expectedVersion;

		Verifier(String expectedVersion) {
			m_expectedVersion = expectedVersion;
		}

		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception {
			ResponseContext ctx = service.getResponseContext();

			String version = ctx.getTransportHeader(SOAHeaders.VERSION);
			Assert.assertTrue(version != null
					&& version.equalsIgnoreCase(m_expectedVersion));
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResponse, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception {
			Map ctx = null;
			if (mode.equals(TestMode.ASYNC_SYNC))
				ctx = dispatch.getResponseContext();
			else
				ctx = futureResponse.getContext();
			String version = (String) ctx.get(SOAHeaders.VERSION);
			Assert.assertTrue(version != null
					&& version.equalsIgnoreCase(m_expectedVersion));
		}
	}
}
