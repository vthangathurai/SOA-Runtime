/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

// Copy non-null source data into the destination holder.
// The very first time the ServiceInvokerOptions is initialized,
public class MessageProcessorConfigMapper {
	public static void map(String filename,
			NodeList customSerializers,
			String errorClass,
			String errorDataProviderClass,
			Element pipelineConfig,
			NodeList protocolProcessors,
			NodeList transports,
			Element dataBindingConfig,
			MessageProcessorConfigHolder dst) throws ServiceCreationException {

		dst.setConfigFilename(filename);
		if (errorClass != null && errorClass.length() > 0) {
			dst.setErrorMappingClass(errorClass);
		}
		if (errorDataProviderClass != null && errorDataProviderClass.length() > 0) {
			dst.setErrorDataProviderClass(errorDataProviderClass);
		}
		mapCustomSerializers(filename, customSerializers, dst);
		mapPipelineConfig(filename, pipelineConfig, dst);
		mapProtocolProcessor(filename, protocolProcessors, dst);
		mapTransports(filename, transports, dst);
		mapDataBindingConfig(filename, dataBindingConfig, dst);
	}

	// Walk the outer list of all bindings (each of which has a list of
	// custom serializer/deserializer entries).
	// For each such binding, get the map associated with its binding name.
	// Then, take the serializer list for that particular binding, and
	// add item by item into the map.
	// The result is an outer hashmap keyed by binding names, each entry giving
	// an inner hashmap keyed by XML type name.
	// The group data is stored into this map, and the instance data
	// is stored, effectively allowing the instance data to add to/replace
	// any entries based on binding name and XML type name.
	private static void mapCustomSerializers(String filename, NodeList customSerializers, MessageProcessorConfigHolder dstConfig) throws ServiceCreationException {
		if (customSerializers == null) {
			return;
		}
		for (int i = 0; i < customSerializers.getLength(); i++) {
			Element customSerializer = (Element) customSerializers.item(i);
			mapOneCustomSerializer(filename, customSerializer, dstConfig);
		}
	}

	private static void mapOneCustomSerializer(String filename, Element customSerializer, MessageProcessorConfigHolder dstConfig) throws ServiceCreationException {
		String bindingName = customSerializer.getAttribute("binding");
		if (bindingName == null) {
			DomParseUtils.throwError(filename, "No binding name specified for custom-serializers entry");
		}
		Map<String, CustomSerializerConfig> serializerMap = dstConfig.getCustomSerializerMap(bindingName);
		Map<String, TypeConverterConfig> typeConverterMap = dstConfig.getTypeConverterMap(bindingName);

		NodeList customSerializerDeserializers = DomParseUtils.getImmediateChildrenByTagName(customSerializer, "custom-serializer-deserializer");
		for (int i = 0; i < customSerializerDeserializers.getLength(); i++) {
			Element customSerializerDeserializer = (Element) customSerializerDeserializers.item(i);
			CustomSerializerConfig config = new CustomSerializerConfig();
			// No merging going on here, straight create with null or with data
			String javaTypeName = DomParseUtils.getElementText(filename, customSerializerDeserializer, "java-type-name");
			config.setJavaTypeName(javaTypeName);
			String serClassName = DomParseUtils.getElementText(filename, customSerializerDeserializer, "serializer-class-name");
			config.setSerializerClassName(serClassName);
			String deserClassName = DomParseUtils.getElementText(filename, customSerializerDeserializer, "deserializer-class-name");
			config.setDeserializerClassName(deserClassName);
			String xmlTypeName = DomParseUtils.getElementText(filename, customSerializerDeserializer, "xml-type-name");
			config.setXmlTypeName(xmlTypeName);
			serializerMap.put(javaTypeName, config);
		}
		NodeList typeConverters = DomParseUtils.getImmediateChildrenByTagName(customSerializer, "type-converter");
		for (int i = 0; i < typeConverters.getLength(); i++) {
			Element typeConverter = (Element) typeConverters.item(i);
			TypeConverterConfig config = new TypeConverterConfig();
			String boundJavaTypeName = DomParseUtils.getElementText(filename, typeConverter, "bound-java-type-name");
			config.setBoundJavaTypeName(boundJavaTypeName);
			String valueJavaTypeName = DomParseUtils.getElementText(filename, typeConverter, "value-java-type-name");
			config.setValueJavaTypeName(valueJavaTypeName);
			String typeConverterClassName = DomParseUtils.getElementText(filename, typeConverter, "type-converter-class-name");
			config.setTypeConverterClassName(typeConverterClassName);
			String xmlTypeName = DomParseUtils.getElementText(filename, typeConverter, "xml-type-name");
			config.setXmlTypeName(xmlTypeName);
			typeConverterMap.put(boundJavaTypeName, config);
		}
	}

	private static void mapPipelineConfig(String filename, Element pipelineConfig, MessageProcessorConfigHolder mpConfig) throws ServiceCreationException {
		if (pipelineConfig == null) {
			return;
		}
		String value = getPipelineClassName(filename, pipelineConfig, "request-pipeline");
		if (value != null) {
			mpConfig.setRequestPipelineClassName(value);
		}
		value = getPipelineClassName(filename, pipelineConfig, "response-pipeline");
		if (value != null) {
			mpConfig.setResponsePipelineClassName(value);
		}
		value = getPipelineClassName(filename, pipelineConfig, "request-dispatcher");
		if (value != null) {
			mpConfig.setRequestDispatcherClassName(value);
		}
		value = getPipelineClassName(filename, pipelineConfig, "response-dispatcher");
		if (value != null) {
			mpConfig.setResponseDispatcherClassName(value);
		}
		NodeList loggingHandlers = DomParseUtils.getImmediateChildrenByTagName(pipelineConfig, "logging-handler");
		if (loggingHandlers != null && loggingHandlers.getLength() > 0) {
			List<FrameworkHandlerConfig> outHandlers = mpConfig.getLoggingHandlers();
			for (int i = 0; i < loggingHandlers.getLength(); i++) {
				Element loggingHandler = (Element) loggingHandlers.item(i);
				String classname = DomParseUtils.getElementText(filename, loggingHandler, "class-name");
				OptionList options = DomParseUtils.getOptionList(filename, loggingHandler, "options");
				FrameworkHandlerConfig outHandler = new FrameworkHandlerConfig();
				outHandler.setClassName(classname);
				HashMap<String,String> outOptions = outHandler.getOptions();
				DomParseUtils.storeNVListToHashMap(filename, options, outOptions);
				outHandlers.add(outHandler);
			}
		}
		PipelineTreeConfig requestHandlerTree = parsePipelineTree(filename, pipelineConfig, "request-handlers");
		PipelineTreeConfig mergedTree;
		if (requestHandlerTree != null) {
			mergedTree = combinePipelines(mpConfig.getConfigFilename(), requestHandlerTree, mpConfig.getRequestPipelineTree(), "request-handlers");
			validateTree(filename, "request-handlers", mergedTree);
			mpConfig.setRequestPipelineTree(mergedTree);
		}
		PipelineTreeConfig responseHandlerTree = parsePipelineTree(filename, pipelineConfig, "response-handlers");
		if (responseHandlerTree != null) {
			mergedTree = combinePipelines(mpConfig.getConfigFilename(), responseHandlerTree, mpConfig.getResponsePipelineTree(), "response-handlers");
			validateTree(filename, "response-handlers", mergedTree);
			mpConfig.setResponsePipelineTree(mergedTree);
		}
	}

	private static void validateTree(String filename, String handlerSection, PipelineTreeConfig pipelineTree) throws ServiceCreationException {
		List elements = pipelineTree.getHandlerOrChain();
		for (Object element : elements) {
			if (element instanceof HandlerConfig) {
				HandlerConfig handler = (HandlerConfig)element;
				validateHandler(filename, handlerSection, handler);
			} else if (element instanceof ChainConfig) {
				ChainConfig chain = (ChainConfig) element;
				for (HandlerConfig handler : chain.getHandler()) {
					validateHandler(filename, handlerSection, handler);
				}
			}
		}
	}

	private static void validateHandler(String filename, String handlerSection, HandlerConfig handler) throws ServiceCreationException {
		String classname = handler.getClassName();
		if (classname == null || classname.length() == 0) {
			throwError(filename, "handler '" + handler.getName() + "' is missing classname in the pipeline section: " + handlerSection);
		}
	}

	private static PipelineTreeConfig parsePipelineTree(String filename, Element pipelineConfig, String name) throws ServiceCreationException {
		Element handlerTree = (Element) DomParseUtils.getSingleNode(filename, pipelineConfig, name);
		if (handlerTree == null) {
			return null;
		}
		PipelineTreeConfig result = new PipelineTreeConfig();
		List<Object> outElements = result.getHandlerOrChain();
		NodeList inElements = handlerTree.getChildNodes();
		for (int i = 0; i < inElements.getLength(); i++) {
			Node element = inElements.item(i);
			if (element.getNodeName().equals("handler")) {
				HandlerConfig handler = mapHandlerConfig(filename, (Element) element);
				outElements.add(handler);
			} else if (element.getNodeName().equals("chain")) {
				ChainConfig chain = mapChainConfig(filename, (Element) element);
				outElements.add(chain);
			}
		}
		return result;
	}

	private static ChainConfig mapChainConfig(String filename, Element inChain) throws ServiceCreationException {
		ChainConfig outChain = new ChainConfig();
		String value = DomParseUtils.getRequiredAttribute(filename, inChain, "name");
		outChain.setName(value);
		value = inChain.getAttribute("presence");
		outChain.setPresence(mapPresence(filename, value));
		List<HandlerConfig> outHandlerList = outChain.getHandler();
		NodeList inHandlerList = DomParseUtils.getImmediateChildrenByTagName(inChain, "handler");
		for (int i = 0; i < inHandlerList.getLength(); i++) {
			Element inHandler = (Element) inHandlerList.item(i);
			HandlerConfig outHandler = mapHandlerConfig(filename, inHandler);
			outHandlerList.add(outHandler);
		}
		return outChain;
	}

	private static HandlerConfig mapHandlerConfig(String filename, Element inHandler) throws ServiceCreationException {
		HandlerConfig outHandler = new HandlerConfig();
		String value = DomParseUtils.getElementText(filename, inHandler, "class-name");
		outHandler.setClassName(value);
		value = DomParseUtils.getRequiredAttribute(filename, inHandler, "name");
		outHandler.setName(value);
		value = inHandler.getAttribute("presence");
		outHandler.setPresence(mapPresence(filename, value));
		value = inHandler.getAttribute("continue-on-error");
		if (value != null && value.length() > 0) {
			outHandler.setContinueOnError(Boolean.valueOf(value));
		}
		value = inHandler.getAttribute("run-on-error");
		if (value != null && value.length() > 0) {
			outHandler.setRunOnError(Boolean.valueOf(value));
		}
		OptionList options = DomParseUtils.getOptionList(filename, inHandler, "options");
		outHandler.setOptions(options);
		return outHandler;
	}

	private static PresenceConfig mapPresence(String filename, String value) throws ServiceCreationException {
		if (value.equals("")) {
			return null;
		}
		try {
			return PresenceConfig.fromValue(value);
		} catch (IllegalArgumentException e) {
			DomParseUtils.throwError(filename, "Invalid handler presence value: " + value);
		}
		return null;
	}

	private static String getPipelineClassName(String filename, Element pipelineConfig, String name) throws ServiceCreationException {
		Element pipelineClassConfig = (Element) DomParseUtils.getSingleNode(filename, pipelineConfig, name);
		if (pipelineClassConfig == null) {
			return null;
		}
		return DomParseUtils.getElementText(filename, pipelineClassConfig, "class-name");
	}

	private static PipelineTreeConfig combinePipelines(String filename,
			PipelineTreeConfig overridePipelineTree,
			PipelineTreeConfig basePipelineTree,
			String elementName) throws ServiceCreationException {
		PipelineTreeConfig outTree = new PipelineTreeConfig();
		List<Object> outList = outTree.getHandlerOrChain();
		List baseElements = null;
		if (basePipelineTree != null) {
			baseElements = basePipelineTree.getHandlerOrChain();
		}
		// If we don't have anything to start with, just return the "override" data as the "combined" tree.
		if (baseElements == null) {
			outTree = overridePipelineTree;
			validateTree(filename, outTree, elementName);
			return outTree;
		}
		// Start with a new list, with copies of handlers and references to chains.
		for (Object baseElement : baseElements) {
			if (baseElement instanceof HandlerConfig) {
				outList.add(ConfigUtils.copyHandler((HandlerConfig) baseElement));
			} else if (baseElement instanceof ChainConfig) {
				outList.add(baseElement);
			}
		}
		List overrideElements = null;
		if (overridePipelineTree != null) {
			overrideElements = overridePipelineTree.getHandlerOrChain();
		}
		if (overrideElements != null) {
			for (Object overrideElement : overrideElements) {
				if (overrideElement instanceof HandlerConfig) {
					replaceHandler(filename, outList, (HandlerConfig)overrideElement);
				} else if (overrideElement instanceof ChainConfig) {
					replaceChain(filename, outList, (ChainConfig) overrideElement);
				}
			}
		}
		return outTree;
	}

	private static void validateTree(String filename,
			PipelineTreeConfig tree,
			String elementName) throws ServiceCreationException {
		List elements = tree.getHandlerOrChain();
		for (Object element : elements) {
			if (element instanceof HandlerConfig) {
				HandlerConfig handlerConfig = (HandlerConfig) element;
				if (isRemoved(handlerConfig.getPresence())) {
					throwError(filename, "Pipeline '" + elementName + "'is not overriding anything, so handler '" +  handlerConfig.getName() + "' cannot use 'removed' keyword");
				}
			} else if (element instanceof ChainConfig) {
				ChainConfig chainConfig = (ChainConfig) element;
				if (isRemoved(chainConfig.getPresence())) {
					throwError(filename, "Pipeline '" + elementName + "'is not overriding anything, so chain '" +  chainConfig.getName() + "' cannot use 'removed' keyword");
				}
			}
		}
	}

	private static boolean replaceChain(String filename, List<Object> outList, ChainConfig overrideChain) throws ServiceCreationException {
		int ix = findByName(outList, overrideChain.getName());
		if (ix < 0) {
			throwError(filename, "Can't find chain: " + overrideChain.getName());
			return false;
		}
		Object baseElement = outList.get(ix);
		if (! (baseElement instanceof ChainConfig)) {
			throwError(filename, "Can't override handler with a chain: " + overrideChain.getName());
		}
		ChainConfig baseChain = (ChainConfig) baseElement;
		if (isMandatory(baseChain.getPresence())) {
			throwError(filename, "Can't override mandatory chain: " + overrideChain.getName());
		}
		// Is this a "remove"?
		if (isRemoved(overrideChain.getPresence())) {
			outList.remove(ix);
			return true;
		}
		ChainConfig newChain = ConfigUtils.copyChain(overrideChain);
		outList.set(ix, newChain);
		return true;
	}

	private static boolean replaceHandler (String filename, List<Object> outList, HandlerConfig overrideHandler) throws ServiceCreationException {
		int ix = findByName(outList, overrideHandler.getName());
		if (ix < 0) {
			throwError(filename, "Handler '" + overrideHandler.getName() + "' is not allowed because it is not specified in the global configuration pipeline");
			return false;
		}
		Object baseElement = outList.get(ix);
		if (! (baseElement instanceof HandlerConfig)) {
			throwError(filename, "Can't override chain with a handler: " + overrideHandler.getName());
			return false;
		}
		HandlerConfig baseHandler = (HandlerConfig) baseElement;
		if (isMandatory(baseHandler.getPresence())) {
			throwError(filename, "Can't override mandatory handler: " + overrideHandler.getName());
			return false;
		}
		// Is this a "remove"?
		if (isRemoved(overrideHandler.getPresence())) {
			outList.remove(ix);
			return true;
		}
		// Replace base handler data with any declared override handler data
		ConfigUtils.copyMutableHandlerData(baseHandler, overrideHandler);
		return true;
	}

	private static boolean isMandatory(PresenceConfig p) {
		return (p != null && p.equals(PresenceConfig.MANDATORY));
	}

	private static boolean isRemoved(PresenceConfig p) {
		return (p != null && p.equals(PresenceConfig.REMOVED));
	}

	private static int findByName(List<Object> baseElements, String name) {
		for (int ix = 0; ix < baseElements.size(); ix++) {
			Object baseElement = baseElements.get(ix);
			if (baseElement instanceof HandlerConfig) {
				HandlerConfig baseHandler = (HandlerConfig) baseElement;
				if (baseHandler.getName().equals(name))
					return ix;
			} else if (baseElement instanceof ChainConfig) {
				ChainConfig baseChain = (ChainConfig) baseElement;
				if (baseChain.getName().equals(name))
					return ix;
			}
		}
		return -1;
	}

	// Keeping a list instead of a hashmap because order could be significant in the indicator matching
	private static void mapProtocolProcessor(String filename, NodeList protocolProcessors, MessageProcessorConfigHolder dstConfig) throws ServiceCreationException {
		if (protocolProcessors == null) {
			return;
		}
		List<ProtocolProcessorConfig> dstPPs = dstConfig.getProtocolProcessors();

		for (int i = 0; i < protocolProcessors.getLength(); i++) {
			Element inPP = (Element) protocolProcessors.item(i);
			ProtocolProcessorConfig outPP = mapProtocolProcessor(filename, inPP);
			int ix = findPPByName(dstPPs, outPP.getName());
			if (ix < 0) {
				// New entry - add it
				dstPPs.add(outPP);
			} else {
				dstPPs.set(ix, outPP);
			}
		}
	}

	private static ProtocolProcessorConfig mapProtocolProcessor(String filename, Element inPP) throws ServiceCreationException {
		ProtocolProcessorConfig outPP = new ProtocolProcessorConfig();
		String name = inPP.getAttribute("name");
		outPP.setName(name);
		String version = inPP.getAttribute("version");
		outPP.setVersion(version);
		String classname = DomParseUtils.getElementText(filename, inPP, "class-name");
		outPP.setClassName(classname);
		Element inIndicator = (Element) DomParseUtils.getSingleNode(filename, inPP, "indicator");
		FeatureIndicatorConfig outIndicator = mapIndicator(filename, inIndicator);
		outPP.setIndicator(outIndicator);
		return outPP;
	}

	private static FeatureIndicatorConfig mapIndicator(String filename, Element inIndicator) throws ServiceCreationException {
		// This is a choice, exactly one will appear in the XML. Modeled as two properties on FeatureIndicatorConfig
		// of which one will be null and the other present.
		FeatureIndicatorConfig indicator = new FeatureIndicatorConfig();
		if (inIndicator == null) {
			throwError(filename, "missing element 'indicator'");
		}
		String urlPattern = DomParseUtils.getElementText(filename, inIndicator, "URL-pattern");
		if (urlPattern != null) {
			indicator.setURLPattern(urlPattern);
		}
		Element inNV = (Element) DomParseUtils.getSingleNode(filename, inIndicator, "transport-header");
		if (inNV != null) {
			NameValue outNV = new NameValue();
			String name = inNV.getAttribute("name");
			String value = DomParseUtils.getText(inNV);
			outNV.setName(name);
			outNV.setValue(value);
			indicator.setTransportHeader(outNV);
		}
		if (urlPattern == null && inNV == null) {
			throwError(filename, "indicator must contain either URL-pattern or transport-header");
		}
		return indicator;
	}

	private static void mapTransports(String filename, NodeList transports, MessageProcessorConfigHolder dstConfig) throws ServiceCreationException {
		if (transports == null) {
			return;
		}

		TransportOptions outDefaultOptions = null;
		Map<String, String> dstTransportClasses = dstConfig.getTransportClasses();
		Map<String, TransportOptions> dstTransportOptions = dstConfig.getTransportOptions();
		Map<String, Map<String, String>> dstTransportHeaderOptions = dstConfig.getTransportHeaderOptions();
		for (int i = 0; i < transports.getLength(); i++) {
			Element transport = (Element) transports.item(i);
			String name = transport.getAttribute("name");
			String classname = DomParseUtils.getElementText(filename, transport, "class-name");
			dstTransportClasses.put(name.toUpperCase(), classname);  // converting to uppercase
			
			Element inDefaultOptions = (Element) DomParseUtils.getSingleNode(filename, transport, "default-options");
			if (inDefaultOptions != null) {
				outDefaultOptions = DomParseUtils.mapTransportOptions(filename, inDefaultOptions);
			} else {
				outDefaultOptions = new TransportOptions();
			}
			outDefaultOptions.setHttpTransportClassName(classname); // set the className field with the 'class-name' element value
			dstTransportOptions.put(name.toUpperCase(), outDefaultOptions);

			Map<String, String> outHeaderOptions = dstTransportHeaderOptions.get(name.toUpperCase());
			if (outHeaderOptions == null) {
				outHeaderOptions = new HashMap<String, String>();
				dstTransportHeaderOptions.put(name.toUpperCase(), outHeaderOptions);
			}
			OptionList inHeaderOptions = DomParseUtils.getOptionList(filename, transport, "header-options");
			if (inHeaderOptions != null) {
				DomParseUtils.storeNVListToHashMap(filename, inHeaderOptions, outHeaderOptions);
			}
		}
	}

	private static int findPPByName(List<ProtocolProcessorConfig> elements, String name) {
		if (elements == null) {
			return -1;
		}
		for (int ix = 0; ix < elements.size(); ix++) {
			ProtocolProcessorConfig element = elements.get(ix);
			if (element.getName().equals(name))
					return ix;
		}
		return -1;
	}


	private static void mapDataBindingConfig(String filename, Element dataBindingConfig, MessageProcessorConfigHolder dstConfig) throws ServiceCreationException {
		if (dataBindingConfig == null) {
			return;
		}
		Map<String, SerializerConfig> outDataBindings = dstConfig.getDataBindings();

		NodeList inDataBindings = DomParseUtils.getImmediateChildrenByTagName(dataBindingConfig, "data-binding");
		for (int i = 0; i < inDataBindings.getLength(); i++) {
			Element inDataBinding = (Element) inDataBindings.item(i);
			SerializerConfig outDataBinding = mapDataBinding(filename, inDataBinding);
			outDataBindings.put(outDataBinding.getName(), outDataBinding);
		}
	}

	private static SerializerConfig mapDataBinding(String filename, Element inDataBinding) throws ServiceCreationException {
		SerializerConfig outDataBinding = new SerializerConfig();
		String name = DomParseUtils.getRequiredAttribute(filename, inDataBinding, "name");
		outDataBinding.setName(name);
		String mimeType = DomParseUtils.getElementText(filename, inDataBinding, "mime-type", true);
		outDataBinding.setMimeType(mimeType);
		String serClass = DomParseUtils.getElementText(filename, inDataBinding, "serializer-factory-class-name");
		outDataBinding.setSerializerFactoryClassName(serClass);
		String deserClass = DomParseUtils.getElementText(filename, inDataBinding, "deserializer-factory-class-name");
		outDataBinding.setDeserializerFactoryClassName(deserClass);
		OptionList options = DomParseUtils.getOptionList(filename, inDataBinding, "options");
		HashMap<String,String> outOptions = outDataBinding.getOptions();
		DomParseUtils.storeNVListToHashMap(filename, options, outOptions);
		return outDataBinding;
	}

	// TODO - many of the errors thrown in this way should be individual exceptions so the text can
	// be localized better.
	private static void throwError(String filename, String cause) throws ServiceCreationException {
		throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
				ErrorConstants.ERRORDOMAIN, new Object[] {filename, cause}));
	}


}
