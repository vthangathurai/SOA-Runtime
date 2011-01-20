/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.monitoring.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


public abstract class RenderView {

	private static final String COL1 = "Snapshot Time";

	private static final String COL2 = "Service Name";

	private static final String COL3 = "Operation Name";

	private static final String COL4 = "Metric";

	private static final String COL5 = "Use Case";

	private static final String COL6 = "Client Data Center";

	private static final String COL7 = "Server Data Center";

	private static final String COL8 = "Value Part 1 (Count)";

	private static final String COL9 = "Value Part 2";

	private static final String COL10 = "Average";

	protected static final String[] HEADER = { COL1, COL2, COL3, COL4, COL5,
			COL6, COL7, COL8, COL9, COL10 };

	private static final String LOG_FILE_PATH = "logFilePath";

	protected String m_outputFileName;

	protected String m_inputFileName;

	protected String m_header;

	protected String m_snapshotId;

	private static final String XSL_URL = "<?xml-stylesheet type=\"text/xsl\" href=\"SOAMetricViewer.xsl\"?>";

	public void renderXml(Collection<List<String>> records)
			throws FactoryConfigurationError, XMLStreamException, IOException {
		XMLStreamWriter xsw = null;
		try {
			XMLOutputFactory xof = XMLOutputFactory.newInstance();

			xsw = xof.createXMLStreamWriter(new FileWriter(m_outputFileName));
			// xtw.writeComment("create links for each snapshot");
			xsw.writeStartDocument("utf-8", "1.0");
			xsw.writeDTD(XSL_URL);
			xsw.writeStartElement("Component");
			xsw.writeAttribute("name", "SOA Metric Viewer");
			xsw.writeAttribute("alias", "SOAMetricViewer");
			xsw.writeAttribute("status", "Successs");
			xsw.writeAttribute(LOG_FILE_PATH, m_inputFileName + ", "
					+ m_header + ", Snapshot=" + m_snapshotId);

			renderProperties(records, xsw);

			xsw.writeEndElement(); // component
			xsw.writeEndDocument();
		} finally {
			CodeGenUtil.closeQuietly(xsw);
		}
	}

	protected static void writePropertyElement(XMLStreamWriter xtw,
			String name, String value) throws XMLStreamException {
		xtw.writeStartElement("Property");
		xtw.writeAttribute("name", name);
		xtw.writeCData(value);
		xtw.writeEndElement();
	}

	protected abstract void renderProperties(Collection<List<String>> records,
			XMLStreamWriter xsw) throws XMLStreamException;
}
