/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.io.ByteArrayOutputStream;

import org.ebayopensource.turmeric.junit.asserts.ClassLoaderAssert;
import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.fastinfoset.JAXBFastInfosetDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.fastinfoset.JAXBFastInfosetSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Before;
import org.junit.Test;


public class FastInfosetSerDeserTest extends BaseSerDeserTest {
	public FastInfosetSerDeserTest() {
		super();
	}
	
	@Before
	public void setUpFactoriesAndConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		m_serFactory = new JAXBFastInfosetSerializerFactory();
		m_deserFactory = new JAXBFastInfosetDeserializerFactory();
		super.setUp();
	}
	
	@Before
	public void ensureConfigurationExists() {
		ClassLoaderAssert.assertResourcePresent("Service Configuration", "META-INF/soa/services/config/GlobalServiceConfig.xml");
		ClassLoaderAssert.assertResourcePresent("test1 Service Config XML", "META-INF/soa/services/config/test1/ServiceConfig.xml");
		ClassLoaderAssert.assertResourcePresent("test1 Service Config Schema", "META-INF/soa/schema/server/ServiceConfig.xsd");
	}

	@Test
	public void fastInfosetBasic() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		jaxbtest.setInboundDBDesc(xmlDbDesc);
		jaxbtest.setOutboundDBDesc(xmlDbDesc);
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		jaxbtest.setPayload(out.toByteArray());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		jaxbtest.setPayload(out.toByteArray());
		ctx = jaxbtest.createTestMessageContext();
		msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg1);
		String xml2 = out.toString();
		System.out.println(xml2);
//TODO: fix config file issue
		// Assert.assertEquals(msg, msg1);
		// Assert.assertEquals(msg.getBody(),((MyMessage)msg1).getBody());
	}
}
