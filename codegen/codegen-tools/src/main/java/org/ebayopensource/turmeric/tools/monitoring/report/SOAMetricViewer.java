/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.monitoring.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


public class SOAMetricViewer {

	public static final String OUTPUT_XML_FILE = ".SOAMetricViewer.xml";

	public static final String XSL_TEMPLATE = "SOAMetricViewer.xsl";

	private static final String USAGE_TEXT = "Usage: java -classpath <...> SOAMetricViewer -i <SOAMetricFileNameWithPath> -x <internetBrowserExecutableWithPath>";

	private static final String EXAMPLE_TEXT = "Example: java -classpath . -i \"d:/ws/SOAViewer/DiffBasedSOAMetrics.log\" -x \"C:/Program Files/Internet Explorer/iexplore.exe\" ";

	private String s_logFileName;

	private String s_outFileName;

	private String s_InternetBrowerPath;

	private String s_header = "";

	private static void printUsage() {
		System.out.println(USAGE_TEXT); // KEEPME
		System.out.println(EXAMPLE_TEXT); // KEEPME
	}

	private String m_snapshotId;

	private Map<String, Integer> m_timeStamps = new LinkedHashMap<String, Integer>();

	private Map<Integer, String> m_lookupByIndex = new LinkedHashMap<Integer, String>();

	private Map<String, List<String>> m_metricSummary = new LinkedHashMap<String, List<String>>();

	public SOAMetricViewer(String inputLogFile, String IEPath) {
		s_logFileName = inputLogFile;
		s_InternetBrowerPath = IEPath.trim();
	}

	public SOAMetricViewer(String inputLogFile) {
		this(inputLogFile, "");
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		try {
			if (args.length > 3 && args[0].equals("-i")
					&& (args[2].equals("-x")) && checkArguments(args[1])) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						SOAMetricViewer viewer = new SOAMetricViewer(args[1],
								args[3]);
						try {
							viewer.init();
							viewer.getSnapshotSelection();
						} catch (Exception e) {
							printUsage();
							return;
						}
					}
				});
				t.start();
				t.join();
			} else {
				printUsage();
				return;
			}
		} catch (Exception e) {
			printUsage();
			return;
		}
	}

	/**
	 * Returns true if validated.
	 *
	 */
	private static boolean checkArguments(String logFileName) throws Exception {
		File file = new File(logFileName);
		if (!file.exists()) {
			Utils.printMessage(
					"Unable to read SOA Log file. Please check the path=",
					logFileName);
			printUsage();
			return false;

		}
		return fileValidation(file);
	}
	
	public void init() throws Exception {
		setOutputFilename(new File(s_logFileName));
		processLogFile();
	}

	private void setOutputFilename(File file) {
		String pathSeperator = File.separator;
		String absPath = file.getAbsolutePath();

		String path = file.getAbsolutePath().substring(0,
				absPath.lastIndexOf(pathSeperator));

		s_outFileName = path + pathSeperator + OUTPUT_XML_FILE;
	}
	/**
	 *
	 * @param file
	 * @return true when the file is validate.
	 * 
	 * @throws Exception
	 */
	private static boolean fileValidation(File file) throws Exception {
		String pathSeperator = File.separator;
		String absPath = file.getAbsolutePath();

		String path = file.getAbsolutePath().substring(0,
				absPath.lastIndexOf(pathSeperator));

		String xslTemplateCopy = path + pathSeperator + XSL_TEMPLATE;

		File xslFileCopy = new File(xslTemplateCopy);
		if (!xslFileCopy.exists()) {
			String xslTemplateOrig = SOAMetricViewer.class.getResource(XSL_TEMPLATE)
					.getPath();
			File xslFileOrig = new File(xslTemplateOrig);
			if (!xslFileOrig.exists()) {
				ClassLoader loader = ClassLoader.getSystemClassLoader();
				xslTemplateOrig = loader.getResource(XSL_TEMPLATE).getPath();
				xslFileOrig = new File(xslTemplateOrig);
				if (!xslFileOrig.exists()) {
					Utils.printMessage(
							"Error: XSL template missing. Please copy ",
							XSL_TEMPLATE, " file to the classpath "
									+ System.getProperty("java.class.path"));
					return false;
				}
			}
			Utils.copyFile(xslFileOrig, xslFileCopy);
		}
		return true;
	}

	private void getSnapshotSelection() throws Exception {
		if (m_timeStamps.size() < 1) {
			Utils.printMessage("No data found in logfile=", s_logFileName);
			return;
		}

		while (true) {
			displaySnapshotTimes();

			Utils.printMessage("\nPlease enter your choice [index or ALL]: ");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			Integer selectionId = null;
			try {
				m_snapshotId = br.readLine();

				if (m_snapshotId.equalsIgnoreCase("ALL")) {
					renderSummary();
				} else {
					selectionId = Integer.valueOf(m_snapshotId);

					String snapShotTimeStamp = m_lookupByIndex.get(selectionId);
					if (snapShotTimeStamp == null) {
						Utils.printMessage("Invalid selection=", m_snapshotId);
						continue;
					}

					renderSnapshot(snapShotTimeStamp);
				}
			} catch (IOException e) {
				printException(e);
				return;
			} catch (NumberFormatException e) {
				Utils.printMessage("Invalid selection");
				continue;
			}

			Utils.lanunchBrower(s_InternetBrowerPath, s_outFileName);
		}
	}

	private void displaySnapshotTimes() {
		try {
			Iterator<?> keyValuePairs = m_timeStamps.entrySet().iterator();
			Utils.printMessage("\n\n***********Snapshot choices***********");
			while (keyValuePairs.hasNext()) {
				for (int columns = 0; columns < 3 && keyValuePairs.hasNext(); columns++) {
					Map.Entry<?,?> entry = (Map.Entry<?,?>) keyValuePairs.next();
					if (entry == null) {
						return;
					}
					String timeStamp = (String) entry.getKey();
					Integer index = (Integer) entry.getValue();
					System.out.print(index + ".  " + timeStamp + "\t"); // KEEPME
				}
				System.out.println(""); // KEEPME
			}
		} catch (Exception e) {
			printException(e);
		}
	}

	private void processLogFile() {
		File file = new File(s_logFileName);
		file.getAbsolutePath();
		if (file.exists()) {
			BufferedReader inFile = null;
			try {
				inFile = new BufferedReader(new FileReader(file));
				s_header = inFile.readLine();
				String line = inFile.readLine();
				int lineNum = 1;
				while (line != null) {
					line = inFile.readLine();
					String timeStamp = null;
					if (line == null) {
						return;
					}
					if (!line.contains(";")) {
						Utils.printDebugMessage("Invalid line encountered="
								+ line);
						continue;
					}
					try {
						timeStamp = line.substring(0, line.indexOf(';'));
					} catch (StringIndexOutOfBoundsException e) {
						Utils.printDebugMessage("Invalid line encountered="
								+ line);
						continue;
					}
					Utils.printDebugMessage("TimeStamp=" + timeStamp);
					if (timeStamp != null && timeStamp.trim().length() > 0) {
						Integer index = m_timeStamps.get(timeStamp);
						if (index == null) {
							m_lookupByIndex.put(lineNum, timeStamp);
							m_timeStamps.put(timeStamp, lineNum++);
						}

						createSummary(line);
					}
				}
			} catch (Exception e) {
				printException(e);
			} finally {
				CodeGenUtil.closeQuietly(inFile);
			}
		} else {
			Utils.printMessage("File not found=", s_logFileName);
		}
	}

	private void createSummary(String record) {
		List<String> tokensList = Utils.tokenizeLine(record);

		String key = getSummaryKey(tokensList);
		List<String> cachsedSummary = m_metricSummary.get(key);
		if (cachsedSummary == null) {
			m_metricSummary.put(key, tokensList);
		} else {
			m_metricSummary.put(key, updateValues(cachsedSummary, tokensList));
		}
	}

	public List<String> updateValues(List<String> cachsedSummary,
			List<String> tokensList) {
		String[] cachsedArray = cachsedSummary.toArray(new String[8]);
		String[] tokensArray = tokensList.toArray(new String[8]);
		String[] result = cachsedSummary.toArray(new String[0]);
		result[7] = Utils.addLongs(cachsedArray[7], tokensArray[7]);
		if (cachsedArray.length > 8)
			result[8] = Utils.addBigDecimals(cachsedArray[8], tokensArray[8]);

		return Arrays.asList(result);
	}

	private String getSummaryKey(List<String> tokenList) {
		String[] tokens = tokenList.toArray(new String[0]);
		StringBuffer key = new StringBuffer();
		for (int i = 1; i < 4; i++) {

			key.append(tokens[i]);
		}
		Utils.printDebugMessage("key=" + key);
		return key.toString();
	}

	private List<String> getMatchingSnapshots(String matchingTimeStamp) {
		List<String> matchingLines = new ArrayList<String>();
		File file = new File(s_logFileName);
		if (file.exists()) {
			BufferedReader inFile = null;
			try {
				inFile = new BufferedReader(new FileReader(file));
				String line = inFile.readLine();
				while (line != null) {
					line = inFile.readLine();
					String timeStamp = null;
					if (line == null) {
						break;
					}
					if (!line.contains(";")) {
						Utils.printDebugMessage("Invalid line encountered="
								+ line);
						continue;
					}
					try {
						timeStamp = line.substring(0, line.indexOf(';'));
					} catch (StringIndexOutOfBoundsException e) {
						Utils.printDebugMessage("Invalid line encountered="
								+ line);
						continue;
					}
					Utils.printDebugMessage("TimeStamp=" + timeStamp);

					if (timeStamp != null && timeStamp.trim().length() > 0) {
						if (matchingTimeStamp.equals(timeStamp)) {
							matchingLines.add(line);
						} else if (matchingLines.size() > 0) {
							break;
						}
					}
				}
			} catch (Exception e) {
				printException(e);
			} finally {
				CodeGenUtil.closeQuietly(inFile);
			}
		} else {
			Utils.printMessage("File not found=" + s_logFileName);
		}

		return matchingLines;
	}

	public void renderSnapshot(String snapShotTimeStamp)
			throws XMLStreamException, IOException {
		List<String> snapshots = getMatchingSnapshots(snapShotTimeStamp);
		Collection<List<String>> envelope = new ArrayList<List<String>>();
		envelope.add(snapshots);
		RenderView view = new SnapshotView(s_outFileName, s_logFileName,
				s_header, m_snapshotId);
		view.renderXml(envelope);
	}

	public void renderSummary() throws XMLStreamException, IOException {
		Collection<List<String>> values = m_metricSummary.values();
		RenderView view = new SummaryView(s_outFileName, s_logFileName,
				s_header, m_snapshotId);
		view.renderXml(values);
	}

	private void printException(Exception e) {
		System.err.println(e.toString()); // KEEPME
	}
}
