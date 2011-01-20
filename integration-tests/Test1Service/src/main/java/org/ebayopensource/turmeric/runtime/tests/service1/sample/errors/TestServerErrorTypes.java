/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.errors;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.tests.service1.errors.ErrorDesc;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;


/**
 * @author ichernyshev
 */
@SuppressWarnings("deprecation")
public class TestServerErrorTypes {

	public static final String TEST_DOMAIN = "SOA_TEST";

	public static final long SERVER_TEST_BASE = 120000;

	public final static ErrorDesc TEST1_SERVICE_EXCEPTION =
		new TestErrorDesc(SERVER_TEST_BASE+0, "test1_service_exception");

	/*****************************
	 * HELPER METHODS
	 *****************************/

	private TestServerErrorTypes() {
	}

	private static class TestErrorDesc extends ErrorDesc {
		TestErrorDesc(long id, String messageId) {
			this(id, messageId, ErrorSeverity.ERROR, ErrorCategory.SYSTEM, ErrorSubcategory.SYSTEM);
		}

		TestErrorDesc(long id, String messageId, ErrorSeverity severity,
			ErrorCategory category, ErrorSubcategory subcategory)
		{
			super(id, messageId, TEST_DOMAIN, severity, category, subcategory,
				TestServerErrorTypes.class, "TestServerErrorTypes");
		}
	}

	static {
		ErrorDesc.validateErrors(TestServerErrorTypes.class);
	}
}
