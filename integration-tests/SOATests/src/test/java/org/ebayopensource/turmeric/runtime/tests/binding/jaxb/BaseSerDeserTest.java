/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.junit.logging.UKernelLoggingUtils;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Before;


/**
 * @author wdeng
 *
 */
public abstract class BaseSerDeserTest extends AbstractWithServerTest {
	private static Map<String, String> DEFAULT_OPTIONS;
	private static Class<?>[] DEFAULT_ROOT_CLASSES_FOR_TEST = new Class[] {MyMessage.class, ErrorMessage.class};

	static {
		DEFAULT_OPTIONS = new HashMap<String, String>();
		DEFAULT_OPTIONS.put("elementFormDefault", XmlNsForm.UNQUALIFIED.name());
	}

	protected SerializerFactory m_serFactory;
	protected DeserializerFactory m_deserFactory;
	protected Class<?>[] m_rootClasses;

	public BaseSerDeserTest() {
		m_rootClasses = DEFAULT_ROOT_CLASSES_FOR_TEST;
		UKernelLoggingUtils.initTesting();
	}
	
	protected BaseSerDeserTest(Class<?>[] rootClasses) {
		m_rootClasses = rootClasses;
		UKernelLoggingUtils.initTesting();
	}

	@Before
	public void setUp() throws Exception {
		if (m_serFactory != null)
			m_serFactory.init(new TestSerInitContext());
		if (m_deserFactory != null)
			m_deserFactory.init(new TestDeserInitContext());
	}
	
	private class TestInitContext {
		Map<String, String> m_options;

		TestInitContext() {
			this(DEFAULT_OPTIONS);
		}

		TestInitContext(Map<String, String> options) {
			m_options = options;
		}

		public ServiceId getServiceId() {
			return new ClientServiceId("MySerDeserTest", "");
		}

		public Map<String,String> getOptions() {
			return m_options;
		}

		public Class<?>[] getRootClasses() {
			return m_rootClasses;
		}
	}

	protected final class TestSerInitContext extends TestInitContext
	implements ISerializerFactory.InitContext {
		TestSerInitContext() {
			this(DEFAULT_OPTIONS);
		}

		TestSerInitContext(Map<String, String> options) {
			m_options = options;
		}
	}


	protected final class TestDeserInitContext extends TestInitContext
	implements IDeserializerFactory.InitContext {
		TestDeserInitContext() {
			this(DEFAULT_OPTIONS);
		}

		TestDeserInitContext(Map<String, String> options) {
			m_options = options;
		}

		@Override
		public Schema getUpaAwareMasterSchema() {
			return null;
		}
	}
}
