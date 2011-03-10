/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.builders;

import static org.ebayopensource.turmeric.tools.library.TypeLibraryConstants.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import javax.xml.bind.JAXB;

import org.apache.commons.io.FileUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenClassLoader;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.SOAGlobalRegistryFactory;
import org.ebayopensource.turmeric.tools.library.SOATypeRegistry;
import org.ebayopensource.turmeric.tools.library.TypeInformationParser;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.AdditionalXSDInformation;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;

import org.ebayopensource.turmeric.common.config.TypeInformationType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;
import com.sun.java.xml.ns.jaxb.Bindings;
import com.sun.java.xml.ns.jaxb.BindingsClassType;
import com.sun.java.xml.ns.jaxb.BindingsLevelThree;
import com.sun.java.xml.ns.jaxb.BindingsLevelTwo;
import com.sun.java.xml.ns.jaxb.SchemaBindings;

public class CodeGenTypeLibraryGenerator {

	private static CallTrackingLogger s_logger = LogManager
			.getInstance(CodeGenTypeLibraryGenerator.class);

	private static final String TYPE_INFORMATION_TEMPLATE = "org/ebayopensource/turmeric/tools/library/template/typeinformation.tpt";
	private static final String TYPE_INFORMATION_TEMPLATE_NO_CATEGORY = "org/ebayopensource/turmeric/tools/library/template/typeinformation_without_category_template.tpt";
	private static final String OBJECTFACTORY_FILE_NAME = "ObjectFactory.java";
	private static final String PACKAGEINFO_FILE_NAME = "package-info.java"; 
	private static final String DEPENDENT_JARS_DELIMITER = ";";

	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	private static Set<String> depJarsAndPaths = new HashSet<String>();

	



	private static CallTrackingLogger getLogger() {
		return s_logger;
	}

	/**
	 * 
	 * @param projectRoot
	 * @param libraryName
	 * @param stagingLocation
	 * @param dependentTypeLibs
	 * @param codeGenCtx 
	 * @throws Exception
	 */
	public static void genTypeCleanBuildTypeLibrary(String projectRoot,
			String libraryName, String stagingLocation,
			String dependentTypeLibs, TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException,Exception {
		
		// TODO: This entire process should be a class of its own, that looks up information in a pre-described order.
		
		String libraryVersion = codeGenCtx.getTypeLibraryInputOptions().getLibraryVersion();
		String libNamespace = codeGenCtx.getTypeLibraryInputOptions().getLibraryNamespace();
		String libCategory = codeGenCtx.getTypeLibraryInputOptions().getLibraryCategory();

		// Are any of these values not specified on the command line?
		if(CodeGenUtil.isEmptyString(libraryVersion) || 
				CodeGenUtil.isEmptyString(libNamespace) ||
				CodeGenUtil.isEmptyString(libCategory))
		{
			// We need these values from the "type_library_project.properties" file.
			String propertiesFilePath = TypeLibraryUtilities.toOSFilePath(projectRoot) + TypeLibraryProjectPropertiesGenerator.TYPE_LIB_PRJ_PROPERTIES_FILE_NAME;
			
			Properties typeLibraryProperties = TypeLibraryUtilities.getPropertiesFromFile(propertiesFilePath);

			if(CodeGenUtil.isEmptyString(libraryVersion)) {
				libraryVersion = typeLibraryProperties.getProperty(TypeLibraryConstants.TYPE_LIBRARY_VERSION);
			}
			
			if(CodeGenUtil.isEmptyString(libNamespace)) {
				libNamespace = typeLibraryProperties.getProperty(TypeLibraryConstants.TYPE_LIBRARY_NAMESPACE);
			}
			
			if(CodeGenUtil.isEmptyString(libNamespace)) {
				libCategory = typeLibraryProperties.getProperty(TypeLibraryConstants.TYPE_LIBRARY_CATEGORY);
			}
		}
		
		// Are any of these values not specified from the properties file?
		if(CodeGenUtil.isEmptyString(libraryVersion) || 
				CodeGenUtil.isEmptyString(libNamespace) ||
				CodeGenUtil.isEmptyString(libCategory))
		{
			// We need these values from the "TypeInformation.xml" file.
			String typeInfoXMLFilePath = TypeLibraryUtilities.getTypeInfoFolder(
					codeGenCtx, libraryName)
					+ File.separator + TYPE_INFORMATION_FILE_NAME;

			File typeInfoFile = new File(typeInfoXMLFilePath);
			if(typeInfoFile.exists()){
				TypeLibraryType typeLibraryType = JAXB.unmarshal(typeInfoFile , TypeLibraryType.class);
				if (CodeGenUtil.isEmptyString(libraryVersion)) {
					libraryVersion = typeLibraryType.getVersion();
				}
				if (CodeGenUtil.isEmptyString(libNamespace)) {
					libNamespace   = typeLibraryType.getLibraryNamespace();
				}
				if (CodeGenUtil.isEmptyString(libCategory)) {
					libCategory    = typeLibraryType.getCategory();
				}
			}
		}

		// Default any values that are *still* unspecified
		if (CodeGenUtil.isEmptyString(libraryVersion)) {
			libraryVersion = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_VERSION;
		}
		if (CodeGenUtil.isEmptyString(libNamespace)) {
			libNamespace = TypeLibraryConstants.TYPE_INFORMATION_NAMESPACE;
		}
		if (CodeGenUtil.isEmptyString(libCategory)) {
			libCategory = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_CATEGORY;
		}

		//Setting codegenContext
		codeGenCtx.getLibrariesNamespace().put(libraryName, libNamespace);
		
		genTypeCleanBuildTypeLibraryPrivate(libraryName,
				libraryVersion, libCategory, stagingLocation,
				dependentTypeLibs, libNamespace,codeGenCtx);
	}
	


	/**
	 * 
	 * @param projectRoot
	 * @param libraryName
	 * @param libraryVersion
	 * @param libraryCategory
	 * @param codeGenCtx 
	 * @throws Exception
	 */
	private static void genTypeCleanBuildTypeLibraryPrivate(
			String libraryName, String libraryVersion, String libraryCategory,
			String stagingLocation, String dependentTypeLibs,
			String libNamespace, TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException,Exception {
		
		clean(codeGenCtx);

		String episodeSrcPath = TypeLibraryUtilities.getEpisodeFolder(codeGenCtx,libraryName);
		String typesSrcPath = TypeLibraryUtilities.getTypesFolder(codeGenCtx,
				libraryName);
		//change the typesSrc path
		typesSrcPath = typesSrcPath + File.separator + libraryName;
		
		String sunJaxbEpisodeSrcpath = TypeLibraryUtilities
				.getSunJaxBEpisodeFolder(codeGenCtx,libraryName);

		Map<String, TypeInformationType> modifiedTypeMap = new HashMap<String, TypeInformationType>();
		Map<String, String> typesVersion = new HashMap<String, String>();
		String[] xsdTypes = TypeLibraryUtilities.getFilesInDir(typesSrcPath,".xsd");
		// get the xsd present in the old structure if there is none in new Structure
		if(xsdTypes==null || (xsdTypes!=null&&xsdTypes.length==0))
		{
			xsdTypes = TypeLibraryUtilities.getXsdPresentInolderPath(codeGenCtx,libraryName);
		}

		TypeLibraryUtilities.createProjectSubFolders(codeGenCtx);
		HashMap<String, List<Exception>> XSDsWithError = new HashMap<String, List<Exception>>();
		ArrayList<AdditionalXSDInformation> simpleTypesAdditionalXSDInfo = new ArrayList<AdditionalXSDInformation>();
		
		runXJC(libraryName, xsdTypes, stagingLocation,
				dependentTypeLibs, modifiedTypeMap, typesVersion,
				libNamespace, XSDsWithError, simpleTypesAdditionalXSDInfo,codeGenCtx);

		createTypeInformationFile(codeGenCtx, libraryName, libNamespace,libraryVersion,libraryCategory);

		List<String> xsdTypesList = new ArrayList<String>(xsdTypes.length);
		for (int i = 0; i < xsdTypes.length; i++)
			xsdTypesList.add(xsdTypes[i]);

		EpisodeGenerator.genSunJaxbEpisodeFile(episodeSrcPath, 
				xsdTypesList, sunJaxbEpisodeSrcpath, typesVersion,simpleTypesAdditionalXSDInfo);

		TypeInformationParser typeInformationParser = TypeInformationParser
				.getInstance();
		typeInformationParser.updateTypeInformationXMLFile(codeGenCtx,
				libraryName, modifiedTypeMap);

		/*
		 * Report XSDs which had errors
		 */
		reportXSDsForError(XSDsWithError);

	}

	/**
	 * Codegen for Incremental Build
	 * 
	 * @param projectRoot
	 * @param libraryName
	 * @param types
	 * @param codeGenCtx 
	 * @throws Exception
	 */
	public static void genTypeIncrBuildTypeLibrary(String libraryName, List<String> types, TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException, Exception {
		String episodeSrcPath = TypeLibraryUtilities.getEpisodeFolder(codeGenCtx,libraryName);
		String sunJaxbEpisodeSrcpath = TypeLibraryUtilities
				.getSunJaxBEpisodeFolder(codeGenCtx,libraryName);

		Map<String, TypeInformationType> modifiedTypeMap = new HashMap<String, TypeInformationType>();
		Map<String, String> typesVersion = new HashMap<String, String>();
		String[] strTypes = types.toArray(new String[0]);

		String libraryNameSpace = getNamespaceOfLibrary(codeGenCtx,
				libraryName);
	    //		Setting codegenContext
		codeGenCtx.getLibrariesNamespace().put(libraryName, libraryNameSpace);
		
		HashMap<String, List<Exception>> XSDsWithError = new HashMap<String, List<Exception>>();
		ArrayList<AdditionalXSDInformation> simpleTypesAdditionalXSDInfo = new ArrayList<AdditionalXSDInformation>();
		runXJC(libraryName, strTypes, null, null, modifiedTypeMap,
				typesVersion, libraryNameSpace, XSDsWithError, simpleTypesAdditionalXSDInfo,codeGenCtx);

		List<String> xsdTypesList = new ArrayList<String>(strTypes.length);
		for (int i = 0; i < strTypes.length; i++)
			xsdTypesList.add(strTypes[i]);

		EpisodeGenerator.genSunJaxbEpisodeFile(episodeSrcPath, 
				xsdTypesList, sunJaxbEpisodeSrcpath, typesVersion,simpleTypesAdditionalXSDInfo);

		TypeInformationParser typeInformationParser = TypeInformationParser
				.getInstance();
		typeInformationParser.updateTypeInformationXMLFile(codeGenCtx,
				libraryName, modifiedTypeMap);


		/*
		 * Report XSDs which had errors
		 */
		reportXSDsForError(XSDsWithError);

	}

	
	/**
	 * 
	 * @param projectRoot
	 * @param libraryName
	 * @return
	 * @throws Exception
	 */
	private static String getNamespaceOfLibrary(TypeLibraryCodeGenContext codeGenCtx,
			String libraryName) throws BadInputValueException,Exception {

		String libraryNamespace = null;

		String typeInfoXMLFilePath = TypeLibraryUtilities.getTypeInfoFolder(
				codeGenCtx, libraryName)
				+ File.separator + TYPE_INFORMATION_FILE_NAME;

		File typeInfoFile = new File(typeInfoXMLFilePath);
		if (!typeInfoFile.exists())
			throw new Exception(
					"Could not find the TypeInformation.xml file for library "
							+ libraryName + "  at location : "
							+ typeInfoXMLFilePath);

		try {
			TypeLibraryType typeLibraryType = JAXB.unmarshal(typeInfoFile,
					TypeLibraryType.class);
			TypeInformationParser.getInstance().populateTypeInfoGlobalTable(
					typeLibraryType, libraryName);
			libraryNamespace = typeLibraryType.getLibraryNamespace();

		} catch (Throwable t) {
			String errMsg = "Unable to parse the TypeInformation.xml file, of library "
				+ libraryName + " its content could be invalid";
			getLogger().log(
					Level.SEVERE,
					errMsg, t);
			throw new BadInputValueException(errMsg,t);
		}

		return libraryNamespace;
	}

	/**
	 * Codegen for Adding the Type
	 * 
	 * @param projectRoot
	 * @param libraryName
	 * @param xsdTypes
	 * @param codeGenCtx 
	 * @throws Exception
	 */
	public static void genTypeAddType(String libraryName,
			List<String> xsdTypes, TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException,Exception {
		genTypeIncrBuildTypeLibrary(libraryName, xsdTypes,codeGenCtx);
	}

	/**
	 * 
	 * @param projectRoot
	 * @param libraryName
	 * @param xsdTypes
	 * @param codeGenCtx 
	 * @throws IOException
	 */
	public static void genTypeDeleteType(String libraryName, List<String> xsdTypes, TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException,Exception {

		String genSrcPath = codeGenCtx.getGenJavaSrcDestFolder();
		String typesSrcPath = TypeLibraryUtilities.getNewTypesFolderLocation(codeGenCtx, libraryName);
		String episodeSrcPath = TypeLibraryUtilities.getEpisodeFolder(codeGenCtx,libraryName);
		
		String sunJaxbEpisodeSrcpath = TypeLibraryUtilities
				.getSunJaxBEpisodeFolder(codeGenCtx,libraryName);
		Iterator<String> iterator = xsdTypes.iterator();
		while (iterator.hasNext()) {

			String xsdType = iterator.next();
			String episodeFile = TypeLibraryUtilities
					.getEpisodeFileName(xsdType)
					+ ".episode";
			String episodeFilePath = episodeSrcPath + File.separator
					+ episodeFile;
			String xsdSrcPath = typesSrcPath + File.separator + xsdType ;

			/*
			 * Before deleting the XSD , parse the XSD to find out whether this represents a simple type or a ComplexType
			 */
			
			AdditionalXSDInformation additionalXSDInformation = TypeLibraryUtilities.parseTheXSDFile(xsdSrcPath);
			//before deleting xsd, check if the xsdPath was changed to the older structure
			if(additionalXSDInformation.isXsdPathChanged())
			{
				xsdSrcPath = TypeLibraryUtilities.getOlderXsdSrcPath(xsdSrcPath);
			}
			// Delete the xsd
			if(additionalXSDInformation.isDoesFileExist())
				CodeGenUtil.deleteFile(new File(xsdSrcPath));
			else{
				getLogger().log(Level.INFO, "The type file : " + xsdSrcPath + " does not exist and therefore it could not be deleted.");
				return;
			}

			
			
			/*
			 *  Delete the Java artifact generated, for simpe types the java type information won't be present in the master episode file
			 */
			String javaClassName = null;
			String typeName = additionalXSDInformation.getTypeName();
			if(typeName == null)
				typeName = TypeLibraryUtilities.filterExtensionFromXSDFileName(xsdType);
			
			
			if(additionalXSDInformation.isSimpleType()){
				javaClassName = TypeLibraryUtilities.getPackageFromNamespace(additionalXSDInformation.getTargetNamespace());
				javaClassName += "." + typeName;
			}
			else
				javaClassName = EpisodeGenerator.getJavaClassName(episodeFilePath);

			if(!TypeLibraryUtilities.isEmptyString(javaClassName)) {
				String javaPackage = CodeGenUtil.getPackageName(javaClassName);
				String javaName = TypeLibraryUtilities
						.getJavaClassName(javaClassName);
				String javaPackagePath = javaPackage.replaceAll("\\.", "/");
				String javaSrcPath = genSrcPath + File.separator + javaPackagePath;
				String javaSrcName = CodeGenUtil.toJavaSrcFilePath(javaSrcPath,
						javaName);
			
				CodeGenUtil.deleteFile(new File(javaSrcName));
			}

			
			
			
			/* 
			 * Delete the coressponding episode file if one exists, simple types may not have a episode file
			 */
			
			CodeGenUtil.deleteFile(new File(episodeFilePath));

		}

		// Regenerate the sun-jaxb.episode file for all of the remaining types
		// TODO : this logic has to be improved, since this regenerates all the
		// episodes
		String[] remainingXSDTypes = TypeLibraryUtilities.getFilesInDir(
				typesSrcPath, ".xsd");
		List<String> xsdTypesList = new ArrayList<String>();
		if(remainingXSDTypes != null){
			for (int i = 0; i < remainingXSDTypes.length; i++)
				xsdTypesList.add(remainingXSDTypes[i]);			
		}

		Map<String, String> typesVersion = new HashMap<String, String>();
		EpisodeGenerator.genSunJaxbEpisodeFile(episodeSrcPath, 
				xsdTypesList, sunJaxbEpisodeSrcpath, typesVersion,null);

		// Code to update the TypeInformation.xml and thereby the global
		// registry
		TypeInformationParser typeInformationParser = TypeInformationParser
				.getInstance();
		typeInformationParser.deleteTypesFromInformationXMLFile(codeGenCtx,
				libraryName, xsdTypes);

	}

	/**
	 * 
	 * @param projectRoot  Project root
	 * @param libraryName  Name of the library
	 * @param libNamespace Namespace of the library 
	 * @param libraryVersion Version of the library
	 * @param libraryCategory Category of the library
	 * @throws Exception
	 */
	public static void genTypeCreateTypeLibrary(
			String libraryName, String libNamespace, String libraryVersion, String libraryCategory, TypeLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException,Exception {
		
		if (CodeGenUtil.isEmptyString(libraryVersion))
			libraryVersion = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_VERSION;
		if (CodeGenUtil.isEmptyString(libNamespace))
			libNamespace = TypeLibraryConstants.TYPE_INFORMATION_NAMESPACE;
		if (CodeGenUtil.isEmptyString(libraryCategory))
			libraryCategory = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_CATEGORY;

		
		createBaseFolders(codeGenCtx);
		createTypeInformationFile(codeGenCtx, libraryName, libNamespace,libraryVersion,libraryCategory);
		updateGlobalTableWithNewLibrary(codeGenCtx, libraryName);
	}

	
	private static void updateGlobalTableWithNewLibrary(TypeLibraryCodeGenContext codeGenCtx,
			String libraryName) throws Exception {
		String typeInfoXMLFilePath = TypeLibraryUtilities.getTypeInfoFolder(
				codeGenCtx, libraryName)
				+ File.separator + TYPE_INFORMATION_FILE_NAME;

		File typeInfoFile = new File(typeInfoXMLFilePath);
		if (!typeInfoFile.exists())
			throw new Exception(
					"Could not find the TypeInformation.xml file for library "
							+ libraryName + "  at location : "
							+ typeInfoXMLFilePath);

		try {
			TypeLibraryType typeLibraryType = JAXB.unmarshal(typeInfoFile,
					TypeLibraryType.class);
			TypeInformationParser.getInstance().populateTypeInfoGlobalTable(
					typeLibraryType, libraryName);

		} catch (Throwable t) {
			getLogger().log(
					Level.SEVERE,
					"Unable to parse the TypeInformation.xml file, of library "
							+ libraryName + " its content could be invalid", t);
			throw new Exception(t);
		}

	}


	private static void runXJC(String libraryName,
			String[] xsdTypes, String stagingLocation,
			String dependentTypeLibs,
			Map<String, TypeInformationType> modifiedTypeMap,
			Map<String, String> typesVersion, String libNamespace,
			HashMap<String, List<Exception>> XSDsWithError,
			ArrayList<AdditionalXSDInformation> simpleTypesAdditionalXSDInfo, TypeLibraryCodeGenContext codeGenCtx
			) throws CodeGenFailedException,Exception {

		String genJavaDestPath = codeGenCtx.getGenJavaSrcDestFolder();
		String episodeDestpath = TypeLibraryUtilities.getEpisodeFolder(codeGenCtx,libraryName);
		String typesSrcPath = TypeLibraryUtilities.getNewTypesFolderLocation(codeGenCtx, libraryName);
		String dependentJarPathForPLugin = null;
		String dependentJarForPlugin = null;
		String xjcArguments[] = null;
		
		boolean isPlugin = false;
		String dependentJarPath = null;


		for (int i = 0; i < xsdTypes.length; i++) {
			String xsdFileName = xsdTypes[i];

			String xsdSrcPath = typesSrcPath + File.separator + xsdFileName;
			int index = xsdTypes[i].lastIndexOf(".");
			String episodeFileName = xsdTypes[i].substring(0, index);
			String episodePath = episodeDestpath + File.separator
					+ episodeFileName.concat(".episode");
			
			/* arajmony */
			
			
			String typeName = TypeLibraryUtilities.getTypeNameFromFileName(xsdFileName);
			setDependentTypeDetailsOnCodegenctx(libraryName,typeName,codeGenCtx);
			
			Map<String,Set<String>> depLibAndTypes = codeGenCtx.getTypesAndDependentTypesMap();
			
			Set<String> depLibrariesSet = new HashSet<String>(depLibAndTypes.keySet());
			getLogger().log(Level.INFO, "The original dependent libraries are : " + depLibrariesSet);
			
			Set<String> depLibrariesOtherThanCurrent  = new HashSet<String>(depLibrariesSet);
			depLibrariesOtherThanCurrent.remove(libraryName);
			getLogger().log(Level.INFO, "The dependent libraries set for deleting gen files are : " + depLibrariesOtherThanCurrent);
			
			

			/* arajmony */
			
			if (stagingLocation != null) {
				// For V3build
				isPlugin = false;
				getLogger().log(Level.INFO, "Call is from V3Build.");
				
				dependentJarPath = getDependentJarFromV3(
						episodeFileName, stagingLocation, dependentTypeLibs);
				
				xjcArguments = getXJCArgs(xsdFileName, dependentJarPath, depLibrariesSet, codeGenCtx).split(",");
				
			} else {
				// Call is from plugin
				isPlugin = true;
				getLogger().log(Level.INFO, "Call is from plugin. NEW CODE7");

				dependentJarPathForPLugin = getDependentJarFromPlugin(depLibrariesOtherThanCurrent);
				
				if (!isDependentPathLength(dependentJarPathForPLugin)) {
					dependentJarForPlugin = "";
				} else {
					String dependentJars[] = dependentJarPathForPLugin.split("!");
					dependentJarForPlugin = dependentJars[0];
				}
				xjcArguments = getXJCArgsForPlugin(dependentJarForPlugin,
						episodePath, genJavaDestPath, xsdSrcPath, 
						libraryName,depLibrariesSet,codeGenCtx).split(",");

			}

			/*
			 * Parse the XSD and get information like targetnamespace , version ,
			 * simple or complex type etc
			 */
			AdditionalXSDInformation additionalXSDInformation = TypeLibraryUtilities.parseTheXSDFile(xsdSrcPath);
			boolean isValid = validateAdditionalXSDInformation(additionalXSDInformation,XSDsWithError,libNamespace,xsdFileName);
			if(! isValid)
				continue; // its not a valid XSD move to the next one
			else {
				//Add all valid types to the modified map , its possible that such an XSD can still fail XJC compilation for want of dependent 
				//XSD types. But even if such types fail compilation they need to be in the TypeInformation.xml 
				// Refer Bug http://quickbugstage.arch.ebay.com/show_bug.cgi?id=2415
				addTypeToModifiedMap(modifiedTypeMap,additionalXSDInformation,xsdFileName);
			}
				
			if (additionalXSDInformation.isSimpleType()) {
				simpleTypesAdditionalXSDInfo.add(additionalXSDInformation);
			}
			
			addTypeVersion(typesVersion,additionalXSDInformation, xsdFileName);
			
		    //before calling xjc, check if the types were present in the older structure and change arguments accordingly
			if(additionalXSDInformation.isXsdPathChanged())
			{
			
			xjcArguments = getModifiedXjcArguments(xjcArguments,xsdSrcPath);
			}

			/*
			 * The main call to XJC
			 */
			try {
				
				getLogger().log(Level.INFO, "Arguments to XJC for type : " + xsdFileName + "\n" + Arrays.toString(xjcArguments) );
				
				ToolsXJCWrappper.runXJC(xjcArguments,XSDsWithError,xsdFileName);

				getLogger().log(Level.INFO, "Returned from the call to XJC");
				
				//Need to delete ObjectFactory and package_info.refer to http://quickbugstage.arch.ebay.com/show_bug.cgi?id=11832
				
				String packageForObjectFactory = TypeLibraryUtilities.getPackageFromNamespace(libNamespace);
				deleteObjectfactoryAndPackageInfo(codeGenCtx,
						packageForObjectFactory);
				
				if(additionalXSDInformation.isSimpleType()){
					postProcessSimpleType(codeGenCtx,additionalXSDInformation,xsdFileName);
				}
				
			
				try{
					long startTime = new Date().getTime();
					if(isPlugin)
						deleteTheGeneratedDependentTypesForPlugin(depLibrariesOtherThanCurrent,codeGenCtx);
					else
						deleteTheGeneratedDependentTypesForV3(dependentJarPath,codeGenCtx);
					
					long endTime = new Date().getTime();
					getLogger().log(Level.INFO, "Time spent on trying to identify and delete additional types : " +  (endTime - startTime ) + " milliSeconds");

				}catch(Exception e){
					getLogger().log(Level.WARNING, "exception : " +e, e);
				}
				
			} catch (Throwable e) {
				getLogger().log(Level.WARNING,
						"XJC throws Exception for type : " + xsdFileName, e);
				// Should proceed with the rest of XSDs even if a XSD fails
				// compilation
				addExceptionsToXSDErrorList(XSDsWithError, new Exception(e), xsdFileName);
			}
		}

	}

	/**
	 * @param projectRoot
	 * @param packageForObjectFactory
	 */
	private static void deleteObjectfactoryAndPackageInfo(TypeLibraryCodeGenContext codeGenCtx,
			String packageForObjectFactory ) {
		
		String objectFactoryPath = codeGenCtx.getGenJavaSrcDestFolder() + File.separator + packageForObjectFactory.replace('.', File.separatorChar) 
		                             + File.separator;
		
		s_logger.log(Level.INFO,"Relative path for Objectfactory and packageInfo determined is :" + objectFactoryPath);
		
		String objectFactoryAbsolutePath = objectFactoryPath + OBJECTFACTORY_FILE_NAME;
		String packageinfoAbsolutePath = objectFactoryPath + PACKAGEINFO_FILE_NAME; 
		
		try
		{
		TypeLibraryUtilities.deleteFile(new File(objectFactoryAbsolutePath));
		s_logger.log(Level.INFO,"Successfully Deleted " + objectFactoryAbsolutePath);
		}
		catch(IOException e)
		{
			s_logger.log(Level.WARNING,"Could Not Delete " + objectFactoryAbsolutePath);
		}
		//Delte packageinfo file if it exists.
		File packageinfoFile = new File(packageinfoAbsolutePath);
		if(! packageinfoFile.exists())
			return;
		try
		{
		TypeLibraryUtilities.deleteFile(packageinfoFile);
		s_logger.log(Level.INFO,"Successfully Deleted " + packageinfoAbsolutePath);
		}
		catch(IOException e)
		{
			s_logger.log(Level.WARNING,"Could Not Delete " + packageinfoAbsolutePath);
		}
	}

	
	/*
	 * This method modifies the xjcArguments and changes the XsdSourcePath to the older structure.
	 */
	private static String[] getModifiedXjcArguments(String[] xjcArguments,
			String newXsdPath) {
		String olderXsdPath = TypeLibraryUtilities.getOlderXsdSrcPath(newXsdPath);
		int indexOfNewXsdPath=0;
		for(int i=0;i<xjcArguments.length;i++)
		{
			if(xjcArguments[i].contentEquals("-d"))
			{
				indexOfNewXsdPath = i+2;
				break;
			}
		}
		xjcArguments[indexOfNewXsdPath] = olderXsdPath;
		return xjcArguments;
		
	}

	private static void postProcessSimpleType(TypeLibraryCodeGenContext codeGenCtx, AdditionalXSDInformation additionalXSDInformation, String xsdTypeName) {
		
		String typeName = TypeLibraryUtilities.getTypeNameFromFileName(xsdTypeName);
		
		hasJavaFileBeenGenerated(codeGenCtx,additionalXSDInformation,typeName);
		
		if(!additionalXSDInformation.isJavaFileGenerated()){
			TypeLibraryTypesAndJavaPropertiesGenerator generator =TypeLibraryTypesAndJavaPropertiesGenerator.getInstance();
			generator.createUpdatePropertiesFile(codeGenCtx,additionalXSDInformation,typeName);
		}
		
	}
	

	private static void hasJavaFileBeenGenerated(TypeLibraryCodeGenContext codeGenCtx, AdditionalXSDInformation additionalXSDInformation,
			String typeName) {
		// XJC does not generate the java type files for certain simple and enum types .
		// http://quickbugstage.arch.ebay.com/show_bug.cgi?id=2118  - this is one issue bcos of this 
		
		if(!additionalXSDInformation.isSimpleType())
			return;
		
		typeName = CodeGenUtil.makeFirstLetterUpper(typeName);
		
		String libraryName = codeGenCtx.getLibraryName();
		String libraryNS   = codeGenCtx.getLibrariesNamespace().get(libraryName);
		
		
		String javaDest = TypeLibraryUtilities.getGenSrcFolder(codeGenCtx, libraryName);
		
		String packageName = TypeLibraryUtilities.getPackageFromNamespace(libraryNS);
		packageName = packageName + "." + typeName ;
		String javaFilePath = packageName.replace(".", File.separator);
		javaFilePath = javaFilePath +  ".java";
		
		
		String filePath = TypeLibraryUtilities.normalizePath(javaDest) + javaFilePath;
		
		File file = new File(filePath);
		if(file.exists())
			additionalXSDInformation.setJavaFileGenerated(true);
		else{
			additionalXSDInformation.setJavaFileGenerated(false);
			getLogger().log(Level.FINE, "Java type file was not generated for the simple type " + typeName);
		}
		
		
	}

	//simple types are getting regenerated even if a manual entry is added for the simple types in sun-jaxb.episode file
	private static void deleteTheGeneratedDependentTypesForV3(String dependentJarPath,TypeLibraryCodeGenContext codeGenCtx) {
		
		if(TypeLibraryUtilities.isEmptyString(dependentJarPath))
			return;
			
		String[] dependentLibrariesJarPaths = dependentJarPath.split(DEPENDENT_JARS_DELIMITER);
		
		if(dependentLibrariesJarPaths.length == 0)
		   return;

		
		//identify the possible java types to be deleted
		List<String> javaTypesToDelete = new ArrayList<String>();
		//Need to check if ObjectFactory is being deleted.
		//One ObjectFactory per library.Need to get a set of all namespaces being referred.
		Set<String> dependentLibsNamespace = new HashSet<String>();

		for(String currDepLibraryJar : dependentLibrariesJarPaths){
		String currLibraryName = getLibraryNameFromJarFilePath(currDepLibraryJar);
			//for referred libraries  the TypeInformation.xml from the related jar . this jar won't be available in classpath and hence has to be mnaually processed
		String typeInformationFileRelativePath = TypeLibraryConstants.META_INF_FOLDER  + File.separator + currLibraryName 
							+ File.separator + TypeLibraryConstants.TYPE_INFORMATION_FILE_NAME;
		
		
		File theJarFile = new File(currDepLibraryJar);
		if (!theJarFile.exists())
				continue;

			JarFile jarFile = null;
			try {
				jarFile = new JarFile(currDepLibraryJar);
				JarEntry entry = jarFile.getJarEntry(typeInformationFileRelativePath);
				if (entry == null)
					entry = jarFile.getJarEntry(typeInformationFileRelativePath.replace("\\", "/"));
				if (entry == null) {
					getLogger().log(
									Level.WARNING,
									"Could not find the TypeInformation.xml file for the dependent library represented by the jar : "
											+ currDepLibraryJar);
					continue;
				}
				InputStream inputStream = jarFile.getInputStream(entry);
				
				if(inputStream != null){
					
					TypeLibraryType typeLibraryType = JAXB.unmarshal(inputStream,TypeLibraryType.class );
					if(typeLibraryType != null){
						dependentLibsNamespace.add( typeLibraryType.getLibraryNamespace() );
						for(TypeInformationType typeInformationType : typeLibraryType.getType())
							javaTypesToDelete.add(typeInformationType.getJavaTypeName());
					}
					
				}

			} catch (IOException e) {
				getLogger().log(Level.WARNING, "Exception while parsing the TypeInformation.xml of jar " + currDepLibraryJar, e);
			}
		}
		
		deleteJavaTypes(javaTypesToDelete,codeGenCtx);
		findPackageForObjectFactoriesAndDelete(codeGenCtx,dependentLibsNamespace);
	}

	private static void deleteTheGeneratedDependentTypesForPlugin(Set<String> depLibrariesSetForDeletingFiles, TypeLibraryCodeGenContext codeGenCtx) {
		
		if(depLibrariesSetForDeletingFiles.size() == 0)
			return;
		
		SOATypeRegistry typeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		
		for(String libraryName : depLibrariesSetForDeletingFiles){
			try{
				typeRegistry.addTypeLibraryToRegistry(libraryName);
			}catch(Exception e){
				getLogger().log(Level.WARNING, "Exception while trying to populate the registry for library : "+ libraryName +"\n" +
						"Exception is : " + e.getMessage());
			}
		}
		//Need to check if ObjectFactory is being deleted.
		//One ObjectFactory per library.Need to get a set of all namespaces being refered.
		Set<String> dependentLibsNamespace = new HashSet<String>();
		//identify the possible java types to be deleted
		List<String> javaTypesToDelete = new ArrayList<String>();
		try{
		for(TypeLibraryType typeLibraryType : typeRegistry.getAllTypeLibraries()){
			
			if(depLibrariesSetForDeletingFiles.contains(typeLibraryType.getLibraryName())) // the registry will hold the current library as well whose types should not be deleted
			{
				dependentLibsNamespace.add(typeLibraryType.getLibraryNamespace());
				for(TypeInformationType typeInformationType : typeLibraryType.getType())
					javaTypesToDelete.add(typeInformationType.getJavaTypeName());
			}
		}
		}catch(Exception e){
			getLogger().log(Level.WARNING, "exception : " +e);
		}
			
		deleteJavaTypes(javaTypesToDelete,codeGenCtx);
		findPackageForObjectFactoriesAndDelete(codeGenCtx,dependentLibsNamespace);
		
		
	}

	private static void findPackageForObjectFactoriesAndDelete(TypeLibraryCodeGenContext codeGenCtx,Set<String> dependentLibraryNamespaces)
	{
		for(String currentNamespace : dependentLibraryNamespaces)
		{
			String currentPackageForObjectfactory = TypeLibraryUtilities.getPackageFromNamespace(currentNamespace);
			deleteObjectfactoryAndPackageInfo(codeGenCtx, currentPackageForObjectfactory);
		}
	}

	private static void deleteJavaTypes(List<String> javaTypesToDelete,TypeLibraryCodeGenContext codeGenCtx){
		getLogger().log(Level.INFO, "java types size : " + javaTypesToDelete.size());
		getLogger().log(Level.INFO, "java types that can be deleted id they exist : " + javaTypesToDelete);
		
		String basePathForTypeFiles = codeGenCtx.getGenJavaSrcDestFolder();
		int deleteFilesCount = 0 ;
		int failDeleteCount = 0;
		
		
		for(String javaClassName : javaTypesToDelete){
			String filePath = CodeGenUtil.toJavaSrcFilePath(basePathForTypeFiles,javaClassName);
			
			File javaTypeFile = new File(filePath);
			getLogger().log(Level.INFO, "FilePath : " +filePath);
			if(javaTypeFile.exists()){
				boolean isFileDeleted = javaTypeFile.delete();
				if(!isFileDeleted){
					failDeleteCount++;
					getLogger().log(Level.WARNING, " File " + javaTypeFile.getAbsolutePath() + "  could not be deleted. This java type is imported from a type library " );
				}else {
					deleteFilesCount++;
					getLogger().log(Level.INFO," File " + javaTypeFile.getAbsolutePath() + "  deleted. This java type is imported from a type library " );
				}
					
			}
			
		}
		
		getLogger().log(Level.INFO, "count of java types that were actually deleted  : " + deleteFilesCount);
		getLogger().log(Level.INFO, "count of java types that could not be deleted  : " + failDeleteCount);
	}

	

	/**
	 * 
	 * @param XSDsWithError
	 * @param exception
	 * @param typeName
	 */
	public static void addExceptionsToXSDErrorList(HashMap<String, List<Exception>> XSDsWithError, Exception exception,String typeName){
		
		List<Exception> listOfException = XSDsWithError.get(typeName);
		
		if(listOfException == null){
			listOfException = new ArrayList<Exception>();
			XSDsWithError.put(typeName, listOfException);
		}
		
		listOfException.add(exception);
		
	}
	
	
	
	/**
	 *   If it is an invalid XSD for any reason then return a false.
	 * @param additionalXSDInformation
	 * @param dsWithError
	 * @param libNamespace
	 * @param xsdTypeName 
	 * @return
	 */
	private static boolean validateAdditionalXSDInformation(
			AdditionalXSDInformation additionalXSDInformation,
			HashMap<String, List<Exception>> XSDsWithError, String libNamespace,
			String xsdTypeName) {

		boolean isXSDValid = true;
		StringBuffer errorMsgBuf = new StringBuffer();
		int exceptionCount = 0;
		
		
		/*  This first of the validation is a special validation */
		if(!additionalXSDInformation.isDoesFileExist()){
			errorMsgBuf.append("\n");
			errorMsgBuf.append("Exception # : " + ++exceptionCount + "\n");
			errorMsgBuf.append( "The XSD file : "+ xsdTypeName + " does not exist." );
			errorMsgBuf.append("\n");
			
			isXSDValid = false;
			addExceptionsToXSDErrorList(XSDsWithError,  new Exception(errorMsgBuf.toString()) , xsdTypeName);
			return isXSDValid; 
		}
		
		
		if(TypeLibraryUtilities.isEmptyString(additionalXSDInformation.getTypeName())){
			errorMsgBuf.append("\n");
			errorMsgBuf.append("Exception # : " + ++exceptionCount + "\n");
			errorMsgBuf.append( "The XSD file contents are invalid, it does not have a type name." );
			errorMsgBuf.append("\n");
			
			isXSDValid = false;
			addExceptionsToXSDErrorList(XSDsWithError,  new Exception(errorMsgBuf.toString()) , xsdTypeName);
			return isXSDValid; 
		}
		
		
		String typeNameFromFileName = TypeLibraryUtilities.filterExtensionFromXSDFileName(xsdTypeName);
		if(!additionalXSDInformation.getTypeName().equals(typeNameFromFileName)){
			errorMsgBuf.append("\n");
			errorMsgBuf.append("Exception # : " + ++exceptionCount + "\n");
			errorMsgBuf.append( "The File : " + xsdTypeName
					+" does not have a type defined as " + "\"" + typeNameFromFileName + "\""
					+ "\n" + "The file name and type name should match and is case-sensitive");
			errorMsgBuf.append("\n");
			
			isXSDValid = false;

		}
		
		/* Fix for bug BUGDB00573075 */
		String typesNS = additionalXSDInformation.getTargetNamespace();
		if (!libNamespace.equals(typesNS)) {
			errorMsgBuf.append("\n");
			errorMsgBuf.append("Exception # : " + ++exceptionCount + "\n");
			errorMsgBuf.append( "The type "
					+ xsdTypeName
					+ " has a different namespace than the Libraries namepace."
					+ "\nThe types namespace is     : "
					+ additionalXSDInformation.getTargetNamespace()
					+ "\nThe Libraries namespace is : " + libNamespace);
			errorMsgBuf.append("\n");
			
			isXSDValid = false;
		}
		
		
		/* checking for multiple types within a single XSD file */
		if(additionalXSDInformation.getTypeNamesList().size() > 1){
			errorMsgBuf.append("\n");
			errorMsgBuf.append("Exception # : " + ++exceptionCount + "\n");
			errorMsgBuf.append( "The file " + xsdTypeName +".xsd " 
					+ "has more than one type defined in the file which is not allowed. "
					+ "\nThe types in the file are : " + additionalXSDInformation.getTypeNamesList());
			errorMsgBuf.append("\n");
			isXSDValid = false;
		}
		
		/* checking for proper version format if one is given */
		String version = additionalXSDInformation.getVersion();
		if(!TypeLibraryUtilities.isEmptyString(version)){
			
			if(!TypeLibraryUtilities.checkVersionFormat(version, TypeLibraryConstants.TYPE_VERSION_LEVEL)){
				errorMsgBuf.append("\n");
				errorMsgBuf.append("Exception # : " + ++exceptionCount + "\n");
				errorMsgBuf.append( "The file " + xsdTypeName +".xsd " 
						+ "has an invalid version namely " + version
						+ "\n Version should be of the format A.B.C where A,B,C are integers" );
				errorMsgBuf.append("\n");
				isXSDValid = false;
			}
		}
		
		
		if(! TypeLibraryUtilities.isEmptyString(errorMsgBuf.toString()))
			addExceptionsToXSDErrorList(XSDsWithError,  new Exception(errorMsgBuf.toString()) , xsdTypeName);

		
		return isXSDValid;
	}

	/**
	 * 
	 * @param XSDsWithError
	 * @throws Exception
	 */
	private static void reportXSDsForError(
			HashMap<String, List<Exception>> XSDsWithError) throws CodeGenFailedException {
		if (XSDsWithError.size() > 0) {
			StringBuffer stBuffer = new StringBuffer();

			Set<String> set = XSDsWithError.keySet();
			stBuffer.append("Artifact generation failed for the following XSDs : "
					+ set + "\n");

			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				String typeName = iter.next();
				List<Exception> list = XSDsWithError.get(typeName);
				if(list == null)
					continue;
				
				stBuffer.append("\n" + "XSD : " + typeName);
				
				for(Exception exception : list){
					stBuffer.append("\n" + "Exception was : "+ exception.getMessage());
				}
				
				stBuffer.append("\n\n");
			}

			String errMsg =  stBuffer.toString();
			getLogger().log(Level.INFO, "Error Message from reportXSDsForError : \n " + errMsg);
			throw new CodeGenFailedException(errMsg);
		}

	}

	private static void addTypeVersion(
			Map<String, String> complexTypesVersion,
			AdditionalXSDInformation additionalXSDInformation,
			String xsdTypeName) {

		String typeName = additionalXSDInformation.getTypeName();
		if (CodeGenUtil.isEmptyString(typeName)) {
			int index = xsdTypeName.indexOf(".xsd");
			if (index < 0)
				index = xsdTypeName.indexOf(".XSD");

			if (index > 0) {
				typeName = xsdTypeName.substring(0, index);
			}

		}

		String version = additionalXSDInformation.getVersion();
		if (CodeGenUtil.isEmptyString(version))
			version = TypeLibraryConstants.TYPE_DEFAULT_VERSION;

		complexTypesVersion.put(typeName, version);
	}

	private static void addTypeToModifiedMap(
			Map<String, TypeInformationType> modifiedTypeMap,
			AdditionalXSDInformation additionalXSDInformation,
			String xsdTypeName) {
		TypeInformationType typeInformationType = new TypeInformationType();

		String typeName = additionalXSDInformation.getTypeName();
		if (CodeGenUtil.isEmptyString(typeName)) {
			int index = xsdTypeName.indexOf(".xsd");
			if (index < 0)
				index = xsdTypeName.indexOf(".XSD");

			if (index > 0) {
				typeName = xsdTypeName.substring(0, index);
			}

		}
		typeInformationType.setXmlTypeName(typeName);

		String nameSpace = additionalXSDInformation.getTargetNamespace();
		String packagePath = com.sun.tools.xjc.api.XJC
				.getDefaultPackageName(nameSpace);
		packagePath += "." + WSDLUtil.getXMLIdentifiersClassName(typeName);
		typeInformationType.setJavaTypeName(packagePath);

		String version = additionalXSDInformation.getVersion();
		if (CodeGenUtil.isEmptyString(version))
			version = TypeLibraryConstants.TYPE_DEFAULT_VERSION;
		typeInformationType.setVersion(version);

		modifiedTypeMap.put(typeName, typeInformationType);

	}

	
	

	private static void createBaseFolders(TypeLibraryCodeGenContext codeGenCtx)
			throws Exception {
		String typesFolder = TypeLibraryUtilities.getTypesFolder(codeGenCtx, codeGenCtx.getLibraryName());
		String typeInfoFolder = TypeLibraryUtilities.getTypeInfoFolder(codeGenCtx, codeGenCtx.getLibraryName());
		String typeDepFolder = TypeLibraryUtilities.getTypeDepFolder(codeGenCtx, codeGenCtx.getLibraryName());
		String episodeFolder = TypeLibraryUtilities.getEpisodeFolder(codeGenCtx, codeGenCtx.getLibraryName());
		String genSrcFolder = TypeLibraryUtilities.getGenSrcFolder(codeGenCtx, codeGenCtx.getLibraryName());

		try {
			TypeLibraryUtilities.createDir(typesFolder);
			TypeLibraryUtilities.createDir(typeInfoFolder);
			TypeLibraryUtilities.createDir(typeDepFolder);
			TypeLibraryUtilities.createDir(episodeFolder);
			TypeLibraryUtilities.createDir(genSrcFolder);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE,
					"Error while trying to create folders ", e);
			throw e;
		}

	}

	private static void createTypeInformationFile(TypeLibraryCodeGenContext codeGenCtx,
			String libraryName, String libNamespace, String libraryVersion, String libraryCategory) throws Exception {
		
		String typeInfoXMLFilePath = TypeLibraryUtilities.getTypeInfoFolder(
				codeGenCtx, libraryName)
				+ File.separator + TYPE_INFORMATION_FILE_NAME;

		File typeInfoFile = new File(typeInfoXMLFilePath);
		FileWriter fw = null;
		try {
			fw = new FileWriter(typeInfoFile);
			fw.write(getTypeInformationTemplate(libraryName, libraryVersion,libNamespace,libraryCategory));

		} catch (IOException ie) {
			getLogger().log(Level.SEVERE,
					"TyepInformation.xml file could not be created");
			throw ie;
		} finally {
			if (fw != null)
				fw.close();
		}
	}

	private static String getTypeInformationTemplate(String libraryName,
			String libraryVersion, String libNamespace, String libraryCategory) throws Exception {
		
		String templateContent = "";
		
		if(TypeLibraryUtilities.isEmptyString(libraryCategory))
			templateContent = TypeLibraryUtilities.getTemplateContent(TYPE_INFORMATION_TEMPLATE_NO_CATEGORY);
		else 
			templateContent = TypeLibraryUtilities.getTemplateContent(TYPE_INFORMATION_TEMPLATE);
		
		
		templateContent = templateContent.replaceAll("@@LIBRARY@@", libraryName);

		if (CodeGenUtil.isEmptyString(libNamespace))
			libNamespace = TypeLibraryConstants.TYPE_INFORMATION_NAMESPACE;
		templateContent = templateContent.replaceAll("@@NAMESPACE@@",libNamespace);

		if (TypeLibraryUtilities.isEmptyString(libraryVersion))
			libraryVersion = TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_VERSION;
		templateContent = templateContent.replaceAll("@@VERSION@@", libraryVersion);

		if (!TypeLibraryUtilities.isEmptyString(libraryCategory))
			templateContent = templateContent.replaceAll("@@CATEGORY@@", libraryCategory);
		
		
		return templateContent;
	}

	private static void clean(TypeLibraryCodeGenContext codeGenCtx)
			 throws Exception{
		String genSrcPath = codeGenCtx.getGenJavaSrcDestFolder();
		String genMetaSrcPath = codeGenCtx.getGenMetaSrcDestFolder();
		
		try {
			FileUtils.cleanDirectory(new File(genSrcPath));
			FileUtils.cleanDirectory(new File(genMetaSrcPath));
		} catch (Exception e) {
			getLogger().log(Level.INFO, "Error while cleaning the directory");

		}
		createBaseFolders(codeGenCtx);
	}

	/*
	 * Get the Dependent Type Library jars given a type name from V3 build
	 */
	private static String getDependentJarFromV3(String typeName,
			String stagingLocation, String dependentTypeLibs) {
		stagingLocation = TypeLibraryUtilities.toOSFilePath(stagingLocation);
		StringBuilder sb = new StringBuilder();
		if (! TypeLibraryUtilities.isEmptyString(dependentTypeLibs)) {
			String depTypeLibs[] = dependentTypeLibs.split(",");
			if (depTypeLibs != null && depTypeLibs.length > 0) {
				for (int i = 0; i < depTypeLibs.length; i++) {
					sb.append(stagingLocation).append(depTypeLibs[i]).append(".jar");
					sb.append(",");
				}

				return sb.toString();
			}
		}
		return sb.toString();
	}
	
	
	private static String getXJCArgs(String xsdFileName,String dependentJarPath, 
										 Set<String> depLibraries,TypeLibraryCodeGenContext codeGenCtx){
		
		TypeLibraryInputOptions inputOptions = codeGenCtx.getTypeLibraryInputOptions();
		
		String projectRoot = TypeLibraryUtilities.normalizePath(inputOptions.getProjectRoot());
		String libraryName = inputOptions.getTypeLibraryName();
		String xsdSrcPath  = null;
 
		String typesNewSrcPath =  TypeLibraryUtilities.normalizePath(TypeLibraryUtilities.getNewTypesFolderLocation(codeGenCtx, libraryName));	
		String genJavaDestPath = TypeLibraryUtilities.normalizePath(TypeLibraryUtilities.getGenSrcFolder(codeGenCtx, libraryName));
		String xsdSrcnewPath = typesNewSrcPath + xsdFileName;
		
		File xsdinNewPath = new File(xsdSrcnewPath);
		
		//check if xsd  exist in new structure 
		if(xsdinNewPath.exists())
		{
			xsdSrcPath = xsdSrcnewPath;
		}
		
		//support for existing typeLibs with older structure, search in older structure if xsd not found in new structure
		if(xsdSrcPath ==null)
		{
		 String typesSrcPath  = TypeLibraryUtilities.normalizePath(TypeLibraryUtilities.getTypesFolder(codeGenCtx, libraryName));		
		 xsdSrcPath  = typesSrcPath + xsdFileName;
		}
		
		String episodeDestpath = TypeLibraryUtilities.normalizePath(TypeLibraryUtilities.getEpisodeFolder(codeGenCtx,libraryName));
		
		String typeName        = TypeLibraryUtilities.getTypeNameFromFileName(xsdFileName);
		String episodePath     = episodeDestpath + typeName + ".episode";
		
		
		/******************* populating arguments    *****************/
		
		StringBuilder xjcArg = new StringBuilder();
		
		xjcArg.append("-extension").append(",");
		
		xjcArg.append("-nv").append(","); //-nv is to avoid throwing an error if a complex type has both an element as well as xs:any element
		
		if (! TypeLibraryUtilities.isEmptyString(episodePath)) {
			xjcArg.append("-episode").append(",").append(episodePath).append(",");
		}
		if(! TypeLibraryUtilities.isEmptyString(genJavaDestPath))  {
			xjcArg.append("-d").append(",").append(genJavaDestPath).append(",");
		}
		if(! TypeLibraryUtilities.isEmptyString(xsdSrcPath)) {
			xjcArg.append(xsdSrcPath).append(",");
		}
		
		String populateClasspath = populateClasspath(codeGenCtx);
		if (populateClasspath != null) {
			xjcArg.append(populateClasspath);
		}	
		
		
		Set<String> depLibJarSet = new HashSet<String>();
		if (! TypeLibraryUtilities.isEmptyString(dependentJarPath)) {
		   
			String[] depLibrariesJarPath = dependentJarPath.split(",");
			for(String jarfile : depLibrariesJarPath)
				depLibJarSet.add(jarfile);
			
			String dependentJar = populateClasspathForDepJars(depLibJarSet);
			if (dependentJar != null) {
				xjcArg.append(dependentJar);
			}
			
		}
		
		
		/* preparing the episode file for -b option , this is not just a union of the sun-jaxb.episode files of dependent library,
		 * but it has more intelligence built into it for handling multiple namespace, simple types java file creation issue. */
		
		if (depLibraries.size() > 0)  { 
		
			try {
				File  preProcessedMasterEpisodeFile = File.createTempFile("ebay",".episode");
				preProcessedMasterEpisodeFile.deleteOnExit();
				
				Bindings masterEpisodeBindings = createMasterEpisodeBinding(codeGenCtx);
				
				JAXB.marshal(masterEpisodeBindings, preProcessedMasterEpisodeFile);
				
				File processedMasterEpisodeFile = getPostProcessedMasterEpisodeFile(preProcessedMasterEpisodeFile);
				
				xjcArg.append("-b");
				xjcArg.append(",");
				xjcArg.append(processedMasterEpisodeFile);
				xjcArg.append(",");
		
				
			} catch (IOException e) {
				getLogger().log(Level.INFO, "Error while creating the master episode file for library "+ libraryName);
			}
		}
		
		return xjcArg.toString();
	}
	
	

	

	private static String getXJCArgsForPlugin(String dependentJarPath,
			String episodePath, String genJavaDestPath, String xsdSrcPath,
			String libraryName, Set<String> depLibraries, TypeLibraryCodeGenContext codeGenCtx) {
		

		StringBuilder xjcArg = new StringBuilder();
		xjcArg.append("-extension").append(",");
		
		xjcArg.append("-nv").append(","); //-nv is to avoid throwing an error if a complex type has both an element as well as xs:any element
		if (!TypeLibraryUtilities.isEmptyString(episodePath))  {
			xjcArg.append("-episode").append(",").append(episodePath).append(",");
		}
		if (!TypeLibraryUtilities.isEmptyString(genJavaDestPath)) {
			xjcArg.append("-d").append(",").append(genJavaDestPath).append(",");
		}
		if (!TypeLibraryUtilities.isEmptyString(xsdSrcPath)) {
			xjcArg.append(xsdSrcPath).append(",");
		}

		String populateClasspath = populateClasspath(codeGenCtx);
		if (populateClasspath != null) {
			xjcArg.append(populateClasspath);
		}
		String dependentJar = populateClasspathForDepJars(depJarsAndPaths);
		if (dependentJar != null) {
			xjcArg.append(dependentJar);
		}

		/*
		 *  PREPARING THE episode file which is a union of all the dependent sun-jaxb.episode files
		 */
		
 
		if (depLibraries.size() > 0)  { 
		
			try {
				File  preProcessedMasterEpisodeFile = File.createTempFile("ebay",".episode");
				preProcessedMasterEpisodeFile.deleteOnExit();
				
				Bindings masterEpisodeBindings = createMasterEpisodeBinding(codeGenCtx);
				
				JAXB.marshal(masterEpisodeBindings, preProcessedMasterEpisodeFile);
				
				File processedMasterEpisodeFile = getPostProcessedMasterEpisodeFile(preProcessedMasterEpisodeFile);
				
				xjcArg.append("-b");
				xjcArg.append(",");
				xjcArg.append(processedMasterEpisodeFile);
				xjcArg.append(",");
		
				
			} catch (IOException e) {
				getLogger().log(Level.INFO, "Error while creating the master episode file for library "+ libraryName);
			}
		}
		
		
		
		xjcArg.append("-verbose");
		return xjcArg.toString();
	}

	

	private static File getPostProcessedMasterEpisodeFile(File preProcessedMasterEpisodeFile) throws IOException {
		
		
		String preProcessedContent = TypeLibraryUtilities.getContentFromFile(preProcessedMasterEpisodeFile);
		String postProcessedContent = preProcessedContent;
		
		String prefix = getPrefixFromEpisodeContent(preProcessedContent);
		
		postProcessedContent = postProcessedContent.replaceAll("xmlnstns", "xmlns:tns");
		postProcessedContent = postProcessedContent.replaceAll(":"+prefix, "");
		postProcessedContent = postProcessedContent.replaceAll("<" + prefix + ":", "<");
		postProcessedContent = postProcessedContent.replaceAll("</ns2:", "</");
		getLogger().log(Level.FINE, "The master epsiode content :  \n  " + postProcessedContent +"\n\n");
		
		File  processedMasterEpisodeFile = File.createTempFile("ebay",".episode");
		FileOutputStream fileOutputStream = new FileOutputStream(processedMasterEpisodeFile);
		fileOutputStream.write(postProcessedContent.getBytes());
		fileOutputStream.close();

		return processedMasterEpisodeFile;
	}

	private static String getPrefixFromEpisodeContent(String preProcessedContent) {
		int pos1 = preProcessedContent.indexOf("xmlns:");
		pos1 += 6;
		String postContent = preProcessedContent.substring(pos1);
		int pos2 = postContent.indexOf("=");
		
		String prefix = postContent.substring(0, pos2);
		
		return prefix;
	}

	private static Bindings createMasterEpisodeBinding(TypeLibraryCodeGenContext codeGenCtx) {
		
		Bindings masterEpisodeBindings = new Bindings();
		masterEpisodeBindings.setVersion("2.1");
		
		for(String currLibraryName : codeGenCtx.getTypesAndDependentTypesMap().keySet()){
			String currLibNS = codeGenCtx.getLibrariesNamespace().get(currLibraryName);
			
			boolean isCurrentLibraryMasterLibrary = false;
			String masterLibraryPackagePath = null;
			if(currLibraryName.equals(codeGenCtx.getLibraryName())){
					isCurrentLibraryMasterLibrary = true;
					masterLibraryPackagePath = TypeLibraryUtilities.getGenSrcFolder(codeGenCtx, codeGenCtx.getLibraryName());
					
					String pkgPrefix = TypeLibraryUtilities.getPackageFromNamespace(codeGenCtx.getLibrariesNamespace().get(currLibraryName));
					pkgPrefix = pkgPrefix.replace(".",File.separator);
					
					masterLibraryPackagePath = TypeLibraryUtilities.normalizePath(masterLibraryPackagePath) + pkgPrefix;
					masterLibraryPackagePath = TypeLibraryUtilities.normalizePath(masterLibraryPackagePath);
			}
			
			if(TypeLibraryUtilities.isEmptyString(currLibNS)){
				getLogger().log(Level.WARNING, "While trying to create the master episode file for -b option of XJC for library " + currLibraryName +
						".\n The namespace of the library could not be found and hence the master episode file will not have entries for  \n  the dependent types : " +
						codeGenCtx.getTypesAndDependentTypesMap().get(currLibraryName));
				
				continue;
			}
			
			String currLibPkgPrefix = TypeLibraryUtilities.getPackageFromNamespace(currLibNS);
			
			boolean isExisitingBindingLevelTwo = true;
			BindingsLevelTwo bindingLevelTwo = getExisitingBindingsLevelTwoIFAny(masterEpisodeBindings,currLibNS);
				
			if(bindingLevelTwo == null){	
				isExisitingBindingLevelTwo = false;
				bindingLevelTwo = new BindingsLevelTwo();
				bindingLevelTwo.setScd("x-schema::tns");
				bindingLevelTwo.setXmlnstns(currLibNS);
				SchemaBindings schemaBindings = new SchemaBindings();
				schemaBindings.setMap("true");
				bindingLevelTwo.setSchemaBindings(schemaBindings);
			}
			
			Set<String> typesForWhichJavaFiles	 = codeGenCtx.getLibraryNameSimpleTypeJavaMap().get(currLibraryName);
			if(typesForWhichJavaFiles == null)
				typesForWhichJavaFiles = new HashSet<String>();
			
			/*
			 * using this boolean variable the code will make sure that only if valid bindings for referred types are added, the
			 * parent binding  "bindingLevelTwo" would be added to the master binding "masterEpisodeBindings"
			 */
			boolean bindingHasAtleastOneValidChild = false;
			 
			for(String referredType : codeGenCtx.getTypesAndDependentTypesMap().get(currLibraryName)){
				
				String referredTypeJavaFileName  = CodeGenUtil.makeFirstLetterUpper(referredType);
				
				if(typesForWhichJavaFiles.contains(referredType))
					continue;
				
				if(isCurrentLibraryMasterLibrary){
					// if current library is master library then verify whether the referred file exists , iff it exists
					// add the bindings for it, this is neededed bcos for gentype cleanBuild the code does not process
					// the XSDs of the master library (library which is currently processed) in the order of dependency graph.
					String referredTypeFilePath = masterLibraryPackagePath + CodeGenUtil.makeFirstLetterUpper(referredTypeJavaFileName) + ".java";
					File referredFile = new File(referredTypeFilePath);
					if(!referredFile.exists())
						continue;
					
				}
				
				BindingsLevelThree bindingLevelThree = getExisitingBindingsLevelThreeIFAny(bindingLevelTwo,referredTypeJavaFileName);
				
				//dont' add binding for this element if it already exists
				if(bindingLevelThree == null){
					bindingLevelThree = new BindingsLevelThree();
					bindingLevelThree.setIfExists("true");
					bindingLevelThree.setScd("~tns:" + referredType);
					
					BindingsClassType classType = new BindingsClassType();
					classType.setRef(currLibPkgPrefix + "." + referredTypeJavaFileName);
					
					bindingLevelThree.setClazz(classType);
					
					bindingLevelTwo.getBindings().add(bindingLevelThree);
					
					bindingHasAtleastOneValidChild = true;
				}
				
				
			}
			
			if(bindingHasAtleastOneValidChild &&  ! isExisitingBindingLevelTwo)
				masterEpisodeBindings.getBindings().add(bindingLevelTwo);
			
		}
		
		
		return masterEpisodeBindings;
	}
	

	
		
	private static BindingsLevelThree getExisitingBindingsLevelThreeIFAny(BindingsLevelTwo bindingLevelTwo, String referredType) {
		
		BindingsLevelThree result = null;
		
		if(bindingLevelTwo == null)
			return result;
		
		for(BindingsLevelThree currBindingsLevelThree : bindingLevelTwo.getBindings()){
			if(currBindingsLevelThree.getScd().equals("~tns:"+referredType)){
				result = currBindingsLevelThree;
				break;
			}
		}
		return result;
	}

	
	private static BindingsLevelTwo getExisitingBindingsLevelTwoIFAny(Bindings masterEpisodeBindings, String currLibNS) {
		
		BindingsLevelTwo result = null;
		
		if(masterEpisodeBindings == null)
			return result;
		
		for(BindingsLevelTwo currBindingsLevelTwo :masterEpisodeBindings.getBindings()){
			if(currBindingsLevelTwo.getXmlnstns().equals(currLibNS)){
				result = currBindingsLevelTwo;
				break;
			}
		}
		
		return result;
	}

	
	private static String getLibraryNameFromJarFilePath(String currDepLibraryJarName) {
		
		String path = currDepLibraryJarName.replace("\\", "/");
		int index = path.lastIndexOf("/");
		int index2 = path.indexOf(".jar");
		String libraryName = path.substring(index+1,index2);
		return libraryName;
	}



	/*
	 * Get the Dependent Type Library jars given a type name from Plugin
	 */
	private static String getDependentJarFromPlugin(Set<String> depLibraries) {
		
		depJarsAndPaths = new HashSet<String>();//re-initilize this map every time, otherwise when clean build or incr build is called 
												// it will result in un-necessary paths/jars being set in the class path of xjc	
		
		StringBuilder sb = new StringBuilder();
		ClassLoader classLoader = CodeGenTypeLibraryGenerator.class.getClassLoader();
		
		URL[] parentClasspathURLs = new URL[0];
		URL[] mergedClasspathURLs = new URL[0];
		if (classLoader instanceof CodeGenClassLoader){
			getLogger().log(Level.INFO, " The class loader within CodeGenTypelibraryGenerator is an instance CodeGenClassLoader. " );
			CodeGenClassLoader codeGenClassLoader = (CodeGenClassLoader)classLoader;
			parentClasspathURLs = codeGenClassLoader.getAllURLs();
		}
		else if (classLoader instanceof URLClassLoader) {
			parentClasspathURLs = ((URLClassLoader) classLoader).getURLs();
		}
		
		// add the current context urls if not yet in the parent
		List<URL> classPathList = new ArrayList<URL>();
		for (URL url : parentClasspathURLs) {
			classPathList.add(url);
		}
		
		ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
		if (threadLoader instanceof URLClassLoader) {
			URL[] threadClassPaths =( (URLClassLoader) threadLoader).getURLs();
			for (URL url : threadClassPaths) {
				if (!classPathList.contains(url))
					classPathList.add(url);
			}
		}
		mergedClasspathURLs = classPathList.toArray(new URL[0]);
		// Get the list of files
		File files[] = TypeLibraryUtilities.toFiles(mergedClasspathURLs);
		getLogger().log(Level.INFO, "The URLs available to the type library generator's class loader are : "+ Arrays.toString(files));

		// Process all the files
		Set<String> depLibrariesJarNotFoundSet = new HashSet<String>(depLibraries);
		for (int i = 0; i < files.length; i++) {
			String fileToCheck = files[i].getPath();
			String fileNameToCheck = files[i].getName();
			// Check if the file is a typelibrary jar
			boolean isTypeLibJar = isTypeLibraryJar(files[i], depLibraries);
			
			if (isTypeLibJar) {
				sb.append(fileToCheck);
				depJarsAndPaths.add(fileToCheck);
				sb.append("!");
				String libraryNameFromJarFileName = getLibraryNameFromJarFileName(fileNameToCheck);
				depLibrariesJarNotFoundSet.remove(libraryNameFromJarFileName);
			}

		}

		// Identify the libraries for which jars were not found and report the same as warning
		Iterator<String> libraryNamesIter = depLibrariesJarNotFoundSet.iterator();
		while(libraryNamesIter.hasNext()){
			
			String depLibraryName = libraryNamesIter.next();
			
			getLogger().log(Level.WARNING, "Tha jar for the dependent library \"" + depLibraryName + 
					"\" could not be found. Codegen would now try to see whether the dependent project root can be identified.");
			
			for (int i = 0; i < files.length; i++) {
				String filePathToCheck = files[i].getPath();
				filePathToCheck = TypeLibraryUtilities.normalizeFilePath(filePathToCheck);
				
				String searchString = File.separatorChar + depLibraryName + File.separatorChar;
				int index = filePathToCheck.indexOf(searchString);
				
				if(index < 0)
					continue;
				
				String filePathForclassPath = filePathToCheck.substring(0,index);
				filePathForclassPath = filePathForclassPath
						+ File.separatorChar + depLibraryName
						+ File.separatorChar + "build" 
						+ File.separatorChar + "classes";
				
				sb.append(filePathForclassPath).append("!");
				depJarsAndPaths.add(filePathForclassPath);
				libraryNamesIter.remove();
				
				getLogger().log(Level.INFO, "The project root for dependent library " +  depLibraryName +" is found : " + filePathForclassPath);
				
				break;
			}
			
		}
		
		
		
		// Identify the libraries for which jars were not found and report the same as warning
		Iterator<String> notfoundLibIter = depLibrariesJarNotFoundSet.iterator();
		while(notfoundLibIter.hasNext()){
			
			String depLibraryName = notfoundLibIter.next();
			
			getLogger().log(Level.WARNING, "Tha jar nor the project root path for the dependent library \"" + depLibraryName + 
					"\" could not be found hence the java files corresponding to such dependent types would get " +
					"re-generated.But codegen would delete these unwanted new files.");
		}
		
		
		
		
		return sb.toString();
	}


	private static String getLibraryNameFromJarFileName(String fileNameToCheck) {
		int index = fileNameToCheck.indexOf(".jar");
		if (index < 0)
			index = fileNameToCheck.indexOf(".JAR");

		if (index < 0)
			return null;

		String fileName = fileNameToCheck.substring(0, index);
		if (fileName.contains("-"))
			fileName = fileName.substring(0, fileName.indexOf("-"));
		return fileName;
	}

	
	
	private static boolean isTypeLibraryJar(File file,
			Set<String> typeLibs) {
		
		String filePathToCheck = file.getPath();
		filePathToCheck = TypeLibraryUtilities
				.normalizeFilePath(filePathToCheck);

		if (!filePathToCheck.toLowerCase().endsWith(".jar"))
			return false; // verify only for jar files

		boolean flag = false;
		Iterator it = typeLibs.iterator();
		while (it.hasNext()) {
			String typeLibName = (String) it.next();
			if (filePathToCheck.contains(File.separatorChar + typeLibName
					+ ".jar")) {
				return true;
			} else if (filePathToCheck.contains(File.separatorChar + typeLibName +"-") && filePathToCheck.endsWith(".jar")){
				return true;
			}	
			else {
			
				String fileName = file.getName();
				if (fileName.toLowerCase().equals(typeLibName + ".jar"))
					return true;
			}
		}
		return flag;
	}

	
	
	/*
	 * Populate the classpath for all the dependent Jar Files
	 */

	private static String populateClasspathForDepJars(Set<String> depJars) {
		StringBuilder sb = new StringBuilder();
		if (depJars != null && depJars.size() != 0) {
			Iterator it = depJars.iterator();
			while (it.hasNext()) {
				String dependentJarPath = (String) it.next();
				sb.append("-classpath").append(",").append(dependentJarPath)
						.append(",");
			}
		}
		return sb.toString();
	}

	/*
	 * Populate the classpath for the gen-meta-src and meta-src directories of
	 * the project
	 */
	private static String populateClasspath(TypeLibraryCodeGenContext codeGenCtx) {
		StringBuilder sb = new StringBuilder();
		sb.append("-classpath").append(",");
		sb.append(codeGenCtx.getGenMetaSrcDestFolder()).append(",");
		sb.append("-classpath").append(",");
		sb.append(codeGenCtx.getMetaSrcFolder()).append(",");
		
		if(codeGenCtx!= null){
			String additionalClasPathFromPlugin = codeGenCtx.getTypeLibraryInputOptions().getAdditionalClassPathToXJC();
			if(! TypeLibraryUtilities.isEmptyString(additionalClasPathFromPlugin)){
				String classpaths[] = additionalClasPathFromPlugin.split(File.pathSeparator);
				for(String classPath : classpaths){
					sb.append("-classpath").append(",").append(classPath).append(",");
					if(codeGenCtx.getTypeLibraryInputOptions().isAddBuildClassPathToXJC()) {
						// This should not be added by default, it's not compatible with eclipse or maven
						sb.append("-classpath").append(",");
						sb.append(TypeLibraryUtilities.normalizePath(classPath));
						sb.append("build").append(File.separatorChar);
						sb.append("classes,");
					}
				}
			}
		}
		
		return sb.toString();

	}

	private static boolean isDependentPathLength(String dependentJarPath) {
		String str[] = new String[0];
		boolean flag = true;
		if (dependentJarPath != null) {
			str = dependentJarPath.split("!");
		}
		if (str.length != 1) {
			return false;
		}
		return flag;
	}


	private static void setDependentTypeDetailsOnCodegenctx(
			String libraryName, String typeName, TypeLibraryCodeGenContext codeGenCtx) throws Exception {
		
		 Map<String, Set<String>> dependantTypesAndLibraries = null; 
		 try{
			getLogger().log(Level.INFO, "before calling TypeLibraryUtilities.findDependentLibrariesAndTypesForAType for "+ 
					"type : " + typeName + " belonging to library : " + libraryName);
			 
			dependantTypesAndLibraries = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(codeGenCtx, libraryName, typeName);
			
			codeGenCtx.getTypesAndDependentTypesMap().clear();//clear each time to remove entries from previous types
			codeGenCtx.getTypesAndDependentTypesMap().putAll(dependantTypesAndLibraries);
			
			
			
			for(String referredLibrary : dependantTypesAndLibraries.keySet()){
				
				if(!referredLibrary.equals(libraryName)){
					// The current libraries namespace would have been set earlier, we can't make this call to util since during cleanBuild the TypeInformation.xml file
					// would not be created until XJC is run for all XSD.
					List<String> libList = new ArrayList<String>();
					libList.add(referredLibrary);
					
					codeGenCtx.getLibrariesNamespace().putAll(TypeLibraryUtilities.getLibrariesNameSpace(libList, null, CodeGenTypeLibraryGenerator.class.getClassLoader()));
				}
				
				getLogger().log(Level.INFO, "Referred library Details : Library Name -> " + referredLibrary + "\n" +
						" Library's Namespace -> " + codeGenCtx.getLibrariesNamespace().get(referredLibrary)+ "\n" +
						"  Referred Types -> " + dependantTypesAndLibraries.get(referredLibrary) + "\n\n");
			}
			
			for(String referredLibrary : dependantTypesAndLibraries.keySet()){
				

				//only for the current library we will have to update this map every time
				//for dependent libraries we have to chk the codegen context and only if it does not exist then 
				//populate the map by loading the properties file for the same  

				Set<String> typesForWhichJavaFilesNotCreated = codeGenCtx.getLibraryNameSimpleTypeJavaMap().get(referredLibrary);
				
				if(referredLibrary.equals(libraryName)){
					String propertiesFilePath = TypeLibraryUtilities.getTypesJavaPropertiesFolder(codeGenCtx, libraryName);
					propertiesFilePath += TypeLibraryTypesAndJavaPropertiesGenerator.TYPES_JAVA_PROP_FILE_NAME;
					
					Properties properties = TypeLibraryUtilities.getPropertiesFromFile(propertiesFilePath);
					if(properties != null)
						codeGenCtx.getLibraryNameSimpleTypeJavaMap().put(referredLibrary,
																			TypeLibraryUtilities.convertSetOfObjectsToSetOfStrings(properties.keySet()));
					
				}else{
					if(typesForWhichJavaFilesNotCreated == null){
						String propertiesFileRelativePath = TypeLibraryConstants.META_INF_FOLDER + File.separator + referredLibrary + 
														File.separator + TypeLibraryTypesAndJavaPropertiesGenerator.TYPES_JAVA_PROP_FILE_NAME;
						
						Properties properties = TypeLibraryUtilities.getPropertiesFromFileFromClassPath(propertiesFileRelativePath,
																CodeGenTypeLibraryGenerator.class.getClassLoader());
						if(properties != null)
							codeGenCtx.getLibraryNameSimpleTypeJavaMap().put(referredLibrary,
																					TypeLibraryUtilities.convertSetOfObjectsToSetOfStrings(properties.keySet()));
					}
				}
				
			}
			
			
			
		}catch (Exception e) {
			getLogger().log(Level.INFO, "exception e :" + e.getMessage(), e);
			throw e;
		}
		
		
	}
}
