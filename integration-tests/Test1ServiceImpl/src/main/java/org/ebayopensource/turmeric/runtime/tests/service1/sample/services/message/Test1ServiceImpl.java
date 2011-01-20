/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.TestServerErrorTypes;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Exception;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Service;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1ServiceException;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


/**
 * @author ichernyshev
 */
public class Test1ServiceImpl implements Test1Service {

	private boolean clientRegistered;

	static final String METRIC_NAME = "NumberOfRecipients";

	static final String CLIENT_METRIC_NAME = "NumberOfClientCalls";

	// Register serverside metric
	public Test1ServiceImpl() {
		MetricDef messageCountDef = new MetricDef(
				METRIC_NAME,
				MetricDef.SVC_APPLY_TO_ALL,
				"myTestOperation",
				MonitoringLevel.NORMAL,
				MetricCategory.Timing,
				org.ebayopensource.turmeric.runtime.common.monitoring.value.LongSumMetricValue.class);

		try {
			MetricsRegistry.getServerInstance().registerMetric(messageCountDef);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public MyMessage myTestOperation(MyMessage param1) throws Test1Exception,
			Test1ServiceException {
		boolean needTest1Exception = false;
		boolean needTest1ServiceException = false;
		MessageContext messageContext = MessageContextAccessor.getContext();

		updateMessageCount(param1);

		if (param1 != null && param1.getBinaryData() != null) {
			echoAttachment(param1);
		}

		StringBuilder chainedData = new StringBuilder();
		Message reqMsg = messageContext.getRequestMessage();
		Message respMsg = messageContext.getResponseMessage();
		try {
			needTest1Exception = reqMsg
					.hasTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION);
			needTest1ServiceException = reqMsg
					.hasTransportHeader(Test1Constants.TR_HDR_TEST1_SERVICE_EXCEPTION);
			String chainedHeader = reqMsg.getTransportHeader(Test1Constants.TR_CHAIN_HEADER);
			if (chainedHeader != null) {
				chainedData.append(chainedHeader);
			}
		} catch (ServiceException e) {
			throw new IllegalStateException("Enexpected exception: "
					+ e.toString(), e);
		}

		if (needTest1Exception) {
			throw new Test1Exception("Our test1 exception");
		}
		if (needTest1ServiceException) {
			throw new Test1ServiceException(
					TestServerErrorTypes.TEST1_SERVICE_EXCEPTION,
					new Object[] { "my_program_data" });
		}

		try {
			String cookie = reqMsg
					.getTransportHeader(Test1Constants.TR_HDR_TEST1_COOKIE);
			if (cookie != null) {
				respMsg.setCookie(HTTPCommonUtils.parseSetCookieValue(cookie));
			}
			Cookie chainedCookie = reqMsg.getCookie(Test1Constants.TR_CHAIN_COOKIE);
			if (chainedCookie != null) {
				chainedData.append(chainedCookie.getValue());
			}
		} catch (ServiceException e) {
			throw new Test1ServiceException(
					TestServerErrorTypes.TEST1_SERVICE_EXCEPTION,
					new Object[] { e.getMessage() }, e);
		}

		Cookie[] reqCookies;
		try {
			reqCookies = reqMsg.getCookies();
			for (int i = 0; i < reqCookies.length; i++) {
				Cookie cookie = reqCookies[i];
				Cookie respCookie = new Cookie("response-" + cookie.getName(),
						"response-" + cookie.getValue());
				/*if (cookie.getPath() != null) {
					respCookie.setPath(cookie.getPath());
				}
				if (cookie.getDomain() != null) {
					respCookie.setDomain(cookie.getDomain());
				}*/
				respMsg.setCookie(respCookie);
			}
		} catch (ServiceException e) {
			throw new Test1ServiceException(
					TestServerErrorTypes.TEST1_SERVICE_EXCEPTION,
					new Object[] { e.getMessage() }, e);
		}

		if (chainedData.length() > 0) {
			param1.setBody(chainedData.toString());
		}

		return param1;
	}

	private void echoAttachment(MyMessage msg) {
		DataHandler inDataHandler = msg.getBinaryData();
		ByteArrayOutputStream inData = new ByteArrayOutputStream();
		try {
			inDataHandler.writeTo(inData);
		} catch (IOException e) {}
//		System.out.println("Incoming attachment data: " + inData.toString());
//TODO: uncomment me when mail.jar issue resolved.
		DataSource outDataSource = new ByteArrayDataSource(inData.toByteArray(), inDataHandler.getContentType());
		DataHandler outDataHandler = new DataHandler(outDataSource);
		msg.setBinaryData(outDataHandler);

//		msg.setBinaryData(null);
	}

	/**
	 * Returning null.
	 */
	public MyMessage myNonArgOperation() throws Test1Exception,
			Test1ServiceException {
		return null;
	}

	public void myVoidReturnOperation(MyMessage param1) throws Test1Exception,
			Test1ServiceException {
		// Does nothing.
	}

	public MyMessage serviceChainingOperation(MyMessage param1)
			throws Test1Exception, Test1ServiceException {

		// TODO: collect data
		MyMessage result = callTest1Service(param1);

		updateClientMessageCount();
		// TODO: verify data collected above

		return result;
	}

	public String echoString(String msg) {
		return msg;
	}

	public MyMessage monitoringLevelOperation(MyMessage param1) {
		// TODO: should be done via config bean?
		return param1;
	}

	private MyMessage callTest1Service(MyMessage param1) {
		try {
			Service svc = ServiceFactory
					.create(
							org.ebayopensource.turmeric.runtime.tests.service1.sample.util.TestUtils.TEST1_SERVICE_NAME,
							"chaining");

			registerClientMetrics();

			ServiceInvokerOptions options = svc.getInvokerOptions();
			options.setTransportName(SOAConstants.TRANSPORT_LOCAL);

			Test1Service proxy = svc.getProxy();
			return proxy.myTestOperation(param1);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void registerClientMetrics() {
		if (!clientRegistered) {
			MetricDef messageCountDef = new MetricDef(
					CLIENT_METRIC_NAME,
					org.ebayopensource.turmeric.runtime.tests.service1.sample.util.TestUtils.TEST1_SERVICE_QNAME,
					"myTestOperation",
					MonitoringLevel.NORMAL,
					MetricCategory.Timing,
					org.ebayopensource.turmeric.runtime.common.monitoring.value.LongSumMetricValue.class);
			try {
				MetricsRegistry.getClientInstance().registerMetric(
						messageCountDef);
				clientRegistered = true;
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateMessageCount(MyMessage param1) {
		MetricValueAggregator mvAggregator = MetricsCollector
				.getServerInstance().getMetricValue(METRIC_NAME);

		// MetricValue mvBefore = mvAggregator.deepCopy(true);

		mvAggregator.update(MessageContextAccessor.getContext(),
				(param1 == null ? 0 : param1.getRecipients().size()));

		// MetricValue mvAfter = mvAggregator.deepCopy(true);
		//
		// MetricValue diff = mvAfter.diff(mvBefore, true);
	}

	private void updateClientMessageCount() {
		MetricValueAggregator mvAggregator = MetricsCollector
				.getClientInstance()
				.getMetricValue(
						CLIENT_METRIC_NAME,
						org.ebayopensource.turmeric.runtime.tests.service1.sample.util.TestUtils.TEST1_SERVICE_NAME,
						null, "myTestOperation");

		mvAggregator.update(MessageContextAccessor.getContext(), 1);
	}
	public void customError1() throws Test1Exception, Test1ServiceException {
		testThrowException();
	}
	public MyMessage customError2(MyMessage param1) throws Test1Exception, Test1ServiceException {
		testThrowException();
		return param1;
	}
	private void testThrowException() throws Test1Exception, Test1ServiceException {
		MessageContext messageContext = MessageContextAccessor.getContext();
		Message reqMsg = messageContext.getRequestMessage();
		boolean needTest1Exception = false;
		boolean needTest1ServiceException = false;
		try {
			needTest1Exception = reqMsg
					.hasTransportHeader(Test1Constants.TR_HDR_TEST1_EXCEPTION);
			needTest1ServiceException = reqMsg
					.hasTransportHeader(Test1Constants.TR_HDR_TEST1_SERVICE_EXCEPTION);
		} catch (ServiceException e) {
			throw new IllegalStateException("Enexpected exception: "
					+ e.toString(), e);
		}

		if (needTest1Exception) {
			throw new Test1Exception("Our test1 exception");
		}
		if (needTest1ServiceException) {
			throw new Test1ServiceException(
					TestServerErrorTypes.TEST1_SERVICE_EXCEPTION,
					new Object[] { "my_program_data" });
		}
	}
}
