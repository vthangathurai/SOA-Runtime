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

import org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseLoggingHandler;


public class ClientServicePayloadLogConfigBeanListener extends
		ClientServiceConfigBeanListener {

	ClientServicePayloadLogConfigBeanListener(ClientServicePayloadLogConfigBean bean) {
		super(bean);
	}

	@Override
	protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
		String name = evt.getPropertyName();
		String value = getValue(evt);

		ClientServicePayloadLogConfigBean bean = (ClientServicePayloadLogConfigBean) m_bean;

		if (!ClientServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name) ){
			value= getValue(evt);
		}
		
		if (ClientServicePayloadLogConfigBean.PROP_RequestPAYLOADLOG
				.equalsIgnoreCase(name)) {
			bean.setRequestPayloadLog(value);
		} else if (ClientServicePayloadLogConfigBean.PROP_RequestPAYLOADCALLOG
				.equalsIgnoreCase(name)) {
			bean.setRequestPayloadCalLog(value);
		} else if (ClientServicePayloadLogConfigBean.PROP_ResponsePAYLOADLOG
				.equalsIgnoreCase(name)) {
			bean.setResponsePayloadLog(value);
		} else if (ClientServicePayloadLogConfigBean.PROP_ResponsePAYLOADCALLOG
				.equalsIgnoreCase(name)) {
			bean.setResponsePayloadCalLog(value);
		} else if (ClientServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES
				.equalsIgnoreCase(name)) {
			value= validateInt(value);
			bean.setPayloadMaxBytes(value);
		} 
		else {
			String msg = "Property(" + name + ") not supported";
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	protected void setValuesForVeto(ClientConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException {
		String name = evt.getPropertyName();
		String value = getValue(evt);

		if (ClientServicePayloadLogConfigBean.PROP_RequestPAYLOADLOG
				.equalsIgnoreCase(name)) {
			ClientServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ClientServicePayloadLogConfigBean.PROP_RequestPAYLOADLOG, 
					value);			
		} else if (ClientServicePayloadLogConfigBean.PROP_RequestPAYLOADCALLOG
				.equalsIgnoreCase(name)) {
			ClientServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ClientServicePayloadLogConfigBean.PROP_RequestPAYLOADCALLOG, 
					value);
		} else if (ClientServicePayloadLogConfigBean.PROP_ResponsePAYLOADLOG
				.equalsIgnoreCase(name)) {
			ClientServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ClientServicePayloadLogConfigBean.PROP_ResponsePAYLOADLOG, 
					value);
		} else if (ClientServicePayloadLogConfigBean.PROP_ResponsePAYLOADCALLOG
				.equalsIgnoreCase(name)) {
			ClientServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ClientServicePayloadLogConfigBean.PROP_ResponsePAYLOADCALLOG, 
					value);
		}else if (ClientServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES
				.equalsIgnoreCase(name)) {
			value= getValue(evt);
			ClientServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ClientServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES, 
					value);
		}  
		else {
			String msg = "Property(" + name + ") not supported";
			throw new PropertyVetoException(msg, evt);
		}
	}
	
	private String getValue(PropertyChangeEvent evt) throws PropertyVetoException
	{
		String value = (String)evt.getNewValue();
		
		if (value == null || 
				(!value.equalsIgnoreCase(BaseLoggingHandler.PAYLOADLOG_ON) && 
				!value.equalsIgnoreCase(BaseLoggingHandler.PAYLOADLOG_OFF) &&
				!value.equalsIgnoreCase(BaseLoggingHandler.PAYLOADLOG_FULL) && 
				!value.equalsIgnoreCase(BaseLoggingHandler.PAYLOADLOG_ERRORONLY)))
		{
			value = BaseLoggingHandler.PAYLOADLOG_ERRORONLY;
		}
		
		return value;
	}
	
	private String validateInt(String value) {
		try { 
			 Integer.parseInt(value);
		}catch(Exception exc) {
			return "4096";
		}	
		return value;
	}

}
