/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.fi;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractSerializerFactory;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

/**
 * FISerializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components serialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = Fast InfoSet) 
 * as needed to implement the serialization operation. 
 */
public class FISerializerFactory extends AbstractSerializerFactory implements ISerializerFactory {

	/**
	 * Initializes FISerializerFactory instance
	 */
	public FISerializerFactory() {
		super(false);
	}

	/**
	 * Given a serialization context and an output stream, 
	 * creates and return an XMLStreamWriter for the Fast Infoset payload type. 
	 *
	 * @param ctxt - instance of ISerializationContext
	 * @param out - instace of OutputStream.
	 * @return Returns a XMLStreamWriter object for Payload type Fast Infoset.
	 */
	@Override
	public XMLStreamWriter createXMLStreamWriter(ISerializationContext ctxt,
		OutputStream out) throws Exception
	{
		//	Create the StAX document serializer
		StAXDocumentSerializer staxDocumentSerializer = new StAXDocumentSerializer();
		staxDocumentSerializer.setOutputStream(out);

        XMLStreamWriter xmlStreamWriter = staxDocumentSerializer;
        
		return xmlStreamWriter;
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
