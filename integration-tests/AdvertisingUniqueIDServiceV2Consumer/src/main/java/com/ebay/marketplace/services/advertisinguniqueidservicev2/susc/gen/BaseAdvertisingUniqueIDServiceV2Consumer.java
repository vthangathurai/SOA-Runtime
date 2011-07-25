
package com.ebay.marketplace.services.advertisinguniqueidservicev2.AdvertisingUniqueIDServiceV2Consumer.gen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import com.ebay.marketplace.advertising.v1.services.GetVersionResponse;
import com.ebay.marketplace.services.advertisinguniqueidservicev2.AsyncAdvertisingUniqueIDServiceV2;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.exceptions.ServiceRuntimeException;
import com.ebay.soaframework.common.types.Cookie;
import com.ebay.soaframework.common.types.SOAHeaders;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;
import com.ebay.soaframework.sif.service.ServiceInvokerOptions;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * 
 */
public class BaseAdvertisingUniqueIDServiceV2Consumer {

    private URL m_serviceLocation = null;
    private final static String SVC_ADMIN_NAME = "AdvertisingUniqueIDServiceV2";
    private String m_clientName = "AdvertisingUniqueIDServiceV2";
    private String m_environment = "production";
    private AsyncAdvertisingUniqueIDServiceV2 m_proxy = null;
    private String m_authToken = null;
    private Cookie[] m_cookies;
    private Service m_service = null;

    public BaseAdvertisingUniqueIDServiceV2Consumer() {
    }

    public BaseAdvertisingUniqueIDServiceV2Consumer(String clientName)
        throws ServiceException
    {
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
    }

    public BaseAdvertisingUniqueIDServiceV2Consumer(String clientName, String environment)
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

    protected AsyncAdvertisingUniqueIDServiceV2 getProxy()
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

    public Future<?> getVersionAsync(AsyncHandler<GetVersionResponse> param0) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getVersionAsync(param0);
        return result;
    }

    public Response<GetVersionResponse> getVersionAsync() {
        Response<GetVersionResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getVersionAsync();
        return result;
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

    public GetVersionResponse getVersion() {
        GetVersionResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getVersion();
        return result;
    }

}
