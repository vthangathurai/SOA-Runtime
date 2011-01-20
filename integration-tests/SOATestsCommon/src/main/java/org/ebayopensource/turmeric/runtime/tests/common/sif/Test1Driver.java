/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import static org.hamcrest.Matchers.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP11Fault;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP12Fault;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.service.RequestContext;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.sif.error.MarkdownTestHelper;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Service;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.CustomErrorMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;

import org.junit.Assert;


/**
 * @author ichernyshev
 */
public class Test1Driver {

	public static final String TEST1_ADMIN_NAME = "test1";

	private static URL s_serviceURLOverriden = null;

	private static final String[] ALL_FORMATS = { BindingConstants.PAYLOAD_XML,
			BindingConstants.PAYLOAD_FAST_INFOSET,
			BindingConstants.PAYLOAD_JSON, BindingConstants.PAYLOAD_NV };

	public static final String OP_NAME_myTestOperation = "myTestOperation";

	public static final String OP_NAME_myNonArgOperation = "myNonArgOperation";

	public static final String OP_NAME_myVoidReturnOperation = "myVoidReturnOperation";

	public static final String OP_NAME_serviceChainingOperation = "serviceChainingOperation";

	public static final String OP_NAME_customError1 = "customError1";

	public static final String OP_NAME_customError2 = "customError2";

	private String m_configRoot;

	private URL m_serviceURL;

	private String[] m_reqDataFormats;

	private String[] m_resDataFormats;

	private long m_expectedError;

	private long m_expectedErrorAsync;

	private int m_repeatCount;

	private String m_protocolName;

	private Map<String, String> m_transportHeaders = new HashMap<String, String>();

	private Map<String, String> m_outTransportHeaders = new HashMap<String, String>();

	private Collection<ObjectNode> m_reqMessageHeaders = new ArrayList<ObjectNode>();

	private Collection<ObjectNode> m_sessionMessageHeaders = new ArrayList<ObjectNode>();

	private Class m_diiException;

	private Class m_proxyException;

	private String m_errorSubText;

	protected MyMessage m_message;

	private String m_operationName;

	private G11nOptions m_g11nOptions = new G11nOptions();

	private SuccessVerifier m_verifier;

	private String m_clientName;

	private String m_serviceName;

	private String m_serviceVersion;

	private boolean m_expectingSameMessage = true;

	private List<Cookie> m_cookies;

	private boolean m_isSkipSerialization;

	private boolean m_isNoPayloadData;

	private String m_transportName;

	private boolean m_useRest;

	private boolean m_useDefaultBinding;

	private boolean m_isExpectResponseMsgHeader;

	private boolean m_isDetachedLocalBinding;

	private Integer m_requestTimeoutMs;

	private String m_urlPathInfo = "";

	private boolean m_isUrlPathTest = false;

	public static enum TestMode {
		SYNC, ASYNC_SYNC, ASYNC_PULL, ASYNC_PUSH
	};

	private boolean m_skipAsyncTest = false;

	private boolean m_isLocalVoidReturnTest;

	public static void setServiceURL(URL serviceLocation) {
		s_serviceURLOverriden = serviceLocation;
	}

	public Test1Driver(String configRoot, URL serviceURL) {
		this(TEST1_ADMIN_NAME, null, configRoot, serviceURL, null, null);
	}

	public Test1Driver(String serviceName, String clientName,
			String configRoot, URL serviceURL) {
		this(serviceName, clientName, configRoot, serviceURL, null, null);
	}

	public Test1Driver(String serviceName, String clientName,
			String configRoot, URL serviceURL, String reqDataFormat,
			String resDataFormat) {
		this(serviceName, clientName, configRoot, serviceURL, reqDataFormat,
				resDataFormat, "myTestOperation");
	}

	public Test1Driver(String serviceName, String clientName,
			String configRoot, URL serviceURL, String reqDataFormat,
			String resDataFormat, String operationName) {
		this(
				serviceName,
				clientName,
				configRoot,
				serviceURL,
				(reqDataFormat != null ? new String[] { reqDataFormat } : null),
				(resDataFormat != null ? new String[] { resDataFormat } : null),
				operationName, TestUtils.createTestMessage());
	}

	public Test1Driver(String serviceName, String clientName,
			String configRoot, URL serviceURL, String[] reqDataFormats,
			String[] resDataFormats, String operationName, MyMessage msg) {
		Assert.assertThat("serviceName", serviceName, notNullValue());

		if (configRoot == null) {
			configRoot = "config";
		}

		m_clientName = clientName;
		m_serviceName = serviceName;
		m_configRoot = configRoot;
		m_serviceURL = serviceURL;
		m_repeatCount = 1;

		m_reqDataFormats = (reqDataFormats != null ? reqDataFormats
				: ALL_FORMATS);
		m_resDataFormats = (resDataFormats != null ? resDataFormats
				: ALL_FORMATS);
		if (null != msg)
			msg.setSubject("</div></div></div>");
		m_message = msg;
		m_operationName = operationName;

		// always add some custom header, just ot make sure it does not break
		// anything
		setTransportHeader("TEST_CALL_HEADER", "MyValue");
	}

	public void setExpectedError(long id, Class<?> exception, String subText) {
		setExpectedError(id, exception, exception, subText);
	}

	public void setExpectedError(long id, Class<?> diiException,
			Class<?> proxyException, String subText) {
		Assert.assertThat("id", id, notNullValue());
		Assert.assertThat("proxyException", proxyException, notNullValue());
		Assert.assertThat("subText", subText, notNullValue());

		m_expectedError = id;
		m_proxyException = proxyException;
		m_errorSubText = subText;

		if (proxyException != null && diiException == null) {
			m_diiException = ErrorMessage.class;
		} else {
			m_diiException = diiException;
		}
	}

	public void setUseSoap11(boolean value) {
		if (value) {
			m_protocolName = SOAConstants.MSG_PROTOCOL_SOAP_11;
		} else {
			m_protocolName = null;
		}
	}

	public void setUseSoap12(boolean value) {
		if (value) {
			m_protocolName = SOAConstants.MSG_PROTOCOL_SOAP_12;
		} else {
			m_protocolName = null;
		}
	}

	public void setRepeatCount(int value) {
		if (value > 0) {
			m_repeatCount = value;
		} else {
			m_repeatCount = 1;
		}
	}

	public void setG11nOptions(G11nOptions options) {
		m_g11nOptions = options;
	}

	public void setUseRest(boolean value) {
		m_useRest = value;
	}

	public void setUseDefaultBinding(boolean value) {
		m_useDefaultBinding = value;
	}

	public void setExpectingSameMessage(boolean value) {
		m_expectingSameMessage = value;
	}

	public void setExpectResponseMsgHeader(boolean value) {
		m_isExpectResponseMsgHeader = value;
	}

	public void setTransportHeader(String name, String value) {
		m_transportHeaders.put(name, value);
	}

	public void removeTransportHeader(String name) {
		m_transportHeaders.remove(name);
	}

	public void setTransportHeader(String name, boolean value) {
		if (value) {
			setTransportHeader(name, "true");
		} else {
			removeTransportHeader(name);
		}
	}

	public void addMessageHeaderAsJavaObject(Object headerJavaObject) {
		setExpectResponseMsgHeader(true);
		m_reqMessageHeaders.add(new JavaObjectNodeImpl(null, headerJavaObject));
	}

	public void addSessionMessageHeaderAsJavaObject(Object headerJavaObject) {
		setExpectResponseMsgHeader(true);
		m_sessionMessageHeaders.add(new JavaObjectNodeImpl(null,
				headerJavaObject));
	}

	public void addMessageHeader(ObjectNode header) {
		m_reqMessageHeaders.add(header);
	}

	public void addSessionMessageHeader(ObjectNode header) {
		m_sessionMessageHeaders.add(header);
	}

	public void removeMessageHeader(ObjectNode header) {
		m_reqMessageHeaders.remove(header);
	}

	public void removeSessionMessageHeader(ObjectNode header) {
		m_sessionMessageHeaders.remove(header);
	}

	public void setOutboundTransportHeader(String name, String value) {
		m_outTransportHeaders.put(name, value);
	}

	public void setHeader_Test1Cookie(String cookieString) {
		setTransportHeader(Test1Constants.TR_HDR_TEST1_COOKIE, cookieString);
	}

	public void setHeader_Test1Exception(boolean enable) {
		setTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION, enable);
	}

	public void setHeader_Test1ServiceException(boolean enable) {
		setTransportHeader(Test1Constants.TR_HDR_TEST1_SERVICE_EXCEPTION,
				enable);
	}

	public void setCookies(Cookie[] cookies) {
		for (int i = 0; i < cookies.length; i++) {
			addCookie(cookies[i]);
		}
	}

	public void addCookie(String name, String value) {
		addCookie(new Cookie(name, value));
	}

	public void addCookie(Cookie cookie) {
		if (m_cookies == null) {
			m_cookies = new ArrayList<Cookie>();
		}
		m_cookies.add(cookie);
	}

	/**
	 * @param m_verifier
	 *            the m_verifier to set
	 */
	public void setVerifier(SuccessVerifier m_verifier) {
		this.m_verifier = m_verifier;
	}

	public void setServiceVersion(String version) {
		m_serviceVersion = version;
	}

	public void setSkipSerialization(boolean skipSerialization) {
		m_isSkipSerialization = skipSerialization;
	}

	public void setDetachedLocalBinding(boolean useDetachedLocalBinding) {
		m_isDetachedLocalBinding = useDetachedLocalBinding;
	}

	public void setNoPayloadData(boolean value) {
		m_isNoPayloadData = value;
	}

	public void setTransportName(String name) {
		m_transportName = name;
	}

	public void setRequestTimeout(Integer timeout) {
		m_requestTimeoutMs = timeout;
	}

	private void checkSuccess(Service service, MyMessage msg,
			MyMessage outParam, Response response, TestMode mode)
			throws Exception {
		if (getExceptedError(mode) != 0) {
			Assert.fail("Expected error " + getExceptedError(mode)
					+ ", but execution was successful");
		}

		if (m_expectingSameMessage) {
			Assert.assertEquals(msg, outParam);
		}

		byte[] payloadData = verifyPayload(service);

		if (m_verifier != null) {
			m_verifier.checkSuccess(service, m_operationName, msg, outParam,
					payloadData);
		}

		// check msg header
		Collection c = service.getResponseContext().getMessageHeaders();
		if (m_isExpectResponseMsgHeader) {
			// assert that response msg header presents!
			Assert.assertTrue(c != null);
			Assert.assertTrue(c.size() > 0);
		} else {
			// assert that response msg header is not there
			Assert.assertTrue(c != null);
			Assert.assertTrue(c.size() == 0);
		}
	}

	/*
	 * private void checkSuccess(Service service, MyMessage msg, List<Object>
	 * outParams) throws Exception { // todo: make sure to reuse the
	 * checkSuccess method from interface and // here.
	 * checkSuccessInternal(service, null, null, msg, outParams, TestMode.SYNC); }
	 */
	@SuppressWarnings("rawtypes")
	private void checkSuccess(Service service, Dispatch dispatch,
			Response response, MyMessage msg, List<Object> outParams,
			TestMode mode) throws Exception {
		checkSuccessInternal(service, dispatch, response, msg, outParams, mode);
	}

	/**
	 * this method is supposed to return error code specific to the invocation
	 * mode. For eg: Async methods return ExecutionExcetion which wraps
	 * ServiceException Sync methods of Async functionality returns
	 * WebServiceException which wraps ServiceException Old Sync methods return
	 * ServiceInvocaitonException.
	 * 
	 * @param mode
	 * @return
	 */
	/*
	 * private long getExceptedError(TestMode mode) { if
	 * (mode.equals(TestMode.ASYNC_SYNC)) return m_expectedErrorAsync; return
	 * m_expectedError; }
	 */
	
	@SuppressWarnings("rawtypes")
	private void checkSuccessInternal(Service service, Dispatch dispatch,
			Response response, MyMessage msg, List<Object> outParams,
			TestMode mode) throws Exception {
		if (getExceptedError(mode)!= 0) {
			Assert.fail("Expected error " + getExceptedError(mode)
					+ ", but execution was successful");
		}

		MyMessage resultMsg;
		if (OP_NAME_myVoidReturnOperation.equals(m_operationName)) {
			Assert.assertThat(OP_NAME_myVoidReturnOperation
					+ " should have no outParams", outParams.size(), is(0));
			resultMsg = null;
		} else {
			Assert.assertThat(m_operationName + " should have 1 outParam",
					outParams.size(), is(1));
			resultMsg = (MyMessage) outParams.get(0);

			if (m_expectingSameMessage) {
			    Assert.assertNotNull(m_operationName + " should have resultMsg", resultMsg);
                Assert.assertNotNull(m_operationName + " should have msg", msg);
				Assert.assertThat(m_operationName + " should have same message",
						resultMsg.getBody(), is(msg.getBody()));
			}
		}

		byte[] payloadData = verifyPayload(service, dispatch, response, mode);

		if (m_verifier != null) {
			if (mode.equals(TestMode.SYNC)) {
				m_verifier.checkSuccess(service, m_operationName, msg,
						resultMsg, payloadData);
			} else {
				m_verifier.checkSuccess(service, dispatch, response, msg,
						resultMsg, payloadData, mode);
			}
		}
	}

	protected byte[] verifyPayload(Service service) throws Exception {
		byte[] payloadData = service.getResponseContext().getPayloadData();
		if (m_isSkipSerialization || m_isNoPayloadData) {
			Assert.assertNull("Expecting no payload data", payloadData);
		} else {
			Assert.assertNotNull("Expecting some payload data", payloadData);
		}
		return payloadData;
	}

	protected byte[] verifyPayload(Service service, Dispatch dispatch,
			Response response, TestMode mode) throws Exception {

		byte[] payloadData = null;

		if (mode.equals(TestMode.SYNC))
			payloadData = (byte[]) service.getResponseContext()
					.getPayloadData();
		else if (mode.equals(TestMode.ASYNC_SYNC))
			payloadData = (byte[]) dispatch.getResponseContext().get("PAYLOAD");
		else
			payloadData = (byte[]) response.getContext().get("PAYLOAD");

		if (m_isSkipSerialization || m_isNoPayloadData) {
			Assert.assertNull("Expecting no payload data", payloadData);
		} else {
			Assert.assertNotNull("Expecting some payload data", payloadData);
		}
		return payloadData;
	}

	protected void checkError(Service service, Object e, boolean isProxy)
			throws Exception {
		checkError(service, e, isProxy, TestMode.SYNC, false);
	}

	protected void checkError(Service service, Object e, boolean isProxy,
			TestMode mode, boolean isResponseGet) throws Exception {

		 long local_expected_error;
		 local_expected_error = getExceptedError(mode);

		if (local_expected_error == 0) {
			String cause = printErrorInfo(e);
			Assert.fail("Expected successful execution, but got error "
					+ cause);
		}

		ErrorData errorData;

		if (e instanceof WebServiceException) {
			Object response = null;
			if (mode.equals(TestMode.ASYNC_SYNC)) {
				ServiceInvocationException sie = (ServiceInvocationException) ((Throwable) e)
						.getCause();
				response = sie.getErrorResponse();
				if (response != null && sie.isAppOnlyException()) {
					e = response;
				}
			} else {
				ServiceException sie = (ServiceException) ((Throwable) e)
						.getCause();
				response = sie.getMessage();
			}
		}

		if (e instanceof ServiceInvocationException) {
			ServiceInvocationException sie = (ServiceInvocationException) e;
			Object response = sie.getErrorResponse();
			if (response != null && sie.isAppOnlyException()) {
				e = response;
			}
		}

		if (e instanceof Exception) {
			if (e instanceof ServiceExceptionInterface) {
				ServiceExceptionInterface e2 = (ServiceExceptionInterface) e;
				ErrorMessage errorMessage = e2.getErrorMessage();
				if (errorMessage != null) {
					errorData = e2.getErrorMessage().getError().get(0);
				} else {
					errorData = null;
				}
			} else if (e instanceof ExecutionException) {
				ServiceException se = (ServiceException) ((Throwable) e)
						.getCause();
				ErrorMessage errorMessage = se.getErrorMessage();
				if (errorMessage != null) {
					errorData = errorMessage.getError().get(0);
				} else {
					errorData = null;
				}
			} else {
				// Exception e.g. Test1Exception was able to be instantiated by
				// the proxy, but
				// does not implement ServiceExceptionInterface.
				errorData = null;
			}
		} else if (e instanceof CustomErrorMessage) {
			CustomErrorMessage e2 = (CustomErrorMessage) e;
			ErrorType errorType = e2.getError().get(0);
			errorData = TestUtils.errorTypeToErrorData(errorType);
		} else if (e instanceof MyMessage) {
			// customError2 operation - use the result to include the error
			MyMessage e2 = (MyMessage) e;
			ErrorType errorType = e2.getError().get(0);
			errorData = TestUtils.errorTypeToErrorData(errorType);
		} else if (e instanceof ErrorMessage) {
			ErrorMessage e2 = (ErrorMessage) e;
			errorData = e2.getError().get(0);
		} else if (e instanceof SOAP11Fault) {
			SOAP11Fault e2 = (SOAP11Fault) e;
			errorData = ((ErrorMessage) e2.getDetail()).getError().get(0);
			e = (ErrorMessage) e2.getDetail();
		} else if (e instanceof SOAP12Fault) {
			SOAP12Fault e2 = (SOAP12Fault) e;
			errorData = ((ErrorMessage) e2.getDetail()).getError().get(0);
			e = (ErrorMessage) e2.getDetail();
		} else if (e == null) {
			Assert.fail("Unexpected NULL error message");
			return;
		} else {
			Assert.fail("Unknown error message " + e.getClass().getName()
					+ ": " + e.toString());
			return;
		}

		if (errorData != null) {
			if (unexpectedError(errorData.getErrorId(), local_expected_error)) {
				String cause = printErrorInfo(e);
				Assert.fail("Unexpected error id " + errorData.getErrorId()
						+ ", expected " + local_expected_error + ". Error "
						+ e.getClass().getName() + ": " + cause);
			}
		}

		if (isProxy) {
			checkException(e, getProxyException(mode), mode);
		} else {
			checkException(e, getDiiException(mode,isResponseGet), mode);
		}

		// verifyPayload(service);

		if (m_repeatCount == 1) {
			System.out.println("Received expected error: " + e.toString());
		}
	}

	private Class getProxyException(TestMode mode) {
		if (m_isLocalVoidReturnTest
				&& (mode.equals(TestMode.ASYNC_PULL) || mode
						.equals(TestMode.ASYNC_PUSH))) {
			return ExecutionException.class;
		} else {
			if (mode.equals(TestMode.SYNC))
				return m_proxyException;
			else if (mode.equals(TestMode.ASYNC_SYNC))
				return WebServiceException.class;
			else
				return ExecutionException.class;
		}
	}

	private Class getDiiException(TestMode mode, boolean isResponseGet) {
		if(isResponseGet)
		{
			return ExecutionException.class;
		}
		else if (mode.equals(TestMode.ASYNC_PULL) || mode.equals(TestMode.ASYNC_PUSH)) {
			return WebServiceException.class;
		} else {
				return m_diiException;
		}
	}

	public boolean unexpectedError(long errorId, long expectedErrorId) {
		// Exists to allow LocalCallFallbackOperationTest to override.
		return (errorId != expectedErrorId);
	}

	private void checkException(Object e, Class<?> clazz, TestMode mode)
			throws Exception {
		String local_error_subtext = getErrorSubText(mode);
		
		if (!(e instanceof Throwable)) {
			if (clazz != null && clazz != e.getClass()) {
				String cause = printErrorInfo(e);
				Assert.fail("Expected " + clazz.getName()
						+ ", but got another error response: " + cause);
			}

			ErrorMessage msg = null;
			if (e instanceof CustomErrorMessage) {
				CustomErrorMessage e2 = (CustomErrorMessage) e;
				msg = TestUtils.errorTypeListToErrorMessage(e2.getError());
			} else if (e instanceof MyMessage) {
				MyMessage e2 = (MyMessage) e;
				msg = TestUtils.errorTypeListToErrorMessage(e2.getError());
			} else {
				msg = (ErrorMessage) e;
			}
			String text = msg.getError().get(0).getMessage();

			if (text.indexOf(local_error_subtext) == -1) {
				String cause = printErrorInfo(e);
				Assert.fail("ErrorMessage does not contain expected subtext '"
								+ local_error_subtext + "' : " + cause);
			}

			return;
		}

		if (clazz == null) {
			String cause = printErrorInfo(e);
			Assert.fail("Expected no exception, but got exception: " + cause);
		}

		Throwable e2 = (Throwable) e;
		if (e2.getClass() != clazz) {
			String cause = printErrorInfo(e);
			Assert.fail("Unexpected error class " + e2.getClass().getName()
					+ ", expected " + clazz.getName() + ": " + cause);
		}

		String text = e2.toString();
		if (text.indexOf(local_error_subtext) == -1) {
			String cause = printErrorInfo(e);
			Assert.fail("Exception does not contain expected subtext '"                                                                                                          
					+ local_error_subtext + "' : " + cause);
		}
	}

	private String printErrorInfo(Object e) {
		// TODO: print more ErrorDataList info here

		String causeText;
		if (e instanceof Throwable) {
			((Throwable) e).printStackTrace();
			causeText = e.toString();
		} else if (e instanceof ErrorMessage) {
			ErrorMessage msg = (ErrorMessage) e;
			causeText = getCauseText(msg);
		} else if (e instanceof CustomErrorMessage) {
			CustomErrorMessage e2 = (CustomErrorMessage) e;
			ErrorMessage msg = TestUtils.errorTypeListToErrorMessage(e2
					.getError());
			causeText = getCauseText(msg);
		} else {
			causeText = e.toString();
		}

		System.err.println("ErrorInfo: " + causeText);

		return causeText;
	}

	private String getCauseText(ErrorMessage msg) {
		List<CommonErrorData> errorList = msg.getError();
		if (errorList != null && !errorList.isEmpty()) {
			ErrorData err = errorList.get(0);
			return err.getErrorId() + " : " + err.getMessage();
		} else {
			return msg.toString();
		}
	}

	public void doCall() throws Exception {
		ClientConfigManager.getInstance().setConfigTestCase(m_configRoot);
		ServiceConfigManager.getInstance().setConfigTestCase(m_configRoot);

		for (int i = 0; i < m_reqDataFormats.length; i++) {
			String reqDataFormat = m_reqDataFormats[i];
			for (int j = 0; j < m_resDataFormats.length; j++) {
				String resDataFormat = m_resDataFormats[j];
				doCall(reqDataFormat, resDataFormat, TestMode.SYNC);
				if (!m_skipAsyncTest)
					doCall(reqDataFormat, resDataFormat, TestMode.ASYNC_PULL);
				if (!m_skipAsyncTest)
					doCall(reqDataFormat, resDataFormat, TestMode.ASYNC_PUSH);
				 //break;
			}
			// break;
		}

		// doCall(SOAConstants.PAYLOAD_XML, SOAConstants.PAYLOAD_FAST_INFOSET);

	}

	private Service createServiceInstance(String reqDataFormat,
			String resDataFormat) throws Exception {
		URL serviceURL = getServiceURL();
		Service test1 = ServiceFactory.create(m_serviceName, m_clientName,
				serviceURL, m_serviceVersion);
		ServiceInvokerOptions options = test1.getInvokerOptions();
		if (m_g11nOptions != null) {
			test1.setG11nOptions(m_g11nOptions);
		}

		if (serviceURL == null) {
			options.setTransportName(SOAConstants.TRANSPORT_LOCAL);
		} else {
			if (m_transportName != null) {
				options.setTransportName(m_transportName);
			}
		}

		if (m_protocolName != null) {
			options.setMessageProtocolName(m_protocolName);
		}

		if (m_cookies != null) {
			for (int i = 0; i < m_cookies.size(); i++) {
				test1.setCookie(m_cookies.get(i));
			}
		}

		if (m_outTransportHeaders != null) {
			for (Map.Entry<String, String> e : m_outTransportHeaders.entrySet()) {
				test1.setSessionTransportHeader(e.getKey(), e.getValue());
			}
		}

		if (m_sessionMessageHeaders != null) {
			for (ObjectNode node : m_sessionMessageHeaders) {
				test1.addSessionMessageHeader(node);
			}
		}

		if (!m_useDefaultBinding) {
			options.setRequestBinding(reqDataFormat);
			options.setResponseBinding(resDataFormat);
		}

		if (m_isSkipSerialization) {
			options.getTransportOptions().setSkipSerialization(Boolean.TRUE);
		}

		if (m_isDetachedLocalBinding) {
			options.getTransportOptions().setUseDetachedLocalBinding(
					Boolean.TRUE);
		}

		if (m_requestTimeoutMs != null) {
			options.getTransportOptions().setInvocationTimeout(
					m_requestTimeoutMs);
		}

		options.setRecordResponsePayload(true);

		RequestContext reqCtx = test1.getRequestContext();
		for (Map.Entry<String, String> e : m_transportHeaders.entrySet()) {
			reqCtx.setTransportHeader(e.getKey(), e.getValue());
		}
		for (ObjectNode headerObj : m_reqMessageHeaders) {
			reqCtx.addMessageHeader(headerObj);
		}

		if (m_useRest) {
			options.setREST(Boolean.TRUE);
		}

		return test1;
	}

	private URL getServiceURL() {
		if (s_serviceURLOverriden != null) {
			return s_serviceURLOverriden;
		}
		return m_serviceURL;
	}

	private int getRepeatCount(TestMode mode) {
		if (mode.equals(TestMode.SYNC))
			return m_repeatCount;
		else if (mode.equals(TestMode.ASYNC_SYNC))
			return m_repeatCount;
		else
			return 1;
	}

	@SuppressWarnings("unchecked")
	private void doCall(String reqDataFormat, String resDataFormat,
			TestMode mode) throws Exception {
		System.out.println("Start " + getClass().getSimpleName() + " with "
				+ reqDataFormat + " request and " + resDataFormat
				+ " response :: " + mode);

		long diiResponseTime = 0;
		long proxyResponseTime = 0;
		boolean isResponseGet = false;

		Charset charset = m_g11nOptions.getCharset();
		MyMessage encodedMsg = reqDataFormat
				.equals(BindingConstants.PAYLOAD_FAST_INFOSET)
				&& resDataFormat.equals(BindingConstants.PAYLOAD_FAST_INFOSET) ? m_message
				: TestUtils.encodeMessage(m_message, charset);

		int local_repeatCount = getRepeatCount(mode);

		for (int i = 0; i < local_repeatCount; i++) {
			Object[] inParams;
			if (m_operationName.equals(OP_NAME_myNonArgOperation)
					|| m_operationName.equals(OP_NAME_customError1)) {
				inParams = null;
			} else {
				inParams = new Object[] { m_message };
			}
			List<Object> outParams = new ArrayList<Object>();

			Service test1 = createServiceInstance(reqDataFormat, resDataFormat);
			if (m_isUrlPathTest)
				test1.getInvokerOptions().setUrlPathInfo(m_urlPathInfo);
			Dispatch dispatch = null;
			Response response = null;
			Future future = null;
			Object obj = null;
			try {
				long startTime = System.nanoTime();
				try {
					if (mode.equals(TestMode.SYNC))
						test1.invoke(m_operationName, inParams, outParams);
					else if (mode.equals(TestMode.ASYNC_SYNC)) {
						dispatch = test1.createDispatch(m_operationName);
						obj = dispatch.invoke(inParams);
						outParams.add(obj);
					} else if (mode.equals(TestMode.ASYNC_PULL)) {
						dispatch = test1.createDispatch(m_operationName);
						/*if (m_isLocalVoidReturnTest)
							setExpectedError(2013, ExecutionException.class,
									"Unable to access parameter values on void message");*/
						if (m_operationName.equals(OP_NAME_myNonArgOperation)
								|| m_operationName.equals(OP_NAME_customError1)) {
							response = dispatch.invokeAsync(null);
						} else {
							response = dispatch.invokeAsync(m_message);
						}
						isResponseGet = true;
						while (!response.isDone()) {
							Thread.sleep(100);
						}
						obj = response.get();
						outParams.add(obj);
					} else if (mode.equals(TestMode.ASYNC_PUSH)) {
						dispatch = test1.createDispatch(m_operationName);
						TestHander handler = new TestHander<MyMessage>();
						/*if (m_isLocalVoidReturnTest)
							setExpectedError(2013, ExecutionException.class,
									"Unable to access parameter values on void message");*/

						if (m_operationName.equals(OP_NAME_myNonArgOperation)
								|| m_operationName.equals(OP_NAME_customError1)) {
							future = dispatch.invokeAsync(null, handler);
						} else {
							future = dispatch.invokeAsync(m_message, handler);
						}
						
						while (!handler.isDone()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
								break;
							}
						}
						isResponseGet = true;
						if (handler.hasError())
							throw (ExecutionException) handler.getError();
						else {
							outParams.add(handler.get());
							response = handler.getResponse();
						}
					}
				} finally {
					diiResponseTime += System.nanoTime() - startTime;
				}
				checkSuccess(test1, dispatch, response, encodedMsg, outParams,
						mode);
			} catch (Exception e) {
				checkError(test1, e, false, mode,isResponseGet);
			}
		}

		if (mode.equals(TestMode.SYNC)) {
			for (int i = 0; i < local_repeatCount; i++) {
				MyMessage msg = m_message;

				Service test1 = createServiceInstance(reqDataFormat,
						resDataFormat);

				try {
					Test1Service proxy = test1.getProxy();
					MyMessage msgResult = null;
					if (m_operationName.equals(OP_NAME_myNonArgOperation)) {
						long startTime = System.nanoTime();
						try {
							msgResult = proxy.myNonArgOperation();
						} finally {
							proxyResponseTime += System.nanoTime() - startTime;
						}
						checkSuccess(test1, null, msgResult, null, null);
					} else if (m_operationName
							.equals(OP_NAME_myVoidReturnOperation)) {
						long startTime = System.nanoTime();
						try {
							proxy.myVoidReturnOperation(msg);
						} finally {
							proxyResponseTime += System.nanoTime() - startTime;
						}
					} else if (m_operationName.equals(OP_NAME_customError1)) {
						long startTime = System.nanoTime();
						try {
							proxy.customError1();
						} finally {
							proxyResponseTime += System.nanoTime() - startTime;
						}
					} else if (m_operationName.equals(OP_NAME_customError2)) {
						long startTime = System.nanoTime();
						try {
							msgResult = proxy.customError2(msg);
						} finally {
							proxyResponseTime += System.nanoTime() - startTime;
						}
					} else {
						Response response = null;
						long startTime = System.nanoTime();
						try {
							if (mode.equals(TestMode.SYNC))
								msgResult = proxy.myTestOperation(msg);
							/*
							 * else if (mode.equals(TestMode.ASYNC_PULL)) {
							 * response = proxy.myTestOperationAsync(msg); while
							 * (!response.isDone()) { try { Thread.sleep(100); }
							 * catch (InterruptedException e) {
							 * e.printStackTrace(); } } msgResult = (MyMessage)
							 * response.get(); } else
							 * if(mode.equals(TestMode.ASYNC_PUSH)) { TestHander
							 * handler = new TestHander<MyMessage>(); Future<?>
							 * future = proxy.myTestOperationAsync(msg,
							 * handler); while (!handler.isDone()) { try {
							 * Thread.sleep(100); } catch (InterruptedException
							 * e) { e.printStackTrace(); } } if
							 * (handler.hasError()) throw (ExecutionException)
							 * handler.getError(); else { msgResult =
							 * (MyMessage) handler.get(); response =
							 * handler.getResponse(); } }
							 */
						} finally {
							proxyResponseTime += System.nanoTime() - startTime;
						}
						checkSuccess(test1, encodedMsg, msgResult, response,
								mode);
					}
				} catch (Exception e) {
					checkError(test1, e, true, mode, isResponseGet);
				}
			}
		}

		MarkdownTestHelper.markupClientManually(m_serviceName, null, null);

		if (getExceptedError(mode) == 0) {
			System.out.println("End " + getClass().getSimpleName()
					+ " using data format " + reqDataFormat + " - "
					+ resDataFormat + " takes: \n" + proxyResponseTime
					/ m_repeatCount / 2000000.0 + " for proxy, and\n"
					+ diiResponseTime / m_repeatCount / 2000000.0
					+ " for DII :: " + mode + ", and\n");
		} else {
			System.out.println("End " + getClass().getSimpleName()
					+ " using data format " + reqDataFormat + " - "
					+ resDataFormat + " :: " + mode);
		}
	}

	public void skipAyncTest(boolean skipAsyncTest) {
		m_skipAsyncTest = skipAsyncTest;
		;
	}
	
	private long getExceptedError(TestMode mode)
	{
		if (m_isLocalVoidReturnTest
				&& (mode.equals(TestMode.ASYNC_PULL) || mode
						.equals(TestMode.ASYNC_PUSH))) {
			return 2013;
		}
		else
			return m_expectedError;
	}
	
	private String getErrorSubText(TestMode mode)
	{
		if (m_isLocalVoidReturnTest
				&& (mode.equals(TestMode.ASYNC_PULL) || mode
						.equals(TestMode.ASYNC_PUSH))) {
			return "Unable to access parameter values on void message";
		}
		else
		{
			return m_errorSubText;
		}
	}

	public interface SuccessVerifier {
		public void checkSuccess(Service service, String opName,
				MyMessage request, MyMessage response, byte[] payloadData)
				throws Exception;

		public void checkSuccess(Service service, Dispatch dispatch,
				Response futureResp, MyMessage request, MyMessage response,
				byte[] payloadData, TestMode mode) throws Exception;
	}

	public interface ErrorVerifier {
		public void checkError(Service service, Object e, boolean isProxy)
				throws Exception;
	}

	private static class TestHander<T> implements AsyncHandler<T> {
		private volatile Response<T> m_resp;

		private volatile Throwable m_error;

		private volatile T m_message;

		private volatile boolean m_done = false;

		public void handleResponse(Response<T> resp) {
			try {
				m_resp = resp;
				m_message = m_resp.get();
			} catch (Throwable e) {
				m_error = e;
			} finally {
				m_done = true;
			}
		}

		public T get() {
			return m_message;
		}

		public Throwable getError() {
			return m_error;
		}

		public boolean isDone() {
			return m_done;
		}

		public boolean hasError() {
			return m_error != null;
		}

		public Response<T> getResponse() {
			return m_resp;
		}
	}

	public void setUrlPathInfo(String urlPath) {
		m_urlPathInfo = urlPath;
	}

	public void isUrlPathTest(boolean b) {
		m_isUrlPathTest = b;
	}

	public void setLocalVoidReturnTest(boolean b) {
		m_isLocalVoidReturnTest = b;
	}
}
