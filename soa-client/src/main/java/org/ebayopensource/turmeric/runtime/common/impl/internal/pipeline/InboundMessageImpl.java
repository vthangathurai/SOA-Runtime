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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorParameter;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeBuilder;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.SOAPObjectNodeStreamReader;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.InboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.PrereadingRawDataRecorder;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;


/**
 * Internal class representing incoming messages (requests on server side or responses on client side).
 *
 */
public final class InboundMessageImpl extends BaseMessageImpl implements InboundMessage {

	private InputStream m_inputStream;
	private XMLStreamReader m_xmlReader;
	private boolean m_deserCompleted;
	private boolean m_hasParamsByRef;
	private boolean m_hasErrorHeader;
	private boolean m_hasErrorHeaderChecked;
	private boolean m_noStream;
	private boolean m_messageHeaderCompleted;
	private int m_maxBytesToRecord;
	private ObjectNode m_root;
	private PrereadingRawDataRecorder m_recordingStream;
	private Collection<Object> m_javaObjectMsgHeaders;

	/**
	 * Internal constructor. Client or service writers should never construct messages directly.
	 * @param isRequestMessage
	 * @param transportProtocol
	 * @param dataBindingDesc
	 * @param g11nOptions
	 * @param transportHeaders
	 * @param cookies
	 * @param attachments
	 * @param operationDesc
	 */
	public InboundMessageImpl(boolean isRequestMessage, String transportProtocol,
		DataBindingDesc dataBindingDesc, G11nOptions g11nOptions,
		Map<String,String> transportHeaders, Cookie[] cookies,
		Collection<ObjectNode> messageHeaders,
		BaseMessageAttachments attachments,
		ServiceOperationDesc operationDesc)
		throws ServiceException
	{
		super(isRequestMessage, transportProtocol, dataBindingDesc,
			g11nOptions, transportHeaders, cookies, messageHeaders, attachments, operationDesc);
	}


	public Collection<Object> getMessageHeadersAsJavaObject() throws ServiceException {

		if (m_javaObjectMsgHeaders != null)
			return m_javaObjectMsgHeaders;

		// java object nodes message headers not created. Create now.
		Collection<ObjectNode> messageHeaders = getMessageHeaders();
		if (messageHeaders != null) {
			m_javaObjectMsgHeaders = new ArrayList<Object>();
			// convert message headres from DOM to java object nodes
			for (ObjectNode node : messageHeaders) {
				Object headerObject = getMessageHeaderObject(node);

				if (headerObject == null) {
					// If the header object returned is null, don't add it
					// to the list.
					continue;
				}
				m_javaObjectMsgHeaders.add(headerObject);
			}

		}
		return m_javaObjectMsgHeaders;
	}


	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getMessageHeader()
	 */
	public Collection<ObjectNode> getMessageHeaders() throws ServiceException {
		if (m_hasParamsByRef) {
			// This is the skip serialization case.
			// In this case,  there is only the java object as message
			// body, THe message headers is null. Return null.
			m_messageHeaderCompleted = true;
			return null;
		}
		if (m_messageHeaderCompleted) {
			return m_messageHeaders;
		}

		ProtocolProcessor pp = getContextImpl().getProtocolProcessor();
		if (!pp.supportsHeaders()) {
			m_messageHeaderCompleted = true;
			return null;
		}

		if (null == m_root) {
			createRootNode();
		}

		if (m_root == ObjectNodeImpl.EMPTY_ROOT_NODE) {
			m_messageHeaderCompleted = true;
			return null;
		}

		m_messageHeaders = pp.getMessageHeaders(m_root);
		m_messageHeaderCompleted = true;
		return m_messageHeaders;
	}

	private Object getMessageHeaderObject(ObjectNode object) throws ServiceException {
		DeserializerFactory deserFactory = getDataBindingDesc().getDeserializerFactory();
		XMLStreamReader reader = deserFactory.getXMLStreamReader(this, object);
		Deserializer deser = deserFactory.getDeserializer();
		QName name = object.getNodeName();
		Class javaType = getJavaTypeFromXMLName(getHeaderParamDesc(), name);
		// If javaType of a given header element is not found, the header element is
		// unexpected.  Return null to ignor it.
		if (null == javaType) {
			return null;
		}
		Object obj = deser.deserialize(this, javaType, reader);
		return obj;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseMessageImpl#getMessageBody(),
	 * org.ebayopensource.turmeric.runtime.common.pipeline.Message#getMessageBody()
	 */
	@Override
	public ObjectNode getMessageBody() throws ServiceException {
		if (m_deserCompleted) {
			return super.getMessageBody();
		}
		if (null == m_root) {
			createRootNode();
		}
		if (m_root == ObjectNodeImpl.EMPTY_ROOT_NODE) {
			return null;
		}
		ProtocolProcessor pp = getContextImpl().getProtocolProcessor();
		return pp.getMessageBody(m_root);
	}

	private synchronized void createRootNode() throws ServiceException {
		try {
			if (null != m_root) {
				return;
			}

			// make sure streams are OK
			getXMLStreamReader();

			if (!(m_xmlReader instanceof ObjectNodeBuilder)) {
				m_root = ObjectNodeImpl.createEmptyRootNode();
				return;
			}
			ObjectNodeBuilder builder = (ObjectNodeBuilder)m_xmlReader;
			m_root = builder.getObjectNode();
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {e.getMessage()}), e);
		}
	}

	public ObjectNode getRootNode() throws ServiceException {
		if (null == m_root) {
			try {
				createRootNode();
			} catch (Exception e) {
				//e.printStackTrace(); // Not a SOAP message
			}
		}

		return m_root;
	}

	public final boolean isErrorMessage() throws ServiceException {
		if (m_errorResponse != null) {
			return true;
		}

		return hasErrorTransportHeader();
	}

	/**
	 * Returns true if SOA X-TURMERIC-ERROR-RESPONSE header is present
	 */
	private boolean hasErrorTransportHeader() throws ServiceException {
		if (!m_hasErrorHeaderChecked) {
			m_hasErrorHeader = hasTransportHeader(SOAHeaders.ERROR_RESPONSE);
			m_hasErrorHeaderChecked = true;
		}

		return m_hasErrorHeader;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseMessageImpl#loadParamsImpl()
	 */
	@Override
	protected void loadParamsImpl() throws ServiceException {
		if (m_deserCompleted) {
			return;
		}

		// TODO: should we disallow param access in case of errors ?

		if (m_noStream) {
			// no data and no error
			m_deserCompleted = true;
			return;
		}

		try {
			loadParamsInt();
		} finally {
			// make sure further deserialization is not possible
			m_deserCompleted = true;
			m_xmlReader = null;
		}
	}

	private void loadParamsInt() throws ServiceException {
		BaseMessageContextImpl ctx = getContextImpl();

		long startTime = System.nanoTime();
		try {
			DeserializerFactory deserFactory = getDataBindingDesc().getDeserializerFactory();
			Deserializer deser = deserFactory.getDeserializer();

			if (hasErrorTransportHeader()) {
				Class javaType = getErrorParamType();
				if (hasExpectedErrorElement()) {
					// found expected error element. Continue deserialization
					m_errorResponse = deser.deserialize(this, javaType);

				} else {
					// found unknown error element. Consume the element and
					// return the root element inside message body as an object node
					m_errorResponse = consumeMessageBodyRootElement(deser, javaType);
				}
				if (m_errorResponse == null ) {
					// unable to deserialize the error response
					throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_NULL_ERROR_DESERIALIZE,
							ErrorConstants.ERRORDOMAIN));
				}
			} else {

				List<Class> paramTypes = getParamTypes();

				if (!paramTypes.isEmpty()) {
					m_params = new Object[paramTypes.size()];
					for (int i=0; i<paramTypes.size(); i++) {
						Class paramType = paramTypes.get(i);
						Object value = deser.deserialize(this, paramType);
						m_params[i] = value;
						processQueryParameters(ctx.getQueryParams(), paramType, value);	
					}
				}

				// TODO: throw exception if there is more data in the stream past protocol processor
				//       which would indicate too many parameters passed in
			}

			ProtocolProcessor pp = ctx.getProtocolProcessor();
			pp.postDeserialize(this);
		} finally {
			long duration = System.nanoTime() - startTime;
			ctx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_DESERIALIZATION, startTime, duration);
		}

		// TODO: get m_body and make sure changes
		// are synchronized between params and body
	}


	// determine whether the reader contains a common.types.ErrorMessage.
	// This method should be called after calling hasExpectedErrorElement()
	private boolean isCommonTypesError() throws ServiceException {

		if (m_xmlReader != null) {
			if (m_xmlReader.getName().equals(SOAConstants.COMMON_ERROR_MESSAGE_ELEMENT_NAME)) {
				// found expected error element
				return true;
			}
		}
		return false;
	}

	// determine whether the reader contains an unexpected error element
	private boolean hasExpectedErrorElement() throws ServiceException {
		getXMLStreamReader();
		if (!(m_xmlReader instanceof ObjectNodeBuilder)) {
			return false;
		}

		// get the expected error element name
		QName errElementName = getExpectedErrorElementQName();
		if (errElementName == null)
			return false;

		if (m_xmlReader != null) {
			if (m_xmlReader.getName().equals(ObjectNodeImpl.ROOT_NODE_QNAME)) {
				// if it is at root, consumes the root to get the child
				try {
					m_xmlReader.next();
				} catch (XMLStreamException e) {
					throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR,
							ErrorConstants.ERRORDOMAIN));
				}
			}
			if (m_xmlReader.getName().equals(errElementName)) {
				// found expected error element
				return true;
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseMessageImpl#cleanupData()
	 */
	@Override
	protected void cleanupData() {
		super.cleanupData();
		m_inputStream = null;
		m_xmlReader = null;
		m_noStream = false;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage#setInputStream(java.io.InputStream)
	 */
	public void setInputStream(InputStream is) throws ServiceException {
		if (is == null) {
			throw new NullPointerException();
		}

		if (hasContext() && getContext().isOutboundRawMode()) {
			if (m_byteBuffer == null) { // We are operating in streaming mode
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				int n;
				try {
					while ((n = is.read(buf)) != -1) {
						baos.write(buf, 0, n);
					}
					m_byteBuffer = ByteBuffer.wrap(baos.toByteArray());
				} catch (IOException e) {
					// add error to the context if we could not read the stream
					ServiceException e2 = new ServiceException(
							ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_CANNOT_READ_FROM_STREAM,
							ErrorConstants.ERRORDOMAIN, new Object[] {e.toString()}), e);
					getContext().addError(e2);
				}

				if (m_byteBuffer == null) { // At this point we must have a byte buffer
					throw new IllegalStateException("ByteBuffer in InboundMessageImpl cannot be null when OutboundRawMode is true");
				}
				processInboundHeaders();
				return;
			}
		}

		checkNotCleaned();

		if (m_inputStream != null) {
			throw new IllegalStateException("InputStream is already set on InboundMessageImpl");
		}

		if (m_hasParamsByRef) {
			throw new IllegalStateException("InputStream cannot be set after setting params by reference");
		}

		if (m_noStream) {
			throw new IllegalStateException("InboundMessageImpl is in no-stream mode");
		}

		InboundMessageAttachments attachments = InboundMessageAttachments.createInboundAttachments(is, this);
		if (null != attachments) {
			m_attachments = attachments;
			InputStream rootInputStream = m_attachments.getInputStreamForMasterMessage();
			is = rootInputStream;
		}
		m_inputStream = is;

		startRecording();

		if (hasAttachment()) {
			((InboundMessageAttachments)m_attachments).setInputStream(m_inputStream);
		}
		processInboundHeaders();
	}

	private void processInboundHeaders() throws ServiceException {
		if (!hasContext()) {
			return;
		}

		Map<String,String> transportHeaders = getTransportHeadersNoCopy();
		getContextImpl().processInboundHeaders(transportHeaders);
	}

	public void setParamReferences(Object[] params) throws ServiceException {
		if (params == null) {
			throw new NullPointerException();
		}

		checkNotCleaned();

		if (m_inputStream != null) {
			throw new IllegalStateException("InputStream is already set on InboundMessageImpl");
		}

		// TODO - deal with errors - getParamTypes does not work for errors
		if (getParamTypes().size() != params.length) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_PARAM_COUNT_MISMATCH,
					ErrorConstants.ERRORDOMAIN, new Object[] {Integer.valueOf(getParamTypes().size()), Integer.valueOf(params.length)}));
		}

		if (params.length > 0) {
			m_params = new Object[params.length];
		}

		for (int i = 0; i < params.length; i++) {
			setParamInt(i, params[i]);
		}

		m_hasParamsByRef = true;
		m_deserCompleted = true;

		processInboundHeaders();
	}

	public void setErrorResponseReference(Object errorResponse) throws ServiceException {
		if (errorResponse == null) {
			throw new NullPointerException();
		}

		checkNotCleaned();

		if (m_inputStream != null) {
			throw new IllegalStateException("InputStream is already set on InboundMessageImpl");
		}

		Class errorJavaType = getErrorParamType();

		if (!errorJavaType.isAssignableFrom(errorResponse.getClass())) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_INCOMPATIBLE_SET,
					ErrorConstants.ERRORDOMAIN, new Object[] {errorResponse.getClass().getName(), errorJavaType.getName()}));
		}

		m_errorResponse = errorResponse;

		m_hasParamsByRef = true;
		m_deserCompleted = true;

		processInboundHeaders();
	}

	public void setErrorResponse(Object errorResponse) throws ServiceException {
		if (errorResponse == null) {
			throw new NullPointerException();
		}

		m_errorResponse = errorResponse;
	}

	public Object getErrorResponseInternal() throws ServiceException {
		return m_errorResponse;
	}

	public void recordPayload(int maxBytes) throws ServiceException {
		checkNotCleaned();

		if (maxBytes > m_maxBytesToRecord) {
			m_maxBytesToRecord = maxBytes;

			if (m_inputStream != null) {
				startRecording();
			}
		}
	}

	private void startRecording() {
		if (m_maxBytesToRecord == 0 || m_recordingStream != null) {
			// already recording or no need to record
			return;
		}

		m_recordingStream = new PrereadingRawDataRecorder(m_inputStream, m_maxBytesToRecord);
		m_inputStream = m_recordingStream;
	}

	public byte[] getRecordedData() throws ServiceException {
		if (m_recordingStream == null) {
			return null;
		}

		try {
			return m_recordingStream.getRawByteData();
		} catch (IOException e) {
			// add error to the context as we could not read the stream
			ServiceException e2 = new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_CANNOT_READ_FROM_STREAM,
					ErrorConstants.ERRORDOMAIN, new Object[] {e.toString()}), e);
			getContext().addError(e2);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage#unableToProvideStream()
	 */
	public void unableToProvideStream() {
		if (m_noStream || hasCleanedData()) {
			return;
		}

		if (m_inputStream != null && m_hasParamsByRef) {
			// a failure happened after input stream was set..
			// let's not fail and continue with exception processing
			return;
		}

		m_noStream = true;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage#getXMLStreamReader()
	 */
	public XMLStreamReader getXMLStreamReader() throws ServiceException {
		if (m_xmlReader != null) {
			return m_xmlReader;
		}

		checkNotCleaned();

		if (!isRequestMessage()) {  // for client inbound message
			checkExectedPayload();
		}

		if (m_deserCompleted) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_DESER_COMPLETE,
					ErrorConstants.ERRORDOMAIN));
		}

		if (m_hasParamsByRef) {
			throw new IllegalStateException("Stream Reader cannot be used after setting params by reference");
		}

		if (m_inputStream == null) {
			throw new IllegalStateException("InputStream is not set on InboundMessageImpl");
		}

		DeserializerFactory deserFactory = getDataBindingDesc().getDeserializerFactory();

		List<Class> paramTypes;
		if (hasErrorTransportHeader()) {
			paramTypes = getErrorParamTypeList();
		} else {
			paramTypes = getParamTypes();
		}

		XMLStreamReader reader = deserFactory.getXMLStreamReader(this, paramTypes, m_inputStream);
		String msgProtocol = getContextImpl().getMessageProtocol();
		try {
			if (SOAConstants.MSG_PROTOCOL_SOAP_11.equals(msgProtocol)
					|| SOAConstants.MSG_PROTOCOL_SOAP_12.equals(msgProtocol)) {
				m_xmlReader = new SOAPObjectNodeStreamReader(reader);
			} else {
				m_xmlReader = new ObjectNodeStreamReader(reader);
			}

		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_DATA_XML_STREAM_READER_CREATION_ERROR,
					ErrorConstants.ERRORDOMAIN,
					new Object[] { getPayloadType() }), e);
		}

		return m_xmlReader;
	}

	private void checkExectedPayload() throws ServiceException {
		String actualPayloadTypeName = getTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT);
		String expectedPayloadTypeName = getPayloadType();
		if (actualPayloadTypeName != null &&
			!actualPayloadTypeName.equalsIgnoreCase(expectedPayloadTypeName))
		{
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_UNEXPECTED_SERVER_RESPONSE_FORMAT,
					ErrorConstants.ERRORDOMAIN, new Object[] {actualPayloadTypeName, expectedPayloadTypeName}));
		}
	}

	private QName getExpectedErrorElementQName() throws ServiceException {
		ServiceOperationDesc opDesc = getOperationDesc();
		ServiceOperationParamDesc errParamDesc = opDesc.getErrorType();
		QName errElementName = errParamDesc.getXmlNameForJavaType(errParamDesc.getRootJavaTypes().get(0));
		return errElementName;
	}

	private ObjectNode consumeMessageBodyRootElement(Deserializer deser, Class javaType) throws ServiceException {
		ObjectNode rootElement = null;
		List<ObjectNode> childNodesList = null;
		try {
			// get the root child of the Message Body
			childNodesList = getMessageBody().getChildNodes();
			if (childNodesList != null && childNodesList.size() > 0) {
				rootElement = childNodesList.get(0);
			}
			// At the end, force deserialization here to advance the reader
			deser.deserialize(this, javaType);
			return rootElement;
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {e.getMessage()}), e);
		}
	}

	/**
	 * This method transforms any aliased headers present per configuration
	 *
	 * @throws ServiceException
	 */
	public void doHeaderMapping() throws ServiceException {
		processInboundHeaders();
	}

	public Class getInputStreamClass() {
	    return m_inputStream == null ? null : m_inputStream.getClass();
	}

	private Object processQueryParameters(Map<String, String> queryParams, Class<?> paramType, Object toReturn) {
		if(queryParams == null || queryParams.isEmpty()) { 
			return toReturn;					
		}
		Set<Map.Entry<String, String>> entries = queryParams.entrySet();
		
		for(Map.Entry<String, String> e: entries) {
			setValue(e.getKey(), e.getValue(), toReturn, paramType);
		}		

		return toReturn;
	}
	
	private void setValue(String key, String value, Object obj, Class<?> type) {
		
		Field[] fields = type.getDeclaredFields();
		
		for(Field field: fields) {
			field.setAccessible(true);
		}		
		int index = key.indexOf(".");		
		try {
			if(index > -1) {
				String fname = key.substring(0, index);
				Field f = type.getDeclaredField(fname);
				f.setAccessible(true);
				setValue(key.substring(index+1), value, f.get(obj), f.getType());
			}				
			else {	
				Field f = type.getDeclaredField(key);
				f.setAccessible(true);
				setFieldValue(obj, f, value);
			}			
		} catch (Exception ignored) {			
			// Ignore the exception
			// Exceptions may occur for arrays and Maps.
			// Collections are not supported in SOA 2.8
		}
	}
	
	private void setFieldValue(Object o, Field f, String v) 
	throws IllegalArgumentException, IllegalAccessException {
		Object val  = adaptType(v, f.getType(), f.getName(), o);		
		if(val != null) {
			f.set(o, val);
		}
	}
	
	@SuppressWarnings("unchecked")
    private static <T> T adaptType(String from, Class<T> to, String field, Object obj) {
        T returnObject = null;
        
        if (to == String.class) {
            returnObject = (T) from;
        } else if (to == Integer.class || to == int.class) {
            returnObject = (T) Integer.valueOf(from);
        } else if (to == Long.class || to == long.class) {
            returnObject = (T) Long.valueOf(from);
        } else if (to == boolean.class || to == Boolean.class) {
            returnObject = (T) Boolean.valueOf(from);
        } else if (to == Short.class || to == short.class) {
            returnObject = (T) Short.valueOf(from);
        } else if (to == Double.class || to == double.class) {
            returnObject = (T) Double.valueOf(from);
        } else if (to == Float.class || to == float.class) {
            returnObject = (T) Float.valueOf(from);
        } else if (to == Byte.class || to == byte.class) {
            returnObject = (T) Byte.valueOf(from);
        } else if (to == Character.class || to == char.class) {
            returnObject = (T) Character.valueOf(from.charAt(0));
        }
        return returnObject;
    }


}
