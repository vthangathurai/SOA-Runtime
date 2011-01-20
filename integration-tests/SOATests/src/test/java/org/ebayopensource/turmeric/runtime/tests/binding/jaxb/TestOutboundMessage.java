/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.Serializer;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;

/**
 * Attachment not supported.
 * 
 * @author wdeng
 */
public class TestOutboundMessage implements OutboundMessage {

    MessageContext m_ctx;
    private Object m_transportData;
    private DataBindingDesc m_dbDesc;
    private ServiceOperationParamDesc m_paramDesc;
    private Object[] m_objs = new Object[1];
    private G11nOptions m_g11n = new G11nOptions();
    private ByteBuffer m_byteBuffer;

    public TestOutboundMessage(DataBindingDesc dbDesc, ServiceOperationParamDesc paramDesc) {
        m_dbDesc = dbDesc;
        m_paramDesc = paramDesc;
    }

    @Override
    public DataBindingDesc getDataBindingDesc() {
        return m_dbDesc;
    }

    @Override
    public ServiceOperationParamDesc getParamDesc() {
        return m_paramDesc;
    }

    @Override
    public Map<String, String> buildOutputHeaders() throws ServiceException {
        return null;
    }

    @Override
    public void serialize(OutputStream out) throws ServiceException {
        try {
            List<Class> paramTypes = new ArrayList<Class>();
            paramTypes.add(MyMessage.class);

            SerializerFactory serFactory = getDataBindingDesc().getSerializerFactory();
            Serializer ser = serFactory.getSerializer();
            XMLStreamWriter writer = serFactory.getXMLStreamWriter(this, paramTypes, out);
            writer.writeStartDocument();
            Object obj = m_objs[0];
            Class objectClz = MyMessage.class;
            if (null != obj) {
                objectClz = obj.getClass();
            }
            QName xmlName = getParamDesc().getXmlNameForJavaType(objectClz);
            ser.serialize(this, obj, xmlName, objectClz, writer);
            writer.writeEndDocument();
            writer.flush();
        }
        catch (XMLStreamException e) {
            throw new ServiceException(ErrorUtils.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
                            ErrorConstants.ERRORDOMAIN), e);
        }
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
        return m_objs[0];
    }

    @Override
    public int getParamCount() {
        if (getParam(0) != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public String getPayloadType() {
        return null;
    }

    @Override
    public String getTransportHeader(String name) {
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
    public void setCookie(Cookie cookie) throws ServiceException {
    }

    public void setMessageHeaderNode(ObjectNode header) throws ServiceException {
    }

    public void setMessageHeaderElement(String name, String value) throws ServiceException {
    }

    @Override
    public void setParam(int idx, Object value) throws ServiceException {
        m_objs[0] = value;
    }

    @Override
    public void setTransportHeader(String name, String value) throws ServiceException {
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
    public void setErrorResponse(Object error) {
    }

    @Override
    public void addDataHandler(DataHandler dh, String id) {
    }

    @Override
    public boolean hasAttachment() {
        return false;
    }

    @Override
    public void setG11nOptions(G11nOptions options) {
    }

    @Override
    public void recordPayload(int maxBytes) throws ServiceException {
    }

    @Override
    public byte[] getRecordedData() throws ServiceException {
        return null;
    }

    @Override
    public boolean isREST() {
        return false;
    }

    public void setREST(boolean isREST) {
    }

    @Override
    public String getUnserializableReason() {
        return null;
    }

    @Override
    public boolean isUnserializable() {
        return false;
    }

    @Override
    public void setUnserializable(String reason) {
    }

    @Override
    public int getMaxURLLengthForREST() {
        return 0;
    }

    @Override
    public void addMessageHeader(ObjectNode header) throws ServiceException {
    }

    @Override
    public void serializeHeader(XMLStreamWriter out) throws ServiceException {
    }

    @Override
    public void serializeBody(OutputStream out) throws ServiceException {
    }

    @Override
    public ServiceOperationParamDesc getHeaderParamDesc() throws ServiceException {
        return null;
    }

    @Override
    public void addMessageHeaderAsJavaObject(Object headerJavaObject) throws ServiceException {
    }

    @Override
    public int getTransportErrorResponseIndicationCode() throws ServiceException {
        return HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    @Override
    public List<Class> getParamTypes() throws ServiceException {
        return null;
    }
}
