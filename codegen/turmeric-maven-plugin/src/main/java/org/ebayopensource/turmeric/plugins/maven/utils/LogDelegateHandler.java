/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.utils;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.maven.plugin.logging.Log;

/**
 * <p>This class is intended to be added to the JDK Logging framework. 
 * This would ensure any logging messages will go through it so that 
 * we could reformat the log message to follow the SOA logging format.</p>
 * @author Yang Yu(yayu@ebay.com)
 *
 */
public class LogDelegateHandler 
extends Handler
{
	private Log mavenPluginLogger;
    
    public LogDelegateHandler(Log log) {
		super();
		this.mavenPluginLogger = log;
	}
	@Override
    public void close() throws SecurityException
    {
		/* do nothing */
    }
	
    @Override
    public void flush()
    {
    	/* do nothing */
    }

    @Override
    public void publish( final LogRecord record )
    {
        if( record == null || record.getMessage() == null) {
            return;
        }
        
        int level = record.getLevel().intValue();
        String msg = record.getMessage();
        Throwable thrown = record.getThrown();
        
        // Severe or above
        if(level >= Level.SEVERE.intValue()) {
        	if(thrown == null) {
        		mavenPluginLogger.error(msg);
        	} else {
        		mavenPluginLogger.error(msg, thrown);
        	}
        	return;
        } 
        
        // Warning or above
        if(level >= Level.WARNING.intValue()) {
        	if(thrown == null) {
        		mavenPluginLogger.warn(msg);
        	} else {
        		mavenPluginLogger.warn(msg, thrown);
        	}
        	return;
        }
        
        // Info or above
        if (level >= Level.INFO.intValue()) {
        	if(thrown == null) {
        		mavenPluginLogger.info(msg);
        	} else {
        		mavenPluginLogger.info(msg, thrown);
        	}
        	return;
        } 
        
        // All else is debug
    	if(thrown == null) {
    		mavenPluginLogger.debug(msg);
    	} else {
    		mavenPluginLogger.debug(msg, thrown);
    	}
    }
}
