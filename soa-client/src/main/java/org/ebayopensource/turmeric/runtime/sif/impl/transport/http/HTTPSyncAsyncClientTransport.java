/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.HTTPTransportException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ITransportPoller;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
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
import org.ebayopensource.turmeric.runtime.common.utils.BufferUtil;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

import com.ebay.kernel.service.invocation.client.exception.BaseClientSideException;
import com.ebay.kernel.service.invocation.client.http.HttpStatusEnum;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.RequestBodyWriter;
import com.ebay.kernel.service.invocation.client.http.Response;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncCallback;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClient;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncHttpClientImpl;
import com.ebay.kernel.service.invocation.client.http.nio.NioAsyncResponseFuture;
import com.ebay.kernel.service.invocation.client.http.nio.NioSvcInvocationConfig;

public class HTTPSyncAsyncClientTransport implements Transport {

	static final Logger LOGGER = LogManager
			.getInstance(HTTPSyncAsyncClientTransport.class);

	static public final String QUERY_STR = "?";

	static public final char AMP_STR = '&';

	static public final char EQUAL_STR = '=';

	private ClientServiceId m_svcId;

	private NioAsyncHttpClient m_client;

	private static Map<String, NioAsyncHttpClient> m_asyncHttpClients = new ConcurrentHashMap<String, NioAsyncHttpClient>();

	private String m_httpVersion;
	private boolean m_accept_gzip = false;

	private HTTPClientTransportConfig m_config;

	private static Map<String, HTTPClientTransportConfig> s_configs = new HashMap<String, HTTPClientTransportConfig>();

	// private static Map<String, HttpTransportPollQueue> m_pollQueues = new
	// ConcurrentHashMap<String, HttpTransportPollQueue>();
	public HTTPSyncAsyncClientTransport() {
		// empty
	}

	public void checkTransportPoller(IAsyncResponsePoller holder) {
		if (holder.getTransportPoller() == null
				|| !(holder.getTransportPoller() instanceof HTTPSyncAsyncClientTransportPoller)) {
			holder.setTransportPoller(new HTTPSyncAsyncClientTransportPoller());
		}
	}

	public void init(InitContext ctx) throws ServiceException {
		m_svcId = (ClientServiceId) ctx.getServiceId();
		String configName = getConfigName(ctx.getServiceId(), ctx.getName());
		m_config = createSvcConfig(configName, ctx.getOptions());

		m_client = getAsyncHttpClient(configName, m_config
				.getNioSvcInvocationConfig());

		String httpVersion = ctx.getOptions().getProperties().get(
				SOAConstants.HTTP_VERSION);
		if (httpVersion != null
				&& httpVersion.equals(SOAConstants.TRANSPORT_HTTP_10)) {
			m_httpVersion = Request.HTTP_10;
		} else {
			m_httpVersion = Request.HTTP_11;
		}
		String useZipping = ctx.getOptions().getProperties().get(
				SOAConstants.GZIP_ENCODING);
		if (useZipping != null && Boolean.parseBoolean(useZipping))
			m_accept_gzip = true;
	}

	private String getConfigName(ServiceId svcId, String name) {
		ClientServiceId clientId = (ClientServiceId) svcId;
		StringBuilder sb = new StringBuilder();
		sb.append(clientId.getClientName()).append('.').append(
				clientId.getAdminName()).append('.').append(name);
		return sb.toString();
	}

	private NioAsyncHttpClient getAsyncHttpClient(String configName,
			NioSvcInvocationConfig config) {

		NioAsyncHttpClient asyncClient;
		synchronized (m_asyncHttpClients) {
			asyncClient = m_asyncHttpClients.get(configName);
			if (asyncClient == null) {
				asyncClient = new NioAsyncHttpClientImpl(config);
				m_asyncHttpClients.put(configName, asyncClient);
			}
		}

		return asyncClient;
	}

	private HTTPClientTransportConfig createSvcConfig(String configName,
			TransportOptions options) {

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

	public Future<?> invokeAsync(Message msg, TransportOptions transportOptions)
			throws ServiceException {

		// Client's Message Context
		BaseMessageContextImpl clientCtx = (BaseMessageContextImpl) msg
				.getContext();

		// URL for destination
		URL serviceLocation = getServiceLocation(clientCtx);
		String serviceLocationString = serviceLocation.toString();

		Request request = getRequest(msg, transportOptions, serviceLocation,
				serviceLocationString);

		AsyncCallBack callback = clientCtx.getServiceAsyncCallback();

		return callback == null ? sendMessageGetResponseFuture(
				serviceLocationString, request, clientCtx)
				: sendMessageGetResponseOnCallBack(serviceLocationString,
						request, clientCtx, new AsyncClientCallBack(clientCtx));
	}

	protected boolean isClientStreaming() {
		return m_client.getConfiguration().getUseResponseStreaming();
	}

	public void invoke(Message msg, TransportOptions transportOptions)
			throws ServiceException {

		// Client's Message Context
		BaseMessageContextImpl clientCtx = (BaseMessageContextImpl) msg
				.getContext();

		// URL for destination
		URL serviceLocation = getServiceLocation(clientCtx);
		String serviceLocationString = serviceLocation.toString();

		Request request = getRequest(msg, transportOptions, serviceLocation,
				serviceLocationString);

		ResponseAvatar response = sendMessageGetResponse(serviceLocationString,
				request, clientCtx);
		setResponseToContextInputStream(serviceLocationString, response,
				clientCtx);
	}

	public void retrieve(MessageContext ctx, Future<?> futureResp)
			throws ServiceException {

		if (((BaseMessageContextImpl) ctx).getServiceAsyncCallback() != null) {
			return;
		}

		// Client's Message Context
		BaseMessageContextImpl clientCtx = (BaseMessageContextImpl) ctx;

		// URL for destination
		URL serviceLocation = getServiceLocation(clientCtx);
		String serviceLocationString = serviceLocation.toString();

		ResponseAvatar response = getResponseFromFuture(serviceLocationString,
				futureResp, clientCtx);

		setResponseToContextInputStream(serviceLocationString, response,
				clientCtx);
	}

	URL getServiceLocation(BaseMessageContextImpl clientCtx)
			throws ServiceException {
		ServiceAddress serviceAddress = clientCtx.getServiceAddress();
		if (serviceAddress == null || serviceAddress.getServiceUrl() == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_NO_SERVICE_ADDRESS,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							clientCtx.getAdminName(),
							clientCtx.getOperationName() }));
		}
		return serviceAddress.getServiceUrl();
	}

	private Request getRequest(Message msg, TransportOptions transportOptions,
			URL serviceLocation, String serviceLocationString)
			throws ServiceException {
		ClientMessageContext clientCtx = (ClientMessageContext) msg
				.getContext();

		OutboundMessage clientRequestMsg = (OutboundMessage) msg;
		if (clientRequestMsg.isUnserializable()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_UNSERIALIZABLE_MESSAGE,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							clientCtx.getAdminName(),
							clientRequestMsg.getUnserializableReason() }));
		}

		boolean httpGet = clientRequestMsg.isREST();
		int httpGetBufferSize = clientRequestMsg.getMaxURLLengthForREST();

		String adminName = clientCtx.getAdminName();

		if (httpGet) {
			if (clientRequestMsg.hasAttachment()) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_TRANSPORT_NO_GET_WITH_ATTACHMENTS,
						ErrorConstants.ERRORDOMAIN, new Object[] {
								adminName, serviceLocationString }));
			}
			String payloadType = clientRequestMsg.getPayloadType();
			// Should ideally allow a data binding to register whether it
			// supports REST. However, only
			// NV is anticipated in the forseeable future.
			if (!payloadType.equals(BindingConstants.PAYLOAD_NV)) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_TRANSPORT_GET_REQUIRES_NV,
						ErrorConstants.ERRORDOMAIN, new Object[] {
								adminName, serviceLocationString }));
			}
			// Should ideally allow message protocols to register whether they
			// suport REST; but we have
			// only SOAP, and it does not support REST.
			String messageProtocol = clientCtx.getMessageProtocol();
			if (!messageProtocol.equals(SOAConstants.MSG_PROTOCOL_NONE)) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_TRANSPORT_NO_GET_WITH_SOAP,
						ErrorConstants.ERRORDOMAIN, new Object[] {
								adminName, serviceLocationString }));
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
		request.setHttpVersion(m_httpVersion);

		Cookie[] cookies = clientRequestMsg.getCookies();
		if (cookies != null && cookies.length != 0) {
			StringBuffer buf = new StringBuffer();
			HTTPCommonUtils.encodeCookieValue(buf, cookies);
			String cookieString = buf.toString();
			request.addHeader("Cookie", cookieString);
		}
		if (m_accept_gzip)
			request.addHeader(SOAConstants.HTTP_HEADER_ACCEPT_ENCODING, "gzip");

		return request;
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
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_UNSERIALIZABLE_MESSAGE,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString() }), e);
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
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_URL_TOO_LONG,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							adminName, urlStringToLog,
							Integer.valueOf(urlLength) }));
		}

		Request request;
		try {
			request = new Request(urlString);
		} catch (MalformedURLException e) {
			String urlStringToLog = getUrlLogString(urlString, urlLength);
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_INVALID_SERVICE_ADDRESS,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							adminName, urlStringToLog }), e);
		}

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

		addTransportHeaders(transportHeaders, request);

		// TODO - if 1.1, then set streaming, otherwise not
		boolean streaming = false;
		if (streaming) {
			RequestBodyWriter outWriter = new StreamingMessageBodyWriter(
					clientRequestMsg);
			request.setBodyWriter(outWriter);
		} else {
			byte[] httpPayloadData = serializeRequest(clientRequestMsg);
			request.setRawData(httpPayloadData);
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

	/**
	 * Sends message for the pull (future-based) asynchronous mode.
	 *
	 * @param serviceLocationString
	 *            URL to send the message to.
	 * @param request
	 *            the request to send.
	 * @param clientCtx
	 *            the client context.
	 * @return the future to poll for retrieving the response.
	 * @throws ServiceException
	 *             when it hits it.
	 */
	private Future<?> sendMessageGetResponseFuture(
			String serviceLocationString, Request request,
			BaseMessageContextImpl clientCtx) throws ServiceException {

		Future<Response> futureResponse = null;
		try {
			IAsyncResponsePoller poller = clientCtx.getServicePoller();
			ITransportPoller transpPoller = null;

			if (poller != null) {
				checkTransportPoller(poller);
				transpPoller = poller.getTransportPoller();
			}
			futureResponse = poller != null && transpPoller != null ? m_client
					.send(request,
							(HTTPSyncAsyncClientTransportPoller) transpPoller)
					: m_client.send(request);
		} catch (BaseClientSideException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_COMM_FAILURE,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							serviceLocationString, e.toString() }), -1, e);
		}

		return futureResponse;
	}

	private Future<?> sendMessageGetResponseOnCallBack(
			String serviceLocationString, Request request,
			MessageContext clientCtx, AsyncClientCallBack callback)
			throws ServiceException {

		try {
			m_client.send(request, callback);
		} catch (BaseClientSideException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_COMM_FAILURE,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							serviceLocationString, e.toString() }), -1, e);
		}
		return new CallBackRequestFuture(callback);
	}

	private ResponseAvatar sendMessageGetResponse(String serviceLocationString,
			Request request, BaseMessageContextImpl clientCtx)
			throws ServiceException {

		Future<Response> future = null;
		ResponseAvatar response = null;
		try {
			future = m_client.send(request);
			if (isClientStreaming()) {
				NioAsyncResponseFuture nioAsyncResponseFuture = (NioAsyncResponseFuture) future;
				response = new FutureResponseWrapper(nioAsyncResponseFuture);
			} else {
				response = new ResponseWrapper(future.get(
						getInvocationTimeout(), TimeUnit.MILLISECONDS));
			}
		} catch (BaseClientSideException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_COMM_FAILURE,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							serviceLocationString, e.toString() }), -1, e);
		} catch (InterruptedException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (ExecutionException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (TimeoutException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		}

		return doCommonResponseProcessing(serviceLocationString, clientCtx,
				response);

	}

	private ResponseAvatar getResponseFromFuture(String serviceLocationString,
			Future<?> futureResp, MessageContext clientCtx)
			throws ServiceException {

		ResponseAvatar response = null;
		try {
			response = isClientStreaming() ? new FutureResponseWrapper(
					(NioAsyncResponseFuture) futureResp) : new ResponseWrapper(
					(Response) futureResp.get(getInvocationTimeout(),
							TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (ExecutionException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		} catch (TimeoutException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							m_svcId.getAdminName(), e.toString(),
							serviceLocationString }), -1, e);
		}

		return doCommonResponseProcessing(serviceLocationString, clientCtx,
				response);
	}

	private long getInvocationTimeout() {
		if (m_config == null) {
			throw new NullPointerException("Invalid Trasport Intialization");
		}
		long timeout = m_config.getMaxInvocationDuration();

		timeout = timeout < 1 ? HTTPClientTransportConfig.DEFAULT_HTTP_CONNECTION_TIMEOUT
				+ HTTPClientTransportConfig.DEFAULT_SOCKET_RECV_TIMEOUT
				: timeout;

		return timeout;
	}

	ResponseAvatar doCommonResponseProcessing(String serviceLocationString,
			MessageContext clientCtx, ResponseAvatar response)
			throws ServiceException, HTTPTransportException {

		setResponseToContextHeaderMapping(response,
				(BaseMessageContextImpl) clientCtx);

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
		throw new HTTPTransportException(ErrorDataFactory.createErrorData(
				ErrorConstants.SVC_TRANSPORT_HTTP_ERROR,
				ErrorConstants.ERRORDOMAIN, new Object[] {
						serviceLocationString,
						response.getRequestStatus().getName(),
						Integer.valueOf(httpStatusCode), responseBody }),
				httpStatusCode, null);

	}

	private static final int RESPONSE_BODY_LIMIT = 256;

	private static final String LOOKUP_BODY_TEXT = "HTTP Status 500";

	private String getAnyOtherErrorAsString(
			ResponseAvatar response) {
		String responseBody = " ";
		final String responseBodyStr = response.getBody() == null ? " "
				: response.getBody();
		responseBody = responseBodyStr
				.substring(0, responseBodyStr.length() > 256 ? 256
						: responseBodyStr.length());
		return responseBody;
	}

	private void generateExceptionMessage(String serviceLocationString,
			ResponseAvatar response, int httpStatusCode)
			throws HTTPTransportException {
		String responseBody = get500ErrorResponseAsString(response);
		throw new HTTPTransportException(ErrorDataFactory.createErrorData(
				ErrorConstants.SVC_TRANSPORT_HTTP_ERROR,
				ErrorConstants.ERRORDOMAIN, new Object[] {
						serviceLocationString,
						response.getRequestStatus().getName(),
						Integer.valueOf(httpStatusCode), responseBody }),
				httpStatusCode, null);
	}

	private String get500ErrorResponseAsString(ResponseAvatar response) {
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

	void setResponseToContextHeaderMapping(ResponseAvatar httpClientResponse,
			BaseMessageContextImpl clientCtx) throws ServiceException {

		InboundMessage clientResponse = (InboundMessage) clientCtx
				.getResponseMessage();

		Iterator<String> headerNames = httpClientResponse.getHeaderNames();
		while (headerNames.hasNext()) {
			String header = headerNames.next();
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

		clientResponse.doHeaderMapping();

	}

	void setResponseToContextInputStream(String serviceLocationString,
			ResponseAvatar httpClientResponse, BaseMessageContextImpl clientCtx)
			throws ServiceException {

		InboundMessage clientResponse = (InboundMessage) clientCtx
				.getResponseMessage();

		try {
			if (clientCtx.isOutboundRawMode()) { // raw mode - read everything
													// in memory
				if (httpClientResponse.isGzipped()) {
					clientResponse.setByteBuffer(BufferUtil
							.readInputStream(new GZIPInputStream(
									httpClientResponse.getContentStream())));
					httpClientResponse.deallocateContent();
				} else {
					byte[] rawData = httpClientResponse.getRawData();
					rawData = rawData == null ? new byte[0] : rawData;
					clientResponse.setByteBuffer(ByteBuffer.wrap(rawData));
				}
			} else {
				InputStream bis = httpClientResponse.getContentStream();
				clientResponse
						.setInputStream(httpClientResponse.isGzipped() ? new GZIPInputStream(
								bis)
								: bis);
			}
		} catch (IOException e) {
			throw new HTTPTransportException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_DECODE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {
							serviceLocationString,
							"Unzip failed " + e.getLocalizedMessage() }),
					httpClientResponse.getStatusCode(), e);
		}
	}

	private class AsyncClientCallBack implements NioAsyncCallback {

		private final BaseMessageContextImpl m_msgContext;

		private final AsyncCallBack m_callback;

		private final PipedOutputStream m_pipeOS;

		private PipedInputStream m_pipeIS;

		AsyncClientCallBack(BaseMessageContextImpl msgContext) {
			m_msgContext = msgContext;
			m_callback = m_msgContext.getServiceAsyncCallback();
			if (HTTPSyncAsyncClientTransport.this.isClientStreaming()) {
				m_pipeOS = new PipedOutputStream();
			} else {
				m_pipeOS = null;
			}
		}

		public void onException(Throwable cause) {
			closePipeIfNecessary();
			m_callback.onException(cause);
		}

		public void onResponse(Response response) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "******** onResponse() called");
			}
			try {
				if (!HTTPSyncAsyncClientTransport.this.isClientStreaming()) {
					if (LOGGER.isLoggable(Level.INFO)) {
						LOGGER
								.info("Processing message in non-clientStreaming mode");
					}
					// URL for destination
					URL serviceLocation = getServiceLocation(m_msgContext);
					String serviceLocationString = serviceLocation.toString();
					ResponseAvatar responseAvatar = new ResponseWrapper(
							response);
					doCommonResponseProcessing(serviceLocationString,
							m_msgContext, responseAvatar);
					setResponseToContextInputStream(serviceLocationString,
							responseAvatar, m_msgContext);
					m_callback.onResponseInContext();
				} else { // we're in client streaming mode
					// if no data has been previously pushed with
					// onResponseData(), spawn off empty response processing
					if (m_pipeIS == null) {
						m_pipeIS = new PipedInputStream(m_pipeOS);
						spawnOffClientProcessing(response);
					}
				}
			} catch (IOException e) {
				m_callback.onException(e);
			} catch (ServiceException e) {
				m_callback.onException(e);
			} finally {
				closePipeIfNecessary();
			}
		}

		/**
		 * Checks for the streaming mode and closes the pipe if streaming is on.
		 */
		private void closePipeIfNecessary() {
			if (HTTPSyncAsyncClientTransport.this.isClientStreaming()) {
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.info("Adjudicating pipe for clientStreaming mode");
				}
				closePipe();
			}
		}

		/**
		 * Closes the pipe - should only be called in client streaming mode. Use
		 * {@link #closePipeIfNecessary()} if you don't know you're in client
		 * streaming mode.
		 */
		private void closePipe() {
			try {
				m_pipeOS.close();
			} catch (IOException e) {
				HTTPSyncAsyncClientTransport.LOGGER.log(Level.SEVERE,
						"Could not close the PipeOutputStream", e);
			}
		}

		public void onTimeout() {
			closePipeIfNecessary();
			m_callback.onTimeout();
		}

		public boolean isDone() {
			return m_callback.isDone();
		}

		@Override
		public void onResponseData(byte[] data, Response response) {
			if (!HTTPSyncAsyncClientTransport.this.isClientStreaming()) {
				throw new IllegalStateException(
						"onResponseData only supported in responseStreaming mode");
			}
			final boolean firstInvocation = m_pipeIS == null;
			try {
				if (firstInvocation) { // first invocation
					if (LOGGER.isLoggable(Level.INFO)) {
						LOGGER.info("First invocation");
					}
					m_pipeIS = new PipedInputStream(m_pipeOS);
				}
			} catch (IOException e) {
				closePipe();
				m_callback.onException(e);
			}
			if (LOGGER.isLoggable(Level.FINE)) { // all the following madness is
													// due to findBugs forcing
													// us to use an explicit
													// encoding :(
				try {
					LOGGER.fine("Received: " + new String(data, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.log(Level.WARNING,
								"Error when trying to log :(", e);
					}
				}
			}
			try {
				m_pipeOS.write(data);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Could not write data to stream", e);
			}
			if (firstInvocation) {
				try {
					spawnOffClientProcessing(response);
				} catch (ServiceException e) {
					closePipe(); // it IS necessary, we know it for sure
					m_callback.onException(e);
				}
			}
		}

		private void spawnOffClientProcessing(Response response)
				throws ServiceException {
			URL serviceLocation = getServiceLocation(m_msgContext);
			final String serviceLocationString = serviceLocation.toString();
			final ResponseAvatar responseAvatar = new ResponseWrapperWithInputStream(
					response, m_pipeIS);
			doCommonResponseProcessing(serviceLocationString, m_msgContext,
					responseAvatar);
			AsyncCallBack.RunBefore runBefore = new AsyncCallBack.RunBefore() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void run() throws ServiceException {
					HTTPSyncAsyncClientTransport.this
							.setResponseToContextInputStream(
									serviceLocationString, responseAvatar,
									AsyncClientCallBack.this.m_msgContext);
				}

			};
			m_callback.onResponseInContext(runBefore);
		}
	}

	private static class CallBackRequestFuture<T> implements Future<T> {

		private final AsyncClientCallBack m_asyncClientCB;

		CallBackRequestFuture(AsyncClientCallBack asyncClientCB) {
			m_asyncClientCB = asyncClientCB;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public T get() throws InterruptedException, ExecutionException {
			throw new UnsupportedOperationException(
					"isDone() is only valid function to call");
		}

		public T get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException {
			return get();
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return m_asyncClientCB.isDone();
		}

	}

	public boolean supportsPoll() {
		return true;
	}

	public HTTPClientTransportConfig getConfig() {
		return m_config;
	}

	private static interface ResponseAvatar {

		public int getStatusCode();

		public HttpStatusEnum getRequestStatus();

		public String getHeader(String name);

		/**
		 * Returns an iterator of all header names.
		 */
		public Iterator<String> getHeaderNames();

		/**
		 * Returns an Iterator of Strings listing all Set-Cookie header values
		 * in response.
		 *
		 * @return Iterator over all Set-Cookie header values
		 */
		public Iterator getCookies();

		public InputStream getContentStream();

		public byte[] getRawData();

		public boolean isGzipped();

		/**
		 * deallocate content to free memory
		 */
		public void deallocateContent();

		public String getBody();

	}

	private static class ResponseWrapper implements ResponseAvatar {

		private final Response response;

		public ResponseWrapper(Response response) {
			super();

			this.response = response;
		}

		public Iterator getCookies() {
			return this.response.getCookies();
		}

		public String getHeader(String name) {
			return this.response.getHeader(name);
		}

		public Iterator<String> getHeaderNames() {
			return this.response.getHeaderNames();
		}

		public HttpStatusEnum getRequestStatus() {
			return this.response.getRequestStatus();
		}

		public int getStatusCode() {
			return this.response.getStatusCode();
		}

		@Override
		public InputStream getContentStream() {
			byte[] rawData = this.response.getRawData();
			return new ByteArrayInputStream(rawData == null ? new byte[0]
					: rawData);
		}

		@Override
		public byte[] getRawData() {
			return this.response.getRawData();
		}

		@Override
		public boolean isGzipped() {
			return this.response.isGzipped();
		}

		/**
		 * deallocate content to free memory
		 */
		public void deallocateContent() {
			this.response.deallocateContent();
		}

		@Override
		public String getBody() {
			return this.response.getBody();
		}

	}

	private static class ResponseWrapperWithInputStream extends ResponseWrapper {

		private final InputStream is;

		public ResponseWrapperWithInputStream(Response response, InputStream is) {
			super(response);

			this.is = is;
		}

		@Override
		public InputStream getContentStream() {
			return this.is;
		}

	}

	private static class FutureResponseWrapper implements ResponseAvatar {

		private final NioAsyncResponseFuture future;

		/**
		 * Do *NOT* use this directly - use {@link #getResponse()} instead.
		 */
		private Response response;

		public FutureResponseWrapper(NioAsyncResponseFuture future) {
			super();

			this.future = future;
		}

		/**
		 * Lazily retrieves the response from the future.
		 * <strong>Caution!</strong> This method will block until last piece of
		 * data is retrieved.
		 */
		protected Response getResponse() {
			if (this.response == null) {
				try {
					this.response = this.future.getStreamingResponseObject();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			return this.response;
		}

		@Override
		public Iterator getCookies() {
			return getResponse().getCookies();
		}

		@Override
		public String getHeader(String name) {
			return getResponse().getHeader(name);
		}

		@Override
		public Iterator<String> getHeaderNames() {
			return getResponse().getHeaderNames();
		}

		@Override
		public HttpStatusEnum getRequestStatus() {
			return getResponse().getRequestStatus();
		}

		@Override
		public int getStatusCode() {
			return getResponse().getStatusCode();
		}

		@Override
		public InputStream getContentStream() {
			return this.future.getResponseContentStream();
		}

		@Override
		public byte[] getRawData() {
			try {
				ByteBuffer byteBuffer = BufferUtil
						.readInputStream(getContentStream());
				return byteBuffer.array();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void deallocateContent() {
			// do *NOT* replace this - should only be called for raw mode
			getResponse().deallocateContent();
		}

		@Override
		public boolean isGzipped() {
			return getResponse().isGzipped();
		}

		/**
		 * Blocking method - reads all the response data and returns the body.
		 *
		 * @see org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport.ResponseAvatar#getBody()
		 */
		@Override
		public String getBody() {
			return getResponse().getBody();
		}

	}

}