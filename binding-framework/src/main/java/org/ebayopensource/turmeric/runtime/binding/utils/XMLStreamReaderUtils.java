/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * XMLStreamReaderUtils class provides utility methods for XMLStreamReader.
 * @author gyue
 */
public final class XMLStreamReaderUtils {

	private XMLStreamReaderUtils() {
		// no instances
	}

	/**
	 * Consume the end element specified from the input XMLStream reader.
	 * @param reader a XMLStreamReader
	 * @param elementName The name of the element.
	 * @throws XMLStreamException Exception when reading from the XMLStreamReader fails.
	 * @throws XMLStreamReaderUtilsException  Exception when failed to consume the end element tag.
	 */
	public static void consumeEndElement(XMLStreamReader reader, String elementName) throws XMLStreamException, XMLStreamReaderUtilsException {
		
		// advance to the next end element
		if (!advanceToNextEndElement(reader)) {
			// Next end element not found. Throw error
			throw new XMLStreamReaderUtilsException("Next end element not found!");
		}

		// found an end element. Check if it's the element that we want to consume
		// NOTE: the comparsion has been changed to ignoreCase to bypass case sensitive problem
		if (reader.hasName()) {
			if (reader.isEndElement() && reader.getName().getLocalPart().equalsIgnoreCase(elementName)) {
				// expected element found. Consume!
				if (reader.hasNext()) {
					// consuming element
					reader.next();
				} else {
					// ERROR???
					//System.out.println("End of stream reached. Error??!?");
				}
				return;
			}

			throw new XMLStreamReaderUtilsException("Unexpected element encountered: [" + reader.getName().getLocalPart() + "]");
		}
	}

	/**
	 * Advance the reader to the next start element and return true. Return false if next end element is not found
	 * @param reader a XMLStreamReader
	 * @return True when successfully advanced the reader.
	 * @throws XMLStreamException Exception when reading from the XMLStreamReader fails.
	 */
	public static boolean advanceToNextStartElement(XMLStreamReader reader) throws XMLStreamException {
		while (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
			if (reader.hasNext()) {
				reader.next();
			} else {
				// reach the end of elements in reader. Not found.
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Advance the reader to the next end element and return true. Return false if next end element is not found
	 * @param reader a XMLStreamReader
	 * @return True when successfully advanced the reader.
	 * @throws XMLStreamException Exception when reading from the XMLStreamReader fails.
	 */
	public static boolean advanceToNextEndElement(XMLStreamReader reader) throws XMLStreamException {
		while (reader.getEventType() != XMLStreamConstants.END_ELEMENT) {
			if (reader.hasNext()) {
				reader.next();
			} else {
				// reach the end of elements in reader. Not found.
				return false;
			}
		}
		return true;
	}	
	
	/**
	 * Advance the reader to the next element after start element and return true. 
	 * Return false if next start element is not found
	 * @param reader a XMLStreamReader
	 * @param startElement Name of the start element.
	 * @return True when successfully advanced the reader.
	 * @throws XMLStreamException Exception when reading from the XMLStreamReader fails.
	 */
	public static boolean advanceToAfterStartElement(XMLStreamReader reader, String startElement) throws XMLStreamException {
		while (!(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
					reader.hasName() && 
					reader.getLocalName().equalsIgnoreCase(startElement))) {
			//String name = (reader.hasName()? reader.getLocalName() : "");
			if (reader.hasNext()) {
					reader.next();
			} else {
				// reach the end of elements in reader. Not found.
				return false;
			}
		}
		// found the startElement. Consume that start element also
		if (reader.hasNext()) {
			reader.next();
			return true;
		}

		return false;
	}
	
	private static final String[] XML_EVENT_NAMES = new String[] {"UnknownEvent", "StartElement", "EndElement", "ProcessingInstruction", "Characters", "Comment", "Space", "StartDocument", "EndDocument", "entityReference",
		"Attribute", "DTD", "CDATA","NameSpace", "NotationDeclaration", "EntityDeclaration"};
	
	/**
	 * This method returns the event name given the integer representation of event. 
	 * Return UnknownEvent if the element is not found.
	 *
	 * @param event - integer representation of event
	 * @return String - name of the event 
	 */
	public static String xmlStreamReaderEventName(int event) {
		if (event > 0 && event < XML_EVENT_NAMES.length) {
			return XML_EVENT_NAMES[event];
		}
		return XML_EVENT_NAMES[0];
	}
}
