/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.lang.reflect.Type;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * This class is responsible for generating Java Bean classes
 * 
 * @author rmandapati
 */
public class JavaBeanGenerator extends BaseCodeGenerator  {

	private static final String FIELD_NAME_PREFIX = "m_";
	private static final String GET_METHOD_PREFIX = "get";
	private static final String SET_METHOD_PREIFX = "set";
	
	
	private static JavaBeanGenerator s_javaBeanGenerator  =
		new JavaBeanGenerator();


	private JavaBeanGenerator() {}
	
	
	public static JavaBeanGenerator getInstance() {
		return s_javaBeanGenerator;
	}
	
	
	public JDefinedClass generateBeanClass(
			String fullyQualifiedBeanName, 
			Type[] fieldTypes, 
			String[] fieldNames,
			String destLocation) throws CodeGenFailedException {
		
		JCodeModel jCodeModel = new JCodeModel();
		JDefinedClass beanClass = 
			createNewClass(jCodeModel, fullyQualifiedBeanName);
		
		for (int i = 0; i < fieldTypes.length; i++) {
			Type fieldType = fieldTypes[i];
			addGetterAndSetter(jCodeModel, beanClass, fieldType, fieldNames[i]);
		}
		
		generateJavaFile(jCodeModel, destLocation);
		
		return beanClass;
	}
	
	
	
	private void addGetterAndSetter(
				JCodeModel jCodeModel,
				JDefinedClass beanClass, 
				Type field,
				String fieldName) {
		
		JType fieldType = getJType(field, jCodeModel);
		String varName = FIELD_NAME_PREFIX+fieldName;
		JFieldVar fieldVar  = 
				beanClass.field(JMod.PRIVATE, fieldType, varName);
		
		addGetter(beanClass, fieldType, fieldVar, fieldName);
		addSetter(beanClass, fieldType, fieldVar, fieldName);
	}
	
	
	
	private void addGetter(
			JDefinedClass beanClass, 
			JType fieldType,
			JFieldVar fieldVar,
			String fieldName) {
		
		JMethod jGetMethod = 
			beanClass.method(JMod.PUBLIC, fieldType, getterName(fieldName));
		jGetMethod.body()._return(fieldVar);
	}
	
	
	
	private void addSetter(
			JDefinedClass beanClass, 
			JType fieldType,
			JFieldVar fieldVar,
			String fieldName) {
		
		JMethod jSetMethod = 
			beanClass.method(JMod.PUBLIC, Void.TYPE, setterName(fieldName));
		JVar methodParamVar = jSetMethod.param(fieldType, fieldName);
		JBlock setMethodBody = jSetMethod.body();
		
		setMethodBody.assign(fieldVar, methodParamVar);
	}
	
	
	
	private String getterName(String methodName) {
		String getMethodName = GET_METHOD_PREFIX + CodeGenUtil.makeFirstLetterUpper(methodName);
		return getMethodName;
	}

	
	private String setterName(String methodName) {
		String setMethodName = SET_METHOD_PREIFX + CodeGenUtil.makeFirstLetterUpper(methodName);
		return setMethodName;
	}	


}
