/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.TypeConverter;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author wdeng
 *
 */
public class TypeConversionAdapter extends XmlAdapter<Object, Object> {
	
    private static ThreadLocal<MessageContext> s_threadContext 
    		= new ThreadLocal<MessageContext>();

    public TypeConversionAdapter() {
    	// local instance
    }
    
	public static void setMessageContext(MessageContext ctx) {
		s_threadContext.set(ctx);
	}
	
	public static MessageContext getMessageContext() {
		return s_threadContext.get();

	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object marshal(Object v) throws Exception {
		if (null == v) {
			return null;
		}
		
		TypeConverter converter = getTypeConverter(Direction.Outbound, v.getClass());
		if (null == converter) {
			return null;
		}
		Class<?> expectedType = converter.getBoundType();
		if (!expectedType.isAssignableFrom(v.getClass())) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_UNEXPECTED_BOUND_TYPE, 
					ErrorConstants.ERRORDOMAIN, new Object[] {expectedType, v.getClass()}));
		}
		return converter.preSerializationConvert(getMessageContext(), v);
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(Object v) throws Exception {
		if (null == v) {
			return null;
		}
		TypeConverter converter = getTypeConverter(Direction.Inbound, v.getClass());
		if (null == converter) {
			return null;
		}
		Class<?> expectedType = converter.getValueType();
		if (!expectedType.isAssignableFrom(v.getClass())) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_UNEXPECTED_VALUE_TYPE, 
					ErrorConstants.ERRORDOMAIN, new Object[] {expectedType, v.getClass()}));
		}
		return converter.postDeserializationConvert(getMessageContext(), v);
	}

	private TypeConverter getTypeConverter(Direction direction, Class type) throws ServiceException {
		MessageContext ctx = getMessageContext();
		Message msg = ctx.getRequestMessage();
		if (direction == Direction.Inbound && msg instanceof OutboundMessage) {
			msg = ctx.getResponseMessage(); 
		}


		if (direction == Direction.Outbound && msg instanceof InboundMessage) {
			msg = ctx.getResponseMessage(); 
		}
		DataBindingDesc dbDesc = msg.getDataBindingDesc();
		if (direction == Direction.Inbound) {

			return dbDesc.getConverterForValueType(type.getName());
		}
		return dbDesc.getConverterForBoundType(type.getName());
	}
	
	private enum Direction {Inbound, Outbound}
}
