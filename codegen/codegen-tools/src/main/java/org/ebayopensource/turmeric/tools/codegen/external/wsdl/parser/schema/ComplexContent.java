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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class to represent a &lt;complexContent&gt; element in a schema
 * 
 * @author Owen Burroughs <owenb@apache.org>
 */
public class ComplexContent implements Serializable {
	
	static final long serialVersionUID = 1L;
		
	private Restriction restriction = null;
	private Extension extention = null;	

	/**
	 * Constructor
	 * @param el The dom element for this complexContent
	 */	
	ComplexContent(Element el, String tns) {
		NodeList children = el.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element subEl = (Element) child;
				String elType = subEl.getLocalName();
				if (elType.equals("restriction")) {
					restriction = new Restriction(subEl, tns);
					break;
				} else if (elType.equals("extension")) {
					extention = new Extension(subEl,tns);
					break;
				}
			}			
		}	
	}
	
	/**
	 * Get the restriction element for this complexContent
	 * @return A Restriction object representing the restriction
	 */
	public Restriction getRestriction() {
		return restriction;
	}

	/**
	 * Get the extension element for this complexContent
	 * @return An Extension object representing the restriction
	 */	
	public Extension getExtension() {
		return extention;
	}	
}
