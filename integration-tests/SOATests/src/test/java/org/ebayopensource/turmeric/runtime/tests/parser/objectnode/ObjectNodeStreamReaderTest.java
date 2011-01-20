/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 *
 */
package org.ebayopensource.turmeric.runtime.tests.parser.objectnode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.StreamableObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.binding.jaxb.BaseSerDeserTest;
import org.ebayopensource.turmeric.runtime.tests.binding.jaxb.JAXBTestBuilder;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


/**
 * @author wdeng
 *
 */
public class ObjectNodeStreamReaderTest extends BaseSerDeserTest {

	public ObjectNodeStreamReaderTest() {
		super();
	}

	@Before
	public void setUpConfig() throws Exception {
		ClientConfigManager.getInstance().setConfigTestCase("config");
		ServiceConfigManager.getInstance().setConfigTestCase("config");
	}
	
	@Before
	public void setUpFactories() throws Exception {
		m_serFactory = new JAXBXMLSerializerFactory();
		m_deserFactory = new JAXBXMLDeserializerFactory();
		super.setUp();
	}
	
	@Test
	public void testObjectNodeStreamReaderBasic() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		msg.setError(null);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireString(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		
		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();
		XMLStreamReader rawReader = reqMsg.getXMLStreamReader();
		ObjectNodeStreamReader objectNodeFactory = (ObjectNodeStreamReader) rawReader;
		ObjectNode node = objectNodeFactory.getObjectNode();


		// Get one child
		Iterator<ObjectNode> childIter = node.getChildrenIterator();
		ObjectNode child = childIter.next();
		Assert.assertEquals("MyMessage", child.getNodeName().getLocalPart());

		// Get one grand child
		Iterator<ObjectNode> grandChildIter = child.getChildrenIterator();
		ObjectNode grandChild = grandChildIter.next();
		Assert.assertEquals("body", grandChild.getNodeName().getLocalPart());

		ObjectNode secondGrandChild = grandChildIter.next();
		Assert.assertEquals("recipients", secondGrandChild.getNodeName().getLocalPart());

		// Get one grand grand child
		Iterator<ObjectNode> ggChildIter = secondGrandChild.getChildrenIterator();
		ObjectNode ggChild = ggChildIter.next();
		Assert.assertEquals("entry", ggChild.getNodeName().getLocalPart());

		if (node.hasChildNodes()) {
			node.getChildNodesSize();
			node.cloneNode();
			if (node instanceof ObjectNodeImpl) {
				ObjectNodeImpl oniInst = (ObjectNodeImpl) node;
				List<ObjectNode> childNodes = oniInst.getChildNodes(child.getNodeName());
				Assert.assertTrue(childNodes.size() > 0);
				Assert.assertFalse("Should contain only one childNode",
						oniInst.hasChildNode(child.getNodeName(), Integer.MAX_VALUE));
			}

		}

		MyMessage msg1 = (MyMessage) deser.deserialize(reqMsg, MyMessage.class);
		Assert.assertEquals(msg,msg1);
	}

	@Test
	public void testObjectNodeStreamReaderWOGetNodeCall() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		msg.setError(null);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireString(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();
		
		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();

		MyMessage msg1 = (MyMessage) deser.deserialize(reqMsg, MyMessage.class);
		Assert.assertEquals(msg,msg1);
	}

	@Test
	public void testObjectNodeStreamReaderJumpOverUnbuiltNode() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(3);
		msg.setError(null);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);


		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireString(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();

		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();
		XMLStreamReader rawReader = reqMsg.getXMLStreamReader();
		ObjectNodeStreamReader objectNodeFactory = (ObjectNodeStreamReader) rawReader;
		ObjectNode node = objectNodeFactory.getObjectNode();

		// Get one child
		Iterator<ObjectNode> childIter = node.getChildrenIterator();
		ObjectNode child = childIter.next();
		Assert.assertEquals("MyMessage", child.getNodeName().getLocalPart());

		// Gets to recipients
		childIter = child.getChildrenIterator();
		ObjectNode grandChild = childIter.next();
		Assert.assertEquals("body", grandChild.getNodeName().getLocalPart());

		ObjectNode recipients = childIter.next();
		Assert.assertEquals("recipients", recipients.getNodeName().getLocalPart());

		// Get first address
		Iterator<ObjectNode> entryIter = recipients.getChildrenIterator();
		nextAddress(entryIter);

		// Get first address
		nextAddress(entryIter);

		MyMessage msg1 = (MyMessage) deser.deserialize(reqMsg, MyMessage.class);
		Assert.assertEquals(msg,msg1);
	}

	private ObjectNode nextAddress(Iterator<ObjectNode> entryIter) throws XMLStreamException {
		ObjectNode entry1 = entryIter.next();
		Assert.assertEquals("entry", entry1.getNodeName().getLocalPart());

		Iterator<ObjectNode> keyvalueIter = entry1.getChildrenIterator();
		ObjectNode key1 = keyvalueIter.next();
		Assert.assertEquals("key", key1.getNodeName().getLocalPart());

		ObjectNode value1 = keyvalueIter.next();
		Assert.assertEquals("value", value1.getNodeName().getLocalPart());

		Iterator<ObjectNode> addrIter1 = value1.getChildrenIterator();
		ObjectNode city = addrIter1.next();
		Assert.assertEquals("city", city.getNodeName().getLocalPart());
		Assert.assertEquals("San Jose", city.getNodeValue());
		return value1;
	}
	@Test
	public void testObjectNodeStreamReaderWithExtraEndTag() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireStringWithExtraEndTag(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();

		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();
		XMLStreamReader rawReader = reqMsg.getXMLStreamReader();
		ObjectNodeStreamReader objectNodeFactory = (ObjectNodeStreamReader) rawReader;
		ObjectNode node = objectNodeFactory.getObjectNode();

		// Get one child
		Iterator<ObjectNode> childIter = node.getChildrenIterator();
		ObjectNode child = childIter.next();
		Assert.assertEquals("MyMessage", child.getNodeName().getLocalPart());

		// Get one grand child
		Iterator<ObjectNode> grandChildIter = child.getChildrenIterator();
		ObjectNode grandChild = grandChildIter.next();
		Assert.assertEquals("body", grandChild.getNodeName().getLocalPart());

		ObjectNode secondGrandChild = grandChildIter.next();
		Assert.assertEquals("recipients", secondGrandChild.getNodeName().getLocalPart());

		// Get one grand grand child
		Iterator<ObjectNode> ggChildIter = secondGrandChild.getChildrenIterator();
		ObjectNode ggChild = ggChildIter.next();
		Assert.assertEquals("entry", ggChild.getNodeName().getLocalPart());

		try {
			deser.deserialize(reqMsg, MyMessage.class);
			Assert.assertFalse(true);
		} catch (Exception e) {
			Assert.assertTrue(ServiceException.class.isAssignableFrom(e.getClass()));
			Throwable cause = e.getCause();
			Assert.assertEquals(UnmarshalException.class.getName(), cause.getClass().getName());
		}
	}


	@Test
	public void testObjectNodeStreamReaderWOEndTagAndGetNodeCall() throws Exception {
		MyMessage msg = TestUtils.createTestMessage(1);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);


		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireStringWithMissingEndTag(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();

		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();
		XMLStreamReader rawReader = reqMsg.getXMLStreamReader();
		Assert.assertTrue(rawReader instanceof ObjectNodeStreamReader);

		MyMessage msg1 = null;
		try {
			msg1 = (MyMessage) deser.deserialize(reqMsg, MyMessage.class);
			Assert.assertTrue("unexpected success", false);
		} catch (ServiceException e) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (null != msg1) {
				jaxbtest.serialize(ctx, out, msg1);
				String xml2 = out.toString();
				System.out.println(xml2);
				Assert.assertNotSame(xml1,xml2);
			}

			List<Throwable> errors = ctx.getWarningList();
			Assert.assertTrue(errors.size()>0);

			Throwable t = errors.get(0);
			ServiceException dve = (ServiceException)t;
			Assert.assertEquals(ErrorDataCollection.svc_data_validation_warning.getErrorId(),
					dve.getErrorMessage().getError().get(0).getErrorId());
			dve.localizeMessage("en-US");

			System.out.println(dve.toString());
		}
	}

	@Test
	public void testWithNullMsgWithGetNodeCall() throws Exception {
		MyMessage msg = null;
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireString(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();

		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();
		XMLStreamReader rawReader = reqMsg.getXMLStreamReader();
		ObjectNodeStreamReader objectNodeFactory = (ObjectNodeStreamReader) rawReader;

		ObjectNode node = objectNodeFactory.getObjectNode();
		Iterator<ObjectNode> childIter = node.getChildrenIterator();
		ObjectNode child = childIter.next();
		ObjectNode attribute = child.getAttribute(0);
		Assert.assertEquals("nil", attribute.getNodeName().getLocalPart());
		Assert.assertEquals("xsi", attribute.getNodeName().getPrefix());
		Assert.assertEquals("true", attribute.getNodeValue());


		MyMessage msg1 = (MyMessage) deser.deserialize(reqMsg, MyMessage.class);
		Assert.assertEquals(msg,msg1);
	}

	@Test
	public void testWithNullMsg() throws Exception {
		MyMessage msg = null;
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(true);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		MessageContext ctx = jaxbtest.createTestMessageContext();
		String xml1 = jaxbtest.createOnWireString(msg);
		System.out.println(xml1);
		jaxbtest.setPayload(xml1, ctx.getEffectiveCharset());
		Deserializer deser = m_deserFactory.getDeserializer();
		ctx = jaxbtest.createTestMessageContext();

		InboundMessage reqMsg = (InboundMessage)ctx.getRequestMessage();
		XMLStreamReader rawReader = reqMsg.getXMLStreamReader();
		Assert.assertTrue(rawReader instanceof ObjectNodeStreamReader);

		MyMessage msg1 = (MyMessage) deser.deserialize(reqMsg, MyMessage.class);
		Assert.assertEquals(msg,msg1);
	}

	@Test
	public void testConstructAttributeNode() throws Exception {

		String text = "<bookstore><book category=\"web\" cover=\"paperback\">" +
		"<title lang=\"en\">Learning XML</title>" + "<author>Erik T. Ray</author>" +
		"<year>2003</year>" + "<price>39.95</price>" + "</book></bookstore>";
		InputStream inStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
		XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inStream);
		ObjectNodeStreamReader oNodeReader = new ObjectNodeStreamReader(xmlStreamReader);
		StreamableObjectNodeImpl rootNode = new StreamableObjectNodeImpl(oNodeReader);
		ObjectNode attribNode = rootNode.nextChild().nextChild().getAttribute(0);
		System.out.println("Attrib Node : " +attribNode.getNodeName());
		System.out.println("Attrib Node : " +attribNode.isAttribute());
		Assert.assertEquals(attribNode.getNodeName().getLocalPart(), "category");
		Assert.assertTrue(attribNode.isAttribute());

	}


}
