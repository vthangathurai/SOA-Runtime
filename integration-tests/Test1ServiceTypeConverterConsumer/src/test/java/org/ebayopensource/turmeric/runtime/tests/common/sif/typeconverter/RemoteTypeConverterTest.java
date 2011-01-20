/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.typeconverter;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;




/**
 * @author ichernyshev
 */
public class RemoteTypeConverterTest extends BaseCallTest {
	private static final String TYPE_CONVERTER_SERVICE_NAME = "typeconvert";

	private static final String[] FORMATS = {
		BindingConstants.PAYLOAD_XML,
		BindingConstants.PAYLOAD_FAST_INFOSET,
//		SOAConstants.PAYLOAD_JSON,
//		SOAConstants.PAYLOAD_NV
		};

	public RemoteTypeConverterTest() throws Exception {
		super(null);
	}

	protected Test1Driver createDriver() throws Exception {
		MyMessage msg = TestUtils.createTestMessage();
		msg.setSomething(null);
		Test1Driver driver = new Test1Driver(TYPE_CONVERTER_SERVICE_NAME, "typeconvert", BaseCallTest.CONFIG_ROOT,  serverUri.toURL(),
				FORMATS, FORMATS, "myTestOperation", msg);
		// TODO: check NV and FAST_INFOSET cases
		setupDriver(driver);
		driver.setExpectingSameMessage(false);
		return driver;
	}

	protected void setupDriver(Test1Driver driver) {
		driver.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
	}

}
