/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.objectnode;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Utility class to render an {@link ObjectNode} to a previously-created {@link XMLStreamWriter}.
 * The class is stateless and therefore thread-safe (provided the method arguments are thread-safe).  
 * <strong>Note: </strong>This utility should only be used when JAXB-based serialization
 * is not available. 
 * 
 * @author mpoplacenel
 */
public final class ObjectNodeWriter {
	
	private static final String INDENT = "    ";
	private static final String NL = System.getProperty("line.separator");

	/**
	 * Prints the given object node to the given {@link XMLStreamWriter}.
	 *  
	 * @param objNode the node to be printed. 
	 * @param xmlStreamWriter the XML stream writer to write to.  
	 * 
	 * @throws XMLStreamException for access problems. 
	 */
	public void write(ObjectNode objNode, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
		write(objNode, xmlStreamWriter, false);
	}

	/**
	 * Prints the given object node to the given {@link XMLStreamWriter}.
	 *  
	 * @param objNode the node to be printed. 
	 * @param xmlStreamWriter the XML stream writer to write to.  
	 * @param prettyPrint <code>true</code> for a human-friendly, pretty-printed version, 
	 * <code>false</code> for a more compact and economical one. 
	 * 
	 * @throws XMLStreamException for access problems. 
	 */
	public void write(ObjectNode objNode, XMLStreamWriter xmlStreamWriter, boolean prettyPrint) 
	throws XMLStreamException {
		writeAsXMLInternal(0, objNode, xmlStreamWriter, false);
	}

	/**
	 * The method writes an XML node including its attributes and child nodes.
	 * 
	 * @param level	The nested level 
	 * @param objNode  The ObjectNode to write
	 * @param xsw The XMLStreamWriter to output the payload.
	 * @param prettyPrint formats the payload if it is true.
	 * @throws XMLStreamException an XMLStream exception
	 */
	protected final void writeAsXMLInternal(int level, ObjectNode objNode, XMLStreamWriter xsw, boolean prettyPrint) 
	throws XMLStreamException {
		final QName nodeQName = objNode.getNodeName();
		xsw.writeStartElement(nodeQName.getNamespaceURI(), nodeQName.getLocalPart());
		writeAttributes(objNode, xsw);
		
		writeChildren(level, objNode, xsw, prettyPrint);
		xsw.writeEndElement();
	}

	/**
	 * Write the children of the given object node. 
	 * @param level the level the node is at. 
	 * @param objNode the object node.
	 * @param xsw The XMLStreamWriter to output the payload.
	 * @param prettyPrint formats the payload if it is true.
	 * @throws XMLStreamException an XMLStream exception
	 */
	protected void writeChildren(int level, ObjectNode objNode,
			XMLStreamWriter xsw, boolean prettyPrint) throws XMLStreamException {
		List<ObjectNode> childNodes = objNode.getChildNodes();
		if (childNodes != null && childNodes.size() > 0) {
			indent(prettyPrint, level + 1, xsw);
			int i = 0;
	    	for (ObjectNode childNode : childNodes) {
	    		if (i++ > 0) {
	    			indent(prettyPrint, level + 1, xsw);
	    		}
	    		writeAsXMLInternal(level + 1, childNode, xsw, prettyPrint);
			}
			indent(prettyPrint, level, xsw);
		} else {
			final Object nodeValue = objNode.getNodeValue() == null ? "" : objNode.getNodeValue();
			xsw.writeCharacters(nodeValue.toString());
		}
	}

	/**
	 * Writes all the attributes of the given ObjectNode.
	 * 
	 * @param objNode the ObjectNode of an attribute
	 * @param xsw The XMLStreamWriter to output the payload.
	 * @throws XMLStreamException an XMLStream exception
	 */
	protected void writeAttributes(ObjectNode objNode, XMLStreamWriter xsw)
			throws XMLStreamException {
		List<ObjectNode> attributes = objNode.getAttributes();
		if (attributes != null && attributes.size() > 0) {
			for (ObjectNode attrNode : attributes) {
				writeAttributeNode(attrNode, xsw);
			}
		}
	}

	/**
	 * Writes the given attribute node.
	 * @param attrNode ObjectNode for an attribute.
	 * @param xsw an XMLStreamWriter to output the payload.
	 * @throws XMLStreamException XMLStream exception.
	 */
	protected void writeAttributeNode(ObjectNode attrNode, XMLStreamWriter xsw)
			throws XMLStreamException {
		QName attrQName = attrNode.getNodeName();
		Object attrValue = attrNode.getNodeValue() == null ? "" : attrNode.getNodeValue();
		xsw.writeAttribute(attrQName.getNamespaceURI(), attrQName.getLocalPart(), attrValue.toString());
	}
	
	/**
	 * Goes to next line and advance to the indent location.
	 * @param prettyPrint  format the output if true.
	 * @param level  The level of indentation.
	 * @param xsw an XMLStreamWriter to output the payload.
	 * @throws XMLStreamException XMLStream exception.
	 */
	protected void indent(boolean prettyPrint, int level, XMLStreamWriter xsw) throws XMLStreamException {
		if (!prettyPrint) return;
		xsw.writeCharacters(NL);
		doIndent(level, xsw);
		
	}

	
	/**
	 * Advances to the indent location.
	 * @param level  The level of indentation.
	 * @param xsw an XMLStreamWriter to output the payload.
	 * @throws XMLStreamException XMLStream exception.
	 */
	protected void doIndent(int level, XMLStreamWriter xsw) throws XMLStreamException {
		for (int j = 0; j < level; j++) {
			xsw.writeCharacters(INDENT);
		}
	}

}
