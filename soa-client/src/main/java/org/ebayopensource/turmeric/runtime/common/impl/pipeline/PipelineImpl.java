/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.AverageMetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.LongSumMetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.Handler;
import org.ebayopensource.turmeric.runtime.common.pipeline.HandlerOptions;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.pipeline.PipelineMode;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;


/**
 * @author ichernyshev, smalladi
 */
public class PipelineImpl implements Pipeline {

	//private ServiceId m_serviceId;
	private PipelineMode m_pipelineMode;
	private PipelineHandlerConnectorImpl[] m_handlers;

	/**
	 * Create new pipeline instance according to the passed configuration
	 */
	public void init(InitContext ctx)
		throws ServiceException
	{
		ServiceId svcId = ctx.getServiceId();
		PipelineMode pipelineMode = ctx.getPipelineMode();
		List<HandlerOptions> handlerConfigs = ctx.getHandlerConfigs();
		ClassLoader cl = ctx.getClassLoader();

		int handlerCount = handlerConfigs.size();
		PipelineHandlerConnectorImpl[] handlers = new PipelineHandlerConnectorImpl[handlerCount];
		for (int i=0; i<handlerCount; i++) {
			HandlerOptions config = handlerConfigs.get(i);
			String name = config.getName();
			String className = config.getClassName();
			String fullName = svcId.getAdminName() + "." + name;

			boolean isContinueOnError = config.isContinueOnError();
			boolean isErrorSafe = config.isRunOnError();
			Map<String,String> options = config.getOptions();

			Handler handler = ReflectionUtils.createInstance(className, Handler.class, cl);

			HandlerInitContextImpl initCtx = new HandlerInitContextImpl(svcId, name, options);
			handler.init(initCtx);
			initCtx.kill();

			MetricValueAggregator metricTime = getMetric(svcId,
				name, pipelineMode, "Time", MonitoringLevel.FINE, MetricCategory.Timing,
				AverageMetricValue.class);
			MetricValueAggregator metricErrors = getMetric(svcId,
				name, pipelineMode, "Err", MonitoringLevel.FINE, MetricCategory.Error,
				LongSumMetricValue.class);

			handlers[i] = new PipelineHandlerConnectorImpl(handler, name, fullName,
				isContinueOnError, isErrorSafe, metricTime, metricErrors);
		}

		m_pipelineMode = pipelineMode;
		m_handlers = handlers;
	}

	private String getMetricName(String metricShortName, String handlerName, PipelineMode pipelineMode) {
		StringBuilder sb = new StringBuilder();
		sb.append("SoaFwk.");
		sb.append(metricShortName);
		sb.append('.');
		if (pipelineMode == PipelineMode.REQUEST) {
			sb.append("Request");
		} else {
			sb.append("Response");
		}
		sb.append("Pipeline.");

		for (int i=0; i<handlerName.length(); i++) {
			char c = handlerName.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
				(c >= '0' && c <= '9') || c == '_')
			{
				sb.append(c);
			} else {
				sb.append('_');
			}
		}

		return sb.toString();
	}

	private MetricValueAggregator getMetric(ServiceId svcId, String handlerName,
		PipelineMode pipelineMode, String metricShortName,
		MonitoringLevel level, MetricCategory category, Class<? extends MetricValue> valueClass)
		throws ServiceException
	{
		boolean isClientSide = svcId.isClientSide();

		MetricValueAggregator result = null;
		try {
			String metricName = getMetricName(metricShortName, handlerName, pipelineMode);
			MetricDef def = new MetricDef(metricName, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_DONT_CARE,
				level, category, valueClass);

			if (isClientSide) {
				MetricsRegistry.getClientInstance().registerMetric(def);
				result = MetricsCollector.getClientInstance().getMetricValue(metricName, svcId, null);
			} else {
				MetricsRegistry.getServerInstance().registerMetric(def);
				result = MetricsCollector.getServerInstance().getMetricValue(metricName, svcId, null);
			}
		} catch (Exception e) {
			LogManager.getInstance(this.getClass()).log(Level.WARNING,
				"Error registering metrics for handler " + handlerName + " in " +
				svcId.getAdminName() + " : " + e.toString(), e);
		}

		return result;
	}

	public void invoke(MessageContext ctx)
		throws ServiceException
	{
		for (int i=0; i<m_handlers.length; i++) {
			PipelineHandlerConnectorImpl handler = m_handlers[i];

			try {
				handler.invoke(ctx);
			} catch (ServiceException e) {
				handleError(ctx, handler, e);
			} catch (RuntimeException e) {
				handleError(ctx, handler, e);
			}
		}
	}

	private void handleError(MessageContext ctx, PipelineHandlerConnectorImpl handler, Throwable e) {
		LogManager.getInstance(PipelineImpl.class).log(Level.INFO,
			"Handler " + handler.getFullName() + " failed to execute due to: " + e.toString(), e);

		if (handler.isContinueOnError()) {
			ctx.addWarning(e);
		} else {
			ctx.addError(e);
		}
	}

	public PipelineMode getMode() {
		return m_pipelineMode;
	}

	private static class PipelineHandlerConnectorImpl
	{
		private final Handler m_handler;
		private final String m_name;
		private final String m_fullName;
		private final boolean m_isContinueOnError;
		private final boolean m_isErrorSafe;
		private final MetricValueAggregator m_metricTime;
		private final MetricValueAggregator m_metricErrors;

		public PipelineHandlerConnectorImpl(Handler handler,
			String name, String fullName,
			boolean isContinueOnError, boolean isErrorSafe,
			MetricValueAggregator metricTime,
			MetricValueAggregator metricErrors)
		{
			m_handler = handler;
			m_name = name;
			m_fullName = fullName;
			m_isContinueOnError = isContinueOnError;
			m_isErrorSafe = isErrorSafe;
			m_metricTime = metricTime;
			m_metricErrors = metricErrors;
		}

		public void invoke(MessageContext ctx)
			throws ServiceException
		{
			List<Throwable> errors = ctx.getErrorList();
			int origErrCount = (errors != null ? errors.size() : 0);
			/**
			 * The error count is now inclusive of RRE's
			 */
			origErrCount += ctx.hasResponseResidentErrors() ? ctx.getResponseResidentErrorList().size() : 0; 
			if (!m_isErrorSafe && origErrCount > 0) {
				// skip error-unsafe handlers if we have errors
				return;
			}

			if (m_metricTime != null && m_metricTime.isEnabled()) {
				long startTime = System.nanoTime();
				try {
					invokeHandler(ctx);
				} finally {
					long duration = System.nanoTime() - startTime;
					m_metricTime.update(duration);
				}
			} else {
				invokeHandler(ctx);
			}

			errors = ctx.getErrorList();
			int newErrCount = (errors != null ? errors.size() : 0);
			newErrCount += getResponseResidentErrorCount( ctx );
			if (newErrCount > origErrCount && m_metricErrors != null && m_metricErrors.isEnabled()) {
				m_metricErrors.update(1);
			}
		}

		/**
		 * Fetch the number of "errors" in the RRE
		 * @param ctx
		 * @return
		 */
		private int getResponseResidentErrorCount( MessageContext ctx ) {
			int count = 0;
			if ( ctx.hasResponseResidentErrors() ) {
				for ( ErrorData errorData : ctx.getResponseResidentErrorList() )
					if ( errorData.getSeverity() == ErrorSeverity.ERROR )
						++count;
			}
			return count;
		}

		public String getName() {
			return m_name;
		}

		public String getFullName() {
			return m_fullName;
		}

		public String getHandlerType() {
			return m_handler.getClass().getName();
		}

		public boolean isContinueOnError() {
			return m_isContinueOnError;
		}

		public boolean isErrorSafe() {
			return m_isErrorSafe;
		}

		private void invokeHandler(MessageContext ctx)
			throws ServiceException
		{
			m_handler.invoke(ctx);
		}
	}
}
