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
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.json;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ObjectNodeToXMLStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.json.JSONConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.json.JSONStreamObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.json.JSONStreamReadContext;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;


/**
 * JSONDeserializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components deserialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = JSON) 
 * as needed to implement the deserialization operation. 
 * @author wdeng
 *
 */
public class JSONDeserializerFactory extends AbstractDeserializerFactory
		implements IDeserializerFactory {

	/**
	 * Given a deserialization context and an input stream, 
	 * creates and return an XMLStreamReader for the JSON payload type. 
	 *
	 * @param ctxt - instance of IDeserializationContext
	 * @param in - instace of inputstream.
	 * @return Returns a XMLStreamReader object for Payload type JSON.
	 */
	@Override
	public XMLStreamReader createXMLStreamReader(IDeserializationContext ctxt,
			InputStream in) throws Exception {
		NamespaceConvention convention = NamespaceConvention.createDeserializationNSConvention(ctxt);
		Charset charset = ctxt.getCharset();
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(in, ctxt.getRootXMLName(), charset);
		
		JSONStreamReadContext jsonCtx = new JSONStreamReadContext(jfin, convention, charset, m_options);
		
		ObjectNodeImpl root = new JSONStreamObjectNodeImpl(jsonCtx);
		XMLStreamReader reader = new ObjectNodeToXMLStreamReader(root,
			JSONConstants.JSON_NAMESPACE_DEF_PREFIX, convention, m_options);
		return reader;
	}

	/**
	 * This method returns the PayloadType supported by this factory 
	 * @return JSON as payload type.
	 */
	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_JSON;
	}

}
