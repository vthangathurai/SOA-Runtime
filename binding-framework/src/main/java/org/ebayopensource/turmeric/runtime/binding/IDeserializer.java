/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;
import org.ebayopensource.turmeric.runtime.binding.exception.DeserializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.TypeConversionAdapterCreationException;


/**
 * Deserializer is responsible for unmarshalling 
 * encoded data of a particular data type, for example, schema xml,  
 * name-value, or JSON, into a Java content tree.  It can also provide 
 * data encoding validation.
 *
 * @author smalladi
 * @author wdeng
 */
public interface IDeserializer {
	
	/**
	 * 	
	 * Initiate deserialization of the specified payload passed in from 
	 * the XMLStreamReader descrializer configuration information is passed
	 * in through the IDeserializationContextImpl.
	 *
	 * @param ctxt 	An IDeserializationContext
	 * @param xmlStreamReader An XMLStreamReader
	 * @return the deserialized object.
	 * @throws DeserializationException Exception when deserialization fails, 
	 * @throws TypeConversionAdapterCreationException Exception when type 
	 * 			convertion fails,
	 * @throws BindingSetupException Exception for when initializing 
	 * 			the deserializer.
	 */
	public Object deserialize(IDeserializationContext ctxt, XMLStreamReader xmlStreamReader) 
	throws  BindingSetupException, 
			DeserializationException,
			TypeConversionAdapterCreationException;

	/**
	 * There are two kinds of deserializers,
	 *  1.One general deserializer, this is the top level deserializer. It is 
	 *  called to deserializer object of any type, its getBoundType() method 
	 *  should return null;
	 *  2.Custom deserializers,  these deserializers are type specific, they 
	 *  are called to deserialize the type of object defined by the getBoundedType() 
	 *  method. The getBoundedType() method of these deserializer must returns a 
	 *  Class object that it supports.
	 *  
	 *  @return a Class object that the custom deserializer creates, null for a general deserializer.
	 */
	public Class getBoundType();
}
