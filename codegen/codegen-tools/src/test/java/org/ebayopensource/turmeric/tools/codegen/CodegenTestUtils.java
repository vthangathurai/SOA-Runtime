/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.junit.Assert;

/**
 * @author arajmony
 */
public class CodegenTestUtils {
	/**
	 * Assertion that tests a generated file contents against a template file containing a snippet of generated code
	 * that is tested for in the actual file contents.
	 * 
	 * @param generatedFile
	 * @param targetArtifactFile
	 * @param svcNameFromWSDL
	 * @param serviceNamethruCodegen
	 * @param operationName
	 * @throws IOException
	 */
	public static void assertGeneratedContent(File generatedFile,
			File targetArtifactFile, String svcNameFromWSDL,
			String serviceNamethruCodegen, String operationName)
			throws IOException {
		PathAssert.assertFileExists(targetArtifactFile);
		PathAssert.assertFileExists(generatedFile);

		Map<String, String> filters = new HashMap<String, String>();
		filters.put("@@SVC_ADMIN_NAME@@", svcNameFromWSDL);
		filters.put("@@SVC_NAME@@", svcNameFromWSDL);
		filters.put("@@OPERATION_NAME@@", operationName);

		String expectedContents = readTargetWithFiltering(targetArtifactFile,
				filters);

		Assert.assertThat("File Content", notNullValue());

		Assert.assertThat("File Content.length: " + expectedContents,
				expectedContents.length(), greaterThan(0));

		String actualContents = readFileToStringTrimmed(generatedFile);
		Assert.assertThat(actualContents, containsString(expectedContents));
	}
	
	private static String readFileToStringTrimmed(File file) throws IOException {
		StringBuilder contents = new StringBuilder();

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String tmpStr = null;

			while ((tmpStr = br.readLine()) != null) {
				if (tmpStr.startsWith("#")) {
					// a comment: skip
					continue;
				}

				if (StringUtils.isBlank(tmpStr)) {
					// empty line: skip
					continue;
				}

				contents.append(tmpStr.trim()).append(
						SystemUtils.LINE_SEPARATOR);
			}
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(fr);
		}

		return contents.toString();
	}

	private static String readTargetWithFiltering(File file,
			Map<String, String> filters) throws IOException {
		StringBuilder contents = new StringBuilder();

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String tmpStr = null;

			while ((tmpStr = br.readLine()) != null) {
				if (tmpStr.startsWith("#")) {
					// a comment: skip
					continue;
				}

				for (String key : filters.keySet()) {
					if (tmpStr.contains(key)) {
						tmpStr = tmpStr.replace(key, filters.get(key));
					}
				}

				if (StringUtils.isBlank(tmpStr)) {
					// empty line: skip
					continue;
				}

				contents.append(tmpStr.trim()).append(
						SystemUtils.LINE_SEPARATOR);
			}
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(fr);
		}

		return contents.toString();
	}
}
