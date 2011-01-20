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
import java.util.List;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;

public class FileFinderTest {
    @Test
    public void testFileFind() {
        File testResDir = MavenTestingUtils.getTestResourcesDir();
        List<File> hits = FileFinder.findFileMatches(testResDir, "META-INF/soa/client/[^/]*/[^/]*/ClientConfig.xml");
        Assert.assertThat("hits.size", hits.size(), is(2));
    }
}
