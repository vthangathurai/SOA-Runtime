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
package org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode;

import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;


/**
 * @author wdeng
 *
 */
public interface ObjectNodeBuilder {
	/**
	 * Returns the root object node.
	 * 
	 * @return
	 * @throws XMLStreamException
	 */
	public ObjectNode getObjectNode() throws XMLStreamException;
	
	/**
	 * 
	 * @return the current status of whether node building is allowed.
	 * 
	 */
	public boolean allowNodeBuilding();
	
	/**
	 * Signals the stopping of node building.  
	 *
	 */
	public void stopNodeBuilding();
}
