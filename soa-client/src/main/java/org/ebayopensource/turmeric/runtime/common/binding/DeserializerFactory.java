/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.binding;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;


/**
 * DeserializerFactory provides a Data Binding client an entry 
 * point to the Data Binding components deserialization process. 
 * It provides an abstraction of the binding information 
 * for a supported encoding method (payload type) as needed to implement the 
 * deserialization operation.
 * 
 * @author smalladi
 * @author wdeng
 */
public interface DeserializerFactory {
	/**
	 * This method initializes the factory with options specified in the InitContext.  
	 * It should be called after DeserializerFactory is created.  
	 * @param ctx An InitContext
	 * @throws ServiceException Exception when initialization fails.
	 */
	public void init(IDeserializerFactory.InitContext ctx) throws ServiceException;

	/**
	 * This method returns the deserializer for the data binding supported by the
	 * factory (for a particular payload type).  
	 * 
	 * @return the deserializer
	 */
	public Deserializer getDeserializer();

	/**
	 * Given an inbound message and an ObjectNode object(The DOM of the message), 
	 * creates and return an XMLStreamReader for the supported
	 * payload type.
	 * 
	 * @param msg the inbound message
	 * @param objNode the root level object node of the message DOM to be deserialized.
	 * @return the stream reader
	 * @throws ServiceException Exception when failed to get a XMLStreamReader.
	 */
	public XMLStreamReader getXMLStreamReader(InboundMessage msg, ObjectNode objNode) throws ServiceException;

	/**
	 * Given an inbound message and an input stream, creates and return an XMLStreamReader for the supported
	 * payload type.
	 * 
	 * @param msg the inbound message
	 * @param paramTypes the Java classes of the arguments in the message
	 * @param in The InputStream to read the payload.
	 * @return the stream reader
	 * @throws ServiceException Exception when failed to get a XMLStreamReader.
	 */
	public XMLStreamReader getXMLStreamReader(InboundMessage msg, List<Class> paramTypes,
		InputStream in) throws ServiceException;

	/**
	 * This method returns the PayloadType supported by this factory.
	 * This payload type is used to verify the configuration information is valid, for example, that the 
	 * data binding name specified in the config is actually supported by the factory.
	 * 
	 * @return the supported payload type
	 */
	public String getPayloadType();
	
	/**
	 * Returns the options associated with the DeserializationFactory.
	 * 
	 * @return the options associated with the DeserializationFactory.
	 */
	public Map<String, String> getOptions();
}
