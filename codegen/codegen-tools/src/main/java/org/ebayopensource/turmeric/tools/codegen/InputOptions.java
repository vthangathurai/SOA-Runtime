/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.TypeLibraryClassDetails;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.codegen.common.NSToPkgMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.OpNameToCemcMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.PkgToNSMappingList;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceCodeGenDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.ServiceType;
import org.ebayopensource.turmeric.runtime.codegen.common.ToolInputType;

/**
 * Holder class for tool input options
 * 
 * @author rmandapati
 */
public class InputOptions {
	
	public static final String DEFAULT_DIR = ".";
	
	public static final String DEFAULT_SERVICE_VERSION = "1.0.0";
	
	public static final String OPT_HELP = "-help";
	// Option to specify Name of the service
	public static final String OPT_SRVC_NAME = "-servicename";
	// Option to specify Admin Name of the service
	public static final String OPT_ADMIN_NAME = "-adminname";
	// Option to specify Namespace for the service
	public static final String OPT_SVC_NAME_SPACE = "-namespace";
	// Option to specify Service Implementation class name of a service
	public static final String OPT_SVC_IMPL_CLASS_NAME = "-sicn";
	// Option to specify Service interface class
	public static final String OPT_INTERFACE = "-interface";
	// Option to specify  Any concreate class
	public static final String OPT_CLASS = "-class";
	// Option to specify Input data in an XML file
	public static final String OPT_XML = "-xml";
	// Option to specify WebServices description language file as input
	public static final String OPT_WSDL = "-wsdl";
	// Option to specify Code generation type, to indicate what files need to be generated
	public static final String OPT_CODE_GEN_TYPE = "-gentype";
	// Option to specify Interface name for the generated interface class
	public static final String OPT_GEN_INTERFACE_NAME = "-gin";
	// Option to specify Interface package name for the generated interface class
	public static final String OPT_GEN_INTERFACE_PACKAGE = "-gip";
	// Option to specify Name of the client application
	public static final String OPT_CLIENT_NAME = "-cn";
	// Option to specify Generate skeleton class for Service Impl class
	public static final String OPT_GEN_SVC_SKELETON = "-gss";
	// Option to specify Generated skeleton class for Service Impl class
	public static final String OPT_OVER_WRITE_IMPLEMENTATION_SKELETON = "-owic";
	// Option to specify Service Impl class to implement Common Service operations interface
	public static final String OPT_IMPL_CSI = "-icsi";
	// Option to specify where to find Source (.java) files of input interface / concrete class
	public static final String OPT_SRC_DIR = "-src";
    // Option to specify where to find meta-src (resource) files
    public static final String OPT_META_SRC_DIR = "-metasrc";
	// Option to specify where to place generated files
	public static final String OPT_DEST_DIR = "-dest";
	// Option to specify where to place generated java source files
	public static final String OPT_JAVA_SRC_GEN_DIR = "-jdest";
	// Option to specify where to place generated meta source files
	public static final String OPT_META_SRC_GEN_DIR = "-mdest";
	// Option to specify where to place compiled classes
	public static final String OPT_BIN_DIR = "-bin";
	// Option to specify Client config group name
	public static final String OPT_CCFG_GROUP_NAME = "-ccgn";
	// Option to specify Service config group name
	public static final String OPT_SCFG_GROUP_NAME = "-scgn";
	// Option to specify Add Validate Internals servlet in web.xml
	public static final String OPT_ADD_VI = "-avi";
	// Option to specify current version of the service being built
	public static final String OPT_SVC_CURR_VERSION = "-scv";
	// Option to specify Location of the service (where it's located)
	public static final String OPT_SVC_LOC = "-sl";
	// Option to specify Location of the WSDL (where it's located)
	public static final String OPT_WSDL_LOC = "-wl";
	// Option to specify whether to generate tests or not
	public static final String OPT_GEN_TESTS = "-gt";
	// Option to specify to output  more debug messages
	public static final String OPT_VERBOSE = "-verbose";
	// Option to specify to suppress any prompt messages
	public static final String OPT_DONT_PROMPT = "-dontprompt";
	// Option to specify Service Level / Layer
	public static final String OPT_SVC_LAYER = "-slayer";
	// Option to specify custom error message class
	public static final String OPT_OP_NAME_CEMC_MAP = "-op2cemc";
	// Option to specify not to generate global config files
	public static final String OPT_NO_GLOBAL_CONFIG = "-ngc";
	// Option to specify Package to Namespace mapping
	public static final String OPT_PKG_2_NS = "-pkg2ns";
	// Option to specify Namespace to Package mapping
	public static final String OPT_NS_2_PKG = "-ns2pkg";
	// Option to specify whether to compile generated code or not
	public static final String OPT_NO_COMPILE = "-nc";
	// Option to specify whether to continue on error or not
	public static final String OPT_CONTINUE_ON_ERROR = "-ce";
	// Option to specify whether to override existing file(s) or not
	public static final String OPT_DONT_OVERRIDE = "-do";
	// Option to specify whether to use Doc/Lit/Wrapped style for WSDL
	public static final String OPT_DOC_LIT_WRAPPED = "-dlw";
	// Option to specify the file location containing the list of service layers
	public static final String OPT_SVC_LAYER_FILE_LOC = "-asl";
   // Option to specify the root path for the creation of the service_metadata.properties file
	public static final String OPT_PROJECT_ROOT = "-pr";
	// Option to indicate the CodeGen that it has to look for the service_metadata.properties file from the class path
	public static final String OPT_USE_INTERFACE_JAR = "-uij";
	// Option to specify the codegen logging config file
	public static final String OPT_LOG_CONFIG_FILE ="-lcf";
	// Option to specify the type library xml file
	public static final String OPT_TYPE_LIBRARY_XML_FILE = "-tlx";
	// Option to specify binding file : jaxb episode file or the jar containing the episode file with other artifacts
	public static final String OPT_BINDING_FILE ="-eBindingFileName";
	// Option to specify http proxy host
	public static final String OPT_HTTP_PROXY_HOST = "-http-proxy-host";
	// Option to specify http proxy port
	public static final String OPT_HTTP_PROXY_PORT = "-http-proxy-port";
	// Option to specify Common types Namepsace
	public static final String OPT_COMMON_TYPES_NS = "-ctns";
	//Option to specify publicServiceName
	public static final String OPT_PUBLIC_SVC = "-publicservicename";
	//option to specify enabledNamespace
	public static final String OPT_ENABLEDNAMESPACE_FOLDING = "-enablednamespacefolding";
	//option to specify environment for consumer
	public static final String OPT_ENV_NAME = "-environment";
	//option to specify environmentMapper
	public static final String OPT_ENV_MAPPER = "-envmapper";
	//option to specify consumerid
	public static final String OPT_CONSUMER_ID = "-consumerid";
	//option to specify javaHome
	public static final String OPT_JAVA_HOME = "-javahome";
	//option to specify jdkHome
	public static final String OPT_JDK_HOME ="-jdkhome";
    //option to specify if ObjectFactory needs to be removed while code generation
	public static final String OPT_OBJECTFACT_GEN = "-noObjectFactoryGeneration";
    // Option to specify generation of shared consumer in an interface project
    public static final String OPT_GEN_SHARED_CONSUMER = "-gen-sharedconsumer";
    // Option to specify generation of shared consumer package in an interface project
    public static final String OPT_PACKAGE_SHARED_CONSUMER = "-package-sharedconsumer";
	

	private String m_namespace;	
	private String m_inputFile;	
	private String m_srcLocation;
	private String m_metaSrcLocation;  
	private String m_destLocation;	
	private String m_javaSrcDestLocation;	
	private String m_metaSrcDestLocation;	
	private String m_binLocation;	
	private boolean m_verbose = false;
	private boolean m_help = false;	
	private InputType m_inputType;	
	private InputType m_orgInputType;
	private CodeGenType m_codeGenType;	
	private String m_serviceName;		
	private String m_adminName;
	private QName m_serviceQName;
	private String m_serviceLocation;	
	private String m_wsdlLocation;
	private String m_svcImplClassName;
	private String m_genInterfaceName;
	private String m_genInterfacePackage;
	private String m_clientCfgGroupName;
	private String m_serverCfgGroupName;
	private boolean m_isGenSkeleton = false;	
	private boolean m_overWriteSkeleton = false;
	private boolean m_implCommonSvcInterface = false;	
	private boolean m_addVI = false;	
	private boolean m_genTests = false;	
	private String m_clientName;
	private String m_svcCurrVersion;
	private String m_wsdlURI;
	private boolean m_isDontPrompt = false;	
	private String m_serviceLayer;
	private boolean m_isNoGlobalConfig = false;	
	private boolean m_isNoCompile = false;
	private boolean m_isGenerateSharedConsumer = false;
	private boolean m_isContinueOnError = false;
	private boolean m_isDocLitWrapped = false;
	private String  m_svcLayerFileLocation; 
	private boolean m_migrate = false;
	private String m_projectRoot;
	private boolean m_useInterfaceJar = false;
	private String m_logConfigFile;
	private String m_typeLibXmlFile;
	private List<TypeLibraryClassDetails> m_typeLibraryClassDetails;
	private List<String> m_bindingFileNames;
	private String m_httpProxyHost;
	private String m_httpProxyPort;
	private String m_commonTypesNS;
	private String m_packageToNSMapString;
	private PkgToNSMappingList m_pkgToNSMappings;
	private NSToPkgMappingList m_NSToPkgMappingList;
	private String m_NS2PkgString;
	private boolean m_IsEnabledNamespaceFolding;
	private String m_PublicServiceName;
	private String m_Environment;
	private String m_EnvironmentMapper;
	private String m_ConsumerId;
	private boolean m_isConsumerAnInterfaceProjectArtifact = false;
	private boolean m_IsBaseConsumerGenertionReq = true;
	private String m_JavaHome;
	private String m_JdkHome;
	private boolean m_isServiceNameRequired = true;
	//this option is used to decide if the inputFile needs to be deleted after preprocessing 
	//For DispatcherForBuild, it is a temporary file that is used for wsdl2java
	private boolean m_isWsdlTobeDeleted;
	private boolean m_isObjectFactoryTobeDeleted = false;
	private boolean m_isObjectFactoryDeletionOptionPassed = false;
	
	private String m_shortPathForSharedConsumer;
	private String m_sharedConsumerPackage;
	
	
	private String m_opNameToCemcMapString;
	private OpNameToCemcMappingList m_opNameToCemcMappings;
	
	
	private String[] m_allSrcLocations;	
	
	private ServiceCodeGenDefType m_svcCodeGenDefType;

	//added as part of SOAPLATFORM-497
	private boolean useExternalServiceFactory = false;
	
	//This variable does not have any corresponding inputOption. This is a transient/derived variable
	private String m_caller;
	
	public static  enum InputType {
		INTERFACE("-interface", ""), 
		CLASS("-class", ""), 
		XML("-xml", ".xml"), 
		WSDL("-wsdl", "");
		
		private final String TYPE_VALUE;
		private final String TYPE_EXT;
		
		InputType(String value, String ext) {
			TYPE_VALUE = value;
			TYPE_EXT = ext;
		}
		
		public String value() {
			return TYPE_VALUE;
		}
		
		public String ext() {
			return TYPE_EXT;
		}
		
		
		public static InputType getInputType(String inputTypeName) {
   			InputType inputOption = null;
            for( InputType inputType : InputType.values() ) {
                if(inputType.value().equals(inputTypeName)) {
                	inputOption = inputType;
                	break;
                 }
            }
			return inputOption;
		}
	}
	
	public static  enum CodeGenType {
		All(0), Client(1), Server(2), Proxy(3), Dispatcher(4), 
		ConfigAll(5), ClientConfig(6), ServerConfig(7), Wsdl(8), 
		Schema(9), SISkeleton(10), TypeMappings(11), WebXml(12),
		UnitTest(13), GlobalServerConfig(14), GlobalClientConfig(15),
		Interface(16), Consumer(17), TypeDefs(18), ClientNoConfig(19),
		ServerNoConfig(20), ServiceOpProps(21), SecurityPolicyConfig(22),
		ServiceMetadataProps(23),ServiceIntfProjectProps(24),DispatcherForBuild(25),ServiceFromWSDLIntf(26),ServiceFromWSDLImpl(27),
		DispatcherForMaven(28),WSDLWithSingleSchema(29),WsdlConversionToMns(30),WsdlWithPublicServiceName(31),SharedConsumer(32)  ;
		
		private final int TYPE_VALUE;
		
		private CodeGenType(int value) {
			TYPE_VALUE = value;
		}
		
		public int value() {
			return TYPE_VALUE;
		}
		
		
		public static CodeGenType getCodeGenType(String codeGenTypeName) {
			CodeGenType codeGenOption = null;
            for( CodeGenType codeGenType : CodeGenType.values() ) {
                if(codeGenType.name().equalsIgnoreCase(codeGenTypeName)) {
                	codeGenOption = codeGenType;
                	break;
                 }
            }
			return codeGenOption;
		}
		
	}
	
	/*
	 * dependency: This enum ServiceLayer should be in sync with the contents of the 
	 *             file  codegen-tools\src\main\resources\META-INF\soa\service_layers.txt
	 *             So pls modify the contents of service_layers.txt whenever a change is made here as well. 
	 */
	public static  enum ServiceLayer {
		COMMON, 
		INTERMEDIATE, 
		BUSINESS;
		
		public static ServiceLayer getServiceLayer(String svcLayerlName) {
			ServiceLayer svcLayerOption = null;
            for( ServiceLayer svcLayer : ServiceLayer.values() ) {
                if(svcLayer.name().equals(svcLayerlName)) {
                	svcLayerOption = svcLayer;
                	break;
                 }
            }
			return svcLayerOption;
		}
	}
	

	public static  enum InterfaceSourceType {
		BLANK_WSDL("blank_wsdl"), 
		WSDL("wsdl"), 
		INTERFACE("java"); 
		
		
		private final String TYPE_VALUE;
		
		InterfaceSourceType(String value) {
			TYPE_VALUE = value;
		}
		
		public String value() {
			return TYPE_VALUE;
		}
	
	}
	
	public InputOptions() {}
	
	
	private boolean isValidDir(String path) {
	    File dir = new File(CodeGenUtil.toOSFilePath(path));
		return dir.exists() && dir.isDirectory();
	}

	public CodeGenType getCodeGenType() {		
		return m_codeGenType;
	}

	public void setCodeGenType(CodeGenType genType) {
		m_codeGenType = genType;
	}

	public String getDestLocation() {
		if (!CodeGenUtil.isEmptyString(m_destLocation)) 
			return m_destLocation;		
		else if(!CodeGenUtil.isEmptyString(m_projectRoot))
			return m_projectRoot;
		
		return DEFAULT_DIR;
	}

	public void setDestLocation(String location) {
		m_destLocation = location;
	}

	public boolean isHelp() {
		return m_help;
	}

	public void setHelp(boolean help) {
		this.m_help = help;
	}

	public String getInputFile() {
		return m_inputFile;
	}

	public void setInputFile(String file) {
		m_inputFile = file;
	}

	public InputType getInputType() {
		return m_inputType;
	}

	public void setInputType(InputType type) {
		m_inputType = type;
	}
	

	public InputType getOriginalInputType() {
		return m_orgInputType;
	}

	public void setOriginalInputType(InputType type) {
		m_orgInputType = type;
	}

	public String getServiceAdminName() {
		if(CodeGenUtil.isEmptyString(m_adminName))
				return getServiceName();
		else
			return m_adminName;
	}
	
	public String getServiceName() {
		return m_serviceName;
	}

	public void setServiceName(String name) {
		m_serviceName = name;
	}
	
	public void setServiceAdminName(String name) {
		m_adminName = name;
	}

	public String getSrcLocation() {
		if (!CodeGenUtil.isEmptyString(m_srcLocation)) {
			return m_srcLocation;
		}
		
		return DEFAULT_DIR;
	}

	public void setSrcLocation(String location) {
		m_srcLocation = location;
	}
	
	public String getMetaSrcLocation() {
	    if(CodeGenUtil.isEmptyString(m_metaSrcLocation)) {
	        // Only need to enter this path if metaSrcLocation is undefined
	        // by the user.  In other words, we have to guess what directory
	        // they want to use.
	        Set<String> searchPaths = new LinkedHashSet<String>();
	        addPath(searchPaths, getDestLocation(), "/src/main/resources");
	        addPath(searchPaths, getDestLocation(), "/src/main/resources");
	        addPath(searchPaths, getDestLocation(), "/meta-src");
	        addPath(searchPaths, getMetaSrcDestLocation(), "/../meta-src");
	        addPath(searchPaths, getSrcLocation(), "/../resources");
	        addPath(searchPaths, getSrcLocation(), "/../meta-src");
	        addPath(searchPaths, getProjectRoot(), "/src/main/resources");
	        addPath(searchPaths, getProjectRoot(), "/meta-src");
	        for(String searchPath: searchPaths) {
	            String path = CodeGenUtil.toOSFilePath(searchPath);
	            if(isValidDir(path)) {
	                m_metaSrcLocation = path;
	                return m_metaSrcLocation;
	            }
	        }
	    }
	    return m_metaSrcLocation;
	}
	
    private void addPath(Set<String> searchPaths, String parentLocation, String subDir) {
        if (parentLocation == null) {
            return; // no parent, skip
        }

        if (parentLocation.equals(".")) {
            return; // can't be default dir either
        }

        searchPaths.add(parentLocation + subDir);
    }


    public void setMetaSrcLocation(String location) {
	    m_metaSrcLocation = location;
	}

	public boolean isVerbose() {
		return m_verbose;
	}

	public void setVerbose(boolean verbose) {
		this.m_verbose = verbose;
	}


	public String getNamespace() {
		return m_namespace;
	}


	public void setNamespace(String namespace) {
		m_namespace = namespace;
	}


	public String getServiceLocation() {
		return m_serviceLocation;
	}


	public void setServiceLocation(String svcLocation) {
		m_serviceLocation = svcLocation;
	}


	public String getWSDLLocation() {
		return m_wsdlLocation;
	}


	public void setWSDLLocation(String wsdlLocation) {
		m_wsdlLocation = wsdlLocation;
	}


	public QName getServiceQName() {
		return m_serviceQName;
	}


	public void setServiceQName(QName svcQName) {
		m_serviceQName = svcQName;
	}


	public ServiceCodeGenDefType getSvcCodeGenDefType() {
		return m_svcCodeGenDefType;
	}


	public void setSvcCodegenDefType(ServiceCodeGenDefType codeGenDefType) {
		m_svcCodeGenDefType = codeGenDefType;
	}


	public String getGenInterfaceName() {
		return m_genInterfaceName;
	}


	public void setGenInterfaceName(String interfaceName) {
		m_genInterfaceName = interfaceName;
	}


	public String getGenInterfacePackage() {
		return m_genInterfacePackage;
	}


	public void setGenInterfacePackage(String interfacePackage) {
		m_genInterfacePackage = interfacePackage;
	}


	public String getServiceImplClassName() {
		return m_svcImplClassName;
	}


	public void setServiceImplClassName(String svcImplClassName) {
		m_svcImplClassName = svcImplClassName;
	}


	public String getClientCfgGroupName() {
		return m_clientCfgGroupName;
	}


	public void setClientCfgGroupName(String cfgGroupName) {
		m_clientCfgGroupName = cfgGroupName;
	}


	public String getServerCfgGroupName() {
		return m_serverCfgGroupName;
	}


	public void setServerCfgGroupName(String cfgGroupName) {
		m_serverCfgGroupName = cfgGroupName;
	}


	public boolean isGenSkeleton() {
		return m_isGenSkeleton;
	}


	public void setIsGenSkeleton(boolean isGenSkeleton) {
		m_isGenSkeleton = isGenSkeleton;
	}


	public boolean isImplCommonSvcInterface() {
		return m_implCommonSvcInterface;
	}


	public void setImplCommonSvcInterface(boolean implCommonSvcInterface) {
		m_implCommonSvcInterface = implCommonSvcInterface;
	}


	public boolean isAddVI() {
		return m_addVI;
	}


	public void setAddVI(boolean isAddVI) {
		m_addVI = isAddVI;
	}


	public String getBinLocation() {
		return m_binLocation;
	}


	public void setBinLocation(String binLocation) {
		m_binLocation = binLocation;
	}


	public String getClientName() {
		return m_clientName;
	}


	public void setClientName(String clientName) {
		m_clientName = clientName;
	}


	public String getSvcCurrVersion() {
		if (m_svcCurrVersion == null) {
			String currVersionFromMetaData = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.SERVICE_VERSION,getServiceAdminName());
			if(CodeGenUtil.isEmptyString(currVersionFromMetaData))
			  return DEFAULT_SERVICE_VERSION;
			else
			  return currVersionFromMetaData;	
		}
		return m_svcCurrVersion;
	}


	public void setSvcCurrVersion(String currVersion) {
		m_svcCurrVersion = currVersion;
	}


	public boolean isGenTests() {
		return m_genTests;
	}


	public void setGenTests(boolean genTests) {
		m_genTests = genTests;
	}


	public String getJavaSrcDestLocation() {
		return m_javaSrcDestLocation;
	}


	public void setJavaSrcDestLocation(String srcDestLocation) {
		m_javaSrcDestLocation = srcDestLocation;
	}


	public String getMetaSrcDestLocation() {
		return m_metaSrcDestLocation;
	}


	public void setMetaSrcDestLocation(String srcDestLocation) {
		m_metaSrcDestLocation = srcDestLocation;
	}


	public String getWSDLURI() {
		return m_wsdlURI;
	}


	public void setWSDLURI(String wsdlURI) {
		m_wsdlURI = wsdlURI;
	}


    public boolean isWSDLBasedService() {
    	
    	// If Input type is WSDL / WSDL URI specified
    	// then Service is based on an existing WSDL
    	if((getInputType() == InputType.WSDL))
    	   return true;
    	
    	if(!CodeGenUtil.isEmptyString(getWSDLURI()))
    	   return true;	

    	boolean isProjectSourceTypeWSDL = false;
    	String projectSourceType = CodeGenInfoFinder.getPropertyFromSvcIntfProjProp(CodeGenConstants.INTERFACE_SOURCE_TYPE,this.getServiceAdminName());
    	if(InputOptions.InterfaceSourceType.BLANK_WSDL.value().equalsIgnoreCase(projectSourceType)  
    		|| InputOptions.InterfaceSourceType.WSDL.value().equalsIgnoreCase(projectSourceType))
    		isProjectSourceTypeWSDL = true;
    		
    	return isProjectSourceTypeWSDL;
    }

    
    public boolean isDontPrompt() {
		return m_isDontPrompt;
	}


	public void setIsDontPrompt(boolean dontPrompt) {
		m_isDontPrompt = dontPrompt;
	}


	public String getServiceLayer() {
		if (m_serviceLayer == null) {
			String layerFromMetadata = CodeGenInfoFinder.getPropertyFromMetaData(CodeGenConstants.SERVICE_LAYER,getServiceAdminName());

			if(CodeGenUtil.isEmptyString(layerFromMetadata))
			    return ServiceLayer.BUSINESS.name();
			else
				return layerFromMetadata;
		}
		return m_serviceLayer;
	}


	public void setServiceLayer(String level) {
		m_serviceLayer = level;
	}


	public String[] getAllSrcLocations() {
		return m_allSrcLocations;
	}


	public void setAllSrcLocations(String[] srcLocations) {
		m_allSrcLocations = srcLocations;
	}


	public boolean isNoGlobalConfig() {
		return m_isNoGlobalConfig;
	}


	public void setIsNoGlobalConfig(boolean noGlobalConfig) {
		m_isNoGlobalConfig = noGlobalConfig;
	}


	public String getPackageToNSMap() {
		return m_packageToNSMapString;
	}


	public void setPackageToNSMap(String pkgToNSMapStr) {
		m_packageToNSMapString = pkgToNSMapStr;
	}
	
	
	public PkgToNSMappingList getPkgNSMappings() {
		
		PkgToNSMappingList result = null;
		
		if (m_pkgToNSMappings != null) {
			result =  m_pkgToNSMappings;
		}
		else if (getSvcCodeGenDefType() != null) {
			ServiceType serviceType = getSvcCodeGenDefType().getServiceInfo();
			result =  serviceType.getPkgNsMapList();
		}
		
		return result;
		
	}
	
	
	public void setPkgNSMappings(PkgToNSMappingList pkgToNSMappings) {
		m_pkgToNSMappings = pkgToNSMappings;
	}

	/**
	 * @deprecated use {@link #getPkgNSMappings()} instead
	 */
	@Deprecated
	public NSToPkgMappingList getNSToPkgMappingList() {
		return m_NSToPkgMappingList;
	}


	public void setNSToPkgMappingList(NSToPkgMappingList toPkgMappingList) {
		m_NSToPkgMappingList = toPkgMappingList;
	}


	public void setNS2Pkg(String NS2Pkg){
		m_NS2PkgString = NS2Pkg;
	}
	
	public String getNS2Pkg(){
		return m_NS2PkgString;
	}
	
	
	public OpNameToCemcMappingList getOpNameToCemcMappings() {
		
		OpNameToCemcMappingList result = null;
		
		if (m_opNameToCemcMappings != null) {
			result =  m_opNameToCemcMappings;
		}
		else if (getSvcCodeGenDefType() != null) {
			ToolInputType toolInputType = getSvcCodeGenDefType().getToolInputInfo();
			result =  toolInputType.getOpNameToCemcMapList();
		}
		
		return result;
	}


	public void setOpNameToCemcMappings(OpNameToCemcMappingList nameToCemcMappings) {
		m_opNameToCemcMappings = nameToCemcMappings;
	}


	public String getOpNameToCemcMapString() {
		return m_opNameToCemcMapString;
	}


	public void setOpNameToCemcMapString(String nameToCemcMapString) {
		m_opNameToCemcMapString = nameToCemcMapString;
	}
	

	public boolean isContinueOnError() {
		return m_isContinueOnError;
	}


	public void setIsContinueOnError(boolean continueOnError) {
		m_isContinueOnError = continueOnError;
	}


	public boolean isNoCompile() {
		return m_isNoCompile;
	}


	public void setIsNoCompile(boolean noCompile) {
		m_isNoCompile = noCompile;
	}
	
	public boolean isGenerateSharedConsumer() {
	    return m_isGenerateSharedConsumer;
	}

    public void setIsGenerateSharedConsumer(boolean generate) {
        m_isGenerateSharedConsumer = generate;
        setIsConsumerAnInterfaceProjectArtifact(true);
    }
    
    public void setSharedConsumerPackage(String packagename) {
        m_sharedConsumerPackage = packagename;
        setShortPathForSharedConsumer(packagename);
        setIsGenerateSharedConsumer(true);
    }
    
    public String getSharedConsumerPackage() {
        return m_sharedConsumerPackage;
    }

	public boolean isDocLitWrapped() {
		return m_isDocLitWrapped;
	}

	public void setIsDocLitWrapped(boolean docLitWrapped) {
		m_isDocLitWrapped = docLitWrapped;
	}

	public String getSvcLayerFileLocation() {
		return m_svcLayerFileLocation;
	}


	public void setSvcLayerFileLocation(String svcFileLocation) {
		m_svcLayerFileLocation = svcFileLocation;
	}

	public boolean getShouldMigrate() {
		return m_migrate;
	}


	public void setShouldMigrate(boolean shouldMigrate) {
		m_migrate = shouldMigrate;
	}
	
	public void setProjectRoot(String projectRoot){
		m_projectRoot = projectRoot;
	}
	
	public String getProjectRoot(){

		if (CodeGenUtil.isEmptyString(m_projectRoot) && !CodeGenUtil.isEmptyString(m_destLocation))
			return m_destLocation;
		
		return m_projectRoot;	
	}
	
	public void setUseInterfaceJar(boolean useInterfaceJar){
		m_useInterfaceJar = useInterfaceJar;
	}
	
	public boolean getUseInterfaceJar(){
		return m_useInterfaceJar;
	}
	
	public void setCaller(String caller){
		m_caller = caller;
	}
	
	public String getCaller(){
		return m_caller;
	}
	
	public void setLogConfigFile(String filePath){
		m_logConfigFile = filePath;
	}
	
	public String getLogConfigFile() {
		return m_logConfigFile;
	}
	
	
	/**
	 * @return the m_typeLibXmlFile
	 */
	public String getTypeLibXmlFile() {
		return m_typeLibXmlFile;
	}


	/**
	 * @param libXmlFile the m_typeLibXmlFile to set
	 */
	public void setTypeLibXmlFile(String libXmlFile) {
		m_typeLibXmlFile = libXmlFile;
	}




	/**
	 * @return the m_typeLibraryClassDetails
	 */
	public List<TypeLibraryClassDetails> getTypeLibraryClassDetails() {
		if(m_typeLibraryClassDetails == null)
			m_typeLibraryClassDetails = new ArrayList<TypeLibraryClassDetails>();
		return m_typeLibraryClassDetails;
	}


	/**
	 * @param libraryClassDetails the m_typeLibraryClassDetails to set
	 */
	public void setTypeLibraryClassDetails(
			List<TypeLibraryClassDetails> libraryClassDetails) {
		m_typeLibraryClassDetails = libraryClassDetails;
	}


	


	/**
	 * @return the m_httpProxyHost
	 */
	public String getHttpProxyHost() {
		return m_httpProxyHost;
	}


	/**
	 * @param proxyHost the m_httpProxyHost to set
	 */
	public void setHttpProxyHost(String proxyHost) {
		m_httpProxyHost = proxyHost;
		System.setProperty("http.proxyHost", proxyHost);
	}


	/**
	 * @return the m_httpProxyPort
	 */
	public String getHttpProxyPort() {
		return m_httpProxyPort;
	}


	/**
	 * @param proxyPort the m_httpProxyPort to set
	 */
	public void setHttpProxyPort(String proxyPort) {
		m_httpProxyPort = proxyPort;
		System.setProperty("http.proxyPort", proxyPort);
	}


	public boolean isOverWriteSkeleton() {
		return m_overWriteSkeleton;
	}


	public void setOverWriteSkeleton(boolean writeSkeleton) {
		m_overWriteSkeleton = writeSkeleton;
	}


	/**
	 * @return the m_bindingFileNames
	 */
	public List<String> getBindingFileNames() {
		if(m_bindingFileNames == null)
			m_bindingFileNames = new ArrayList<String>();
		
		return m_bindingFileNames;
	}



	public String getCommonTypesNS() {
		return m_commonTypesNS;
	}


	public void setCommonTypesNS(String typesNS) {
		m_commonTypesNS = typesNS;
	}
	public boolean isEnabledNamespaceFoldingSet() {
		return m_IsEnabledNamespaceFolding;
	}


	public void setEnabledNamespaceFolding(boolean isEnabledNamespaceFolding) {
		m_IsEnabledNamespaceFolding = isEnabledNamespaceFolding;
	}


	public String getPublicServiceName() {
		return m_PublicServiceName;
	}


	public void setPublicServiceName(String publicServiceName) {
		m_PublicServiceName = publicServiceName;
	}

	public String getEnvironment() {
		return m_Environment;
	}


	public void setEnvironment(String environment) {
		m_Environment = environment;
	}


	public String getEnvironmentMapper() {
		return m_EnvironmentMapper;
	}


	public void setEnvironmentMapper(String environmentMapper) {
		m_EnvironmentMapper = environmentMapper;
	}


	public boolean getIsWsdlTobeDeleted() {
		return m_isWsdlTobeDeleted;
	}


	public void setIsFileTobeDeleted(boolean fileTobeDeleted) {
		m_isWsdlTobeDeleted = fileTobeDeleted;
	}


	public String getConsumerId() {
		return m_ConsumerId;
	}


	public void setConsumerId(String consumerId) {
		m_ConsumerId = consumerId;
	}


	public boolean isConsumerAnInterfaceProjectArtifact() {
		return m_isConsumerAnInterfaceProjectArtifact;
	}


	public void setIsConsumerAnInterfaceProjectArtifact(
			boolean consumerAnInterfaceProjectArtifact) {
		m_isConsumerAnInterfaceProjectArtifact = consumerAnInterfaceProjectArtifact;
	}

	public boolean isBaseConsumerGenertionReq() {
		return m_IsBaseConsumerGenertionReq;
	}


	public void setIsBaseConsumerGenertionReq(boolean isBaseConsumerGenertionReq) {
		m_IsBaseConsumerGenertionReq = isBaseConsumerGenertionReq;
	}
	
	public String getJavaHome() {
		return m_JavaHome;
	}


	public void setJavaHome(String javaHome) {
		m_JavaHome = javaHome;
	}
	
	public String getJdkHome() {
		return m_JdkHome;
	}


	public void setJdkHome(String jdkHome) {
		m_JdkHome = jdkHome;
	}
	
	//This method is to make sure serviceName tag does not appear in cc.xml for post 2.4 projects.
	//This method is also used to check if Consumer is post2.4 world or not.
	public boolean isServiceNameRequired() {
		return m_isServiceNameRequired;
	}

	public void setServiceNameRequired(boolean serviceNameRequired) {
		m_isServiceNameRequired = serviceNameRequired;
	}

	public boolean isObjectFactoryTobeDeleted() {
		return m_isObjectFactoryTobeDeleted;
	}


	public void setObjectFactoryTobeDeleted(boolean objectFactoryTobeDeleted) {
		
		m_isObjectFactoryTobeDeleted = objectFactoryTobeDeleted;
	}
	public boolean isObjectFactoryDeletionOptionPassed() {
		return m_isObjectFactoryDeletionOptionPassed;
	}


	public void setObjectFactoryDeletionOptionPassed(
			boolean objectFactoryDeletionOptionPassed) {
		m_isObjectFactoryDeletionOptionPassed = objectFactoryDeletionOptionPassed;
	}
	
	
	public String getShortPathForSharedConsumer() {
		return m_shortPathForSharedConsumer;
	}


	public void setShortPathForSharedConsumer(String pathForSharedConsumer) {
		m_shortPathForSharedConsumer = pathForSharedConsumer;
	}


	public boolean isUseExternalServiceFactory() {
		return useExternalServiceFactory;
	}


	public void setUseExternalServiceFactory(boolean useExternalServiceFactory) {
		this.useExternalServiceFactory = useExternalServiceFactory;
	}

	public String toString() {
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("[ ");
		strBuilder.append(OPT_SRVC_NAME + " = " + m_serviceName).append("\n");	
		strBuilder.append(OPT_SVC_NAME_SPACE + " = " + m_namespace).append("\n");	
		strBuilder.append("InputType = " + getInputType().name()).append("\n");	
		strBuilder.append("Input File = " + m_inputFile).append("\n");	
		strBuilder.append(OPT_CODE_GEN_TYPE + " = " + getCodeGenType().name()).append("\n");	
		strBuilder.append(OPT_SRC_DIR + " = " + m_srcLocation).append("\n");	
        strBuilder.append(OPT_META_SRC_DIR + " = " + m_metaSrcLocation).append("\n");    
		strBuilder.append(OPT_DEST_DIR + " = " + m_destLocation).append("\n");	
		strBuilder.append(OPT_JAVA_SRC_GEN_DIR + " = " + m_javaSrcDestLocation).append("\n");	
		strBuilder.append(OPT_META_SRC_GEN_DIR + " = " + m_metaSrcDestLocation).append("\n");	
		strBuilder.append(OPT_BIN_DIR + " = " + m_binLocation).append("\n");	
		strBuilder.append(OPT_VERBOSE + " = " + m_verbose).append("\n");	
		strBuilder.append(OPT_HELP + " = " + m_help).append("\n");	
		strBuilder.append(OPT_SVC_LOC + " = " + m_serviceLocation).append("\n");	
		strBuilder.append(OPT_WSDL_LOC + " = " + m_wsdlLocation).append("\n");
		strBuilder.append(OPT_SVC_IMPL_CLASS_NAME + " = " + m_svcImplClassName).append("\n");
		strBuilder.append(OPT_GEN_INTERFACE_NAME + " = " + m_genInterfaceName).append("\n");
		strBuilder.append(OPT_GEN_INTERFACE_PACKAGE + " = " + m_genInterfacePackage).append("\n");
        strBuilder.append(OPT_GEN_SHARED_CONSUMER + " = " + m_isGenerateSharedConsumer).append("\n");
        strBuilder.append(OPT_PACKAGE_SHARED_CONSUMER + " = " + m_sharedConsumerPackage).append("\n");
		strBuilder.append(OPT_CCFG_GROUP_NAME + " = " + m_clientCfgGroupName).append("\n");
		strBuilder.append(OPT_SCFG_GROUP_NAME + " = " + m_serverCfgGroupName).append("\n");
		strBuilder.append(OPT_GEN_SVC_SKELETON + " = " + m_isGenSkeleton).append("\n");
		strBuilder.append(OPT_OVER_WRITE_IMPLEMENTATION_SKELETON + " = " + m_overWriteSkeleton).append("\n");
		strBuilder.append(OPT_IMPL_CSI + " = " + m_implCommonSvcInterface).append("\n");	
		strBuilder.append(OPT_ADD_VI + " = " + m_addVI).append("\n");	
		strBuilder.append(OPT_GEN_TESTS + " = " + m_genTests).append("\n");	
		strBuilder.append(OPT_CLIENT_NAME + " = " + m_clientName).append("\n");
		strBuilder.append(OPT_SVC_CURR_VERSION + " = " + m_svcCurrVersion).append("\n");
		strBuilder.append(OPT_DONT_PROMPT + " = " + m_isDontPrompt).append("\n");	
		strBuilder.append(OPT_SVC_LAYER + " = " + getServiceLayer()).append("\n");
		strBuilder.append(OPT_NO_GLOBAL_CONFIG + " = " + m_isNoGlobalConfig).append("\n");	
		strBuilder.append(OPT_PKG_2_NS + " = " + m_packageToNSMapString).append("\n"); 	
		strBuilder.append(OPT_NO_COMPILE + " = " + m_isNoCompile).append("\n"); 	
		strBuilder.append(OPT_CONTINUE_ON_ERROR + " = " + m_isContinueOnError).append("\n"); 	
		strBuilder.append(OPT_OP_NAME_CEMC_MAP + " = " + m_opNameToCemcMapString).append("\n");
		strBuilder.append(OPT_DOC_LIT_WRAPPED + " = " + m_isDocLitWrapped).append("\n");
		strBuilder.append(OPT_SVC_LAYER_FILE_LOC + " = " + m_svcLayerFileLocation).append("\n");		
		strBuilder.append("Migrate option is :" + " = " + m_migrate).append("\n");
		strBuilder.append(OPT_PROJECT_ROOT + " = " + m_projectRoot).append("\n");
		strBuilder.append(OPT_USE_INTERFACE_JAR + " = " + m_useInterfaceJar).append("\n");
		strBuilder.append(OPT_NS_2_PKG+ " = " + m_NS2PkgString).append("\n");
		strBuilder.append(OPT_LOG_CONFIG_FILE+ " = " + m_logConfigFile).append("\n");
		strBuilder.append(OPT_TYPE_LIBRARY_XML_FILE + " = " + m_typeLibXmlFile).append("\n");
		strBuilder.append(OPT_BINDING_FILE + " = " + m_bindingFileNames).append("\n");
		strBuilder.append(OPT_HTTP_PROXY_HOST + " = " + m_httpProxyHost).append("\n");
		strBuilder.append(OPT_HTTP_PROXY_PORT + " = " + m_httpProxyPort).append("\n");
		strBuilder.append(OPT_COMMON_TYPES_NS + " = " + m_commonTypesNS).append("\n");
	    strBuilder.append(CodeGenConstants.ENABLE_NAMESPACE_FOLDING + " = "+m_IsEnabledNamespaceFolding).append("\n");
	    strBuilder.append(CodeGenConstants.PUBLIC_SERVICE_NAME + " = " + m_PublicServiceName).append("\n");
	    strBuilder.append(CodeGenConstants.PROPERTY_SHARED_CONSUMER_SHORTER_PATH + " = " + m_shortPathForSharedConsumer).append("\n");
	    strBuilder.append(OPT_ENV_NAME + " = " + m_Environment).append("\n");
	    strBuilder.append(OPT_ENV_MAPPER + " = " + m_EnvironmentMapper).append("\n");
	    strBuilder.append(OPT_CONSUMER_ID + " = " + m_ConsumerId).append("\n");
	    strBuilder.append(OPT_JAVA_HOME + " = " + m_JavaHome).append("\n");
	    strBuilder.append(OPT_JDK_HOME + " = " + m_JdkHome).append("\n");
	    strBuilder.append(OPT_OBJECTFACT_GEN + " = " + m_isObjectFactoryTobeDeleted).append("\n");
	    strBuilder.append("DERIVED-NO-INPUT:m_isWsdlTobeDeleted"+ " = " + m_isWsdlTobeDeleted).append("\n");
	    strBuilder.append("DERIVED-NO-INPUT:m_isConsumerAnInterfaceProjectArtifact"+ " = " + m_isConsumerAnInterfaceProjectArtifact).append("\n");
	    strBuilder.append("DERIVED-NO-INPUT:m_IsBaseConsumerGenertionReq"+ " = " + m_IsBaseConsumerGenertionReq).append("\n");
		
		strBuilder.append(" ]");
		
		return strBuilder.toString();
	}
}
