/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.DataBindingOptions;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Serializer;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ExceptionUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.OutboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.SOAMimeUtils;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.JAXBBasedSerializer;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.OutboundRawDataRecorder;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;

public final class OutboundMessageImpl extends BaseMessageImpl implements OutboundMessage {
	private int m_maxBytesToRecord;
	private byte[] m_recordedData;
	private boolean m_isRest;
	private boolean m_isUnserializable;
	private String m_unserializableReason;
	private int m_maxURLLengthForREST;

	public OutboundMessageImpl(boolean isRequestMessage,
		String transportProtocol,
		DataBindingDesc dataBindingDesc, G11nOptions g11nOptions,
		Map<String,String> transportHeaders, Cookie[] cookies,
		Collection<ObjectNode> messageHeaders,
		BaseMessageAttachments attachment, ServiceOperationDesc operationDesc,
		boolean isREST, int maxURLLengthForREST)
		throws ServiceException
	{
		super(isRequestMessage, transportProtocol, dataBindingDesc,
			g11nOptions, transportHeaders, cookies, messageHeaders, attachment, operationDesc);
		m_isRest = isREST;
		m_maxURLLengthForREST = maxURLLengthForREST;
	}

	@Override
	protected void loadParamsImpl() throws ServiceException {
		List<Class> paramTypes = getParamTypes();
		if (paramTypes != null && !paramTypes.isEmpty()) {
			m_params = new Object[paramTypes.size()];
		}
	}

	public final boolean isErrorMessage() throws ServiceException {
		return (m_errorResponse != null);
	}

	@SuppressWarnings("unchecked")
	public Map<String,String> buildOutputHeaders() throws ServiceException
	{
		if (hasAttachment()) {
			((OutboundMessageAttachments)m_attachments).addAttachmentHeaders(this);
		}
		Map<String,String> customHeaders = getTransportHeadersNoCopy();
		Map<String,String> result = getContextImpl().buildOutputHeaders(customHeaders);
		return result;
	}

	public void recordPayload(int maxBytes) throws ServiceException {
		checkNotCleaned();

		if (maxBytes > m_maxBytesToRecord) {
			m_maxBytesToRecord = maxBytes;
		}
	}

	public byte[] getRecordedData() throws ServiceException {
		if (m_recordedData != null) {
			byte result[] = new byte[m_recordedData.length];
			System.arraycopy(m_recordedData, 0, result, 0, m_recordedData.length);
			return result;
		}

		return null;
	}

	public void serialize(OutputStream out) throws ServiceException {
		if (isUnserializable()) {
			throw new IllegalStateException("Message is not serializable");
		}

		m_recordedData = null;
		OutboundRawDataRecorder recorder = null;
		if (m_maxBytesToRecord != 0) {
			recorder = new OutboundRawDataRecorder(out, false, m_maxBytesToRecord);
			out = recorder;
		}

		if (getContextImpl().isInboundRawMode()) {
			try {
				out.write(getByteBuffer().array());
			} catch (IOException e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_WRITE_ERROR, 
						ErrorConstants.ERRORDOMAIN, new String[] {e.toString()}), e);
			}
		} else {
			// make sure to get m_params set, even if no values were specified by the app
			checkParams();
	
			try {
				if (hasAttachment()) {
					serializeMessageWithAttachment(out);
				} else {
					serializeMessage(out);
				}
			} catch (Exception e) {
				try {
					filterException(e);
				} catch(ServiceException se) {
					try {
						out.write("Internal server error. Please check the server logs for details".getBytes());
					} catch(IOException ioe) {
						// Something is seriously wrong with the state of output stream
					}
					throw se;
				}
			}
		}

		if (recorder != null) {
			m_recordedData = recorder.getRawByteData();
		}
	}

	public void serializeMessageWithAttachment(OutputStream out) throws ServiceException {
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
		serializeMessage(bodyStream);
		
		SOAMimeUtils.complete(out, bodyStream.toString(), ((OutboundMessageAttachments)m_attachments));
	}

	public void serializeMessage(OutputStream out) throws ServiceException {

		BaseMessageContextImpl ctx = getContextImpl();

		if (!isRequestMessage()) {
			MessageProcessingStage processingStage = ctx.getProcessingStage();
			if (processingStage != MessageProcessingStage.RESPONSE_DISPATCH) {
				throw new IllegalStateException(
					"Server-side serialization is allowed during response dispatch only");
			}
		}

		SerializerFactory serFactory = getDataBindingDesc().getSerializerFactory();
		Serializer ser = serFactory.getSerializer();

		List<Class> paramTypes;
		if (m_errorResponse != null) {
			paramTypes = getErrorParamTypeList();
		} else {
			paramTypes = getParamTypes();
		}

		XMLStreamWriter xmlStream = serFactory.getXMLStreamWriter(this, paramTypes, out);

		ProtocolProcessor protocolProcessor = ctx.getProtocolProcessor();

		long startTime = System.nanoTime();
		try {
			if (m_errorResponse != null) {
				// note that we use errorMessage's class here
				// in case when ErrorMapper fails, this could be our default ErrorMessage
				// and not the one produced by ErrorMapper implementation
				
				
				protocolProcessor.preSerialize(this, xmlStream);
				QName xmlName = getMessageXMLName(getParamDesc(), m_errorResponse.getClass());
				// If service version is not provided as http header by client request, 
				// we assume the request is using common types ErrorMessage to pass error back.
/*	03/06/2009			Comment this code out because the migration is over.
 * 				if (shouldReturnCommonTypeErrorMessage()) {
					convertToCommonTypeErrorMessage();
					xmlName = new QName(BindingConstants.SOA_COMMON_TYPES_NAMESPACE, xmlName.getLocalPart(),
							BindingConstants.SOA_COMMON_TYPES_PREFIX);
				}
*/				ser.serialize(this, m_errorResponse, xmlName, m_errorResponse.getClass(), xmlStream);
				protocolProcessor.postSerialize(this, xmlStream);
			} else {
				protocolProcessor.preSerialize(this, xmlStream);
				if (m_params != null) {
					for (int i=0; i<m_params.length; i++) {
						Object value = m_params[i];
						Class<?> clazz = paramTypes.get(i);
						QName xmlName = getMessageXMLName(getParamDesc(), clazz);
						ser.serialize(this, value, xmlName, clazz, xmlStream);
					}
				}
				protocolProcessor.postSerialize(this, xmlStream);
			}
		} finally {
			long duration = System.nanoTime() - startTime;
			ctx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_SERIALIZATION, startTime, duration);
		}

		try {
			xmlStream.flush();
			out.flush();
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_WRITE_ERROR, 
					ErrorConstants.ERRORDOMAIN, new String[] {e.toString()}), e);
		}
	}
	
	public void serializeBody(OutputStream out) throws ServiceException {
		
		SerializerFactory serFactory = getDataBindingDesc().getSerializerFactory();
		Serializer ser = serFactory.getSerializer();

		List<Class> paramTypes;
		paramTypes = getParamTypes();

		XMLStreamWriter xmlStream = serFactory.getXMLStreamWriter(this, paramTypes, out);

		if (m_params != null) {
			for (int i=0; i<m_params.length; i++) {
				Object value = m_params[i];
				Class<?> clazz = paramTypes.get(i);
				QName xmlName = getMessageXMLName(getParamDesc(), clazz);
				ser.serialize(this, value, xmlName, clazz, xmlStream);
			}
		}
	}
	
	public void setErrorResponse(Object error) throws ServiceException {
		if (error == null) {
			throw new NullPointerException();
		}

		markAsLoaded();

		m_errorResponse = error;
		m_params = null;
	}

	// TODO - protection may need to be tightened up
	public void setG11nOptions(G11nOptions options) {
		m_g11nOptions = options;
	}
	
	public boolean isREST() {
		return m_isRest;
	}
	
	public int getMaxURLLengthForREST() {
		return m_maxURLLengthForREST;
	}
	
	public void addDataHandler(DataHandler dh, String id) {
    	if (null == m_attachments) {
    		return;
    	}
    	((OutboundMessageAttachments)m_attachments).addDataHandler(dh, id);
    }

	public String getUnserializableReason() {
		return m_unserializableReason;
	}

	public boolean isUnserializable() {
		return m_isUnserializable;
	}

	public void setUnserializable(String reason) {
		m_isUnserializable = true;
		m_unserializableReason = reason;
	}
	
	
	public Collection<ObjectNode> getMessageHeaders() throws ServiceException {
		return m_messageHeaders;
	}
	
	public void addMessageHeaderAsJavaObject(Object headerJavaObject) throws ServiceException {
		m_messageHeaders.add(new JavaObjectNodeImpl(null, headerJavaObject));
	}

	
	public void serializeHeader(XMLStreamWriter out) throws ServiceException {
		if (null == getMessageHeaders()) {
			return;
		}
		for (ObjectNode node : getMessageHeaders()) {
			//TODO: We should refactor this, possibly add a serialize method to ObjectNode.
			if (node instanceof JavaObjectNodeImpl) {
				serializeJavaObjectNode((JavaObjectNodeImpl)node, out);
			} else {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_UNSUPPORTED_OBJECT_NODE_SERIALIZATION, 
						ErrorConstants.ERRORDOMAIN, new Object[] {node.getClass().getSimpleName()}));
			}
		}
	}
		
	private void serializeJavaObjectNode(JavaObjectNodeImpl node, XMLStreamWriter out) 
	 throws ServiceException {
		Object obj = node.getNodeValue();
		SerializerFactory serFactory = getDataBindingDesc().getSerializerFactory();
		Serializer ser = serFactory.getSerializer();
		QName xmlName = getMessageXMLName(getHeaderParamDesc(), obj.getClass());
		ser.serialize(this, obj, xmlName, obj.getClass(), out);
	}
	
	public int getTransportErrorResponseIndicationCode() throws ServiceException {
		BaseMessageContextImpl ctx = getContextImpl();
		ProtocolProcessor protocolProcessor = ctx.getProtocolProcessor();
		int code = HttpURLConnection.HTTP_INTERNAL_ERROR; // Default. Will be overwritten.

		if (ctx.getRequestMessage().getTransportHeader(SOAHeaders.ALTERNATE_FAULT_STATUS) != null) {
			code = protocolProcessor.getAlternateTransportErrorResponseIndicationCode();
		} else {
			code = protocolProcessor.getTransportErrorResponseIndicationCode();
		}
		return code;
	}

	
	private boolean shouldReturnCommonTypeErrorMessage() {
		MessageContext context = getContext();
		if (context instanceof ClientMessageContext) {
			return false;
		}
		String invokerVersion = context.getInvokerVersion();
		return invokerVersion == null;
	}


	public void filterException(Exception e) throws ServiceException {
		SerializerFactory serFactory = getDataBindingDesc().getSerializerFactory();
		Map<String, String> options = serFactory.getOptions();
		boolean ignoreClientTimeout = DataBindingOptions.IgnoreClientTimeout.getBoolOption(options);
		if (!ignoreClientTimeout || !ExceptionUtils.isClientTimeoutException(e)) {
			ServiceException se = null;
			if (e instanceof ServiceException) {
				se = (ServiceException) e;
			} else {
				se = new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_WRITE_ERROR, 
						ErrorConstants.ERRORDOMAIN, new String[] {e.toString()}), e);
			}
			throw se;
		}
		LogManager.getInstance(JAXBBasedSerializer.class).log(Level.SEVERE, "Client timeout", e); 
	}
}
