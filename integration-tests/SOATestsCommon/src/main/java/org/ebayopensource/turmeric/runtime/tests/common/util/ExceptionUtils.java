/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.util;

import junit.framework.TestCase;

public class ExceptionUtils {

	static public void checkException(Object e, Class clazz, String subtext) throws Exception {
		if (!(e instanceof Throwable)) {
			if (clazz != null) {
				String cause = printErrorInfo(e);
				TestCase.fail("Expected " + clazz.getName() + ", but got another error response: " + cause);
			}

			return;
		}

		if (clazz == null) {
			String cause = printErrorInfo(e);
			TestCase.fail("Expected no exception, but got exception: " + cause);
		}

		Throwable e2 = (Throwable)e;
		if (e2.getClass() != clazz) {
			String cause = printErrorInfo(e);
			TestCase.fail("Unexpected error class " + e2.getClass().getName() +
				", expected " + clazz.getName() + ": " + cause);
		}

		String text = e2.toString();
		if (text.indexOf(subtext) == -1) {
			String cause = printErrorInfo(e);
			TestCase.fail("Exception does not contain expected subtext '" + subtext +
				"' : " + cause);
		}
	}
	static private String printErrorInfo(Object e) {

		String causeText;
		if (e instanceof Throwable) {
			((Throwable)e).printStackTrace();
			causeText = e.toString();
		} else {
			causeText = e.toString();
		}

		System.err.println("ErrorInfo: " + causeText);

		return causeText;
	}
}
