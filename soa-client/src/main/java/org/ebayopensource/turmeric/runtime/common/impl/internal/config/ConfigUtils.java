/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;


public class ConfigUtils {
	private static final String NL = "\n";

	public static PipelineTreeConfig copyPipelineList(PipelineTreeConfig inList) {
		if (inList == null)
			return null;
		PipelineTreeConfig outList = new PipelineTreeConfig();
		List<Object> outInnerList = outList.getHandlerOrChain();
		for (Object inElement : inList.getHandlerOrChain()) {
			if (inElement instanceof HandlerConfig) {
				outInnerList.add(inElement);
			} else if (inElement instanceof ChainConfig) {
				ChainConfig inChain = (ChainConfig) inElement;
				outInnerList.add(copyChain(inChain));
			}
		}
		return outList;
	}

	public static ChainConfig copyChain(ChainConfig inChain) {
		if (inChain == null)
			return null;
		ChainConfig outChain = new ChainConfig();
		outChain.setName(inChain.getName());
		outChain.setPresence(inChain.getPresence());
		List<HandlerConfig> outHandlers = outChain.getHandler(); // empty list
		for (HandlerConfig inHandler : inChain.getHandler()) {
			HandlerConfig outHandler = copyHandler(inHandler);
			outHandlers.add(outHandler);
		}
		return outChain;
	}


	public static HandlerConfig copyHandler(HandlerConfig inHandler) {
		if (inHandler == null)
			return null;
		HandlerConfig outHandler = new HandlerConfig();
		outHandler.setName(inHandler.getName());
		outHandler.setPresence(inHandler.getPresence());
		copyMutableHandlerData(outHandler, inHandler);
		return outHandler;
	}

	public static void copyMutableHandlerData(HandlerConfig dst, HandlerConfig src) {
		dst.setClassName(src.getClassName());
		dst.setContinueOnError(src.isContinueOnError());
		dst.setRunOnError(src.isRunOnError());
		dst.setOptions(copyOptionList(src.getOptions()));
	}

	public static OptionList copyOptionList(OptionList inList) {
		if (inList == null)
			return null;
		OptionList outList = new OptionList();
		putNameValueList(inList.getOption(), outList.getOption());
		return outList;
	}

	public static void putNameValueList(List<NameValue> inList, List<NameValue> outList) {
		for (NameValue nv : inList) {
			NameValue outNv = new NameValue();
			outNv.setName(nv.getName());
			outNv.setValue(nv.getValue());
			outList.add(outNv);
		}
	}

	public static CustomSerializerConfig copyCustomSerializer(CustomSerializerConfig inCS) {
		if (inCS == null) {
			return null;
		}
		CustomSerializerConfig outCS = new CustomSerializerConfig();
		outCS.setDeserializerClassName(inCS.getDeserializerClassName());
		outCS.setJavaTypeName(inCS.getJavaTypeName());
		outCS.setSerializerClassName(inCS.getSerializerClassName());
		outCS.setXmlTypeName(inCS.getXmlTypeName());
		return outCS;
	}

	public static SerializerConfig copySerializerConfig(SerializerConfig inSC) {
		if (inSC == null) {
			return null;
		}
		SerializerConfig outSC = new SerializerConfig();
		outSC.setDeserializerFactoryClassName(inSC.getDeserializerFactoryClassName());
		outSC.setName(inSC.getName());
		outSC.setMimeType(inSC.getMimeType());
		outSC.setSerializerFactoryClassName(inSC.getSerializerFactoryClassName());
		outSC.setOptions(new HashMap<String,String>(inSC.getOptions()));
		return outSC;
	}

	public static TypeConverterConfig copyTypeConverter(TypeConverterConfig inTC) {
		if (inTC == null) {
			return null;
		}
		TypeConverterConfig outTC = new TypeConverterConfig();
		outTC.setBoundJavaTypeName(inTC.getBoundJavaTypeName());
		outTC.setTypeConverterClassName(inTC.getTypeConverterClassName());
		outTC.setValueJavaTypeName(inTC.getValueJavaTypeName());
		outTC.setXmlTypeName(inTC.getXmlTypeName());
		return outTC;
	}

	public static OperationConfig copyOperationConfig(OperationConfig inOC) {
		if (inOC == null) {
			return null;
		}
		OperationConfig outOC = new OperationConfig();
		outOC.setErrorMessage(copyMessageTypeConfig(inOC.getErrorMessage()));
		outOC.setName(inOC.getName());
		outOC.setRequestMessage(copyMessageTypeConfig(inOC.getRequestMessage()));
		outOC.setResponseMessage(copyMessageTypeConfig(inOC.getResponseMessage()));
		transferHeaderConfigList(outOC.getRequestHeader(), inOC.getRequestHeader());
		transferHeaderConfigList(outOC.getResponseHeader(), inOC.getResponseHeader());
		return outOC;
	}

	private static void transferHeaderConfigList(List<MessageHeaderConfig> outList, List<MessageHeaderConfig> inList) {
		for (MessageHeaderConfig inMHC : inList) {
			MessageHeaderConfig outMHC = copyMessageHeaderConfig(inMHC);
			outList.add(outMHC);
		}
	}

	private static MessageHeaderConfig copyMessageHeaderConfig(MessageHeaderConfig inMHC) {
		if (inMHC == null) {
			return null;
		}
		MessageHeaderConfig outMHC = new MessageHeaderConfig();
		outMHC.setJavaTypeName(inMHC.getJavaTypeName());
		outMHC.setXmlElementName(inMHC.getXmlElementName());
		outMHC.setXmlTypeName(inMHC.getXmlTypeName());
		return outMHC;
	}

	public static MessageTypeConfig copyMessageTypeConfig(MessageTypeConfig inMTC) {
		if (inMTC == null) {
			return null;
		}
		MessageTypeConfig outMTC = new MessageTypeConfig();
		outMTC.setJavaTypeName(inMTC.getJavaTypeName());
		outMTC.setXmlElementName(inMTC.getXmlElementName());
		outMTC.setXmlTypeName(inMTC.getXmlTypeName());
		outMTC.setHasAttachment(inMTC.hasAttachment());
		return outMTC;
	}

	public static ProtocolProcessorConfig copyProcessorConfig(ProtocolProcessorConfig inConfig) {
		if (inConfig == null) {
			return null;
		}
		ProtocolProcessorConfig outConfig = new ProtocolProcessorConfig();
		outConfig.setClassName(inConfig.getClassName());
		outConfig.setIndicator(inConfig.getIndicator());
		outConfig.setName(inConfig.getName());
		outConfig.setVersion(inConfig.getVersion());
		return outConfig;
	}

	public static QName copyQName(QName inName) {
		if (inName == null) {
			return null;
		}
		return new QName(inName.getNamespaceURI(), inName.getLocalPart());
	}

	public static TransportOptions copyTransportOptions(TransportOptions holder) {
		if (holder == null) {
			return null;
		}

		return new TransportOptions(holder);
	}

	public static <T> void dumpList(StringBuffer sb, Collection<T> slist) {
		boolean first = true;
		for (T s : slist) {
			if (!first) {
				sb.append(",");
			}
			sb.append(s);
			first = false;
		}
	}

	public static void dumpStringMap(StringBuffer sb, Map<String, String> map, String prefix) {
		List<String> list = new ArrayList<String>(map.keySet());
		Collections.sort(list);
		for (String key : list) {
			String value = map.get(key);
			sb.append(prefix + "key="+key+" value="+value+'\n');
		}

	}

	public static void dumpTransportOptions(StringBuffer sb, TransportOptions options, String indent) {
		if (options.getConnectTimeout() != null) {
			sb.append(indent + "connectTimeout=" + options.getConnectTimeout() + NL);
		}
		if (options.getNumConnectRetries() != null) {
			sb.append(indent + "numConnectRetries=" + options.getNumConnectRetries() + NL);
		}
		if (options.getReceiveTimeout() != null) {
			sb.append(indent + "receiveTimeout=" + options.getReceiveTimeout() + NL);
		}
		if (options.getInvocationTimeout() != null) {
			sb.append(indent + "invocationTimeout=" + options.getInvocationTimeout() + NL);
		}
		if (options.getSkipSerialization() != null) {
			sb.append(indent + "skipSerialization=" + options.getSkipSerialization() + NL);
		}
		if (options.isUseDetachedLocalBinding() != null) {
			sb.append(indent + "useDetachedLocalBinding=" + options.isUseDetachedLocalBinding() + NL);
		}
		dumpStringMap(sb, options.getProperties(), indent + "\t");
	}

	public static void dumpErrorStatusOptions(StringBuffer sb, ErrorStatusOptions options, String indent) {
		if (options.getMetric() != null) {
			sb.append(indent + "metric=" + options.getMetric() + NL);
		}
		if (options.getThreshold() != null) {
			sb.append(indent + "threshold=" + options.getThreshold() + NL);
		}
		if (options.getSampleSize() != -1) {
			sb.append(indent + "sample-size=" + options.getSampleSize() + NL);
		}
	}

	public static void dumpOptionList(StringBuffer sb, OptionList options, String prefix) {
		if (options != null && options.getOption() != null
				&& !options.getOption().isEmpty()) {

			sb.append(prefix+"\toptions:" + NL);
			for (NameValue nv : options.getOption()) {
				sb.append(prefix+"\t\t("+NVToString(nv)+")" + NL);
			}
		}
	}

	public static String NVToString(NameValue nv) {
		return nv.getName()+"="+nv.getValue();
	}

	public static FrameworkHandlerConfig copyFrameworkHandlerConfig(FrameworkHandlerConfig inHandler) {
		if (inHandler == null) {
			return null;
		}
		FrameworkHandlerConfig outHandler = new FrameworkHandlerConfig();
		outHandler.setClassName(inHandler.getClassName());
		outHandler.setOptions(new HashMap<String,String>(inHandler.getOptions()));
		return outHandler;
	}
}
