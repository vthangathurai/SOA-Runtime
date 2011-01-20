/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;

/**
 * Defines constants used for the operation of the framework and available for
 * programmatic use by clients and services.
 * 
 * @author ichernyshev
 */
public final class SOAConstants {

	/**
	 * Defines the namespace for message schema types available to all services,
	 * such as ErrorMessage, BaseAttachmentType, etc.
	 */
	public static final String SOA_TYPES_NAMESPACE = BindingConstants.SOA_TYPES_NAMESPACE;
	/**
	 * Defines the XML qualified element name of the ErrorMessage type.
	 */
	public static final QName ERROR_MESSAGE_ELEMENT_NAME = new QName(
			BindingConstants.SOA_TYPES_NAMESPACE, "errorMessage");

	/**
	 * Defines the XML qualified element name of the ErrorMessage type.
	 */
	public static final QName COMMON_ERROR_MESSAGE_ELEMENT_NAME = new QName(
			BindingConstants.SOA_TYPES_NAMESPACE, "errorMessage");

	/**
	 * Defines the namespace for the SOA Framework configuration XML files.
	 */
	public final static String SOA_CONFIG_NAMESPACE = "http://www.ebayopensource.org/turmeric/common/config";

	/**
	 * Defines the default namespace for service schema; this is also the
	 * default namespace for service qualified names.
	 */
	public final static String DEFAULT_SERVICE_NAMESPACE = "http://www.ebayopensource.org/turmeric/common/v1/services";

	/**
	 * Defines the naming prefix for message context system properties.
	 */
	public static final String CTX_PROP_PREFIX = "org.ebayopensource.turmeric.runtime.";

	/**
	 * Defines the web.xml parameter name used to define the default service
	 * "admin name" for a service endpoint. The associated configuration will be
	 * initialized and used as the only supported service QName for this
	 * endpoint.
	 * 
	 * @deprecated 2.4
	 */
	public static final String SERVLET_PARAM_SERVICE_NAME = "SOA_SERVICE_NAME";

	/**
	 * Defines the web.xml parameter name used after 2.4 release to define the
	 * default service "admin name" for a service endpoint. The associated
	 * configuration will be initialized and used as the only supported service
	 * QName for this endpoint.
	 */
	public static final String SERVLET_PARAM_ADMIN_NAME = "SOA_ADMIN_NAME";
	/**
	 * Defines the optional SPF servlet parameter to specify the logger resource
	 * to use for uKernel logging.
	 */
	public static final String SERVLET_PARAM_LOGGER_RESOURCE_NAME = "SOA_PARAM_LOGGER_RESOURCE_NAME";

	/**
	 * Defines the optional SPF servlet parameter to specify if MicroKernel
	 * logger initialization should occur (or not).
	 * <p>
	 * Default: true
	 */
	public static final String SERVLET_PARAM_LOGGER_INIT = "SOA_PARAM_LOGGER_INIT";

	/**
	 * The servlet parameter url matching expression.
	 */
	public static final String SERVLET_PARAM_URL_MATCH_EXPRESSION = "SOA_SERVICE_URL_MATCH_EXPRESSION";

	/**
	 * Servlet parameter to inhibit the execution of the initializers for that
	 * particular instance of SPFServlet.
	 */
	public static final String NO_INITIALIZER_PARAM_NAME = "no-initializer";

	/**
	 * MIME type sent in Content-Type representations for XML.
	 */
	public static final String MIME_XML = "text/xml"; // http://www.rfc-editor.org/rfc/rfc3023.txt

	/**
	 * MIME type sent in Content-Type representations for name-value pair.
	 */
	public static final String MIME_NV = "text/plain";

	/**
	 * MIME type sent in Content-Type representations for JSON.
	 */
	public static final String MIME_JSON = "application/json"; // http://www.ietf.org/rfc/rfc4627.txt

	/**
	 * MIME type sent in Content-Type representations for ASN.1 Fast Infoset.
	 */
	public static final String MIME_FAST_INFOSET = "application/fastinfoset"; // http://www.iana.org/assignments/media-types/application/fastinfoset

	/**
	 * SOA name for built-in HTTP 1.0 transport. Refers to both the transport
	 * name, and the TransportOptions property "HTTP_VERSION".
	 */
	public static final String TRANSPORT_HTTP_10 = "HTTP10";

	/**
	 * SOA name for built-in HTTP 1.1 transport. Refers to both the transport
	 * name, and the TransportOptions property "HTTP_VERSION".
	 */
	public static final String TRANSPORT_HTTP_11 = "HTTP11";

	/**
	 * SOA name for built-in local transport.
	 */
	public static final String TRANSPORT_LOCAL = "LOCAL";

	/**
	 * SOA name for SOAP 1.1 protocol processor.
	 */
	public static final String MSG_PROTOCOL_SOAP_11 = "SOAP11";

	/**
	 * SOA name for SOAP 1.2 protocol processor.
	 */
	public static final String MSG_PROTOCOL_SOAP_12 = "SOAP12";

	/**
	 * SOA name for null protocol processor (NullProtocolProcessor class).
	 */
	public static final String MSG_PROTOCOL_NONE = "NONE";

	/**
	 * Transport option property name for enabling chunked encoding. By default,
	 * no chunked encoding is used.
	 */
	public static final String CHUNKED_ENCODING = "CHUNKED_ENCODING";

	/**
	 * Transport option property name for HTTP version. Use one of the values
	 * TRANSPORT_HTTP_10 or TRANSPORT_HTTP_11.
	 */
	public static final String HTTP_VERSION = "HTTP_VERSION";

	/**
	 * Transport option property name for enabling content-encoding=gzip on
	 * response. By default, no zipped encoding is used.
	 */
	public static final String GZIP_ENCODING = "GZIP_ENCODING";

	/**
	 * Header name of HTTP Content-Type header.
	 */
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

	/**
	 * Header name of HTTP Accept-Encoding header.
	 */
	public static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	/**
	 * Header name of proxy X-FORWARDED-FOR header.
	 */
	public static final String HTTP_HEADER_FORWARDED_FOR = "X-FORWARDED-FOR";

	/**
	 * Header name of SOAPAction required for SOAP11.
	 */
	public static final String HTTP_HEADER_SOAP11_SOAPACTION = "SOAPAction";

	/**
	 * Header name of CLIENT-POOL-NAME header.
	 */
	public static final String HTTP_HEADER_CLIENT_POOL_NAME = "CLIENT-POOL-NAME";

	/**
	 * Name for default globalization global ID.
	 */
	public static final String DEFAULT_GLOBAL_ID = "DEFAULT";

	/**
	 * Default client name used if client is instantiated (using
	 * <code>ServiceFactory.create()</code>) with null client name.
	 */
	public static final String DEFAULT_CLIENT_NAME = "default";

	/**
	 * Name for default Server CAL Transaction type.
	 */
	public static final String DEFAULT_SERVER_CAL_TRANS_TYPE = "SOA_SERVER";

	/**
	 * Name for default Client CAL Transaction type.
	 */
	public static final String DEFAULT_CLIENT_CAL_TRANS_TYPE = "SOA_CLIENT";

	/**
	 * Name for default USECASE CAL Transaction name.
	 */
	public static final String DEFAULT_USECASE_CAL_TRANS_NAME = "Usecase";

	/**
	 * Prefix for the APP_NAME, when mapped to UseCase.
	 */
	public static final String APPNAME_PREFIX = "a:";

	/**
	 * Name for the default use case to be used for HTTP header if no use case
	 * can be identified.
	 */
	public static final String DEFAULT_USE_CASE = "Missing";
	/**
	 * Name for the getServiceVersion standard service operation.
	 */

	public static final String OP_GET_VERSION = "getVersion";
	/**
	 * Name for the isServiceVersionSupported standard service operation.
	 */
	public static final String OP_IS_SERVICE_VERSION_SUPPORTED = "isServiceVersionSupported";

	/**
	 * Name for the getCachePolicy standard service operation.
	 */

	public static final String OP_GET_CACHE_POLICY = "getCachePolicy";

	/**
	 * Context property name for client source IP, kept on the server side (e.g.
	 * for logging).
	 */
	public static final String CTX_PROP_TRANSPORT_CLIENT_SOURCE_IP = "transport.clientsourceip";

	/**
	 * Context property name for forwarded-for information, kept on the server
	 * side.
	 */
	public static final String CTX_PROP_TRANSPORT_FORWARDED_FOR = "transport.forwardedfor";

	/**
	 * Context property name for GUID created information.
	 */
	public static final String CTX_PROP_GUID_CREATED = "context.guid.created";

	/**
	 * Context property name for client pool name, kept on the server side.
	 */
	public static final String CTX_PROP_CLIENT_POOL_NAME = "client.pool.name";

	/**
	 * Name for turmeric config bean group.
	 */
	public static final String CONFIG_BEAN_GROUP = "org.ebayopensource.turmeric";
	/**
	 * Name for turmeric server side config bean group.
	 */
	public static final String CONFIG_BEAN_GROUP_SERVER = "org.ebayopensource.turmeric.server";
	/**
	 * Name for turmeric client side config bean group.
	 */
	public static final String CONFIG_BEAN_PREFIX_CLIENT = "org.ebayopensource.turmeric.client.";
	/**
	 * 
	 */
	public static final String CONFIG_BEAN_PREFIX_SERVER = "org.ebayopensource.turmeric.server.";

	/**
	 * Name of the query parameter used by the client to obtain a service's WSDL
	 * ("?wsdl" pseudo-operation).
	 */
	public static final String PSEUDO_OP_WSDL = "WSDL";

	/**
	 * Name of the query parameter used by the client to obtain a service's
	 * runtime status ("?status" pseudo-operation).
	 */
	public static final String PSEUDO_OP_STATUS = "STATUS";

	/**
	 * String value for specifying wildcard for security operation configuration
	 * in (SecurityPolicy.xml).
	 */
	public static final String SECURITY_OPERATION_WILDCARD = "*";

	/**
	 * Cache policy schema path.
	 */
	public static final String CACHE_POLICY_SCHEMA = "META-INF/soa/schema/server/CachePolicy.xsd";
	
	/*
	 * TypeMappings node names.
	 *****************************/
	/**
	 * XML node name for operation list in TypeMapping.xml.
	 */
	public static final String XML_NODE_OPERATIONLIST = "operation-list";
	/**
	 * XML node name for operation in TypeMapping.xml.
	 */
	public static final String XML_NODE_OPERATION = "operation";
	/**
	 * XML node name for package map in TypeMapping.xml.
	 */
	public static final String XML_NODE_PACKAGEMAP = "package-map";
	/**
	 * XML node name for package in TypeMapping.xml.
	 */
	public static final String XML_NODE_PACKAGE = "package";
	/**
	 * XML node name for request message in TypeMapping.xml.
	 */
	public static final String XML_NODE_REQUEST_MESSAGE = "request-message";
	/**
	 * XML node name for response message in TypeMapping.xml.
	 */
	public static final String XML_NODE_RESPONSE_MESSAGE = "response-message";
	/**
	 * XML node name for error message in TypeMapping.xml.
	 */
	public static final String XML_NODE_ERROR_MESSAGE = "error-message";
	/**
	 * XML node name for request header in TypeMapping.xml.
	 */
	public static final String XML_NODE_REQUEST_HEADER = "request-header";
	/**
	 * XML node name for response header in TypeMapping.xml.
	 */
	public static final String XML_NODE_RESPONSE_HEADER = "response-header";
	/**
	 * XML node name for java type name in TypeMapping.xml.
	 */
	public static final String XML_NODE_JAVA_TYPE_NAME = "java-type-name";
	/**
	 * XML node name for xml type name. in TypeMapping.xml.
	 */
	public static final String XML_NODE_XML_TYPE_NAME = "xml-type-name";
	/**
	 * XML node name for xml element name in TypeMapping.xml.
	 */
	public static final String XML_NODE_XML_ELEMENT_NAME = "xml-element-name";
	/**
	 * XML node name for has attachment field in TypeMapping.xml.
	 */
	public static final String XML_NODE_HAS_ATTACHMENT = "has-attachment";

	private SOAConstants() {
		// no instances
	}
}
