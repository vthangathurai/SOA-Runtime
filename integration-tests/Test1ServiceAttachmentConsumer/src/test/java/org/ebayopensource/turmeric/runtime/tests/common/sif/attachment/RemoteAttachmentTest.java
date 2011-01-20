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
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Test;


/**
 * See comments in LocalAttachmentTest.
 * 
 * @author wdeng
 */
public class RemoteAttachmentTest extends BaseAttachmentTest {
	public RemoteAttachmentTest() throws Exception {
		super("attachmentremote");
	}
	
	
	protected Test1Driver createDriver() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		msg.setBinaryData(getDataHandler());
		Test1Driver driver = new Test1Driver("attachment",
				"attachmentremote", CONFIG_ROOT, serverUri.toURL(),
				new String[] { "XML" }, new String[] { "XML" },
				"myTestOperation", msg);
		
		driver.setExpectingSameMessage(false);
		return driver;
	}
	
	@Test
	public void soap12WithAttachment() throws Exception{
		Test1Driver driver = createDriver();
		driver.setUseSoap12(true);
		driver.doCall();
	}
	
	@Test
	public void soap11WithAttachment() throws Exception{
		Test1Driver driver = createDriver();
		driver.setUseSoap11(true);
		driver.doCall();
	}
	

}
