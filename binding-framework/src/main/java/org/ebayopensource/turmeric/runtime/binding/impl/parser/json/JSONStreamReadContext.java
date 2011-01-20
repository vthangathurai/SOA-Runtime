/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ParseException;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * @author wdeng
 */
public class JSONStreamReadContext {
	public static final String KEY_VALUE_KEY = "valueKey";

	private JSONLexer m_lexer;
	private NamespaceConvention m_convention;
	private JSONToken m_currentToken;
	private Stack<ObjectNodeImpl> m_nodes;		// The object node that is builting or built.
	private String m_singleNamespace;
	String m_valueKey = BindingConstants.JSON_VALUE_KEY;

	public JSONStreamReadContext(InputStream in, NamespaceConvention convention, Charset charset) {
		this(in, convention, charset, CollectionUtils.EMPTY_STRING_MAP);
	}
	
	public JSONStreamReadContext(InputStream in, NamespaceConvention convention, Charset charset, Map<String, String> options) {
		setupOptions(options);
		m_lexer = new JSONLexer(new InputStreamReader(in, charset));
		m_convention = convention;
		m_singleNamespace = m_convention.getSingleNamespace();
		m_nodes = new Stack<ObjectNodeImpl>();
		getNextToken();
		if (null == m_currentToken) {
			m_currentToken = JSONToken.END_TOKEN;
		}
	}

	public JSONTokenType getCurrentTokenType() {
		if (null == m_currentToken) {
			throw new ParseException(m_lexer.yytext(), m_lexer.getRow(), m_lexer.getColumn(), "Unexpected end of input stream.");
		}
		return m_currentToken.m_type;
	}
	
	public JSONToken getCurrentToken() {
		return m_currentToken;
	}
	
	public QName createQName(String prefix, String localPart, boolean isTopNode) {
		if ((null == prefix || prefix.length() == 0) && m_singleNamespace != null) {
			if (isTopNode) {
				return new QName(m_singleNamespace, localPart);
			}

			return new QName("", localPart);
		}
		
		return JSONStreamWriter.createQName(m_convention, prefix, localPart);
	}
	
	public JSONToken getNextToken() {
		try {
			m_currentToken = m_lexer.yylex();
//			LogManager.getInstance(JSONStreamReadContext.class).log(Level.FINE, "Token read: " + m_currentToken);
			return m_currentToken;
		} catch (IOException ioe) {
			throw new ParseException(m_lexer.yytext(), m_lexer.getRow(), m_lexer.getColumn(), ioe);
		}
	}
	
	public void newNodeCreated(ObjectNodeImpl newNode) {
//		LogManager.getInstance(JSONStreamReadContext.class).log(Level.INFO, "Push: " + newNode  + " to " + m_nodes);
		m_nodes.push(newNode);
	}

	public void nodeBuildingCompleted(ObjectNodeImpl node) {
		ObjectNodeImpl currentNode = m_nodes.pop();
//		LogManager.getInstance(JSONStreamReadContext.class).log(Level.INFO, "Pop: " + node  + " from " + m_nodes);
		assert(currentNode==node);
	}
	
	public void buildValue(JSONStreamObjectNodeImpl caller, QName key, int idxToFind, boolean singleChildPolicyApplied) {
		buildAndFindParent(caller, singleChildPolicyApplied);
		caller.buildValue(key, idxToFind, false);
	}

	
	public void buildNextChild(JSONStreamObjectNodeImpl caller) {
		if (m_currentToken == JSONToken.END_TOKEN) {
			return;
		}
		buildAndFindParent(caller, true);
		caller.buildValue(null, Integer.MAX_VALUE, true);
	}
	
	private void buildAndFindParent(JSONStreamObjectNodeImpl caller, boolean singleChildPolicyApplied) {
		JSONStreamObjectNodeImpl node = (JSONStreamObjectNodeImpl) m_nodes.peek();
		while (node != caller) {
			node.buildValue(null, Integer.MAX_VALUE, false);
			node.nodeBuildingCompleted(singleChildPolicyApplied);
			if (m_nodes.empty()) {
				break;
			}
			node = (JSONStreamObjectNodeImpl) m_nodes.peek();
		}
	}

	private void setupOptions(Map<String, String>options) {
		String valueKey = options.get(KEY_VALUE_KEY);
		if (null != valueKey && valueKey.length() > 0) {
			m_valueKey = valueKey;
		}
	}
}
