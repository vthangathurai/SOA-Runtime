/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.OutboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.OutboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigHolder;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateId;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.AsyncResponse;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.sif.service.RequestContext;
import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceDispatch;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;

import com.ebay.kernel.markdown.MarkdownStateSnapshot;

public abstract class BaseServiceDispatchImpl<T> extends ServiceDispatch<T> {
	static final int MAX_URL_LENGTH_FOR_REST = 2048;

	static final G11nOptions s_emptyG11nOptions = new G11nOptions();

	private final ClientMessageProcessor m_messageProcessor;

	private final URL m_serviceLocation;

	private final ClientServiceDesc m_serviceDesc;

	private final ServiceInvokerOptions m_invokerOptions;

	private final String m_serviceVersion;

	private final Map<String, String> m_sessionTransportHeaders;

	private final Collection<ObjectNode> m_sessionMessageHeaders;

	private Map<String, Cookie> m_dispatchCookies;

	private RequestContext m_requestContext;

	private ResponseContext m_responseContext = null;

	private ClientMessageContextImpl m_currentContext = null;

	private String m_urlPathInfo;

	private G11nOptions m_g11nOptions;

	private int m_numTries = 0;

	private IAsyncResponsePoller m_servicePoller;

	private final Executor m_executor;

	BaseServiceDispatchImpl(String opName, URL serviceLocation,
			ClientServiceDesc serviceDesc, URL wsdlLocation,
			ServiceInvokerOptions invokerOptions, String serviceVersion,
			Map<String, Cookie> cookies, Map<String, String> transportHeaders,
			Collection<ObjectNode> messageHeaders, G11nOptions g11nOptions,
			RequestContext requestContext, Executor executor,
			IAsyncResponsePoller servicePoller) throws ServiceException {

		super(opName);

		m_messageProcessor = ClientMessageProcessor.getInstance();

		m_serviceLocation = serviceLocation;
		m_serviceDesc = serviceDesc;
		m_invokerOptions = invokerOptions;
		m_serviceVersion = serviceVersion;
		m_dispatchCookies = cookies;
		m_sessionTransportHeaders = transportHeaders;
		m_sessionMessageHeaders = messageHeaders;

		m_g11nOptions = g11nOptions;

		m_requestContext = requestContext;
		m_servicePoller = servicePoller;

		m_executor = executor;
	}

	@Override
	public Map<String, Object> getRequestContext() {
		return getRequestContextInternal(m_requestContext);
	}

	@Override
	public Map<String, Object> getResponseContext() {
		return getResponseContextInternal(m_responseContext);
	}

	public String getUrlPathInfo() {
		return m_urlPathInfo;
	}

	public RequestContext getDispatchRequestContext() {
		return m_requestContext;
	}

	public ResponseContext getDispatchResponseContext() {
		if (m_responseContext == null) {
			m_responseContext = createResponseContext();
		}
		return m_responseContext;
	}

	/**
	 * Returns the total number of attempted service invocation tries during the
	 * most recent call to invoke(). The total number of attempted invocations
	 * is one plus the number of retries attempted.
	 *
	 * @return the number of attempted invocations, or zero if invoke() has
	 *         never been called for this Service object.
	 */
	public int getLastTryCount() {
		return m_numTries;
	}

	private boolean isRetryable(Throwable exception) {
		if (getCurrentContext() == null) {
			// we could not even create the context, there is no point in
			// retries
			return false;
		}

		return m_serviceDesc.getRetryHandler().isRetryable(getCurrentContext(),
				exception);
	}

	private int getApplicationTryCount() {
		Integer appLevelNumRetries = m_invokerOptions.getAppLevelNumRetries();
		if (appLevelNumRetries == null) {
			appLevelNumRetries = m_serviceDesc.getAppLevelNumRetries();
		}

		if (appLevelNumRetries == null) {
			return 1;
		}

		return appLevelNumRetries.intValue() + 1;
	}

	protected void invokeWithRetry(String opName, boolean inboundRawMode,
			boolean outboundRawMode, Object[] inParams, List<Object> outParams,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper)
			throws ServiceInvocationException {

		m_numTries = 1;

		while (true) {
			try {
				invokeInternal(opName, inboundRawMode, outboundRawMode,
						inParams, outParams, inWrapper, outWrapper);
				postInvokeSuccess();
				return;
			} catch (RuntimeException e) {
				handleRetryForRuntimeException(e);
			} catch (Error e) {
				handleRetryForError(e);
			} catch (ServiceInvocationException e) {
				handleRetryServiceInvocationException(e);
			} catch (ServiceException e) {
				handleRetryUnexpectedException(opName, e);
			}
		}
	}

	private void handleRetryForRuntimeException(RuntimeException e) {
		// Unexpected exception - check if it's on the retry list,
		// otherwise re-throw it.
		postInvokeErrorAndMarkDown(e);
		if (!isRetryable(e) || m_numTries >= getApplicationTryCount()) {
			throw e;
		}
		m_numTries++;
	}

	private void handleRetryForError(Error e) throws Error {
		// Unexpected exception - re-throw it. Not subject to retry
		// logic.
		postInvokeErrorAndMarkDown(e);
		ServiceCallHelper.checkMarkdownError(getCurrentContext(), e);
		throw e;
	}

	private void handleRetryServiceInvocationException(
			ServiceInvocationException e) throws ServiceInvocationException {

		// invokeInternal called checkForErrors, which threw this to
		// indicate a system error from either client
		// or server side. Check if it's on the retry list, otherwise
		// re-throw it.
		postInvokeErrorAndMarkDown(e);
		if (!isRetryable(e) || m_numTries >= getApplicationTryCount()) {
			throw e;
		}

		// TODO: log the decisions being made?
		m_numTries++;
	}

	protected void handleRetryUnexpectedException(String opName, Throwable e)
			throws ServiceInvocationException {
		// Since ClientMessageProcessor.processMessage() controls most
		// exceptions it sees, an exception here would mean we did
		// not get as far as that call. Since the last thing done
		// before that is creating the message context, we can't
		// assume we have even a valid message context, so there's no
		// point in trying to accumulate all errors per the checkForErrors()
		// call. Most likely there is also no response - responseMsg is null.
		// We just pass through retry, and then throw the single
		// exception received.

		ServiceInvocationException e2 = getServiceInvocationExceptionForServiceException(
				opName, e);

		handleRetryServiceInvocationException(e2);
	}

	protected void invokeInternal(String opName, boolean inboundRawMode,
			boolean outboundRawMode, Object[] inParams, List<Object> outParams,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper)
			throws ServiceException, ServiceInvocationException {
		// Pre-ProcessMessage
		InboundMessageImpl responseMsg = preProcessMessage(opName,
				inboundRawMode, outboundRawMode, inParams, outParams,
				inWrapper, outWrapper);

		// Message now to be processed by the CMP
		getMessageProcessor().processMessage(getCurrentContext(), false);

		// Process Response
		processResponse(outboundRawMode, outParams, outWrapper, responseMsg);
	}

	protected void postInvokeError() {
		m_requestContext = null;
		m_responseContext = null;
		//m_urlPathInfo = null;

		if (getCurrentContext() != null) {
			// transfer headers ...
			try {
				m_responseContext = createResponseContext(getCurrentContext(),
						m_invokerOptions.shouldRecordResponsePayload());
			} catch (ServiceException e) {
				// should never happen
			}

			// but no cookies
		}
	}

	void setCookie(Cookie cookie) {
		if (m_dispatchCookies == null) {
			m_dispatchCookies = new HashMap<String, Cookie>();
		}

		m_dispatchCookies.put(cookie.getName(), cookie);
	}

	Map<String, Cookie> getCookiesMap() {
		if (m_dispatchCookies == null) {
			return Collections.unmodifiableMap(new HashMap<String, Cookie>());
		}

		return Collections.unmodifiableMap(new HashMap<String, Cookie>(
				m_dispatchCookies));
	}

	static Logger getLogger() {
		return LogManager.getInstance(BaseServiceDispatchImpl.class);
	}

	private InboundMessageImpl preProcessMessage(String opName,
			boolean inboundRawMode, boolean outboundRawMode, Object[] inParams,
			List<Object> outParams, ByteBufferWrapper inWrapper,
			ByteBufferWrapper outWrapper) throws ServiceException {
		return preProcessMessage(opName, inboundRawMode, outboundRawMode,
				inParams, outParams, inWrapper, outWrapper, false);
	}

	protected InboundMessageImpl preProcessMessage(String opName,
			boolean inboundRawMode, boolean outboundRawMode, Object[] inParams,
			List<Object> outParams, ByteBufferWrapper inWrapper,
			ByteBufferWrapper outWrapper, boolean useAsync)
			throws ServiceException {

		// Reset response context
		m_responseContext = null;

		// Get the ByteBuffer to receive response
		ByteBuffer inBuffer = prepareInBufferForRawMode(inboundRawMode,
				outboundRawMode, outParams, inWrapper, outWrapper);

		// Get the operation description
		ServiceOperationDesc operation = getOperationDescription(opName,
				inboundRawMode);

		// Get transport
		Transport transport;
		String transportName = m_invokerOptions.getTransportName();
		if (transportName != null) {
			transportName = transportName.toUpperCase();
			transport = getTransport(transportName);
		} else {
			transportName = m_serviceDesc.getDefTransportName();
			transport = m_serviceDesc.getDefTransport();
		}

		// Check if it is a REST request and get max URL length
		boolean isRest = Boolean.TRUE.equals(m_invokerOptions.isREST());
		int maxUrlLengthForREST = isRest ? getMaxUrlLengthForRest(MAX_URL_LENGTH_FOR_REST)
				: MAX_URL_LENGTH_FOR_REST;

		// Get Request Data Binding
		DataBindingDesc requestDataBinding = getRequestDataBindingDesc(isRest);

		// Get Response Data Binding
		DataBindingDesc responseDataBinding = getResponseDataBindingDesc(
				requestDataBinding, isRest);

		// Get protocol processor
		ProtocolProcessorDesc protocolProcessor = getProtocolProcessor(
				transport, requestDataBinding, responseDataBinding);

		// Get G11n options
		G11nOptions configG11nOptions = m_serviceDesc.getG11nOptions() == null ? s_emptyG11nOptions
				: m_serviceDesc.getG11nOptions();
		G11nOptions g11nOptions = G11nOptions.mergeFallbackOptions(
				m_g11nOptions, configG11nOptions);

		// Get Transport Headers
		Map<String, String> transportHeaders = combineTransportHeaders();

		// Get Transport Cookies
		Cookie[] cookies = combineCookies();

		// Get Message Headers
		Collection<ObjectNode> messageHeaders = combineMessageHeaders();

		// Get Message for attachments in the request
		BaseMessageAttachments outAttachments = !inboundRawMode
				&& operation.getRequestType().hasAttachment() ? new OutboundMessageAttachments(
				protocolProcessor.getName())
				: null;

		// Get out bound Message
		OutboundMessageImpl requestMsg = new OutboundMessageImpl(true,
				transportName, requestDataBinding, g11nOptions,
				transportHeaders, cookies, messageHeaders, outAttachments,
				operation, isRest, maxUrlLengthForREST);

		// Get in bound Message
		InboundMessageImpl responseMsg = new InboundMessageImpl(false,
				transportName, responseDataBinding, g11nOptions, null, null,
				null, null, operation);

		// Get response Transport Name
		String responseTransportName = m_invokerOptions
				.getResponseTransportName() == null ? m_serviceDesc
				.getResponseTransport() : m_invokerOptions
				.getResponseTransportName();

		// Get service Version
		String serviceVersion = getVersion();

		String useCase;
		String consumerId = getConsumerId(transportHeaders);
		if (consumerId != null && !consumerId.isEmpty()) {
			// set the useCase to use consumerId
			useCase = SOAConstants.APPNAME_PREFIX + consumerId;
		}
		else {
			 useCase = getUseCase(transportHeaders);
		}

		// Get urlPathInfo
		m_urlPathInfo = m_invokerOptions.getUrlPathInfo() == null ? m_serviceDesc
				.getUrlPathInfo()
				: m_invokerOptions.getUrlPathInfo();

		// get host name
		String hostName = (m_serviceLocation != null ? m_serviceLocation
				.getHost() : null);
		ServiceAddress serviceAddress = getServiceAddress(m_serviceLocation,
				m_urlPathInfo, hostName);

		URL serviceUrl = serviceAddress.getServiceUrl();
		String serviceUrlStr = (serviceUrl != null ? serviceUrl.toString()
				: null);

		// check for Mark-down
		checkMarkdown(opName, serviceAddress);

		// Check for recording in response pay load
		if (Boolean.TRUE.equals(m_invokerOptions.shouldRecordResponsePayload())) {
			responseMsg.recordPayload(Integer.MAX_VALUE);
		}

		// Client Message context
		setCurrentContext(getClientContext(inboundRawMode, outboundRawMode,
				inParams, inBuffer, outParams, outWrapper, operation,
				transport, protocolProcessor, g11nOptions, requestMsg,
				responseMsg, responseTransportName, serviceVersion, useCase,
				consumerId, serviceUrlStr, serviceAddress,useAsync));

		return responseMsg;
	}

	private String getConsumerId(Map<String, String> transportHeaders) {
		String consumerId = null;
		if(transportHeaders != null )
			consumerId = transportHeaders.get(SOAHeaders.CONSUMER_ID);
		if(consumerId == null || consumerId.isEmpty()) {
			consumerId = m_invokerOptions.getConsumerId();
		}
		if(consumerId == null || consumerId.isEmpty()) {
			consumerId = m_serviceDesc.getConsumerId();
		}
		return consumerId;
	}

	@SuppressWarnings("deprecation")
	private String getUseCase(Map<String, String> transportHeaders) {
		String usecase = null;
		if(transportHeaders != null )
			usecase = transportHeaders.get(SOAHeaders.USECASE_NAME);
		if(usecase == null || usecase.isEmpty()) {
			usecase = m_invokerOptions.getUseCase();
		}
		if(usecase == null || usecase.isEmpty()) {
			usecase = m_serviceDesc.getUseCase();
		}
		return usecase;
	}

	private String getVersion() {
		if (m_serviceVersion == null) {
			return m_serviceDesc.getServiceVersion();
		}

		return m_serviceVersion;
	}

	Map<String, String> getSessionTransportHeaders() {
		if (m_sessionTransportHeaders == null) {
			return CollectionUtils.EMPTY_STRING_MAP;
		}

		return Collections.unmodifiableMap(new HashMap<String, String>(
				m_sessionTransportHeaders));
	}

	private Collection<ObjectNode> getSessionMessageHeaders() {
		if (m_sessionMessageHeaders == null) {
			return Collections
					.unmodifiableCollection(new ArrayList<ObjectNode>());
		}

		return Collections.unmodifiableCollection(new ArrayList<ObjectNode>(
				m_sessionMessageHeaders));
	}

	private void processResponse(boolean outboundRawMode,
			List<Object> outParams, ByteBufferWrapper outWrapper,
			InboundMessageImpl responseMsg) throws ServiceException,
			ServiceInvocationException {
		if (outboundRawMode) {
			getOutBoundRawData(getCurrentContext().getResponseMessage(),
					outWrapper);
			if (outWrapper.getByteBuffer() == null
					|| outWrapper.getByteBuffer().array() == null
					|| outWrapper.getByteBuffer().array().length == 0)
				checkForErrors(responseMsg);
		} else {
			checkForErrors(responseMsg);
			getOutParams(getCurrentContext().getResponseMessage(), outParams);
		}
	}

	static public void getOutParams(Message inboundMessage,
			List<Object> outParams) throws ServiceException,
			ServiceInvocationException {
		if (outParams != null) {
			int respCparamCount = inboundMessage.getParamCount();
			for (int i = 0; i < respCparamCount; i++) {
				Object param = inboundMessage.getParam(i);
				outParams.add(param);
			}
		}
	}

	static public void getOutBoundRawData(Message inboundMessage,
			ByteBufferWrapper outWrapper) throws ServiceException {
		outWrapper.setByteBuffer(inboundMessage.getByteBuffer());
	}

	private ClientMessageContextImpl getClientContext(boolean inboundRawMode,
			boolean outboundRawMode, Object[] inParams, ByteBuffer inBuffer,
			List<Object> outParams, ByteBufferWrapper outWrapper,
			ServiceOperationDesc operation, Transport transport,
			ProtocolProcessorDesc protocolProcessor, G11nOptions g11nOptions,
			OutboundMessageImpl requestMsg, InboundMessageImpl responseMsg,
			String responseTransportName, String serviceVersion,
			String useCase, String consumerId, String serviceUrl, ServiceAddress serviceAddress, boolean useAsync)
			throws ServiceException {
		ClientMessageContextImpl ctx = new ClientMessageContextImpl(
				m_serviceDesc, operation, protocolProcessor, transport,
				requestMsg, responseMsg, serviceAddress, null, serviceVersion,
				m_invokerOptions, responseTransportName, useCase, consumerId, g11nOptions
						.getCharset(), serviceUrl,useAsync);
		ctx.setServicePoller(m_servicePoller);
		if (inboundRawMode) {
			ctx.setInboundRawMode(true);
			requestMsg.setByteBuffer(inBuffer);
		} else {
			setInParams(inParams, requestMsg);
		}

		ctx.setOutboundRawMode(outboundRawMode);
		ctx.setOutParams(outParams);
		ctx.setOutBuffer(outWrapper);

		Map<String, Object> requestProperties = null;
		if (m_requestContext != null) {
			requestProperties = getRequestContextPropertiesInternal(m_requestContext);
			for (Map.Entry<String, Object> p : requestProperties.entrySet()) {
				ctx.setRequestProperty(p.getKey(), p.getValue());
			}
		}
		return ctx;
	}

	private ServiceAddress getServiceAddress(URL serviceLocationUrl,
			String pathInfo, String hostName) {
		ServiceAddress serviceAddress = null;

		serviceAddress = new ServiceAddress(hostName, null, serviceLocationUrl,
				pathInfo, false);

		return serviceAddress;
	}

	private Transport getTransport(String transportName)
			throws ServiceException {
		Transport transport;
		transport = m_serviceDesc.getTransport(transportName);
		if (transport == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CANNOT_GET_TRANSPORT,
					ErrorConstants.ERRORDOMAIN, new Object[] { getAdminName(), transportName }));
		}
		return transport;
	}

	private int getMaxUrlLengthForRest(int maxUrlLengthForREST)
			throws ServiceException {
		Integer maxUrlLengthForRESTInt = m_invokerOptions
				.getMaxURLLengthForREST();
		if (maxUrlLengthForRESTInt != null) {
			maxUrlLengthForREST = maxUrlLengthForRESTInt.intValue();
			if (maxUrlLengthForREST <= 0) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_INVALID_MAX_URL_LENGTH,
						ErrorConstants.ERRORDOMAIN, new Object[] { getAdminName(), Integer.valueOf(maxUrlLengthForREST) }));
			}
		}
		return maxUrlLengthForREST;
	}

	private ServiceOperationDesc getOperationDescription(String opName,
			boolean inboundRawMode) throws ServiceException {
		ServiceOperationDesc operation;
		if (inboundRawMode) {
			operation = new ServiceOperationDescImpl(m_serviceDesc
					.getServiceId(), opName);
		} else {
			operation = m_serviceDesc.getOperation(opName);
		}

		if (operation == null && !inboundRawMode) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_UNKNOWN_OPERATION,
					ErrorConstants.ERRORDOMAIN, new Object[] { opName, getAdminName() }));
		}
		return operation;
	}

	private ByteBuffer prepareInBufferForRawMode(boolean inboundRawMode,
			boolean outboundRawMode, List<Object> outParams,
			ByteBufferWrapper inWrapper, ByteBufferWrapper outWrapper) {
		ByteBuffer inBuffer = null;
		if (inboundRawMode) {
			if (inWrapper == null) {
				throw new RuntimeException(
						"DII inbound byte buffer wrapper cannot be null");

				// TODO:
				// create a new ErrorData
			}
			inBuffer = inWrapper.getByteBuffer();
		}

		if (outboundRawMode) {
			if (outWrapper == null) {
				throw new RuntimeException(
						"DII outbound byte buffer wrapper cannot be null");
				// TODO:
				// create a new ErrorData
			}
		} else {
			if (outParams != null) {
				outParams.clear();
			}
		}
		return inBuffer;
	}

	/**
	 * Compose the transport headers from multiple sources, as an ordered sequence of potential overrides:
	 * <ol>
	 * <li>ClientConfig.xml, &lt;transport.../&gt; section</li>
	 * <li>ClientConfig.xml, &lt;invoker-options&gt;&lt;preferred-transport.../&gt;&lt;/invoker-options&gt; section</li>
	 * <li>session transport headers</li>
	 * <li>request transport headers</li>
	 * </ol>
	 * @return
	 */
	private Map<String, String> combineTransportHeaders() {
		String transportName = m_invokerOptions.getTransportName();
		if (transportName != null) {
			transportName = transportName.toUpperCase();
		} else {
			transportName = m_serviceDesc.getDefTransportName();
		}
		ClientConfigHolder configHolder = (ClientConfigHolder) m_serviceDesc.getConfig();
		Map<String, String> headerOptions = null;
		Map<String, Map<String, String>> allHeaderOptions = configHolder.getMessageProcessorConfig().getTransportHeaderOptions();
		if (allHeaderOptions != null) {
			headerOptions = allHeaderOptions.get(transportName);
		}
		Map<String, String> result = headerOptions == null
				? new HashMap<String, String>()
				: new HashMap<String, String>(headerOptions);
		Map<String, String> overrideHeaderOptions = configHolder.getTransportOverrideHeaderOptions();
		if (overrideHeaderOptions != null) {
			result.putAll(overrideHeaderOptions);
		}

		// now override with session headers
		result.putAll(getSessionTransportHeaders());

		// final override - the request headers
		Map<String, String> reqCtxHeaders =
			((m_requestContext == null) ? null
			: getRequestContextTransportHeadersInternal(m_requestContext));

		result = ServiceCallHelper.combineMaps(result, reqCtxHeaders);

		return result;
	}

	private Collection<ObjectNode> combineMessageHeaders() {
		Collection<ObjectNode> reqCtxMessageHeaders = null;
		if (m_requestContext != null) {
			reqCtxMessageHeaders = getRequestContextMessageHeadersInternal(m_requestContext);
		}

		Collection<ObjectNode> result = ServiceCallHelper.combineCollections(
				getSessionMessageHeaders(), reqCtxMessageHeaders);
		if (result == null) {
			return null;
		}
		return result;
	}

	/**
	 * Builds custom cookies
	 */
	private Cookie[] combineCookies() {
		Map<String, Cookie> reqCtxCookies = null;
		if (m_requestContext != null) {
			reqCtxCookies = getRequestContextCookiesInternal(m_requestContext);
		}

		Map<String, Cookie> cookies = ServiceCallHelper.combineMaps(
				getCookiesMap(), reqCtxCookies);
		if (cookies == null) {
			return null;
		}

		Cookie[] result = cookies.values().toArray(new Cookie[cookies.size()]);
		return result;
	}

	/**
	 * Selects protocol processor for this request
	 */
	private ProtocolProcessorDesc getProtocolProcessor(Transport transport,
			DataBindingDesc requestDataBinding,
			DataBindingDesc responseDataBinding) throws ServiceException {
		String protocolName = m_invokerOptions.getMessageProtocolName();
		if (protocolName == null) {
			protocolName = m_serviceDesc.getMessageProtocolName();
		}

		if (protocolName == null) {
			return m_serviceDesc.getNullProtocolProcessor();
		}

		ProtocolProcessorDesc result = m_serviceDesc
				.getProtocolProcessor(protocolName);
		if (result == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_UNKNOWN_PROTOCOL,
					ErrorConstants.ERRORDOMAIN, new Object[] {protocolName, getAdminName() }));
		}

		String requestPayload = requestDataBinding.getPayloadType();
		if (!result.isPayloadSupported(requestPayload)) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_UNSUPPORTED_REQUEST_FORMAT,
					ErrorConstants.ERRORDOMAIN, new Object[] { protocolName, getAdminName(), requestPayload }));
		}

		String responsePayload = responseDataBinding.getPayloadType();
		if (!result.isPayloadSupported(responsePayload)) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_UNSUPPORTED_RESPONSE_FORMAT,
					ErrorConstants.ERRORDOMAIN, new Object[] { protocolName, getAdminName(),responsePayload }));
		}

		return result;
	}

	/**
	 * Finds appropriate DataBindingDesc for message
	 * serialization/de-serialization
	 */
	private DataBindingDesc getRequestDataBindingDesc(boolean isRest)
			throws ServiceException {
		String bindingName = m_invokerOptions.getRequestBinding();

		if (bindingName != null) {
			DataBindingDesc result = m_serviceDesc
					.getDataBindingDesc(bindingName);

			if (result == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_UNKNOWN_BINDING,
						ErrorConstants.ERRORDOMAIN, new Object[] { bindingName, getAdminName() }));
			}

			return result;
		}

		if (isRest) {
			return m_serviceDesc.getDefRestRequestDataBinding();
		}

		DataBindingDesc result = m_serviceDesc.getDefRequestDataBinding();
		if (result == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_BINDING_NOT_SPECIFIED,
					ErrorConstants.ERRORDOMAIN, new Object[] { getAdminName() }));
		}

		return result;
	}

	private String getAdminName() {
		return m_serviceDesc.getAdminName();
	}

	private DataBindingDesc getResponseDataBindingDesc(
			DataBindingDesc defBinding, boolean isRest) throws ServiceException {
		String bindingName = m_invokerOptions.getResponseBinding();

		if (bindingName != null) {
			DataBindingDesc result = m_serviceDesc
					.getDataBindingDesc(bindingName);

			if (result == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_UNKNOWN_BINDING,
						ErrorConstants.ERRORDOMAIN, new Object[] { bindingName, getAdminName() }));
			}

			return result;
		}

		if (isRest) {
			return m_serviceDesc.getDefRestResponseDataBinding();
		}

		DataBindingDesc result = m_serviceDesc.getDefResponseDataBinding();
		if (result != null) {
			return result;
		}

		return defBinding;
	}

	private void setInParams(Object[] inParams, BaseMessageImpl msg)
			throws ServiceException {
		int actualParamCount = (inParams != null ? inParams.length : 0);
		if (actualParamCount != msg.getParamCount()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_INVALID_PARAM_COUNT,
					ErrorConstants.ERRORDOMAIN, new Object[] { Integer.toString(actualParamCount),
							Integer.toString(msg.getParamCount()) }));
		}

		if (inParams != null) {
			for (int i = 0; i < inParams.length; i++) {
				Object value = inParams[i];
				msg.setParam(i, value);
			}
		}
	}

	private void checkMarkdown(String opName, ServiceAddress serviceAddress)
			throws ServiceException {
		MarkdownStateSnapshot<SOAClientMarkdownStateId> markdownState = SOAClientMarkdownStateManager
				.getInstance().getMarkdownState(m_serviceDesc, opName,
						serviceAddress, true);

		if (markdownState != null) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_SERVICE_MARKDOWN,
							ErrorConstants.ERRORDOMAIN, new Object[] {markdownState.getId().getStringId(),
							markdownState.getReason() }));
		}
	}

	public static void checkForErrors(BaseMessageImpl responseMsg,
			ClientMessageContextImpl context, String adminName)
			throws ServiceException, ServiceInvocationException {

		List<Throwable> clientErrors = context.getErrorList();
		boolean hasClientSystemErrors = (clientErrors != null && !clientErrors.isEmpty());
		boolean isAppOnlyException = false;
		Object errorResponse = responseMsg.getErrorResponse();
		if (errorResponse != null) {
			boolean hasServerSystemErrors =
				ServiceCallHelper.hasSystemErrorInResponse(context, errorResponse);
			if (!hasClientSystemErrors && !hasServerSystemErrors) {
				isAppOnlyException = true;
			}

			// TODO: do we want to extract server errors as well and keep them
			// separate in the exception ?

			// we have error response and client-or-server errors
			throw ServiceCallHelper.createInvocationException(context,
					adminName, context.getOperationName(), clientErrors,
					errorResponse, isAppOnlyException, hasServerSystemErrors,
					context.getRequestGuid());
		}
		if (hasClientSystemErrors) {
			throw ServiceCallHelper.createInvocationException(context,
					adminName, context.getOperationName(), clientErrors, null,
					false, false, context.getRequestGuid());
		}
	}

	/**
	 * Check for both system exceptions (client side, or server side contained
	 * in the error response), and application exceptions in the error response.
	 *
	 * Possible outcomes: 1. No errors in our client context, and no error
	 * response. Returns. 2. No errors in our client context; error response
	 * contains application error(s) only. Throws app-only
	 * ServiceInvocationException 3. Client context has errors (which are system
	 * exceptions by definition), and/or error response contains system errors.
	 * Collects all system errors together; adds the error response to the list;
	 * and throws ServiceInvocationException with all these errors.
	 */
	private void checkForErrors(BaseMessageImpl responseMsg)
			throws ServiceException, ServiceInvocationException {
		checkForErrors(responseMsg, getCurrentContext(), getAdminName());
	}

	void postInvokeSuccess() throws ServiceException {
		m_requestContext = null;
		//m_urlPathInfo = null;
		m_responseContext = createResponseContext(getCurrentContext(),
				m_invokerOptions.shouldRecordResponsePayload());

		if (getCurrentContext() != null) {
			// transfer cookies to this session
			Message response = getCurrentContext().getResponseMessage();
			Cookie[] cookies = response.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				setCookie(cookie);
			}

			try {
				SOAClientMarkdownStateManager.getInstance().countSuccess(
						getCurrentContext());
			} catch (Throwable e) {
				getLogger().log(
						Level.SEVERE,
						"Unable to call countSuccess for "
								+ m_serviceDesc.getAdminName() + ": "
								+ e.toString(), e);
			}
		}
	}

	protected void commonHandlingOfError(Throwable exception) {
		postInvokeErrorAndMarkDown(exception);
		throw new WebServiceException(exception);
	}

	private void postInvokeErrorAndMarkDown(Throwable exception) {
		postInvokeError();
		ServiceCallHelper.checkMarkdownError(getCurrentContext(), exception);
	}

	protected void handlingOfSericeException(String opName, ServiceException e) {
		ServiceInvocationException e2 = getServiceInvocationExceptionForServiceException(
				opName, e);
		commonHandlingOfError(e2);
	}

	abstract protected InboundMessageImpl preProcessMessageForDispatch(
			String opName, T inArg) throws ServiceException;

	@Override
	protected Response<T> invokeAsync(String opName, T inArg) {

		try {
			// Do dispatch specific pre processing.
			preProcessMessageForDispatch(opName, inArg);

			ClientMessageContextImpl ctx = getCurrentContext();

			// Message now to be processed by the CMP
			getMessageProcessor().processMessage(ctx, true);

			if (ctx.getErrorList() != null && !ctx.getErrorList().isEmpty()) {
				throw ServiceCallHelper.createInvocationException(ctx, ctx
						.getAdminName(), ctx.getOperationName(), ctx
						.getErrorList(), null, false, false, ctx
						.getRequestGuid());
			}

			AsyncResponse<T> response = new AsyncResponse<T>(ctx);
			getServiePoller().add(ctx.getFutureResponse(), response);
			return response;
		} catch (RuntimeException e) {
			commonHandlingOfError(e);
		} catch (Error e) {
			commonHandlingOfError(e);
		} catch (ServiceInvocationException e) {
			commonHandlingOfError(e);
		} catch (ServiceException e) {
			handlingOfSericeException(opName, e);
		}
		return null;

	}

	@Override
	protected Future<?> invokeAsync(String opName, T inArg,
			AsyncHandler<T> handler) {

		try {
			// Do dispatch specific pre processing.
			preProcessMessageForDispatch(opName, inArg);

			// Store the clients handler
			ClientMessageContextImpl ctx = getCurrentContext();

			ctx.setClientAsyncHandler(handler);

			ctx.setExecutor(m_executor);

			// Message now to be processed by the CMP
			getMessageProcessor().processMessage(ctx, true);

			if (ctx.getErrorList() != null && !ctx.getErrorList().isEmpty()) {
				throw ServiceCallHelper.createInvocationException(ctx, ctx
						.getAdminName(), ctx.getOperationName(), ctx
						.getErrorList(), null, false, false, ctx
						.getRequestGuid());
			}

			// Return Response for pull
			return getCurrentContext().getFutureResponse();
		} catch (RuntimeException e) {
			commonHandlingOfError(e);
		} catch (Error e) {
			commonHandlingOfError(e);
		} catch (ServiceInvocationException e) {
			commonHandlingOfError(e);
		} catch (ServiceException e) {
			handlingOfSericeException(opName, e);
		}
		return null;
	}

	@Override
	protected void invokeOneWay(String opName, T inArg) {
		invokeAsync(opName, inArg);
	}

	private ServiceInvocationException getServiceInvocationExceptionForServiceException(
			String opName, Throwable e) {
		List<Throwable> clientErrors = new ArrayList<Throwable>(1);
		clientErrors.add(e);
		ServiceInvocationException e2 = ServiceCallHelper
				.createInvocationException(getCurrentContext(), getAdminName(),
						opName, clientErrors, null, false, false, null);
		return e2;
	}

	private void setCurrentContext(ClientMessageContextImpl m_currentContext) {
		this.m_currentContext = m_currentContext;
	}

	public ClientMessageContextImpl getCurrentContext() {
		return m_currentContext;
	}

	public ClientMessageProcessor getMessageProcessor() {
		return m_messageProcessor;
	}

	public IAsyncResponsePoller getServiePoller() {
		return m_servicePoller;
	}

	public void setServicePoller(IAsyncResponsePoller servicePoller) {
		m_servicePoller = servicePoller;
	}
}