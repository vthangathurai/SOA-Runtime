/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.schema;

import java.util.Collection;

import javax.xml.namespace.QName;

/**
 * DataElementSchema is a runtime data structure to capture schema information defined in 
 * schema sections of a wsdl file. The structure provide necessary information 
 * for XMLStreamWriters to produce the right payload. 
 * 
 * For example, JSON format uses JSON array to represent multiple element of the same type.
 * JSON array are delimited by "[" and "]".  At the time, the writer gets a writeElementStart
 * call for the first element of and array.  the stream writer doesn't have enough context
 * information to determine whether this element is a single element or the first element of
 * an array.  
 * 
 * DataElementSchema provides addition information that it captured form wsdl.  In this case
 * the maxOccurs of the element.  Based on maxOccurs > 1 or not, the stream writer will be
 * able to determine whether it should add a '[' or not.
 * 
 * @author ichernyshev
 */
public interface DataElementSchema {

	/**
	 * This method returns name of the Element.
	 * @return name of the data Element.
	 */
	public QName getElementName();

	/**
	 * Get the Max Occurance Count.
	 * @return count of max occurances.
	 */
	public int getMaxOccurs();

	/**
	 * This method returns true if the data element has Children.
	 * @return true / false based on if the data element has Children.
	 */
	public boolean hasChildren();

	/**
	 * This method returns List of children name of element.
	 * @return List of Children name.
	 */
	public Collection<QName> getChildrenNames();

	/**
	 * Get the Child of the data element.
	 * @param name - Qualified Name
	 * @return instance of Data element schema.
	 */
	public DataElementSchema getChild(QName name);

	/**
	 * Get the Child of the data element.
	 * @param namespaceURI - NameSpace URI
	 * @param localName - Local Name
	 * @return instance of Data element schema.
	 */
	public DataElementSchema getChild(String namespaceURI, String localName);
}
