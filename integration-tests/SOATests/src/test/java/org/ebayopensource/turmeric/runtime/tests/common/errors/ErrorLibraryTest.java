/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.errors;

import static org.hamcrest.Matchers.*;

import java.util.Locale;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.junit.Assert;
import org.junit.Test;



public class ErrorLibraryTest {

    private final String runtimeDomain = "TurmericRuntime";
    private final String nonExistentDomain = "SomeDomain";
    private final String nonExistentErrorName = "svc_factory_create_svc";
    private final String properErrorName = "svc_factory_cannot_create_svc";
    
    private final String properErrorMessage = "One is unable to create ServiceDesc for";

	@Test    
    public void testLazyInit() {
    	CommonErrorData errorData = ErrorDataFactory.createErrorData(properErrorName, runtimeDomain);
    	Assert.assertNotNull("errorData should not be null", errorData);
    	Assert.assertThat("errorData", errorData, instanceOf(CommonErrorData.class));
    }
	
	@Test(expected=ServiceRuntimeException.class)
    public void testGetErrorNonExistentDomain() {
        ErrorDataFactory.createErrorData(properErrorName, nonExistentDomain);
    }
	

	@Test(expected=ServiceRuntimeException.class)
    public void testGetNonExistentError() {
        ErrorDataFactory.createErrorData(nonExistentErrorName, runtimeDomain);
    }
	
	@Test    
    public void testErrorContents() {
    	CommonErrorData errorData = ErrorDataFactory.createErrorData(properErrorName, runtimeDomain, 
    				new Object[]{"One", "Two", "Three"});
        Assert.assertNotNull("errorData should not be null", errorData);
        Assert.assertThat("errorData", errorData, instanceOf(CommonErrorData.class));
        Assert.assertTrue(verifyContents((CommonErrorData)errorData, properErrorName, properErrorMessage, 1000L, ErrorCategory.SYSTEM, ErrorSeverity.ERROR));
    }

	@Test    
    public void testErrorMessage() {
		CommonErrorData errorData = ErrorDataFactory.createErrorData(properErrorName, runtimeDomain, 
    				new Object[]{"One", "Two", "Three"});
        Assert.assertNotNull("errorData should not be null", errorData);
        Assert.assertThat("errorData", errorData, instanceOf(CommonErrorData.class));
        String message = errorData.getMessage();
        Assert.assertThat("errorData.message", message, containsString(properErrorMessage));
    }

	@Test    
    public void testDifferentLocale() {
		CommonErrorData errorData = ErrorDataFactory.createErrorData(properErrorName, null, runtimeDomain, 
    				new Object[]{"One", "Two", "Three"}, new Locale("fr"));
        Assert.assertNotNull("errorData should not be null", errorData);
        Assert.assertThat("errorData", errorData, instanceOf(CommonErrorData.class));
        String message = errorData.getMessage();
        Assert.assertThat("errorData.message", message, containsString("French"));
    }
	
	private boolean verifyContents(CommonErrorData errorData, 
			String errorName, String message, long errorId, ErrorCategory category,
			ErrorSeverity severity){
		boolean validated = false;
		if(errorData != null){
			if(errorData.getErrorName().equalsIgnoreCase(errorName)
					&& errorData.getSeverity().equals(severity)
					&& errorData.getCategory().equals(category)
					&& errorData.getErrorId() == errorId
					&& errorData.getMessage().contains(message) )
				validated = true;
		}
		return validated;
	}
}
