/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H

/**
 * Change Activity:
 *
 * Reason      Name             Date           Description
 * ----------------------------------------------------------------------------
 * SOA 2.3     pkaliyamurthy    03/08/2009     Better System Error Classification.
 *
 */

package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;

import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.exception.DataValidationErrorException;
import org.ebayopensource.turmeric.runtime.binding.exception.DataValidationWarningException;
import org.ebayopensource.turmeric.runtime.binding.impl.TypeConversionContextImpl;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.TypeConversionAdapter;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MetricsRegistrationHelper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs.OpLevelMetricDef;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs.SvcLevelMetricDef;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.LoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ErrorDataLoggingRegistry;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;
import org.ebayopensource.turmeric.runtime.common.types.ByteBufferWrapper;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;

import com.ctc.wstx.exc.WstxParsingException;




/**
 * Base implementation class of the message context.
 *
 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext
 * @author ichernyshev
 */
public abstract class BaseMessageContextImpl<D extends ServiceDesc, C extends ServiceContext>
                implements MessageContext {

	private static final Logger LOGGER = LogManager.getInstance(BaseMessageContextImpl.class);

    private final D m_serviceDesc;

    private final C m_serviceCtx;

    protected ProtocolProcessorDesc m_protocolProcessor;

    private final Transport m_transport;

    private final BaseMessageImpl m_requestMessage;

    private final BaseMessageImpl m_responseMessage;

    private final ServiceOperationDescImpl m_operation;

    private final ServiceAddress m_clientAddress;

    private final ServiceAddress m_serviceAddress;

    protected Map<String, Object> m_systemProperties;

    private MessageProcessingStage m_processingStage;

    private Object m_authenticatedUser;

    private List<CommonErrorData> m_responseResidentErrors;

    private List<Throwable> m_errors;

    private List<Throwable> m_warnings;

    protected Map<String, Object> m_userProperties;

    private String m_requestId;

    private String m_requestGuid;

    private final String m_serviceVersion;

    private final Charset m_effectiveCharset;

    protected SecurityContext m_securityContext;

    private final String m_requestUri;

    private final String m_serviceLayer;

    private boolean m_inboundRawMode;

    private boolean m_outboundRawMode;

    private AsyncMessageContextImpl m_asynMessageContext;

    private IAsyncResponsePoller m_servicePoller;

    private List<Object> m_outParams;

    private ByteBufferWrapper m_outWrapper;
    
    protected Map<String, String> m_queryParams;

    /**
     * Internal constructor to be called by the derived classes.
     *
     * @param serviceDesc
     *                the service description (processed configuration and
     *                associated extension classes and state) for the service
     *                which is currently being invoked
     * @param operation
     *                the processed configuration of the operation which is
     *                currently being invoked
     * @param protocolProcessor
     *                the processed configuration and class name of the protocol
     *                processor which is currently operating on the invocation
     * @param transport
     *                the processed configuration and class name of the
     *                transport being used to send and receive message data
     * @param requestMessage
     *                the current request message
     * @param responseMessage
     *                the current response
     * @param clientAddress
     *                the ServiceAddress of the invoking client
     * @param serviceAddress
     *                the ServiceAddress of the service endpoint instance
     * @param systemProperties
     *                a map holding any object state required for the duration
     *                of the service processing; state data is held until
     *                processing completes, even in asynchronous processing
     * @param serviceVersion
     *                the version of the service which is being invoked
     * @param effectiveCharset
     *                the character set in which the client or service is
     *                configured to process information; deserialization occurs
     *                into this target character set
     */
    protected BaseMessageContextImpl(D serviceDesc, ServiceOperationDesc operation, ProtocolProcessorDesc protocolProcessor,
            Transport transport, BaseMessageImpl requestMessage, BaseMessageImpl responseMessage, ServiceAddress clientAddress,
            ServiceAddress serviceAddress, Map<String, Object> systemProperties, String serviceVersion,
            Charset effectiveCharset, String requestUri) throws ServiceException {

        // Check the input parameters
        Preconditions.checkNotNull(serviceDesc);
        Preconditions.checkNotNull(operation);
        Preconditions.checkNotNull(transport);
        Preconditions.checkNotNull(serviceAddress);

        m_serviceDesc = serviceDesc;
        m_operation = (ServiceOperationDescImpl) operation;
        m_protocolProcessor = protocolProcessor;
        m_transport = transport;
        m_requestMessage = requestMessage;
        m_responseMessage = responseMessage;
        m_clientAddress = clientAddress;
        m_serviceAddress = serviceAddress;
        m_systemProperties = systemProperties;
        m_effectiveCharset = effectiveCharset;
        m_requestUri = requestUri;

        m_processingStage = MessageProcessingStage.REQUEST_INIT;

        if (serviceDesc.getServiceId().isClientSide()) {
            @SuppressWarnings("unchecked")
            C serviceCtx = (C) BaseServiceDescFactory.getClientInstance().getServiceContext(serviceDesc);
            m_serviceCtx = serviceCtx;
        } else {
            @SuppressWarnings("unchecked")
            C serviceCtx = (C) BaseServiceDescFactory.getServerInstance().getServiceContext(serviceDesc);
            m_serviceCtx = serviceCtx;
        }
        /*
         * request message is not required for retrieve of Service Similarly,
         * response message is not required from invokeAsync method of Service
         */
        if (m_requestMessage != null)
            m_requestMessage.setContext(this);
        if (m_responseMessage != null)
            m_responseMessage.setContext(this);
        if (m_serviceDesc.isFallback()) {
            m_serviceLayer = null;
            m_serviceVersion = null;
        } else {
            m_serviceLayer = m_serviceDesc.getConfig().getMetaData().getLayer();
            if (null != serviceVersion) {
                m_serviceVersion = serviceVersion;
            } else {
                m_serviceVersion = m_serviceDesc.getConfig().getMetaData().getVersion();
            }
        }
    }

    /**
     * Returns the service description (processed configuration and associated
     * extension classes and state) for the currently operating service.
     *
     * @return the service description
     */
    public final D getServiceDesc() {
        return m_serviceDesc;
    }

    /**
     * Returns the service context
     *
     * @return service context.
     */
    public C getServiceContext() {
        return m_serviceCtx;
    }

    /**
     * Returns the admin name in the service description
     *
     * @return the adminName
     */
    public final String getAdminName() {
        return m_serviceDesc.getAdminName();
    }

    /**
     * Returns the service QName
     *
     * @return the service name
     */
    public final QName getServiceQName() {
        return m_serviceDesc.getServiceQName();
    }

    /**
     * Returns the operation
     *
     * @return a <code>ServiceOperationDescImpl</code> object representing the operation
     */
    public final ServiceOperationDescImpl getOperation() {
        return m_operation;
    }

    /**
     * Returns the operation name
     *
     * @return a <code>String</code>, operation name
     */
    public final String getOperationName() {
        return m_operation.getName();
    }

    /**
     * Returns the processed configuration and class name of the protocol
     * processor which is currently operating on the invocation
     *
     * @return the protocol processor configuration
     */
    public final ProtocolProcessor getProtocolProcessor() {
        return m_protocolProcessor.getProcessor();
    }

    /**
     * Sets the processed configuration and class name of the protocol processor
     * which is currently operating on the invocation
     */
    public final void setProtocolProcessor(ProtocolProcessorDesc processor) {
        m_protocolProcessor = processor;
    }

    /**
     * Returns the protocol processor name
     * which is currently operating on the invocation
     *
     * @return a <code>String</code> representing the message protocol
     */
    public final String getMessageProtocol() {
        return getProtocolProcessor().getMessageProtocol();
    }

    /**
     * Returns the processed configuration and class name of the transport being
     * used to send and receive message data
     *
     * @return the transport configuration
     */
    public final Transport getTransport() {
        return m_transport;
    }

    /**
     * Returns the client address
     *
     * @return the client address as a <code>ServiceAddress</code> object
     */
    public final ServiceAddress getClientAddress() {
        return m_clientAddress;
    }

    /**
     * Returns the service address
     *
     * @return the service address as a <code>ServiceAddress</code> object
     */
    public final ServiceAddress getServiceAddress() {
        return m_serviceAddress;
    }

    /**
     * Returns the current version of the service
     *
     * @return a <code>String</code> representing the service version
     */
    public final String getServiceVersion() {
        return m_serviceVersion;
    }

    /**
     * Returns the Charset.
     *
     * @return a <code>Charset</code>
     */
    public final Charset getEffectiveCharset() {
        return m_effectiveCharset;
    }

    /**
     * Returns the request URI.
     *
     * @return the URI <code>String</code>
     */
    public final String getRequestUri() {
        return m_requestUri;
    }

    /**
     * The Property associated with the parameter name.
     *
     * @param name the name of the property
     * @return an <code>Object</code>
     * @throws <code>NullPointerException</code>, if the <code>name</code> is null
     */
    public final Object getProperty(String name) {
        Preconditions.checkNotNull(name, "Property name can not be null");
        if (m_systemProperties != null && m_systemProperties.containsKey(name)) {
            return m_systemProperties.get(name);
        }
        if (name.startsWith(SOAConstants.CTX_PROP_PREFIX)) {
            return null;
        }
        if (m_userProperties != null) {
            return m_userProperties.get(name);
        }
        return null;
    }

    /**
     * Returns the service layer
     *
     * @return the service layer as a <code>String</code>
     */
    public String getServiceLayer() {
        return m_serviceLayer;
    }

    /**
     * Sets the property <name,value> pair
     *
     * @param name the property name
     * @param value the <code>Object</code> value for the property
     * @throws <code>ServiceException</code> if the property passed is a System property
     */
    public final void setProperty(String name, Object value) throws ServiceException {
        if ((m_systemProperties != null && m_systemProperties.containsKey(name))
                || (name.startsWith(SOAConstants.CTX_PROP_PREFIX))) {
        	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_CANNOT_SET_SYSTEM_PROP,
        			ErrorConstants.ERRORDOMAIN, new Object[] { name }));
        }
        if (m_userProperties == null) {
            m_userProperties = new HashMap<String, Object>();
        }
        m_userProperties.put(name, value);
    }

    /**
     * Internal method to set a SOA framework-specific property. Service writers
     * and consumers should never call this method.
     *
     * @param name    the name of the property
     * @param value   state information associated with the property
     */
    public final void setSystemProperty(String name, Object value) {
        // DO NOT make this method public on MessageContext interface
        if (m_systemProperties == null) {
            m_systemProperties = new HashMap<String, Object>();
        }
        m_systemProperties.put(name, value);
    }

    /**
     * Returns the request message
     *
     * @return a request <code>Message</code> object
     */
    public final Message getRequestMessage() {
        return m_requestMessage;
    }

    /**
     * Returns the response message
     *
     * @return a response <code>Message</code> object
     */
    public final Message getResponseMessage() {
        return m_responseMessage;
    }

    /**
     * Adds a RRE error. Logs the error to CAL and updates the metrics accordingly
     *
     * @param errorData the error data object to add.
     */
    public final void addResponseResidentError(CommonErrorData errorData) {
        Preconditions.checkNotNull(errorData);
        for (LoggingHandler loggingHandler : m_serviceDesc.getLoggingHandlers()) {
            try {
                loggingHandler.logResponseResidentError(this, errorData);
            } catch (Throwable loggingError) {
                logLoggingError(loggingError, errorData);
            }
        }
        addErrorMetricData(errorData);
        if (m_responseResidentErrors == null) {
            m_responseResidentErrors = new ArrayList<CommonErrorData>();
        }
        m_responseResidentErrors.add(errorData);
        ErrorDataLoggingRegistry.getInstance().remove(errorData);
    }

    /**
     * Adds the <code>Throwable</code>. Updates the metrics and the CAL info.
     *
     * @param t the Throwable to be added
     */
    public final void addError(Throwable t) {
    	Preconditions.checkNotNull(t, new ServiceRuntimeException(
    			ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_NULL_ERROR_DATA, ErrorConstants.ERRORDOMAIN)));
        t = validateNewError(t);
        for (LoggingHandler loggingHandler : m_serviceDesc.getLoggingHandlers()) {
            try {
				if (t instanceof DataValidationErrorException || t instanceof WstxParsingException) {
					loggingHandler.logWarning(this, t);
				} else {
                loggingHandler.logError(this, t);
				}             		 
            } catch (Throwable loggingError) {
                logLoggingError(loggingError, t);
            }
        }
        addErrorMetricData(t);
        if (m_errors == null) {
            m_errors = new ArrayList<Throwable>();
        }
        m_errors.add(convertToServiceException(t));
    }

    /**
     * Update the metric for the passed metric defintion
     *
     * @param def the metric definition
     * @param count the number that has to be added to the metric
     */
    public final void updateSvcMetric(SystemMetricDefs.SvcLevelMetricDef def, long count) {
        m_serviceDesc.updateMetric(this, def, count);
    }

    /**
     * Update the service and operation time metrics
     *
     * @param def the <code>OpLevelMetricDef</code> metric
     * @param startTime starting time
     * @param timeSpan the time span
     */
    public final void updateSvcAndOpMetric(OpLevelMetricDef def, long startTime, long timeSpan) {
        OpLevelMetricDef startTimeOpMetric = SystemMetricDefs.getStartTimeOperationMetrics(def);
        if (null != startTimeOpMetric) {
            Long processingStartTime = (Long) getProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED);
            if (null != processingStartTime) {
                long relativeStartTime = startTime - processingStartTime.longValue();
                m_serviceDesc.updateMetric(this, startTimeOpMetric.getSvcDef(), relativeStartTime);
                m_operation.updateMetric(this, startTimeOpMetric, relativeStartTime);
            }
        }
        m_serviceDesc.updateMetric(this, def.getSvcDef(), timeSpan);
        m_operation.updateMetric(this, def, timeSpan);
    }

    /**
     * Update the service and operation count metrics
     *
     * @param def the <code>OpLevelMetricDef</code> metric
     * @param count the count to increase
     */
    public final void updateSvcAndOpMetric(OpLevelMetricDef def, long count) {
        m_serviceDesc.updateMetric(this, def.getSvcDef(), count);
        m_operation.updateMetric(this, def, count);
    }

    /**
     * Update the service count metrics by 1
     *
     * @param def the <code>SvcLevelMetricDef</code> metric
     */
    public final void incrementSvcMetric(SvcLevelMetricDef def) {
        m_serviceDesc.updateMetric(this, def, 1);
    }

    /**
     * Update the service and operation count metrics by 1
     *
     * @param def the <code>OpLevelMetricDef</code> metric
     */
    public final void incrementSvcAndOpMetric(OpLevelMetricDef def) {
        m_serviceDesc.updateMetric(this, def.getSvcDef(), 1);
        m_operation.updateMetric(this, def, 1);
    }

    /**
     * Adds the warning. Logs the warning using the configured LoggingHandlers.
     *
     * @param t <code>Throwable</code> that has to be added
     */
    public final void addWarning(Throwable t) {
        Preconditions.checkNotNull(t);
        List<LoggingHandler> loggingHandlers = m_serviceDesc.getLoggingHandlers();
        for (LoggingHandler loggingHandler : loggingHandlers) {
            try {
                loggingHandler.logWarning(this, t);
            } catch (Throwable e2) {
                logLoggingError(e2, t);
            }
        }
        if (m_warnings == null) {
            m_warnings = new ArrayList<Throwable>();
        }
        m_warnings.add(convertToServiceException(t));
    }

    /**
     * Returns <code>true</code> if there are any RRE errors
     *
     * @return <code>true</code> if RRE exists, <code>false</code> otherwise
     */
    public boolean hasResponseResidentErrors() {
        return m_responseResidentErrors == null ? false : !m_responseResidentErrors.isEmpty();
    }

    /**
     * Return <code>true</code> if there are any errors
     *
     * @return a boolean value to indicate whether there are errors or not
     */
    public boolean hasErrors() {
        return m_errors == null ? false : !m_errors.isEmpty();
    }

    /**
     * Returns the list of warnings
     *
     * @return a List of <code>Throwable</code>s if there are warnings. A list of size 0 is returned, if there are no warnings
     */
    public final List<Throwable> getWarningList() {
        if (m_warnings != null) {
            return Collections.unmodifiableList(m_warnings);
        }
        return CollectionUtils.EMPTY_THROWABLE_LIST;
    }

    /**
     * Internal method to set the current list of warnings (exceptions in the
     * pipeline that were recovered via the continue-on-error feature)
     *
     * @param list
     *                the list of warnings
     */
    public final void setWarningList(List<Throwable> list) {
        m_warnings = list;
    }

    /**
     * Returns the list of RRE errors
     *
     * @return a unmodifiable list of RRE errors. A list of size 0 is returned, if there are no warnings
     */
    public final List<CommonErrorData> getResponseResidentErrorList() {
        if (m_responseResidentErrors != null) {
            return Collections.unmodifiableList(this.m_responseResidentErrors);
        }
        return Collections.unmodifiableList(new ArrayList<CommonErrorData>());
    }

    /**
     * Returns the list of Errors
     *
     * @return a unmodifiable list of errors. A list of size 0 is returned, if there are no warnings
     */
    public final List<Throwable> getErrorList() {
        if (m_errors != null) {
            return Collections.unmodifiableList(m_errors);
        }
        return CollectionUtils.EMPTY_THROWABLE_LIST;
    }

    /**
     * Returns the authenticated user
     *
     * @return an <code>Object</code> representing the user
     */
    public final Object getAuthenticatedUser() {
        return m_authenticatedUser;
    }

    /**
     * Sets the passed in user as the authenticated user
     *
     * @param user a user object to set.
     */
    public final void setAuthenticatedUser(Object user) {
        m_authenticatedUser = user;
    }

    /**
     * Internal method to change the current processing stage during pipeline
     * operation. Service writers or consumers should never call this method.
     *
     * @param value
     */
    public final void changeProcessingStage(MessageProcessingStage value) {
        Preconditions.checkNotNull(value);
        m_processingStage = value;
    }

    /**
     * Logs the processing stage for each of the Logging handlers configured.
     *
     * @param stage the Logging handler stage.
     */
    public final void runLoggingHandlerStage(LoggingHandlerStage stage) {
        List<LoggingHandler> loggingHandlers = m_serviceDesc.getLoggingHandlers();
        if (stage == LoggingHandlerStage.REQUEST_STARTED || stage == LoggingHandlerStage.BEFORE_REQUEST_DISPATCH
                || stage == LoggingHandlerStage.REQUEST_DISPATCH_START) {
            for (int i = 0; i < loggingHandlers.size(); i++) {
                LoggingHandler loggingHandler = loggingHandlers.get(i);
                try {
                    loggingHandler.logProcessingStage(this, stage);
                } catch (Throwable e) {
                	logLoggingError(e, "Processing stage " + stage + " for service "
                			+ getServiceQName() + ", operation " + getOperationName());
                }
            }
        } else {
            for (int i = loggingHandlers.size() - 1; i >= 0; i--) {
                LoggingHandler loggingHandler = loggingHandlers.get(i);
                try {
                    loggingHandler.logProcessingStage(this, stage);
                } catch (Throwable e) {
                	logLoggingError(e, "Processing stage " + stage + " for service "
                			+ getServiceQName() + ", operation " + getOperationName());
                }
            }
        }
    }

    /**
     * Internal method to reset the state of the request message and allow
     * freeing of associated memory. Service writers and consumers should never
     * call this method.
     */
    public final void cleanupRequestData() {
        m_requestMessage.cleanupData();
    }

    /**
     * Returns the current processing stage
     *
     * @return the processing stage
     */
    public final MessageProcessingStage getProcessingStage() {
        return m_processingStage;
    }

    /**
     * Returns the request id
     *
     * @return the request id as a <code>String</code>
     */
    public final String getRequestId() {
        return m_requestId;
    }

    /**
     * Returns the request GUID
     *
     * @return the request GUID as a <code>String</code>
     */
    public String getRequestGuid() {
        if (m_requestGuid == null && m_requestId != null) {
            // lazy parsing
            m_requestGuid = parseGuid(m_requestId);
        }
        return m_requestGuid;
    }

    /**
     * Sets the passed string as the request GUID
     *
     * @param guid a <code>String</code> to set as the request guid
     */
    public void setRequestGuid(String guid) {
        m_requestGuid = guid;
    }

    /**
     * Sets the request ID and GUID
     *
     * @param requestID the request ID
     * @param  requestGuid the request GUID
     */
    public final void setRequestId(String requestId, String requestGuid) throws ServiceException {

        // Preconditions.checkArgument(requestId != null && requestGuid != null, "Both request Id and request GUID cant be null at the same time")
        // TODO IS the logic correct?
        if (requestId == null && requestGuid == null) {
            throw new NullPointerException();
        }

        if (requestId == null) {
            // setting both ID and GUID to be the same as GUID
            m_requestId = requestGuid;
            m_requestGuid = requestGuid;
            return;
        }
        m_requestId = requestId;
        m_requestGuid = requestGuid;
    }

    /**
     * Returns the security context.
     * @return the <code>SecurityContext</code> object
     */
    public SecurityContext getSecurityContext() {
        return m_securityContext;
    }

    /**
     * Internal method to construct outbound meta-information such as HTTP
     * headers, for handover to the transport.
     *
     * @param customHeaders
     *                the map of header information from which to construct the
     *                outbound transport header map
     * @return the resulting transport header map
     * @throws ServiceException
     */
    protected abstract Map<String, String> buildOutputHeaders(Map<String, String> customHeaders) throws ServiceException;

    /**
     * Sub classes may override this method to process the inbound headers.
     *
     * @param transportHeaders
     * @throws ServiceException
     */
    protected void processInboundHeaders(Map<String, String> transportHeaders) throws ServiceException {
        // allow override
    }

    /**
     * Validation hook for the subclasses to extend.
     *
     * This implementation returns the Throwable that is passed.
     *
     * @param t the throwable that has to be validated.
     * @return the Throwable that has been validated.
     */
    protected Throwable validateNewError(Throwable t) {
        return t;
    }

    /**
     * Returns <code>true</code> if the inbound raw mode is set, <code>false</code> otherwise
     *
     * @return the inbound raw mode
     */
    public boolean isInboundRawMode() {
        return m_inboundRawMode;
    }

    /**
     * Sets the inbound raw mode
     *
     * @param mode a boolean value for the mode
     */
    public void setInboundRawMode(boolean mode) {
        m_inboundRawMode = mode;
    }

    /**
     * Returns <code>true</code> if the outbound raw mode is set, <code>false</code> otherwise
     *
     * @return the inbound raw mode
     */
    public boolean isOutboundRawMode() {
        return m_outboundRawMode;
    }

    /**
     * Sets the outbound raw mode
     *
     * @param mode a boolean value for the mode
     */
    public void setOutboundRawMode(boolean mode) {
        m_outboundRawMode = mode;
    }

    /**
     * Sets the client async handler
     *
     * @param handler a <code>AsyncHandler</code> to set
     */
    public void setClientAsyncHandler(AsyncHandler handler) {
        prepareAsyncMessageContext();
        m_asynMessageContext.setClientAsyncHandler(handler);
    }

    /**
     * Returns the Client asyc handler.
     *
     * @return the async handler if present, else <code>null</code>
     */

    public AsyncHandler getClientAsyncHandler() {
        return m_asynMessageContext == null ? null : m_asynMessageContext.getClientAsyncHandler();
    }

    /**
     * Returns the Future Object in the asyncmessageContext
     *
     * @return the Future object
     */
    public Future<?> getFutureResponse() {
        return m_asynMessageContext == null ? null : m_asynMessageContext.getFutureResponse();
    }

    /**
     * Sets the passed future object to the message context
     *
     * @param futureResponse the future response to set
     */
    public void setFutureResponse(Future<?> futureResponse) {
        prepareAsyncMessageContext();
        m_asynMessageContext.setFutureResponse(futureResponse);
    }

    /**
     * Returns the Executor associated with the Message context
     *
     * @return returns the <code>Executor</code>
     */
    public Executor getExecutor() {
        return m_asynMessageContext == null ? null : m_asynMessageContext.getExecutor();
    }

    /**
     * Sets the Executor in the Message Context
     *
     * @param executor an <code>Executor</code>to set
     */
    public void setExecutor(Executor executor) {
        prepareAsyncMessageContext();
        m_asynMessageContext.setExecutor(executor);
    }

    /**
     * Sets the Service asynchronous Call back in the Message Context
     *
     * @param callback the <code>AsyncCallBack</code> variable to set
     */
    public void setServiceAsyncCallback(AsyncCallBack callback) {
        prepareAsyncMessageContext();
        m_asynMessageContext.setServiceAsyncCallback(callback);
    }

    /**
     * Returns the  <code>AsyncCallBack</code> associated with the Message Context.
     *
     * @return the AsynCallBack object
     */
    public AsyncCallBack getServiceAsyncCallback() {
        return m_asynMessageContext == null ? null : m_asynMessageContext.getServiceAsyncCallback();
    }

    public String getPayloadType() {
        String payloadType = null;
        try {
            payloadType = getCurrentMessage().getPayloadType();
        } catch (ServiceException se) {
            // DataBindingDesc.getPayloadType() doesn't throw exceptions
        }
        return payloadType;
    }

    public String getDefaultNamespace() {
        return getServiceQName().getNamespaceURI();
    }

    public String getSingleNamespace() {
        return getServiceContext().getTypeMappings().getSingleNamespace();
    }

    public Map<String, List<String>> getNamespaceToPrefixMap() {
        ServiceTypeMappings typeMappings = getServiceContext().getTypeMappings();
        Map<String, List<String>> ns2Prefix = typeMappings.getNamespaceToPrefixesMap();
        return ns2Prefix;
    }

    public Map<String, String> getPrefixToNamespaceMap() {
        ServiceTypeMappings typeMappings = getServiceContext().getTypeMappings();
        Map<String, String> prefix2NS = typeMappings.getPrefixToNamespaceMap();
        return prefix2NS;
    }

    /**
     * Returns the char set
     * @return the char set
     */
    public Charset getCharset() {
        Charset c = null;
        try {
            c = getCurrentMessage().getG11nOptions().getCharset();
        } catch (ServiceException se) {
            // Shouldn't throw an exception
        }
        return c;
    }

    public String getNsForJavaType(Class javaType) {
        return getServiceContext().getTypeMappings().getNsForJavaType(javaType);
    }

    public Class getRootClass() {
        List<Class> paramTypes = null;
        try {
            paramTypes = getCurrentMessage().getParamDesc().getRootJavaTypes();
        } catch (ServiceException se) {
            // Shouldn't throw an exception
        }

        Class topElementClass = paramTypes == null || paramTypes.size() == 0 ? null : paramTypes.get(0);
        return topElementClass;
    }

    public ITypeConversionContext getTypeConversionContext() {
        DataBindingDesc dbDesc = null;
        try {
            dbDesc = getCurrentMessage().getDataBindingDesc();
        } catch (ServiceException se) {
            // Simple getter, doesn't really throw anything
        }

        ITypeConversionContext tcCtxt = null;
        if (dbDesc != null) {
            tcCtxt = new TypeConversionContextImpl(dbDesc.getAllTypeConverterBoundTypes(), dbDesc
                    .getAllTypeConverterValueTypes(), TypeConversionAdapter.class);
        }
        return tcCtxt;
    }

    private ServiceOperationParamDesc getParamDesc() {
        ServiceOperationParamDesc paramDesc = null;
        try {
            paramDesc = getCurrentMessage().getParamDesc();
        } catch (ServiceException se) {
            // getParamDesc() doesn't throw any exceptions
        }
        return paramDesc;
    }

    public QName getRootXMLName() {
        QName rootXmlName = getRootClass() == null ? null : getParamDesc().getXmlNameForJavaType(getRootClass());
        return rootXmlName;
    }

    public DataElementSchema getRootElementSchema() {
        DataElementSchema rootSchema = getRootClass() == null ? null : getParamDesc().getRootElements().get(0);
        return rootSchema;
    }

    public boolean isREST() {
        Message m = getCurrentMessage();
        if (m instanceof OutboundMessage) {
            return ((OutboundMessage) m).isREST();
        }
        return false;
    }

    public boolean isElementOrderPreserved() {
        Message msg = getCurrentMessage();
        if (null == msg) {
            return true;
        }
        try {
            String headerString = msg.getTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE);
            if (null == headerString) {
                return true;
            }
            return Boolean.parseBoolean(headerString);
        } catch (ServiceException se) {
            return true;
        }
    }

    public Message getCurrentMessage() {
        return getProcessingStage().isRequestDirection() ? getRequestMessage() : getResponseMessage();
    }

    private static Throwable convertToServiceException(Throwable t) {
        if (t instanceof DataValidationWarningException) {
            DataValidationWarningException warning = (DataValidationWarningException) t;
            return new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_VALIDATION_WARNING, ErrorConstants.ERRORDOMAIN,
            		new Object[] {new Integer(warning.getRow()), new Integer(warning.getCol()), warning.getMessage() }), t.getCause());
        } else if (t instanceof DataValidationErrorException) {
            DataValidationErrorException error = (DataValidationErrorException) t;
            return new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_VALIDATION_ERROR, ErrorConstants.ERRORDOMAIN,
            		new Object[] { new Integer(error.getRow()), new Integer(error.getCol()), error.getMessage() }), t.getCause());
        }
        return t;
    }

    public IAsyncResponsePoller getServicePoller() {
        return m_servicePoller;
    }

    public void setServicePoller(IAsyncResponsePoller poller) {
        m_servicePoller = poller;
    }

    public void setOutParams(List<Object> outParams) {
        m_outParams = outParams;
    }

    public List<Object> getOutParams() {
        return m_outParams;
    }

    /**
     *
     * @param outWrapper
     */
    public void setOutBuffer(ByteBufferWrapper outWrapper) {
        m_outWrapper = outWrapper;
    }

    /**
     *
     * @return
     */
    public ByteBufferWrapper getOutBuffer() {
        return m_outWrapper;
    }

    /**
     * Returns the
     */
    public boolean isAsync() {
        return m_asynMessageContext != null;
    }

    protected void prepareAsyncMessageContext() {
        if (m_asynMessageContext == null) {
            m_asynMessageContext = new AsyncMessageContextImpl();
        }
    }

    public ErrorDataProvider getErrorDataProvider() {
        return m_serviceDesc.getErrorDataProviderClass();
    }
    
	public Map<String,String> getQueryParams() {
		return m_queryParams;
    }

    private String parseGuid(String id) {
        int delimiterIdx = id.indexOf('!');
        if (delimiterIdx >= 0) {
            return id.substring(0, delimiterIdx);
        }
        return id;
    }

    private void updateTotalErrorMetric() {
        incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_TOTAL);
    }

    private void updateErrorSeverityMetrics(final ErrorSeverity severity) {
        if (severity == ErrorSeverity.ERROR) {
            incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_SEVERITY_ERROR);
        } else if (severity == ErrorSeverity.WARNING) {
            incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_SEVERITY_WARNING);
        }
    }

    private void updateErrorCategoryMetrics(final CommonErrorData errorData) {
        try {
            ErrorCategory category = errorData.getCategory();
            if (category == ErrorCategory.APPLICATION) {
                // For Application category, return without updating the sub domain metric.
                incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_CAT_APPLICATION);
                return;
            } else if (category == ErrorCategory.SYSTEM) {
                incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_CAT_SYSTEM);
            } else if (category == ErrorCategory.REQUEST) {
                incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_CAT_REQUEST);
            }
            updateErrorSubdomainMetrics(errorData);
        } catch (Exception e) {
            logMetricUpdateError(e, errorData);
        }
    }

    private void addErrorMetricData(final CommonErrorData errorData) {
        Preconditions.checkNotNull(errorData);
        updateErrorCategoryMetrics(errorData);
        updateErrorSeverityMetrics(errorData.getSeverity());
        updateErrorGroupMetric(errorData);
        updateTotalErrorMetric();
    }

    private void updateErrorSubdomainMetrics(final CommonErrorData errorData) {
        MetricDef metric =
            MetricsRegistrationHelper.createSubDomainMetrics(errorData, getServiceId().isClientSide());
        if(metric != null) {
            boolean clientSide = m_serviceDesc.getServiceId().isClientSide();
            MetricValueAggregator valueAgg = null;
            if (clientSide) {
                valueAgg = MetricsCollector.getClientInstance().getMetricValue(((OpLevelMetricDef)metric).getSvcDef().getMetricName(),
                        m_serviceDesc.getServiceId(), null);
            } else {
                valueAgg = MetricsCollector.getServerInstance().getMetricValue(((OpLevelMetricDef)metric).getSvcDef().getMetricName(),
                        m_serviceDesc.getServiceId(), null);
            }
            if (valueAgg != null)
                valueAgg.update(1);
        }
    }

    private void updateErrorGroupMetric(CommonErrorData... errDatas) {
        List<SystemMetricDefs.OpLevelErrorMetricDef> groupMetricDefs = null;
        if (errDatas != null) {
            for (CommonErrorData errorData : errDatas) {
                try {
                    if (errorData != null) {
                        groupMetricDefs = MetricsRegistrationHelper.getGroupMetricDefs(errorData);
                        if (groupMetricDefs != null) {
                            for (SystemMetricDefs.OpLevelErrorMetricDef def : groupMetricDefs) {
                                updateErrorGroupMetric(def);
                            }
                        }
                        // Remove the reference in the
                        // MetricsRegistrationHelper, as we
                        // are done updating the metrics
                        MetricsRegistrationHelper.remove(errorData);
                    }
                } catch (Exception e) {
                    logMetricUpdateError(e, errorData);
                }
            }
        }
    }

    private void updateErrorGroupMetric(SystemMetricDefs.OpLevelErrorMetricDef def) {
        boolean clientSide = m_serviceDesc.getServiceId().isClientSide();
        MetricValueAggregator valueAgg = null;
        if (clientSide) {
            valueAgg = MetricsCollector.getClientInstance().getMetricValue(def.getSvcDef().getMetricName(),
                    m_serviceDesc.getServiceId(), null);
        } else {
            valueAgg = MetricsCollector.getServerInstance().getMetricValue(def.getSvcDef().getMetricName(),
                    m_serviceDesc.getServiceId(), null);
        }

        if (valueAgg != null)
            valueAgg.update(1);

        if (clientSide) {
            valueAgg = MetricsCollector.getClientInstance().getMetricValue(def.getMetricName(), m_serviceDesc.getServiceId(),
                    this.getOperationName());
        } else {
            valueAgg = MetricsCollector.getServerInstance().getMetricValue(def.getMetricName(), m_serviceDesc.getServiceId(),
                    this.getOperationName());
        }

        if (valueAgg != null)
            valueAgg.update(1);

    }

    private void addErrorMetricData(final Throwable t) {
        LoggingHandlerUtils utils = getLoggingHandlerUtils();
        // t has one or more errorData associated.
        List<CommonErrorData> errorDatas = utils.getErrorData(t);
        if (errorDatas != null) {
            for (final CommonErrorData errorData : errorDatas) {
                addErrorMetricData(errorData);
                ErrorDataLoggingRegistry.getInstance().remove(errorData);
            }
        }
    }

    protected abstract LoggingHandlerUtils getLoggingHandlerUtils();

    private void logMetricUpdateError(Throwable t, CommonErrorData errorData) {
    	if (LOGGER.isLoggable(Level.WARNING)) {
	        LOGGER.log(Level.WARNING,
	                "Error in Update of Error Category/Group Metrics '" + errorData + "'. " + t.toString());
    	}
    }

    private void logLoggingError(Throwable e, CommonErrorData errorData) {
    	if (LOGGER.isLoggable(Level.WARNING)) {
    		LOGGER.log(Level.WARNING,
                "Unable to log response resident error '" + errorData + "' due to error in logger: " + e.toString());
    	}
    }

    private void logLoggingError(Throwable e, Throwable originalEx) {
    	if (LOGGER.isLoggable(Level.WARNING)) {
    		LOGGER.log(Level.WARNING,
                "Unable to log exception '" + originalEx + "' due to error in logger: " + e.toString());
    	}
    }

    private void logLoggingError(Throwable e, String message) {
    	if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.log(Level.WARNING,
			        "Unable to log message '" + message + "' due to error in logger: " + e.toString(),
			        e);
		}
    }
}
