/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.schema;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaComplexTypeImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaElementDeclImpl;
import org.ebayopensource.turmeric.tools.codegen.exception.BrokenSchemaException;
import org.ebayopensource.turmeric.tools.codegen.exception.UnsupportedSchemaException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.internalizer.DOMForestScanner;
import com.sun.tools.xjc.reader.internalizer.SCDBasedBindingSet;
import com.sun.tools.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * Helper class allowing to parse the schema
 *
 * This class uses XSOM schema parser since it's a part of Sun JAXB
 * implementation, while Xerces XS is an unofficial part of JDK.
 *
 * See BaseTypeDefsBuilder JavaDoc for discussion on code model vs schema
 *
 * The use of XJC plugins for accessing the schema is limited by two reasons:
 * 1. Plugin provides access to XSOM schema only if it was read from a schema file
 * 2. Use of plugins in Axis->JAXB mode is questionable
 *
 * @author ichernyshev
 */
public class XSOMFlatSchemaLoader extends FlatSchemaLoader {

	private XSSchemaSet m_schemaSet;
	private NodeList m_Imports;
	private XSType m_anyType;
	private List<FlatSchemaComplexTypeHolder> m_complexTypes =
		new ArrayList<FlatSchemaComplexTypeHolder>();
	private Map<QName,FlatSchemaElementDeclImpl> m_rootElements =
		new HashMap<QName,FlatSchemaElementDeclImpl>();
	private List<String> processedElementList  
	= new ArrayList<String>();
	private boolean processingLoadFreeElements = false;
	private static final String  IMPORT_NAMESPACE = "namespace";
	private static final String IMPORT_SCHEMALOC = "schemaLocation";

	public XSOMFlatSchemaLoader() {
	}

	@Override
	protected void init(String uri) throws UnsupportedSchemaException {
	}

	@Override
	protected void parseSchemaRootElements(List<Element> schemas,NodeList imports) throws UnsupportedSchemaException {
		try {
			m_Imports=imports;
			DOMForest forest = createDomForest(null, null);
			XSOMParser parser = createParser(forest);
			m_schemaSet = parseSchemaElements(parser, forest, schemas);

			if (m_schemaSet != null) {
				m_anyType = m_schemaSet.getAnyType();
			}
		} catch (SAXException e) {
			throw new BrokenSchemaException(e.toString(), e);
		}
	}

	@Override
	protected void parseSchemaFromFiles(File[] files) throws UnsupportedSchemaException {
		List<InputSource> sources = new ArrayList<InputSource>();
		for (int i=0; i<files.length; i++) {
			String fileName;
			try {
				fileName = files[i].getCanonicalPath();
			} catch (IOException ioe) {
				throw new UnsupportedSchemaException("Unable to canonicalize file name '" +
						files[i].getPath() + "'", ioe);
			}
			fileName = fileName.replace('\\', '/');

			InputSource src = new InputSource();
			if (fileName.length() > 0 && fileName.charAt(0) == '/') {
				src.setSystemId("file:" + fileName);
			} else {
				src.setSystemId("file:/" + fileName);
			}
			sources.add(src);
		}

		try {
			DOMForest forest = createDomForest(null, sources);
			XSOMParser parser = createParser(forest);
			m_schemaSet = parseForest(parser, forest);

			if (m_schemaSet != null) {
				m_anyType = m_schemaSet.getAnyType();
			}
		} catch (SAXException e) {
			throw new BrokenSchemaException(e.toString(), e);
		}
	}

	private DOMForest createDomForest(EntityResolver entityResolver, List<InputSource> files)
	throws SAXException, UnsupportedSchemaException
	{
		ErrorReceiver errorReceiver = getErrorReceiver();

		// parse into DOM forest
		DOMForest forest = new DOMForest(new XMLSchemaInternalizationLogic());

		forest.setErrorHandler(errorReceiver);

		if (entityResolver != null) {
			forest.setEntityResolver(entityResolver);
		}

		// parse source grammars
		//for (InputSource value : opt.getGrammars()) {
		//	errorReceiver.pollAbort();
		//	forest.parse(value, true);
		//}

		// parse external binding files
		if (files != null) {
			for (InputSource value : files) {
				errorReceiver.pollAbort();
				forest.parse(value, true);
			}
		}

		return forest;
	}
	private class DependeciesEntityResolver implements EntityResolver
	{

		public InputSource resolveEntity(String publicId, String systemId)
		throws SAXException, IOException {
			for (int i=0; i<m_Imports.getLength(); i++)
			{
				//systemId populated is not correct hence need to resolve
				Element impElem = (Element)m_Imports.item(i);
				NamedNodeMap nodeMap = impElem.getAttributes();
				if(nodeMap.getNamedItem(IMPORT_NAMESPACE).getNodeValue().equals(publicId)&& nodeMap.getNamedItem(IMPORT_SCHEMALOC)!=null)
					return new InputSource(nodeMap.getNamedItem(IMPORT_SCHEMALOC).getNodeValue());
			}
			return null;
		}
	}

	private XSOMParser createParser(DOMForest forest)
	throws SAXException
	{
		// parse into DOM forest
		XSOMParser parser = new XSOMParser(forest.createParser());
		//parser.setAnnotationParser(new AnnotationParserFactoryImpl(opt));
		parser.setErrorHandler(getErrorReceiver());

		EntityResolver entityResolver = parser.getEntityResolver();
		if (entityResolver != null) {
			parser.setEntityResolver(entityResolver);
		}
		else
		{
			parser.setEntityResolver(new DependeciesEntityResolver());
		}

		return parser;
	}

	private XSSchemaSet parseSchemaElements(XSOMParser parser, final DOMForest forest, List<Element> schemas)
	throws SAXException
	{
		ErrorReceiver errorReceiver = getErrorReceiver();

		final EntityResolver entityResolver = parser.getEntityResolver();

		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				// DOMForest only parses documents that are rearchable through systemIds,
				// and it won't pick up references like <xs:import namespace="..." /> without
				// @schemaLocation. So we still need to use an entity resolver here to resolve
				// these references, yet we don't want to just run them blindly, since if we do that
				// DOMForestParser always get the translated system ID when catalog is used
				// (where DOMForest records trees with their original system IDs.)
				if (systemId != null && forest.get(systemId) != null) {
					return new InputSource(systemId);
				}

				if (entityResolver != null) {
					return entityResolver.resolveEntity(publicId,systemId);
				}

				return null;
			}
		});

		DOMForestScanner scanner = new DOMForestScanner(forest);
		for (Element schema: schemas) {
			scanner.scan(schema, parser.getParserHandler());
		}

		XSSchemaSet result = parser.getResult();
		if (result == null) {
			// error should be added already
			return null;
		}

		SCDBasedBindingSet scdBasedBindingSet = forest.transform(true);
		scdBasedBindingSet.apply(result, errorReceiver);

		return result;
	}

	public XSSchemaSet parseForest(XSOMParser parser, DOMForest forest)
	throws SAXException
	{
		ErrorReceiver errorReceiver = getErrorReceiver();

		for (String systemId: forest.getRootDocuments()) {
			errorReceiver.pollAbort();
			parser.parse(systemId);
		}

		XSSchemaSet result = parser.getResult();
		if (result == null) {
			// error should be added already
			return null;
		}

		SCDBasedBindingSet scdBasedBindingSet = forest.transform(true);
		scdBasedBindingSet.apply(result, errorReceiver);

		return result;
	}

	@Override
	protected void load() throws UnsupportedSchemaException {
		Collection<XSSchema> schemas = m_schemaSet.getSchemas();
		for (XSSchema schema: schemas) {
			// first load all complex type hierarchies
			for (XSComplexType complexType: schema.getComplexTypes().values()) {
				if (complexType == m_anyType) {
					// ignore "ANY" type
					continue;
				}

				if (complexType.getRedefinedBy() != null) {
					// ignore type that had been redefined
					continue;
				}

				loadComplexType(complexType);
			}
		}

		for (XSSchema schema: schemas) {
			// load free-standing element declarations
			processingLoadFreeElements = true;
			for (XSElementDecl elementDecl: schema.getElementDecls().values()) {
				QName elementName = getQName(elementDecl);
				processedElementList.clear();//has to be cleared for each call to this load method
				//System.out.println("Found " + elementName + " - "  +
				//elementDecl.getType().getClass().getName() + " - " +
				//elementDecl.getType().getName());

				FlatSchemaElementDeclImpl element = expandElemDecl(elementDecl, 1, null);
				if(element != null)
					m_rootElements.put(elementName, element);
			}
			processedElementList.clear();//has to be cleared for the last set
			processingLoadFreeElements = false;
		}

		// expand actual elements
		// iterate over a copy as the original array will keep growing
		// new anonymous elements in the array will be expanded on the fly
		List<FlatSchemaComplexTypeHolder> complexTypes2 =
			new ArrayList<FlatSchemaComplexTypeHolder>(m_complexTypes);
		for (FlatSchemaComplexTypeHolder holder: complexTypes2) {
			expandElements(holder);
		}
	}

	@Override
	public List<FlatSchemaComplexTypeImpl> getComplexTypes() {
		List<FlatSchemaComplexTypeImpl> result =
			new ArrayList<FlatSchemaComplexTypeImpl>(m_complexTypes.size());
		for (FlatSchemaComplexTypeHolder holder: m_complexTypes) {
			result.add(holder.m_type);
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public Map<QName,FlatSchemaElementDeclImpl> getRootElements() {
		/*Map<QName,FlatSchemaElementDeclImpl> result =
			new HashMap<QName,FlatSchemaElementDeclImpl>(m_rootElements.size());
		for (Map.Entry<QName,FlatSchemaElementDeclImpl> e: m_rootElements.entrySet()) {
			QName elementName = e.getKey();
			FlatSchemaElementDeclImpl holder = e.getValue();
			result.put(elementName, holder.m_type);
		}*/
		return Collections.unmodifiableMap(m_rootElements);
	}

	private QName getQName(XSDeclaration decl) {
		String typeNS = decl.getTargetNamespace();
		String typeLocalName = decl.getName();
		return new QName(typeNS, typeLocalName);
	}

	private FlatSchemaComplexTypeHolder findComplexTypeHolder(XSComplexType complexType) {
		for (int i=0; i<m_complexTypes.size(); i++) {
			FlatSchemaComplexTypeHolder result = m_complexTypes.get(i);
			if (result.m_xsType == complexType) {
				return result;
			}
		}

		return null;
	}
	private FlatSchemaComplexTypeHolder loadComplexType(XSComplexType complexType)
	throws UnsupportedSchemaException
	{
		QName typeName = getQName(complexType);

		FlatSchemaComplexTypeHolder result = findComplexTypeHolder(complexType);
		if (result != null) {
			return result;
		}

//		if (complexType.isMixed()) {
//		throw new UnsupportedSchemaException("Mixed complex type '" + typeName +
//		"' is not supported");
//		}

		XSType baseType = complexType.getBaseType();

		if (baseType == m_anyType) {
			// it's a flat type, load it first
			result = loadFreshComplexType(complexType, typeName);
			m_complexTypes.add(result);
			return result;
		}

		if (baseType.isSimpleType()) {
			// it's a flat type, load it first
			result = loadFreshComplexType(complexType, typeName);
			m_complexTypes.add(result);
			return result;
		}

		if (!baseType.isComplexType()) {
			throw new UnsupportedSchemaException("Complex type '" + typeName +
			"' has to be fresh or derived from another complex type or simple type");
		}

		// load base type first
		XSComplexType baseComplexType = (XSComplexType)baseType;
		FlatSchemaComplexTypeHolder flatBaseType = loadComplexType(baseComplexType);

		// load our complex type
		if (complexType.getDerivationMethod() == XSType.EXTENSION) {
			result = loadExtendedComplexType(complexType, typeName, flatBaseType);
		} else if (complexType.getDerivationMethod() == XSType.RESTRICTION) {
			// flat restriction should be just like fresh type
			result = loadFreshComplexType(complexType, typeName);
		} else {
			throw new UnsupportedSchemaException("Complex type '" + typeName +
					"' uses unsupported derivation method, " +
			"only EXTENSION and RESTRICTION are supported");
		}

		m_complexTypes.add(result);
		return result;
	}

	private FlatSchemaComplexTypeHolder loadFreshComplexType(XSComplexType complexType, QName typeName)
	throws UnsupportedSchemaException
	{
		FlatSchemaComplexTypeImpl flatType = new FlatSchemaComplexTypeImpl(typeName);
		FlatSchemaComplexTypeHolder result = new FlatSchemaComplexTypeHolder(
				flatType, complexType, typeName.toString(), null);
		return result;
	}

	private FlatSchemaComplexTypeHolder loadExtendedComplexType(XSComplexType complexType, QName typeName,
			FlatSchemaComplexTypeHolder flatBaseType) throws UnsupportedSchemaException
			{
		FlatSchemaComplexTypeImpl flatType = new FlatSchemaComplexTypeImpl(typeName);
		FlatSchemaComplexTypeHolder result = new FlatSchemaComplexTypeHolder(
				flatType, complexType, typeName.toString(), flatBaseType);
		return result;
			}

	private void expandElements(FlatSchemaComplexTypeHolder holder) throws UnsupportedSchemaException {
		if (holder.m_expandedElements) {
			return;
		}

		if (holder.m_baseType != null) {
			// this is extension case

			// expand base types
			expandElements(holder.m_baseType);

			if(holder.m_baseType.m_type == null ||
					holder.m_type == null)
				return;

			// add all elements from base types
			List<FlatSchemaElementDeclImpl> elements = holder.m_baseType.m_type.getElements();
			holder.m_type.addElements(elements);

			// add our own elements
			expandAttributes(holder);
			expandExtensionElements(holder);
		} else {
			expandAttributes(holder);
			expandFreshComplexTypeElements(holder);
		}

		holder.m_expandedElements = true;
	}

	private void expandFreshComplexTypeElements(final FlatSchemaComplexTypeHolder holder)
	throws UnsupportedSchemaException
	{
		if(holder.m_xsType == null)
			return;

		XSContentType contentType = holder.m_xsType.getContentType();

		XSParticle particle = contentType.asParticle();
		if (particle != null) {
			expandParticleElements(holder, particle);
			return;
		}

		XSSimpleType simpleType = contentType.asSimpleType();
		if (simpleType != null) {
			expandSimpleTypeElement(holder, simpleType);
			return;
		}

		if (contentType.asEmpty() != null) {
			return;
		}

		throw new UnsupportedSchemaException("Complex type '" + holder.m_friendlyName +
				"' declares unsupported content type " + contentType.getClass().getName());
	}

	private void expandExtensionElements(FlatSchemaComplexTypeHolder holder) throws UnsupportedSchemaException {
		if(holder.m_xsType == null)
			return;

		XSContentType explicitContent = holder.m_xsType.getExplicitContent();
		if (explicitContent == null) {
			// empty type, no further processing is needed
			return;
		}

		XSParticle particle = explicitContent.asParticle();
		if (particle == null) {
			// empty type, no further processing is needed
			return;
		}

		expandParticleElements(holder, particle);
	}

	private void expandSimpleTypeElement(FlatSchemaComplexTypeHolder holder, XSSimpleType simpleType)
	throws UnsupportedSchemaException
	{
		// nothis to expand, leave complex type empty
		//throw new UnsupportedSchemaException("Complex type '" + holder.m_friendlyName +
		//	"' declares unsupported SIMPLE content type");
	}

	private void expandParticleElements(FlatSchemaComplexTypeHolder holder, XSParticle particle)
	throws UnsupportedSchemaException
	{
		int maxOccurs = particle.getMaxOccurs();
		if (maxOccurs == XSParticle.UNBOUNDED) {
			maxOccurs = FlatSchemaElementDeclImpl.UNBOUNDED;
		}

		XSTerm term = particle.getTerm();

		XSElementDecl elemDecl = term.asElementDecl();
		if (elemDecl != null) {
			expandElemDecl(elemDecl, maxOccurs, holder.m_type);
			return;
		}

		XSModelGroupDecl modelGroupDecl = term.asModelGroupDecl();
		if (modelGroupDecl != null) {
			XSModelGroup modelGroup = modelGroupDecl.getModelGroup();
			expandModelGroup(holder, modelGroup, maxOccurs);
			return;
		}

		XSModelGroup modelGroup = term.asModelGroup();
		if (modelGroup != null) {
			expandModelGroup(holder, modelGroup, maxOccurs);
			return;
		}

		XSWildcard wildcard = term.asWildcard();
		if (wildcard != null) {
			// ignore 'ANY' type at the end

			//if (wildcard instanceof XSWildcard.Any) {
			//	return;
			//}

			//throw new UnsupportedSchemaException("Complex type '" + holder.m_friendlyName +
			//	"' declares unsupported WILDCARD particle type '" + wildcard.getClass().getName() + "'");
			return;
		}
	}

	private FlatSchemaElementDeclImpl expandElemDecl(XSElementDecl elemDecl, int maxOccurs,
			FlatSchemaComplexTypeImpl enclosingType) throws UnsupportedSchemaException
			{
		XSType type = elemDecl.getType();

		// the namespace of an element is that of the enclosing type
		QName elementName = getQName(elemDecl);

		//verify if the element has already been processed, if so return.This helps in avoiding recusrsion of types which refer to each other / subset of each other
		if (processingLoadFreeElements) {
			String elementNameStr = elementName.toString();
			if (processedElementList.contains(elementNameStr))
				return null;
			else
				processedElementList.add(elementNameStr);
		}

		XSSimpleType simpleType = type.asSimpleType();
		if (simpleType != null) {
			FlatSchemaElementDeclImpl result;
			if (enclosingType != null) {
				result = enclosingType.addSimpleElement(elementName, maxOccurs);
			} else {
				result = FlatSchemaElementDeclImpl.createRootSimpleElement(elementName);
			}

			return result;
		}

		XSComplexType complexType = type.asComplexType();

		if (complexType == m_anyType) {
			FlatSchemaElementDeclImpl result;
			if (enclosingType != null) {
				result = enclosingType.addAnyElement(elementName, maxOccurs);
			} else {
				result = FlatSchemaElementDeclImpl.createRootAnyElement(elementName);
			}

			return result;
		}

		if (complexType != null) {
			FlatSchemaComplexTypeHolder otherHolder;
			if (complexType.getName() != null) {
				// named complex type reference
				otherHolder = findComplexTypeHolder(complexType);
				if (otherHolder == null) {
					throw new UnsupportedSchemaException("Internal error in element '" +
							elementName + "': unable to find referenced complex type '" +
							getQName(complexType) + "'");
				}
			} else {
				// unnamed complex type embedded in element
				otherHolder = buildAnonimousComplexType(complexType, elementName);
			}

			FlatSchemaElementDeclImpl result;
			if (enclosingType != null) {
				result = enclosingType.addComplexElement(elementName, otherHolder.m_type, maxOccurs);
			} else {
				result = FlatSchemaElementDeclImpl.createRootComplexElement(elementName, otherHolder.m_type);
			}

			return result;
		}

		throw new UnsupportedSchemaException("Unknown element type '" + type.getClass().getName() +
				"' in element '" + elementName + "'");
			}

	private void expandModelGroup(FlatSchemaComplexTypeHolder holder, XSModelGroup modelGroup,
			int maxOccurs) throws UnsupportedSchemaException
			{
		for (int i=0; i<modelGroup.getSize(); i++) {
			XSParticle particle = modelGroup.getChild(i);
			expandParticleElements(holder, particle);
		}

		//if (modelGroup.getCompositor() == XSModelGroup.SEQUENCE) {
		//	return;
		//}

		//throw new UnsupportedSchemaException("Complex type '" + holder.m_friendlyName +
		//	"' declares unsupported model group type '" + modelGroup.getCompositor() + "'");
			}

	private FlatSchemaComplexTypeHolder buildAnonimousComplexType(
			XSComplexType complexType, QName elementName) throws UnsupportedSchemaException
			{
		FlatSchemaComplexTypeImpl flatType = new FlatSchemaComplexTypeImpl();
		FlatSchemaComplexTypeHolder result = new FlatSchemaComplexTypeHolder(
				flatType, complexType, elementName.toString(), null);
		expandElements(result);
		m_complexTypes.add(result);
		return result;
			}

	private void expandAttributes(FlatSchemaComplexTypeHolder holder) {
		if(holder.m_xsType == null)
			return;

		XSAttContainer attrs = holder.m_xsType;


		Collection<? extends XSAttributeUse> attrUses = attrs.getDeclaredAttributeUses();
		for (XSAttributeUse attrUse: attrUses) {
			XSAttributeDecl attrDecl = attrUse.getDecl();
			QName attrName = getQName(attrDecl);
			if(holder.m_type != null)
				holder.m_type.addAttribute(attrName);
		}
	}

	private static class FlatSchemaComplexTypeHolder {
		final FlatSchemaComplexTypeImpl m_type;
		final XSComplexType m_xsType;
		final String m_friendlyName;
		final FlatSchemaComplexTypeHolder m_baseType;
		boolean m_expandedElements;

		FlatSchemaComplexTypeHolder(FlatSchemaComplexTypeImpl type, XSComplexType xsType,
				String friendlyName, FlatSchemaComplexTypeHolder baseType)
				{
			if (type == null || xsType == null || friendlyName == null) {
				throw new NullPointerException();
			}

			m_type = type;
			m_xsType = xsType;
			m_friendlyName = friendlyName;
			m_baseType = baseType;
				}
	}
}
