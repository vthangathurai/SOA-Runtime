/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;

import org.apache.commons.lang.StringUtils;
import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.RequestContext;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.SimpleJettyServer;
import org.ebayopensource.turmeric.runtime.tests.common.sif.Test1Driver.TestMode;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;

public class ServicePayloadExecutor {
    private static final Logger LOG = Logger.getLogger(ServicePayloadExecutor.class.getName());
    private String serviceName;
    private String clientName;
    private String serviceVersion;
    private URL serviceURL;
    private List<String> requestPayloadFormats = new ArrayList<String>();
    private List<String> responsePayloadFormats = new ArrayList<String>();
    private List<TestMode> testModes = new ArrayList<TestMode>();
    private String operationName;
    private G11nOptions g11nOptions = new G11nOptions();
    private boolean useInParams = true;
    private int repeatCount = 1;
    private MyMessage message;
    private Map<String, String> transportHeaders = new HashMap<String, String>();
    private List<ObjectNode> requestMessageHeaders = new ArrayList<ObjectNode>();
    private Map<String, String> sessionTransportHeaders = new HashMap<String, String>();
    private List<ObjectNode> sessionMessageHeaders = new ArrayList<ObjectNode>();
    private String transportName;
    private String protocolName;
    private List<Cookie> cookies = new ArrayList<Cookie>();
    private Integer invocationTimeout;
    private boolean useDefaultBinding;
    private boolean skipSerialization;
    private boolean detachedLocalBinding;
    private boolean useRest;
    private String urlPathInfo;
    private ExceptionAssert assertException;
    private ResponseAssert assertResponse;
    private PayloadAssert assertPayload;

    /**
     * Adds all payload formats to the test scenarios being tested (both request and response)
     * 
     * @see BindingConstants#PAYLOAD_XML
     * @see BindingConstants#PAYLOAD_FAST_INFOSET
     * @see BindingConstants#PAYLOAD_JSON
     * @see BindingConstants#PAYLOAD_NV
     */
    public void addAllPayloadFormats() {
        addSymmetricPayloadFormats(BindingConstants.PAYLOAD_XML, BindingConstants.PAYLOAD_FAST_INFOSET,
                        BindingConstants.PAYLOAD_JSON, BindingConstants.PAYLOAD_NV);
    }

    /**
     * Adds all test modes to the test scenario being tested.
     * 
     * @see TestMode#SYNC
     * @see TestMode#ASYNC_SYNC
     * @see TestMode#ASYNC_PUSH
     * @see TestMode#ASYNC_PULL
     */
    public void addAllTestModes() {
        addTestMode(TestMode.SYNC, TestMode.ASYNC_PULL, TestMode.ASYNC_PUSH);
    }

    /**
     * Add a request payload format.
     * 
     * @see BindingConstants#PAYLOAD_XML
     * @see BindingConstants#PAYLOAD_FAST_INFOSET
     * @see BindingConstants#PAYLOAD_JSON
     * @see BindingConstants#PAYLOAD_NV
     */
    public void addRequestPayloadFormats(String... payloadIds) {
        for (String payloadId : payloadIds) {
            if (!requestPayloadFormats.contains(payloadId)) {
                requestPayloadFormats.add(payloadId);
            }
        }
    }

    /**
     * Add a request payload format.
     * 
     * @see BindingConstants#PAYLOAD_XML
     * @see BindingConstants#PAYLOAD_FAST_INFOSET
     * @see BindingConstants#PAYLOAD_JSON
     * @see BindingConstants#PAYLOAD_NV
     */
    public void addResponsePayloadFormats(String... payloadIds) {
        for (String payloadId : payloadIds) {
            if (!responsePayloadFormats.contains(payloadId)) {
                responsePayloadFormats.add(payloadId);
            }
        }
    }

    /**
     * Add a request & response payload format.
     * <p>
     * Adds the same payloadIds to both request and response formats
     * 
     * @see BindingConstants#PAYLOAD_XML
     * @see BindingConstants#PAYLOAD_FAST_INFOSET
     * @see BindingConstants#PAYLOAD_JSON
     * @see BindingConstants#PAYLOAD_NV
     */
    public void addSymmetricPayloadFormats(String... payloadIds) {
        addRequestPayloadFormats(payloadIds);
        addResponsePayloadFormats(payloadIds);
    }

    /**
     * Add test mode to the test scenario being tested.
     * 
     * @see TestMode#SYNC
     * @see TestMode#ASYNC_SYNC
     * @see TestMode#ASYNC_PUSH
     * @see TestMode#ASYNC_PULL
     */
    public void addTestMode(TestMode... modes) {
        for (TestMode mode : modes) {
            if (!testModes.contains(mode)) {
                testModes.add(mode);
            }
        }
    }

    public void addTransportHeader(String key, boolean flag) {
        this.transportHeaders.put(key, Boolean.toString(flag));
    }

    public void addTransportHeader(String key, String value) {
        this.transportHeaders.put(key, value);
    }

    public void addTransportHeaderHarmlessException() {
        addTransportHeader("TR_HDR_TEST1_HARMLESS_EXCEPTION", "true");
    }

    private void assertNotExpectingException() {
        Assert.assertNull("Service invocation succeeded, but the test case was expecting an Exception: "
                        + assertException, assertException);
    }

    private Service createServiceInstance(String requestPayloadFormat, String responsePayloadFormat)
                    throws ServiceException {
        Service test1 = ServiceFactory.create(serviceName, clientName, serviceURL, serviceVersion);
        ServiceInvokerOptions options = test1.getInvokerOptions();

        if (g11nOptions != null) {
            test1.setG11nOptions(g11nOptions);
        }

        if (serviceURL == null) {
            options.setTransportName(SOAConstants.TRANSPORT_LOCAL);
        }
        else if (transportName != null) {
            options.setTransportName(transportName);
        }

        if (protocolName != null) {
            options.setMessageProtocolName(protocolName);
        }

        for (Cookie cookie : cookies) {
            test1.setCookie(cookie);
        }

        for (Map.Entry<String, String> e : sessionTransportHeaders.entrySet()) {
            test1.setSessionTransportHeader(e.getKey(), e.getValue());
        }

        for (ObjectNode node : sessionMessageHeaders) {
            test1.addSessionMessageHeader(node);
        }

        if (!useDefaultBinding) {
            options.setRequestBinding(requestPayloadFormat);
            options.setResponseBinding(responsePayloadFormat);
        }

        if (skipSerialization) {
            options.getTransportOptions().setSkipSerialization(Boolean.TRUE);
        }

        if (detachedLocalBinding) {
            options.getTransportOptions().setUseDetachedLocalBinding(Boolean.TRUE);
        }

        if (invocationTimeout != null) {
            options.getTransportOptions().setInvocationTimeout(invocationTimeout);
        }

        if (useRest) {
            options.setREST(Boolean.TRUE);
        }

        if (StringUtils.isNotBlank(urlPathInfo)) {
            options.setUrlPathInfo(urlPathInfo);
        }

        options.setRecordResponsePayload(true);

        RequestContext ctx = test1.getRequestContext();
        for (Map.Entry<String, String> e : transportHeaders.entrySet()) {
            ctx.setTransportHeader(e.getKey(), e.getValue());
        }

        for (ObjectNode headerObj : requestMessageHeaders) {
            ctx.addMessageHeader(headerObj);
        }

        return test1;
    }

    private void doCall(String requestPayloadFormat, String responsePayloadFormat, TestMode mode)
                    throws AssertionError, Exception {
        ExecutionScope scope = new ExecutionScope(requestPayloadFormat, responsePayloadFormat, mode);
        LOG.info(String.format("Starting Test1Service execution with %s", scope));

        int iterCount = repeatCount;

        for (int iteration = 0; iteration < iterCount; iteration++) {
            List<Object> outParams = new ArrayList<Object>();

            Service svc = createServiceInstance(requestPayloadFormat, responsePayloadFormat);

            long startTime, endTime, duration = -1;
            startTime = System.nanoTime();
            try {
                switch (mode) {
                    case SYNC:
                        invokeSync(scope, svc, outParams);
                        break;
                    case ASYNC_SYNC:
                        invokeAsyncSync(scope, svc, outParams);
                        break;
                    case ASYNC_PULL:
                        invokeAsyncPull(scope, svc, outParams);
                        break;
                    case ASYNC_PUSH:
                        invokeAsyncPush(scope, svc, outParams);
                        break;
                }
            }
            catch (Exception e) {
                if (assertException != null) {
                    assertException.assertException(scope, svc, e);
                }
            }
            finally {
                endTime = System.nanoTime();
                duration = endTime - startTime;
                LOG.info(String.format("Duration: %,d nsecs with %s", duration, scope));
            }
        }
    }

    public void doCalls() throws AssertionError, Exception {
        String payloadErrSuffix = " payload formats is empty."
                        + "  You should specify what formats you want to test with using the "
                        + ".addAllPayloadFormats() to test all formats, or "
                        + ".addSymmetricPayloadFormat() test the same format for request & response, or "
                        + ".addRequesPayloadFormat() or .addResponsePayloadFormat() to test specific payload combinations.";

        Assert.assertFalse("ServicePayloadExecutor.request" + payloadErrSuffix, requestPayloadFormats.isEmpty());
        Assert.assertFalse("ServicePayloadExecutor.response" + payloadErrSuffix, responsePayloadFormats.isEmpty());

        if (testModes.isEmpty()) {
            testModes.add(TestMode.SYNC);
        }

        for (String requestPayloadFormat : requestPayloadFormats) {
            for (String responsePayloadFormat : responsePayloadFormats) {
                for (TestMode mode : testModes) {
                    doCall(requestPayloadFormat, responsePayloadFormat, mode);
                }
            }
        }
    }

    public ExceptionAssert getAssertException() {
        return assertException;
    }

    public PayloadAssert getAssertPayload() {
        return assertPayload;
    }

    public ResponseAssert getAssertResponse() {
        return assertResponse;
    }

    public String getClientName() {
        return clientName;
    }

    public G11nOptions getG11nOptions() {
        return g11nOptions;
    }

    public Integer getInvocationTimeout() {
        return invocationTimeout;
    }

    public MyMessage getMessage() {
        return message;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public URL getServiceURL() {
        return serviceURL;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getTransportName() {
        return transportName;
    }

    public String getUrlPathInfo() {
        return urlPathInfo;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void invokeAsyncPull(ExecutionScope scope, Service svc, List<Object> outParams) throws Exception {
        Dispatch dispatch = null;
        Response response = null;
        Object obj = null;
        try {
            dispatch = svc.createDispatch(operationName);
            if(useInParams) {
                response = dispatch.invokeAsync(message);
            } else {
                response = dispatch.invokeAsync(null);
            }
            while (!response.isDone()) {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            obj = response.get();
            outParams.add(obj);
            assertNotExpectingException();
            if (assertPayload != null) {
                byte payload[] = (byte[]) response.getContext().get("PAYLOAD");
                assertPayload.assertPayload(scope, svc, payload);
            }
            if (assertResponse != null) {
                assertResponse.assertResponse(scope, svc, new ResponseAssertableResponse(response));
            }
        }
        finally {

        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void invokeAsyncPush(ExecutionScope scope, Service svc, List<Object> outParams) throws Exception {
        Dispatch dispatch = null;
        Future future = null;

        try {
            dispatch = svc.createDispatch(operationName);
            GenericAsyncHandler handler = new GenericAsyncHandler<MyMessage>();

            if(useInParams) {
                future = dispatch.invokeAsync(message, handler);
            } else {
                future = dispatch.invokeAsync(null, handler);
            }
            
            while (!future.isDone()) {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if (handler.hasError()) {
                throw (ExecutionException) handler.getError();
            }

            outParams.add(handler.get());

            Response response = handler.getResponse();
            assertNotExpectingException();
            if (assertPayload != null) {
                byte payload[] = (byte[]) response.getContext().get("PAYLOAD");
                assertPayload.assertPayload(scope, svc, payload);
            }
            if (assertResponse != null) {
                assertResponse.assertResponse(scope, svc, new ResponseAssertableResponse(response));
            }
        }
        finally {

        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void invokeAsyncSync(ExecutionScope scope, Service svc, List<Object> outParams) throws Exception {
        Object[] inParams = null;
        if (useInParams) {
            inParams = new Object[] { message };
        }

        Dispatch dispatch = null;
        Object obj = null;
        try {
            dispatch = svc.createDispatch(operationName);
            obj = dispatch.invoke(inParams);
            outParams.add(obj);
            assertNotExpectingException();
            if (assertPayload != null) {
                byte payload[] = (byte[]) dispatch.getResponseContext().get("PAYLOAD");
                assertPayload.assertPayload(scope, svc, payload);
            }
            if (assertResponse != null) {
                assertResponse.assertResponse(scope, svc, new DispatchAssertableResponse(dispatch));
            }
        }
        finally {

        }
    }

    private void invokeSync(ExecutionScope scope, Service svc, List<Object> outParams) throws Exception {
        Object[] inParams = null;
        if (useInParams) {
            inParams = new Object[] { message };
        }

        svc.invoke(this.operationName, inParams, outParams);
        assertNotExpectingException();
        if (assertPayload != null) {
            byte payload[] = svc.getResponseContext().getPayloadData();
            assertPayload.assertPayload(scope, svc, payload);
        }
        if (assertResponse != null) {
            assertResponse.assertResponse(scope, svc, new ServiceAssertableResponse(svc));
        }
    }

    public boolean isDetachedLocalBinding() {
        return detachedLocalBinding;
    }

    public boolean isSkipSerialization() {
        return skipSerialization;
    }

    public boolean isUseDefaultBinding() {
        return useDefaultBinding;
    }

    public boolean isUseInParams() {
        return useInParams;
    }

    public boolean isUseRest() {
        return useRest;
    }

    public void setAssertException(ExceptionAssert assertException) {
        this.assertException = assertException;
    }

    public void setAssertPayload(PayloadAssert assertPayload) {
        this.assertPayload = assertPayload;
    }

    public void setAssertResponse(ResponseAssert assertResponse) {
        this.assertResponse = assertResponse;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setDetachedLocalBinding(boolean detachedLocalBinding) {
        this.detachedLocalBinding = detachedLocalBinding;
    }

    public void setG11nOptions(G11nOptions g11nOptions) {
        this.g11nOptions = g11nOptions;
    }

    public void setInvocationTimeout(Integer invocationTimeout) {
        this.invocationTimeout = invocationTimeout;
    }

    public void setMessage(MyMessage messsage) {
        this.message = messsage;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceNameDefault() {
        setServiceName("test1");
    }

    public void setServiceURL(SimpleJettyServer jetty) throws MalformedURLException {
        setServiceURL(jetty.getSPFURI().toURL());
    }

    public void setServiceURL(URI uri) throws MalformedURLException {
        setServiceURL(uri.toURL());
    }

    public void setServiceURL(URL url) {
        this.serviceURL = url;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setSkipSerialization(boolean skipSerialization) {
        this.skipSerialization = skipSerialization;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public void setUrlPathInfo(String urlPathInfo) {
        this.urlPathInfo = urlPathInfo;
    }

    public void setUseDefaultBinding(boolean useDefaultBinding) {
        this.useDefaultBinding = useDefaultBinding;
    }

    public void setUseInParams(boolean useInParams) {
        this.useInParams = useInParams;
    }

    public void setUseLocalService() {
        this.serviceURL = null;
    }

    public void setUseRest(boolean useRest) {
        this.useRest = useRest;
    }

}
