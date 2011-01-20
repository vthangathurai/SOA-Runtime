/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools;

import static org.hamcrest.Matchers.*;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;

/**
 * Generic Asserts for Generated Content.
 */
public final class GeneratedAssert {
    private GeneratedAssert() {
        /* prevent instantiation */
    }

    public static File assertFileExists(File outputDir, String path) {
        File expectedFile = new File(outputDir, FilenameUtils.separatorsToSystem(path));
        Assert.assertThat("Generated Path should exist: " + 
                expectedFile.getAbsolutePath(), expectedFile.exists(), is(true));
        Assert.assertThat("Generated Path should be a file: " + 
                expectedFile.getAbsolutePath(), expectedFile.isFile(), is(true));
        return expectedFile;
    }
    
    public static File assertJavaExists(File outputDir, String classname) {
        String filename = classname.replace(".",File.separator) + ".java";
        File expectedFile = new File(outputDir, filename);
        Assert.assertThat("Generated Java File should exist (" + classname + "): " + 
                expectedFile.getAbsolutePath(), expectedFile.exists(), is(true));
        Assert.assertThat("Generated Java File should be a file (" + classname + "): " +
                expectedFile.getAbsolutePath(), expectedFile.isFile(), is(true));
        return expectedFile;
    }
    
    public static File assertJavaNotExists(File outputDir, String classname) {
        String filename = classname.replace(".",File.separator) + ".java";
        File expectedFile = new File(outputDir, filename);
        Assert.assertThat("Generated Java should NOT exist (" + classname + "): " +
                expectedFile.getAbsolutePath(), expectedFile.exists(), is(false));
        return expectedFile;
    }

    public static File assertDirExists(File outputDir, String path) {
        File expectedDir = new File(outputDir, FilenameUtils.separatorsToSystem(path));
        Assert.assertThat("Generated Path should exist: " + 
                expectedDir.getAbsolutePath(), expectedDir.exists(), is(true));
        Assert.assertThat("Generated Path should be a directory: " + 
                expectedDir.getAbsolutePath(), expectedDir.isDirectory(), is(true));
        return expectedDir;
    }

    public static void assertPathNotExists(File outputDir, String path) {
        File expectedFile = new File(outputDir, FilenameUtils.separatorsToSystem(path));
        Assert.assertThat("Generated Path should NOT exist: " + expectedFile.getAbsolutePath(), 
                        expectedFile.exists(), is(false));
    }
}
