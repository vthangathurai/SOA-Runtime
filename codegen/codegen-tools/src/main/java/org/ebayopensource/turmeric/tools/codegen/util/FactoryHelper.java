/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;


public class FactoryHelper {
    
    private static Object newInstance(String provider, String className, ClassLoader classLoader) throws PreProcessFailedException
    {
        try {
            Class<?> clazz = loadClass(className, classLoader);
            return clazz.newInstance();
        } catch (ClassNotFoundException exception) {
        	throw new PreProcessFailedException("Provider for " + provider + " not found", exception);
        } catch (Exception exception) {
        	throw new PreProcessFailedException("Provider for " + provider + " could not be instantiated or may not be accessible: ", exception);
        }
    }
   
    public static Object findFactory(String factoryId, String defaultImpl) throws PreProcessFailedException
    {
    	CodeGenClassLoader classLoader = null;
        try {
        	ClassLoader parent = Thread.currentThread().getContextClassLoader().getParent();
        	if(parent instanceof CodeGenClassLoader) {
        		classLoader = (CodeGenClassLoader) parent;
        	}
        } catch (Exception exception) {
        	throw new PreProcessFailedException(exception.getMessage(), exception);
        }
     
        try {
            String systemProp = System.getProperty( factoryId );
            if( systemProp!=null) {
                return newInstance(factoryId, systemProp, classLoader);
            }
        } catch (Exception exception) {
        	// TODO throw exception or go with our impl
        }

        FileInputStream in = null;
        try {
            String javah=System.getProperty( "java.home" );
            String configFile = javah + File.separator +
                "lib" + File.separator + "ebaysoa.properties";
            File f=new File( configFile );
            if( f.exists()) {
                Properties props=new Properties();
                in = new FileInputStream(f);
                props.load(in);
                String factoryClassName = props.getProperty(factoryId);
                return newInstance(factoryId, factoryClassName, classLoader);
            } 
        } catch(Exception exception) {
        	//TODO throw exception or go with our impl
        } finally {
        	CodeGenUtil.closeQuietly(in);
        }
      
        if (defaultImpl == null) {
        	throw new PreProcessFailedException("Provider for " + factoryId + " cannot be found");        	 
        }
        	
        return newInstance(factoryId, defaultImpl, classLoader);
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) {
            return Class.forName(className);
        }
        
        return classLoader.loadClass(className);
    }

}

