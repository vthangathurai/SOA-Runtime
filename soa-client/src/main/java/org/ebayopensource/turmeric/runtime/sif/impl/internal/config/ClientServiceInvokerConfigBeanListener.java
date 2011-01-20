/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ProtocolProcessorConfig;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


public class ClientServiceInvokerConfigBeanListener extends
		ClientServiceConfigBeanListener {

	ClientServiceInvokerConfigBeanListener(ClientServiceInvokerConfigBean bean) {
		super(bean);
	}

	@Override
	protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
		String name = evt.getPropertyName();
		String value = (String) evt.getNewValue();

		ClientServiceInvokerConfigBean bean = (ClientServiceInvokerConfigBean) m_bean;

		if (ClientServiceInvokerConfigBean.PROP_APP_LEVEL_NUM_RETRIES
				.equalsIgnoreCase(name)) {
			bean.setAppLevelNumRetries(value);
		} else if (ClientServiceInvokerConfigBean.PROP_REQUEST_BINDING
				.equalsIgnoreCase(name)) {
			bean.setRequestBinding(value);
		} else if (ClientServiceInvokerConfigBean.PROP_RESPONSE_BINDING
				.equalsIgnoreCase(name)) {
			bean.setResponseBinding(value);
		} else if (ClientServiceInvokerConfigBean.PROP_PREFERRED_TRANSPORT_NAME
				.equalsIgnoreCase(name)) {
			if (value != null)
				bean.setPreferredTransport(value.toUpperCase());
			else
				bean.setPreferredTransport("");
		} else if (ClientServiceInvokerConfigBean.PROP_MESSAGE_PROTOCOL_NAME
				.equalsIgnoreCase(name)) {
			if ( value != null)
				bean.setMessageProtocolName(value.toUpperCase());
			else 
				bean.setMessageProtocolName(SOAConstants.MSG_PROTOCOL_NONE);
		} else if (ClientServiceInvokerConfigBean.PROP_USE_CASE
				.equalsIgnoreCase(name)) {
			bean.setUseCase(value);
		} else if (ClientServiceInvokerConfigBean.PROP_SERVICE_URL
				.equalsIgnoreCase(name)) {
			bean.setServiceUrl(value);
		} else if (ClientServiceInvokerConfigBean.PROP_USE_REST
				.equalsIgnoreCase(name)) {
			bean.setUseREST(value);
		} else if (ClientServiceInvokerConfigBean.PROP_MAX_URL_REST_LEN
				.equalsIgnoreCase(name)) {
			bean.setMaxURLLengthForREST(value);
		} else {
			String msg = "Property(" + name + ") not supported";
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	protected void setValuesForVeto(ClientConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException {
		String name = evt.getPropertyName();
		String value = (String) evt.getNewValue();

		if (ClientServiceInvokerConfigBean.PROP_APP_LEVEL_NUM_RETRIES
				.equalsIgnoreCase(name)) {
			validateIntegerValue(evt, name, value);
			configHolder.setAppLevelNumRetries(Integer.valueOf(value));
		} else if (ClientServiceInvokerConfigBean.PROP_REQUEST_BINDING
				.equalsIgnoreCase(name)) {
			configHolder.setRequestDataBinding(value);
		} else if (ClientServiceInvokerConfigBean.PROP_RESPONSE_BINDING
				.equalsIgnoreCase(name)) {
			configHolder.setResponseDataBinding(value);
		} else if (ClientServiceInvokerConfigBean.PROP_PREFERRED_TRANSPORT_NAME
				.equalsIgnoreCase(name)) {
			if (value != null) {
				validateTransportInput(evt, configHolder, name, value.toUpperCase());
				configHolder.setPreferredTransport(value.toUpperCase());
			} else {
				configHolder.setPreferredTransport(null);
			}
		} else if (ClientServiceInvokerConfigBean.PROP_MESSAGE_PROTOCOL_NAME
				.equalsIgnoreCase(name)) {
			if (value != null) {
				validateMessageProtocol(evt, configHolder, name, value.toUpperCase());
				configHolder.setMessageProtocol(value.toUpperCase());
			} else {
				configHolder.setMessageProtocol(SOAConstants.MSG_PROTOCOL_NONE);
			}
		} else if (ClientServiceInvokerConfigBean.PROP_USE_CASE
				.equalsIgnoreCase(name)) {
			configHolder.setInvocationUseCase(value);
		} else if (ClientServiceInvokerConfigBean.PROP_SERVICE_URL
				.equalsIgnoreCase(name)) {
			configHolder.setServiceLocation(value);
		} else if (ClientServiceInvokerConfigBean.PROP_USE_REST
				.equalsIgnoreCase(name)) {
			validateBooleanValue(evt, name, value);
			configHolder.setUseREST(Boolean.valueOf(value));
		} else if (ClientServiceInvokerConfigBean.PROP_MAX_URL_REST_LEN
				.equalsIgnoreCase(name)) {
			validateIntegerValue(evt, name, value);
			configHolder.setMaxURLLengthForREST(Integer.valueOf(value));
		} else {
			String msg = "Property(" + name + ") not supported";
			throw new PropertyVetoException(msg, evt);
		}
	}

	private void validateTransportInput(PropertyChangeEvent evt, ClientConfigHolder config, String name,
			String value) throws PropertyVetoException {
		Set<String> transports = config.getMessageProcessorConfig().getTransportClasses().keySet();
		if (!transports.contains(value))
		{
			String msg = "Encountered invalid property.value for " + name + "."
			+ value + "; The value specified should be in the Transports configured (" + transports + ")";
			throw new PropertyVetoException(msg, evt);
		}
	}
	
	private void validateMessageProtocol (PropertyChangeEvent evt, ClientConfigHolder config, String name,
			String value) throws PropertyVetoException {
		List<ProtocolProcessorConfig> protocolProcessors = config.getMessageProcessorConfig().getProtocolProcessors();
	     boolean found = false;
		for(ProtocolProcessorConfig pp: protocolProcessors) {
	    	 if (pp.getName().equalsIgnoreCase(value) ) {
	    		 found  = true;
	    		 break;
	    	 }
	     }
		if (!found) {
			if (SOAConstants.MSG_PROTOCOL_NONE.equalsIgnoreCase(value)) {
				found = true;
			}
		}
		
		if (!found)
		{
			String msg = "Encountered invalid property.value for " + name + "."
			+ value + "; The value specified should be in the Message Protocols configured";
			throw new PropertyVetoException(msg, evt);
		}
	}

}
