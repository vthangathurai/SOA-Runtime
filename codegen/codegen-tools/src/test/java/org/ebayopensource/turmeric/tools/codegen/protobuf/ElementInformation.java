package org.ebayopensource.turmeric.tools.codegen.protobuf;

public class ElementInformation {
	
	
	String elementName;
	
	String jaxbName;
	
	boolean enums =false;
	
	
    public boolean isEnums() {
		return enums;
	}

	public void setEnums(boolean enums) {
		this.enums = enums;
	}

	public String getJaxbName() {
		return jaxbName;
	}

	public void setJaxbName(String jaxbName) {
		this.jaxbName = jaxbName;
	}

	boolean optional = false;
    
    boolean list = false;
    
    String dataType;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}


	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean listOrNot) {
		this.list = listOrNot;
	}

}
