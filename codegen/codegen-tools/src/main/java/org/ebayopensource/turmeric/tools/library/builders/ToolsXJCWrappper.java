/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.builders;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.AbortException;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.XJCListener;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;

public class ToolsXJCWrappper {
	
	private static ToolsXJCWrappper toolsXJCWrappper;
	
	
	private ToolsXJCWrappper(){}
	
	private static HashMap<String, List<Exception>> s_refToxsdsWithError;
	private static String s_currProcessedType;
	
	
	public static synchronized ToolsXJCWrappper getInstance(){
		if(toolsXJCWrappper == null)
			toolsXJCWrappper = new ToolsXJCWrappper();
		
		return toolsXJCWrappper;
	}
	
	public static boolean runXJC(String[] xjcArguments, HashMap<String, List<Exception>> dsWithError, String xsdTypeName) throws Exception{
	
		s_refToxsdsWithError = dsWithError;
		s_currProcessedType = xsdTypeName;
		
		run(xjcArguments,System.err,System.out,dsWithError,xsdTypeName);
		return true;
	}
	
	
	static class SOACatalogResolver implements EntityResolver{

		private static CallTrackingLogger logger = LogManager.getInstance(SOACatalogResolver.class);
		
		private CallTrackingLogger getLogger(){
			return logger;
		}
		
		
		public InputSource resolveEntity(String publicId, String systemId) {
			getLogger().log(Level.INFO, "systemId passed  : " +systemId);
			
			InputSource inputSource = null;
			
			
			if (systemId != null && systemId.startsWith(TypeLibraryConstants.TYPE_LIB_REF_PROTOCOL)){
				if(inputSource == null)
					inputSource = new InputSource();
			
				String libraryName =null;
				String importedXsdFileName = null;
				
				String[] values =deriveLibraryAndTypeDetailFromSystemID(systemId);
				libraryName = values[0];
				importedXsdFileName = values[1];
			
				String importedXSDFilePath = null;
				ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
				InputStream	inStream = null;
				
				if(!TypeLibraryUtilities.isEmptyString(libraryName)){
					importedXSDFilePath = TypeLibraryConstants.TYPES_FOLDER + "/" + libraryName +  "/"   + importedXsdFileName;
					inStream = findResource(myClassLoader, "1. Looking for XSD as resource path: ", importedXSDFilePath);
				}

				if(inStream == null){
					importedXSDFilePath = TypeLibraryConstants.TYPES_FOLDER + "/"  + importedXsdFileName;
                    inStream = findResource(myClassLoader, "2. Looking for XSD as resource path: ", importedXSDFilePath);
				}
				
		  	   if(inStream == null){
		  		    getLogger().log(Level.SEVERE, "Resolver could not locate the XSD file :  " + importedXSDFilePath );
		  		    
		  		    String errMsg = "The Type " + s_currProcessedType + "  refers to the file " + importedXsdFileName + ". But the referred file could not be found.";
		  		    FileNotFoundException fileNotFoundException = new FileNotFoundException(errMsg);
		  		    CodeGenTypeLibraryGenerator.addExceptionsToXSDErrorList(s_refToxsdsWithError, fileNotFoundException, s_currProcessedType);
		  		    
		  	    	return null;
		  	    }else{
		  	    	getLogger().log(Level.INFO, "Found XSD in the path : " + importedXSDFilePath); 	
		  	    }
		  	   
		  	    inputSource.setSystemId(importedXSDFilePath);
				inputSource.setByteStream(inStream);
			}
			
			
			return inputSource;
		}
		
		
		/**
		 * Make the lookup of schemas a bit more reliable, regardless of
		 * behavior of classloaders and whatnot.
		 * <p>
		 * This will issues a logger {@link Level#WARNING} if the requested schema
		 * exists in multiple places within the classloader.
		 * <p>
		 * This will also prefer local over jar if given a choice between
		 * multiple schemas.
		 * 
		 * @param cl the classloader to lookup the resources within.
		 * @param msg the message to prefix all logs with.
		 * @param path the path to look up.
		 * @return the found resource as an {@link InputStream} or null if not found.
		 */
        private InputStream findResource(ClassLoader cl, String msg, String path) {
            getLogger().log(Level.INFO, msg + path);
            try {
                return ContextClassLoaderUtil.getResourceAsStream(path);
            }
            catch (IOException e) {
                getLogger().log(Level.INFO, msg + path, e);
            }
            return null;
        }

        private  String[] deriveLibraryAndTypeDetailFromSystemID(String systemId) {
		
			String derivedSystemId = systemId.replaceAll("//", "/");
			getLogger().log(Level.INFO, "systemId derived : " +derivedSystemId);

			
			String[] response = new String[2];
			String libraryName = null;
			String typeName = null;
			
			int firstIndex = derivedSystemId.indexOf('/'); // this indexOf would never return -1
			int secondIndex = derivedSystemId.lastIndexOf('/');
			
			
			if( secondIndex > firstIndex){
				libraryName = derivedSystemId.substring(firstIndex + 1, secondIndex);
				typeName = derivedSystemId.substring(secondIndex + 1);
			}
			else if(firstIndex == secondIndex){
				typeName = derivedSystemId.substring(firstIndex+1);
			}
			
			response[0] = libraryName;
			response[1] = typeName;
			
			return response;
			
		}
		
		
	}

	public static int run(String[] args, final PrintStream status,
			final PrintStream out)throws Exception {
		String xsdTypeName = "";
		HashMap<String, List<Exception>> xsdsWithError = new HashMap<String, List<Exception>>();		//populate to avoid NPE 
		
		return run(args, status, out, xsdsWithError, xsdTypeName);
	}
	
	public static int run(String[] args, final PrintStream status,
			final PrintStream out, final HashMap<String, List<Exception>> XSDsWithError, final String xsdTypeName) throws Exception {

		class Listener extends XJCListener {
			
			private  CallTrackingLogger logger = LogManager.getInstance(ToolsXJCWrappper.class);
			
			private CallTrackingLogger getLogger(){
				return logger;
			}
			
			
			/*
			 * commenting out , as this is a findbug because of no charset specification.
			 * 
			 */
			/*
			ConsoleErrorReporter cer = new ConsoleErrorReporter(
					out == null ? new PrintStream(new NullStream()) : out);
			*/

			public void generatedFile(String fileName, int count, int total) {
				message(fileName);
				
			}

			public void message(String msg) {
				if (status != null)
					status.println(msg);
			}

			public void error(SAXParseException exception) {
				//cer.error(exception);
				CodeGenTypeLibraryGenerator.addExceptionsToXSDErrorList(XSDsWithError, exception, xsdTypeName);
				getLogger().log(Level.SEVERE,"XJCListener Exception" , exception);
			}

			public void fatalError(SAXParseException exception) {
				//cer.fatalError(exception);
				CodeGenTypeLibraryGenerator.addExceptionsToXSDErrorList(XSDsWithError, exception, xsdTypeName);
				getLogger().log(Level.SEVERE,"XJCListener Exception" , exception);
			}

			public void warning(SAXParseException exception) {
				//cer.warning(exception);
				getLogger().log(Level.WARNING,"XJCListener Exception" , exception);
			}

			public void info(SAXParseException exception) {
				//cer.info(exception);
				//getLogger().log(Level.INFO,"XJCListener Exception" , exception);
			}
		}

		return run(args, new Listener());
	}

	public static int run(String[] args, @NotNull
	final XJCListener listener) throws BadCommandLineException {

		final OptionsEx opt = new OptionsEx();
		opt.entityResolver = new SOACatalogResolver();
		opt.setSchemaLanguage(Language.XMLSCHEMA); // disable auto-guessing
		try {
			opt.parseArguments(args);
			
		} catch (BadCommandLineException e) {
			e.initOptions(opt);
			throw e;
		}

		
		final ClassLoader contextClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				opt.getUserClassLoader(contextClassLoader));
		
		/*
		ClassLoader codegenClassLoader = ToolsXJCWrappper.class.getClassLoader();
		Thread.currentThread().setContextClassLoader(
				opt.getUserClassLoader(codegenClassLoader));
		*/
		
		try {

			final boolean[] hadWarning = new boolean[1];

			ErrorReceiver receiver = new ErrorReceiverFilter(listener) {
				public void info(SAXParseException exception) {
					if (opt.verbose)
						super.info(exception);
				}

				public void warning(SAXParseException exception) {
					hadWarning[0] = true;
					if (!opt.quiet)
						super.warning(exception);
				}

				@Override
				public void pollAbort() throws AbortException {
					if (listener.isCanceled())
						throw new AbortException();
				}
			};

			Model model = ModelLoader.load(opt, new JCodeModel(), receiver);

			if (model == null) {
				return -1;
			}

			// generate actual code
			receiver.debug("generating code");
			{// don't want to hold outline in memory for too long.
				Outline outline = model.generateCode(opt, receiver);
				if (outline == null) {
					return -1;
				}

				listener.compiled(outline);
			}

			// then print them out
			try {
				CodeWriter cw;
				cw = opt.createCodeWriter();
				model.codeModel.build(cw);
			} catch (IOException e) {
				receiver.error(e);
				return -1;
			}

			return 0;
		} catch (StackOverflowError e) {
			if (opt.verbose)
				throw e;
			else {
				return -1;
			}
		}
	}

	/**
	 * Operation mode.
	 */
	private static enum Mode {
		// normal mode. compile the code
		CODE,

		// dump the signature of the generated code
		SIGNATURE,

		// dump DOMForest
		FOREST,

		// same as CODE but don't produce any Java source code
		DRYRUN,

		// same as CODE but pack all the outputs into a zip and dumps to stdout
		ZIP,

		// testing a new binding mode
		GBIND
	}

	static class OptionsEx extends Options {
		/** Operation mode. */
		protected Mode mode = Mode.CODE;

		/** A switch that determines the behavior in the BGM mode. */
		public boolean noNS = false;

		/** Parse XJC-specific options. */
		public int parseArgument(String[] args, int i)
				throws BadCommandLineException {
			if (args[i].equals("-noNS")) {
				noNS = true;
				return 1;
			}
			if (args[i].equals("-mode")) {
				i++;

				String mstr = args[i].toLowerCase();

				for (Mode m : Mode.values()) {
					if (m.name().toLowerCase().startsWith(mstr)
							&& mstr.length() > 2) {
						mode = m;
						return 2;
					}
				}

			}

			return super.parseArgument(args, i);
		}
	}

}
