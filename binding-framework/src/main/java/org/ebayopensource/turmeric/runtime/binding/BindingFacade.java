/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.binding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.exception.SerializationException;
import org.ebayopensource.turmeric.runtime.binding.impl.DeserializationContextImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.SerializationContextImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.TypeConversionContextImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.XmlSerializationSupport;


/**
 * To perform serialization/deserialization, the corresponding serialization/deserialization context 
 * must be created first. Then a SerializerFactory/DeserializerFactory is created.  Once a 
 * serializer/deserializer is created form the factory,  feed the context to the serializer/deserializer 
 * results in the seialized payload or the deserialized java object.  
 * 
 * BindingFacade provides list of convenient methods to create serialization/deserialization context and
 * to perform the serialization/deserialization. 
 *   
 * @author wdeng
 *
 */
public class BindingFacade {
	
	/**
	 * Default namespace.
	 */
	public static final String DEFAULT_NAMESPACE = "urn:default";
	/**
	 * Creates and returns a IDeserializationContext of the given payload 
	 * type, the given default namespace, the single namespace and a given 
	 * map of all namespaces
	 * to prefix mappings used in the payload.  The payload is encoded using 
	 * the given charset.  The caller must provide information about the root
	 * class, its XML name, and its element schema.  Caller must also provide
	 * mappings of java type to their namespaces and mappings of java packages
	 * to their namespaces. ITypeConversionContext is optional.  If provided, 
	 * Caller can specify customized type conversion.  preserve of element order
	 * and unordered NV format are two options that you need to provide when 
	 * dealing with NV format. The caller also has the option
	 * to request the support for on-demand object node creation.
	 *   
	 * @param payloadType data format type of the payload
	 * @param defaultNamespace the namespace to be recognized as the default so that prefix 
	 * 			is skipped for elements from this namespace.
	 * @param ns2Prefix namespace to namespace prefix mapping.
	 * @param prefix2NS namespace prefix to namespace mapping.
	 * @param charset Charset of the payload.
	 * @param rootXMLName XML QName for the root element.
	 * @param rootClass Class object of the root java bean object.
	 * @param rootSchema DataElementSchema for the root object.
	 * @param supportObjectNode  true if the context should support accessing
	 * 			the payload as DOM.
	 * @param preserveElementOrder  Only appliable to NV format, whether the 
	 * 			NV pairs in the payload ordered the same as the XML elements
	 * 			in the corresponding XML payload
	 * @param javaType2NS  JavaType Class to namespace mapping;
	 * @param pkg2NS  Java package to namespace mapping;
	 * @param typeConversion  an object implemented the TypeConversion interface.
	 * 			It is intented to be used to support additional type conversion. 
	 * 			However TypeConversion is being deprecated. Should pass in <code>null</code> 
	 * @param isREST True if the payload is a rest request.	
	 * @return an IDeserializationContext.
	 */
	public static IDeserializationContext createDeserializationContext(
			String payloadType,
			String defaultNamespace,
			Map<String, List<String>> ns2Prefix,
			Map<String, String> prefix2NS,
			Charset charset,
			QName rootXMLName,
			Class rootClass,
			DataElementSchema rootSchema,
			boolean supportObjectNode,
			boolean preserveElementOrder,
			Map<Class, String> javaType2NS,
			Map<String, String> pkg2NS,
			ITypeConversionContext typeConversion,
			boolean isREST) {
		return createDeserializationContext(payloadType, defaultNamespace, ns2Prefix, prefix2NS, charset, rootXMLName,
				rootClass, rootSchema, supportObjectNode, preserveElementOrder, javaType2NS, pkg2NS, typeConversion,
				isREST, defaultNamespace);
	}

	/**
	 * Creates and returns a IDeserializationContext of the given payload 
	 * type, the given default namespace, and a given map of all namespaces
	 * to prefix mappings used in the payload.  The payload is encoded using 
	 * the given charset.  The caller must provide information about the root
	 * class, its XML name, and its element schema.  Caller must also provide
	 * mappings of java type to their namespaces and mappings of java packages
	 * to their namespaces. ITypeConversionContext is optional.  If provided, 
	 * Caller can specify customized type conversion.  preserve of element order
	 * and unordered NV format are two options that you need to provide when 
	 * dealing with NV format. The caller also has the option
	 * to request the support for on-demand object node creation.
	 *   
	 * @param payloadType data format type of the payload
	 * @param defaultNamespace the namespace to be recognized as the default so that prefix 
	 * 			is skipped for elements from this namespace.
	 * @param ns2Prefix namespace to namespace prefix mapping.
	 * @param prefix2NS namespace prefix to namespace mapping.
	 * @param charset Charset of the payload.
	 * @param rootXMLName XML QName for the root element.
	 * @param rootClass Class object of the root java bean object.
	 * @param rootSchema DataElementSchema for the root object.
	 * @param supportObjectNode  true if the context should support accessing
	 * 			the payload as DOM.
	 * @param preserveElementOrder  Only appliable to NV format, whether the 
	 * 			NV pairs in the payload ordered the same as the XML elements
	 * 			in the corresponding XML payload
	 * @param javaType2NS  JavaType Class to namespace mapping;
	 * @param pkg2NS  Java package to namespace mapping;
	 * @param typeConversion  an object implemented the TypeConversion interface.
	 * 			It is intented to be used to support additional type conversion. 
	 * 			However TypeConversion is being deprecated. Should pass in <code>null</code> 
	 * @param isREST True if the payload is a rest request.	
	 * @param singleNamespace the namespace of types if all the types are associated with
	 *  			only one namespace.  otherwise, should leave it null.
	 * @return an IDeserializationContext.
	 */
	public static IDeserializationContext createDeserializationContext(
			String payloadType,
			String defaultNamespace,
			Map<String, List<String>> ns2Prefix,
			Map<String, String> prefix2NS,
			Charset charset,
			QName rootXMLName,
			Class rootClass,
			DataElementSchema rootSchema,
			boolean supportObjectNode,
			boolean preserveElementOrder,
			Map<Class, String> javaType2NS,
			Map<String, String> pkg2NS,
			ITypeConversionContext typeConversion,
			boolean isREST,
			String singleNamespace) {
		return new DeserializationContextImpl(payloadType, defaultNamespace, ns2Prefix, prefix2NS, charset, 
				rootXMLName, rootClass, rootSchema, supportObjectNode, preserveElementOrder, pkg2NS, 
				typeConversion, isREST, singleNamespace);
	}
	/**
	 * Creates and returns a ISerializationContext of the given payload 
	 * type, the given default namespace and a given map of all namespaces
	 * to prefix mappings used in the payload.  The payload is encoded using 
	 * the given charset.  The caller must provide information about the root
	 * class, its XML name, and its element schema.  The caller must also provide
	 * mappings of java types to their namespaces and mappings of java packages
	 * to their namespaces. ITypeConversionContext is optional.  If provided, 
	 * Caller can enforce type conversion.  Caller can also specify the options
	 * to support on-demand object node creation and 
	 * unordered NV format.
	 *   
	 * @param payloadType data format type of the payload
	 * @param defaultNamespace the namespace to be recognized as the default so that prefix 
	 * 			is skipped for elements from this namespace.
	 * @param ns2Prefix namespace to namespace prefix mapping.
	 * @param prefix2NS namespace prefix to namespace mapping.
	 * @param charset Charset of the payload.
	 * @param rootXMLName XML QName for the root element.
	 * @param rootClass Class object of the root java bean object.
	 * @param rootSchema DataElementSchema for the root object.
	 * @param supportObjectNode  true if the context should support accessing
	 * 			the payload as DOM.
	 * @param javaType2NS  JavaType Class to namespace mapping;
	 * @param pkg2NS  Java package to namespace mapping;
	 * @param typeConversion  an object implemented the TypeConversion interface.
	 * 			It is intented to be used to support additional type conversion. 
	 * 			However TypeConversion is being deprecated. Should pass in <code>null</code> 
	 * @param isREST True if the payload is a rest request.	
	 * @return an ISerializationContext
	 */
	public static ISerializationContext createSerializationContext(
			String payloadType,
			String defaultNamespace,
			Map<String, List<String>> ns2Prefix,
			Map<String, String> prefix2NS,
			Charset charset,
			QName rootXMLName,
			Class rootClass,
			DataElementSchema rootSchema,
			boolean supportObjectNode,
			Map<Class, String> javaType2NS,
			Map<String, String> pkg2NS,
			ITypeConversionContext typeConversion,
			boolean isREST) {
		return createSerializationContext(payloadType, defaultNamespace, ns2Prefix, prefix2NS, charset,
				rootXMLName, rootClass, rootSchema, supportObjectNode, javaType2NS, pkg2NS, typeConversion,
				isREST, defaultNamespace);
	}
	
	/**
	 * Creates and returns a ISerializationContext of the given payload 
	 * type, the given default namespace and a given map of all namespaces
	 * to prefix mappings used in the payload.  The payload is encoded using 
	 * the given charset.  The caller must provide information about the root
	 * class, its XML name, and its element schema.  The caller must also provide
	 * mappings of java types to their namespaces and mappings of java packages
	 * to their namespaces. ITypeConversionContext is optional.  If provided, 
	 * Caller can enforce type conversion.  Caller can also specify the options
	 * to support on-demand object node creation and 
	 * unordered NV format.
	 *   
	 * @param payloadType data format type of the payload
	 * @param defaultNamespace the namespace to be recognized as the default so that prefix 
	 * 			is skipped for elements from this namespace.
	 * @param ns2Prefix namespace to namespace prefix mapping.
	 * @param prefix2NS namespace prefix to namespace mapping.
	 * @param charset Charset of the payload.
	 * @param rootXMLName XML QName for the root element.
	 * @param rootClass Class object of the root java bean object.
	 * @param rootSchema DataElementSchema for the root object.
	 * @param supportObjectNode  true if the context should support accessing
	 * 			the payload as DOM.
	 * @param javaType2NS  JavaType Class to namespace mapping;
	 * @param pkg2NS  Java package to namespace mapping;
	 * @param typeConversion  an object implemented the TypeConversion interface.
	 * 			It is intented to be used to support additional type conversion. 
	 * 			However TypeConversion is being deprecated. Should pass in <code>null</code> 
	 * @param isREST True if the payload is a rest request.	
	 * @param singleNamespace the namespace of types if all the types are associated with
	 *  			only one namespace.  otherwise, should leave it null.
	 * @return an ISerializationContext
	 */
	public static ISerializationContext createSerializationContext(
			String payloadType,
			String defaultNamespace,
			Map<String, List<String>> ns2Prefix,
			Map<String, String> prefix2NS,
			Charset charset,
			QName rootXMLName,
			Class rootClass,
			DataElementSchema rootSchema,
			boolean supportObjectNode,
			Map<Class, String> javaType2NS,
			Map<String, String> pkg2NS,
			ITypeConversionContext typeConversion,
			boolean isREST, 
			String singleNamespace) {
		return new SerializationContextImpl(payloadType, defaultNamespace, ns2Prefix, prefix2NS, charset, 
				rootXMLName, rootClass, rootSchema, supportObjectNode, pkg2NS, typeConversion, isREST, 
				singleNamespace);
	}

	/**
	 * Creates a type conversion context of the given bound types and value types.
	 * The context also defines the type converter adaptor class.
	 * 
	 * @param boundTypes The type deserialized from payload  
	 * @param valueTypes  The type used in the final java bean object.
	 * @param typeConvertAdaptor  The type converter adaptor class
	 * @return an ITypeConversionContext
	 */
	public static ITypeConversionContext createTypeConversionContext(
			Collection<String> boundTypes, 
			Collection<Class> valueTypes,
			Class typeConvertAdaptor) {
		return new TypeConversionContextImpl(boundTypes, valueTypes, typeConvertAdaptor);
	}


	/**
	 * This is a simplified version of createSerializationContext.  It uses the system's default charset,
	 * the default namespace prefix ('ns'), no object node support, no type conversion support, and
	 * no unordered NV support.   
	 * 
	 * @param msg The java bean object to be serialized.
	 * @param serFactory The SerializerFactory used to create the serializer.
	 * @param namespace The namespace of the root element.
	 * @param elementSchema A DataElementSchema that contains schema information for msg object. It is optional.
	 * @param msgClass Class of the msg object. It is optional if msg is not null.
	 * @return an ISerializationContext
	 */
	public static ISerializationContext createSerializationContext(Object msg,
			ISerializerFactory serFactory, String namespace, 
			DataElementSchema elementSchema, Class msgClass) {
		if (null == serFactory) {
			throw new NullPointerException("ISerializaerFactory cannot be null.");
		}
		if (null == msg && null == msgClass) {
			throw new NullPointerException("When msg is null, msgClass must be provided.");
		}
		
		List<String> namespaces = new ArrayList<String>(1);
		if (null == namespace) {
			namespace = DEFAULT_NAMESPACE;
		}
		namespaces.add(namespace);
		Map<String, List<String>> ns2Prefix = new HashMap<String, List<String>>();
		Map<String, String> prefix2NS = new HashMap<String, String>();
		NamespaceConvention.buildNsPrefixes(namespaces, ns2Prefix, prefix2NS);
		Class rootClass = msgClass == null? msg.getClass() : msgClass;
		QName rootXMLName = new QName(namespace, rootClass.getSimpleName(), BindingConstants.DEFAULT_PREFIX);
		Map<Class, String> javaType2NS = new HashMap<Class, String>(1);
		javaType2NS.put(rootClass, namespace);
		Map<String, String> pkg2NS = new HashMap<String, String>(1);
		pkg2NS.put(rootClass.getPackage().getName(), namespace);
		ISerializationContext serCtx = BindingFacade.createSerializationContext(
				serFactory.getPayloadType(), 
				namespace, 
				ns2Prefix, 
				prefix2NS, 
				Charset.defaultCharset(), 
				rootXMLName, 
				rootClass, 
				elementSchema, 
				false, 
				javaType2NS, 
				pkg2NS, 
				null, 
				false);
		return serCtx;
	}

	/**
	 * This is a simplified version of createSerializationContext.  It uses the system's default charset,
	 * the default namespace prefix ('ns'), no object node support, no type conversion support, and
	 * no unordered NV support.   
	 * 
	 * @param msg The java bean object to be serialized.
	 * @param serFactory The SerializerFactory used to create the serializer.
	 * @param namespace The namespace of the root element.
	 * @return an ISerializationContext
	 */
	public static ISerializationContext createSerializationContext(Object msg, ISerializerFactory serFactory, String namespace) {
		if (null == msg) {
			throw new NullPointerException("msg cannot be null.");
		}
		return createSerializationContext(msg, serFactory, namespace, null, msg.getClass());
	}


	/**
	 * This is a simplified version of createDeserializationContext.  It uses the system's default charset,
	 * the default namespace prefix ('ns'), no object node support, no type conversion support, and
	 * no unordered NV support.   
	 * 
	 * @param msg The payload to be deserialized.
	 * @param deserFactory The DeserializerFactory used to create the deserializer.
	 * @param namespace The namespace of the root element.
	 * @param elementSchema A DataElementSchema that contains schema information for msg object. It is optional.
	 * @param msgClass Class of the msg object. It is optional if msg is not null.
	 * @return an ISerializationContext
	 */
	public static IDeserializationContext createDeserializationContext(Object msg, 
			IDeserializerFactory deserFactory, String namespace, 
			DataElementSchema elementSchema, Class msgClass) {
		if (null == deserFactory) {
			throw new NullPointerException("ISerializaerFactory cannot be null");
		}
		if (null == msg && null == msgClass) {
			throw new NullPointerException("When msg is null, msgClass must be provided.");
		}
		
		List<String> namespaces = new ArrayList<String>(1);
		if (null == namespace) {
			namespace = DEFAULT_NAMESPACE;
		}
		namespaces.add(namespace);
		Map<String, List<String>> ns2Prefix = new HashMap<String, List<String>>();
		Map<String, String> prefix2NS = new HashMap<String, String>();
		NamespaceConvention.buildNsPrefixes(namespaces, ns2Prefix, prefix2NS);
		Class rootClass = msgClass == null? msg.getClass() : msgClass;
		QName rootXMLName = new QName(namespace, rootClass.getSimpleName(), BindingConstants.DEFAULT_PREFIX);
		Map<Class, String> javaType2NS = new HashMap<Class, String>(1);
		javaType2NS.put(rootClass, namespace);
		Map<String, String> pkg2NS = new HashMap<String, String>(1);
		pkg2NS.put(rootClass.getPackage().getName(), namespace);
		IDeserializationContext deserCtx = BindingFacade.createDeserializationContext(
				deserFactory.getPayloadType(), 
				namespace, 
				ns2Prefix, 
				prefix2NS, 
				Charset.defaultCharset(), 
				rootXMLName, 
				rootClass, 
				elementSchema, 
				false, 
				true,
				javaType2NS, 
				pkg2NS, 
				null, 
				false);
		return deserCtx;
	}
	/**
	 * This is a simplified version of createDeserializationContext.  It uses the system's default charset,
	 * the default namespace prefix ('ns'), no object node support, no type conversion support, and
	 * no unordered NV support.   
	 * 
	 * @param msg The payload to be deserialized.
	 * @param deserFactory The DeserializerFactory used to create the deserializer.
	 * @param namespace The namespace of the root element.
	 * @return an IDeserializationContext
	 */
	public static IDeserializationContext createDeserializationContext(Object msg, IDeserializerFactory deserFactory, String namespace) {
		if (null == msg) {
			throw new NullPointerException("msg cannot be null.");
		}
		return createDeserializationContext(msg, deserFactory, namespace, null, msg.getClass());
	}

	/**
	 * This method provides a convinient way to call binding-framework to serialize an
	 * object into a payload of your choice.
	 * 
	 * @param ctx The IDeserializationContext
	 * @param factory The IDeserializerFactory
	 * @param out The OutputStream to write the payload.
	 * @param msg The Java bean object to be serialized.
	 * @throws BindingException a BindingException
	 */
	public static void serialize(ISerializationContext ctx,
			ISerializerFactory factory, OutputStream out, Object msg)
			throws BindingException {
		ISerializer ser = factory.getSerializer();
		try {
			XMLStreamWriter streamWriter = factory.getXMLStreamWriter(ctx, out);
			streamWriter.writeStartDocument(ctx.getCharset().name(), "1.0");
			ser.serialize(ctx, msg, streamWriter);
			streamWriter.writeEndDocument();
			streamWriter.close();
		} catch (XMLStreamException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * A convenient method to serialize an object into xml payload using the given 
	 * charset.
	 * 
	 * @param obj - the object to be serialized.
	 * @param charsetName - the name of the charset to use.
	 * @return the serialized xml payload.
	 * @throws BindingException a BindingException.
	 */
	public static String serializeToXML(final Object obj, final String charsetName)
	throws BindingException {
		return XmlSerializationSupport.serialize(obj, charsetName);
	}

	/**
	 * A convenient method to serialize an object into xml payload using the given 
	 * charset.
	 * 
	 * @param obj - the object to be serialized.
	 * @param charset - the charset.
	 * @return the serialized xml payload.
	 * @throws BindingException a BindingException.
	 */
	public static String serializeToXML(final Object obj, final Charset charset)
	throws BindingException {
		return XmlSerializationSupport.serialize(obj, charset);
	}
	
	/**
	 * A convenient method to serialize an object into xml payload using the given 
	 * charset.
	 * 
	 * @param obj - the object to be serialized.
	 * @param charsetName - the name of the charset to use.
	 * @param os - the output stream to write the payload.
	 * @throws BindingException a BindingException.
	 */
	public static void serializeToXML(
			final Object obj, final String charsetName, final OutputStream os)
			throws BindingException {
		XmlSerializationSupport.serialize(obj, charsetName, os);		
	}	

	
	/**
	 * A convenient method to serialize an object into xml payload using the given 
	 * charset.
	 * 
	 * @param obj - the object to be serialized.
	 * @param charset - the charset.
	 * @param os - the output stream to write the payload.
	 * @throws BindingException a BindingException.
	 */
	public static void serializeToXML(
			final Object obj, final Charset charset, final OutputStream os)
			throws BindingException {
		XmlSerializationSupport.serialize(obj, charset, os);		
	}

	/**
	 * This method provides a convinient way to call binding-framework to deserialize 
	 * a payload into its corresponding java object.
	 * 
	 * @param ctx The IDeserializationContext
	 * @param factory The IDeserializerFactory
	 * @param payload the payload to be deserialized.
	 * @return Java bean object deserialized from the payload.
	 * @throws BindingException a BindingException.
	 */
	public static Object deserialize(IDeserializationContext ctx,
			IDeserializerFactory factory, String payload)
			throws BindingException {
		IDeserializer deser = factory.getDeserializer();
		InputStream is = new ByteArrayInputStream(payload.getBytes());
		XMLStreamReader xsr = factory.getXMLStreamReader(ctx, is);
		return deser.deserialize(ctx, xsr);
	}

	/**
	 * A convenient method to deserialize an XML payload to Java bean of the given 
	 * root class with the given charset.
	 * @param <T>  - the type of the root java object to be created
	 * @param xml - the incoming XML payload.
	 * @param charsetName - the charset name of the XML payload.
	 * @param rootClz - the type of the root java object.
	 * @return - The java object deserialized from the xml payload.
	 * @throws BindingException a BindingException.
	 */
	public static <T> T deserializeFromXML(
			final String xml, final String charsetName, final Class<T> rootClz) 
			throws BindingException {
		return XmlSerializationSupport.deserialize(xml, charsetName, rootClz);
	}
	
	/**
	 * A convenient method to deserialize an XML payload to Java bean of the given 
	 * root class with the given charset.
	 * @param <T>  - the type of the root java object to be created
	 * @param xml - the incoming XML payload.
	 * @param charset - the charset of the XML payload.
	 * @param rootClz - the type of the root java object.
	 * @return - The java object deserialized from the xml payload.
	 * @throws BindingException a BindingException.
	 */
	public static <T> T deserializeFromXML(
			final String xml, final Charset charset, final Class<T> rootClz) 
			throws BindingException {
		return XmlSerializationSupport.deserialize(xml, charset, rootClz);
	}
		

	/**
	 * A convenient method to deserialize an XML payload to Java bean of the given 
	 * root class with the given charset.
	 * @param <T>  - the type of the root java object to be created
	 * @param is - the incoming XML payload stream.
	 * @param charsetName - the charset name of the XML payload.
	 * @param rootClz - the type of the root java object.
	 * @return - The java object deserialized from the xml payload.
	 * @throws BindingException a BindingException.
	 */
	public static <T> T deserializeFromXML(
		final InputStream is, final String charsetName, final Class<T> rootClz) 
		throws BindingException {
		return XmlSerializationSupport.deserialize(is, charsetName, rootClz);
	}
		

	/**
	 * A convenient method to deserialize an XML payload to Java bean of the given 
	 * root class with the given charset.
	 * @param <T>  - the type of the root java object to be created
	 * @param is - the incoming XML payload stream.
	 * @param charset - the charset of the XML payload.
	 * @param rootClz - the type of the root java object.
	 * @return - The java object deserialized from the xml payload.
	 * @throws BindingException a BindingException.
	 */
	public static <T> T deserializeFromXML(
		final InputStream is, final Charset charset, final Class<T> rootClz) 
		throws BindingException {
		return XmlSerializationSupport.deserialize(is, charset, rootClz);
	}

	
	/**
	 * The set of serializeToXML methods caches their ISerializerFactory based on the root class
	 * name. clearSerializerFactoryCache clears cache for ISerializerFactory
	 */
	public static void clearSerializerFactoryCache() {
		XmlSerializationSupport.clearSerializerFactoryCache();
	}
	
	/**
	 * The set of serializeToXML methods caches their IDeserializerFactory based on the root class
	 * name. clearDeserializerFactoryCache clears cache for IDeserializerFactory
	 */
	public static void clearDeserializerFactoryCache() {
		XmlSerializationSupport.clearDeserializerFactoryCache();
	}

}
