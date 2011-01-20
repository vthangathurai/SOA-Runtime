/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;

import static org.junit.Assert.*;

/**
 * 
 */
public class LocalEncodingTest extends BaseCallTest {

	private static final EncodingTestCase[] TEST_STRINGS = new EncodingTestCase[20];

	private static final String[] DATA_FORMATS = new String[] {
			BindingConstants.PAYLOAD_JSON, BindingConstants.PAYLOAD_NV,
			BindingConstants.PAYLOAD_FAST_INFOSET, BindingConstants.PAYLOAD_XML };

	private static int NUM_TEST_CASES = 0;
	static {
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase("ASCII Only",
				"\u0041\u0042\u0043", "0x41,0x42,0x43");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"8 bit range of ISO 8859-1", "\u00C1\u00C2\u00C3",
				"0xC3,0x81,0xC3,0x82,0xC3,0x83");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"8 bit range of Cp1252 extension", "\u20AC\u2122\u0178",
				"0xE2,0x82,0xAC,0xE2,0x84,0xA2,0xC5,0xB8");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"8 bit range of ISO 8859-2", "\u0141\u015A\u0142",
				"0xC5,0x81,0xC5,0x9A,0xC5,0x82");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"8 bit range of Cp1250 extension", "\u0179\u0164\u0165",
				"0xC5,0xB9,0xC5,0xA4,0xC5,0xA5");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"3 byte range Chinese character", "\u4E00\u4E01\u4E1D",
				"0xE4,0xB8,0x80,0xE4,0xB8,0x81,0xE4,0xB8,0x9D");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"3 byte range Chinese character with byte values not in Cp1252",
				"\u4E10\u4E0D\u4E0F",
				"0xE4,0xB8,0x90,0xE4,0xB8,0x8D,0xE4,0xB8,0x8F");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"3 byte range Unicode 3.0 Chinese character",
				"\u3400\u3401\u3402",
				"0xE3,0x90,0x80,0xE3,0x90,0x81,0xE3,0x90,0x82");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"4 byte range Unicode 3.1 Chinese character",
				"\u20000\u20001\u20002",
				"0xF0,0xA0,0x80,0x80,0xF0,0xA0,0x80,0x81,0xF0,0xA0,0x80,0x82");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase("Korean Hangual",
				"\uAC01\uAC10\uAC20",
				"0xEA,0xB0,0x81,0xEA,0xB0,0x90,0xEA,0xB0,0xA0");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase("Thai",
				"\u0E09\u0E31\u0E19\u0E01\u0E34",
				"0xE0,0xB8,0x89,0xE0,0xB8,0xB1,0xE0,0xB8,0x99,0xE0,0xB8,0x81,0xE0,0xB8,0xB4");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase("Hebrew",
				"\u05DE\u05D6\u05D9\u05E7",
				"0xD7,0x9E,0xD7,0x96,0xD7,0x99,0xD7,0xA7");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase("Arabic",
				"\u064A\u0624\u0644\u0645\u0646\u064A",
				"0xD9,0x8A,0xD8,0xA4,0xD9,0x84,0xD9,0x85,0xD9,0x86,0xD9,0x8A");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase("Arabic",
				"\u064A\u0624\u0644\u0645\u0646\u064A",
				"0xD9,0x8A,0xD8,0xA4,0xD9,0x84,0xD9,0x85,0xD9,0x86,0xD9,0x8A");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"Defined in GBK not in BIG5", "\u7506\u7551\u75EC", "unknown");
		TEST_STRINGS[NUM_TEST_CASES++] = new EncodingTestCase(
				"Defined in BIG5 and in GBK", "\u7533\u7409\u7329", "unknown");
	}
	
	/**
	 * Required by JUnit
	 */
	public LocalEncodingTest() throws Exception {
		super("configremote");
	}

	protected LocalEncodingTest(String clientName) throws Exception {
		super(clientName == null?"configremote":clientName);
	}

	protected Test1Driver createDriver() throws Exception {
		// Create a test message. In this test, we only use subject for the
		// description and
		// body to carry the coded string.
		MyMessage msg = TestUtils.createTestMessage();
		msg.setBinaryData(null);
		msg.setSomething(null);
		Test1Driver driver = new EncodingTest1Driver("test1", "configremote",
				BaseCallTest.CONFIG_ROOT, serverUri.toURL(), DATA_FORMATS, DATA_FORMATS,
				"myTestOperation", msg);

		setupDriver(driver);
		driver.setExpectingSameMessage(false);
		return driver;
	}

	private static final Charset ENCODING_UTF8 = Charset.forName("UTF-8");

	protected void setupDriver(Test1Driver driver) {
		G11nOptions options = new G11nOptions(ENCODING_UTF8, null, null);
		driver.setG11nOptions(options);
		driver.setVerifier(new Verifier(ENCODING_UTF8));
	}

	protected class Verifier implements Test1Driver.SuccessVerifier {
		Verifier(Charset charset) {
		}

		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception {
			String description = response.getSubject();
			String code = response.getBody();

			assertFalse("description shouldn't be empty.", description == null);

			for (int i = 0; i < NUM_TEST_CASES; i++) {
				EncodingTestCase testCase = TEST_STRINGS[i];
				if (description.equals(testCase.m_description)) {
					assertEquals(code, testCase.m_internalCode);
					return;
				}
			}
			assertFalse("Cannot find test case", true);
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResponse, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception {
			String description = response.getSubject();
			String code = response.getBody();

			assertFalse("description shouldn't be empty.", description == null);

			for (int i = 0; i < NUM_TEST_CASES; i++) {
				EncodingTestCase testCase = TEST_STRINGS[i];
				if (description.equals(testCase.m_description)) {
					assertEquals(code, testCase.m_internalCode);
					return;
				}
			}
			assertFalse("Cannot find test case", true);
		}

	}

	protected static class EncodingTest1Driver extends Test1Driver {
		public EncodingTest1Driver(String serviceName, String clientName,
				String configRoot, URL serviceURL, String[] reqDataFormats,
				String[] resDataFormats, String operationName, MyMessage msg) {
			super(serviceName, clientName, configRoot, serviceURL,
					reqDataFormats, resDataFormats, operationName, msg);
		}

		public void doCall() throws Exception {
			for (int i = 0; i < NUM_TEST_CASES; i++) {
				EncodingTestCase testCase = TEST_STRINGS[i];
				m_message.setSubject(testCase.m_description);
				m_message.setBody(testCase.m_internalCode);
				System.out.println("Start encoding test for '"
						+ testCase.m_description + "' with encoded input '"
						+ testCase.m_internalCode + "'.");
				super.doCall();
				System.out.println("End encoding test for '"
						+ testCase.m_description + "' with encoded input '"
						+ testCase.m_internalCode + "'.");
			}
		}

	}

	public static class EncodingTestCase {
		String m_description;

		String m_internalCode;

		String m_utf8Encoded;

		public EncodingTestCase(String description, String internalCode,
				String utf8Encoded) {
			m_description = description;
			m_internalCode = internalCode;
			m_utf8Encoded = utf8Encoded;
		}
	}
}
