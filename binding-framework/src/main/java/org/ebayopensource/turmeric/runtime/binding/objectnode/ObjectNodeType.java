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

/**
 * This Enum represents the type of the node in the message object tree.
 * @author smalladi
 *
 */
public enum ObjectNodeType {
	/**
	 * Indicates an object node representing an XML infoset (data corresponding abstractly to the XML representation).
	 */
	XML, 
	/**
	 * Indicates a Java content object, essentially deserialized data. 
	 */
	JAVA
}
