/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.HTTPTransportException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.client.exception.BaseClientSideException;
import com.ebay.kernel.service.invocation.client.exception.ConnectionException;
import com.ebay.kernel.service.invocation.client.exception.ConnectionTimeoutException;
import com.ebay.kernel.service.invocation.client.exception.ReceivingException;
import com.ebay.kernel.service.invocation.client.exception.ReceivingTimeoutException;
import com.ebay.kernel.service.invocation.client.exception.SendingException;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.HttpStatusEnum;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.RequestBodyWriter;
import com.ebay.kernel.service.invocation.client.http.Response;

/**
 * Transport making use of HTTPClient.
 *
 * @author rmurphy, wdeng
 */

// TODO - for binary, may have to do transfer encoding and set
// Content-Transfer-Encoding header. Handler
// will do it?
public class HTTPClientTransport implements Transport {
	static public final String QUERY_STR = "?";

	static public final char AMP_STR = '&';

	static public final char EQUAL_STR = '=';

	private ClientServiceId m_svcId;

	private HttpClient m_client;

	private String m_httpVersion;

	private boolean m_chunked = false;

	private boolean m_zipped = false;

	private static Map<String, HTTPClientTransportConfig> s_configs = new HashMap<String, HTTPClientTransportConfig>();

	private HTTPClientTransportConfig m_config;

	public HTTPClientTransport() {
		// empty
	}

	public void init(InitContext ctx) throws ServiceException {
		m_svcId = (ClientServiceId) ctx.getServiceId();

		m_config = createSvcConfig(ctx.getServiceId(), ctx.getName(), ctx
				.getOptions());
		SvcInvocationConfig svcInvConfig = m_config.getSvcInvocationConfig();
		m_client = new HttpClient(svcInvConfig, null);
		Map<String, String> transportProperties = ctx.getOptions()
				.getProperties();
		if (null == transportProperties) {
			return;
		}
		String httpVersion = transportProperties.get(SOAConstants.HTTP_VERSION);
		if (httpVersion != null
				&& httpVersion.equals(SOAConstants.TRANSPORT_HTTP_10)) {
			m_httpVersion = Request.HTTP_10;
		} else {
			m_httpVersion = Request.HTTP_11;
			String useChunkedEncoding = transportProperties
					.get(SOAConstants.CHUNKED_ENCODING);
			m_chunked = useChunkedEncoding != null
					&& Boolean.parseBoolean(useChunkedEncoding);
		}
		String useZipping = transportProperties.get(SOAConstants.GZIP_ENCODING);
		m_zipped = useZipping != null && Boolean.parseBoolean(useZipping);
	}

	private HTTPClientTransportConfig createSvcConfig(ServiceId svcId,
			String name, TransportOptions options) {
		ClientServiceId clientId = (ClientServiceId) svcId;
		StringBuilder sb = new StringBuilder();
		sb.append(clientId.getClientName()).append('.').append(
				clientId.getAdminName()).append('.').append(name);
		String configName = sb.toString();

		HTTPClientTransportConfig config;
		// ClientServiceDescFactory creates one instance of HTTPClientTransport
		// per service. We keep a map of all previously
		// initialized config beans by service name and transport name. This
		// avoids creation of multiple beans across uses,
		// for the same service name and transport name (currently equal to the
		// transport name, HTTP10 or HTTP11).
		synchronized (s_configs) {
			config = s_configs.get(configName);
		}
		if (config == null) {
			config = new HTTPClientTransportConfig(configName, options);
			synchronized (s_configs) {
				HTTPClientTransportConfig regetconfig = s_configs
						.get(configName);
				if (regetconfig == null) {
					s_configs.put(configName, config);
				} else {
					config = regetconfig;
				}
			}
		}

		return config;
	}

	// Preinvoke is used only in cases of deferred invoke (e.g. async).
	// Other cases do nothing.
	public Object preInvoke(MessageContext ctx) throws ServiceException {
		OutboundMessage clientRequestMsg = (OutboundMessage) ctx
				.getRequestMessage();
		// Set the content-type of request only if it has not been set (in
		// soap1.2 case,
		// this gets over-written at the Client Protocol processor.
		// So make sure we are only setting here if it has not already been set)
		if (clientRequestMsg
				.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE) == null) {
			DataBindingDesc binding = clientRequestMsg.getDataBindingDesc();
			String mimeType = binding.getMimeType();
			Charset charset = clientRequestMsg.getG11nOptions().getCharset();
			String contentType = HTTPCommonUtils.formatContentType(mimeType,
					charset);
			clientRequestMsg.setTransportHeader(
					SOAConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
		}
		return null;
	}

	public void invoke(Message msg, TransportOptions transportOptions)
			throws ServiceException {
		ClientMessageContext clientCtx = (ClientMessageContext) msg
				.getContext();

		ServiceAddress serviceAddress = clientCtx.getServiceAddress();
		if (serviceAddress == null || serviceAddress.getServiceUrl() == null) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_NO_SERVICE_ADDRESS,
							ErrorConstants.ERRORDOMAIN, new Object[] { clientCtx.getAdminName(), clientCtx.getOperationName() }));
		}
		URL serviceLocation = serviceAddress.getServiceUrl();
		String serviceLocationString = serviceLocation.toString();

		OutboundMessage clientRequestMsg = (OutboundMessage) msg;
		if (clientRequestMsg.isUnserializable()) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_UNSERIALIZABLE_MESSAGE,
					ErrorConstants.ERRORDOMAIN, new Object[] { clientCtx.getAdminName(),
							clientRequestMsg.getUnserializableReason() }));
		}

		boolean httpGet = clientRequestMsg.isREST();
		int httpGetBufferSize = clientRequestMsg.getMaxURLLengthForREST();

		String adminName = clientCtx.getAdminName();

		if (httpGet) {
			if (clientRequestMsg.hasAttachment()) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_NO_GET_WITH_ATTACHMENTS,
								ErrorConstants.ERRORDOMAIN, new Object[] { adminName, serviceLocationString }));
			}
			String payloadType = clientRequestMsg.getPayloadType();
			// Should ideally allow a data binding to register whether it
			// supports REST. However, only
			// NV is anticipated in the forseeable future.
			if (!payloadType.equals(BindingConstants.PAYLOAD_NV)) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_GET_REQUIRES_NV,
						ErrorConstants.ERRORDOMAIN, new Object[] { adminName, serviceLocationString }));
			}
			// Should ideally allow message protocols to register whether they
			// suport REST; but we have
			// only SOAP, and it does not support REST.
			String messageProtocol = clientCtx.getMessageProtocol();
			if (!messageProtocol.equals(SOAConstants.MSG_PROTOCOL_NONE)) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_NO_GET_WITH_SOAP,
								ErrorConstants.ERRORDOMAIN, new Object[] { adminName, serviceLocationString }));
			}
		}

		Map<String, String> transportHeaders = clientRequestMsg
				.buildOutputHeaders();

		Request request;
		if (httpGet) {
			request = createHTTPGetRequest(adminName, serviceLocationString,
					clientRequestMsg, transportHeaders, httpGetBufferSize);
		} else {
			request = createHTTPPostRequest(adminName, serviceLocation,
					serviceLocationString, clientRequestMsg, transportHeaders);
		}

		Cookie[] cookies = clientRequestMsg.getCookies();
		if (cookies != null && cookies.length != 0) {
			StringBuffer buf = new StringBuffer();
			HTTPCommonUtils.encodeCookieValue(buf, cookies);
			String cookieString = buf.toString();
			request.addHeader("Cookie", cookieString);
		}

		DeferredBodyReaderSelector selector = new DeferredBodyReaderSelector();
		DeferredBodyReader bodyReader = selector.getReader();
		Response response = sendMessage(serviceLocationString, request,
				selector, clientCtx);
		setResponseToContextInputStream(response, clientCtx, bodyReader);
	}

	private Request createHTTPGetRequest(String adminName,
			String serviceLocationString, OutboundMessage clientRequestMsg,
			Map<String, String> transportHeaders, int httpGetBufferSize)
			throws ServiceException {

		StringBuilder urlBuffer = new StringBuilder(httpGetBufferSize);
		urlBuffer.append(serviceLocationString);
		if (urlBuffer.indexOf(QUERY_STR) != -1) {
			// already have the query start
			urlBuffer.append(AMP_STR);
		} else {
			urlBuffer.append(QUERY_STR);
		}

		// The bytes here will be in UTF-8 or whatever encoding is passed via
		// the g11n options in the
		// clientRequestMsg - indicated by "charset" below.
		//
		// TODO do we want to do a more efficient serialize process in which we
		// go directly to an OutputStream with
		// an underlying StringBuffer? XMLStreamWriters deal in character data.
		// However, all SOA output is normally based
		// on output streams. Since Get encodings are normally small, we can
		// probably leave it the way it is.
		byte[] httpPayloadData = serializeRequest(clientRequestMsg);
		String httpPayloadString;
		try {
			String charset = clientRequestMsg.getG11nOptions().getCharset()
					.name();
			httpPayloadString = new String(httpPayloadData, charset);
		} catch (Exception e) { // UnsupportedEncodingException, etc.
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_UNSERIALIZABLE_MESSAGE,
					ErrorConstants.ERRORDOMAIN, new Object[] { m_svcId.getAdminName(), e.toString() }), e);
		}

		// this is a SOA-specific parameter, that is only analyzed on the server
		// end
		// until we have free-form HTTP paramters, we should not need it on the
		// client end
		// urlBuffer.append(SOAHeaders.REST_PAYLOAD);
		// urlBuffer.append("=true");
		// urlBuffer.append(AMP_STR);

		urlBuffer.append(httpPayloadString);

		int urlLength = urlBuffer.length();
		String urlString = urlBuffer.toString();
		if (urlLength > httpGetBufferSize) {
			String urlStringToLog = getUrlLogString(urlString, urlLength);
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_URL_TOO_LONG,
					ErrorConstants.ERRORDOMAIN, new Object[] {adminName, urlStringToLog,Integer.valueOf(urlLength) }));
		}

		Request request;
		try {
			request = new Request(urlString);
		} catch (MalformedURLException e) {
			String urlStringToLog = getUrlLogString(urlString, urlLength);
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_INVALID_SERVICE_ADDRESS,
					ErrorConstants.ERRORDOMAIN, new Object[] { adminName, urlStringToLog }), e);
		}
		request.setHttpVersion(m_httpVersion);

		addTransportHeaders(transportHeaders, request);

		request.setMethod(Request.GET);
		return request;
	}

	private String getUrlLogString(String urlString, int urlLength) {
		if (urlLength > 80) {
			return urlString.substring(0, 80);
		}

		return urlString;
	}

	private Request createHTTPPostRequest(String adminName,
			URL serviceLocation, String serviceLocationString,
			OutboundMessage clientRequestMsg,
			Map<String, String> transportHeaders) throws ServiceException {
		Request request = new Request(serviceLocation);
		request.setMethod(Request.POST);
		request.setHttpVersion(m_httpVersion);

		addTransportHeaders(transportHeaders, request);

		if (m_chunked) {
			request.setChunkedEncoding();
			request.addHeader("Transfer-Encoding", "chunked");
			RequestBodyWriter outWriter = new StreamingMessageBodyWriter(
					clientRequestMsg);
			request.setBodyWriter(outWriter);
		} else {
			byte[] httpPayloadData = serializeRequest(clientRequestMsg);
			request.setRawData(httpPayloadData);
		}
		if (m_zipped) {
			request.addHeader(SOAConstants.HTTP_HEADER_ACCEPT_ENCODING, "gzip");
		}
		return request;
	}

	private void addTransportHeaders(Map<String, String> transportHeaders,
			Request request) {
		if (transportHeaders == null || transportHeaders.isEmpty()) {
			return;
		}

		for (Map.Entry<String, String> entry : transportHeaders.entrySet()) {
			String header = entry.getKey();
			String value = entry.getValue();
			request.addHeader(header, value);
		}
	}

	private byte[] serializeRequest(OutboundMessage clientRequestMsg)
			throws ServiceException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		clientRequestMsg.serialize(bos);
		return bos.toByteArray();
	}

	private Response sendMessage(String serviceLocationString, Request request,
			DeferredBodyReaderSelector selector, ClientMessageContext clientCtx)
			throws ServiceException {
		Response response = null;
		try {
			// response = m_client.invokeWithSelector(request, selector);
			response = m_client.invoke(request);
		} catch (ConnectionTimeoutException e) {
			throw new HTTPTransportException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_CONNECT_TIMEOUT_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] { serviceLocationString,
							m_svcId.getAdminName(), e.toString() }), -1, e);
		} catch (ConnectionException e) {
			throw new HTTPTransportException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_CONNECT_EXCEPTION,
							ErrorConstants.ERRORDOMAIN, new Object[] { serviceLocationString,
							m_svcId.getAdminName(), e.toString() }), -1, e);
		} catch (ReceivingTimeoutException e) {
			throw new HTTPTransportException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_INBOUND_IO_EXCEPTION,
							ErrorConstants.ERRORDOMAIN, new Object[] { m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (ReceivingException e) {
			throw new HTTPTransportException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_INBOUND_IO_EXCEPTION,
							ErrorConstants.ERRORDOMAIN, new Object[] { m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (SendingException e) {
			throw new HTTPTransportException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] { m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (BaseClientSideException e) {
			throw new HTTPTransportException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_COMM_FAILURE,
					ErrorConstants.ERRORDOMAIN, new Object[] {serviceLocationString, e.toString() }), -1, e);
		}

		setResponseToContextHeaderMapping(response, clientCtx, selector
				.getReader());

		if (response.getRequestStatus() == HttpStatusEnum.SUCCESS) {
			return response;
		}

		boolean isSoap = false;
		if (clientCtx.getMessageProtocol().equals(
				SOAConstants.MSG_PROTOCOL_SOAP_11)
				|| clientCtx.getMessageProtocol().equals(
						SOAConstants.MSG_PROTOCOL_SOAP_12)) {
			isSoap = true;
		}

		int httpStatusCode = response.getStatusCode();
		if (httpStatusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
			if (isSoap) {
				// if HTTP 500 and SOAP, we will manually set the ERROR_RESPON
				// header, to handler the case when
				// we are talking to foreign SOAP web servers that doesn't set
				// the ERROR_RESPONSE header.
				InboundMessage responseMsg = (InboundMessage) clientCtx
						.getResponseMessage();
				responseMsg.setTransportHeader(SOAHeaders.ERROR_RESPONSE,
						"true");
			}
			// if (response.getHeader(SOAHeaders.ERROR_RESPONSE) != null ||
			// isSoap) {
			if (((InboundMessage) clientCtx.getResponseMessage())
					.getTransportHeader(SOAHeaders.ERROR_RESPONSE) != null
					|| isSoap) {
				return response;
			}
			generateExceptionMessage(serviceLocationString, response,
					httpStatusCode);
		}

		String responseBody = getAnyOtherErrorAsString(response);
		throw new HTTPTransportException(
				ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_HTTP_ERROR,
				ErrorConstants.ERRORDOMAIN, new Object[] {serviceLocationString,
					response.getRequestStatus().getName(),
					Integer.valueOf(httpStatusCode), responseBody }), httpStatusCode, null);

	}

	private String getAnyOtherErrorAsString(
			Response response) {
		String responseBody = " ";
		final String responseBodyStr = response.getBody() == null ? " "
				: response.getBody();
		responseBody = responseBodyStr
				.substring(0, responseBodyStr.length() > 256 ? 256
						: responseBodyStr.length());
		return responseBody;
	}

	private static final int RESPONSE_BODY_LIMIT = 256;

	private static final String LOOKUP_BODY_TEXT = "HTTP Status 500";

	private void generateExceptionMessage(String serviceLocationString,
			Response response, int httpStatusCode)
			throws HTTPTransportException {
		String responseBody = get500ErrorResponseAsString(response);
		throw new HTTPTransportException(
				ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_HTTP_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {
						serviceLocationString,
						response.getRequestStatus().getName(),
						Integer.valueOf(httpStatusCode), responseBody }),
				httpStatusCode, null);
	}

	private String get500ErrorResponseAsString(Response response) {
		String responseBody = " ";
		final String responseBodyStr = response.getBody() == null ? " "
				: response.getBody();
		int startIndex = responseBodyStr.indexOf(LOOKUP_BODY_TEXT);
		startIndex = startIndex > 0 ? startIndex + LOOKUP_BODY_TEXT.length()
				: 0;
		try {
			responseBody = responseBodyStr.substring(startIndex,
					(responseBodyStr.length() > startIndex
							+ RESPONSE_BODY_LIMIT ? startIndex
							+ RESPONSE_BODY_LIMIT : Math.min(startIndex
							+ RESPONSE_BODY_LIMIT, responseBodyStr.length())));
		} catch (StringIndexOutOfBoundsException e) {
			responseBody = responseBodyStr.substring(0, Math.min(
					RESPONSE_BODY_LIMIT, responseBodyStr.length()));
		}
		return responseBody;
	}

	private void setResponseToContextHeaderMapping(Response httpClientResponse,
			ClientMessageContext clientCtx, DeferredBodyReader bodyReader)
			throws ServiceException {
		InboundMessage clientResponse = (InboundMessage) clientCtx
				.getResponseMessage();

		@SuppressWarnings("deprecation")
		Enumeration headers = httpClientResponse.getHeaders();

		while (headers.hasMoreElements()) {
			String header = (String) headers.nextElement();
			String value = httpClientResponse.getHeader(header);
			// HOT FIX: For Porlet call, there's a compatibility issue regarding
			// Upper case vs lower case operation.
			// Here, we want to make the first letter of the operation name
			// lower case always,
			// to handle 589 client talking to 587 server scenario
			// CLIENT SIDE RECEIVING SIDE LOGIC
			if (header != null
					&& header
							.equalsIgnoreCase(SOAHeaders.SERVICE_OPERATION_NAME)
					&& value != null && value.equals("Portlet")) {
				StringBuffer sb = new StringBuffer();
				sb.append(Character.toLowerCase(value.charAt(0)));
				sb.append(value.substring(1));
				value = sb.toString();
			}
			clientResponse.setTransportHeader(header, value);
		}

		Iterator cookies = httpClientResponse.getCookies();
		while (cookies.hasNext()) {
			String cookieString = (String) cookies.next();
			clientResponse.setCookie(HTTPCommonUtils
					.parseSetCookieValue(cookieString));
		}
	}

	private void setResponseToContextInputStream(Response httpClientResponse,
			ClientMessageContext clientCtx, DeferredBodyReader bodyReader)
			throws ServiceException {

		InboundMessage clientResponse = (InboundMessage) clientCtx
			.getResponseMessage();

		byte[] rawData = httpClientResponse.getRawData();

		if (clientCtx.isOutboundRawMode()) {
			clientResponse.setByteBuffer(ByteBuffer.wrap(rawData));
		} else {
			ByteArrayInputStream bis = new ByteArrayInputStream(rawData);
			clientResponse.setInputStream(bis);
		}

		// httpClientResponse.getRawData();
		// InputStream responseInputStream = bodyReader.getInputStream();
		// clientResponse.setInputStream(responseInputStream);
	}

	public Future<?> invokeAsync(Message msg, TransportOptions transportOptions)
			throws ServiceException {
		throw new UnsupportedOperationException(
				"HTTPClientTransport transport doesn't support invokeAsync, use HTTPSyncAsyncClientTransport");
	}

	public void retrieve(MessageContext ctx, Future<?> futureResp)
			throws ServiceException {
		throw new UnsupportedOperationException(
				"HTTPClientTransport transport doesn't support retrieve");
	}

	public boolean supportsPoll() {
		return false;
	}

	public HTTPClientTransportConfig getConfig() {
		return m_config;
	}

}
