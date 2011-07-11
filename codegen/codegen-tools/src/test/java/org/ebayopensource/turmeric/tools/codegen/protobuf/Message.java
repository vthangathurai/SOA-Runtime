package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.util.List;

public class Message {
	
	String messageName;
	
	List<Field> fields;
	
	List<Message> nestedMessages;
	
	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Message> getNestedMessages() {
		return nestedMessages;
	}

	public void setNestedMessages(List<Message> nestedMessages) {
		this.nestedMessages = nestedMessages;
	}

	

}
