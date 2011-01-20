/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class NamespaceContextImpl implements NamespaceContext {

	String ns2 = "http://www.ebayopensource.org/turmeric/common/config";
	
	@Override
	public String getNamespaceURI(String arg0) {
		String namespaceURI = null;
		if(arg0.equals("xs"))
			namespaceURI ="http://www.w3.org/2001/XMLSchema";
		else if(arg0.equals("ns2"))
			namespaceURI = ns2;
			
		return namespaceURI;
	}

	@Override
	public String getPrefix(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getPrefixes(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNs2() {
		return ns2;
	}

	public void setNs2(String ns2) {
		this.ns2 = ns2;
	}

}
