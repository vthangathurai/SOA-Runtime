/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * Generates Web Application descriptor (web.xml) for a service.
 *
 *
 * @author rmandapati
 */
public class WebAppDescriptorGenerator implements SourceGenerator {

	private static final String WEB_DOT_XML_TEMPLATE =
			"org/ebayopensource/turmeric/tools/codegen/template/webdotxml.tpt";
	private static final String GEN_FILE_NAME = "web.xml";
	private static final String DEST_DIR = "WEB-INF";
	private static final String DIR_GEN_WEB = "gen-web-content";

	private static final String WEB_DOT_XML_CONTENT = "@@WEB_DOT_XML_CONTENT@@";

	private static final String SERVLET_DESC = "@@SERVLET_DESC@@";
	private static final String SERVLET_DISP_NAME = "@@SERVLET_DISP_NAME@@";
	private static final String SERVLET_NAME = "@@SERVLET_NAME@@";
	private static final String SERVLET_CLASS_NAME = "@@SERVLET_CLASS_NAME@@";
	private static final String SERVLET_URL_PATTERN = "@@SERVLET_URL_PATTERN@@";
	private static final String INIT_PARAMS = "@@INIT_PARAMS@@";
	private static final String INIT_PARAM_NAME = "@@INIT_PARAM_NAME@@";
	private static final String INIT_PARAM_VALUE = "@@INIT_PARAM_VALUE@@";

	// TODO: use a smarter console front controller in the future
	private static final String VI_SERVLET_DESC = "Validate Internals";
	private static final String VI_SERVLET_NAME = "ConsoleFrontController";
	private static final String VI_SERVLET_CLASS = "com.ebay.configuration.console.LocalConsoleFrontController";
	private static final String VI_SERVLET_URL_PATTERN = "/admin/v3console/*";

	private static final String SPF_SERVLET_CLASS = "org.ebayopensource.turmeric.runtime.spf.pipeline.SPFServlet";


	private static final String SERVLET_DEFINITION_TMPLT =
	"   <servlet>\n" +
	"       <description>@@SERVLET_DESC@@</description>\n" +
	"       <display-name>@@SERVLET_DISP_NAME@@</display-name>\n" +
	"       <servlet-name>@@SERVLET_NAME@@</servlet-name>\n" +
	"       <servlet-class>@@SERVLET_CLASS_NAME@@</servlet-class>\n" +
	"@@INIT_PARAMS@@"+
	"   </servlet>\n";

	private static final String SERVLET_MAPPING_TMPLT =
	"   <servlet-mapping>\n" +
	"       <servlet-name>@@SERVLET_NAME@@</servlet-name>\n" +
	"       <url-pattern>@@SERVLET_URL_PATTERN@@</url-pattern>\n" +
	"   </servlet-mapping>\n";


	private static final String SERVLET_INIT_PARAM_TMPLT =
    "      <init-param>\n" +
    "         <param-name>@@INIT_PARAM_NAME@@</param-name>\n" +
    "         <param-value>@@INIT_PARAM_VALUE@@</param-value>\n" +
    "      </init-param>\n";



	private static Logger s_logger = LogManager.getInstance(WebAppDescriptorGenerator.class);



	private static WebAppDescriptorGenerator s_webAppDescGenerator =
			new WebAppDescriptorGenerator();

	private WebAppDescriptorGenerator() {}


	public static WebAppDescriptorGenerator getInstance() {
		return s_webAppDescGenerator;
	}



	private Logger getLogger() {
		return s_logger;
	}


	public boolean continueOnError() {
		return false;
	}


	public void generate(CodeGenContext codeGenCtx)  throws CodeGenFailedException  {
		// get WebDotXml template content
		String templateContent = null;
		try {
			templateContent = CodeGenUtil.getTemplateContent(WEB_DOT_XML_TEMPLATE);
		} catch (Exception ex) {
			throw new CodeGenFailedException(
						"Failed to read : " + WEB_DOT_XML_TEMPLATE, ex);
		}

		// repalce all marker parameters
		String content = buildContent(codeGenCtx);
		content = templateContent.replaceAll(WEB_DOT_XML_CONTENT, content);
		// generate a new web.xml file
		generateWebDotXml(content, codeGenCtx);


		getLogger().log(Level.INFO, "Successfully generated " + GEN_FILE_NAME);

	}


	private String buildContent(CodeGenContext codeGenCtx) {

		String svcName = codeGenCtx.getServiceAdminName();

		StringBuilder strBuilder = new StringBuilder();

		if (codeGenCtx.getInputOptions().isAddVI()) {
			strBuilder.append(getVIServletDefContent());
			strBuilder.append(getVIServletMappingContent());
		}

		StringTokenizer strTokenizer = new StringTokenizer(svcName, ",");
		while (strTokenizer.hasMoreTokens()) {
			String serviceName = strTokenizer.nextToken().trim();

			Map<String, String> initParamMap = new HashMap<String, String>();
			initParamMap.put(
						SOAConstants.SERVLET_PARAM_SERVICE_NAME,
						codeGenCtx.getServiceQName().getLocalPart());

			String servletDefContent =
				getServletDefContent(
						serviceName,
						serviceName,
						serviceName,
						SPF_SERVLET_CLASS,
						initParamMap);
			strBuilder.append(servletDefContent);

			String servletMappingContent =
					getServletMappingContent(serviceName, "/" + serviceName);
			strBuilder.append(servletMappingContent);

		}

		return strBuilder.toString();
	}



	private String getVIServletDefContent() {
		return getServletDefContent(
				VI_SERVLET_DESC,
				VI_SERVLET_NAME,
				VI_SERVLET_NAME,
				VI_SERVLET_CLASS,
				null);
	}


	private String getVIServletMappingContent() {
		return getServletMappingContent(VI_SERVLET_NAME, VI_SERVLET_URL_PATTERN);
	}



	private String getServletDefContent(
			String servletDesc,
			String servletDispName,
			String servletName,
			String servletClassName,
			Map<String, String> nameValMap) {

		String content = SERVLET_DEFINITION_TMPLT;

		content = content.replaceAll(SERVLET_DESC, servletDesc);
		content = content.replaceAll(SERVLET_DISP_NAME, servletDispName);
		content = content.replaceAll(SERVLET_NAME, servletName);
		content = content.replaceAll(SERVLET_CLASS_NAME, servletClassName);

		if (nameValMap == null || nameValMap.isEmpty()) {
			content = content.replaceAll(INIT_PARAMS, "");
		}
		else {
			StringBuilder strBuilder = new StringBuilder();
			for (Map.Entry<String, String> mapEntry : nameValMap.entrySet()) {
				strBuilder.append(getInitParamContent(mapEntry.getKey(), mapEntry.getValue()));
			}
			content = content.replaceAll(INIT_PARAMS, strBuilder.toString());
		}

		return content;
	}


	private String getServletMappingContent(
			String servletName,
			String servletURLPattern) {

		String content = SERVLET_MAPPING_TMPLT;

		content = content.replaceAll(SERVLET_NAME, servletName);
		content = content.replaceAll(SERVLET_URL_PATTERN, servletURLPattern);
		return content;
	}


	private String getInitParamContent(
			String paramName,
			String paramValue) {

		String content = SERVLET_INIT_PARAM_TMPLT;

		content = content.replaceAll(INIT_PARAM_NAME, paramName);
		content = content.replaceAll(INIT_PARAM_VALUE, paramValue);
		return content;
	}



	private Writer getFileWriter(String destLoc) throws CodeGenFailedException {
		Writer fileWriter = null;
 		String destFolder = getDestFolder(destLoc);
 		try {
 			fileWriter = CodeGenUtil.getFileWriter(destFolder, GEN_FILE_NAME);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}

 		 return fileWriter;
	}


	private String getDestFolder(String destLoc) {
		String destFolder = CodeGenUtil.toOSFilePath(destLoc) + DEST_DIR;
		return destFolder;
	}




	private void generateWebDotXml(
				String fileContent,
				CodeGenContext codeGenCtx) throws CodeGenFailedException  {

		String baseDir=codeGenCtx.getProjectRoot();
		if(CodeGenUtil.isEmptyString(baseDir)){
		   baseDir = codeGenCtx.getMetaSrcDestLocation();
		}
		else
		  baseDir = CodeGenUtil.toOSFilePath(baseDir) +  DIR_GEN_WEB;

		Writer fileWriter = getFileWriter(baseDir);
		try {
			fileWriter.write(fileContent);
			//fileWriter.flush();
		} catch (IOException ioEx) {
			String errMsg = "Failed to generate : " + GEN_FILE_NAME;
			throw new CodeGenFailedException(errMsg, ioEx);
		} finally {
			CodeGenUtil.closeQuietly(fileWriter);
		}

	}


	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(DEST_DIR) + GEN_FILE_NAME ;
		return filePath;

	}





}
