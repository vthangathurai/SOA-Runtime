/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * The data binding description (DataBindingDesc) is loaded at service initialization
 * as part of ServiceDesc creation.  DataBindingDesc contains most of the pre-built data 
 * structures necessary to operate serialization and deserialization.
 * 
 * The data includes the Serializer/DeserializerFactory for the data binding, the name and MIME type
 * and the Payload type for the data binding.
 * 
 * Name: the name of the data binding as it is defined in the configuration file;
 * MIME type: the MIME type of the payload
 * Payload type:  the payload type defined in the Serializer/DeserializerFactory. 
 * 
 * @author ichernyshev
 */
public final class DataBindingDesc {

	private final String m_name;
	private final String m_mimeType;
	private final String m_payloadType;
	private final SerializerFactory m_serializerFactory;
	private final DeserializerFactory m_deserializerFactory;
	private final Map<String,Serializer> m_customSerializers;
	private final Map<String,Deserializer> m_customDeserializers;
	private final Map<String,TypeConverter<?,?>> m_typeConvertersByBoundType;
	private final Map<String,TypeConverter<?,?>> m_typeConvertersByValueType;
	private final Collection<Class> m_valueTypeList;

	/**
	 * Constructor; It is used in Service initialization as part of the ServiceDesc creation
	 * process.
	 * 
	 * @param name the name of this data binding
	 * @param mimeType the MIME type to be used when representing this data binding's content type.
	 * @param serializerFactory the Serializer factory instance supporting serialization of this data binding.
	 * @param deserializerFactory the Deserializer factory instance supporting serialization of this data binding.
	 * @param customSerializers a map of custom serializers keyed by the Java type name.  (Not currently used.)
	 * @param customDeserializers a map of custom deserializers keyed by the Java type name.  (Not currently used.)
	 * @param typeConvertersByBoundType a map of type converters keyed by the bound (programming side) Java type name.
	 * @param typeConvertersByValueType a map of type converters keyed by the bound (programming side) Java type name.
	 */

public DataBindingDesc(String name,
		String mimeType,
		SerializerFactory serializerFactory,
		DeserializerFactory deserializerFactory,
		Map<String,Serializer> customSerializers,
		Map<String,Deserializer> customDeserializers,
		Map<String,TypeConverter<?,?>> typeConvertersByBoundType,
		Map<String,TypeConverter<?,?>> typeConvertersByValueType)
	{
		if (name == null || mimeType == null || serializerFactory == null ||
			deserializerFactory == null)
		{
			throw new NullPointerException();
		}

		m_name = name;
		m_mimeType = mimeType;
		m_serializerFactory = serializerFactory;
		m_deserializerFactory = deserializerFactory;
		m_customSerializers = customSerializers;
		m_customDeserializers = customDeserializers;
		m_typeConvertersByBoundType = typeConvertersByBoundType;
		m_typeConvertersByValueType = typeConvertersByValueType;

		m_payloadType = serializerFactory.getPayloadType();

		// payload types have to be the same for both serializer and deserializer
		if (m_payloadType != deserializerFactory.getPayloadType()) {
			throw new IllegalStateException("Payload mismatch in " + name);
		}
		
		m_valueTypeList = buildValueTypeClassList();
	}

	/**
	 * Returns the data binding name.
	 * @return the data binding name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Returns the payload type.
	 * @return the payload type
	 */
	public String getPayloadType() {
		return m_payloadType;
	}

	/**
	 * Returns the MIME type corresponding to the payload type (as configured in the
	 * data binding configuration).
	 * @return the MIME type
	 */
	public String getMimeType() {
		return m_mimeType;
	}

	/**
	 * Returns the serializer factory associated with this payload type.
	 * @return the serializer factory
	 */
	public SerializerFactory getSerializerFactory() {
		return m_serializerFactory;
	}

	/**
	 * This function is not supported given the current serialization implementation.
	 * Returns the deserializer factory associated with this payload type.
	 * @return the deserializer factory
	 */
	public DeserializerFactory getDeserializerFactory() {
		return m_deserializerFactory;
	}

	/**
	 * This function is not supported given the current serialization implementation.
	 * Returns the custom serializer for the specified Java type.
	 * @param type the java type
	 * @return the custom serializer
	 */
	public Serializer getCustomSerializer(String type) {
		if (m_customSerializers == null) {
			return null;
		}

		return m_customSerializers.get(type);
	}

	/**
	 * This function is not supported given the current serialization implementation.
	 * Returns the custom deserializer for the specified Java type.
	 * @param type the java type
	 * @return the custom deserializer
	 */
	public Deserializer getCustomDeserializer(String type) {
		if (m_customDeserializers == null) {
			return null;
		}

		return m_customDeserializers.get(type);
	}

	/**
	 * Returns the type converter associated with the specified "convert to" type.
	 * @param type the target "convert to" type associated with the converter.
	 * @return the type converter associated with the specified "convert to" type.
	 */
	public TypeConverter<?,?> getConverterForBoundType(String type) {
		if (m_typeConvertersByBoundType == null) {
			return null;
		}

		return m_typeConvertersByBoundType.get(type);
	}

	/**
	 * Returns the type converter associated with the specified "convert from" type.
	 * @param type the source "convert from" type associated with the converter.
	 * @return the type converter associated with the specified "convert from" type.
	 */
	public TypeConverter<?,?> getConverterForValueType(String type) {
		if (m_typeConvertersByValueType == null) {
			return null;
		}

		return m_typeConvertersByValueType.get(type);
	}

	/**
	 * Get the complete collection of type converter bound types for this data binding.
	 * @return the collection of bound type names
	 */
	public Collection<String> getAllTypeConverterBoundTypes() {
		if (m_typeConvertersByBoundType == null) {
			return CollectionUtils.EMPTY_STRING_SET;
		}

		return Collections.unmodifiableCollection(m_typeConvertersByBoundType.keySet());
	}

	/**
	 * Get the complete collection of type converter value types for this data binding.
	 * @return the collection of value type names
	 */
	public Collection<Class> getAllTypeConverterValueTypes() {
		return m_valueTypeList;
	}

	private Collection<Class> buildValueTypeClassList() {
		if (null == m_typeConvertersByValueType) {
			return CollectionUtils.EMPTY_CLASS_LIST;
		} 
		
		Collection<TypeConverter<?,?>> converters =
			m_typeConvertersByValueType.values();
		ArrayList<Class> valueTypeList = new ArrayList<Class>(converters.size());
		for (Iterator<TypeConverter<?,?>> iter=converters.iterator(); iter.hasNext();) {
			TypeConverter converter = iter.next();
			valueTypeList.add(converter.getValueType());
		}
		return Collections.unmodifiableCollection(valueTypeList);
	}
}
