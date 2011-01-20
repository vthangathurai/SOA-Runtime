/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author rmurphy
 * @author ichernyshev
 */
public final class PayloadLoggingHelper {

	private final PayloadAccessHelper m_accessHelper;
	private final String m_separator;
	private final boolean m_failOnFileError;
	private final File m_requestDumpFile;
	private final File m_responseDumpFile;
	private final Writer m_requestDumpWriter;
	private final Writer m_responseDumpWriter;
	private final boolean m_isAppend;
	private final Logger m_logger;
	private final Level m_logLevel;

	public PayloadLoggingHelper(ServiceId svcId, Map<String, String> options,
			Logger logger) throws ServiceException {

		String value = options.get("payload-fail-on-file-error");
		if (value != null) {
			m_failOnFileError = Boolean.parseBoolean(value);
		} else {
			m_failOnFileError = false;
		}

		value = options.get("payload-separator");
		m_separator = value;

		value = options.get("payload-file-append");
		if (value != null) {
			m_isAppend = Boolean.parseBoolean(value);
		} else {
			m_isAppend = true;
		}

		File dumpDir = new File(System.getProperty(
				"org.ebayopensource.payload.dump.dir", "."));

		String requestDumpFileNameStr = options
				.get("payload-request-dump-file");
		if (requestDumpFileNameStr != null) {
			if (requestDumpFileNameStr.startsWith("$dumpdir")) {
				requestDumpFileNameStr = requestDumpFileNameStr.replace(
						"$dumpdir", dumpDir.getAbsolutePath());
			}
			m_requestDumpFile = new File(requestDumpFileNameStr)
					.getAbsoluteFile();
			requestDumpFileNameStr = m_requestDumpFile.getPath();

			m_requestDumpWriter = openFile(m_requestDumpFile, m_isAppend,
					m_failOnFileError);
		} else {
			m_requestDumpFile = null;
			m_requestDumpWriter = null;
		}

		String responseDumpFileNameStr = options
				.get("payload-response-dump-file");
		if (responseDumpFileNameStr != null) {
			if (responseDumpFileNameStr.startsWith("$dumpdir")) {
				responseDumpFileNameStr = responseDumpFileNameStr.replace(
						"$dumpdir", dumpDir.getAbsolutePath());
			}
			m_responseDumpFile = new File(responseDumpFileNameStr)
					.getAbsoluteFile();
			responseDumpFileNameStr = m_responseDumpFile.getPath();

			if (responseDumpFileNameStr.equals(requestDumpFileNameStr)) {
				m_responseDumpWriter = m_requestDumpWriter;
			} else {
				m_responseDumpWriter = openFile(m_responseDumpFile, m_isAppend,
						m_failOnFileError);
			}
		} else {
			m_responseDumpFile = null;
			m_responseDumpWriter = null;
		}

		value = options.get("payload-log-level");
		if (value != null && logger != null) {
			try {
				Level logLevel = Level.parse(value);
				// Make sure Level.WARNING is the highest level we can use for
				// payload logging to log file.
				if (logLevel.equals(Level.SEVERE)) {
					m_logLevel = Level.WARNING;
				} else {
					m_logLevel = logLevel;
				}
			} catch (Exception e) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(
								ErrorConstants.CFG_GENERIC_ERROR,
								ErrorConstants.ERRORDOMAIN,
								new Object[] { "Invalid payload-log-level: "
										+ value }), e);
			}

			if (m_logLevel != Level.OFF) {
				m_logger = logger;
			} else {
				m_logger = null;
			}
		} else {
			m_logger = null;
			m_logLevel = Level.OFF;
		}

		boolean shouldLogRequests = (m_logger != null || m_requestDumpWriter != null);
		boolean shouldLogResponses = (m_logger != null || m_responseDumpWriter != null);

		m_accessHelper = new PayloadAccessHelper(svcId, options,
				shouldLogRequests, shouldLogResponses);
	}

	private static BufferedWriter openFile(File dumpFile, boolean append,
			boolean failOnError) throws ServiceException {
		BufferedWriter result;
		try {
			result = new BufferedWriter(new FileWriter(dumpFile, append));
		} catch (IOException e) {
			if (failOnError) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_RT_CANNOT_OPEN_FOR_WRITING,
						ErrorConstants.ERRORDOMAIN,
						new Object[] { dumpFile.getPath(), e.toString() }), e);
			}

			LogManager.getInstance(PayloadLoggingHelper.class).log(
					Level.SEVERE,
					"Unable to open payload log file for writing - "
							+ dumpFile.getPath() + ": " + e.toString(), e);
			result = null;
		}

		return result;
	}

	private void writeFile(Writer bw, String payloadData, File dumpFile)
			throws ServiceException {
		if (bw == null) { // logging not enabled
			return;
		}

		try {
			synchronized (this) {
				bw.write(payloadData);
				if (m_separator != null) {
					bw.write(m_separator);
				}
				bw.flush();
			}
		} catch (IOException e) {
			if (m_failOnFileError) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_RT_CANNOT_OPEN_FOR_WRITING,
						ErrorConstants.ERRORDOMAIN,
						new Object[] { dumpFile.getPath(), e.toString() }), e);
			}
		}
	}

	public void startRequestRecording(MessageContext ctx) {
		m_accessHelper.startRequestRecording(ctx);
	}

	public void startResponseRecording(MessageContext ctx) {
		m_accessHelper.startResponseRecording(ctx);
	}

	public void logRequestPayload(MessageContext ctx) {

		String payloadData = m_accessHelper.getRequestPayload(ctx);
		if (payloadData == null) {
			return;
		}

		try {
			if (m_requestDumpWriter != null) {
				writeFile(m_requestDumpWriter, payloadData, m_requestDumpFile);
			}

			if (m_logger != null) {
				m_logger.log(m_logLevel, "Request payload: " + payloadData);
			}
		} catch (Throwable e) {
			LogManager.getInstance(PayloadLoggingHelper.class).log(
					Level.WARNING,
					"Unable to log Request Payload data due to: "
							+ e.toString(), e);
		}
	}

	public void logResponsePayload(MessageContext ctx) {
		String payloadData = m_accessHelper.getResponsePayload(ctx);
		if (payloadData == null) {
			return;
		}

		try {
			if (m_responseDumpWriter != null) {
				writeFile(m_responseDumpWriter, payloadData, m_responseDumpFile);
			}

			if (m_logger != null) {
				m_logger.log(m_logLevel, "Response payload: " + payloadData);
			}
		} catch (Throwable e) {
			LogManager.getInstance(PayloadLoggingHelper.class).log(
					Level.WARNING,
					"Unable to log Response Payload data due to: "
							+ e.toString(), e);
		}
	}
}
