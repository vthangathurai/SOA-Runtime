package org.ebayopensource.turmeric.tools.codegen.protobuf;

public class Field {
	
	
	String fieldRestriction;
	
	String fieldName;
	
	String sequenceNumber;
	
	String fieldType;
	
	boolean enums=false;
	
	public boolean isEnums() {
		return enums;
	}

	public void setEnums(boolean enums) {
		this.enums = enums;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldRestriction() {
		return fieldRestriction;
	}

	public void setFieldRestriction(String fieldRestriction) {
		this.fieldRestriction = fieldRestriction;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	

}
