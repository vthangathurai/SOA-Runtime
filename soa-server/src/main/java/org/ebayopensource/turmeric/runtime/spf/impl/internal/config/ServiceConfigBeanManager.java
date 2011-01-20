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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.HandlerConfig;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.NameValue;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;

import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.DynamicBeanPropertyCreationException;
import com.ebay.kernel.bean.configuration.DynamicConfigBean;
import com.ebay.kernel.exception.BaseRuntimeException;
import com.ebay.kernel.initialization.InitializationException;

/**
 * @author idralyuk
 */
public class ServiceConfigBeanManager {

	private static Map<String, ServiceOptionsConfigBean> s_optionsConfigBeans =
		Collections.synchronizedMap(new HashMap<String, ServiceOptionsConfigBean>());
	
	private static Map<String, ServicePayloadLogConfigBean> s_payloadLogConfigBeans =
		Collections.synchronizedMap(new HashMap<String, ServicePayloadLogConfigBean>());

	private static Map<String, DynamicConfigBean> s_URLHeaderMappingsConfigBeans =
		Collections.synchronizedMap(new HashMap<String, DynamicConfigBean>());

	private static Map<String, DynamicConfigBean> s_RequestHeaderMappingsConfigBeans =
		Collections.synchronizedMap(new HashMap<String, DynamicConfigBean>());

	private static Map<String, DynamicConfigBean> s_ResponseHeaderMappingsConfigBeans =
		Collections.synchronizedMap(new HashMap<String, DynamicConfigBean>());

	private static Map<String, DynamicConfigBean> s_RequestHandlerOptionsConfigBeans =
		Collections.synchronizedMap(new HashMap<String, DynamicConfigBean>());

	private static Map<String, DynamicConfigBean> s_ResponseHandlerOptionsConfigBeans =
		Collections.synchronizedMap(new HashMap<String, DynamicConfigBean>());

	public static final String REQUEST_HANDLER_PREFIX = "request-handler.";
	public static final String RESPONSE_HANDLER_PREFIX = "response-handler.";
	
	public static ServiceOptionsConfigBean getOptionsInstance(String adminName) {
		ServiceOptionsConfigBean sBean = null;
		if (s_optionsConfigBeans == null) {
			throw new BaseRuntimeException("ServiceOptionsConfigBeans Map is not initialized");
		} 

		String lookupName = adminName;
		sBean = s_optionsConfigBeans.get(lookupName);
		if (sBean == null) {
			throw new BaseRuntimeException("ServiceOptionsConfigBean is not initialized");
		}

		return sBean;
	}
	
	public static ServicePayloadLogConfigBean getPayloadLogInstance(String adminName) {
		ServicePayloadLogConfigBean sBean = null;
		if (s_payloadLogConfigBeans == null) {
			throw new BaseRuntimeException("ServicePayloadLogConfigBeans Map is not initialized");
		} 

		String lookupName = adminName;
		sBean = s_payloadLogConfigBeans.get(lookupName);
		if (sBean == null) {
			throw new BaseRuntimeException("ServicePayloadLogConfigBean is not initialized, Please check whether your service is properly initialized");
		}

		return sBean;
	}

	public static void initConfigBeans(ServiceConfigHolder config) {
		initOptionsConfigBean(config);
		initPayloadLogConfigBean(config);
		initURLHeaderMappingsConfigBean(config);
		initRequestHeaderMappingsConfigBean(config);
		initResponseHeaderMappingsConfigBean(config);
		initRequestHandlerOptionsConfigBeans(config);
		initResponseHandlerOptionsConfigBeans(config);
	}
	
	private static void initOptionsConfigBean(ServiceConfigHolder config) {
		ServiceOptionsConfigBean sBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_optionsConfigBeans) {
				sBean = s_optionsConfigBeans.get(lookupName);
				if (sBean == null) {
					sBean = new ServiceOptionsConfigBean(config);
					ServiceOptionsConfigBeanListener listener = new ServiceOptionsConfigBeanListener(sBean);
					sBean.addVetoableChangeListener(listener);
					sBean.addPropertyChangeListener(listener);

					s_optionsConfigBeans.put(lookupName, sBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}
	
	private static void initPayloadLogConfigBean(ServiceConfigHolder config) {
		ServicePayloadLogConfigBean sBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_payloadLogConfigBeans) {
				sBean = s_payloadLogConfigBeans.get(lookupName);
				if (sBean == null) {
					sBean = new ServicePayloadLogConfigBean(config);
					ServicePayloadLogConfigBeanListener listener = new ServicePayloadLogConfigBeanListener(sBean);
					sBean.addVetoableChangeListener(listener);
					sBean.addPropertyChangeListener(listener);

					s_payloadLogConfigBeans.put(lookupName, sBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static void initURLHeaderMappingsConfigBean(ServiceConfigHolder config) {
		DynamicConfigBean dBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_URLHeaderMappingsConfigBeans) {
				dBean = s_URLHeaderMappingsConfigBeans.get(lookupName);
				if (dBean == null) {
					
					dBean = ServiceConfigBean.createDynamicConfigBean(config, "URLHeaderMappings");

					URLHeaderMappingsConfigBeanListener listener = new URLHeaderMappingsConfigBeanListener(dBean, lookupName);
					
					listener.setMappings(config.getHeaderMappingOptions());
					
					config.setHeaderMappingOptions(listener.getMappings());
					
					dBean.addVetoableChangeListener(listener);
					dBean.addPropertyChangeListener(listener);

					s_URLHeaderMappingsConfigBeans.put(lookupName, dBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static void initRequestHeaderMappingsConfigBean(ServiceConfigHolder config) {
		DynamicConfigBean dBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_RequestHeaderMappingsConfigBeans) {
				dBean = s_RequestHeaderMappingsConfigBeans.get(lookupName);
				if (dBean == null) {
					
					dBean = ServiceConfigBean.createDynamicConfigBean(config, "RequestHeaderMappings");
					
					RequestHeaderMappingsConfigBeanListener listener = new RequestHeaderMappingsConfigBeanListener(dBean, lookupName);
					
					listener.setMappings(config.getRequestHeaderMappingOptions());

					config.setRequestHeaderMappingOptions(listener.getMappings());
					
					dBean.addVetoableChangeListener(listener);
					dBean.addPropertyChangeListener(listener);

					s_RequestHeaderMappingsConfigBeans.put(lookupName, dBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static void initResponseHeaderMappingsConfigBean(ServiceConfigHolder config) {
		DynamicConfigBean dBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_ResponseHeaderMappingsConfigBeans) {
				dBean = s_ResponseHeaderMappingsConfigBeans.get(lookupName);
				if (dBean == null) {
					
					dBean = ServiceConfigBean.createDynamicConfigBean(config, "ResponseHeaderMappings");
					
					ResponseHeaderMappingsConfigBeanListener listener = new ResponseHeaderMappingsConfigBeanListener(dBean, lookupName);

					listener.setMappings(config.getResponseHeaderMappingOptions());

					config.setResponseHeaderMappingOptions(listener.getMappings());
					
					dBean.addVetoableChangeListener(listener);
					dBean.addPropertyChangeListener(listener);

					s_ResponseHeaderMappingsConfigBeans.put(lookupName, dBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static void initRequestHandlerOptionsConfigBeans(ServiceConfigHolder config) {
		try {
			String lookupName, adminName, handlerName;
			List<HandlerConfig> requestHandlers = config.getMessageProcessorConfig().getRequestHandlers();
			if (requestHandlers != null) {
				synchronized (s_RequestHandlerOptionsConfigBeans) {
					for (HandlerConfig hc : requestHandlers) {
						handlerName = hc.getName();
						adminName = getLookupName(config);
						lookupName = adminName + "." + REQUEST_HANDLER_PREFIX + handlerName;
						DynamicConfigBean dBean = s_RequestHandlerOptionsConfigBeans.get(lookupName);
						if (dBean == null) {
							dBean = ServiceConfigBean.createDynamicConfigBean(config, REQUEST_HANDLER_PREFIX + handlerName + ".Options");

							RequestHandlerOptionsConfigBeanListener listener = new RequestHandlerOptionsConfigBeanListener(dBean, adminName, handlerName);
							listener.setMappings(hc.getOptions());
							hc.setOptions(listener.getMappings());
							
							dBean.addVetoableChangeListener(listener);
							dBean.addPropertyChangeListener(listener);

							s_RequestHandlerOptionsConfigBeans.put(lookupName, dBean);
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static void initResponseHandlerOptionsConfigBeans(ServiceConfigHolder config) {
		try {
			String lookupName, adminName, handlerName;
			List<HandlerConfig> responseHandlers = config.getMessageProcessorConfig().getResponseHandlers();
			if (responseHandlers != null) {
				synchronized (s_ResponseHandlerOptionsConfigBeans) {
					for (HandlerConfig hc : responseHandlers) {
						handlerName = hc.getName();
						adminName = getLookupName(config);
						lookupName = adminName + "." + RESPONSE_HANDLER_PREFIX + handlerName;
						DynamicConfigBean dBean = s_ResponseHandlerOptionsConfigBeans.get(lookupName);
						if (dBean == null) {
							dBean = ServiceConfigBean.createDynamicConfigBean(config, RESPONSE_HANDLER_PREFIX + handlerName + ".Options");

							ResponseHandlerOptionsConfigBeanListener listener = new ResponseHandlerOptionsConfigBeanListener(dBean, adminName, handlerName);
							listener.setMappings(hc.getOptions());
							hc.setOptions(listener.getMappings());
							
							dBean.addVetoableChangeListener(listener);
							dBean.addPropertyChangeListener(listener);

							s_ResponseHandlerOptionsConfigBeans.put(lookupName, dBean);
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static String getLookupName(ServiceConfigHolder config) {
		return config.getAdminName();
	}
	
	
	static class URLHeaderMappingsConfigBeanListener extends DynamicConfigBeanListener {

		URLHeaderMappingsConfigBeanListener(DynamicConfigBean bean, String adminName) {
			super(bean, adminName);
		}

		@Override
		protected void setValuesForVeto(ServiceConfigHolder configHolder, PropertyChangeEvent evt) throws PropertyVetoException {
			if (configHolder.getHeaderMappingOptions() == null) {
				OptionList optionList = new OptionList();
				configHolder.setHeaderMappingOptions(optionList);
			}

			setValuesForVeto(evt, configHolder.getHeaderMappingOptions());
		}
	}
	
	static class RequestHeaderMappingsConfigBeanListener extends DynamicConfigBeanListener {

		RequestHeaderMappingsConfigBeanListener(DynamicConfigBean bean, String adminName) {
			super(bean, adminName);
		}

		@Override
		protected void setValuesForVeto(ServiceConfigHolder configHolder, PropertyChangeEvent evt) throws PropertyVetoException {
			if (configHolder.getRequestHeaderMappingOptions() == null) {
				OptionList optionList = new OptionList();
				configHolder.setRequestHeaderMappingOptions(optionList);
			}

			setValuesForVeto(evt, configHolder.getRequestHeaderMappingOptions());
		}
	}

	static class ResponseHeaderMappingsConfigBeanListener extends DynamicConfigBeanListener {
		ResponseHeaderMappingsConfigBeanListener(DynamicConfigBean bean, String adminName) {
			super(bean, adminName);
		}

		@Override
		protected void setValuesForVeto(ServiceConfigHolder configHolder, PropertyChangeEvent evt) throws PropertyVetoException {
			if (configHolder.getResponseHeaderMappingOptions() == null) {
				OptionList optionList = new OptionList();
				configHolder.setResponseHeaderMappingOptions(optionList);
			}

			setValuesForVeto(evt, configHolder.getResponseHeaderMappingOptions());
		}
	}
	
	static class RequestHandlerOptionsConfigBeanListener extends DynamicConfigBeanListener {

		protected String m_handlerName;
		
		RequestHandlerOptionsConfigBeanListener(DynamicConfigBean bean, String adminName, String handlerName) {
			super(bean, adminName);
			m_handlerName = handlerName;
		}

		@Override
		protected void setValuesForVeto(ServiceConfigHolder configHolder, PropertyChangeEvent evt) throws PropertyVetoException {
			setValuesForVeto(evt, getHandlerOptionList(getHandlerByName(configHolder.getMessageProcessorConfig().getRequestHandlers(), m_handlerName)));
		}
	}

	static class ResponseHandlerOptionsConfigBeanListener extends DynamicConfigBeanListener {
		protected String m_handlerName;
		
		ResponseHandlerOptionsConfigBeanListener(DynamicConfigBean bean, String adminName, String handlerName) {
			super(bean, adminName);
			m_handlerName = handlerName;
		}

		@Override
		protected void setValuesForVeto(ServiceConfigHolder configHolder, PropertyChangeEvent evt) throws PropertyVetoException {
			setValuesForVeto(evt, getHandlerOptionList(getHandlerByName(configHolder.getMessageProcessorConfig().getResponseHandlers(), m_handlerName)));
		}
	}
	
	static abstract class DynamicConfigBeanListener extends ServiceConfigBeanListener {
		protected final String m_adminName;

		DynamicConfigBeanListener(DynamicConfigBean bean, String adminName) {
			super(bean);
			m_adminName = adminName;
		}

		@Override
		protected String getAdminName() {
			return m_adminName;
		}

		@Override
		protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
			String name = evt.getPropertyName();
			if (name == null || name.equals(DynamicConfigBean.EXTERNAL_MUTABLE)) {
				return;
			}
			Object value = evt.getNewValue();
			if (!m_dynamicBean.hasProperty(name)) m_dynamicBean.addProperty(name, value);
			else m_dynamicBean.setPropertyValue(name, value.toString());
		}

		protected void setValuesForVeto(PropertyChangeEvent evt, OptionList options) throws PropertyVetoException {
			try {
				String name = evt.getPropertyName();
				if (name == null || name.equals(DynamicConfigBean.EXTERNAL_MUTABLE)) {
					return;
				}
				Object value = evt.getNewValue();
				validateStringValue(evt, name, value);

				NameValue nv = new NameValue();
				nv.setName(name);
				nv.setValue(value.toString());

				List<NameValue> optionList = options.getOption();
				
				synchronized (optionList) {
					Iterator<NameValue> iter = optionList.iterator();
					NameValue tmpNV = null;
					while (iter.hasNext()) {
						tmpNV = iter.next();
						if (name.equals(tmpNV.getName())) {
							optionList.remove(tmpNV);
							break;
						}
					}
					optionList.add(nv);
				}
			} catch (Exception e) {
				throw new PropertyVetoException(e.getMessage(), evt);
			}
		}

		protected void setMappings(OptionList options) throws DynamicBeanPropertyCreationException {
			if (options == null) return;
			List<NameValue> list = options.getOption();
			if (list == null) return;
			for (NameValue nv : list) {
				if (!m_dynamicBean.hasProperty(nv.getName())) {
					m_dynamicBean.addProperty(nv.getName(), nv.getValue());
				}
			}
		}
		
		protected OptionList getMappings() {
			OptionList optionList = new OptionList();
			List<NameValue> list = optionList.getOption();
			
			for (Iterator iter = m_dynamicBean.getAllPropertyInfos(); iter.hasNext(); ) {
				BeanPropertyInfo p = (BeanPropertyInfo) iter.next();
				if (!DynamicConfigBean.EXTERNAL_MUTABLE.equals(p.getName())) {
					NameValue nv = new NameValue();
					nv.setName(p.getName());
					nv.setValue((String)p.getPropertyValue(m_dynamicBean));
					list.add(nv);
				}
			}
			return optionList;
		}
		
		protected HandlerConfig getHandlerByName(List<HandlerConfig> handlers, String handlerName) {
			if (handlerName == null) return null;
			for (HandlerConfig hc : handlers) {
				if (handlerName.equals(hc.getName())) return hc;
			}
			return null;
		}

		protected OptionList getHandlerOptionList(HandlerConfig handler) {
			OptionList optionList = handler.getOptions();
			if (optionList == null) {
				optionList = new OptionList();
				handler.setOptions(optionList);
			}
			return optionList;
		}
	}
}
