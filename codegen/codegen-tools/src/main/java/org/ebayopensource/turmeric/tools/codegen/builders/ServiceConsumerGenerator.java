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
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.EnvironmentMapper;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;


/**
 * Generates consumer class for a Service
 *
 * @author rmandapati
 * @author arajmony
 */
public class ServiceConsumerGenerator extends BaseCodeGenerator implements SourceGenerator {

	private static final String GET_PROXY_METHOD_NAME = "getProxy";
	private static final String SET_SVC_LOC_METHOD_NAME = "setServiceLocation";
	private static final String SET_USER_PROV_SEC_METHOD_NAME = "setUserProvidedSecurityCredentials";
	private static final String SET_SEC_CRE_SEC_METHOD_NAME = "setAuthToken";
	private static final String SET_COOKIES_METHOD_NAME = "setCookies";
	private static final String GET_SVC_INVOKER_OPTIONS_METHOD_NAME ="getServiceInvokerOptions";
	private static final String GET_INVOKER_OPTIONS_METHOD_NAME = "getInvokerOptions";
	private static final String GET_SERVICE_METHOD_NAME = "getService";
	private static final String INIT_METHOD_NAME = "init";

	private static final String SVC_LOCATION_FIELD_NAME = "m_serviceLocation";
	private static final String SVC_PROXY_FIELD_NAME = "m_proxy";
	private static final String AUTH_TOKEN_FIELD_NAME = "m_authToken";
	private static final String AUTH_COOKIE_FIELD_NAME = "m_cookies";
	private static final String SVC_INSTANCE_NAME = "m_service";
	private static final String SVC_STATIC_NAME = "SVC_ADMIN_NAME";
	private static final String ENV_MAPPER = "s_envMapper";
	private static final String CLIENT_FIELD_NAME = "m_clientName";
	private static final String ENV_FIELD_NAME = "m_environment";
	private static final String DEFAULT = "production";
	private static final String EXCEPTION_ENV_MSG = "environment can not be null";
	private static final String EXCEPTION_CLIENT_MSG = "clientName can not be null";
	private static final String ENV_MAPPER_GET_DEPLOYED_ENV = "getDeploymentEnvironment";

	private static Logger s_logger = LogManager.getInstance(ServiceConsumerGenerator.class);


	private static ServiceConsumerGenerator s_serviceConsumerGenerator  = new ServiceConsumerGenerator();



	private ServiceConsumerGenerator() {}


	public static ServiceConsumerGenerator getInstance() {
		return s_serviceConsumerGenerator;
	}


	private Logger getLogger() {
		return s_logger;
	}

	public boolean continueOnError() {
		return true;
	}



	public void generate(CodeGenContext codeGenCtx) throws CodeGenFailedException {
		
		String qualifiedInterfaceName = "";
		Class<?> interfaceClass = null;
		if(codeGenCtx.isAsyncInterfaceRequired()){
			try {
				qualifiedInterfaceName  = getAsyncInterfaceClassName(codeGenCtx.getServiceInterfaceClassName());
				interfaceClass = ContextClassLoaderUtil.loadClass(qualifiedInterfaceName);
			} catch (ClassNotFoundException e) {
				// Load the base interface class. Async interface does not exists
				qualifiedInterfaceName = codeGenCtx.getServiceInterfaceClassName();
				interfaceClass = ContextClassLoaderUtil.loadRequiredClass(qualifiedInterfaceName);
			}
		}else{
			qualifiedInterfaceName = codeGenCtx.getServiceInterfaceClassName();
			interfaceClass = ContextClassLoaderUtil.loadRequiredClass(qualifiedInterfaceName);
		}
			
		//Class interfaceClass = loadClass(qualifiedInterfaceName);
		
		JCodeModel jCodeModel = new JCodeModel();

		String  testClientClassName =
				getServiceConsumerClassName(codeGenCtx.getServiceAdminName(), qualifiedInterfaceName,codeGenCtx);
		JDefinedClass testClientClass = createNewClass(jCodeModel, testClientClassName);
		
		if(codeGenCtx.getInputOptions().isConsumerAnInterfaceProjectArtifact())
			implement(testClientClass, interfaceClass);
        
		addFields(jCodeModel, interfaceClass, testClientClass,codeGenCtx);
		//shared consumer should not have default constructor
		if(! codeGenCtx.getInputOptions().isConsumerAnInterfaceProjectArtifact())
			addConstructor(jCodeModel, testClientClass);
		addConstructorwithClientName(jCodeModel, testClientClass,codeGenCtx);
		addConstructorWithParams(jCodeModel, testClientClass,codeGenCtx);
		//init() method added
		addInitMethod(testClientClass,jCodeModel);
		addSetSvcLocationMethod(testClientClass, jCodeModel);
		addSetUserProvidedSecurityCredentialsMethod(testClientClass, jCodeModel);
		addSetSecurtiyCredentials(testClientClass, jCodeModel);
		addSetCookiesMethod(testClientClass, jCodeModel);
		addGetServiceInvokerOptionsMethod(testClientClass,jCodeModel,codeGenCtx);
		addGetProxyMethod(testClientClass, interfaceClass, jCodeModel, codeGenCtx);
		addGetServiceMethod(testClientClass,jCodeModel,codeGenCtx);
		

		addServiceMethods(interfaceClass, testClientClass, jCodeModel);
		addJavaDocs(testClientClass);
		//adding comments for Consumer
		testClientClass.javadoc().append("\n"+"This class is not thread safe");
		
		generateJavaFile(codeGenCtx, testClientClass, "");

		if (codeGenCtx.getInputOptions().isNoCompile() == false) {
			compileJavaFilesNoException(
					codeGenCtx.getGeneratedJavaFiles(),
					codeGenCtx.getBinLocation());
		}

		getLogger().log(Level.INFO, "Successfully generated " + testClientClassName);
	}

	//--------------------------------------------------------------------
	//generates:
	//public void init() throws ServiceException 
	//{
	//getService();
	//}
	//--------------------------------------------------------------------
    private void addInitMethod(JDefinedClass testClientClass,JCodeModel codeModel)
    {
    	JMethod initMethod = addMethod(testClientClass, INIT_METHOD_NAME,JMod.PUBLIC,codeModel.VOID);
    	initMethod._throws(ServiceException.class);
    	JBlock initMethodBody = initMethod.body();
		JInvocation getServiceInvoker = JExpr.invoke(GET_SERVICE_METHOD_NAME);
		initMethodBody.add(getServiceInvoker);
		
		//Adding javadocs
		initMethod.javadoc().add("Use this method to initialize ConsumerApp after creating a Consumer instance");

    }
	
	private void addGetServiceMethod(JDefinedClass testClientClass, JCodeModel jCodeModel, CodeGenContext codeGenCtx) {
		
		//---------------------------------------------------------------
		// generates:
		// public Service getService()  throws ServiceException
		// --------------------------------------------------------------
		JClass svcJClass = getJClass(Service.class, jCodeModel);
		JMethod getServiceMethod =
				addMethod(testClientClass,
						GET_SERVICE_METHOD_NAME,
						JMod.PUBLIC,
						svcJClass);
		getServiceMethod._throws(ServiceException.class);
		
		//adding javadocs
		getServiceMethod.javadoc().add("Method returns an instance of Service which has been initilized for this Consumer");
		
		

		JBlock getServiceMethodBody = getServiceMethod.body();
		

		//---------------------------------------------------------------
		// generates:
		// if( m_service == null)
		//    m_service = ServiceFactory.create(SVC_ADMIN_NAME,  m_environment,m_clientName, m_serviceLocation);
		//---------------------------------------------------------------
		
		
		JClass svcFactoryJClass = getJClass(ServiceFactory.class, jCodeModel);
		JInvocation svcFactoryInvoker = svcFactoryJClass.staticInvoke("create");
		svcFactoryInvoker.arg(JExpr.ref(SVC_STATIC_NAME));
		svcFactoryInvoker.arg(JExpr.ref(ENV_FIELD_NAME));
		svcFactoryInvoker.arg(JExpr.ref(CLIENT_FIELD_NAME));
		svcFactoryInvoker.arg(JExpr.ref(SVC_LOCATION_FIELD_NAME));

		JFieldRef svcInstanceRef = JExpr.ref(SVC_INSTANCE_NAME);
		JConditional ifServiceCondition = getServiceMethodBody._if(svcInstanceRef.eq(JExpr._null()));
		ifServiceCondition._then().assign(svcInstanceRef, svcFactoryInvoker);
		
	
		

		//------------------------------------------------------------
		// generates:
		//	setUserProvidedSecurityCredentials(m_service);
		//------------------------------------------------------------
		JInvocation setUserProvidedSecurityCredentialsInvoker = JExpr.invoke("setUserProvidedSecurityCredentials");
		setUserProvidedSecurityCredentialsInvoker.arg(svcInstanceRef);
		getServiceMethodBody.add(setUserProvidedSecurityCredentialsInvoker);


		//------------------------------------------------
		// generates:
		// return m_service;
		//------------------------------------------------
		getServiceMethodBody._return(svcInstanceRef);
		
		
	}


	private void addGetServiceInvokerOptionsMethod(JDefinedClass testClientClass, JCodeModel jCodeModel, CodeGenContext codeGenCtx) {
		
		//---------------------------------------------------------------
		// generates:
		// public  ServiceInvokerOptions getServiceInvokerOptions()
		// --------------------------------------------------------------
		
		JMethod getInvokerOptionsMethod =
				addMethod(testClientClass,
						GET_SVC_INVOKER_OPTIONS_METHOD_NAME,
						JMod.PUBLIC,
						getJClass(ServiceInvokerOptions.class, jCodeModel));
		getInvokerOptionsMethod._throws(ServiceException.class);
		getInvokerOptionsMethod.javadoc().add("Use this method to get the Invoker Options on the Service and set them to user-preferences");
		

		JBlock getInvokerOptionsMethodBody = getInvokerOptionsMethod.body();
		//---------------------------------------------------------------
		// generates:
		// m_service = getService();
		//---------------------------------------------------------------
		
		JFieldRef svcInstanceRef = JExpr.ref(SVC_INSTANCE_NAME);
		getInvokerOptionsMethodBody.assign(svcInstanceRef, JExpr.invoke(GET_SERVICE_METHOD_NAME));
		

		/*
		 * generates
		 *    return m_service.getInvokerOptions();
		 */
		JInvocation invokerOptionsCall = svcInstanceRef.invoke(GET_INVOKER_OPTIONS_METHOD_NAME);
		getInvokerOptionsMethodBody._return(invokerOptionsCall);
		
		
	}


	private String getServiceConsumerClassName(String svcAdminName, String interfaceClassName,CodeGenContext codegenCtx) {
		String svcConsumerClassName = null;
		InputOptions inputOptions = codegenCtx.getInputOptions();
		String response = null;
		
		if(inputOptions.isConsumerAnInterfaceProjectArtifact()){
			svcConsumerClassName = "Shared" + CodeGenUtil.makeFirstLetterUpper(svcAdminName) + "Consumer";
			
			if( !CodeGenUtil.isEmptyString(inputOptions.getShortPathForSharedConsumer())){
				String shortPkgPath = inputOptions.getShortPathForSharedConsumer();
				if(!shortPkgPath.endsWith("."))
					shortPkgPath += ".";
				response = shortPkgPath + svcConsumerClassName;
			}
		}
		else{
			svcConsumerClassName = "Base" + CodeGenUtil.makeFirstLetterUpper(svcAdminName) + "Consumer";
		}
		
		if(response == null)
			response = generateQualifiedClassName(interfaceClassName, "gen", svcConsumerClassName,codegenCtx);
		
		return response;
	}
	
	
	private String generateQualifiedClassName(String classNameForPkg,
			String pkgPrefix, String genClassName, CodeGenContext codegenCtx) {
		// The new structure would be package>/<serviceNmae/
		String genPkgName = null;

		int lastDotPos = classNameForPkg.lastIndexOf(DOT);
		if (lastDotPos > -1) {
			String destinationLoc = null;

			//for sharedConsumer,destination location includes AdminName.
			if(codegenCtx.getInputOptions().isConsumerAnInterfaceProjectArtifact())
				destinationLoc = codegenCtx.getInputOptions().getServiceAdminName().toLowerCase();
			genPkgName = destinationLoc == null ? classNameForPkg.substring(0,
					lastDotPos)
					+ DOT + pkgPrefix : classNameForPkg
					.substring(0, lastDotPos)
					+ DOT
					+ destinationLoc
					+ DOT
					+ pkgPrefix;
		} else {
			genPkgName = pkgPrefix;
		}

		return genPkgName.toLowerCase() + DOT + genClassName;
	}

	private void addFields(
				JCodeModel jCodeModel,
				Class<?> interfaceClass,
				JDefinedClass testClientClass,CodeGenContext codegenCtx) throws CodeGenFailedException {
		//--------------------------------------------------
		// adds private URL m_serviceLocation = null;
		//--------------------------------------------------
		JClass urlJClass = getJClass("java.net.URL", jCodeModel);
		JFieldVar servicLocField =
				testClientClass.field(JMod.PRIVATE, urlJClass, SVC_LOCATION_FIELD_NAME);
		servicLocField.init(JExpr._null());
		
		//--------------------------------------------------
		//adds private static final String SVC_ADMIN_NAME = <serviceName>;
		//--------------------------------------------------
		 int FIELD_MODS = (JMod.PRIVATE | JMod.STATIC | JMod.FINAL);
		JClass stringJClass = getJClass(String.class, jCodeModel);
		JFieldVar serviceAdminName = testClientClass.field(FIELD_MODS,stringJClass, SVC_STATIC_NAME);
		JExpression svcNameExpr = JExpr.lit(codegenCtx.getServiceAdminName());
		serviceAdminName.init(svcNameExpr);
		
		//--------------------------------------------------
		//adds private String m_clientName = <client name"; from -cn option for BaseConsumer, in the case of SharedConsumer it should always be null
		//--------------------------------------------------
	    JFieldVar clientName = testClientClass.field(JMod.PRIVATE, stringJClass, CLIENT_FIELD_NAME);
		if(codegenCtx.getInputOptions().isConsumerAnInterfaceProjectArtifact())
			clientName.init(null);
		else{
			//for pre 2.4 consumer, override cientName with serviceName
			if(codegenCtx.getInputOptions().isServiceNameRequired())
				codegenCtx.getInputOptions().setClientName(codegenCtx.getServiceAdminName());
			JExpression clientNameExpr =  JExpr.lit(codegenCtx.getInputOptions().getClientName());
			clientName.init(clientNameExpr);
		}
			
	    
	    //-------------------------------------------------
	    //adds private String m_environment ="default"
	    //-------------------------------------------------
	    JFieldVar environmentName = testClientClass.field(JMod.PRIVATE, stringJClass, ENV_FIELD_NAME);
	    JExpression environmentExp = JExpr.lit(DEFAULT);
	    if(codegenCtx.getInputOptions().isServiceNameRequired() && ! codegenCtx.getInputOptions().isConsumerAnInterfaceProjectArtifact())
	    	environmentName.init(null);
	    else
	    	environmentName.init(environmentExp);
	   
	    
	    


		//--------------------------------------------------
		// adds private <InterfaceClass> m_proxy = null;
		//--------------------------------------------------
		JClass interfaceJClass = getJClass(interfaceClass, jCodeModel);
		JFieldVar proxyField =
				testClientClass.field(JMod.PRIVATE, interfaceJClass, SVC_PROXY_FIELD_NAME);
		proxyField.init(JExpr._null());


		JClass StringJClass = getJClass("java.lang.String", jCodeModel);


		//--------------------------------------------------
		// adds private String m_authToken = null;
		//--------------------------------------------------
		JFieldVar authTokenField =
				testClientClass.field(JMod.PRIVATE, StringJClass, AUTH_TOKEN_FIELD_NAME);
		authTokenField.init(JExpr._null());


		//--------------------------------------------------
		// adds private Cookie[] m_cookies;
		//--------------------------------------------------
        JClass cookieClass = getJClass("org.ebayopensource.turmeric.runtime.common.types.Cookie",jCodeModel).array();
        testClientClass.field(JMod.PRIVATE,cookieClass,AUTH_COOKIE_FIELD_NAME);

        
        //--------------------------------------------------
		// adds private Service m_service = null;
		//--------------------------------------------------
		JFieldVar serviceInstanceField =
				testClientClass.field(JMod.PRIVATE, Service.class, SVC_INSTANCE_NAME);
		serviceInstanceField.init(JExpr._null());
		
		if (!CodeGenUtil.isEmptyString(codegenCtx.getInputOptions()
				.getEnvironmentMapper())) {
			// --------------------------------------------------
			// adds private String final EnvironmentMapper s_envMapper = new
			// org.ebayopensource.turmeric.MarketplaceEnvironmentMapperImpl();
			int FIELD_MODE = (JMod.PRIVATE | JMod.STATIC | JMod.FINAL);
			JClass envMapperClass = getJClass(EnvironmentMapper.class,
					jCodeModel);
			JFieldVar envmappername = testClientClass.field(FIELD_MODE,
					envMapperClass, ENV_MAPPER);
			JClass envmapperClass = getJClass(
					codegenCtx.getInputOptions()
					.getEnvironmentMapper(),
					jCodeModel);
			JInvocation envmapperObjCreater = JExpr._new(envmapperClass);
			envmappername.init(envmapperObjCreater);
		}
	}



	private void addConstructor(JCodeModel jCodeModel, JDefinedClass testClientClass)
		throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		// public <Class Name>() {
		// }
		//---------------------------------------------------------------
		
		testClientClass.constructor(JMod.PUBLIC);
		
	}
	
	private void addConstructorwithClientName(JCodeModel jCodeModel,
			JDefinedClass testClientClass, CodeGenContext ctx)
			throws CodeGenFailedException {

		// ---------------------------------------------------------------
		// generates:
		// public BaseCalculatorServiceConsumer(String clientName)
		// throws ServiceException
		// {
		// if (clientName == null) {
		// throw new ServiceException("clientName can not be null");
		// }
		// }
		// ---------------------------------------------------------------
		JMethod constructorWithArgs = testClientClass.constructor(JMod.PUBLIC);
		JClass stringJClass = getJClass(String.class, jCodeModel);
		String paramClientName = "clientName";
		constructorWithArgs.param(stringJClass, paramClientName);
		constructorWithArgs._throws(ServiceException.class);
		JBlock constructorWithArgsBody = constructorWithArgs.body();
		JVar[] allLocalVariables = constructorWithArgs.listParams();
		JConditional ifResultCondition = constructorWithArgsBody
				._if(allLocalVariables[0].eq(JExpr._null()));
		JClass ExceptionClass = getJClass(ServiceException.class, jCodeModel);
		JInvocation invokeNewException1 = JExpr._new(ExceptionClass);
		invokeNewException1.arg(EXCEPTION_CLIENT_MSG);
		ifResultCondition._then()._throw(invokeNewException1);
		constructorWithArgsBody.assign(JExpr.ref(CLIENT_FIELD_NAME), JExpr.ref(paramClientName));

		if (!CodeGenUtil.isEmptyString(ctx.getInputOptions()
				.getEnvironmentMapper())) {
			JInvocation invocation = JExpr.ref(ENV_MAPPER).invoke(
					ENV_MAPPER_GET_DEPLOYED_ENV);
			constructorWithArgsBody.assign(JExpr.ref(ENV_FIELD_NAME),
					invocation);
		}
		
	}




	private void addConstructorWithParams(JCodeModel jCodeModel,
			JDefinedClass testClientClass,CodeGenContext codegenCtx) {
		//---------------------------------------------------------------
		//generates:
		//public <Class Name>(String clientName, String environment)throws ServiceException{
		//if(environment ==null)
		//throw new ServiceException("environment can not be null");
		//m_clientName = clientName;
		//m_environment = environment;
		//}
		//---------------------------------------------------------------
		JMethod constructorWithArgs = testClientClass.constructor(JMod.PUBLIC);
		JClass stringJClass = getJClass(String.class,jCodeModel);
		String paramClientName = "clientName";
		constructorWithArgs.param(stringJClass, paramClientName);
		String paramEnvironment = "environment";
		constructorWithArgs.param(stringJClass, paramEnvironment);
		constructorWithArgs._throws(ServiceException.class);
		
		JBlock constructorWithArgsBody = constructorWithArgs.body();
		JVar[] allLocalVariables = constructorWithArgs.listParams();
		JConditional ifResultCondition = constructorWithArgsBody._if(allLocalVariables[1].eq(JExpr._null()));
		JClass ExceptionClass = getJClass(ServiceException.class, jCodeModel);
		
		if (!CodeGenUtil.isEmptyString(codegenCtx.getInputOptions()
				.getEnvironmentMapper())) {
			JInvocation invocation = JExpr.ref(ENV_MAPPER).invoke(ENV_MAPPER_GET_DEPLOYED_ENV);
			JBlock block =ifResultCondition._then(); 
			block.assign(
				JExpr.ref(ENV_FIELD_NAME),
				invocation
				
				);
			JBlock elseBlock = ifResultCondition._else();
			elseBlock.assign(JExpr.ref(ENV_FIELD_NAME), allLocalVariables[1]);
		}
		else
		{
		JInvocation invokeNewException1 = JExpr._new(ExceptionClass);
		invokeNewException1.arg(EXCEPTION_ENV_MSG);
		ifResultCondition._then()._throw(invokeNewException1);
		}
		
		JConditional ifResultCondition2 = constructorWithArgs.body()._if(allLocalVariables[0].eq(JExpr._null()));
		JInvocation invokeNewException2 = JExpr._new(ExceptionClass);
		invokeNewException2.arg(EXCEPTION_CLIENT_MSG);
		ifResultCondition2._then()._throw(invokeNewException2);
		
		constructorWithArgsBody.assign(
				JExpr.ref(CLIENT_FIELD_NAME),
				JExpr.ref(paramClientName)
				);
		
		if (CodeGenUtil.isEmptyString(codegenCtx.getInputOptions()
				.getEnvironmentMapper())) 
		constructorWithArgsBody.assign(
				JExpr.ref(ENV_FIELD_NAME),
				JExpr.ref(paramEnvironment)
				);
		
		
	
	}



	private void addSetUserProvidedSecurityCredentialsMethod(
			JDefinedClass testClientClass,
			JCodeModel jCodeModel
			) throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		//  private void  setUserProvidedSecurityCredentials(Service service) {
		//  ...
		//  ...
		// }
		//---------------------------------------------------------------

		JMethod  setUserProvidedSecurityCredentialsMethod =
				addMethod(testClientClass,
						  SET_USER_PROV_SEC_METHOD_NAME,
						  JMod.PRIVATE,
						  jCodeModel.VOID
						);
		JBlock setUserProvidedSecurityCredentialsMethodBody = setUserProvidedSecurityCredentialsMethod.body();

		JClass serviceClass = getJClass(Service.class,jCodeModel);
		setUserProvidedSecurityCredentialsMethod.param(serviceClass,"service");

		JFieldRef proxyFieldRef = JExpr.ref("service");



		//----------------------------------------------------------------------------
		// generates:
		//  if(m_authToken != null)
		//      service.setSessionTransportHeader(SOAHeaders.AUTH_TOKEN, m_authToken);
		//----------------------------------------------------------------------------
        JFieldRef authTokenFieldRef  = JExpr.ref(AUTH_TOKEN_FIELD_NAME);
        JConditional ifAuthTokenCheck = setUserProvidedSecurityCredentialsMethodBody._if(authTokenFieldRef.ne(JExpr._null()));

        JClass soaHeaderClass = getJClass(SOAHeaders.class,jCodeModel);
        JInvocation setSessionTransportHeaderInvokerForToken = proxyFieldRef.invoke("setSessionTransportHeader");
        setSessionTransportHeaderInvokerForToken.arg(soaHeaderClass.staticRef("AUTH_TOKEN"));
        setSessionTransportHeaderInvokerForToken.arg(authTokenFieldRef);
		JBlock ifAuthTokenBlock = ifAuthTokenCheck._then();
		ifAuthTokenBlock.add(setSessionTransportHeaderInvokerForToken);


		//----------------------------------------------------------------------------
		// generates:
		//  if(m_cookies != null) {
		//      for (int i = 0; (i<m_cookies.length); i ++)
		//          service.setCookie(m_cookies[i]);
		//----------------------------------------------------------------------------


		JFieldRef authCookieFieldRef  = JExpr.ref(AUTH_COOKIE_FIELD_NAME);
		JConditional ifCookiesCheck   = setUserProvidedSecurityCredentialsMethodBody._if(authCookieFieldRef.ne(JExpr._null()));

		JBlock ifCookiesBlock  = ifCookiesCheck._then();
		JForLoop cookieForLoop = ifCookiesBlock._for();

		JVar initVar = cookieForLoop.init(jCodeModel._ref(int.class), "i", JExpr.lit(0));
		cookieForLoop.test(initVar.lt(authCookieFieldRef.ref("length")));
		cookieForLoop.update(initVar.incr());

		JBlock forLoopBlock = cookieForLoop.body();
		JInvocation setCookieInvoker= proxyFieldRef.invoke("setCookie");
		setCookieInvoker.arg(authCookieFieldRef.component(initVar));
		forLoopBlock.add(setCookieInvoker);

	}

	private void addSetSecurtiyCredentials(
			JDefinedClass testClientClass,
			JCodeModel jCodeModel
			) throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		//  protected void setSecurtiyCredentials(String authUserID,String authPassword, String authToken){
		//  ...
		//  ...
		// }
		//---------------------------------------------------------------

		JMethod  setSecurtiyCredentialsMethod =
				addMethod(testClientClass,
						  SET_SEC_CRE_SEC_METHOD_NAME,
						  JMod.PROTECTED,
						  jCodeModel.VOID
						);

		setSecurtiyCredentialsMethod.javadoc().add("Use this method to set User Credentials (Token) ");
		JBlock addSetSecurtiyCredentialsMethodBody = setSecurtiyCredentialsMethod.body();

		JClass stringJClass = getJClass(String.class,jCodeModel);
		String paramAuthToken = "authToken";
		setSecurtiyCredentialsMethod.param(stringJClass, paramAuthToken);
		//setSecurtiyCredentialsMethod.javadoc().addParam(paramAuthToken + "  Authentication Token");

		//---------------------------------------------------------------
		// generates:
	     //		 m_authToken = authToken;
		// }
		//---------------------------------------------------------------
		addSetSecurtiyCredentialsMethodBody.assign(
				JExpr.ref(AUTH_TOKEN_FIELD_NAME),
				JExpr.ref(paramAuthToken)
				);

	}

	private void addSetSvcLocationMethod(
			JDefinedClass testClientClass,
			JCodeModel jCodeModel) throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		// public void setServiceLocation(String serviceLocation)
		//          throws MalformedURLException {
		//  ...
		//  ...
		// }
		//---------------------------------------------------------------
		JMethod setSvcLocMethod =
				addMethod(testClientClass,
						SET_SVC_LOC_METHOD_NAME,
						JMod.PROTECTED,
						jCodeModel.VOID);
		setSvcLocMethod._throws(MalformedURLException.class);
		JClass stringJClass = getJClass("java.lang.String", jCodeModel);
		JVar serviceLocParam = setSvcLocMethod.param(stringJClass, "serviceLocation");

		JClass urlJClass = getJClass("java.net.URL", jCodeModel);
		JInvocation urlClassObjCreater = JExpr._new(urlJClass);
		urlClassObjCreater.arg(serviceLocParam);

		//---------------------------------------------------------------
		// generates:
		// m_serviceLocation = new URL(serviceLocation);
		//---------------------------------------------------------------
		JBlock setSvcLocMethodBody = setSvcLocMethod.body();
		JFieldRef serviceLocFieldRef = JExpr.ref(SVC_LOCATION_FIELD_NAME);
		setSvcLocMethodBody.assign(serviceLocFieldRef, urlClassObjCreater);
		
		
		//---------------------------------------------------------------
		//generates:
		//if(m_service!=null)
		//m_service.setServiceLocation(m_serviceLocation);
		//---------------------------------------------------------------
		
		JFieldRef svcInstanceRef = JExpr.ref(SVC_INSTANCE_NAME);
		JConditional ifServiceLocCondition = setSvcLocMethodBody._if(svcInstanceRef.ne(JExpr._null()));
		JInvocation setServiceLocationInvok = svcInstanceRef.invoke(SET_SVC_LOC_METHOD_NAME);
		setServiceLocationInvok.arg(serviceLocFieldRef);
		ifServiceLocCondition._then().add(setServiceLocationInvok);
		
		
		
		
		
	}


	private void addGetProxyMethod(
			JDefinedClass testClientClass,
			Class<?> interfaceClass,
			JCodeModel jCodeModel,
			CodeGenContext codeGenCtx) throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		// private <Interface> getProxy() {}
		// --------------------------------------------------------------
		JMethod getProxyMethod =
				addMethod(testClientClass,
						GET_PROXY_METHOD_NAME,
						JMod.PROTECTED,
						getJClass(interfaceClass, jCodeModel));
		getProxyMethod._throws(ServiceException.class);



		//---------------------------------------------------------------
		// generates:
		// m_service = getService();
		// m_proxy = m_service.getProxy();
		//---------------------------------------------------------------
		
		JBlock proxyMethodBody = getProxyMethod.body();
		
		
		JFieldRef svcInstanceRef = JExpr.ref(SVC_INSTANCE_NAME);
		JInvocation getServiceInvocation = JExpr.invoke(GET_SERVICE_METHOD_NAME);
		proxyMethodBody.assign(svcInstanceRef, getServiceInvocation);
		
		
		JFieldRef proxyFieldRef = JExpr.ref(SVC_PROXY_FIELD_NAME);
		proxyMethodBody.assign(proxyFieldRef, svcInstanceRef.invoke("getProxy"));
		

		//------------------------------------------------
		// generates:
		// return m_proxy;
		//------------------------------------------------
		proxyMethodBody._return(proxyFieldRef);
	}


	private void addSetCookiesMethod(
			JDefinedClass testClientClass,
			JCodeModel jCodeModel) throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		// protected void setCookies(Cookie[] cookies){
		//  ...
		//  ...
		// }
		//---------------------------------------------------------------
		JMethod setCookiesMethod =
			  	addMethod(testClientClass,
			  			SET_COOKIES_METHOD_NAME,
			  			JMod.PROTECTED,
			  			jCodeModel.VOID);
		setCookiesMethod.javadoc().add("Use this method to set User Credentials (Cookie)");

		JClass cookieArrayJClass = getJClass("org.ebayopensource.turmeric.runtime.common.types.Cookie", jCodeModel).array();
		JVar   cookieArrayParam = setCookiesMethod.param(cookieArrayJClass,"cookies");

		//---------------------------------------------------------------
		// generates:
		// m_cookies = cookies;
		//---------------------------------------------------------------
		JBlock setCookiesMethodBody = setCookiesMethod.body();
		JFieldRef cookieArrayFieldRef = JExpr.ref(AUTH_COOKIE_FIELD_NAME);
		setCookiesMethodBody.assign(cookieArrayFieldRef, cookieArrayParam);

	}


	private void addServiceMethods(
			Class<?> interfaceClass,
			JDefinedClass testClientClass,
			JCodeModel jCodeModel) throws CodeGenFailedException {

		List<Method> methods = IntrospectUtil.getMethods(interfaceClass);

		for (Method method : methods) {
			String methodName = method.getName();

			//---------------------------------------------------------------
			// For each interface method
			// generates:
			// public <ReturnType> test<InterfaceMethodName>() {
			// }
			//---------------------------------------------------------------
			JType returnJType = null;
			Type returnType = method.getGenericReturnType();
			if (returnType != null) {
				returnJType = getJType(returnType, jCodeModel);
			} else {
				returnJType = jCodeModel.VOID;
			}


			JMethod testMethod = addMethod(testClientClass, methodName, returnJType);

			Type[] paramTypes = method.getGenericParameterTypes();
			if (paramTypes != null && paramTypes.length > 0) {
				for(int i=0; i<paramTypes.length; i++){
				addParameter(testClientClass, testMethod, paramTypes[i], "param"+i);
				}
			}

			Type[] exceptionTypes = method.getGenericExceptionTypes();
			for (Type exceptionType : exceptionTypes) {
				testMethod._throws(getTypeClass(exceptionType));
					}

			JBlock testMethodBody = testMethod.body();
			//---------------------------------------------------------------
			// generates:
			// <ReturnType> result = <InitialValue>
			//---------------------------------------------------------------
			JVar resultVar = null;
			if (returnJType != null && returnJType != jCodeModel.VOID) {
				resultVar = testMethodBody.decl(returnJType, "result", getValueForType(returnJType));
			}

			//---------------------------------------------------------------
			// generates:
			// try {
			//      m_proxy = getProxy();
			//---------------------------------------------------------------
			JTryBlock tryBlock = testMethodBody._try();
			JBlock tryBlockBody = tryBlock.body();

			JFieldRef proxyFieldRef     = JExpr.ref(SVC_PROXY_FIELD_NAME);
			JInvocation getProxyInvoker = JExpr.invoke(GET_PROXY_METHOD_NAME);
			tryBlockBody.assign(proxyFieldRef, getProxyInvoker);



			//---------------------------------------------------------------
			// generates:
			// catch (ServiceException serviceException) {
            //     throw ServiceRuntimeException.wrap(serviceException);
            // }
			//---------------------------------------------------------------
			JCatchBlock jCatchBlock =
					tryBlock._catch(getJClass("org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException", jCodeModel));
			jCatchBlock.param("serviceException");
			JBlock catchBlockBody = jCatchBlock.body();

			JClass ServiceRuntimeException = getJClass(ServiceRuntimeException.class,jCodeModel);
			JInvocation serviceExceptionWrapInvoker = ServiceRuntimeException.staticInvoke("wrap");
			serviceExceptionWrapInvoker.arg(JExpr.ref("serviceException"));

			catchBlockBody._throw(serviceExceptionWrapInvoker);

     		//---------------------------------------------------------------
			// generates:
			// result = m_proxy.<InterfaceMethod>([<ParameterValue>]);
			//---------------------------------------------------------------

			JInvocation methodInvoker = proxyFieldRef.invoke(methodName);
			// Size should be atmost 1
			// we don't allow more than one parameter
			// for Service interface methods
			for(int i =0; i< paramTypes.length; i++){
				if (paramTypes != null && paramTypes.length > 0) {
					methodInvoker.arg(JExpr.ref("param"+i));
				}
			}

			if (returnJType != null && returnJType != jCodeModel.VOID) {
				testMethodBody.assign(resultVar, methodInvoker);
			} else {
				testMethodBody.add(methodInvoker);
			}



			if (resultVar != null) {
				testMethodBody._return(resultVar);
			} else {
				testMethodBody._return();
			}

		}

	}


	public String getFilePath(String serviceAdminName, String interfaceName) {
		return null;
	}

	/*
	private void addMainMethod(
			JDefinedClass testClientClass,
			JCodeModel jCodeModel) throws CodeGenFailedException {

		//---------------------------------------------------------------
		// generates:
		// public static void main(String[] args) {
		//  ...
		//  ...
		// }
		//---------------------------------------------------------------
		JMethod mainMethod =
				addMethod(testClientClass,
						"main",
						(JMod.PUBLIC | JMod.STATIC),
						jCodeModel.VOID);

		JClass stringJClass = getJClass("java.lang.String", jCodeModel);
		JClass strJClassArray = stringJClass.array();
		JVar argsArraysVar = mainMethod.param(strJClassArray, "args");

		mainMethod._throws(Exception.class);

		JBlock mainMethodBody = mainMethod.body();

		//---------------------------------------------------------------
		// <TestClientClassName> testClient = new <TestClientClassName>();
		//---------------------------------------------------------------
		JInvocation testClientInvoker = JExpr._new(testClientClass);
		JVar testClientVar = mainMethodBody.decl(testClientClass, "testClient", testClientInvoker);

		//---------------------------------------------------------------
		// if (args.length > 0) {
		//     testClient.setServiceLocation(args[0]);
		// }
		//---------------------------------------------------------------
		JConditional jCondition = mainMethodBody._if(argsArraysVar.ref("length").gt(JExpr.lit(0)));
		JBlock ifThenBlock = jCondition._then();
		JInvocation setSvcLocInvoker = testClientVar.invoke(SET_SVC_LOC_METHOD_NAME);
		setSvcLocInvoker.arg(JExpr.component(argsArraysVar, JExpr.lit(0)));
		ifThenBlock.add(setSvcLocInvoker);

		mainMethodBody.directStatement("// Invoke methods on 'testClient' for testing!!");

	}
	*/

}
