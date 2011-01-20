/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.errors;

import static org.junit.Assert.*;

import org.ebayopensource.turmeric.runtime.tests.service1.errors.ErrorDesc;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.junit.Test;



public class ErrorDescTest {

    @Test    
    public void testBuildMessage() {

        final String domain = "Test.domain";
        String errMessage = "error''s message";
        String msgExpected = "error's message";
        
        ErrorDesc errorDesc = new ErrorDesc(123456, errMessage, domain, ErrorSeverity.ERROR);
        String msgActual = errorDesc.buildMessage("en_US", new Object[] { "param1" });
        assertEquals(msgExpected, msgActual);        
        
        msgActual = errorDesc.buildMessage("en_US", null);
        assertEquals(msgExpected, msgActual);
        
        errMessage = "error''s '''{'0}'' message";
        msgExpected = "error's '{0}' message";
        errorDesc = new ErrorDesc(123456, errMessage, domain, ErrorSeverity.ERROR);
        msgActual = errorDesc.buildMessage("en_US", new Object[] { "param1" });
        assertEquals(msgExpected, msgActual);        
        
        msgActual = errorDesc.buildMessage("en_US", null);       
        assertEquals(msgExpected, msgActual);
        
        errMessage = "errors {0} message";
        msgExpected = "errors param1 message";        
        errorDesc = new ErrorDesc(123456, errMessage, domain, ErrorSeverity.ERROR);
        msgActual = errorDesc.buildMessage("en_US", new Object[] { "param1" });
        assertEquals(msgExpected, msgActual);
        
        errMessage = "errors ''{0} message";
        msgExpected = "errors 'param1 message";
        errorDesc = new ErrorDesc(123456, errMessage, domain, ErrorSeverity.ERROR);
        msgActual = errorDesc.buildMessage("en_US", new Object[] { "param1" });
        assertEquals(msgExpected, msgActual);       

    }
}
