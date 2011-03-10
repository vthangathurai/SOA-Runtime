/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.management.Descriptor;
import javax.management.modelmbean.DescriptorSupport;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.NameValue;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;

import com.ebay.kernel.bean.configuration.BaseConfigBean;
import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.bean.configuration.DynamicConfigBean;
import com.ebay.kernel.configuration.ConfigurationAttribute;
import com.ebay.kernel.configuration.ConfigurationAttributeList;
import com.ebay.kernel.configuration.ConfigurationConstants;
import com.ebay.kernel.configuration.ConfigurationException;
import com.ebay.kernel.configuration.ConfigurationManager;
import com.ebay.kernel.configuration.ObjectTypeConverter;
import com.ebay.kernel.context.RuntimeContext;
import com.ebay.kernel.util.JdkUtil;

/**
 * @author idralyuk
 */
public abstract class ServiceConfigBean extends BaseConfigBean {

	protected final String m_adminName;
	public static final String PERSIST_DIR = "config/soa/bean-settings/";
	public static final String PERSIST_DIR_WHEN_CONFIG_IS_NULL = "ConfigRoot/WEB-INF/config/resources/" + PERSIST_DIR;

	static {
		JdkUtil.forceInit(StringListObjectTypeConverter.class);
		JdkUtil.forceInit(StringList.class);
		ObjectTypeConverter.addTypeConverter(StringList.class.getName(), new StringListObjectTypeConverter());
	}

	protected ServiceConfigBean(ServiceConfigHolder config, String category)
			throws ConfigCategoryCreateException {

		m_adminName = config.getAdminName();

		// get defaults from config
		setDefaultsFromConfig(config);

		final String persistFile = getPersistDirName() + m_adminName + "." + category + "-persist.xml";

		final BeanConfigCategoryInfo beanInfo = BeanConfigCategoryInfo
				.createBeanConfigCategoryInfo(getCategoryId(category), null,
						SOAConstants.CONFIG_BEAN_GROUP_SERVER, true, // persistent
						true, // ops managable
						persistFile, // persistent file
						getDescription(category), // description
						true // return an existing one
				);

		loadDefaultOverrides(beanInfo, Service.class, getDefConfigFile(category));
		loadDefaultOverrides(beanInfo, Service.class, getConfigFile(category));

		init(beanInfo, true);

		updateConfigHolder(config);
	}

	static DynamicConfigBean createDynamicConfigBean(ServiceConfigHolder config, String category)
		throws ConfigCategoryCreateException {

		final String persistFile = getPersistDirName() + config.getAdminName() + "." + category + "-persist.xml";

		String categoryId = getCategoryId(config.getAdminName(), category);
		
		final BeanConfigCategoryInfo beanInfo = BeanConfigCategoryInfo
		.createBeanConfigCategoryInfo(categoryId, null,
				SOAConstants.CONFIG_BEAN_GROUP_SERVER, true, // persistent
				true, // ops managable
				persistFile, // persistent file
				getDescription(config.getAdminName(), category), // description
				true // return an existing one
		);

		DynamicConfigBean configBean = new DynamicConfigBean(beanInfo);
		configBean.setExternalMutable();

		return configBean;
	}
	
	static void initDynamicBeanInfo(DynamicConfigBean dBean, OptionList options) 
		throws ConfigurationException
	{	
		if (options != null) {
			List<NameValue> list = options.getOption();
			if (list == null) {
				return;
			}

			ConfigurationAttributeList attrsForUpdate = new ConfigurationAttributeList();

			for (NameValue nv : list) {
				if(!dBean.hasProperty(nv.getName()))
				{
					// this is the attribute value...
					ConfigurationAttribute newAttribute = new ConfigurationAttribute(
							nv.getName(), nv.getValue());
					newAttribute.setMBeanAttributeInfo(null); // Give us a defualt
																// one
					Descriptor attrDescriptor = new DescriptorSupport();
					attrDescriptor.setField("name", newAttribute.getName());
					attrDescriptor.setField("descriptorType", "attribute");
					attrDescriptor.setField("displayName", newAttribute.getName());
					attrDescriptor.setField(
							ConfigurationAttribute.CONFIG_PERSIST_TYPE,
							ConfigurationAttribute.CONFIG_PERSIST_TYPE_NEVER);
					newAttribute.getMBeanAttributeInfo().setDescriptor(
							attrDescriptor);
	
					attrsForUpdate.add(newAttribute);
				}
			}

			if(attrsForUpdate.size() > 0)
			{
				ConfigurationManager configMgr = ConfigurationManager.getInstance();
	
				String categoryId = dBean.getConfigCategoryId();
				configMgr.setAttributeValues(categoryId, attrsForUpdate, true);
			}
		}
	}
	
	public String getAdminName() {
		return m_adminName;
	}

	private String getCategoryId(String category) {
		return SOAConstants.CONFIG_BEAN_PREFIX_SERVER + m_adminName + "." + category;
	}

	private static String getCategoryId(String adminName, String category) {
		return SOAConstants.CONFIG_BEAN_PREFIX_SERVER + adminName + "." + category;
	}

	private String getDescription(String category) {
		return "SOA Service " + category + " Config for " + m_adminName;
	}

	private static String getDescription(String adminName, String category) {
		return "SOA Service " + category + " Config for " + adminName;
	}

	private String getDefConfigFile(String category) {
		return "Service" + category + m_adminName + "DefConfig.xml";
	}

	private String getConfigFile(String category) {
		return "Service" + category + m_adminName + "Config.xml";
	}

	public void addPropertyToConfigMng(BeanPropertyInfo propertyInfo, Object newValue){
		getConfigManagementAdapter().setManagedAttribute(propertyInfo, newValue);
	}

	public void removePropertyFromConfMng(BeanPropertyInfo propertyInfo){
		getConfigManagementAdapter().removeManagedAttribute(propertyInfo);
	}

	abstract protected void setDefaultsFromConfig(ServiceConfigHolder config);

	abstract protected void updateConfigHolder(ServiceConfigHolder config);

	public static class StringList extends ArrayList {

		private static ObjectTypeConverter s_converter = new StringListObjectTypeConverter();

	    public StringList() {
	    	super();
	    }

		public StringList(int initialCapacity) {
	    	super(initialCapacity);
	    }

		public StringList(Collection c) {
	    	super(c.size());
	    	addAll(c);
	    }

		public ObjectTypeConverter getTypeConverter() {
			return s_converter;
		}
	}

	public static class StringListObjectTypeConverter extends ObjectTypeConverter {

		@Override
		public Object convert(String strVal) {
			if ("null".equalsIgnoreCase(strVal)) {
				return null;
			}
			if ("[]".equals(strVal)) return new StringList(0);
			StringTokenizer st = new StringTokenizer(strVal, "[,]");
			List result = new StringList(st.countTokens());
			while (st.hasMoreTokens()) result.add(st.nextToken().trim());
			return result;
		}
		@Override
		public String getStringValue(Object obj) {
			if (obj == null) return "null";
			Iterator i = ((List)obj).iterator();
			if (! i.hasNext()) return "";
			Object o;
			StringBuilder sb = new StringBuilder();
			while (true) {
				o = i.next();
				sb.append(o);
				if (! i.hasNext()) return sb.toString();
				sb.append(",");
			}
		}
	}

	static String getPersistDirName() {
		String dirName = null;
		try {
			URL resourceURL = RuntimeContext.getResourceRoot();
			if (resourceURL != null) {
				dirName = resourceURL.getFile();
			} else {
				dirName = System.getProperty(ConfigurationConstants.PERSIST_CONFIG_BASE_DIR);
			}

			dirName = (dirName == null) ? PERSIST_DIR_WHEN_CONFIG_IS_NULL : dirName + PERSIST_DIR;
			File dir = new File(dirName);
			if (!dir.exists()) {
				boolean created = dir.mkdirs();
				if (!created) {
					throw new RuntimeException("Unable to create persist directory: " + dirName);
				}
			}
			return dirName;
		} catch (Exception e) {
			throw new RuntimeException("Cannot create persist directory: " + dirName, e);
		}
	}
}
