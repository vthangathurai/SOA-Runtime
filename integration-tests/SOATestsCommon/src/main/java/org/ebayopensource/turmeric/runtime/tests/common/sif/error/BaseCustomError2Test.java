/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Test;


/**
 * @author ichernyshev
 */
public abstract class BaseCustomError2Test extends AbstractWithServerTest {
	private String m_config;
	private String m_clientName;
	protected long m_mappingErrorId;
	protected String m_mappingErrorString;

	private static final String[] WORKING_RESP_FORMATS = {
		BindingConstants.PAYLOAD_XML,
		BindingConstants.PAYLOAD_FAST_INFOSET,
		BindingConstants.PAYLOAD_JSON,
		BindingConstants.PAYLOAD_NV
	};

	private static final String[] WORKING_REQ_FORMATS = {
		BindingConstants.PAYLOAD_XML,
		BindingConstants.PAYLOAD_FAST_INFOSET,
		BindingConstants.PAYLOAD_JSON,
		BindingConstants.PAYLOAD_NV
	};

	public BaseCustomError2Test(String config, String clientName) {
		m_config = config;
		m_clientName = clientName;
	}

	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME, m_clientName, m_config,
				serverUri.toURL(), WORKING_REQ_FORMATS, WORKING_RESP_FORMATS, Test1Driver.OP_NAME_customError2, TestUtils.createTestMessage());
		driver.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		return driver;
	}

	@Test
	public void test1Exception() throws Exception {
		Test1Driver driver = createDriver();
		driver.setHeader_Test1Exception(true);
		driver.setExpectedError(2005, MyMessage.class, ServiceInvocationRuntimeException.class, "Test1Exception");
		driver.doCall();
	}

	@Test
	public void test1ServiceException() throws Exception {
		Test1Driver driver = createDriver();
		driver.setHeader_Test1ServiceException(true);
		/*driver.setExpectedError(TestServerErrorTypes.TEST1_SERVICE_EXCEPTION.getId(),
			MyMessage.class, Test1ServiceException.class, "with data my_program_data");*/
		driver.setExpectedError(120000, MyMessage.class, ServiceInvocationRuntimeException.class, "with data my_program_data");
		driver.doCall();
	}

	@Test
	public void errorMapperException() throws Exception {
		Test1Driver driver = createDriver();
		driver.setTransportHeader(Test1Constants.TR_HDR_ERROR_MAPPER_EXCEPTION, true);
		driver.setHeader_Test1Exception(true);
		driver.setExpectedError(m_mappingErrorId, 
				ServiceInvocationException.class, ServiceInvocationRuntimeException.class,
				m_mappingErrorString);
		driver.setNoPayloadData(true);
		driver.doCall();
	}
}
