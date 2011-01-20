/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library;

import java.io.File;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.NonInteractiveCodeGen;
import org.junit.Test;

/**
 * Purpose is to test the generation of the newly renamed/repackaged common type library generation.
 */
public class TypeLibraryFromCommonTest extends AbstractServiceGeneratorTestCase {

    /**
     * The generation command line as seen from the build for /runtime/common-type-library/
     */
    @Test
    public void testGenerateCommonTypeLibrary() throws Exception {
        testingdir.ensureEmpty();
        
        TestResourceUtil.copyResourceRootDir("common-typelib", testingdir);
        File rootDir = testingdir.getDir();
        File jdestDir = testingdir.getFile("generated-sources");
        File mdestDir = testingdir.getFile("generated-resources");
        File metasrcDir = testingdir.getFile("src-main-resources");
        File binDir = testingdir.getFile("bin");
        
        MavenTestingUtils.ensureDirExists(jdestDir);
        MavenTestingUtils.ensureDirExists(mdestDir);
        MavenTestingUtils.ensureDirExists(binDir);
        
        // @formatter:off
        String args[] = {
            "-gentype", "genTypeCleanBuildTypeLibrary", 
            "-pr", rootDir.getAbsolutePath(),
            "-jdest", jdestDir.getAbsolutePath(),
            "-mdest", mdestDir.getAbsolutePath(),
            "-metasrc", metasrcDir.getAbsolutePath(),
            "-libname", "common-type-library",
            "-libversion", "1.0.0",
            "-libNamespace", "http://www.ebayopensource.org/turmeric/common/v1/types",
            "-libCategory", "COMMON"
        };
        // @formatter:on
        
        NonInteractiveCodeGen gen = new NonInteractiveCodeGen();
        gen.addExtraClassPath(binDir);
        gen.addExtraClassPath(metasrcDir);
        gen.addJavaToolsClassPath();
        gen.execute(args);
    }
}
