/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;

import com.ebay.kernel.util.StringUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Helper class that provides convenient methods for invoking Java Compiler
 * programmatically.
 * 
 * 
 * @author rmandapati
 */
public class JavacHelper {
	private static final Logger LOG = Logger.getLogger(JavacHelper.class.getName());
	
	static final String ERR_MSG = "Failed to compile java source files";

	static final Class<?>[] java5CompileMethodSignature;

	static {
		java5CompileMethodSignature = new Class[2];
		java5CompileMethodSignature[0] = (new String[0]).getClass();
		java5CompileMethodSignature[1] = PrintWriter.class;
	}

	private OutputStream m_out = null;

	public JavacHelper(OutputStream out) {
		m_out = out;
	}

	public void oldCompileJavaSource(List<String> javaSrcFiles, String outputDir)
			throws Exception {

		if (javaSrcFiles == null || javaSrcFiles.isEmpty()) {
			return;
		}

		String classpathString = buildClasspath(outputDir);

		int baseIndex = 5;
		String[] javacArgs = new String[baseIndex + javaSrcFiles.size()];
		javacArgs[0] = "-d";
		javacArgs[1] = outputDir;
		javacArgs[2] = "-classpath";
		javacArgs[3] = classpathString;
		javacArgs[4] = "-g";

		for (int i = 0; i < javaSrcFiles.size(); ++i) {
			javacArgs[baseIndex + i] = javaSrcFiles.get(i);
		}

		internalCompile(javacArgs);
	}
	
	public void compileJavaSource(List<String> javaSrcFiles, String outputDir)
	throws Exception {
		if (javaSrcFiles == null || javaSrcFiles.isEmpty()) {
			return;
		}
		
		String cp = buildClasspath(outputDir);
		
		// @formatter:off
		String baseArgs[] = {
			"-d", outputDir,
			"-cp", cp,
			"-g"
		};
		// @formatter:on
		
		int size = javaSrcFiles.size()+baseArgs.length;
		String args[] = new String[size];
		int offset = baseArgs.length;
		for(String srcFile: javaSrcFiles) {
			args[offset++] = srcFile;
		}
		System.arraycopy(baseArgs, 0, args, 0, baseArgs.length);

		modernJavaCompile(args);
	}

	public void oldCompileJavaSource(String javaSources, String srcDir,
			String outputDir) throws Exception {

		if (CodeGenUtil.isEmptyString(srcDir)) {
			return;
		}

		String classpathPrefix = srcDir + File.pathSeparator + outputDir;
		String classpathString = buildClasspath(classpathPrefix);

		int baseIndex = 6;
		String[] javacArgs = new String[baseIndex];
		javacArgs[0] = "-d";
		javacArgs[1] = outputDir;
		javacArgs[2] = "-classpath";
		javacArgs[3] = classpathString;
		javacArgs[4] = "-g";
		javacArgs[5] = javaSources;

		internalCompile(javacArgs);
	}
	
	public void compileJavaSource(String javaSources, String srcDir,
			String outputDir) throws Exception {
		if (CodeGenUtil.isEmptyString(srcDir)) {
			return;
		}

		String cp = buildClasspath(outputDir);

		// @formatter:off
		String args[] = {
			"-d", outputDir,
			"-sourcepath", srcDir,
			"-cp", cp,
			"-g", javaSources
		};
		// @formatter:on

		modernJavaCompile(args);
	}
	
	private void modernJavaCompile(String args[]) throws CodeGenFailedException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		if(LOG.isLoggable(Level.FINE)) {
			@SuppressWarnings("unchecked")
			List<String> l = Arrays.asList(args);
			LOG.fine("Executing JAVAC: [" + StringUtils.join(l, " ") + "]");
		}
		
		int result = compiler.run(null, m_out, m_out, args);
		if(result != 0) {
			throw new CodeGenFailedException("JAVAC Compile Failure (see output)");
		}
	}

	protected void internalCompile(String[] args) throws Exception {

		Class<?> javacMainClass = null;
		try {
			javacMainClass = Class.forName("com.sun.tools.javac.Main", true, this.getClass().getClassLoader());

			Method compileMethod = javacMainClass.getMethod("compile",
					java5CompileMethodSignature);

			if(LOG.isLoggable(Level.FINER)) {
				@SuppressWarnings("unchecked")
				List<String> l = Arrays.asList(args);
				LOG.finer("Executing Javac: " + StringUtils.join(l, " "));
			}
			
			Object result = compileMethod.invoke(null, new Object[] { args,
					new PrintWriter(m_out) });

			if ((!(result instanceof Integer))
					|| ((Integer) result).intValue() == 0) {

			}

		} catch (IllegalAccessException illegalAccessEx) {
			throw new Exception(ERR_MSG, illegalAccessEx);
		} catch (IllegalArgumentException illegalArgEx) {
			throw new Exception(ERR_MSG, illegalArgEx);
		} catch (InvocationTargetException invTargetEx) {
			throw new Exception(ERR_MSG, invTargetEx);
		} catch (NoSuchMethodException noSuchmethodEx) {
			throw new Exception(ERR_MSG, noSuchmethodEx);
		} catch (ClassNotFoundException clsNotFoundEx) {
			throw clsNotFoundEx;
		} catch (SecurityException securityEx) {
			throw new Exception(ERR_MSG, securityEx);
		}
	}

	public static String buildClasspath(String classpathPrefix) {
		LinkedList<File> classpath = ClassPathUtil.getClassPath();

		StringBuilder cp = new StringBuilder();
		
		if (!CodeGenUtil.isEmptyString(classpathPrefix)) {
			cp.append(classpathPrefix);
			cp.append(File.pathSeparator);
		}
		
		if(classpath.isEmpty()) {
			cp.append(System.getProperty("java.class.path"));
			return cp.toString();
		}
		
		ClassPathUtil.appendClasspath(cp, classpath);
			
		if(LOG.isLoggable(Level.FINER)) {
			StringBuilder b = new StringBuilder();
			b.append("Classpath:");
			String ln = System.getProperty("line.separator");
			for(File path: classpath) {
				b.append(ln).append(path.getAbsolutePath());
			}
			LOG.finer(b.toString());
		}

		return cp.toString();
	}

	/**
	 * Adds given classpath entry to URL class loader if the entry does not
	 * exist already.
	 * 
	 * @param String
	 * @return boolean, true if entry added to classloader otherwise false
	 */
	public static boolean addToClasspath(String classpathEntry) {

		if (CodeGenUtil.isEmptyString(classpathEntry)) {
			return false;
		}

		URL classpathURL = null;
		try {
			File classpathEntryFile = new File(classpathEntry);
			classpathURL = classpathEntryFile.toURI().toURL();
		} catch (Exception ex) {
		}

		if (classpathURL == null) {
			return false;
		}

		boolean entryFound = false;

		ClassLoader classLoader = JavacHelper.class.getClassLoader();

		if (classLoader instanceof CodeGenClassLoader) {
			CodeGenClassLoader codeGenClassLoader = (CodeGenClassLoader) classLoader;
			URL[] classpathURLs = codeGenClassLoader.getURLs();
			URI classpathURI = null;
			try {
				classpathURI = classpathURL.toURI();
			} catch (URISyntaxException uriSyntaxEx) {
				// NOPMD
			}
			if (classpathURI != null) {
				// Check whether classpath entry already exists?
				for (int i = 0; i < classpathURLs.length; i++) {
					try {
						if (classpathURI.equals(classpathURLs[i].toURI())) {
							entryFound = true;
							break;
						}
					} catch (URISyntaxException uriSyntaxEx) {
						// NOPMD
					}
				}
			}
			// given classpath entry not found in the classloader space....
			// adding it...
			if (entryFound == false) {
				codeGenClassLoader.addURL(classpathURL);
				entryFound=true;
			}
		}

		// if entry not found means that entry has been
		// added to url classloader
		return entryFound;
	}

	public static String normalizeURLPath(URI uri) {
		File file = new File(uri);
		return file.getPath();
	}
}
