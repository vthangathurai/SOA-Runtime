/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ServiceLayerType;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;


/**
 * @author wdeng
 */
public class TestMessageContext implements MessageContext {

    TestInboundMessage m_in;
    TestOutboundMessage m_out;
    ServerServiceDesc m_svcDesc;
    ArrayList<Throwable> m_errors = new ArrayList<Throwable>();
    ArrayList<Throwable> m_warnings = new ArrayList<Throwable>();
    boolean m_inboundRawMode;
    boolean m_outboundRawMode;

    TestMessageContext(Class topLevelObjClz, TestInboundMessage in, TestOutboundMessage out,
                    SerializerFactory serFactory, DeserializerFactory deserFactory) throws Exception {
        this.m_in = in;
        m_in.setContext(this);
        this.m_out = out;
        m_out.setContext(this);
        m_svcDesc = TestServiceDesc.createTestDesc(topLevelObjClz, serFactory, deserFactory);
    }

    @Override
    public ServiceId getServiceId() {
        return m_svcDesc.getServiceId();
    }

    @Override
    public void addError(Throwable t) {
        m_errors.add(t);
    }

    @Override
    public void addWarning(Throwable t) {
        m_warnings.add(t);
    }

    @Override
    public Object getAuthenticatedUser() {
        return null;
    }

    @Override
    public List<Throwable> getErrorList() {
        return m_errors;
    }

    @Override
    public List<Throwable> getWarningList() {
        return m_warnings;
    }

    @Override
    public MessageProcessingStage getProcessingStage() {
        return null;
    }

    @Override
    public String getMessageProtocol() {
        return null;
    }

    @Override
    public ServiceOperationDesc getOperation() {
        return null;
    }

    @Override
    public String getOperationName() {
        return null;
    }

    @Override
    public ServiceAddress getServiceAddress() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public String getRequestId() {
        return null;
    }

    @Override
    public String getRequestGuid() {
        return null;
    }

    @Override
    public String getRequestUri() {
        return null;
    }

    @Override
    public void setRequestId(String requestId, String requestGuid) {
    }

    @Override
    public Message getRequestMessage() {
        return m_in;
    }

    @Override
    public Message getResponseMessage() {
        return m_out;
    }

    @Override
    public String getServiceLayer() {
        return ServiceLayerType.BUSINESS.getValue();
    }

    @Override
    public ServiceContext getServiceContext() {
        try {
            return ServerServiceDescFactory.getInstance().getServiceContext(m_svcDesc);
        }
        catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QName getServiceQName() {
        return null;
    }

    @Override
    public String getAdminName() {
        return null;
    }

    public int getStatus() {
        return 0;
    }

    public Transport getTransport() {
        return null;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public void setAuthenticatedUser(Object user) {
    }

    public void setErrorList(List<Throwable> list) {
    }

    @Override
    public void setProperty(String name, Object value) throws ServiceException {
    }

    public void setStatus(int status) {
    }

    @Override
    public Charset getEffectiveCharset() {
        return m_in.getG11nOptions().getCharset();
    }

    @Override
    public String getInvokerVersion() {
        return null;
    }

    @Override
    public ServiceAddress getClientAddress() {
        return null;
    }

    @Override
    public String getServiceVersion() {
        return "1.0";
    }

    @Override
    public SecurityContext getSecurityContext() {
        return null;
    }

    @Override
    public boolean isInboundRawMode() {
        return m_inboundRawMode;
    }

    @Override
    public void setInboundRawMode(boolean mode) {
        m_inboundRawMode = mode;
    }

    @Override
    public boolean isOutboundRawMode() {
        return m_outboundRawMode;
    }

    @Override
    public void setOutboundRawMode(boolean mode) {
        m_outboundRawMode = mode;
    }

    @Override
    public boolean isElementOrderPreserved() {
        return false;
    }

    @Override
    public Charset getCharset() {
        return null;
    }

    @Override
    public String getDefaultNamespace() {
        return null;
    }

    @Override
    public String getSingleNamespace() {
        return null;
    }

    @Override
    public Map<String, List<String>> getNamespaceToPrefixMap() {
        return null;
    }

    @Override
    public String getNsForJavaType(Class type) {
        return null;
    }

    @Override
    public String getPayloadType() {
        return null;
    }

    @Override
    public Map<String, String> getPrefixToNamespaceMap() {
        return null;
    }

    @Override
    public Class getRootClass() {
        return null;
    }

    @Override
    public DataElementSchema getRootElementSchema() {
        return null;
    }

    @Override
    public QName getRootXMLName() {
        return null;
    }

    @Override
    public ITypeConversionContext getTypeConversionContext() {
        return null;
    }

    @Override
    public boolean isREST() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void addResponseResidentError(CommonErrorData arg0) {
    }

    @Override
    public List<CommonErrorData> getResponseResidentErrorList() {
        return null;
    }

    @Override
    public boolean hasResponseResidentErrors() {
        return false;
    }

    @Override
    public ErrorDataProvider getErrorDataProvider() throws ServiceException {
        throw new UnsupportedOperationException();
    }
}
