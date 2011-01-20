/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;


import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.MessageContextAccessorImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.exceptions.AppErrorWrapperException;
import org.ebayopensource.turmeric.runtime.spf.pipeline.QueryCachePolicy;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;


import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * Base class implementing functionality common for all code-generated service dispatchers.
 * This is a generic class parameterized by the type <code>T</code> of the service interface.
 *
 * @author ichernyshev
 */
public abstract class BaseServiceRequestDispatcher<T> implements Dispatcher {

	private static final String OP_GET_SERVICE_VERSION = "getServiceVersion";

	private static Set<String> s_systemOpNames = new HashSet<String>();

	private final Class<T> m_genServiceInterface;
	private final LinkedList<T> m_serviceInstances = new LinkedList<T>();
	private final Map<String,DispatchOperactionDef> m_supportedOps =
		new HashMap<String,DispatchOperactionDef>();

	private ServerServiceId m_svcId;
	private ClassLoader m_classLoader;
	private String m_serviceImplClassName;
	private int m_serviceCreationCount;
	private VersionCheckHandler m_versionCheckHandler;
	private static Logger LOGGER = Logger.getInstance( BaseServiceRequestDispatcher.class );

	/**
	 * Constructor; called by the type-specific derived class.
	 * @param serviceInterface the Java class designating the service interface.
	 */
	protected BaseServiceRequestDispatcher(Class<T> serviceInterface) {
		if (serviceInterface == null) {
			throw new NullPointerException();
		}

		m_genServiceInterface = serviceInterface;

		addSupportedOperation(SOAConstants.OP_GET_VERSION, null,  new Class[] {Object.class});
		addSupportedOperation(OP_GET_SERVICE_VERSION, null,  new Class[] {Object.class});
		addSupportedOperation(SOAConstants.OP_IS_SERVICE_VERSION_SUPPORTED,
			new Class[] {String.class}, new Class[] {Boolean.class});
		addSupportedOperation(SOAConstants.OP_GET_CACHE_POLICY, null, new Class[] {String.class});
	}

	/**
	 * Adds the specified operation to the list of supported operations.  Used by the type-specific derived class dispatcher
	 * to register its coded operations, so that they can be cross-checked against the configured operations for the
	 * service.
	 * @param name the operation name being added
	 * @param inParams the Java classes of the input parameters to the operation (as dispatched to the service implementation).
	 * @param outParams the Java classes of the output parameters to the operation (as dispatched to the service implementation).
	 */
	protected final void addSupportedOperation(String name, Class[] inParams, Class[] outParams) {
		m_supportedOps.put(name, new DispatchOperactionDef(name, inParams, outParams));
	}

	/**
	 * Called by the framework to initialize the dispatcher at service initialization.
	 * @param svcId the server-side service ID of the service for which this handler operates.
	 * @param serviceInterface the class of the service interface (implemented by the
	 * service implementation).
	 * @param serviceImplClassName the class name of the service implementation.
	 * @param cl the ClassLoader under which this service operates.
	 * @param ops the collection of operation descriptions for this service (from the type
	 * mapping configuration).
	 * @param versionCheckHandler the version check handler used by this service.
	 * @throws ServiceException
	 */
	public final void init(ServerServiceId svcId, Class serviceInterface,
		String serviceImplClassName, ClassLoader cl,
		Collection<ServiceOperationDesc> ops,
		VersionCheckHandler versionCheckHandler)
		throws ServiceException
	{
		if (svcId == null || serviceInterface == null ||
			serviceImplClassName == null || cl == null || ops == null)
		{
			throw new NullPointerException();
		}

		m_svcId = svcId;
		m_serviceImplClassName = serviceImplClassName;
		m_classLoader = cl;
		m_versionCheckHandler = versionCheckHandler;

		if (m_genServiceInterface != serviceInterface) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_GEN_USES_WRONG_INTERFACE,
					ErrorConstants.ERRORDOMAIN, new Object[] {this.getClass().getName(), m_genServiceInterface.getName(),
						serviceInterface.getName()}));
		}

		// create a single instance to make sure class is there
		T warmupInst = createServiceInstance();
		returnServiceInstance(warmupInst);

		verifyOperations(ops);
	}

	/**
	 * Abstract method implemented by the specific typed service dispatcher class.
	 * @param ctx the message context of the current invocation.
	 * @param service passes an instance of the service implementation (which implements
	 * <code>T</code>, the service interface).
	 * @return true if the dispatch located the service method to which to dispatch the
	 * current operation, otherwise false.
	 * @throws ServiceException
	 */
	protected abstract boolean dispatch(MessageContext ctx, T service) throws ServiceException;

	/**
	 * @param ctx the message context of the current invocation.
	 * @param service passes an instance of the service implementation (which implements
	 * <code>T</code>, the service interface).
	 * @return true if the dispatch located the	specified system operation (e.g.
	 * getServiceVersion, isServiceVersionSupported).
	 * @throws ServiceException
	 */
	private boolean dispatchSystemCall(MessageContext ctx, T service) throws ServiceException {
		String opName = ctx.getOperationName();

		if (opName.equals(SOAConstants.OP_GET_VERSION) || opName.equals(OP_GET_SERVICE_VERSION)) {
			Message response = ctx.getResponseMessage();

			String result = m_versionCheckHandler.getVersion();
			if(((ServerMessageContextImpl)ctx).getServiceDesc().getConfig().getTypeMappings().getOperationAdded()
					&& opName.equals(SOAConstants.OP_GET_VERSION))
				response.setParam(0, result);
			else if(ctx.getOperation().getResponseType() != null && ctx.getOperation().getResponseType().getRootJavaTypes() != null){
				Class clazz = ctx.getOperation().getResponseType().getRootJavaTypes().get(0);
				if(clazz.equals(String.class))
					response.setParam(0, result);
				else {
					Object respObject = ReflectionUtils.createInstance(clazz);
					response.setParam(0, respObject);
				}
			}
			return true;
		}

		if (opName.equals(SOAConstants.OP_IS_SERVICE_VERSION_SUPPORTED)) {
			Message request = ctx.getRequestMessage();
			Message response = ctx.getResponseMessage();

			String version = (String)request.getParam(0);
			boolean result = m_versionCheckHandler.isVersionSupported(version);

			response.setParam(0, Boolean.valueOf(result));
			return true;
		}

		if (opName.equals(SOAConstants.OP_GET_CACHE_POLICY)) {
			Message response = ctx.getResponseMessage();
			QueryCachePolicy queryCachePolicy = new QueryCachePolicy();
			String cachePolicy = queryCachePolicy.getCachePolicy(((ServerMessageContextImpl)ctx).getServiceDesc());
			response.setParam(0, cachePolicy);
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher#dispatch(org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext)
	 */
	public final void dispatchSynchronously(MessageContext ctx) throws ServiceException {
		// trigger deserialization before logging/monitoring starts
		ServerMessageContextImpl ctxImpl = (ServerMessageContextImpl)ctx;
		ctxImpl.checkOperationName();

		ctx.getRequestMessage().getParamCount();

		T service = getServiceInstance();


		ctxImpl.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_DISPATCH_START);

		long startTime = System.nanoTime();
		MessageContextAccessorImpl.setContext(ctx);
		try {
			boolean processed;
			if (s_systemOpNames.contains(ctx.getOperationName())) {
				processed = dispatchSystemCall(ctx, service);
			} else {
				processed = dispatch(ctx, service);
			}

			if (!processed) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_OPERATION_UNKNOWN_AT_DISPATCH,
						ErrorConstants.ERRORDOMAIN, new Object[] {ctx.getAdminName() + "." + ctx.getOperationName()}));
			}
		} finally {
			MessageContextAccessorImpl.resetContext();
			returnServiceInstance(service);

			long duration = System.nanoTime() - startTime;
			ctxImpl.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_CALL, startTime, duration);

			ctxImpl.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_DISPATCH_COMPLETE);
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher#dispatch(org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext)
	 */
	public Future<?> dispatch(MessageContext ctx)	throws ServiceException {
		// ServiceDispatcher is not allowed to implement async logic
		throw new UnsupportedOperationException();
	}

	public void retrieve(MessageContext ctx, Future<?> name) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Handles an exceptions that occurs in the service implementation.  Any contained
	 * ErrorData are coerced to be error category <code>APPLICATION</code>, since errors
	 * thrown by the service ipmlementation are application category by definition.
	 * @param ctx the message context of the current invocation.
	 * @param e the exception.
	 * @throws ServiceException
	 */
	protected final void handleServiceException(MessageContext ctx, Throwable e)
		throws ServiceException
	{
		// reset error category to APPLICATION
		if (e instanceof ServiceExceptionInterface) {
			if (e instanceof ServiceException) {
				((ServiceException)e).eraseSubcategory();
			}

			if (e instanceof ServiceRuntimeException) {
				((ServiceRuntimeException)e).eraseSubcategory();
			}

			ServiceExceptionInterface e2 = (ServiceExceptionInterface)e;
			ErrorMessage errorMessage = e2.getErrorMessage();
			if (errorMessage != null) {
				List<CommonErrorData> errorDataList = errorMessage.getError();
				for (int i=0; i<errorDataList.size(); i++) {
					CommonErrorData errorData = errorDataList.get(i);
					/**
					 * Only the SYSTEM category is not honored, since this is clearly an error raised
					 * from the application space
					 */
					if ( ErrorCategory.SYSTEM.equals( errorData.getCategory() ) ) {
						if ( LOGGER.isLogEnabled( LogLevel.WARN ) )
							LOGGER.log( LogLevel.WARN, "Mapping SYSTEM category to APPLICATION for errorData " + errorData.getErrorId() );
						errorData.setCategory(ErrorCategory.APPLICATION);
					}
				}
			}
		} else {
			// wrap the exception, so that logger and error mapper can figure out it's an app error
			e = new AppErrorWrapperException(e);
		}

		// add to context only, ErrorMapper is responsible for setting ErrorResponse on the message
		ctx.addError(e);
	}

	/**
	 * Private method to get service instance.
	 * @return
	 * @throws ServiceException
	 */
	private T getServiceInstance() throws ServiceException {
		T result = null;
		synchronized (this) {
			if (!m_serviceInstances.isEmpty()) {
				result = m_serviceInstances.removeFirst();
			}
		}

		if (result == null) {
			result = createServiceInstance();
		}

		return result;
	}

	private synchronized void returnServiceInstance(T service) {
		m_serviceInstances.addFirst(service);
	}

	private T createServiceInstance() throws ServiceException {
		if (m_svcId == null) {
			throw new IllegalStateException(this.getClass().getName() + " has not been initialized");
		}

		T result = ReflectionUtils.createInstance(m_serviceImplClassName,
			m_genServiceInterface, m_classLoader);

		m_serviceCreationCount++;
		if ((m_serviceCreationCount % 500) == 0) {
			LogManager.getInstance(BaseServiceRequestDispatcher.class).log(Level.SEVERE,
				"Excessive creation of service instances " + m_serviceImplClassName + " has been detected, " +
				m_serviceCreationCount + " instances have been created since service startup");
		}

		return result;
	}

	private void verifyOperations(Collection<ServiceOperationDesc> ops) throws ServiceException {
		for (ServiceOperationDesc op: ops) {
			String name = op.getName();

/*			if (s_systemOpNames.contains(name)) {
				return;
			}
*/			DispatchOperactionDef def = m_supportedOps.get(name);
			if (def == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNKNOWN_OPERATION_IN_CONFIG,
						ErrorConstants.ERRORDOMAIN, new Object[] {m_svcId.getAdminName() + "." + name}));
			}

			verifyParams(op.getRequestType().getRootJavaTypes(), def.m_inParams, name);
			verifyParams(op.getResponseType().getRootJavaTypes(), def.m_outParams, name);
		}
	}

	private void verifyParams(List<Class> configParams, Class[] realParams, String opName)
		throws ServiceException
	{
		if (configParams.size() == realParams.length) {
			return;
		}

		throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_PARAM_COUNT_IN_CONFIG,
				ErrorConstants.ERRORDOMAIN, new Object[] {Integer.toString(realParams.length),
					m_svcId.getAdminName() + "." + opName, Integer.toString(configParams.size())}));
	}

	static {
		s_systemOpNames.add(SOAConstants.OP_GET_VERSION);
		s_systemOpNames.add(OP_GET_SERVICE_VERSION);
		s_systemOpNames.add(SOAConstants.OP_IS_SERVICE_VERSION_SUPPORTED);
		s_systemOpNames.add(SOAConstants.OP_GET_CACHE_POLICY);
	}

	private static class DispatchOperactionDef {
		final Class[] m_inParams;
		final Class[] m_outParams;

		DispatchOperactionDef(String name, Class[] inParams, Class[] outParams) {
			m_inParams = (inParams != null ? inParams : new Class[0]);
			m_outParams = (outParams != null ? outParams : new Class[0]);
		}
	}


}
