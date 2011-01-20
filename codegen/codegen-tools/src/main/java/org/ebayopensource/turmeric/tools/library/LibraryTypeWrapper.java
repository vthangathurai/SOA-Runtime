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
package org.ebayopensource.turmeric.tools.library;

import org.ebayopensource.turmeric.common.config.LibraryType;

/**
 * @author arajmony
 *
 */
public class LibraryTypeWrapper {

	private LibraryType libraryType;
	private String libraryTypeName;
	private String typesNameSpace;
	
	public LibraryTypeWrapper(){}
	
	public LibraryTypeWrapper(LibraryType libraryType){
		this.libraryType = libraryType;
		libraryTypeName  = libraryType.getName();
		typesNameSpace   = libraryType.getNamespace();
	}


	@Override
	public String toString() {
		
		return libraryTypeName + " ## " + typesNameSpace;
	}

	/**
	 * @return the libraryType
	 */
	public LibraryType getLibraryType() {
		return libraryType;
	}

	/**
	 * @param libraryType the libraryType to set
	 */
	public void setLibraryType(LibraryType libraryType) {
		this.libraryType = libraryType;
		libraryTypeName  = libraryType.getName();
		typesNameSpace   = libraryType.getNamespace();
	}

	
	
	
	/*
	 *  over-ride the equals and the hashcode method to be based on the  libraryTypeName
	 */
	
	
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((libraryTypeName == null) ? 0 : libraryTypeName.hashCode());
		result = PRIME * result + ((typesNameSpace == null) ? 0 : typesNameSpace.hashCode());
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
		final LibraryTypeWrapper other = (LibraryTypeWrapper) obj;
		if (libraryTypeName == null) {
			if (other.libraryTypeName != null)
				return false;
		} else if (!libraryTypeName.equals(other.libraryTypeName))
			return false;
		if (typesNameSpace == null) {
			if (other.typesNameSpace != null)
				return false;
		} else if (!typesNameSpace.equals(other.typesNameSpace))
			return false;
		return true;
	}
	
	
	
}
