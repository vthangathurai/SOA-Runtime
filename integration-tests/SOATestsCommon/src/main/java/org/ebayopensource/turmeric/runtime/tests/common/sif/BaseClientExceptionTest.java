/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;



import org.junit.Test;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorDataCollection;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.TestServerErrorTypes;

/**
 * @author ichernyshev
 */
public abstract class BaseClientExceptionTest extends AbstractTurmericTestCase {

	protected abstract Test1Driver createDriver() throws Exception;

	@Test
	public void test1Exception() throws Exception {
		Test1Driver driver = createDriver();
		driver.setHeader_Test1Exception(true);
		driver.setNoPayloadData(true);
		driver.setExpectedError(ErrorDataCollection.svc_client_invocation_failed_sys_client.getErrorId(),
				ServiceInvocationException.class, ServiceInvocationRuntimeException.class, "system error on client: java.lang.NullPointerException");
		driver.doCall();
	}

	@Test
	public void test1ServiceException() throws Exception {
		Test1Driver driver = createDriver();
		driver.setHeader_Test1ServiceException(true);
		driver.setNoPayloadData(true);
		driver.setExpectedError(ErrorDataCollection.svc_wsdl_fault_exception.getErrorId(),
				ServiceInvocationException.class, ServiceInvocationRuntimeException.class, "with data my_program_data");
		driver.doCall();
	}
}
