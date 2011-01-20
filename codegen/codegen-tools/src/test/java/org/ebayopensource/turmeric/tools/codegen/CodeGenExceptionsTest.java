/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.BrokenSchemaException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.UnsupportedSchemaException;
import org.ebayopensource.turmeric.tools.codegen.validator.MessageObject;
import org.junit.Test;


public class CodeGenExceptionsTest extends AbstractServiceGeneratorTestCase {

	@Test
	public void messageObject() {
		MessageObject msgObject = new MessageObject();

		msgObject.setMethodName("doSomeThing");
		msgObject.setMessage("Overloaded method");
		msgObject.setResolution("remove one method");
		msgObject.setIsFatalError(true);

		assertEquals(msgObject.getMethodName(), "doSomeThing");
		assertEquals(msgObject.getMessage(), "Overloaded method");
		assertEquals(msgObject.getResolution(), "remove one method");
		assertTrue(msgObject.isFatalError());
	}


	@Test
	public void badInputOptionException1() throws Exception {
		BadInputOptionException badInputEx = new BadInputOptionException();		
		assertTrue(badInputEx.getMessage() == null);
	}


	@Test
	public void badInputOptionException2() throws Exception {
		BadInputOptionException badInputEx = 
			new BadInputOptionException("Unknown option specified, option name : -text");		
		assertTrue(badInputEx.getMessage() != null);
	}


	@Test
	public void badInputOptionException3() throws Exception {
		BadInputOptionException badInputEx = 
			new BadInputOptionException("Unknown option specified", new Exception());		
		assertTrue((badInputEx.getMessage() != null) && (badInputEx.getCause() != null));
	}

	@Test
	public void codeGenFailedException1() throws Exception {
		CodeGenFailedException codeGenFailedEx = new CodeGenFailedException();		
		assertTrue(codeGenFailedEx.getMessage() == null);
	}
	@Test
	public void codeGenFailedException2() throws Exception {
		CodeGenFailedException codeGenFailedEx = 
			new CodeGenFailedException("Unable to generated service proxy");		
		assertTrue(codeGenFailedEx.getMessage() != null);
	}

	@Test
	public void codeGenFailedException3() throws Exception {
		CodeGenFailedException codeGenFailedEx = 
			new CodeGenFailedException("Unable to generated service proxy", new Exception());		
		assertTrue((codeGenFailedEx.getMessage() != null) && (codeGenFailedEx.getCause() != null));
	}



	@Test
	public void preProcessFailedException1() throws Exception {
		PreProcessFailedException preProcessFailedEx = new PreProcessFailedException();		
		assertTrue(preProcessFailedEx.getMessage() == null);
	}


	@Test
	public void preProcessFailedException2() throws Exception {
		PreProcessFailedException preProcessFailedEx = 
			new PreProcessFailedException("Unable to load interface class");		
		assertTrue(preProcessFailedEx.getMessage() != null);
	}


	@Test
	public void preProcessFailedException3() throws Exception {
		PreProcessFailedException preProcessFailedEx = 
			new PreProcessFailedException("Unable to load interface class", new Exception());		
		assertTrue((preProcessFailedEx.getMessage() != null) && (preProcessFailedEx.getCause() != null));
	}


	@Test
	public void preProcessFailedException4() throws Exception {
		List<MessageObject> errMsgList =  new ArrayList<MessageObject>(2);
		MessageObject msgObj = new MessageObject("doSomething", "Overloaded methods not allowed", "");
		errMsgList.add(msgObj);

		MessageObject msgObj2 = new MessageObject("Overloaded methods not allowed");
		errMsgList.add(msgObj2);

		PreProcessFailedException preProcessFailedEx = 
			new PreProcessFailedException("Unable to generated service proxy", new Exception(), errMsgList);		
		assertTrue((preProcessFailedEx.getMessage() != null) 
				&& (preProcessFailedEx.getCause() != null) 
				&& (preProcessFailedEx.getErrorMsgList() != null));
	}


	@Test
	public void preProcessFailedException5() throws Exception {
		List<MessageObject> errMsgList =  new ArrayList<MessageObject>(2);
		MessageObject msgObj = new MessageObject("doSomething", "Overloaded methods not allowed", "");
		errMsgList.add(msgObj);

		MessageObject msgObj2 = new MessageObject("Overloaded methods not allowed");
		errMsgList.add(msgObj2);


		PreProcessFailedException preProcessFailedEx = 
			new PreProcessFailedException("Unable to generated service proxy", errMsgList);		
		assertTrue((preProcessFailedEx.getMessage() != null) 
				&& (preProcessFailedEx.getErrorMsgList() != null));
	}



	@Test
	public void missingInputOptionException1() throws Exception {
		MissingInputOptionException missingOptionEx = new MissingInputOptionException();		
		assertTrue(missingOptionEx.getMessage() == null);
	}


	@Test
	public void missingInputOptionException2() throws Exception {
		MissingInputOptionException missingOptionEx = 
			new MissingInputOptionException("Missing option, option name : -servicename");		
		assertTrue(missingOptionEx.getMessage() != null);
	}


	@Test
	public void missingInputOptionException3() throws Exception {
		MissingInputOptionException missingOptionEx = 
			new MissingInputOptionException("Missing option, option name : -servicename", new Exception());		
		assertTrue((missingOptionEx.getMessage() != null) && (missingOptionEx.getCause() != null));
	}


	@Test
	public void preValidationFailedException1() throws Exception {
		PreValidationFailedException preValidationFailedEx = new PreValidationFailedException();		
		assertTrue(preValidationFailedEx.getMessage() == null);
	}


	@Test
	public void preValidationFailedException2() throws Exception {
		PreValidationFailedException preValidationFailedEx = 
			new PreValidationFailedException("Collection types cannot be used as input / output types");		
		assertTrue(preValidationFailedEx.getMessage() != null);
	}


	@Test
	public void preValidationFailedException3() throws Exception {
		PreValidationFailedException preValidationFailedEx = 
			new PreValidationFailedException("Collection types cannot be used as input / output types", new Exception());		
		assertTrue((preValidationFailedEx.getMessage() != null) && (preValidationFailedEx.getCause() != null));
	}


	@Test
	public void preValidationFailedException4() throws Exception {
		List<MessageObject> errMsgList =  new ArrayList<MessageObject>(1);
		MessageObject msgObj = new MessageObject("doSomething", "Overloaded methods not allowed");
		errMsgList.add(msgObj);

		PreValidationFailedException preValidationFailedEx = 
			new PreValidationFailedException("Interface validation failed", new Exception(), errMsgList);		
		assertTrue((preValidationFailedEx.getMessage() != null) 
				&& (preValidationFailedEx.getCause() != null) 
				&& (preValidationFailedEx.getErrorMsgList() != null));
	}


	@Test
	public void preValidationFailedException5() throws Exception {
		List<MessageObject> errMsgList =  new ArrayList<MessageObject>(1);
		MessageObject msgObj = new MessageObject("doSomething", "Overloaded methods not allowed");
		errMsgList.add(msgObj);

		PreValidationFailedException preValidationFailedEx = 
			new PreValidationFailedException("Interface validation failed", errMsgList);		
		assertTrue((preValidationFailedEx.getMessage() != null) 
				&& (preValidationFailedEx.getErrorMsgList() != null));
	}

	@Test
	public void brokenSchemaException1() throws Exception {
		BrokenSchemaException brokenSchemaEx = 
			new BrokenSchemaException("Schema is not valid");		
		assertTrue(brokenSchemaEx.getMessage() != null);
	}


	@Test
	public void brokenSchemaException2() throws Exception {
		BrokenSchemaException brokenSchemaEx = 
			new BrokenSchemaException("Schema is not valid", new Exception());		
		assertTrue((brokenSchemaEx.getMessage() != null) && (brokenSchemaEx.getCause() != null));
	}



	@Test
	public void unsupportedSchemaException1() throws Exception {
		UnsupportedSchemaException unsupportedEx = 
			new UnsupportedSchemaException("Schema not supported");		
		assertTrue(unsupportedEx.getMessage() != null);
	}


	@Test
	public void unsupportedSchemaException2() throws Exception {
		UnsupportedSchemaException unsupportedEx = 
			new UnsupportedSchemaException("Schema not supported", new Exception());		
		assertTrue((unsupportedEx.getMessage() != null) && (unsupportedEx.getCause() != null));
	}


	@Test
	public void badInputValueException1() throws Exception {
		BadInputValueException badInputEx = new BadInputValueException();
		Assert.assertNull(badInputEx.getMessage());
	}


	@Test
	public void badInputValueException2() throws Exception {
		BadInputValueException badInputEx = 
			new BadInputValueException("Invalid value specified, value  : TestGen");
		Assert.assertNotNull(badInputEx.getMessage());
	}


	@Test
	public void badInputValueException3() throws Exception {
		BadInputValueException badInputEx = 
			new BadInputValueException("Invalid value specified, value  : TestGen", new Exception());
		Assert.assertNotNull(badInputEx.getMessage());
		Assert.assertNotNull(badInputEx.getCause());
	}
}
