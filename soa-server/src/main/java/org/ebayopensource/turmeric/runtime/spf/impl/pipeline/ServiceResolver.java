/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.NameValue;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.NumericServiceVersion;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.pipeline.RequestMetaContext;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;


import com.ebay.kernel.util.StringUtils;

public class ServiceResolver {
    private final RequestMetaContext m_metaCtx;
    private List<Throwable> m_errors;
    private ServerServiceDesc m_serviceDesc;
    private static final String EMPTY = "";

    public ServiceResolver(RequestMetaContext metaCtx) {
        m_metaCtx = metaCtx;
    }

    public ServerServiceDesc lookupServiceDesc() throws ServiceException {
        if (m_serviceDesc != null) {
            return m_serviceDesc;
        }

        String adminName = m_metaCtx.getRequiredAdminName();
        // adminName not null means service with its own end point and servlet-mapping
        if (adminName != null) {
            try {
                m_serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc(adminName);
            }
            catch (ServiceException e) {
            	Throwable cause = e;
            	String causedBy = "";
            	while(cause != null) {
            		causedBy = "\nCaused by: " + cause.getMessage();
            		cause = cause.getCause();
            	}
                StringBuilder error = new StringBuilder();
                error.append("Unable to lookup ServiceDesc for required admin name ");
                error.append(adminName).append(" in request " );
                error.append(getRequestUriForLog()).append(" due to ").append(e.toString());
				logError(e, error);
				CommonErrorData errorData = ErrorDataFactory
						.createErrorData(
								ErrorConstants.SVC_RT_NO_SERVICE_DESC_FOR_ADMIN_NAME,
								ErrorConstants.ERRORDOMAIN,
								new Object[] { adminName + causedBy });
				addError(new ServiceException(errorData));
				m_serviceDesc = createFallbackServiceDesc(adminName);
				return m_serviceDesc;
            }
        }

        Map<String,String> transportHeaders = m_metaCtx.getTransportHeaders();
        String serviceNameInHeader = transportHeaders.get(SOAHeaders.SERVICE_NAME);
        String versionInHeader = transportHeaders.get(SOAHeaders.VERSION);
        if (serviceNameInHeader == null) {
            if (m_serviceDesc != null) {
                return m_serviceDesc;
            }
            m_serviceDesc = getServiceDescFromUrlMatchExpression();
            if (m_serviceDesc == null) {
                StringBuilder error = new StringBuilder();
                error.append("Service name not specified in request ");
                error.append(getRequestUriForLog());
                logError(error);
                addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_NO_SERVICE_NAME,
                        ErrorConstants.ERRORDOMAIN)));
                m_serviceDesc = createFallbackServiceDesc("NO SERVICE");
            }
            return m_serviceDesc;
        }

        QName serviceQName = convertServiceNameToQName(serviceNameInHeader);

        if (m_serviceDesc != null) {
            ServerServiceId serviceId = m_serviceDesc.getServiceId();
            if (!m_serviceDesc.getServiceQName().equals(serviceQName) &&
                    !serviceNameInHeader.equals(serviceId.getServiceName())) {
                StringBuilder error = new StringBuilder();
                error.append("Unsupported service name in request ");
                error.append(getRequestUriForLog()).append(". Actual serviceQName, while expected ");
                error.append(m_serviceDesc.getServiceQName());
                logError(error);
                addError(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_UNSUPPORTED_SERVICE_NAME,
                        ErrorConstants.ERRORDOMAIN, new Object[] {serviceQName.toString()})));
                m_serviceDesc = createFallbackServiceDesc(serviceNameInHeader);
                return m_serviceDesc;
            }
            if (!isVersionSupported(versionInHeader, serviceId.getVersion())) {
                ServiceException servExp = new ServiceException(
                        ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_VERSION_UNSUPPORTED,
                                ErrorConstants.ERRORDOMAIN, new Object[] { versionInHeader }));
                StringBuilder error = new StringBuilder().append("Unsupported service version in request ");
                error.append(getRequestUriForLog()).append(". Actual Version, while expected ");
                error.append(m_serviceDesc.getServiceQName());
                logError(error);
                addError(servExp);
            }
            return m_serviceDesc;
        }
        adminName = ServerServiceDescFactory.getInstance().findKnownAdminNameByQName(serviceQName);
        if (adminName == null) {
            adminName = serviceQName.getLocalPart();
        }
        try {
            m_serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc(adminName);
        }
        catch (ServiceException e) {
            // this has been logged by factory already, so INFO level should be good enough
            StringBuilder error = new StringBuilder();
            error.append("Unable to obtain ServiceDesc for '");
            error.append(adminName).append("' in request ").append(getRequestUriForLog());
            logError(error);
            addError(e);
            m_serviceDesc = createFallbackServiceDesc(adminName);
        }
        return m_serviceDesc;
    }

    private ServerServiceDesc getServiceDescFromUrlMatchExpression() throws ServiceException {
        String adminName;
        String urlMatchExpression = m_metaCtx.getUrlMatchExpression();
        if (urlMatchExpression != null) {
            adminName = extractServiceNameFromUrl(urlMatchExpression);
            if (adminName != null) {
                try {
                    m_serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc(adminName);
                }
                catch (ServiceException e) {
                    StringBuilder error = new StringBuilder();
                    error.append("Unable to lookup ServiceDesc for Admin Name ");
                    error.append(adminName).append(" in request ");
                    error.append(getRequestUriForLog()).append(" due to ").append(e.toString());
                    logError(e, error);
                }
                if (m_serviceDesc != null) {
                    try {
                        checkUrlMatchExpressionConflicts(m_serviceDesc.getAdminName(), urlMatchExpression);
                    }
                    catch (ServiceException e) {
                        m_serviceDesc = null;
                        addError(e);
                    }
                }
            }
        }
        return m_serviceDesc;
    }

    private void logError(StringBuilder error) {
        getLogger().log(Level.FINE, error.toString());
    }

    private void logError(ServiceException e, StringBuilder error) {
        getLogger().log(Level.FINE, error.toString(), e);
    }

    /*
     * detect conflicts in which the same mapping spec, such as path[2], is used
     * to infer different metadata values, such as both service name, and operation name.
     */
    private void checkUrlMatchExpressionConflicts(String adminName, String urlMatchExpression)
    throws ServiceException {
        ServiceConfigHolder config = (ServiceConfigHolder)m_serviceDesc.getConfig();
        List<NameValue> nameValues = config.getHeaderMappingOptions().getOption();
        for (int i = 0; i < nameValues.size(); i++) {
            NameValue nameValue = nameValues.get(i);
            if (urlMatchExpression.equalsIgnoreCase(nameValue.getValue())) {
                throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_URL_MATCH_EXPRESSION_CONFLICT,
                        ErrorConstants.ERRORDOMAIN, new Object[] {adminName, urlMatchExpression}));
            }
        }
    }

    private String extractServiceNameFromUrl(String urlMatchExpression) throws ServiceException {
        String serviceName = null;

        if (urlMatchExpression.startsWith("query[")) {
            String indexval = urlMatchExpression.substring(6, urlMatchExpression.length()-1);
            serviceName = getServiceNameFromQuery(indexval);
        } else if (urlMatchExpression.equals("queryop")) {
            serviceName = getServiceNameFromQueryOp();
        } else if (urlMatchExpression.startsWith("path[")) {
            String indexval = urlMatchExpression.substring(5, urlMatchExpression.length()-1);
            Integer pathIndex = Integer.valueOf(indexval);
            serviceName = getServiceNameFromURIPath(pathIndex.intValue());
        }

        return serviceName;
    }

    private String getServiceNameFromURIPath(int pathIndex) {
        //http://localhost/ws/spf/json/test1?myNonArgOperation&ver=1.0.0
        String requestUri = m_metaCtx.getRequestUri();
        List<String> pathParts = StringUtils.splitStr(requestUri, '/');
        int i = 0;
        pathIndex++;
        for (String part: pathParts) {
            if (i++ == pathIndex) {
                return part;
            }
        }

        return null;
    }

    private String getServiceNameFromQueryOp() {
        //http://localhost/ws/spf/json?test1&ver=1.0.0
        Set<String> keys = m_metaCtx.getQueryParams().keySet();
        Iterator<String> keysItr = keys.iterator();
        if (keysItr.hasNext()) {
            return keysItr.next();
        }

        return null;
    }

    private String getServiceNameFromQuery(String indexval) {
        //http://localhost/ws/spf/json?myNonArgOperation&SERVICE-NAME=test1&ver=1.0.0
        Set<String> keys = m_metaCtx.getQueryParams().keySet();
        Iterator<String> keysItr = keys.iterator();
        while(keysItr.hasNext()) {
            String entryName = keysItr.next();
            if (indexval.equals(entryName)) {
                return m_metaCtx.getQueryParams().get(entryName);
            }
        }

        return null;
    }

    private static QName convertServiceNameToQName(String str) {
        // TODO: support namespaces, JavaDoc says this format can change
        QName qname = QName.valueOf(str);
        return ServiceNameUtils.normalizeQName(qname);
    }

    public List<Throwable> getErrors() {
        return m_errors;
    }

    private ServerServiceDesc createFallbackServiceDesc(String adminName) throws ServiceException {
        QName serviceQName = new QName(EMPTY, adminName, EMPTY);
        return ServerServiceDescFactory.getInstance().createFallbackServiceDesc(serviceQName);
    }

    private void addError(Throwable th) {
        if (m_errors == null) {
            m_errors = new ArrayList<Throwable>();
        }
        m_errors.add(th);
    }

    private static Logger getLogger() {
        return LogManager.getInstance(ServiceResolver.class);
    }

    private String getRequestUriForLog() {
        return "'" + m_metaCtx.getRequestUri() + "'";
    }

    private boolean isVersionSupported(String versionInHeader, String versionSupported) {
        if(versionInHeader != null) {
            NumericServiceVersion versionPassed = NumericServiceVersion.valueOf(versionInHeader);
            if (versionPassed != null) {
                NumericServiceVersion version = NumericServiceVersion.valueOf(versionSupported);
                // Check for major version only
                return version.getMajorVersion() == versionPassed.getMajorVersion();
            }
        }
        return true;
    }
}
