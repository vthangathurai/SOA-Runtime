/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.DataElementSchemaImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.RequestPatternMatcher;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationParamDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceTypeMappingsImpl;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.PipelineImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.OperationMappings;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ErrorMapperInitContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.VersionCheckHandlerInitContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.UrlMappingsDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.DefaultErrorMapperImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.NullVersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.SimpleInvokerDispatcher;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.SimpleServerResponseDispatcher;
import org.ebayopensource.turmeric.runtime.spf.impl.service.GlobalIdDesc;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.gen.Test1RequestDispatcher;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;


/**
 * @author wdeng
 */
public class TestServiceDesc {

	public static ServerServiceDesc createTestDesc(Class topLevelObjClz,
		SerializerFactory serFactory, DeserializerFactory deserFactory)
		throws Exception
	{
		QName svcName = new QName("http://www.ebay.com/soaframework/test", "JAXBDataBinding");
		ServerServiceId svcId = ServerServiceId.createFallbackServiceId(svcName.getLocalPart());

		ServiceTypeMappings typeMappings = createXmlToJavaMappings(topLevelObjClz);
		Map<String, GlobalIdDesc> globalIdMap = new HashMap<String, GlobalIdDesc>();

		List<LoggingHandler> loggingHandlers = new ArrayList<LoggingHandler>();

		VersionCheckHandler versionCheckHandler = new NullVersionCheckHandler();
		VersionCheckHandlerInitContextImpl versInitCtx =
			new VersionCheckHandlerInitContextImpl(svcId, "1.0", null);
		versionCheckHandler.init(versInitCtx);
		versInitCtx.kill();

		DefaultErrorMapperImpl errorMapper = new DefaultErrorMapperImpl();
		ErrorMapperInitContextImpl errInitCtx = new ErrorMapperInitContextImpl(svcId);
		errorMapper.init(errInitCtx);
		errInitCtx.kill();

		return new ServerServiceDesc(
				svcId,
				svcName,
				new ServiceConfigHolder(svcName.getLocalPart()),
				new PipelineImpl(),
				new PipelineImpl(),
				new SimpleInvokerDispatcher(new Test1RequestDispatcher()),
				new SimpleServerResponseDispatcher(true),
				createAllOperations(svcId, topLevelObjClz),
				new HashMap<String,ProtocolProcessorDesc>(),
				new HashMap<String,DataBindingDesc>(),
				typeMappings,
				Thread.currentThread().getContextClassLoader(),
				loggingHandlers,
				Object.class,
				new RequestPatternMatcher<ServiceOperationDesc>(false),
				new RequestPatternMatcher<ProtocolProcessorDesc>(false),
				new RequestPatternMatcher<DataBindingDesc>(false),
				new RequestPatternMatcher<DataBindingDesc>(false),
				"",
				errorMapper,
				null,
				globalIdMap,
				versionCheckHandler,
				null,
				UrlMappingsDesc.EMPTY_MAPPINGS,
				new OperationMappings(),
				HeaderMappingsDesc.EMPTY_MAPPINGS,
				HeaderMappingsDesc.EMPTY_MAPPINGS,
				Collections.unmodifiableMap(new HashMap<String, Map<String, String>>()),
				createDefaultDataBinding(),
				createDefaultDataBinding(),
				Arrays.asList(new String[] {"COMMON", "INTERMEDIATE", "BUSINESS"}), null
				);
	}

	private static final String NS = "http://www.ebay.com/test/soaframework/sample/service/message";

	private static ServiceTypeMappings createXmlToJavaMappings(Class topLevelObjClz) {
		HashMap<String,String> pkgToNs = new HashMap<String,String>();
		pkgToNs.put(topLevelObjClz.getPackage().getName(), NS);
		pkgToNs.put(ErrorMessage.class.getPackage().getName(), NS);

		ServiceTypeMappings typeMappings = new ServiceTypeMappingsImpl(pkgToNs, null);
		return typeMappings;
	}

	public static ServiceOperationParamDesc createParamDesc(Class objClz) throws ServiceCreationException {
		String clzName = objClz.getSimpleName();
		List<Class> rootJavaTypes = new ArrayList<Class>();
		List<DataElementSchema> rootElements = new ArrayList<DataElementSchema>();
		Map<QName,Class> xmlToJavaMappings = new HashMap<QName,Class>();

		rootElements.add(new DataElementSchemaImpl(new QName(NS, clzName), 1));
		rootJavaTypes.add(objClz);
		xmlToJavaMappings.put(new QName(NS, clzName), objClz);

		return new ServiceOperationParamDescImpl(rootJavaTypes, rootElements, xmlToJavaMappings, false);
	}

	public static ServiceOperationParamDesc createErrorParamDesc() throws ServiceCreationException {
		List<Class> rootJavaTypes = new ArrayList<Class>();
		List<DataElementSchema> rootElements = new ArrayList<DataElementSchema>();
		Map<QName,Class> xmlToJavaMappings = new HashMap<QName,Class>();

		rootElements.add(new DataElementSchemaImpl(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME, 1));
		rootJavaTypes.add(ErrorMessage.class);
		xmlToJavaMappings.put(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME, ErrorMessage.class);

		return new ServiceOperationParamDescImpl(rootJavaTypes, rootElements, xmlToJavaMappings, false);
	}

	private static Map<String,ServiceOperationDesc> createAllOperations(ServerServiceId svcId, Class topLevelObjClz) throws ServiceCreationException {
		ServiceOperationParamDesc paramDesc = createParamDesc(topLevelObjClz);
		ServiceOperationParamDesc errorParamDesc = createErrorParamDesc();

		ServiceOperationDesc op = new ServiceOperationDescImpl(svcId, "addMessage",
			paramDesc, paramDesc, errorParamDesc, null, null, null, true, true);

		Map<String,ServiceOperationDesc> ops = new HashMap<String,ServiceOperationDesc>();
		ops.put(op.getName(), op);
		return ops;
	}

	private static DataBindingDesc createDefaultDataBinding() {
		return new DataBindingDesc(BindingConstants.PAYLOAD_XML,
			SOAConstants.MIME_XML,
			new JAXBXMLSerializerFactory(),
			new JAXBXMLDeserializerFactory(),
			null, null, null, null);

			}
}
