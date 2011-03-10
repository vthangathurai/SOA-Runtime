/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.DiffBasedSnapshotFileLogger;
import org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.SnapshotFileLogger;
import org.ebayopensource.turmeric.runtime.sif.impl.handlers.MessageContextHandler;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.ClientLoggingHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.handlers.G11nHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.NumericVersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerLoggingHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.protocolprocessor.soap.ServerSOAPProtocolProcessor;
import org.w3c.dom.Document;

public class CodeGenConfigUtil {

	private static Map<String, String> classPackageMap;

	static {
		classPackageMap = new HashMap<String, String>();

		/* globalclientconfig */
		classPackageMap.put("DiffBasedSnapshotFileLogger",
				DiffBasedSnapshotFileLogger.class.getName());
		classPackageMap.put("SnapshotFileLogger",
				SnapshotFileLogger.class.getName());
		classPackageMap.put("DiffBasedSnapshotFileLogger",
				DiffBasedSnapshotFileLogger.class.getName());

		/* clientgroupconfig */
		classPackageMap.put("MessageContextHandler",
				MessageContextHandler.class.getName());
		classPackageMap.put("ClientLoggingHandler",
				ClientLoggingHandler.class.getName());

		/* globalserviceconfig */
		classPackageMap.put("DiffBasedSnapshotFileLogger",
				DiffBasedSnapshotFileLogger.class.getName());
		classPackageMap.put("SnapshotFileLogger",
				SnapshotFileLogger.class.getName());

		/* servicegroupconfig */
		classPackageMap.put("NumericVersionCheckHandler",
				NumericVersionCheckHandler.class.getName());
		classPackageMap.put("G11nHandler", G11nHandler.class.getName());
		classPackageMap.put("ServerLoggingHandler",
				ServerLoggingHandler.class.getName());
		classPackageMap.put("ServerSOAPProtocolProcessor",
				ServerSOAPProtocolProcessor.class.getName());

	}

	public static String addPackageDetailsToTemplateClasses(
			String templateContent) {
		String newTemplateContent = new String(templateContent);

		java.util.Iterator mapIterator = classPackageMap.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) mapIterator
					.next();
			String className = "@@" + entry.getKey() + "@@";
			if (newTemplateContent.contains(className))
				newTemplateContent = newTemplateContent.replaceAll(className,
						entry.getValue());
		}

		return newTemplateContent;
	}

	public static Document parseDOMConfigFile(String folderPath,
			String configFileName) throws Exception {
		File clientConfigFile = new File(folderPath, configFileName);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(clientConfigFile);
	}

	public static void saveDOMDocumentToFile(Document result,
			String folderPath, String configFileName) throws Exception {
		Writer fileWriter = CodeGenUtil.getFileWriter(folderPath,
				configFileName);
		TransformerFactory tranFactory = TransformerFactory.newInstance();
		Transformer aTransformer = tranFactory.newTransformer();
		Source src = new DOMSource(result);
		Result dest = new StreamResult(fileWriter);

		aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
		aTransformer.transform(src, dest); // print the DOM tree

		CodeGenUtil.flushAndCloseQuietly(fileWriter);
	}

	/**
	 * This methods replaces the template contents with the target string for
	 * searchString. If the target string is null, the it uses
	 * 'searchStringForNull' as the search string.
	 * 
	 * @param templateContents
	 * @param searchString
	 * @param targetString
	 * @param searchStringForNull
	 * @return
	 */
	public static String replaceTemplate(String templateContents,
			String searchString, String targetString, String searchStringForNull) {
		if (targetString == null) {
			return replaceTemplate(templateContents, searchStringForNull, "");
		} else {
			return replaceTemplate(templateContents, searchString, targetString);
		}
	}

	/**
	 * This methods replaces the template contents with the target string for
	 * searchString.
	 * 
	 * @param templateContents
	 * @param searchString
	 * @param targetString
	 * @return
	 */
	public static String replaceTemplate(String templateContents,
			String searchString, String targetString) {
		return templateContents.replaceAll(searchString, targetString);
	}

}
