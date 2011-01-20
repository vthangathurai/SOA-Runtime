/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation.verifiers;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.runtime.config.validation.Violation;
import org.ebayopensource.turmeric.runtime.config.validation.Violations;
import org.junit.Assert;
import org.junit.Test;

public class TypeMappingVerifierTest {
    @Test
    public void testVerify() {
        File testDir = MavenTestingUtils.getTestResourcesDir();
        File testXml = MavenTestingUtils.getTestResourceFile("META-INF/soa/common/config/testmapping/TypeMappings.xml");

        TypeMappingVerifier verifier = new TypeMappingVerifier();
        Violations violations = new Violations();
        verifier.setExcludedClasses(new ArrayList<String>());
        verifier.setReport(violations);
        verifier.setBaseDir(testDir);
        verifier.verifyFile(testXml);

        Assert.assertThat(violations.hasViolation(), is(true));
        Assert.assertThat(violations.getViolationCount(), is(23));

        List<String> expected = new ArrayList<String>();
        expected.add("Class \"org.ebayopensource.turmeric.runtime.tests.sample.types1.CustomErrorMessage\" " +
        		"does not have corresponding <package-map name=\"org.ebayopensource.turmeric.runtime.tests.sample.types1\">");
        expected.add("Class not found: org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage");
        expected.add("Class not found: org.ebayopensource.turmeric.runtime.types.ErrorMessage");
        expected.add("Expected namespace of \"http://www.ebayopensource.org/turmeric/common/v1/types\" " +
        		"for package name \"org.ebayopensource.turmeric.runtime.types\", but found namespaces" +
        		" \"http://www.ebay.com/marketplace/services\" instead");

        for (Violation viol : violations) {
            expected.remove(viol.getMsg());
        }

        if (expected.size() > 0) {
            StringBuilder err = new StringBuilder();
            err.append(expected.size());
            err.append(" expected error message(s) not found!");
            for (String expect : expected) {
                err.append("\n").append(expect);
            }
            System.out.println(err.toString());

            System.out.println("--Actual Error Messages");
            System.out.println(violations.createReport());

            Assert.fail(err.toString());
        }
    }
}
