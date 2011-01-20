/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import java.net.URI;
import java.util.Map;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.common.sif.UrlPathTest.Verifier;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;



/**
 * @author ichernyshev
 */
public class RemoteCallHTTP10Test extends BaseCallTest {
	public RemoteCallHTTP10Test() throws Exception {
		super("configremote");
	}


	@Override
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver("test1",
				"configremote", BaseCallTest.CONFIG_ROOT, jetty.getSPFURI().resolve("?myTestOperation&test1param=abc&test2param=def").toURL());
		driver.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		driver.setVerifier(new Verifier());
		return driver;
	}
	
	
	
	
	protected class Verifier implements Test1Driver.SuccessVerifier  {
		public void checkSuccess(Service service, String opName, MyMessage request,
			MyMessage response, byte[] payloadData) throws Exception
		{
			service.getResponseContext().getTransportHeader("RETURN_SOA_SERVER_NAME");
			String serverName = service.getResponseContext().getTransportHeader("RETURN_SOA_SERVER_NAME");
			String serverPort = service.getResponseContext().getTransportHeader("RETURN_SOA_SERVER_PORT");
			String queryParams = service.getResponseContext().getTransportHeader("RETURN_QUERY_PARAMS");
			
			URI serverURI = jetty.getServerURI();
			
			Assert.assertEquals("Server Name", serverURI.getHost(), serverName);
			Assert.assertEquals("Server Port", String.valueOf(serverURI.getPort()), serverPort);
			Assert.assertEquals("test1param=abc,test2param=def", queryParams);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void checkSuccess(Service service, Dispatch dispatch, Response futureResponse, MyMessage request,
				MyMessage response, byte[] payloadData, TestMode mode) throws Exception
		{
			String serverName = "";
			String serverPort = "";
			String queryParams = "";
			Map<String,Object> context;
			
			if(mode.equals(TestMode.ASYNC_SYNC))
			{
				context = dispatch.getResponseContext();
			}
			else
			{
				context = futureResponse.getContext();
			}
			serverName = (String) context.get("RETURN_SOA_SERVER_NAME");
			serverPort = (String) context.get("RETURN_SOA_SERVER_PORT");
			queryParams = (String) context.get("RETURN_QUERY_PARAMS");
			
			URI serverURI = jetty.getServerURI();
			
			Assert.assertEquals("Server Name", serverURI.getHost(), serverName);
			Assert.assertEquals("Server Port", String.valueOf(serverURI.getPort()), serverPort);
			Assert.assertEquals("test1param=abc,test2param=def", queryParams);
		}
	}
}
