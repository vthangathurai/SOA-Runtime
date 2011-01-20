/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;
import org.ebayopensource.turmeric.runtime.binding.exception.SerializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.SerializationOutputException;
import org.ebayopensource.turmeric.runtime.binding.exception.TypeConversionAdapterCreationException;


/**
 * ISerializer is responsible for  
 * serializing Java content trees back into an encoded data. 
 * 
 * @author smalladi
 * @author wdeng
 */
public interface ISerializer {

	
	/**
	 * method serialize takes a java content tree, marshal it into an encoded data 
	 * and output it to the given output stream. It throws 
	 * SerializationException when there is an error during the process.
	 * 
	 * @param ctx an ISerializationContext.
	 * @param in an Java bean to be serialized.
	 * @param out an XMLStreamWriter to write the payload.
	 * @throws  SerializationException Exception when serialization fails,
	 * @throws  SerializationOutputException Exception when serialization fails to write 
	 *				to the writer, 
	 * @throws  BindingSetupException Exception when failed to set up the serializer, and
	 * @throws  TypeConversionAdapterCreationException Exception when TypeConversionAdapter
	 *				creation fails.
	 */
	public void serialize(
			ISerializationContext ctx, 
			Object in, 
			XMLStreamWriter out) 
		throws 	SerializationException, 
				SerializationOutputException, 
				BindingSetupException, 
				TypeConversionAdapterCreationException;


	/**
	 * @return the Class object of the top level java type this custom serializer is used for. 
	 * null if it is a generic serializer.
	 */
	public Class getBoundType();

}
