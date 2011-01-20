/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ebay.kernel.util.StringUtils;

public final class LogManager {
	
	private static final Level m_traceLevel = Level.FINE;
	private static final Level m_defaultLevel = Level.INFO;
	public static final String GLOBAL_LOGGER_ID = "org.ebayopensource.turmeric.runtime";
	private static final String GENERAL_LOGGER_ID ="";
	private static Properties m_codegenLoggingProperties = new Properties();
	//private static final String m_defaultConfigFilePath = ".\\src\\com\\ebay\\soaframework\\common\\config\\default_codegen_logging.config"; 
	
    private static FileHandler m_fileHandler; 
	private static volatile boolean m_isTracingEnabled = Logger.getLogger(GLOBAL_LOGGER_ID).isLoggable(m_traceLevel);
	private static final int FILE_SIZE_LIMIT = 2 * 1048576; // X * MB = X MB
	private static final int FILE_COUNT = 1;
	
	private static Hashtable<String,CallTrackingLogger> loggers =  new Hashtable<String,CallTrackingLogger>();

	private static CallTrackingLogger s_logger =  LogManager.getInstance(LogManager.class);
	
	private static CallTrackingLogger getLogger() {
		return s_logger;
	}
	
	public static CallTrackingLogger getInstance(Class clazz) {
		// We want 1 logger per subsystem
		String subsystem = "";
		if (clazz != null) {
			// TODO: after plugin added SOABinding in. add this back
			// subsystem = BindingUtils.getPackageName(clazz);
			if (subsystem.length() == 0) {
				subsystem = clazz.getName();
			}
		} else {
			subsystem = "";
		}

		CallTrackingLogger codegenLogger = getCodegenLogger(subsystem);
		
		return codegenLogger; 
	}

	
	private LogManager() {
		// no instances
	}

	
	/**
	 * If the tracing is enabled, then the logging level will be setup accordingly.
	 * @param clazz
	 * @return An instance of <code>LogManager</code>
	 */
	public static synchronized CallTrackingLogger getCodegenLogger(String clazz)
	{
		java.util.logging.LogManager manager = java.util.logging.LogManager.getLogManager();
		Logger result = manager.getLogger(clazz);
		
		if (result instanceof CallTrackingLogger) {
		    return (CallTrackingLogger)result;
		}
		else if (result != null)
		{//there is an existing logger instance
			if (loggers.keySet().contains(clazz))
			{
				return loggers.get(clazz);
			}
			CallTrackingLogger logger = CallTrackingLogger.getCodegenLogger(clazz);
			logger.setLevel(result.getLevel());
			logger.setFilter(result.getFilter());
			logger.setParent(result.getParent());
			logger.setUseParentHandlers(logger.getUseParentHandlers());
			loggers.put(clazz, logger);
			return logger;
		}
		else
		{//can not find a logger, so let's create one.
			result = CallTrackingLogger.getCodegenLogger(clazz);
			manager.addLogger(result);
			return (CallTrackingLogger)result;
		}
	}
	
	
	
	private static void deriveLoggingAttributes(String configFilePath){
		
		    boolean isConfigFilePathValid = false;
		    boolean isNewFileEveryTime = true;
		    boolean enableTraceLevelLogging = false;
		    File defaultDummyConfigFile = null;
		
		    if (configFilePath != null){
		    	File configFile = new File(configFilePath);
		    	if (configFile.exists())
		    		isConfigFilePathValid = true;
		    	else
		    		getLogger().log(Level.INFO, "The codegen logging config file path does not exist at : " + configFilePath);
		    }
		    
		    if(!isConfigFilePathValid) {
		       	try {
					 defaultDummyConfigFile = File.createTempFile("soa_dummy", "config_file");
					 defaultDummyConfigFile.deleteOnExit();
					configFilePath = defaultDummyConfigFile.getAbsolutePath();
				} catch (IOException e) {
					configFilePath ="";		
				}
				
				getLogger().log(Level.INFO, "The codegen logging is using default values since the logging config file mentioned by user is not found");		    	
		    }
		    
		    
		    FileInputStream logConfigFileInStream = null;
		    try {
				if (configFilePath != null && !configFilePath.equals("")) {
					logConfigFileInStream = new FileInputStream(configFilePath);
					m_codegenLoggingProperties.load(logConfigFileInStream);
				}
		    	
		    	String alwaysCreateLogFile =  getPropertyFromPropertiesWithDefault(m_codegenLoggingProperties,"ALWAYS_CREATE_LOG_FILE","false");
		    	String enableTrace = getPropertyFromPropertiesWithDefault(m_codegenLoggingProperties,"ENABLE_TRACE","false");
		    	String traceDirectory = getPropertyFromPropertiesWithDefault(m_codegenLoggingProperties,"TRACE_DIRECTORY",System.getProperty("user.home"));
		    	String traceFileName  = getPropertyFromPropertiesWithDefault(m_codegenLoggingProperties,"TRACE_FILE","codegen_log");
		    	String newFileEveryTime = getPropertyFromPropertiesWithDefault(m_codegenLoggingProperties,"NEW_FILE_EVERY_TIME","false");
		    	String fileFormat = getPropertyFromPropertiesWithDefault(m_codegenLoggingProperties,"FILE_FORMAT","PLAIN_TEXT");
		    	
		    	
		    	
                if(enableTrace.equalsIgnoreCase("true"))
                	enableTraceLevelLogging = true;
                else {
                	enableTraceLevelLogging = false;
                	if(!alwaysCreateLogFile.equalsIgnoreCase("true"))
                		return;
                }
                
                
                while(traceDirectory.endsWith("\\") || traceDirectory.endsWith("/")){
                	traceDirectory = traceDirectory.substring(0,traceDirectory.length() - 1);
                }
                
                String fileHandlerPath = traceDirectory + "/" + traceFileName + "_%u_"  + ".log";
		    	
                if(newFileEveryTime.equalsIgnoreCase("true"))
                	isNewFileEveryTime = true;
                else
                	isNewFileEveryTime = false;
                
                m_fileHandler = new FileHandler(fileHandlerPath,FILE_SIZE_LIMIT,FILE_COUNT,!isNewFileEveryTime);
                if(!fileFormat.equalsIgnoreCase("XML"))
                	m_fileHandler.setFormatter(new java.util.logging.SimpleFormatter());
                
                
                if(enableTraceLevelLogging)
                	enableTracing();
                else
                	enableLoggingToFile();
                
                System.out.println("The codegen log file is created in the directory : " + traceDirectory); //KEEPME
		    	
		    } catch (FileNotFoundException e) {
		    	getLogger().log(Level.INFO, "Exception in deriveLoggingAttributes : " + e.getMessage());
		    	return;
		    } catch (IOException e) {
		    	getLogger().log(Level.INFO, "Exception in deriveLoggingAttributes : " + e.getMessage());
		    	return;
		    } 
		    finally {
		    	
		    	try {
		    		if (logConfigFileInStream != null)
		    			logConfigFileInStream.close();
		    	} catch (IOException e) {
		    		return;
		    	}
		    	// need to delete dummyFile if it exists
		    	if (defaultDummyConfigFile != null) {
		    		s_logger.log(Level.FINE, "Trying to Delete soa_dummy file at " + defaultDummyConfigFile.getAbsolutePath());
		    		if (defaultDummyConfigFile.delete()) {
		    			s_logger.log(Level.FINE, "soa_dummy deleted");
		    		} else
		    			s_logger
		    			.log(Level.INFO,
		    			"Could not delete soa_dummy  at " + defaultDummyConfigFile.getAbsolutePath() + "InputStream could not be closed");
		    	}
		    		
		    }
			
	}
	
	
	/*
	 * Debug Logging
	 */
    /**
     * This is a way to share the logging level between Plugin and CodeGen.
     * @return The logger with an empty name
     */
    private static Logger getGlobalLogger()
    {
    	return Logger.getLogger( GLOBAL_LOGGER_ID );
    }
    
    public static Logger getGeneralLogger() {
    	return Logger.getLogger(GENERAL_LOGGER_ID);
    }
    

    /**
     * <p>Enable the Tracing, and set the logging level to <code>Level.FINE</code></p>
     * @see java.util.logging.Level#FINER
     */
    private static void enableTracing()
    {
    	if (m_isTracingEnabled == false)
    	{
    		m_isTracingEnabled = true;
    		getGlobalLogger().setLevel(m_traceLevel);
    		getGeneralLogger().setLevel(Level.INFO);
    		
    		//adding the filehandler to the general logger would suffice for the global logger as well, since the global logger is a subset of the general logger
    		getGeneralLogger().addHandler(m_fileHandler);
    	}
    	
    }
    
    private static void enableLoggingToFile(){
		getGlobalLogger().setLevel(m_defaultLevel);
		getGeneralLogger().setLevel(Level.INFO);
		//adding the filehandler to the general logger would suffice for the global logger as well, since the global logger is a subset of the general logger
		getGeneralLogger().addHandler(m_fileHandler);
    	
    }
    

    public static void initilizeLoggingCriteria(String configFilePath){
    	deriveLoggingAttributes(configFilePath);
    }
    
    
    /**
     * <p>Disable the Tracing, and set the logging level to <code>Level.INFO</code></p>
     * @see java.util.logging.Level#INFO
     */
    private static void disableTracing()
    {
    	if (m_isTracingEnabled == true)
    	{
    		m_isTracingEnabled = false;
    		getGlobalLogger().setLevel(m_defaultLevel);
    		getGeneralLogger().setLevel(Level.INFO);
    		
    		if(m_fileHandler != null)
    			getGlobalLogger().removeHandler(m_fileHandler);
    	}
    }
    
    /**
     * @return <code>True</code> if the Tracing/Debugging is enabled, or <code>False</code> otherwise.
     */
    public static boolean isTracingEnabled()
    {
    	return m_isTracingEnabled;
    }
    
    
    
    // utility methods
    
    private static String getPropertyFromPropertiesWithDefault (Properties properties,String propertyName, String defaultValue) {
    	String result = properties.getProperty(propertyName);
    	if(isEmptyString(result))
    		result = defaultValue;
    	
    	return result;
    }
    
    private static boolean isEmptyString(String str) {
		return (str == null || str.trim().length() == 0);
	}
	
}
