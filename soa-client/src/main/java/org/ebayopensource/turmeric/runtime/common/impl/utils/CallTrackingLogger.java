/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallTrackingLogger extends Logger {

    public static final String USAGE_TRACKING = "!USAGE_TRACKING: ";
	public static final String NAME = "NAME=";
	public static final String ACCESS_TIME = "; ACCESS_TIME=";
	public static final String DURATION = "; DURATION=";

	
	
	private CallTrackingLogger(String  name, String resourceBundleName)	{
		super(name,resourceBundleName);
	}
	
	
	public static CallTrackingLogger getCodegenLogger(String subSystem){
		return new CallTrackingLogger(subSystem,null);
	}
	

	public static CallTrackingLogger getCodegenLogger(String subSystem, String resourceBundleName){
		return new CallTrackingLogger(subSystem,resourceBundleName);
	}

	
	//////////////////////////////////////////
	//Logging Entry/Exit
	//////////////////////////////////////////
	/**
	 * @see java.util.logging.Level#FINER
	 */
	public void entering()
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		entering(elements[1].getClassName(), elements[1].getMethodName());
	}
	
	/**
	 * @param param The parameter that passed in
	 * @see java.util.logging.Level#FINER
	 */
	public void entering(Object param)
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		entering(elements[1].getClassName(), elements[1].getMethodName(), param);
	}

	/**
	 * @param params The multiple parameters that passed in
	 * @see java.util.logging.Level#FINER
	 */
	public void entering(Object[] params)
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		entering(elements[1].getClassName(), elements[1].getMethodName(), params);
	}
	
	/**
	 * <p>Suitable for logging methods with <code>void</code> return type.</p>
	 * @see java.util.logging.Level#FINER
	 */
	public void exiting()
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		exiting(elements[1].getClassName(), elements[1].getMethodName());
	}

	/**
	 * @param result The result of the method call
	 * @see java.util.logging.Level#FINER
	 */
	public void exiting(Object result)
	{
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		exiting(elements[1].getClassName(), elements[1].getMethodName(), result);
	}
	
	
	 //////////////////////////////////////////
	//Error Logging
	//////////////////////////////////////////
	/**
	 * @param msg The error message
	 * @see java.util.logging.Level#SEVERE
	 */
	public void error(String msg) {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        logp(Level.SEVERE, elements[1].getClassName(), elements[1].getMethodName(), String.valueOf(msg));
	}
	/**
	 * @param message The error message
	 * @param cause The cause of the error
	 * @see java.util.logging.Level#SEVERE
	 */
	public void error(Object message, Throwable cause)
    {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        logp(Level.SEVERE, elements[1].getClassName(), elements[1].getMethodName(), String.valueOf(message), cause);
    }
	/**
	 * @param cause The cause of the error
	 * @see java.util.logging.Level#SEVERE
	 */
	public void error(Throwable cause)
    {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        logp(Level.SEVERE, elements[1].getClassName(), elements[1].getMethodName(),cause.getLocalizedMessage(), cause);
    }
	/**
	 * @param thrown The instance of <code>Throwable</code>
	 * @see java.util.logging.Level#SEVERE
	 */
	public void throwing(Throwable thrown) {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		throwing(elements[1].getClassName(), elements[1].getMethodName(), thrown);
	}
	
	
	//////////////////////////////////////////
	//Feature Usage Tracking
	//////////////////////////////////////////
	/**
	 * !USAGE_TRACKING: NAME=[FEATURE NAME]; ACCESS_TIME=[FEATURE ACCESS TIME as Date]; DURATION=[FEATURE USAGE DURATION in milliseconds] 
	 * @param featureName
	 * @param accessTime
	 * @param duration
	 */
	public void tracking(String featureName, Date accessTime, long duration)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(USAGE_TRACKING);
		buf.append(NAME);
		buf.append(featureName);
		buf.append(ACCESS_TIME);
		buf.append(accessTime);
		if (duration > 0)
		{
			buf.append(DURATION);
			buf.append(duration);
		}
		buf.append(";!");
		error(buf.toString());
	}
	
	public void tracking(String featureName, long duration)
	{
		new Date(System.currentTimeMillis());
		tracking(featureName, new Date(), duration);
	}
	
	public void tracking(String featureName, Date accessTime)
	{
		tracking(featureName, accessTime, -1);
	}
	
	public void tracking(String featureName)
	{
		tracking(featureName, new Date());
	}
	
	
	//////////////////////////////////////////
	//Info Logging
	//////////////////////////////////////////
	/**
	 * @param msg The message
	 * @see java.util.logging.Level#INFO
	 */
	public void info(Object msg) {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    	logp(Level.INFO, elements[1].getClassName(), elements[1].getMethodName(), String.valueOf(msg));
	}
	/**
	 * @return <code>True</code> if the <code>INFO</code> logging level is enabled, or <code>False</code> otherwise.
	 * @see java.util.logging.Level#INFO
	 */
	public boolean isInfoEnabled()
	{
		return isLoggable(Level.INFO);
	}

	//////////////////////////////////////////
	//Warning Logging
	//////////////////////////////////////////
	/**
	 * @param message The warning message
	 * @see java.util.logging.Level#WARNING
	 */
	public void warning(Object message)
    {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    	logp(Level.WARNING, elements[1].getClassName(), elements[1].getMethodName(), String.valueOf(message));
    }
    /**
     * <p>This method will work if the <code>WARNING</code> logging level is enabled.</p>
     * @param message The message
     * @param cause The cause of the problem
     * @see java.util.logging.Level#WARNING
     */
    public void warning(Object message, Throwable cause)
    {
    	StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    	logp(Level.WARNING, elements[1].getClassName(), elements[1].getMethodName(), String.valueOf(message), cause);
    }
	
    
    //////////////////////////////////////////
	//Debug Logging
	//////////////////////////////////////////
    /**
     * @param msg The debug message
     * @see java.util.logging.Level#FINE
     */
    public void debug(Object msg)
    {
    	StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        logp(Level.FINE, elements[1].getClassName(), elements[1].getMethodName(), String.valueOf(msg));
    }

	
	
}
