/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Utility methods for working with the {@link Thread#getContextClassLoader()} in a consistent fashion.
 */
public class ContextClassLoaderUtil {
    private final static Logger LOG = Logger.getLogger(ContextClassLoaderUtil.class.getName());

    /**
     * Return the InputStream of the resource provided, following the {@link #findResource(String)} rules.
     * 
     * @param path
     *            the resource to find.
     * @return the {@link InputStream} to the resource, or null if not found.
     * @throws IOException
     *             if unable to open the {@link InputStream}.
     * @see {@link URL#openStream()}
     */
    public static InputStream getResourceAsStream(String path) throws IOException {
    	ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(path);                  
    }

    /**
     * Look for a single resource in the classloader.
     * <p>
     * Logic on what is returned.
     * <ol>
     * <li>If one resource match is found, return it.</li>
     * <li>If more than one is found, return the project local version.</li>
     * <li>Return first hit</li>
     * </ol>
     * 
     * @param path
     *            the path to look for
     * @return the URL of the found resource, following the rules above.
     */
    public static URL findResource(String path) {
        LOG.info("Attempting to find resource: " + path);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> hits = cl.getResources(path);
            if (!hits.hasMoreElements()) {
                LOG.info("Resource does not exist: " + path);
                return null; // no hits
            }
            @SuppressWarnings("unchecked")
            List<URL> urls = Collections.list(hits);
            if (urls.size() > 1) {
                // Local always takes precedence.
                URL local = null;
                LOG.warning(path + " (MORE THAN 1 FOUND)");
                for (URL url : urls) {
                    LOG.warning(path + " = " + url);
                    if (url.getProtocol().equals("file")) {
                        local = url;
                    }
                }

                // Return local hit (if found)
                if (local != null) {
                    LOG.info(path + " RETURNING LOCAL at " + local.toExternalForm());
                    return local;
                }
            }

            URL url = urls.get(0);
            LOG.info(path + " FOUND at " + url.toExternalForm());
            return url;
        }
        catch (IOException e) {
            LOG.log(Level.INFO, "findResource: " + path, e);
        }
        return null;
    }

    /**
     * Load a class from the context class loader.
     * 
     * @param fullyQualifiedClassName
     *            the class to load from the context classloader.
     * @return the loaded class.
     * @throws ClassNotFoundException
     *             if unable to find the class.
     * @see #loadOptionalClass(String)
     */
    public static Class<?> loadClass(String fullyQualifiedClassName) throws ClassNotFoundException {
        LOG.fine("Attempting to load class: " + fullyQualifiedClassName);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.loadClass(fullyQualifiedClassName);
    }

    /**
     * Similar to {@link #loadClass(String)} but doesn't throw a {@link ClassNotFoundException} if unable to find the
     * class, just returns null (to make logic easier to code against).
     * <p>
     * Emits a log message on {@link ClassNotFoundException} at {@link Level#FINE} level.
     * 
     * @param fullyQualifiedClassName
     *            the classname to load.
     * @return the class, or null if not found.
     */
    public static Class<?> loadOptionalClass(String fullyQualifiedClassName) {
        LOG.fine("Attempting to load optional class: " + fullyQualifiedClassName);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            return cl.loadClass(fullyQualifiedClassName);
        }
        catch (ClassNotFoundException e) {
            LOG.log(Level.FINE, "Unable to find class: " + fullyQualifiedClassName, e);
            return null;
        }
    }

    /**
     * Similar to {@link #loadOptionalClass(String)} but throws a {@link ClassNotFoundException} if unable to find the
     * class
     * 
     * @param fullyQualifiedClassName
     *            the classname to load.
     * @return the class
     * @throws CodeGenFailedException
     *             if unable to find the required class
     */
    public static Class<?> loadRequiredClass(String fullyQualifiedClassName) throws CodeGenFailedException {
        LOG.fine("Attempting to load required class: " + fullyQualifiedClassName);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            return cl.loadClass(fullyQualifiedClassName);
        }
        catch (ClassNotFoundException e) {
            throw new CodeGenFailedException("Failed to load class : " + fullyQualifiedClassName, e);
        }
    }
}
