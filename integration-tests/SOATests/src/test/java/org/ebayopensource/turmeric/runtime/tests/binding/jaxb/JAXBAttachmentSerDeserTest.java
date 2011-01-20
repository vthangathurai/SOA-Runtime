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
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;

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


public class JAXBAttachmentSerDeserTest extends BaseSerDeserTest {
	private static final String ATTACHMENT_CONTENT_TYPE =
		"multipart/related;boundary=MIMEBoundaryurn_uuid_9E55D9AADCAC7C46E811592318362121; " +
		"type=\"application/xop+xml\";start=\"<0.urn:uuid:9E55D9AADCAC7C46E811592318362122.org>" +
		"\";start-info=\"text/xml\"; charset=UTF-8";

	public JAXBAttachmentSerDeserTest() {
		super();
	}

	@Before
	public void setUpFactoriesAndConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
		m_serFactory = new JAXBXMLSerializerFactory();
		m_deserFactory = new JAXBXMLDeserializerFactory();
		super.setUp();
	}
	
	private static final Pattern cidPattern = Pattern.compile("^\\x2d\\x2d(\\S*)\\s(.*)Content\\x2dID: (\\S*)\\s(.*)\\<\\?(.*)", Pattern.DOTALL | Pattern.MULTILINE);

	@Test
	public void basicAttachmentSerDeser() throws Exception {
		System.out.println("**** Starting testBasicAttachmentSerDeser");
		MyMessage msg = TestUtils.createTestMessage(1);
		msg.setBody("This is the attachment test");

		URLDataSource ds = new URLDataSource(new URL("http://www.google.com"));
		DataHandler dh = new DataHandler(ds);
		msg.setBinaryData(dh);

		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);

		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);
		jaxbtest.setContentType(ATTACHMENT_CONTENT_TYPE);
		
		MessageContext ctx = jaxbtest.createTestMessageContext();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBTestHelper.serialize(ctx, out, msg);
		String xml1 = out.toString();
		System.out.println(xml1);
		Matcher cidMatcher = cidPattern.matcher(xml1);
		cidMatcher.matches();
		String contentType = "multipart/related;boundary=" + cidMatcher.group(1) + "; " +
		"type=\"application/xop+xml\";start=\"" + cidMatcher.group(3) +
		"\";start-info=\"text/xml\"";
		System.out.println("ContentType=**" + contentType + "**");

		Deserializer deser = m_deserFactory.getDeserializer();
		
		jaxbtest.setContentType(contentType);
		jaxbtest.setPayload(out);
		
		ctx = jaxbtest.createTestMessageContext();
		Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		out = new ByteArrayOutputStream();
		assertEquals(msg,msg1);
		System.out.println("**** Ending testBasicAttachmentSerDeser");
	}
}
