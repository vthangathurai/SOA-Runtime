/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.gen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.AsyncTest1Service;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Exception;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1ServiceException;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * This class is not thread safe
 * 
 */
public class BaseTest1ServiceConsumer {

    private URL m_serviceLocation = null;
    private final static String SVC_ADMIN_NAME = "Test1Service";
    private String m_clientName = "Test1Service";
    private String m_environment;
    private AsyncTest1Service m_proxy = null;
    private String m_authToken = null;
    private Cookie[] m_cookies;
    private Service m_service = null;

    public BaseTest1ServiceConsumer() {
    }

    public BaseTest1ServiceConsumer(String clientName)
        throws ServiceException
    {
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
    }

    public BaseTest1ServiceConsumer(String clientName, String environment)
        throws ServiceException
    {
        if (environment == null) {
            throw new ServiceException("environment can not be null");
        }
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
        m_environment = environment;
    }

    /**
     * Use this method to initialize ConsumerApp after creating a Consumer instance
     * 
     */
    public void init()
        throws ServiceException
    {
        getService();
    }

    protected void setServiceLocation(String serviceLocation)
        throws MalformedURLException
    {
        m_serviceLocation = new URL(serviceLocation);
        if (m_service!= null) {
            m_service.setServiceLocation(m_serviceLocation);
        }
    }

    private void setUserProvidedSecurityCredentials(Service service) {
        if (m_authToken!= null) {
            service.setSessionTransportHeader(SOAHeaders.AUTH_TOKEN, m_authToken);
        }
        if (m_cookies!= null) {
            for (int i = 0; (i<m_cookies.length); i ++) {
                service.setCookie(m_cookies[i]);
            }
        }
    }

    /**
     * Use this method to set User Credentials (Token) 
     * 
     */
    protected void setAuthToken(String authToken) {
        m_authToken = authToken;
    }

    /**
     * Use this method to set User Credentials (Cookie)
     * 
     */
    protected void setCookies(Cookie[] cookies) {
        m_cookies = cookies;
    }

    /**
     * Use this method to get the Invoker Options on the Service and set them to user-preferences
     * 
     */
    public ServiceInvokerOptions getServiceInvokerOptions()
        throws ServiceException
    {
        m_service = getService();
        return m_service.getInvokerOptions();
    }

    protected AsyncTest1Service getProxy()
        throws ServiceException
    {
        m_service = getService();
        m_proxy = m_service.getProxy();
        return m_proxy;
    }

    /**
     * Method returns an instance of Service which has been initilized for this Consumer
     * 
     */
    public Service getService()
        throws ServiceException
    {
        if (m_service == null) {
            m_service = ServiceFactory.create(SVC_ADMIN_NAME, m_environment, m_clientName, m_serviceLocation);
        }
        setUserProvidedSecurityCredentials(m_service);
        return m_service;
    }

    public List<Response<?>> poll(boolean param0, boolean param1)
        throws InterruptedException
    {
        List<Response<?>> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.poll(param0, param1);
        return result;
    }

    public Response<MyMessage> myTestOperationAsync(MyMessage param0) {
        Response<MyMessage> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myTestOperationAsync(param0);
        return result;
    }

    public Future<?> myTestOperationAsync(MyMessage param0, AsyncHandler<MyMessage> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myTestOperationAsync(param0, param1);
        return result;
    }

    public Future<?> myNonArgOperationAsync(AsyncHandler<MyMessage> param0) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myNonArgOperationAsync(param0);
        return result;
    }

    public Response<MyMessage> myNonArgOperationAsync() {
        Response<MyMessage> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myNonArgOperationAsync();
        return result;
    }

    public Response<?> myVoidReturnOperationAsync(MyMessage param0) {
        Response<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myVoidReturnOperationAsync(param0);
        return result;
    }

    public Future<?> myVoidReturnOperationAsync(MyMessage param0, AsyncHandler<?> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myVoidReturnOperationAsync(param0, param1);
        return result;
    }

    public Future<?> serviceChainingOperationAsync(MyMessage param0, AsyncHandler<MyMessage> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.serviceChainingOperationAsync(param0, param1);
        return result;
    }

    public Response<MyMessage> serviceChainingOperationAsync(MyMessage param0) {
        Response<MyMessage> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.serviceChainingOperationAsync(param0);
        return result;
    }

    public Response<String> echoStringAsync(String param0) {
        Response<String> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.echoStringAsync(param0);
        return result;
    }

    public Future<?> echoStringAsync(String param0, AsyncHandler<String> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.echoStringAsync(param0, param1);
        return result;
    }

    public Response<?> customError1Async() {
        Response<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.customError1Async();
        return result;
    }

    public Future<?> customError1Async(AsyncHandler<?> param0) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.customError1Async(param0);
        return result;
    }

    public Response<MyMessage> customError2Async(MyMessage param0) {
        Response<MyMessage> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.customError2Async(param0);
        return result;
    }

    public Future<?> customError2Async(MyMessage param0, AsyncHandler<MyMessage> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.customError2Async(param0, param1);
        return result;
    }

    public MyMessage myTestOperation(MyMessage param0)
        throws Test1Exception, Test1ServiceException
    {
        MyMessage result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myTestOperation(param0);
        return result;
    }

    public MyMessage myNonArgOperation()
        throws Test1Exception, Test1ServiceException
    {
        MyMessage result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.myNonArgOperation();
        return result;
    }

    public void myVoidReturnOperation(MyMessage param0)
        throws Test1Exception, Test1ServiceException
    {
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        m_proxy.myVoidReturnOperation(param0);
        return ;
    }

    public MyMessage serviceChainingOperation(MyMessage param0)
        throws Test1Exception, Test1ServiceException
    {
        MyMessage result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.serviceChainingOperation(param0);
        return result;
    }

    public String echoString(String param0)
        throws Test1Exception, Test1ServiceException
    {
        String result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.echoString(param0);
        return result;
    }

    public void customError1()
        throws Test1Exception, Test1ServiceException
    {
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        m_proxy.customError1();
        return ;
    }

    public MyMessage customError2(MyMessage param0)
        throws Test1Exception, Test1ServiceException
    {
        MyMessage result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.customError2(param0);
        return result;
    }

}
