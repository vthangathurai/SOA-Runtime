
package com.ebay.marketplace.services.advertisinguniqueidservicev2.gen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayRequest;
import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayResponse;
import com.ebay.marketplace.advertising.v1.services.GetNestedGenericClientInfoRequest;
import com.ebay.marketplace.advertising.v1.services.GetNestedGenericClientInfoResponse;
import com.ebay.marketplace.advertising.v1.services.GetNestedServiceRequestIDResponse;
import com.ebay.marketplace.advertising.v1.services.GetNestedTransportHeaders;
import com.ebay.marketplace.advertising.v1.services.GetNestedTransportHeadersResponse;
import com.ebay.marketplace.advertising.v1.services.GetVersionResponse;
import com.ebay.marketplace.advertising.v1.services.TestSchemaValidationWithoutUPA;
import com.ebay.marketplace.advertising.v1.services.TestSchemaValidationWithoutUPAResponse;
import com.ebay.marketplace.services.advertisinguniqueidservicev2.AsyncAdvertisingUniqueIDServiceV2;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.exceptions.ServiceRuntimeException;
import com.ebay.soaframework.common.registration.ClassLoaderRegistry;
import com.ebay.soaframework.common.types.Cookie;
import com.ebay.soaframework.common.types.SOAHeaders;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;
import com.ebay.soaframework.sif.service.ServiceInvokerOptions;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * This class is not thread safe
 * 
 */
public class BaseAdvertisingUniqueIDServiceV2Consumer {

    private URL m_serviceLocation = null;
    private boolean m_useDefaultClientConfig;
    private final static String SVC_ADMIN_NAME = "AdvertisingUniqueIDServiceV2";
    private String m_clientName = "AdvertisingUniqueIDServiceV2Consumer";
    private String m_environment = "production";
    private AsyncAdvertisingUniqueIDServiceV2 m_proxy = null;
    private String m_authToken = null;
    private Cookie[] m_cookies;
    private Service m_service = null;

    public BaseAdvertisingUniqueIDServiceV2Consumer() {
    }

    /**
     * This constructor should be used, when a ClientConfig.xml is located in the 
     * "client" bundle, so that a ClassLoader of this Shared Consumer can be used.
     * 
     * @param clientName
     * @throws ServiceException
     * 
     */
    public BaseAdvertisingUniqueIDServiceV2Consumer(String clientName)
        throws ServiceException
    {
        this(clientName, null);
    }

    /**
     * This constructor should be used, when a ClientConfig.xml is located in the 
     * "client" bundle, so that a ClassLoader of this Shared Consumer can be used.
     * 
     * @param clientName
     * @param environment
     * @throws ServiceException
     * 
     */
    public BaseAdvertisingUniqueIDServiceV2Consumer(String clientName, String environment)
        throws ServiceException
    {
        this(clientName, environment, null, false);
    }

    /**
     * This constructor should be used, when a ClientConfig.xml is located 
     * in some application bundle. Shared Consumer then will call ClassLoaderRegistry 
     * to register a ClassLoader of an application bundle.
     * 
     * @param clientName
     * @param caller
     * @param useDefaultClientConfig
     * @throws ServiceException
     * 
     */
    public BaseAdvertisingUniqueIDServiceV2Consumer(String clientName, Class caller, boolean useDefaultClientConfig)
        throws ServiceException
    {
        this(clientName, null, caller, useDefaultClientConfig);
    }

    /**
     * This constructor should be used, when a ClientConfig.xml is located 
     * in some application bundle. Shared Consumer then will call ClassLoaderRegistry 
     * to register a ClassLoader of an application bundle.
     * 
     * @param clientName
     * @param environment
     * @param caller
     * @param useDefaultClientConfig
     * @throws ServiceException
     * 
     */
    public BaseAdvertisingUniqueIDServiceV2Consumer(String clientName, String environment, Class caller, boolean useDefaultClientConfig)
        throws ServiceException
    {
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
        m_environment = environment;
        m_useDefaultClientConfig = useDefaultClientConfig;
        ClassLoaderRegistry.instanceOf().registerServiceClient(m_clientName, m_environment, SVC_ADMIN_NAME, (BaseAdvertisingUniqueIDServiceV2Consumer.class), caller, m_useDefaultClientConfig);
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
            m_service = ServiceFactory.create(SVC_ADMIN_NAME, m_environment, m_clientName, m_serviceLocation, false, m_useDefaultClientConfig);
        }
        setUserProvidedSecurityCredentials(m_service);
        return m_service;
    }

    public Future<?> getNestedTransportHeadersAsync(GetNestedTransportHeaders param0, AsyncHandler<GetNestedTransportHeadersResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedTransportHeadersAsync(param0, param1);
        return result;
    }

    public Response<GetNestedTransportHeadersResponse> getNestedTransportHeadersAsync(GetNestedTransportHeaders param0) {
        Response<GetNestedTransportHeadersResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedTransportHeadersAsync(param0);
        return result;
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

    public Future<?> getNestedServiceRequestIDAsync(AsyncHandler<GetNestedServiceRequestIDResponse> param0) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedServiceRequestIDAsync(param0);
        return result;
    }

    public Response<GetNestedServiceRequestIDResponse> getNestedServiceRequestIDAsync() {
        Response<GetNestedServiceRequestIDResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedServiceRequestIDAsync();
        return result;
    }

    public Future<?> getNestedGenericClientInfoAsync(GetNestedGenericClientInfoRequest param0, AsyncHandler<GetNestedGenericClientInfoResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedGenericClientInfoAsync(param0, param1);
        return result;
    }

    public Response<GetNestedGenericClientInfoResponse> getNestedGenericClientInfoAsync(GetNestedGenericClientInfoRequest param0) {
        Response<GetNestedGenericClientInfoResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedGenericClientInfoAsync(param0);
        return result;
    }

    public Future<?> testSchemaValidationWithUPAAsync(GetMessagesForTheDayRequest param0, AsyncHandler<GetMessagesForTheDayResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.testSchemaValidationWithUPAAsync(param0, param1);
        return result;
    }

    public Response<GetMessagesForTheDayResponse> testSchemaValidationWithUPAAsync(GetMessagesForTheDayRequest param0) {
        Response<GetMessagesForTheDayResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.testSchemaValidationWithUPAAsync(param0);
        return result;
    }

    public Future<?> testSchemaValidationWithoutUPAAsync(TestSchemaValidationWithoutUPA param0, AsyncHandler<TestSchemaValidationWithoutUPAResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.testSchemaValidationWithoutUPAAsync(param0, param1);
        return result;
    }

    public Response<TestSchemaValidationWithoutUPAResponse> testSchemaValidationWithoutUPAAsync(TestSchemaValidationWithoutUPA param0) {
        Response<TestSchemaValidationWithoutUPAResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.testSchemaValidationWithoutUPAAsync(param0);
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

    public GetNestedTransportHeadersResponse getNestedTransportHeaders(GetNestedTransportHeaders param0) {
        GetNestedTransportHeadersResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedTransportHeaders(param0);
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

    public GetNestedServiceRequestIDResponse getNestedServiceRequestID() {
        GetNestedServiceRequestIDResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedServiceRequestID();
        return result;
    }

    public GetNestedGenericClientInfoResponse getNestedGenericClientInfo(GetNestedGenericClientInfoRequest param0) {
        GetNestedGenericClientInfoResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getNestedGenericClientInfo(param0);
        return result;
    }

    public GetMessagesForTheDayResponse testSchemaValidationWithUPA(GetMessagesForTheDayRequest param0) {
        GetMessagesForTheDayResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.testSchemaValidationWithUPA(param0);
        return result;
    }

    public TestSchemaValidationWithoutUPAResponse testSchemaValidationWithoutUPA(TestSchemaValidationWithoutUPA param0) {
        TestSchemaValidationWithoutUPAResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.testSchemaValidationWithoutUPA(param0);
        return result;
    }

}
