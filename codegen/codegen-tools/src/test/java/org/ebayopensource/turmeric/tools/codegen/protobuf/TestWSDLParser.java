package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.io.File;

public class TestWSDLParser {
	
	public static void main(String[] args) {
		
		File file = new File("wsdlorxsd/wsdlinfo.txt");
		WSDLInformationParser info = new WSDLInformationParser(file);
		info.parse();
	}

}
