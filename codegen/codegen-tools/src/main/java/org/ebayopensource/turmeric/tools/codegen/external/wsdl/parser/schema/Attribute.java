/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema;

import java.io.Serializable;
import java.util.Hashtable;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * A class to represent an &lt;attribute&gt; element in a schema
 * 
 * @author Owen Burroughs <owenb@apache.org>
 */
public class Attribute implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	Hashtable attributes = new Hashtable();

	/**
	 * Constructor
	 * @param el The dom element for this attribute
	 */	
	Attribute(Element el, String tns) {
		SchemaType.getAllAttributes(el, null, attributes);
	}

	/**
	 * Get the value of a specified attribute on this element
	 * @param The name of the attribute
	 * @return The value of the attribute or null if the attribute does not exist
	 */	
	QName getXMLAttribute(String name) {
        return (QName) attributes.get(new QName(name));
    }

	/**
	 * Get the value of a specified attribute on this element when the attribute name is
	 * a QName
	 * @param The name of the attribute
	 * @return The value of the attribute or null if the attribute does not exist
	 */		
	QName getXMLAttribute(QName name) {
        return (QName) attributes.get(name);
    }
}
