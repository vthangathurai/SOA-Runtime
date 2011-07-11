package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.util.ArrayList;
import java.util.List;

public class MessageInformation {
	
	String messageName;
	
	String namespace;
	
	boolean enums = false;
	
	List<String> enumList;
	
	public boolean isEnums() {
		return enums;
	}

	public void setEnums(boolean enums) {
		this.enums = enums;
	}

	public List<String> getEnumList() {
		if(enumList == null){
			enumList = new ArrayList<String>();
		}
		return enumList;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	List<ElementInformation> elementInfo = new ArrayList<ElementInformation>();

	public List<ElementInformation> getElementInfo() {
		return elementInfo;
	}

	

}
