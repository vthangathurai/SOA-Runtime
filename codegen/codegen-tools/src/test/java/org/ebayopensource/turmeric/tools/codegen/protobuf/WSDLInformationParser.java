package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WSDLInformationParser {
	
	File wsdlInfoFile;
	
	static int typesCount = 0 ;
	
	public WSDLInformationParser(File file) {
		
		wsdlInfoFile = file;
	}
	
	public List<MessageInformation> parse(){
		
		List<String> lines = FileUtil.readFileAsLines(wsdlInfoFile);
		List<MessageInformation> msg =new ArrayList<MessageInformation>();
		MessageInformation msgInfo = null;
		for(String str :lines){
			
			String [] info = str.split("=");
			if(info.length == 2){
				
				msgInfo = new MessageInformation();
				msg.add(msgInfo);
				msgInfo.setMessageName(info[1]);
				msgInfo.setNamespace(info[0]);
				
			}
			if(info.length == 4){
				
				if(info[3].startsWith("enum")){
					msgInfo.setEnums(true);
					String values = info[3].substring(info[3].indexOf("[") +1, info[3].length()-1);
					String vals [] = values.split(",");
					for(String s : vals){
					msgInfo.getEnumList().add(s);
					}
					continue;
				}
				ElementInformation ele = new ElementInformation();
				ele.setElementName(info[0]);
				ele.setJaxbName(info[1]);
				if(info[2].contains("Enum.")){
					ele.setEnums(true);
				}
				ele.setDataType(info[2]);
				
				if(info[3].trim().toLowerCase().equals("optional")){
					
					ele.setOptional(true);
				}else if(info[3].trim().toLowerCase().equals("required")){
					ele.setOptional(false);
				}else if(info[3].trim().toLowerCase().equals("repeated")){
					
					ele.setList(true);
				}
				msgInfo.getElementInfo().add(ele);
				
			}
			
		
			
		
		}
		
		for(MessageInformation m :msg){
			System.out.println(m.getMessageName());
			System.out.println(m.getNamespace());
			
			for(ElementInformation e :m.getElementInfo()){
				System.out.println(e);
			}
		}
		return msg;
		
	
		
	}

}
