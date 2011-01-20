/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

import com.ibm.wsdl.DefinitionImpl;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.ibm.wsdl.xml.WSDLWriterImpl;


/**
 * SOA Tools specific implementation of javax.wsdl.factory.WSDLFactory
 * 
 * @author rmandapati@ebay.com
 */
public class WSDLFactoryImpl extends WSDLFactory {
	
	static class  SOAWSDLReaderImpl  extends WSDLReaderImpl{
		WSDLFactoryImpl factoryImpl;
		
		protected WSDLFactory getWSDLFactory() throws WSDLException
		  {
			if (factoryImpl == null)
		       factoryImpl = new WSDLFactoryImpl();

			return factoryImpl;
		  }
	};
	
    
    public WSDLFactoryImpl() {
    }

    public Definition newDefinition() {
        Definition def = new DefinitionImpl();
        def.setExtensionRegistry(newPopulatedExtensionRegistry());
        return def;
    }

    public WSDLReader newWSDLReader() {
         WSDLReaderImpl reader = new SOAWSDLReaderImpl();
        reader.setFactoryImplName(this.getClass().getName());
        reader.setExtensionRegistry(newPopulatedExtensionRegistry());
        return reader;
    }

    public WSDLWriter newWSDLWriter() {
        WSDLWriterImpl writer = new WSDLWriterImpl();
        return writer;
    }

    public ExtensionRegistry newPopulatedExtensionRegistry() {
        ExtensionRegistry extReg = 
        		new com.ibm.wsdl.extensions.PopulatedExtensionRegistry();
         return extReg;
    }
}
