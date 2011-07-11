package org.ebayopensource.turmeric.tools.codegen.protobuf;

public class PMDInfo {
	
	String messageName;
	
	String fieldName;
	
	String protoType;
	
	String sequenceNumber;

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getProtoType() {
		return protoType;
	}

	public void setProtoType(String protoType) {
		this.protoType = protoType;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

}
