/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.utils;

/**
 * This class has static methods to check the arguments in the start of a method.
 * Code of the form 
 * <code>if (reference == null) {
 *          throw new NullPointerException();
 *       } </code>   
 * will be simplified as Preconditions.checkNotNull(reference).
 * 
 * @author prabhakhar kaliyamurthy
 */
public final class Preconditions {

    private Preconditions() { /* Do not allow instantiations */ }
    
    /**
     * Ensures the truth value of the expression passed.
     * Throws IllegalArgumentException if {@code expression} is false
     * 
     * @param expression a boolean expression
     */
    public static void checkArgument(boolean expression) {
        if(!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Ensures the truth value of the expression passed.
     * Throws IllegalArgumentException if {@code expression} is false
     * 
     * @param expression a boolean expression
     * @param errorMessage the exception string to use if the expression is false. 
     *    converted using {@link String#valueOf(Object)}  
     */
    public static void checkArgument(boolean expression, Object errorMessage) {
        if(!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }
    
    /**
     * Ensures the reference passed is not null.
     * Throws NullPointerException if {@code reference} is null
     * 
     * @param <T> An Object Type
     * @param reference object reference to check
     * @return the validated non-null reference
     */
    public static <T> T checkNotNull(T reference) {
        return checkNotNull(reference, new NullPointerException());
    }
    
    /**
     * Ensures the reference passed is not null.
     * Throws NullPointerException if {@code reference} is null
     *  
     * @param <T> An Object Type
     * @param reference object reference to check
     * @param rtException this exception to be thrown when the reference is null
     * @return the validated non-null reference
     */
    public static <T> T checkNotNull(T reference, RuntimeException rtException) {
        if (reference == null) {
            throw rtException;
        }        
        return reference;
    }
    
    /**
     * 
     * Ensures the reference passed is not null.
     * Throws NullPointerException if {@code reference} is null
     *  
     * @param <T> An Object Type
     * @param reference object reference to check
     * @param errorMessage the exception message to use if the check fails; using {@code String#valueOf(Object)} 
     * @return the validated non-null reference
     */
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }        
        return reference;
    }    
}
