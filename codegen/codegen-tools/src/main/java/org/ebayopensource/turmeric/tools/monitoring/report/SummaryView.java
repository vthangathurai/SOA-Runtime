/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.monitoring.report;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SummaryView extends RenderView {

	public SummaryView(String outputFileName, String inputFileName, String header, String snapshotId) {
		m_outputFileName = outputFileName;
		m_inputFileName = inputFileName;
		m_header = header;
		m_snapshotId = snapshotId;
	}

	protected void renderProperties(Collection<List<String>> summaries,
			XMLStreamWriter xsw) throws XMLStreamException {
		for (List<String> row : summaries) {
			int i = 0;
			try {
				xsw.writeStartElement("Properties");
				String average = "0.0 sec";
				long calls = 0;
				for (String token : row) {
					switch (i) {
					case 7:
						try {
							calls = Long.valueOf(token);
						} catch (NumberFormatException e) {
						}
						break;
					case 8:
						if (calls > 0) {
							try {
								double valuePart2 = Double
										.valueOf(token.trim()) / 1000000000.0;
								NumberFormat formatter = new DecimalFormat(
										"#.000000");
								token = formatter.format(valuePart2) + " sec";

								double averageTmp = valuePart2 / calls;
								average = formatter.format(averageTmp) + " sec";
							} catch (NumberFormatException e) {
							}
						}
						break;
					default:
					}

					writePropertyElement(xsw, HEADER[i], token);
					i++;
					if (i >= HEADER.length) {
						break;
					}
				}
				writePropertyElement(xsw, HEADER[i], String.valueOf(average));
			} finally {
				xsw.writeEndElement(); // properties
			}
		}
	}
}
