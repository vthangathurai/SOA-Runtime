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

public class WsdlVerifierTest {
    @Test
    public void testVerifySample() {
        File testDir = MavenTestingUtils.getTestResourcesDir();
        File testXml = MavenTestingUtils
                        .getTestResourceFile("META-INF/soa/services/wsdl/SampleTurmericServiceV1/SampleTurmericServiceV1.wsdl");

        WsdlVerifier verifier = new WsdlVerifier();
        Violations violations = new Violations();
        verifier.setExcludedClasses(new ArrayList<String>());
        verifier.setReport(violations);
        verifier.setBaseDir(testDir);
        verifier.verifyFile(testXml);

        Assert.assertThat(violations.hasViolation(), is(true));
        Assert.assertThat(violations.getViolationCount(), is(9));

        List<String> expected = new ArrayList<String>();
        expected.add("Bad typeLibrarySource namespace \"http://www.ebay.com/marketplace/services\" expected "
                        + "\"http://www.ebayopensource.org/turmeric/common/v1/types\" "
                        + "for library \"common-type-library\"");

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

    @Test
    public void testVerifyPolicyService() {
        File testDir = MavenTestingUtils.getTestResourcesDir();
        File testXml = MavenTestingUtils
                        .getTestResourceFile("META-INF/soa/services/wsdl/PolicyService/PolicyService.wsdl");

        WsdlVerifier verifier = new WsdlVerifier();
        Violations violations = new Violations();
        verifier.setExcludedClasses(new ArrayList<String>());
        verifier.setReport(violations);
        verifier.setBaseDir(testDir);
        verifier.verifyFile(testXml);

        Assert.assertThat(violations.hasViolation(), is(true));
        Assert.assertThat(violations.getViolationCount(), is(2));

        List<String> expected = new ArrayList<String>();
        expected.add("Bad marketplace namespace declaration "
                        + "\"http://www.ebay.com/marketplace/services\" use "
                        + "\"http://www.ebayopensource.org/turmeric/common/v1/types\" instead.");

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
