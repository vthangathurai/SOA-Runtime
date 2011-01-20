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
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.fi;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractDeserializerFactory;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;

/**
 * FIDeserializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components deserialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = Fast InfoSet) 
 * as needed to implement the deserialization operation. 
 * @author wdeng
 *
 */
public class FIDeserializerFactory extends AbstractDeserializerFactory
		implements IDeserializerFactory {

	/**
	 * Initializes FIDeserializerFactory instance
	 */
	public FIDeserializerFactory() {
		// empty
	}

	/**
	 * Given a deserialization context and an input stream, 
	 * creates and return an XMLStreamReader for the Fast Infoset payload type. 
	 *
	 * @param ctxt - instance of IDeserializationContext
	 * @param in - instace of inputstream.
	 * @return Returns a XMLStreamReader object for Payload type Fast Infoset.
	 */
	@Override
	public XMLStreamReader createXMLStreamReader(IDeserializationContext ctxt,
			InputStream in) throws Exception {
		XMLStreamReader xmlStreamReader = new StAXDocumentParser(in);
		return xmlStreamReader;
	}

	/**
	 * This method returns the PayloadType supported by this factory 
	 * @return FAST_INFOSET as payload type.
	 */
	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_FAST_INFOSET;
	}
}
