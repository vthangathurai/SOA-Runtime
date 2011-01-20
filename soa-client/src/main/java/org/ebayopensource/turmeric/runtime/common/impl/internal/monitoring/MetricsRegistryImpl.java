/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.ebay.kernel.initialization.InitializationException;

/**
 * @author wdeng
 * @author ichernyshev
 */
final class MetricsRegistryImpl extends MetricsRegistry {

	private final boolean m_isClientSide;
	private MetricsCollectorImpl m_collector;
	private List<MetricDef> m_metricDefs = new ArrayList<MetricDef>();

	private MetricsRegistryImpl(boolean isClientSide) {
		m_isClientSide = isClientSide;
	}

	static void createClientInstance() {
		MetricsRegistryImpl result = createRegistryInstance(true);
		setClientInstance(result);
		MetricsCollectorImpl.setClientInstance(result.m_collector);
	}

	static void createServerInstance() {
		MetricsRegistryImpl result = createRegistryInstance(false);
		setServerInstance(result);
		MetricsCollectorImpl.setServerInstance(result.m_collector);
	}

	static MetricsRegistryImpl createRegistryInstance(boolean isClientSide) {
		MetricsRegistryImpl registry = new MetricsRegistryImpl(isClientSide);
		MetricsCollectorImpl collector = new MetricsCollectorImpl(registry);
		registry.m_collector = collector;
		return registry;
	}

	boolean isClientSide() {
		return m_isClientSide;
	}

	/**
	 * RegisterMetric is expected to be called at init time
	 */
	@Override
	public void registerMetrics(List<MetricDef> metricDefs) throws ServiceException {
		for (MetricDef def: metricDefs) {
			registerMetric(def);
		}
	}

	/**
	 * The registerMetric method register the MetricDefinitions. 
	 * The MetricId can contain wildcard in its serviceName, 
	 * operationName, and/or usecaseName to indicate an apply-all.  
	 * or null to indicate don't-care.
	 *  
	 * registerMetric is expected to be called at init time.
	 */
	@Override
	public synchronized void registerMetric(MetricDef metricDef) throws ServiceException {	
		boolean alreadyRegistered = validateMetricDef(metricDef);
		if (alreadyRegistered) {
			return;
		}

		m_metricDefs.add(metricDef);
	}

	@Override
	public List<MetricDef> unregisterMetricsByDef(List<MetricDef> metricDefs) {
		ArrayList<MetricDef> result = new ArrayList<MetricDef>(metricDefs.size()); 
		for (MetricDef def: metricDefs) {
			MetricDef def2 = unregisterMetricByDef(def);
			if (def2 != null) {
				result.add(def2);
			}
		}
		return result;
	}

	@Override
	public MetricDef unregisterMetricByDef(MetricDef metricDef) {
		return unregisterMetric(metricDef.getMetricName(),
			metricDef.getServiceName(), metricDef.getOperationName());
	}

	/**
	 * What is the behavior when some of the classifiers are wild card or null?
	 */
	@Override
	public MetricDef unregisterMetric(String metricName, QName serviceName, String operationName) {
		MetricDef def = null;
		synchronized (this) {
			for (Iterator<MetricDef> it=m_metricDefs.iterator(); it.hasNext();) {
				MetricDef def2 = it.next();

				if (!def2.getMetricName().equals(metricName) ||
					!def2.getServiceName().equals(serviceName) ||
					!BindingUtils.sameObject(def2.getOperationName(), operationName))
				{
					continue;
				}

				it.remove();
				def = def2;
				break;
			}
		}

		if (def != null) {
			m_collector.removeMetricValues(def);
		}

		return def;
	}

	/**
	 * Check if the metricDef is registered.  If the metricDef has conflict with
	 * existing metricDef,  throws ServiceException.  
	 * 
	 * Returns the existing registered MetricDef that contains the given
	 * MetricDef.
	 */
	private boolean validateMetricDef(MetricDef newDef) throws ServiceException {
		MetricDef overlappingDef = null;
		boolean result = false;
		for (MetricDef def: m_metricDefs) {
			if (!def.getMetricName().equals(newDef.getMetricName())) {
				// different name, ignoring
				continue;
			}

			if (def.equals(newDef)) {
				// equivalent redefinition, ignore
				result = true;
				continue;
			}

			QName svcName = def.getServiceName();
			QName newSvcName = newDef.getServiceName();
			if (MetricDef.SVC_APPLY_TO_ALL.equals(svcName) ||
				MetricDef.SVC_APPLY_TO_ALL.equals(newSvcName) ||
				svcName.equals(newSvcName))
			{
				// service names would overlap
				overlappingDef = def;
				break;
			}

			String opName = def.getOperationName();
			String newOpName = newDef.getOperationName();
			if (opName == MetricDef.OP_DONT_CARE || opName.equals(MetricDef.OP_APPLY_TO_ALL) ||
				newOpName == MetricDef.OP_DONT_CARE || newOpName.equals(MetricDef.OP_APPLY_TO_ALL) ||
				opName.equals(newOpName))
			{
				// operation names would overlap
				overlappingDef = def;
				break;
			}
		}

		if (overlappingDef != null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_METRICS_DEFINITION_CONFLICT,
					ErrorConstants.ERRORDOMAIN, new Object[] {newDef.toString(), overlappingDef.toString()}));
		}

		return result;
	}

	/**
	 * Gets the MetricDef for the given MetricId.  
	 * If the definition don't exist in the registry but the id 
	 * is included in a definition with wildcards, A new 
	 * MetricDef will be created, registered and return.
	 * 
	 * @param metricId A metric id without any wildcard and/or don't-care
	 */
	@Override
	public synchronized MetricDef findMetricDef(MetricId metricId) {
		for (MetricDef def: m_metricDefs) {
			if (matchDef(def, metricId)) {
				return def;
			}
		}

		return null;
	}

	/**
	 * otherId is contained if it is the same as this or this is a
	 * wildcard id that contains otherId.
	 */
	private boolean matchDef(MetricDef def, MetricId id) {
		if (!def.getMetricName().equals(id.getMetricName())) {
			// metric name must be the same.
			return false;
		}

		QName svcName = def.getServiceName();
		if (!MetricDef.SVC_APPLY_TO_ALL.equals(svcName)) {
			BaseServiceDescFactory serviceFactory;
			if (m_isClientSide) {
				serviceFactory = BaseServiceDescFactory.getClientInstance();
			} else {
				serviceFactory = BaseServiceDescFactory.getServerInstance();
			}

			QName idQName = serviceFactory.findKnownQNameByAdminName(id.getAdminName());

			if (idQName == null || !svcName.equals(idQName)) {
				// we accept only specific service name and it's not a match
				return false;
			}
		}

		String defOpName = def.getOperationName();
		if (defOpName == MetricDef.OP_DONT_CARE || defOpName.equals(MetricDef.OP_APPLY_TO_ALL)) {
			return true;
		}

		String opName = id.getOperationName();
		return (opName != null && defOpName.equals(opName));
	}

	/**
	 * Registers all the MetricDefs defined in a class as public static
	 * and final constants.
	 */
	@Override
	public void registerAllMetricsForClass(Class clazz) {
		try {
			Field[] fields = clazz.getFields();
			for (int i=0; i<fields.length; i++) {
				Field field = fields[i];
				int modifier = field.getModifiers();
				if (!MetricDef.class.isAssignableFrom(field.getType()) ||
					!Modifier.isFinal(modifier) ||
					!Modifier.isStatic(modifier) ||
					!Modifier.isPublic(modifier))
				{
					continue;
				}

				MetricDef def = (MetricDef)field.get(null);
				if (def != null) {
					registerMetric(def);
				}
			}
		} catch (SecurityException e) {
			throw new InitializationException(e);
		} catch (IllegalAccessException e) {
			throw new InitializationException(e);
		} catch (ServiceException e) {
			throw new InitializationException(e);
		}
	}
}
