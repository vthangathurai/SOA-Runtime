/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * Change Activity:
 * 
 * Reason      Name             Date           Description
 * ----------------------------------------------------------------------------                   
 * SOA 2.3     pkaliyamurthy    03/08/2009     Better System Error Classification.
 *  
 */

package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs.OpLevelErrorMetricDef;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs.SvcLevelErrorMetricDef;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;


/**
 * This helper class enables to create new metrics dynamically and register them in the {@link MetricsRegistry}
 * 
 * @author rpallikonda 
 */
public final class MetricsRegistrationHelper {
	
	private final static Map<ErrorData, List<SystemMetricDefs.OpLevelErrorMetricDef>> s_errorDataToGroupMetricMap = Collections
            .synchronizedMap(new WeakHashMap<ErrorData, List<SystemMetricDefs.OpLevelErrorMetricDef>>());
    private final static Map<String, GroupMetricDefWrapper> s_groupToMetricDefMap = Collections
            .synchronizedMap(new HashMap<String, GroupMetricDefWrapper>());
	
	private MetricsRegistrationHelper() {
		// Singleton
	}

	public static boolean registerMetricsForErrorDataGrps(ErrorData errData, String organization, Collection<String> errorgroups, MessageContext ctx ) {
		return registerMetricsForErrorDataGrps( errData, organization, errorgroups.toArray( new String[0] ), ctx );
	}
	
	public static boolean registerMetricsForErrorDataGrps(ErrorData errData, String organization, String errorgroups, MessageContext ctx ) {
		if (errorgroups == null)
            return false;
        String[] errGroups = errorgroups.split("\\s");
        return registerMetricsForErrorDataGrps(errData, organization, errGroups, ctx);
	}
	
	public static boolean registerMetricsForErrorDataGrps(ErrorData errData, String organization, String errorgroups) {
		return registerMetricsForErrorDataGrps( errData, organization, errorgroups, MessageContextAccessor.getContext() );
	}
	
	public static boolean registerMetricsForErrorDataGrps(ErrorData errData, String organization, String[] errGroups, MessageContext ctx ) {
		
		boolean isSuccess = true;
		List<SystemMetricDefs.OpLevelErrorMetricDef> groupMetricsArr = null;
		GroupMetricDefWrapper groupDefWrapper = null;

		// ctx cannot be null, even if the error was created from a handler
		if (ctx == null) return false;
		boolean isClientSide = ctx.getServiceId().isClientSide();
		
		if (s_errorDataToGroupMetricMap.containsKey(errData))
			return true;

		groupMetricsArr = new ArrayList<SystemMetricDefs.OpLevelErrorMetricDef>(errGroups.length);				
		for (int i = 0; i < errGroups.length; i++) {
			if (errGroups[i] == null || errGroups[i].length() == 0) continue;
			
			String errGroupName = calcGroupName(errData, organization, errGroups[i]);
			if (s_groupToMetricDefMap.containsKey(errGroupName)) {
				groupDefWrapper = s_groupToMetricDefMap.get(errGroupName);

				// register, if not registered already
				if (groupDefWrapper.registerGroupMetricDef(isClientSide)) {
					groupMetricsArr.add(groupDefWrapper.getMetric());
				} else {
					isSuccess = false;
					break;
				}
				continue;
			}
			
			groupDefWrapper = new GroupMetricDefWrapper();					
			if (!groupDefWrapper.createGroupMetricDef(errGroupName, isClientSide)) {
				isSuccess = false;
				break;
			}
			s_groupToMetricDefMap.put(errGroupName, groupDefWrapper);
			groupMetricsArr.add(groupDefWrapper.getMetric());
		}
		
		if (isSuccess && groupMetricsArr.size() > 0) {
			s_errorDataToGroupMetricMap.put(errData, groupMetricsArr);
		}
		return isSuccess;
	}	
	
	public static List<SystemMetricDefs.OpLevelErrorMetricDef> getGroupMetricDefs(ErrorData errData) {
		
		if (errData == null) return null;
		
		List<SystemMetricDefs.OpLevelErrorMetricDef> metrics = s_errorDataToGroupMetricMap.get(errData);
		
		if (metrics != null) {	
			metrics = Collections.unmodifiableList(metrics);
		}
		return metrics;
	}
	
	public static void remove(ErrorData errData) {
		s_errorDataToGroupMetricMap.remove(errData);
	}


	private static String calcGroupName(ErrorData errData, String organization, String grpName) {
		if (organization == null || errData.getDomain() == null || grpName == null)
			throw new NullPointerException();
		return organization + "." + errData.getDomain() + "." + grpName;
	}
	
	/*
	 * Wrapper for the ErrorGroup Metric Definition. This is needed as the 
	 * metrics registration for a particualar registry (client/server side) can 
	 * only be done in that context
	 */
	
	private static class GroupMetricDefWrapper {
	    
		private SystemMetricDefs.OpLevelErrorMetricDef groupDef = null;
		private boolean registeredClientSide = false;
		private boolean registeredServerSide = false;
		
		public GroupMetricDefWrapper() {
			// no-op
		}
		
		private void registerClientMetrics() throws ServiceException {
			if ( !registeredClientSide ) {
				MetricsRegistry.getClientInstance().registerMetric(groupDef.getSvcDef());
				MetricsRegistry.getClientInstance().registerMetric(groupDef);
				registeredClientSide = true;
			}			
		}
		
		private void registerServerMetrics()throws ServiceException {
			if ( !registeredServerSide ) {
				MetricsRegistry.getServerInstance().registerMetric(groupDef.getSvcDef());
				MetricsRegistry.getServerInstance().registerMetric(groupDef);
				registeredServerSide = true;
			}			
		}
		
		public SystemMetricDefs.OpLevelErrorMetricDef getMetric() {
			return groupDef;
		}
		
		private void registerMetrics(boolean clientSide) throws ServiceException{
			if (clientSide) {
				registerClientMetrics();
			} else {
				registerServerMetrics();
			}				
		}
		
		private void unRegisterMetrics(boolean clientSide){
			if (clientSide) {
					MetricsRegistry.getClientInstance().unregisterMetricByDef(groupDef.getSvcDef());
			} else {
					MetricsRegistry.getServerInstance().unregisterMetricByDef(groupDef.getSvcDef());
			}				
		}

		public boolean createGroupMetricDef(String errGroupName, boolean isClientSide) {
			//	Create Metrics for ServiceLevel and OpLevel
			SystemMetricDefs.SvcLevelErrorMetricDef svcMetricDef = new SystemMetricDefs.SvcLevelErrorMetricDef(errGroupName);
			groupDef = new SystemMetricDefs.OpLevelErrorMetricDef(svcMetricDef);
			if (groupDef == null) return false;
			try {
				registerMetrics(isClientSide);
			} catch (ServiceException e) {
				unRegisterMetrics(isClientSide);
				LogManager.getInstance(MetricsRegistrationHelper.class).log(Level.SEVERE,
						"Error registering metrics for errorGroupMetricDef " + " : " + e.toString(), e);
				groupDef = null;
			}
			
			return groupDef != null ? true: false; 						
		}
		
		public boolean registerGroupMetricDef(boolean isClientSide) {			
			boolean status = false;
			if (groupDef == null) return false;
			try {
				registerMetrics(isClientSide);
				status = true;
			} catch (ServiceException e) {
				unRegisterMetrics(isClientSide);
				status = false;
			}
			return status;
		}
		
	}

	/**
	 * Sub-domain metrics, both SvcLevel and OpLevel, are created based on the errorData passed.
	 * Duplicate registrations are taken care by MetricRegistry.
	 *  	 
	 * @param errorData
	 * @param isClientSide
	 * @return the MetricDef that was created and registered. <code>null</code>, if the registration fails.	   
	 */
    public static MetricDef createSubDomainMetrics(final ErrorData errorData, boolean isClientSide) {        
        String metricName = errorData.getDomain() + "." + errorData.getSubdomain();
        SvcLevelErrorMetricDef svcMetricDef = new SystemMetricDefs.SvcLevelErrorMetricDef(metricName);
        OpLevelErrorMetricDef metric = new OpLevelErrorMetricDef(svcMetricDef);
        MetricsRegistry registry = isClientSide ? MetricsRegistry.getClientInstance() : MetricsRegistry.getServerInstance();
        try {
            registry.registerMetrics(Arrays.asList(svcMetricDef, metric));
        } catch (ServiceException e) {
            registry.unregisterMetricsByDef(Arrays.asList(svcMetricDef, metric));
            LogManager.getInstance(MetricsRegistrationHelper.class).log(Level.SEVERE,
                    "Error registering metrics for Subdomain " + " : " + e.toString(), e);
            metric = null;
        }
        return metric;
    }
}
