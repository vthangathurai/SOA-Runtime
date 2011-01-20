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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Violations implements Report, Iterable<Violation> {
    private List<Violation> violations = new ArrayList<Violation>();
    private File activeFile;
    private int fileCount;
    private int violationCount;
    private int fileViolationCount;
    private boolean hasViolation;

    public Violations() {
        this.fileCount = 0;
        this.violationCount = 0;
        this.fileViolationCount = 0;
    }

    public String createReport() {
        StringBuilder msg = new StringBuilder();
        msg.append("Found ").append(violations.size()).append(" configuration violation(s)");
        File lastFile = null;
        for (Violation viol : violations) {
            if (!(viol.getFile().equals(lastFile))) {
                msg.append("\n Config Violation: ").append(viol.getFile());
            }
            msg.append("\n   * ").append(viol.getContext());
            msg.append("\n     ").append(viol.getMsg());
            lastFile = viol.getFile();
        }
        msg.append("\n## Reported ").append(violations.size())
                        .append(" configuration violation(s)");
        return msg.toString();
    }

    public Violation get(int index) {
        return violations.get(index);
    }

    public boolean hasViolation() {
        return violationCount > 0;
    }

    @Override
    public Iterator<Violation> iterator() {
        return violations.iterator();
    }

    @Override
    public void fileStart(File file) {
        this.activeFile = file;
        this.hasViolation = false;
        this.fileCount++;
    }

    @Override
    public void fileEnd() {
        if (hasViolation) {
            this.fileViolationCount++;
        }
    }

    @Override
    public void violation(String context, String format, Object... args) {
        this.violationCount++;
        this.hasViolation = true;
        violations.add(new Violation(activeFile, context, String.format(format, args)));
    }

    @Override
    public int getFileCount() {
        return this.fileCount;
    }

    @Override
    public int getFileViolationCount() {
        return this.fileViolationCount;
    }

    @Override
    public int getViolationCount() {
        return this.violationCount;
    }
}
