/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.attachment;

import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.junit.Assert;

public abstract class BaseAttachmentTest extends BaseCallTest {

	DataHandler m_binaryData = createBinaryDataHandler();

	public BaseAttachmentTest() throws Exception {
		super();
	}

	public BaseAttachmentTest(String clientName) throws Exception {
		super(clientName);
	}
	
	public static byte[] getDataFromHandler(DataHandler data) {
		if (null == data) {
			return null;
		}
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			data.writeTo(os);
			return os.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	private static String create10KBString() {
		StringBuilder message = new StringBuilder(10240);
		for (int i = 0; i < 1024; ++i) {
			message.append("AAAAAAAAAA");
		}
		return message.toString();
	}
			
	private static DataHandler createBinaryDataHandler() {
		try {			
			DataSource ds = new ByteArrayDataSource(create10KBString(), "text/plain;charset=UTF-8");
			return new DataHandler(ds);

		} catch (Exception e) {
			return null;
		}
	}
	
	public DataHandler getDataHandler() {
		if (null == m_binaryData) {
			Assert.assertTrue("Not able to create test binary data.", false);
		}
		return m_binaryData;
	}
	
}
