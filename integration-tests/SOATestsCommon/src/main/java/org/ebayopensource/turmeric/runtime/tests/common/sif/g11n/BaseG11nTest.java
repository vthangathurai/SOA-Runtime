/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.g11n;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;



public abstract class BaseG11nTest extends BaseCallTest {
	static Map<String, String> s_contentMap = new HashMap<String,String>();
	static {
		s_contentMap.put(BindingConstants.PAYLOAD_JSON, "application/json"); // http://www.ietf.org/rfc/rfc4627.txt
		s_contentMap.put(BindingConstants.PAYLOAD_NV, "text/plain");
		s_contentMap.put(BindingConstants.PAYLOAD_XML, "text/xml"); // http://www.rfc-editor.org/rfc/rfc3023.txt
		s_contentMap.put(BindingConstants.PAYLOAD_FAST_INFOSET, "application/fastinfoset");	// http://www.iana.org/assignments/media-types/application/fastinfoset
	}

	public BaseG11nTest() throws Exception {
		super(null);
	}

	public BaseG11nTest(String configRoot) throws Exception {
		super(configRoot);
	}

	protected class Verifier implements Test1Driver.SuccessVerifier {
		private final String m_encoding;
		private final String m_localeList;
		private final String m_globalID;
		Verifier(String encoding, String localeList, String globalID) {
			m_encoding = encoding;
			m_localeList = localeList;
			m_globalID = globalID;
		}
		public void checkSuccess(Service service, String opName, MyMessage request,
			MyMessage response, byte[] payloadData) throws Exception
		{
			ResponseContext ctx = service.getResponseContext();

			String localeList = ctx.getTransportHeader(SOAHeaders.LOCALE_LIST);
			Assert.assertTrue(localeList != null && localeList.equalsIgnoreCase(m_localeList));
			String globalId = ctx.getTransportHeader(SOAHeaders.GLOBAL_ID);
			Assert.assertEquals(m_globalID, globalId);
			String messageEncoding = ctx.getTransportHeader(SOAHeaders.MESSAGE_ENCODING);
			Assert.assertEquals(m_encoding, messageEncoding);
			//String dataFormat = ctx.getTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT);
			//String mimeType = s_contentMap.get(dataFormat);
			//String expectedContentType = mimeType + ";" + m_encoding;
			//String contentType = ctx.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);
//			Assert.assertEquals(expectedContentType, contentType);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void checkSuccess(Service service, Dispatch dispatch, Response futureResponse, MyMessage request,
				MyMessage response, byte[] payloadData, TestMode mode) throws Exception
		{
			Map<String, Object> responseCtx = null;
			
			if(mode.equals(TestMode.ASYNC_SYNC))
				responseCtx = dispatch.getResponseContext();
			else 
				responseCtx = futureResponse.getContext();

			String localeList = (String) responseCtx.get(SOAHeaders.LOCALE_LIST);
			Assert.assertTrue(localeList != null && localeList.equalsIgnoreCase(m_localeList));
			String globalId = (String) responseCtx.get(SOAHeaders.GLOBAL_ID);
			Assert.assertEquals(m_globalID, globalId);
			String messageEncoding = (String) responseCtx.get(SOAHeaders.MESSAGE_ENCODING);
			Assert.assertEquals(m_encoding, messageEncoding);
			//String dataFormat = ctx.getTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT);
			//String mimeType = s_contentMap.get(dataFormat);
			//String expectedContentType = mimeType + ";" + m_encoding;
			//String contentType = ctx.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);
//				Assert.assertEquals(expectedContentType, contentType);
		}
	}
}
