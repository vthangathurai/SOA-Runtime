/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.binding;

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;


/**
 * Deserializer is responsible for managing the process of unmarshalling 
 * encoded data of a particular encoding, for example, schema xml,  
 * name-value, or JSON, into a Java content tree.  It can also provide 
 * data encoding validation.
 *
 * @author smalladi
 * @author wdeng
 */
public interface Deserializer {
	
	/**
	 * Initiate deserialization of the specified inbound message value.
	 * @param msg the inbound message
	 * @param clazz the bound (target) type
	 * @return an object of the specified bound type
	 * @throws ServiceException Exception when deserialization fails.
	 */
	public Object deserialize(InboundMessage msg, Class<?> clazz) throws ServiceException;
	
	/**
	 * Initiate deserialization of the specified inbound message value.
	 * @param msg the inbound message
	 * @param clazz the bound (target) type
	 * @param reader the XMLStreamReader to read in the payload.
	 * @return an object of the specified bound type
	 * @throws ServiceException Exception when deserialization fails.
	 */
	public Object deserialize(InboundMessage msg, Class<?> clazz, XMLStreamReader reader) throws ServiceException;

	/**
	 * @deprecated 
	 * 
	 * For custom deserializer.  This method returns the Class object of 
	 * the top level java type this deserializer creates. 
	 * 
	 * @return The Class object of 
	 *         the top level java type this deserializer creates.
	 */
	public Class getBoundType();
}
