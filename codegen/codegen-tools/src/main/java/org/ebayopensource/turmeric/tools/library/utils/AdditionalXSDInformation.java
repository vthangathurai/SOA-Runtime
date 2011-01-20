/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.library.utils;

import java.util.ArrayList;

/**
 * @author arajmony
 *
 */
public class AdditionalXSDInformation {
	
	private String targetNamespace;
	private String version;
	private boolean isSimpleType;
	private String typeName;
	private ArrayList<String> typeNamesList;
	private boolean doesFileExist;
	private boolean isJavaFileGenerated = true;
	private boolean isXsdPathChanged;

	public boolean isDoesFileExist() {
		return doesFileExist;
	}

	public void setDoesFileExist(boolean doesFileExist) {
		this.doesFileExist = doesFileExist;
	}

	public boolean isSimpleType() {
		return isSimpleType;
	}

	public void setSimpleType(boolean isSimpleType) {
		this.isSimpleType = isSimpleType;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public ArrayList<String> getTypeNamesList() {
		if(typeNamesList == null)
			typeNamesList = new ArrayList<String>();
		return typeNamesList;
	}

	public boolean isJavaFileGenerated() {
		return isJavaFileGenerated;
	}

	public void setJavaFileGenerated(boolean isJavaFileGenerated) {
		this.isJavaFileGenerated = isJavaFileGenerated;
	}

	public boolean isXsdPathChanged() {
		return isXsdPathChanged;
	}

	public void setXsdPathChanged(boolean isXsdPathChanged) {
		this.isXsdPathChanged = isXsdPathChanged;
	}


}
