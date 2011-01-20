/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNodeWriter;

import com.ctc.wstx.api.WstxOutputProperties;

/**
 * Utility class to render an {@link ObjectNode} as XML, to a given Writer. 
 * <strong>Note: </strong>This utility should only be used when JAXB-based serialization
 * is not available. 
 * 
 * @author mpoplacenel
 */
public class ObjectNodeUtils {
	
	private static final ObjectNodeWriter OBJECT_NODE_WRITER = new ObjectNodeWriter();
	
	/**
	 * Default encoding for the XML declaration ("UTF-8").
	 */
	public static String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * Prints the given object node to the given {@link Writer}, with 
	 * {@link #DEFAULT_ENCODING default encoding} and no pretty-printing (i.e. compact mode).
	 *  
	 * @param objNode the node to be printed. 
	 * @param writer the writer to print to. 
	 * 
	 * @throws XMLStreamException for access problems. 
	 */
	public static void writeAsXML(ObjectNode objNode, Writer writer) throws XMLStreamException {
		writeAsXML(objNode, writer, false, DEFAULT_ENCODING);
	}

	/**
	 * Prints the given object node to the given {@link Writer}.
	 *  
	 * @param objNode the node to be printed. 
	 * @param writer the writer to print to. 
	 * @param encoding the char encoding to use.
	 * 
	 * @throws XMLStreamException for access problems. 
	 */
	public static void writeAsXML(ObjectNode objNode, Writer writer, String encoding) 
	throws XMLStreamException {
		writeAsXML(objNode, writer, false, "UTF-8");
	}

	/**
	 * Prints the given object node to the given {@link Writer}.
	 *  
	 * @param objNode the node to be printed. 
	 * @param writer the string builder to print to. 
	 * @param prettyPrint <code>true</code> for a human-friendly, pretty-printed version, 
	 * <code>false</code> for a more compact and economical one. 
	 * @param encoding the encoding to be specified in the XML document declaration. 
	 * @throws XMLStreamException for access problems. 
	 */
	public static void writeAsXML(ObjectNode objNode, Writer writer, boolean prettyPrint, String encoding) 
	throws XMLStreamException {
		XMLStreamWriter xsw = null;
		try {
			XMLOutputFactory xof = XMLOutputFactory.newInstance();
			xof.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
			xof.setProperty(WstxOutputProperties.P_OUTPUT_ESCAPE_CR, false);
			xsw = xof.createXMLStreamWriter(writer);
			if (prettyPrint) {
				xsw = new PrettyXMLStreamWriter(xsw);
			}
			xsw.setDefaultNamespace(objNode.getNodeName().getNamespaceURI());
			
			xsw.writeStartDocument(encoding, "1.0");
			OBJECT_NODE_WRITER.write(objNode, xsw);
			xsw.writeEndDocument();
		} finally {
			if (xsw != null) {
				xsw.flush();
				xsw.close();
			}
		}
	}

	/**
	 * Private constructor to prevent instantiation. 
	 */
	private ObjectNodeUtils() {
		// nothing in here
	}

}
