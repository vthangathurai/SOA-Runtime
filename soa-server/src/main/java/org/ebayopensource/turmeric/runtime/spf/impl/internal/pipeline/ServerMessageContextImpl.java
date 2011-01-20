/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.LoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.security.ServerSecurityContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerLoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceContext;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;

/**
 * Internal representation of the server-side message context.
 *
 * @author ichernyshev
 */
public final class ServerMessageContextImpl
	extends BaseMessageContextImpl<ServerServiceDesc,ServerServiceContext>
	implements ServerMessageContext
{
	private final String m_requestVersion;
	private final String m_targetServerName;
	private final int m_targetServerPort;
	private Map<String, String> m_queryParams;

	/**
	 * Constructor. Service writers should never call this method.
	 * @param serviceDesc
	 * @param operation
	 * @param protocolProcessor
	 * @param transport
	 * @param requestMessage
	 * @param responseMessage
	 * @param serviceAddress
	 * @param systemProperties
	 * @param clientAddress
	 * @param requestVersion
	 * @param serviceVersion
	 * @param effectiveCharset
	 * @param requestUri
	 * @param targetServerName
	 * @param targetServerPort
	 * @param queryParams
	 * @throws ServiceException
	 */
	public ServerMessageContextImpl(
		ServerServiceDesc serviceDesc,
		ServiceOperationDesc operation,
		ProtocolProcessorDesc protocolProcessor,
		Transport transport,
		BaseMessageImpl requestMessage,
		BaseMessageImpl responseMessage,
		ServiceAddress serviceAddress,
		Map<String,Object> systemProperties,
		ServiceAddress clientAddress,
		String requestVersion,
		String serviceVersion,
		Charset effectiveCharset,
		String requestUri, String targetServerName, int targetServerPort, Map<String, String> queryParams)
		throws ServiceException
	{
		super(serviceDesc, operation, protocolProcessor, transport,
			requestMessage, responseMessage, clientAddress, serviceAddress,
			systemProperties, serviceVersion, effectiveCharset, requestUri);

		m_requestVersion = requestVersion;
		m_securityContext = new ServerSecurityContextImpl(this);
		m_targetServerName = targetServerName;
		m_targetServerPort = targetServerPort;
		if (queryParams == null) {
			queryParams = new HashMap<String,String>();
		}
		m_queryParams = Collections.unmodifiableMap(queryParams);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getServiceId()
	 */
	public ServerServiceId getServiceId() {
		return getServiceDesc().getServiceId();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getInvokerVersion()
	 */
	public String getInvokerVersion() {
		return m_requestVersion;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext#getTargetServerName()
	 */
	public String getTargetServerName() {
		return m_targetServerName;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext#getTargetServerPort()
	 */
	public int getTargetServerPort() {
		return m_targetServerPort;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext#getQueryParams()
	 */
	public Map<String,String> getQueryParams() {
		return m_queryParams;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseMessageContextImpl#buildOutputHeaders(java.util.Map)
	 */
	@Override
	protected Map<String,String> buildOutputHeaders(Map<String,String> customHeaders)
		throws ServiceException
	{
		Map<String,String> result = new HashMap<String,String>();
		if (customHeaders != null) {
			result.putAll(customHeaders);
		}

		if (getServiceVersion() != null) {
			result.put(SOAHeaders.VERSION, getServiceVersion());
		}

		HTTPCommonUtils.addServiceAndOperationHeaders(getServiceQName(), getOperationName(), result);

		Message responseMsg = getResponseMessage();
		String payloadType = responseMsg.getPayloadType();
		result.put(SOAHeaders.RESPONSE_DATA_FORMAT, payloadType);

		HTTPCommonUtils.addG11nHeaders(responseMsg.getG11nOptions(), result);

		String messageProtocol =getMessageProtocol();
		if (messageProtocol != null) {
			result.put(SOAHeaders.MESSAGE_PROTOCOL, messageProtocol);
		}

		String requestId = getRequestId();
		if (requestId != null) {
			requestId = requestId + "]";
			setRequestId(requestId, getRequestGuid());
			result.put(SOAHeaders.REQUEST_ID, requestId);
		}

		HeaderMappingsDesc responseHeaderMappings = getServiceDesc().getResponseHeaderMappings();
		HTTPCommonUtils.applySuppressHeaderSet(responseHeaderMappings.getSuppressHeaderSet(), result); // suppress first, map later
		HTTPCommonUtils.applyHeaderMap(responseHeaderMappings.getHeaderMap(), result);

		return result;
	}

	@Override
	protected LoggingHandlerUtils getLoggingHandlerUtils() {
		return new ServerLoggingHandlerUtils();
	}

	public void checkOperationName() throws ServiceException {
		Message requestMsg = getRequestMessage();

		if(requestMsg.getParamTypes().isEmpty()){
			return;
		}

		ObjectNode objectNode = requestMsg.getMessageBody();

		if(objectNode == null) {
			return;
		}

		List<ObjectNode> childNodes;

		try {
			childNodes = objectNode.getChildNodes();
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {e.getMessage()}), e);
		} catch (UnsupportedOperationException e) {
			return;
		}

		if (childNodes == null || childNodes.size() == 0 || childNodes.get(0) == null) {
			return;
		}

		QName requestName = childNodes.get(0).getNodeName();
		ServiceOperationDesc operationDesc = getOperation();
		if(operationDesc==null || operationDesc.getRequestType()==null)
		{
			return;
		}
		List<DataElementSchema> rootElements = operationDesc
				.getRequestType().getRootElements();

		if (rootElements == null || rootElements.size() == 0 || rootElements.get(0) == null) {
			return;
		}

		QName requestTypeName = rootElements.get(0).getElementName();

		if (requestTypeName == null || !requestTypeName.equals(requestName)) {
			ServiceException err = new ServiceException(
				ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_RT_NON_MATCHING_OPERATION_NAME,
					ErrorConstants.ERRORDOMAIN,new Object[] {
						requestName, requestTypeName}));
			addWarning(err);
		}
	}
}
