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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.tools.codegen.CodeGenPreProcessor;
import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.ebayopensource.turmeric.tools.codegen.validator.MessageObject;
import org.ebayopensource.turmeric.tools.codegen.validator.SourceValidator;
import org.junit.Assert;
import org.junit.Test;

import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;

public class SourceValidatorTest extends AbstractServiceGeneratorTestCase {
	@Test
	public void validateServiceImpl() throws Exception {	
		Class<?> svcImplClass = SimpleServiceImpl.class;
		Class<?> svcInterfaceClass = SimpleServiceInterface.class;

		List<MessageObject> errMsgList = 
			SourceValidator.validateServiceImpl(svcImplClass, svcInterfaceClass);

		assertTrue(errMsgList.isEmpty());
	}


	@Test
	public void validateServiceImp2() throws Exception {	
		String svcImplClass = SimpleServiceImpl.class.getName();
		String svcInterfaceClass = SimpleServiceInterface.class.getName();

		List<MessageObject> errMsgList = 
			SourceValidator.validateServiceImpl(svcImplClass, svcInterfaceClass);

		assertTrue(errMsgList.isEmpty());
	}


	@Test
	public void validateServiceImp3() throws Exception {	
		List<MessageObject> errMsgList = null;
		try {
			SourceValidator.validateServiceImpl(null, "");
		} catch (PreValidationFailedException ex) {			
		} catch (Exception ex) {
			errMsgList = new ArrayList<MessageObject>();
		}				

		assertNull(errMsgList);
	}


	@Test
	public void validateServiceImp4() throws Exception {	
		List<MessageObject> errMsgList = null;
		try {
			SourceValidator.validateServiceImpl(SimpleServiceImpl.class, null);
		} catch (PreValidationFailedException ex) {			
		} catch (Exception ex) {
			errMsgList = new ArrayList<MessageObject>();
		}				

		assertNull(errMsgList);
	}


	@Test
	public void validateServiceImp5() throws Exception {	
		Class<?> svcImplClass = TestService.class;
		Class<?> svcInterfaceClass = SimpleServiceInterface.class;

		List<MessageObject> errMsgList = null;
		try {
			errMsgList = SourceValidator.validateServiceImpl(svcImplClass, svcInterfaceClass);
		} catch (Exception ex) {
		}				

		assertNotNull(errMsgList);
	}


	@Test
	public void validateServiceImp6() throws Exception {	
		Class<?> svcImplClass = AbstractTestService.class;
		Class<?> svcInterfaceClass = TestService.class;

		List<MessageObject> errMsgList = null;
		try {
			errMsgList = SourceValidator.validateServiceImpl(svcImplClass, svcInterfaceClass);
		} catch (Exception ex) {
		}				

		assertNotNull(errMsgList);
	}

	@Test
	public void validateServiceIntf1() {	
		boolean failed = false;
		try {
			SourceValidator.validateServiceInterface("InvalidInterfaceName");
			fail("invalid return from a error scenario");
		} catch (PreValidationFailedException e) {			
			e.printStackTrace();
			failed = true;
		}

		assertTrue("null parameter should have failed", failed);
	}

	@Test
	public void validateServiceIntf2() {	
		boolean failed = false;
		try {
			List<MessageObject> errorList = 
				SourceValidator.validateServiceInterface(AbstractTestService.class.getName());
			if (hasFatalErrors(errorList)) {
				failed = true;
			} else {
				fail("success from a error scenario");
			}
		} catch (PreValidationFailedException e) {			
			e.printStackTrace();
			failed = true;
		}

		assertTrue("Impl passed in as parameter, should have failed", failed);
	}

	@Test
	public void validateServiceIntf3() throws Exception {	
		SourceValidator.validateServiceInterface(SimpleServiceInterface.class.getName());
	}

	@Test
	public void validateServiceIntf4() throws PreValidationFailedException {	
		SourceValidator.validateServiceInterface(NestedCollectionInterface.class.getName());
	}

	@Test
	public void validateServiceIntf5() throws Exception {	
		SourceValidator.validateServiceInterface(NoMethodsInterface.class.getName());
	}

	@Test(expected=PreValidationFailedException.class)
	public void validateClassForService1() throws PreValidationFailedException {	
		SourceValidator.validateClassForService("InvalidImplClassName");
	}

	/**
	 * Interface passed in as parameter, should have failed
	 */
	@Test
	public void validateClassForService2() {	
		try {
			List<MessageObject> errorList =  
				SourceValidator.validateClassForService(SimpleServiceInterface.class.getName());
			
			assertHasFatalErrors(errorList);
		} catch (PreValidationFailedException e) {
			/* valid path */
		}
	}

	@Test
	public void validateClassForService3() throws Exception {
		SourceValidator.validateClassForService(TestService.class.getName());
	}

	@Test(expected=PreValidationFailedException.class)
	public void validateClassForService4() throws PreValidationFailedException {	
		SourceValidator.validateClassForService("InvalidImplClassName", null);
	}

	@Test
	public void validateClassForService5() throws Exception {	
		SourceValidator.validateClassForService(TestService.class.getName(), null);
	}

	@Test
	public void validateClassForService6() throws Exception {	
		Method[] methods = TestService.class.getMethods();
		List<String> methodList = new ArrayList<String>(); 
		methodList.add(methods[0].getName());
		SourceValidator.validateClassForService(TestService.class.getName(), methodList);
	}

	@Test
	public void validateInterfaceDef() throws Exception {
		File xmlFile = getCodegenDataFileInput("TestService3.xml");
		ServiceCodeGenDefType svDefType = CodeGenPreProcessor.parseCodeGenXml(xmlFile.getAbsolutePath());
		SourceValidator.validateInterfaceDef(svDefType.getInterfaceInfo().getInterfaceDef());
	}
	
	protected void assertHasNoFatalErrors(List<MessageObject> errorList) {
		for (MessageObject errMsgObj : errorList) {
			Assert.assertFalse("Should not have encountered a Fatal Error: " + errMsgObj.getMessage(), errMsgObj.isFatalError());
		}	
	}
	
	protected void assertHasFatalErrors(List<MessageObject> errorList) {
		boolean found = false;
		for (MessageObject errMsgObj : errorList) {
			if(errMsgObj.isFatalError()) {
				found = true;
			}
		}	
		Assert.assertTrue("Should have encountered a Fatal Error, but didn't", found);
	}

	private static boolean hasFatalErrors(List<MessageObject> errorList)  {
		for (MessageObject errMsgObj : errorList) {
			if (errMsgObj.isFatalError()) {
				return true;
			}
		}	
		return false;
	}
}
