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

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.runtime.config.validation.Violations;
import org.junit.Assert;
import org.junit.Test;

public class ClientConfigVerifierTest {
    @Test
    public void testVerifyNoNamespace() {
        File testDir = MavenTestingUtils.getTestResourcesDir();
        File testXml = MavenTestingUtils
                        .getTestResourceFile("META-INF/soa/client/config/defaultNameSpace/ClientConfig.xml");

        ClientConfigVerifier verifier = new ClientConfigVerifier();
        Violations violations = new Violations();
        verifier.setReport(violations);
        verifier.setBaseDir(testDir);
        verifier.verifyFile(testXml);

        Assert.assertThat(violations.hasViolation(), is(true));
        Assert.assertThat(violations.getViolationCount(), is(1));

        String actualMsg = violations.get(0).getMsg();
        Assert.assertThat(actualMsg, is("Must have the default namespace declaration of "
                        + "<client-config-list xmlns=\"http://www.ebayopensource.org/turmeric/common/config\"/>"
                        + ", but found none"));
    }
}
