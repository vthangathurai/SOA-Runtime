/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigHolder;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientServiceConfigBeanManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientServiceInvokerConfigBean;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.security.ClientSecurityContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceContext;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;


import com.ebay.kernel.exception.BaseRuntimeException;

/**
 * Internal representation of the client-side message context.
 *
 */
public final class ClientMessageContextImpl extends
		ReducedClientMessageContextImpl implements ClientMessageContext {
	private final ServiceInvokerOptions m_invokerOptions;

	private final String m_responseTransport;

	private final String m_useCase;

	private final String m_consumerId;

	/**
	 * Constructor. Client and service writers should never call this method.
	 *
	 * @param serviceDesc
	 * @param operation
	 * @param protocolProcessor
	 * @param transport
	 * @param requestMessage
	 * @param responseMessage
	 * @param serviceAddress
	 * @param systemProperties
	 * @param serviceVersion
	 * @param invokerOptions
	 * @param responseTransport
	 * @param useCase
	 * @param requestCharset
	 * @param userAsync
	 */
	public ClientMessageContextImpl(ClientServiceDesc serviceDesc,
			ServiceOperationDesc operation,
			ProtocolProcessorDesc protocolProcessor, Transport transport,
			BaseMessageImpl requestMessage, BaseMessageImpl responseMessage,
			ServiceAddress serviceAddress,
			Map<String, Object> systemProperties, String serviceVersion,
			ServiceInvokerOptions invokerOptions, String responseTransport,
			String useCase, String consumerId, Charset requestCharset, String requestUri,
			boolean useAsync) throws ServiceException {
		super(serviceDesc, operation, protocolProcessor, transport,
				requestMessage, responseMessage, buildClientAddress(),
				serviceAddress, systemProperties, serviceVersion,
				requestCharset, requestUri, null, useAsync);

		if (invokerOptions != null) {
			m_invokerOptions = invokerOptions;
		} else {
			m_invokerOptions = new ServiceInvokerOptions();
		}

		m_responseTransport = responseTransport; // null OK
		m_useCase = useCase; // null OK
		m_consumerId = consumerId; // null OK

		m_securityContext = new ClientSecurityContextImpl(this);
	}

	/**
	 * Constructor. Client and service writers should never call this method.
	 *
	 * @param serviceDesc
	 * @param operation
	 * @param protocolProcessor
	 * @param transport
	 * @param requestMessage
	 * @param responseMessage
	 * @param serviceAddress
	 * @param systemProperties
	 * @param serviceVersion
	 * @param invokerOptions
	 * @param responseTransport
	 * @param useCase
	 * @param requestCharset
	 */
	public ClientMessageContextImpl(ClientServiceDesc serviceDesc,
			ServiceOperationDesc operation,
			ProtocolProcessorDesc protocolProcessor, Transport transport,
			BaseMessageImpl requestMessage, BaseMessageImpl responseMessage,
			ServiceAddress serviceAddress,
			Map<String, Object> systemProperties, String serviceVersion,
			ServiceInvokerOptions invokerOptions, String responseTransport,
			String useCase, String consumerId, Charset requestCharset, String requestUri)
			throws ServiceException {
		this(serviceDesc,operation,protocolProcessor,transport,
			requestMessage,responseMessage,serviceAddress,
			systemProperties,serviceVersion,invokerOptions,
			responseTransport,useCase, consumerId, requestCharset,requestUri,false);
	}

	@Override
	public final ClientServiceContext getServiceContext() {
		// TODO: find out why do we need this hack... this method is defined in
		// the base class
		return super.getServiceContext();
	}

	/**
	 * Returns the service invoker options that were supplied for this specific
	 * invocation. This value is never null. Any service invoker options will
	 * override their corresponding values in configuration.
	 *
	 */
	public ServiceInvokerOptions getInvokerOptions() {
		return m_invokerOptions;
	}

	/**
	 * Returns the transport options that were supplied for this specific
	 * invocation, within the ServiceInvokerOptions. Any transport options will
	 * override their corresponding values in configuration (provided that the
	 * transport is capable of supporting per-request override of these values).
	 */
	public TransportOptions getTransportOptions() {
		return m_invokerOptions.getTransportOptions();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseMessageContextImpl#buildOutputHeaders(java.util.Map)
	 */
	@Override
	protected Map<String, String> buildOutputHeaders(
			Map<String, String> customHeaders) throws ServiceException {
		Map<String, String> result = new HashMap<String, String>();

		if (customHeaders != null) {
			result.putAll(customHeaders);
		}

		// Operation name, service name
		HTTPCommonUtils.addServiceAndOperationHeaders(getServiceQName(),
				getOperationName(), result);

		Message requestMsg = getRequestMessage();
		Message responseMsg = getResponseMessage();

		// build request/response data format headers
		String requestPayload = requestMsg.getPayloadType();
		String responsePayload = responseMsg.getPayloadType();

		result.put(SOAHeaders.REQUEST_DATA_FORMAT, requestPayload);
		if (responsePayload != null) {
			result.put(SOAHeaders.RESPONSE_DATA_FORMAT, responsePayload);
		}

		String requestId = getRequestId();
		if (requestId == null) {
			// also check the transport headers for requestId
			requestId = getRequestMessage().getTransportHeader(SOAHeaders.REQUEST_ID);
			if (requestId != null) {
				setRequestId(requestId, null);
			}
		}
		if (requestId != null) {
			result.put(SOAHeaders.REQUEST_ID, requestId);
		} else {
			String requestGuid = getRequestGuid();
			if (null == requestGuid) {
				requestGuid = getRequestMessage().getTransportHeader(SOAHeaders.REQUEST_GUID);
				if (null == requestGuid || requestGuid.isEmpty()) {
					requestGuid = HTTPCommonUtils.generateRequestGuid();
				}
				setRequestGuid(requestGuid);
				setProperty(SOAConstants.CTX_PROP_GUID_CREATED, Boolean.valueOf(true));
			}
			result.put(SOAHeaders.REQUEST_GUID, requestGuid);
		}

		HTTPCommonUtils.addG11nHeaders(requestMsg.getG11nOptions(), result);
		// version

		if (getServiceVersion() != null && !isVersionIgnored()) {
			result.put(SOAHeaders.VERSION, getServiceVersion());
		}

		String messageProtocol = getProtocolProcessor().getMessageProtocol();
		if (messageProtocol != null
				&& !messageProtocol.equals(SOAConstants.MSG_PROTOCOL_NONE)) {
			result.put(SOAHeaders.MESSAGE_PROTOCOL, messageProtocol);
		}

		if (m_responseTransport != null) {
			result.put(SOAHeaders.RESPONSE_TRANSPORT, m_responseTransport);
		}

		if (m_useCase != null) {
			result.put(SOAHeaders.USECASE_NAME, m_useCase);
		}

		if (m_consumerId != null) {
			result.put(SOAHeaders.CONSUMER_ID, m_consumerId);
		}

		HeaderMappingsDesc requestHeaderMappings = getServiceDesc()
				.getRequestHeaderMappings();
		HTTPCommonUtils.applyHeaderMap(requestHeaderMappings.getHeaderMap(),
				result);
		HTTPCommonUtils.applySuppressHeaderSet(requestHeaderMappings
				.getSuppressHeaderSet(), result); // map first, then suppress

		return result;
	}

	@SuppressWarnings("cast")
	private boolean isVersionIgnored() {
		return ((ClientConfigHolder) ((ClientServiceDesc) getServiceDesc())
				.getConfig()).isIgnoreServiceVersion();
	}

	@Override
	protected void processInboundHeaders(Map<String, String> transportHeaders)
			throws ServiceException {
		super.processInboundHeaders(transportHeaders);

		if (transportHeaders == null) {
			return;
		}

		HeaderMappingsDesc responseHeaderMappings = getServiceDesc()
				.getResponseHeaderMappings();
		HTTPCommonUtils.applyHeaderMap(responseHeaderMappings.getHeaderMap(),
				transportHeaders);
		HTTPCommonUtils.applySuppressHeaderSet(responseHeaderMappings
				.getSuppressHeaderSet(), transportHeaders);


		String requestId = transportHeaders.get(SOAHeaders.REQUEST_ID);
		String guid = transportHeaders.get(SOAHeaders.REQUEST_GUID);
		if (requestId != null) {
			setRequestId(requestId, guid);
			if (getCallerMessageContext() != null) {
				getCallerMessageContext().setRequestId(requestId, guid);
			}
		} else if (guid != null) {
			setRequestId(null, guid);
			if (getCallerMessageContext() != null) {
				getCallerMessageContext().setRequestId(null, guid);
			}
		} // else if both requestId and guid are null, do nothing.
	}

	@Override
	protected Throwable validateNewError(Throwable t) {
		if (t instanceof ServiceInvocationExceptionInterface) {
			LogManager
					.getInstance(this.getClass())
					.log(
							Level.SEVERE,
							"A ServiceInvocationException was added to the ClientMessageContext. "
									+ "This is disallowed and SOA framework has made its best efforts "
									+ "to convert error to a system error. Exception text: "
									+ t.toString(), t);


			t = new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_SYS_CLIENT,
						ErrorConstants.ERRORDOMAIN, new Object[] { getAdminName(), t.toString() }), t);
			return super.validateNewError(t);
		}

		// erase any hints that it could be an application exception
		if (t instanceof ServiceExceptionInterface) {
			boolean isAppError = false;

			if (t instanceof ServiceException) {
				ServiceException e2 = (ServiceException) t;
				if (e2.getSubcategory() == ErrorSubcategory.APPLICATION) {
					e2.eraseSubcategory();
					isAppError = true;
				}
			}

			if (t instanceof ServiceRuntimeException) {
				ServiceRuntimeException e2 = (ServiceRuntimeException) t;
				if (e2.getSubcategory() == ErrorSubcategory.APPLICATION) {
					e2.eraseSubcategory();
					isAppError = true;
				}
			}

			ServiceExceptionInterface e2 = (ServiceExceptionInterface) t;
			ErrorMessage errorMessage = e2.getErrorMessage();
			if (errorMessage != null) {
				List<CommonErrorData> errorDataList = errorMessage.getError();
				for (int i = 0; i < errorDataList.size(); i++) {
					CommonErrorData errorData = errorDataList.get(i);
					if (errorData != null
							&& errorData.getCategory() == ErrorCategory.APPLICATION) {
						errorData.setCategory(ErrorCategory.SYSTEM);
						isAppError = true;
					}
				}
			}

			if (isAppError) {
				LogManager
						.getInstance(this.getClass())
						.log(
								Level.SEVERE,
								"Application error was added to the ClientMessageContext. "
										+ "This is disallowed and SOA framework has made its best efforts "
										+ "to convert error to a system error. Exception text: "
										+ t.toString(), t);
			}
		}

		return super.validateNewError(t);
	}

	private static ServiceAddress buildClientAddress() {
		// return local address
		return new ServiceAddress(null);
	}

	/*
	 * Updates the ClientConfigHolder and reloads the ServiceDesc for a valid
	 * env (ie. serviceLocation is not null)
	 */
	public String setServiceLocationFromLocationMapping(String env)
			throws ServiceException {

		ClientConfigManager configMgr = ClientConfigManager.getInstance();

		ClientConfigHolder holdercopy = configMgr.getConfigForUpdate(
				getServiceId().getAdminName(), getServiceId()
						.getClientName(), getServiceId().getEnvName());

		String serviceLocation = holdercopy
				.setServiceLocationFromLocationMapping(env);

		if (serviceLocation != null && serviceLocation.length() > 0) {
			// Constructing the URL shouldn't be throwing an error as the
			// ServiceLocations in Config
			// have been validated
			try {
				getServiceAddress().setServiceLocationUrl(
						new URL(serviceLocation));
			} catch (MalformedURLException e) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_BAD_REQUEST_URL,
								ErrorConstants.ERRORDOMAIN, new Object[] {getServiceDesc().getClientName(),serviceLocation }));
			}

			// The 'env' is a valid key, and the holder is updated.
			configMgr.updateConfig(getServiceDesc().getAdminName(),
					getServiceDesc().getClientName(), holdercopy);

			ClientServiceDescFactory descFactory = ClientServiceDescFactory
					.getInstance();
			descFactory.reloadServiceDesc(getServiceId().getAdminName(), getServiceId()
					.getClientName(), getServiceId().getEnvName());

			// Update the ClientConfig Invoker bean with the Service Location
			//env is the EnvironmentName read via envmapper.
			try {
				ClientServiceInvokerConfigBean bean = ClientServiceConfigBeanManager
						.getInvokerInstance(getServiceId().getAdminName(), getServiceId()
								.getClientName(), getServiceId().getEnvName());
				bean.setServiceUrl(serviceLocation);
			} catch (BaseRuntimeException be) {
				// For backward compatibility
				try {
					ClientServiceInvokerConfigBean bean = ClientServiceConfigBeanManager
					.getInvokerInstance(getServiceDesc().getAdminName(),
							getServiceDesc().getClientName());
					bean.setServiceUrl(serviceLocation);
				} catch (BaseRuntimeException e) {
					// if we do not find a ClientServiceInvokerConfigBean with
					// no environment specified then we re-throw the exception
					// ClientServiceInvokerConfigBean not initialized for input
					// environment (env). This would only happen If some one
					// mixes old style (Using EnvironmentResolutionHandler)
					// with new style i.e. with multiple client config support.
					throw be;
				}
			}
		}
		return serviceLocation;
	}

}
