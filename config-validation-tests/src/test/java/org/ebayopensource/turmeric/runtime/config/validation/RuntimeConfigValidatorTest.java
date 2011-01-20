/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation;

import static org.hamcrest.Matchers.*;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;

public class RuntimeConfigValidatorTest {
    private static String os(String path) {
        return FilenameUtils.separatorsToSystem(path);
    }

    @Test
    public void testConfigValidator() {
        File basedir = MavenTestingUtils.getBasedir();
        File testResources = new File(basedir, os("src/test/resources"));

        RuntimeConfigValidator verifier = new RuntimeConfigValidator();
        Violations violations = new Violations();
        verifier.validateAll(testResources, violations);
        Assert.assertThat(violations, notNullValue());
        Assert.assertTrue(violations.hasViolation());
        
        // Intentionally created violations present in /config-validation-tests/src/test/resources
        System.out.println(violations.createReport());
        Assert.assertThat(violations.getViolationCount(), greaterThan(220));
    }
}
