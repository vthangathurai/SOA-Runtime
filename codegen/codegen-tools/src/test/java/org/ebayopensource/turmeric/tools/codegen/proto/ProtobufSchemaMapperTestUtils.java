/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.proto;

import static com.ebay.soaframework.tools.codegen.util.CodeGenConstants.GEN_META_SRC_FOLDER;
import static com.ebay.soaframework.tools.codegen.util.CodeGenConstants.GEN_SRC_FOLDER;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.wsdl.Definition;

import com.ebay.soaframework.tools.codegen.CodeGenContext;
import com.ebay.soaframework.tools.codegen.InputOptions;
import com.ebay.soaframework.tools.codegen.ServiceCodeGenArgsParser;
import com.ebay.soaframework.tools.codegen.ServiceCodeGenArgsValidator;
import com.ebay.soaframework.tools.codegen.exception.BadInputOptionException;
import com.ebay.soaframework.tools.codegen.exception.BadInputValueException;
import com.ebay.soaframework.tools.codegen.exception.CodeGenFailedException;
import com.ebay.soaframework.tools.codegen.exception.MissingInputOptionException;
import com.ebay.soaframework.tools.codegen.external.WSDLUtil;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.dotproto.DotProtoGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufEnumEntry;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufEnumMessage;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufField;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufFieldModifier;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufFieldType;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufMessage;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufSchema;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.SchemaTypeName;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.tag.DefaultProtobufTagGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.tag.ProtobufTagGenerator;
import com.ebay.soaframework.tools.codegen.handler.ConsoleResponseHandler;
import com.ebay.soaframework.tools.codegen.handler.DontPromptResponseHandler;
import com.ebay.soaframework.tools.codegen.handler.UserResponseHandler;
import com.ebay.soaframework.tools.codegen.util.CodeGenUtil;
import com.ebay.soaframework.tools.library.TypeLibraryInputOptions;
import com.ebay.soaframework.tools.library.codegen.TypeLibraryCodeGenArgsParser;
import com.ebay.soaframework.tools.library.codegen.TypeLibraryCodeGenArgsValidator;
import com.ebay.soaframework.tools.library.codegen.TypeLibraryCodeGenBuilder;
import com.ebay.soaframework.tools.library.codegen.TypeLibraryCodeGenContext;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author rkulandaivel
 *
 */
@SuppressWarnings("unchecked")
public class ProtobufSchemaMapperTestUtils {
	
	private static final List<String> s_protobufTypes = Arrays.asList(new String[]{
											"int32",
											"sint32",
											"string",
											"double",
											"bool",
											"bytes",
											"sint64",
											"float"
											});


	private static InputOptions getInputOptions(String[] args)
			throws MissingInputOptionException, BadInputOptionException,
			BadInputValueException {

		// Parse & Validate input arguments
		InputOptions inputOptions = ServiceCodeGenArgsParser.getInstance()
				.parse(args);
		ServiceCodeGenArgsValidator.getInstance().validate(inputOptions);

		return inputOptions;
	}

	private static CodeGenContext createContext(
			InputOptions inputOptions, 
			UserResponseHandler userResponseHandler) 
			throws CodeGenFailedException {
	
		CodeGenContext codeGenCtx = 
				new CodeGenContext(inputOptions, userResponseHandler);
		
		String javaSrcDestLoc = inputOptions.getJavaSrcDestLocation();
		String metaSrcDestLoc = inputOptions.getMetaSrcDestLocation();

		String destLocation = inputOptions.getDestLocation();
		
		// Generated Java Source files will go here
		if (CodeGenUtil.isEmptyString(javaSrcDestLoc)) {
			// set it to default
			javaSrcDestLoc = CodeGenUtil.genDestFolderPath(destLocation, GEN_SRC_FOLDER);
		} 		
		// Generated XML/Config files will go here
		if (CodeGenUtil.isEmptyString(metaSrcDestLoc)) {
			// set it to default
			metaSrcDestLoc = CodeGenUtil.genDestFolderPath(destLocation, GEN_META_SRC_FOLDER);
		} 
			
		// Compiled Java Classes will go here
		String binLocation = inputOptions.getBinLocation();
		if (CodeGenUtil.isEmptyString(binLocation)) {
			binLocation = CodeGenUtil.genDestFolderPath(destLocation, "bin");
			inputOptions.setBinLocation(binLocation);
		}
		// create directories if doesn't exists
		try {			
			CodeGenUtil.createDir(javaSrcDestLoc);
			CodeGenUtil.createDir(metaSrcDestLoc);
			CodeGenUtil.createDir(binLocation);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}
		
		codeGenCtx.setJavaSrcDestLocation(javaSrcDestLoc);
		codeGenCtx.setMetaSrcDestLocation(metaSrcDestLoc);
		
		
		return codeGenCtx;
	}

	private static TypeLibraryCodeGenContext createContext(
			TypeLibraryInputOptions typeLibraryOptions,
			UserResponseHandler userResponseHandler) 
			throws CodeGenFailedException {
	
		TypeLibraryCodeGenContext typeLibraryCodeGenCtx = 
				new TypeLibraryCodeGenContext(typeLibraryOptions, userResponseHandler);
		
			
		return typeLibraryCodeGenCtx;
	}
	public static TypeLibraryInputOptions getTypeLibraryInputOptions(String[] args) 
	throws MissingInputOptionException, BadInputOptionException, BadInputValueException {
		// Parse & Validate input arguments 
		TypeLibraryInputOptions typeLibraryInputOptions = TypeLibraryCodeGenArgsParser.getInstance().parseTypeLibraryOptions(args);
		TypeLibraryCodeGenArgsValidator.getInstance().validate(typeLibraryInputOptions);
		return typeLibraryInputOptions;
	
	}

	public static TypeLibraryCodeGenContext getTypeLibraryCodeGenContext(String[] pluginParameters, TypeLibraryCodeGenBuilder builder) throws Exception{
		TypeLibraryInputOptions typeLibraryInputOptions = builder.getTypeLibraryInputOptions(pluginParameters);
		
		
		UserResponseHandler userResponseHandler = null;		
		if (typeLibraryInputOptions.isDontPrompt()) {
			userResponseHandler = new DontPromptResponseHandler();
		} 
		else if (userResponseHandler == null) {
			userResponseHandler = new ConsoleResponseHandler();
		}
		
		return createContext(typeLibraryInputOptions, userResponseHandler);
	}
	public static CodeGenContext getCodeGenContext(String testArgs[]) throws Exception{
		//String testArgs[] = getTestAWsdlArgs();
		
		InputOptions inputOptions =getInputOptions( testArgs );

		UserResponseHandler userResponseHandler = null;		
		if (inputOptions.isDontPrompt()) {
			userResponseHandler = new DontPromptResponseHandler();
		} 
		else if (userResponseHandler == null) {
			userResponseHandler = new ConsoleResponseHandler();
		}
		CodeGenContext context = createContext( inputOptions , userResponseHandler);
		
		
		String wsdlFileLoc = context.getInputOptions().getInputFile();
		
		if(context.getWsdlDefinition() == null){
			Definition definition = WSDLUtil.getWSDLDefinition(wsdlFileLoc);
			context.setWsdlDefinition(definition);
		}
		context.setWSDLURI(wsdlFileLoc);
		WSDLUtil.populateCodegenCtxWithWSDLDetails(wsdlFileLoc, context);
		
		return context;
	}

	public static  List<ProtobufMessage> loadFindItemServiceManuallyWrittenProtoFile( String dotprotofilepath ) throws Exception{
		//String dotprotofilepath = "UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/FindItemServiceAdjustedV3.proto";
		
		DataInputStream in = null;
		try{
	    FileInputStream fstream = new FileInputStream( dotprotofilepath );

	    // Get the object of DataInputStream
	    in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;

	    boolean messageInLoop = false;
	    boolean isEnumMessage = false;
	    ProtobufMessage message = null;
	    List<ProtobufMessage> messages = new ArrayList<ProtobufMessage>();

	    //Read File Line By Line
	    while ((strLine = br.readLine()) != null)   {
	      // Print the content on the console
	      //System.out.println (strLine);
	      
	      strLine = strLine.trim();
	      
	      if(CodeGenUtil.isEmptyString(strLine)){
	    	  continue;
	      }
	      
	      if(strLine.startsWith("//") || strLine.startsWith("package") || strLine.startsWith("option ")){
	    	  continue;
	      }else if(strLine.startsWith("message")){
	    	  String messageName = getMessageNameFromLine( strLine );
	    	  message = new ProtobufMessage();
	    	  messageInLoop = true;
	    	  message.setMessageName(messageName);

	      }else if(messageInLoop && strLine.startsWith("enum")){
	    	  String enumMessageName = getEnumMessageNameFromLine( strLine );
	    	  isEnumMessage = true;
	    	  ProtobufEnumMessage enumMessage = new ProtobufEnumMessage();
	    	  enumMessage.setEnumMessageName(message.getMessageName());
	    	  enumMessage.setMessageName(enumMessageName);
	    	  enumMessage.setEnumType(true);
	    	  message = enumMessage;

	      }else if(messageInLoop && strLine.startsWith("}")){
	    	  if(messageInLoop && isEnumMessage){
	    		  isEnumMessage = false;
	    	  }else if(messageInLoop){
	    		  messageInLoop = false;
	    		  messages.add(message);
	    	  }
	      }else{
	    	  if(isEnumMessage){
	    		  ProtobufEnumEntry enumEntry = parseLineAndReturnEnumInstance( strLine );
	    		  ((ProtobufEnumMessage)message).getEnumEntries().add(enumEntry);
	    	  }else{
	    		  ProtobufField field = parseLineAndReturnFieldInstance( strLine );
	    		  message.getFields().add(field);
	    	  }
	      }
	    }

	    return messages;
		}finally{
		    //Close the input stream
			if(in != null){
				in.close();
			}
		}
	}
	public static  ProtobufField parseLineAndReturnFieldInstance(String strLine) throws Exception{
		String[] parts = strLine.split("=");
		if(parts.length != 2){
			throw new Exception("Invalid line");
		}

		String lineBeforeEqualSign = parts[0];
		lineBeforeEqualSign = lineBeforeEqualSign.trim();
		String[] fieldInfoParts = lineBeforeEqualSign.split(" ");
		if(fieldInfoParts.length != 3){
			throw new Exception("Invalid line");
		}

		String lineAfterEqualSign = parts[1].trim();
		String seqNo = lineAfterEqualSign.substring(0, lineAfterEqualSign.length()-1);
		seqNo = seqNo.trim();

		ProtobufField field = new ProtobufField();
		field.setFieldModifier( ProtobufFieldModifier.fromValue( fieldInfoParts[0] ) );

		String fieldType = fieldInfoParts[1].trim();
		field.setProtobufTypeName(fieldType);

		String fieldName = fieldInfoParts[2].trim();
		field.setFieldName(fieldName);
		field.setConvertedFieldName(fieldName);

		field.setSequenceTagNumber( Integer.parseInt( seqNo ) );
		return field;
		
	}
	public static  ProtobufEnumEntry parseLineAndReturnEnumInstance(String strLine) throws Exception{
		String[] parts = strLine.split("=");
		if(parts.length != 2){
			throw new Exception("Invalid line");
		}

		String lineBeforeEqualSign = parts[0];
		lineBeforeEqualSign = lineBeforeEqualSign.trim();


		String lineAfterEqualSign = parts[1].trim();
		String seqNo = lineAfterEqualSign.substring(0, lineAfterEqualSign.length()-1);
		seqNo = seqNo.trim();

		ProtobufEnumEntry field = new ProtobufEnumEntry();
		field.setEnumValue(lineBeforeEqualSign);
		field.setSequenceNumber(Integer.parseInt( seqNo ));

		return field;
		
	}
	public static  String getEnumMessageNameFromLine(String strLine){
		final String messageKeyWord = "enum";
		int index = strLine.indexOf( messageKeyWord );
		
		strLine = strLine.substring(index + messageKeyWord.length());
		
		int endIndex = strLine.lastIndexOf("{");
		
		strLine = strLine.substring(0, endIndex);
		return strLine.trim();
	}
	public static  String getMessageNameFromLine(String strLine){
		final String messageKeyWord = "message";
		int index = strLine.indexOf( messageKeyWord );
		
		strLine = strLine.substring(index + messageKeyWord.length());
		
		int endIndex = strLine.lastIndexOf("{");
		
		strLine = strLine.substring(0, endIndex);
		return strLine.trim();
	}
	public static Map<SchemaTypeName, ProtobufMessage> createMessageMapFromList(List<ProtobufMessage> messages){
		Map<SchemaTypeName, ProtobufMessage> schemaTypeMap = new HashMap<SchemaTypeName, ProtobufMessage>();
		
		
		for(ProtobufMessage message : messages){
			schemaTypeMap.put(message.getSchemaTypeName() , message);
		}

		return schemaTypeMap;
	}

	public static ProtobufFieldType getFieldType(ProtobufField field){

		String typeName = field.getProtobufTypeName();
		if( typeName.indexOf(".") > 0 ){
			return ProtobufFieldType.ENUM_TYPE;
		}
		if(!s_protobufTypes.contains( typeName ) ){
			return ProtobufFieldType.COMPLEX_TYPE;
		}

		return ProtobufFieldType.INBUILT_TYPE;
	}
	public static class ProtobufMessageComparator {

		public static boolean compareEnumMessage(ProtobufEnumMessage object1, ProtobufEnumMessage object2) {
			if(!compareMessage(object1, object2)){
				return false;
			}
			if(object1.getEnumMessageName() == null || object2.getEnumMessageName() == null){
				return false;
			}
			if( !(object1.getEnumMessageName().equals(object2.getEnumMessageName())) ){
				return false;
			}
			if( !(compareEnumFields( object1.getEnumEntries(), object2.getEnumEntries() )) ){
				return false;
			}

			return true;
		}
		public static boolean compareMessage(ProtobufMessage object1, ProtobufMessage object2) {
			if(object1.getMessageName() == null || object2.getMessageName() == null){
				return false;
			}
			if( !(object1.getMessageName().equals(object2.getMessageName())) ){
				return false;
			}
			if(object1.getEprotoClassName() == null || object2.getEprotoClassName() == null){
				return false;
			}
			if( !(object1.getEprotoClassName().equals(object2.getEprotoClassName())) ){
				return false;
			}
			if(object1.getJprotoClassName() == null || object2.getJprotoClassName() == null){
				return false;
			}
			if( !(object1.getJprotoClassName().equals(object2.getJprotoClassName())) ){
				return false;
			}
			if(object1.getJaxbClassName() == null || object2.getJaxbClassName() == null){
				return false;
			}
			if( !(object1.getJaxbClassName().equals(object2.getJaxbClassName())) ){
				return false;
			}
			if( object1.isEnumType() != object2.isEnumType() ){
				return false;
			}
			if( !(compareFields( object1.getFields(), object2.getFields() ) ) ){
				return false;
			}
			return true;
		}

		public static boolean compareFields(List<ProtobufField> msg1Fields, List<ProtobufField> msg2Fields) {
            List<ProtobufField> list1 = (List<ProtobufField>) msg1Fields;
            List<ProtobufField> list = (List<ProtobufField>) msg2Fields;
            if (list.size() != list1.size()) {
                return false;
            }
            Collections.sort(list1, new ProtobufFieldComparator() );
            Collections.sort(list, new ProtobufFieldComparator() );
            
            Iterator<ProtobufField> it1 = list1.iterator(), it2 = list.iterator();
            while (it1.hasNext()) {
            	ProtobufField e1 = it1.next(), e2 = it2.next();
                if (!(e1 == null ? (e2 == null) : compareField(e1, e2))  ){
                    return false;
                }
            }
            return true;
		}
		public static boolean compareField(ProtobufField field1, ProtobufField field2) {
			if(field1.getConvertedFieldName() == null || field2.getConvertedFieldName() == null ){
				return false;
			}
			if( !(field1.getConvertedFieldName().equals(field2.getConvertedFieldName())) ){
				return false;
			}
			if( !(field1.getFieldModifier().equals(field2.getFieldModifier())) ){
				return false;
			}
			if(field1.getProtobufTypeName() == null || field2.getProtobufTypeName() == null ){
				return false;
			}
			if( !(field1.getProtobufTypeName().equals(field2.getProtobufTypeName())) ){
				return false;
			}
			if(field1.getTypeOfField() == null || field2.getTypeOfField() == null ){
				return false;
			}

			ProtobufFieldType type1 = field1.getTypeOfField();
			ProtobufFieldType type2 = field2.getTypeOfField();

			if( type1 != type2 ){
				if(type1 == ProtobufFieldType.DATE_TYPE || type1 == ProtobufFieldType.DURATION_TYPE || type1 == ProtobufFieldType.DURATION_TYPE){
					if(type2 != ProtobufFieldType.INBUILT_TYPE){
						return false;
					}
				}else if(type2 == ProtobufFieldType.DATE_TYPE || type2 == ProtobufFieldType.DURATION_TYPE || type2 == ProtobufFieldType.DURATION_TYPE){
					if(type1 != ProtobufFieldType.INBUILT_TYPE){
						return false;
					}
				} 
			}
			return true;
		}

		public static boolean compareEnumFields(List<ProtobufEnumEntry> msg1Fields, List<ProtobufEnumEntry> msg2Fields) {
            List<ProtobufEnumEntry> list1 = (List<ProtobufEnumEntry>) msg1Fields;
            List<ProtobufEnumEntry> list = (List<ProtobufEnumEntry>) msg2Fields;
            if (list.size() != list1.size()) {
                return false;
            }

            Iterator<ProtobufEnumEntry> it1 = list1.iterator(), it2 = list.iterator();
            while (it1.hasNext()) {
            	ProtobufEnumEntry e1 = it1.next(), e2 = it2.next();
                if (!(e1 == null ? (e2 == null) : compareEnumField(e1, e2))  ){
                    return false;
                }
            }
            return true;
		}
		public static boolean compareEnumField(ProtobufEnumEntry field1, ProtobufEnumEntry field2) {
			if(field1.getEnumValue() == null || field2.getEnumValue() == null ){
				return false;
			}
			if( !(field1.getEnumValue().equals(field2.getEnumValue())) ){
				return false;
			}
			return true;
		}
		
	}
	
	private static class ProtobufFieldComparator implements Comparator<ProtobufField>{

		@Override
		public int compare(ProtobufField object1, ProtobufField object2) {
			if( object1 == null && object2 == null){
				return 0;
			}
			if( object1 == null ){
				return -1;
			}
			if( object2 == null ){
				return 1;
			}
			int xx = object1.getConvertedFieldName().compareTo( object2.getConvertedFieldName() ); 
			return xx;
		}
		
	}
	
	public static void validateTagNumberGeneration( CodeGenContext context, ProtobufSchema schema ) throws Exception
	{
        ProtobufTagGenerator generator = null;
        try {
            generator = new DefaultProtobufTagGenerator(new File( context.getInputOptions().getInputFile() ), null );
        } catch (Exception e) {
            throw e;
        }
        
        try {
            DotProtoGenerator.getInstance().updateSchemaWithSequenceNumbers( schema, generator, true );
        } catch (Exception e1) {
            throw e1;
        }
    }

}
