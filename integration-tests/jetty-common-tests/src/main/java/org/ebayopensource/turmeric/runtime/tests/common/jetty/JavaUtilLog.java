/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;
import org.mortbay.log.Logger;

/**
 * Output Jetty Logs to {@link java.util.logging.Logger}
 */
public class JavaUtilLog implements Logger {
	private java.util.logging.Logger logger;

	public static void init(String name) {
		System.setProperty("org.mortbay.log.class", JavaUtilLog.class.getName());
		Log.setLog(new JavaUtilLog(name));
	}

	public JavaUtilLog() {
		this(null);
	}

	public JavaUtilLog(String name) {
		StringBuilder logname = new StringBuilder();
		logname.append("jetty");
		if(name != null) {
			logname.append('.').append(name);
		}
		
		logger = java.util.logging.Logger.getLogger(logname.toString());
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	public void setDebugEnabled(boolean enabled) {
		logger.setLevel(Level.FINE);
	}
	
	public void info(String msg, Object arg0, Object arg1) {
		if(logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, format(msg, arg0, arg1) );
		}
	}

	public void debug(String msg, Throwable th) {
		if (logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, msg, th);
		}
	}

	public void debug(String msg, Object arg0, Object arg1) {
		if (logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, format(msg, arg0, arg1));
		}
	}

	public void warn(String msg, Object arg0, Object arg1) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.log(Level.WARNING, format(msg, arg0, arg1));
		}
	}

	public void warn(String msg, Throwable th) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.log(Level.WARNING, msg, th);
		}
	}

	private String format(String msg, Object arg0, Object arg1) {
		int i0 = msg.indexOf("{}");
		int i1 = i0 < 0 ? -1 : msg.indexOf("{}", i0 + 2);

		if (arg1 != null && i1 >= 0)
			msg = msg.substring(0, i1) + arg1 + msg.substring(i1 + 2);
		if (arg0 != null && i0 >= 0)
			msg = msg.substring(0, i0) + arg0 + msg.substring(i0 + 2);
		return msg;
	}

	public Logger getLogger(String name) {
		if(StringUtils.isBlank(name)) {
			return this;
		}
		
		return new JavaUtilLog(logger.getName() + "." + name);
	}

	public String toString() {
		return "JavaUtilLog:" + logger.getName();
	}

}
