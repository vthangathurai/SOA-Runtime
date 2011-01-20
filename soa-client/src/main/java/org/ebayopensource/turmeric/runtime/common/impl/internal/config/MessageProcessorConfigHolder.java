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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;


/**
 * This class represents processed configuration of the message processor; common between client and server.
 * <p>
 * Note: Most ConfigHolder data is available in higher-level structures. Refer to ServiceDesc and related structures
 * as the primary configuration in the public API for SOA framework.
 * @author rmurphy
 *
 */
public class MessageProcessorConfigHolder extends BaseConfigHolder {
	// When adding properties, please ensure that copy(), dump(), and getters/setters
	// are all covered for the new properties.  Make sure setters call checkReadOnly().

	// Hashmap indexed by binding, pointing to hashmap indexed by XML type.
	private Map<String, Map<String, CustomSerializerConfig>> m_serializerMap
	 = new HashMap<String, Map<String, CustomSerializerConfig>>();
	private Map<String, Map<String, TypeConverterConfig>> m_converterMap
	 = new HashMap<String, Map<String, TypeConverterConfig>>();
	// Pipeline config, data binding config, protocol processor
	private String m_requestPipelineClassName;
	private String m_responsePipelineClassName;
	private String m_requestDispatcherClassName;
	private String m_responseDispatcherClassName;
	private List<FrameworkHandlerConfig> m_loggingHandlers = new ArrayList<FrameworkHandlerConfig>();
	private List<HandlerConfig> m_requestHandlers;
	private List<HandlerConfig> m_responseHandlers;
	private PipelineTreeConfig m_requestPipelineTree;
	private PipelineTreeConfig m_responsePipelineTree;
	private Map<String, SerializerConfig> m_dataBindings = new HashMap<String, SerializerConfig> ();
	private List<ProtocolProcessorConfig> m_protocolProcessors = new ArrayList<ProtocolProcessorConfig> ();
	private Map<String, String> m_transportClasses = new HashMap<String, String> ();
	private Map<String, TransportOptions> m_transportOptions = new HashMap<String, TransportOptions> ();
	private Map<String, Map<String, String>> m_transportHeaderOptions = new HashMap<String, Map<String, String>>();
	private String m_errorMappingClass;
	private String m_errorDataProviderClass;	
	private String m_configFilename;
	private String m_groupFilename;
	private static final char NL = '\n';

	/*
	 * Safe copy method.
	 * @return a new object with a safe copy of the original data
	 */
	public MessageProcessorConfigHolder copy() {
		//
		// Current object copy pattern:
		// When read-only: Gets of complex properties return full deep copy so subsequent caller modifications do not
		// alter the source object's state.  Sets are not allowed on any properties.
		// When writeable: Gets return simple or complex objects directly, and they are alterable.  Sets are
		// accepted directly on all properties.
		// Clone of a read-only object: Produces a new deep copy that starts out life writeable. It can be locked
		// down subsequently to become a read-only object if desired, and will remain a full independent copy from
		// the original.
		MessageProcessorConfigHolder newCH = new MessageProcessorConfigHolder();
		newCH.m_readOnly = false;
		newCH.m_requestPipelineClassName = m_requestPipelineClassName;
		newCH.m_responsePipelineClassName = m_responsePipelineClassName;
		newCH.m_requestDispatcherClassName = m_requestDispatcherClassName;
		newCH.m_responseDispatcherClassName = m_responseDispatcherClassName;
		if (m_loggingHandlers != null) {
			newCH.m_loggingHandlers = copyLoggingHandlers(m_loggingHandlers);
		}
		newCH.m_errorMappingClass = m_errorMappingClass;
		newCH.m_errorDataProviderClass = m_errorDataProviderClass;		
		newCH.m_configFilename = m_configFilename;
		newCH.m_groupFilename = m_groupFilename;
		// Copy the following deep structures.
		newCH.m_serializerMap = copyCustomSerializers(m_serializerMap);
		newCH.m_converterMap = copyTypeConverters(m_converterMap);
		newCH.m_requestPipelineTree = ConfigUtils.copyPipelineList(m_requestPipelineTree);
		if (m_requestPipelineTree != null) {
			newCH.m_requestHandlers = flattenTree(m_requestPipelineTree);
		}
		newCH.m_responsePipelineTree = ConfigUtils.copyPipelineList(m_responsePipelineTree);
		if (m_responsePipelineTree != null) {
			newCH.m_responseHandlers = flattenTree(m_responsePipelineTree);
		}
		newCH.m_dataBindings = copyDataBindings(m_dataBindings);
		if (m_protocolProcessors != null) {
			newCH.m_protocolProcessors = copyProcessorConfigList(m_protocolProcessors);
		}
		if (m_transportClasses != null) {
			newCH.m_transportClasses = new HashMap<String, String>(m_transportClasses);
		}
		newCH.m_transportOptions = copyTransportOptions(m_transportOptions);
		newCH.m_transportHeaderOptions = copyTransportHeaderOptions(m_transportHeaderOptions);
		
		return newCH;
	}

	/**
	 * Deep-copies the local transport header options into another map. 
	 * @return the newly-created map with a copy of the local transport header options. 
	 */
	private Map<String, Map<String, String>> copyTransportHeaderOptions(Map<String, Map<String, String>> transportHeaderOptions) {
		if (transportHeaderOptions == null) { 
			return null;
		}
		Map<String, Map<String, String>> newTransportHeaderOptions = 
			new HashMap<String, Map<String, String>>(transportHeaderOptions.size());
		for (Map.Entry<String, Map<String, String>> transportHeaderOptionsMapEntry : transportHeaderOptions.entrySet()) {
			newTransportHeaderOptions.put(transportHeaderOptionsMapEntry.getKey(), 
					new HashMap<String, String>(transportHeaderOptionsMapEntry.getValue()));
		}
		return newTransportHeaderOptions;
	}

	private List<FrameworkHandlerConfig> copyLoggingHandlers(List<FrameworkHandlerConfig> inHandlers) {
		if (inHandlers == null) {
			return null;
		}
		List<FrameworkHandlerConfig> outHandlers = new ArrayList<FrameworkHandlerConfig>();
		for (FrameworkHandlerConfig inHandler : inHandlers) {
			FrameworkHandlerConfig outHandler = ConfigUtils.copyFrameworkHandlerConfig(inHandler);
			outHandlers.add(outHandler);
		}
		return outHandlers;
	}

	private List<ProtocolProcessorConfig> copyProcessorConfigList(List<ProtocolProcessorConfig> inList) {
		if (inList == null) {
			return null;
		}
		List<ProtocolProcessorConfig> outList = new ArrayList<ProtocolProcessorConfig>();
		for (int i = 0; i < inList.size(); i++) {
			outList.add(ConfigUtils.copyProcessorConfig(inList.get(i)));
		}
		return outList;
	}

	private Map<String, TransportOptions> copyTransportOptions(Map<String, TransportOptions> inMap) {
		if (inMap == null) {
			return null;
		}
		Map<String, TransportOptions> outMap = new HashMap<String, TransportOptions>();
		for (Map.Entry<String, TransportOptions> entry : inMap.entrySet()) {
			String key = entry.getKey();
			TransportOptions inTC = entry.getValue();
			TransportOptions outTC = ConfigUtils.copyTransportOptions(inTC);
			outMap.put(key, outTC);
		}
		return outMap;
	}

	private HashMap<String, CustomSerializerConfig> copyOneSerializerMap(Map<String, CustomSerializerConfig> inMap) {
		if (inMap == null) {
			return null;
		}
		HashMap<String, CustomSerializerConfig> outMap = new HashMap<String, CustomSerializerConfig>();
		for (Map.Entry<String, CustomSerializerConfig> entry : inMap.entrySet()) {
			String key = entry.getKey();
			CustomSerializerConfig inCS = entry.getValue();
			CustomSerializerConfig outCS = ConfigUtils.copyCustomSerializer(inCS);
			outMap.put(key, outCS);
		}
		return outMap;
	}

	private Map<String, Map<String, CustomSerializerConfig>> copyCustomSerializers(Map<String, Map<String, CustomSerializerConfig>> inMap) {
		if (inMap == null) {
			return null;
		}
		HashMap<String, Map<String, CustomSerializerConfig>> outMap = new HashMap<String, Map<String, CustomSerializerConfig>>();
		for (Map.Entry<String, Map<String, CustomSerializerConfig>> entry : inMap.entrySet()) {
			String key = entry.getKey();
			Map<String, CustomSerializerConfig> inOneMap = entry.getValue();
			Map<String, CustomSerializerConfig> outOneMap = copyOneSerializerMap(inOneMap);
			outMap.put(key, outOneMap);
		}
		return outMap;
	}

	private Map<String, TypeConverterConfig> copyOneTypeConverterMap(Map<String, TypeConverterConfig> inMap) {
		if (inMap == null) {
			return null;
		}
		HashMap<String, TypeConverterConfig> outMap = new HashMap<String, TypeConverterConfig>();
		for (Map.Entry<String, TypeConverterConfig> entry : inMap.entrySet()) {
			String key = entry.getKey();
			TypeConverterConfig inTC = entry.getValue();
			TypeConverterConfig outTC = ConfigUtils.copyTypeConverter(inTC);
			outMap.put(key, outTC);
		}
		return outMap;
	}

	private Map<String, SerializerConfig> copyDataBindings(Map<String, SerializerConfig> inBindings) {
		if (inBindings == null) {
			return null;
		}
		Map<String, SerializerConfig> outBindings = new HashMap<String, SerializerConfig>();
		for (Map.Entry<String, SerializerConfig> entry : inBindings.entrySet()) {
			String key = entry.getKey();
			SerializerConfig inSC = entry.getValue();
			SerializerConfig outSC = ConfigUtils.copySerializerConfig(inSC);
			outBindings.put(key, outSC);
		}
		return outBindings;
	}

	private Map<String, Map<String, TypeConverterConfig>> copyTypeConverters(Map<String, Map<String, TypeConverterConfig>> inMap) {
		if (inMap == null) {
			return null;
		}
		Map<String, Map<String, TypeConverterConfig>> outMap = new HashMap<String, Map<String, TypeConverterConfig>>();
		for (Map.Entry<String, Map<String, TypeConverterConfig>> entry : inMap.entrySet()) {
			String key = entry.getKey();
			Map<String, TypeConverterConfig> inOneMap = entry.getValue();
			Map<String, TypeConverterConfig> outOneMap = copyOneTypeConverterMap(inOneMap);
			outMap.put(key, outOneMap);
		}
		return outMap;
	}

	/**
	 * Get the configured map of custom serializers.
	 * @param bindingName  the name of the binding whose map is to be retrieved.
	 * @return  the serializer map for the data binding given by bindingName.
	 */
	public Map<String, CustomSerializerConfig> getCustomSerializerMap(String bindingName) {
		Map<String, CustomSerializerConfig> retMap = m_serializerMap.get(bindingName);
		if (retMap == null) {
			retMap = new HashMap<String, CustomSerializerConfig>();
			m_serializerMap.put(bindingName, retMap);
		}

		if (isReadOnly()) {
			return copyOneSerializerMap(retMap);
		}

		return retMap;
	}

	/**
	 * Get the configured map of type converters.
	 * @param bindingName  the name of the binding whose map is to be retrieved.
	 * @return  the type converter map for the data binding given by bindingName.
	 */
	public Map<String, TypeConverterConfig> getTypeConverterMap(String bindingName) {
		Map<String, TypeConverterConfig> retMap = m_converterMap.get(bindingName);
		if (retMap == null) {
			retMap = new HashMap<String, TypeConverterConfig>();
			m_converterMap.put(bindingName, retMap);
		}

		if (isReadOnly()) // call recursively and return a copy.
			return copyOneTypeConverterMap(retMap);

		return retMap;
	}

	/**
	 * @return  the request pipeline class name
	 */
	public String getRequestPipelineClassName() {
		return m_requestPipelineClassName;
	}

	/**
	 * Set the request pipeline class name.
	 * @param requestPipelineClassName  the class name to set
	 */
	public void setRequestPipelineClassName(String requestPipelineClassName) {
		checkReadOnly();
		this.m_requestPipelineClassName = requestPipelineClassName;
	}

	/**
	 * @return  the response pipeline class name.
	 */
	public String getResponsePipelineClassName() {
		return m_responsePipelineClassName;
	}

	/**
	 * Set the response pipeline class name.
	 * @param responsePipelineClassName the class name to set
	 */
	public void setResponsePipelineClassName(String responsePipelineClassName) {
		checkReadOnly();
		this.m_responsePipelineClassName = responsePipelineClassName;
	}

	/**
	 * @return  the request dispatcher class name.
	 */
	public String getRequestDispatcherClassName() {
		return m_requestDispatcherClassName;
	}

	/**
	 * Set the request dispatcher class name.
	 * @param requestDispatcherClassName  the class name to set
	 */
	public void setRequestDispatcherClassName(String requestDispatcherClassName) {
		checkReadOnly();
		this.m_requestDispatcherClassName = requestDispatcherClassName;
	}

	/**
	 * @return  the response dispatcher class name.
	 */
	public String getResponseDispatcherClassName() {
		return m_responseDispatcherClassName;
	}

	/**
	 * Set the response dispatcher class name.
	 * @param responseDispatcherClassName  the class name to set
	 */
	public void setResponseDispatcherClassName(String responseDispatcherClassName) {
		checkReadOnly();
		this.m_responseDispatcherClassName = responseDispatcherClassName;
	}

	/**
	 * @return the configured list of logging handler configuration entries.
	 */
	public List<FrameworkHandlerConfig> getLoggingHandlers() {
		if (isReadOnly() && m_loggingHandlers != null) {
			return copyLoggingHandlers(m_loggingHandlers);
		}
		return m_loggingHandlers;
	}

	/**
	 * @return  the configured list of request handler configuration entries.
	 */
	public List<HandlerConfig> getRequestHandlers() {
		if (isReadOnly() && m_requestHandlers != null) {
			return new ArrayList<HandlerConfig>(m_requestHandlers);
		}
		return m_requestHandlers;
	}

	/**
	 * @return  the configured list of response handler configuration entries.
	 */
	public List<HandlerConfig> getResponseHandlers() {
		if (isReadOnly() && m_responseHandlers != null) {
			return new ArrayList<HandlerConfig>(m_responseHandlers);
		}
		return m_responseHandlers;
	}

	/**
	 * @return  the m_requestPipelineTree
	 * @uml.property  name="m_requestPipelineTree"
	 */
	PipelineTreeConfig getRequestPipelineTree() {
		if (isReadOnly()) {
			return ConfigUtils.copyPipelineList(m_requestPipelineTree);
		}
		return m_requestPipelineTree;
	}


	/**
	 * @param requestTree  the m_requestPipelineTree to set
	 * @uml.property  name="m_requestPipelineTree"
	 */
	void setRequestPipelineTree(PipelineTreeConfig requestTree) {
		checkReadOnly();
		m_requestPipelineTree = requestTree;
		m_requestHandlers = flattenTree(requestTree);
	}

	/**
	 * @return  the m_responsePipelineTree
	 * @uml.property  name="m_responsePipelineTree"
	 */
	PipelineTreeConfig getResponsePipelineTree() {
		if (isReadOnly()) {
			return ConfigUtils.copyPipelineList(m_responsePipelineTree);
		}
		return m_responsePipelineTree;
	}

	/**
	 * @param responseTree  the m_responsePipelineTree to set
	 * @uml.property  name="m_responsePipelineTree"
	 */
	void setResponsePipelineTree(PipelineTreeConfig responseTree) {
		checkReadOnly();
		m_responsePipelineTree = responseTree;
		m_responseHandlers = flattenTree(responseTree);
	}

	/**
	 * @return the configured map of data binding configurations.
	 */
	public Map<String, SerializerConfig> getDataBindings() {
		if (isReadOnly()) {
			return copyDataBindings(m_dataBindings);
		}
		return m_dataBindings;
	}

	/**
	 * @return  the ocnfigured list of protocol processor configurations.
	 */
	public List<ProtocolProcessorConfig> getProtocolProcessors() {
		if (isReadOnly()) {
			return new ArrayList<ProtocolProcessorConfig>(m_protocolProcessors);
		}
		return m_protocolProcessors;
	}

	/**
	 * @return  the configured map of transport names and their associated options.
	 */
	public Map<String, TransportOptions> getTransportOptions() {
		if (isReadOnly()) {
			return copyTransportOptions(m_transportOptions);
		}
		return m_transportOptions;
	}

	/**
	 * Getter for the transport header options
	 * @return the configured map of transport names and their associated header options.
	 */
	public Map<String, Map<String, String>> getTransportHeaderOptions() {
		if (isReadOnly()) {
			return copyTransportHeaderOptions(m_transportHeaderOptions);
		}
		return m_transportHeaderOptions;
	}

	/**
	 * @return  the configured map of transport names and their associated class names.
	 */
	public Map<String, String> getTransportClasses() {
		if (isReadOnly()) {
			return new HashMap<String, String>(m_transportClasses);
		}
		return m_transportClasses;
	}

	/**
	 * @return  the name of the server-side error data provider class (which implements the ErrorDataProvider interface).
	 */
	public String getErrorDataProviderClass() {
		return m_errorDataProviderClass;
	}

	/**
	 * Set the name of the server-side error data provider class.
	 * @param errorDataProviderClass  the class name to set
	 */
	public void setErrorDataProviderClass(String errorDataProviderClass) {
		checkReadOnly();
		m_errorDataProviderClass = errorDataProviderClass;
	}
	
	/**
	 * @return  the name of the server-side error mapping class (which implements the ErrorMapper interface).
	 */
	public String getErrorMappingClass() {
		return m_errorMappingClass;
	}

	/**
	 * Set the name of the server-side error mapping class.
	 * @param errorMapClass  the class name to set
	 */
	public void setErrorMappingClass(String errorMapClass) {
		checkReadOnly();
		m_errorMappingClass = errorMapClass;
	}	

	/**
	 * @return the filename of the associated configuration.
	 */
	public String getConfigFilename() {
		return m_configFilename;
	}

	/**
	 * Set the filename of the associated configuration.
	 * @param filename the filename to set
	 */
	public void setConfigFilename(String filename) {
		m_configFilename = filename;
	}

	/**
	 * @return the filename of the referenced group configuration, or null if none.
	 */
	public String getGroupFilename() {
		return m_groupFilename;
	}

	// TODO - it's possible to have "incomplete" handlers/chains from the group level entry.  Check for these and remove or error out.
	private static ArrayList<HandlerConfig> flattenTree(PipelineTreeConfig pipelineTree) {
		ArrayList<HandlerConfig> outList = new ArrayList<HandlerConfig>();
		if (pipelineTree == null) {
			return outList;
		}
		List elements = pipelineTree.getHandlerOrChain();
		for (Object element : elements) {
			if (element instanceof HandlerConfig) {
				outList.add((HandlerConfig)element);
			} else if (element instanceof ChainConfig) {
				ChainConfig chain = (ChainConfig) element;
				for (HandlerConfig handler : chain.getHandler()) {
					outList.add(handler);
				}
			}
		}
		return outList;
	}

	/*
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		if (m_errorMappingClass != null) {
			sb.append("errorMappingClass="+m_errorMappingClass+NL);
		}
		if (m_errorDataProviderClass != null) {
			sb.append("errorDataProviderClass="+m_errorDataProviderClass+NL);
		}
		if (m_serializerMap != null) {
			dumpSerializerMap(sb, m_serializerMap);
		}
		if (m_converterMap != null) {
			dumpTypeConverters(sb, m_converterMap);
		}
		sb.append("========== Pipeline =========="+NL);
		if (m_requestPipelineClassName != null) {
			sb.append("requestPipelineClassName="+m_requestPipelineClassName+NL);
		}
		if (m_responsePipelineClassName != null) {
			sb.append("responsePipelineClassName="+m_responsePipelineClassName+NL);
		}
		if (m_requestDispatcherClassName != null) {
			sb.append("requestDispatcherClassName="+m_requestDispatcherClassName+NL);
		}
		if (m_responseDispatcherClassName != null) {
			sb.append("responseDispatcherClassName="+m_responseDispatcherClassName+NL);
		}
		if (m_loggingHandlers != null && !m_loggingHandlers.isEmpty()) {
			sb.append("Logging Handlers:" + NL);
			for (FrameworkHandlerConfig handler : m_loggingHandlers) {
				sb.append("Class name: " + handler.getClassName() + NL);
				ConfigUtils.dumpStringMap(sb, handler.getOptions(), "\t");
			}
		}
		if (m_requestPipelineTree != null) {
			sb.append("Request Pipeline Tree:"+NL);
			dumpPipelineTree(sb, m_requestPipelineTree);
		}
		if (m_requestHandlers != null) {
			sb.append("Request Handler List:"+NL);
			for (HandlerConfig handler : m_requestHandlers)
				dumpHandler(sb, handler, "");
		}
		if (m_responsePipelineTree != null) {
			sb.append("Response Pipeline Tree:"+NL);
			dumpPipelineTree(sb, m_responsePipelineTree);
		}
		if (m_responseHandlers != null) {
			sb.append("Response Handler List:"+NL);
			for (HandlerConfig handler : m_responseHandlers)
				dumpHandler(sb, handler, "");
		}
		sb.append("========== Data Bindings =========="+NL);
		if (m_dataBindings != null) {
			sb.append("Data Bindings:"+NL);
			List<String> dataBindings = new ArrayList<String>(m_dataBindings.keySet());
			Collections.sort(dataBindings);
			for(String key : dataBindings) {
				SerializerConfig value = m_dataBindings.get(key);
				dumpDataBinding(sb, value);
			}
		}
		if (m_protocolProcessors != null) {
			sb.append("========== Protocol Processors =========="+NL);
			for (ProtocolProcessorConfig pp : m_protocolProcessors) {
				dumpProtocolProcessor(sb, pp);
			}
		}
		sb.append("========== Transports ==========" + NL);
		if (m_transportClasses != null) {
			sb.append("Transports:" + NL);
			List<String> transports = new ArrayList<String>(m_transportClasses.keySet());
			Collections.sort(transports);
			for (String key : transports) {
				String transportClass = m_transportClasses.get(key);
				TransportOptions options = m_transportOptions.get(key);
				Map<String, String> headerOptions = m_transportHeaderOptions.get(key);
				dumpTransport(sb, key, transportClass, options, headerOptions);
			}
		}
	}

	private void dumpSerializerMap(StringBuffer sb, Map<String, Map<String, CustomSerializerConfig>> serMap) {
		sb.append("========== Custom Serializers =========="+NL);

		List<String> serKeys = new ArrayList<String>(serMap.keySet());
		Collections.sort(serKeys);
		for(String serkey : serKeys) {
			Map<String, CustomSerializerConfig> perDataBindingMap = serMap.get(serkey);
			sb.append("Binding=" + serkey + NL);
			List<String> javaTypeNameKeys = new ArrayList<String> (perDataBindingMap.keySet());
			Collections.sort(javaTypeNameKeys);

			for(String javaTypeNameKey : javaTypeNameKeys) {
				//String javaTypeName = entry2.getKey();
				CustomSerializerConfig cs = perDataBindingMap.get(javaTypeNameKey);
				sb.append("\tcustomSer java type="+cs.getJavaTypeName());
				if (cs.getSerializerClassName() != null) {
					sb.append(NL+"\t\tser="+cs.getSerializerClassName());
				}
				if (cs.getDeserializerClassName() != null) {
					sb.append(NL+"\t\tser="+cs.getDeserializerClassName());
				}
				sb.append(NL);
			}
		}

	}

	private void dumpTypeConverters(StringBuffer sb, Map<String, Map<String, TypeConverterConfig>> convMap) {
		sb.append("========== Type converters =========="+NL);

		List<String> convKeys = new ArrayList<String>(convMap.keySet());
		Collections.sort(convKeys);
		for(String key : convKeys) {
			Map<String, TypeConverterConfig> dataBindingMap = convMap.get(key);
			sb.append("Binding=" + key + NL);
			List<String> dataKeys = new ArrayList<String> (dataBindingMap.keySet());
			Collections.sort(dataKeys);

			for(String dataKey : dataKeys) {
				TypeConverterConfig tc = dataBindingMap.get(dataKey);
				sb.append("\tcustomSer bound type="+tc.getBoundJavaTypeName());
				if (tc.getValueJavaTypeName() != null) {
					sb.append(NL+"\t\tvalue type="+tc.getValueJavaTypeName());
				}
				if (tc.getTypeConverterClassName() != null) {
					sb.append(NL+"\t\type conv="+tc.getTypeConverterClassName());
				}
				if (tc.getXmlTypeName() != null) {
					sb.append(NL+"\t\tXML type="+tc.getXmlTypeName());
				}
				sb.append(NL);
			}
		}


	}
	private static void dumpProtocolProcessor(StringBuffer sb, ProtocolProcessorConfig pp) {
		sb.append("{name="+pp.getName());
		if (pp.getVersion() != null)
			sb.append(" version="+pp.getVersion());
		if (pp.getIndicator() != null) {
			if (pp.getIndicator().getURLPattern() != null) {
				sb.append(NL+"\turl-indicator="+pp.getIndicator().getURLPattern());
			}
			if (pp.getIndicator().getTransportHeader() != null) {
				sb.append(NL+"\ttransport-indicator="+ConfigUtils.NVToString(pp.getIndicator().getTransportHeader()));
			}
		}
		if (pp.getClassName() != null) {
			sb.append(NL+"\tclassname="+pp.getClassName());
		}
		sb.append("}"+NL);
	}

	private static void dumpDataBinding(StringBuffer sb, SerializerConfig db) {
		sb.append("\tname="+db.getName());
		if (db.getMimeType() != null) {
			sb.append(" mimeType=" + db.getMimeType());
		}
		if (db.getSerializerFactoryClassName() != null) {
			sb.append(NL+"\t\tserFactory="+db.getSerializerFactoryClassName());
		}
		if (db.getDeserializerFactoryClassName() != null) {
			sb.append(NL+"\t\tdeserFactory="+db.getDeserializerFactoryClassName());
		}
		if (db.getOptions() != null && !db.getOptions().isEmpty()) {
			sb.append(NL+"\t\tOptions:"+NL);
			ConfigUtils.dumpStringMap(sb, db.getOptions(), "\t\t\t");
		}
		sb.append(NL);
	}

	private static void dumpTransport(StringBuffer sb, String name, String transportClass, TransportOptions options, Map<String, String> headerOptions) {
		sb.append("\tname="+name);
		if (transportClass != null) {
			sb.append(" class=" + transportClass + NL);
		}
		if (options != null) {
			ConfigUtils.dumpTransportOptions(sb, options, "\t\t");
		}
		sb.append(NL);
		if (headerOptions != null && !headerOptions.isEmpty()) {
			sb.append("headerOptions=").append(headerOptions.toString());
			sb.append(NL);
		}
		
	}

	private static void dumpHandler(StringBuffer sb, HandlerConfig h, String prefix) {
		sb.append(prefix+"\t{name="+h.getName());
		if (h.getClassName() != null) {
			sb.append(",class=" + h.getClassName());
		}
		if (h.getPresence() != null) {
			sb.append(",presence="+h.getPresence().toString());
		}
		if (h.isContinueOnError() != null) {
			sb.append(",continue="+h.isContinueOnError().toString());
		}
		if (h.isRunOnError() != null) {
			sb.append(",run="+h.isRunOnError().toString());
		}
		sb.append("}"+NL);
		ConfigUtils.dumpOptionList(sb, h.getOptions(), prefix);
	}

	private static void dumpPipelineTree(StringBuffer sb, PipelineTreeConfig pipelineTree) {
		List elements = pipelineTree.getHandlerOrChain();
		for (Object element : elements) {
			if (element instanceof HandlerConfig) {
				dumpHandler(sb, (HandlerConfig) element, "");
			} else if (element instanceof ChainConfig) {
				ChainConfig chain = (ChainConfig) element;
				sb.append("\tChain="+chain.getName());
				if (chain.getPresence() != null)
					sb.append(" presence="+chain.getPresence().toString());
				sb.append(NL);
				for (HandlerConfig handler : chain.getHandler()) {
					sb.append("\t");
					dumpHandler(sb, handler, "");
				}
			}
		}
	}
}
