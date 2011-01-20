/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Replacement of the XSL process to generate an error descriptor. Mainly due to JDK bugs causing the merge of id &
 * metadata xmls to fail, depending on the JDK in use.
 * <p>
 * Example culprit, the antrun xslt task for ErrorDescriptor will fail on JDK 1.6.0_14 (32-bit, on Linux) to generate an
 * xslt internal key needed to produce an ErrorDescriptor.value in the output.
 */
public class GenErrorDescriptorTask {
	private static final String URL_SOA_ERROR_DESCRIPTORS = "http://wiki.arch.ebay.com/index.php?page=SOAErrorDescriptors";

	public class ErrorDescriptor {
		/* found in both id and metadata xmls */
		public String id;
		/* found in id xml only */
		public int value;
		public String author;
		public String created;
		/* found in metadata xml only */
		public String severity;
		public String category;
		public String description;
	}

	public class ErrorDescriptorSorter implements Comparator<ErrorDescriptor> {
		@Override
		public int compare(ErrorDescriptor o1, ErrorDescriptor o2) {
			return (o1.value - o2.value);
		}
	}

	private Map<String, ErrorDescriptor> descriptors = new HashMap<String, GenErrorDescriptorTask.ErrorDescriptor>();
	private String packageName;
	private String className;
	private String subDomain;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	private Document parseXml(File xmlfile) throws MojoExecutionException {
		try {
			SAXBuilder builder = new SAXBuilder(false);
			return builder.build(xmlfile);
		} catch (JDOMException e) {
			throw new MojoExecutionException("Unable to parse: " + xmlfile, e);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to parse: " + xmlfile, e);
		}
	}

	public void parseMetadataErrorXml(File metadataErrorFile)
			throws MojoExecutionException, MojoFailureException {
		Document doc = parseXml(metadataErrorFile);

		Element root = doc.getRootElement();
		if ("ErrorDescriptors".equals(root.getName()) == false) {
			throw new MojoFailureException(
					"Not a proper ErrorDescriptors file (metadata): "
							+ metadataErrorFile);
		}

		this.subDomain = root.getChildTextTrim("SubDomain");
		String actualPackageName = root.getChildTextTrim("Package");
		String actualClassName = root.getChildTextTrim("ClassName");

		// Sanity Check
		if (packageName.equals(actualPackageName) == false) {
			throw new MojoFailureException(
					"The plugin configured <packageName>,"
							+ " does not match the xml specified <Package>.  "
							+ " Expected [" + packageName + "], but found ["
							+ actualPackageName + "] instead");
		}
		
		// Sanity Check
		if (className.equals(actualClassName) == false) {
			throw new MojoFailureException(
					"The plugin configured <className>,"
							+ " does not match the xml specified <ClassName>.  "
							+ " Expected [" + className + "], but found ["
							+ actualClassName + "] instead");
		}

		@SuppressWarnings("unchecked")
		List<Element> children = root.getChildren("ErrorDescriptor");
		for (Element child : children) {
			String id = child.getAttributeValue("id");
			if (StringUtils.isBlank(id)) {
				throw new MojoFailureException(
						"Bad Metadata ErrorDescriptor XML: "
								+ "Encountered blank id on ErrorDescriptor");
			}
			ErrorDescriptor ed = descriptors.get(id);
			if (ed == null) {
				ed = new ErrorDescriptor();
			}
			ed.id = id;
			ed.severity = child.getAttributeValue("severity");
			ed.category = child.getAttributeValue("category");
			ed.description = child.getChildTextTrim("Description");
			descriptors.put(id, ed);
		}
	}

	public void parseIdErrorXml(File idErrorFile)
			throws MojoExecutionException, MojoFailureException {
		Document doc = parseXml(idErrorFile);

		Element root = doc.getRootElement();
		if ("ErrorDescriptors".equals(root.getName()) == false) {
			throw new MojoFailureException(
					"Not a proper ErrorDescriptors file (id): " + idErrorFile);
		}

		@SuppressWarnings("unchecked")
		List<Element> children = root.getChildren("ErrorDescriptor");
		for (Element child : children) {
			String id = child.getAttributeValue("id");
			if (StringUtils.isBlank(id)) {
				throw new MojoFailureException(
						"Bad Metadata ErrorDescriptor XML: "
								+ "Encountered blank id on ErrorDescriptor");
			}
			ErrorDescriptor ed = descriptors.get(id);
			if (ed == null) {
				ed = new ErrorDescriptor();
			}
			ed.id = id;
			ed.value = asInt(id, child.getAttributeValue("value"));
			ed.author = child.getAttributeValue("author");
			ed.created = child.getAttributeValue("created");
			descriptors.put(id, ed);
		}
	}

	private int asInt(String id, String value) throws MojoFailureException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new MojoFailureException("Bad value on ErrorDescriptor: "
					+ id + ", [" + value + "] is not a parsable number.");
		}
	}

	public void generate(File outputSourceFile) throws MojoExecutionException {
		PrintStream out = null;
		FileOutputStream stream = null;

		try {
			stream = new FileOutputStream(outputSourceFile);
			out = new PrintStream(stream);
			printSource(out);
			out.flush();
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Unable to generate output source file: "
							+ outputSourceFile);
		} finally {
			IOUtil.close(out);
			IOUtil.close(stream);
		}

	}

	public void printSource(PrintStream out) {
		// File Header
		out.println("/*");
		out.println(" * Generated Source, DO NOT EDIT OR COMMIT TO SCM");
		out.printf(" * Generated On: %s%n", (new Date()).toString());
		out.println(" */");

		// Package Namespace
		out.printf("package %s;%n", packageName);
		out.println();

		// Imports
		out.println("import com.ebay.kernel.CodeGenerated;");
		out.println("import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;");
		out.println("import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;");
		out.println("import org.ebayopensource.turmeric.runtime.types.common.error.ServiceBaseErrorDescriptor;");
		out.println();

		// Class Header
		out.println("/**");
		out.println(" * Please DONOT EDIT/CHECKIN this file. If you want to add new Errors to this file");
		out.printf(" * please reserve the error in Entity File %s.xml%n",
				className);
		out.println(" *");
		out.printf(" * For more information please see wiki - %s%n",
				URL_SOA_ERROR_DESCRIPTORS);
		out.println(" *");
		out.println(" * @author turmeric-maven-plugin:gen-errordescriptor");
		out.println(" */");

		// Class
		out.printf("public final class %s%n", className);
		out.println("  extends ServiceBaseErrorDescriptor");
		out.println("  implements CodeGenerated {");

		// Fields
		// TODO: handle ErrorArgument (not seen yet)
		// public static final String PARAM_@name@ = "@name@";
		out.println();

		out.println("    private static final long serialVersionUID = 1L;");
		out.printf("    private static final String SUB_DOMAIN = \"%s\";%n",
				subDomain);
		out.println();

		// Constructor Declaration
		out.printf("    private %s(", className);
		out.print("long errorId, String errorName, String subDomain,");
		out.print(" ErrorSeverity errorSeverity, ErrorCategory category,");
		out.println(" String message) {");
		out.print("        super(errorId, errorName, subDomain,");
		out.println(" errorSeverity, category, message);");
		out.println("    }");
		out.println();

		// Constants
		List<ErrorDescriptor> sorted = new ArrayList<ErrorDescriptor>();
		sorted.addAll(descriptors.values());
		Collections.sort(sorted, new ErrorDescriptorSorter());

		for (ErrorDescriptor ed : sorted) {
			out.printf("    public static final %s %s = new %s(%n", className,
					ed.id, className);
			out.printf("        %d,%n", ed.value);
			out.printf("        \"%s\",%n", ed.id);
			out.printf("        SUB_DOMAIN,%n");
			out.printf("        ErrorSeverity.%s,%n", ed.severity);
			out.printf("        ErrorCategory.%s,%n", ed.category);
			out.printf("        \"%s\");%n", ed.description);
			out.println();
		}

		out.println("}");
	}
}
