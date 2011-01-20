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
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractDeserializerFactory;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * XMLDeserializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components deserialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = XML) 
 * as needed to implement the deserialization operation. 
 * @author wdeng
 *
 */
public class XMLDeserializerFactory extends AbstractDeserializerFactory
		implements IDeserializerFactory {
	
	/**
	 * Given a deserialization context and an input stream, 
	 * creates and return an XMLStreamReader for the payload type XML. 
	 *
	 * @param ctxt - instance of IDeserializationContext
	 * @param in - instace of inputstream.
	 * @return Returns a XMLStreamReader object for Payload type XML.
	 */
	@Override
	public XMLStreamReader createXMLStreamReader(IDeserializationContext ctxt,
			InputStream in) throws Exception {
		Charset charset = ctxt.getCharset();
		XMLInputFactory factory = new WstxInputFactory();		
		disableDTDValidation(factory);		
		InputStreamReader isReader = new InputStreamReader(in, charset);
		XMLStreamReader xmlStreamReader = 
			factory.createXMLStreamReader(isReader);
		return xmlStreamReader;
	}

	/**
	 * This method returns the PayloadType supported by this factory 
	 * @return XML as payload type.
	 */
	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_XML;
	}
	
	private void disableDTDValidation(XMLInputFactory factory) {		
		factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);		
	}

}
