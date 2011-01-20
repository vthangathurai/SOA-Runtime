/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.utils;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.library.V4TypeMappings;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;

public class V4TypeMappingsFactory{

	private static final String CLASS_NAME = "org.ebayopensource.turmeric.runtime.tools.codegen.external.V4TypeMappingsGenerator";
	
	public static V4TypeMappings getInstance(TypeLibraryCodeGenContext codeGenContext) throws CodeGenFailedException{
		ClassLoader classLoader = TypeLibraryCodeGenContext.class.getClassLoader();
		
		Class clazz = null;
		try {
			clazz = Class.forName(CLASS_NAME, true, classLoader);
			return (V4TypeMappings) clazz.newInstance();
		}  catch (Exception exception) {
			CodeGenFailedException codeGenFailedException = new 
        	CodeGenFailedException(exception.getMessage(), exception);
			codeGenFailedException.setMessageFormatted(true);
			throw codeGenFailedException;
		}
	}
}
