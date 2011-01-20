/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.util.ArrayList;

import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;


/**
 * @author ichernyshev
 */
public final class MessageContextAccessorImpl extends MessageContextAccessor {

	private final ThreadLocal<ArrayList<MessageContext>> m_context = new ThreadLocal<ArrayList<MessageContext>>();

	private MessageContextAccessorImpl() {
		// no instances
	}

	private static MessageContextAccessorImpl getImpl() {
		return (MessageContextAccessorImpl)getInstance();
	}

	public static void blockPreviousContext() {
		setContextInt(null);
	}

	public static void setContext(MessageContext ctx) {
		if (ctx == null) {
			throw new NullPointerException();
		}

		setContextInt(ctx);
	}

	private static void setContextInt(MessageContext ctx) {
		ArrayList<MessageContext> stack = getImpl().m_context.get();
		if (stack == null) {
			stack = new ArrayList<MessageContext>();
			getImpl().m_context.set(stack);
		}

		stack.add(ctx);
	}

	public static void resetContext() {
		ArrayList<MessageContext> stack = getImpl().m_context.get();
		if (stack == null || stack.isEmpty()) {
			return;
		}

		stack.remove(stack.size() - 1);
	}

	@Override
	protected MessageContext getContextInternal() {
		ArrayList<MessageContext> stack = getImpl().m_context.get();
		if (stack == null || stack.isEmpty()) {
			return null;
		}

		return stack.get(stack.size() - 1);
	}

	static {
		MessageContextAccessor.setInstance(new MessageContextAccessorImpl());
	}
}
