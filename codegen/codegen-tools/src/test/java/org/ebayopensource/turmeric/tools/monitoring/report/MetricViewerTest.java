/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.monitoring.report;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.junit.Test;


public class MetricViewerTest extends AbstractCodegenTestCase {
	@Test
	public void metricViewer() throws Exception {
	    String logFilename = "DiffBasedSOAMetrics-server.log";
	    String logPath = "org/ebayopensource/turmeric/test/tools/monitoring/report/" + logFilename;
	    
	    testingdir.ensureEmpty();
	    File destFile = testingdir.getFile(logPath);
	    
	    TestResourceUtil.copyResource(logPath, destFile);
	    
		SOAMetricViewer viewer = new SOAMetricViewer(destFile.getAbsolutePath());

		viewer.init();

		String snapShotTimeStamp = "08/23/07 15:20:11";

		viewer.renderSummary();

		viewer.renderSnapshot(snapShotTimeStamp);

		checkFileCreated(destFile.getAbsolutePath());
		
	}

	private void checkFileCreated(String inputFile) {
		String pathSeperator = File.separator;
		File file = new File(inputFile);
		String absPath = file.getAbsolutePath();
		String path = file.getAbsolutePath().substring(0,
				absPath.lastIndexOf(pathSeperator));

		String outFilePath = path + pathSeperator
				+ SOAMetricViewer.OUTPUT_XML_FILE;
		File outputFile = new File(outFilePath);
		assertTrue(outputFile.exists());
	}
}
