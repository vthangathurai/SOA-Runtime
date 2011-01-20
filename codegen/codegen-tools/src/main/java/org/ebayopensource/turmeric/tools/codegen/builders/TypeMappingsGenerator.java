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
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOACommonConstants;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBinder;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBindingFactory;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLMessageType;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLOperationType;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLParserException;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema.ComplexType;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema.ElementType;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema.Parser;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema.SimpleType;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;
import org.ebayopensource.turmeric.tools.codegen.util.J2STypeMappingUtil;

import org.ebayopensource.turmeric.common.config.JavaTypeListConfig;
import org.ebayopensource.turmeric.common.config.MessageHeaderConfig;
import org.ebayopensource.turmeric.common.config.MessageTypeConfig;
import org.ebayopensource.turmeric.common.config.OperationConfig;
import org.ebayopensource.turmeric.common.config.OperationListConfig;
import org.ebayopensource.turmeric.common.config.PackageConfig;
import org.ebayopensource.turmeric.common.config.PackageMapConfig;
import org.ebayopensource.turmeric.common.config.ServiceTypeMappingConfig;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameCemcMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameToCemcMappingList;


/**
 * Generates Type mappings configuration file for a service, based on interface.
 * 
 * Type mappings configuration file maintain Package to Namespace mapping, list
 * of operations supported by a service and Java types to XML types and Element
 * name mapping.
 * 
 * @author rmandapati
 */
public class TypeMappingsGenerator extends BaseCodeGenerator implements
		SourceGenerator {

	private static final String JAVA_PKG_PREFIX = "java.";

	private static final String GEN_TYPE_MAPPINGS_DIR = "META-INF/soa/common/config";

	private static final String TYPE_MAPPINGS_FILE_NAME = "TypeMappings.xml";

	private static String s_commonTypePackage = ErrorMessage.class.getPackage()
			.getName();

	private static Logger s_logger = LogManager
			.getInstance(TypeMappingsGenerator.class);

	private static TypeMappingsGenerator s_typeMappingsGenerator = new TypeMappingsGenerator();

	private TypeMappingsGenerator() {
	}

	public static TypeMappingsGenerator getInstance() {
		return s_typeMappingsGenerator;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {

		ServiceTypeMappingConfig typeMappings = createTypeMappings(codeGenCtx);

		generateTypeMappingsXml(codeGenCtx, typeMappings);
	}

	private Logger getLogger() {
		return s_logger;
	}

	public boolean continueOnError() {
		return false;
	}

	private void generateTypeMappingsXml(CodeGenContext codeGenCtx,
			ServiceTypeMappingConfig typeMappings)
			throws CodeGenFailedException {

		Writer fileWriter = null;
		try {
			String destFolderPath = CodeGenUtil.genDestFolderPath(codeGenCtx
					.getMetaSrcDestLocation(), codeGenCtx.getServiceAdminName(),
					GEN_TYPE_MAPPINGS_DIR);

			fileWriter = CodeGenUtil.getFileWriter(destFolderPath,
					TYPE_MAPPINGS_FILE_NAME);

			JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			javaXmlBinder.generateTypeMappingsXml(typeMappings, fileWriter);

			getLogger().log(
					Level.INFO,
					"Successfully generated " + destFolderPath + File.separator
							+ TYPE_MAPPINGS_FILE_NAME);

		} catch (Exception ex) {
			String errMsg = "Failed to generate " + TYPE_MAPPINGS_FILE_NAME;
			getLogger().log(Level.SEVERE, errMsg, ex);
			throw new CodeGenFailedException(errMsg, ex);
		} finally {
			CodeGenUtil.closeQuietly(fileWriter);
		}

	}

	private ServiceTypeMappingConfig createTypeMappings(
			CodeGenContext codeGenCtx) throws CodeGenFailedException {

		InputOptions inputOptions = codeGenCtx.getInputOptions();

		Set<String> packageSet = new HashSet<String>();
		Map<String, String> headerPkgNsMap = new HashMap<String, String>();

		ServiceTypeMappingConfig typeMappings = new ServiceTypeMappingConfig();
		typeMappings.setName(codeGenCtx.getServiceAdminName());
		typeMappings.setEnableNamespaceFolding(inputOptions.isEnabledNamespaceFoldingSet());

		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();
		List<Method> methodsList = jTypeTable.getMethods();

		if (inputOptions.isImplCommonSvcInterface()) {
			Method[] commonMethods = getCommonServiceInterface()
					.getDeclaredMethods();
			for (Method method : commonMethods) {
				methodsList.add(method);
			}
		}

		// if input is specified as WSDL file then
		// use XML elements names defined in the WSDL
		Map<String, WSDLOperationType> wsdlOperations = WSDLUtil
				.getWSDLOparations(codeGenCtx.getWSDLURI(), codeGenCtx);

		Map<String, String> opNameToCemcMap = getOpNameToCemcMap(inputOptions
				.getOpNameToCemcMappings());

		OperationListConfig operationList = new OperationListConfig();
		for (Method method : methodsList) {
			OperationConfig opConfig = createOperationConfig(method,
					codeGenCtx, wsdlOperations, opNameToCemcMap, headerPkgNsMap);

			operationList.getOperation().add(opConfig);

			addPackages(method, packageSet, opConfig);
		}

		typeMappings.setOperationList(operationList);

		JavaTypeListConfig javaTypeListConfig = getJavaTypeListConfig(codeGenCtx,packageSet,headerPkgNsMap);
		typeMappings.setJavaTypeList(javaTypeListConfig);

		
		packageSet.add(getDefaultErrorMsgClass().getPackage().getName());
		PackageMapConfig pkgMapConfig = createPackageConfigMap(codeGenCtx,
				packageSet, headerPkgNsMap);
		typeMappings.setPackageMap(pkgMapConfig);

		
		
		
		return typeMappings;
	}

	private JavaTypeListConfig getJavaTypeListConfig(CodeGenContext codeGenCtx, Set<String> packageSet, Map<String, String> headerPkgNsMap) {

		JavaTypeListConfig javaTypeListConfig = new JavaTypeListConfig();
		List<Object> listOfSchemaTypes = new ArrayList<Object>();
		

		Definition wsdlDefinition = codeGenCtx.getWsdlDefinition();
		if (wsdlDefinition == null) {
			getLogger().log(Level.WARNING,
							"WSDL definition is null in the method getAllTypesQName in TypeMappingsGenerator. So the types list won't be created  ");
			return javaTypeListConfig;
		}

		try {
			Parser.getAllSchemaTypes(wsdlDefinition, listOfSchemaTypes, null);
		} catch (WSDLParserException e) {
			getLogger().log(Level.WARNING,
							"WSDL definition parsing failed in the method getAllTypesQName in TypeMappingsGenerator. So the types list won't be created  \n"
									+ "Exception is :" + e.getMessage());
			return javaTypeListConfig;
		}

		Map<String, String> NS2PkgMap = getNS2PkgMap(codeGenCtx);
		Set<QName> allEligibleTypesQName = new HashSet<QName>();

		for (Object currSchemaTypeObject : listOfSchemaTypes) {

			if (currSchemaTypeObject instanceof ElementType) {
				ElementType elementType = (ElementType) currSchemaTypeObject;
				if(elementType.getElementType() == null){
					//anonymous element, for such elements a complex type with the same name and namespace as the element name would be created
					allEligibleTypesQName.add(elementType.getTypeName());
				}
			} 
			else if(currSchemaTypeObject instanceof ComplexType){
				ComplexType complexType = (ComplexType)currSchemaTypeObject;
				allEligibleTypesQName.add(complexType.getTypeName());
			} 
			else if (currSchemaTypeObject instanceof SimpleType){
				SimpleType simpleType = (SimpleType)currSchemaTypeObject;
				QName simpleTypeQName = simpleType.getTypeName();
				
				if(isJavaFileCreatedForType(simpleTypeQName,codeGenCtx,NS2PkgMap))
					allEligibleTypesQName.add(simpleTypeQName);
				
			}

		
		}
		
		
		Set<String> sortedJavaTypeNames = new TreeSet<String>();
		
		for(QName currTypesQName : allEligibleTypesQName){
			String NS = currTypesQName.getNamespaceURI();
			String pkg = NS2PkgMap.get(NS);
			
			if (CodeGenUtil.isEmptyString(pkg)) {
				pkg = WSDLUtil.getPackageFromNamespace(NS);
				
			}
		
			//The following entry is needed for the mapping to occur in the package-config section BUGDB00603919
			packageSet.add(pkg);
			if( ! headerPkgNsMap.containsKey(pkg)){
				headerPkgNsMap.put(pkg, NS);
			}

			String javaTypeFullName = pkg + "."	+ WSDLUtil.getXMLIdentifiersClassName(currTypesQName.getLocalPart());
			sortedJavaTypeNames.add(javaTypeFullName);
			
		}
		
		for(String currJavaTypeFullName : sortedJavaTypeNames){
			javaTypeListConfig.getJavaTypeName().add(currJavaTypeFullName);
		}
		

		return javaTypeListConfig;
	}
	
	
	//BUGDB00597898  - for some simple types the java file might  not have been created, we need to add mapping only for thoe types for which the java 
	//					file is generated
	private boolean isJavaFileCreatedForType(QName typesQName,CodeGenContext codeGenCtx,Map<String, String> NS2PkgMap){
		
		String schemaTypesLocation = codeGenCtx.getSchemaTypesJavaFileLocation();
		schemaTypesLocation = CodeGenUtil.toOSFilePath(schemaTypesLocation);
		
		String packageName = NS2PkgMap.get(typesQName.getNamespaceURI());
		String typeName = WSDLUtil.getXMLIdentifiersClassName(typesQName.getLocalPart());
		
		if (CodeGenUtil.isEmptyString(packageName)) {
			packageName = WSDLUtil.getPackageFromNamespace(typesQName.getNamespaceURI());
		} 
		
		
		String javaFilePathSuffix =  packageName.replace(".", File.separator);
		javaFilePathSuffix = CodeGenUtil.toOSFilePath(javaFilePathSuffix) + typeName +  ".java";
		String filePath = schemaTypesLocation + javaFilePathSuffix;
		
		File javaFile = new File(filePath);
		
		if(javaFile.exists())
			return true;
		else
			return false;

	}
	

	private OperationConfig createOperationConfig(Method method,
			CodeGenContext codeGenCtx,
			Map<String, WSDLOperationType> wsdlOperations,
			Map<String, String> opNameToCemcMap,
			Map<String, String> headerPkgNsMap) throws CodeGenFailedException {

		String methodName = method.getName();

		// Get the name of the corresponding operation name from the WSDL for
		// the given java method
		String operationName = codeGenCtx.getJavaMethodOperationNameMap().get(
				methodName);
		if (CodeGenUtil.isEmptyString(operationName))
			operationName = methodName;

		OperationConfig operationConfig = new OperationConfig();
		operationConfig.setName(operationName);

		Class<?>[] paramTypes = method.getParameterTypes();
		// method accepts parameters
		if (paramTypes.length > 0) {
			MessageTypeConfig requestMsg = getRequestMessageTypeConfig(
					operationName, paramTypes[0], wsdlOperations);
			operationConfig.setRequestMessage(requestMsg);
		}

		Class<?> returnType = method.getReturnType();
		// method accepts parameters
		if (returnType != Void.TYPE) {
			MessageTypeConfig responseMsg = getResponseMessageTypeConfig(
					operationName, returnType, wsdlOperations);
			operationConfig.setResponseMessage(responseMsg);
		}

		Map<String, String> ns2PkgMap = getNS2PkgMap(codeGenCtx);

		// adding error-message
		MessageTypeConfig errorMsg = getErrorMsgTypeConfig(operationName,
				opNameToCemcMap, wsdlOperations, ns2PkgMap, headerPkgNsMap);
		operationConfig.setErrorMessage(errorMsg);

		// adding SOAPHeaderRequest
		List<MessageHeaderConfig> msgReqHeaderList = getRequestMessageHeaderConfig(
				operationName, wsdlOperations, codeGenCtx, ns2PkgMap,
				headerPkgNsMap);
		operationConfig.getRequestHeader().addAll(msgReqHeaderList);

		// adding SOAPHeaderResponse
		List<MessageHeaderConfig> msgResHeaderList = getResponseMessageHeaderConfig(
				operationName, wsdlOperations, codeGenCtx, ns2PkgMap,
				headerPkgNsMap);
		operationConfig.getResponseHeader().addAll(msgResHeaderList);

		return operationConfig;
	}

	private void addPackages(Method method, Set<String> packageSet,
			OperationConfig opConfig) {

		Class<?>[] allTypes = combineTypes(method);

		for (Class<?> type : allTypes) {
			if (type.isArray()) {
				type = type.getComponentType();
			}

			if (type == Void.TYPE || type.isPrimitive()) {
				packageSet.add("java.lang");
			} else {
				packageSet.add(type.getPackage().getName());
			}
		}

		// for SOAP headers if present
		List<MessageHeaderConfig> msgReqHeaderList = opConfig
				.getRequestHeader();
		for (MessageHeaderConfig messageHeaderConfig : msgReqHeaderList) {
			String javaTypeName = messageHeaderConfig.getJavaTypeName();
			packageSet.add(CodeGenUtil.getPackageName(javaTypeName));
		}

		List<MessageHeaderConfig> msgResHeaderList = opConfig
				.getResponseHeader();
		for (MessageHeaderConfig messageHeaderConfig : msgResHeaderList) {
			String javaTypeName = messageHeaderConfig.getJavaTypeName();
			packageSet.add(CodeGenUtil.getPackageName(javaTypeName));
		}

	}

	private PackageMapConfig createPackageConfigMap(CodeGenContext codeGenCtx,
			Set<String> packageSet, Map<String, String> headerPkgNsMap) {

		Map<String, String> pkgNSMap = createPkgNSMap(codeGenCtx
				.getInputOptions());

		PackageMapConfig pkgMapConfig = new PackageMapConfig();
		PackageConfig pkgConfig = new PackageConfig();
		List<PackageConfig> pkgList = pkgMapConfig.getPackage();

		String userGivenCommonTypesNS = codeGenCtx.getInputOptions()
				.getCommonTypesNS();
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();

		/*
		 * If the isEnabledNamespaceFolding is set, then all the packages should be mapped to 
		 * the service's namespace . Even -ctns is ignored.
		 */
		
		// Adds the package map for the interface class.
		/* order of preference to choose a NS for the interface's package
		 * a) if the pkg is common types pkg then use NS given thru -ctns
		 * b) use the service's NS
		 * 
		 */
		String intfPkg = CodeGenUtil.getPackageName(codeGenCtx
				.getServiceInterfaceClassName());
		pkgConfig.setName(intfPkg);
		String serviceNS = codeGenCtx.getNamespace();

		String nameSpaceForIntfPkg = serviceNS;
		
		if(!inputOptions.isEnabledNamespaceFoldingSet()){
			if (isCommonTypesPkg(intfPkg)
					&& !CodeGenUtil.isEmptyString(userGivenCommonTypesNS)) {
				nameSpaceForIntfPkg = userGivenCommonTypesNS;
			}
		}
			
		pkgConfig.setXmlNamespace(nameSpaceForIntfPkg);
		
		pkgList.add(pkgConfig);

		for (String packageName : packageSet) {
			if(packageName.equals(intfPkg))// Avoid two entries for the intf package
				continue;
				
			pkgConfig = new PackageConfig();
			pkgConfig.setName(packageName);
			
			/*
			 * If the isEnabledNamespaceFolding is set, then all the packages should be mapped to 
			 * the service's namespace . Even -ctns is ignored.
			 * java.* package needs to be mapped to standard xmlschemaNS.
			 */
			if(inputOptions.isEnabledNamespaceFoldingSet() ){
				if (packageName.startsWith(JAVA_PKG_PREFIX)) 
					pkgConfig.setXmlNamespace(J2STypeMappingUtil.XSD_NAMESPACE);
				else
					pkgConfig.setXmlNamespace(serviceNS);
			}
			else{	
			
				/* order of preference to choose a NS for a non-interface package (the interface's package is dealt before entering the current loop) 
				 * a) if the pkg is common types pkg then use NS given thru -ctns 
				 * b) take from -ns2pkg and -pkg2ns 
				 * c) if pkg has entry in header use the corresponding NS
				 * d) if pkg prefix starts with "java." then use XSD_NAMESPACE
				 * e) if the pkg is common types pkg but -ctns is not given , use the SOA_TYPES_NAMESPACE
				 * f) else use the Service's namespace 
				 */
				
				
				if (isCommonTypesPkg(packageName)
						&& !CodeGenUtil.isEmptyString(userGivenCommonTypesNS)) {
					pkgConfig.setXmlNamespace(userGivenCommonTypesNS);
				}else if (pkgNSMap.containsKey(packageName)) {
					pkgConfig.setXmlNamespace(pkgNSMap.get(packageName));
				} else if (headerPkgNsMap.containsKey(packageName)) {
					pkgConfig.setXmlNamespace(headerPkgNsMap.get(packageName));
				} else if (packageName.startsWith(JAVA_PKG_PREFIX)) {
					pkgConfig.setXmlNamespace(J2STypeMappingUtil.XSD_NAMESPACE);
				} else if (isCommonTypesPkg(packageName) 
						&& CodeGenUtil.isEmptyString(userGivenCommonTypesNS)) {
					pkgConfig.setXmlNamespace(SOACommonConstants.SOA_TYPES_NAMESPACE);
				} else {
					pkgConfig.setXmlNamespace(serviceNS);
				}
			
			}
			
			addToList(pkgList, pkgConfig);
		}

		return pkgMapConfig;
	}

	/**
	 * Adds PackageConfig to the package list without duplicate.
	 * 
	 * @param pkgList
	 * @param pkgCfg
	 */
	private void addToList(List<PackageConfig> pkgList, PackageConfig pkgCfg) {
		boolean exists = false;
		for (PackageConfig existCfg : pkgList) {
			if (existCfg.getName().equals(pkgCfg.getName())
					&& existCfg.getXmlNamespace().equals(
							pkgCfg.getXmlNamespace())) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			pkgList.add(pkgCfg);
		}
	}

	private Map<String, String> createPkgNSMap(InputOptions inputOptions) {

	   Map<String, String> NS2PkgMap = WSDLUtil.getNS2PkgMappings(inputOptions);
	   
	   //interchange keys and values 
	   Map<String, String> pkgNSMap = new HashMap<String, String>();
	   for(Entry<String, String> currNSpkgEntry : NS2PkgMap.entrySet()){
		   pkgNSMap.put(currNSpkgEntry.getValue(), currNSpkgEntry.getKey());
	   }
		
		return pkgNSMap;
	}

	private boolean isCommonTypesPkg(String packageName) {
		return s_commonTypePackage.equals(packageName);
	}

	private MessageTypeConfig getRequestMessageTypeConfig(String operationName,
			Class<?> type, Map<String, WSDLOperationType> wsdlOperations) {

		MessageTypeConfig msgTypeConfig = new MessageTypeConfig();

		msgTypeConfig.setJavaTypeName(getJavaTypeName(type));
		WSDLOperationType wsdlOperation = wsdlOperations.get(operationName);
		String xmlTypeName = null;
		if (wsdlOperation == null) {
			xmlTypeName = getXmlTypeName(type, null);
		} else {
			xmlTypeName = getXmlTypeName(type, wsdlOperation.getInMessage());
		}
		msgTypeConfig.setXmlTypeName(xmlTypeName);
		if (wsdlOperation != null) {
			msgTypeConfig.setXmlElementName(wsdlOperation.getInMessage().getElementQname().toString());
		} else {
			QName INMessageQname = QName.valueOf(operationName);
			msgTypeConfig.setXmlElementName(INMessageQname.toString());
		}

		Set<String> typeNameSet = new HashSet<String>();
		boolean hasAttachmentRef = IntrospectUtil.hasAttachmentTypeRef(type,
				typeNameSet);
		msgTypeConfig.setHasAttachment(hasAttachmentRef);

		return msgTypeConfig;
	}

	
	private MessageTypeConfig getResponseMessageTypeConfig(
			String operationName, Class<?> type,
			Map<String, WSDLOperationType> wsdlOperations) {

		MessageTypeConfig msgTypeConfig = new MessageTypeConfig();

		msgTypeConfig.setJavaTypeName(getJavaTypeName(type));
		WSDLOperationType wsdlOperation = wsdlOperations.get(operationName);
		String xmlTypeName = null;
		if (wsdlOperation == null) {
			xmlTypeName = getXmlTypeName(type, null);
		} else {
			xmlTypeName = getXmlTypeName(type, wsdlOperation.getOutMessage());
		}
		msgTypeConfig.setXmlTypeName(xmlTypeName);
		if (wsdlOperation != null) {
			msgTypeConfig.setXmlElementName(wsdlOperation.getOutMessage().getElementQname().toString());
		} else {
			QName operationQname = QName.valueOf(operationName);
			msgTypeConfig.setXmlElementName(operationQname.toString());
		}

		Set<String> typeNameSet = new HashSet<String>();
		boolean hasAttachmentRef = IntrospectUtil.hasAttachmentTypeRef(type,
				typeNameSet);
		msgTypeConfig.setHasAttachment(hasAttachmentRef);

		return msgTypeConfig;
	}

	private String getJavaTypeName(Class<?> type) {

		Class<?> actualType = getActualType(type);
		if (actualType.isPrimitive()) {
			Class<?> wrapperType = IntrospectUtil.getWrapperType(actualType
					.getName());
			return wrapperType.getName();
		} else {
			return actualType.getName();
		}
	}

	private MessageTypeConfig getErrorMsgTypeConfig(String operationName,
			Map<String, String> opNameToCemcMap,
			Map<String, WSDLOperationType> wsdlOperations,
			Map<String, String> ns2PkgMap, Map<String, String> headerPkgNsMap)
			throws CodeGenFailedException {

		MessageTypeConfig errorMsgTypeConfig = new MessageTypeConfig();

		Class<?> errorMsgClass = null;
		boolean isErrorMessageConfigSet = false;

		if (opNameToCemcMap.get(operationName) != null) {
			errorMsgClass = ContextClassLoaderUtil.loadRequiredClass(opNameToCemcMap.get(operationName));
		} else if (opNameToCemcMap.get(CodeGenConstants.ALL) != null) {
			errorMsgClass = ContextClassLoaderUtil.loadRequiredClass(opNameToCemcMap.get(CodeGenConstants.ALL));
		} else {
			/*
			 * If -op2cemc option is not provided, then codegen would respect
			 * the wsdl:fault defined for each of the operations.IF wsdl:fault
			 * is not defined, then tit uses the default ErrorMessage class
			 * though by WSDL standards an operation can have more than one
			 * fault. Codegen as of now would assume that there is only one
			 * fault defined per operation. The WSDL validation tool should have
			 * this assertion also.
			 */

			WSDLOperationType wsdlOperation = wsdlOperations.get(operationName);
			if (wsdlOperation != null) {

				List<WSDLMessageType> faultsMessage = wsdlOperation.getFaults();
				if (faultsMessage != null && (faultsMessage.size() > 0)) {
					WSDLMessageType messageType = faultsMessage.get(0);// process
																		// only
																		// the
																		// first
																		// fault
																		// defined
																		// for
																		// an
																		// operation

					String packageName = ns2PkgMap.get(messageType.getName());
					if (CodeGenUtil.isEmptyString(packageName)) {
						packageName = WSDLUtil
								.getPackageFromNamespace(messageType.getName());
						headerPkgNsMap.put(packageName, messageType.getName());
					}

					errorMsgTypeConfig.setJavaTypeName(packageName
							+ "."
							+ WSDLUtil.getXMLIdentifiersClassName(messageType
									.getSchemaTypeName()));
					errorMsgTypeConfig.setXmlTypeName(messageType
							.getSchemaTypeName());
					errorMsgTypeConfig.setXmlElementName(messageType.getElementQname().toString());

					isErrorMessageConfigSet = true;
				}
			}

			if (!isErrorMessageConfigSet)
				errorMsgClass = getDefaultErrorMsgClass();

		}

		if (!isErrorMessageConfigSet) {
			errorMsgTypeConfig.setJavaTypeName(errorMsgClass.getName());
			errorMsgTypeConfig.setXmlTypeName(errorMsgClass.getSimpleName());
			String localPartErrMsg = CodeGenUtil
			.makeFirstLetterLower(errorMsgClass.getSimpleName());
			QName xmlElementQname = getQnameForErrorMessage(localPartErrMsg,errorMsgClass,ns2PkgMap);
			errorMsgTypeConfig.setXmlElementName(xmlElementQname.toString());
		}

		return errorMsgTypeConfig;
	}

	private QName getQnameForErrorMessage(String errorMsgLocalPart,Class<?> errorMsgClass,Map<String, String> ns2pkgMap) {
        
		String packageName = errorMsgClass.getPackage().getName();
		String namespace = null;
		Iterator<Entry<String, String>> itr= ns2pkgMap.entrySet().iterator();
		while(itr.hasNext())
		{
		Entry<String, String> currentEntry = itr.next();
		if(currentEntry.getValue().equals(packageName))
			namespace = currentEntry.getKey();
		}
		return new QName(namespace,errorMsgLocalPart);
	
	}

	private List<MessageHeaderConfig> getRequestMessageHeaderConfig(
			String operationName,
			Map<String, WSDLOperationType> wsdlOperations,
			CodeGenContext codeGenCtx, Map<String, String> ns2PkgMap,
			Map<String, String> headerPkgNsMap) {

		List<MessageHeaderConfig> msgHeaderConfigList = new ArrayList<MessageHeaderConfig>();

		WSDLOperationType wsdlOperation = wsdlOperations.get(operationName);
		if (wsdlOperation == null) {
			// for service methods wsdlOperation would be null
			return msgHeaderConfigList;
		}

		List<WSDLMessageType> WSDLMessageList = wsdlOperation
				.getRequestHeader();

		for (WSDLMessageType messageType : WSDLMessageList) {

			MessageHeaderConfig msgHeaderConfig = new MessageHeaderConfig();

			String packageName = ns2PkgMap.get(messageType.getName());
			if (CodeGenUtil.isEmptyString(packageName)) {
				packageName = WSDLUtil.getPackageFromNamespace(messageType
						.getName());
				headerPkgNsMap.put(packageName, messageType.getName());
			}
			msgHeaderConfig.setJavaTypeName(packageName
					+ "."
					+ WSDLUtil.getXMLIdentifiersClassName(messageType
							.getSchemaTypeName()));

			msgHeaderConfig.setXmlTypeName(messageType.getSchemaTypeName());

			msgHeaderConfig.setXmlElementName(messageType.getElementName());

			msgHeaderConfigList.add(msgHeaderConfig);
		}

		return msgHeaderConfigList;
	}

	private List<MessageHeaderConfig> getResponseMessageHeaderConfig(
			String operationName,
			Map<String, WSDLOperationType> wsdlOperations,
			CodeGenContext codeGenCtx, Map<String, String> ns2PkgMap,
			Map<String, String> headerPkgNsMap) {

		List<MessageHeaderConfig> msgHeaderConfigList = new ArrayList<MessageHeaderConfig>();

		WSDLOperationType wsdlOperation = wsdlOperations.get(operationName);
		if (wsdlOperation == null) {
			// for service methods wsdlOperation would be null
			return msgHeaderConfigList;
		}

		List<WSDLMessageType> WSDLMessageList = wsdlOperation
				.getResponseHeader();

		for (WSDLMessageType messageType : WSDLMessageList) {

			MessageHeaderConfig msgHeaderConfig = new MessageHeaderConfig();

			String packageName = ns2PkgMap.get(messageType.getName());
			if (CodeGenUtil.isEmptyString(packageName)) {
				packageName = WSDLUtil.getPackageFromNamespace(messageType
						.getName());
				headerPkgNsMap.put(packageName, messageType.getName());
			}
			msgHeaderConfig.setJavaTypeName(packageName
					+ "."
					+ WSDLUtil.getXMLIdentifiersClassName(messageType
							.getSchemaTypeName()));

			msgHeaderConfig.setXmlTypeName(messageType.getSchemaTypeName());

			msgHeaderConfig.setXmlElementName(messageType.getElementName());

			msgHeaderConfigList.add(msgHeaderConfig);
		}

		return msgHeaderConfigList;
	}

	private Map<String, String> getNS2PkgMap(CodeGenContext codeGenCtx) {

		return WSDLUtil.getNS2PkgMappings(codeGenCtx.getInputOptions());
	}


	private String getXmlTypeName(Class<?> type, WSDLMessageType wsdlMsgType) {
		Class<?> actualType = getActualType(type);
		QName qName = J2STypeMappingUtil.getXmlTypeName(actualType);
		if (qName != null) {
			return qName.getLocalPart();
		} else if (wsdlMsgType != null) {
			// Type names should be same as Type names in WSDL
			return wsdlMsgType.getSchemaTypeName();
		} else if (actualType.getName().startsWith("java")) {
			return CodeGenUtil.makeFirstLetterLower(actualType.getSimpleName());
		} else {
			return actualType.getSimpleName();
		}
	}

	private Class<?> getDefaultErrorMsgClass() {
		return ErrorMessage.class;
	}

	private Class<?> getActualType(Class<?> type) {
		if (type.isArray()) {
			return type.getComponentType();
		} else {
			return type;
		}
	}

	private static Class<?>[] combineTypes(Method method) {
		Class<?>[] paramTypes = method.getParameterTypes();
		Class<?> returnType = method.getReturnType();
		Class<?>[] exceptionTypes = method.getExceptionTypes();

		int length = paramTypes.length + exceptionTypes.length + 1;
		Class<?>[] allTypes = new Class[length];
		System.arraycopy(paramTypes, 0, allTypes, 0, paramTypes.length);
		System.arraycopy(exceptionTypes, 0, allTypes, paramTypes.length,
				exceptionTypes.length);
		allTypes[length - 1] = returnType;

		return allTypes;
	}

	private Map<String, String> getOpNameToCemcMap(
			OpNameToCemcMappingList opNameToCemcMappings) {

		Map<String, String> opNameToCemcMap = new HashMap<String, String>();

		if (opNameToCemcMappings == null
				|| opNameToCemcMappings.getOpNameCemcMap().size() == 0) {
			// opNameToCemcMap.put("all", getDefaultErrorMsgClass().getName());
			return opNameToCemcMap;
		} else {
			for (OpNameCemcMappingType opNameCemcMapEntry : opNameToCemcMappings
					.getOpNameCemcMap()) {
				opNameToCemcMap.put(opNameCemcMapEntry.getOperationName(),
						opNameCemcMapEntry.getCustomErrMsgClass());
			}
		}

		return opNameToCemcMap;
	}

	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(GEN_TYPE_MAPPINGS_DIR)
				+ serviceAdminName + File.separatorChar
				+ TYPE_MAPPINGS_FILE_NAME;
		return filePath;

	}

}
