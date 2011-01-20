/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;


/**
 * @author ichernyshev
 */
public abstract class BaseServiceContext<T extends ServiceDesc> implements ServiceContext {
	protected final T m_serviceDesc;

	protected BaseServiceContext(T serviceDesc) {
		if (serviceDesc == null) {
			throw new NullPointerException();
		}

		m_serviceDesc = serviceDesc;
	}

	public final Collection<DataBindingDesc> getAllDataBindings() {
		return m_serviceDesc.getAllDataBindings();
	}

	public final Collection<ServiceOperationDesc> getAllOperations() {
		return m_serviceDesc.getAllOperations();
	}

	public final ClassLoader getClassLoader() {
		return m_serviceDesc.getClassLoader();
	}

	public final DataBindingDesc getDataBindingDesc(String name) {
		return m_serviceDesc.getDataBindingDesc(name);
	}

	public final String getAdminName() {
		return m_serviceDesc.getAdminName();
	}

	public final QName getServiceQName() {
		return m_serviceDesc.getServiceQName();
	}

	public final ServiceId getServiceId() {
		return m_serviceDesc.getServiceId();
	}

	public final boolean isClientSide() {
		return m_serviceDesc.getServiceId().isClientSide();
	}

	public final ServiceOperationDesc getOperation(String name) {
		return m_serviceDesc.getOperation(name);
	}

	public final ServiceTypeMappings getTypeMappings() {
		return m_serviceDesc.getTypeMappings();
	}

	public final boolean isFallback() {
		return m_serviceDesc.isFallback();
	}

	public final String getServiceIntfClassName() {
		Class intfClass = m_serviceDesc.getServiceInterfaceClass();
		if (intfClass != null) {
			return intfClass.getName();
		}

		return null;
	}
	
	public final List<String> getServiceLayerNames() {
		return m_serviceDesc.getServiceLayerNames();
	}
}
