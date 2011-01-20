/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.builders;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.errorlibrary.ELDomainInfoHolder;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.SourceGeneratorErrorLib;
import org.ebayopensource.turmeric.tools.errorlibrary.codegen.ErrorLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.errorlibrary.util.ErrorLibraryUtils;

import org.ebayopensource.turmeric.common.config.Error;
import org.ebayopensource.turmeric.common.config.ErrorBundle;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class ErrorDataCollectionGenerator extends ErrorLibraryBaseGenerator
					implements SourceGeneratorErrorLib{
		
	private static final String ERRORDATA_FIELD = "errorData";
	private static final String ORGANISATION = "ORGANIZATION";
	private static final String ARG_ERRORID = "errorId";
	private static final String ARG_SEVERITY = "severity";
	private static final String ARG_CATEGORY = "category";
	private static final String ARG_ERRORNAME = "errorName";
	private static final String ARG_SUBDOMAIN = "subDomain";
	private static final String ARG_DOMAIN = "domain";
	private static final String ARG_ERROR_GROUP = "errorGroup";
	private static final String ARG_METHOD_NAME = "createCommonErrorData";
	private static final String ENUM_ERROR_SEVERITY = "ErrorSeverity";
	private static final String ENUM_ERROR_CATEGORY = "ErrorCategory";
	private static final String ERROR_DATA_CLASSNAME = "ErrorDataCollection";
	
	
	private static Logger s_logger = LogManager.getInstance(ErrorDataCollectionGenerator.class);

	private static ErrorDataCollectionGenerator s_errorDataCollectionGenerator  =
		new ErrorDataCollectionGenerator();

	private ErrorDataCollectionGenerator() {
	}

	public static ErrorDataCollectionGenerator getInstance() {
		return s_errorDataCollectionGenerator;
	}

	private Logger getLogger() {
		return s_logger;
	}

	public boolean continueOnError() {
		return false;
	}

	public void generate(ErrorLibraryCodeGenContext codeGenContext) throws CodeGenFailedException{
		
		ErrorLibraryInputOptions inputOptions = codeGenContext.getInputOptions();
		
		List<String> listOfDomains = inputOptions.getDomainList();
		
		populateCodeGenContext(codeGenContext, listOfDomains);
		
		getLogger().log(Level.FINE, "Populated CodeGenContext with required data");
		
		StringBuffer codegenFailedMessage = new StringBuffer();
		boolean codegenFailed = false;
		for (String domainName : listOfDomains) {
			try {
				generateErrorDataCollection(codeGenContext, domainName);
			}  catch (PreProcessFailedException exception) {
				codegenFailed = true;
				codegenFailedMessage.append("ErrorConstants generation for the domain \"").append(domainName)
						.append("\" failed. ").append(exception.getMessage()).append("\n\n");
			}catch (CodeGenFailedException exception) {
				codegenFailed = true;		
				codegenFailedMessage.append("ErrorDataCollection generation for the domain \"").append(domainName)
						.append("\" failed. ").append(exception.getMessage()).append("\n\n");
			}
		}
		
		if(codegenFailed)
			throw new CodeGenFailedException("CodeGen failed : " + codegenFailedMessage.toString());
		
	}
	
	private void generateErrorDataCollection(ErrorLibraryCodeGenContext codeGenContext, String domainName)
							throws CodeGenFailedException, PreProcessFailedException{

		ELDomainInfoHolder holder = codeGenContext.getDomainInfoMap().get(domainName);
		
		String fullyQualifiedClassName = ErrorLibraryUtils.getFullyQualifiedClassName(holder.getPackageName(),
								ERROR_DATA_CLASSNAME);
				
		JCodeModel codeModel = new JCodeModel();
		
		JDefinedClass targetClass = createNewClass(codeModel, fullyQualifiedClassName);
		
		validateMetaData(codeGenContext, domainName);
		
		getLogger().log(Level.FINE, "Validation of Metadata files successful for domain " + domainName);
		
		addFields(codeGenContext, codeModel, targetClass, domainName); 
		
		addGetErrorData(codeGenContext, codeModel, targetClass, domainName);
		
		generateJavaFile(codeModel, codeGenContext.getGenJavaSrcDestFolder());
		
		getLogger().log(Level.INFO, "Successfully generated " + fullyQualifiedClassName + " for the domain " + domainName);
	}
	
	private void addFields(ErrorLibraryCodeGenContext codeGenContext, JCodeModel codeModel,
			JDefinedClass targetClass, String domain) throws CodeGenFailedException{
		

		//-------------------------------------------------------------------------------- 
		//		creates
		//
		//		public final static String ORGANIZATION = "eBay";
		//
	    // 		public final static CommonErrorData svc_factory_cannot_create_svc = 
		//					createCommonErrorData(1000L, (ErrorSeverity.ERROR), (ErrorCategory.SYSTEM), 
		//					"svc_factory_cannot_create_svc", "TurmericRuntime", "System", "testGroup");	    
		//--------------------------------------------------------------------------------
		
		ELDomainInfoHolder holder = codeGenContext.getDomainInfoMap().get(domain);
		
		JClass stringJClass = getJClass(String.class, codeModel);
		JClass commonErrorDataClazz = getJClass(CommonErrorData.class, codeModel);
		
		targetClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, stringJClass, ORGANISATION, 
					JExpr.lit(codeGenContext.getOrganization()));
		ErrorBundle tempErrorBundle = holder.getErrorBundle();
		
		if(tempErrorBundle != null && tempErrorBundle.getErrorlist() != null && tempErrorBundle.getErrorlist().getError() != null){
			List<Error> errors = tempErrorBundle.getErrorlist().getError();
			for (Error error : errors) {
				if(error.getName() != null && ErrorLibraryUtils.validateVariableSemantics(error.getName())){
					JInvocation createErrorDataMethodInvocation = JExpr.invoke(ARG_METHOD_NAME);
					createErrorDataMethodInvocation.arg(JExpr.lit(error.getId()));
					createErrorDataMethodInvocation.arg(JExpr.direct(ENUM_ERROR_SEVERITY + "." + error.getSeverity()));
					createErrorDataMethodInvocation.arg(JExpr.direct(ENUM_ERROR_CATEGORY + "." + error.getCategory()));
					createErrorDataMethodInvocation.arg(error.getName());
					createErrorDataMethodInvocation.arg(domain);
					
					if(error.getSubdomain() != null)
						createErrorDataMethodInvocation.arg(error.getSubdomain());
					else
						createErrorDataMethodInvocation.arg(JExpr.direct("null"));
					
					if(error.getErrorGroup() != null)
						createErrorDataMethodInvocation.arg(error.getErrorGroup());
					else
						createErrorDataMethodInvocation.arg(JExpr.direct("null"));
					
					targetClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, commonErrorDataClazz, error.getName(), 
							createErrorDataMethodInvocation);
				} else{
					CodeGenFailedException codeGenFailedException = new 
		        	CodeGenFailedException("The name of the error should not contain any whitespace" +
		        			" character. Pls check your ErrorData.xml or Error.properties");
					throw codeGenFailedException;
				}
				
			}
			getLogger().log(Level.INFO, "Total number of ErrorData objects " + errors.size());
		}
	}	
	
	private void addGetErrorData(ErrorLibraryCodeGenContext codeGenContext, JCodeModel codeModel,
			JDefinedClass targetClass, String domain){
		
//-------------------------------------------------------------------------------------- 
//		private static CommonErrorData createCommonErrorData(long id, ErrorSeverity severity, ErrorCategory category, 
//				String errorName, String subcategory, String domain, String errorGroup){
//			CommonErrorData errorData = new CommonErrorData();
//			errorData.setErrorId(id);
//			errorData.setSeverity(severity);
//			errorData.setCategory(category);
//			errorData.setSubdomain(subcategory);
//			errorData.setDomain(domain);
//			errorData.setErrorGroups(errorGroup);
//			errorData.setErrorName(errorName);
//			errorData.setOrganization(ORGANIZATION);
//			
//			
//			return errorData;
//		}
//----------------------------------------------------------------------------------------
	
		JType returnType = getJType(CommonErrorData.class, codeModel);
		JMethod jGetErrorDataMethod = addMethod(targetClass, ARG_METHOD_NAME, JMod.PRIVATE | JMod.STATIC , returnType);
		jGetErrorDataMethod.param(codeModel.LONG, ARG_ERRORID);
		jGetErrorDataMethod.param(ErrorSeverity.class, ARG_SEVERITY);
		jGetErrorDataMethod.param(ErrorCategory.class, ARG_CATEGORY);
		jGetErrorDataMethod.param(String.class, ARG_ERRORNAME);
		jGetErrorDataMethod.param(String.class, ARG_DOMAIN);
		jGetErrorDataMethod.param(String.class, ARG_SUBDOMAIN);
		jGetErrorDataMethod.param(String.class, ARG_ERROR_GROUP);
		
		JBlock jGetErrorDataMethodBody = jGetErrorDataMethod.body();
		
		
		JClass commonErrorDataClazz = getJClass(CommonErrorData.class, codeModel);
		JVar returnValue = jGetErrorDataMethodBody.decl(commonErrorDataClazz, ERRORDATA_FIELD, JExpr._new(commonErrorDataClazz));
		
		JFieldRef setErrorIdRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setErrorIdInvocation = setErrorIdRef.invoke("setErrorId");
		setErrorIdInvocation.arg(JExpr.ref(ARG_ERRORID));
		jGetErrorDataMethodBody.add(setErrorIdInvocation);

		JFieldRef setSeverityRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setSeverityInvocation = setSeverityRef.invoke("setSeverity");
		setSeverityInvocation.arg(JExpr.ref(ARG_SEVERITY));
		jGetErrorDataMethodBody.add(setSeverityInvocation);

		JFieldRef setCategoryRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setCategoryInvocation = setCategoryRef.invoke("setCategory");
		setCategoryInvocation.arg(JExpr.ref(ARG_CATEGORY));
		jGetErrorDataMethodBody.add(setCategoryInvocation);

		JFieldRef setSubdomainRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setSubdomainInvocation = setSubdomainRef.invoke("setSubdomain");
		setSubdomainInvocation.arg(JExpr.ref(ARG_SUBDOMAIN));
		jGetErrorDataMethodBody.add(setSubdomainInvocation);

		JFieldRef setDomainRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setDomainInvocation = setDomainRef.invoke("setDomain");
		setDomainInvocation.arg(JExpr.ref(ARG_DOMAIN));
		jGetErrorDataMethodBody.add(setDomainInvocation);

		JFieldRef setErrorGroupsRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setErrorGroupsInvocation = setErrorGroupsRef.invoke("setErrorGroups");
		setErrorGroupsInvocation.arg(JExpr.ref(ARG_ERROR_GROUP));
		jGetErrorDataMethodBody.add(setErrorGroupsInvocation);

		JFieldRef setErrorNameRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setErrorNameInvocation = setErrorNameRef.invoke("setErrorName");
		setErrorNameInvocation.arg(JExpr.ref(ARG_ERRORNAME));
		jGetErrorDataMethodBody.add(setErrorNameInvocation);

		JFieldRef setOrganizationRef = JExpr.ref(ERRORDATA_FIELD);
		JInvocation setOrganizationInvocation = setOrganizationRef.invoke("setOrganization");
		setOrganizationInvocation.arg(JExpr.ref(ORGANISATION));
		jGetErrorDataMethodBody.add(setOrganizationInvocation);

		jGetErrorDataMethodBody._return(returnValue);	
		
	}	
		
}