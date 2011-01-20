/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif;

import static org.junit.Assert.*;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientServiceConfigBeanManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientServiceInvokerConfigBean;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientServiceTransportConfigBean;
import org.ebayopensource.turmeric.runtime.tests.common.sif.BaseCallTest;
import org.junit.Rule;
import org.junit.rules.Verifier;

import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.configuration.AttributeNotFoundException;
import com.ebay.kernel.configuration.ConfigurationAttribute;
import com.ebay.kernel.configuration.ConfigurationManager;

/* this test should run after ServiceChainingTest */

public class ClientServiceConfigBeanTest extends BaseCallTest {
	private static final String INVOKER_CONFIG_ID = SOAConstants.CONFIG_BEAN_PREFIX_CLIENT
			+ "test1" + "." + "chaining" + ".Invoker";

	private static final String TRANSPORT_CONFIG_ID = SOAConstants.CONFIG_BEAN_PREFIX_CLIENT
			+ "test1" + "." + "chaining" + ".Transport.HTTP11";
 
	private static final Object[][] INVOKER_PROPERTIES = {
			{ ClientServiceInvokerConfigBean.REQUEST_BINDING, "Nv" },
			{ ClientServiceInvokerConfigBean.RESPONSE_BINDING, "JSon" },
			{ ClientServiceInvokerConfigBean.PREFERRED_TRANSPORT_NAME, "HTTP10" },
			{ ClientServiceInvokerConfigBean.MESSAGE_PROTOCOL_NAME, "SOAP11" },
			{ ClientServiceInvokerConfigBean.APP_LEVEL_NUM_RETRIES, "2" },
			{ ClientServiceInvokerConfigBean.USE_CASE, "newUseCase" },
			{ ClientServiceInvokerConfigBean.SERVICE_URL,
					"http://10.254.30.193:8080/ws/spf" },
			{ ClientServiceInvokerConfigBean.USE_REST, "true" },
			{ ClientServiceInvokerConfigBean.MAX_URL_REST_LEN, "1994" } };

	private static final Object[][] TRANSPORT_PROPERTIES = {
			{ ClientServiceTransportConfigBean.NUM_CONNECT_RETRIES, "2" },
			{ ClientServiceTransportConfigBean.CONNECTION_TIMEOUT, "1996" },
			{ ClientServiceTransportConfigBean.RECEIVE_TIMEOUT, "1997" },
			{ ClientServiceTransportConfigBean.INVOCATION_TIMEOUT, "1998" },
			{ ClientServiceTransportConfigBean.SKIP_SERIALIZATION, "false" },
			{ ClientServiceTransportConfigBean.USE_DETACHED_LOCAL_BINDING, "false" } };

	public ClientServiceConfigBeanTest() throws Exception {
		super("configremote");
	}
	
	protected Test1Driver createDriver() throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME, m_clientName, CONFIG_ROOT, serverUri.toURL(), 
				null, null, Test1Driver.OP_NAME_serviceChainingOperation);
		
		return driver;
	}
	
	@Rule
	public Verifier verifier = new Verifier() {
		@Override
		protected void verify() throws Throwable {
			try{
				verifyInvokerOptionsTest();
			}catch(Throwable t){
				t.printStackTrace();
				//ignore
			}
			try{
				verifyTransportOptionsTest();
			}catch(Throwable t){
					//ignore
				t.printStackTrace();
		    }
		}
	};

	public void verifyInvokerOptionsTest() throws Exception {
		for (int i = 0; i < INVOKER_PROPERTIES.length; i++) {
			validateInvokerOption((BeanPropertyInfo) INVOKER_PROPERTIES[i][0],
					(String) INVOKER_PROPERTIES[i][1]);
		}
		assertNotNull(ClientServiceConfigBeanManager.getInvokerInstance(
				"test1", "configremote",null));
	}

	public void verifyTransportOptionsTest() throws Exception {
		for (int i = 0; i < TRANSPORT_PROPERTIES.length; i++) {
			validateTransportOption(
					(BeanPropertyInfo) TRANSPORT_PROPERTIES[i][0],
					(String) TRANSPORT_PROPERTIES[i][1]);
		}
		assertNotNull(ClientServiceConfigBeanManager.getTransportInstance(
				"test1", "configremote", null,"HTTP11"));
	}

	public static Object setConfigProp(String configId,
			BeanPropertyInfo propInfo, Object value) throws Exception {
		return setConfigProp(configId, propInfo, value, true);
	}

	public static Object setConfigProp(String configId, String propName,
			Object value) throws Exception {
		return setConfigProp(configId, propName, value, true);
	}

	public static Object setConfigProp(String configId,
			BeanPropertyInfo propInfo, Object value, boolean verify)
			throws Exception {
		String propName = propInfo.getName();
		return setConfigProp(configId, propName, value, verify);
	}

	public static Object setConfigProp(String configId, String propName,
			Object value, boolean verify) throws Exception {
		ConfigurationAttribute attribute = new ConfigurationAttribute(propName,
				value);
		ConfigurationManager cm = ConfigurationManager.getInstance();
		Object oldValue = null;
		try {
			oldValue = cm.getAttributeValue(configId, propName);
		} catch (AttributeNotFoundException e) {
		}
		cm.setAttributeValue(configId, attribute);

		Thread.sleep(20);

		Object newValue = cm.getAttributeValue(configId, propName);
		if (value == newValue) {
			return oldValue;
		}
		if (verify) {
			if (value == null || newValue == null || !value.equals(newValue)) {
				throw new RuntimeException("Unable to change property "
						+ propName + " for " + configId + ". Requested value: "
						+ value + ". Result value: " + newValue);
			}
		}
		return oldValue;
	}

	private void validateInvokerOption(BeanPropertyInfo property,
			String newValue) throws Exception {
		validateOption(INVOKER_CONFIG_ID, property, newValue);
	}

	private void validateTransportOption(BeanPropertyInfo property,
			String newValue) throws Exception {
		validateOption(TRANSPORT_CONFIG_ID, property, newValue);
	}

	private void validateOption(String category, BeanPropertyInfo property,
			String newValue) throws Exception {
		String oldValue = String.valueOf(setConfigProp(category, property,
				newValue));
		System.out.println(property.getName() + ", Oldvalue=" + oldValue
				+ ", newValue=" + newValue);
		assertTrue(!newValue.equals(oldValue));
	}
}
