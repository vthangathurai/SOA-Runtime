/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.exception.WriterCreationException;


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
public interface ISerializerFactory {

	/**
	 * Given a InitContext, initialize the Serializer factory. 
	 * 
	 * @param ctx  an InitContext.
	 * @throws BindingException Exception when initialization fails.
	 */
	public void init(InitContext ctx) throws BindingException;

	/**
	 * This method returns the serializer for the data binding supported by the
	 * factory (for a particular payload type).  
	 * 
	 * @return the serializer
	 */
	public ISerializer getSerializer();
	
	/**
	 * Given a serialization context and an input stream, creates and return an 
	 * XMLStreamWriter for the supported payload type.

	 * @param ctx A ISerializationContext
	 * @param out An OutputStream to write the payload.
	 * @return an XMLStreamWriter from the given output stream.
	 * @throws WriterCreationException Exception when failed to create the writer. 
	 */
	public XMLStreamWriter getXMLStreamWriter(ISerializationContext ctx,
		OutputStream out) throws WriterCreationException;

	/**
	 * This method returns the PayloadType supported by this factory.
	 * This payload type is used to verify the configuration information is valid, for example, that the 
	 * data binding name specified in the config is actually supported by the factory.  Currently,  
	 * framework supported payload types are:  FAST_INFOSET, XML, JSON, and NV (Name-value pair)
	 * 
	 * @return the supported payload type
	 */
	public String getPayloadType();
	
	/**
	 * Returns the options associated with the SerializationFactory. 
	 * @return Map of options
	 */
	public Map<String, String> getOptions();

	/**
	 * Interface for passing initialization parameters when creating
	 * ISerializerfactory.
	 * 
	 * @author wdeng
	 *
	 */
	public static interface InitContext {
		/**
		 * Returns the options associated with the SerializationFactory. 
		 * @return Map of options
		 */
		public Map<String,String> getOptions();
		
		/**
		 * Get the array of root element classes.
		 * @return array of root element classes.
		 */
		public Class[] getRootClasses();
	}
}
