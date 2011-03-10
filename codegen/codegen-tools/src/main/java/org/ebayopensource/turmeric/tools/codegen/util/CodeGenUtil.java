/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import javax.wsdl.xml.WSDLLocator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.xml.sax.InputSource;

import org.ebayopensource.turmeric.runtime.codegen.common.PkgNSMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.PkgToNSMappingList;


/**
 * Provides utility methods for code generation tools.
 * 
 * 
 * @author rmandapati
 */
public class CodeGenUtil {	
	
    public static final String HTTP = "http://";
    public static final char PACKAGE_CLASS_DELIMITER = '.';

    private static CallTrackingLogger logger = LogManager.getInstance(CodeGenUtil.class);
	
	private static CallTrackingLogger getLogger(){
		return logger;
	}
    
	public static boolean isEmptyString(String str) {
		return (str == null || str.trim().length() == 0);
	}
	
	public static String toQualifiedClassName(String javaFilePath) {
		String filePathNoExt = null;
		if (javaFilePath == null) {
			return null;
		}

		boolean isJavaExtensionPresent = javaFilePath.endsWith(".java");
		if (isJavaExtensionPresent) {
			filePathNoExt = javaFilePath
					.substring(0, javaFilePath.length() - 5);
		}
		boolean isClassExtensionPresent = javaFilePath.endsWith(".class");
		if (isClassExtensionPresent) {
			filePathNoExt = javaFilePath
					.substring(0, javaFilePath.length() - 6);
		}

		if (filePathNoExt == null)
			filePathNoExt = javaFilePath;
		return filePathNoExt.replace('\\', '.').replace('/', '.');
	}
	
	
	public static String getQualifiedClassName(String javaFilePath, String srcLocation) {
		int pkgStartPos = javaFilePath.indexOf(srcLocation);
		String qualifiedJavaFile = null;
		if (pkgStartPos > -1) {
			String normalizedSrcLoc = CodeGenUtil.normalizePath(srcLocation);
			int startPos = pkgStartPos + normalizedSrcLoc.length();
			qualifiedJavaFile = javaFilePath.substring(startPos); 
		}
		else {
			qualifiedJavaFile = javaFilePath;
		}
		
		return toQualifiedClassName(qualifiedJavaFile);
	}
	
	public static String getPackageName(String className) {
		int idx = className.lastIndexOf(".");
		if (idx <= 0) {
			return "";
		}
		return className.substring(0, idx);
	}
	/*
	 * This method is changed to private 
	 * Since hardcoding of "\\" makes it as  OS dependent.
	 * Use toOSFilePath() instead of this method.
	 */
	private  static String normalizePath(String path) {
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
	
	
	public static String getFilePath(String dir, String fileName) {
		if (dir == null || fileName == null) {
			return null;
		}
		
		String filePath = toOSFilePath(dir) + fileName;
		
		return filePath;
	}
	

	public static String toJavaSrcFilePath(String srcDir, Class<?> clazz) {
		if (srcDir == null || clazz == null) {
			return null;
		}
		
		String filePath = toJavaSrcFilePath(srcDir, clazz.getName());
		return filePath;
	}
	
	
	public static String toJavaSrcFilePath(String srcDir, String qualifiedJavaName) {
		if (srcDir == null || qualifiedJavaName == null) {
			return null;
		}		
		String filePath = toOSFilePath(srcDir) + convertToJavaSrcFilePath(qualifiedJavaName);
		return filePath;
	}
	
	
	private static String convertToJavaSrcFilePath(String qualifiedJavaName) {
		if (isEmptyString(qualifiedJavaName)) {
			return qualifiedJavaName;
		}
		
		int dotJavaPos = qualifiedJavaName.lastIndexOf(".java");
		if (dotJavaPos > -1) {
			return convertToFilePath(qualifiedJavaName.substring(0, dotJavaPos), ".java");
		}
		else {
			return  convertToFilePath(qualifiedJavaName, ".java");
		}
	}
	
	

	public static String convertToFilePath(String qualifiedJavaName, String suffix) {
		return qualifiedJavaName.replace('.', File.separatorChar) + suffix;
	}
	
	
	public static String normalizePackageName(String packageName) {
		if (isEmptyString(packageName) || !packageName.endsWith(".")) {
			return packageName;
		}
		else {
			return packageName.substring(0, packageName.length()-1);
		}
		
	}
	
	
	public static boolean isParameterizedType(Type type) {
		return (type instanceof ParameterizedType);
	}
	
	public static boolean isWildCardType(Type type) {
		return (type instanceof WildcardType);
	}
	
	public static boolean isGenericArrayType(Type type) {
		return (type instanceof GenericArrayType);
	}
	
	
	public static String makeFirstLetterUpper(String str) {
		if (isEmptyString(str)) {
			return str;
		}
		
		char firstChar = str.charAt(0);		
		if (Character.isLetter(firstChar) &&
			Character.isLowerCase(firstChar)) {					
			char[] chars = str.toCharArray();
			chars[0] = Character.toUpperCase(firstChar);			
			return String.valueOf(chars);
		}
		else {
			return str;
		}
	}
	
	
	public static String makeFirstLetterLower(String str) {		
		if (isEmptyString(str)) {
			return str;
		}
		
		char firstChar = str.charAt(0);		
		if (Character.isLetter(firstChar) &&
			Character.isUpperCase(firstChar)) {					
			char[] chars = str.toCharArray();
			chars[0] = Character.toLowerCase(firstChar);			
			return String.valueOf(chars);
		}
		else {
			return str;
		}
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
	
	
	public static String genDestFolderPath(
				String destLoc, 
				String serviceName, 
				String suffixPath) {
		
		StringBuilder destFolderPath = new StringBuilder();
		
		destFolderPath.append(toOSFilePath(destLoc));
		destFolderPath.append(toOSFilePath(suffixPath));
		destFolderPath.append(serviceName);
		
		return destFolderPath.toString();
		
	}
	
	public static String genDestFolderPath(String destLoc, String suffixLoc) {
		if (isEmptyString(destLoc)) {
			return destLoc;
		}
		
		String destPath = toOSFilePath(destLoc);
		if (!isEmptyString(suffixLoc)) {
			destPath = destPath + toOSFilePath(suffixLoc);
		}
		return destPath;
	}
	
	
	
	
	
    public static String getNSFromPackageName(String packageName) {

        StringBuffer strBuf = new StringBuffer();
        int prevIndex = packageName.length();
        int currentIndex = packageName.lastIndexOf(PACKAGE_CLASS_DELIMITER);
        if (currentIndex > 0) {
            strBuf.append(HTTP);
        } else if (prevIndex > 0) {
            strBuf.append(HTTP);
            strBuf.append(packageName);
            return strBuf.toString();
        } else if (currentIndex == -1) {
            return strBuf.toString();
        }
        while (currentIndex != -1) {
            strBuf.append(packageName.substring(currentIndex + 1, prevIndex));
            prevIndex = currentIndex;
            currentIndex = packageName.lastIndexOf(PACKAGE_CLASS_DELIMITER, prevIndex - 1);
            strBuf.append(PACKAGE_CLASS_DELIMITER);

            if (currentIndex == -1) {
                strBuf.append(packageName.substring(0, prevIndex));
            }
        }
        return strBuf.toString();
    }
	
	
	
	public static File createDir(String dirPath) throws IOException {
		File dir = new File(dirPath);
		if(dir.exists()) {
			// It exists. all done.
			return dir;
		}
		
		if(dir.mkdirs() == false) {
			// Unable to create directories.
			throw new IOException("Failed to create dir(s) : " + dirPath);
		}
		
		return dir;
	}
	
	
	public static boolean isFileExists(String filePath) {
		if (isEmptyString(filePath)) {
			return false;
		}
		
		File file = new File(filePath);		
		return file.exists();
	}
	
	public static boolean dirExists(String path) {
        if (isEmptyString(path)) {
            return false;
        }
        
        File dir = new File(path);     
        return dir.exists() && dir.isDirectory();
    }
	
	public static void deleteFile(File file) throws IOException {
	      if (file == null || !file.exists()) {
	    	  return;
	      }
	            
	      if (!file.delete()) {
	        throw new IOException("Can't delete file : " + file.getPath());
	       }
	}
	
	
	// Deletes all sub-dir, files under a dir, 
	// and also deletes given dir
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }	
	
	// Deletes all sub-dir, files under a dir,
	// it does not deletes given dir
	public static boolean deleteContentsOfDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return true;
	}	
	
	
	public static Writer getFileWriter(
			String destDir, 
			String fileName) throws IOException {
		
		if (isEmptyString(destDir) || isEmptyString(fileName)) {
			return null;
		}		
		
		File dir = createDir(destDir);
		File outputFile = new File(dir, fileName);
		
		// delete previous file, if exists
		deleteFile(outputFile);
		
	    Charset defaultCharset = Charset.defaultCharset(); 
		FileOutputStream fileOutStream = new FileOutputStream(outputFile);
		OutputStreamWriter bw = new OutputStreamWriter(fileOutStream,defaultCharset);
		Writer buffWriter = new BufferedWriter(bw);
		
		return buffWriter; 
	}

	public static BufferedReader getFileReader(
			String destDir, String fileName) throws IOException {

		if (isEmptyString(destDir) || isEmptyString(fileName)) {
			return null;
		}

		File dir = createDir(destDir);
		File inFile = new File(dir, fileName);

	    Charset defaultCharset = Charset.defaultCharset(); 
	    FileInputStream fileInStream = new FileInputStream(inFile);
	    InputStreamReader bw = new InputStreamReader(fileInStream,defaultCharset);
	    BufferedReader buffReader = new BufferedReader(bw);
		
		return buffReader; 
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
	
	public static void closeQuietly(InputSource inputSource) {
		if (inputSource == null) {
			return; // nothing to do
		}
		
		closeQuietly(inputSource.getCharacterStream());
		closeQuietly(inputSource.getByteStream());
	}
	
	public static void closeQuietly(WSDLLocator locator) {
		if (locator == null) {
			return; // nothing to do
		}
		
		locator.close();
	}
	
	public static void closeQuietly(XMLStreamWriter writer) {
		if (writer == null) {
			return; // nothing to do
		}
		
		try {
			writer.close();
		} catch (XMLStreamException ignore) {
			/* ignore */
		}
	}
	
	public static void closeQuietly(JarFile jarfile) {
		if (jarfile == null) {
			return; // nothing to do
		}
		try {
			jarfile.close();
		} catch (IOException ignore) {
			/* ignore */
		}
	}
	
	public static void closeQuietly(FileHandler fileHandler) {
		if (fileHandler == null) {
			return; // nothing to do
		}
		fileHandler.close();
	}
	
	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return; // nothing to do
		}
		try {
			closeable.close();
		} catch (IOException ignore) {
			/* ignore */
		}
	}

	public static void flushAndCloseQuietly(Closeable closeable) {
		if (closeable == null) {
			return; // nothing to do
		}

		try {
			if(closeable instanceof Flushable){
			((Flushable)closeable).flush();
			}
		} catch (IOException e) {
			/* ignore */
		}

		try {
			closeable.close();
		} catch (IOException ignore) {
			/* ignore */
		}
	}
	public static void move(
			String srcFilePath, 
			String destLoc, 
			boolean override) throws IOException {
		
		File srcFile = new File(srcFilePath);	    
	    File destDir = createDir(destLoc);
	    
	    File newFile = new File(destDir, srcFile.getName());
	    if (newFile.exists()) {
	    	if (override == false) {
	    		return;
	    	} else {
	    		deleteFile(newFile);
	    	}
	    }
	   
	    // Move file to new directory
	    boolean success = srcFile.renameTo(newFile);    
	    if (success == false) {
	    	throw new IOException("Failed to move file : " + srcFilePath + " to " + destLoc);
	    }
	}
	
	
	public static void addAllFiles(File dir, List<String> files) {
        if (dir.isDirectory()) {
        	if (!dir.getName().equals(".")&& 
        		!dir.getName().equals("..")) {
	            File[] children = dir.listFiles();
	            for (int i = 0; i < children.length; i++) {
	               if (children[i].isDirectory()) {
	            	   addAllFiles(children[i], files);
	               }
	               else {
	            	   files.add(children[i].getAbsolutePath());
	               }
	            }
        	}
        }
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
		InputStreamReader isr = new InputStreamReader(input,defaultCharset);
		BufferedReader reader = new BufferedReader(isr);
		
		StringBuilder strBuff = new StringBuilder();
		try {
			char[] charBuff = new char[512];
			int charsRead = -1;
			while ((charsRead = reader.read(charBuff)) > -1) {
				strBuff.append(charBuff, 0, charsRead);
			}
		} finally {
			reader.close();
		}
		return strBuff.toString();
	}
	
	
	public static String getFileContents(String filePath) 
			throws IOException{
		FileInputStream fileInStream = new FileInputStream(filePath);
		return readContent(fileInStream);
	}
	
	
	public static void writeToFile(
			String destLoc, 
			String fileName, 
			String contents) throws IOException {
		
		Writer fileWriter = null;
		try {
			fileWriter = getFileWriter(destLoc, fileName);
			fileWriter.write(contents);
		} finally {
			closeQuietly(fileWriter);
		}
	}
	
	
public static Map<String, String> createNS2PackageMap(InputOptions inputOptions) {
		
		Map<String, String> ns2PkgMap = new HashMap<String, String>();
		
		PkgToNSMappingList pkgNsMapList =  inputOptions.getPkgNSMappings();	
		if (pkgNsMapList != null && !pkgNsMapList.getPkgNsMap().isEmpty()) {
			for (PkgNSMappingType pkgNsMapType : pkgNsMapList.getPkgNsMap()) {
				ns2PkgMap.put(pkgNsMapType.getNamespace(), pkgNsMapType.getPackage());
			}
		}

		return ns2PkgMap;			
	}
	
public static String getJavaClassName(String className) {
	int idx = className.lastIndexOf(".");
	if (idx <= 0) {
		return "";
	}
	return className.substring(idx+1);
}

public static String getFolderPathFrompackageName(String packageName)
{
	if(packageName==null)
		return null;
	
	packageName = packageName.replace('.', File.separatorChar);
	return toOSFilePath(packageName);
}

public static File urlToFile(URL url){
	File file = null;
	try{
		file = new File(url.toURI());
	}catch(Exception exception){
		file = new File(url.getPath());
	}
	return file;	
}


/**
 * Tries to get the input stream using the classloader used for the loading the class passed as input param. If not found
 * then it tries to load the file from the current Threads context classloader
 * @param relativeFilePath
 * @param parentClassLoader (optional) 
 * @return
 */
public static InputStream getInputStreamForAFileFromClasspath(String relativeFilePath, ClassLoader parentClassLoader ){
	
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
		inStream = CodeGenUtil.class.getClassLoader().getResourceAsStream(relativeFilePath);
	
	if(inStream == null)
		getLogger().log(Level.WARNING, "Could not find the file from classpath : " + relativeFilePath + "  in the method getInputStreamForAFileFromClasspath");
	else
		getLogger().log(Level.INFO, "Found the file from classpath : " + relativeFilePath + "  in the method getInputStreamForAFileFromClasspath");
	
	return inStream;
}


public static File getFileFromInputStream (InputStream inputStream , String fileExtension){
	
	FileOutputStream fileOutputStream = null;
	File file = null;
	try {
		
		file = File.createTempFile("ebayCodegen", fileExtension);
		
		byte[] bytes = new byte[10000];
		
		 fileOutputStream = new FileOutputStream(file);
		
		int readCount = 0;
        while ( (readCount = inputStream.read(bytes)) > 0 ){
        	fileOutputStream.write(bytes,0,readCount);
        }
		
	} catch (IOException e) {
		getLogger().log(Level.INFO, "exception while trying to create the tekmp file : exception is : " + e.getMessage());
	} finally{
		if(fileOutputStream != null){
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				getLogger().log(Level.FINE, "Exception while closing the file outut stream for the file ");
			}
		}
	}
	
	
	return file;
}

}


