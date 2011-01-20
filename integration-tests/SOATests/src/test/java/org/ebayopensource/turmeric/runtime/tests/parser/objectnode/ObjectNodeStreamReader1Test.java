/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.parser.objectnode;

import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeStreamReader;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.binding.jaxb.BaseSerDeserTest;
import org.ebayopensource.turmeric.runtime.tests.binding.jaxb.JAXBTestBuilder;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ObjectNodeStreamReader1Test extends BaseSerDeserTest {

	private String m_longXml = "<aces><![CDATA[<ACES version=\"1\"><Header><Company>Company</Company><SenderName>SenderName</SenderName><SenderPhone>SenderPhone</SenderPhone><TransferDate>TransferDate</TransferDate><DocumentTitle>DocumentTitle</DocumentTitle><EffectiveDate>EffectiveDate</EffectiveDate><SubmissionType>SubmissionType</SubmissionType><VcdbVersionDate>VcdbVersionDate</VcdbVersionDate><QdbVersionDate>QdbVersionDate</QdbVersionDate><PcdbVersionDate>PcdbVersionDate</PcdbVersionDate></Header><App action=\"A\" id=\"1\"><BaseVehicle id=\"5896\"/><SubModel id=\"2\"/><MfrBodyCode id=\"1359\"/><BodyNumDoors id=\"8\"/><BodyType id=\"8\"/><DriveType id=\"5\"/><EngineBase id=\"555\"/><EngineDesignation id=\"135\"/><EngineVIN id=\"1\"/><EngineVersion id=\"3\"/><EngineMfr id=\"25\"/><ValvesPerEngine id=\"16\"/><FuelDeliveryType id=\"5\"/><FuelDeliverySubType id=\"5\"/><FuelSystemControlType id=\"5\"/><FuelSystemDesign id=\"60\"/><Aspirationid=\"5\"/><CylinderHeadTypeid=\"6\"/><FuelTypeid=\"5\"/><IgnitionSystemTypeid=\"5\"/><TransmissionMfrCodeid=\"1183\"/><TransmissionTypeid=\"6\"/><TransmissionControlTypeid=\"5\"/><TransmissionNumSpeedsid=\"6\"/><TransElecControlledid=\"2\"/><TransmissionMfrid=\"25\"/><TransferCaseBaseid=\"5\"/><TransferCaseid=\"4\"/><TransferCaseMfrid=\"25\"/><BedLengthid=\"3\"/><BedTypeid=\"3\"/><WheelBaseid=\"1\"/><BrakeSystemid=\"5\"/><FrontBrakeTypeid=\"5\"/><RearBrakeTypeid=\"5\"/><BrakeABSid=\"8\"/><FrontSpringTypeid=\"5\"/><RearSpringTypeid=\"5\"/><SteeringSystemid=\"5\"/><SteeringTypeid=\"6\"/><RestraintTypeid=\"1\"/><Regionid=\"1\"/><Qualid=\"123\"><paramvalue=\"14\"uom=\"in\"/><text>With14\"Wheels</text></Qual><Noteid=\"2\"/><Qty>Qty</Qty><PartTypeid=\"\">PartType</PartType><MfrLabel>ACMESuperduperStrut</MfrLabel><Positionid=\"5\"/><Part>Part</Part><DisplayOrder>1</DisplayOrder></App><Footer><RecordCount>RecordCount</RecordCount></Footer></ACES>]]></aces>";
		
	public ObjectNodeStreamReader1Test() {
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
	public void testObjectNodeStreamReaderGetNodeCallWithLargeData() throws Exception {
		MyMessage msg = TestUtils.createCustomMessage(1, m_longXml);
		msg.setError(null);
		DataBindingDesc xmlDbDesc = new DataBindingDesc(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML, m_serFactory, m_deserFactory, null, null, null, null);
		
		JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
		jaxbtest.setTestServer(jetty);
		jaxbtest.setOrdered(false);
		jaxbtest.setSymmetricDBDesc(xmlDbDesc);
		jaxbtest.setSerializerFactory(m_serFactory);
		jaxbtest.setDeserializerFactory(m_deserFactory);

		String xml1 = jaxbtest.createOnWireString(msg);
		System.out.println(xml1);
		Deserializer deser = m_deserFactory.getDeserializer();
		MessageContext ctx = jaxbtest.createTestMessageContext(xml1);

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
		System.out.println("Deserialized message is ==> " + msg1.getBody());
		Assert.assertEquals(msg,msg1);
		Assert.assertEquals(m_longXml,msg1.getBody());
	}
	
}
