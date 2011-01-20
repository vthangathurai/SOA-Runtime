/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.json.JAXBJSONDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.json.JAXBJSONSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorClassificationCodeType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorParameterType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.SeverityCodeType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.Address;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Before;
import org.junit.Test;


public class JAXBJSONStreamSerDeserTest extends BaseSerDeserTest {
	public JAXBJSONStreamSerDeserTest() {
		super();
	}

	@Before
	public void setUpFactoriesAndConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		m_serFactory = new JAXBJSONSerializerFactory();
		m_deserFactory = new JAXBJSONDeserializerFactory();
		super.setUp();
	}
	@Test
	public void jsonStreamBasic() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(2);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_JSON, SOAConstants.MIME_JSON, m_serFactory,
			m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out.toByteArray());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage) ctx
				.getRequestMessage(), msg.getClass());
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg, msg1);
	}

	@Test
	public void jsonSimpleType() throws Exception {
		String msg = "This is to test with simple message string";
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_JSON, BindingConstants.PAYLOAD_JSON, m_serFactory, m_deserFactory, null, null,null,null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		jaxbtest.setOpName("echoString");
		
		jaxbtest.clearPayload();
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out.toByteArray());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), String.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}

	//TODO: add test case for ErrorMessage

	@Test
	public void jsonStreamWithNullArgument() throws Exception {
		MyMessage msg = null;
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_JSON, SOAConstants.MIME_JSON, m_serFactory,
				m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out.toByteArray());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage) ctx
				.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(null, msg1);
	}


	@Test
	public void jsonEmptyArray() throws Exception {
		MyMessage msg = new MyMessage();
		ArrayList<ErrorType> errors = new ArrayList<ErrorType>(1);
		ErrorType error = new ErrorType();
		error.setErrorCode("10");
		error.setSeverityCode(SeverityCodeType.ERROR);
		ErrorParameterType errParam = new ErrorParameterType();
		ArrayList<ErrorParameterType> errParams = new ArrayList<ErrorParameterType>();
		errParam.setParamID("1000");
		errParam.setValue("itemId=10000");
		errParams.add(errParam);
		errParam = new ErrorParameterType();
		errParam.setParamID("itemID");
		errParam.setValue("027493");
		errParams.add(errParam);
		errParam = new ErrorParameterType();
		errParam.setValue("no attribute 1");
		errParams.add(errParam);
		errParam = new ErrorParameterType();
		errParam.setValue("no attribute 2");
		errParams.add(errParam);

		error.setErrorParameters(errParams);
		error.setErrorClassification(ErrorClassificationCodeType.SYSTEM_ERROR);
		error.setShortMessage("Testing error");
		error.setLongMessage("Testing error message");
		errors.add(error);
		msg.setError(errors);
		msg.setRecipients(new HashMap<String,Address>());
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_JSON, SOAConstants.MIME_JSON, m_serFactory,
			m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out);
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		MyMessage msg1 = (MyMessage)deser.deserialize((InboundMessage) ctx
				.getRequestMessage(), msg.getClass());
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg, msg1);
		ErrorType msgError = msg.getError().get(0);
		ErrorType msg1Error = msg1.getError().get(0);
		assertEquals(msgError.getErrorCode(), msg1Error.getErrorCode());
		assertEquals(msgError.getLongMessage(), msg1Error.getLongMessage());
	}

}
