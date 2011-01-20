/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.attachment;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.internet.ContentType;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;


/**
 * NOTE: How to test attachments. Currently the mail.jar packaged with ibm JDK
 * is not compatible with the mail.jar that axis2 is using. So the attachment
 * testcases are commented out.
 * 
 * To run the test case in dev box, rename the mail.jar in your
 * C:\opt\java-ibm...\jre\lib\ext copy the mail.jar from your view's
 * externalv3\jwsdp\1.2\jwsdp-shared\lib
 * 
 * Uncomment the DataHandler related code in createBinaryDataHandler() method of
 * this class and in Test1ServiceImpl. compile them.
 * 
 * Uncomment the code in AllTests of this package. then run.
 * 
 * @author wdeng
 */
public class LocalAttachmentTest extends BaseAttachmentTest {

	protected static final String ATTACHMENT_SERVICE_NAME = "attachment";

	
	public LocalAttachmentTest() throws Exception {
		super("attachment");
	}

	/*public LocalAttachmentTest(String configRoot, String entryURL)
			throws Exception {
		super(configRoot, entryURL);
	}*/

	protected Test1Driver createDriver() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		msg.setBinaryData(getDataHandler());
		Test1Driver driver = new Test1Driver(ATTACHMENT_SERVICE_NAME,
				m_clientName, CONFIG_ROOT, serverUri.toURL(),
				new String[] { "XML" }, new String[] { "XML" },
				"myTestOperation", msg);
		setupDriver(driver);
		driver.setExpectingSameMessage(false);
		return driver;
	}

	protected void setupDriver(Test1Driver driver) {
		driver.setVerifier(new AttachmentVerifier(getDataHandler()));
	}

	
	protected class AttachmentVerifier implements Test1Driver.SuccessVerifier {
		private byte[] m_data;

		AttachmentVerifier(DataHandler data) {
			m_data = getDataFromHandler(data);
		}

		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception {
			DataHandler handler = response.getBinaryData();
			byte[] receivedData = getDataFromHandler(handler);

			Assert.assertFalse("binary data is null", handler == null);
			Assert.assertTrue("Attachment data received is not the same", equals(
					m_data, receivedData));
		}

		@SuppressWarnings("rawtypes")
		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResponse, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception {
			DataHandler handler = response.getBinaryData();
			byte[] receivedData = getDataFromHandler(handler);

			Assert.assertFalse("binary data is null", handler == null);
			Assert.assertTrue("Attachment data received is not the same", equals(
					m_data, receivedData));
		}

		private boolean equals(byte[] s1, byte[] s2) {
			if (s1 == null) {
				return s2 == null;
			}
			int len = s1.length;
			if (len != s2.length) {
				len = s2.length;
			}
			for (int i = 0; i < len; i++) {
				if (s1[i] != s2[i]) {
					return false;
				}
			}
			return true;
		}
	}

	
	public static void main(String[] argv) {
		try {
			ContentType ct1 = new ContentType(
					"multipart/related; boundary=MIMEBoundaryurn_uuid_AB4AF6CF4B6DE9EB94118478716760019; type=\"application/xop+xml\"; start=\"<0.urn:uuid:AB4AF6CF4B6DE9EB94118478716760020@eBay.com>\"; start-info=\"text/xml;charset=UTF-8");
			System.out.println(ct1.getParameter("start"));
			System.out.println(ct1.getParameter("start-info"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
