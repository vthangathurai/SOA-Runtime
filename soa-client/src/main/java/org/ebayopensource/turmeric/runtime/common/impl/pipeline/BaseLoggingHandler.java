/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ErrorDataLoggingRegistry;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorParameter;


/**
 * @author ichernyshev
 * @author rmurphy
 */
public abstract class BaseLoggingHandler implements LoggingHandler {

	private boolean m_isClientSide;
	private Logger m_logger;
	private PayloadLoggingHelper m_payloadHelper;
	private Level m_callEventLogLevel = Level.OFF;
	private Level m_inputErrorLogLevel = Level.INFO;
	private Level m_appErrorLogLevel = Level.SEVERE;
	private Level m_errorLogLevel = Level.SEVERE;
	private Level m_warningLogLevel = Level.WARNING;
	private boolean m_shouldLogCalls;
	private boolean m_logRequestUrl;
	private boolean m_logHeaders;
	
	public static final String PAYLOADLOG_ON ="on";
	public static final String PAYLOADLOG_OFF ="off";
	public static final String PAYLOADLOG_FULL ="full";
	public static final String PAYLOADLOG_ERRORONLY ="errorOnly";
	
	public static final String REQUEST_PAYLOAD_LOG_LEVEL="request-payload-log-level";
	public static final String RESPONSE_PAYLOAD_LOG_LEVEL="response-payload-log-level";
	
	/**
	 * Used in client requests to log the metrics for the configured logging handlers
	 */
	private static final String ENABLE_LOGGING_METRICS_HEADER = SOAHeaders.SYS_PREFIX + "LOGGING-METRICS";

	
	private static ThreadLocal<Long> s_totalTime = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return 0L;
        }
	};

	public void init(InitContext ctx)
		throws ServiceException
	{
		ServiceId svcId = ctx.getServiceId();
		Map<String,String> options = ctx.getOptions();

		m_isClientSide = svcId.isClientSide();

		m_logger = LogManager.getInstance(this.getClass());

		m_payloadHelper = new PayloadLoggingHelper(svcId, options, m_logger);

		String value = options.get("log-level-call-event");
		if (value != null) {
			try {
				m_callEventLogLevel = Level.parse(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid log-level-call-event: " + value}), e);
			}
		}

		m_shouldLogCalls = (m_callEventLogLevel != Level.OFF);

		value = options.get("log-level-errors");
		if (value != null) {
			try {
				m_errorLogLevel = Level.parse(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid log-level-errors: " + value}), e);
			}
		}

		value = options.get("log-level-input-errors");
		if (value != null) {
			try {
				m_inputErrorLogLevel = Level.parse(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid log-level-input-errors: " + value}), e);
			}
		}

		value = options.get("log-level-app-errors");
		if (value != null) {
			try {
				m_appErrorLogLevel = Level.parse(value);
/*				// Make sure the app error log level is not higher then WARNING
				if (m_appErrorLogLevel.equals(Level.SEVERE)) {
					m_appErrorLogLevel = Level.WARNING;
				}
				 */} catch (Exception e) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] { "Invalid log-level-app-errors: " + value }), e);
			}
		}

		value = options.get("log-level-warnings");
		if (value != null) {
			try {
				m_warningLogLevel = Level.parse(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid log-level-warnings: " + value}), e);
			}
		}

		value = options.get("log-request-url");
		if (value != null) {
			try {
				m_logRequestUrl = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid log-request-url: " + value}), e);
			}
		}

		value = options.get("log-headers");
		if (value != null) {
			try {
				m_logHeaders = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid log-headers: " + value}), e);
			}
		}

		ctx.setSupportsErrorLogging();
	}

	protected final Level getAppErrorLogLevel() {
		return m_appErrorLogLevel;
	}

	protected final Logger getLogger() {
		return m_logger;
	}

	public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage)
		throws ServiceException
	{
		String requestPayloadBeanValue = getRequestPayloadLog(ctx);
		String responsePayloadBeanValue = getResponsePayloadLog(ctx);
		
		if (m_shouldLogCalls) {
			StringBuilder sb = new StringBuilder();
			if (stage == LoggingHandlerStage.REQUEST_STARTED) {
				addFullCallInfo(ctx, LoggingHandlerStage.REQUEST_STARTED, ", ", sb);
			} else {
				addCallInfo(ctx, stage, ", ", sb);
			}
			logEventInternal(ctx, stage, sb.toString());
		}

		if (stage == LoggingHandlerStage.REQUEST_STARTED) {
			if (!m_isClientSide) {
				if (m_logRequestUrl) {
					logRequestUrl(ctx, stage);
				}

				if (m_logHeaders) {
					logReqestHeaders(ctx, stage);
				}
			}
	
			if (!requestPayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_OFF))
			{
				m_payloadHelper.startRequestRecording(ctx);
			}
		} else if (stage == LoggingHandlerStage.BEFORE_REQUEST_DISPATCH) {
			if (!m_isClientSide) {
				if (requestPayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_ON) ||
						(requestPayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_ERRORONLY) 
								&& (ctx.hasErrors() || ctx.hasResponseResidentErrors())))
					m_payloadHelper.logRequestPayload(ctx);
			}
		} else if (stage == LoggingHandlerStage.RESPONSE_STARTED) {
			if (!responsePayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_OFF))
			{
				m_payloadHelper.startResponseRecording(ctx);
			}
		} else if (stage == LoggingHandlerStage.RESPONSE_COMPLETE) {
	
			if (m_isClientSide) {
				if (requestPayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_ON) ||
						(requestPayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_ERRORONLY) 
								&& (ctx.hasErrors() || ctx.hasResponseResidentErrors())))
					m_payloadHelper.logRequestPayload(ctx);
				if (m_logHeaders) {
					logResponseHeaders(ctx, stage);
				}
			}

			if (responsePayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_ON) ||
					(responsePayloadBeanValue.equalsIgnoreCase(PAYLOADLOG_ERRORONLY) 
							&& (ctx.hasErrors() || ctx.hasResponseResidentErrors())))
			{
				m_payloadHelper.logResponsePayload(ctx);
			} 
		}
	}

	private void logReqestHeaders(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
		Map<String, String> requestHeaders = ctx.getRequestMessage().getTransportHeaders();

		logHeaders(ctx, stage, requestHeaders, "Request headers: ");
	}

	private void logResponseHeaders(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
		Map<String, String> responseHeaders = ctx.getResponseMessage().getTransportHeaders();

		logHeaders(ctx, stage, responseHeaders, "Response headers: ");
	}

	private void logHeaders(MessageContext ctx, LoggingHandlerStage stage,
			Map<String, String> inputHeaders, String title) throws ServiceException {
		if (null == inputHeaders || inputHeaders.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder(title);
		Iterator<String> iter = inputHeaders.keySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			String value = inputHeaders.get(name);

			sb.append(name).append(": ").append(value);
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}

		logEventInternal(ctx, stage, sb.toString());
	}

	private static final int URI_LEN_LIMIT = 2048;
	private void logRequestUrl(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
		String uriStr = ctx.getRequestUri() == null ? "none" : ctx.getRequestUri();
		String uri = uriStr.substring(0, (uriStr.length() > URI_LEN_LIMIT ? URI_LEN_LIMIT : uriStr.length()));
		logEventInternal(ctx, stage, "Request Uri: " + uri);
	}

	protected void addCallInfo(MessageContext ctx, LoggingHandlerStage stage, String separator, StringBuilder sb) throws ServiceException {
		if (sb.length() != 0) {
			sb.append(separator);
		}
		sb.append(m_isClientSide ? "Client" : "Server");
		sb.append(separator);
		sb.append(stage);
		String requestGuid = ctx.getRequestGuid();
		if (requestGuid != null) {
			sb.append(separator);
			sb.append("GUID=");
			sb.append(requestGuid);
		}
	}

	protected void addFullCallInfo(MessageContext ctx, LoggingHandlerStage stage, String separator, StringBuilder sb) throws ServiceException {

		addCallInfo(ctx, stage, separator, sb);
		if (sb.length() != 0) {
			sb.append(separator);
		}
		sb.append("Svc=");
		sb.append(ctx.getAdminName());

		String version = ctx.getInvokerVersion();
		sb.append(separator);
		sb.append("Ver=");
		sb.append(version != null? version : "");

		sb.append(separator);
		sb.append("Op=");
		sb.append(ctx.getOperationName());


		String useCase = ""; // TODO: get the real use case
		if (useCase != null && useCase.length() != 0 && !useCase.equals("default")) {
			sb.append(separator);
			sb.append("Use Case=");
			sb.append(useCase);
		}
	}
	
	public void logResponseResidentError(MessageContext ctx, ErrorData errorData) throws ServiceException {
		long startTime = System.nanoTime();
		try {
			Level logLevel = getErrorLogLevel( errorData );
			logErrorInternal(ctx, errorData, logLevel);
		} finally {
			logMetrics(System.nanoTime() - startTime);
		}
	}

	public void logError(MessageContext ctx, Throwable e) throws ServiceException {
		long startTime = System.nanoTime();
		try {
			Level logLevel = getErrorLogLevel(ctx, e);
			logErrorInternal(ctx, e, logLevel);
		} finally {
			logMetrics(System.nanoTime() - startTime);
		}
	}

	public void logWarning(MessageContext ctx, Throwable e) throws ServiceException {
		long startTime = System.nanoTime();
		try {
			Level logLevel = getWarningLogLevel(ctx, e);
			logWarningInternal(ctx, e, logLevel);
		} finally {
			logMetrics(System.nanoTime() - startTime);
		}
	}
	
	protected Level getErrorLogLevel( ErrorData errorData ) {
//		ErrorSeverity errorSeverity = errorData.getSeverity();
//		if ( errorSeverity == null ) throw new NullPointerException();
//		return errorSeverity.equals( ErrorSeverity.WARNING ) ? Level.WARNING : Level.SEVERE;
		// RRE logging should use at most WARNING level.
		if (m_appErrorLogLevel.equals(Level.SEVERE)) {
			return Level.WARNING;
		}
		return m_appErrorLogLevel;
	}

	protected Level getErrorLogLevel(MessageContext ctx, Throwable e)
		throws ServiceException
	{
		LoggingHandlerUtils handlerUtils = getHandlerUtils();

		ErrorSubcategory subcategory = handlerUtils.getErrorSubcategory(ctx, e);

		if (subcategory == ErrorSubcategory.INBOUND_DATA ||
			subcategory == ErrorSubcategory.INBOUND_META_DATA ||
			subcategory == ErrorSubcategory.MARKDOWN)
		{
			return m_inputErrorLogLevel;
		}

		ErrorCategory category = handlerUtils.getErrorCategory(ctx, e);

		if (category == ErrorCategory.APPLICATION) {
			return m_appErrorLogLevel;
		}

		return m_errorLogLevel;
	}

	protected Level getWarningLogLevel(MessageContext ctx, Throwable e)
		throws ServiceException
	{
		return m_warningLogLevel;
	}

	protected void logEventInternal(MessageContext ctx, LoggingHandlerStage stage, String text)
		throws ServiceException
	{
		m_logger.log(m_callEventLogLevel, text);
	}

	protected void logErrorInternal(MessageContext ctx, ErrorData errorData, Level logLevel)
	{
		logErrorInternal( ctx, errorData, logLevel, getErrorPrefixText(ctx) );
	}
		
	protected void logErrorInternal(MessageContext ctx, ErrorData errorData, Level logLevel, String prefix)
	{
		if ( errorData instanceof CommonErrorData ) {
			logCommonErrorInternal( ctx, (CommonErrorData) errorData, logLevel, prefix );
			return;
		}

		assert ctx != null;
		assert errorData != null;
		assert logLevel != null;
		assert prefix != null;
		
		StringBuffer buf = new StringBuffer();
		// Don't add {} to log message, the logger treats {n} as arguments
		buf.append( "ErrorData=(" );
		buf.append( "id=" ).append( errorData.getErrorId() ).append( "," );
		buf.append( "domain=" ).append( errorData.getDomain() ).append( "," );
		buf.append( "subdomain=" ).append( errorData.getSubdomain() ).append( "," );
		buf.append( "severity=" ).append( errorData.getSeverity() ).append( "," );
		buf.append( "category=" ).append( errorData.getCategory() ).append( "," );

		String message = getMessageText( errorData );
		buf.append( "message=" ).append( message ).append( "," );
		List<ErrorParameter> errorParameterList = errorData.getParameter();
		for ( ErrorParameter errorParameter : errorParameterList ) {
			buf.append( "param=" ).append( errorParameter.getName() ).append( "," );
			buf.append( "value=" ).append( errorParameter.getValue() ).append( "," );
		}
		// Don't add {} to log message, the logger treats {n} as arguments
		buf.append( ")" );
		
		m_logger.log(logLevel, prefix + buf );
	}
	
	protected void logCommonErrorInternal(MessageContext ctx, CommonErrorData errorData, Level logLevel, String prefix)
	{
		assert ctx != null;
		assert errorData != null;
		assert logLevel != null;
		assert prefix != null;
		
		StringBuffer buf = new StringBuffer();
		// Don't add {} to log message, the logger treats {n} as arguments
		buf.append( "ErrorData=(" );
		buf.append( "id=" ).append( errorData.getErrorId() ).append( "," );
		buf.append( "name=" ).append( errorData.getErrorName() ).append( "," );
		buf.append( "organization=" ).append( errorData.getOrganization() ).append( "," );
		buf.append( "domain=" ).append( errorData.getDomain() ).append( "," );
		buf.append( "subdomain=" ).append( errorData.getSubdomain() ).append( "," );
		buf.append( "severity=" ).append( errorData.getSeverity() ).append( "," );
		buf.append( "category=" ).append( errorData.getCategory() ).append( "," );

		String message = getMessageText( errorData );
		buf.append( "message=" ).append( message ).append( "," );
		buf.append( "cause=" ).append( errorData.getCause() ).append( "," );

		String resolution = getResolutionText( errorData );
		buf.append( "resolution=" ).append( resolution ).append( "," );
		List<ErrorParameter> errorParameterList = errorData.getParameter();
		for ( ErrorParameter errorParameter : errorParameterList ) {
			buf.append( "param=" ).append( errorParameter.getName() ).append( "," );
			buf.append( "value=" ).append( errorParameter.getValue() ).append( "," );
		}
		// Don't add {} to log message, the logger treats {n} as arguments
		buf.append( ")" );
		
		m_logger.log(logLevel, prefix + buf );
	}

	private String getMessageText( ErrorData errorData ) {
		/**
		 * Assuming that it exists, log the English-version of the message, not the localized one
		 */
		String message = ErrorDataLoggingRegistry.getInstance().getEnglishMessage( errorData );
		return message == null ? errorData.getMessage() : message;
	}

	private String getResolutionText( CommonErrorData errorData ) {
		/**
		 * Assuming that it exists, log the English-version of the message, not the localized one
		 */
		String resolution = ErrorDataLoggingRegistry.getInstance().getEnglishResolution( errorData );
		return resolution == null ? errorData.getResolution() : resolution;
	}
	
	protected void logErrorInternal(MessageContext ctx, Throwable e, Level logLevel)
		throws ServiceException
	{
		e = unwrapException(e);
		String prefix = getErrorPrefixText(ctx);
		m_logger.log(logLevel, prefix + e.toString(), e);
	}

	protected void logWarningInternal(MessageContext ctx, Throwable e, Level logLevel)
		throws ServiceException
	{
		e = unwrapException(e);
		String prefix = getErrorPrefixText(ctx);
		m_logger.log(logLevel, prefix + e.toString(), e);
	}

	protected Throwable unwrapException(Throwable e) {
		return e;
	}

	protected String getErrorPrefixText(MessageContext ctx) {
		StringBuilder sb = new StringBuilder();

		String guid = ctx.getRequestGuid();
		if (guid != null) {
			sb.append('{');
			sb.append(guid);
			sb.append("} ");
		}

		return sb.toString();
	}
	

	protected final void logMetrics(MessageContext ctx, LoggingHandlerStage stage,
			long duration) {
		logMetrics(duration); 
		if (stage == LoggingHandlerStage.RESPONSE_COMPLETE && isMetricEnabled(ctx)) {
			((BaseMessageContextImpl)ctx).updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_LOGGING, s_totalTime.get());
			// reset the accumulated total time
			s_totalTime.remove();
		}
	}

	private boolean isMetricEnabled(MessageContext ctx) {
		String enableStr = null;
		try {
			enableStr = ctx.getRequestMessage().getTransportHeader(ENABLE_LOGGING_METRICS_HEADER);
		} catch (ServiceException e) {
			// FIXIT: for now ignoring the exception and returning immediately		
		}
		return enableStr != null;
	}
	

	protected final void logMetrics(long duration) {
		Long curTotal = s_totalTime.get();
		curTotal += duration;
		s_totalTime.set(curTotal);
	}


	protected abstract LoggingHandlerUtils getHandlerUtils();
	
	protected abstract String getRequestPayloadLog(MessageContext ctx);
	
	protected abstract String getResponsePayloadLog(MessageContext ctx);
	
}
