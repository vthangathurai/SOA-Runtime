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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class PathRegex {
    private static final Logger LOG = Logger.getLogger(PathRegex.class.getName());
    private File basedir;
    private String basedirname;
    private int basedirnamelength;
    private Pattern pattern;

    public PathRegex(File basedir, String regex) {
        this.basedir = basedir;
        this.basedirname = this.basedir.getAbsolutePath() + File.separator;
        this.basedirnamelength = this.basedirname.length();
        this.pattern = Pattern.compile(regex);
    }

    public Matcher getMatcher(File path) {
        String relpath = getRelativePath(path);
        return pattern.matcher(relpath);
    }
    
    public File getBaseDir() {
        return basedir;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getRelativePath(File path) {
        String fullfilename = path.getAbsolutePath();
        if (!fullfilename.startsWith(basedirname)) {
            throw new IllegalArgumentException("File " + path + " is not part of the basedir " + basedir);
        }
        String relpath = fullfilename.substring(basedirnamelength);
        return FilenameUtils.separatorsToUnix(relpath);
    }

    public boolean matches(File path) {
        String relpath = getRelativePath(path);
        boolean match = pattern.matcher(relpath).matches();
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine((match ? "HIT " : "miss") + " on " + pattern + " : " + relpath);
        }
        return match;
    }
}
