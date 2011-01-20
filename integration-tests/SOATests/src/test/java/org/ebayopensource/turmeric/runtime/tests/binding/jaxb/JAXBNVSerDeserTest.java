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

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.nv.JAXBNVDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.nv.JAXBNVSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Before;
import org.junit.Test;


public class JAXBNVSerDeserTest extends BaseSerDeserTest {
	public JAXBNVSerDeserTest() {
		super();
	}

	@Before
	public void setUpFactoriesAndConfig() throws Exception {
		setUpConfig();
		m_serFactory = new JAXBNVSerializerFactory();
		m_deserFactory = new JAXBNVDeserializerFactory();
		super.setUp();
	}

	protected void setUpConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
	}

	 @Test
	public void jaxbNVUnordered() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,null,null);
		
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
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}

	 @Test
	public void nVUnorderedSimpleType() throws Exception {
		String msg = "This is to test with simple message string";
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,null,null);

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		jaxbtest.setOpName("echoString");

		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out);
		
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), String.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}

	 @Test
	public void nVOrderedSimpleType() throws Exception {
		String msg = "This is to test with simple message string";
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,null,null);

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		jaxbtest.setOpName("echoString");
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		Deserializer deser = m_deserFactory.getDeserializer();
		
		jaxbtest.setPayload(out);
		jaxbtest.setOrdered(true);
		
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), String.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}

	 @Test
	public void jaxbNVUnorderedWithNullArgument() throws Exception {
		MyMessage msg = null;
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,null,null);
		
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
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(null,msg1);
	}

	 @Test
	public void jaxbNVUnorderedRepeatedAddresses() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(2);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,null,null);

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
		Deserializer deser = m_deserFactory.getDeserializer();
		jaxbtest.setPayload(out);
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}

	 @Test
	public void jaxbNVOrdered() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,
				JAXBTestHelper.createTypeConvertersByBoundTypeMap(), JAXBTestHelper.createTypeConvertersByValueTypeMap());

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
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
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}


	 @Test
	public void jaxbNVOrderedWithNullArgument() throws Exception {
		MyMessage msg = null;
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,
				JAXBTestHelper.createTypeConvertersByBoundTypeMap(), JAXBTestHelper.createTypeConvertersByValueTypeMap());
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
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
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(null,msg1);
	}

	 @Test
	public void jaxbNVOrderedWithRepeatedAddress() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(2);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,
				JAXBTestHelper.createTypeConvertersByBoundTypeMap(), JAXBTestHelper.createTypeConvertersByValueTypeMap());

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
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
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}


	 @Test
	public void jaxbNVOrderedWithImpliedRoot() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,
				JAXBTestHelper.createTypeConvertersByBoundTypeMap(), JAXBTestHelper.createTypeConvertersByValueTypeMap());

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		
		MessageContext ctx = jaxbtest.createTestMessageContext();
		Message inMsg = ctx.getRequestMessage();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out);
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		inMsg = ctx.getRequestMessage();
		inMsg.setTransportHeader(SOAHeaders.NV_IMPLIED_ROOT, Boolean.TRUE.toString());
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}


	 @Test
	public void jaxbNVUnorderedWithImpliedRoot() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV, m_serFactory, m_deserFactory, null, null,null,null);

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ctx.getRequestMessage();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		jaxbtest.setPayload(out);
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		ctx.getRequestMessage();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}
}
