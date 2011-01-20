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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.config.validation.verifiers.ClientConfigVerifier;
import org.ebayopensource.turmeric.runtime.config.validation.verifiers.ErrorDataVerifier;
import org.ebayopensource.turmeric.runtime.config.validation.verifiers.GlobalClientConfigVerifier;
import org.ebayopensource.turmeric.runtime.config.validation.verifiers.ServiceConfigVerifier;
import org.ebayopensource.turmeric.runtime.config.validation.verifiers.TypeMappingVerifier;
import org.ebayopensource.turmeric.runtime.config.validation.verifiers.WsdlVerifier;
import org.junit.Assert;

/**
 * Utility class for scanning all known Runtime Configuration settings and ensure that they are valid for testing.
 * <p>
 * The need for this was brought on due to the package renaming effort.
 */
public class RuntimeConfigValidator {
    static final Logger LOG = Logger.getLogger(RuntimeConfigValidator.class.getName());
    private List<String> excludedClasses = new ArrayList<String>();
    private List<String> excludedConfigs = new ArrayList<String>();

    public void addConfigExclude(String path) {
        this.excludedConfigs.add(path);
    }

    public void addClassExclude(String classname) {
        this.excludedClasses.add(classname);
    }

    public static void assertNoViolations(Violations violations) {
        LOG.fine("Violations: " + violations);
        if (violations.hasViolation()) {
            String report = violations.createReport();
            Assert.fail(report);
        }
    }
    
    public void validateAll(File dir, Report report) {
        if (!dir.exists()) {
            LOG.fine("Skipping Config Verification (does not exist): " + dir);
            return;
        }
        if (!dir.isDirectory()) {
            LOG.fine("Skipping Config Verification (not a directory): " + dir);
            return;
        }
        LOG.fine("Verifying Configs in " + dir);

        // @formatter:off
        AbstractVerifier verifiers[] = {
            new ClientConfigVerifier(),
            new GlobalClientConfigVerifier(),
            new ServiceConfigVerifier(),
            new TypeMappingVerifier(),
            new ErrorDataVerifier(),
            new WsdlVerifier()
        };
        // @formatter:on

        for (AbstractVerifier verifier : verifiers) {
            verifier.setReport(report);
            verifier.setExcludedConfigs(this.excludedConfigs);
            verifier.setExcludedClasses(this.excludedClasses);
            verifier.setBaseDir(dir);
            verifier.verifyAllConfigMatches();
        }
    }
}
