/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.utils;



import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.ReferredType;
import org.ebayopensource.turmeric.common.config.ReferredTypeLibraryType;
import org.ebayopensource.turmeric.common.config.TypeDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;

public class TypeLibraryUtilities {
	
	private static CallTrackingLogger logger = LogManager.getInstance(TypeLibraryUtilities.class);
	private static final String KEY_VALUE_SEPERATOR = "=";
	
	private static CallTrackingLogger getLogger(){
		return logger;
	}
	
	
	public static final File[] EMPTY_FILE_ARRAY = new File[0];
	
	
	public boolean  deleteFiles(String fileLocation) {
		File f = new File(fileLocation);
		boolean temp = f.delete();
		return temp;
	}
	
	/**
     * Scans a local directory for existing subdirectories.
     * 
     * @param directory
     * @param files
     */
    public static List<File> getDirectoryList(File directory)
    {
    	ArrayList<File> directories = new ArrayList<File>();
        File[] filesInDir = directory.listFiles();
        for (int i=0; i<filesInDir.length; i++) {
            File file = filesInDir[i];
            if (file.isDirectory()) {
                directories.add(file);
            }
        }
        return directories;
    }
    
    
    /**
     * Scans a local directory for existing files.
     * 
     * @param directory
     * @param files
     */
    public static List<String> getFiles(File directory)
    {	
    	ArrayList<String> files = new ArrayList<String>();
        File[] filesInDir = directory.listFiles();
        for (int i=0; i<filesInDir.length; i++) {
            File file = filesInDir[i];
            if (file.isFile()) {
                  files.add(file.getName());
                }
            }
        return files; 
        }
    
    /**
     * Deletes everything under a directory (including subdirectories)
     */
    public static List<String> getFiles(String dir) {
        return getFiles(new File(dir));
        
    }
    
    /**
     * Deletes everything under a directory (including subdirectories)
     */
    public static boolean deleteRecursive(File dir) {

    	if(!dir.exists()) {
            return true;
        }

        File tempFile;
        File[] fileList = dir.listFiles();
        int fileListLength = fileList.length;
        int index;
        boolean result = false;
        // use recursion to go through all the subdirectories and files and delete all of the files
        for(index = 0; index < fileListLength; index++) {
            tempFile = fileList[index];
            if(tempFile.isDirectory()) {
                deleteRecursive(tempFile);
            } else if(tempFile.isFile()) {
            	 result = tempFile.delete();
            }
            
        }
        // all the files are deleted, so now delete the directory skeleton
        result= dir.delete();
        return result;
    }
    
    /**
     * Deletes everything under a directory (including subdirectories)
     */
    public static void deleteRecursive(String dir) {
        deleteRecursive(new File(dir));
    }
    
    /**
     * Creates a file or directory
     * 
     * @param   file    The file or directory to create
     * @return  true if the file or directory was created; false otherwise
     */
    public static boolean create(String file) {
        return create(new File(file));
    }

    /**
     * Creates a file or directory
     * 
     * @param   file    The file or directory to create
     * @return  true if the file or directory was created; false otherwise
     */
    public static boolean create(File file) {
        boolean result;

        if(isPath(file)) {
            result = file.mkdirs();
        } else {
            try {
            	result = new File(getPath(file)).mkdirs();
                result = file.createNewFile();
            } catch (IOException e) {
                result = false;
            }
        }

        return result;
    }
    
    /**
     * Determines whether or not a given file name is a path
     */
    public static boolean isPath(File file) {
        return isPath(file.toString());
    }

    /**
     * Determines whether or not a given file name is a path
     * 
     * If the name given exists and 
     */
    public static boolean isPath(String file) {
        
        if(file.endsWith("/")) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Gets the path name for a given file name
     * 
     * @param   file    The file name to process
     * @return  The name of the path to the given file
     */
    public static String getPath(File file) {
        return getPath(file.toString());
    }

    /**
     * Gets the path name for a given file name
     * 
     * @param   file    The file name to process
     * @return  The name of the path to the given file
     */
    public static String getPath(String file) {
       
      //  return StringUtils.getLeftLast(file, "/") + "/";
    	return "";
    }
    
    /**
     * Creates folder structure for gen-meta-src and gen-src
     * for TypeLibrary project structure 
     */
    public static  boolean createProjectSubFolders(TypeLibraryCodeGenContext codeGenCtx) {
    	String projectPath = codeGenCtx.getGenMetaSrcDestFolder();
    	String genMetaSrc =  CodeGenConstants.META_INF_FOLDER + File.separator + codeGenCtx.getLibraryName();
    	File genMetaSrcFolder = new File(projectPath, genMetaSrc);
    	return genMetaSrcFolder.mkdirs();
    }
    
    public static String getTypeLibraryProjectName(String projectRootFolder) {
    	String projectName = null;
    	int index = projectRootFolder.lastIndexOf(File.separator);
    	projectName = projectRootFolder.substring(index + 1);
    	
    	return projectName;
    }
    
    public static String getEpisodeFileName(String xsdType){
    	int index = xsdType.indexOf(".");
    	return xsdType.substring(0, index);
    }
    
	public static String getJavaClassName(String className) {
		int idx = className.lastIndexOf(".");
		if (idx <= 0) {
			return "";
		}
		return className.substring(idx+1);
	}
 	
	
	public static String getTemplateContent(String templateName) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = 
				classLoader.getResourceAsStream(templateName);
		if (inputStream == null) {
			throw new IOException("Failed to load resource : " + templateName);
		}
		
		String templateContent = readContent(inputStream);

		return templateContent;
	}
	
	
	private static String readContent(InputStream input) throws IOException {
		Charset defaultCharset = Charset.defaultCharset();
		StringBuilder strBuff = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader reader = null;
		
		try {
			isr = new InputStreamReader(input,defaultCharset);
			reader = new BufferedReader(isr);
			char[] charBuff = new char[512];
			int charsRead = -1;
			while ((charsRead = reader.read(charBuff)) > -1) {
				strBuff.append(charBuff, 0, charsRead);
			}
		} finally {
			CodeGenUtil.closeQuietly(reader);
			CodeGenUtil.closeQuietly(isr);
		}
		return strBuff.toString();
	}
	
	public static File createDir(String dirPath) throws IOException {
		File dir = null;
		try {
			dir = getDir(dirPath);
		} catch (IOException io) {
			// Directory doesn't exists
			dir = new File(dirPath);
			boolean mkDirSuccess = dir.mkdirs();
			if (!mkDirSuccess) {
				throw new IOException("Failed to create dir(s) : " + dirPath);
			}
		}
		
		return dir;
	}

	public static File getDir(String destDir) throws IOException {
		if (destDir == null) {
			return null;
		}

		File dir = new File(destDir);
        if(!dir.exists() || !dir.isDirectory()) {
            throw new IOException(destDir + ": non-existent directory");
        }
        
        return dir;
	}
	
	public static String getTypeDepFolder(TypeLibraryCodeGenContext ctx, String libraryName) {
		StringBuilder path = new StringBuilder();
		path.append(ctx.getMetaSrcFolder()).append(File.separator);
		path.append(TypeLibraryConstants.META_INF_FOLDER).append(File.separator);
		path.append(libraryName);
		return path.toString();
	}

	public static String getTypeInfoFolder(TypeLibraryCodeGenContext ctx, String libraryName) {
		StringBuilder path = new StringBuilder();
		path.append(ctx.getGenMetaSrcDestFolder()).append(File.separator);
		path.append(TypeLibraryConstants.META_INF_FOLDER).append(File.separator);
		path.append(libraryName);
		return path.toString();
	}

	public static String getTypesFolder(TypeLibraryCodeGenContext ctx, String libraryName) {
		StringBuilder path = new StringBuilder();
		path.append(ctx.getMetaSrcFolder()).append(File.separator);
		path.append(TypeLibraryConstants.TYPES_FOLDER);
		return path.toString();
	}

	public static String getNewTypesFolderLocation(TypeLibraryCodeGenContext ctx,String libraryName) {
		StringBuilder path = new StringBuilder();
		path.append(ctx.getMetaSrcFolder()).append(File.separator);
		path.append(TypeLibraryConstants.TYPES_FOLDER).append(File.separator);
		path.append(libraryName);
		return path.toString();
	}

	public static String getEpisodeFolder(TypeLibraryCodeGenContext ctx, String libraryName) {
		StringBuilder path = new StringBuilder();
		path.append(ctx.getGenMetaSrcDestFolder()).append(File.separator);
		path.append(TypeLibraryConstants.META_INF_FOLDER).append(File.separator);
		path.append(libraryName);
		return path.toString();
	}

	public static String getTypesJavaPropertiesFolder(TypeLibraryCodeGenContext ctx, String libraryName) {
		StringBuilder path = new StringBuilder();
		path.append(ctx.getGenMetaSrcDestFolder()).append(File.separator);
		path.append(TypeLibraryConstants.META_INF_FOLDER).append(File.separator);
		path.append(libraryName);
		return path.toString();
	}

	public static String getGenSrcFolder(TypeLibraryCodeGenContext ctx, String libraryName) {
		return ctx.getGenJavaSrcDestFolder();
	}

	public static String removePrefix(String input){
		String str = input; 
		int index = 0;
		while((index = str.indexOf(":")) >=  0){
			if(index < str.length())
				str = str.substring(index+1);
			else if (index == str.length())
				str = str.substring(index);
		}
		
		return str;
	}

    public static String[] getFilesInDir(String directory, String fileNameEndsWith) throws Exception{
    	final String localFilter = fileNameEndsWith;
    	File dir = new File(directory);
    	
    	FilenameFilter filenameFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(localFilter);
			} 
    	};
    	logger.log(Level.INFO,"filenameFilterFiles available  "+ dir.list(filenameFilter));
    	return dir.list(filenameFilter);
    }
	
    public static File[] toFiles(URL[] urls) {
        if (urls == null || urls.length == 0) {
            return EMPTY_FILE_ARRAY;
        }
        File[] files = new File[urls.length];
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            if (url != null) {
                if (url.getProtocol().equals("file") == false) {
                    throw new IllegalArgumentException(
                            "URL could not be converted to a File: " + url);
                }
                files[i] = toFile(url);
            }
        }
        return files;
    }

    public static File toFile(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        } else {
            String filename = url.getFile().replace('/', File.separatorChar);
            int pos =0;
            while ((pos = filename.indexOf('%', pos)) >= 0) {
                if (pos + 2 < filename.length()) {
                    String hexStr = filename.substring(pos + 1, pos + 3);
                    char ch = (char) Integer.parseInt(hexStr, 16);
                    filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
                }
            }
            return new File(filename);
        }
    }
    
    public static String getSunJaxBEpisodeFolder(TypeLibraryCodeGenContext ctx,String libraryName) {
    	return getEpisodeFolder(ctx,libraryName);
	}
	
    /**
     *  This method validates a version format. It assumes that each part of the version would be a numeric value. 
     * @param input  The input String to be verified for version pattern compliance.
     * @param level  The number of major and minor versions etc ..  for somethng like 1.0  the level is two , and for something like 1.0.4 the level is 3.
     * @return
     */
    public static boolean checkVersionFormat(String input,int level){
    	String patternStr = "";
    	StringBuffer strBuf = new StringBuffer();
    	String onePatternStr = "[0-9]+.";
    	for(int i=0; i<level ; i++){
    		strBuf.append(onePatternStr);
    	}
    	
    	patternStr = strBuf.toString();
    	patternStr = patternStr.substring(0, patternStr.length()-1); // remove the extra dot at the end
    	

		Pattern regexPattern = Pattern.compile(patternStr);
		Matcher regexMatcher = regexPattern.matcher(input);
		if(regexMatcher.matches())
    	   return true;
    	else
    		return false;
    }
    
    
	public static boolean isEmptyString(String str) {
		return (str == null || str.trim().length() == 0);
	}

	public static void deleteFile(File file) throws IOException {
	      if (file == null || !file.exists()) {
	    	  return;
	      }
	            
	      if (!file.delete()) {
	        throw new IOException("Can't delete file : " + file.getPath());
	       }
	}

    
	
	 /**
     * Namespace 2 Package algorithm as defined by the JAXB Specification
     *
     * @param Namespace
     * @return String represeting Namespace
     */
    public static String getPackageFromNamespace(String namespace) {
    	
    	//Using the method used by JAXB directly to avoid potential conflicts with JAXB generated code
    	//Therefore commenting out the old code which is based on JAXB 2.0 spec
    	return com.sun.tools.xjc.api.XJC.getDefaultPackageName(namespace);
    }
    
    
    public static String filterExtensionFromXSDFileName(String xsdTypeName) {
    	String typeName = "";
		int index = xsdTypeName.indexOf(".xsd");
		if (index < 0)
			index = xsdTypeName.indexOf(".XSD");

		if (index > 0) {
			typeName = xsdTypeName.substring(0, index);
		}
		
		return typeName;
	}
    
    
    public static String filterTypeNameFromXSDPath(String xsdSrcPath){
    	xsdSrcPath = xsdSrcPath.replace("\\", "/");
    	int index = xsdSrcPath.lastIndexOf("/");
    	return filterExtensionFromXSDFileName(xsdSrcPath.substring(index+1));
    }
    
    
    public static AdditionalXSDInformation parseTheXSDFile(String xsdSrcPath) {

		final AdditionalXSDInformation additionalXSDInformation = new AdditionalXSDInformation();

		class ParseClass extends DefaultHandler {

			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				String elementName = ("".equals(localName)) ? qName : localName;

				if (elementName.startsWith("schema")
						|| elementName.contains(":schema")) {
					String tns = attributes.getValue("targetNamespace");
					String version = attributes.getValue("version");
					additionalXSDInformation.setTargetNamespace(tns);
					additionalXSDInformation.setVersion(version);
				}

				if (elementName.startsWith("simpleType")
						|| elementName.contains(":simpleType")) {
					additionalXSDInformation.setSimpleType(true);
					String typeName = attributes.getValue("name");
					additionalXSDInformation.setTypeName(typeName);
					additionalXSDInformation.getTypeNamesList().add(typeName);
				}

				if (elementName.startsWith("complexType")
						|| elementName.contains(":complexType")) {
					additionalXSDInformation.setSimpleType(false);
					String typeName = attributes.getValue("name");
					additionalXSDInformation.setTypeName(typeName); 
					additionalXSDInformation.getTypeNamesList().add(typeName);
				}

			}

		}

		File xsdFile = new File(xsdSrcPath);
		if(!xsdFile.exists()){
			//need to do additional check for backward compatibility
			if(!checkIfXsdExistsInOlderPath(xsdSrcPath,additionalXSDInformation))
			{
			logger.log(Level.INFO,"Xsd file not found in "+ xsdSrcPath);
				additionalXSDInformation.setDoesFileExist(false);
				logger.log(Level.INFO,"Setting AdditionalXsdInformation setDoesFileExist to false");
			return additionalXSDInformation;
			}
		}else{
			additionalXSDInformation.setDoesFileExist(true);
		}
		
		DefaultHandler defaultHandler = new ParseClass();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        
		if(additionalXSDInformation.isXsdPathChanged())
		{
		 String newXsdLocation = getOlderXsdSrcPath(xsdSrcPath); 
		 xsdFile = new File(newXsdLocation);
		}
		
		try {
			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(xsdFile, defaultHandler);
		} catch (ParserConfigurationException e) {
			getLogger().log(
					Level.WARNING,
					"ParserConfigurationException while parsing XSD file "
							+ xsdSrcPath + "\n" + e.getMessage());
		} catch (SAXException e) {
			getLogger().log(
					Level.WARNING,
					"SAXException while parsing XSD file " + xsdSrcPath + "\n"
							+ e.getMessage());
		} catch (IOException e) {
			getLogger().log(
					Level.WARNING,
					"IOException while parsing XSD file " + xsdSrcPath + "\n"
							+ e.getMessage());
		}

		return additionalXSDInformation;

	}

    /*
     * This method checks if xsd files are present in the older structure.
     */
    private static boolean checkIfXsdExistsInOlderPath(String newXsdPath,
			AdditionalXSDInformation additionalInfo) {

		String oldXsdLocation = getOlderXsdSrcPath(newXsdPath);
		File xsdFileinOldPath = new File(oldXsdLocation);
		if (xsdFileinOldPath.exists()) {
			logger.log(Level.INFO, "Found xsd file in "+oldXsdLocation);
			additionalInfo.setXsdPathChanged(true);
			additionalInfo.setDoesFileExist(true);
			return true;
		} else {
			logger.log(Level.INFO, " xsd file not present in older path ");
			return false;
		}
	}
    /*
     * This method is written to support backward compatibility for typeLibrary.
     * returns the older structure of xsd files.
     */
    public static String getOlderXsdSrcPath(String newXsdPath)
    {
    	String osIndepedentPath = normalizeFilePath(newXsdPath);
    	int indexForSeperator = osIndepedentPath.lastIndexOf(File.separatorChar);
		String xsdTypeName = osIndepedentPath.substring(indexForSeperator+1);
    	int typesIndex = osIndepedentPath.substring(0,indexForSeperator).lastIndexOf(File.separatorChar);
		String olderPath = osIndepedentPath.substring(0, typesIndex);
		
		String olderXsdsrcPath = olderPath  + File.separator
		+ xsdTypeName ;
		return olderXsdsrcPath;
    }

	/**
	 * Returns the content of the input file as a String
	 * 
	 * @param file
	 * @return
	 */
    public static String getContentFromFile(File file) {
    	StringBuilder stringBuilder = new StringBuilder();
    	BufferedReader br = null;
    	try {
			 br = new BufferedReader(new FileReader(file));
			
			String lineContent = null;
			while((lineContent = br.readLine()) != null){
				stringBuilder.append(lineContent).append("\n");
			}
		} catch (FileNotFoundException e) {
			
			getLogger().log(Level.WARNING, e.getMessage());
		} catch (IOException e) {
			getLogger().log(Level.WARNING, e.getMessage());
		}finally{
			CodeGenUtil.closeQuietly(br);
		}
    	
    	return stringBuilder.toString();
    }
    
    
	public static String getContentFromSunEpisodeForMasterEpisode(InputStream inputStream, boolean isFirstFile) throws IOException{
		
		Charset defaultCharset = Charset.defaultCharset(); 
		InputStreamReader isr = null;
		BufferedReader reader = null;
		
		StringBuffer strBuff = new StringBuffer();
		String lineStr = "";
		boolean startOfContentReached = false;
		try{
			isr = new InputStreamReader(inputStream,defaultCharset);
			reader = new BufferedReader(isr);
			while( (lineStr = reader.readLine()) != null){
				if(lineStr.trim().contains(TypeLibraryConstants.MASTER_EPISODE_TURMERIC_START_COMMNENT)){
					startOfContentReached = true;
					
					if(! isFirstFile){
						// if this sun-jaxb.episode file is not the first episode file then we will have to skip two more lines
							reader.readLine();
							reader.readLine();
					}
					
					break;
				}
			}
		
			if(startOfContentReached){
				while( (lineStr = reader.readLine()) != null){
					if(lineStr.trim().contains(TypeLibraryConstants.MASTER_EPISODE_TURMERIC_END_COMMNENT)){
						
						int index = strBuff.lastIndexOf("</bindings>");
						if(index > 0)
							strBuff = new StringBuffer(strBuff.substring(0,index));
						break;
					}
			
					strBuff.append(lineStr + "\n");
				}
			}
			}finally{
				CodeGenUtil.closeQuietly(reader);
				CodeGenUtil.closeQuietly(isr);
			}
		
			return strBuff.toString();
	}
	
	
	public static String normalizePath(String path) {
		if (path == null) {
			return null;
		}
		
		if (path.endsWith("\\") || path.endsWith("/")) {
			return path;
		}
		else {
			return path + File.separatorChar;
		}
	}
	
	public static String toOSFilePath(String path) {
		if (path == null) {
			return null;
		}
		String normaliedOSPath = 
				path.replace('\\', File.separatorChar)
					.replace('/', File.separatorChar);
		
		return normalizePath(normaliedOSPath);
	}
	
	
	public static String normalizeFilePath(String filePath){
		if (filePath == null) {
			return null;
		}
		String normaliedOSFilePath = 
			filePath.replace('\\', File.separatorChar)
					.replace('/', File.separatorChar);
		return normaliedOSFilePath;
	}
	
	public static OutputStream getFileOutputStream(
			String destDir, 
			String fileName) throws IOException {
		
		if (isEmptyString(destDir) || isEmptyString(fileName)) {
			return null;
		}		
		
		File dir = createDir(destDir);
		File outputFile = new File(dir, fileName);
		
		// delete previous file, if exists
		deleteFile(outputFile);
		
		FileOutputStream fileOutStream = new FileOutputStream(outputFile);		
		return fileOutStream;
	}
	
	/**
	 * @deprecated use {@link CodeGenUtil#closeQuietly(Closeable)} instead.
	 */
	@Deprecated
	public static void closeOutputStream(OutputStream outputStream) {
		CodeGenUtil.closeQuietly(outputStream);
	}
	
	
	public static Properties getPropertiesFromFile(String propertiesFilePath) {
		Properties properties = new Properties();
		File file = new File(propertiesFilePath);
		if(!file.exists())
			return properties;
		
		InputStream inputStream = null;
		try {
			 inputStream = new FileInputStream(file);
			properties.load(inputStream);
			
		} catch (FileNotFoundException e) {
			getLogger().log(Level.WARNING, "Could not open input stream for properties file  " + propertiesFilePath);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Could not open input stream for properties file  " + propertiesFilePath);
		} finally{
			CodeGenUtil.closeQuietly(inputStream);
		}
		
		return properties;
	}

	
	public static Set<String> convertSetOfObjectsToSetOfStrings(Set<Object> setOfObjects){
		Set<String> setOfStrings = new HashSet<String>(setOfObjects.size());
		for(Object object : setOfObjects)
			setOfStrings.add(object.toString());
		
		return setOfStrings;
	}
	
	
	
	/**
	 * 
	 * @param propertiesFileRelativePath
	 * @param classLoader
	 * @return a Properties which represents the properties file
	 */
	public static Properties getPropertiesFromFileFromClassPath(String propertiesFileRelativePath,ClassLoader classLoader) {
		Properties properties = new  Properties();
		
		InputStream inputStream = getInputStreamForAFileFromClasspath(propertiesFileRelativePath,classLoader);
		
		if(inputStream != null){
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "Could not get properties file : " + propertiesFileRelativePath + "\n Exception is :" + e.getMessage());
			}
		}
		
		return properties;
	}
	
	
	
	public static  String getTypeNameFromFileName(String xsdFileName){
		String result=  xsdFileName;
		if (!CodeGenUtil.isEmptyString(xsdFileName)) {
			int index = xsdFileName.indexOf(".xsd");
			if (index < 0)
				index = xsdFileName.indexOf(".XSD");

			if (index > 0) {
				result = xsdFileName.substring(0, index);
			}
		}
		return result;
	}
	
	
	public static  QName getQNameOfLibraryType(LibraryType libraryType){
		return new QName(libraryType.getNamespace(),libraryType.getName());
	}

	
	
	@Deprecated
	public static  Set<String> findDependentLibrariesForAType(TypeLibraryCodeGenContext ctx, String typeLibraryName,String typeName) throws Exception {
		getLogger().log(Level.INFO, "Input params for findDependentLibrariesForAType \n" +
				"projectRoot :" +ctx.getProjectRoot() +"\n" +
				"typelibrary name :" + typeLibraryName + "\n" +
				"typeName :" + typeName);
		
		Set<String> depLibraryNames = new HashSet<String>();
		
		TypeLibraryDependencyType typeLibraryDependencyType = null; 
		
  	    
   	    if(!ctx.isProjectRootBlank()){
   	    	String typeDefsFolder = TypeLibraryUtilities.getTypeDepFolder(ctx, typeLibraryName );
   	    	File typeDepFile = new File(typeDefsFolder + File.separator + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME);
   	    	if(!typeDepFile.exists())
   	    		return depLibraryNames; //Its not mandatory for a project to have TypeDependencies.xml file
   	    	
   	    	FileInputStream fis = null;
   	    	try {
   	    		fis = new FileInputStream(typeDepFile);	
  	    		typeLibraryDependencyType = JAXB.unmarshal(fis, TypeLibraryDependencyType.class);
   	    	} catch(IOException e) {
  	  	    	getLogger().log(Level.WARNING, "Could not find the " + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME + " for the library "+ typeLibraryName
  	  	    			+ "  in the method findDependentLibrariesForAType in TypeLibraryUtilities , \n the file was searched at location " + typeDepFile.getAbsolutePath(), e);
  	    		return depLibraryNames;
   	    	} finally {
   	    		IOUtils.closeQuietly(fis);
   	    	}
  	    }
   	    
   	    if(typeLibraryDependencyType == null)
   	    	typeLibraryDependencyType = getTypeLibraryDependencyTypeForLibrary(typeLibraryName);
   	    

   	    getLogger().log(Level.INFO, "Calling function findDependantLibrariesRecursivelyAtInvidualTypeLevel for type : " + typeName);
		
		ProcessedType typeToBeProcessed = new ProcessedType(typeName,typeLibraryName);
		Set<ProcessedType> processedTypes = new HashSet<ProcessedType>();
		
		findDependantLibrariesRecursivelyAtIndividualTypeLevel(
				typeLibraryDependencyType,
				typeToBeProcessed,
				depLibraryNames,
				processedTypes
				);
		
		getLogger().exiting();
		
		return depLibraryNames;
	}
	
	@Deprecated
	private static  void findDependantLibrariesRecursivelyAtIndividualTypeLevel(TypeLibraryDependencyType typeLibraryDependencyType,
			ProcessedType inputProcessType,
			Set<String> depLibrariesName,
			Set<ProcessedType> processedTypes) throws Exception {

		getLogger().entering(new Object[]{typeLibraryDependencyType,inputProcessType,depLibrariesName,processedTypes});
		
		if(typeLibraryDependencyType == null)   return;
		
		if ( ! processedTypes.add(inputProcessType))
			return; // type already processed, so return. this is necessary to avoid cyclic dependency related issues
		
		TypeDependencyType currTypesTypeDependencyType = null;
		
		for(TypeDependencyType typeDependencyType : typeLibraryDependencyType.getType()){
			 if(typeDependencyType.getName().equals(inputProcessType.getTypeName())){
				 currTypesTypeDependencyType = typeDependencyType;
				 break;
			 }
		}
		
		if(currTypesTypeDependencyType == null)
			return; //This type does not have any dependency and hence return.
		

		 for(ReferredTypeLibraryType referredTypeLibraryType :   currTypesTypeDependencyType.getReferredTypeLibrary()){
			 
			 String currReferredLibraryName = referredTypeLibraryType.getName();
			 depLibrariesName.add( currReferredLibraryName );
			 
			 TypeLibraryDependencyType currRefTypeLibraryDependencyType = getTypeLibraryDependencyTypeForLibrary(referredTypeLibraryType.getName());
			 if(currRefTypeLibraryDependencyType == null)
				 return;
			 
				 for(ReferredType currReferredType : referredTypeLibraryType.getReferredType()){
					 depLibrariesName.add(currReferredLibraryName);
					 ProcessedType currTypeToBeProcessed = new ProcessedType(currReferredType.getName(),currReferredLibraryName);
					 
					 getLogger().log(Level.INFO, "Calling function findDependantLibrariesRecursivelyAtInvidualTypeLevel for type : " + currTypeToBeProcessed.getTypeName());
					 findDependantLibrariesRecursivelyAtIndividualTypeLevel(currRefTypeLibraryDependencyType,
							 currTypeToBeProcessed,
							 depLibrariesName,
							 processedTypes);
				 }
			
		 }
		
	}
	
	
	
	
	private static  TypeLibraryDependencyType getTypeLibraryDependencyTypeForLibrary(String libraryName) {
		getLogger().log(Level.INFO, "Entering method getTypeLibraryDependencyTypeForLibrary for : " + libraryName);
		
		String defaultTypeDepFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + libraryName + "/" + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME;
		
		ClassLoader myClassLoader = TypeLibraryUtilities.class.getClassLoader();
		InputStream	inStream      = null;
  	    try {
			inStream      = myClassLoader.getResourceAsStream(defaultTypeDepFilePath);
			
			if(inStream == null){
				getLogger().log(Level.WARNING, "Could not find the " + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME + " for the library "+ libraryName
	  	    			+ " using the first classloader " + myClassLoader);
				
			   myClassLoader = Thread.currentThread().getContextClassLoader();
	  	       inStream   = myClassLoader.getResourceAsStream(defaultTypeDepFilePath);
			}
	  	    
	  	    if(inStream == null) {
	  	    	getLogger().log(Level.WARNING, "Could not find the " + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME + " for the library "+ libraryName
	  	    			+ "  in the method getTypeLibraryDependencyTypeForLibrary in TypeLibraryUtilities using classloader "+ myClassLoader);
	  	    	return null;
	  	    }
	
	  	    getLogger().log(Level.INFO, "Found the " + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME + " for the library "+ libraryName
	    			+ "  in the method getTypeLibraryDependencyTypeForLibrary in TypeLibraryUtilities using classloader "+ myClassLoader + "\n"+
	    			"The path used was : " + defaultTypeDepFilePath);
	  	    
	  	    TypeLibraryDependencyType typeLibraryDependencyType = JAXB.unmarshal(inStream, TypeLibraryDependencyType.class);
	  	    
	  	    return typeLibraryDependencyType;
  	    } finally {
  	    	CodeGenUtil.closeQuietly(inStream);
  	    }
	}


	
	private static class ProcessedType{
		private String typeName;
		private String libraryName;
		
		public ProcessedType(String typeName, String libraryName) {
			super();
			this.typeName = typeName;
			this.libraryName = libraryName;
		}

		@SuppressWarnings("unused")
		public String getLibraryName() {
			return libraryName;
		}

		public String getTypeName() {
			return typeName;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((libraryName == null) ? 0 : libraryName.hashCode());
			result = PRIME * result + ((typeName == null) ? 0 : typeName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ProcessedType other = (ProcessedType) obj;
			if (libraryName == null) {
				if (other.libraryName != null)
					return false;
			} else if (!libraryName.equals(other.libraryName))
				return false;
			if (typeName == null) {
				if (other.typeName != null)
					return false;
			} else if (!typeName.equals(other.typeName))
				return false;
			return true;
		}

		@Override
		public String toString() {
			
			return "Type Name = " + typeName + "  : " + "Library Name = " + libraryName ;
		}
		
		
		
	}
	

	/**
	 * Tries to get the input stream using the classloader used for the loading the class passed as input param. If not found
	 * then it tries to load the file from the current Threads context classloader
	 * @param relativeFilePath
	 * @param parentClassLoader (optional) 
	 * @return
	 */
	public static InputStream getInputStreamForAFileFromClasspath(String relativeFilePath, ClassLoader parentClassLoader ){
		
		relativeFilePath = TypeLibraryUtilities.normalizeFilePath(relativeFilePath);
		relativeFilePath = relativeFilePath.replace("\\", "/");
		
		getLogger().log(Level.INFO, "call to getInputStreamForAFileFromClasspath for path : " + relativeFilePath);
		
		InputStream	inStream      = null;
		if(parentClassLoader != null)
			inStream = parentClassLoader.getResourceAsStream(relativeFilePath);
		
		
		if(inStream == null){
		   ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
  	       inStream   = myClassLoader.getResourceAsStream(relativeFilePath);
		}
		
		if(inStream == null)
			inStream = TypeLibraryUtilities.class.getClassLoader().getResourceAsStream(relativeFilePath);
		
		if(inStream == null)
			getLogger().log(Level.WARNING, "Could not find the file from classpath : " + relativeFilePath + "  in the method getInputStreamForAFileFromClasspath");
		else
			getLogger().log(Level.INFO, "Found the file from classpath : " + relativeFilePath + "  in the method getInputStreamForAFileFromClasspath");
		
		return inStream;
	}
	
	
	/**
	 * 
	 * @param libraryNames       list of library names for which the namespace information has to be derived.  
	 * @param projectRoot        optional - If passed, the TypeInformation.xml file would be looked under this folder first. Only if not found the claasloaders
	 * 										would be used to find the TypeInformaion.xml file
	 * @param parentClassLoader  optional - If passed this classloaded would be used to locate the TypeInformation.xml file
	 * 								before trying to make use of the context class loader.
	 * @return
	 */
	public static Map<String,String> getLibrariesNameSpace(List<String> libraryNames,TypeLibraryCodeGenContext ctx,ClassLoader parentClassLoader) {
		
		Map<String, String> libNSMap = new HashMap<String, String>(libraryNames.size());
		

		for(String currLibraryName : libraryNames){
			
			InputStream inputStream = null;
			try {
				String nameSpace= null;
				
				if(ctx != null && !ctx.isProjectRootBlank()){
					String typeInformationFilePath = TypeLibraryUtilities.getTypeInfoFolder(ctx, currLibraryName) ;
					typeInformationFilePath = normalizePath(typeInformationFilePath) + TypeLibraryConstants.TYPE_INFORMATION_FILE_NAME;
					
					File typeInfoFile = new File(typeInformationFilePath);
					if(typeInfoFile.exists()) {
						try {
							inputStream = new FileInputStream(typeInfoFile);
						} catch (FileNotFoundException e) {
							getLogger().log(Level.WARNING, "Exception while getting file from path " + typeInfoFile.getAbsolutePath());
						}
					}
				}
			
				if(inputStream == null){
					String typeInfoFileRelativePath = getTypeInformationFileRelativePath(currLibraryName);
					inputStream = getInputStreamForAFileFromClasspath(typeInfoFileRelativePath,parentClassLoader);
				}
			
				if(inputStream != null){
					 TypeLibraryType typeLibraryType = JAXB.unmarshal(inputStream, TypeLibraryType.class);
					 if(typeLibraryType != null){
						 nameSpace = typeLibraryType.getLibraryNamespace();
					 }
					 
					 libNSMap.put(currLibraryName, nameSpace);
				}
			} finally {
				 CodeGenUtil.closeQuietly(inputStream);
			}
		}
		
		return libNSMap;
	}
	
	
	public static String getTypeInformationFileRelativePath(String libraryName){
		 String path = normalizePath(TypeLibraryConstants.META_INF_FOLDER ) + libraryName;
		 path = normalizePath(path) + TypeLibraryConstants.TYPE_INFORMATION_FILE_NAME;
		 return path.replace("\\", "/");
	}


	public static  Map<String,Set<String>> findDependentLibrariesAndTypesForAType(TypeLibraryCodeGenContext ctx, String typeLibraryName,String typeName) throws Exception {
		getLogger().log(Level.INFO, "Input params for findDependentLibrariesForAType \n" +
				"projectRoot :" +ctx.getProjectRoot() +"\n" +
				"typelibrary name :" + typeLibraryName + "\n" +
				"typeName :" + typeName);
		
		Map<String,Set<String>> depLibraryAndTypeNamesMap =  new HashMap<String, Set<String>>();
		
		TypeLibraryDependencyType typeLibraryDependencyType = null; 
		
  	    
   	    if(!ctx.isProjectRootBlank()){
   	    	String typeDefsFolder = TypeLibraryUtilities.getTypeDepFolder(ctx, typeLibraryName );
   	    	File typeDepFile = new File(typeDefsFolder + File.separator + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME);
   	    	if(!typeDepFile.exists())
   	    		return depLibraryAndTypeNamesMap; //Its not mandatory for a project to have TypeDependencies.xml file
   	    	
   	    	FileInputStream fis = null;
   	    	try {
   	    		fis = new FileInputStream(typeDepFile);	
  	    		typeLibraryDependencyType = JAXB.unmarshal(fis, TypeLibraryDependencyType.class);
   	    	} catch(IOException e) {
  	  	    	getLogger().log(Level.WARNING, "Could not find the " + TypeLibraryConstants.TYPE_DEPENDENCIES_FILE_NAME + " for the library "+ typeLibraryName
  	  	    			+ "  in the method findDependentLibrariesAndTypesForAType in TypeLibraryUtilities , \n the file was searched at location " + typeDepFile.getAbsolutePath(), e);
  	    		return depLibraryAndTypeNamesMap;
   	    	} finally {
   	    		IOUtils.closeQuietly(fis);
   	    	}
  	    }
   	    
   	    if(typeLibraryDependencyType == null)
   	    	typeLibraryDependencyType = getTypeLibraryDependencyTypeForLibrary(typeLibraryName);
   	    

   	    getLogger().log(Level.INFO, "Calling function findDependantLibrariesRecursivelyAtInvidualTypeLevel for type : " + typeName);
		
		ProcessedType typeToBeProcessed = new ProcessedType(typeName,typeLibraryName);
		Set<ProcessedType> processedTypes = new HashSet<ProcessedType>();
		
		findDependantLibrariesAndTypesRecursivelyAtIndividualTypeLevel(
				typeLibraryDependencyType,
				typeToBeProcessed,
				depLibraryAndTypeNamesMap,
				processedTypes
				);
		
		
		
		return depLibraryAndTypeNamesMap;
	}
	
	
	private static  void findDependantLibrariesAndTypesRecursivelyAtIndividualTypeLevel(TypeLibraryDependencyType typeLibraryDependencyType,
			ProcessedType inputProcessType,
			Map<String, Set<String>> depLibraryAndTypeNamesMap,
			Set<ProcessedType> processedTypes) throws Exception {

		getLogger().entering(new Object[]{typeLibraryDependencyType,inputProcessType,depLibraryAndTypeNamesMap,processedTypes});
		
		if(typeLibraryDependencyType == null)   return;
		
		if ( ! processedTypes.add(inputProcessType))
			return; // type already processed, so return. this is necessary to avoid cyclic dependency related issues
		
		TypeDependencyType currTypesTypeDependencyType = null;
		
		for(TypeDependencyType typeDependencyType : typeLibraryDependencyType.getType()){
			 if(typeDependencyType.getName().equals(inputProcessType.getTypeName())){
				 currTypesTypeDependencyType = typeDependencyType;
				 break;
			 }
		}
		
		if(currTypesTypeDependencyType == null)
			return; //This type does not have any dependency and hence return.
		

		 for(ReferredTypeLibraryType referredTypeLibraryType :   currTypesTypeDependencyType.getReferredTypeLibrary()){
			 
			 String currReferredLibraryName = referredTypeLibraryType.getName();
			 
			 TypeLibraryDependencyType currRefTypeLibraryDependencyType = getTypeLibraryDependencyTypeForLibrary(currReferredLibraryName);
			 if(currRefTypeLibraryDependencyType == null)
				 return;
			 
			 for(ReferredType currReferredType : referredTypeLibraryType.getReferredType()){
				 
				 Set<String> listOfTypesFromCurrLibrary = depLibraryAndTypeNamesMap.get(currReferredLibraryName);
				 if(listOfTypesFromCurrLibrary == null)
					 listOfTypesFromCurrLibrary = new HashSet<String>();
				 
				 listOfTypesFromCurrLibrary.add(currReferredType.getName());
				 
				 if(!depLibraryAndTypeNamesMap.containsKey(currReferredLibraryName)) // the map does not yet have the entry for this library, so use the put method
					 depLibraryAndTypeNamesMap.put(currReferredLibraryName, listOfTypesFromCurrLibrary);
				 
				 ProcessedType currTypeToBeProcessed = new ProcessedType(currReferredType.getName(),currReferredLibraryName);
				 
				 getLogger().log(Level.INFO, "Calling function findDependantLibrariesRecursivelyAtInvidualTypeLevel for type : " + currTypeToBeProcessed.getTypeName());
				 findDependantLibrariesAndTypesRecursivelyAtIndividualTypeLevel(currRefTypeLibraryDependencyType,
						 currTypeToBeProcessed,
						 depLibraryAndTypeNamesMap,
						 processedTypes);
			 }

		 }
		
	}
	
	
	public static String getStringContentFromProperties(Properties properties){
	
			StringBuilder serviceOpsStrBuilder = new StringBuilder();
			serviceOpsStrBuilder.append("#*** Generated file, any changes will be lost upon regeneration ***").append("\n");
			
			// generates:
			// key1=value1
			// key2=value2
			// etc.. like property file contents
			for (Map.Entry<Object, Object> serviceOpsEntry : properties.entrySet()) {
				serviceOpsStrBuilder.append(serviceOpsEntry.getKey().toString())
						  			.append(KEY_VALUE_SEPERATOR)
						  			.append(serviceOpsEntry.getValue().toString())
						  			.append("\r\n");
			}

			return serviceOpsStrBuilder.toString();

		
	}

	public static String[] getXsdPresentInolderPath(TypeLibraryCodeGenContext ctx,
			String libraryName) throws Exception {
		String oldTypesrcPath = TypeLibraryUtilities.getTypesFolder(ctx, libraryName);
		String[]xsdTypes = TypeLibraryUtilities.getFilesInDir(oldTypesrcPath, ".xsd");
		return xsdTypes;
	}
}
