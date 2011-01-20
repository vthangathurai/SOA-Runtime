/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;

/**
 * Attachment not supported.
 * 
 * @author wdeng
 */
public class TestInboundMessage implements InboundMessage {

    boolean m_ordered;
    MessageContext m_ctx;
    private Object m_transportData;
    private DataBindingDesc m_dbDesc;
    private ServiceOperationParamDesc m_paramDesc;
    private InputStream m_inputStream;
    private ByteBuffer m_byteBuffer;
    private G11nOptions m_g11n = new G11nOptions();
    private XMLStreamReader m_streamReader;
    private Class m_rootClz;

    TestInboundMessage(Class rootClz, boolean ordered, DataBindingDesc dbDesc, ServiceOperationParamDesc paramDesc,
                    InputStream inputStream) {
        m_ordered = ordered;
        m_dbDesc = dbDesc;
        m_paramDesc = paramDesc;
        m_inputStream = inputStream;
        m_rootClz = rootClz;
    }

    @Override
    public DataBindingDesc getDataBindingDesc() {
        return m_dbDesc;
    }

    @Override
    public ServiceOperationParamDesc getParamDesc() {
        return m_paramDesc;
    }

    public Class getRootClass() {
        return m_rootClz;
    }

    @Override
    public void setInputStream(InputStream is) throws ServiceException {
    }

    public void setXMLStreamReader(XMLStreamReader reader) {
        m_streamReader = reader;
    }

    @Override
    public ObjectNode getMessageBody() {
        return null;
    }

    @Override
    public MessageContext getContext() {
        return m_ctx;
    }

    public void setContext(MessageContext ctx) {
        m_ctx = ctx;
    }

    @Override
    public Cookie getCookie(String name) {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return null;
    }

    @Override
    public G11nOptions getG11nOptions() {
        return m_g11n;
    }

    @Override
    public Collection<ObjectNode> getMessageHeaders() {
        return null;
    }

    public String getMessageHeaderElement(String name) {
        return null;
    }

    public Map<String, String> getMessageHeaderElements() {
        return null;
    }

    @Override
    public Object getParam(int idx) {
        return null;
    }

    @Override
    public int getParamCount() {
        return 0;
    }

    @Override
    public String getPayloadType() {
        return null;
    }

    @Override
    public String getTransportHeader(String name) {
        if (SOAHeaders.ELEMENT_ORDERING_PRESERVE.equals(name)) {
            return String.valueOf(m_ordered);
        }
        return null;
    }

    @Override
    public boolean hasTransportHeader(String name) {
        return false;
    }

    @Override
    public Map<String, String> getTransportHeaders() {
        return null;
    }

    @Override
    public String getTransportProtocol() {
        return null;
    }

    public boolean hasMessageHeader() {
        return false;
    }

    @Override
    public boolean isErrorMessage() {
        return false;
    }

    @Override
    public Object getErrorResponse() {
        return null;
    }

    @Override
    public Object getErrorResponseInternal() throws ServiceException {
        return null;
    }

    @Override
    public ObjectNode getRootNode() throws ServiceException {
        return null;
    }

    @Override
    public void setCookie(Cookie cookie) throws ServiceException {
    }

    public void setMessageHeaderNode(ObjectNode header) throws ServiceException {
    }

    public void setMessageHeaderElement(String name, String value) throws ServiceException {
    }

    @Override
    public void setParam(int idx, Object value) throws ServiceException {
    }

    @Override
    public void setTransportHeader(String name, String value) throws ServiceException {
    }

    @Override
    public XMLStreamReader getXMLStreamReader() throws ServiceException {
        if (null == m_streamReader) {
            List<Class> paramTypes = new ArrayList<Class>();
            paramTypes.add(m_rootClz);
            m_streamReader = m_dbDesc.getDeserializerFactory().getXMLStreamReader(this, paramTypes, m_inputStream);
        }
        return m_streamReader;
    }

    public boolean supportsMessageHeader() {
        return false;
    }

    @Override
    public Object getTransportData() {
        return m_transportData;
    }

    @Override
    public void setTransportData(Object data) {
        m_transportData = data;
    }

    @Override
    public ByteBuffer getByteBuffer() throws ServiceException {
        return m_byteBuffer;
    }

    @Override
    public void setByteBuffer(ByteBuffer buffer) throws ServiceException {
        m_byteBuffer = buffer;
    }

    @Override
    public void unableToProvideStream() {
    }

    @Override
    public DataHandler getDataHandler(String cid) {
        return null;
    }

    @Override
    public boolean hasAttachment() {
        return false;
    }

    @Override
    public void recordPayload(int maxBytes) throws ServiceException {
    }

    @Override
    public byte[] getRecordedData() throws ServiceException {
        return null;
    }

    @Override
    public void setParamReferences(Object[] params) throws ServiceException {
    }

    @Override
    public void setErrorResponseReference(Object errorResponse) throws ServiceException {
    }

    @Override
    public void setErrorResponse(Object errorResponse) throws ServiceException {
    }

    @Override
    public ServiceOperationParamDesc getHeaderParamDesc() throws ServiceException {
        return null;
    }

    @Override
    public void addMessageHeader(ObjectNode header) throws ServiceException {
    }

    @Override
    public Collection<Object> getMessageHeadersAsJavaObject() throws ServiceException {
        return null;
    }

    @Override
    public List<Class> getParamTypes() throws ServiceException {
        return null;
    }

    @Override
    public void doHeaderMapping() throws ServiceException {
    }
}
