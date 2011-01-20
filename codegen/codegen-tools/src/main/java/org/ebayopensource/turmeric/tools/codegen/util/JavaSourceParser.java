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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;


/**
 * Helper class for parsing Java Source / Class files. 
 * 
 * 
 * @author rmandapati
 */
public final class JavaSourceParser {
	
	private static Logger s_logger = LogManager.getInstance(JavaSourceParser.class);
	
	public static Map<String, String[]> methodToParamNamesMap(
			String javaSrcFile,
			Class<?> clazz)  {

		Map<String, String[]> map = new HashMap<String, String[]>();
		try {
			JavaDocBuilder builder = new JavaDocBuilder();
			builder.addSource(new File(javaSrcFile));
			JavaClass jc = builder.getClassByName(clazz.getName());

			JavaMethod methods[] = jc.getMethods();
			for(JavaMethod method: methods) {
				 // Add public and non-static members, only 
				 // these methods should be exposed / added to the interface
				 if (method.isPublic() && !method.isStatic()) {
					 String methodName = method.getName();
					 String[] paramNames = getParameterNames(method);
					 map.put(methodName, paramNames);
				 }
			}
		} catch (IOException e) {
			s_logger.log(Level.WARNING, "Failed to parse source file: " + javaSrcFile, e);
		}
		
		return map;
	}
	
	private static String[] getParameterNames(JavaMethod method) {
		
		JavaParameter[] jParameters = method.getParameters();	
		if (jParameters == null || jParameters.length == 0) {
			return null;
		}
		
		String[] paramNames = new String[jParameters.length];		
		for (int i = 0; i < jParameters.length; i++) {
			paramNames[i] = jParameters[i].getName();
		}

		return paramNames;
	}
}
