/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.junit.Assert;

public class ExpectedLogMessage extends Handler {
	private String expectedMessage;
	private boolean foundExpectedError = false;

	public void assertFoundMessage() {
		Assert.assertTrue("Should have caused expected error message in log: "
				+ expectedMessage, foundExpectedError);
	}

	public void setExpectedMessage(String expectedMessage) {
		this.expectedMessage = expectedMessage;
	}

	@Override
	public void publish(LogRecord record) {
		if (record.getMessage().contains(expectedMessage)) {
			foundExpectedError = true;
		}
	}

	@Override
	public void flush() {
		/* do nothing */
	}

	@Override
	public void close() throws SecurityException {
		/* do nothing */
	}
}