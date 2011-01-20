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
import java.util.regex.Matcher;

import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;

public class PathRegexTest {

    @Test
    public void testMatcherGroups() {
        File testResDir = MavenTestingUtils.getTestResourcesDir();
        PathRegex pregex = new PathRegex(testResDir, "META-INF/soa/client/([^/]+)/([^/]+)/ClientConfig.xml");
        File testXml = PathAssert.assertFileExists(testResDir, "META-INF/soa/client/config/default/ClientConfig.xml");

        Assert.assertTrue("Should match", pregex.matches(testXml));
        Matcher match = pregex.getMatcher(testXml);
        Assert.assertTrue("Matcher.find", match.find());
        Assert.assertNotNull("Should not be null", match);
        
        Assert.assertThat("match.groupCount", match.groupCount(), is(2));
        Assert.assertThat("match.group(1)", match.group(1), is("config"));
        Assert.assertThat("match.group(2)", match.group(2), is("default"));
    }
}
