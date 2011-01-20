/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.transport.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.InboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.LocalBindingThreadPool;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerMessageContextBuilder;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.HTTPServerUtils;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.ISOATransportRequest;
import org.ebayopensource.turmeric.runtime.spf.impl.transport.http.SOALocalTransportRequest;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;


/**
 * This class is a null transport, i.e, there is no physical transport. The
 * message is handed off directly to the next step. That next step is reall the
 * message processor on the side.
 * 
 * @author ichernyshev
 */
public class LocalTransport implements Transport {
	private final static int DFLT_REQUEST_TIMEOUT_MS = 1000;

	// SOA2.4, changing default skip serialization to true 
	private boolean m_configSkipSerialization;

	private boolean m_configUseDetachedLocalBinding;

	private int m_configInvocationTimeoutMs = DFLT_REQUEST_TIMEOUT_MS;

	// private String tName = "LocalTransport";

	public void checkTransportPoller(IAsyncResponsePoller holder) {
		if (holder.getTransportPoller() == null
				|| !(holder.getTransportPoller() instanceof LocalTransportPoller)) {
			holder.setTransportPoller(new LocalTransportPoller());
		}
	}

	public boolean supportsPoll() {
		return true;
	}

	public void init(InitContext ctx) throws ServiceException {
		ServerMessageContextBuilder.init();

		Boolean configSkipSer = ctx.getOptions().getSkipSerialization();
		if (configSkipSer != null && configSkipSer.booleanValue()) {
			m_configSkipSerialization = true;
		} else {
			m_configSkipSerialization = false;
		}
		// TODO - skip-serialization is not compatible with using protocol
		// processor. We should catch this, maybe
		// in Service.getProtocolProcessor().

		Boolean configUseDetached = ctx.getOptions()
				.isUseDetachedLocalBinding();
		// By default, detached local binding is used if not specified in
		// configuration
		m_configUseDetachedLocalBinding = false;
		if (configUseDetached != null) {
			m_configUseDetachedLocalBinding = configUseDetached.booleanValue();
		}

		Integer configInvTimeoutMs = ctx.getOptions().getInvocationTimeout();
		if (configInvTimeoutMs != null) {
			m_configInvocationTimeoutMs = configInvTimeoutMs.intValue();
		}
	}

	public Object preInvoke(MessageContext ctx) throws ServiceException {

		OutboundMessage clientRequestMsg = (OutboundMessage) ctx
				.getRequestMessage();
		// Set the content-type of response only if it has not been set (in
		// soap1.2 case,
		// this gets over-written at the Server Protocol processor.
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

	public void invoke(Message msg, TransportOptions invokerOptions)
			throws ServiceException {
		validate((OutboundMessage) msg);
		ClientMessageContext clientCtx = (ClientMessageContext) msg
				.getContext();
		ServerMessageContextBuilder builder = createServerContext(clientCtx,
				invokerOptions);
		invokeInternal(builder, clientCtx, invokerOptions);
	}

	public Future<?> invokeAsync(Message msg, TransportOptions invokerOptions)
			throws ServiceException {
		validate((OutboundMessage) msg);
		ClientMessageContext clientCtx = (ClientMessageContext) msg
				.getContext();
		ServerMessageContextBuilder builder = createServerContext(clientCtx,
				invokerOptions);

		@SuppressWarnings("unused")
		AsyncCallBack callback = ((BaseMessageContextImpl) clientCtx)
				.getServiceAsyncCallback();

		Future<MessageContext> pullFuture = getPullFuture(clientCtx, builder);

		return callback == null ? pullFuture : getPushFuture(clientCtx,
				builder, callback, pullFuture);
	}

	public void retrieve(MessageContext context, Future<?> futureResp)
			throws ServiceException {

		if (((BaseMessageContextImpl) context).getServiceAsyncCallback() != null) {
			return;
		}

		try {
			futureResp.get();
		} catch (Throwable t) {
			handleLocalBindingError(t, context, 0);
		}
	}

	private Future<MessageContext> getPushFuture(
			ClientMessageContext clientCtx,
			ServerMessageContextBuilder builder, AsyncCallBack callback,
			Future<MessageContext> pullFuture) throws ServiceException {

		Future<MessageContext> pushFuture = null;
		try {
			pushFuture = LocalBindingThreadPool.getInstance().execute(
					new CallBackWorker(pullFuture, callback));
		} catch (Throwable t) {
			handleLocalBindingError(t, clientCtx, 0);
		}

		return pushFuture;
	}

	private Future<MessageContext> getPullFuture(
			ClientMessageContext clientCtx, ServerMessageContextBuilder builder)
			throws ServiceException {

		Future<MessageContext> future = null;
		try {
			future = new WorkerFutureTask(new LocalBindingWorker(builder,
					clientCtx));
			LocalBindingThreadPool.getInstance().execute(
					(FutureTask<MessageContext>) future);

			IAsyncResponsePoller poller = ((BaseMessageContextImpl) clientCtx)
					.getServicePoller();
			LocalTransportPoller transpPoller = null;

			if (poller != null) {
				checkTransportPoller(poller);
				transpPoller = (LocalTransportPoller) poller
						.getTransportPoller();
			}

			if (transpPoller != null) {
				transpPoller.getBlockingQueue().add(future);
			}

		} catch (Throwable t) {
			handleLocalBindingError(t, clientCtx, 0);
		}
		return future;
	}

	private void invokeInternal(final ServerMessageContextBuilder builder,
			final MessageContext msgContext,
			final TransportOptions invokerTransportOptions)
			throws ServiceException {
		if (!isDetachedLocalBinding(invokerTransportOptions)) {
			builder.processCall();
			return;
		}
		// Detached local binding
		long timeout = getRequestTimeoutMs(invokerTransportOptions);
		try {
			Future<MessageContext> f = LocalBindingThreadPool.getInstance()
					.execute(new LocalBindingWorker(builder, msgContext));
			String testlog = System.getProperty("test.log.out");
			if(testlog!=null && testlog.equals("true"))
				System.out.println("<><><> TIMEOUT: "+ timeout);
			f.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Throwable t) {
			handleLocalBindingError(t, msgContext, timeout);
		}
	}

	private void handleLocalBindingError(final Throwable t,
			final MessageContext msgContext, final long timeout)
			throws ServiceException {
		Throwable cause = t;
		StringBuilder sb = new StringBuilder();
		ServiceException exception = null;
		if (t instanceof TimeoutException) {
			sb.append("Request timed out after ").append(timeout);
			sb.append(" ms in local transport for '");
			exception = new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_LOCAL_BINDING_TIMEOUT,
					ErrorConstants.ERRORDOMAIN, new Object[] { String.valueOf(timeout),
						msgContext.getAdminName(), msgContext.getOperationName() }));
		} else {
			cause = t.getCause();
			sb.append("Unexpected error in LocalTransport.invoke() for '");
			exception = new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_INVOCATION_FAILED_APP,
					ErrorConstants.ERRORDOMAIN), cause);
		}
		sb.append(msgContext.getAdminName()).append(".");
		sb.append(msgContext.getOperationName()).append("': ");
		sb.append(cause.toString());
		String errorMsg = sb.toString();
		// log it
		LogManager.getInstance(this.getClass()).log(Level.SEVERE, errorMsg, t);
		// throw the composed exception
		throw exception;
	}

	private void validate(OutboundMessage msg)
			throws ServiceException {
		ClientMessageContext clientCtx = (ClientMessageContext) msg
				.getContext();
		if (msg.isREST()) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_NO_GET_WITH_LOCAL,
					ErrorConstants.ERRORDOMAIN, new Object[] { clientCtx.getAdminName() }));
		}
		if (m_configSkipSerialization) {
			String messageProtocol = clientCtx.getMessageProtocol();
			if (!messageProtocol.equals(SOAConstants.MSG_PROTOCOL_NONE)) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_NO_SOAP_WITH_LOCAL,
							ErrorConstants.ERRORDOMAIN, new Object[] { clientCtx.getAdminName() }));
			}
		}
	}

	
	private ServerMessageContextBuilder createServerContext(
			ClientMessageContext clientCtx, TransportOptions options)
			throws ServiceException {
		OutboundMessage clientRequestMsg = (OutboundMessage) clientCtx
				.getRequestMessage();
		if (clientRequestMsg.isUnserializable()) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_UNSERIALIZABLE_MESSAGE,
						ErrorConstants.ERRORDOMAIN, 
						new Object[] { clientRequestMsg.getUnserializableReason()}));
		}
		
		/*
		 * Get admin name from metadata holder. It will be null for pre-ServiceUID services.
		 * Othierwise initialize the requestMetacontext with the admin name.
		 */
		ServiceDesc serviceDesc = ((ClientMessageContextImpl)clientCtx).getServiceDesc();
		MetadataPropertyConfigHolder metadatsHolder = serviceDesc.getConfig().getMetaData();
		String adminName =    metadatsHolder.getAdminName();		
		if (adminName==null || adminName.trim().length() <1)
			 adminName =   clientCtx.getAdminName();
		ISOATransportRequest soaRequest = SOALocalTransportRequest.createRequest(clientCtx,options);  
		HTTPServerUtils serverUtils = new HTTPServerUtils(soaRequest, adminName, null);
		
		boolean skipSerialization;
		if (options.getSkipSerialization() != null) {
			// ServiceInvokerOptions override of config
			skipSerialization = options.getSkipSerialization().booleanValue();
		} else {
			// Use configured skip-serialization value
			skipSerialization = m_configSkipSerialization;
		}

		Transport responseTransport = new LocalServerResponseTransport(
				clientCtx, skipSerialization);
		
		ServerMessageContextBuilder builder = serverUtils.createMessageContext(responseTransport);
		
		// TODO: config it
		boolean hasInboundAttachments = false;
		if (hasInboundAttachments) {
			builder.setRequestAttachments(new InboundMessageAttachments());
		}

		if (skipSerialization) {
			// Directly set the outbound (client) request params, to the inbound
			// (server) request params.
			int paramCount = clientRequestMsg.getParamCount();
			Object[] serverParams = new Object[paramCount];
			for (int i = 0; i < paramCount; i++) {
				Object param = clientRequestMsg.getParam(i);
				serverParams[i] = param;
			}
			builder.setParamReferences(serverParams);
		} else {
			byte[] body = serializeData(clientRequestMsg);
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			builder.setInputStream(bis);
		}

		return builder;
	}


	private Cookie[] cloneCookies(Cookie[] cookies) {
		if (cookies == null) {
			return null;
		}

		Cookie[] result = new Cookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			Cookie oldCookie = cookies[i];
			Cookie newCookie = new Cookie(oldCookie.getName(), oldCookie
					.getValue());
			result[i] = newCookie;
		}

		return result;
	}

	private byte[] serializeData(OutboundMessage outboundMsg)
			throws ServiceException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		outboundMsg.serialize(bos);
        // LogManager.getInstance(LocalTransport.class).log(Level.INFO, 
        // "Response msg: " + bos.toString()); 	 
		return bos.toByteArray();
	}

	private void transferParamReferences(OutboundMessage outboundMsg,
			InboundMessage inboundMsg) throws ServiceException {
		if (outboundMsg.isErrorMessage()) {
			inboundMsg
					.setErrorResponseReference(outboundMsg.getErrorResponse());
		} else {
			int paramCount = outboundMsg.getParamCount();
			Object[] serverParams = new Object[paramCount];
			for (int i = 0; i < paramCount; i++) {
				Object param = outboundMsg.getParam(i);
				serverParams[i] = param;
			}
			inboundMsg.setParamReferences(serverParams);
		}
	}

	void populateClientResponse(ServerMessageContext serverCtx,
			ClientMessageContext clientCtx, boolean skipSerialization)
			throws ServiceException {
		InboundMessage clientResponse = (InboundMessage) clientCtx
				.getResponseMessage();
		OutboundMessage serverResponse = (OutboundMessage) serverCtx
				.getResponseMessage();

		Map<String, String> transportHeaders = serverResponse
				.buildOutputHeaders();

		if (transportHeaders != null) {
			for (Iterator<Map.Entry<String, String>> it = transportHeaders
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> e = it.next();
				clientResponse.setTransportHeader(e.getKey(), e.getValue());
			}
		}

		Cookie[] cookies = cloneCookies(serverResponse.getCookies());

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				clientResponse.setCookie(cookies[i]);
			}
		}

		if (serverResponse.isUnserializable()) {
			clientResponse.unableToProvideStream();
			ServiceException e = new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_UNSERIALIZABLE_MESSAGE,
							ErrorConstants.ERRORDOMAIN, new Object[] { "Server returned: "
					+ serverResponse.getUnserializableReason() }));
			
			clientCtx.addError(e);
			return;
		}

		if (skipSerialization) {
			// Directly set the outbound (client) request params, to the inbound
			// (server) request params.
			transferParamReferences(serverResponse, clientResponse);
		} else {
			boolean gotBytes = false;
			byte[] body = null;
			try {
				body = serializeData(serverResponse);
				gotBytes = true;
			} catch (Throwable e) {
				clientCtx.addError(e);
				clientResponse.unableToProvideStream();
			}

			if (gotBytes) {
				if (clientCtx.isOutboundRawMode()) {
					clientResponse.setByteBuffer(ByteBuffer.wrap(body));
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(body);
				clientResponse.setInputStream(bis);
			}
		}
	}

	/**
	 * CONFIGURATION MERGERS
	 * 
	 * The following methods are responsible for merging the configuration
	 * between the static configuration and the invoker options. The static
	 * configuration includes the defaults, the overrides and the dynamic
	 * changes from the config beans.
	 * 
	 * In a nutshell, if there's no override from the invoker option, use the
	 * one from the static configuration. The invoker options essentially
	 * supercede the static configuration.
	 */
	private boolean isDetachedLocalBinding(final TransportOptions invokerOptions) {
		Boolean isInvokerDetached = invokerOptions.isUseDetachedLocalBinding();
		if (isInvokerDetached == null) {
			return m_configUseDetachedLocalBinding;
		}
		return isInvokerDetached.booleanValue();
	}

	private int getRequestTimeoutMs(final TransportOptions invokerOptions) {
		Integer requestTimeoutMs = invokerOptions.getInvocationTimeout();
		if (requestTimeoutMs == null) {
			return m_configInvocationTimeoutMs;
		}
		return requestTimeoutMs.intValue();
	}

	/**
	 * end of CONFIGURATION MERGERS
	 */

	// ------------------- Inner class -----------------------
	private class LocalBindingWorker implements Callable<MessageContext> {
		private ServerMessageContextBuilder m_msgContextBuilder;

		private MessageContext m_msgContext;

		private Future<?> m_future;

		LocalBindingWorker(final ServerMessageContextBuilder builder,
				final MessageContext msgContext) {
			m_msgContextBuilder = builder;
			m_msgContext = msgContext;
		}

		@SuppressWarnings( { "unchecked", "synthetic-access" })
		public MessageContext call() throws Exception {
			m_msgContextBuilder.processCall();
			BaseMessageContextImpl clientCtx = (BaseMessageContextImpl) m_msgContext;

			if (clientCtx.getServicePoller() != null
					&& clientCtx.getServicePoller().getTransportPoller() != null
					&& m_future != null) {
				((LocalTransportPoller) clientCtx.getServicePoller()
						.getTransportPoller()).getBlockingQueue().add(m_future);
			}

			return m_msgContext;
		}

		public void setFuture(Future<?> future) {
			m_future = future;
		}
	}

	private static class WorkerFutureTask extends FutureTask<MessageContext> {
		public WorkerFutureTask(LocalBindingWorker worker) {
			super(worker);
			worker.setFuture(this);
		}
	}

	private class LocalServerResponseTransport implements Transport {
		private ClientMessageContext m_clientCtx;

		private boolean m_skipSerialization;

		public LocalServerResponseTransport(ClientMessageContext clientCtx,
				boolean skipSerialization) {
			this.m_clientCtx = clientCtx;
			this.m_skipSerialization = skipSerialization;
		}

		public void init(InitContext ctx) throws ServiceException {
			// noop
		}

		public Object preInvoke(MessageContext ctx) throws ServiceException {
			OutboundMessage serverResponse = (OutboundMessage) ctx
					.getResponseMessage();

			DataBindingDesc binding = serverResponse.getDataBindingDesc();
			String mimeType = binding.getMimeType();
			Charset charset = serverResponse.getG11nOptions().getCharset();
			String contentType = HTTPCommonUtils.formatContentType(mimeType,
					charset);
			serverResponse.setTransportHeader(
					SOAConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
			return null;
		}

		public void invoke(Message msg, TransportOptions options)
				throws ServiceException {
			ServerMessageContext serverCtx = (ServerMessageContext) msg
					.getContext();
			populateClientResponse(serverCtx, m_clientCtx, m_skipSerialization);
		}

		public Future<?> invokeAsync(Message msg,
				TransportOptions transportOptions) throws ServiceException {
			throw new UnsupportedOperationException(
					"Async invoke is not supported on "
							+ "LocalTransport.LocalServerResponseTransport");
		}

		public void retrieve(MessageContext context, Future<?> futureResp)
				throws ServiceException {
			throw new UnsupportedOperationException(
					"Async retrieve is not supported on "
							+ "LocalTransport.LocalServerResponseTransport");
		}

		public boolean supportsPoll() {
			throw new UnsupportedOperationException(
					"supportsPoll is not supported on "
							+ "LocalTransport.LocalServerResponseTransport");

		}
	}

	private static class CallBackWorker implements Callable<MessageContext> {

		private final Future<MessageContext> m_asyncFuture;

		private final AsyncCallBack m_callback;

		CallBackWorker(final Future<MessageContext> asyncFuture,
				final AsyncCallBack callback) {
			m_asyncFuture = asyncFuture;
			m_callback = callback;
		}

		public MessageContext call() throws Exception {
			MessageContext msgCtx = null;
			try {
				msgCtx = m_asyncFuture.get();
				m_callback.onResponseInContext();
			} catch (Throwable e) {
				m_callback.onException(e);
			}
			return msgCtx;
		}
	}

}
