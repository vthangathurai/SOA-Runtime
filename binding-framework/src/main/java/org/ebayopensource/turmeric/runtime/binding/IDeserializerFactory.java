/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.exception.DeserializtionInitializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.ReaderCreationException;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;


/**
 * IDeserializerFactory provides a Data Binding client an entry 
 * point to the Data Binding components deserialization process. 
 * It provides an abstraction of the binding information 
 * for a supported encoding method (payload type) as needed to implement the 
 * deserialization operation.
 * 
 * @author smalladi
 * @author wdeng
 */
public interface IDeserializerFactory {

	/**
	 * Given a InitContext, initialize the deserialization factory.
	 * 
	 * @param ctx The initContext.
	 * @throws DeserializtionInitializationException Exception thrown when failed
	 * 		to intializing DeserializationFactory.
	 */
	public void init(InitContext ctx) throws DeserializtionInitializationException;

	/**
	 * This method returns the deserializer for the data binding supported by the
	 * factory (for a particular payload type).  
	 * 
	 * @return the deserializer
	 */
	public IDeserializer getDeserializer();

	/**
	 * Given a deserialization context and an ObjectNode object(The DOM of the message), 
	 * creates and return an XMLStreamReader for the supported
	 * payload type.

	 * @param context The IDeserializationContext
	 * @param objNode The ObjectNode to create XMLStreamReader.
	 * @return an XMLStreamReader created for the given objNode.
	 * @throws ReaderCreationException Exception thrown when failed to create a reader.
	 */
	public XMLStreamReader getXMLStreamReader(IDeserializationContext context, ObjectNode objNode) throws ReaderCreationException;
	
	/**
	 * Given a deserialization context and an input stream, creates and return an 
	 * XMLStreamReader for the supported payload type.
	 * 
	 * @param context The IDeserializationContext.
	 * @param in The InputStream.
	 * @return an XMLStreamReader for the given input stream.
	 * @throws ReaderCreationException Exception throws when having problem creating
	 * 		the reader.
	 */
	public XMLStreamReader getXMLStreamReader(IDeserializationContext context, InputStream in) throws ReaderCreationException;

	/**
	 * This method returns the PayloadType supported by this factory.
	 * This payload type is used to verify the configuration information is valid, for example, that the 
	 * data binding name specified in the config is actually supported by the factory.   Currently,  
	 * framework supported payload types are:  FAST_INFOSET, XML, JSON, and NV (Name-value pair)
	 * 
	 * @return the supported payload type
	 */
	public String getPayloadType();
	
	/**
	 * Returns the options associated with the DeserializationFactory.
	 * 
	 * @return the map of deserialization options the deserializer is set up with.
	 */
	public Map<String, String> getOptions();

	/**
	 * This interface is used to pass initialization parameter for
	 * IDeserializerFactory creation.
	 * 
	 * @author wdeng
	 *
	 */
	public static interface InitContext {
		/**
		 * 
		 * @return A name-value options map.
		 */
		public Map<String,String> getOptions();
		/**
		 * 
		 * @return An array of possible Root Classes.
		 */
		public Class[] getRootClasses();
		/**
		 * 
		 * @return a XML schema for payload XML validation. 
		 */
		public Schema getUpaAwareMasterSchema();
	}
}
