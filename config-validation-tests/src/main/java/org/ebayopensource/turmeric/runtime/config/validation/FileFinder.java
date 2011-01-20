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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public final class FileFinder {
    private FileFinder() {
        /* prevent instantiation */
    }
    
    public static List<File> findFileMatches(File dir, String regex) {
        PathRegex pathregex = new PathRegex(dir, regex);
        return findFileMatches(pathregex);
    }

    public static List<File> findFileMatches(PathRegex pathregex) {
        List<File> hits = new ArrayList<File>();

        AddMatchingFilesFilter filter = new AddMatchingFilesFilter(pathregex, hits);        
        recursiveAddMatchingFiles(pathregex.getBaseDir(), filter);

        return hits;
    }

    private static void recursiveAddMatchingFiles(File dir, FileFilter filter) {
        for (File path : dir.listFiles()) {
            if (path.isFile()) {
                filter.accept(path);
                continue;
            }

            if (path.isDirectory()) {
                recursiveAddMatchingFiles(path, filter);
            }
        }
    }
}
