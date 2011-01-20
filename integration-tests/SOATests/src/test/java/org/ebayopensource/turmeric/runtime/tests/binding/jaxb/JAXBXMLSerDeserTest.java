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
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Before;
import org.junit.Test;


public class JAXBXMLSerDeserTest extends BaseSerDeserTest {
	public JAXBXMLSerDeserTest() {
		super();
	}

	@Before
	public void setUpFactoriesAndConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		m_serFactory = new JAXBXMLSerializerFactory();
		m_deserFactory = new JAXBXMLDeserializerFactory();
		super.setUp();
	}

	 @Test
	public void jaxbXmlBasic() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
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
		jaxbtest.setPayload(out);
		ctx = jaxbtest.createTestMessageContext();
		msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(msg,msg1);
	}

	 @Test
	public void jaxbXmlWithNull() throws Exception {
		MyMessage msg = null;
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
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
		jaxbtest.setPayload(out);
		ctx = jaxbtest.createTestMessageContext();
		msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
		assertEquals(null,msg1);
	}
}
