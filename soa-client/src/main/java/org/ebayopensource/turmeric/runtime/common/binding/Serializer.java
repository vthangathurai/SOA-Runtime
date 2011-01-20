/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.binding;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;


/**
 * Serializer is responsible for managing the process of 
 * serializing Java content trees back into a encoded data. 
 * 
 * @author smalladi
 * @author wdeng
 */
public interface Serializer {
	/**
	 * It takes a java content tree, marshall it into an encoded data 
	 * and output it to the given output stream. It throws 
	 * SerializationException when there is an error during the process.
	 * 
	 * @param msg the outbound message
	 * @param in the object to be serialized
	 * @param xmlName the top level element name of the object.
	 * @param clazz the class of the value (source) type
	 * @param out Output stream to write the data.
	 * @throws ServiceException thrown when there is error
	 */
	public void serialize(OutboundMessage msg, Object in, QName xmlName, Class<?> clazz,
		XMLStreamWriter out) throws ServiceException;


	/**
	 * @deprecated 
	 * 
	 * For custom serializer.  This method returns the Class object of 
	 * the top level java type this serializer is used for. 
	 * 
	 * @return The Class object of 
	 * the top level java type this serializer is used for.
	 */
	public Class getBoundType();

}
