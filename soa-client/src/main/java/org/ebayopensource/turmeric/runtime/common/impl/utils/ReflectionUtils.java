/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author ichernyshev
 */
public final class ReflectionUtils {
    private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());

	private ReflectionUtils() {
		// no instances
	}

	public static <T> Class<T> loadClass(String className, Class<T> targetType, ClassLoader cl)
		throws ServiceException
	{
		return loadClass(className, targetType, false, cl);
	}

	public static <T> Class<T> loadClass(String className, Class<T> targetType,
		boolean ignoreMissingClass, ClassLoader cl)
		throws ServiceException
	{
		String targetTypeName;
		if (targetType != null) {
			targetTypeName = targetType.getName();
		} else {
			targetTypeName = "(unspecified assignment type)";
		}

		Class clazz;
		try {
			clazz = Class.forName(className, true, cl);
		} catch (NoClassDefFoundError err) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INST_NOT_FOUND,
					ErrorConstants.ERRORDOMAIN, new Object[] {targetTypeName, className}), err);
		} catch (ClassNotFoundException e) {
			if (ignoreMissingClass) {
				return null;
			}

			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INST_NOT_FOUND,
					ErrorConstants.ERRORDOMAIN, new Object[] {targetTypeName, className}), e);
		}

		if (targetType != null && !targetType.isAssignableFrom(clazz)) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INST_CANNOT_CAST,
					ErrorConstants.ERRORDOMAIN, new Object[] {targetTypeName, className}));
		}

		@SuppressWarnings("unchecked")
		Class<T> result = clazz;

		return result;
	}

	public static <T> T createInstance(String className, Class<T> targetType, ClassLoader cl)
		throws ServiceException
	{
		return createInstance(className, targetType, cl, null, null);
	}

	public static <T> T createInstance(String className, Class<T> targetType,
		ClassLoader cl, Class[] paramTypes, Object[] params)
		throws ServiceException
	{
		Class<T> clazz = loadClass(className, targetType, cl);

		return createInstance(clazz, paramTypes, params);
	}

	public static <T> T createInstance(Class<T> clazz)
		throws ServiceException
	{
		return createInstance(clazz, null, null);
	}

	public static <T> T createInstance(Class<T> clazz, Class[] paramTypes, Object[] params)
		throws ServiceException
	{
		Object result;
		try {
			if (paramTypes != null) {
				Constructor con = clazz.getConstructor(paramTypes);
				result = con.newInstance(params);
			} else {
				result = clazz.newInstance();
			}
		} catch (IllegalAccessException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INST_ILLEGAL_ACCESS,
					ErrorConstants.ERRORDOMAIN, new Object[] {clazz.getName()}), e);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INST_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {clazz.getName()}), e);
		}

		// type cast cannot be done, but we've checked isAssignableFrom
		@SuppressWarnings("unchecked")
		T result2 = (T)result;

		return result2;
	}
	
 	public static Method findMatchingJavaMethod(Class<?> clz, String xmlElement) {
		
		//1. iterate thru all the methods of the java bean and find a matching
		// method w/ the same xmlElement name
		Method[] methods = clz.getDeclaredMethods();
		String methodName = toGetMethodName(xmlElement);
		// look from property fields
		Field[] fields = clz.getDeclaredFields();
		for (int i = 0; i<fields.length; i++) {
			XmlElement element = fields[i].getAnnotation(XmlElement.class);
			if (element != null) {
				// if there is a property-xml mapping, use that to check to find a match
				if (xmlElement.equalsIgnoreCase(element.name())) {
					// Found a match
					// System.out.println("matching name found property (map):" + xmlElement);
					// use this field name to find the corresponding method name
					return getMethodFromField(clz, fields[i]);
				}
			}
		}
		// if no matching found, search in methods
		for (int i = 0; i<methods.length; i++) {
			XmlElement element = methods[i].getAnnotation(XmlElement.class);
			if (element != null) {
				// if there is a property-xml mapping, use that to check to find a match
				// Also, it has to be a getter method.. so the prefix should be get
				if (xmlElement.equalsIgnoreCase(element.name()) && methods[i].getName().startsWith("get")) {
					// Found a match
					
					return methods[i];
				}
			} else {
				// if there isn't, use the method name
				if (methodName.equalsIgnoreCase(methods[i].getName())) {
					// Found a match
					return methods[i];
				}				
			}
		}			
		return null;
	}
 	
	private static String toGetMethodName(String xmlElement) {
		String adjustedFieldName = xmlElement.toUpperCase().charAt(0) + xmlElement.substring(1);
		return "get" + adjustedFieldName;		
	}
	
	public static Method getMethodFromField(Class<?> clz, Field f) {
		//String adjustedFieldName = f.getName().toUpperCase().charAt(0) + f.getName().substring(1);
		String methodName = toGetMethodName(f.getName());
		Method m = null;
		try {
			m = clz.getMethod(methodName, (Class[]) null);
		} catch (SecurityException e) {
			LOG.log(Level.FINE, methodName, e);
		} catch (NoSuchMethodException e) {
		    LOG.log(Level.FINE, methodName, e);
		}
		return m;
	}

}
