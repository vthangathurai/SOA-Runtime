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
import java.util.List;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
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


public class DataValidationTest extends BaseSerDeserTest {
	public DataValidationTest() {
		super();
	}

	@Before
	public void setUpFactoriesAndConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		m_serFactory = new JAXBXMLSerializerFactory();
		m_deserFactory = new JAXBXMLDeserializerFactory();
		super.setUp();
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void xMLExtraEndTag() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null,
				JAXBTestHelper.createTypeConvertersByBoundTypeMap(), JAXBTestHelper.createTypeConvertersByValueTypeMap());
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		
		String xml1 = jaxbtest.createOnWireStringWithExtraEndTag(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1);

		Deserializer deser = m_deserFactory.getDeserializer();
		MessageContext ctx = jaxbtest.createTestMessageContext();
		Object msg1 = null;
		try {
			msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		} catch (Exception e) {
			if (null != msg1) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXBTestHelper.serialize(ctx, out, msg1);
				String xml2 = out.toString();
				System.out.println(xml2);
				assertNotSame(xml1,xml2);
			}
			return;
		}
		assertTrue(false);
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void xMLMissingEndTag() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc dbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null,
				JAXBTestHelper.createTypeConvertersByBoundTypeMap(), JAXBTestHelper.createTypeConvertersByValueTypeMap());

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setSymmetricDBDesc(dbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		
		String xml1 = jaxbtest.createOnWireStringWithMissingEndTag(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1);

		Deserializer deser = m_deserFactory.getDeserializer();
		MessageContext ctx = jaxbtest.createTestMessageContext();
		Object msg1 = null;
		try {
			msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
			fail("unexpected success");
		} catch (ServiceException e) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (null != msg1) {
				JAXBTestHelper.serialize(ctx, out, msg1);
				String xml2 = out.toString();
				System.out.println(xml2);
				assertNotSame(xml1,xml2);
			}

			List<Throwable> errors = ctx.getWarningList();
			assertTrue(errors.size()>0);

			Throwable t = errors.get(0);
			ServiceException dve = (ServiceException)t;
            // Will cause IllegalArgumentException
			// Error id 1000 belongs to system errors range and should not be used in application code. Please, pick value below 1000 or above 99999
			
			assertEquals(5003L, dve.getErrorMessage().getError().get(0).getErrorId());
			dve.localizeMessage("en-US");

			System.out.println(dve.toString());
		}
	}
}
