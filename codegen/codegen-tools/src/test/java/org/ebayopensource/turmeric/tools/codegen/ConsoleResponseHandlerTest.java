/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.ebayopensource.turmeric.tools.codegen.handler.ConsoleResponseHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author rpallikonda
 */
public class ConsoleResponseHandlerTest extends AbstractServiceGeneratorTestCase {
	
	/**
	 * Test method for {@link org.ebayopensource.turmeric.runtime.tools.codegen.handler.ConsoleResponseHandler#getBooleanResponse(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetBooleanResponse() throws IOException {
		PipedInputStream testInPipe = new PipedInputStream();
		PipedOutputStream testOutPipe = new PipedOutputStream();
		ConsoleResponseHandler csrHandle = new ConsoleResponseHandler();

		testInPipe.connect(testOutPipe);
		
		System.setIn(testInPipe);
		
		// first test		
		issueKeystroke(testOutPipe, "Y\n");
		
		boolean respFlag = csrHandle.getBooleanResponse("testGetBooleanResponse() prompt");
		Assert.assertTrue("expecting true response", respFlag);

		// second test		
		issueKeystroke(testOutPipe, "N\n");

		respFlag = csrHandle.getBooleanResponse("testGetBooleanResponse() prompt");
		Assert.assertFalse("expecting false response", respFlag);
	}
	
	private void issueKeystroke(PipedOutputStream stream, String keys)
			throws IOException {
		stream.write(keys.getBytes(), 0, keys.length());
		stream.flush();
		System.out.println();
	}

}
