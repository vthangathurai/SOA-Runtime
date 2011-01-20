/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.errors;

import org.junit.Assert;
import org.junit.Test;

public class TestErrorTypesTest {
    @Test
    public void testInit() {
        Assert.assertNotNull("NO_REQUEST_HEADERS", TestErrorTypes.NO_REQUEST_HEADERS);
    }
}
