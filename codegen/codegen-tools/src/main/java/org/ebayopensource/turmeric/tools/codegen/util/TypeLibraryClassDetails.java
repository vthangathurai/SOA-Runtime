/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

public class TypeLibraryClassDetails {

	private String className;
	private String packageName;
	private String nameSpace;
	
	
	public TypeLibraryClassDetails() {
		super();
	}



	public TypeLibraryClassDetails(String className, String packageName, String nameSpace) {
		super();
		this.className = className;
		this.packageName = packageName;
		this.nameSpace = nameSpace;
	}
	
	

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}
	/**
	 * @param nameSpace the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	
}
