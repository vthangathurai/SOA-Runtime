/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.ebayopensource.turmeric.tools.codegen.handler.ConsoleResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenClassLoader;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.JavaToolsClassLoader;



/**
 * Service generator provides higher-level API for code generation tools.
 * 
 * 
 * @author rmandapati
 */
public class ServiceGenerator {
	
	private static final String CODEGEN_BUILDER_CLASS = "org.ebayopensource.turmeric.tools.codegen.ServiceCodeGenBuilder";

	private static Logger s_logger = LogManager.getInstance(ServiceGenerator.class);
	
	private static String s_printUsage = null;	
	private UserResponseHandler m_responseHandler = null;
	public static  String s_JavaHome;
	public static  String s_JdkHome;
	private boolean useClassLoader = true;
	
   // Includes classes from these packages to be loadable by CodeGenClassLoader
	private static String[] s_inclPackagePrefixes = 
		{ 	
	   		"org.apache.axis2",
	   		"org.codehaus",	
			"org.ebayopensource.turmeric.runtime" 
		};
			
   // Excludes classes from these packages from loading by CodeGenClassLoader	
   private static final String[] s_exclPackagePrefixes = 
		{ 	  
	   		"org.ebayopensource.turmeric.runtime.tools.codegen.exception",
	   		"org.ebayopensource.turmeric.runtime.tools.codegen.handler"
		};
   
   // Excludes classes from loading by CodeGenClassLoader	
   private static final String[] s_exclClasses = 
		{ 	  
	  		"org.ebayopensource.turmeric.runtime.tools.codegen.util.CodeGenClassLoader"
		};

   
	
	public ServiceGenerator() {
		setUserResponseHandler(new ConsoleResponseHandler());
	}
	
	
	public ServiceGenerator(UserResponseHandler userResponseHandler) {
		setUserResponseHandler(userResponseHandler);
	}
	

    public UserResponseHandler getUserResponseHandler() {
        return m_responseHandler;
    }


    public void setUserResponseHandler(final UserResponseHandler responseHandler) {
        m_responseHandler = responseHandler;
    }
    
	public boolean isUseClassLoader() {
		return useClassLoader;
	}

	public void setUseClassLoader(boolean useClassLoader) {
		this.useClassLoader = useClassLoader;
	}

	/**
	 * Initiates service code generation process
	 * 
	 * @param String[] - Code generation options
	 * @throws Exception
	 */
	public void startCodeGen(String[] args)  throws Exception  {
        
		parseArgs(args);
		performLoggingInit(args);
		
		if(this.useClassLoader) {
			ClassLoader classLoader = ServiceGenerator.class.getClassLoader();
	        if (classLoader == null) {
	        	classLoader = ClassLoader.getSystemClassLoader();
	        }
	        ClassLoader peerClassLoader =  null;
	        if(Thread.currentThread().getContextClassLoader() != classLoader){
	        	peerClassLoader = Thread.currentThread().getContextClassLoader();
	        }
	        
	        ClassLoader codeGenClassLoader = 
	        		new CodeGenClassLoader(classLoader, peerClassLoader,
	        					s_inclPackagePrefixes, 
	        					s_exclPackagePrefixes, 
	        					s_exclClasses);
	    	startCodeGen(args, codeGenClassLoader);
		} else {
			Class<?> clazz = Class.forName(CODEGEN_BUILDER_CLASS);
			startCodeGen(args, clazz);
		}
    }
	
	private void performLoggingInit(String[] inputArgs) {

		String configFilePath = "";
		for (int i = 0; i < inputArgs.length; i++) {
			if (inputArgs[i].equals(InputOptions.OPT_LOG_CONFIG_FILE)
					&& ((i + 1) < inputArgs.length)) {
				configFilePath = inputArgs[i + 1];
				if (configFilePath.startsWith("-"))
					configFilePath = "";
				break;
			}

		}

		if (CodeGenUtil.isEmptyString(configFilePath)) {
			configFilePath = System.getProperty(CODEGEN_LOG_CONFIG);
			if (CodeGenUtil.isEmptyString(configFilePath))
				configFilePath = System.getenv(CODEGEN_LOG_CONFIG);
		}

		LogManager.initilizeLoggingCriteria(configFilePath);

	}
	
	public void startCodeGen(String[] args, ClassLoader classLoader) throws Exception {
    	String serviceCodeGenClassName = CODEGEN_BUILDER_CLASS;        
    	Class<?> codeGenRunner = classLoader.loadClass(serviceCodeGenClassName);

    	startCodeGen(args, codeGenRunner);
	}
	
    public void startCodeGen(String[] args, Class<?> codeGenRunner) throws Exception {
    	Object codeGenRunnerObj = codeGenRunner.newInstance();
    	
    	Class paramType1 = String[].class;
    	Class paramType2 = UserResponseHandler.class;
        Method codeGenRunMethod = codeGenRunner.getDeclaredMethod("build", paramType1, paramType2);
    	
        try {
        	Object[] runMethodParams = new Object[] { args, getUserResponseHandler() };
        	codeGenRunMethod.invoke(codeGenRunnerObj, runMethodParams);
        }
        catch (InvocationTargetException invocationTargetEx) {
        	Throwable th = invocationTargetEx.getTargetException();
        	if (th instanceof BadInputOptionException) {				
				throw (BadInputOptionException) th;
			} 
        	else if (th instanceof MissingInputOptionException) {
				throw (MissingInputOptionException) th;
			} 
        	else if (th instanceof BadInputValueException) {
				throw (BadInputValueException) th;
			} 
        	else if (th instanceof NoClassDefFoundError) {
				throw new CodeGenFailedException(th.toString(), th);
			} 
        	else if (th instanceof PreValidationFailedException) {
				throw (PreValidationFailedException) th;
			} 
        	else if (th instanceof Exception) {
				throw (Exception) th;
			}
        	
        	throw invocationTargetEx;
        }
        
    }
    
    
	static void initConsoleLogging(boolean enableDebugLogging) {
		Handler consoleLogHandler = new ConsoleOutHandler();		
		s_logger.addHandler(consoleLogHandler);		
		
		Level logLevel = (enableDebugLogging ? Level.FINEST : Level.INFO);
		consoleLogHandler.setLevel(logLevel);
		s_logger.setLevel(logLevel);
	}
	
	private void parseArgs(String[] args) throws BadInputOptionException,
			BadInputValueException, MissingInputOptionException {
		if (args == null || args.length == 0) {
			throw new BadInputOptionException("Arguments for code generation missing");
		}
		int i = 0;
		int argsLength = args.length;
		boolean isGentypeAvailable = false;
		boolean isInputXML = false;
		while (i < argsLength) {
			String optName = args[i].toLowerCase();

			if (InputOptions.OPT_JAVA_HOME.equals(optName)) {
				i = getNextOptionIndex(i, args, optName, true);
				s_JavaHome = args[i];
				System.setProperty("java.home", s_JavaHome);
				s_logger
				.log(Level.INFO, "java.home is being set to " + args[i]);
				
			}
			else if (InputOptions.OPT_JDK_HOME.equals(optName)) {
				i = getNextOptionIndex(i, args, optName, true);
				s_JdkHome = args[i];
				s_logger
				.log(Level.INFO, "jdk home  being used to " + args[i]);
				
			}
			else if(InputOptions.OPT_CODE_GEN_TYPE.equals(optName)){
				i = getNextOptionIndex(i, args, optName, true);
				if(!CodeGenUtil.isEmptyString(args[i]) && !args[i].startsWith("-"))
					isGentypeAvailable = true;				
			}
			else if(InputOptions.OPT_XML.equals(optName))
				isInputXML = true;
			i++;
		}
		
		if(!isGentypeAvailable && !isInputXML){
			throw new MissingInputOptionException("Gentype option is not provided. Pls provide " +
					"a value for " + InputOptions.OPT_CODE_GEN_TYPE + " option");
		}

	}

	
	
	public static int getNextOptionIndex(int currentOptIndex, String[] args, String optionName,boolean shouldHaveValue)
	throws BadInputOptionException,BadInputValueException {
		int nextOptionIndex = currentOptIndex + 1;
		if (nextOptionIndex >= args.length) {
			throw new BadInputValueException("Missing parameter for '"
					+ args[currentOptIndex] + "' option.");
		}

		if(args[nextOptionIndex].startsWith("-") && shouldHaveValue){
			String errMsg = "Please provide a value for the option " + optionName;
			throw new BadInputValueException(errMsg);
		}
		
		String nextArgument = args[nextOptionIndex];
		if(CodeGenUtil.isEmptyString(nextArgument.trim())){
			String errMsg = "Please provide a proper value for the option " + optionName;
			throw new BadInputValueException(errMsg);
		}
				
		
		return nextOptionIndex;
	}
	static void main(final String[] args) throws Exception {		
		ServiceGenerator serviceGenerator = new ServiceGenerator();
		try {
			boolean enableDebugLogging = false;
			for (int i = 0; i < args.length; i++) {
				if (InputOptions.OPT_VERBOSE.equalsIgnoreCase(args[i])) {
					enableDebugLogging = true;
					break;
				}
			}
			initConsoleLogging(enableDebugLogging);
			
			serviceGenerator.startCodeGen(args);			
		} 
		catch (BadInputOptionException badInputEx) {
			s_logger.log(Level.SEVERE, "Code gen failed, bad input options specified : ", badInputEx);
			s_logger.log(Level.FINE, getUsage());
			throw badInputEx;
		} 
		catch (MissingInputOptionException missingInputEx) {
			s_logger.log(Level.SEVERE, "Code gen failed, required input option is missing : ", missingInputEx);
			s_logger.log(Level.FINE, getUsage());
			throw missingInputEx;
		}
		catch(CodeGenFailedException cfe){
			s_logger.log(Level.SEVERE, "Code gen failed, while trying to generate artifacts : ", cfe);
			throw cfe;
		}
		catch (Exception ex) {
			s_logger.log(Level.SEVERE,"Code gen failed : ", ex);
			throw ex;
		}
			
	}

    
 	/**
 	 * Code generation tool usage
 	 * 
 	 * <pre>
 	 * 
 	 *  Usage: java -classpath <classpath> org.ebayopensource.turmeric.runtime.tools.codegen.ServiceGeneratorFacade [Options . . . .]
 	 * 
 	 * -serviceName  <ServiceName>            : Name of the service
 	 * -namespace  <NamespaceURI>             : Namespace URI of the service
 	 * -interface|-class|-xml|-wsdl    <file> : Type of input file, should specify fully qualified file/path
 	 * -gentype      <CodeGenType>            : Which files need to be generated  
 	 *                                          {All|Client|ClientNoConfig|Server|ServerNoConfig|Proxy|Dispatcher|ConfigAll|ClientConfig|ServerConfig|
 	 *                                           GlobalServerConfig|GlocalClientConfig|Wsdl|Interface|Schema|SISkeleton|TypeMappings|WebXml|UnitTest|
 	 *                                           TestClient|ServiceOpProps|SecurityPolicyConfig|ServiceMetadataProps}
 	 * -src          <Dir>                    : Location of source files (incase of -interface/-class options)
 	 * -dest         <Dir>                    : Generated files will go into this directory under gen-src and gen-meta-src
 	 * -jdest        <Dir>                    : Destination location for generated Java source files
 	 * -mdest        <Dir>                    : Destination location for generated configuration and other XML files
 	 * -bin          <Dir>                    : Compiled classes will go into this directory
 	 * -sicn         <ServiceImplClassName>   : Qualified Service Implementation class name
 	 * -gin          <GenInterfaceName>       : Name for the generated interface (incase of -interface/-class/-wsdl options)
 	 * -gip          <GenInterfacePackage>    : Package name for the generated interface (incase of -interface/-class/-wsdl options)
 	 * -cn           <ClientName>             : Name of the client application	
 	 * -scv          <ServiceCurrVersion>     : Current version of the Service	
 	 * -ccgn         <ClientCfgGroupName>     : Client Config Group Name
 	 * -scgn         <ServiceCfgGroupName>    : Server Config Group Name
 	 * -op2cemc      <all=cemc|op1=cemc1,...> : Service operation name to Custom error message class mapping
 	 * -pkg2ns       <pkg1=ns1,pkg2=ns2>      : Java Package to Namespace mapping
 	 * -ngc                                   : Don't generate global configuration files
 	 * -gss                                   : Generate Service Impl Skeleton
 	 * -icsi                                  : Make ServiceImpl implement Common Service operations interface
 	 * -avi                                   : Add Validate Internals for this Service
 	 * -slayer       <ServiceLayer>           : Layer to which Service belongs to, valid values are [INFRASTRUCTURE|APPLICATION|BUSINESS|INTEGRATION]
 	 * -sl           <ServiceLocationURL>     : Service Location
 	 * -wl           <WSDLLocationURL>        : WSDL Location
 	 * -gt                                    : Generate Unit Testcase and Test client classes
 	 * -nc                                    : Don't compile generated java source files (No-Compile)
 	 * -ce                                    : Continue with rest of the code generation when an error occurs 
 	 * -dlw                                   : Generated Doc/Lit/Wrap style WSDL, default is Doc/Lit/Bare 
 	 * -dontprompt                            : Supresses prompt messages
 	 * -verbose                               : Display more debug messages
 	 * -help                                  : Displays this Help / Usage information
 	 * 
 	 * </pre>
 	 * 
 	 * @return Code generation tool usage string
 	 */
	public static String getUsage() {
		
		if (s_printUsage == null) {	
			String NEW_LINE = "\012";
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(NEW_LINE);			
			strBuff.append("Usage: java -classpath <classpath> org.ebayopensource.turmeric.runtime.tools.codegen.ServiceGeneratorFacade [Options . . . .]").append(NEW_LINE).append(NEW_LINE);
			strBuff.append("-serviceName  <ServiceName>             : Name of the service").append(NEW_LINE);
			strBuff.append("-namespace  <NamespaceURI>              : Namespace URI of the service").append(NEW_LINE);
			strBuff.append("-interface|-class|-xml|-wsdl  <file>    : Type of the input file, should specify fully qualified file (path)").append(NEW_LINE);
			strBuff.append("-gentype      <CodeGenType>             : Which files need to be generated  {All|Client|ClientNoConfig|Server|ServerNoConfig|Proxy|Dispatcher|ConfigAll|ClientConfig|ServerConfig|");
			strBuff.append("                                          GlobalServerConfig|GlocalClientConfig|Wsdl|Interface|Schema|SISkeleton|TypeMappings|TypeDefs|WebXml|UnitTest|");
			strBuff.append("                                          TestClient|ServiceOpProps|SecurityPolicyConfig|ServiceMetadataProps}").append(NEW_LINE);
			strBuff.append("-src          <Dir>                     : Location of source files").append(NEW_LINE);
			strBuff.append("-dest         <Dir>                     : Generated files will go into this directory under gen-src and gen-meta-src").append(NEW_LINE);
			strBuff.append("-jdest        <Dir>                     : Destination location for generated Java source files").append(NEW_LINE);
			strBuff.append("-mdest        <Dir>                     : Destination location for generated configuration and other XML files").append(NEW_LINE);
			strBuff.append("-bin          <Dir>                     : Compiled classes will go into this directory").append(NEW_LINE);
			strBuff.append("-pr           <Dir>						: Root path for the creation of the service_metadata.properties file").append(NEW_LINE);
			strBuff.append("-uij 									: Look for the service_metadata.properties file from the class path").append(NEW_LINE);
			strBuff.append("-sicn         <ServiceImplClassName>    : Qualified Service Implementation class name").append(NEW_LINE);
			strBuff.append("-gin          <GenInterfaceName>        : Name for the generated interface (for -interface/-class/-wsdl options)").append(NEW_LINE);
			strBuff.append("-gip          <GenInterfacePackage>     : Package name for the generated interface (for -interface/-class/-wsdl options)").append(NEW_LINE);
			strBuff.append("-cn           <ClientName>              : Name of the client application").append(NEW_LINE);
			strBuff.append("-scv          <ServiceCurrVersion>      : Current version of the Service").append(NEW_LINE);	
			strBuff.append("-ccgn         <ClientCfgGroupName>      : Client Config Group Name").append(NEW_LINE);
			strBuff.append("-scgn         <ServiceCfgGroupName>     : Server Config Group Name").append(NEW_LINE);
			strBuff.append("-op2cemc      <all=cemc|op1=cemc1,...>  : Service operation name to Custom error message class mapping").append(NEW_LINE);
			strBuff.append("-pkg2ns       <pkg1=ns1, pkg2=ns2>      : Java Package to Namespace mapping").append(NEW_LINE);
			strBuff.append("-ns2pkg       <ns1=pkg1, ns2=pkg2>      : Namespace to Java Package mapping").append(NEW_LINE);
			strBuff.append("-ngc                                    : Don't generate global configuration files").append(NEW_LINE);
			strBuff.append("-gss                                    : Generate Service Impl Skeleton").append(NEW_LINE);
			strBuff.append("-icsi                                   : Make ServiceImpl implement Common Service operations interface").append(NEW_LINE);
			strBuff.append("-avi                                    : Add Validate Internals for this Service").append(NEW_LINE);
			strBuff.append("-asl          <ServiceLayerFileLocation>: Location of the file containing the list of service layers").append(NEW_LINE);
			strBuff.append("-slayer       <ServiceLayer>            : Layer to which Service belongs to, pre-defined valid values are [COMMON|INTERMEDIATE|BUSINESS]").append(NEW_LINE);
			strBuff.append("-sl           <ServiceLocationURL>      : Service Location").append(NEW_LINE);
			strBuff.append("-wl           <WSDLLocationURL>         : WSDL Location").append(NEW_LINE);
			strBuff.append("-gt                                     : Generate Unit Testcase and Test client classes").append(NEW_LINE);
			strBuff.append("-nc                                     : Don't compile generated java source files (No-Compile)").append(NEW_LINE);
			strBuff.append("-ce                                     : Continue with rest of the code generation when an error occurs").append(NEW_LINE);
			strBuff.append("-dlw                                    : Generated Doc/Lit/Wrap style WSDL, default is Doc/Lit/Bare").append(NEW_LINE);
			strBuff.append("-dontprompt                             : Supresses prompt messages").append(NEW_LINE);
			strBuff.append("-verbose                                : Display more debug messages").append(NEW_LINE);
			strBuff.append("-lcf 		  <LoggingConfigFile>   	: Location of codegen logging config file").append(NEW_LINE);
			strBuff.append("-help                                   : Displays this Help / Usage information");
			
			s_printUsage = strBuff.toString();
		}
		
		return s_printUsage;
	}
	
	
	private static class ConsoleOutHandler extends ConsoleHandler {
		public ConsoleOutHandler() {
			super();
			setOutputStream(System.out); //KEEPME
		}
	}
	


}
