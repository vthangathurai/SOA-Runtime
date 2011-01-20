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
public class TestErrorTypes {

	public static final String TEST_DOMAIN = "SOA_TEST";
	public static final long CLIENT_TEST_BASE = 100000;

	public final static ErrorDesc HEADER_MISMATCH =
		new TestErrorDesc(CLIENT_TEST_BASE+0, "req_and_resp_headers_mismatch");

	public final static ErrorDesc CLIENT_HANDLER_EXCEPTION_TEST =
		new TestErrorDesc(CLIENT_TEST_BASE+1, "client_handler_exception_test");

	public final static ErrorDesc NO_REQUEST_HEADERS =
		new TestNoBundleErrorDesc(CLIENT_TEST_BASE+2,"no_request_header_found");

	public final static ErrorDesc HANDLER_EXCEPTION_TEST =
		new TestNoBundleErrorDesc(CLIENT_TEST_BASE+3, "handler_exception_test");

	public final static ErrorDesc HANDLER_HARMLESS_EXCEPTION_TEST =
		new TestErrorDesc(CLIENT_TEST_BASE+4, "handler_harmless_exception_test");

	/*****************************
	 * HELPER METHODS
	 *****************************/
	private TestErrorTypes() {
	}

	private static class TestErrorDesc extends ErrorDesc {
		TestErrorDesc(long id, String messageId) {
			this(id, messageId, ErrorSeverity.ERROR, ErrorCategory.SYSTEM, ErrorSubcategory.SYSTEM);
		}

		TestErrorDesc(long id, String messageId, ErrorSeverity severity,
			ErrorCategory category, ErrorSubcategory subcategory)
		{
			super(id, messageId, TEST_DOMAIN, severity, category, subcategory,
				TestErrorTypes.class, "TestErrorDescs");
		}
	}

	private static class TestNoBundleErrorDesc extends ErrorDesc {
		TestNoBundleErrorDesc(long id, String messageText)
		{
			super(id, messageText, TEST_DOMAIN, ErrorSeverity.ERROR);
		}
	}

	static {
		ErrorDesc.validateErrors(TestErrorTypes.class);
	}
}
