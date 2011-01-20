/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.service.ComponentStatusDataProvider;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;

import com.ebay.kernel.component.IComponentStatusXml;
import com.ebay.kernel.markdown.MarkdownStateSnapshot;
import com.ebay.kernel.util.StringUtils;
import com.ebay.kernel.util.xml.IXmlStreamWriter;
import com.ebay.kernel.util.xml.XmlStreamUtil;

/**
 * @author ichernyshev
 */
public abstract class BaseServiceBrowserCompStatus implements IComponentStatusXml {

	private final String m_name;
	private final String m_prefix;
	private final boolean m_isClient;

	public BaseServiceBrowserCompStatus(String name, boolean isClient, String prefix) {
		m_name = name;
		m_isClient = isClient;
		m_prefix = prefix;
	}

	public final String getName() {
		return m_name;
	}

	public final String getAlias() {
		return null;
	}

	public final String getStatus() {
		return "created";
	}

	public final List getProperties() {
		return Collections.EMPTY_LIST;
	}

	public final void renderXml(IXmlStreamWriter xmlWriter, Map<String,String> props) {
		xmlWriter.writeStartElement(m_prefix + "ServiceBrowser");
		try {
			BaseServiceDescFactory<ServiceDesc> factory;
			if (m_isClient) {
				@SuppressWarnings("unchecked")
				BaseServiceDescFactory<ServiceDesc> factory2 = BaseServiceDescFactory.getClientInstance();
				factory = factory2;
			} else {
				@SuppressWarnings("unchecked")
				BaseServiceDescFactory<ServiceDesc> factory2 = BaseServiceDescFactory.getServerInstance();
				factory = factory2;
			}

			String failedNamesViewStr = props.get("failed-only");
			boolean isFailedNamesView = Boolean.parseBoolean(failedNamesViewStr);
			if (isFailedNamesView) {
				xmlWriter.writeAttribute("failed-names-view", "true");
				renderFailedServices(factory, xmlWriter, props, true);
				return;
			}

			Collection<ServiceDesc> descs = factory.getKnownServiceDescs();

			String detailStr = props.get("detail");
			if (detailStr != null) {
				xmlWriter.writeAttribute("single-service", "true");

				for (ServiceDesc desc: descs) {
					String name = getDescName(desc.getServiceId());
					if (!name.equals(detailStr)) {
						continue;
					}

					renderServiceDesc(desc, true, xmlWriter, props);
				}

				return;
			}

			String fullViewStr = props.get("fullview");
			boolean isFullView = Boolean.parseBoolean(fullViewStr);

			Set<String> filter = null;
			String filterStr = props.get("filter");
			if (filterStr != null) {
				List<String> names = StringUtils.splitStr(filterStr, ',', true);
				if (!names.isEmpty()) {
					filter = new HashSet<String>(names);

					if (fullViewStr == null) {
						isFullView = true;
					}
				}
			}

			for (ServiceDesc desc: descs) {
				if (filter != null && !matchFilter(desc, filter)) {
					continue;
				}

				renderServiceDesc(desc, isFullView, xmlWriter, props);
			}

			// TODO: add failed services HTML

			renderFailedServices(factory, xmlWriter, props, false);
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private String getDescName(ServiceId id) {
		String adminName = id.getAdminName();
		String subname = id.getServiceSubname();

		StringBuilder sb = new StringBuilder();
		sb.append(adminName);
		if (subname != null) {
			sb.append('.');
			sb.append(subname);
		}

		return sb.toString();
	}

	private boolean matchFilter(ServiceDesc desc, Set<String> filter) {
		if (filter.contains(desc.getAdminName())) {
			return true;
		}

		String name = getDescName(desc.getServiceId());
		return filter.contains(name);
	}

	private void renderFailedServices(BaseServiceDescFactory<ServiceDesc> factory,
		IXmlStreamWriter xmlWriter, Map<String,String> props, boolean forceDisplay)
	{
		Map<ServiceId,Throwable> ids = factory.getFailedIds();
		if (ids.isEmpty() && !forceDisplay) {
			return;
		}

		xmlWriter.writeStartElement("FailedServices");
		try {
			for (Map.Entry<ServiceId,Throwable> e: ids.entrySet()) {
				xmlWriter.writeStartElement("FailedService");
				try {
					ServiceId id = e.getKey();
					Throwable error = e.getValue();

					String name = getDescName(id);
					xmlWriter.writeAttribute("name", name);
					xmlWriter.writeAttribute("admin-name", id.getAdminName());

					XmlStreamUtil.safeWriteChildCData(xmlWriter, "error", error.toString());
				} finally {
					xmlWriter.writeEndElement();
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderServiceDesc(ServiceDesc desc, boolean isFullView,
		IXmlStreamWriter xmlWriter, Map<String,String> props)
	{
		xmlWriter.writeStartElement(m_prefix + "ServiceDesc");
		try {
			// write all relevant attributes
			String name = getDescName(desc.getServiceId());
			xmlWriter.writeAttribute("name", name);
			xmlWriter.writeAttribute("admin-name", desc.getAdminName());

			MarkdownStateSnapshot markdownState = getMarkdownStatus(desc, null);
			boolean isDown = markdownState.isAlert();
			xmlWriter.writeAttribute("is-down", String.valueOf(isDown));

			boolean hasOpMarkdown = false;
			Map<String,MarkdownStateSnapshot> opMarkdownStates = new HashMap<String,MarkdownStateSnapshot>();
			for (ServiceOperationDesc op: desc.getAllOperations()) {
				String opName = op.getName();
				MarkdownStateSnapshot opMarkdownState = getMarkdownStatus(desc, opName);
				opMarkdownStates.put(opName, opMarkdownState);

				hasOpMarkdown |= opMarkdownState.isAlert();
			}

			if (!isDown && hasOpMarkdown) {
				xmlWriter.writeAttribute("is-partial-down", String.valueOf(hasOpMarkdown));
			}

			renderServiceDescAttrs(desc, isFullView, xmlWriter, props);

			// write all relevant elements			
			XmlStreamUtil.safeWriteChildCData(xmlWriter, "qname", desc.getCanonicalServiceName());
			
			if (isDown) {
				renderMarkdownElements(desc, markdownState, xmlWriter);
			}

			Class intfClass = desc.getServiceInterfaceClass();
			if (intfClass != null) {
				XmlStreamUtil.safeWriteChildCData(xmlWriter, "interface-class", intfClass.getName());
			}

			if (isFullView) {
				XmlStreamUtil.safeWriteChildCData(xmlWriter, "request-pipeline-class",
					desc.getRequestPipeline().getClass().getName());
				XmlStreamUtil.safeWriteChildCData(xmlWriter, "response-pipeline-class",
					desc.getResponsePipeline().getClass().getName());
				XmlStreamUtil.safeWriteChildCData(xmlWriter, "request-dispatcher-class",
					desc.getRequestDispatcher().getClass().getName());
				XmlStreamUtil.safeWriteChildCData(xmlWriter, "response-dispatcher-class",
					desc.getResponseDispatcher().getClass().getName());
			}

			if (isFullView) {
				renderOperations(desc, opMarkdownStates, xmlWriter, props);
				renderProtocolProcessors(desc, xmlWriter, props);
				renderBindings(desc, xmlWriter, props);
				renderLoggingHandlers(desc, xmlWriter, props);
			}

			renderServiceDescElements(desc, isFullView, xmlWriter, props);
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderMarkdownElements(ServiceDesc desc,
		MarkdownStateSnapshot markdownState, IXmlStreamWriter xmlWriter)
	{
		XmlStreamUtil.safeWriteChildCData(xmlWriter, "markdown-status", markdownState.getStatus());
		XmlStreamUtil.safeWriteChildCData(xmlWriter, "markdown-reason", markdownState.getReason());
	}

	private void renderOperations(ServiceDesc desc, Map<String,MarkdownStateSnapshot> opMarkdownStates,
		IXmlStreamWriter xmlWriter, Map<String,String> props)
	{
		xmlWriter.writeStartElement("ServiceOperations");
		try {
			Collection<ServiceOperationDesc> ops = desc.getAllOperations();
			ServiceOperationDesc[] ops2 = ops.toArray(new ServiceOperationDesc[ops.size()]);
			// TODO: sort
			for (int i=0; i<ops2.length; i++) {
				ServiceOperationDesc op = ops2[i];
				MarkdownStateSnapshot markdownState = opMarkdownStates.get(op.getName());

				xmlWriter.writeStartElement("ServiceOperation");
				try {
					xmlWriter.writeAttribute("name", op.getName());
					xmlWriter.writeAttribute("supported", String.valueOf(op.isSupported()));

					if (markdownState != null) {
						boolean isDown = markdownState.isAlert();
						xmlWriter.writeAttribute("is-down", String.valueOf(isDown));
					}

					// TODO: add types
					// TODO: add properties
				} finally {
					xmlWriter.writeEndElement();
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderProtocolProcessors(ServiceDesc desc, IXmlStreamWriter xmlWriter,
		Map<String,String> props)
	{
		xmlWriter.writeStartElement("ServiceProtocolProcessors");
		try {
			Collection<ProtocolProcessorDesc> procs = desc.getAllProtocolProcessors();
			ProtocolProcessorDesc[] procs2 = procs.toArray(new ProtocolProcessorDesc[procs.size()]);
			// TODO: sort
			for (int i=0; i<procs2.length; i++) {
				ProtocolProcessorDesc proc = procs2[i];
				xmlWriter.writeStartElement("ServiceProtocolProcessor");
				try {
					xmlWriter.writeAttribute("name", proc.getName());

					XmlStreamUtil.safeWriteChildCData(xmlWriter, "class",
						proc.getProcessor().getClass().getName());

					// TODO: add supported payloads

					renderCustomStatus(proc, xmlWriter, props);
				} finally {
					xmlWriter.writeEndElement();
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderBindings(ServiceDesc desc, IXmlStreamWriter xmlWriter,
		Map<String,String> props)
	{
		xmlWriter.writeStartElement("ServiceDataBindings");
		try {
			Collection<DataBindingDesc> bindings = desc.getAllDataBindings();
			DataBindingDesc[] bindings2 = bindings.toArray(new DataBindingDesc[bindings.size()]);

			// TODO: sort

			for (int i=0; i<bindings2.length; i++) {
				DataBindingDesc binding = bindings2[i];
				xmlWriter.writeStartElement("ServiceDataBinding");
				try {
					xmlWriter.writeAttribute("name", binding.getName());

					XmlStreamUtil.safeWriteChildCData(xmlWriter, "payload",
						binding.getPayloadType());
					XmlStreamUtil.safeWriteChildCData(xmlWriter, "mime-type",
						binding.getMimeType());
					XmlStreamUtil.safeWriteChildCData(xmlWriter, "serializer",
						binding.getSerializerFactory().getClass().getName());
					XmlStreamUtil.safeWriteChildCData(xmlWriter, "deserializer",
						binding.getDeserializerFactory().getClass().getName());

					// TODO: add custom serializers
					// TODO: add type converters
					// TODO: add type list
				} finally {
					xmlWriter.writeEndElement();
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderLoggingHandlers(ServiceDesc desc, IXmlStreamWriter xmlWriter,
		Map<String,String> props)
	{
		xmlWriter.writeStartElement("ServiceLoggingHandlers");
		try {
			Collection<LoggingHandler> handlers = desc.getLoggingHandlers();
			LoggingHandler[] handlers2 = handlers.toArray(new LoggingHandler[handlers.size()]);
			for (int i=0; i<handlers2.length; i++) {
				LoggingHandler handler = handlers2[i];
				xmlWriter.writeStartElement("ServiceLoggingHandler");
				try {
					XmlStreamUtil.safeWriteChildCData(xmlWriter, "class",
						handler.getClass().getName());

					renderCustomStatus(handler, xmlWriter, props);
				} finally {
					xmlWriter.writeEndElement();
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	protected final void writeG11Options(G11nOptions g11nOptions, IXmlStreamWriter xmlWriter, String name) {
		Charset charset = g11nOptions.getCharset();
		String globalId = g11nOptions.getGlobalId();
		List<String> locales = g11nOptions.getLocales();

		xmlWriter.writeStartElement(name);
		try {
			XmlStreamUtil.safeWriteChildCData(xmlWriter, "charset", charset.toString());
			if (globalId != null) {
				XmlStreamUtil.safeWriteChildCData(xmlWriter, "globalid", globalId);
			}
			if (locales != null) {
				StringBuilder sb = new StringBuilder();
				for (String locale: locales) {
					if (sb.length() > 0) {
						sb.append(',');
					}
					sb.append(locale);
				}

				if (sb.length() > 0) {
					XmlStreamUtil.safeWriteChildCData(xmlWriter, "locales", sb.toString());
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	protected final void renderCustomStatus(Object obj, IXmlStreamWriter xmlWriter,
		Map<String,String> props)
	{
		if (!(obj instanceof ComponentStatusDataProvider)) {
			return;
		}

		ComponentStatusDataProvider provider = (ComponentStatusDataProvider)obj;

		xmlWriter.writeStartElement("data");
		try {
			provider.writeStatusXml(xmlWriter, props);
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	protected abstract MarkdownStateSnapshot getMarkdownStatus(ServiceDesc desc, String operation);

	protected abstract void renderServiceDescAttrs(ServiceDesc desc, boolean isFullView,
		IXmlStreamWriter xmlWriter, Map<String,String> props);

	protected abstract void renderServiceDescElements(ServiceDesc desc, boolean isFullView,
		IXmlStreamWriter xmlWriter, Map<String,String> props);
}
