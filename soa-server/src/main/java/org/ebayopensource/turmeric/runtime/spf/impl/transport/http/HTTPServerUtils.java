/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.transport.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.OperationMapping;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.OperationMappings;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.RequestParamsDescriptor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.RequestParamsDescriptor.RequestParams;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.UrlMappingsDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerMessageContextBuilder;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServiceResolver;
import org.ebayopensource.turmeric.runtime.spf.pipeline.PseudoOperationHelper;
import org.ebayopensource.turmeric.runtime.spf.pipeline.RequestMetaContext;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;
import com.ebay.kernel.util.IpAddressUtils;
import com.ebay.kernel.util.JdkUtil;
import com.ebay.kernel.util.StringUtils;

/**
 * @author rmurphy
 * @author ichernyshev
 */
public final class HTTPServerUtils {
	public static final String HTTP_1_1 = "HTTP/1.1";
	public static final String HTTP_1_0 = "HTTP/1.0";
	public static final String HTTP_SERVLET_REQUEST = "HttpServletRequest";

	// This will be changed in the future to be a configurable parameter per
	// service. This parameter signifies whether security header are allowed to
	// be part
	// of URL in GET requests
	public static final boolean nonAnonGetAllowed = true;

	// Namespace token prefix
	private static final String NAMESPACE_PREFIX = "nvns:";

	// Security header prefix
	private static final String SECURITY_HEADER_PREFIX = "X-TURMERIC-SECURITY";

	private final ISOATransportRequest m_request;
	private final RequestMetaContext m_reqMetaCtx;
	private final String m_rawRequestUri;
	private final boolean m_isGetMethod;
	private final ServiceResolver m_serviceResolver;
	private final List<ParamData> m_params;
	private int m_systemParamCount;
	private List<Throwable> m_errors;

	private final int relativeOffset; // '0' based

	
	private final static Logger s_logger = Logger.getInstance(JdkUtil
			.forceInit(HTTPServerUtils.class));

	public HTTPServerUtils(ISOATransportRequest request,
			String requiredAdminName, String urlMatchExpression)
			throws ServiceException {
		m_request = request;

		m_rawRequestUri = request.getRequestURI();

		m_isGetMethod = request.getMethod().equalsIgnoreCase("GET");

		m_reqMetaCtx = new RequestMetaContext(m_isGetMethod, m_rawRequestUri,
				requiredAdminName);

		m_params = new ArrayList<ParamData>();
		// we have to always parse parameters, as we do not know
		// whether parameter index will be used in URL mappings
		parseParameters();

		m_systemParamCount = m_params.size();

		extractTransportHeaders();
		extractSystemParameters();

		m_reqMetaCtx.setUrlMatchExpression(urlMatchExpression);
		m_reqMetaCtx.setQueryParams(getQueryParams());

		m_serviceResolver = new ServiceResolver(m_reqMetaCtx);
		this.relativeOffset = getRelativeOffset(request.getServletPath());
		
		checkRejectList();

		getUrlMappedHeaders();

		if (m_isGetMethod) {
			adjustRestHeaders();
		}
	}

	public RequestMetaContext getReqMetaCtx() {
		return m_reqMetaCtx;
	}

	public ServiceResolver getServiceResolver() {
		return m_serviceResolver;
	}

	private void addError(Throwable th) {
		if (m_errors == null) {
			m_errors = new ArrayList<Throwable>();
		}

		m_errors.add(th);
	}

	private int getRelativeOffset(String servletPath) {		
		if(servletPath == null) {
			return 0;
		}
		return servletPath.split("/").length - 2;
	} 
	


	public ServerMessageContextBuilder createMessageContext(
			Transport responseTransport) throws ServiceException {

		Map<String, String> rawTransportHeaders = m_reqMetaCtx
				.getTransportHeaders();
		String forwardedFor = rawTransportHeaders
				.get(SOAConstants.HTTP_HEADER_FORWARDED_FOR);
		String clientPoolName = rawTransportHeaders
				.get(SOAConstants.HTTP_HEADER_CLIENT_POOL_NAME);
		ServiceAddress clientAddress = m_request.getClientAddress();
		ServiceAddress serviceAddress = m_request.getServiceAddress();

		String requestTransport;

		if (m_request.getProtocol().equals(HTTP_1_1)) {
			requestTransport = SOAConstants.TRANSPORT_HTTP_11;
		} else if (m_request.getProtocol().equals(HTTP_1_0)) {
			requestTransport = SOAConstants.TRANSPORT_HTTP_10;
		} else if (m_request.getProtocol().equals(SOAConstants.TRANSPORT_LOCAL)) {
			requestTransport = SOAConstants.TRANSPORT_LOCAL;
		} else
			requestTransport = SOAConstants.TRANSPORT_HTTP_11;

		Cookie[] soaCookies = m_request.retrieveCookies();

		ServerMessageContextBuilder builder = new ServerMessageContextBuilder(
				m_serviceResolver, m_rawRequestUri, requestTransport,
				responseTransport, rawTransportHeaders, soaCookies,
				clientAddress, serviceAddress, m_errors,
				m_request.getServerName(), m_request.getServerPort(),
				m_reqMetaCtx.getQueryParams());

		if (forwardedFor != null) {
			forwardedFor = forwardedFor.trim();
			builder.setContextProperty(
					SOAConstants.CTX_PROP_TRANSPORT_FORWARDED_FOR, forwardedFor);
		}

		if (clientPoolName != null) {
			clientPoolName = clientPoolName.trim();
			builder.setContextProperty(SOAConstants.CTX_PROP_CLIENT_POOL_NAME,
					clientPoolName);
		}

		// Setting the HttpServlet Request into the MessageContext for later use
		// by ClientIpHandler
		if (!requestTransport.equalsIgnoreCase(SOAConstants.TRANSPORT_LOCAL)) {
			builder.setContextProperty(HTTP_SERVLET_REQUEST,
					m_request.getUnderlyingObject());
		}

		G11nOptions g11n = builder.getG11nOptions();
		Charset charset = g11n.getCharset();
		String encoding = charset.name();

		if (!requestTransport.equalsIgnoreCase(SOAConstants.TRANSPORT_LOCAL)) {
			InputStream inputStream = getInputStream(encoding);
			builder.setInputStream(inputStream);
		}

		return builder;
	}

	private void checkRejectList() throws ServiceException {

		ServerServiceDesc serviceDesc = m_serviceResolver.lookupServiceDesc();
		UrlMappingsDesc urlMappings = serviceDesc.getUrlMappings();
		Set<String> rejectList = urlMappings.getRejectList();
		if (rejectList == null) {
			return;
		}

		for (int i = 0; i < m_params.size(); i++) {
			ParamData param = m_params.get(i);

			String name = param.getDecodedName();
			if (rejectList.contains(name)) {
				addError(new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_RT_HEADER_NOT_ALLOWED_IN_URL,
						ErrorConstants.ERRORDOMAIN, new String[] { name })));
			}

		}
	}

	private void extractTransportHeaders() throws ServiceException {
		Map<String, String> headers = m_request.getHeaderNames();
		Set<Map.Entry<String, String>> headerSet = headers.entrySet();
		for (Map.Entry<String, String> header : headerSet) {
			String name = SOAHeaders.normalizeName(header.getKey(), true);
			addTransportHeader(name, header.getValue());
		}
	}

	private void extractSystemParameters() throws ServiceException {
		// get from query string
		for (int i = 0; i < m_params.size(); i++) {
			ParamData param = m_params.get(i);
			String name = SOAHeaders
					.normalizeName(param.getDecodedName(), true);
			if (SOAHeaders.isSOAHeader(name)) {
				if (m_isGetMethod && name.equals(SOAHeaders.REST_PAYLOAD)) {
					m_systemParamCount = i;
					param.setConsumed();
					continue;
				}

				String value = param.getDecodedValue();
				addTransportHeader(name, value);
				param.setConsumed();
			} else if (PseudoOperationHelper.isPseudoOpParam(name)) {
				String value = param.getDecodedValue();
				addPseudoOpParameter(name, value, true, false);
				param.setConsumed();
			}
		}
	}

	private void getUrlMappedHeaders() throws ServiceException {
		ServerServiceDesc serviceDesc = m_serviceResolver.lookupServiceDesc();
		UrlMappingsDesc mappingDesc = serviceDesc.getUrlMappings();
		if(mappingDesc != null) {
			applyQueryMap(mappingDesc);
			applyQueryOp(mappingDesc);
			applyPathMap(mappingDesc);
		}

		List<Throwable> errors = new ArrayList<Throwable>();
		HeaderMappingsDesc requestHeaderMappings = serviceDesc.getRequestHeaderMappings();
		HTTPCommonUtils.applyHeaderMap(requestHeaderMappings.getHeaderMap(), 
				m_reqMetaCtx.getTransportHeaders(), errors);
		if (errors.size() > 0) {
			if (m_errors == null)
				m_errors = errors;
			else
				m_errors.addAll(errors);
		}
		HTTPCommonUtils.applySuppressHeaderSet(
				requestHeaderMappings.getSuppressHeaderSet(),
				m_reqMetaCtx.getTransportHeaders());
		
		String operationName = m_reqMetaCtx.getTransportHeaders().get(SOAHeaders.SERVICE_OPERATION_NAME);
		// do custom mapping of operation name
		if (operationName != null) {
			OperationMappings operationMappings = serviceDesc.getOperationMappings();



			if (operationMappings != null) {
				OperationMapping operationMapping = operationMappings.getOperationMapping(operationName);

				if (operationMapping != null) {
					operationName = operationMapping.getOperationName();
					HTTPCommonUtils.addTransportHeader(
							SOAHeaders.SERVICE_OPERATION_NAME, operationName, 
							m_reqMetaCtx.getTransportHeaders(), false, false, errors);
				}
			}
		}
		// Do the request parameter mapping first as it has the lease priority.
		// If the same parameter specified in the query parameter,
		// that has to be taken into account.		
		applyOperationRequestParamMap(operationName, serviceDesc.getOperationRequestParamsDescriptor());
		applyAliases(operationName, serviceDesc.getOperationRequestParamsDescriptor());	

	}

	private void applyAliases(String operationName,
			RequestParamsDescriptor operationRequestParamsDescriptor) {
		// If the request param mapping is not defined, exit immediately.
		if (operationRequestParamsDescriptor == null || operationName == null) {
			return;
		}				
		RequestParams reqParams = operationRequestParamsDescriptor.getRequestParams(operationName);
		if(reqParams == null) {
			return; 
		}
		List<ParamData> newParams = new ArrayList<ParamData>();
		for(Iterator<ParamData> i = m_params.iterator() ; i.hasNext(); ) {
			ParamData param = i.next();
			String alias = param.getRawName();
			String paramName = reqParams.getParamName(alias);			
			if(paramName != null) {
				newParams.add(new ParamData(paramName, param.getRawValue(), param.getIndex()));
				i.remove();				
			}
		}			
		m_params.addAll(newParams);
	}

	private void applyOperationRequestParamMap(String operationName,
			RequestParamsDescriptor operationRequestParamsDescriptor) {

		// If the request param mapping is not defined, exit immediately.
		if (operationRequestParamsDescriptor == null) {
			return;
		}
		// If there is no request uri, exit.
		if (m_rawRequestUri == null || m_rawRequestUri.trim().length() < 2) {
			return;
		}
		List<String> pathParts = StringUtils.splitStr(m_rawRequestUri.substring(1), '/');		
		RequestParams reqParams = operationRequestParamsDescriptor.getRequestParams(operationName);
		// Build the NV pair with name as seen in the ServiceConfig and value as
		// passed in the URI. Append the NP pairs to the payload.
		if (reqParams == null || reqParams.count() <= 0) {
			return;
		}

		for (Map.Entry<String, String> entry : reqParams.entries()) {
			int pathIndex = Integer.parseInt(entry.getKey());
			if(pathIndex < 0) {
				// negative index implies relative indexing
				pathIndex = relativeOffset  + -pathIndex;
			}			
			String pname = entry.getValue();
			if (pname != null && pathIndex < pathParts.size()) {
				String pvalue = pathParts.get(pathIndex);
				m_params.add(new ParamData(pname, pvalue, m_params.size()));
			}
		}
	}

	public String getRequestUriForLogging(String requestUri) {
		if (requestUri.length() > 80) {
			return requestUri.substring(0, 80);
		}

		return requestUri;
	}

	private void applyPathMap(UrlMappingsDesc mappingDesc)
			throws ServiceException {
		Map<Integer, String> pathMap = mappingDesc.getPathMap();

		if (m_rawRequestUri == null || m_rawRequestUri.trim().length() < 2)
			return;

		List<String> pathParts = StringUtils.splitStr(m_rawRequestUri.substring(1), '/');
		for (Map.Entry<Integer, String> entry : pathMap.entrySet()) {
			String headerName = entry.getValue();
			checkIfSecurityCredential(headerName);
			int i = entry.getKey().intValue();
			if (i < 0) {
				// Negative index indicates relative index
				i = relativeOffset + -i;
			}
			if (i < pathParts.size()) {
				String headerValue = pathParts.get(i);
				addTransportHeader(headerName, headerValue);
			}
		}
	}

	private void applyQueryOp(UrlMappingsDesc mappingDesc)
			throws ServiceException {
		String queryOpMapping = mappingDesc.getQueryOpMapping();
		if (queryOpMapping == null || m_systemParamCount == 0) {
			return;
		}
		checkIfSecurityCredential(queryOpMapping);
		ParamData param = m_params.get(0);
		addTransportHeader(queryOpMapping, param.getDecodedName());
		param.setConsumed();
	}

	private Map<String, String> getQueryParams() {
		Map<String, String> queryParams = new LinkedHashMap<String, String>();

		// Fix for issue bug 540599. Since payload can occur before the
		// payload marker, we need to
		for (int i = 0; i < m_params.size(); i++) {
			ParamData param = m_params.get(i);
			queryParams.put(param.getDecodedName(), param.getDecodedValue());
		}

		return queryParams;
	}

	private void applyQueryMap(UrlMappingsDesc mappingDesc)
			throws ServiceException {
		Map<String, String> queryMap = mappingDesc.getQueryMap();
		Map<String, String> upperCaseQueryMap = mappingDesc
				.getUpperCaseQueryMap();
		for (int i = 0; i < m_params.size(); i++) {

			ParamData param = m_params.get(i);
			String paramName = param.getDecodedName();
			String headerName = queryMap.get(paramName);
			if (headerName == null) {
				headerName = upperCaseQueryMap.get(paramName.toUpperCase());
			}

			if (headerName != null) {
				checkIfSecurityCredential(headerName);
				String paramValue = param.getDecodedValue();
				addTransportHeader(headerName, paramValue);
				param.setConsumed();
			}
		}
	}

	private void addTransportHeader(String name, String value)
			throws ServiceException {
		addTransportHeader(name, value, true, false);
	}

	private void addTransportHeader(String name, String value,
			boolean checkConflicts, boolean keepOriginalValue)
			throws ServiceException {
		addParameter(name, value, m_reqMetaCtx.getTransportHeaders(),
				checkConflicts, keepOriginalValue);
	}

	private void addPseudoOpParameter(String name, String value,
			boolean checkConflicts, boolean keepOriginalValue)
			throws ServiceException {
		addParameter(name, value, m_reqMetaCtx.getPseudoOperationParameters(),
				checkConflicts, keepOriginalValue);
	}

	private void addParameter(String name, String value,
			Map<String, String> target, boolean checkConflicts,
			boolean keepOriginalValue) throws ServiceException {
		String oldValue = target.get(name);
		if (oldValue != null) {
			if (oldValue.equals(value)) {
				return;
			}

			if (checkConflicts) {
				addError(new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_RT_PARAM_CONFLICT,
						ErrorConstants.ERRORDOMAIN, new Object[] { name,
								oldValue, value })));
				return;
			}

			if (keepOriginalValue) {
				return;
			}
		}

		target.put(name, value);
	}

	private void parseParameters() {
		String str = m_request.getQueryString();
		if (str == null || str.length() == 0) {
			return;
		}

		int startPos = 0;
		int equalPos = -1;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '&') {
				parseParameter(str, startPos, i, equalPos);
				startPos = i + 1;
				equalPos = -1;
				continue;
			}
			if (c == '=' && equalPos == -1) {
				equalPos = i;
			}
		}

		if (startPos < str.length()) {
			parseParameter(str, startPos, str.length(), equalPos);
		}
	}

	private void parseParameter(String str, int start, int end, int equalPos) {
		String name;
		String value;
		if (equalPos != -1) {
			name = str.substring(start, equalPos);
			value = str.substring(equalPos + 1, end);
		} else {
			name = str.substring(start, end);
			value = "";
		}

		checkIfSecurityCredential(name);		
		m_params.add(new ParamData(name, value, m_params.size()));
	}

	/**
	 * This method checks in the case of the GET request the specified param
	 * name is a security-credential. If it is, an error is added to the
	 * error-list.
	 * 
	 * @param paramName
	 *            the parameter name
	 * @return true, if it is a security credential, false otherwise
	 */
	private void checkIfSecurityCredential(String paramName) {
		if (!nonAnonGetAllowed) {
			// Check if this query parameter name is a security header
			if (m_isGetMethod
					&& (paramName != null)
					&& paramName.toUpperCase().startsWith(
							SECURITY_HEADER_PREFIX)) {

				// do error
				addError(new ServiceException(
						ErrorDataFactory
								.createErrorData(
										org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.SVC_SECURITY_INVALID_URL_CREDENTIALS,
										org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.ERRORDOMAIN,
										new String[] { paramName })));

			}
		}
	}

	static String decode(String str) {
		int idx = str.indexOf('%');
		int idx2 = str.indexOf('+');
		if (idx == -1 && idx2 == -1) {
			return str;
		}

		// find first position
		if (idx == -1) {
			idx = idx2;
		} else if (idx2 != -1 && idx > idx2) {
			idx = idx2;
		}

		int len = str.length();
		StringBuilder sb = new StringBuilder(len);
		sb.append(str.substring(0, idx));
		for (int i = idx; i < len; i++) {
			char c = str.charAt(i);

			if (c == '+') {
				sb.append(' ');
				continue;
			}

			if (c != '%') {
				sb.append(c);
				continue;
			}

			// read next 2 digits
			if (i + 2 >= len) {
				// invalid
				sb.append(c);
				continue;
			}

			char c1 = str.charAt(i + 1);
			char c2 = str.charAt(i + 2);
			i += 2;

			int b1 = BindingUtils.getHexDigitValue(c1);
			int b2 = BindingUtils.getHexDigitValue(c2);
			if (b1 == -1 || b2 == -1) {
				// invalid
				sb.append(c1);
				sb.append(c2);
				continue;
			}

			sb.append((char) ((b1 << 4) | b2));
		}

		return sb.toString();
	}

	private String buildQueryString() {
		StringBuilder sb = new StringBuilder(1024);
		ParamData namespaceParamData = null;

		// Per Ron, we need to take into account payload parameters
		// that happen to appear before the payload-marker to
		// fix bug 540599
		int startPos = 0;

		for (int i = startPos; i < m_params.size(); i++) {
			ParamData param = m_params.get(i);
			if (param.isConsumed()) {
				continue;
			}

			String name = param.getRawName();
			String value = param.getRawValue();

			// case insensitive check to figure out of this is
			// the first namespace parameter
			if ((namespaceParamData == null)
					&& name.toLowerCase().startsWith(NAMESPACE_PREFIX)) {
				namespaceParamData = param;
				continue;
			}

			if (sb.length() > 0) {
				sb.append('&');
			}

			sb.append(name);
			sb.append('=');

			if (value != null && value.length() > 0) {
				sb.append(value);
			}
		}

		if (namespaceParamData == null) {
			return sb.toString();
		}

		StringBuilder ns = new StringBuilder();
		ns.append(namespaceParamData.getRawName());
		if (namespaceParamData.getRawValue() != null) {
			ns.append("=").append(namespaceParamData.getRawValue());
		}

		if (sb.length() == 0) {
			return ns.toString();
		}

		return ns.append("&").append(sb.toString()).toString();

	}

	private InputStream getInputStream(String encoding) throws ServiceException {
		try {
			if (m_isGetMethod) {
				// remove consumed params
				String queryString = buildQueryString();

				return new ByteArrayInputStream(queryString.getBytes(encoding));
			}

			return m_request.getInputStream();
		} catch (IOException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_TRANSPORT_INBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN), e);
		}
	}

	// Has Req DF Has Resp DF Final Req DF Final Resp DF
	// ======================================================================
	// T T Incoming Req DF Incoming Resp DF
	// T F Incoming Req DF Incoming Req DF
	// F T NV Incoming Resp DF
	// F F NV XML
	private void adjustRestHeaders() throws ServiceException {

		addTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE, "FALSE",
				false, true);
		// addTransportHeader(SOAHeaders.REQUEST_DATA_FORMAT,
		// BindingConstants.PAYLOAD_NV, false, true);
		// addTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT,
		// BindingConstants.PAYLOAD_XML, false, true);

		if (!m_reqMetaCtx.getTransportHeaders().containsKey(
				SOAHeaders.REQUEST_DATA_FORMAT)) {
			addTransportHeader(SOAHeaders.REQUEST_DATA_FORMAT,
					BindingConstants.PAYLOAD_NV, false, true);
			if (!m_reqMetaCtx.getTransportHeaders().containsKey(
					SOAHeaders.RESPONSE_DATA_FORMAT)) {
				addTransportHeader(SOAHeaders.RESPONSE_DATA_FORMAT,
						BindingConstants.PAYLOAD_XML, false, true);
			}
		}

	}

	private boolean isRestCall() {
		if (m_isGetMethod
				&& !m_reqMetaCtx.getTransportHeaders().containsKey(
						SOAHeaders.REQUEST_DATA_FORMAT)
				&& !m_reqMetaCtx.getTransportHeaders().containsKey(
						SOAHeaders.RESPONSE_DATA_FORMAT)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This detects the first public proxy ip from the commas delimited IP list
	 * from the x-forwarded-for header going from left to right. An IP is
	 * considered PRIVATE if it falls in one of the following IP address spaces
	 * as noted in RFC1918,
	 * 
	 * 10.0.0.0 - 10.255.255.255 (10/8 prefix) 172.16.0.0 - 172.31.255.255
	 * (172.16/12 prefix) 192.168.0.0 - 192.168.255.255 (192.168/16 prefix)
	 * 
	 * An empty string is returned should 1) the IP list from the header is
	 * invalid 2) no public proxy IP has been identified
	 * 
	 * @param xForwardedForHeader
	 * @return String proxy ip
	 */
	public static String getFirstPublicProxyIP(final String xForwardedForHeader) {
		if (xForwardedForHeader == null || xForwardedForHeader.length() == 0) {
			if (isDebugOn()) {
				log("getFirstPublicProxyIP() - x-forwarded-for header is empty!");
			}
			return "";
		}
		final StringTokenizer st = new StringTokenizer(xForwardedForHeader, ",");
		String token = "";
		while (st.hasMoreTokens()) {
			token = st.nextToken().trim();
			if (isPublicIpAddress(token)) {
				if (isDebugOn()) {
					log("The first *public* proxy IP (x-forwarded-for) - "
							+ token);
				}
				return token;
			}
		}
		if (isDebugOn()) {
			log("getFirstPublicProxyIP() - no *public* proxy IP is found (x-forwarded-for)!");
		}
		return "";
	}

	private static final int RFC1918_10_0_0_0 = 0xA000000; // 10.0.0.0
	private static final int RFC1918_10_255_255_255 = 0xAFFFFFF; // 10.255.255.255
	private static final int RFC1918_172_16_0_0 = 0xAC100000; // 172.16.0.0
	private static final int RFC1918_172_31_255_255 = 0xAC1FFFFF; // 172.31.255.255
	private static final int RFC1918_192_168_0_0 = 0xC0A80000; // 192.168.0.0
	private static final int RFC1918_192_168_255_255 = 0xC0A8FFFF; // 192.168.255.255
	private static final String IP_ADDRESS_LOCALHOST = "127.0.0.1";

	private static boolean isPublicIpAddress(final String ipAddress) {
		if (ipAddress == null || ipAddress.length() == 0) {
			if (isDebugOn()) {
				log("isPublicIpAddress() - ip address is null or empty!");
			}
			return false;
		}

		// Exclude the self-looping scenario. Note that this should not happen.
		// If it does, it's a good indication that the request has been
		// 'hacked'.
		if (ipAddress.equalsIgnoreCase(IP_ADDRESS_LOCALHOST)) {
			log(LogLevel.WARN,
					"Localhost ip detected from header, x-forwarded-for: "
							+ ipAddress);
			return false;
		}

		try {
			final InetAddress inetAddr = IpAddressUtils.getByName(ipAddress);
			final byte[] addrInBytes = inetAddr.getAddress();

			// The byte array is in network order and the most significant
			// byte of the address is in byte 0.
			int converted = addrInBytes[3] & 0xFF;
			converted |= ((addrInBytes[2] << 8) & 0xFF00);
			converted |= ((addrInBytes[1] << 16) & 0xFF0000);
			converted |= ((addrInBytes[0] << 24) & 0xFF000000);
			return (!(converted >= RFC1918_10_0_0_0 && converted <= RFC1918_10_255_255_255)
					&& !(converted >= RFC1918_172_16_0_0 && converted <= RFC1918_172_31_255_255) && !(converted >= RFC1918_192_168_0_0 && converted <= RFC1918_192_168_255_255));

		} catch (UnknownHostException e) {
			log(LogLevel.WARN, "UnknowHostException (x-forwarded-for): "
					+ ipAddress);
			return false;
		}
	}

	// End of borrowed code

	static boolean isDebugOn() {
		return s_logger.isLogEnabled(LogLevel.DEBUG);
	}

	static void log(final String message) {
		s_logger.debug(message);
	}

	static void log(final Throwable throwable) {
		s_logger.log(LogLevel.DEBUG, throwable);
	}

	static void log(final LogLevel level, final String message) {
		s_logger.log(level, message);
	}

	static class ParamData {
		private final String m_rawName;
		private final String m_rawValue;
		private final boolean m_hasRawData;
		private final int m_index;

		private boolean m_isConsumed;
		private String m_decodedName;

		private String m_decodedValue;

		public ParamData(String rawName, String rawValue, int index) {
			this(rawName, null, rawValue, null, true, index);			
		}
		
		public ParamData(String rawName, String decodedName, String rawValue,
				String decodedValue, boolean hasRawData, int index) {
			m_rawName = rawName;
			m_rawValue = rawValue;
			m_hasRawData = hasRawData;
			m_decodedName = decodedName;
			m_decodedValue = decodedValue;
			m_index = index;
		}

		public String getRawName() {
			if (!m_hasRawData) {
				throw new IllegalStateException(
						"Internel error: HTTP param RAW name is not available");
			}

			return m_rawName;
		}

		public String getRawValue() {
			if (!m_hasRawData) {
				throw new IllegalStateException(
						"Internel error: HTTP param RAW value is not available");
			}

			return m_rawValue;
		}

		public boolean hasRawData() {
			return m_hasRawData;
		}

		public String getDecodedName() {
			if (m_decodedName == null) {
				m_decodedName = decode(m_rawName);
			}

			return m_decodedName;
		}

		public String getDecodedValue() {
			if (m_decodedValue == null) {
				m_decodedValue = decode(m_rawValue);
			}

			return m_decodedValue;
		}

		public int getIndex() {
			return m_index;
		}

		public boolean isConsumed() {
			return m_isConsumed;
		}

		public void setConsumed() {
			m_isConsumed = true;
		}

		@Override
		public String toString() {
			return m_rawName + "=" + m_rawValue;
		}
	}
}
