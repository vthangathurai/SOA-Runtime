/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializer;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializer;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.impl.DeserializationContextImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.SerializationContextImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.xml.XMLDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.xml.XMLSerializerFactory;


/**
 * Utility class to support Object to XML serialization
 * and XML to Object deserialization.
 * 
 * It caches ISerializerFactory and IDeserializerFactory
 * instances for performance.
 * 
 * @author yyao
 */
public class XmlSerializationSupport {
	
	/**
	 * return XML string for a given java bean and charset name.
	 * @param obj  The Java bean to be serialized.
	 * @param charsetName The name of the final payload Charset.
	 * @return The serialized payload string.
	 * @throws BindingException a BindingException.
	 */
	public static String serialize(final Object obj, final String charsetName)
		throws BindingException {
					
		return serialize(obj, Charset.forName(charsetName));
	}
	
	/**
	 * return XML string for a given java bean and charset.
	 * 
	 * @param obj  The Java bean to be serialized.
	 * @param charset The Charset of the final payload.
	 * @return The serialized payload string.
	 * @throws BindingException a BindingException.
	 */
	public static String serialize(final Object obj, final Charset charset)
		throws BindingException {
				
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		serialize(obj, charset, bos);
		try {			
			return bos.toString(charset.name());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * stream XML bytes to output stream for a given java bean and charset name.
	 * @param obj  The Java bean to be serialized.
	 * @param charsetName The name of the final payload Charset.
	 * @param os The OutputStream for writing payload string.
	 * @throws BindingException a BindingException.
	 */
	public static void serialize(
		final Object obj, final String charsetName, final OutputStream os)
		throws BindingException {
		
		serialize(obj, Charset.forName(charsetName), os);
	}
	
	/**
	 * stream XML bytes to output stream for a given java bean and charset.
	 * @param obj  The Java bean to be serialized.
	 * @param charset The final payload Charset.
	 * @param os The OutputStream for writing payload string.
	 * @throws BindingException a BindingException.
	 */
	public static void serialize(
		final Object obj, final Charset charset, final OutputStream os)
		throws BindingException {
		
		Class<?> rootClz = obj.getClass();
		ISerializerFactory factory = getSerializerFactory(rootClz);
		ISerializationContext ctx = createSerializationContext(rootClz, factory, charset);	
		ISerializer ser = factory.getSerializer();
		try {			
			XMLStreamWriter streamWriter = factory.getXMLStreamWriter(ctx, os);
			streamWriter.writeStartDocument(charset.name(), "1.0");
			ser.serialize(ctx, obj, streamWriter);
			streamWriter.writeEndDocument();
			streamWriter.close();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Return deserialized java bean instance from given XML string.
	 * 
	 * 
	 * @param  <T> The root class of the deserialized object.
	 * @param  xml The payload string to be deserialized.
	 * @param  charsetName The charset name for the payload string.
	 * @param  rootClz The root class of the deserialized object.
	 * 
	 * @return The deserialized object from the given <code>xml</code> string.
	 * 
	 * @throws BindingException binding exception
	 */
	public static <T> T deserialize(
		final String xml, final String charsetName, final Class<T> rootClz) 
		throws BindingException {
		
		return deserialize(xml, Charset.forName(charsetName), rootClz);
	}
	
	/**
	 * return deserialized java bean instance from given XML string,
	 * charset and java type.
	 *
	 * @param  <T> The root class of the deserialized object.
	 * @param  xml The payload string to be deserialized.
	 * @param  charset The charset of the payload string.
	 * @param  rootClz The root class of the deserialized object.
	 * 
	 * @return The deserialized object from the given <code>xml</code> string.
	 * 
	 * @throws BindingException binding exception
	 */
	public static <T> T deserialize(
		final String xml, final Charset charset, final Class<T> rootClz) 
		throws BindingException {
		
		InputStream is = new ByteArrayInputStream(xml.getBytes(charset));
		return deserialize(is, charset, rootClz);
	}
	
	/**
	 * return deserialized java bean instance from given 'XML' input stream,
	 * charset name and java type.
	 *
	 *
	 * @param  <T> The root class of the deserialized object.
	 * @param  is The payload input stream to be deserialized.
	 * @param  charsetName The charset name that the payload string uses.
	 * @param  rootClz The root class of the deserialized object.
	 * 
	 * @return The deserialized object from the given <code>xml</code> string.
	 * 
	 * @throws BindingException binding exception
	 */
	public static <T> T deserialize(
		final InputStream is, final String charsetName, final Class<T> rootClz) 
		throws BindingException {
		
		return deserialize(is, Charset.forName(charsetName), rootClz);
	}
	/**
	 * return deserialized java bean instance from given 'XML' input stream,
	 * charset and java type.
	 *
	 * @param  <T> The root class of the deserialized object.
	 * @param  is The payload input stream to be deserialized.
	 * @param  charset The charset of the payload string.
	 * @param  rootClz The root class of the deserialized object.
	 * 
	 * @return The deserialized object from the given <code>xml</code> string.
	 * 
	 * @throws BindingException binding exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(
		final InputStream is, final Charset charset, final Class<T> rootClz) 
		throws BindingException {
		
		IDeserializerFactory factory = getDeserializerFactory(rootClz);
		IDeserializationContext ctx = createDeserializationContext(rootClz, factory, charset);
		IDeserializer deser = factory.getDeserializer();	
		XMLStreamReader xsr = factory.getXMLStreamReader(ctx, is);
		return (T)deser.deserialize(ctx, xsr);
	}
	
	/**
	 * clear cache for ISerializerFactory.
	 */
	public static void clearSerializerFactoryCache() {
		SerializerFactoryCache.clear();
	}
	
	/**
	 * clear cache for IDeserializerFactory.
	 */
	public static void clearDeserializerFactoryCache() {
		DeserializerFactoryCache.clear();
	}
	
	private static final Map<String, List<String>> NS_TO_PREFIX = Collections.emptyMap();
	private static final Map<String, String> PREFIX_TO_NS = Collections.emptyMap();
	private static final Map<String, String> PKG_TO_NS = Collections.emptyMap();
	
	private static ISerializationContext createSerializationContext
		(Class<?> rootClass, ISerializerFactory serFactory, final Charset charset) {
		
		return new SerializationContextImpl(
			serFactory.getPayloadType(), 
			null, 
			NS_TO_PREFIX, 
			PREFIX_TO_NS, 
			charset, 
			new QName(rootClass.getSimpleName()), 
			rootClass, 
			null, 
			false,
			PKG_TO_NS, 
			null, 
			false);
	}
	
	private static IDeserializationContext createDeserializationContext
		(Class<?> rootClz, IDeserializerFactory deserFactory, final Charset charset) {

		return new DeserializationContextImpl(
			deserFactory.getPayloadType(), 
			null, 
			NS_TO_PREFIX, 
			PREFIX_TO_NS, 
			charset, 
			new QName(rootClz.getSimpleName()), 
			rootClz, 
			null, 
			false, 
			true,
			PKG_TO_NS, 
			null, 
			false);
	}
	
	private static ISerializerFactory getSerializerFactory
		(Class<?> rootClz) throws BindingException {
		
		ISerializerFactory factory = SerializerFactoryCache.get(rootClz);
		if (factory == null) {
			synchronized (rootClz) {
				factory = SerializerFactoryCache.get(rootClz);
				if (factory == null) {
					factory = createSerializerFactory(rootClz);
					SerializerFactoryCache.put(rootClz, factory);
				}
			}			
		}
		return factory;
	}
	
	/**
	 * Returns a <code>IDeserializerFactory</code> for the given root class.
	 * 
	 * @param rootClz The root class of the deserialized object.
	 * @return <code>IDeserializerFactory</code> for the given root class.
	 * @throws BindingException binding exception
	 */
	private static IDeserializerFactory getDeserializerFactory
		(Class<?> rootClz) throws BindingException {
		
		IDeserializerFactory factory = DeserializerFactoryCache.get(rootClz);
		if (factory == null) {
			synchronized (rootClz) {
				factory = DeserializerFactoryCache.get(rootClz);
				if (factory == null) {
					factory = createDeserializerFactory(rootClz);
					DeserializerFactoryCache.put(rootClz, factory);
				}
			}			
		}
		return factory;
	}
	
	private static final Map<Class<?>, ISerializerFactory> SerializerFactoryCache
		= new ConcurrentHashMap<Class<?>, ISerializerFactory>();
	
	private static final Map<Class<?>, IDeserializerFactory> DeserializerFactoryCache
		= new ConcurrentHashMap<Class<?>, IDeserializerFactory>();
	
	private static ISerializerFactory createSerializerFactory
		(final Class<?> rootClz) throws BindingException {
		
		ISerializerFactory factory = new XMLSerializerFactory();
		ISerializerFactory.InitContext sInitCtx = 
			new ISerializerFactory.InitContext() {
				public Map<String, String> getOptions() {
					return Collections.emptyMap();
				}
				@SuppressWarnings("unchecked")
				public Class[] getRootClasses() {
					return new Class[] {rootClz};
				}			
			};
		factory.init(sInitCtx);
		return factory;
	}
	
	private static IDeserializerFactory createDeserializerFactory
		(final Class<?> rootClz) throws BindingException {
		
		IDeserializerFactory factory = new XMLDeserializerFactory();
		IDeserializerFactory.InitContext sInitCtx = 
			new IDeserializerFactory.InitContext() {
				public Map<String, String> getOptions() {
					return Collections.emptyMap();
				}
				@SuppressWarnings("unchecked")
				public Class[] getRootClasses() {
					return new Class[] {rootClz};
				}
				public Schema getUpaAwareMasterSchema() {
					return null;
				};		
			};
		factory.init(sInitCtx);
		return factory;
	}
}
