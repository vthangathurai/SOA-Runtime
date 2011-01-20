/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Utility for adjusting pre-written XML content
 */
public class XmlAdjuster {

	/**
	 * Correct the xml entries as specified.
	 * 
	 * @param xmlFile
	 *            the file to correct.
	 * @param entries
	 *            the map of entries to correct. (Map.key is the xpath Map.value is the value to use)
	 * @throws IOException
	 * @throws JDOMException
	 * @throws JaxenException
	 */
	public static void correct(File xmlFile, Map<String, String> namespaceMap,
			Map<String, String> entries) throws IOException, JDOMException,
			JaxenException {
		Document doc = readXml(xmlFile);

		for (Map.Entry<String, String> xpathEntry : entries.entrySet()) {
			XPath expression = new JDOMXPath(xpathEntry.getKey());
			if (namespaceMap != null) {
				for (Map.Entry<String, String> ns : namespaceMap.entrySet()) {
					expression.addNamespace(ns.getKey(), ns.getValue());
				}
			}

			@SuppressWarnings("unchecked")
			List<Element> elements = expression.selectNodes(doc);
			for (Element elem : elements) {
				elem.setText(xpathEntry.getValue());
			}
		}

		writeXml(xmlFile, doc);
	}

	public static Document readXml(File xmlFile) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder(false);
		return builder.build(xmlFile);
	}

	public static void writeXml(File xmlFile, Document doc) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(xmlFile);
			XMLOutputter serializer = new XMLOutputter();
			serializer.getFormat().setIndent("  ");
			serializer.getFormat().setLineSeparator(SystemUtils.LINE_SEPARATOR);
			serializer.output(doc, writer);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
