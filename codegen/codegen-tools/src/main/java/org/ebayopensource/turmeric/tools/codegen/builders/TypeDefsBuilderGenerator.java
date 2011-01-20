/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.BaseTypeDefsBuilder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaComplexTypeImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaElementDeclImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAFrameworkCommonTypeDefsBuilder;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.CodeGenInfoFinder;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.UnsupportedSchemaException;
import org.ebayopensource.turmeric.tools.codegen.schema.FlatSchemaLoader;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * Generates TypeDefs.java based on the XSD or WSDL
 *
 * @author ichernyshev
 */
public class TypeDefsBuilderGenerator extends BaseCodeGenerator implements SourceGenerator {

	private JCodeModel m_codeModel;
	private JDefinedClass m_targetClass;
	private Map<String,JFieldVar> m_namespaces = new HashMap<String,JFieldVar>();

	public static TypeDefsBuilderGenerator getInstance() {
		return new TypeDefsBuilderGenerator();
	}

	public boolean continueOnError() {
		return true;
	}

	public void generate(CodeGenContext codeGenCtx) throws CodeGenFailedException {
		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		FlatSchemaLoader loader = null;
		Set<Class<?>> allTypes = jTypeTable.getTypesReferred();
		if (allTypes != null && !allTypes.isEmpty()) {
			// If it is not a wsdl based service, try to find the schema file first, if
			// schema file is not found, go find the wsdl file.
			if (!inputOptions.isWSDLBasedService()) {
				String schemaDir = XMLSchemaGenerator.getSchemaFileDir(codeGenCtx);
				File schemaDirFile = new File(schemaDir);
				schemaDirFile = schemaDirFile.getAbsoluteFile();

				File[] files = schemaDirFile.listFiles();
				if (files != null && files.length != 0) {
					try {
						loader = FlatSchemaLoader.createInstanceFromXsd(files);
					} catch (UnsupportedSchemaException e) {
						throw new CodeGenFailedException("Unable to parse XSD files in '" +
							schemaDirFile.getPath() + "': " + e.toString(), e);
					}
				}
			}

			if (loader == null) {
				String wsdlFilename = codeGenCtx.getWSDLURI();
				try {
					if (null == wsdlFilename) {
						String metaSrcDestLoc = inputOptions.getMetaSrcDestLocation();
						String destLocation   = inputOptions.getDestLocation();
						if (CodeGenUtil.isEmptyString(destLocation))
							destLocation = "."; //defaulting

						if (CodeGenUtil.isEmptyString(metaSrcDestLoc)) {
							// set it to default
							String metaSrc = inputOptions.getOriginalInputType().equals(InputType.WSDL) ? CodeGenConstants.META_SRC_FOLDER : CodeGenConstants.GEN_META_SRC_FOLDER;
							metaSrcDestLoc = CodeGenUtil.genDestFolderPath(destLocation, metaSrc);
							//defaulting to the current directory if values for options -mdest and -dest are null(or empty).
						}

						wsdlFilename  =  CodeGenUtil.toOSFilePath(metaSrcDestLoc);
						wsdlFilename += CodeGenInfoFinder.getPathforNonModifiableArtifact(inputOptions.getServiceAdminName(), "WSDL");
					}
					loader = FlatSchemaLoader.createInstanceFromWsdl(wsdlFilename);
				} catch (BadInputValueException e) {
					// No-op.  This is never thrown.
				}
				catch (UnsupportedSchemaException e) {
					throw new CodeGenFailedException("Unable to parse WSDL '" + wsdlFilename +
						"': " + e.toString(), e);
				}
			}
		} else {
			loader = FlatSchemaLoader.createEmptyInstance();
		}

		m_codeModel = new JCodeModel();

		Class<?> serviceInterfaceClass = jTypeTable.getClazz();
		String targetClassName = getTargetClassName(codeGenCtx, serviceInterfaceClass.getName());

		m_targetClass = createNewClass(m_codeModel, targetClassName);
		extend(m_targetClass, BaseTypeDefsBuilder.class);

		JMethod allComplexTypesMethod = addAllComplexTypesMethod();
		if(loader != null)
			addAllComplexTypesLogic(allComplexTypesMethod, loader);

		addJavaDocs(m_targetClass);

		generateJavaFile(codeGenCtx, m_targetClass, CodeGenConstants.CLIENT_GEN_FOLDER);

		if (codeGenCtx.getInputOptions().isNoCompile() == false) {
			compileJavaFilesNoException(
				codeGenCtx.getGeneratedJavaFiles(),
				codeGenCtx.getBinLocation());
		}
	}

	private String getTargetClassName(CodeGenContext codeGenCtx, String className) {
		QName serviceQName = codeGenCtx.getServiceQName();
		String dispatcherClassName = ServiceNameUtils.getServiceTypeDefsBuilderClassName(
			serviceQName.getLocalPart(), className);

		return dispatcherClassName;
	}

	private JMethod addAllComplexTypesMethod()
		throws CodeGenFailedException
	{
		String methodName = "build";

		Method baseMethod = IntrospectUtil.getMethodWithSignature(
			BaseTypeDefsBuilder.class, methodName, null);
		if (baseMethod == null) {
			throw new CodeGenFailedException(
				"No 'build' method defined in BaseTypeDefsBuilder");
		}

		Type[] paramTypes = new Type[0];

		JMethod newMethod = addMethod(m_targetClass, methodName, paramTypes,
			baseMethod.getGenericExceptionTypes(),
			baseMethod.getGenericReturnType());

		return newMethod;
	}

	private void addAllComplexTypesLogic(JMethod method, FlatSchemaLoader loader)
		throws CodeGenFailedException
	{
		JBlock methodBody = method.body();

		// create complexTypes list
		JClass complexTypeClass = m_codeModel.ref(FlatSchemaComplexTypeImpl.class);
		JClass complexTypesVarType = m_codeModel.ref(ArrayList.class).narrow(complexTypeClass);
		JVar complexTypesVar = methodBody.decl(complexTypesVarType, "complexTypes", JExpr._new(complexTypesVarType));

		// create all FlatSchemaComplexTypeImpl instances and add to the result list
		List<FlatSchemaComplexTypeImpl> complexTypes = loader.getComplexTypes();
		addComplexTypesPopulationLogic(methodBody, complexTypeClass, complexTypesVar, complexTypes);
		methodBody.directStatement(" ");

		// add all elements
		if (!complexTypes.isEmpty()) {
			addComplexTypeElementsLogic(methodBody, complexTypeClass, complexTypesVar, complexTypes);
			methodBody.directStatement(" ");
		}

		// create rootElements list
		JClass elementDeclClass = m_codeModel.ref(FlatSchemaElementDeclImpl.class);
		JClass rootElementsVarType = m_codeModel.ref(HashMap.class).narrow(
			new JClass[] {m_codeModel.ref(QName.class), elementDeclClass});
		JVar rootElementsVar = methodBody.decl(rootElementsVarType, "rootElements", JExpr._new(rootElementsVarType));

		// add all root elements
		Map<QName,FlatSchemaElementDeclImpl> rootElements = loader.getRootElements();
		addRootElementsPopulationLogic(methodBody, elementDeclClass, rootElementsVar,
			complexTypesVar, rootElements, complexTypes);
		methodBody.directStatement(" ");

		//adding common types
		//generates:  SOAFrameworkCommonTypeDefsBuilder.includeTypeDefs(complexTypes, rootElements);
		JClass commonTypeDefsBuilderClass = m_codeModel.ref(SOAFrameworkCommonTypeDefsBuilder.class);
		JInvocation includeTypeDefsInvo = commonTypeDefsBuilderClass.staticInvoke("includeTypeDefs");
		includeTypeDefsInvo.arg(complexTypesVar);
		includeTypeDefsInvo.arg(rootElementsVar);
		methodBody.add(includeTypeDefsInvo);
		methodBody.directStatement(" ");

		// copy variable pointers
		methodBody.directStatement("m_complexTypes = complexTypes;");
		methodBody.directStatement("m_rootElements = rootElements;");
	}

	private void addComplexTypesPopulationLogic(JBlock outerMethodBody,
		JClass complexTypeClass, JVar outerComplexTypesVar,
		List<FlatSchemaComplexTypeImpl> complexTypes)
	{
		JVar complexTypesVar = outerComplexTypesVar;
		JBlock currentBlock = outerMethodBody;
		for (int i=0; i<complexTypes.size(); i++) {
			if ((i % 100) == 0) {
				JMethod newMethod = m_targetClass.method(JMod.PRIVATE,
					m_codeModel.VOID, "addComplexTypes" + (i/100));
				complexTypesVar = newMethod.param(outerComplexTypesVar.type(), "complexTypes");
				currentBlock = newMethod.body();

				JInvocation newMethodCall = outerMethodBody.invoke(newMethod);
				newMethodCall.arg(outerComplexTypesVar);
			}

			FlatSchemaComplexTypeImpl complexType = complexTypes.get(i);
			QName typeName = complexType.getTypeName();

			JInvocation newComplexTypeExpr;
			if (typeName != null) {
				// construct type QName
				JExpression newQNameExpr = getQNameExpression(typeName);

				// call FlatSchemaComplexTypeImpl constructor
				newComplexTypeExpr = JExpr._new(complexTypeClass);
				newComplexTypeExpr.arg(newQNameExpr);
			} else {
				// anonymous complex type
				newComplexTypeExpr = JExpr._new(complexTypeClass);
			}

			// add to the result list
			JInvocation addToResultExpr = complexTypesVar.invoke("add");
			addToResultExpr.arg(newComplexTypeExpr);

			addComplexTypeComment(currentBlock, i, complexType);
			currentBlock.add(addToResultExpr);
		}
	}

	private void addComplexTypeComment(JBlock currentBlock, int index,
		FlatSchemaComplexTypeImpl complexType)
	{
		QName typeQName = complexType.getTypeName();
		String typeName = (typeQName != null ? typeQName.getLocalPart() : "<Anonymous>");
		currentBlock.directStatement("// Type #" + index + " (" + typeName + ")");
	}

	private void addComplexTypeElementsLogic(JBlock outerMethodBody,
		JClass complexTypeClass, JVar outerComplexTypesVar,
		List<FlatSchemaComplexTypeImpl> complexTypes) throws CodeGenFailedException
	{
		int nextSubmethodNo = 0;
		int statementCount = 0;
		JVar complexTypesVar = outerComplexTypesVar;
		JBlock currentBlock = outerMethodBody;
		JVar currTypeVar = null;
		for (int i=0; i<complexTypes.size(); i++) {
			if (currTypeVar == null || statementCount >= 200) {
				JMethod newMethod = m_targetClass.method(JMod.PRIVATE,
					m_codeModel.VOID, "addComplexTypeElements" + nextSubmethodNo);
				complexTypesVar = newMethod.param(outerComplexTypesVar.type(), "complexTypes");
				currentBlock = newMethod.body();
				currTypeVar = currentBlock.decl(complexTypeClass, "currType");

				nextSubmethodNo++;
				statementCount = 0;

				JInvocation newMethodCall = outerMethodBody.invoke(newMethod);
				newMethodCall.arg(outerComplexTypesVar);
			}

			FlatSchemaComplexTypeImpl complexType = complexTypes.get(i);

			currentBlock.directStatement(" ");
			addComplexTypeComment(currentBlock, i, complexType);

			List<FlatSchemaElementDeclImpl> elements = complexType.getElements();
			
			if (elements.isEmpty()) {
				currentBlock.directStatement("// type has no child elements");
				continue;
			}

			// get current FlatSchemaComplexTypeImpl instance
			JInvocation getFromResultExpr = complexTypesVar.invoke("get");
			getFromResultExpr.arg(JExpr.lit(i));
			currentBlock.assign(currTypeVar, getFromResultExpr);

			for (int j=0; j<elements.size(); j++) {
				FlatSchemaElementDeclImpl element = elements.get(j);
				QName elementName=null;
				//for anonymous complex Type .Need to have a check.
				if(CodeGenUtil.isEmptyString(element.getName().getNamespaceURI()) && complexType.getTypeName()!=null)
					elementName = new QName(complexType.getTypeName().getNamespaceURI(),element.getName().getLocalPart());
				else
					 elementName = element.getName();

				// construct element QName
				JExpression newQNameExpr = getQNameExpression(elementName);

				if (!element.isComplexType()) {
					JInvocation addSimpleElementExpr;
					if (element.isAttribute()) {
						// add attribute to the current complex type
						addSimpleElementExpr = currTypeVar.invoke("addAttribute");
						addSimpleElementExpr.arg(newQNameExpr);
					} else {
						// add simple element to the current complex type
						addSimpleElementExpr = currTypeVar.invoke("addSimpleElement");
						addSimpleElementExpr.arg(newQNameExpr);
						addSimpleElementExpr.arg(JExpr.lit(element.getMaxOccurs()));
					}

					currentBlock.add(addSimpleElementExpr);
					statementCount++;
					continue;
				}

				JInvocation getOtherFromResultExpr = addOtherComplexTypeGetInvocation(
					element, complexTypesVar, complexTypes);

				JInvocation addComplexElementExpr = currTypeVar.invoke("addComplexElement");
				addComplexElementExpr.arg(newQNameExpr);
				addComplexElementExpr.arg(getOtherFromResultExpr);
				addComplexElementExpr.arg(JExpr.lit(element.getMaxOccurs()));

				currentBlock.add(addComplexElementExpr);
				statementCount++;
			}
		}
	}

	private JInvocation addOtherComplexTypeGetInvocation(FlatSchemaElementDeclImpl element,
		JVar complexTypesVar, List<FlatSchemaComplexTypeImpl> complexTypes)
		throws CodeGenFailedException
	{
		int otherComplexTypeIdx = getComplexTypeIndex(complexTypes,
			element.getComplexType(), element.getName());

		JInvocation getOtherFromResultExpr = complexTypesVar.invoke("get");
		getOtherFromResultExpr.arg(JExpr.lit(otherComplexTypeIdx));

		return getOtherFromResultExpr;
	}

	private void addRootElementsPopulationLogic(JBlock outerMethodBody,
		JClass elementDeclClass, JVar outerRootElementsVar, JVar outerComplexTypesVar,
		Map<QName,FlatSchemaElementDeclImpl> rootElements,
		List<FlatSchemaComplexTypeImpl> complexTypes) throws CodeGenFailedException
	{
		int lineNo = 0;
		JVar complexTypesVar = outerComplexTypesVar;
		JVar rootElementsVar = outerRootElementsVar;
		JBlock currentBlock = outerMethodBody;
		for (Map.Entry<QName,FlatSchemaElementDeclImpl> e: rootElements.entrySet()) {
			if ((lineNo % 100) == 0) {
				JMethod newMethod = m_targetClass.method(JMod.PRIVATE,
					m_codeModel.VOID, "addRootElements" + (lineNo/100));
				complexTypesVar = newMethod.param(outerComplexTypesVar.type(), "complexTypes");
				rootElementsVar = newMethod.param(outerRootElementsVar.type(), "rootElements");
				currentBlock = newMethod.body();

				JInvocation newMethodCall = outerMethodBody.invoke(newMethod);
				newMethodCall.arg(outerComplexTypesVar);
				newMethodCall.arg(outerRootElementsVar);
			}

			QName elementName = e.getKey();
			FlatSchemaElementDeclImpl element = e.getValue();

			// construct element QName
			JExpression newQNameExpr = getQNameExpression(elementName);

			JInvocation createElementExpr;
			if (!element.isComplexType()) {
				if (element.isAttribute()) {
					throw new CodeGenFailedException("Internal error - attribute '" +
						element.getName() + "' should not be at the root level");
				} else {
					// add attribute to the current complex type
					createElementExpr = elementDeclClass.staticInvoke("createRootSimpleElement");
					createElementExpr.arg(newQNameExpr);
				}
			} else {
				JInvocation getOtherFromResultExpr = addOtherComplexTypeGetInvocation(
					element, complexTypesVar, complexTypes);

				createElementExpr = elementDeclClass.staticInvoke("createRootComplexElement");
				createElementExpr.arg(newQNameExpr);
				createElementExpr.arg(getOtherFromResultExpr);
			}

			JInvocation putRootElementExpr = rootElementsVar.invoke("put");
			putRootElementExpr.arg(newQNameExpr);
			putRootElementExpr.arg(createElementExpr);

			currentBlock.add(putRootElementExpr);
			lineNo++;
		}
	}

	private JExpression getNamespaceExpression(String namespace) {
		if (namespace == null || namespace.length() == 0) {
			return JExpr._null();
		}

		JFieldVar var = m_namespaces.get(namespace);
		if (var != null) {
			return var;
		}

		// optimize namespace generation by caching instances in the statics

	    int FIELD_MODS = (JMod.PRIVATE | JMod.STATIC | JMod.FINAL);

	    var = m_targetClass.field(FIELD_MODS, m_codeModel.ref(String.class),
			"NS" + (m_namespaces.size()+1), JExpr.lit(namespace));
		m_namespaces.put(namespace, var);
		return var;
	}

	private JExpression getQNameExpression(QName name) {
		JInvocation qnameNew = JExpr._new(m_codeModel.ref(QName.class));
		qnameNew.arg(getNamespaceExpression(name.getNamespaceURI()));
		qnameNew.arg(JExpr.lit(name.getLocalPart()));
		return qnameNew;
	}

	private int getComplexTypeIndex(List<FlatSchemaComplexTypeImpl> list,
		FlatSchemaComplexTypeImpl elem, QName outerName) throws CodeGenFailedException
	{
		for (int i=0; i<list.size(); i++) {
			if (list.get(i) == elem) {
				return i;
			}
		}

		throw new CodeGenFailedException("Internal error - unable to find " +
			"complex type '" + elem.getTypeName() +
			"' referred within element '" + outerName + "'");
	}

	private TypeDefsBuilderGenerator() {
	}

	public String getFilePath(String serviceAdminName, String interfaceName) {
		// TODO Auto-generated method stub
		return null;
	}
}
