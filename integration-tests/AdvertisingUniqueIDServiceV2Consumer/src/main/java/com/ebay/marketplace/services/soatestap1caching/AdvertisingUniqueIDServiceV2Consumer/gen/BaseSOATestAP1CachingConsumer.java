
package com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer.gen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import com.ebay.marketplace.services.soatestap1caching.AsyncSOATestAP1Caching;
import com.ebay.marketplace.v1.services.CheckItemValidityRequest;
import com.ebay.marketplace.v1.services.CheckItemValidityResponse;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsResponse;
import com.ebay.marketplace.v1.services.GetItemIdDetailsRequest;
import com.ebay.marketplace.v1.services.GetItemIdDetailsResponse;
import com.ebay.marketplace.v1.services.GetManagerDetailsResponse;
import com.ebay.marketplace.v1.services.GetPaymentDetailsRequest;
import com.ebay.marketplace.v1.services.GetPaymentDetailsResponse;
import com.ebay.marketplace.v1.services.GetUserDetailsRequest;
import com.ebay.marketplace.v1.services.GetUserDetailsResponse;
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
public class BaseSOATestAP1CachingConsumer {

    private URL m_serviceLocation = null;
    private final static String SVC_ADMIN_NAME = "SOATestAP1Caching";
    private String m_clientName = "AdvertisingUniqueIDServiceV2Consumer";
    private String m_environment = "production";
    private AsyncSOATestAP1Caching m_proxy = null;
    private String m_authToken = null;
    private Cookie[] m_cookies;
    private Service m_service = null;

    public BaseSOATestAP1CachingConsumer() {
    }

    public BaseSOATestAP1CachingConsumer(String clientName)
        throws ServiceException
    {
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
    }

    public BaseSOATestAP1CachingConsumer(String clientName, String environment)
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

    protected AsyncSOATestAP1Caching getProxy()
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

    public Future<?> getEmployeeDetailsAsync(GetEmployeeDetailsRequest param0, AsyncHandler<GetEmployeeDetailsResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getEmployeeDetailsAsync(param0, param1);
        return result;
    }

    public Response<GetEmployeeDetailsResponse> getEmployeeDetailsAsync(GetEmployeeDetailsRequest param0) {
        Response<GetEmployeeDetailsResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getEmployeeDetailsAsync(param0);
        return result;
    }

    public Future<?> checkItemValidityAsync(CheckItemValidityRequest param0, AsyncHandler<CheckItemValidityResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.checkItemValidityAsync(param0, param1);
        return result;
    }

    public Response<CheckItemValidityResponse> checkItemValidityAsync(CheckItemValidityRequest param0) {
        Response<CheckItemValidityResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.checkItemValidityAsync(param0);
        return result;
    }

    public Future<?> getItemIdDetailsAsync(GetItemIdDetailsRequest param0, AsyncHandler<GetItemIdDetailsResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getItemIdDetailsAsync(param0, param1);
        return result;
    }

    public Response<GetItemIdDetailsResponse> getItemIdDetailsAsync(GetItemIdDetailsRequest param0) {
        Response<GetItemIdDetailsResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getItemIdDetailsAsync(param0);
        return result;
    }

    public Future<?> getUserDetailsAsync(GetUserDetailsRequest param0, AsyncHandler<GetUserDetailsResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getUserDetailsAsync(param0, param1);
        return result;
    }

    public Response<GetUserDetailsResponse> getUserDetailsAsync(GetUserDetailsRequest param0) {
        Response<GetUserDetailsResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getUserDetailsAsync(param0);
        return result;
    }

    public Future<?> getManagerDetailsAsync(AsyncHandler<GetManagerDetailsResponse> param0) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getManagerDetailsAsync(param0);
        return result;
    }

    public Response<GetManagerDetailsResponse> getManagerDetailsAsync() {
        Response<GetManagerDetailsResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getManagerDetailsAsync();
        return result;
    }

    public Future<?> getPaymentDetailsAsync(GetPaymentDetailsRequest param0, AsyncHandler<GetPaymentDetailsResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getPaymentDetailsAsync(param0, param1);
        return result;
    }

    public Response<GetPaymentDetailsResponse> getPaymentDetailsAsync(GetPaymentDetailsRequest param0) {
        Response<GetPaymentDetailsResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getPaymentDetailsAsync(param0);
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

    public GetEmployeeDetailsResponse getEmployeeDetails(GetEmployeeDetailsRequest param0) {
        GetEmployeeDetailsResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getEmployeeDetails(param0);
        return result;
    }

    public CheckItemValidityResponse checkItemValidity(CheckItemValidityRequest param0) {
        CheckItemValidityResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.checkItemValidity(param0);
        return result;
    }

    public GetItemIdDetailsResponse getItemIdDetails(GetItemIdDetailsRequest param0) {
        GetItemIdDetailsResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getItemIdDetails(param0);
        return result;
    }

    public GetUserDetailsResponse getUserDetails(GetUserDetailsRequest param0) {
        GetUserDetailsResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getUserDetails(param0);
        return result;
    }

    public GetManagerDetailsResponse getManagerDetails() {
        GetManagerDetailsResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getManagerDetails();
        return result;
    }

    public GetPaymentDetailsResponse getPaymentDetails(GetPaymentDetailsRequest param0) {
        GetPaymentDetailsResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getPaymentDetails(param0);
        return result;
    }

}
