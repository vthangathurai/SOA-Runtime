/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.OutboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.OutboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.DeserializerFactoryInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.SerializerFactoryInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateId;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;

import com.ebay.kernel.context.AppBuildConfig;
import com.ebay.kernel.context.ServerContext;
import com.ebay.kernel.markdown.MarkdownStateSnapshot;
import com.ebay.kernel.util.StringUtils;

/**
 * Helps creating ServerMessageContext in a safe way
 *
 * @author ichernyshev
 */
public final class ServerMessageContextBuilder {

	private static String s_poolName = null;

	private final String m_requestUri;
	private final String m_requestTransport;
	private final Transport m_responseTransport;
	private final Map<String,String> m_transportHeaders;
	private final Cookie[] m_cookies;
	private final ServiceAddress m_clientAddress;
	private final ServiceAddress m_serviceAddress;
	private final String m_targetServerName;
	private final int m_targetServerPort;
	private final Map<String, String> m_queryParams;
	private ServerServiceDesc m_serviceDesc;
	private ServiceOperationDesc m_operationDesc;
	private ProtocolProcessorDesc m_protocolProcessor;
	private DataBindingDesc m_requestDataBinding;
	private DataBindingDesc m_responseDataBinding;
	private G11nOptions m_g11nOptions;
	private String m_requestId;
	private String m_requestGuid;
	private List<Throwable> m_errors;
	private String m_requestVersion;
	private Map<String,Object> m_contextProperties;
	private BaseMessageAttachments m_requestAttachments;
	private BaseMessageAttachments m_responseAttachments;
	private ServerMessageContextImpl m_context;
	private boolean m_hasInput;

	public ServerMessageContextBuilder(ServiceResolver resolver,
		String requestUri, String requestTransport, Transport responseTransport,
		Map<String,String> transportHeaders, Cookie[] cookies,
		ServiceAddress clientAddress, ServiceAddress serviceAddress,
		Collection<Throwable> errors, String targetServerName, int targetServerPort, Map<String, String> queryParams)
		throws ServiceException
	{
		m_requestUri = requestUri;
		m_requestTransport = requestTransport;
		m_responseTransport = responseTransport;
		m_transportHeaders = normalizeTransportHeaders(transportHeaders);
		m_cookies = normalizeCookies(cookies);
		m_clientAddress = clientAddress;
		m_serviceAddress = serviceAddress;
		m_targetServerName = targetServerName;
		m_targetServerPort = targetServerPort;
		m_queryParams = queryParams;		// can be null; can be modified at this level

		m_serviceDesc = resolver.lookupServiceDesc();

		Collection<Throwable> resolverErrors = resolver.getErrors();
		if (resolverErrors != null) {
			addErrors(resolverErrors);
		}

		if (errors != null) {
			addErrors(errors);
		}

		lookupOperationDesc();
		lookupRequestBindingDesc();
		lookupResponseBindingDesc();

		loadRequestG11nOptions();
		loadRequestVersion();

		if (!m_serviceDesc.isFallback() && m_operationDesc.isExisting()) {
			MarkdownStateSnapshot<SOAServerMarkdownStateId> markdownState =
				SOAServerMarkdownStateManager.getInstance().getMarkdownState(
					m_serviceDesc, m_operationDesc.getName(), true);

			if (markdownState != null) {
				getLogger().log(Level.FINE, "Service " + markdownState.getId().getStringId() +
					" has been marked down due to " + markdownState.getReason() +
					" in request " + getRequestUriForLog());

				addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_SERVICE_MARKDOWN,
						ErrorConstants.ERRORDOMAIN, new Object[] {
						markdownState.getId().getStringId(), markdownState.getReason()})));
			}
		}

		// If the GUID id exists, use it to create request id
		String requestGuid = m_transportHeaders.get(SOAHeaders.REQUEST_GUID);
		if (requestGuid != null) {
			m_requestGuid = requestGuid;
			m_requestId = completeRequestId(m_requestGuid);
		} else {  // If GUID id doesn't exist,
			String requestId = m_transportHeaders.get(SOAHeaders.REQUEST_ID);
			if (requestId != null) {
				m_requestId = completeRequestId(requestId);
			} else {
				m_requestGuid = HTTPCommonUtils.generateRequestGuid();
				m_requestId = completeRequestId(m_requestGuid);
				setContextProperty(SOAConstants.CTX_PROP_GUID_CREATED, Boolean.valueOf(true));
			}
		}
		m_transportHeaders.put(SOAHeaders.REQUEST_ID, m_requestId);
	}

	public String getRequestUri() {
		return m_requestUri;
	}

	public String getTransportHeader(String name) {
		name = SOAHeaders.normalizeName(name, true);
		return m_transportHeaders.get(name);
	}

	public ServerServiceId getServiceId() {
		return m_serviceDesc.getServiceId();
	}

	public ServiceOperationDesc getOperationDesc() {
		return m_operationDesc;
	}

	public DataBindingDesc getRequestDataBinding() {
		return m_requestDataBinding;
	}

	public DataBindingDesc getResponseDataBinding() {
		return m_responseDataBinding;
	}

	public G11nOptions getG11nOptions() {
		return m_g11nOptions;
	}

	private String getServiceVersion() {
		return m_serviceDesc.getVersionCheckHandler().getVersion();
	}

	public Charset getEffectiveCharset() {
		if (m_serviceDesc.getServiceCharset() != null) {
			// Service has configured a default-encoding; use that.
			return m_serviceDesc.getServiceCharset();
		}

		// use the encoding of the request data.  Never null.
		return m_g11nOptions.getCharset();
	}

	private String getRequestUriForLog() {
		return "'" + m_requestUri + "'";
	}

	public void addError(Throwable th) {
		if (m_errors == null) {
			m_errors = new ArrayList<Throwable>();
		}
		m_errors.add(th);
	}

	public void addErrors(Collection<Throwable> errors) {
		if (m_errors == null) {
			m_errors = new ArrayList<Throwable>();
		}
		m_errors.addAll(errors);
	}

	public boolean hasErrors() {
		return (m_errors != null && !m_errors.isEmpty());
	}

	public void setContextProperty(String name, Object value) {
		if (name == null) {
			throw new NullPointerException();
		}

		if (m_contextProperties == null) {
			m_contextProperties = new HashMap<String,Object>();
		}

		m_contextProperties.put(name, value);
	}

	public void setRequestAttachments(BaseMessageAttachments value) {
		m_requestAttachments = value;
	}

	public void setResponseAttachments(BaseMessageAttachments value) {
		m_responseAttachments = value;
	}

	public void setContextValues(ServerMessageContextImpl ctx) throws ServiceException {
		ctx.setRequestId(m_requestId, m_requestGuid);

		if (m_contextProperties != null) {
			for (Map.Entry<String,Object> e: m_contextProperties.entrySet()) {
				String name = e.getKey();
				Object value = e.getValue();
				if (name.startsWith(SOAConstants.CTX_PROP_PREFIX)) {
					ctx.setSystemProperty(name, value);
				} else {
					ctx.setProperty(name, value);
				}
			}
		}

		if (hasErrors()) {
			for (Throwable e: m_errors) {
				ctx.addError(e);
			}
		}
	}

	private static Map<String,String> normalizeTransportHeaders(Map<String,String> headers) {
		Map<String,String> result = new HashMap<String,String>();
		if (headers == null || headers.isEmpty()) {
			return result;
		}

		for (Iterator<Map.Entry<String,String>> it=headers.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String,String> e = it.next();
			String name = e.getKey();
			String value = e.getValue();

			if (name == null) {
				continue;
			}

			// make all system headers names uppercase
			name = SOAHeaders.normalizeName(name, true);
			value = SOAHeaders.normalizeValue(name, value);

			result.put(name, value);
		}

		return result;
	}

	private static Cookie[] normalizeCookies(Cookie[] cookies) {
		if (cookies == null) {
			return new Cookie[0];
		}

		return cookies;
	}

	private void lookupOperationDesc() throws ServiceException {
		// HOT FIX: For Porlet call, there's a compatibility issue regarding Upper case vs lower case operation.
		// Here, we want to make the first letter of the operation name lower case for Porlet,
		// to handle 587 client talking to 589 server scenario
		// RECEIVING SIDE LOGIC
		String opName = m_transportHeaders.get(SOAHeaders.SERVICE_OPERATION_NAME);
		if (opName != null && opName.equals("Portlet")) {
			StringBuffer sb = new StringBuffer();
			sb.append(Character.toLowerCase(opName.charAt(0)));
			sb.append(opName.substring(1));
			opName = sb.toString();
			m_transportHeaders.put(SOAHeaders.SERVICE_OPERATION_NAME, opName);
		}

		m_operationDesc = m_serviceDesc.lookupOperation(m_requestUri, m_transportHeaders);
		if (m_operationDesc != null) {
			if (!m_operationDesc.isSupported()) {
				getLogger().log(Level.FINE, "Operation name not supported in request " + getRequestUriForLog() +
					" to " + m_serviceDesc.getAdminName() + "." + m_operationDesc.getName());

				addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_OPERATION_UNSUPPORTED,
						ErrorConstants.ERRORDOMAIN, new Object[] {m_operationDesc.getName()})));
			}

			return;
		}

		String operationName = m_transportHeaders.get(SOAHeaders.SERVICE_OPERATION_NAME);
		if (operationName == null) {
			operationName = "***null***";

			getLogger().log(Level.FINE, "Unknown operation name in request " + getRequestUriForLog() +
					" to " + m_serviceDesc.getAdminName() + "." + operationName);

			addError(new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MISSING_OPERATION_NAME,
							ErrorConstants.ERRORDOMAIN, new Object[] {})));

		} else 	if (!m_serviceDesc.isFallback()) {
			getLogger().log(Level.FINE, "Unknown operation name in request " + getRequestUriForLog() +
				" to " + m_serviceDesc.getAdminName() + "." + operationName);

			addError(new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNKNOWN_OPERATION,
					ErrorConstants.ERRORDOMAIN, new Object[] {operationName})));
		}

		// create fallback operation
		m_operationDesc = ServerServiceDescFactory.getInstance().createFallbackOperationDesc(
			m_serviceDesc, operationName);
	}

	private void lookupProtocolProcessor() {
		if (m_serviceDesc.isFallback()) {
			m_protocolProcessor = m_serviceDesc.getNullProtocolProcessor();
			return;
		}

		m_protocolProcessor = m_serviceDesc.lookupProtocolProcessor(m_requestUri, m_transportHeaders);
		if (m_protocolProcessor != null) {

			String requestPayload = m_requestDataBinding.getPayloadType();
			if (!m_protocolProcessor.isPayloadSupported(requestPayload)) {
				getLogger().log(Level.FINE, "Protocol '" + m_protocolProcessor.getName() + "' in request " +
					getRequestUriForLog() + " to " + m_serviceDesc.getAdminName() +
					" does not support request payload " + requestPayload);

				addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_UNSUPPORTED_REQUEST_FORMAT,
						ErrorConstants.ERRORDOMAIN, new Object[] {m_protocolProcessor.getName(),
							m_serviceDesc.getAdminName(), requestPayload})));
			}

			String responsePayload = m_responseDataBinding.getPayloadType();
			if (!m_protocolProcessor.isPayloadSupported(requestPayload)) {
				getLogger().log(Level.FINE, "Protocol '" + m_protocolProcessor.getName() + "' in request " +
					getRequestUriForLog() + " to " + m_serviceDesc.getAdminName() +
					" does not support response payload " + responsePayload);

				addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_UNSUPPORTED_RESPONSE_FORMAT,
						ErrorConstants.ERRORDOMAIN, new Object[] {m_protocolProcessor.getName(),
							m_serviceDesc.getAdminName(), responsePayload})));
			}

			return;
		}

		String protocolName = m_transportHeaders.get(SOAHeaders.MESSAGE_PROTOCOL);
		if (protocolName == null) {
			m_protocolProcessor = m_serviceDesc.getNullProtocolProcessor();
			return;
		}

		// protocol is passed, but not recognized
		getLogger().log(Level.FINE, "Unknown protocol name '" + protocolName + "' in request " +
			getRequestUriForLog() + " to " + m_serviceDesc.getAdminName());

		addError(new ServiceException(
				ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNKNOWN_PROTOCOL,
				ErrorConstants.ERRORDOMAIN, new Object[] {protocolName})));
		m_protocolProcessor = m_serviceDesc.getNullProtocolProcessor();
	}

	private void lookupProtocolProcessor(InboundMessage msg) throws ServiceException {
		if (m_serviceDesc.isFallback()) {
			m_protocolProcessor = m_serviceDesc.getNullProtocolProcessor();
			return;
		}

		// First check the headers
		m_protocolProcessor = m_serviceDesc.lookupProtocolProcessor(m_requestUri, m_transportHeaders);

		if (m_protocolProcessor == null) {
			String protocolName = m_transportHeaders.get(SOAHeaders.MESSAGE_PROTOCOL);
			if (protocolName != null) {	// Protocol name is passed, but not recognized
				getLogger().log(Level.FINE, "Unknown protocol name '" + protocolName + "' in request " +
						getRequestUriForLog() + " to " + m_serviceDesc.getAdminName());

				addError(new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNKNOWN_PROTOCOL,
						ErrorConstants.ERRORDOMAIN, new Object[] {protocolName})));
			}

			// Then ask each processor whether it's the right one for the message

			Collection<ProtocolProcessorDesc> protocolProcessors = m_serviceDesc.getAllProtocolProcessors();
			for (ProtocolProcessorDesc ppdesc : protocolProcessors) {
				if (ppdesc.getProcessor().isExpectedMessageProtocol(msg)) {
					m_protocolProcessor = ppdesc;
					break;
				}
			}
		}

		if (m_protocolProcessor != null) {
			String requestPayload = m_requestDataBinding.getPayloadType();
			if (!m_protocolProcessor.isPayloadSupported(requestPayload)) {
				getLogger().log(Level.FINE, "Protocol '" + m_protocolProcessor.getName() + "' in request " +
					getRequestUriForLog() + " to " + m_serviceDesc.getAdminName() +
					" does not support request payload " + requestPayload);

				addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_UNSUPPORTED_REQUEST_FORMAT,
						ErrorConstants.ERRORDOMAIN, new Object[] {m_protocolProcessor.getName(),
							m_serviceDesc.getAdminName(), requestPayload})));
			}

			String responsePayload = m_responseDataBinding.getPayloadType();
			if (!m_protocolProcessor.isPayloadSupported(requestPayload)) {
				getLogger().log(Level.FINE, "Protocol '" + m_protocolProcessor.getName() + "' in request " +
					getRequestUriForLog() + " to " + m_serviceDesc.getAdminName() +
					" does not support response payload " + responsePayload);

				addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_UNSUPPORTED_RESPONSE_FORMAT,
						ErrorConstants.ERRORDOMAIN, new Object[] {m_protocolProcessor.getName(),
							m_serviceDesc.getAdminName(), responsePayload})));
			}
			return;
		}

		m_protocolProcessor = m_serviceDesc.getNullProtocolProcessor();
	}

	private void lookupRequestBindingDesc() throws ServiceException {
		m_requestDataBinding = m_serviceDesc.lookupDataBindingForRequest(m_requestUri, m_transportHeaders);
		if (m_requestDataBinding != null) {
			return;
		}

		String payloadType = m_transportHeaders.get(SOAHeaders.REQUEST_DATA_FORMAT);
		if (payloadType == null) {
			payloadType = "***null***";
		}

		if (!m_serviceDesc.isFallback()) {
			getLogger().log(Level.FINE, "Unknown request data format '" + payloadType + "' in request " +
				getRequestUriForLog() + " to " + m_serviceDesc.getAdminName());

			addError(new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNKNOWN_REQUEST_PAYLOAD,
							ErrorConstants.ERRORDOMAIN, new Object[] {payloadType})));
		}

		// create fallback request binding
		m_requestDataBinding = createFallbackDataBinding("request", payloadType);
	}

	private void lookupResponseBindingDesc() throws ServiceException {
		m_responseDataBinding = m_serviceDesc.lookupDataBindingForResponse(m_requestUri, m_transportHeaders);
		if (m_responseDataBinding != null) {
			return;
		}

		String payloadType = m_transportHeaders.get(SOAHeaders.RESPONSE_DATA_FORMAT);
		if (payloadType == null) {
			// TODO: move this logic to matcher?
			m_responseDataBinding = m_requestDataBinding;
			return;
		}

		if (!m_serviceDesc.isFallback()) {
			getLogger().log(Level.FINE, "Unknown response data format '" + payloadType + "' in request " +
				getRequestUriForLog() + " to " + m_serviceDesc.getAdminName());

			addError(new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNKNOWN_RESPONSE_PAYLOAD,
					ErrorConstants.ERRORDOMAIN, new Object[] {payloadType})));
		}

		// create fallback response binding
		m_responseDataBinding = createFallbackDataBinding("request", payloadType);
	}

	private void initSerializerFactory(SerializerFactory factory,
		ServiceId svcId, Map<String,String> options, Class[] rootClasses) throws ServiceException
	{
		SerializerFactoryInitContextImpl initCtx =
			new SerializerFactoryInitContextImpl(svcId, options, rootClasses);
		factory.init(initCtx);
		initCtx.kill();
	}

	private void initDeserializerFactory(DeserializerFactory factory,
		ServiceId svcId, Map<String,String> options, Class[] rootClasses) throws ServiceException
	{
		DeserializerFactoryInitContextImpl initCtx =
			new DeserializerFactoryInitContextImpl(svcId, options, rootClasses, null);
		factory.init(initCtx);
		initCtx.kill();
	}

	private DataBindingDesc createFallbackDataBinding(String name, String payloadType)
		throws ServiceException
	{

		// There are 2 scenarios why we're here:
		// 1. it is a totally custom payload type (not JSON, NV or XML),
		//    which is not supported by this real or fallback ServiceDesc
		// 2. it is a real service, but this well-known payload type (NV, JSON or XML)
		//    is not in the list of supported bindings
		// In both cases, fallback binding for sending error response should be XML

		DeserializerFactory deser = new JAXBXMLDeserializerFactory();
		SerializerFactory ser = new JAXBXMLSerializerFactory();
		ServerServiceId serverServiceId = null;

		Collection<ServiceOperationDesc> opDescs = null;
		if (m_serviceDesc != null) {
			opDescs = m_serviceDesc.getAllOperations();
			serverServiceId = m_serviceDesc.getServiceId();
		}

		Set<Class> rootClasses = BaseServiceDescFactory.getRootClassesFromOperations(opDescs);
		Class[] rootClzes = BaseServiceDescFactory.addDataBindingSpecificTypes(rootClasses, null);
		initSerializerFactory(ser, serverServiceId, null, rootClzes);
		initDeserializerFactory(deser, serverServiceId, null, rootClzes);

		return new DataBindingDesc(
			"***fallback_" + name + "_data_binding_" + ser.getPayloadType() + "***",
			SOAConstants.MIME_XML,
			ser, deser, null, null, null, null);
	}

	private void loadRequestG11nOptions() throws ServiceException {
		String globalIdStr = m_transportHeaders.get(SOAHeaders.GLOBAL_ID);
		String localeStr = m_transportHeaders.get(SOAHeaders.LOCALE_LIST);
		String encoding = m_transportHeaders.get(SOAHeaders.MESSAGE_ENCODING);

		Charset charset = G11nOptions.DEFAULT_CHARSET;
		if (encoding != null) {
			try {
				charset = Charset.forName(encoding);
			} catch (Exception e) {
				getLogger().log(Level.FINE, "Unknown encoding name '" + encoding + "' in request " +
					getRequestUriForLog() + " to " + m_serviceDesc.getAdminName());

				addError(new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNSUPPORTED_CHARSET,
						ErrorConstants.ERRORDOMAIN, new Object[] {encoding}), e));
			}
		}

		List<String> locales;
		if (localeStr != null) {
			locales = StringUtils.splitStr(localeStr, ',', true);
		} else {
			locales = null;
		}

		m_g11nOptions = new G11nOptions(charset, locales, globalIdStr);
	}

	private void loadRequestVersion() {
		m_requestVersion = m_transportHeaders.get(SOAHeaders.VERSION);
	}

	/**
	 * Request ID could be just "guid", or a combination of guid+svcName+IP+poolName.
	 * We will always append our current svcName+IP+poolName with "!" as delimiter.
	 *
	 * @param requestId
	 * @return
	 */
	private String completeRequestId(String requestId) {
		String serviceName = m_serviceDesc.getAdminName();
		String ipAddr = ServerContext.getHostAddress();
		return requestId + "!" + serviceName + "!" + ipAddr + "!" + getPoolName() + "[";
	}

	/**
	 * Get pool name from build info
	 */
	private static String getPoolName() {
		if (s_poolName != null) {
			return s_poolName;
		}

		try {
			s_poolName = AppBuildConfig.getInstance().getCalPoolName();
		} catch (Throwable e) {
			s_poolName = "";
			getLogger().log(Level.WARNING, "Unable to get pool name", e);
		}

		return s_poolName;
	}

	private void createMessageContext(InputStream is) throws ServiceException {
		if (m_context != null) {
			return;
		}

		// NOTE: no message header will be available at this stage, thus NULL is being passed into the outbound message impl
		InboundMessageImpl requestMsg = new InboundMessageImpl(true,
			m_requestTransport, getRequestDataBinding(),
			getG11nOptions(), m_transportHeaders,
			m_cookies, null, m_requestAttachments, getOperationDesc());

		if (is != null) requestMsg.setInputStream(is);

		ServiceOperationDesc opDesc = getOperationDesc();
				ServiceOperationParamDesc respParam = opDesc.getResponseType();
				boolean respHasAttachment = respParam.hasAttachment();
				if (respHasAttachment) {
					String protocolName = m_transportHeaders.get(SOAHeaders.MESSAGE_PROTOCOL);
					setResponseAttachments(new OutboundMessageAttachments(protocolName));
		}

		OutboundMessageImpl responseMsg = new OutboundMessageImpl(false,
			m_requestTransport, getResponseDataBinding(),
			getG11nOptions(), null, null, null, m_responseAttachments, getOperationDesc(), false, 0);

		ServerMessageContextImpl ctx = new ServerMessageContextImpl(
			m_serviceDesc, getOperationDesc(),
			null, m_responseTransport,
			requestMsg, responseMsg, m_serviceAddress, null,
			m_clientAddress, m_requestVersion,
			getServiceVersion(),
			getEffectiveCharset(),
			m_requestUri,
			m_targetServerName,
			m_targetServerPort,
			m_queryParams);

		lookupProtocolProcessor(requestMsg);

		ctx.setProtocolProcessor(m_protocolProcessor);

		setContextValues(ctx);

		m_context = ctx;
	}

	public final List<Throwable> getContextErrorList() {
		if (m_context != null) {
			return m_context.getErrorList();
		}
		return CollectionUtils.EMPTY_THROWABLE_LIST;
    }

	public void setInputStream(InputStream is) throws ServiceException {
		createMessageContext(is);
		m_hasInput = true;
	}

	public void setParamReferences(Object[] params) throws ServiceException {
		createMessageContext(null);
		InboundMessage msg = (InboundMessage)m_context.getRequestMessage();
		msg.setParamReferences(params);
		m_hasInput = true;
	}

	public void processCall() throws ServiceException {
		if (!m_hasInput) {
			throw new IllegalStateException("No input was provided for the request message");
		}

		ServerMessageProcessor processor = ServerMessageProcessor.getInstance();
		processor.processMessage(m_context);
	}

	private static Logger getLogger() {
		return LogManager.getInstance(ServerMessageContextBuilder.class);
	}

	public static void validateServiceName(String adminName) throws ServiceException {
		ServerServiceDescFactory.getInstance().getServiceDesc(adminName);
	}

	public static void init() throws ServiceException {
		ServerMessageProcessor.getInstance();
	}
}
