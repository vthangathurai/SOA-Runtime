/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.PushResponse;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.Assert;
import org.junit.Test;


public class PushResponseTest extends AbstractTurmericTestCase {

	private static final String RETURN_VALUE = "Return Value";

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void pushResponseGet() throws Exception {

		// Push Response that has some value
		PushResponse<Object> pushResponse1 = new PushResponse<Object>(
				RETURN_VALUE, null, null);

		try {
			Assert.assertEquals(RETURN_VALUE, pushResponse1.get());
		} catch (Throwable e) {
			Assert.fail("Unexpected Exception");
		}

		// Push Response for exception
		PushResponse<Object> pushResponse2 = new PushResponse<Object>(null,
				new RuntimeException(RETURN_VALUE), null);

		try {
			pushResponse2.get();
			Assert.fail("RuntimeException was expected");
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			Assert.assertTrue(cause instanceof RuntimeException);
			Assert.assertTrue(cause.getMessage().contains(RETURN_VALUE));
		}

		// Push Response for exception
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put(RETURN_VALUE, RETURN_VALUE);
		PushResponse<Object> pushResponse3 = new PushResponse<Object>(
				RETURN_VALUE, null, context);

		try {
			Assert.assertEquals(RETURN_VALUE, pushResponse3.get());
			Assert.assertNotNull(pushResponse3.getContext());
			Assert.assertEquals(RETURN_VALUE, pushResponse3.getContext().get(
					RETURN_VALUE));
		} catch (Throwable e) {
			Assert.fail("Unexpected Exception");
		}
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void pushResponseGetTimeout() throws Exception {

		// Push Response that has some value
		PushResponse<Object> pushResponse1 = new PushResponse<Object>(
				RETURN_VALUE, null, null);

		try {
			Assert.assertEquals(RETURN_VALUE, pushResponse1.get(1,
					TimeUnit.NANOSECONDS));
		} catch (Throwable e) {
			Assert.fail("Unexpected Exception");
		}

		// Push Response for exception
		PushResponse<Object> pushResponse2 = new PushResponse<Object>(null,
				new RuntimeException(RETURN_VALUE), null);

		try {
			pushResponse2.get(1, TimeUnit.NANOSECONDS);
			Assert.fail("RuntimeException was expected");
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			Assert.assertTrue(cause instanceof RuntimeException);
			Assert.assertTrue(cause.getMessage().contains(RETURN_VALUE));
		}

		// Push Response for exception
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put(RETURN_VALUE, RETURN_VALUE);
		PushResponse<Object> pushResponse3 = new PushResponse<Object>(
				RETURN_VALUE, null, context);

		try {
			Assert.assertEquals(RETURN_VALUE, pushResponse3.get(1,
					TimeUnit.NANOSECONDS));
			Assert.assertNotNull(pushResponse3.getContext());
			Assert.assertEquals(RETURN_VALUE, pushResponse3.getContext().get(
					RETURN_VALUE));
		} catch (Throwable e) {
			Assert.fail("Unexpected Exception");
		}
	}
	
	@Test
	public void pushResponseTestCancel() throws Exception {

		PushResponse<Object> pushResponse1 = new PushResponse<Object>(
				RETURN_VALUE, null, null);

		Assert.assertFalse(pushResponse1.isCancelled());

		PushResponse<Object> pushResponse2 = new PushResponse<Object>(null,
				new RuntimeException(RETURN_VALUE), null);
		Assert.assertFalse(pushResponse2.isCancelled());
		
		PushResponse<Object> pushResponse3 = new PushResponse<Object>(null,
				new RuntimeException(RETURN_VALUE), null);
		Assert.assertFalse(pushResponse3.isCancelled());
	}
	
	@Test
	public void pushResponseTestDone() throws Exception {

		PushResponse<Object> pushResponse1 = new PushResponse<Object>(
				RETURN_VALUE, null, null);

		Assert.assertTrue(pushResponse1.isDone());

		PushResponse<Object> pushResponse2 = new PushResponse<Object>(null,
				new RuntimeException(RETURN_VALUE), null);
		Assert.assertTrue(pushResponse2.isDone());
		
		PushResponse<Object> pushResponse3 = new PushResponse<Object>(null,
				new RuntimeException(RETURN_VALUE), null);
		Assert.assertTrue(pushResponse3.isDone());
	}

}
