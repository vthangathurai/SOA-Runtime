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

public interface Report {
    /**
     * A File has started to be scanned.
     * 
     * @param file
     *            the file being scanned.
     */
    void fileStart(File file);

    /**
     * A File has been finished being scanned.
     */
    void fileEnd();

    /**
     * A violation has been found.
     * 
     * @param context
     *            the context within the file for where the violation was found
     * @param format
     *            the message as a {@link String#format(String, Object...)} format String
     * @param args
     *            the arguments for the format string
     */
    void violation(String context, String format, Object... args);

    /**
     * A count of the number of files scanned.
     * 
     * @return the number of files
     */
    int getFileCount();
    
    /**
     * A count of violations that have occurred.
     * <p>
     * Note: this is the number of raw violations.  
     * This count can be larger than the number of files from {@link #getFileCount()}
     * 
     * @return the number of violations
     */
    int getViolationCount();
    
    /**
     * A count of files with violations.
     * 
     * @return the number of files with violations.
     */
    int getFileViolationCount();
}
