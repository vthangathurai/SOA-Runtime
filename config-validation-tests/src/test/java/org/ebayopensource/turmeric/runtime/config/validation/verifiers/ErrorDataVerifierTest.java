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

public class ErrorDataVerifierTest {
    @Test
    public void testVerify() {
        File testDir = MavenTestingUtils.getTestResourcesDir();
        File testXml = MavenTestingUtils.getTestResourceFile("META-INF/errorlibrary/BadErrors/ErrorData.xml");

        ErrorDataVerifier verifier = new ErrorDataVerifier();
        Violations violations = new Violations();
        verifier.setReport(violations);
        verifier.setBaseDir(testDir);
        verifier.verifyFile(testXml);

        Assert.assertThat(violations.hasViolation(), is(true));
        Assert.assertThat(violations.getViolationCount(), is(4));

        List<String> expected = new ArrayList<String>();
        expected.add("Domain name declared in file [IntentionallyWrongDomain] must match the directory name in the path to the file [BadErrors] (case is important!)");
        expected.add("Found 2 duplicates for id [2000] = names [svc_factory_unable_to_register_schema, svc_rt_unknown_operation]");
        expected.add("Found 2 duplicates for id [1003] = names [svc_factory_custom_ser_no_bound_type, svc_factory_custom_deser_no_bound_type]");
        expected.add("Found 3 duplicates for name [blah_blah] = ids [5010, 7001, 8002]");

        for (Violation viol : violations) {
            expected.remove(viol.getMsg());
        }

        if (expected.size() > 0) {
            StringBuilder err = new StringBuilder();
            err.append(expected.size());
            err.append(" expected error message(s) not found!");
            for(String expect: expected) {
                err.append("\n").append(expect);
            }
            System.out.println(err.toString());
            
            System.out.println("--Actual Error Messages");
            for(Violation viol: violations) {
                System.out.println(viol);
            }
            
            Assert.fail(err.toString());
        }
    }
}
