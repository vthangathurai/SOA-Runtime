/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;


/**
 * Generates Service Implementation skeleton class based on interface.
 * 
 * 
 * @author rmandapati
 */
public class ServiceSkeletonGenerator extends BaseCodeGenerator implements SourceGenerator {
	
	private static ServiceSkeletonGenerator s_serviceSkeletonGenerator  =
			new ServiceSkeletonGenerator();

	
	private static Logger s_logger = LogManager.getInstance(ServiceSkeletonGenerator.class);
	

	private ServiceSkeletonGenerator() {}


	public static ServiceSkeletonGenerator getInstance() {
		return s_serviceSkeletonGenerator;
	}
	
	
	public boolean continueOnError() {
		return false;
	}
	
	
	
	private Logger getLogger() {
		return s_logger;
	}
	
	
	
	public void generate(CodeGenContext codeGenCtx) throws CodeGenFailedException {
		// checking whether service impl skeleton need to be generated or not?
		if (!shouldGenSkeletonClass(codeGenCtx)) {
			return;
		}
		
		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();		
		JCodeModel jCodeModel = new JCodeModel();		
	
		Class<?> svcInterfaceClass = jTypeTable.getClazz();
		
		String svcSkeletonClassName = 
			generateQualifiedSkeletonClassName(
					codeGenCtx.getServiceQName(),
					codeGenCtx.getServiceImplClassName(),
					svcInterfaceClass.getName(),
					codeGenCtx);
		
		JDefinedClass serviceSkeletonClass = createNewClass(jCodeModel, svcSkeletonClassName);		
		implement(serviceSkeletonClass, svcInterfaceClass);	
		
		List<Method> interfaceMethods = jTypeTable.getMethods();
		
		JMethod[] jServiceMethods = addMethods(serviceSkeletonClass, interfaceMethods);
		JMethod[] allSvcImplMethods = null;
		
		
		if (!codeGenCtx.getInputOptions().isImplCommonSvcInterface()) {
			allSvcImplMethods = new JMethod[jServiceMethods.length];
			System.arraycopy(jServiceMethods, 0, allSvcImplMethods, 0, jServiceMethods.length);			
		}
		else {
			implement(serviceSkeletonClass, getCommonServiceInterface());	
			
			Method[] commonMethods = getCommonServiceInterface().getDeclaredMethods();
			JMethod[] jCommonMethods = addMethods(serviceSkeletonClass, commonMethods);
		
			allSvcImplMethods = new JMethod[jServiceMethods.length + jCommonMethods.length];
			System.arraycopy(jServiceMethods, 0, allSvcImplMethods, 0, jServiceMethods.length);
			System.arraycopy(jCommonMethods, 0, allSvcImplMethods, jServiceMethods.length, jCommonMethods.length);	
		}

		addDummyImplementation(allSvcImplMethods, jCodeModel);
		
		String destLocation = null;
		if (    (codeGenCtx.getInputOptions().getCodeGenType() == InputOptions.CodeGenType.SISkeleton )
				||
				(codeGenCtx.getInputOptions().getCodeGenType() ==  InputOptions.CodeGenType.ServiceFromWSDLImpl) 
			){
			destLocation = CodeGenUtil.toOSFilePath(codeGenCtx.getSrcLocation());
		} else {
			// [LEGACY] Adding extra "service" folder name.
			destLocation = 
				generateDestLocation(
						codeGenCtx.getJavaSrcDestLocation(), 
					CodeGenConstants.SERVICE_GEN_FOLDER);	
		}
			
		generateJavaFile(jCodeModel, destLocation);
		
		codeGenCtx.setServiceImplSkeletonName(svcSkeletonClassName); 
		
		String serviceImplJavaFilePath = 
			CodeGenUtil.toJavaSrcFilePath(destLocation, svcSkeletonClassName);
		codeGenCtx.addGeneratedJavaSrcFile(serviceImplJavaFilePath);
		
		if (codeGenCtx.getInputOptions().isNoCompile() == false) {
			compileJavaFilesNoException(
						codeGenCtx.getGeneratedJavaFiles(), 
						codeGenCtx.getBinLocation());
		}
		
		
		getLogger().log(Level.INFO, "Successfully generated " + svcSkeletonClassName);
		
	}
	

	private void addDummyImplementation(
			JMethod[] jServiceMethods,				
			JCodeModel jCodeModel) throws CodeGenFailedException {

		for (JMethod serviceMethod : jServiceMethods) {
			
			JBlock methodBody = serviceMethod.body();
			JType returnType = serviceMethod.type();
			if ( returnType == null || returnType == jCodeModel.VOID) {
				methodBody._return();
			}
			else {
				methodBody._return(getValueForType(returnType));
			}
		}
	
	}
	
	
	private boolean shouldGenSkeletonClass(CodeGenContext codeGenCtx) {
		boolean shouldGenSkeleton = false;
		
		String serviceImplClassName = codeGenCtx.getServiceImplClassName();
		
		if (CodeGenUtil.isEmptyString(serviceImplClassName)) {
			shouldGenSkeleton = true;	
		} else {
			Class<?> svcImplClass = ContextClassLoaderUtil.loadOptionalClass(serviceImplClassName);;
			
			if (svcImplClass == null) {
				// Check whether Service Impl source file exists?
				shouldGenSkeleton = !isServiceImplSourceExists(codeGenCtx.getInputOptions());
			} else {
				shouldGenSkeleton = codeGenCtx.getInputOptions().isGenSkeleton();
			}			
		}		
		
		return shouldGenSkeleton;
	}
	
	
	private boolean isServiceImplSourceExists(InputOptions inputOptions) {
		
		String[] allSrcLocations = inputOptions.getAllSrcLocations();
		if (allSrcLocations == null || allSrcLocations.length == 0) {
			allSrcLocations = new String[] { inputOptions.getSrcLocation() };
		}
		
		boolean isSvcImplExists = false;
		for (String srcLoc : allSrcLocations) {
			String serviceImplSrcFile = 
				CodeGenUtil.toJavaSrcFilePath(srcLoc, inputOptions.getServiceImplClassName());
			isSvcImplExists =  CodeGenUtil.isFileExists(serviceImplSrcFile);
			if (isSvcImplExists) {
				break;
			}
		}
		
		return isSvcImplExists;
	}
	
	
	
	private String generateQualifiedSkeletonClassName(
			QName svcQName,
			String svcImplClassName,
			String interfaceClassName, CodeGenContext codeGenCtx) {	
	
		String skeletonClassName = null;
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		
		
		if (CodeGenUtil.isEmptyString(svcImplClassName)) {
			skeletonClassName = defaultSkeletonClassName(svcQName, interfaceClassName);	
		}
		else {
			Class<?> svcImplClass = ContextClassLoaderUtil.loadOptionalClass(svcImplClassName);
			
			if (svcImplClass == null || inputOptions.isOverWriteSkeleton()) {
				skeletonClassName = svcImplClassName;
			} else {
				skeletonClassName = 
					generateQualifiedClassName(
							svcImplClassName, 
							GEN_PKG_NAME, 
							svcImplClass.getSimpleName());
			}
			
		}
		
		return skeletonClassName;
	}
	
	
	
	
	private String defaultSkeletonClassName(QName svcQName, String interfaceClassName) {
		String skeletonClassName = 
				ServiceNameUtils.getServiceImplSkeletonClassName(
							svcQName.getLocalPart(),
							interfaceClassName);
		
		return skeletonClassName;
	}
	
	public String getFilePath(String serviceAdminName, String interfaceName){
		return null;
	}


}
