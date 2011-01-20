/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.error;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.error.model.Error;
import org.ebayopensource.turmeric.runtime.error.model.ErrorValue;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.utils.jpa.JPAAroundAdvice;
import org.ebayopensource.turmeric.utils.jpa.PersistenceContext;

/**
 * The errors are made up by the framework and by the application.
 * Framework errors are most always wrapped up into a ServiceException that contains ErrorData objects.
 * Application errors *must* be converted by the application to ErrorData objects.
 * To do this, the framework provides {@link org.ebayopensource.turmeric.runtime.spf.impl.internal.servicse.ServiceImplHelper}
 * which has a bunch of getCommonErrorData() methods that allow to convert the exception into an ErrorData object.
 * If the application needs to rethrow some business exception, then the ErrorData object
 * can be stuffed into the BaseResponse object, and it will be logged by the ResponseResidentErrorHandler
 * (which must be a normal handler and not a logging handler - since the invocation did not throw).
 *
 * Note that ServiceImplHelper says that ErrorData is deprecated, and indeed it lacks a
 * very important field, the errorName, which is present in CommonErrorData.
 *
 * Applications that build ErrorData object must pass to getCommonErrorData() a bunch of arguments
 * which specify the resource bundle to look for to create errors.
 *
 * So for example:
 *
 * public WithdrawResponse withdraw(WithdrawRequest request)
 * {
 *     WithdrawResponse response = new WithdrawResponse();
 *     response.setErrorMessage(new ErrorMessage());
 *     if (balance < request.amount)
 *     {
 *         ErrorDataProvider.ErrorDataKey errorDataKey =
 *             new ErrorDataProvider.ErrorDataKey("some_error_library", "some_error_bundle", "some_error_name");
 *         CommonErrorData error = ServiceImplHelper.getCommonErrorData(errorDataKey, new Object[]{}});
 *
 *         // Either throw or add to the response:
 *         response.getErrorMessage().getError().add(error);
 *         throw new ServiceRuntimeException(error);
 *     }
 *     ...
 *     return response;
 * }
 *
 * Error libraries and bundles are specified in META-INF/errorlibrary/[domain]/ErrorData.xml, along with
 * bundles for L10N in the same directory named Errors (hence Errors_it.properties, for example).
 *
 * Since error libraries are not aware of each other, it is possible that they define the same error name;
 * the definition is in an XML file, so there is no possibility to enforce uniqueness of the error name.
 * When an error happens, this handler needs to be able to store the error event information to the DB,
 * so it needs to identify uniquely which error must be linked to the event.
 * This is achieved using errorIds. While errorIds are generated locally by the plugin (that creates the
 * error library through a wizard), the errorId generation is pluggable, and the generator that will be
 * plugged in must enforce uniqueness.
 */
public class DAOErrorLoggingHandler implements LoggingHandler
{
    private LoggingHandler delegate;

    @Override
    public void init(InitContext ctx) throws ServiceException {
        Map<String,String> options = ctx.getOptions();
        String persistenceUnitName = options.get("persistenceUnitName");
        EntityManagerFactory entityManagerFactory = PersistenceContext.createEntityManagerFactory(persistenceUnitName);
        String errorDAOClassName = options.get("errorDAOClassName");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ErrorDAO errorDAO = ReflectionUtils.createInstance(errorDAOClassName, ErrorDAO.class, contextClassLoader);

        ClassLoader classLoader = LoggingHandler.class.getClassLoader();
        Class[] interfaces = {LoggingHandler.class};
        Target target = new Target(errorDAO);
        JPAAroundAdvice handler = new JPAAroundAdvice(entityManagerFactory, target);
        delegate = (LoggingHandler) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }

    @Override
    public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
        delegate.logProcessingStage(ctx, stage);
    }

    @Override
    public void logResponseResidentError(MessageContext ctx, ErrorData errorData) throws ServiceException {
        delegate.logResponseResidentError(ctx, errorData);
    }

    @Override
    public void logError(MessageContext ctx, Throwable throwable) throws ServiceException {
        delegate.logError(ctx, throwable);
    }

    @Override
    public void logWarning(MessageContext ctx, Throwable throwable) throws ServiceException {
        delegate.logWarning(ctx, throwable);
    }

    private static class Target implements LoggingHandler {
        private final ErrorDAO errorDAO;

        private Target(ErrorDAO errorDAO) {
            this.errorDAO = errorDAO;
        }

        @Override
        public void init(InitContext ctx) throws ServiceException {
        }

        @Override
        public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
        }

        @Override
        public void logResponseResidentError(MessageContext ctx, ErrorData errorData) throws ServiceException {
            persistErrors(ctx, Collections.singletonList(errorData));
        }

        @Override
        public void logError(MessageContext ctx, Throwable throwable) throws ServiceException {
            if (throwable instanceof ServiceExceptionInterface) {
                ServiceExceptionInterface serviceException = (ServiceExceptionInterface) throwable;
                persistErrors(ctx, serviceException.getErrorMessage().getError());
            } else {
                // TODO: create an error data from the exception itself ?
            }
        }

        @Override
        public void logWarning(MessageContext ctx, Throwable throwable) throws ServiceException {
            logError(ctx, throwable);
        }

        private void persistErrors(MessageContext ctx, List<? extends ErrorData> errors) throws ServiceException {
            long now = System.currentTimeMillis();

            List<ErrorValue> errorValues = new ArrayList<ErrorValue>();
            for (ErrorData data : errors) {
                CommonErrorData errorData = (CommonErrorData) data;

                org.ebayopensource.turmeric.runtime.error.model.Error error =
                        new Error(
                                errorData.getErrorId(),
                                errorData.getErrorName(),
                                errorData.getCategory(),
                                errorData.getSeverity(),
                                errorData.getDomain(),
                                errorData.getSubdomain(),
                                errorData.getOrganization()
                                );
                Error existing = errorDAO.persistErrorIfAbsent(error);
                if (existing != null)
                    error = existing;

                String errorMessage = errorData.getMessage();
                String serviceAdminName = ctx.getAdminName();
                String operationName = ctx.getOperationName();
                String consumerName = retrieveConsumerName(ctx);
                boolean serverSide = !ctx.getServiceId().isClientSide();
                ErrorValue errorValue = new ErrorValue(error, errorMessage, serviceAdminName, operationName, consumerName, now, serverSide, 0);
                errorValues.add(errorValue);
            }

            errorDAO.persistErrorValues(errorValues);
        }

        private String retrieveConsumerName(MessageContext ctx) throws ServiceException {
            String result = ctx.getRequestMessage().getTransportHeader(SOAHeaders.USECASE_NAME);
            if (result == null)
                result = SOAConstants.DEFAULT_USE_CASE;
            return result;
        }
    }
}
