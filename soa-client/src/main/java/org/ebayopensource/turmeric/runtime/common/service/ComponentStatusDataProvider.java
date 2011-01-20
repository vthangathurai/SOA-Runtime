/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.service;

import java.util.Map;

import com.ebay.kernel.util.xml.IXmlStreamWriter;

/**
 * Allows vairous custom parts of the system to define their own ServiceBrowser XML.
 * 
 * Handlers, Pipeline, ProtocolProcessor and other custom implementations should implement
 * this interface to allow ServiceBrowser component status page to emit custom data
 * 
 * @author ichernyshev
 */
public interface ComponentStatusDataProvider {

	/**
	 * @param xmlWriter A IXmlStremWriter to write the component status xml payload.
	 * @param props A property map holding the content to be written as xml payload.
	 */
	public void writeStatusXml(IXmlStreamWriter xmlWriter, Map<String,String> props);
}
