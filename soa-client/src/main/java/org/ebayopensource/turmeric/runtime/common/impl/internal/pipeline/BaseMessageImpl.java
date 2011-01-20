/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * Abstract base internal class, representing a unit of data being sent or received by the SOA framework.
 * 
 * BaseMessageImpl provides all base functionality required to keep and change the data. 
 * However, it does not define the source of the data, or the serialization mechanisms.
 * 
 * @author ichernyshev
 */
public abstract class BaseMessageImpl implements Message {

	private static final Cookie[] EMPTY_COOKIES = new Cookie[0];

	private BaseMessageContextImpl m_context;
	private final boolean m_isRequestMessage;
	private final String m_transportProtocol;
	private final DataBindingDesc m_dataBindingDesc;
	private final ServiceOperationDesc m_operationDesc;
	private boolean m_paramsLoaded;
	private boolean m_cleanedData;
	private Map<String,String> m_transportHeaders;
	private Map<String,Cookie> m_cookies;
	private Object m_transportData;
	protected Collection<ObjectNode> m_messageHeaders;
	protected ByteBuffer m_byteBuffer;

	// On server side, we will change the g11n options in request pipeline
	// g11n handler.
	protected G11nOptions m_g11nOptions;
	protected Object[] m_params;
	protected Object m_errorResponse;
	protected BaseMessageAttachments m_attachments;

	/**
	 * Internal constructor to be called by the derived classes.
	 * @param isRequestMessage
	 * @param transportProtocol
	 * @param dataBindingDesc
	 * @param g11nOptions
	 * @param transportHeaders
	 * @param cookies
	 * @param attachments
	 * @param operationDesc
	 */
	protected BaseMessageImpl(boolean isRequestMessage,
		String transportProtocol,
		DataBindingDesc dataBindingDesc, G11nOptions g11nOptions,
		Map<String,String> transportHeaders, Cookie[] cookies,
		Collection<ObjectNode> messageHeaders,
		BaseMessageAttachments attachments, ServiceOperationDesc operationDesc)
		throws ServiceException
	{
		if (transportProtocol == null || dataBindingDesc == null ||
			g11nOptions == null || operationDesc == null)
		{
			throw new NullPointerException();
		}

		m_isRequestMessage = isRequestMessage;
		m_transportProtocol = transportProtocol;
		m_dataBindingDesc = dataBindingDesc;
		m_g11nOptions = g11nOptions;

		m_transportHeaders = transportHeaders;
		m_messageHeaders = messageHeaders;
		if (m_messageHeaders == null) {
			m_messageHeaders = new ArrayList<ObjectNode>();
		}

		if (cookies != null) {
			for (int i=0; i<cookies.length; i++) {
				setCookie(cookies[i]);
			}
		}

		m_attachments = attachments;
		m_operationDesc = operationDesc;
	}

	/*
	 * Reset state in order to allow memory to be freed for the message.
	 */
	protected void cleanupData() {
		m_cleanedData = true;
		m_params = null;
		m_errorResponse = null;
	}

	final void setContext(BaseMessageContextImpl context) {
		// this method is called from the context constructor,
		// which means context is not fully initialized yet
		// do not use context here !!!
		if (context == null) {
			throw new NullPointerException();
		}

		if (m_context != null) {
			throw new IllegalStateException();
		}

		m_context = context;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getContext()
	 */
	public final MessageContext getContext() {
		return getContextImpl();
	}

	/**
	 * Internal function to get the framework-level message context information.
	 * @return the full internal version of the (base class) message context.
	 */
	protected final BaseMessageContextImpl getContextImpl() {
		if (m_context == null) {
			throw new IllegalStateException();
		}

		return m_context;
	}

	protected final boolean hasContext() {
		return m_context != null;
	}

	/**
	 * Returns true if this message is a request message (outbound client request or inbound service request).
	 * @return whether this is a request message
	 */
	protected final boolean isRequestMessage() {
		return m_isRequestMessage;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getPayloadType()
	 */
	public final String getPayloadType() throws ServiceException {
		return m_dataBindingDesc.getPayloadType();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getTransportProtocol()
	 */
	public final String getTransportProtocol() throws ServiceException {
		return m_transportProtocol;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getDataBindingDesc()
	 */
	public final DataBindingDesc getDataBindingDesc() throws ServiceException {
		return m_dataBindingDesc;
	}

	/**
	 * Get the configuration, such as request, response, and error message types, for the message's associated service operation.
	 * @return the operation-specific configuration
	 */
	protected final ServiceOperationDesc getOperationDesc() {
		return m_operationDesc;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getParamDesc()
	 */
	public final ServiceOperationParamDesc getParamDesc() throws ServiceException {
		if (isErrorMessage()) {
			return m_operationDesc.getErrorType();
		}

		if (isRequestMessage()) {
			return m_operationDesc.getRequestType();
		}

		return m_operationDesc.getResponseType();
	}
	

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getHeaderParamDesc()
	 */
	public final ServiceOperationParamDesc getHeaderParamDesc() throws ServiceException {
		if (isRequestMessage()) {
			return m_operationDesc.getRequestHeaders();
		}

		return m_operationDesc.getResponseHeaders();
	}
	
	/**
	 * Internal check function; throws an exception if the data in the message is cleaned (state is released).
	 * @throws ServiceException
	 */
	protected final void checkNotCleaned() throws ServiceException {
		if (m_cleanedData) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_DATA_CLEANED,
					ErrorConstants.ERRORDOMAIN));
		}
	}

	/**
	 * Internal function to test if the message is cleaned (state is released).
	 * @return true if the message is cleaned
	 */
	protected final boolean hasCleanedData() {
		return m_cleanedData;
	}

	/**
	 * Internal function; check not cleaned, and load parameters, if not loaded already.
	 * @throws ServiceException
	 */
	protected final void checkParams() throws ServiceException {
		checkNotCleaned();

		if (!m_paramsLoaded) {
			loadParamsImpl();
			m_paramsLoaded = true;
		}
	}

	/**
	 * Internal function; set the parameters loaded state to true.
	 * @throws ServiceException
	 */
	protected final void markAsLoaded() throws ServiceException {
		checkNotCleaned();
		m_paramsLoaded = true;
	}

	/**
	 * Internal function; abstract function to trigger actual loading of parameter information from the stream
	 * within inbound messages.
	 * @throws ServiceException
	 */
	protected abstract void loadParamsImpl() throws ServiceException;

	/**
	 * Return the list of expected Java types (classes) for this message's arguments, in operation argument
	 * order.  Note that a response message may contain either a output arguments or an error response, but
	 * only the output argument types are given here.
	 * 
	 * WARNING: this does not return the Error Types!
	 * 
	 * @return the list of expected java types
	 */
	public final List<Class> getParamTypes() throws ServiceException {
		List<Class> paramTypes;
		if (isRequestMessage()) {
			paramTypes = m_operationDesc.getRequestType().getRootJavaTypes();
		} else {
			paramTypes = m_operationDesc.getResponseType().getRootJavaTypes();
		}

		return paramTypes;
	}

	/**
	 * Return the error response type for this message.
	 * @return a list containing the single error response type (always).
	 */
	protected final List<Class> getErrorParamTypeList() {
		return m_operationDesc.getErrorType().getRootJavaTypes();
	}

	/**
	 * Returns the single error response type for this message.
	 * @return the single error response type
	 */
	protected final Class getErrorParamType() {
		return m_operationDesc.getErrorType().getRootJavaTypes().get(0);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getParamCount()
	 */
	public final int getParamCount() throws ServiceException {
		checkParams();
		return (m_params != null ? m_params.length : 0);
	}

	/**
	 * Verify that the specified index is in range for this message, or throw an exception otherwise.
	 * @param idx the index to be validated
	 * @throws ServiceException
	 */
	private void checkParamAccess(int idx) throws ServiceException {
		checkParams();

		if (m_errorResponse != null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_ERROR_RESPONSE,
					ErrorConstants.ERRORDOMAIN));
		}

		if (m_params == null || m_params.length == 0) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_ACCESS_VOID_DATA, 
					ErrorConstants.ERRORDOMAIN));
		}

		if (idx < 0 || idx >= m_params.length) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_ACCESS_WRONG_IDX,
					ErrorConstants.ERRORDOMAIN, new Object[] {Integer.toString(idx), Integer.toString(m_params.length+1)}));
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getParam(int)
	 */
	public final Object getParam(int idx) throws ServiceException {
		checkParamAccess(idx);
		return m_params[idx];
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#setParam(int, java.lang.Object)
	 */
	public final void setParam(int idx, Object value) throws ServiceException {
		checkParamAccess(idx);
		setParamInt(idx, value);
	}
	
	/**
	 * Internal function to set a parameter on the message.
	 * @param idx the index of the paremeter to be set
	 * @param value the parameter value
	 * @throws ServiceException
	 */
	protected final void setParamInt(int idx, Object value) throws ServiceException {
		if (value != null) {
			// no need to check index as we're in parallel arrays
			List<Class> paramTypes = getParamTypes();
			Class<?> clazz = paramTypes.get(idx);

			if (!clazz.isAssignableFrom(value.getClass())) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_MSG_INCOMPATIBLE_SET,
						ErrorConstants.ERRORDOMAIN, new Object[] {value.getClass().getName(), clazz.getName()}));
			}
		}

		m_params[idx] = value;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getErrorResponse()
	 */
	public final Object getErrorResponse() throws ServiceException {
		checkParams();
		return m_errorResponse;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getG11nOptions()
	 */
	public final G11nOptions getG11nOptions() throws ServiceException {
		return m_g11nOptions;
	}
	
	public void addMessageHeader(ObjectNode header) throws ServiceException {
		if (null == header) {
			return;
		}
		m_messageHeaders.add(header);
	}
		
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getTransportHeaders()
	 */
	public final Map<String,String> getTransportHeaders() throws ServiceException {
		if (m_transportHeaders == null) {
			return CollectionUtils.EMPTY_STRING_MAP;
		}

		// we have to return a copy to avoid ConcurrentModification errors
		return Collections.unmodifiableMap(new HashMap<String,String>(m_transportHeaders));
	}

	/**
	 * Internal function providing write access to the message's transport headers.  Service and client writers
	 * should never call this.
	 * @return the internal header map
	 */
	protected final Map<String,String> getTransportHeadersNoCopy() throws ServiceException {
		return m_transportHeaders;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getTransportHeader(java.lang.String)
	 */
	public final String getTransportHeader(String name) throws ServiceException {
		if (m_transportHeaders != null && name != null) {
			String value = m_transportHeaders.get(name);
			if(value != null){
				return value;
			}
			name = SOAHeaders.normalizeName(name, true);
			return m_transportHeaders.get(name);
		}

		return null;
	}

	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#hasTransportHeader(java.lang.String)
	 */
	public final boolean hasTransportHeader(String name) throws ServiceException {
		if (m_transportHeaders != null && name != null) {
			if(m_transportHeaders.containsKey(name)) {
				return true;
			}
			name = SOAHeaders.normalizeName(name, true);
			return m_transportHeaders.containsKey(name);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#setTransportHeader(java.lang.String, java.lang.String)
	 */
	public final void setTransportHeader(String name, String value) throws ServiceException {
		if (name == null) {
			throw new NullPointerException();
		}

		name = SOAHeaders.normalizeName(name, true);
		value = SOAHeaders.normalizeValue(name, value);

		if (m_transportHeaders == null) {
			m_transportHeaders = new HashMap<String,String>();
		}

		m_transportHeaders.put(name, value);
		if (hasAttachment()) {
			m_attachments.transportHeaderAdded(name, value);
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#setCookie(Cookie)
	 */
	public final void setCookie(Cookie cookie) throws ServiceException {
		if (m_cookies == null) {
			m_cookies = new HashMap<String,Cookie>();
		}

		m_cookies.put(cookie.getName(), cookie);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getCookie(java.lang.String)
	 */
	public final Cookie getCookie(String name) throws ServiceException {
		if (name != null && m_cookies != null) {
			return m_cookies.get(name.toUpperCase());
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getCookies()
	 */
	public final Cookie[] getCookies() throws ServiceException {
		if (m_cookies == null) {
			return EMPTY_COOKIES;
		}

		Cookie[] result = m_cookies.values().toArray(new Cookie[m_cookies.size()]);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getByteBuffer()
	 */
	public ByteBuffer getByteBuffer() throws ServiceException {
		return m_byteBuffer;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#setByteBuffer(java.nio.ByteBuffer)
	 */
	public void setByteBuffer(ByteBuffer buffer) throws ServiceException {
		m_byteBuffer = buffer;
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getTransportData()
	 */
	public Object getTransportData() throws ServiceException {
		return m_transportData;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#setTransportData(java.lang.Object)
	 */
	public void setTransportData(Object data) throws ServiceException {
		m_transportData = data;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#getMessageBody()
	 */
	public ObjectNode getMessageBody() throws ServiceException {
		List<DataElementSchema> responseElements = getParamDesc().getRootElements();
		if (responseElements == null || responseElements.isEmpty()) {
			return null;
		}

		QName responseName = responseElements.get(0).getElementName();
		if (getParamCount() == 0) {
			return null;
		}
		Object object = getParam(0);
		
		if (null == object) {
			return null;
		}
		return new JavaObjectNodeImpl(responseName, object);
	}
		
	/**
	 * TODO - this should apparently be in InboundMessageImpl, not BaseMessageImpl.  This matches functionality
	 * declared in InboundMessage.
	 * Get the data handler for this message, from which attachment streaming can occur.
	 * @param cid the attachment identifier.
	 * @return the data handler
	 */
	public DataHandler getDataHandler(String cid) throws ServiceException {
		if (null == m_attachments) {
			return null;
		}
		return m_attachments.getDataHandler(cid);
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Message#hasAttachment()
	 */
	public boolean hasAttachment() throws ServiceException {
		return m_attachments != null;
	}


	protected QName getMessageXMLName(ServiceOperationParamDesc paramDesc, Class clz) throws ServiceException {
		QName xmlName = paramDesc.getXmlNameForJavaType(clz);
		if (null == xmlName) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_SERIALIZATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {"mapped XML element name not found for class " + clz.getName()} ));
		}
		return xmlName;
	}

	protected Class getJavaTypeFromXMLName(ServiceOperationParamDesc paramDesc, QName eleName) throws ServiceException {
		Class javaType = paramDesc.getJavaTypeForXmlName(eleName);
		return javaType;
	}
	
	public void resetContext()
	{
		m_context = null;
	}
}
