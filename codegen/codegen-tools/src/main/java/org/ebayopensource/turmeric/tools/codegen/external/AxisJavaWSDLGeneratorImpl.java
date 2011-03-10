/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import org.apache.axis2.wsdl.WSDL2Java;
import org.apache.ws.java2wsdl.Java2WSDLCodegenEngine;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOptionParser;
import org.ebayopensource.turmeric.common.config.TypeInformationType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;
import org.ebayopensource.turmeric.runtime.codegen.common.NSPkgMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.NSToPkgMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameCemcMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameToCemcMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.PkgToNSMappingList;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WsdlParserUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.TypeLibraryClassDetails;
import org.ebayopensource.turmeric.tools.library.SOAGlobalRegistryFactory;
import org.ebayopensource.turmeric.tools.library.SOATypeRegistry;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.builders.TypeLibraryParser;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;


public class AxisJavaWSDLGeneratorImpl implements JavaWSDLGenerator {

	private static final String PKG_PARAM = "@pkg@";
	private static final String NAMESPACE_PARAM = "@ns@";

	private static final String JAVA_2_WSDL_PKG_TO_NS_PATTERN = "[" + PKG_PARAM + "," + NAMESPACE_PARAM + "]";


	private static final String XML_TRANSFORMER_FACTORY = "javax.xml.transform.TransformerFactory";

	private static CallTrackingLogger s_logger = LogManager.getInstance(AxisJavaWSDLGeneratorImpl.class);
	
	private FileHandler m_fileHandlerForWSDL2Java;


	private CallTrackingLogger getLogger() {
		return s_logger;
	}


	public AxisJavaWSDLGeneratorImpl() {
		String codegenConfigProp = "/org/ebayopensource/turmeric/tools/codegen/external/codegen-config.properties";
		System.setProperty("org.apache.axis2.codegen.config", codegenConfigProp);

		/* Perform a quick check on the ClassLoader to see if the axis2 config
		 * file can be found.
		 * Axis2 will trigger a NPE if this file is not found via the classloader.
		 * This makes this issue more meaningful in the logs.
		 */
		URL url = this.getClass().getResource(codegenConfigProp);
		if(url == null) {
			getLogger().severe("Axis2 Configuration File not present in ClassLoader: " + codegenConfigProp);
		}
	}

	public void java2WSDL(
				CodeGenContext codeGenCtx,
				String qualifiedIntfName,
				String destLocation) throws CodeGenFailedException {

		System.setProperty("soa.service.default.ns", codeGenCtx.getNamespace());

		OpNameToCemcMappingList opNameToCemcMappings =
				codeGenCtx.getInputOptions().getOpNameToCemcMappings();
		String opNameToCemcMapString = getOpNameToCemcMapString(opNameToCemcMappings);
		if (opNameToCemcMapString != null) {
			System.setProperty("soa.service.opname.to.cemc.map", opNameToCemcMapString);
		}

		String[] args = getJava2WSDLToolArgs(codeGenCtx, qualifiedIntfName, destLocation);
		if(LogManager.isTracingEnabled()){
			String strMsg = Arrays.toString(args);
			getLogger().debug("Arguments passed to AXIS2's Java2WSDL : \n"+ strMsg);
		}
		try {
			Java2WSDLCommandLineOptionParser cmdArgsParser =
					new Java2WSDLCommandLineOptionParser(args);

			Map<String,Java2WSDLCommandLineOption> allOptions = cmdArgsParser.getAllOptions();
			Java2WSDLCodegenEngine java2WSDLEngine = new Java2WSDLCodegenEngine(allOptions);
			// generate WSDL
			java2WSDLEngine.generate();

			// Pretty format generated WSDL
			// this is quick fix until Axis2 stops pretty formatting all
			// XML and WSDL files under output directories.
			String wsdlFilePath =  CodeGenUtil.toOSFilePath(destLocation) +
									getWSDLFileName(codeGenCtx.getServiceAdminName());
			prettyFormatWSDL(wsdlFilePath);

		} catch (Exception ex) {
			throw new CodeGenFailedException(
					"Failed to generate WSDL for : " + qualifiedIntfName, ex);
		} finally {
			System.setProperty("soa.service.default.ns", "");
			System.setProperty("soa.service.opname.to.cemc.map", "");
		}

	}



	public void wsdl2Java(CodeGenContext codeGenCtx, String destLocation)
			throws CodeGenFailedException {
		
		String oldFactoryName = System.getProperty(XML_TRANSFORMER_FACTORY, "");
		// set to new value
		try {
			Class.forName("org.apache.xalan.processor.TransformerFactoryImpl");//Transformer facotry impl available with IBM JRE
			System.setProperty(XML_TRANSFORMER_FACTORY, "org.apache.xalan.processor.TransformerFactoryImpl");
		} catch (ClassNotFoundException e1) {
			try {
				Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");//Transformer facotry impl available with SUN JDK 6
				System.setProperty(XML_TRANSFORMER_FACTORY, "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
			} catch (ClassNotFoundException e) {
				System.setProperty(XML_TRANSFORMER_FACTORY, ""); // use the availble Transformer factory if the preferred ones are not available
			}
		}
		getLogger().log(Level.INFO, "The XML Transformer factory used is : " + System.getProperty(XML_TRANSFORMER_FACTORY) );
		
		

		String[] args = getWSDL2JavaToolArgs(codeGenCtx, destLocation);
		getLogger().log(Level.INFO,"Arguments passed to AXIS2's WSDL2Java : \n"+ Arrays.toString(args));

		
		File wsdl2javaLogFile = getLogFileOfWSDL2JavaCall();
		
		try {
			WSDL2Java.main(args);
		} catch (Exception ex) {
			String wsdl2JavaWarningAndError = getWarningErorFromLog(wsdl2javaLogFile);
			CodeGenFailedException codeGenFailedException = new CodeGenFailedException(
					"Failed to generate Java code for : " + codeGenCtx.getInputOptions().getInputFile() + "\n" + wsdl2JavaWarningAndError, ex);
			codeGenFailedException.setMessageFormatted(true);

			throw codeGenFailedException;
			
		} finally {
			
			removeFileHandlerForWSDL2JavaLog();

			//remove the unneccessary ObjectFactory.java and package-info.java SOAPLATFORM-609
			deleteDuplicateFilesFromDefaultPackage(codeGenCtx);

			if(codeGenCtx.getInputOptions().isObjectFactoryTobeDeleted())
				deleteObjectFactoryfile(codeGenCtx);
			// reset to old factory name
			if (null != oldFactoryName && oldFactoryName.length() != 0) {
				System.setProperty(XML_TRANSFORMER_FACTORY, oldFactoryName);
			} else {
				System.setProperties(null);
			}
	}

		
		try {
			//need to delete the wsdl2javalog file now.
			s_logger.log(Level.FINE,"Deleting wsdl2javaLog file");
			CodeGenUtil.closeQuietly(m_fileHandlerForWSDL2Java);
			CodeGenUtil.deleteFile(wsdl2javaLogFile);
			deleteAdditionalJavaTypeFiles(codeGenCtx,destLocation);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Exception while trying to delete extra types. " + e.getMessage(), e);
		}
		
		
		// Calls to WSDL2Java are nulling out some of  the set System properties like http.ProxyHost and http.ProxyPort
		String proxyHost = codeGenCtx.getInputOptions().getHttpProxyHost();
		String proxyPort = codeGenCtx.getInputOptions().getHttpProxyPort();
		if( !CodeGenUtil.isEmptyString(proxyHost) &&  !CodeGenUtil.isEmptyString(proxyPort)){
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
		}
		

		//Library support : deletion of files mentioned in the XML file of the -tlx option
		List<TypeLibraryClassDetails> typeLibraryClassDetailsList = codeGenCtx.getInputOptions().getTypeLibraryClassDetails();
		for(TypeLibraryClassDetails typeLibraryClassDetail : typeLibraryClassDetailsList){
			String filePath = CodeGenUtil.toJavaSrcFilePath(destLocation +  File.separatorChar + "src",typeLibraryClassDetail.getPackageName() + "." + typeLibraryClassDetail.getClassName());
			File javaTypeFile = new File(filePath);
			if(javaTypeFile.exists()){
				boolean isFileDeleted = javaTypeFile.delete();
				if(!isFileDeleted){
					getLogger().log(Level.WARNING, " File " + javaTypeFile.getAbsolutePath() + "  could not be deleted. This file is mentioned thru the option " + InputOptions.OPT_TYPE_LIBRARY_XML_FILE );
				}
			}

		}


	}

	/**
	 * Deletes the Java files ObjectFactory.java and package-info.java from name space SOAConstants.SOA_TYPES_NAMESPACE.
	 * @param ctx
	 * @throws CodeGenFailedException
	 */
	@SuppressWarnings("deprecation")
	private void deleteDuplicateFilesFromDefaultPackage(CodeGenContext ctx)throws CodeGenFailedException {

		NSToPkgMappingList ns2PkgList = ctx.getInputOptions().getNSToPkgMappingList();
		String packageName = getpackageForNamespace(SOAConstants.SOA_TYPES_NAMESPACE,ns2PkgList);
		deleteEachObjectFactoryClass(ctx, packageName);
		deleteEachPackageInfoClass(ctx, packageName);
	}
	
	private String[] getWSDL2JavaToolArgsForTypeLib(TypeLibraryCodeGenContext typeLibraryCodeGenContext, String outputDirectory)
	throws CodeGenFailedException{

		List<String> argsList = new ArrayList<String>();
		TypeLibraryInputOptions inputOptions = typeLibraryCodeGenContext.getTypeLibraryInputOptions();

		argsList.add("-o"); // all generated files location (parent dir)
		argsList.add(outputDirectory);
		argsList.add("-s"); // sync style
		argsList.add("-l"); // language
		argsList.add("java");
		argsList.add("-d"); // data binding
		argsList.add("jaxbri");
		argsList.add("-ss"); // server side
		argsList.add("-ssi"); // Service skeleton interface
		argsList.add("-noWSDL");
		argsList.add("-noBuildXML");
		argsList.add("-noMessageReceiver");
		argsList.add("-p"); // package name for interface
		argsList.add(getTypePackageForTypeLib(typeLibraryCodeGenContext));
		argsList.add("-ebc");   
		argsList.add("org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException");
		argsList.add("-uri");  // wsdl file uri
		argsList.add(inputOptions.getV4WsdlLocation());
		argsList.add("-sin"); //name for the generated interface
		argsList.add(typeLibraryCodeGenContext.getServiceName());
		
		String[] args = argsList.toArray(new String[0]);
		return args;
	}
	
	private String getTypePackageForTypeLib(TypeLibraryCodeGenContext typeLibraryCodeGenContext){
		String typePackage = null;
		TypeLibraryInputOptions inputOptions = typeLibraryCodeGenContext.getTypeLibraryInputOptions();
		if(inputOptions.getV4Pkg() != null)
			typePackage = inputOptions.getV4Pkg();
		else
			typePackage = typeLibraryCodeGenContext.getInterfacePkg();		
		return typePackage;		
	}

	private void deleteObjectFactoryfile(CodeGenContext ctx)throws CodeGenFailedException {

		Set<String> allNamespaces = null;
		try {
			allNamespaces = WsdlParserUtil.getAllTargetNamespces(ctx.getInputOptions().getInputFile());
		}  catch (Exception e) {
			s_logger.log(Level.SEVERE,"could not find namespaces present in the wsdl");
			throw new CodeGenFailedException("TargetNamespace details for schema section of wsdl could not be found ");
		}
		@SuppressWarnings("deprecation")
		NSToPkgMappingList ns2PkgList = ctx.getInputOptions().getNSToPkgMappingList();
		//One objectFactory per namespace hence needs to be deleted.
		for(String currentnamespace : allNamespaces)
		{
			String packageName = getpackageForNamespace(currentnamespace,ns2PkgList);
			 s_logger.log(Level.INFO,"Namespace mapped for ObjectFactory is " +currentnamespace);
             s_logger.log(Level.INFO,"package found for ObjectFactory to be deleted is "+ packageName);
             deleteEachObjectFactoryClass(ctx, packageName);
			
		}
	}

	
	private String getpackageForNamespace(String currentnamespace,NSToPkgMappingList mappingList) {

		if(mappingList==null)
			return WSDLUtil.getPackageFromNamespace(currentnamespace);
		else
		{
			Iterator<NSPkgMappingType> itr = mappingList.getPkgNsMap().iterator();
            while(itr.hasNext())
            {
                  NSPkgMappingType currentMappingType = itr.next();
                  String currentNamespaceInMappings = currentMappingType.getNamespace();
                  if(currentNamespaceInMappings.equals(currentnamespace))
                	  return currentMappingType.getPackage();
                  
            }
		}
		return WSDLUtil.getPackageFromNamespace(currentnamespace);
			
	}


	/**This would delete each objectfactory class generated for the wsdl
	 * @param ctx
	 * @param packageForObjectFact
	 */
	private void deleteEachObjectFactoryClass(CodeGenContext ctx,
			String packageForObjectFact) {
		deleteClassFromGivenPackage(ctx, packageForObjectFact, "ObjectFactory.java");
	}
	/**This would delete each package-info.java generated for the wsdl
	 * @param ctx
	 * @param packageForObjectFact
	 */
	private void deleteEachPackageInfoClass(CodeGenContext ctx,
			String packageForObjectFact) {
		deleteClassFromGivenPackage(ctx, packageForObjectFact, "package-info.java");
	}
	/**
	 * This deletes the java source file for the class name specified in the given package.
	 * @param ctx
	 * @param packageForObjectFact
	 * @param className
	 */
	private void deleteClassFromGivenPackage(CodeGenContext ctx,
			String packageForObjectFact, String className) {
		String projectRoot = ctx.getProjectRoot()==null?ctx.getDestLocation():ctx.getProjectRoot();
		String folderName = CodeGenUtil.getFolderPathFrompackageName(packageForObjectFact);
		String fileName = CodeGenUtil.toOSFilePath(projectRoot)  + CodeGenConstants.GEN_SRC_FOLDER + File.separatorChar +
						  "src" + File.separatorChar + folderName  + className;
		File fileToBeDeleted  = new File(fileName);

		s_logger.log(Level.INFO,"Class '"+className+"' is at " +fileName);
		
			try {
				CodeGenUtil.deleteFile(fileToBeDeleted );
				s_logger.log(Level.INFO,"Deleted '"+className+"' under package "+ packageForObjectFact);
			} catch (Exception exception) {
				fileName = CodeGenUtil.toOSFilePath(ctx.getJavaSrcDestLocation(true)) +
						  "src" + File.separatorChar + folderName  + className;
				fileToBeDeleted  = new File(fileName);
				s_logger.log(Level.INFO,"Class '"+className+"' is at " +fileName);		
				try {
					CodeGenUtil.deleteFile(fileToBeDeleted );
					s_logger.log(Level.INFO,"Deleted "+className+" under package "+ packageForObjectFact);
				} catch (Exception ex) {
					
					s_logger.log(Level.INFO,"Could not delete "+className+" under the package " 
							+ packageForObjectFact
							+ " due to " + ex.getMessage());
				}
			}
	}


	private String getWarningErorFromLog(File wsdl2javaLogFile) {
		StringBuffer stringBuffer = new  StringBuffer();
		BufferedReader br = null;
		
		try {
			 br = new BufferedReader(new FileReader(wsdl2javaLogFile));
			
			String lineStr = null;
			while((lineStr = br.readLine()) != null){
				if( lineStr.startsWith("WARNING:") || lineStr.startsWith("SEVERE:"))
					stringBuffer.append(lineStr).append("\n");
			}
			
		} catch (FileNotFoundException e) {
			getLogger().log(Level.INFO, e.getMessage(), e);
		} catch (IOException e) {
			getLogger().log(Level.INFO, e.getMessage(), e);
		} finally{
			CodeGenUtil.closeQuietly(br);
		}
		
		
		return stringBuffer.toString();
	}


	private void removeFileHandlerForWSDL2JavaLog() {
		LogManager.getGeneralLogger().removeHandler(m_fileHandlerForWSDL2Java);
	}


	private File getLogFileOfWSDL2JavaCall() {

		File tempFile = null;
		try {
			 tempFile = File.createTempFile("wsdl2java", ".log");
			 getLogger().log(Level.INFO,"Location of wsdl2java log file : " + tempFile.getPath());
			 
			 m_fileHandlerForWSDL2Java = new FileHandler(tempFile.getPath());
			 m_fileHandlerForWSDL2Java.setFormatter(new java.util.logging.SimpleFormatter());
			 LogManager.getGeneralLogger().addHandler(m_fileHandlerForWSDL2Java);
			
		} catch (IOException e) {
			getLogger().log(Level.INFO, "Exception while trying to create a temporary file for storing wsdl2java log : \n " + e);
		}
		
		return tempFile;
	}


	private void deleteAdditionalJavaTypeFiles(CodeGenContext codeGenCtx, String destLocation) throws Exception {
		getLogger().entering();
		
		InputOptions inputOptions = codeGenCtx.getInputOptions();
		String serviceName = inputOptions.getServiceAdminName();
		
		SOATypeRegistry typeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		
		Set<String> dependentLibraries = getAllDependentLibraryNames(codeGenCtx,serviceName);
		if(dependentLibraries.size() == 0) {
            getLogger().log(Level.SEVERE,
                            "No Dependent libraries found, duplicate types might be generated! (Check your META-INF/"
                                            + serviceName + "/TypeDependencies.xml)");
			return;
		}

		getLogger().log(Level.INFO, "Dependent libraries are : " + detail(dependentLibraries));
		
//		for(String libraryName : dependentLibraries){
//			try{
//				typeRegistry.addTypeLibraryToRegistry(libraryName);
//			}catch(Exception e){
//				getLogger().log(Level.WARNING, "Exception while trying to populate the registry for library : "+ libraryName +"\n" +
//						"Exception is : " + e.getMessage());
//			}
//		}

		/*
		 * Instead of populating registry library by library, populate at one go for entire list of libraries.
		 * What difference it makes is, TypeInformation.xml is parsed for all libraries first and 
		 * then TypeDependecies.xml is parsed for all libraries. 
		 */
		List<String> dependentLibrariesList = new ArrayList<String>(dependentLibraries);
		try {
			typeRegistry.populateRegistryWithTypeLibraries(dependentLibrariesList);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Exception while trying to populate the registry for dependent libaries : " +
			"Exception is : " + e.getMessage(), e);
		}

		// identify the possible java types to be deleted.
		// Map of <fully qualified java type name> to <type library name>
		Map<String,String> javaTypesToDelete = new HashMap<String,String>();
		for(TypeLibraryType typeLibraryType : typeRegistry.getAllTypeLibraries()) {
			for(TypeInformationType typeInformationType : typeLibraryType.getType()){
				javaTypesToDelete.put(typeInformationType.getJavaTypeName(), typeLibraryType.getLibraryName());
			}
		}

		if(getLogger().isInfoEnabled()) {
            StringBuilder d = new StringBuilder();
            d.append("Dependendant Library Java Types (to be deleted): ");
            d.append("(count: ").append(javaTypesToDelete.size()).append(")");
            int maxTypeLength = 0;
            for(String typeclassname: javaTypesToDelete.keySet()) {
                int len = typeclassname.length();
                if(len > maxTypeLength) {
                    maxTypeLength = len;
                }
            }
	        int idx=0;
	        for(Map.Entry<String,String> entry : javaTypesToDelete.entrySet()) {
                d.append(String.format("%n %3d) %-" + maxTypeLength + "s - [%s]", idx++, entry.getKey(),
                                entry.getValue()));
	        }
            getLogger().log(Level.INFO, d.toString());
		}
			
        String osDestDir = CodeGenUtil.toOSFilePath(destLocation);

        Set<String> searchPaths = new LinkedHashSet<String>();
		searchPaths.add(osDestDir); // Standard Mode
        searchPaths.add(CodeGenUtil.toOSFilePath(codeGenCtx.getJavaSrcDestLocation())); // Standard Mode
        searchPaths.add(CodeGenUtil.toOSFilePath(osDestDir + "src")); // Legacy Mode

        String projectRoot =  CodeGenUtil.toOSFilePath(inputOptions.getProjectRoot());
        if(!CodeGenUtil.isEmptyString(projectRoot)) {
            searchPaths.add(CodeGenUtil.toOSFilePath(projectRoot + "src")); // Legacy Mode
    		searchPaths.add(CodeGenUtil.toOSFilePath(projectRoot + "gen-src")); // Legacy Mode
        }
        
        getLogger().log(Level.INFO, "Search Paths: " + detail(searchPaths));
		
		for(Map.Entry<String,String> entry : javaTypesToDelete.entrySet()) {
		    String className = entry.getKey();
		    for(String searchPath: searchPaths) {
		        File javaFile = new File(CodeGenUtil.toJavaSrcFilePath(searchPath,className));
			
		        getLogger().log(Level.FINE, "Java Type Path : " + javaFile);
		        if(javaFile.exists()) {
		            if(javaFile.delete()) {
                        getLogger().log(Level.INFO, String.format("Deleted Generated Type [%s] (declared in dependent lib [%s]): %s",
                                        className, entry.getValue(), javaFile));
		            } else {
                        getLogger().log(Level.WARNING, String.format("Unable to Delete Generated Type [%s] (declared in dependent lib [%s]): %s",
                                        className, entry.getValue(), javaFile));
		            }
		        }
			}
		}
		
		getLogger().exiting();
	}
	
	private String detail(Collection<String> coll) {
	    if(coll == null) {
	        return "<null>";
	    }
        StringBuilder d = new StringBuilder();
        d.append(coll.getClass().getSimpleName()).append(" of size [");
        d.append(coll.size()).append("]");
        int idx=0;
        for(String s: coll) {
            d.append(String.format("%n %3d) %s", idx++, s));
        }
        return d.toString();
    }


    private  Set<String> getAllDependentLibraryNames(CodeGenContext context,
			String libraryName) throws Exception {

		TypeLibraryParser parser = TypeLibraryParser.getInstance();
		try {
			TypeLibraryInputOptions typelibInputOptions = new TypeLibraryInputOptions();
			typelibInputOptions.setProjectRoot(context.getProjectRoot());
			typelibInputOptions.setMetaSrcLocation(context.getInputOptions().getMetaSrcLocation());
			
			TypeLibraryCodeGenContext tempTypeLibContext = new TypeLibraryCodeGenContext(typelibInputOptions, null);
			parser.processTypeDepXMLFileForGen(tempTypeLibContext, libraryName);
//            for (String libName : parser.getReferredTypeLibraries()) {
//                parser.processTypeDepXMLFile(libName);
//            }
			/*
			 * Instead of parsing just second level, parse till nth level deep.
			 */
			Set<String> orgReferedTypeLibraries = new HashSet<String>( parser.getReferredTypeLibraries() );
			doRecursiveSearchForReferredLibs(parser, orgReferedTypeLibraries);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		return parser.getReferredTypeLibraries();
	}
	
	
	/**
	 * This method parses all the TypeDependcies.xml in the tree and finds out all the libraries referred till nth level.
	 * @param parser
	 * @param typeLibrariesToBeParsed
	 * @throws Exception
	 */
	private void doRecursiveSearchForReferredLibs(TypeLibraryParser parser, Set<String> typeLibrariesToBeParsed) throws Exception{
		Set<String> orgReferedTypeLibraries = new HashSet<String>( parser.getReferredTypeLibraries() );
		Set<String> currentReferedTypeLibraries = processTypeDepXMLForAllLibaries(parser, typeLibrariesToBeParsed);
		if(currentReferedTypeLibraries.size() != orgReferedTypeLibraries.size() ){
			Set<String> deeplyReferedTypeLibraries = getDeeplyAddedLibraries( orgReferedTypeLibraries, currentReferedTypeLibraries );
			doRecursiveSearchForReferredLibs(parser, deeplyReferedTypeLibraries);
		}
	}

	/**
	 * This method compares two sets and finds out the extra ones added in the second set.
	 * @param orgReferedTypeLibraries
	 * @param curReferedTypeLibraries
	 * @return
	 */
	private Set<String> getDeeplyAddedLibraries(Set<String> orgReferedTypeLibraries, Set<String> curReferedTypeLibraries){
		Set<String> deeplyReferedTypeLibraries = new HashSet<String>();
		for(String library : curReferedTypeLibraries){
			if( !(orgReferedTypeLibraries.contains(library)) ){
				deeplyReferedTypeLibraries.add(library);
			}
		}
		return deeplyReferedTypeLibraries;
		
	}
	/**
	 * This method parses the TypeDependcies.xml for the given set of libraries
	 * @param parser
	 * @param referedTypeLibraries
	 * @return
	 * @throws Exception
	 */
	private Set<String> processTypeDepXMLForAllLibaries(TypeLibraryParser parser, Set<String> referedTypeLibraries) throws Exception{
		Iterator it = referedTypeLibraries.iterator();
		while (it.hasNext()) {
			String libName = (String) it.next();
			parser.processTypeDepXMLFile(libName);
		}
		
		return new HashSet<String>( parser.getReferredTypeLibraries() );
	}


	public String wsdl2JavaGenSrcLoc(String srcLocPrefix) {
		
		return CodeGenUtil.toOSFilePath(srcLocPrefix) + "src";
	}



	private String getWSDLFileName(String svcName) {
		return svcName + ".wsdl";

	}

	private String[] getJava2WSDLToolArgs(
				CodeGenContext codeGenCtx,
				String qualifiedIntfName,
				String destLocation) {

		List<String> argsList = new ArrayList<String>();

		InputOptions inputOptions = codeGenCtx.getInputOptions();

		PkgToNSMappingList pkgNsMapList =  getPkgToNSMapList(inputOptions);
		List<String> pkgNSMapList = WSDLUtil.buildPkgNSMapList(pkgNsMapList, JAVA_2_WSDL_PKG_TO_NS_PATTERN);

		argsList.add("-o"); //output location
		argsList.add(destLocation);
		argsList.add("-tn"); // target namespace
		argsList.add(codeGenCtx.getNamespace());
		argsList.add("-tp"); // target namespace prefix
		argsList.add("svc");
		argsList.add("-stn"); // schema target namespace
		argsList.add(codeGenCtx.getNamespace());
		argsList.add("-sn"); // service name
		argsList.add(codeGenCtx.getServiceAdminName());
		argsList.add("-of"); // output file name
		argsList.add(getWSDLFileName(codeGenCtx.getServiceAdminName()));
		argsList.add("-efd"); // element form default
		argsList.add("qualified");
		argsList.add("-afd"); // attribute form default
		argsList.add("unqualified");
		argsList.add("-st"); // style of binding for the WSDL
		argsList.add("document");
		argsList.add("-u"); // Binding use for the WSDL
		argsList.add("literal");
		argsList.add("-l"); // location URL
		if (CodeGenUtil.isEmptyString( inputOptions.getServiceLocation())) {
		argsList.add(CodeGenConstants.DEFAULT_SERVICE_URL + codeGenCtx.getServiceAdminName());
		} else {
			argsList.add(inputOptions.getServiceLocation());
		}
		argsList.add("-cn"); // Class name
		argsList.add(qualifiedIntfName);

		if (codeGenCtx.isBinLocAddedToClasspath() == true) {
			argsList.add("-cp"); // Classpath Entries
			argsList.add(codeGenCtx.getBinLocation());
		}

		if (!pkgNSMapList.isEmpty()) {
			for (String pkgNsMapEntry : pkgNSMapList) {
				argsList.add("-p2n");
				argsList.add(pkgNsMapEntry);
		}
		}

		String[] args = argsList.toArray(new String[0]);
		return args;
	}



	private String[] getWSDL2JavaToolArgs(
			CodeGenContext codeGenCtx,
			String destLocation)
	throws CodeGenFailedException{

		InputOptions inputOptions = codeGenCtx.getInputOptions();
		String wsdlFileLoc = inputOptions.getInputFile();
		
		List<String> argsList = new ArrayList<String>();

		argsList.add("-o"); // all generated files location (paretn dir)
		argsList.add(destLocation);
		argsList.add("-s"); // sync style
		argsList.add("-l"); // language
		argsList.add("java");
		argsList.add("-d"); // data binding
		argsList.add("jaxbri");
		argsList.add("-ss"); // server side
		argsList.add("-ssi"); // Service skelton interface
		argsList.add("-noWSDL");
		argsList.add("-noBuildXML");
		argsList.add("-noMessageReceiver");


		String interfacePkgName = inputOptions.getGenInterfacePackage();
		if (CodeGenUtil.isEmptyString(interfacePkgName)) {
			Map<String, String> ns2PkgMap = WSDLUtil.getNS2PkgMappings(inputOptions);

			try {
				 String className  = WSDLUtil.getInterfaceName(wsdlFileLoc, null, ns2PkgMap,codeGenCtx);
				 interfacePkgName  = CodeGenUtil.getPackageName(className);
				 inputOptions.setGenInterfacePackage(interfacePkgName);
			} catch (PreProcessFailedException ex) {
				throw new CodeGenFailedException(
						"Failed to derive interface package name. ", ex);
				}

		}
		argsList.add("-p"); // package name for interface
		argsList.add(interfacePkgName);




		String interfaceClassName = inputOptions.getGenInterfaceName();
		if(CodeGenUtil.isEmptyString(interfaceClassName)){
			Map<String, String> ns2PkgMap = WSDLUtil.getNS2PkgMappings(inputOptions);

			try {
				 String className   = WSDLUtil.getInterfaceName(wsdlFileLoc, interfacePkgName, ns2PkgMap,codeGenCtx);
				 int indexOfLastDot = className.lastIndexOf(".");
				 interfaceClassName = className.substring(indexOfLastDot+1);
				 inputOptions.setGenInterfaceName(interfaceClassName);
			} catch (PreProcessFailedException ex) {
				throw new CodeGenFailedException(
						"Failed to derive interface class name. ", ex);
				}
		}
		argsList.add("-sin"); //name for the generated interface
		argsList.add(interfaceClassName);


		//argsList.add("-ep");//exclude package, deletes the packages mentioned thru this option.
		//argsList.add("org.ebayopensource.turmeric.runtime.types");

		String derivedNS2PkgStr =  getNS2PkgMappings(inputOptions);
		if(!CodeGenUtil.isEmptyString(derivedNS2PkgStr)) {
			argsList.add("-ns2p");
			argsList.add(derivedNS2PkgStr);
		}

		
		for(String bindingFileName : inputOptions.getBindingFileNames()){
			if(!CodeGenUtil.isEmptyString(bindingFileName)){
				argsList.add("-EbindingFileName");
				argsList.add(bindingFileName);
			}
		}
		


		String proxyHost = codeGenCtx.getInputOptions().getHttpProxyHost();
		String proxyPort = codeGenCtx.getInputOptions().getHttpProxyPort();
		if( !CodeGenUtil.isEmptyString(proxyHost) &&  !CodeGenUtil.isEmptyString(proxyPort)){
			argsList.add("-http-proxy-host");
			argsList.add(proxyHost);
			argsList.add("-http-proxy-port");
			argsList.add(proxyPort);

		}
		
		argsList.add("-ebc");   
		argsList.add("org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException");
		
		argsList.add("-uri");  // wsdl file uri
		argsList.add(wsdlFileLoc);

		String[] args = argsList.toArray(new String[0]);

		return args;
	}

	private String getNS2PkgMappings(InputOptions inputOptions){
		String derivedNS2PkgStr="";
		Map<String,String> ns2PkgMap = WSDLUtil.getNS2PkgMappings(inputOptions);

		for(Map.Entry<String, String> keyValuePair : ns2PkgMap.entrySet()){
			derivedNS2PkgStr += keyValuePair.getKey() + "=" + keyValuePair.getValue() + ",";
		}

		if(derivedNS2PkgStr.endsWith(","))
		  derivedNS2PkgStr = derivedNS2PkgStr.substring(0, derivedNS2PkgStr.length() - 1 );


		return derivedNS2PkgStr;
	}


	private PkgToNSMappingList getPkgToNSMapList(InputOptions inputOptions) {
		return inputOptions.getPkgNSMappings();
	}


	private void prettyFormatWSDL(String wsdlFilePath) {
		File wsdlFile = new File(wsdlFilePath);
		if (wsdlFile.exists()) {
			WSDLPrettyFormatter wsdlPrettyFormatter = new WSDLPrettyFormatter();
			wsdlPrettyFormatter.prettyFormat(wsdlFile);
		}
	}







	private String  getOpNameToCemcMapString(OpNameToCemcMappingList opNameToCemcMappings) {

		if (opNameToCemcMappings == null ||
			opNameToCemcMappings.getOpNameCemcMap().size() == 0) {
			return null;
		}
		else {
			StringBuilder strBuilder = new StringBuilder();
			for (OpNameCemcMappingType opNameCemcMapEntry : opNameToCemcMappings.getOpNameCemcMap()) {
				strBuilder.append(opNameCemcMapEntry.getOperationName())
						.append("=")
						.append(opNameCemcMapEntry.getCustomErrMsgClass())
						.append(",");
			}
			strBuilder.setLength(strBuilder.length()-1);

			return strBuilder.toString();
		}

	}

}
