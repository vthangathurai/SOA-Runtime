/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.TypeConverter;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.RecipientMapConverter;


/**
 * @author wdeng
 */
public class JAXBTestHelper {
	public static Map<String, TypeConverter<?,?>> createTypeConvertersByBoundTypeMap() {
		HashMap<String, TypeConverter<?,?>> map = new HashMap<String, TypeConverter<?,?>>();
		RecipientMapConverter converter = new RecipientMapConverter();
		map.put(converter.getBoundType().getName(), converter);
		return map;
	}

	public static Map<String, TypeConverter<?,?>> createTypeConvertersByValueTypeMap() {
		HashMap<String, TypeConverter<?,?>> map = new HashMap<String, TypeConverter<?,?>>();
		RecipientMapConverter converter = new RecipientMapConverter();
		map.put(converter.getValueType().getName(), converter);
		return map;
	}

	public static void serialize(MessageContext ctx, ByteArrayOutputStream out, Object msg1) throws ServiceException {
		OutboundMessage outMsg;
		outMsg = (OutboundMessage)ctx.getResponseMessage();
		outMsg.setParam(0, msg1);
		outMsg.serialize(out);
	}

	/**
	 * This method creates a dummy message context to be used by the jaxb data binding.  This message
	 * context doesn't depend on any other SOA framework artifact. This is an example of how to reuse the
	 * (de)ser from out side of the frame work.
	 * @param ordered
	 * @param serFactory
	 * @param deserFactory
	 * @param inboundDBDesc
	 * @param outboundDBDesc
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static TestMessageContext createSampleMessageContext(Class<?> topLevelObjClz, boolean ordered, SerializerFactory serFactory, DeserializerFactory deserFactory,
			DataBindingDesc inboundDBDesc, DataBindingDesc outboundDBDesc, String xmlString) throws Exception {
		ByteArrayInputStream is = null;
		if (null != xmlString) {
			is = new ByteArrayInputStream(xmlString.getBytes());
		}
		TestInboundMessage in = new TestInboundMessage(topLevelObjClz, ordered, inboundDBDesc, TestServiceDesc.createParamDesc(topLevelObjClz), is);
		TestOutboundMessage out = new TestOutboundMessage(outboundDBDesc, TestServiceDesc.createParamDesc(topLevelObjClz));
		return new TestMessageContext(topLevelObjClz, in, out, serFactory, deserFactory);
	}

}
