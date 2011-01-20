/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.errors;

import static org.junit.Assert.assertTrue;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.PropertyFileBasedErrorProvider;
import org.junit.Test;



public class ErrorLibraryInitTest {

    private final String runtimeDomain = "TurmericRuntime";
    private final String nonExistentDomain = "SomeDomain";
    private final String initExceptionMessage = "Error Data Provider is not configured";

	@Test    
    public void testInit() {
		
		boolean initSuccessful = false;
        try{
        	ErrorDataProvider propProvider = ErrorDataFactory.initialize(runtimeDomain);
        	if(propProvider instanceof PropertyFileBasedErrorProvider)
        		initSuccessful = true;
        }catch (Exception e) {
        	initSuccessful = false;
		}
        
        assertTrue(initSuccessful);
    }
	
	@Test    
    public void testInitFailure() {
		
		boolean initSuccessful = false;
        try{
        	ErrorDataFactory.initialize(nonExistentDomain);
        	initSuccessful = false;
        }catch (Exception e) {
        	String message = e.getMessage();
        	if(message != null && message.contains(initExceptionMessage))
        		initSuccessful = true;
		}
        
        assertTrue(initSuccessful);
    }
	
}
