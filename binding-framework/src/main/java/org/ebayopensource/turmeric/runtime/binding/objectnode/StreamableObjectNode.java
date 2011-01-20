/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.binding.objectnode;

import javax.xml.stream.XMLStreamException;


/**
 * This interface is similar to the interface ObjectNode, except that additionally 
 * it provides a method to access children of Streamable object node in sequence. 
 * 
 * @author wdeng
 *
 */
public interface StreamableObjectNode extends ObjectNode {
	
	/**
	 * Returns the next child node of a Streamable object node.
	 * @return the next child.
	 * @throws XMLStreamException Exception when fails to read from stream.
	 */
	public StreamableObjectNode nextChild() throws XMLStreamException;
}
