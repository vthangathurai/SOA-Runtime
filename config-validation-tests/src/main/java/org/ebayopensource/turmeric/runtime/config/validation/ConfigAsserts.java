/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;

/**
 * Used to help assert that all of the configurations present in the project are valid.
 * <p>
 * Simple Example:
 * 
 * <pre>
 * package org.ebayopensource.turmeric.runtime.config.validation;
 * 
 * import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
 * import org.junit.Test;
 * 
 * public class ConfigValidationTest extends AbstractTurmericTestCase {
 *     &#064;Test
 *     public void testConfig() {
 *         ConfigAsserts.assertConfigsValid();
 *     }
 * }
 * </pre>
 * 
 */
public class ConfigAsserts {

    private static String os(String path) {
        return FilenameUtils.separatorsToSystem(path);
    }
    
    public static void assertConfigsValid(RuntimeConfigValidator validator) {
        File basedir = MavenTestingUtils.getBasedir();
        File mainResources = new File(basedir, os("src/main/resources"));
        File testResources = new File(basedir, os("src/test/resources"));

        Violations violations = new Violations();
        validator.validateAll(mainResources, violations);
        validator.validateAll(testResources, violations);
        
        RuntimeConfigValidator.assertNoViolations(violations);
    }

    public static void assertConfigsValid() {
        assertConfigsValid(new RuntimeConfigValidator());
    }
}
