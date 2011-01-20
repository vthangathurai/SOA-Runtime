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
import java.util.HashMap;
import java.util.Map;

import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.fastinfoset.JAXBFastInfosetDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.fastinfoset.JAXBFastInfosetSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.json.JAXBJSONDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.json.JAXBJSONSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.nv.JAXBNVDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.nv.JAXBNVSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.junit.Test;


public class TypeConverterTest extends BaseSerDeserTest {
	private static final String[] SUPPORTED_FORMATS = new String[] {
				BindingConstants.PAYLOAD_XML,
				BindingConstants.PAYLOAD_JSON,
				BindingConstants.PAYLOAD_NV,
				TestUtils.PAYLOAD_UNORDERED_NV,
//				SOAConstants.PAYLOAD_FAST_INFOSET
				};

	private static final HashMap<String, DataFormatInfo> DATA_FORMAT_INFO = new HashMap<String, DataFormatInfo>();
	private static TypeConverterTest s_testInstance;
	static {
		try {
			s_testInstance = new TypeConverterTest();
			SerializerFactory serFactory;
			DeserializerFactory deserFactory;
			serFactory = new JAXBXMLSerializerFactory();
			serFactory.init(s_testInstance.new TestSerInitContext());

			deserFactory = new JAXBXMLDeserializerFactory();
			deserFactory.init(s_testInstance.new TestDeserInitContext());

			DATA_FORMAT_INFO.put(BindingConstants.PAYLOAD_XML,
					new DataFormatInfo(BindingConstants.PAYLOAD_XML,
							serFactory, deserFactory)
							);

			serFactory = new JAXBJSONSerializerFactory();
			serFactory.init(s_testInstance.new TestSerInitContext());

			deserFactory = new JAXBJSONDeserializerFactory();
			deserFactory.init(s_testInstance.new TestDeserInitContext());

			DATA_FORMAT_INFO.put(BindingConstants.PAYLOAD_JSON,
					new DataFormatInfo(BindingConstants.PAYLOAD_JSON,
							serFactory, deserFactory)
							);

			serFactory = new JAXBNVSerializerFactory();
			serFactory.init(s_testInstance.new TestSerInitContext());

			deserFactory = new JAXBNVDeserializerFactory();
			deserFactory.init(s_testInstance.new TestDeserInitContext());

			DATA_FORMAT_INFO.put(BindingConstants.PAYLOAD_NV,
					new DataFormatInfo(BindingConstants.PAYLOAD_NV,
							serFactory, deserFactory)
							);

			serFactory = new JAXBNVSerializerFactory();
			serFactory.init(s_testInstance.new TestSerInitContext());

			deserFactory = new JAXBNVDeserializerFactory();
			deserFactory.init(s_testInstance.new TestDeserInitContext());

			DATA_FORMAT_INFO.put(TestUtils.PAYLOAD_UNORDERED_NV,
					new DataFormatInfo(TestUtils.PAYLOAD_UNORDERED_NV,
							serFactory, deserFactory)
							);

			serFactory = new JAXBFastInfosetSerializerFactory();
			serFactory.init(s_testInstance.new TestSerInitContext());

			deserFactory = new JAXBFastInfosetDeserializerFactory();
			deserFactory.init(s_testInstance.new TestDeserInitContext());

			DATA_FORMAT_INFO.put(BindingConstants.PAYLOAD_FAST_INFOSET,
					new DataFormatInfo(BindingConstants.PAYLOAD_FAST_INFOSET,
							serFactory, deserFactory)
							);
		} catch (Exception e) {};
	}

	public TypeConverterTest() {
		super();
	}

	 @Test
	public void typeConverterBasic() throws Exception {
			System.out.println("**** Starting testTypeConverterBasic");
		MyMessage msg = TestUtils.createTestMessage(1);
		for (int i = 0; i<SUPPORTED_FORMATS.length; i++) {
			String dataFormat = SUPPORTED_FORMATS[i];
			DataFormatInfo info = DATA_FORMAT_INFO.get(dataFormat);
//TODO: make me work.
//			doTest(msg, info);
		}
		System.out.println("**** Ending testTypeConverterBasic");
	}

	private void doTest(MyMessage msg, DataFormatInfo info) throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("configtypeconvert");
		try {
			String name = info.m_name;
			String dataFormat = info.m_format;
			SerializerFactory serFactory = info.m_serFactory;
			DeserializerFactory deserFactory = info.m_deserFactory;

			System.out.println("**** Testing " + name);
			boolean ordered = (BindingConstants.PAYLOAD_NV.equals(dataFormat) && BindingConstants.PAYLOAD_NV.equals(name));
			
			JAXBTestBuilder jaxbtest = new JAXBTestBuilder();
			jaxbtest.setTestServer(jetty);
			jaxbtest.setOrdered(ordered);
			jaxbtest.setSerializerFactory(serFactory);
			jaxbtest.setDeserializerFactory(deserFactory);
			
			MessageContext ctx = jaxbtest.createTestMessageContext();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jaxbtest.serialize(ctx, out, msg);
			String xml1 = out.toString();
			System.out.println(xml1);
			jaxbtest.setPayload(out);
			Deserializer deser = deserFactory.getDeserializer();
			ctx = jaxbtest.createTestMessageContext();
			Object msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
			out = new ByteArrayOutputStream();
			jaxbtest.serialize(ctx, out, msg1);
			jaxbtest.setPayload(out);
			ctx = jaxbtest.createTestMessageContext();
			msg1 = deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
			out = new ByteArrayOutputStream();
			jaxbtest.serialize(ctx, out, msg1);
			String xml2 = out.toString();
			System.out.println(xml2);
			assertEquals(msg,msg1);

			assertTrue(xml2.indexOf("entry") < 0);
			System.out.println("**** Finish testing " + dataFormat);
		} finally {
			ServiceConfigManager.getInstance().setConfigTestCase("config");
		}
	}

	private static class DataFormatInfo {
		String m_name;
		String m_format;
		SerializerFactory m_serFactory;
		DeserializerFactory m_deserFactory;

		DataFormatInfo(String format, SerializerFactory serFactory,
				DeserializerFactory deserFactory) {
			this("", format, serFactory, deserFactory);
		}

		DataFormatInfo(String name, String format, SerializerFactory serFactory,
				DeserializerFactory deserFactory) {
			m_name = name;
			m_format = format;
			m_serFactory = serFactory;
			m_deserFactory = deserFactory;
			try {
				m_serFactory.init(this.new TestSerInitContext());
				m_deserFactory.init(this.new TestDeserInitContext());
			} catch (Exception e) {}
		}

		 private class TestSerInitContext implements ISerializerFactory.InitContext {

				public ServiceId getServiceId() {
					return new ClientServiceId("MySerDeserTest", "");
				}

				public Map<String,String> getOptions() {
					return new HashMap<String, String> ();
				}

				public Class[] getRootClasses() {
					return new Class[] {MyMessage.class};
				}
		 }


		 private class TestDeserInitContext implements IDeserializerFactory.InitContext {

				public ServiceId getServiceId() {
					return new ClientServiceId("MySerDeserTest", "");
				}

				public Map<String,String> getOptions() {
					return new HashMap<String, String> ();
				}

				public Class[] getRootClasses() {
					return new Class[] {MyMessage.class, ErrorMessage.class};
				}

				@Override
				public Schema getUpaAwareMasterSchema() {
					return null;
				}
		 }
	}
}
