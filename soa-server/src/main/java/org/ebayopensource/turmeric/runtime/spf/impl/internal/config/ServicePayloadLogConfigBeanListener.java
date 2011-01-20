/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;



/**
 * @author
 */
public class ServicePayloadLogConfigBeanListener extends ServiceConfigBeanListener {

	
	ServicePayloadLogConfigBeanListener(ServicePayloadLogConfigBean bean) {
		super(bean);
	}
	
	@Override
	protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
		String name = evt.getPropertyName();
		String value = (String) evt.getNewValue();;
		
		
		
		ServicePayloadLogConfigBean bean = (ServicePayloadLogConfigBean) m_serviceBean;

		if (! ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name) ){
			value= getValue(evt);
		}else if (ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name)){
			value= validateInt(value);
		}
	
		if (ServicePayloadLogConfigBean.PROP_RequestPAYLOADLOG.equalsIgnoreCase(name)) {
			bean.setRequestPayloadLog(value);
		} else if (ServicePayloadLogConfigBean.PROP_RequestPAYLOADCALLOG.equalsIgnoreCase(name)) {
			bean.setRequestPayloadCalLog(value);
		} else if (ServicePayloadLogConfigBean.PROP_ResponsePAYLOADLOG.equalsIgnoreCase(name)) {
			bean.setResponsePayloadLog(value);
		} else if (ServicePayloadLogConfigBean.PROP_ResponsePAYLOADCALLOG.equalsIgnoreCase(name)) {
			bean.setResponsePayloadCalLog(value);
		} else if (ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name)) {
			bean.setPayloadMaxBytes(value);
		} else {
			String msg = "Property(" + name + ") not supported";
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	protected void setValuesForVeto(ServiceConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException {
		String name = evt.getPropertyName();
		String value =(String)evt.getNewValue();
		
		if (! ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name) ){
			value= getValue(evt);
		}else if (ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name)){
			value= validateInt(value);
		}
		
		if (ServicePayloadLogConfigBean.PROP_RequestPAYLOADLOG.equalsIgnoreCase(name)) {
			ServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ServicePayloadLogConfigBean.PROP_RequestPAYLOADLOG, 
					value);						
		} else if (ServicePayloadLogConfigBean.PROP_RequestPAYLOADCALLOG.equalsIgnoreCase(name)) {
			ServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ServicePayloadLogConfigBean.PROP_RequestPAYLOADCALLOG, 
					value);						
		} else if (ServicePayloadLogConfigBean.PROP_ResponsePAYLOADLOG.equalsIgnoreCase(name)) {
			ServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ServicePayloadLogConfigBean.PROP_ResponsePAYLOADLOG, 
					value);						
		} else if (ServicePayloadLogConfigBean.PROP_ResponsePAYLOADCALLOG.equalsIgnoreCase(name)) {
			ServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ServicePayloadLogConfigBean.PROP_ResponsePAYLOADCALLOG, 
					value);						
		} else if (ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES.equalsIgnoreCase(name)) {
			ServicePayloadLogConfigBean.updateConfigHolderOption(
					configHolder,
					ServicePayloadLogConfigBean.PROP_PAYLOADMAXBYTES, 
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
				(!value.equalsIgnoreCase(ServicePayloadLogConfigBean.PAYLOADLOG_ON) && 
				!value.equalsIgnoreCase(ServicePayloadLogConfigBean.PAYLOADLOG_OFF) && 
				!value.equalsIgnoreCase(ServicePayloadLogConfigBean.PAYLOADLOG_FULL) && 
				!value.equalsIgnoreCase(ServicePayloadLogConfigBean.PAYLOADLOG_ERRORONLY)))
		{
			value = ServicePayloadLogConfigBean.PAYLOADLOG_ERRORONLY;
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
