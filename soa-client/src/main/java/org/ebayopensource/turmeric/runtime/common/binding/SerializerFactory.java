/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.binding;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;


/**
 * SerializerFactory gives the Data Binding client an 
 * entry point to the Data Binding components serialization 
 * process. It provides an abstraction of the binding 
 * information for a supported encoding method (payload type) as needed to 
 * implement the serialization operation.
 * 
 * @author smalladi
 * @author wdeng
 */
public interface SerializerFactory {

	/**
	 * This method initializes the factory with options specified in the InitContext.  
	 * It should be called after SerializerFactory is created.  
	 * @param ctx An InitContext
	 * @throws ServiceException Exception when initialization fails.
	 */
public void init(ISerializerFactory.InitContext ctx) throws ServiceException;

	/**
	 * This method returns the serializer for the data binding supported by the
	 * factory (for a particular payload type).  
	 * 
	 * @return the serializer
	 */
	public Serializer getSerializer();
	
	/**
	 * Given an outbound message and an input stream, creates and return an XMLStreamWriter for the supported
	 * payload type.
	 * 
	 * @param msg the outbound message
	 * @param paramTypes the Java classes of the arguments in the message
	 * @param out The OutputStream to write the payload.
	 * @return the stream writer
	 * @throws ServiceException Exception when failed to get a XMLStreamWriter.
	 */
	public XMLStreamWriter getXMLStreamWriter(OutboundMessage msg, List<Class> paramTypes,
		OutputStream out) throws ServiceException;

	/**
	 * This method returns the PayloadType supported by this factory.
	 * This payload type is used to verify the configuration information is valid, for example, that the 
	 * data binding name specified in the config is actually supported by the factory.
	 * 
	 * @return the supported payload type
	 */
	public String getPayloadType();
	
	/**
	 * Returns the options associated with the SerializationFactory.
	 * 
	 * @return the options associated with the SerializationFactory.
	 */
	public Map<String, String> getOptions();
}
