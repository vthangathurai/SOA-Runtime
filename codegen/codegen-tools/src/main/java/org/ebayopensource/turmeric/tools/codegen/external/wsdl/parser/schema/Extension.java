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
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class to represent an &lt;extension&gt; element in a schema
 * 
 * @author Owen Burroughs <owenb@apache.org>
 * @author arajmony
 */
public class Extension implements Serializable {
	
	static final long serialVersionUID = 1L;	
	
    QName base = null;
    ArrayList attributes = new ArrayList();
    ArrayList sequenceElements = new ArrayList();
	
	/**
	 * Constructor
	 * @param el The dom element for this extension
	 */	
	Extension(Element el,String tns) {
	    base = SchemaType.getAttributeQName(el, "base");
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element subEl = (Element) child;
                String elType = subEl.getLocalName();
                if (elType.equals("attribute")) {
                    attributes.add(new Attribute(subEl, tns));
                } else if (elType.equals("sequence")) {
                    parseSequenceElements(subEl, tns);
                }
            }
        }
	}
	
	/**
	 * Get the "base" attribute for this extension
	 * @return The "base" attribute
	 */
    public QName getBase() {
        return base;
    }

	/**
	 * Get all the &lt;attribute&gt; elements within this extension
	 * @return The &lt;attribute&gt; elements
	 */
    @SuppressWarnings("unchecked")
	public Attribute[] getAttributes() {
        return (Attribute[]) attributes.toArray(
            new Attribute[attributes.size()]);
    }

	/**
	 * Get all the &lt;element&gt; elements within a sequence within this extension
	 * @return The &lt;element&gt; elements within the sequnce
	 */
    @SuppressWarnings("unchecked")
	public SequenceElement[] getSequenceElements() {
        return (SequenceElement[]) sequenceElements.toArray(
            new SequenceElement[sequenceElements.size()]);
    }

    @SuppressWarnings("unchecked")
	private void parseSequenceElements(Element el, String tns) {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element subEl = (Element) child;
                String elType = subEl.getLocalName();
                if (elType.equals("element")) {
                    sequenceElements.add(new SequenceElement(subEl, tns));
                }
            }
        }
    }
}
