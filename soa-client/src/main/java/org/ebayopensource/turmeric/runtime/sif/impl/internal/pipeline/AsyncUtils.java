/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;


public class AsyncUtils {

	static Map<String, Object> extractContext(Message inboundMessage)
			throws ServiceException {
		Map<String, Object> context = new HashMap<String, Object>();

		Map<String, String> transportHeaders = inboundMessage
				.getTransportHeaders();
		Cookie[] cookies = inboundMessage.getCookies();

		Collection<ObjectNode> messageHeaders = inboundMessage
				.getMessageHeaders();

		Collection<Object> messageHeadersAsJavaObject = null;
		if (inboundMessage instanceof InboundMessage)
			messageHeadersAsJavaObject = ((InboundMessage) inboundMessage)
					.getMessageHeadersAsJavaObject();

		byte[] payloadData = ((InboundMessage) inboundMessage)
				.getRecordedData();
		if (transportHeaders != null)
			context.putAll(transportHeaders);

		for (Cookie cookie : cookies) {
			context.put(cookie.getName(), cookie);
		}

		if (payloadData != null)
			context.put(ResponseContext.PAYLOAD, payloadData);

		if (messageHeaders != null)
			context.put(ResponseContext.MESSAGE_HEADERS, Collections
					.unmodifiableCollection(messageHeaders));

		if (messageHeadersAsJavaObject != null)
			context
					.put(
							ResponseContext.MESSAGE_HEADERS_AS_JAVA_OBJECT,
							Collections
									.unmodifiableCollection(messageHeadersAsJavaObject));

		return context;
	}
}
